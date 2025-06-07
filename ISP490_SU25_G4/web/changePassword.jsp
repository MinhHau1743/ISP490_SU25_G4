<%-- 
    Document   : changePassword
    Created on : June 4, 2025, 11:28:23 PM
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
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">       
        <link rel="stylesheet" href="css/changePassword.css">
    </head>
    <body>
        <!-- Loading Spinner -->
        <div id="loadingOverlay" style="
             position: fixed;
             top: 0;
             left: 0;
             width: 100%;
             height: 100%;
             background-color: rgba(255,255,255,0.9);
             display: flex;
             justify-content: center;
             align-items: center;
             z-index: 9999;
             ">
            <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                <span class="sr-only">Đang tải...</span>
            </div>
        </div>

        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Đổi mật khẩu</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>

                <div class="change-password-container">
                    <c:if test="${not empty sucessfullyMessage}">
                        <div id="customAlert" class="alert alert-success alert-dismissible fade show" role="alert" style="max-width: fit-content; min-width: 1900px; margin-bottom: 20px; padding-right: 3rem; position: relative;">
                            <strong>Success!</strong> ${sucessfullyMessage}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close" style="position: absolute; top: 10px; right: 10px;">
                                <span aria-hidden="true">&times;</span>
                            </button>

                            <!-- Progress bar -->
                            <div class="progress mt-2" style="height: 4px;">
                                <div id="alertProgressBar" class="progress-bar bg-success" role="progressbar" style="width: 100%; transition: width 5s linear;"></div>
                            </div>
                        </div>
                    </c:if>

                    <form action="changePassword" method="POST" class="change-password-form">
                        <div class="form-header">
                            Đổi mật khẩu
                        </div>
                        <div class="form-body">
                            <input type="hidden" value="${user.email}" name="email">
                            <div class="form-row">
                                <label for="currentPassword">Mật khẩu hiện tại</label>
                                <input type="password" id="currentPassword" value="${currentPassword}" name="currentPassword" placeholder="Nhập mật khẩu hiện tại" required>
                            </div>
                            <div class="form-row">
                                <label for="newPassword">Mật khẩu mới</label>
                                <input type="password" id="newPassword" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                            </div>
                            <div class="form-row">
                                <label for="confirmPassword">Nhập lại mật khẩu mới</label>
                                <input type="password" id="confirmNewPassword" name="confirmNewPassword" placeholder="Xác nhận mật khẩu mới" required>
                            </div>
                            <p style="color: red; font-size: 16px;">${errorMessage}</p>
                        </div>
                        <div class="form-footer">
                            <button type="button" class="btn-cancel">Hủy</button>
                            <button type="submit" class="btn-save">Lưu thông tin</button>
                        </div>
                    </form>
                </div>

            </main>
        </div>

        <script>

            feather.replace();
        </script>
        <script src="js/mainMenu.js"></script>
        <script src="js/changePassword.js"></script>
    </body>
</html>