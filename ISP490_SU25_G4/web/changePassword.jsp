<%-- 
    Document   : changePassword
    Created on : June 4, 2025
    Author     : minhnhnhe172717
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="changePassword" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đổi mật khẩu</title>

        <%-- Giữ lại các CSS chung của ứng dụng --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">

        <%-- SỬA 1: Thay thế changePassword.css bằng auth-style.css --%>
        <link rel="stylesheet" href="css/auth-style.css">
    </head>

    <%-- SỬA 2: Thêm class để kích hoạt style từ file gộp --%>
    <body class="page-change-password-internal">
        <div id="loadingOverlay">...</div>

        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="pageTitle" value="Đổi mật khẩu"/>
                </jsp:include>

                <div class="change-password-container">
                    <c:if test="${not empty sucessfullyMessage}">
                        <div id="customAlert" class="alert alert-success alert-dismissible fade show" role="alert">
                            <strong>Thành công!</strong> ${sucessfullyMessage}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                            <div class="progress mt-2" style="height: 4px;">
                                <div id="alertProgressBar" class="progress-bar bg-success" role="progressbar" style="width: 100%; transition: width 5s linear;"></div>
                            </div>
                        </div>
                    </c:if>

                    <form action="auth" method="POST" class="change-password-form">
                        <input type="hidden" name="action" value="changePassword">
                        <input type="hidden" value="${sessionScope.user.email}" name="email">

                        <div class="form-header">Đổi mật khẩu</div>
                        <div class="form-body">
                            <div class="form-row">
                                <label for="currentPassword">Mật khẩu hiện tại</label>
                                <input type="password" id="currentPassword" name="currentPassword" placeholder="Nhập mật khẩu hiện tại" required>
                            </div>
                            <div class="form-row">
                                <label for="newPassword">Mật khẩu mới</label>
                                <input type="password" id="newPassword" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                            </div>
                            <div class="form-row">
                                <label for="confirmNewPassword">Nhập lại mật khẩu mới</label>
                                <input type="password" id="confirmNewPassword" name="confirmNewPassword" placeholder="Xác nhận mật khẩu mới" required>
                            </div>
                            <c:if test="${not empty errorMessage}">
                                <p style="color: red; font-size: 16px; text-align: center;">${errorMessage}</p>
                            </c:if>
                        </div>
                        <div class="form-footer">
                            <a href="dashboard" class="btn-cancel">Hủy</a>
                            <button type="submit" class="btn-save">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <script> feather.replace();</script>
        <script src="js/mainMenu.js"></script>
        <script src="js/changePassword.js"></script>
    </body>
</html>