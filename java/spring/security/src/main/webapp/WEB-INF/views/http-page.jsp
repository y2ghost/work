<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>测试角色权限</title>
</head>
<body>
  <p>
  网址: ${uri}<br/>用户: ${user}<br/>角色: ${roles}<br/><br/>
  <a href="/security/http/admin/">/admin/</a><br />
  <a href="/security/http/user/">/user/</a><br/>
  <a href="/security/http/other/">/other/</a><br/>
  <a href="/security/http/guest/">/guest/</a><br/><br/>
  </p>
  <form action="/security/logout" method="post">
    <input type="hidden" name="${_csrf.parameterName}"
      value="${_csrf.token}" /> <input type="submit" value="登出">
  </form>
</body>
</body>
</html>
