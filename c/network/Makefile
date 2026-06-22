ROOT = .
include $(ROOT)/Make.defines

PROGS := nic_list time_server time_server_dual \
	tcp_client tcp_serve_chat tcp_serve_toupper \
	udp_client udp_serve_toupper dns_query lookup \
	web_get web_server smtp_send https_simple \
	https_get tls_client tls_get_cert https_server \
	ssh_auth ssh_command ssh_connect ssh_download ssh_version

all: $(PROGS)

%: %.c
	$(CC) -o $@ $(CFLAGS) $^

https_%: https_%.c
	$(CC) -o $@ $(CFLAGS) $^ -lssl -lcrypto

tls_%: tls_%.c
	$(CC) -o $@ $(CFLAGS) $^ -lssl -lcrypto

ssh_%: ssh_%.c
	$(CC) -o $@ $(CFLAGS) $^ -lssh

clean:
	rm -rf $(PROGS) a.out
