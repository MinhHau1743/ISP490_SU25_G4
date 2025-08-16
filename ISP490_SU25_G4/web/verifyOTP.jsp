<%-- 
    Document   : verifyOTP.jsp
    Created on : May 29, 2025
    Author     : NGUYEN MINH
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Xác minh mã OTP</title>
        <%-- SỬA 1: Liên kết đến file CSS duy nhất --%>
        <link rel="stylesheet" type="text/css" href="css/auth-style.css" />
    </head>

    <%-- SỬA 2: Thêm class cho body --%>
    <body class="auth-body page-verify">

        <%-- SỬA 3: Tái cấu trúc HTML để nhất quán --%>
        <div class="left-side"></div>

        <div class="auth-container">
            <div class="logo">
                <img src="image/logo.png" alt="Logo" />
            </div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <div class="info-text">
                Mã xác thực đã được gửi đến
                "<b>${sessionScope.email}</b>" <%-- Sửa: Dùng JSTL/EL --%>
            </div>

            <form id="otp-form" action="auth" method="post"> <%-- Sửa: action --%>
                <input type="hidden" name="action" value="verifyOTP"> <%-- Thêm: action ẩn --%>

                <label class="input-label" for="otp">Nhập mã xác minh gồm 6 số</label>
                <input type="text" id="otp" name="otp" placeholder="Mã xác minh" required maxlength="6" pattern="\d{6}">
                <p id="otp-error" class="error-message-client"></p>

                <c:if test="${not empty error}">
                    <p class="message error-message">${error}</p>
                </c:if>
                <c:if test="${not empty message}">
                    <p class="message success-message">${message}</p>
                </c:if>

                <button type="submit">Xác minh</button>
            </form>

            <div class="resend-link">
                Bạn chưa nhận được mã? <a href="auth?action=resendOTP">Gửi lại</a> <%-- Sửa: href --%>
            </div>

            <div class="expire-message">
                Mã sẽ hết hạn sau 5 phút
            </div>

            <div class="footer">
                © 2025 DPCRM from ISP490_SU25_GR4
            </div>
        </div>

        <%-- SỬA 4: Thêm JavaScript Validation --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('otp-form');
                const otpInput = document.getElementById('otp');
                const otpError = document.getElementById('otp-error');

                form.addEventListener('submit', function (event) {
                    otpError.textContent = '';
                    otpInput.classList.remove('input-error');
                    const otpValue = otpInput.value.trim();
                    const otpRegex = /^\d{6}$/;

                    if (otpValue === '') {
                        otpError.textContent = 'Vui lòng nhập mã OTP.';
                        otpInput.classList.add('input-error');
                        event.preventDefault();
                    } else if (!otpRegex.test(otpValue)) {
                        otpError.textContent = 'Mã OTP phải là 6 chữ số.';
                        otpInput.classList.add('input-error');
                        event.preventDefault();
                    }
                });
            });
        </script>
    </body>
</html>