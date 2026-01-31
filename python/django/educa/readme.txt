依赖安装
- pip install django
- pip install Pillow
- pip install python-decouple
- pip install django-braces
- pip install django-embed-video
- pip install redis
- pip install django-debug-toolbar
- pip install django-redisboard
- pip install djangorestframework
- pip install -U 'channels[daphne]'
- pip install channels-redis
- pip freeze > requirements.txt

依赖服务
- redis
- sqlite / mariadb server

依赖文件
- python -m pip install -r requirements.txt

可选依赖
- python-decouple 用于管理配置文件的工具

静态资源整理
- python manage.py collectstatic

动态密钥
- python -c "import secrets; print(secrets.token_urlsafe())"

slug概述
- 由字母、数字、_ 和 - 组成的短标签

导出数据
- ```bash
- mkdir courses/fixtures
- python manage.py dumpdata courses \
  --indent=2 --output=courses/fixtures/subjects.json
- ```

加载数据
- python manage.py loaddata subjects.json

