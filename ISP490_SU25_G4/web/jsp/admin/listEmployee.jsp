<%--
    Document    : listEmployee
    Created on : Jun 6, 2025, 8:52:42 AM
    Author      : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Đặt tên trang để mainMenu.jsp có thể highlight đúng mục --%>
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

        <%-- Chuẩn hóa tất cả đường dẫn CSS --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
  
    </head>
    <body>
        <div class="app-container">
            <%-- Include file mainMenu.jsp đã tạo ở trên --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Nhân viên</div>
                </header>

                <section class="content-body">
                    <div class="table-toolbar">
                        <div class="search-bar">
                            <i data-feather="search"></i>
                            <input type="text" placeholder="Tìm kiếm nhân viên...">
                        </div>
                        <a href="${pageContext.request.contextPath}/addEmployee" class="btn btn-primary">
                            <i data-feather="plus"></i>
                            <span>Thêm nhân viên</span>
                        </a>
                    </div>

                    <div class="table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th class="col-checkbox"><input type="checkbox" /></th>
                                    <th class="col-id">Mã NV</th>
                                    <th class="col-name">Tên nhân viên</th>
                                    <th class="col-phone">Số điện thoại</th>
                                    <th class="col-department">Phòng ban</th>
                                    <th class="col-position">Chức vụ</th>
                                    <th class="col-actions">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="employee" items="${employeeList}">
                                    <tr>
                                        <td class="col-checkbox"><input type="checkbox" name="employeeId" value="${employee.id}" /></td>
                                        <td>${employee.employeeCode}</td>
                                        <td>${employee.lastName} ${employee.middleName} ${employee.firstName}</td>
                                        <td>${employee.phoneNumber}</td>
                                        <td>${not empty employee.departmentName ? employee.departmentName : 'N/A'}</td>
                                        <td>${not empty employee.positionName ? employee.positionName : 'N/A'}</td>
                                        <td class="col-actions">
                                            <div class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/viewEmployee?id=${employee.id}" title="Xem chi tiết">
                                                    <i data-feather="eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/editEmployee?id=${employee.id}" title="Sửa">
                                                    <i data-feather="edit-2"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/deleteEmployee?id=${employee.id}" title="Xóa" 
                                                   onclick="return confirm('Xóa nhân viên ${employee.lastName} ${employee.firstName}?');">
                                                    <i data-feather="trash-2"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <jsp:include page="/pagination.jsp"/>
                </section>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>