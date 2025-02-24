<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="frm"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<title>文件上传</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
  <h3>请您选择文件上传</h3>
  <br />
  <form action="upload" enctype="multipart/form-data" method="post">
    文件: <input type="file" name="user-file"> <br />
    <input type="submit" value="上传">
  </form>
</body>
</html>
