该项目属于大杂烩，主要是产品环境的django技巧汇总

说明
pipenv在python3.8环境中，安装psycopg2-binary存在问题
所以采用docker构建镜像时安装，开发环境可以采取pipenv shell激活环境
后执行如下命令:
pip install psycopg2-binary==2.8.6

搭建react前端框架步骤
npx create-react-app frontend
cd frontend
npm start
备注:
用于演示前后端分离，后端是todos应用

构建docker镜像
docker build .

后台运行
docker-compose up -d

使用postgresql数据库说明
容器运行后需要执行如下操作
docker-compose exec web bash
进入命令交互界面后执行如下命令
python manage.py makemigrations
python manage.py migrate
python manage.py createsuperuser

认证相关的URL地址
accounts/login/ [name='login']
accounts/logout/ [name='logout']
accounts/password_change/ [name='password_change']
accounts/password_change/done/ [name='password_change_done']
accounts/password_reset/ [name='password_reset']
accounts/password_reset/done/ [name='password_reset_done']
accounts/reset/<uidb64>/<token>/ [name='password_reset_confirm']
accounts/reset/done/ [name='password_reset_complete']

