<%-- 
    Document   : addEmployee
    Created on : Jun 7, 2025, 9:04:47 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/menu.css">
        <link rel="stylesheet" href="css/pagination.css">
        <link rel="stylesheet" href="css/dataTable.css">     
        <link rel="stylesheet" href="css/addEmployee.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="menu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thêm nhân viên</div>
                </header>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <strong>Lỗi!</strong> ${error}
                    </div>
                </c:if>

                <form action="addEmployee" method="POST" enctype="multipart/form-data">
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
                                            <label for="employeeCode">Mã nhân viên</label>
                                            <input type="text" id="employeeCode" name="employeeCode" value="${newEmployeeCode}" readonly>
                                        </div>

                                        <div class="form-group">
                                            <label for="lastName">Họ</label>
                                            <input type="text" id="lastName" name="lastName" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="middleName">Tên đệm</label>
                                            <input type="text" id="middleName" name="middleName">
                                        </div>

                                        <div class="form-group">
                                            <label for="firstName">Tên</label>
                                            <input type="text" id="firstName" name="firstName" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="phone">Số điện thoại</label>
                                            <input type="tel" id="phone" name="phone" required>
                                        </div>

                                    </div>
                                </div>

                                <div class="form-card">
                                    <h3 class="form-card-title">Thông tin công việc</h3>
                                    <div class="form-card-grid">
                                        <div class="form-group">
                                            <label for="department">Phòng làm việc</label>
                                            <select id="department" name="department">
                                                <option>Kĩ thuật</option>
                                                <option>CSKH</option>
                                            </select>
                                        </div> 
                                        <div class="form-group">
                                            <label for="position">Chức vụ</label>
                                            <select id="position" name="position">
                                                <option>Quản lý</option>
                                                <option>Nhân viên</option>
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
                                                <label class="radio-option"><input type="radio" name="gender" value="Nam" checked> Nam</label>
                                                <label class="radio-option"><input type="radio" name="gender" value="Nữ"> Nữ</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-card">
                                    <h3 class="form-card-title">Thông tin liên hệ</h3>
                                    <div class="form-card-grid">
                                        <div class="form-group">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email">
                                        </div>
                                        <div class="form-group">
                                            <label for="address">Địa chỉ</label>
                                            <input type="text" id="address" name="address">
                                        </div>
                                        <div class="form-group">
                                            <label for="city">Tỉnh/Thành phố</label>
                                            <input type="text" id="city" name="city">
                                        </div>
                                        <div class="form-group">
                                            <label for="quan">Quận/Huyện</label>
                                            <input type="text" id="quan" name="quan">
                                        </div>
                                        <div class="form-group">
                                            <label for="stress">Phường/Xã</label>
                                            <input type="text" id="stress" name="stress">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <footer class="page-actions-footer">
                        <a href="listEmployee" class="btn btn-secondary" role="button">Hủy</a>
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
        <script src="js/mainMenu.js"></script>
    </body>
</html>