SSL连接的建立流程
- 假设服务端的域名是dev.local(127.0.0.1)
- 首先执行server.sh脚本创建服务器证书文件
- 接着执行client.sh脚本创建客户端证书文件


启动服务端
- socat OPENSSL-LISTEN:4433,reuseaddr,cert=./server.pem,cafile=./client.crt PIPE

启动客户端
- socat STDIO OPENSSL-CONNECT:dev.local:4433,cert=./client.pem,cafile=./server.crt

