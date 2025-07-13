<%--
    Document   : editCustomer
    Author     : anhndhe172050
    Description: Form for editing a customer, with a layout that matches the createCustomer page.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>

<c:set var="currentPage" value="listCustomer" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa: <c:out value="${customer.name}"/></title>
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/createCustomer.css"> <%-- Reusing the same CSS --%>
        <link rel="stylesheet" href="${BASE_URL}/css/viewCustomerDetail.css">
        <script src="https://unpkg.com/feather-icons"></script>
        <style>
            /* Ensures avatar image fits the container */
            .avatar-section #avatarPreview {
                width: 120px;
                height: 120px;
                object-fit: cover;
                border-radius: 8px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <form class="page-content" id="editCustomerForm" action="${BASE_URL}/editCustomer" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="enterpriseId" value="${customer.id}">
                    <input type="hidden" name="addressId" value="${customer.addressId}">
                    <input type="hidden" name="existingAvatarUrl" value="<c:out value='${customer.avatarUrl}'/>">

                    <div class="detail-header">
                        <a href="${BASE_URL}/viewCustomer?id=${customer.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy</span></a>
                        <div class="action-buttons">
                            <c:choose>
                                <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kinh doanh'}">
                                    <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-primary disabled-action" data-error="Bạn không có quyền sửa khách hàng."><i data-feather="save"></i>Lưu thay đổi</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <c:if test="${not empty errorMessage}"><div class="error-message"><strong>Lỗi:</strong> ${errorMessage}</div></c:if>

                        <div class="detail-layout">
                            <!-- MAIN COLUMN (LEFT) -->
                            <div class="main-column">
                                <div class="profile-header-card detail-card">
                                    <div class="card-body">
                                        <div class="avatar-section">

                                        <%-- === SỬA LỖI HIỂN THỊ ẢNH TẠI ĐÂY === --%>
                                        <c:choose>
                                            <c:when test="${not empty customer.avatarUrl}">
                                                <c:set var="imageUrl" value="${BASE_URL}/${customer.avatarUrl}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="imageUrl" value="https://placehold.co/120x120/E0F7FA/00796B?text=${customer.name.substring(0,1)}" />
                                            </c:otherwise>
                                        </c:choose>
                                        <img src="<c:out value='${imageUrl}'/>" alt="Ảnh đại diện" id="avatarPreview">

                                        <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                        <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Đổi ảnh</button>
                                    </div>
                                    <div class="customer-main-info" style="width: 100%;">
                                        <div class="form-group" style="margin-bottom: 16px;">
                                            <label for="name">Tên khách hàng (*)</label>
                                            <input type="text" id="name" name="name" class="form-control" value="<c:out value='${customer.name}'/>" required>
                                        </div>
                                        <%-- The radio buttons for individual/business are not directly editable --%>
                                        <%-- They are represented by the customer type dropdown in the sidebar --%>
                                        <div class="form-group">
                                            <label>Loại khách hàng</label>
                                            <p style="padding: 8px; font-weight: 500;">Doanh nghiệp</p> <%-- Assuming it's always an enterprise --%>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin liên hệ</h3>
                                <div class="card-body">
                                    <div class="info-grid" style="grid-template-columns: 1fr 1fr; gap: 1rem;">
                                        <c:if test="${not empty customer.contacts}">
                                            <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                        </c:if>
                                        <div class="form-group">
                                            <label for="representative">Người đại diện (*)</label>
                                            <input type="text" id="fullName" name="fullName" class="form-control" value="<c:out value='${primaryContact.fullName}'/>" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="position">Chức vụ (*)</label>
                                            <input type="text" id="position" name="position" class="form-control" value="<c:out value='${primaryContact.position}'/>" required>
                                        </div>
                                        <div class="form-group"><label for="phone">Số điện thoại (*)</label><input type="tel" id="phone" name="phone" class="form-control" value="<c:out value='${primaryContact.phoneNumber}'/>" required></div>
                                        <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" class="form-control" value="<c:out value='${primaryContact.email}'/>"></div>
                                        <div class="form-group"><label for="taxCode">Mã số thuế</label><input type="text" id="taxCode" name="taxCode" class="form-control" value="<c:out value='${customer.taxCode}'/>"></div>
                                        <div class="form-group"><label for="bankNumber">Số tài khoản ngân hàng</label><input type="text" id="bankNumber" name="bankNumber" class="form-control" value="<c:out value='${customer.bankNumber}'/>"></div>
                                    </div>
                                    <hr style="margin: 1.5rem 0;">
                                    <div class="info-grid" style="grid-template-columns: repeat(3, 1fr); gap: 1rem;">
                                        <div class="form-group">
                                            <label for="province">Tỉnh/Thành phố (*)</label>
                                            <select id="province" name="province" class="form-control" required>
                                                <c:forEach var="p" items="${allProvinces}"><option value="${p.id}" ${customer.provinceId == p.id ? 'selected' : ''}><c:out value="${p.name}"/></option></c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="district">Quận/Huyện (*)</label>
                                                <select id="district" name="district" class="form-control" required>
                                                <c:forEach var="d" items="${districtsForCustomer}"><option value="${d.id}" ${customer.districtId == d.id ? 'selected' : ''}><c:out value="${d.name}"/></option></c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="ward">Phường/Xã (*)</label>
                                                <select id="ward" name="ward" class="form-control" required>
                                                <c:forEach var="w" items="${wardsForCustomer}"><option value="${w.id}" ${customer.wardId == w.id ? 'selected' : ''}><c:out value="${w.name}"/></option></c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="form-group" style="margin-top: 1rem;">
                                            <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                            <input type="text" id="streetAddress" name="streetAddress" class="form-control" value="<c:out value='${customer.streetAddress}'/>" required>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- SIDEBAR COLUMN (RIGHT) -->
                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin bổ sung</h3>
                                <div class="card-body">
                                    <div class="form-group"><label>Mã khách hàng</label><input type="text" class="form-control" value="<c:out value='${customer.enterpriseCode}'/>" readonly></div>
                                    <div class="form-group">
                                        <label for="customerTypeId">Nhóm khách hàng</label>
                                        <select id="customerTypeId" name="customerTypeId" class="form-control">
                                            <c:forEach var="type" items="${allCustomerTypes}"><option value="${type.id}" ${customer.customerTypeId == type.id ? 'selected' : ''}><c:out value="${type.name}"/></option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="employeeId">Nhân viên phụ trách</label>
                                        <c:set var="assignedUserId" value="${not empty customer.assignedUsers ? customer.assignedUsers[0].id : -1}"/>
                                        <select id="employeeId" name="employeeId" class="form-control" required>
                                            <c:forEach var="emp" items="${allEmployees}"><option value="${emp.id}" ${assignedUserId == emp.id ? 'selected' : ''}><c:out value="${emp.fullName}"/></option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Ngày tham gia</label>
                                            <input type="date" class="form-control" value="<fmt:formatDate value='${customer.createdAt.time}' pattern='yyyy-MM-dd' />" readonly>
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
                        fetch('${BASE_URL}/getDistricts?provinceId=' + provinceId)
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
                        fetch('${BASE_URL}/getWards?districtId=' + districtId)
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
        <script>
            // Script này chỉ cần thêm một lần vào trang layout chính hoặc vào từng trang cần thiết
            document.addEventListener('DOMContentLoaded', function () {
                document.body.addEventListener('click', function (event) {
                    const disabledLink = event.target.closest('.disabled-action');
                    if (disabledLink) {
                        event.preventDefault();
                        const errorMessage = disabledLink.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.';
                        alert(errorMessage);
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
