<%--
    Document   : createCustomer
    Created on : Jun 18, 2025
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="listCustomer" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

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

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/createCustomer.css">
        <link rel="stylesheet" href="${BASE_URL}/css/viewCustomerDetail.css">

        <%-- <<< SỬA LỖI: Thêm CSS để giới hạn kích thước ảnh avatar >>> --%>
        <style>
            .avatar-section #avatarPreview {
                width: 120px;       /* Giới hạn chiều rộng */
                height: 120px;      /* Giới hạn chiều cao */
                object-fit: cover;  /* Đảm bảo ảnh lấp đầy khung mà không bị méo */
                border-radius: 8px; /* Bo góc cho đẹp */
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">

                <c:if test="${not empty errorMessage}">
                    <div class="error-message" style="background-color: #ffebee; color: #c62828; padding: 16px; margin: 0 24px 16px; border-radius: 8px; border: 1px solid #c62828;">
                        <strong>Lỗi:</strong> ${errorMessage}
                    </div>
                </c:if>

                <form class="page-content" id="createCustomerForm" action="${BASE_URL}/createCustomer" method="post" enctype="multipart/form-data">
                    <div class="detail-header">
                        <a href="${BASE_URL}/listCustomer" class="back-link">
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
                                <!-- Phần thông tin chính và avatar giữ nguyên -->
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
                                                <label><input type="radio" name="customerTypeRadio" value="individual" checked> Cá nhân</label>
                                                <label><input type="radio" name="customerTypeRadio" value="business"> Doanh nghiệp</label>
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
                                    </div>

                                    <!-- <<< PHẦN ĐỊA CHỈ ĐƯỢC THAY ĐỔI HOÀN TOÀN >>> -->
                                    <div class="info-grid" style="margin-top: 1rem; grid-template-columns: repeat(3, 1fr);">
                                        <div class="form-group">
                                            <label for="province">Tỉnh/Thành phố (*)</label>
                                            <select id="province" name="province" class="form-control" required>
                                                <option value="" disabled selected>-- Chọn Tỉnh/Thành --</option>
                                                <c:forEach var="p" items="${provinces}">
                                                    <option value="${p.id}">${p.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="district">Quận/Huyện (*)</label>
                                            <select id="district" name="district" class="form-control" required disabled>
                                                <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="ward">Phường/Xã (*)</label>
                                            <select id="ward" name="ward" class="form-control" required disabled>
                                                <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group" style="margin-top: 1rem;">
                                        <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                        <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <!-- Phần thông tin bổ sung -->
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin bổ sung</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="customerCode">Mã khách hàng</label><input type="text" id="customerCode" name="customerCode" class="form-control" value="(Tự động tạo)" readonly></div>
                                    <div class="form-group">
                                        <label for="customerGroup">Nhóm khách hàng</label>
                                        <select id="customerGroup" name="customerGroup" class="form-control">
                                            <c:if test="${empty customerTypes}">
                                                <option value="" disabled>Không tải được dữ liệu</option>
                                            </c:if>
                                            <c:forEach var="type" items="${customerTypes}">
                                                <option value="${type.id}">${type.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- <<< SỬA LỖI: Thêm lại các trường bị thiếu >>> -->
                                    <div class="form-group">
                                        <label for="employeeId">Nhân viên phụ trách</label>
                                        <select id="employeeId" name="employeeId" class="form-control">
                                            <option value="" disabled selected>-- Chọn nhân viên --</option>
                                            <%-- Khi có DAO cho nhân viên, bạn sẽ mở phần này ra
                                            <c:forEach var="emp" items="${employees}">
                                                <option value="${emp.id}">${emp.firstName} ${emp.lastName}</option>
                                            </c:forEach>
                                            --%>
                                            <option value="1">Nguyễn Văn A</option>
                                            <option value="2">Trần Thị B</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="joinDate">Ngày tham gia</label>
                                        <input type="date" id="joinDate" name="joinDate" class="form-control" readonly>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <script>
            // Thay thế biểu tượng feather
            feather.replace();

            // Script cho avatar preview
            document.getElementById('btnChooseAvatar').addEventListener('click', function () {
                document.getElementById('avatarUpload').click();
            });
            document.getElementById('avatarUpload').addEventListener('change', function (event) {
                const [file] = event.target.files;
                if (file) {
                    document.getElementById('avatarPreview').src = URL.createObjectURL(file);
                }
            });

            // <<< SCRIPT TẢI ĐỘNG ĐỊA CHỈ & NGÀY THAM GIA >>>
            document.addEventListener('DOMContentLoaded', function () {
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');
                const baseUrl = '${BASE_URL}';

                // <<< SỬA LỖI: Tự động điền ngày tham gia là ngày hiện tại >>>
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('joinDate').value = today;

                provinceSelect.addEventListener('change', function () {
                    const provinceId = this.value;
                    // Reset district and ward selects
                    districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                    districtSelect.disabled = true;
                    wardSelect.disabled = true;

                    if (provinceId) {
                        fetch(`${baseUrl}/getDistricts?provinceId=${provinceId}`)
                                                .then(response => response.json())
                                                .then(data => {
                                                    districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                                                    data.forEach(function (district) {
                                                        const option = document.createElement('option');
                                                        option.value = district.id;
                                                        option.textContent = district.name;
                                                        districtSelect.appendChild(option);
                                                    });
                                                    districtSelect.disabled = false;
                                                })
                                                .catch(error => console.error('Error fetching districts:', error));
                                    }
                                });

                                districtSelect.addEventListener('change', function () {
                                    const districtId = this.value;
                                    // Reset ward select
                                    wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                                    wardSelect.disabled = true;

                                    if (districtId) {
                                        fetch(`${baseUrl}/getWards?districtId=${districtId}`)
                                                                .then(response => response.json())
                                                                .then(data => {
                                                                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                                                                    data.forEach(function (ward) {
                                                                        const option = document.createElement('option');
                                                                        option.value = ward.id;
                                                                        option.textContent = ward.name;
                                                                        wardSelect.appendChild(option);
                                                                    });
                                                                    wardSelect.disabled = false;
                                                                })
                                                                .catch(error => console.error('Error fetching wards:', error));
                                                    }
                                                });
                                            });
        </script>
    </body>
</html>
