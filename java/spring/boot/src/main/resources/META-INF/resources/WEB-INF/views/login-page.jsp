<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>登录页面</title>
</head>
<body style='margin: 50px;'>
  <h2>自定义登录页面</h2>
  <form action="/my-login" method="post">
    <%
    if (null != request.getParameter("error")) {
        out.write("<p style='color: red'>错误的用户名或密码！</p>");
    }

    if (null != request.getParameter("logout")) {
        out.write("<p style='color: blue'>你已经成功登出！</p>");
    }
    %>
    <p><label for="username">用户</label><input type="text" id="username" name="username" /></p>
    <p><label for="password">密码</label><input type="password" id="password" name="password" /></p>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <button type="submit">登录</button>
  </form>
</body>
</html>
