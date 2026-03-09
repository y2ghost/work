# 卸载gitlab
sudo yum -y erase gitlab-ce
sudo rm -rf /etc/gitlab/ /opt/gitlab/ /var/opt/gitlab/
sudo rm -rf /etc/sysctl.d/90-omnibus-gitlab-*
sudo rm -rf /var/log/gitlab
sudo rm -f /etc/systemd/system/multi-user.target.wants/gitlab-runsvdir.service
sudo rm -f /usr/lib/systemd/system/gitlab-runsvdir.service

