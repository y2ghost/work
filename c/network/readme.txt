概要说明
- 编译测试环境是CentOS或类似系统
- 本目录代码用C语言实现常见的网络编程技巧
- 基本的TCP和UDP编程API交互流程图
  > TCP见图: tcp_socket_flow.png
  > UDP见图: udp_socket_flow.png
- 简单编译测试直接执行make命令即可

依赖的开发库
- openssl-devel
- libssh-devel

诊断问题的方法
- 查看TCP连接情况
  > unix的方式: netstat -nt
  > windows的方法: netstat -nao -p TCP|findstr LISTEN
- tcpdump和tshark工具捕获网络流量分析

