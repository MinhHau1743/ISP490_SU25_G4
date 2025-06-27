<%--
    Document   : listCustomer
    Created on : Jun 17, 2025
    Author     : anhndhe172050 
    Description: Displays a Kanban board of all customers, with a collapsible menu and soft-delete functionality.
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

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCustomer.css">

        <style>
            /* === STYLES FOR SEARCH SUGGESTIONS === */
            .suggestions-list {
                display: none; /* Ẩn mặc định */
                position: absolute;
                background-color: #ffffff;
                border: 1px solid #e2e8f0;
                border-radius: 8px;
                width: 100%;
                max-height: 250px;
                overflow-y: auto;
                z-index: 1000;
                margin-top: 6px; /* Khoảng cách với ô tìm kiếm */
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
            }
            .suggestion-item {
                padding: 10px 16px;
                cursor: pointer;
                font-size: 0.9rem;
                color: #2d3748;
            }
            .suggestion-item:hover {
                background-color: #f7fafc;
            }
            /* Styles for Kanban Board Layout */
            .customer-board-container {
                display: flex;
                justify-content: center;
                width: 100%;
            }
            .customer-board {
                display: inline-flex;
                gap: 16px;
            }

            /* Styles for Collapsible Menu */
            .app-container.menu-collapsed .main-menu {
                width: 80px;
            }
            .app-container.menu-collapsed .main-menu .nav-link span,
            .app-container.menu-collapsed .main-menu .logo-text,
            .app-container.menu-collapsed .main-menu .user-profile .user-name,
            .app-container.menu-collapsed .main-menu .user-profile .user-role {
                opacity: 0;
                display: none;
            }
            .app-container.menu-collapsed .main-menu .nav-link,
            .app-container.menu-collapsed .main-menu .menu-header {
                justify-content: center;
            }
            .app-container.menu-collapsed .main-menu #menu-toggle-btn .feather {
                transform: rotate(180deg);
            }
            .app-container.menu-collapsed .main-content {
                margin-left: 80px;
            }
            .main-menu, .main-content, .main-menu #menu-toggle-btn .feather {
                transition: all 0.3s ease-in-out;
            }

            /* === UPDATED STYLES FOR DELETE MODAL === */
            .modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.6);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 1050;
                opacity: 0;
                visibility: hidden;
                transition: opacity 0.3s;
            }
            .modal-overlay.show {
                opacity: 1;
                visibility: visible;
            }
            .modal-content {
                background: white;
                padding: 24px 32px; /* Increased padding */
                border-radius: 12px;
                width: 90%;
                max-width: 450px;
                text-align: center;
                transform: scale(0.9);
                transition: transform 0.3s;
            }
            .modal-overlay.show .modal-content {
                transform: scale(1);
            }
            .warning-icon {
                color: #f59e0b;
                width: 48px;
                height: 48px;
                margin-bottom: 16px;
            }
            .modal-title {
                font-size: 1.25rem;
                font-weight: 600;
                margin-bottom: 8px;
            }
            #deleteMessage {
                color: #4A5568;
                line-height: 1.6; /* Increased line height for readability */
                margin: 16px 0;
            }
            #deleteMessage strong {
                color: #c53030; /* Red color for emphasis */
                font-weight: 600;
            }
            .modal-footer {
                display: flex;
                justify-content: center;
                gap: 12px;
                margin-top: 24px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <%-- mainMenu.jsp MUST contain the #menu-toggle-btn button --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="page-header">
                    <div class="title-section"><h1 class="title">Danh sách Khách hàng</h1></div>
                    <div class="header-actions"><button class="notification-btn"><i data-feather="bell"></i></button></div>
                </header>

                <div class="page-content">
                    <%-- Display success/error messages from session and remove them --%>
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="success-message">${sessionScope.successMessage}</div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="error-message">${sessionScope.errorMessage}</div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>

                    <div class="table-toolbar">
                        <%-- === SEARCH FORM WITH SUGGESTIONS === --%>
                        <form class="search-form" action="${BASE_URL}/listCustomer" method="GET" style="display: flex; align-items: center; gap: 12px;">
                            <%-- Wrapper for positioning suggestions --%>
                            <div style="position: relative;">
                                <div class="search-box">
                                    <i data-feather="search"></i>
                                    <%-- Thêm id="searchInput" để dễ dàng truy cập bằng JS --%>
                                    <input type="text" id="searchInput" name="search" placeholder="Tìm theo tên khách hàng" value="<c:out value='${searchQuery}'/>" autocomplete="off">
                                </div>
                                <%-- Container để hiển thị các gợi ý --%>
                                <div id="suggestionsContainer" class="suggestions-list"></div>
                            </div>
                            <button type="submit" class="btn">Tìm kiếm</button>
                        </form>

                        <%-- Class "toolbar-actions" với "margin-left: auto" sẽ tự động đẩy nút này sang phải --%>
                        <div class="toolbar-actions">
                            <a href="${BASE_URL}/createCustomer" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                        </div>
                    </div>

                    <div class="customer-board-container">
                        <div class="customer-board">
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="vip"/><jsp:param name="columnTitle" value="Khách hàng VIP"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="loyal"/><jsp:param name="columnTitle" value="Khách hàng Thân thiết"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="potential"/><jsp:param name="columnTitle" value="Khách hàng Tiềm năng"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="other"/><jsp:param name="columnTitle" value="Khách hàng Khác"/></jsp:include>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

        <%-- Delete Confirmation Modal --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <i data-feather="alert-triangle" class="warning-icon"></i>
                <h3 class="modal-title">Xác nhận Xóa</h3>
                <p id="deleteMessage">Bạn có chắc chắn muốn xóa khách hàng này không?</p>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <form id="deleteForm" action="${BASE_URL}/deleteCustomer" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Render all Feather icons on the page
                feather.replace();

                // --- Collapsible Menu Logic ---
                const appContainer = document.querySelector('.app-container');
                const menuToggleButton = document.getElementById('menu-toggle-btn');

                if (appContainer && menuToggleButton) {
                    menuToggleButton.addEventListener('click', function () {
                        appContainer.classList.toggle('menu-collapsed');
                        const isCollapsed = appContainer.classList.contains('menu-collapsed');
                        localStorage.setItem('menuCollapsed', isCollapsed);
                    });

                    if (localStorage.getItem('menuCollapsed') === 'true') {
                        appContainer.classList.add('menu-collapsed');
                    }
                }

                // === UPDATED DELETE CONFIRMATION LOGIC ===
                const modal = document.getElementById('deleteConfirmModal');
                if (modal) {
                    const deleteMessage = document.getElementById('deleteMessage');
                    const customerIdInput = document.getElementById('customerIdToDelete');
                    const cancelBtn = document.getElementById('cancelDeleteBtn');
                    const deleteButtons = document.querySelectorAll('.delete-trigger-btn');

                    deleteButtons.forEach(button => {
                        button.addEventListener('click', function (event) {
                            event.preventDefault();

                            const customerId = this.getAttribute('data-id');
                            const customerName = this.getAttribute('data-name') || "khách hàng này";

                            // Use innerHTML to allow for strong tag styling
                            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa khách hàng <strong>"${customerName}"</strong>? Hành động này không thể hoàn tác.`;
                            customerIdInput.value = customerId;

                            modal.classList.add('show');
                            feather.replace(); // Re-render icon inside the modal
                        });
                    });

                    const closeModal = () => modal.classList.remove('show');
                    cancelBtn.addEventListener('click', closeModal);
                    modal.addEventListener('click', function (event) {
                        if (event.target === modal) {
                            closeModal();
                        }
                    });
                }
                // ===================================================================
                // <<< BẮT ĐẦU: LOGIC GỢI Ý TÌM KIẾM (PHIÊN BẢN ĐÃ SỬA LỖI) >>>
                // ===================================================================
                const searchInput = document.getElementById('searchInput');
                const suggestionsContainer = document.getElementById('suggestionsContainer');
                const searchForm = document.querySelector('.search-form');

                if (searchInput && suggestionsContainer && searchForm) {
                    searchInput.addEventListener('input', async function () {
                        const query = this.value.trim();

                        if (query.length < 2) {
                            suggestionsContainer.style.display = 'none';
                            return;
                        }

                        try {
                            // === DÒNG CODE ĐÃ ĐƯỢC SỬA LẠI ĐỂ AN TOÀN HƠN ===
                            const url = '${BASE_URL}' + '/searchSuggestions?query=' + encodeURIComponent(query);
                            const response = await fetch(url);
                            // ===============================================

                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            const suggestions = await response.json();

                            suggestionsContainer.innerHTML = '';

                            if (suggestions.length > 0) {
                                suggestions.forEach(name => {
                                    const item = document.createElement('div');
                                    item.className = 'suggestion-item';
                                    item.textContent = name;

                                    item.addEventListener('click', function () {
                                        searchInput.value = this.textContent;
                                        suggestionsContainer.style.display = 'none';
                                        searchForm.submit();
                                    });
                                    suggestionsContainer.appendChild(item);
                                });
                                suggestionsContainer.style.display = 'block';
                            } else {
                                suggestionsContainer.style.display = 'none';
                            }

                        } catch (error) {
                            console.error('Lỗi khi lấy gợi ý tìm kiếm:', error);
                            suggestionsContainer.style.display = 'none';
                        }
                    });

                    document.addEventListener('click', function (event) {
                        if (!searchInput.contains(event.target) && !suggestionsContainer.contains(event.target)) {
                            suggestionsContainer.style.display = 'none';
                        }
                    });
                }
                // ===================================================================
                // <<< KẾT THÚC: LOGIC GỢI Ý TÌM KIẾM >>>
                // ===================================================================
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
