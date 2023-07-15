
<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>订购商品</title>
</head>
<body>
  <div>用户信息: ${userInfo}</div>
  <br />
  <form action="/security/order/buy" method="post">
    商品名称: <input type="text" name="item" /> 商品数量: <input type="text"
      name="quantity" /> <br /> <input type="hidden"
      name="${_csrf.parameterName}" value="${_csrf.token}" /> <input
      type="submit" value="购买" />
  </form>
  <br />
  <br />
  <form action="/security/logout" method="post">
    <input type="hidden" name="${_csrf.parameterName}"
      value="${_csrf.token}" /> <input type="submit" value="登出">
  </form>
</body>
</body>
</html>
