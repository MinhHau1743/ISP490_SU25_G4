<%-- 
    Document   : listEmployeeAdmin
    Created on : Jun 14, 2025, 1:30:08 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="listEmployeeAdmin" />
<c:set var="isEmployeeSectionActive" value="${currentPage == 'listEmployeeCustomer' || currentPage == 'listEmployeeTechnical' || currentPage == 'listEmployeeSales' || currentPage == 'listEmployeeOffice'}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Nhân viên Bộ phận kinh doanh</title>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/menu.css"> 
        <link rel="stylesheet" href="../../css/pagination.css">
        <link rel="stylesheet" href="../../css/employeeCard.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../menu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Nhân viên Bộ phận kinh doanh</div>
                    <button class="notification-btn"><i data-feather="bell"></i></button>
                </header>
                <section class="content-body">
                    <div class="table-toolbar">
                        <div class="search-bar">
                            <i data-feather="search"></i>
                            <input type="text" placeholder="Tìm kiếm nhân viên...">
                        </div>
                        <a href="../admin/addEmployeeSales.jsp" class="btn btn-primary"><i data-feather="plus"></i>Thêm nhân viên</a>
                    </div>

                    <div class="employee-grid">
                        <c:if test="${empty employeeList}">
                            <p style="grid-column: 1 / -1; text-align: center;">Không có nhân viên nào để hiển thị.</p>
                        </c:if>

                        <c:forEach var="employee" items="${employeeList}">
                            <c:set var="cardClass" value="${employee.position == 'Quản lý' ? 'manager' : ''}" />
                            <c:set var="badgeClass" value="${employee.position == 'Quản lý' ? 'manager' : 'staff'}" />

                            <div class="employee-card ${cardClass}">
                                <div class="card-main-info">
                                    <h3 class="employee-name">${employee.employeeName}</h3>
                                    <p class="employee-code">${employee.employeeCode}</p>
                                </div>
                                <div class="card-secondary-info">
                                    <div class="info-row">
                                        <i data-feather="briefcase"></i>
                                        <span class="info-label">${employee.department}</span>
                                    </div>
                                    <div class="info-row">
                                        <i data-feather="award"></i>
                                        <span class="position-badge ${badgeClass}">${employee.position}</span>
                                    </div>
                                </div>
                                <div class="card-actions">
                                    <a href="../admin/viewEmployee.jsp?id=${employee.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                    <a href="../admin/editEmployee.jsp?id=${employee.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                    <a href="deleteEmployee?id=${employee.id}" title="Xóa" onclick="return confirm('Xóa nhân viên ${employee.employeeName}?');"><i data-feather="trash-2"></i></a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <jsp:include page="../../pagination.jsp"/>
                </section>
            </main>
        </div>
        <script src="https://unpkg.com/feather-icons"></script>
        <script src="../../js/listEmployee.js"></script>
    </body>
</html>

