
<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>订单状态</title>
</head>
<body>
  <p>状态: ${status}</p>
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

