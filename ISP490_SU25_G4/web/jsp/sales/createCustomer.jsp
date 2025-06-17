<%-- 
    Document   : createCustomer
    Created on : Jun 17, 2025, 12:09:26 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="listCustomer" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm Khách hàng mới</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/createCustomer.css">
        <link rel="stylesheet" href="../../css/viewCustomerDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">

                <form class="page-content" action="createCustomer" method="post" enctype="multipart/form-data">
                    <div class="detail-header">
                        <a href="listCustomer.jsp" class="back-link">
                            <i data-feather="arrow-left"></i><span>Hủy</span>
                        </a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary">
                                <i data-feather="plus-circle"></i>Tạo Khách hàng
                            </button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="profile-header-card detail-card">
                                <div class="card-body">
                                    <div class="avatar-section">
                                        <img src="https://placehold.co/120x120/E0F7FA/00796B?text=Ảnh" alt="Ảnh đại diện" id="avatarPreview">
                                        <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                        <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Chọn ảnh</button>
                                    </div>
                                    <div class="customer-main-info" style="width: 100%;">
                                        <div class="form-group" style="margin-bottom: 16px;">
                                            <label for="customerName">Tên khách hàng (*)</label>
                                            <input type="text" id="customerName" name="customerName" class="form-control" placeholder="Nhập tên công ty hoặc cá nhân" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Loại khách hàng (*)</label>
                                            <div class="radio-group">
                                                <label><input type="radio" name="customerType" value="individual" checked> Cá nhân</label>
                                                <label><input type="radio" name="customerType" value="business"> Doanh nghiệp</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Thông tin liên hệ</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group"><label for="phone">Số điện thoại (*)</label><input type="tel" id="phone" name="phone" class="form-control" placeholder="VD: 0987654321" required></div>
                                        <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" class="form-control" placeholder="VD: example@email.com"></div>
                                        <div class="form-group"><label for="website">Website</label><input type="url" id="website" name="website" class="form-control" placeholder="VD: https://example.com"></div>
                                        <div class="form-group"><label for="address">Địa chỉ</label><input type="text" id="address" name="address" class="form-control" placeholder="Nhập địa chỉ chi tiết"></div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin bổ sung</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="customerCode">Mã khách hàng</label><input type="text" id="customerCode" name="customerCode" class="form-control" value="(Tự động tạo)" readonly></div>
                                    <div class="form-group"><label for="customerGroup">Nhóm khách hàng</label><select id="customerGroup" name="customerGroup" class="form-control"><option value="new" selected>Mới</option><option value="potential">Tiềm năng</option><option value="loyal">Thân thiết</option><option value="VIP">VIP</option></select></div>
                                    <div class="form-group"><label for="employeeId">Nhân viên phụ trách</label><select id="employeeId" name="employeeId" class="form-control"><option value="" disabled selected>-- Chọn nhân viên --</option><option value="1">Nguyễn Văn A</option><option value="2">Trần Thị B</option></select></div>
                                    <div class="form-group"><label for="joinDate">Ngày tham gia</label><input type="date" id="joinDate" name="joinDate" class="form-control" readonly></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <script src="../../js/createCustomer.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
