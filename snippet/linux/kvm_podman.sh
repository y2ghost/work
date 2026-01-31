# kvm安装
sudo dnf install qemu-kvm qemu-img libvirt virt-install libvirt-client
sudo dnf install virt-manager
lsmod | grep kvm
systemctl status libvirtd
sudo systemctl enable --now libvirtd
sudo systemctl start libvirtd
# 通过cockpit方式安装虚拟机
sudo dnf install cockpit-machines

# podman等工具安装
sudo dnf install container-tools
skopeo inspect docker://docker.io/library/rockylinux:9.1-minimal
podman search rockylinux
# podman类似docker命令
podman pull rockylinux:9.1-minimal
podman images
podman inspect docker.io/library/rockylinux:9.1-minimal
podman run --rm docker.io/library/rockylinux:9.1-minimal cat /etc/passwd
podman stop <container>
podman ps -a
podman start <container>
podman exec -it <container> /bin/bash
podman pause <container>
podman unpause <container>
podman rm <container>

