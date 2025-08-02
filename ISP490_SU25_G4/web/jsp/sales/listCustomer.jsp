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
            .no-results-message {
                text-align: center;
                padding: 40px 20px;
                margin: 20px;
                border: 1px dashed #ccc;
                border-radius: 8px;
                background-color: #f9f9f9;
                color: #555;
            }

            .no-results-message .feather-info {
                width: 48px;
                height: 48px;
                color: #888;
                margin-bottom: 16px;
            }

            .no-results-message p {
                margin: 5px 0;
                font-size: 1.1rem;
            }

            .no-results-message strong {
                color: #d9534f;
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
                        <div class="success-message">
                            <i data-feather="check-circle" class="icon"></i>
                            <span>${sessionScope.successMessage}</span>
                        </div>
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
                                    <input type="text" id="searchInput" name="search" 
                                           placeholder="Tìm theo tên, địa chỉ, số fax hoặc nhân viên phụ trách" 
                                           value="<c:out value='${searchQuery}'/>" autocomplete="off">
                                </div>
                                <%-- Container để hiển thị các gợi ý --%>
                                <div id="suggestionsContainer" class="suggestions-list"></div>
                            </div>
                            <button type="submit" class="btn">Tìm kiếm</button>
                        </form>

                        <%-- Class "toolbar-actions" với "margin-left: auto" sẽ tự động đẩy nút này sang phải --%>
                        <div class="toolbar-actions">
                            <c:choose>
                                <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kinh doanh'}">
                                    <%-- Người dùng có quyền: Giữ nguyên code gốc --%>
                                    <a href="${BASE_URL}/createCustomer" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                                </c:when>
                                <c:otherwise>
                                    <%-- Người dùng không có quyền: Vô hiệu hóa link --%>
                                    <a href="#" class="btn btn-primary disabled-action" data-error="Bạn không có quyền thêm khách hàng mới."><i data-feather="plus"></i>Thêm Khách hàng</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div> <%-- Đóng thẻ .table-toolbar --%>

                <%-- === BẮT ĐẦU: KHỐI MÃ HIỂN THỊ THÔNG BÁO === --%>
                <c:if test="${not empty searchQuery && noResultsFound}">
                    <div class="no-results-message">
                        <i data-feather="info"></i>
                        <p>Không tìm thấy khách hàng nào phù hợp với từ khóa "<strong><c:out value="${searchQuery}"/></strong>".</p>
                        <p>Vui lòng thử lại với từ khóa khác hoặc kiểm tra xem khách hàng đã bị xóa hay chưa.</p>
                    </div>
                </c:if>
                <%-- === KẾT THÚC: KHỐI MÃ HIỂN THỊ THÔNG BÁO === --%>

                <%-- Chỉ hiển thị bảng Kanban nếu có kết quả hoặc không tìm kiếm --%>
                <c:if test="${empty noResultsFound}">
                    <div class="customer-board-container">
                        <div class="customer-board">
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="potential"/><jsp:param name="columnTitle" value="Khách hàng Tiềm năng"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="other"/><jsp:param name="columnTitle" value="Khách hàng Mới"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="loyal"/><jsp:param name="columnTitle" value="Khách hàng Thân thiết"/></jsp:include>
                            <jsp:include page="kanbanColumn.jsp"><jsp:param name="columnKey" value="vip"/><jsp:param name="columnTitle" value="Khách hàng VIP"/></jsp:include>
                            </div>
                        </div>
                </c:if>               
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
        setTimeout(() => feather.replace(), 100);

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
<script>
    // Script này chỉ cần thêm một lần vào trang layout chính hoặc vào từng trang cần thiết
    document.addEventListener('DOMContentLoaded', function () {
        document.body.addEventListener('click', function (event) {
            const disabledLink = event.target.closest('.disabled-action');
            if (disabledLink) {
                event.preventDefault();
                const errorMessage = disabledLink.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.';
                alert(errorMessage);
            }
        });
    });
</script>
<script src="${pageContext.request.contextPath}/js/delete-modal-handler.js"></script>
<script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
</body>
</html>
