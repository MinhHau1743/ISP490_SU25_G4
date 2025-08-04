<%--
    Document   : listAllEmployees.jsp
    Created on : Jun 6, 2025
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="menuHighlight" value="listEmployee" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Nhân viên</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employeeCard.css">

        <style>
            /* === BIẾN MÀU SẮC TRUNG TÂM === */
            :root {
                --role-color-cskh: #3b82f6;
                --role-color-kinhdoanh: #16a34a;
                --role-color-chanhvanphong: #f59e0b;
                --role-color-kythuat: #ea580c;
                --role-color-admin: #9333ea;
                --role-color-default: #64748b;
            }

            /* === CSS CHO FORM TÌM KIẾM === */
            .search-form {
                display: flex;
                align-items: center;
                gap: 12px;
                flex-grow: 1;
            }
            .search-bar {
                width: 100%;
                max-width: 400px;
                position: relative;
            }
            .btn-search {
                padding-top: 9px;
                padding-bottom: 9px;
                flex-shrink: 0;
            }

            /* === CSS CHO THẺ NHÂN VIÊN === */
            .employee-card {
                background-color: #ffffff;
                border: 2px solid #e2e8f0;
                border-radius: 12px;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);
                padding: 24px;
                display: flex;
                flex-direction: column;
                text-align: left;
                transition: all 0.2s ease-in-out;
            }
            .card-header-minimal {
                display: flex;
                align-items: center;
                gap: 16px;
            }
            .card-avatar-minimal img {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                object-fit: cover;
            }
            .employee-name {
                color: #1a202c;
                font-size: 1.125rem;
                font-weight: 600;
                margin: 0;
            }
            .employee-code {
                color: #a0aec0;
                font-size: 0.875rem;
                margin-top: 4px;
            }

            .card-separator {
                border: none;
                border-top: 1px solid #cbd5e1;
                margin: 16px 0;
            }

            .card-secondary-info {
                width: 100%;
                text-align: left;
            }
            .info-row {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 10px;
                color: #4a5568;
                font-size: 0.875rem;
            }
            .info-row i {
                color: #a0aec0;
            }
            .card-actions {
                margin-top: auto;
                display: flex;
                gap: 16px;
                align-items: center;
                justify-content: center;
                width: 100%;
                padding-top: 16px;
            }
            .action-btn {
                color: #718096;
                transition: color 0.2s;
            }
            .action-btn:hover {
                color: #2b6cb0;
            }

            /* CSS CÁC PHẦN KHÁC */
            .employee-card.inactive-card {
                background-color: #f8fafc;
                opacity: 0.7;
            }
            .status-badge {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 4px 12px;
                border-radius: 9999px;
                font-weight: 600;
                font-size: 0.8rem;
                text-decoration: none;
                cursor: pointer;
                transition: all 0.2s;
            }
            .status-badge i {
                width: 20px;
                height: 20px;
            }
            .status-badge.status-active {
                background-color: #ecfdf5;
                color: #16a34a;
            }
            .status-badge.status-active:hover {
                background-color: #d1fae5;
            }
            .status-badge.status-inactive {
                background-color: #fee2e2;
                color: #ef4444;
            }
            .status-badge.status-inactive:hover {
                background-color: #fecaca;
            }
            .role-text {
                font-weight: 600;
            }
            .employee-card.role-default {
                border-color: var(--role-color-default);
            }
            .employee-card.role-default .role-text {
                color: var(--role-color-default);
            }
            .employee-card.role-admin {
                border-color: var(--role-color-admin);
            }
            .employee-card.role-admin .role-text {
                color: var(--role-color-admin);
            }
            .employee-card.role-cskh {
                border-color: var(--role-color-cskh);
            }
            .employee-card.role-cskh .role-text {
                color: var(--role-color-cskh);
            }
            .employee-card.role-kinhdoanh {
                border-color: var(--role-color-kinhdoanh);
            }
            .employee-card.role-kinhdoanh .role-text {
                color: var(--role-color-kinhdoanh);
            }
            .employee-card.role-chanhvanphong {
                border-color: var(--role-color-chanhvanphong);
            }
            .employee-card.role-chanhvanphong .role-text {
                color: var(--role-color-chanhvanphong);
            }
            .employee-card.role-kythuat {
                border-color: var(--role-color-kythuat);
            }
            .employee-card.role-kythuat .role-text {
                color: var(--role-color-kythuat);
            }

            /* CSS PHÂN TRANG */
            .table-footer {
                width: 100%;
                display: flex;
                justify-content: center;
                margin-top: 32px;
                margin-bottom: 32px;
            }
            .pagination {
                display: flex;
            }
            .pagination .page-link {
                color: #0d6efd;
                text-decoration: none;
                background-color: #fff;
                border: 1px solid #dee2e6;
                padding: 0.5rem 0.9rem;
                font-size: 1rem;
                font-weight: 500;
                transition: all 0.15s ease-in-out;
                white-space: nowrap;
                margin-left: -1px;
            }
            .pagination .page-link:first-child {
                border-top-left-radius: 0.375rem;
                border-bottom-left-radius: 0.375rem;
                margin-left: 0;
            }
            .pagination .page-link:last-child {
                border-top-right-radius: 0.375rem;
                border-bottom-right-radius: 0.375rem;
            }
            .pagination .page-link:not(.disabled):not(.active):hover {
                z-index: 2;
                background-color: #e9ecef;
                border-color: #dee2e6;
            }
            .pagination .page-link.active {
                z-index: 3;
                color: #fff;
                background-color: #0d6efd;
                border-color: #0d6efd;
                cursor: default;
            }
            .pagination .page-link.disabled {
                color: #6c757d;
                pointer-events: none;
                background-color: #fff;
                border-color: #dee2e6;
            }

            <%-- === SỬA ĐỔI 1: THÊM CSS TÙY CHỈNH CHO POPUP === --%>
            .custom-swal-container {
                width: 480px !important;      /* Giảm chiều rộng của popup */
                border-radius: 20px !important; /* Tăng độ bo tròn góc */
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <%-- **THAY ĐỔI:** Thêm header mới vào đây --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Danh sách Nhân viên"/>
                </jsp:include>

                <section class="content-body">
                    <div class="table-toolbar">
                        <form action="${pageContext.request.contextPath}/listEmployee" method="get" class="search-form">
                            <div class="search-bar">
                                <i data-feather="search"></i>
                                <input type="text" name="searchQuery" placeholder="Tìm theo tên hoặc mã NV..." value="${searchQuery}">
                            </div>
                            <button type="submit" class="btn btn-search">Tìm kiếm</button>
                        </form>
                        <a href="${pageContext.request.contextPath}/addEmployee" class="btn btn-primary">
                            <i data-feather="plus"></i>
                            <span>Thêm nhân viên</span>
                        </a>
                    </div>

                    <div class="employee-grid">
                        <c:if test="${empty employeeList}">
                            <p>
                                <c:if test="${not empty searchQuery}">Không tìm thấy nhân viên nào khớp với từ khóa "${searchQuery}".</c:if>
                                <c:if test="${empty searchQuery}">Không có nhân viên nào để hiển thị.</c:if>
                                </p>
                        </c:if>

                        <c:forEach var="user" items="${employeeList}">
                            <c:set var="roleColorClass" value="role-default"/>
                            <c:choose>
                                <c:when test="${user.positionName == 'Admin'}"><c:set var="roleColorClass" value="role-admin"/></c:when>
                                <c:when test="${user.positionName == 'Chăm sóc khách hàng'}"><c:set var="roleColorClass" value="role-cskh"/></c:when>
                                <c:when test="${user.positionName == 'Kinh doanh'}"><c:set var="roleColorClass" value="role-kinhdoanh"/></c:when>
                                <c:when test="${user.positionName == 'Chánh văn phòng'}"><c:set var="roleColorClass" value="role-chanhvanphong"/></c:when>
                                <c:when test="${user.positionName == 'Kỹ thuật'}"><c:set var="roleColorClass" value="role-kythuat"/></c:when>
                            </c:choose>

                            <div class="employee-card ${roleColorClass} ${user.isDeleted == 1 ? 'inactive-card' : ''}">
                                <div class="card-header-minimal">
                                    <div class="card-avatar-minimal">
                                        <c:url var="avatarUrl" value="/images/default-avatar.png" />
                                        <c:if test="${not empty user.avatarUrl}">
                                            <c:set var="avatarUrl" value="/${user.avatarUrl}" />
                                        </c:if>
                                        <img src="${pageContext.request.contextPath}${avatarUrl}" alt="Avatar">
                                    </div>
                                    <div class="card-main">
                                        <h3 class="employee-name">${user.lastName} ${user.middleName} ${user.firstName}</h3>
                                        <p class="employee-code">${user.employeeCode}</p>
                                    </div>
                                </div>

                                <hr class="card-separator">

                                <div class="card-secondary-info">
                                    <div class="info-row">
                                        <i data-feather="award"></i>
                                        <span class="role-text">${empty user.departmentName ? 'Chưa có phòng làm việc' : user.departmentName}</span>
                                    </div>
                                    <div class="info-row">
                                        <i data-feather="shield"></i>
                                        <span class="role-text">${empty user.positionName ? 'Chưa có chức vụ' : user.positionName}</span>
                                    </div>
                                    <div class="info-row">
                                        <i data-feather="mail"></i>
                                        <span>${empty user.email ? 'Chưa có email' : user.email}</span>
                                    </div>
                                </div>

                                <div class="card-actions">
                                    <c:choose>
                                        <c:when test="${user.roleName == 'Admin'}">
                                            <a href="${pageContext.request.contextPath}/viewEmployee?id=${user.id}" class="action-btn" title="Xem chi tiết">
                                                <i data-feather="eye"></i>
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${user.isDeleted == 1}">
                                                    <a href="javascript:void(0);"
                                                       onclick="showReactivateConfirm('${pageContext.request.contextPath}/updateStatus?id=${user.id}&status=0&page=${currentPage}&searchQuery=${searchQuery}', '${user.lastName} ${user.middleName} ${user.firstName}')"
                                                       class="status-badge status-inactive" title="Kích hoạt lại">
                                                        <i data-feather="toggle-right"></i>
                                                        <span>Vô hiệu hóa</span>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/viewEmployee?id=${user.id}" class="action-btn" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                                    <a href="${pageContext.request.contextPath}/editEmployee?id=${user.id}" class="action-btn" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <a href="javascript:void(0);"
                                                       onclick="showDeactivateConfirm('${pageContext.request.contextPath}/updateStatus?id=${user.id}&status=1&page=${currentPage}&searchQuery=${searchQuery}', '${user.lastName} ${user.middleName} ${user.firstName}')"
                                                       class="status-badge status-active" title="Vô hiệu hóa">
                                                        <i data-feather="toggle-left"></i>
                                                        <span>Hoạt động</span>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <c:set var="queryString" value=""/>
                    <c:if test="${not empty searchQuery}"><c:set var="queryString" value="&searchQuery=${searchQuery}" /></c:if>
                    <jsp:include page="/pagination.jsp">
                        <jsp:param name="queryString" value="${queryString}"/>
                    </jsp:include>
                </section>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            // Hàm hiển thị popup xác nhận VÔ HIỆU HÓA
            function showDeactivateConfirm(url, employeeName) {
                Swal.fire({
                    title: 'Bạn chắc chắn?',
                    text: "Bạn sẽ vô hiệu hóa nhân viên " + employeeName + "!",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#d33',
                    cancelButtonColor: '#3085d6',
                    confirmButtonText: 'Vô hiệu hóa',
                    cancelButtonText: 'Hủy',
                    // === SỬA ĐỔI 2: ÁP DỤNG CLASS TÙY CHỈNH ===
                    customClass: {
                        popup: 'custom-swal-container'
                    }
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = url;
                    }
                });
            }

            // Hàm hiển thị popup xác nhận KÍCH HOẠT LẠI
            function showReactivateConfirm(url, employeeName) {
                Swal.fire({
                    title: 'Bạn chắc chắn?',
                    text: "Bạn sẽ kích hoạt lại nhân viên " + employeeName + "!",
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonColor: '#28a745',
                    cancelButtonColor: '#6c757d',
                    confirmButtonText: 'Kích hoạt',
                    cancelButtonText: 'Hủy',
                    // === SỬA ĐỔI 2: ÁP DỤNG CLASS TÙY CHỈNH ===
                    customClass: {
                        popup: 'custom-swal-container'
                    }
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = url;
                    }
                });
            }
        </script>
    </body>
</html>