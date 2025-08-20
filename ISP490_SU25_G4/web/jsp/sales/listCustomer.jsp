<%-- /jsp/sales/listCustomer.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCustomer.css">
        <link rel="stylesheet" href="${BASE_URL}/css/pagination.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <style>
            .filter-container {
                display: none;
                flex-wrap: wrap;
                gap: 16px;
                padding: 16px;
                background-color: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                margin-top: 16px;
                align-items: flex-end;
            }
            .filter-group {
                display: flex;
                flex-direction: column;
                gap: 4px;
            }
            .filter-group label {
                font-size: 13px;
                font-weight: 500;
                color: #4b5563;
            }
            .filter-group select {
                min-width: 180px;
                height: 38px;
                padding: 0 12px;
                border-radius: 6px;
                border: 1px solid #d1d5db;
                background-color: #fff;
            }
            .filter-actions {
                display: flex;
                gap: 8px;
                margin-left: auto;
                align-items: self-end;
            }
            .table-container {
                width: 100%;
                overflow-x: auto;
            }
            .data-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
            }
            .data-table thead th {
                background-color: #f8f9fa;
                padding: 12px 16px;
                text-align: left;
                font-weight: 600;
                color: #4b5563;
                border-bottom: 2px solid #e5e7eb;
            }
            .data-table tbody td {
                padding: 14px 16px;
                border-bottom: 1px solid #f3f4f6;
                color: #374151;
                vertical-align: middle;
            }
            .customer-info {
                display: flex;
                align-items: center;
                gap: 12px;
            }
            /* Đã xóa CSS cho .customer-info .avatar */
            .table-actions {
                display: flex;
                gap: 8px;
            }
            .table-actions a, .table-actions .delete-trigger-btn {
                color: #6b7280;
                padding: 4px;
                cursor: pointer;
            }
            .data-table tbody td a {
                color: #1E7E34;
                text-decoration: none;
                font-weight: 500;
            }
            .data-table tbody td a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <jsp:include page="/header.jsp"><jsp:param name="pageTitle" value="Danh sách Khách hàng"/></jsp:include>
                <div class="page-content">
                    <c:if test="${not empty sessionScope.successMessage}"><div class="success-message">${sessionScope.successMessage}</div><c:remove var="successMessage" scope="session"/></c:if>
                    <c:if test="${not empty sessionScope.errorMessage}"><div class="error-message">${sessionScope.errorMessage}</div><c:remove var="errorMessage" scope="session"/></c:if>

                    <form action="${BASE_URL}/customer/list" method="GET" id="filterForm">
                        <div class="table-toolbar">
                            <div style="position: relative; flex-grow: 1;">
                                <div class="search-box"><i data-feather="search"></i><input type="text" id="searchInput" name="search" placeholder="Tìm theo tên, mã, hotline..." value="<c:out value='${searchQuery}'/>" autocomplete="off"></div>
                                <div id="suggestionsContainer" class="suggestions-list"></div>
                            </div>
                            <button type="button" class="btn btn-secondary" id="filter-toggle-btn"><i data-feather="filter"></i> Lọc</button>
                            <button type="submit" class="btn btn-primary"><i data-feather="search"></i> Tìm kiếm</button>
                            <div class="toolbar-actions">
                                <c:if test="${sessionScope.user.roleName == 'Admin' || sessionScope.user.roleName == 'Kinh doanh'}">
                                    <a href="${BASE_URL}/customer/create" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                                </c:if>
                            </div>
                        </div>
                        <div class="filter-container" id="filter-container">
                            <div class="filter-group"><label for="province">Tỉnh/Thành phố</label><select id="province" name="provinceId"><option value="">Tất cả</option><c:forEach var="p" items="${allProvinces}"><option value="${p.id}" ${p.id == selectedProvinceId ? 'selected' : ''}>${p.name}</option></c:forEach></select></div>
                            <div class="filter-group"><label for="district">Quận/Huyện</label><select id="district" name="districtId" disabled><option value="">Tất cả</option></select></div>
                            <div class="filter-group"><label for="ward">Phường/Xã</label><select id="ward" name="wardId" disabled><option value="">Tất cả</option></select></div>
                            <div class="filter-group"><label for="customerType">Loại khách hàng</label><select id="customerType" name="customerTypeId"><option value="">Tất cả</option><c:forEach var="type" items="${allCustomerTypes}"><option value="${type.id}" ${type.id == selectedCustomerTypeId ? 'selected' : ''}>${type.name}</option></c:forEach></select></div>
                            <div class="filter-group"><label for="employee">Nhân viên</label><select id="employee" name="employeeId"><option value="">Tất cả</option><c:forEach var="emp" items="${allEmployees}"><option value="${emp.id}" ${emp.id == selectedEmployeeId ? 'selected' : ''}>${emp.fullNameCombined}</option></c:forEach></select></div>
                            <div class="filter-actions">
                                <a href="${BASE_URL}/customer/list" class="btn btn-secondary" id="clear-filter-btn">Xóa lọc</a>
                            </div>
                        </div>
                    </form>

                    <div class="content-card" style="margin-top: 24px;">
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr><th>Mã KH</th><th>Tên Khách hàng</th><th>Email</th><th>Hotline</th><th>Địa chỉ</th><th>Nhân viên phụ trách</th><th style="width: 120px;">Hành động</th></tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="customer" items="${customerList}">
                                        <tr>
                                            <td><a href="${BASE_URL}/customer/view?id=${customer.id}">${customer.enterpriseCode}</a></td>
                                            <%-- === BẮT ĐẦU SỬA LỖI === --%>
                                            <td>
                                                <div class="customer-info">
                                                    <%-- Đã xóa thẻ <img> hiển thị avatar tại đây --%>
                                                    <span class="name">${customer.name}</span>
                                                </div>
                                            </td>
                                            <%-- === KẾT THÚC SỬA LỖI === --%>
                                            <td>${customer.businessEmail}</td>
                                            <td>${customer.hotline}</td>
                                            <td>${customer.fullAddress}</td>
                                            <td>${customer.assignedUsers[0].firstName}</td>
                                            <td>
                                                <div class="table-actions">
                                                    <a href="${BASE_URL}/customer/view?id=${customer.id}" title="Xem"><i data-feather="eye" style="color: #6b7280;"></i></a>
                                                    <c:if test="${sessionScope.user.roleName == 'Admin' || sessionScope.user.roleName == 'Kinh doanh'}">
                                                        <a href="${BASE_URL}/customer/edit?id=${customer.id}" title="Sửa"><i data-feather="edit-2" style="color: #6b7280;"></i></a>
                                                        <span class="delete-trigger-btn" data-id="${customer.id}" data-name="<c:out value='${customer.name}'/>" title="Xóa"><i data-feather="trash-2" style="color: #6b7280;"></i></span>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty customerList}"><td colspan="7" style="text-align: center; padding: 20px;">Không có khách hàng nào.</td></c:if>
                                </tbody>
                            </table>
                        </div>
                        <jsp:include page="/pagination.jsp" />
                    </div>
                </div>
            </main>
        </div>

        <%-- Delete Confirmation Modal --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <i data-feather="alert-triangle" class="warning-icon"></i>
                <h3 class="modal-title">Xác nhận Xóa</h3>
                <p id="deleteMessage"></p>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <form id="deleteForm" action="${BASE_URL}/customer/delete" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            window.APP_CONFIG = {
                BASE_URL: '<c:out value="${BASE_URL}"/>',
                SELECTED_DISTRICT_ID: '<c:out value="${selectedDistrictId}"/>',
                SELECTED_WARD_ID: '<c:out value="${selectedWardId}"/>'
            };
        </script>
        <script src="${BASE_URL}/js/mainMenu.js"></script>
        <script src="${BASE_URL}/js/delete-modal-handler.js"></script>
        <script src="${BASE_URL}/js/customer-list-scripts.js"></script>
    </body>
</html>