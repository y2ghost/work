#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <time.h>

/*
 * 显示服务器时间的web服务
 * 服务端执行: ./timer_server
 * 客户端执行: curl localhost:8080
 */
int main(int ac, char *av[])
{
    printf("配置本地地址...\n");
    struct addrinfo hints;
    memset(&hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    struct addrinfo *bind_address;
    getaddrinfo(0, "8080", &hints, &bind_address);

    printf("创建套接字...\n");
    int socket_listen = socket(bind_address->ai_family,
        bind_address->ai_socktype, bind_address->ai_protocol);

    if (socket_listen < 0) {
        fprintf(stderr, "socket() failed. (%d)\n", errno);
        return 1;
    }

    printf("绑定套接字到本地地址...\n");
    if (bind(socket_listen, bind_address->ai_addr,
        bind_address->ai_addrlen)) {
        fprintf(stderr, "bind() failed. (%d)\n", errno);
        return 1;
    }

    freeaddrinfo(bind_address);
    printf("监听...\n");

    if (listen(socket_listen, 10) < 0) {
        fprintf(stderr, "listen() failed. (%d)\n", errno);
        return 1;
    }


    printf("等待客户端连接...\n");
    struct sockaddr_storage client_address;
    socklen_t client_len = sizeof(client_address);
    int socket_client = accept(socket_listen,
        (struct sockaddr*) &client_address, &client_len);

    if (socket_client < 0) {
        fprintf(stderr, "accept() failed. (%d)\n", errno);
        return 1;
    }


    printf("客户端已连接... ");
    char address_buffer[128] = {0};
    getnameinfo((struct sockaddr*)&client_address,
        client_len, address_buffer, sizeof(address_buffer), 0, 0,
        NI_NUMERICHOST);
    printf("%s\n", address_buffer);

    printf("处理请求...\n");
    char request[1024] = {0};
    int bytes_received = recv(socket_client, request, 1024, 0);
    printf("收到请求字节数: %d\n", bytes_received);

    printf("发送响应...\n");
    const char *response =
        "HTTP/1.1 200 OK\r\n"
        "Connection: close\r\n"
        "Content-Type: text/plain\r\n\r\n"
        "Local time is: ";
    int bytes_sent = send(socket_client, response, strlen(response), 0);
    printf("发送字节数:%d - 时间信息字节数:%d\n",
        bytes_sent, (int)strlen(response));

    time_t timer;
    time(&timer);
    char *time_msg = ctime(&timer);
    bytes_sent = send(socket_client, time_msg, strlen(time_msg), 0);
    printf("发送字节数:%d - 时间信息字节数:%d\n",
        bytes_sent, (int)strlen(time_msg));

    printf("关闭客户端连接...\n");
    close(socket_client);
    printf("关闭监听套接字...\n");
    close(socket_listen);
    printf("完成.\n");
    return 0;
}
