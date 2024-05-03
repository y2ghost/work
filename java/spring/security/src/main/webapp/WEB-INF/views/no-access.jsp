<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>无权限访问</title>
</head>
<body>
  <strong style="color: red">用户无权执行操作</strong>
  <br />
  <br />
  <div>用户信息: ${userInfo}</div>
  <br />
  <br />
  <form action="/security/logout" method="post">
    <input type="hidden" name="${_csrf.parameterName}"
      value="${_csrf.token}" /> <input type="submit" value="登出">
  </form>
</body>
</body>
</html>
