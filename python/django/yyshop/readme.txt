依赖安装
- pip install django
- pip install Pillow
- pip install djangorestframework
- pip install celery
- pip install redis
- pip install stripe
- pip install python-decouple
- pip install WeasyPrint
- pip freeze > requirements.txt

依赖服务
- redis
- sqlite / mariadb server

依赖文件
- python -m pip install -r requirements.txt

可选依赖
- python-decouple 用于管理配置文件的工具

创建项目和应用示例
- django-admin startproject yyshop
- python manage.py startapp shop

静态资源整理
- python manage.py collectstatic

动态密钥
- python -c "import secrets; print(secrets.token_urlsafe())"

slug概述
- 由字母、数字、_ 和 - 组成的短标签

导出数据
- python -Xutf8 manage.py dumpdata --indent=2 --output=mysite_data.json

celery-worker启动
- celery -A yyshop worker -l info

flower说明
- 安装: pip install flower
- 运行: celery -A yyshop flower
- 监控: http://localhost:5555

