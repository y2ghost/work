samba安装
sudo apt install samba
sudo systemctl stop smbd
sudo mv /etc/samba/smb.conf /etc/samba/smb.conf.orig
sudo cp smb.conf  smbshared.conf /etc/samba/
sudo mkdir /share
sudo mkdir /share/documents
sudo mkdir /share/public
sudo useradd -d /home/docUser -m docUser
sudo chown -R docUser:docUser /share
sudo systemctl restart smbd
sudo systemctl status smbd

nfs安装
sudo mkdir /exports
sudo apt install nfs-kernel-server
sudo mv /etc/exports /etc/exports.orig
# 创建共享的目录
sudo mkdir /exports/backup
sudo mkdir /exports/documents
sudo mkdir /exports/public
sudo cp nfs_exports /etc/exports
sudo systemctl restart nfs-kernel-server
systemctl status nfs-kernel-server
# 客户端操作
sudo apt install nfs-common
mkdir documents
sudo mount <nfs-server>:/documents documents/

