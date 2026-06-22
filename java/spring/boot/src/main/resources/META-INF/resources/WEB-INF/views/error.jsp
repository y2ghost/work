<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<title>JSP错误页面</title>
<style>
table td {
	vertical-align: top;
	border: solid 1px #888;
	padding: 10px;
}
</style>
</head>
<body>
  <h1>自定义全局错误页面</h1>
  <table>
    <tr>
      <td>时间</td>
      <td>${timestamp}</td>
    </tr>
    <tr>
      <td>错误</td>
      <td>${error}</td>
    </tr>
    <tr>
      <td>状态码</td>
      <td>${status}</td>
    </tr>
    <tr>
      <td>消息</td>
      <td>${message}</td>
    </tr>
    <tr>
      <td>异常</td>
      <td>${exception}</td>
    </tr>
    <tr>
      <td>错误栈</td>
      <td><pre>${trace}</pre></td>
    </tr>
  </table>
</body>
</html>
