# 服务端操作
sudo dnf install rpcbind nfs-utils
sudo systemctl enable rpcbind
sudo systemctl enable nfs-server
sudo systemctl stop rpcbind
sudo systemctl stop nfs-server
sudo firewall-cmd --zone= --permanent --add-service=mountd
sudo firewall-cmd --zone= --permanent --add-service=nfs
sudo firewall-cmd --zone= --permanent --add-service=rpc-bind
sudo firewall-cmd --reload
sudo mkdir -p /exports/temp
echo "/exports/temp *(rw,sync)" | sudo tee /etc/exports.d/temp.exports
sudo systemctl start rpcbind
sudo systemctl start nfs-server

# 客户端操作
sudo dnf install nfs-utils
mkdir nfs-temp
sudo mount -t nfs <server>:/exports/temp nfs-temp

