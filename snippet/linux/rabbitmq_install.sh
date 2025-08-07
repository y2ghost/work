# 导入KEY
sudo rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/rabbitmq-release-signing-key.asc'
sudo rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-erlang.E495BB49CC4BBE5B.key'
sudo rpm --import 'https://github.com/rabbitmq/signing-keys/releases/download/3.0/cloudsmith.rabbitmq-server.9F4587F226208342.key'

sudo cp rabbitmq.repo /etc/yum.repos.d/rabbitmq.repo
sudo dnf update -y
sudo dnf install -y logrotate
sudo dnf install -y erlang rabbitmq-server
sudo systemctl enable rabbitmq-server
sudo systemctl start rabbitmq-server
sudo systemctl status  rabbitmq-server
sudo systemctl stop rabbitmq-server
# 默认用户和密码: guest/guest

