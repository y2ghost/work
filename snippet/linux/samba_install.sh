sudo dnf install samba samba-common samba-client
sudo firewall-cmd --permanent --add-port={139/tcp,445/tcp}
sudo firewall-cmd --reload
sudo mv /etc/samba/smb.conf /etc/samba/smb.conf.orig
sudo cp smb.conf  smbshared.conf /etc/samba/
sudo mkdir /share
sudo mkdir /share/documents
sudo mkdir /share/public
sudo useradd -d /home/docUser -m docUser
sudo chown -R docUser:docUser /share
sudo smbpasswd -a docUser
# 测试配置
testparm
sudo systemctl enable smb nmb
sudo systemctl start smb nmb
# 测试服务
smbclient -U docUser -L localhost

