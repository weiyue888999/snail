<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="refresh" content="1">
		<title>登录页</title>
		<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css" />
	</head>
	<body>
		<div class="login-page">
			<div class="login-form">
				<p class="error">用户名或密码错误</p>
				<div class="form-group">
					<div class="input-group">
						<div class="input-group-addon"><img src="<%=request.getContextPath()%>/assets/img/user.png"></div>
						<input type="text" class="form-control user-input" placeholder="用户名">
					</div>
				</div>
				<div class="form-group">
					<div class="input-group">
						<div class="input-group-addon"><img src="<%=request.getContextPath()%>/assets/img/pwd.png"></div>
						<input type="password" class="form-control pwd-input" placeholder="************">
						<img class="toggleIcon" src="<%=request.getContextPath()%>/assets/img/close.png">
					</div>
				</div>
				<div class="form-group">
					<%=request.getServletContext().getAttribute("com.github.snail.page_render_captchaLogin")%>
				</div>
				<div class="form-group">
					<p class="forget-pwd">
						<img src="<%=request.getContextPath()%>/assets/img/forget-pwd.png">
						<a href="#">忘记密码</a>
					</p>
				</div>
				<div class="form-group login-btn-wrap">
					<button type="submit" class="btn btn-block btn-primary login-btn">登 录</button>
				</div>
			</div>
		</div>
	</body>
</html>