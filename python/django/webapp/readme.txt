依赖安装
- pip install -r requirements.txt

项目初始化
- python manage.py makemigrations
- python manage.py migrate
- python manage.py createsuperuser

数据库访问
- python manage.py dbshell

代码调试
- python manage.py shell
- sqlite3常见命令: .tables / .schema

测试自定义登录页面
- 访问http://localhost:8000/bands/restricted_page/

加载数据
- python manage.py loaddata bands
- python manage.py loaddata promoters

