KVM安装说明
egrep -c '(vmx|svm)' /proc/cpuinfo
sudo apt install bridge-utils libvirt-clients libvirt-daemon-system qemu-system-x86
systemctl status libvirtd
sudo systemctl stop libvirtd
sudo groupadd kvm
sudo groupadd libvirt
sudo usermod -aG kvm <user>
sudo usermod -aG libvirt <user>
sudo chown :kvm /var/lib/libvirt/images
sudo chmod g+rw /var/lib/libvirt/images
sudo systemctl start libvirtd
sudo systemctl status libvirtd
sudo apt install ssh-askpass virt-manager
# 通过virt-manager安装虚拟机
# 查看虚拟机列表
virsh list

桥接网卡配置示例
sudo cp /etc/netplan/00-installer-config.yaml /etc/netplan/00-installer-config.yaml.bak
sudo cp bridged_netplan_for_virt /etc/netplan/00-installer-config.yaml
sudo netplan apply

ubuntu-docker安装
sudo apt install docker.io
systemctl status docker
sudo systemctl enable --now docker
sudo usermod -aG docker <user>

docker安装
docker_install.sh

docker卸载
docker_uninstall.sh

lxd安装
sudo snap install lxd
sudo usermod -aG lxd <user>
lxd init
lxc launch ubuntu:22.04 mycontainer
lxc list
lxc start <container>
lxc stop <container>
lxc delete <container>
lxc image list
lxc image delete <name>
lxc exec <container> <cmd>

