<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="frm"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>用户注册</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
span.error {
  color: red;
  display: inline-block;
}
</style>
</head>
<body>
  <h3>用户注册</h3>
  <frm:form action="register" method="post" modelAttribute="user">
    名字 <frm:input path="name" />
    <frm:errors path="name" cssClass="error" />
    <br />
    邮箱 <frm:input path="emailAddress" />
    <frm:errors path="emailAddress" cssClass="error" />
    <br />
    密码 <frm:password path="password" />
    <frm:errors path="password" cssClass="error" />
    <br />
    自定义验证 <input type="radio" name="custom" value="false"
      checked="checked" />否
    <input type="radio" name="custom" value="true" />是
    <br />
    <br />
    <input type="submit" value="提交" />
  </frm:form>
</body>
</html>
