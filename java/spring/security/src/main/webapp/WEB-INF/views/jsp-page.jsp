<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>JSP Security示例</title>
</head>
<body>
  <p>
    <%=request.getUserPrincipal().getName().toString()%>
  </p>
  <a href="/security/jsp-security">测试JSP-Security</a>
</body>
</html>
