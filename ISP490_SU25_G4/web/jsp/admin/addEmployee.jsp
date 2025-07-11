<%-- 
    Document   : addEmployeeSales
    Created on : Jun 16, 2025, 9:46:14 PM
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
        <title>Thêm nhân viên kinh doanh</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/menu.css">
        <link rel="stylesheet" href="../../css/pagination.css">
        <link rel="stylesheet" href="../../css/profile.css">
        <link rel="stylesheet" href="../../css/dataTable.css">     
        <link rel="stylesheet" href="../../css/addEmployee.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../menu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thêm nhân viên</div>
                </header>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" style="color: red; background-color: #fdd; padding: 10px; margin: 10px 0; border: 1px solid red; border-radius: 5px;">
                        ${errorMessage}
                    </div>
                </c:if>

                <form action="/ISP490_SU25_G4/admin/employees/add" method="POST" enctype="multipart/form-data">
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
                                            <select id="departmentId" name="departmentId" ><option value="1">Bộ phận kinh doanh</option></select>
                                        </div> 
                                        <div class="form-group">
                                            <label for="position">Chức vụ</label>
                                            <select id="position" name="positionId"><option value="1">Quản lý</option></select>
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
                        <a href="../admin/listEmployeeSales.jsp" class="btn btn-secondary" role="button">Hủy</a>
                        <button type="submit" class="btn btn-primary">Lưu nhân viên</button>
                    </footer>
                </form>
            </main>
        </div>

        <script>
            feather.replace();
            const avatarUploadInput = document.getElementById('avatar-upload');
            const avatarPreviewContainer = document.getElementById('avatar-preview-container');
            avatarUploadInput.addEventListener('change', function (event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        avatarPreviewContainer.innerHTML = `<img src="${e.target.result}" alt="Avatar Preview">`;
                    }
                    reader.readAsDataURL(file);
                }
            });
        </script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
