# 导入KEY
rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc'
rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key'
rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key'

cp rabbitmq.repo /etc/yum.repos.d/rabbitmq.repo
dnf update -y
dnf install -y logrotate
dnf install -y erlang rabbitmq-server
systemctl enable rabbitmq-server
systemctl start rabbitmq-server
systemctl status  rabbitmq-server
systemctl stop rabbitmq-server
# 默认用户和密码: guest/guest

