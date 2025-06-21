<%-- 
    Document   : listEmployee
    Created on : Jun 6, 2025, 8:52:42 AM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/menu.css">
        <link rel="stylesheet" href="css/pagination.css">
        <link rel="stylesheet" href="css/dataTable.css">  
        <link rel="stylesheet" href="css/modal.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="menu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Nhân viên</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>

                <section class="content-body">
                    <div class="table-toolbar">
                        <div class="search-bar">
                            <i data-feather="search"></i>
                            <input type="text" placeholder="Tìm kiếm nhân viên">
                        </div>
                        <a href="addEmployee.jsp" class="btn btn-primary">
                            <i data-feather="plus"></i>
                            <span>Thêm nhân viên</span>
                        </a>
                    </div>

                    <div class="table-container">
                        <div class="table-container">
                            <table class="data-table">
                                <%-- PHẦN 1: SỬA LẠI THỨ TỰ CÁC CỘT TRONG TIÊU ĐỀ CHO ĐÚNG --%>
                                <thead>
                                    <tr>
                                        <th class="col-checkbox"><input type="checkbox" /></th>
                                        <th class="col-id">Mã nhân viên</th>
                                        <th class="col-name">Tên nhân viên</th>
                                        <th class="col-phone">Số điện thoại</th>
                                        <th class="col-idcard">Số CMND/CCCD</th>
                                        <th class="col-position">Chức vụ</th>
                                        <th class="col-department">Phòng ban</th>
                                        <th class="col-actions">Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="employee" items="${employeeList}">
                                        <%-- Để nút "Xem" hoạt động, thẻ <tr> cần các thuộc tính data-* này --%>
                                        <tr data-id="${employee.id}"
                                            data-code="${employee.employeeCode}"
                                            data-name="${employee.employeeName}"
                                            data-phone="${employee.phone}"
                                            data-idcard="${employee.idCard}"
                                            data-position="${employee.position}"
                                            data-department="${employee.department}">

                                            <td class="col-checkbox"><input type="checkbox" name="employeeId" value="${employee.id}" /></td>

                                            
                                            <td>${employee.employeeCode}</td>
                                            <td>${employee.employeeName}</td>
                                            <td>${employee.phone}</td>
                                            <td>${employee.idCard}</td>
                                            <td>${employee.position}</td>
                                            <td>${employee.department}</td>

                                            <%-- PHẦN 3: ĐẶT 3 NÚT HÀNH ĐỘNG VÀO ĐÚNG VỊ TRÍ --%>
                                            <td class="col-actions">
                                                <div class="action-buttons">
                                                    <a href="viewEmployee.jsp" class="view-btn" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                                    <a href="editEmployee?id=${employee.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <a href="deleteEmployee?id=${employee.id}" title="Xóa" onclick="return confirm('Bạn có chắc chắn muốn xóa nhân viên ${employee.employeeName} không?');">
                                                        <i data-feather="trash-2"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <section class="content-body">
                        <div class="table-toolbar">
                        </div>

                        <div class="empty-state">
                        </div>
                        <jsp:include page="pagination.jsp"/>
                    </section>
                </section>
            </main>
        </div>


        <script>
            feather.replace();
        </script>
        <script src="js/mainMenu.js"></script>
    </body>
</html>