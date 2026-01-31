MariaDB安装
sudo apt install mariadb-server

Mariadb官方安装
sudo apt-get install apt-transport-https curl
sudo mkdir -p /etc/apt/keyrings
sudo curl -o /etc/apt/keyrings/mariadb-keyring.pgp 'https://mariadb.org/mariadb_release_signing_key.pgp'
echo "deb [signed-by=/etc/apt/keyrings/mariadb-keyring.pgp] https://mirrors.tuna.tsinghua.edu.cn/mariadb/repo/11.4/ubuntu jammy main" | sudo tee /etc/apt/sources.list.d/mariadb.list
sudo apt-get update
sudo apt-get install mariadb-server

初始化数据库
systemctl status mariadb
sudo mariadb-secure-installation
sudo mariadb
mariadb -uroot -p

数据库管理
1) 进入命令行界面
mariadb -uroot -p
2) 创建远程登录用户
CREATE USER 'root'@'%' IDENTIFIED BY '<password>';
CREATE USER 'admin'@'%' IDENTIFIED BY 'admin123456';
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%';
SHOW GRANTS FOR 'admin'@'%';
DROP USER 'admin'@'%';
flush privileges;
# 查看表的列
SHOW COLUMNS IN mysql.user;

