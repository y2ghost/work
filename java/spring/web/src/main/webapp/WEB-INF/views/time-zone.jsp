<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="frm"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>自定义时区</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
  <form method="post" id="tzForm" action="/web/tzandi18n/timezone/custom">
    <input id="tzInput" type="hidden" name="timeZoneOffset"><br>
  </form>
  <script>
	var date = new Date();
	var offSet = date.getTimezoneOffset();
	document.getElementById("tzInput").value = offSet;
	document.getElementById("tzForm").submit();
  </script>
</body>
</html>
