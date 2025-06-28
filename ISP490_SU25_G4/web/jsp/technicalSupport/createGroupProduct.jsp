<%-- 
    Document   : createGroupProduct
    Created on : Jun 14, 2025, 1:36:00 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="listProduct"/>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">       
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createGroupProduct.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thêm nhóm hàng</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>
                <section class="content-body">
                    <div class="form-container">
                        <form action="${pageContext.request.contextPath}/ProductCategories" method="POST">
                            <div class="form-header">
                                <h2>Thêm nhóm các sản phẩm</h2>
                            </div>

                            <div class="form-group inline-group">
                                <label for="groupName">Tên nhóm sản phẩm:</label>
                                <input type="text" id="groupName" name="groupName" required>
                            </div>
                            <div class="form-actions">
                                <a href="${pageContext.request.contextPath}/ProductController" class="btn btn-cancel" style="text-decoration: none;">Hủy</a>
                                <button type="submit" class="btn btn-submit">Thêm và đóng</button>
                            </div>
                        </form>
                    </div>
                </section>
            </main>
        </div>
        <script>
            feather.replace();
        </script>

        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>

