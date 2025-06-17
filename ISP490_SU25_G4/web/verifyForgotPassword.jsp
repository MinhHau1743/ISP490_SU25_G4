<%-- 
    Document   : verifyForgotPassword
    Created on : May 31, 2025, 12:43:01 PM
    Author     : NGUYEN MINH
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Xác minh mã OTP - Quên mật khẩu</title>
        <link rel="stylesheet" type="text/css" href="css/verifyForgotPassword.css" />
    </head>
    <body>

        <div class="left-side"></div>

        <div class="verify-container">
            <div class="logo">
                <img src="image/logo.png" alt="Logo" />
            </div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <div class="info-text">
                Mã xác thực đã được gửi đến
                "<%= session.getAttribute("email") != null ? session.getAttribute("email") : "email@gmail.com"%>"
            </div>

            <form action="VerifyResetOTPController" method="post">
                <label class="input-label" for="verificationCode">Nhập mã xác minh gồm 6 số</label>
                <input type="text" id="verificationCode" name="verificationCode" placeholder="Mã xác minh" required maxlength="6" pattern="\d{6}" title="Vui lòng nhập đúng 6 chữ số" />
                <%-- Dòng code mới --%>
                <c:if test="${not empty error}">
                    <p class="error-message">${error}</p>
                </c:if>
                <button type="submit" class="verify-btn">Xác minh</button>

            </form>

            <div class="expire-message">
                <div class="expire-icon"></div>
                Mã sẽ hết hạn sau 5 phút
            </div>

            <div class="footer">© 2025 DPCRM from ISP490_SU25_GR4</div>
        </div>

    </body>
</html>


