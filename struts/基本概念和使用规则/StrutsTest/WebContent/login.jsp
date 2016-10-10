<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Login Page</title>
	</head>
	<body>
		<s:form action="login">
			<s:textfield name="username" key="user"/>
			<s:textfield name="passwd" key="pass"/>
			<s:submit key="submit"/>
		</s:form>
	</body>
</html>