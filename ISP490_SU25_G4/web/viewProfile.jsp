<%-- 
    Document   : viewProfile
    Created on : Jun 4, 2025, 11:03:31 AM
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
        <title>Dashboard</title>

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
                    <div class="page-title">Xem thông tin chi tiết </div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>
                <div class="profile-form-container">
                    <form id="profileForm">
                        <div class="profile-card">
                            <div class="card-body-split">
                                <div class="avatar-section">                                   
                                    <img src="${profile.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                    <input type="file" id="avatarUpload" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Chọn ảnh</button>
                                </div>
                                <div class="info-section">
                                    <h2>Thông tin khởi tạo</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="maNhanVien">Mã nhân viên</label>
                                            <span class="form-data">${profile.id}</span> 
                                        </div>
                                        <div class="form-group">
                                            <label for="tenNhanVien">Tên nhân viên</label>
                                            <input type="text" id="tenNhanVien" name="tenNhanVien" value="${profile.name}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="soDienThoai">Số điện thoại</label>
                                            <input type="tel" id="soDienThoai" name="soDienThoai" value="${profile.phone}" disabled>
                                        </div>
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
                                    <select id="phongLamViec" name="phongLamViec" disabled>
                                        <option value="cskh" ${profile.department == 'cskh' ? 'selected' : ''}>CSKH</option>
                                        <option value="ketoan" ${profile.department == 'ketoan' ? 'selected' : ''}>Kế toán</option>
                                        <%-- Thêm các phòng ban khác nếu cần --%>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="chucVu">Chức vụ</label>
                                    <select id="chucVu" name="chucVu" disabled>
                                        <option value="thukho" ${profile.role == 'thukho' ? 'selected' : ''}>Thủ kho</option>
                                        <option value="nhanvien" ${profile.role == 'nhanvien' ? 'selected' : ''}>Nhân viên</option>
                                        <%-- Thêm các chức vụ khác nếu cần --%>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group full-width">
                                <label for="ghiChu">Ghi chú</label>
                                <textarea id="ghiChu" name="ghiChu" rows="3" disabled>${profile.note}</textarea>
                            </div>
                        </div>
                </div>

                <div class="profile-card">
                    <div class="card-body">
                        <h2>Thông tin cá nhân</h2>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="cmnd">Số CMND/CCCD</label>
                                <input type="text" id="cmnd" name="cmnd" value="${profile.idCard}" disabled>
                            </div>
                            <div class="form-group">
                                <label for="ngaySinh">Ngày sinh</label>
                                <input type="date" id="ngaySinh" name="ngaySinh" value="${profile.dob}" disabled>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Giới tính</label>
                            <div class="radio-group">
                                <label><input type="radio" name="gioiTinh" value="nam" ${profile.gender == 'nam' ? 'checked' : ''} disabled> Nam</label>
                                <label><input type="radio" name="gioiTinh" value="nu" ${profile.gender == 'nu' ? 'checked' : ''} disabled> Nữ</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="profile-card">
                    <div class="card-body">
                        <h2>Thông tin liên hệ</h2>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="diaChi">Địa chỉ</label>
                                <input type="text" id="diaChi" name="diaChi" value="${profile.address}" disabled>
                            </div>
                            <div class="form-group">
                                <label for="email">Email</label>
                                <input type="email" id="email" name="email" value="${profile.email}" disabled>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="tinhThanh">Tỉnh/Thành phố</label>
                                <input type="text" id="tinhThanh" name="tinhThanh" value="${profile.cityDistrict}" disabled>
                            </div>
                            <%--  <div class="form-group">
                                <label for="quanHuyen">Quận/Huyện</label>
                                <input type="text" id="quanHuyen" name="quanHuyen" value="${nhanVien.quanHuyen}" disabled>
                            </div>
                            --%> 
                            <div class="form-group">
                                <label for="phuongXa">Phường/Xã</label>
                                <input type="text" id="phuongXa" name="phuongXa" value="${nhanVien.phuongXa}" disabled>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group full-width">
                                <label for="mxh">Mạng xã hội (Link)</label>
                                <input type="url" id="mxh" name="mxh" value="${nhanVien.mxh}" disabled>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" id="btnClose">Đóng</button>
                    <a href="editProfile.jsp?id=${profile.id}" class="btn btn-primary" role="button">Sửa thông tin</a>
                </div>
                </form>
        </div>           

    </main>
</div>

<script>
    feather.replace();
</script>

<script src="js/mainMenu.js"></script>

</body>
</html>
