基本用法
- socat [options] <address> <address>

address的语法
- 类型+零个或多个地址参数+零个或多个地址选项
- 地址参数使用':'分隔
- 地址选项使用','分隔

HTTP示例
- socat -dd - tcp4:www.domain.org:80
- # 记录输入历史
- socat -d -d readline,history=$HOME/.http_history tcp4:www.domain.org:www,crnl

端口转发
- socat TCP4-LISTEN:www TCP4:www.domain.org:www
- socat -d -d -lmlocal2 \
  > TCP4-LISTEN:80,bind=myaddr1,su=nobody,fork,range=10.0.0.0/8,reuseaddr \
  > TCP4:www.domain.org:80,bind=myaddr2
- 备注: myaddr1的地址注意和range网络一致

代理模式
- socat TCP4-LISTEN:2022,reuseaddr,fork \
  > PROXY:proxy.local:www.domain.org:22,proxyport=3128,proxyauth=username:s3cr3t
- 监听2022端口，转发到proxy.local:3128并访问www.domain.org:22

SSL连接
- socat - SSL:server:4443,cafile=./server.crt,cert=./client.pem

文件交互
- socat -u TCP4-LISTEN:3334,reuseaddr,fork \
  > OPEN:/tmp/in.log,creat,append
- socat -u /var/log/dnf.log,seek-end=0,ignoreeof STDIO

调试连接
- socat TCP-L:7777,reuseaddr,fork SYSTEM:'filan -i 0 -s >&2',nofork

DCCP连接
- server / socat TCP4-LISTEN:4096,reuseaddr,type=6,prototype=33 exec:'tr A-Z a-z',pty,raw,echo=0
- client / echo ABCD |socat - TCP4-CONNECT:localhost:4096,type=6,prototype=33

TUN连接
- server / socat -d -d UDP-LISTEN:11443,reuseaddr TUN:192.168.255.1/24,up
- client / socat UDP:1.2.3.4:11443 TUN:192.168.255.2/24,up

GENDER-CHANGER技术
- 服务器防火墙限制了外部访问内部
- 防火墙运行服务器访问外部的80端口
- 假设client的地址是outside-host
- server / socat -d -d -d -t5 tcp:outside-host:80,forever,intervall=10,fork tcp:localhost:80
- client / socat -d -d -d tcp-l:80,reuseaddr,bind=127.0.0.1,fork tcp-l:80,bind=outside-host,reuseaddr,retry=10
- client测试访问http://127.0.0.1/

