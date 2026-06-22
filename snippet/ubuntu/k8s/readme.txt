microk8s安装
sudo snap install microk8s --classic
sudo microk8s kubectl get all --all-namespaces
sudo microk8s enable dns
sudo usermod -aG microk8s <user>

k8s安装
sudo apt install containerd
systemctl status containerd
sudo mkdir /etc/containerd
containerd config default | sudo tee /etc/containerd/config.toml
# 修改配置/etc/containerd/config.toml
# plugins."io.containerd.grpc.v1.cri".containerd.runtimes.runc.options]
# SystemdCgroup = true
sudo swapoff -a
free -m
# 修改配置/etc/sysctl.conf
# net.ipv4.ip_forward=1
echo "br_netfilter" | sudo tee /etc/modules-load.d/k8s.conf
sudo reboot
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://mirrors.tuna.tsinghua.edu.cn/kubernetes/apt kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/k8s.list
# echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install kubeadm kubectl kubelet
sudo kubeadm init --control-plane-endpoint=172.16.250.216 --node-name controller --pod-network-cidr=10.244.0.0/16

