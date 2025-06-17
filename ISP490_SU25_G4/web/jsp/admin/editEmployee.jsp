<%-- 
    Document   : editEmployee
    Created on : Jun 14, 2025, 1:27:37 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="rawPage" value="${pageContext.request.requestURI.substring(pageContext.request.requestURI.lastIndexOf('/') + 1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa thông tin nhân viên</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">


        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/menu.css">
        <link rel="stylesheet" href="../../css/pagination.css">
        <link rel="stylesheet" href="../../css/profile.css">
        <script src="https://cdn.jsdelivr.net/npm/feather-icons/dist/feather.min.js"></script>


    </head>
    <body>

        <div class="app-container">
            <%-- Nhúng menu vào --%>
            <jsp:include page="../../menu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Chỉnh sửa thông tin nhân viên</div>

                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>

                </header>

                <div class="profile-form-container">

                    <form id="editProfileForm" action="updateProfile" method="post" enctype="multipart/form-data">
                        <%-- Card 1: Thông tin khởi tạo --%>
                        <div class="profile-card">
                            <div class="card-body-split">
                                <div class="avatar-section">
                                    <img src="${empty nhanVien.avatarUrl ? 'https://placehold.co/170x170' : nhanVien.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                    <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Thay đổi ảnh</button>
                                </div>
                                <div class="info-section">
                                    <h2>Thông tin khởi tạo</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label>Mã nhân viên</label>
                                            <%-- Mã nhân viên không thể sửa, chỉ hiển thị --%>
                                            <input type="text" value="${nhanVien.maNhanVien}" disabled>
                                            <%-- Gửi mã nhân viên dưới dạng hidden field để server biết cập nhật cho ai --%>
                                            <input type="hidden" name="maNhanVien" value="${nhanVien.maNhanVien}">
                                        </div>
                                        <div class="form-group">
                                            <label for="tenNhanVien">Tên nhân viên</label>
                                            <input type="text" id="tenNhanVien" name="tenNhanVien" value="${nhanVien.tenNhanVien}">
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="soDienThoai">Số điện thoại</label>
                                            <input type="tel" id="soDienThoai" name="soDienThoai" value="${nhanVien.soDienThoai}">
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
                                        <label for="phongLamViec">Phòng làm việc</label>
                                        <select id="phongLamViec" name="phongLamViec">
                                            <option value="cskh" ${nhanVien.phongBan == 'cskh' ? 'selected' : ''}>CSKH</option>
                                            <option value="ketoan" ${nhanVien.phongBan == 'ketoan' ? 'selected' : ''}>Kế toán</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="chucVu">Chức vụ</label>
                                        <select id="chucVu" name="chucVu">
                                            <option value="thukho" ${nhanVien.chucVu == 'thukho' ? 'selected' : ''}>Thủ kho</option>
                                            <option value="nhanvien" ${nhanVien.chucVu == 'nhanvien' ? 'selected' : ''}>Nhân viên</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group full-width">
                                    <label for="ghiChu">Ghi chú</label>
                                    <textarea id="ghiChu" name="ghiChu" rows="3">${nhanVien.ghiChu}</textarea>
                                </div>
                            </div>
                        </div>

                        <%-- Card 3: Thông tin cá nhân --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin cá nhân</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="cmnd">Số CMND/CCCD</label>
                                        <input type="text" id="cmnd" name="cmnd" value="${nhanVien.cmnd}">
                                    </div>
                                    <div class="form-group">
                                        <label for="ngaySinh">Ngày sinh</label>
                                        <input type="date" id="ngaySinh" name="ngaySinh" value="${nhanVien.ngaySinh}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="gioiTinh" value="nam" ${nhanVien.gioiTinh == 'nam' ? 'checked' : ''}> Nam</label>
                                        <label><input type="radio" name="gioiTinh" value="nu" ${nhanVien.gioiTinh == 'nu' ? 'checked' : ''}> Nữ</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Card 4: Thông tin liên hệ --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin liên hệ</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="diaChi">Địa chỉ</label>
                                        <input type="text" id="diaChi" name="diaChi" value="${nhanVien.diaChi}">
                                    </div>
                                    <div class="form-group">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" name="email" value="${nhanVien.email}">
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="tinhThanh">Tỉnh/Thành phố</label>
                                        <input type="text" id="tinhThanh" name="tinhThanh" value="${nhanVien.tinhThanh}">
                                    </div>
                                    <div class="form-group">
                                        <label for="quanHuyen">Quận/Huyện</label>
                                        <input type="text" id="quanHuyen" name="quanHuyen" value="${nhanVien.quanHuyen}">
                                    </div>
                                    <div class="form-group">
                                        <label for="phuongXa">Phường/Xã</label>
                                        <input type="text" id="phuongXa" name="phuongXa" value="${nhanVien.phuongXa}">
                                    </div>
                                </div>

                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="../admin/viewEmployee?id=${nhanVien.maNhanVien}" class="btn btn-secondary" role="button">Hủy</a>
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </main>
        </div>

        <script>
            feather.replace();
        </script>

        <script src="../../js/mainMenu.js"></script>
        <script src="../../js/editProfile.js"></script>

    </body>
</html>


