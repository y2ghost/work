安装配置说明
- 需要准备mysql/mariadb数据库环境
- 配置项目文件src\main\resources\application.yml
  > 修改数据库的配置，适配自己的开发环境
- 创建并进入数据库activiti，执行user.sql脚本

运行项目
- 测试账户和密码: bajie/1, wukong/1
- 登陆: http://localhost:8080/layuimini/page/login-1.html

动态表单使用说明
- 控件命名约束: FormProperty_0ueitp2-_!类型-_!名称-_!默认值-_!是否参数
- ID: 自行标号同一流程定义无重复
- 类型: string、long、cUser（cUser为自定义类型读取用户列表）
- 默认值: 无、字符、FormProperty_开头定义过的控件ID
- 是否参数: f为不是参数，s是字符，t是时间
- 表单例子
  > FormProperty_0lovri0-_!string-_!姓名-_!请输入姓名-_!f
  > FormProperty_1iu6onu-_!long-_!年龄-_!请输入年龄-_!s
  > FormProperty_2rd4dtv-_!cUser-_!执行人-_!无-_!s
- 注意:表单Key必须要任务编号一致
  > 因为参数需要任务key，但是无法获取
  > 只能获取表单key "task.getFormKey()"当做任务key

