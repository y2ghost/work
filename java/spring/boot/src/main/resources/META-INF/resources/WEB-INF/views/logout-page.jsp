<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登出测试</title>
</head>
<body>
  <p>时间: ${time}</p>
  <form action="logout" method="post">
    <input type="hidden" name="${_csrf.parameterName}"
      value="${_csrf.token}" /> <input type="submit" value="登出">
  </form>
</body>
</html>
