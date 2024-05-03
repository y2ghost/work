#include "common.h"
#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

#define BSIZE 1024

struct content_type_map_t {
    char *file_type;
    char *content_type;
};

static struct content_type_map_t content_type_maps[] = {
    {".css", "text/css"},
    {".csv", "text/csv"},
    {".gif", "image/gif"},
    {".htm", "text/html"},
    {".html", "text/html"},
    {".ico", "image/x-icon"},
    {".jpeg", "image/jpeg"},
    {".jpg", "image/jpeg"},
    {".js", "application/javascript"},
    {".json", "application/json"},
    {".png", "image/png"},
    {".pdf", "application/pdf"},
    {".svg", "image/svg+xml"},
    {".txt", "text/plain;charset=utf-8"},
};

static size_t CONTENT_TYPE_MAP_LEN = sizeof(content_type_maps)
    / sizeof(content_type_maps[0]);

static const char *get_content_type(const char* path)
{
    char *content_type = "application/octet-stream";
    const char *last_dot = strrchr(path, '.');

    for (size_t i = 0; i < CONTENT_TYPE_MAP_LEN; i++) {
        struct content_type_map_t *m = content_type_maps + i;
        if (0 == strcmp(last_dot, m->file_type)) {
            content_type = m->content_type;
            break;
        }
    }

    return content_type;
}

static int create_socket(const char* host, const char *port)
{
    printf("Configuring local address...\n");
    struct addrinfo hints;
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    struct addrinfo *bind_address;
    getaddrinfo(host, port, &hints, &bind_address);
    printf("Creating socket...\n");
    int socket_listen = socket(bind_address->ai_family,
        bind_address->ai_socktype, bind_address->ai_protocol);

    if (socket_listen < 0) {
        fprintf(stderr, "socket() failed. (%d)\n", errno);
        exit(1);
    }

    printf("Binding socket to local address...\n");
    if (bind(socket_listen, bind_address->ai_addr,
        bind_address->ai_addrlen)) {
        fprintf(stderr, "bind() failed. (%d)\n", errno);
        exit(1);
    }

    freeaddrinfo(bind_address);
    printf("Listening...\n");

    if (listen(socket_listen, 10) < 0) {
        fprintf(stderr, "listen() failed. (%d)\n", errno);
        exit(1);
    }

    return socket_listen;
}

#define MAX_REQUEST_SIZE 2047
struct client_info {
    socklen_t address_length;
    struct sockaddr_storage address;
    char address_buffer[128];
    SSL *ssl;
    int socket;
    char request[MAX_REQUEST_SIZE + 1];
    int received;
    struct client_info *next;
};

struct client_info *get_client(struct client_info **client_list, int s)
{
    struct client_info *ci = *client_list;
    while(ci) {
        if (ci->socket == s) {
            break;
        }

        ci = ci->next;
    }

    if (ci) {
        return ci;
    }

    struct client_info *n = calloc(1, sizeof(struct client_info));
    if (!n) {
        fprintf(stderr, "Out of memory.\n");
        exit(1);
    }

    n->address_length = sizeof(n->address);
    n->next = *client_list;
    *client_list = n;
    return n;
}

static void drop_client(struct client_info **client_list,
    struct client_info *client)
{
    SSL_shutdown(client->ssl);
    close(client->socket);
    SSL_free(client->ssl);
    struct client_info **p = client_list;

    while(*p) {
        if (*p == client) {
            *p = client->next;
            free(client);
            return;
        }

        p = &(*p)->next;
    }

    fprintf(stderr, "drop_client not found.\n");
    exit(1);
}


static const char *get_client_address(struct client_info *ci)
{
    getnameinfo((struct sockaddr*)&ci->address,
        ci->address_length,
        ci->address_buffer, sizeof(ci->address_buffer), 0, 0,
        NI_NUMERICHOST);
    return ci->address_buffer;
}

static fd_set wait_on_clients(struct client_info **client_list, int server)
{
    fd_set reads;
    FD_ZERO(&reads);
    FD_SET(server, &reads);
    int max_socket = server;
    struct client_info *ci = *client_list;

    while(ci) {
        FD_SET(ci->socket, &reads);
        if (ci->socket > max_socket) {
            max_socket = ci->socket;
        }

        ci = ci->next;
    }

    if (select(max_socket+1, &reads, 0, 0, 0) < 0) {
        fprintf(stderr, "select() failed. (%d)\n", errno);
        exit(1);
    }

    return reads;
}

static void send_400(struct client_info **client_list,
    struct client_info *client)
{
    const char *c400 = "HTTP/1.1 400 Bad Request\r\n"
        "Connection: close\r\n"
        "Content-Length: 11\r\n\r\nBad Request";
    SSL_write(client->ssl, c400, strlen(c400));
    drop_client(client_list, client);
}

static void send_404(struct client_info **client_list,
    struct client_info *client)
{
    const char *c404 = "HTTP/1.1 404 Not Found\r\n"
        "Connection: close\r\n"
        "Content-Length: 9\r\n\r\nNot Found";
    SSL_write(client->ssl, c404, strlen(c404));
    drop_client(client_list, client);
}

