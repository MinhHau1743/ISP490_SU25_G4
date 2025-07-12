<%-- 
    Document   : viewEmployee
    Created on : Jun 14, 2025, 1:30:28 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="viewProfile" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/menu.css">
        <link rel="stylesheet" href="../../css/pagination.css">
        <link rel="stylesheet" href="../../css/profile.css">


    </head>
    <body>
    <div class="app-container">
        <jsp:include page="../../menu.jsp"/>

        <main class="main-content">
            <header class="main-top-bar">
                <div class="page-title">Xem thông tin chi tiết</div>
                <button class="notification-btn">
                    <i data-feather="bell"></i>
                    <span class="notification-badge"></span>
                </button>
            </header>
            <div class="profile-form-container">
                <%-- Kiểm tra xem đối tượng employee có tồn tại không --%>
                <c:if test="${not empty employee}">
                    <form id="profileForm">
                        <%-- Card 1: Thông tin khởi tạo --%>
                        <div class="profile-card">
                            <div class="card-body-split">
                                <div class="avatar-section">
                                    <%-- SỬA: Hiển thị ảnh đại diện động --%>
                                    <img src="<c:url value='/${not empty employee.avatarUrl ? employee.avatarUrl : "images/default-avatar.png"}'/>" 
                                         alt="Ảnh đại diện" id="avatarPreview" style="width:170px; height:170px; object-fit: cover;">
                                </div>
                                <div class="info-section">
                                    <h2>Thông tin khởi tạo</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label>Mã nhân viên</label>
                                            <%-- SỬA: Dùng employee.employeeCode --%>
                                            <span class="form-data">${employee.employeeCode}</span>
                                        </div>
                                        <div class="form-group">
                                            <label>Tên nhân viên</label>
                                            <%-- SỬA: Dùng employee.fullName --%>
                                            <input type="text" value="${employee.fullName}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label>Số điện thoại</label>
                                            <%-- SỬA: Dùng employee.phoneNumber --%>
                                            <input type="tel" value="${employee.phoneNumber}" disabled>
                                        </div>
                                        <div class="form-group">
                                            <label>Email</label>
                                            <%-- SỬA: Dùng employee.email --%>
                                            <input type="email" value="${employee.email}" disabled>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Card 2: Thông tin công việc --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin công việc</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Phòng làm việc</label>
                                        <%-- SỬA: Thay dropdown tĩnh bằng input --%>
                                        <input type="text" value="${employee.departmentName}" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label>Chức vụ</label>
                                        <%-- SỬA: Thay dropdown tĩnh bằng input --%>
                                        <input type="text" value="${employee.positionName}" disabled>
                                    </div>
                                </div>
                                <div class="form-group full-width">
                                    <label>Ghi chú</label>
                                    <%-- SỬA: Dùng employee.notes --%>
                                    <textarea rows="3" disabled>${employee.notes}</textarea>
                                </div>
                            </div>
                        </div>

                        <%-- Card 3: Thông tin cá nhân --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin cá nhân</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Số CMND/CCCD</label>
                                        <%-- SỬA: Dùng employee.identityCardNumber --%>
                                        <input type="text" value="${employee.identityCardNumber}" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label>Ngày sinh</label>
                                        <%-- SỬA: Dùng employee.dateOfBirth --%>
                                        <input type="date" value="${employee.dateOfBirth}" disabled>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <div class="radio-group">
                                        <%-- SỬA: Dùng employee.gender --%>
                                        <label><input type="radio" name="gender" value="male" ${employee.gender == 'male' ? 'checked' : ''} disabled> Nam</label>
                                        <label><input type="radio" name="gender" value="female" ${employee.gender == 'female' ? 'checked' : ''} disabled> Nữ</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Các nút hành động --%>
                        <div class="form-actions">
                            <%-- Nút Đóng, khi click sẽ quay về trang trước --%>
                            <button type="button" class="btn btn-secondary" onclick="window.history.back()">Đóng</button>
                            
                            <%-- SỬA: Sửa lại link cho nút Sửa --%>
                            <a href="${pageContext.request.contextPath}/admin/employees/edit?id=${employee.id}" class="btn btn-primary" role="button">Sửa thông tin</a>
                        </div>
                    </form>
                </c:if>
                
                <%-- Hiển thị thông báo nếu không tìm thấy nhân viên --%>
                <c:if test="${empty employee}">
                    <div class="profile-card">
                        <p style="text-align: center; padding: 20px;">Không tìm thấy thông tin nhân viên hoặc có lỗi xảy ra.</p>
                    </div>
                </c:if>
            </div>
        </main>
    </div>

    <script>
        feather.replace();
    </script>
    <script src="../../js/mainMenu.js"></script>
</body>
</html>


