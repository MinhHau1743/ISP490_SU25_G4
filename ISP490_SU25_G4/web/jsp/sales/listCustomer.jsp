<%--
    Document   : listCustomer
    Created on : Jun 17, 2025
    Author     : NGUYEN MINH
    Description: Displays a Kanban board of all customers, categorized by type, with a collapsible menu.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="listCustomer" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách hàng</title>

        <%-- Google Fonts and Feather Icons --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- Stylesheets --%>
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCustomer.css">

        <%-- === CSS MỚI CHO BỐ CỤC VÀ MENU THU GỌN === --%>
        <style>
            /* Căn giữa các cột Kanban */
            .customer-board-container {
                display: flex;
                justify-content: center; /* This will center the .customer-board */
                width: 100%;
            }

            .customer-board {
                display: inline-flex; /* Make the board only as wide as its columns */
                gap: 16px;
            }
            
            /* CSS cho menu khi được thu gọn */
            /* Giả sử thanh menu của bạn có class là .main-menu */
            .app-container.menu-collapsed .main-menu {
                width: 80px; /* Chiều rộng của menu khi thu gọn */
            }
            .app-container.menu-collapsed .main-menu .nav-link span,
            .app-container.menu-collapsed .main-menu .menu-header,
            .app-container.menu-collapsed .main-menu .user-profile .user-name,
            .app-container.menu-collapsed .main-menu .user-profile .user-role {
                display: none; /* Ẩn chữ đi, chỉ giữ lại icon */
            }
            .app-container.menu-collapsed .main-menu .nav-link {
                justify-content: center;
            }
             .app-container.menu-collapsed .main-menu #menu-toggle-btn i {
                transform: rotate(180deg); /* Xoay icon mũi tên */
            }

            .app-container.menu-collapsed .main-content {
                margin-left: 80px; /* Điều chỉnh lề của nội dung chính */
            }
            
            /* Thêm hiệu ứng chuyển động mượt mà */
            .main-menu, .main-content, .main-menu #menu-toggle-btn i {
                transition: all 0.3s ease-in-out;
            }
        </style>

    </head>
    <body>
        <div class="app-container"> <%-- Thêm class 'menu-collapsed' ở đây nếu muốn mặc định thu gọn --%>
            
            <%-- Include the main menu component --%>
            <%-- Giả sử trong file mainMenu.jsp có nút <button id="menu-toggle-btn"><<</button> --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <h1 class="title">Danh sách Khách hàng</h1>
                    </div>
                    <div class="header-actions">
                        <button class="notification-btn"><i data-feather="bell"></i></button>
                    </div>
                </header>

                <div class="page-content">
                    <c:if test="${not empty errorMessage}">
                        <div class="error-message" style="background-color: #ffebee; color: #c62828; padding: 16px; margin-bottom: 16px; border-radius: 8px; border: 1px solid #c62828;">
                            <strong>Lỗi:</strong> ${errorMessage}
                        </div>
                    </c:if>

                    <div class="content-card">
                        <div class="table-toolbar">
                            <div class="search-box">
                                <i data-feather="search" class="feather-search"></i>
                                <input type="text" placeholder="Tìm kiếm khách hàng...">
                            </div>
                            <div class="toolbar-actions">
                                <a href="${BASE_URL}/createCustomer" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                            </div>
                        </div>
                    </div>

                    <div class="customer-board-container">
                        <div class="customer-board">

                            <%-- Column 1: VIP Customers --%>
                            <jsp:include page="kanbanColumn.jsp">
                                <jsp:param name="columnKey" value="vip"/>
                                <jsp:param name="columnTitle" value="Khách hàng VIP"/>
                            </jsp:include>

                            <%-- Column 2: Loyal Customers --%>
                            <jsp:include page="kanbanColumn.jsp">
                                <jsp:param name="columnKey" value="loyal"/>
                                <jsp:param name="columnTitle" value="Khách hàng Thân thiết"/>
                            </jsp:include>
                            
                            <%-- Column 3: Potential Customers --%>
                            <jsp:include page="kanbanColumn.jsp">
                                <jsp:param name="columnKey" value="potential"/>
                                <jsp:param name="columnTitle" value="Khách hàng Tiềm năng"/>
                            </jsp:include>
                            
                            <%-- ĐÃ XÓA CỘT KHÁCH HÀNG KHÁC --%>

                        </div>
                    </div>
                </div>
            </main>
        </div>

        <%-- Modal for delete confirmation --%>
        <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Xác nhận Xóa Khách hàng</h3>
                    <button class="close-modal-btn"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body">
                    <i data-feather="alert-triangle" class="warning-icon"></i>
                    <p id="deleteMessage">Bạn có chắc chắn muốn xóa khách hàng này không?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                // Render tất cả các icon Feather
                feather.replace();

                // --- LOGIC MỚI CHO MENU THU GỌN ---
                const appContainer = document.querySelector('.app-container');
                // Nút này cần được đặt trong file mainMenu.jsp của bạn
                const menuToggleButton = document.getElementById('menu-toggle-btn'); 

                if (menuToggleButton) {
                    // Xử lý sự kiện click vào nút thu/mở menu
                    menuToggleButton.addEventListener('click', function() {
                        appContainer.classList.toggle('menu-collapsed');
                        // Lưu trạng thái vào localStorage để ghi nhớ lựa chọn của người dùng
                        const isCollapsed = appContainer.classList.contains('menu-collapsed');
                        localStorage.setItem('menuCollapsed', isCollapsed);
                    });
                }
                
                // Kiểm tra trạng thái đã lưu khi tải trang
                if (localStorage.getItem('menuCollapsed') === 'true') {
                    appContainer.classList.add('menu-collapsed');
                }
            });
        </script>
        
    </body>
</html>
