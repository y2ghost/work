依赖安装
- pip install django
- pip install djangorestframework
- pip install django-taggit
- pip install markdown
- pip install psycopg2
- pip install pillow
- pip install requests
- pip install easy-thumbnails
- pip install django-debug-toolbar
- pip install redis
- pip freeze > requirements.txt

依赖文件
- python -m pip install -r requirements.txt

可选依赖
- python-decouple 用于管理配置文件的工具

创建项目和应用示例
- django-admin startproject yysite
- python manage.py startapp blog

静态资源
- 编译静态资源: python manage.py collectstatic

动态密钥
- python -c "import secrets; print(secrets.token_urlsafe())"

postgresql库
- 生产环境建议: pip install psycopg2

slug概述
- 由字母、数字、_ 和 - 组成的短标签

导出数据
- python -Xutf8 manage.py dumpdata --indent=2 --output=mysite_data.json

