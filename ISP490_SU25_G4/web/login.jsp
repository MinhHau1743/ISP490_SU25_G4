<%-- 
    Document   : login
    Created on : May 29, 2025, 10:48:23 AM
    Author     : NGUYEN MINH
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Đăng nhập - CRM Đồng Phát</title>
        <link rel="stylesheet" type="text/css" href="css/login.css" />
    </head>
    <body>
        <div class="container">
            <div class="left"></div>
            <div class="right">
                <form class="form-box" action="LoginController" method="post">
                    <img src="image/logo.png" alt="Logo" width="220" style="display:block; margin:0 auto 0px;">
                    <div class="company-name" style="color:red; text-align:center; margin-top: 10px; margin-bottom: 20px;">DONG PHAT JOINT STOCK COMPANY</div>
                    <div class="input-group">
                        <input type="email" name="email" placeholder="Email" required>
                    </div>
                    <div class="input-group">
                        <input type="password" name="password" placeholder="Mật khẩu" required>
                    </div>
                    <c:if test="${not empty error}">
                        <p style="color:red">${error}</p>
                    </c:if>
                    <button type="submit" class="btn">Đăng nhập</button>

                    <div class="or-divider"><span>Hoặc</span></div>
                    <div class="register-link">
                        <a href="forgotPassword.jsp">Quên mật khẩu</a>
                    </div>
                </form>
                <div class="footer">
                    © 2025 DPCRM from ISP490_SU25_GR4
                </div>
            </div>
        </div>
    </body>
</html>
