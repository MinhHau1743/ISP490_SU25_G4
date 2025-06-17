<%--
    Document   : register
    Created on : May 29, 2025, 10:48:23 AM
    Author     : NGUYEN MINH
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Đăng ký tài khoản DPCMR</title>
        <link rel="stylesheet" type="text/css" href="css/register.css" />
    </head>
    <body>

        <div class="main-container">
            <div class="illustration">
                <img src="image/register.png" alt="CRM Illustration">
            </div>
            <div class="form-container">
                <div class="form-box">
                    <h2>Tạo tài khoản DPCMR</h2>
                    <form method="post" action="RegisterController">
                        <label for="email">Địa chỉ email của bạn</label>
                        <input type="email" id="email" name="email" placeholder="name@gmail.com" required value="${param.email}"> <%-- Giữ lại email đã nhập khi có lỗi --%>

                        <div class="checkbox">
                            <%-- Thay đổi name và id thành 'terms' cho rõ nghĩa --%>
                            <input type="checkbox" id="terms" name="terms" required>
                            <%-- Sửa lại label cho đúng với chức năng --%>
                            <label for="terms" style="display:inline" >Tôi đồng ý với <a href="#">Điều khoản dịch vụ</a></label>
                        </div>

                        <c:if test="${not empty error}">
                            <p style="color:red; margin-top: 10px;">${error}</p>
                        </c:if>

                        <button type="submit" class="btn">Đăng Kí</button>

                        <div class="footer">
                            © 2025 DPCRM from ISP490_SU25_GR4
                        </div>
                    </form>
                </div>
                <div class="login-link">
                    Bạn đã có tài khoản? <a href="login.jsp">Đăng nhập</a>
                </div>
            </div>
        </div>

    </body>
</html>