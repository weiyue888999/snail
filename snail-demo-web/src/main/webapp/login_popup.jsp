<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>登录页</title>
		<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
		<script type="text/javascript" src="<%=request.getContextPath()%>/assets/js/jquery-1.11.1-min.js"></script>
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css" />
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css" />
	</head>
	<body>
		<div class="login-page">
			<div class="login-form">
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
					<p class="forget-pwd">
						<img src="<%=request.getContextPath()%>/assets/img/forget-pwd.png">
						<a href="#">忘记密码</a>
					</p>
				</div>
				<div class="form-group login-btn-wrap">
					<button id="login_button" type="submit" class="btn btn-block btn-primary login-btn">登 录</button>
				</div>
				<div class="form-group">
					<%=request.getServletContext().getAttribute("com.github.snail.page_render_captchaXk")%>
				</div>
			</div>
		</div>
		<script type="text/javascript">
		(function(){
			$(document).ready(function(){
				var captchaXk = new zfdunCaptcha({
					onVerifySuccess:function(){
						alert("验证成功!!!");
						window.open("http://www.baidu.com");
					},
					onVerifyFail:function(){
						alert("验证成功失败!!!")
					},
				});
				window.captchaXk = captchaXk;
			})
		})();
			$("#login_button").click(function(){
				console.log("点击弹出对话框");
				captchaXk.popup();
			});
		</script>
	</body>
</html>