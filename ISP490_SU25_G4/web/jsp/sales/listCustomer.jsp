<%-- /jsp/sales/listCustomer.jsp --%>
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
        <link rel="stylesheet" href="${BASE_URL}/css/pagination.css">
        <link rel="stylesheet" href="css/header.css">

        <style>
            .filter-container {
                display: none; /* Mặc định ẩn */
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
            }

            /* === STYLES FOR TABLE VIEW === */
            .table-container {
                width: 100%;
                overflow-x: auto; /* Cho phép cuộn ngang trên màn hình nhỏ */
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

            .data-table tbody tr:hover {
                background-color: #f9fafb;
            }

            .customer-info {
                display: flex;
                align-items: center;
                gap: 12px;
            }

            .customer-info .avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }

            .customer-info .name {
                font-weight: 500;
                color: #111827;
            }

            .table-actions {
                display: flex;
                gap: 8px;
            }
            .table-actions a {
                color: #6b7280;
                padding: 4px;
            }
            .table-actions a:hover {
                color: var(--primary-color);
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                 <jsp:include page="/header.jsp">
                <jsp:param name="pageTitle" value="Danh sách Khách hàng"/>
            </jsp:include>

                <div class="page-content">
                    <c:if test="${not empty sessionScope.successMessage}"><div class="success-message">${sessionScope.successMessage}</div><c:remove var="successMessage" scope="session"/></c:if>
                    <c:if test="${not empty sessionScope.errorMessage}"><div class="error-message">${sessionScope.errorMessage}</div><c:remove var="errorMessage" scope="session"/></c:if>

                        <form action="${BASE_URL}/listCustomer" method="GET">
                        <div class="table-toolbar">
                            <div style="position: relative; flex-grow: 1;">
                                <div class="search-box">
                                    <i data-feather="search"></i>
                                    <input type="text" id="searchInput" name="search" placeholder="Tìm theo tên, mã, fax..." value="<c:out value='${searchQuery}'/>" autocomplete="off">
                                </div>
                                <div id="suggestionsContainer" class="suggestions-list"></div>
                            </div>

                            <button type="button" class="btn btn-secondary" id="filter-toggle-btn"><i data-feather="filter"></i> Lọc</button>
                            <button type="submit" class="btn btn-primary">Tìm kiếm</button>

                            <div class="toolbar-actions">
                                <a href="${BASE_URL}/createCustomer" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                            </div>
                        </div>

                        <div class="filter-container" id="filter-container">
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
                                    <a href="${BASE_URL}/listCustomer" class="btn btn-secondary">Xóa lọc</a>
                            </div>
                        </div>
                    </form>

                    <div class="content-card" style="margin-top: 24px;">
                        <div class="table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Mã KH</th>
                                        <th>Tên Khách hàng</th>
                                        <th>Email</th>
                                        <th>SĐT/Fax</th>
                                        <th>Địa chỉ</th>
                                        <th>Nhân viên phụ trách</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="customer" items="${customerList}">
                                        <tr>
                                            <td>${customer.enterpriseCode}</td>
                                            <td>
                                                <div class="customer-info">
                                                    <img class="avatar" src="${not empty customer.avatarUrl ? BASE_URL.concat('/').concat(customer.avatarUrl) : 'https://placehold.co/40x40/E0F7FA/00796B?text='.concat(customer.name.substring(0,1))}" alt="Avatar">
                                                    <span class="name">${customer.name}</span>
                                                </div>
                                            </td>
                                            <td>${customer.businessEmail}</td>
                                            <td>${customer.fax}</td>
                                            <td>${customer.fullAddress}</td>
                                            <td>${customer.assignedUsers[0].firstName}</td>
                                            <td>
                                                <div class="table-actions">
                                                    <a href="${BASE_URL}/viewCustomer?id=${customer.id}" title="Xem"><i data-feather="eye"></i></a>
                                                    <a href="${BASE_URL}/editCustomer?id=${customer.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger-btn" data-id="<c:out value='${customer.id}'/>" data-name="<c:out value='${customer.name}'/>" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty customerList}">
                                        <tr><td colspan="7" style="text-align: center; padding: 20px;">Không có khách hàng nào.</td></tr>
                                    </c:if>
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
                    <form id="deleteForm" action="${BASE_URL}/deleteCustomer" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // === LOGIC MỚI: BẬT/TẮT BỘ LỌC ===
                const filterToggleBtn = document.getElementById('filter-toggle-btn');
                const filterContainer = document.getElementById('filter-container');
                filterToggleBtn.addEventListener('click', () => {
                    if (filterContainer.style.display === 'none' || filterContainer.style.display === '') {
                        filterContainer.style.display = 'flex';
                    } else {
                        filterContainer.style.display = 'none';
                    }
                });

                // (Các logic JS khác cho gợi ý tìm kiếm, modal, dropdown địa chỉ giữ nguyên...)
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');

                const selectedDistrictId = "${selectedDistrictId}";
                const selectedWardId = "${selectedWardId}";

                async function fetchDistricts(provinceId) { /* ... */
                }
                async function fetchWards(districtId) { /* ... */
                }

                provinceSelect.addEventListener('change', () => fetchDistricts(provinceSelect.value));
                districtSelect.addEventListener('change', () => fetchWards(districtSelect.value));

                if (provinceSelect.value) {
                    fetchDistricts(provinceSelect.value);
                }
            });
        </script>
        <script src="${BASE_URL}/js/mainMenu.js"></script>
        <script src="${BASE_URL}/js/delete-modal-handler.js"></script>
    </body>
</html>