static void serve_resource(struct client_info **client_list,
    struct client_info *client, const char *path)
{
    printf("serve_resource %s %s\n", get_client_address(client), path);
    if (strcmp(path, "/") == 0) {
        path = "/index.html";
    }

    if (strlen(path) > 100) {
        send_400(client_list, client);
        return;
    }

    if (strstr(path, "..")) {
        send_404(client_list, client);
        return;
    }

    char full_path[128];
    sprintf(full_path, "public%s", path);
    FILE *fp = fopen(full_path, "rb");

    if (!fp) {
        send_404(client_list, client);
        return;
    }

    fseek(fp, 0L, SEEK_END);
    size_t cl = ftell(fp);
    rewind(fp);
    const char *ct = get_content_type(full_path);
    char buffer[BSIZE];

    sprintf(buffer, "HTTP/1.1 200 OK\r\n");
    SSL_write(client->ssl, buffer, strlen(buffer));

    sprintf(buffer, "Connection: close\r\n");
    SSL_write(client->ssl, buffer, strlen(buffer));

    sprintf(buffer, "Content-Length: %lu\r\n", cl);
    SSL_write(client->ssl, buffer, strlen(buffer));

    sprintf(buffer, "Content-Type: %s\r\n", ct);
    SSL_write(client->ssl, buffer, strlen(buffer));

    sprintf(buffer, "\r\n");
    SSL_write(client->ssl, buffer, strlen(buffer));

    int r = fread(buffer, 1, BSIZE, fp);
    while (r) {
        SSL_write(client->ssl, buffer, r);
        r = fread(buffer, 1, BSIZE, fp);
    }

    fclose(fp);
    drop_client(client_list, client);
}

/*
 * HTTPS使用的自签名整数生成命令
 * openssl req -x509 -newkey rsa:2048 -nodes -sha256 -keyout key.pem \
 * -out cert.pem -days 365
*/
int main(int ac, char *av[])
{
    SSL_library_init();
    OpenSSL_add_all_algorithms();
    SSL_load_error_strings();
    SSL_CTX *ctx = SSL_CTX_new(TLS_server_method());

    if (!ctx) {
        fprintf(stderr, "SSL_CTX_new() failed.\n");
        return 1;
    }

    if (!SSL_CTX_use_certificate_file(ctx, "cert.pem" , SSL_FILETYPE_PEM)
        || !SSL_CTX_use_PrivateKey_file(ctx, "key.pem", SSL_FILETYPE_PEM)) {
        fprintf(stderr, "SSL_CTX_use_certificate_file() failed.\n");
        ERR_print_errors_fp(stderr);
        return 1;
    }

    int server = create_socket(0, "8080");
    struct client_info *client_list = NULL;

    while(1) {
        fd_set reads;
        reads = wait_on_clients(&client_list, server);

        if (FD_ISSET(server, &reads)) {
            struct client_info *client = get_client(&client_list, -1);
            client->socket = accept(server,
                (struct sockaddr*) &(client->address),
                &(client->address_length));

            if (client->socket < 0) {
                fprintf(stderr, "accept() failed. (%d)\n", errno);
                return 1;
            }
            
            client->ssl = SSL_new(ctx);
            if (!client->ssl) {
                fprintf(stderr, "SSL_new() failed.\n");
                return 1;
            }

            SSL_set_fd(client->ssl, client->socket);
            if (SSL_accept(client->ssl) != 1) {
                ERR_print_errors_fp(stderr);
                drop_client(&client_list, client);
            } else {
                printf("New connection from %s.\n", get_client_address(client));
                printf("SSL connection using %s.\n", SSL_get_cipher(client->ssl));
            }
        }

        struct client_info *client = client_list;
        while(client) {
            struct client_info *next = client->next;
            if (FD_ISSET(client->socket, &reads)) {
                if (MAX_REQUEST_SIZE == client->received) {
                    send_400(&client_list, client);
                    client = next;
                    continue;
                }

                int r = SSL_read(client->ssl,
                    client->request + client->received,
                    MAX_REQUEST_SIZE - client->received);
                if (r < 1) {
                    printf("Unexpected disconnect from %s.\n",
                        get_client_address(client));
                    drop_client(&client_list, client);
                } else {
                    client->received += r;
                    client->request[client->received] = 0;
                    char *q = strstr(client->request, "\r\n\r\n");

                    if (q) {
                        *q = 0;
                        if (strncmp("GET /", client->request, 5)) {
                            send_400(&client_list, client);
                        } else {
                            char *path = client->request + 4;
                            char *end_path = strstr(path, " ");

                            if (!end_path) {
                                send_400(&client_list, client);
                            } else {
                                *end_path = 0;
                                serve_resource(&client_list, client, path);
                            }
                        }
                    }
                }
            }

            client = next;
        }
    }

    printf("\nClosing socket...\n");
    close(server);
    SSL_CTX_free(ctx);
    printf("Finished.\n");
    return 0;
}

