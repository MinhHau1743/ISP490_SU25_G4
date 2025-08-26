<%--
    Document    : addEmployee
    Created on  : Jun 16, 2025, 9:46:14 PM
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
        <title>Thêm nhân viên</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/addEmployee.css">
    </head>
    <body>

        <div class="app-container">
            <%-- SỬA LỖI: Include đúng file mainMenu.jsp --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thêm nhân viên</div>
                </header>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">
                        ${errorMessage}
                    </div>
                </c:if>

                <%-- KHẮC PHỤC LỖI: Thêm enctype="multipart/form-data" vào thẻ form --%>
                <form action="${pageContext.request.contextPath}/employee?action=add" method="POST" enctype="multipart/form-data">

                    <section class="content-body">
                        <div class="add-employee-page">

                            <div class="avatar-panel">
                                <label for="avatar-upload" class="avatar-upload-box" id="avatar-preview-container">
                                    <i data-feather="image"></i>
                                </label>
                                <input type="file" id="avatar-upload" name="avatar" accept="image/*" style="display: none;">
                                <button type="button" class="btn btn-secondary" onclick="document.getElementById('avatar-upload').click();">
                                    Chọn ảnh
                                </button>
                            </div>

                            <div class="form-panel">
                                <div class="form-card">
                                    <h3 class="form-card-title">Thông tin khởi tạo</h3>
                                    <div class="form-card-grid">
                                        <div class="form-group">
                                            <label for="employeeName">Tên nhân viên</label>
                                            <input type="text" id="employeeName" name="employeeName" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="phone">Số điện thoại</label>
                                            <input type="tel" id="phone" name="phone" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Mã nhân viên</label>
                                            <input type="text" value="Mã sẽ được tạo tự động" disabled>
                                        </div>
                                        <div class="form-group">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email" required>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-card">
                                    <h3 class="form-card-title">Thông tin công việc</h3>
                                    <div class="form-card-grid">
                                        <div class="form-group">
                                            <label for="departmentId">Phòng làm việc</label>
                                            <select id="departmentId" name="departmentId" required>
                                                <c:if test="${empty departmentList}">
                                                    <option value="">Không có phòng ban</option>
                                                </c:if>
                                                <c:forEach var="department" items="${departmentList}">
                                                    <option value="${department.id}">${department.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="position">Chức vụ</label>
                                            <select id="position" name="positionId" required>
                                                <c:if test="${empty positionList}">
                                                    <option value="">Không có chức vụ</option>
                                                </c:if>
                                                <c:forEach var="position" items="${positionList}">
                                                    <option value="${position.id}">${position.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group full-width" style="margin-top: 20px;">
                                        <label for="notes">Ghi chú</label>
                                        <textarea id="notes" name="notes" rows="3" placeholder="Thêm ghi chú về công việc..."></textarea>
                                    </div>
                                </div>

                                <div class="form-card">
                                    <h3 class="form-card-title">Thông tin cá nhân</h3>
                                    <div class="form-card-grid">
                                        <div class="form-group">
                                            <label for="idCard">Số CMND/CCCD</label>
                                            <input type="text" id="idCard" name="idCard">
                                        </div>
                                        <div class="form-group">
                                            <label for="dob">Ngày sinh</label>
                                            <input type="date" id="dob" name="dob">
                                        </div>
                                        <div class="form-group">
                                            <label>Giới tính</label>
                                            <div class="radio-group">
                                                <label class="radio-option"><input type="radio" name="gender" value="male" checked> Nam</label>
                                                <label class="radio-option"><input type="radio" name="gender" value="female"> Nữ</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <footer class="page-actions-footer">
                        <a href="${pageContext.request.contextPath}/employee?action=list" class="btn btn-secondary" role="button">Hủy</a>
                        <button type="submit" class="btn btn-primary">Lưu nhân viên</button>
                    </footer>
                </form>
            </main>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>
                                    feather.replace();

                                    // Script preview avatar
                                    const avatarUploadInput = document.getElementById('avatar-upload');
                                    const avatarPreviewContainer = document.getElementById('avatar-preview-container');
                                    avatarUploadInput.addEventListener('change', function (event) {
                                        const file = event.target.files[0];
                                        if (file) {
                                            const reader = new FileReader();
                                            reader.onload = function (e) {
                                                avatarPreviewContainer.innerHTML = `<img src="${e.target.result}" alt="Avatar Preview" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px;">`;
                                            }
                                            reader.readAsDataURL(file);
                                        }
                                    });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
