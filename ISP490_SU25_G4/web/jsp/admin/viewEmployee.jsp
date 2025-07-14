<%--
    Document    : viewEmployee
    Created on : Jun 14, 2025
    Author      : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="listEmployee" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin chi tiết nhân viên</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <%-- Link đến file CSS mới cho trang này --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewEmployee.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Xem thông tin chi tiết</div>
                </header>

                <section class="content-body">
                    <c:if test="${not empty employee}">
                        <%-- Container chính sử dụng Flexbox --%>
                        <div class="view-employee-page">
                            
                            <%-- Panel bên trái cho ảnh đại diện --%>
                            <div class="avatar-panel">
                                <div class="avatar-display-box">
                                    <c:url var="avatarUrl" value="/images/default-avatar.png" />
                                    <c:if test="${not empty employee.avatarUrl}">
                                        <c:set var="avatarUrl" value="/${employee.avatarUrl}" />
                                    </c:if>
                                    <img src="${pageContext.request.contextPath}${avatarUrl}" alt="Ảnh đại diện">
                                </div>
                            </div>
                            
                            <%-- Panel bên phải chứa các card thông tin --%>
                            <div class="info-panel">
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin khởi tạo</h3>
                                    <div class="info-card-grid">
                                        <div class="form-group"><label>Mã nhân viên</label><input type="text" value="${employee.employeeCode}" disabled></div>
                                        <div class="form-group"><label>Tên nhân viên</label><input type="text" value="${employee.lastName} ${employee.middleName} ${employee.firstName}" disabled></div>
                                        <div class="form-group"><label>Số điện thoại</label><input type="text" value="${employee.phoneNumber}" disabled></div>
                                        <div class="form-group"><label>Email</label><input type="text" value="${employee.email}" disabled></div>
                                    </div>
                                </div>
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin công việc</h3>
                                    <div class="info-card-grid">
                                        <div class="form-group"><label>Phòng làm việc</label><input type="text" value="${not empty employee.departmentName ? employee.departmentName : 'Chưa cập nhật'}" disabled></div>
                                        <div class="form-group"><label>Chức vụ</label><input type="text" value="${not empty employee.positionName ? employee.positionName : 'Chưa cập nhật'}" disabled></div>
                                    </div>
                                    <div class="form-group full-width"><label>Ghi chú</label><textarea rows="3" disabled>${employee.notes}</textarea></div>
                                </div>
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin cá nhân</h3>
                                    <div class="info-card-grid">
                                        <div class="form-group"><label>Số CMND/CCCD</label><input type="text" value="${employee.identityCardNumber}" disabled></div>
                                        <div class="form-group"><label>Ngày sinh</label><input type="text" value="${employee.dateOfBirth}" disabled></div>
                                    </div>
                                    <div class="form-group"><label>Giới tính</label><input type="text" value="${employee.gender == 'male' ? 'Nam' : 'Nữ'}" disabled></div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    
                    <c:if test="${empty employee}">
                         <p>Không tìm thấy thông tin nhân viên.</p>
                    </c:if>
                </section>
                
                 <footer class="page-actions-footer">
                    <button type="button" class="btn btn-secondary" onclick="window.history.back()">Đóng</button>
                    <a href="${pageContext.request.contextPath}/editEmployee?id=${employee.id}" class="btn btn-primary" role="button">Sửa thông tin</a>
                </footer>
            </main>
        </div>
        <script src="https://unpkg.com/feather-icons"></script>
        <script>feather.replace()</script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>