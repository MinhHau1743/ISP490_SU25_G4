<%-- 
    Document   : resetPassword.jsp (Trang TẠO MỚI hoặc ĐẶT LẠI mật khẩu)
    Created on : Jun 7, 2025
    Author     : NGUYEN MINH
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <title>Tạo Mật Khẩu Mới</title>
            </c:when>
            <c:otherwise>
                <title>Đặt Lại Mật Khẩu</title>
            </c:otherwise>
        </c:choose>

        <%-- SỬA 1: Liên kết đến file CSS duy nhất --%>
        <link rel="stylesheet" type="text/css" href="css/auth-style.css" />
    </head>

    <%-- SỬA 2: Thêm class cho body --%>
    <body class="auth-body page-reset">
        <div class="left-side"></div>

        <%-- SỬA 3: Cập nhật class cho container --%>
        <div class="auth-container">
            <div class="logo"><img src="image/logo.png" alt="Logo" /></div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <h2>Tạo Mật Khẩu Mới</h2>
                </c:when>
                <c:otherwise>
                    <h2>Đặt Lại Mật Khẩu</h2>
                </c:otherwise>
            </c:choose>

            <form id="reset-form" action="auth" method="post">
                <input type="hidden" name="action" value="resetPassword">
                <label for="newPassword">Nhập mật khẩu mới</label>
                <input id="newPassword" type="password" name="newPassword" placeholder="Nhập mật khẩu mới" required />
                <p id="new-password-error" class="error-message-client"></p>
                <label for="confirmPassword">Nhập lại mật khẩu</label>
                <input id="confirmPassword" type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu" required />
                <p id="confirm-password-error" class="error-message-client"></p>
                <c:if test="${not empty error}"><p class="message error-message">${error}</p></c:if>
                <c:if test="${not empty success}"><p class="message success-message">${success}</p></c:if>
                    <button type="submit">Xác nhận</button>
                </form>

                <div class="password-requirements">
                    <div class="warning-icon"></div>
                    <div>- Tối thiểu 8 ký tự<br/>- Có chữ hoa, chữ thường và số</div>
                </div>
                <div class="footer">© 2025 DPCRM from ISP490_SU25_GR4</div>
            </div>

        <c:if test="${not empty success}">
            <script>
                console.log("Thao tác thành công. Tự động chuyển trang sau 3 giây.");
                setTimeout(() => {
                    window.location.href = 'login.jsp';
                }, 3000);
            </script>
        </c:if>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('reset-form');
                const newPass = document.getElementById('newPassword');
                const confirmPass = document.getElementById('confirmPassword');
                const newPassError = document.getElementById('new-password-error');
                const confirmPassError = document.getElementById('confirm-password-error');

                form.addEventListener('submit', function (event) {
                    let isValid = true;
                    newPassError.textContent = '';
                    newPass.classList.remove('input-error');
                    confirmPassError.textContent = '';
                    confirmPass.classList.remove('input-error');
                    const newPasswordValue = newPass.value;

                    if (newPasswordValue === '') {
                        newPassError.textContent = 'Vui lòng nhập mật khẩu mới.';
                        isValid = false;
                    } else {
                        if (!(/[A-Z]/.test(newPasswordValue) && /[a-z]/.test(newPasswordValue) && /[0-9]/.test(newPasswordValue) && newPasswordValue.length >= 8)) {
                            newPassError.textContent = 'Mật khẩu không đủ mạnh.';
                            isValid = false;
                        }
                    }
                    if (!isValid)
                        newPass.classList.add('input-error');

                    if (confirmPass.value === '') {
                        confirmPassError.textContent = 'Vui lòng nhập lại mật khẩu.';
                        isValid = false;
                    } else if (newPass.classList.length === 0 && newPasswordValue !== confirmPass.value) {
                        confirmPassError.textContent = 'Mật khẩu không khớp.';
                        isValid = false;
                    }
                    if (confirmPassError.textContent)
                        confirmPass.classList.add('input-error');

                    if (!isValid)
                        event.preventDefault();
                });
            });
        </script>
    </body>
</html>