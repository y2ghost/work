DHCP安装说明
sudo apt install isc-dhcp-server
systemctl status isc-dhcp-server
sudo systemctl stop isc-dhcp-server
sudo systemctl stop isc-dhcp-server6
sudo systemctl disable isc-dhcp-server6
sudo mv /etc/dhcp/dhcpd.conf /etc/dhcp/dhcpd.conf.orig
sudo cp dhcpcd.conf /etc/dhcp/dhcpd.conf
# 配置监听网卡(例如enp0s8)地址为192.168.1.1/24
# 修改配置/etc/default/isc-dhcp-server
# INTERFACESv4="enp0s8"
sudo systemctl start isc-dhcp-server
sudo systemctl status isc-dhcp-server
# 查看日志和分配信息
sudo tail -f /var/log/syslog
cat var/lib/dhcp/dhcpd.leases

DNS服务安装说明
sudo apt install bind9
# 配置/etc/bind/named.conf.options
# forwarders {
#   119.29.29.29;
#   114.114.114.114;
# };
# 修改配置/etc/bind/named.conf.local
# zone "local.dev" IN {
#     type master;
#     file "/etc/bind/net.local.dev";
# };
sudo cp net.local.dev /etc/bind/net.local.dev
# 重启服务
sudo systemctl restart bind9
sudo systemctl status bind9
# 查看日志
cat /var/log/syslog | grep bind10
sudo apt install dnsutils
dig web01.local.dev
resolvectl

