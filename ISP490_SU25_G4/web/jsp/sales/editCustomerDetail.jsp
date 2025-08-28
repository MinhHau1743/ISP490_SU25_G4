<%--
    Document    : editCustomer
    Author      : anhndhe172050
    Description: Form for editing a customer, now with browser-native validation and placeholders.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>

<c:set var="currentPage" value="customer/list" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa: <c:out value="${customer.name}"/></title>
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/createCustomer.css"> 
        <link rel="stylesheet" href="${BASE_URL}/css/viewCustomerDetail.css">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <style>
            .avatar-section #avatarPreview {
                width: 120px;
                height: 120px;
                object-fit: cover;
                border-radius: 8px;
            }
            
            /* Style for error messages */
            .error-message {
                color: #d93025;
                font-size: 14px;
                margin-top: 5px;
                display: block;
            }
            
            .form-group.has-error input,
            .form-group.has-error select {
                border-color: #d93025;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <form class="page-content" id="editCustomerForm" action="${BASE_URL}/customer/edit" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="enterpriseId" value="${customer.id}">
                    <input type="hidden" name="addressId" value="${customer.addressId}">
                    <input type="hidden" name="existingAvatarUrl" value="<c:out value='${customer.avatarUrl}'/>">

                    <div class="detail-header">
                        <a href="${BASE_URL}/customer/view?id=${customer.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy</span></a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                        </div>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="error-message" style="background-color: #ffebee; color: #c62828; padding: 16px; margin: 0 24px 16px; border-radius: 8px; border: 1px solid #c62828;">
                            <strong>Lỗi:</strong> ${errorMessage}
                        </div>
                    </c:if>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="profile-header-card detail-card">
                                <div class="card-body">
                                    <div class="avatar-section">
                                        <c:choose>
                                            <c:when test="${not empty customer.avatarUrl}">
                                                <img src="${BASE_URL}/${customer.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="https://placehold.co/120x120/E0F7FA/00796B?text=${customer.name.substring(0,1)}" alt="Ảnh đại diện" id="avatarPreview">
                                            </c:otherwise>
                                        </c:choose>

                                        <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                        <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Đổi ảnh</button>
                                        <c:if test="${not empty avatarError}">
                                            <span class="error-message">${avatarError}</span>
                                        </c:if>
                                    </div>
                                    <div class="customer-main-info" style="width: 100%;">
                                        <div class="form-group ${not empty customerNameError ? 'has-error' : ''}" style="margin-bottom: 16px;">
                                            <label for="customerName">Tên doanh nghiệp (*)</label>
                                            <input type="text" id="customerName" name="customerName" class="form-control" placeholder="Nhập tên công ty hoặc cá nhân" value="<c:out value='${param.customerName != null ? param.customerName : customer.name}'/>" required>
                                            <c:if test="${not empty customerNameError}">
                                                <span class="error-message">${customerNameError}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <%-- Thông tin doanh nghiệp --%>
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin doanh nghiệp</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group ${not empty hotlineError ? 'has-error' : ''}">
                                            <label for="hotline">Hotline (*)</label>
                                            <input type="tel" id="hotline" name="hotline" class="form-control" placeholder="VD: 0912345678" value="<c:out value='${param.hotline != null ? param.hotline : customer.hotline}'/>" 
                                                   required 
                                                   pattern="^(0(2\d{8}|[35789]\d{8})|(1800|1900)\d{4,6})$"
                                                   title="Vui lòng nhập số điện thoại hợp lệ của Việt Nam (VD: 0912345678, 02412345678, 19001234).">
                                            <c:if test="${not empty hotlineError}">
                                                <span class="error-message">${hotlineError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty businessEmailError ? 'has-error' : ''}">
                                            <label for="businessEmail">Email doanh nghiệp (*)</label>
                                            <input type="email" id="businessEmail" name="businessEmail" class="form-control" placeholder="VD: contact@company.com" value="<c:out value='${param.businessEmail != null ? param.businessEmail : customer.businessEmail}'/>" required>
                                            <c:if test="${not empty businessEmailError}">
                                                <span class="error-message">${businessEmailError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty taxCodeError ? 'has-error' : ''}">
                                            <label for="taxCode">Mã số thuế</label>
                                            <input type="text" id="taxCode" name="taxCode" class="form-control" placeholder="VD: 0102030405" value="<c:out value='${param.taxCode != null ? param.taxCode : customer.taxCode}'/>"
                                                   pattern="^(\d{10}|\d{10}-\d{3})$"
                                                   title="Mã số thuế phải là 10 chữ số, hoặc 13 chữ số theo định dạng XXXXXXXXXX-XXX.">
                                            <c:if test="${not empty taxCodeError}">
                                                <span class="error-message">${taxCodeError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group">
                                            <label for="bankNumber">Số tài khoản ngân hàng</label>
                                            <input type="text" id="bankNumber" name="bankNumber" class="form-control" placeholder="Tên ngân hàng - Số tài khoản" value="<c:out value='${param.bankNumber != null ? param.bankNumber : customer.bankNumber}'/>">
                                        </div>
                                    </div>
                                    <hr style="margin: 1.5rem 0;">
                                    <div class="info-grid" style="margin-top: 1rem; grid-template-columns: repeat(3, 1fr);">
                                        <div class="form-group ${not empty provinceError ? 'has-error' : ''}">
                                            <label for="province">Tỉnh/Thành phố (*)</label>
                                            <select id="province" name="province" class="form-control" required>
                                                <c:forEach var="p" items="${allProvinces}">
                                                    <option value="${p.id}" ${(param.province != null ? param.province : customer.provinceId) == p.id ? 'selected' : ''}>
                                                        <c:out value="${p.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty provinceError}">
                                                <span class="error-message">${provinceError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty districtError ? 'has-error' : ''}">
                                            <label for="district">Quận/Huyện (*)</label>
                                            <select id="district" name="district" class="form-control" required>
                                                <c:forEach var="d" items="${districtsForCustomer}">
                                                    <option value="${d.id}" ${(param.district != null ? param.district : customer.districtId) == d.id ? 'selected' : ''}>
                                                        <c:out value="${d.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty districtError}">
                                                <span class="error-message">${districtError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty wardError ? 'has-error' : ''}">
                                            <label for="ward">Phường/Xã (*)</label>
                                            <select id="ward" name="ward" class="form-control" required>
                                                <c:forEach var="w" items="${wardsForCustomer}">
                                                    <option value="${w.id}" ${(param.ward != null ? param.ward : customer.wardId) == w.id ? 'selected' : ''}>
                                                        <c:out value="${w.name}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                            <c:if test="${not empty wardError}">
                                                <span class="error-message">${wardError}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="form-group ${not empty streetAddressError ? 'has-error' : ''}" style="margin-top: 1rem;">
                                        <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                        <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." value="<c:out value='${param.streetAddress != null ? param.streetAddress : customer.streetAddress}'/>" required>
                                        <c:if test="${not empty streetAddressError}">
                                            <span class="error-message">${streetAddressError}</span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <%-- Thông tin người đại diện --%>
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin người đại diện</h3>
                                <div class="card-body">
                                    <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                    <div class="info-grid">
                                        <div class="form-group">
                                            <label for="fullName">Họ và tên</label>
                                            <input type="text" id="fullName" name="fullName" class="form-control" placeholder="VD: Nguyễn Văn An" value="<c:out value='${param.fullName != null ? param.fullName : primaryContact.fullName}'/>" >
                                        </div>
                                        <div class="form-group">
                                            <label for="position">Chức vụ</label>
                                            <input type="text" id="position" name="position" class="form-control" placeholder="VD: Giám đốc, Kế toán" value="<c:out value='${param.position != null ? param.position : primaryContact.position}'/>" >
                                        </div>
                                        <div class="form-group ${not empty phoneError ? 'has-error' : ''}">
                                            <label for="phone">Số điện thoại</label>
                                            <input type="tel" id="phone" name="phone" class="form-control" placeholder="VD: 0987654321" value="<c:out value='${param.phone != null ? param.phone : primaryContact.phoneNumber}'/>" 
                                                   pattern="^(0(2\d{8}|[35789]\d{8})|(1800|1900)\d{4,6})$"
                                                   title="Vui lòng nhập số điện thoại hợp lệ của Việt Nam (nếu có).">
                                            <c:if test="${not empty phoneError}">
                                                <span class="error-message">${phoneError}</span>
                                            </c:if>
                                        </div>
                                        <div class="form-group ${not empty emailError ? 'has-error' : ''}">
                                            <label for="email">Email</label>
                                            <input type="email" id="email" name="email" class="form-control" placeholder="VD: an.nguyen@email.com" value="<c:out value='${param.email != null ? param.email : primaryContact.email}'/>">
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
                                    <div class="form-group"><label for="customerCode">Mã khách hàng</label><input type="text" id="customerCode" name="customerCode" class="form-control" value="<c:out value='${customer.enterpriseCode}'/>" readonly></div>
                                    <div class="form-group ${not empty customerGroupError ? 'has-error' : ''}">
                                        <label for="customerGroup">Nhóm khách hàng (*)</label>
                                        <select id="customerGroup" name="customerGroup" class="form-control" required>
                                            <c:forEach var="type" items="${allCustomerTypes}">
                                                <option value="${type.id}" ${(param.customerGroup != null ? param.customerGroup : customer.customerTypeId) == type.id ? 'selected' : ''}>
                                                    <c:out value="${type.name}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty customerGroupError}">
                                            <span class="error-message">${customerGroupError}</span>
                                        </c:if>
                                    </div>
                                    <div class="form-group ${not empty employeeIdError ? 'has-error' : ''}">
                                        <label for="employeeId">Nhân viên phụ trách (*)</label>
                                        <c:set var="assignedUserId" value="${not empty customer.assignedUsers ? customer.assignedUsers[0].id : -1}"/>
                                        <select id="employeeId" name="employeeId" class="form-control" required>
                                            <c:forEach var="emp" items="${allEmployees}">
                                                <option value="${emp.id}" ${(param.employeeId != null ? param.employeeId : assignedUserId) == emp.id ? 'selected' : ''}>
                                                    <c:out value="${emp.fullNameCombined}"/>
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty employeeIdError}">
                                            <span class="error-message">${employeeIdError}</span>
                                        </c:if>
                                    </div>
                                    <div class="form-group">
                                        <label for="joinDate">Ngày tham gia</label>
                                        <div style="padding-top: 8px; font-weight: 500;"><fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy"/></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // Avatar preview logic
                document.getElementById('btnChooseAvatar').addEventListener('click', () => document.getElementById('avatarUpload').click());
                document.getElementById('avatarUpload').addEventListener('change', event => {
                    const [file] = event.target.files;
                    if (file) {
                        document.getElementById('avatarPreview').src = URL.createObjectURL(file);
                    }
                });

                // Dynamic address dropdowns logic
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');

                provinceSelect.addEventListener('change', function () {
                    const provinceId = this.value;
                    districtSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
                    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
                    districtSelect.disabled = true;
                    wardSelect.disabled = true;

                    if (provinceId) {
                        fetch('${BASE_URL}/customer/getDistricts?provinceId=' + provinceId)
                                .then(response => response.json())
                                .then(data => {
                                    districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
                                    data.forEach(district => {
                                        districtSelect.add(new Option(district.name, district.id));
                                    });
                                    districtSelect.disabled = false;
                                });
                    }
                });
                districtSelect.addEventListener('change', function () {
                    const districtId = this.value;
                    wardSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
                    wardSelect.disabled = true;
                    if (districtId) {
                        fetch('${BASE_URL}/customer/getWards?districtId=' + districtId)
                                .then(response => response.json())
                                .then(data => {
                                    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
                                    data.forEach(ward => {
                                        wardSelect.add(new Option(ward.name, ward.id));
                                    });
                                    wardSelect.disabled = false;
                                });
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>