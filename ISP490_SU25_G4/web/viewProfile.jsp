<%-- 
    Document   : viewProfile
    Created on : Jun 25, 2025, 12:00:00 AM
    Author     : minhnhn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="viewProfile" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/pagination.css">
        <link rel="stylesheet" href="css/profile.css">
    </head>
    <body>

        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Xem thông tin chi tiết</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>
                
                <div class="profile-form-container">
                    <c:if test="${not empty profile}">
                        <form id="profileForm">
                            <div class="profile-card">
                                <div class="card-body-split">
                                    <div class="avatar-section">
                                        <%-- Sửa: Thêm logic để hiển thị ảnh mặc định nếu không có avatar --%>
                                        <c:choose>
                                            <c:when test="${not empty profile.avatarUrl}">
                                                <img src="${profile.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="images/default-avatar.png" alt="Ảnh đại diện mặc định" id="avatarPreview">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="info-section">
                                        <h2>Thông tin khởi tạo</h2>
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="maNhanVien">Mã nhân viên</label>
                                                <span class="form-data">${profile.employeeCode}</span> 
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="lastName">Họ</label>
                                                <input type="text" id="lastName" name="lastName" value="${profile.lastName}" disabled>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="middleName">Tên đệm</label>
                                                <input type="text" id="middleName" name="middleName" value="${profile.middleName}" disabled>
                                            </div>
                                        </div>
                                        <div class="form-row">
                                            <div class="form-group">
                                                <label for="firstName">Tên</label>
                                                <input type="text" id="firstName" name="firstName" value="${profile.firstName}" disabled>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="soDienThoai">Số điện thoại</label>
                                            <input type="tel" id="soDienThoai" name="soDienThoai" value="${profile.phoneNumber}" disabled>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="profile-card">
                                <div class="card-body">
                                    <h2>Thông tin công việc</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="phongLamViec">Phòng làm việc</label>
                                            <%-- Sửa: Đổi 'department' thành 'departmentName' --%>
                                            <input type="text" id="phongLamViec" name="phongLamViec" value="${profile.departmentName}" disabled>
                                        </div>
                                        <div class="form-group">
                                            <label for="chucVu">Chức vụ</label>
                                            <%-- Sửa: Đổi 'position' thành 'positionName' --%>
                                            <input type="text" id="chucVu" name="chucVu" value="${profile.positionName}" disabled>
                                        </div>
                                        <div class="form-group full-width">
                                            <label for="ghiChu">Ghi chú</label>
                                            <textarea id="ghiChu" name="ghiChu" rows="3" disabled>${profile.notes}</textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="profile-card">
                                <div class="card-body">
                                    <h2>Thông tin cá nhân</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="cmnd">Số CMND/CCCD</label>
                                            <input type="text" id="cmnd" name="cmnd" value="${profile.identityCardNumber}" disabled>
                                        </div>
                                        <div class="form-group">
                                            <label for="ngaySinh">Ngày sinh</label>
                                            <input type="date" id="ngaySinh" name="ngaySinh" value="${profile.dateOfBirth}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label>Giới tính</label>
                                        <div class="radio-group">
                                            <%-- Sửa: So sánh với giá trị trong DB ('male', 'female', 'other') --%>
                                            <label><input type="radio" name="gioiTinh" value="male" ${profile.gender == 'male' ? 'checked' : ''} disabled> Nam</label>
                                            <label><input type="radio" name="gioiTinh" value="female" ${profile.gender == 'female' ? 'checked' : ''} disabled> Nữ</label>
                                            <label><input type="radio" name="gioiTinh" value="other" ${profile.gender == 'other' ? 'checked' : ''} disabled> Khác</label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="profile-card">
                                <div class="card-body">
                                    <h2>Thông tin liên hệ</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email" value="${profile.email}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="tinhThanh">Tỉnh/Thành phố</label>
                                            <%-- Sửa: Đổi 'city' thành 'provinceName' --%>
                                            <input type="text" id="tinhThanh" name="tinhThanh" value="${profile.provinceName}" disabled>
                                        </div>
                                        <div class="form-group">
                                            <label for="quanHuyen">Quận/Huyện</label>
                                            <%-- Sửa: Đổi 'district' thành 'districtName' --%>
                                            <input type="text" id="quanHuyen" name="quanHuyen" value="${profile.districtName}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="phuongXa">Phường/Xã</label>
                                            <%-- Sửa: Đổi 'ward' thành 'wardName' --%>
                                            <input type="text" id="phuongXa" name="phuongXa" value="${profile.wardName}" disabled>
                                        </div>
                                         <div class="form-group">
                                            <label for="diaChi">Số nhà, tên đường</label>
                                            <%-- Sửa: Đổi 'address' thành 'streetAddress' --%>
                                            <input type="text" id="diaChi" name="diaChi" value="${profile.streetAddress}" disabled>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-actions">
                                <button type="button" class="btn btn-secondary" id="btnClose" onclick="window.history.back()">Đóng</button>
                                <a href="editProfile?id=${profile.id}" class="btn btn-primary" role="button">Sửa thông tin</a>
                            </div>
                        </form>
                    </c:if>
                    <c:if test="${empty profile}">
                        <p style="text-align: center; margin-top: 2rem;">Không thể tải thông tin người dùng.</p>
                    </c:if>
                </div>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <script src="js/mainMenu.js"></script>
    </body>
</html>