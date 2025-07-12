<%-- 
    Document   : resetPassword
    Created on : May 31, 2025, 12:47:31 PM
    Author     : NGUYEN MINH
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Đặt lại mật khẩu</title>
        <link rel="stylesheet" type="text/css" href="css/resetPassword.css" />
    </head>
    <body>

        <div class="left-side"></div>

        <div class="reset-container">
            <div class="logo">
                <img src="image/logo.png" alt="Logo" />
            </div>
            <div class="company-name">DONG PHAT JOINT STOCK COMPANY</div>

            <h2>Đặt lại mật khẩu</h2>

            <form action="ResetPasswordController" method="post">
                <label for="newPassword">Mật khẩu mới</label>
                <input type="password" id="newPassword" name="newPassword" placeholder="Nhập mật khẩu mới" required />

                <label for="confirmPassword">Nhập lại mật khẩu</label>
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu" required />
                <input type="hidden" name="productController" value="${sessionScope.productController}" />
                <%-- ===== PHẦN CẬP NHẬT ===== --%>
                <%-- Hiển thị thông báo lỗi với lớp CSS mới --%>
                <c:if test="${not empty error}">
                    <p class="message error-message">${error}</p>
                </c:if>

                <%-- Hiển thị thông báo thành công với lớp CSS mới --%>
                <c:if test="${not empty success}">
                    <%-- Thêm class 'message' để có định dạng chung --%>
                    <p class="message success-message">${success}</p>
                </c:if>
                <%-- ========================== --%>

                <button type="submit" >Tiếp tục</button>

            </form>

            <div class="password-requirements">
                <div class="warning-icon"></div>
                <div>
                    - Tối thiểu 8 ký tự<br/>
                    - Có chữ hoa, chữ thường và số
                </div>
            </div>

            <div class="footer">© 2025 DPCRM from ISP490_SU25_GR4</div>
        </div>

    </body>
    <%-- Chỉ chạy script này KHI có thông báo thành công --%>
    <c:if test="${not empty success}">
        <script>
            // Hiển thị thông báo cho người dùng biết họ sẽ được chuyển trang
            console.log("Cập nhật mật khẩu thành công. Tự động chuyển đến trang đăng nhập sau 3 giây.");

            // Hàm thực hiện chuyển trang sau một khoảng thời gian
            setTimeout(function () {
                // Chuyển hướng người dùng đến trang login.jsp
                window.location.href = 'login.jsp';
            }, 3000); // 3000 mili giây = 3 giây
        </script>
    </c:if>
   <c:if test="${not empty isProductController and not empty success}">
        <script>
            // Hiển thị thông báo cho người dùng biết họ sẽ được chuyển trang
            console.log("Cập nhật mật khẩu thành công. Tự động chuyển đến trang đăng nhập sau 3 giây.");

            // Hàm thực hiện chuyển trang sau một khoảng thời gian
            setTimeout(function () {
                // Chuyển hướng người dùng đến trang login.jsp
                window.location.href = 'editProfile';
            }, 3000); // 3000 mili giây = 3 giây
        </script>
    </c:if>
        
</html>