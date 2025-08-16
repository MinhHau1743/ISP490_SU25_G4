<%-- 
    Document   : forgotPassword.jsp
    Created on : May 31, 2025, 12:32:12 PM
    Author     : NGUYEN MINH
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Quên mật khẩu</title>
        <%-- SỬA 1: Liên kết đến file CSS duy nhất --%>
        <link rel="stylesheet" type="text/css" href="css/auth-style.css" />
    </head>

    <%-- SỬA 2: Thêm class cho body --%>
    <body class="auth-body page-forgot">
        <div class="left-side"></div>

        <%-- SỬA 3: Cập nhật class cho container --%>
        <div class="auth-container">
            <div class="logo">
                <img src="image/logo.png" alt="Logo" />
            </div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <form id="forgot-form" action="auth" method="post">
                <input type="hidden" name="action" value="forgotPassword">
                <div class="form-group">
                    <input type="email" id="email" name="email" placeholder="Nhập email của bạn" value="${param.email}" required />
                    <p id="email-error" class="error-message-client"></p> <%-- Class này cần định nghĩa trong auth-style.css --%>
                </div>
                <button type="submit">Gửi mã xác nhận OTP</button>
            </form>

            <c:if test="${not empty error}">
                <p class="message error-message">${error}</p>
            </c:if>

            <a href="login.jsp" class="back-login">Quay lại đăng nhập</a>

            <div class="footer">
                © 2025 DPCRM from ISP490_SU25_GR4
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('forgot-form');
                const emailInput = document.getElementById('email');
                const emailError = document.getElementById('email-error');

                form.addEventListener('submit', function (event) {
                    let isValid = true;
                    emailError.textContent = '';
                    emailInput.classList.remove('input-error');

                    const emailValue = emailInput.value.trim();
                    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

                    if (emailValue === '') {
                        emailError.textContent = 'Vui lòng nhập email.';
                        isValid = false;
                    } else if (!emailRegex.test(emailValue)) {
                        emailError.textContent = 'Định dạng email không hợp lệ.';
                        isValid = false;
                    }

                    if (!isValid) {
                        event.preventDefault();
                        emailInput.classList.add('input-error');
                    }
                });
            });
        </script>
    </body>
</html>