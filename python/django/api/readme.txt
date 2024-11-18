依赖安装
- pip install django
- pip install djangorestframework
- pip install whitenoise
- pip install django-cors-headers
- pip install dj-rest-auth
- pip install django-allauth
- pip install requests
- pip freeze > requirements.txt

创建项目和应用示例
- django-admin startproject api api 
- python manage.py startapp books

静态资源
- 编译静态资源: python manage.py collectstatic

注册账户
- http://127.0.0.1:8000/api/dj-rest-auth/registration/

