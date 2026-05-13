DEVICE1=$(ls -l /sys/class/net/ | grep -v 'virtual\|total' | tail -n 1 | awk '{print $9}')
echo $DEVICE1
sudo ip link add link $DEVICE1 name macvtap1 type macvtap mode bridge
ip --brief link show macvtap1
ip --detail link show type macvtap
ip link show macvtap1
sudo ip link set macvtap1 up
ip -br  link show macvtap1
sudo ip address add 172.16.99.100/24 dev macvtap1
ip address show macvtap1
nmcli --get-values IP4.ADDRESS,GENERAL.DEVICE  device show  
sudo ip address del 172.16.99.100/24 dev macvtap1

# nmcli配置
nmcli device
DEVICE2=$(ls -l /sys/class/net/ | grep -v 'virtual\|total' | tail -n 1 | awk '{print $9}')
sudo nmcli con add con-name macvtap2 type macvlan mode bridge tap yes dev $DEVICE2 ifname macvtap2
nmcli device show macvtap2
nmcli connection show macvtap2
nmcli -f ipv4.addresses con show macvtap2
sudo nmcli connection modify macvtap2  ipv4.method manual \
    ipv4.addresses 172.16.99.200/24 ipv4.gateway 172.16.99.1 \
    ipv4.dns 8.8.8.8,8.8.4.4 ipv4.dns-search example.com
nmcli -f ipv4.addresses con show macvtap2
nmcli -f ipv4.addresses,IP4.ADDRESS con show macvtap2
sudo nmcli connection down macvtap2
sudo nmcli connection up macvtap2
ss -tuln
sudo nmcli con del macvtap2

