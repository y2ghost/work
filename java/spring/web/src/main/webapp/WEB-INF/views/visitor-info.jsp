<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>访问信息</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
  <p>访问名字: ${visitor.name}</p>
  <p>访问次数: ${visitor.visitCounter}</p>
  <p>首次访问: ${visitor.firstVisitTime}</p>
</body>
</html>
