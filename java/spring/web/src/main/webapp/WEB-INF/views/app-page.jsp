<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>APP测试</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
  <form action="" method="post">
    背景颜色 <input type="text" name="background" value="${style.background}" />
    字体大小 <input type="text" name="fontSize" value="${style.fontSize}" />
    <input type="submit" value="提交" />
  </form>
  <p>APP内容来自 ${uri}</p>
  <p>${msg}</p>
</body>
</html>
