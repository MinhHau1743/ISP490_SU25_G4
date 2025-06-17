<%-- 
    Document   : verifyOTP
    Created on : May 29, 2025, 10:48:23 AM
    Author     : NGUYEN MINH
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xác minh mã OTP</title>
        <link rel="stylesheet" type="text/css" href="css/verifyOTP.css" />
    </head>
    <body>
        <div class="container">
            <!-- Cột trái: ảnh + logo + tên công ty -->
            <div class="left-side">
                <img src="image/logo.png" alt="Logo">
                <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>
            </div>

            <!-- Cột phải: form xác minh -->
            <div class="right-side">
                <div class="verify-box">
                    <h3>Mã xác thực đã được gửi đến<br>"<%= session.getAttribute("email")%>"</h3>

                    <form action="VerifyOTPController" method="post">
                        <input type="text" name="otp" placeholder="Mã xác minh" required>
                        <%-- Dòng code mới --%>
                        <c:if test="${not empty error}">
                            <p class="error-message">${error}</p>
                        </c:if>
                        <button type="submit">Xác minh</button>
                    </form>

                    <div class="expired">
                        <img src="image/warning-icon.png" alt="Cảnh báo">
                        Mã sẽ hết hạn sau 5 phút
                    </div>
                </div>

                <div class="resend">
                    Bạn chưa nhận được mã? <a href="VerifyOTPController?action=resend">Gửi lại</a>
                </div>

                <div class="footer">
                    © 2025 DPCRM from ISP490_SU25_GR4
                </div>
            </div>
        </div>
    </body>
</html>
