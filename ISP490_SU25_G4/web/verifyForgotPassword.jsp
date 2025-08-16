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
        <%-- SỬA 1: Liên kết đến file CSS duy nhất --%>
        <link rel="stylesheet" type="text/css" href="css/auth-style.css" />
    </head>

    <%-- SỬA 2: Thêm class cho body --%>
    <body class="auth-body page-verify">
        <div class="left-side"></div>

        <%-- SỬA 3: Cập nhật class cho container --%>
        <div class="auth-container">
            <div class="logo">
                <img src="image/logo.png" alt="Logo" />
            </div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <div class="info-text">
                Mã xác thực đã được gửi đến
                "<b>${sessionScope.email}</b>"
            </div>

            <form id="otp-form" action="auth" method="post">
                <input type="hidden" name="action" value="verifyOTP">
                <label class="input-label" for="otp">Nhập mã xác minh gồm 6 số</label>
                <input type="text" id="otp" name="otp" placeholder="Mã xác minh" required maxlength="6" pattern="\d{6}" title="Vui lòng nhập đúng 6 chữ số" />
                <p id="otp-error" class="error-message-client"></p> <%-- Class này cần định nghĩa trong auth-style.css --%>

                <c:if test="${not empty error}">
                    <p class="message error-message">${error}</p>
                </c:if>
                <c:if test="${not empty message}">
                    <p class="message success-message">${message}</p>
                </c:if>

                <button type="submit" class="verify-btn">Xác minh</button>
            </form>

            <div class="resend-link">
                Chưa nhận được mã? <a href="auth?action=resendOTP">Gửi lại</a>
            </div>

            <div class="expire-message">
                <div class="expire-icon"></div>
                Mã sẽ hết hạn sau 5 phút
            </div>

            <div class="footer">© 2025 DPCRM from ISP490_SU25_GR4</div>
        </div>

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