MariaDB安装
sudo apt install mariadb-server
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

