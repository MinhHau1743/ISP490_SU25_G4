<%--
    Document   : layout.jsp
    Description: Template giao diện chính cho toàn bộ ứng dụng DPCRM.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <%-- Lấy tiêu đề từ trang con, nếu không có thì dùng tiêu đề mặc định --%>
        <title><decorator:title default="DPCRM - Quản lý khách hàng"/> | FPT</title>

        <%-- CSS Libraries --%>
        <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700&display=swap" rel="stylesheet">
        
        <%-- CSS trung tâm của ứng dụng --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"> <%-- Tệp CSS mới --%>

        <%-- Cho phép các trang con chèn thêm CSS riêng nếu cần --%>
        <decorator:head />
    </head>
    
    <body class="${cookie.sidebar_collapsed.value == 'true' ? 'sidebar-collapsed' : ''}">
        <div class="wrapper">
            <%-- ===== 1. THANH ĐIỀU HƯỚNG (SIDEBAR) ===== --%>
            <jsp:include page="/jsp/mainMenu.jsp" />

            <%-- ===== 2. NỘI DUNG CHÍNH CỦA TRANG ===== --%>
            <main class="main-content">
                
                <%-- Header của trang (chứa tiêu đề và các nút hành động) --%>
                <header class="header">
                    <h1 class="page-title"><decorator:getProperty property="page.title" /></h1>
                    <div class="header-actions">
                        <%-- Cho phép trang con thêm nút vào header --%>
                        <decorator:getProperty property="page.header_actions" />
                    </div>
                </header>
                
                <%-- Phần thân của trang con sẽ được chèn vào đây --%>
                <decorator:body />
                
            </main>
        </div>

        <%-- JavaScript Libraries --%>
        <script src="https://unpkg.com/feather-icons"></script>
        
        <%-- JavaScript chung của ứng dụng --%>
        <script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
        
        <%-- Kích hoạt Feather Icons và các script chung khác --%>
        <script>
            // Luôn chạy sau khi trang đã tải xong
            document.addEventListener("DOMContentLoaded", function() {
                feather.replace();
            });
        </script>

        <%-- Cho phép các trang con chèn thêm JS riêng nếu cần --%>
        <decorator:getProperty property="page.script" />
    </body>
</html>