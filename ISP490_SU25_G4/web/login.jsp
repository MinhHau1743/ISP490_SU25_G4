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
        <%-- SỬA 1: Liên kết đến file CSS duy nhất --%>
        <link rel="stylesheet" type="text/css" href="css/auth-style.css" />
    </head>

    <%-- SỬA 2: Thêm class cho body --%>
    <body class="auth-body page-login">
       
            <div class="left-side"></div>

            <%-- SỬA 3: Cập nhật class cho container --%>
            <div class="auth-container">
                <form id="login-form" class="form-box" action="auth" method="post">
                    <input type="hidden" name="action" value="login">

                    <div class="logo">
                        <img src="image/logo.png" alt="Logo" style="width: 150px;">
                    </div>
                    <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

                    <div class="input-group">
                        <input type="email" id="email" name="email" placeholder="Email" value="${param.email}" required>
                        <p id="email-error" class="error-message-client"></p> <%-- Class này cần được định nghĩa trong auth-style.css --%>
                    </div>

                    <div class="input-group">
                        <input type="password" id="password" name="password" placeholder="Mật khẩu" required>
                        <p id="password-error" class="error-message-client"></p>
                    </div>

                    <c:if test="${not empty error}">
                        <p class="message error-message">${error}</p>
                    </c:if>

                    <button type="submit">Đăng nhập</button>

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

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('login-form');
                const emailInput = document.getElementById('email');
                const passwordInput = document.getElementById('password');
                const emailError = document.getElementById('email-error');
                const passwordError = document.getElementById('password-error');

                form.addEventListener('submit', function (event) {
                    let isValid = true;

                    emailError.textContent = '';
                    emailInput.classList.remove('input-error');
                    passwordError.textContent = '';
                    passwordInput.classList.remove('input-error');

                    const emailValue = emailInput.value.trim();
                    const passwordValue = passwordInput.value.trim();
                    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

                    if (emailValue === '') {
                        emailError.textContent = 'Vui lòng nhập email.';
                        emailInput.classList.add('input-error');
                        isValid = false;
                    } else if (!emailRegex.test(emailValue)) {
                        emailError.textContent = 'Định dạng email không hợp lệ.';
                        emailInput.classList.add('input-error');
                        isValid = false;
                    }

                    if (passwordValue === '') {
                        passwordError.textContent = 'Vui lòng nhập mật khẩu.';
                        passwordInput.classList.add('input-error');
                        isValid = false;
                    }

                    if (!isValid) {
                        event.preventDefault();
                    }
                });
            });
        </script>
    </body>
</html>