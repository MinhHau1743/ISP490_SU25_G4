<%--
    Document   : listCustomer
    Created on : Jun 17, 2025
    Author     : anhndhe172050
    Description: Displays a Kanban board of all customers, with unified search/filtering, a collapsible menu, and soft-delete functionality.
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
                text-align: center; padding: 40px 20px; margin: 20px;
                border: 1px dashed #ccc; border-radius: 8px;
                background-color: #f9f9f9; color: #555;
            }
            .no-results-message .feather-info {
                width: 48px; height: 48px; color: #888; margin-bottom: 16px;
            }
            .no-results-message p { margin: 5px 0; font-size: 1.1rem; }
            
            .filter-toolbar {
                display: flex; flex-wrap: wrap; gap: 16px; padding: 16px;
                background-color: #f8f9fa; border: 1px solid #dee2e6;
                border-radius: 8px; margin-top: 16px; align-items: flex-end;
            }
            .filter-group { display: flex; flex-direction: column; gap: 4px; }
            .filter-group label { font-size: 13px; font-weight: 500; color: #4b5563; }
            .filter-group select {
                min-width: 180px; height: 38px; padding: 0 12px;
                border-radius: 6px; border: 1px solid #d1d5db; background-color: #fff;
            }
            .filter-actions { display: flex; gap: 8px; margin-left: auto; }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="page-header">
                    <div class="title-section"><h1 class="title">Danh sách Khách hàng</h1></div>
                    <div class="header-actions"><button class="notification-btn"><i data-feather="bell"></i></button></div>
                </header>

                <div class="page-content">
                    <%-- Display messages --%>
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="success-message"><i data-feather="check-circle" class="icon"></i><span>${sessionScope.successMessage}</span></div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="error-message">${sessionScope.errorMessage}</div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>

                    <form class="search-and-filter-form" action="${BASE_URL}/listCustomer" method="GET">
                        <div class="table-toolbar">
                             <div class="search-container" style="flex-grow: 1;">
                                <div style="position: relative;">
                                    <div class="search-box">
                                        <i data-feather="search"></i>
                                        <input type="text" id="searchInput" name="search" placeholder="Tìm theo tên, mã, fax, nhân viên..." value="<c:out value='${searchQuery}'/>" autocomplete="off">
                                    </div>
                                    <div id="suggestionsContainer" class="suggestions-list"></div>
                                </div>
                            </div>
                            <div class="toolbar-actions">
                                <c:choose>
                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kinh doanh'}">
                                        <a href="${BASE_URL}/createCustomer" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="#" class="btn btn-primary disabled-action" data-error="Bạn không có quyền thêm khách hàng mới."><i data-feather="plus"></i>Thêm Khách hàng</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <div class="filter-toolbar">
                            <div class="filter-group">
                                <label for="province">Tỉnh/Thành phố</label>
                                <select id="province" name="provinceId">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="p" items="${allProvinces}"><option value="${p.id}" ${p.id == selectedProvinceId ? 'selected' : ''}>${p.name}</option></c:forEach>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="district">Quận/Huyện</label>
                                <select id="district" name="districtId" ${empty selectedProvinceId ? 'disabled' : ''}><option value="">Tất cả</option></select>
                            </div>
                            <div class="filter-group">
                                <label for="ward">Phường/Xã</label>
                                <select id="ward" name="wardId" ${empty selectedDistrictId ? 'disabled' : ''}><option value="">Tất cả</option></select>
                            </div>
                            <div class="filter-group">
                                <label for="customerType">Loại khách hàng</label>
                                <select id="customerType" name="customerTypeId">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="type" items="${allCustomerTypes}"><option value="${type.id}" ${type.id == selectedCustomerTypeId ? 'selected' : ''}>${type.name}</option></c:forEach>
                                </select>
                            </div>
                             <div class="filter-group">
                                <label for="employee">Nhân viên phụ trách</label>
                                <select id="employee" name="employeeId">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="emp" items="${allEmployees}"><option value="${emp.id}" ${emp.id == selectedEmployeeId ? 'selected' : ''}>${emp.fullName}</option></c:forEach>
                                </select>
                            </div>
                            <div class="filter-actions">
                                <button type="submit" class="btn btn-primary"><i data-feather="filter" style="margin-right: 4px;"></i>Lọc</button>
                                <a href="${BASE_URL}/listCustomer" class="btn btn-secondary">Xóa lọc</a>
                            </div>
                        </div>
                    </form>

                    <%-- KANBAN BOARD & MESSAGES --%>
                    <c:if test="${not empty noResultsFound}">
                        <div class="no-results-message"><i data-feather="info"></i><p>Không tìm thấy khách hàng nào phù hợp với các tiêu chí đã chọn.</p></div>
                    </c:if>
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

        <%-- MODAL --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <i data-feather="alert-triangle" class="warning-icon"></i>
                <h3 class="modal-title">Xác nhận Xóa</h3>
                <p id="deleteMessage">Bạn có chắc chắn muốn xóa khách hàng này không?</p>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <form id="deleteForm" action="${BASE_URL}/deleteCustomer" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId"><button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>

        <%-- SCRIPTS --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
                setTimeout(() => feather.replace(), 100);

                // --- Disabled action handler ---
                document.body.addEventListener('click', function (event) {
                    const disabledLink = event.target.closest('.disabled-action');
                    if (disabledLink) {
                        event.preventDefault();
                        alert(disabledLink.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.');
                    }
                });
                
                // --- Search suggestions ---
                const searchInput = document.getElementById('searchInput');
                const suggestionsContainer = document.getElementById('suggestionsContainer');
                const searchForm = document.querySelector('.search-and-filter-form');
                if (searchInput && suggestionsContainer && searchForm) {
                    searchInput.addEventListener('input', async function () {
                        const query = this.value.trim();
                        if (query.length < 2) { suggestionsContainer.style.display = 'none'; return; }
                        try {
                            const response = await fetch('${BASE_URL}/searchSuggestions?query=' + encodeURIComponent(query));
                            if (!response.ok) throw new Error('Network error');
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
                            } else { suggestionsContainer.style.display = 'none'; }
                        } catch (error) { console.error('Suggestion fetch error:', error); suggestionsContainer.style.display = 'none'; }
                    });
                    document.addEventListener('click', function (event) {
                        if (!searchInput.contains(event.target) && !suggestionsContainer.contains(event.target)) {
                            suggestionsContainer.style.display = 'none';
                        }
                    });
                }
                
                // --- Dynamic address dropdowns ---
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');
                const selectedDistrictId = "${selectedDistrictId}";
                const selectedWardId = "${selectedWardId}";

                async function fetchDistricts(provinceId) {
                    districtSelect.innerHTML = '<option value="">Tất cả</option>';
                    districtSelect.disabled = true;
                    wardSelect.innerHTML = '<option value="">Tất cả</option>';
                    wardSelect.disabled = true;
                    if (!provinceId) return;

                    const response = await fetch('${BASE_URL}/getDistricts?provinceId=' + provinceId);
                    const data = await response.json();
                    districtSelect.disabled = false;
                    data.forEach(d => {
                        const option = new Option(d.name, d.id);
                        if (d.id == selectedDistrictId) option.selected = true;
                        districtSelect.add(option);
                    });
                    if (districtSelect.value) await fetchWards(districtSelect.value);
                }

                async function fetchWards(districtId) {
                    wardSelect.innerHTML = '<option value="">Tất cả</option>';
                    wardSelect.disabled = true;
                    if (!districtId) return;

                    const response = await fetch('${BASE_URL}/getWards?districtId=' + districtId);
                    const data = await response.json();
                    wardSelect.disabled = false;
                    data.forEach(w => {
                        const option = new Option(w.name, w.id);
                        if (w.id == selectedWardId) option.selected = true;
                        wardSelect.add(option);
                    });
                }

                provinceSelect.addEventListener('change', () => fetchDistricts(provinceSelect.value));
                districtSelect.addEventListener('change', () => fetchWards(districtSelect.value));
                if (provinceSelect.value) { fetchDistricts(provinceSelect.value); }
            });
        </script>
        
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/delete-modal-handler.js"></script>
    </body>
</html>