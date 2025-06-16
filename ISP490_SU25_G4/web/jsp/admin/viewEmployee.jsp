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
                                    <img src="https://placehold.co/170x170" alt="Ảnh đại diện" id="avatarPreview">
                                    <input type="file" id="avatarUpload" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Chọn ảnh</button>
                                </div>
                                <div class="info-section">
                                    <h2>Thông tin khởi tạo</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="maNhanVien">Mã nhân viên</label>
                                            <span class="form-data">${nhanVien.maNhanVien}</span> 
                                        </div>
                                        <div class="form-group">
                                            <label for="tenNhanVien">Tên nhân viên</label>
                                            <input type="text" id="tenNhanVien" name="tenNhanVien" value="${nhanVien.tenNhanVien}" disabled>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="soDienThoai">Số điện thoại</label>
                                            <input type="tel" id="soDienThoai" name="soDienThoai" value="${nhanVien.soDienThoai}" disabled>
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
                                        <select id="phongLamViec" name="phongLamViec" disabled>
                                            <option value="cskh" selected>CSKH</option>
                                            <option value="ketoan">Kế toán</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="chucVu">Chức vụ</label>
                                        <select id="chucVu" name="chucVu" disabled>
                                            <option value="thukho" selected>Thủ kho</option>
                                            <option value="nhanvien">Nhân viên</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group full-width">
                                    <label for="ghiChu">Ghi chú</label>
                                    <textarea id="ghiChu" name="ghiChu" rows="3" disabled>${nhanVien.ghiChu}</textarea>
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
                                        <input type="text" id="cmnd" name="cmnd" value="${nhanVien.cmnd}" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label for="ngaySinh">Ngày sinh</label>
                                        <input type="date" id="ngaySinh" name="ngaySinh" value="${nhanVien.ngaySinh}" disabled>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="gioiTinh" value="nam" ${nhanVien.gioiTinh == 'nam' ? 'checked' : ''} disabled> Nam</label>
                                        <label><input type="radio" name="gioiTinh" value="nu" ${nhanVien.gioiTinh == 'nu' ? 'checked' : ''} disabled> Nữ</label>
                                    </div>
                                </div>
                            </div>
                        </div>



                        <%-- BỔ SUNG: Card 4: Thông tin liên hệ --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin liên hệ</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="diaChi">Địa chỉ</label>
                                        <input type="text" id="diaChi" name="diaChi" value="${nhanVien.diaChi}" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" name="email" value="${nhanVien.email}" disabled>
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="tinhThanh">Tỉnh/Thành phố</label>
                                        <input type="text" id="tinhThanh" name="tinhThanh" value="${nhanVien.tinhThanh}" disabled>
                                    </div>
                                    <div class="form-group">
                                        <label for="quanHuyen">Quận/Huyện</label>
                                        <input type="text" id="quanHuyen" name="quanHuyen" value="${nhanVien.quanHuyen}" disabled>
                                    </div>
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
                            <a href="../../jsp/admin/editEmployee.jsp?id=${nhanVien.maNhanVien}" class="btn btn-primary" role="button">Sửa thông tin</a>
                        </div>
                    </form>
                </div>           

            </main>
        </div>

        <script>
            feather.replace();
        </script>

        <script src="../../js/mainMenu.js"></script>

    </body>
</html>


