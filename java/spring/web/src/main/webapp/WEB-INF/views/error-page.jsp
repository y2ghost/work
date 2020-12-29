<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>异常处理</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<h3>通用异常处理信息</h3>
 <p>异常类型: ${exceptionType}</p>
 <p>处理函数: ${handlerMethod}</p>
 <p>拦截器类: ${resolver}</p>
</body>
</html>
