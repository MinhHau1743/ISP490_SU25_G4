<%--
    Document    : listAllEmployees.jsp
    Created on : Jun 6, 2025, 8:52:42 AM
    Author      : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Đặt tên trang để mainMenu.jsp có thể highlight đúng mục "Nhân viên" --%>
<c:set var="currentPage" value="listEmployee" />

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

        <%-- SỬA LỖI 1: Chuẩn hóa tất cả các đường dẫn CSS --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employeeCard.css">
    </head>
    <body>
        <div class="app-container">
            <%-- SỬA LỖI 2: Include đúng file menu chuẩn --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Danh sách Nhân viên</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                    </button>
                </header>

                <section class="content-body">
                    <div class="table-toolbar">
                        <div class="search-bar">
                            <i data-feather="search"></i>
                            <input type="text" placeholder="Tìm kiếm nhân viên...">
                        </div>
                        <%-- SỬA LỖI 3: Chuẩn hóa đường dẫn cho nút "Thêm" --%>
                        <a href="${pageContext.request.contextPath}/addEmployee" class="btn btn-primary">
                            <i data-feather="plus"></i>
                            <span>Thêm nhân viên</span>
                        </a>
                    </div>

                    <div class="employee-grid">
                        <c:if test="${empty employeeList}">
                            <p>Không có nhân viên nào để hiển thị.</p>
                        </c:if>

                        <c:forEach var="user" items="${employeeList}">
                            <div class="employee-card">
                                <div class="card-main">
                                    <h3 class="employee-name">${user.lastName} ${user.middleName} ${user.firstName}</h3>
                                    <p class="employee-code">${user.employeeCode}</p>
                                </div>
                                <div class="card-secondary-info">
                                    <div class="info-row">
                                        <i data-feather="shield"></i>
                                        <span>${empty user.roleName ? 'Chưa có vai trò' : user.roleName}</span>
                                    </div>
                                    <div class="info-row">
                                        <i data-feather="briefcase"></i>
                                        <span>${empty user.departmentName ? 'Chưa có phòng ban' : user.departmentName}</span>
                                    </div>
                                    <div class="info-row">
                                        <i data-feather="award"></i>
                                        <span class="position-badge">${empty user.positionName ? 'Nhân viên' : user.positionName}</span>
                                    </div>
                                </div>
                                <div class="card-actions">
                                    <a href="${pageContext.request.contextPath}/viewEmployee?id=${user.id}" title="Xem chi tiết">
                                        <i data-feather="eye"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/editEmployee?id=${user.id}" title="Sửa">
                                        <i data-feather="edit-2"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/deleteEmployee?id=${user.id}" title="Xóa"
                                       onclick="return confirm('Bạn chắc chắn muốn xóa nhân viên ${user.lastName} ${user.middleName} ${user.firstName}?');">
                                        <i data-feather="trash-2"></i>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <%-- Chuẩn hóa đường dẫn cho pagination --%>
                    <jsp:include page="/pagination.jsp" />
                </section>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <%-- Chuẩn hóa đường dẫn script --%>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>