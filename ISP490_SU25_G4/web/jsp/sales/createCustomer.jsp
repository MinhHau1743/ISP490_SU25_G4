<%--
    Document    : createCustomer
    Created on : Jun 18, 2025
    Author      : anhndhe172050
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="customer/list" />
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
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/createCustomer.css">
        <link rel="stylesheet" href="${BASE_URL}/css/viewCustomerDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">

                <c:if test="${not empty errorMessage}">
                    <div class="error-message" style="background-color: #ffebee; color: #c62828; padding: 16px; margin: 0 24px 16px; border-radius: 8px; border: 1px solid #c62828;">
                        <strong>Lỗi từ Server:</strong> ${errorMessage}
                    </div>
                </c:if>

                <form class="page-content" id="createCustomerForm" action="${BASE_URL}/customer/create" method="post" enctype="multipart/form-data">
                    <div class="detail-header">
                        <a href="${BASE_URL}/customer/list" class="back-link">
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
                                        <c:if test="${not empty avatarError}">
                                            <span class="error-message">${avatarError}</span>
                                        </c:if>
                                    </div>
                                    <div class="customer-main-info" style="width: 100%;">
                                        <div class="form-group ${not empty customerNameError ? 'has-error' : ''}" style="margin-bottom: 16px;">
                                            <label for="customerName">Tên doanh nghiệp (*)</label>
                                            <input type="text" id="customerName" name="customerName" class="form-control" placeholder="Nhập tên công ty hoặc cá nhân" 
                                                   value="${param.customerName}" required>
                                            <c:if test="${not empty customerNameError}">
                                                <span class="error-message">${customerNameError}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Thông tin doanh nghiệp</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group ${not empty hotlineError ? 'has-error' : ''}">
                                            <label for="hotline">Hotline (*)</label>
                                            <input type="tel" id="hotline" name="hotline" class="form-control" placeholder="VD: 0912345678" 
                                                   value="${param.hotline}"
                                                   required 
                                                   pattern="^(0(2\d{8}|[35789]\d{8})|(1800|1900)\d{4,6})$"
                                                   title="Vui lòng nhập số điện thoại hợp lệ của Việt Nam (VD: 0912345678, 02412345678, 19001234).">
                                            <c:if test="${not empty hotlineError}">
                                                <span class="error-message">${hotlineError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty businessEmailError ? 'has-error' : ''}">
                                            <label for="businessEmail">Email doanh nghiệp (*)</label>
                                            <input type="email" id="businessEmail" name="businessEmail" class="form-control" 
                                                   placeholder="VD: contact@company.com" value="${param.businessEmail}" required>
                                            <c:if test="${not empty businessEmailError}">
                                                <span class="error-message">${businessEmailError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty taxCodeError ? 'has-error' : ''}">
                                            <label for="taxCode">Mã số thuế</label>
                                            <input type="text" id="taxCode" name="taxCode" class="form-control" placeholder="VD: 0102030405"
                                                   value="${param.taxCode}"
                                                   pattern="^(\d{10}|\d{10}-\d{3})$"
                                                   title="Mã số thuế phải là 10 chữ số, hoặc 13 chữ số theo định dạng XXXXXXXXXX-XXX.">
                                            <c:if test="${not empty taxCodeError}">
                                                <span class="error-message">${taxCodeError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group">
                                            <label for="bankNumber">Số tài khoản ngân hàng</label>
                                            <input type="text" id="bankNumber" name="bankNumber" class="form-control" 
                                                   placeholder="Tên ngân hàng - Số tài khoản" value="${param.bankNumber}">
                                        </div>
                                    </div>

                                    <hr style="margin: 1.5rem 0;">

                                    <div class="info-grid" style="margin-top: 1rem; grid-template-columns: repeat(3, 1fr);">
                                        <div class="form-group ${not empty provinceError ? 'has-error' : ''}">
                                            <label for="province">Tỉnh/Thành phố (*)</label>
                                            <select id="province" name="province" class="form-control" required>
                                                <option value="" disabled selected>-- Chọn Tỉnh/Thành --</option>
                                                <c:forEach var="p" items="${provinces}">
                                                    <option value="${p.id}" ${param.province eq p.id ? 'selected' : ''}>${p.name}</option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty provinceError}">
                                                <span class="error-message">${provinceError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty districtError ? 'has-error' : ''}">
                                            <label for="district">Quận/Huyện (*)</label>
                                            <select id="district" name="district" class="form-control" required disabled>
                                                <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                            </select>
                                            <c:if test="${not empty districtError}">
                                                <span class="error-message">${districtError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty wardError ? 'has-error' : ''}">
                                            <label for="ward">Phường/Xã (*)</label>
                                            <select id="ward" name="ward" class="form-control" required disabled>
                                                <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                            </select>
                                            <c:if test="${not empty wardError}">
                                                <span class="error-message">${wardError}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="form-group ${not empty streetAddressError ? 'has-error' : ''}" style="margin-top: 1rem;">
                                        <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                        <input type="text" id="streetAddress" name="streetAddress" class="form-control" 
                                               placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." value="${param.streetAddress}" required>
                                        <c:if test="${not empty streetAddressError}">
                                            <span class="error-message">${streetAddressError}</span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Thông tin người đại diện</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group">
                                            <label for="fullName">Họ và tên</label>
                                            <input type="text" id="fullName" name="fullName" class="form-control" 
                                                   placeholder="VD: Nguyễn Văn An" value="${param.fullName}">
                                        </div>
                                        <div class="form-group">
                                            <label for="position">Chức vụ</label>
                                            <input type="text" id="position" name="position" class="form-control" 
                                                   placeholder="VD: Giám đốc, Kế toán" value="${param.position}">
                                        </div>
                                        <div class="form-group ${not empty phoneError ? 'has-error' : ''}">
                                            <label for="phone">Số điện thoại</label>
                                            <input type="tel" id="phone" name="phone" class="form-control" placeholder="VD: 0987654321"
                                                   value="${param.phone}"
                                                   pattern="^(0(2\d{8}|[35789]\d{8})|(1800|1900)\d{4,6})$"
                                                   title="Vui lòng nhập số điện thoại hợp lệ của Việt Nam (nếu có).">
                                            <c:if test="${not empty phoneError}">
                                                <span class="error-message">${phoneError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty emailError ? 'has-error' : ''}">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email" class="form-control" 
                                                   placeholder="VD: an.nguyen@email.com" value="${param.email}">
                                            <c:if test="${not empty emailError}">
                                                <span class="error-message">${emailError}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin bổ sung</h3>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="customerCode">Mã khách hàng</label>
                                        <input type="text" id="customerCode" name="customerCode" class="form-control" value="(Tự động tạo)" readonly>
                                    </div>
                                    <div class="form-group ${not empty customerGroupError ? 'has-error' : ''}">
                                        <label for="customerGroup">Nhóm khách hàng (*)</label>
                                        <select id="customerGroup" name="customerGroup" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn nhóm --</option>
                                            <c:forEach var="type" items="${customerTypes}">
                                                <option value="${type.id}" ${param.customerGroup eq type.id ? 'selected' : ''}>${type.name}</option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty customerGroupError}">
                                            <span class="error-message">${customerGroupError}</span>
                                        </c:if>
                                    </div>
                                    <div class="form-group ${not empty employeeIdError ? 'has-error' : ''}">
                                        <label for="employeeId">Nhân viên phụ trách (*)</label>
                                        <select id="employeeId" name="employeeId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn nhân viên --</option>
                                            <c:forEach var="emp" items="${employees}">
                                                <option value="${emp.id}" ${param.employeeId eq emp.id ? 'selected' : ''}>${emp.fullNameCombined}</option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty employeeIdError}">
                                            <span class="error-message">${employeeIdError}</span>
                                        </c:if>
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

        <%-- === HTML FOR SUCCESS OVERLAY === --%>
        <c:if test="${not empty successMessage}">
            <div id="successOverlay">
                <div class="success-box">
                    <i data-feather="check-circle" style="color: #4CAF50; width: 64px; height: 64px;"></i>
                    <h2 style="color: #333; margin-top: 20px;">Thành công!</h2>
                    <p style="color: #555; font-size: 18px; max-width: 400px;">${successMessage}</p>
                    <p style="color: #999; margin-top: 15px; font-size: 14px;">Sẽ tự động chuyển hướng sau 3 giây...</p>
                </div>
            </div>
        </c:if>

        <script>
            feather.replace();

            // Avatar preview script
            document.getElementById('btnChooseAvatar').addEventListener('click', function () {
                document.getElementById('avatarUpload').click();
            });
            document.getElementById('avatarUpload').addEventListener('change', function (event) {
                const [file] = event.target.files;
                if (file) {
                    document.getElementById('avatarPreview').src = URL.createObjectURL(file);
                }
            });

            // Dynamic address loading and other scripts
            document.addEventListener('DOMContentLoaded', function () {
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');

                const today = new Date().toISOString().split('T')[0];
                document.getElementById('joinDate').value = today;

                // Nếu đã chọn tỉnh trước đó (do lỗi validation), tải lại quận/huyện
                const selectedProvinceId = "${param.province}";
                if (selectedProvinceId) {
                    provinceSelect.value = selectedProvinceId;
                    loadDistricts(selectedProvinceId);
                }

                provinceSelect.addEventListener('change', function () {
                    const provinceId = this.value;
                    loadDistricts(provinceId);
                });

                districtSelect.addEventListener('change', function () {
                    const districtId = this.value;
                    loadWards(districtId);
                });

                function loadDistricts(provinceId) {
                    districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                    districtSelect.disabled = true;
                    wardSelect.disabled = true;

                    if (provinceId) {
                        fetch('${BASE_URL}/customer/getDistricts?provinceId=' + provinceId)
                                .then(response => response.json())
                                .then(data => {
                                    districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                                    data.forEach(function (district) {
                                        const option = document.createElement('option');
                                        option.value = district.id;
                                        option.textContent = district.name;
                                        // Nếu đã chọn quận/huyện trước đó (do lỗi validation)
                                        if (district.id == "${param.district}") {
                                            option.selected = true;
                                        }
                                        districtSelect.appendChild(option);
                                    });
                                    districtSelect.disabled = false;
                                    
                                    // Nếu đã chọn quận/huyện trước đó, tải lại phường/xã
                                    if ("${param.district}") {
                                        districtSelect.value = "${param.district}";
                                        loadWards("${param.district}");
                                    }
                                })
                                .catch(error => console.error('Error fetching districts:', error));
                    }
                }

                function loadWards(districtId) {
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                    wardSelect.disabled = true;

                    if (districtId) {
                        fetch('${BASE_URL}/customer/getWards?districtId=' + districtId)
                                .then(response => response.json())
                                .then(data => {
                                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                                    data.forEach(function (ward) {
                                        const option = document.createElement('option');
                                        option.value = ward.id;
                                        option.textContent = ward.name;
                                        // Nếu đã chọn phường/xã trước đó (do lỗi validation)
                                        if (ward.id == "${param.ward}") {
                                            option.selected = true;
                                        }
                                        wardSelect.appendChild(option);
                                    });
                                    wardSelect.disabled = false;
                                })
                                .catch(error => console.error('Error fetching wards:', error));
                    }
                }

                // Success overlay script
                const successOverlay = document.getElementById('successOverlay');
                const redirectUrl = "${redirectUrl}";

                if (successOverlay) {
                    feather.replace();
                    setTimeout(() => {
                        successOverlay.classList.add('show');
                    }, 10);
                    setTimeout(function () {
                        if (redirectUrl) {
                            window.location.href = redirectUrl;
                        }
                    }, 3000);
                }
            });
        </script>

        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>