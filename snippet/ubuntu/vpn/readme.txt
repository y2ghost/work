openvpn服务安装
- sudo apt install openvpn easy-rsa
- sudo make-cadir /etc/openvpn/easy-rsa
- # root用户身份登录
- cd /etc/openvpn/easy-rsa/
- ./easyrsa init-pki
- ./easyrsa build-ca
- # 创建服务器证书
- ./easyrsa gen-req vpn.ywork.dev nopass
- ./easyrsa gen-dh
- ./easyrsa sign-req server vpn.ywork.dev
- cp pki/dh.pem pki/ca.crt pki/issued/vpn.ywork.dev.crt \
  > pki/private/vpn.ywork.dev.key /etc/openvpn/
- # 创建客户端证书
- ./easyrsa gen-req myclient1 nopass
- ./easyrsa sign-req client myclient1
- # 拷贝文件到客户端
- # pki/ca.crt
- # pki/issued/myclient1.crt
- # 配置服务器
- cp /usr/share/doc/openvpn/examples/sample-config-files/server.conf /etc/openvpn/myserver.conf
- # 编辑配置文件/etc/openvpn/myserver.conf
- # ca ca.crt
- # cert vpn.ywork.dev.crt
- # key vpn.ywork.dev.key
- # dh dh.pem
- openvpn --genkey tls-auth /etc/openvpn/ta.key 
- # 配置/etc/sysctl.conf
- # net.ipv4.ip_forward=1
- sudo sysctl -p /etc/sysctl.conf
- sudo systemctl start openvpn@myserver
- sudo journalctl -u openvpn@myserver -xe
- # 检查是否正常配置
- ip addr show dev tun0

openvpn-客户端安装
- sudo apt install openvpn
- sudo cp /usr/share/doc/openvpn/examples/sample-config-files/client.conf /etc/openvpn/
- # 从服务端获取的文件拷贝到/etc/openvpn并更改配置/etc/openvpn/client.conf
- # ca ca.crt
- # cert myclient1.crt
- # key myclient1.key
- # tls-auth ta.key 1
- # client
- # remote vpn.ywork.dev 1194
- sudo systemctl start openvpn@client
- sudo systemctl status openvpn@client
- # 检查服务连通性 
- ping 10.8.0.1
- ip route


桥接配置概述
- 接口配置示例: cat /etc/netplan/01-netcfg.yaml
- sudo apt update
sudo apt install networkd-dispatcher
sudo touch /usr/lib/networkd-dispatcher/dormant.d/promisc_bridge
sudo chmod +x /usr/lib/networkd-dispatcher/dormant.d/promisc_bridge
- # 脚本promisc_bridge内容
  > ```shell
  > #!/bin/sh
  > set -e
  > if [ "$IFACE" = br0 ]; then
  >     # no networkd-dispatcher event for 'carrier' on the physical interface
  >     ip link set enp0s31f6 up promisc on
  > fi
  > ```
- # 服务端配置tap示例/etc/openvpn/server.conf
  > ```txt
  > ;dev tun
  > dev tap
  > ;server 10.8.0.0 255.255.255.0
  > server-bridge 10.0.0.4 255.255.255.0 10.0.0.128 10.0.0.254
  > ```
- sudo systemctl restart openvpn@myserver
- # 客户端配置tap示例
  > ```txt
  > dev tap
  > ;dev tun
  > ```
- sudo systemctl restart openvpn@client

wireguard安装配置
- # 网络拓扑见wireguard_network.txt
- sudo apt install wireguard
- # VPN服务配置
- umask 077
- wg genkey > internal-private.key
- wg pubkey < internal-private.key > internal-public.key
- # 创建配置文件/etc/wireguard/wg0.conf
  > ```txt
  > [Interface]
  > Address = 10.10.10.10/32
  > ListenPort = 51000
  > PrivateKey = <internal-private.key>
  > [Peer]
  > # 客户端地址
  > PublicKey = <internal-public.key>
  > AllowedIPs = 10.10.10.11/32
  > ```
- # 创建配置文件/etc/sysctl.d/70-wireguard-routing.conf
  > ```txt
  > net.ipv4.ip_forward = 1
  > net.ipv4.conf.all.proxy_arp = 1
  > ```
- sudo sysctl -p /etc/sysctl.d/70-wireguard-routing.conf -w
- sudo wg-quick up wg0
- # 客户端的配置示例
- # 配置文件/etc/wireguard/home_internal.conf
  > ```txt
  > [Interface]
  > ListenPort = 51000
  > Address = 10.10.10.11/24
  > PrivateKey = <home-private.key>
  > [Peer]
  > PublicKey = <home-public.key>
  > Endpoint = <home-ppp0-IP-or-hostname>:51000
  > AllowedIPs = 10.10.10.0/24
  > ```
- sudo wg-quick up home_internal

