项目概述
- 项目属于大杂烩，主要是产品环境的django技巧汇总

搭建react前端框架步骤
- npx create-react-app frontend
- cd frontend
- npm start
- 备注: 用于演示前后端分离，后端是todos应用

django依赖安装
- pip install environs
- pip install dj-database-url
- pip install djangorestframework
- pip install django-cors-headers
- pip install django-crispy-forms
- pip install psycopg2-binary

项目初始化
- export DJANGO_SECRET_KEY='django-insecure-44e7hhqvgq*1be23s+torzj-3il4+%u8y32!fp8z7t-dd%uf3!'
- export DJANGO_DEBUG=True
- python manage.py makemigrations
- python manage.py migrate
- python manage.py createsuperuser

认证相关的URL地址
- accounts/login/ [name='login']
- accounts/logout/ [name='logout']
- accounts/password_change/ [name='password_change']
- accounts/password_change/done/ [name='password_change_done']
- accounts/password_reset/ [name='password_reset']
- accounts/password_reset/done/ [name='password_reset_done']
- accounts/reset/<uidb64>/<token>/ [name='password_reset_confirm']
- accounts/reset/done/ [name='password_reset_complete']

