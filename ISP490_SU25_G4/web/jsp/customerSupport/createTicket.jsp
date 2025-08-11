<%-- File: /jsp/customerSupport/createTicket.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Phiếu Giao Việc Mới</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createTicket.css">
        <script src="https://unpkg.com/feather-icons"></script>
        <style>
            .address-section {
                margin-top: 1rem;
                padding: 1rem;
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                background-color: #f9f9f9;
            }
            .address-section h3 {
                margin-top: 0;
                margin-bottom: 1rem;
                color: #333;
                font-size: 1.1rem;
            }
            .address-grid {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 1rem;
                margin-bottom: 1rem;
            }
            .form-group.full-width {
                grid-column: 1 / -1;
            }
            @media (max-width: 768px) {
                .address-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div id="product-suggestion-box" class="suggestion-box"></div>
                <form id="createTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại</span></a>
                        <button type="submit" class="btn btn-primary"><i data-feather="plus-circle"></i>Tạo Phiếu</button>
                    </div>
                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin Phiếu Giao Việc</h2>
                                <div class="form-grid">
                                    <div class="form-group">
                                        <label>Mã Phiếu</label>
                                        <input type="text" class="form-control" value="(Tự động tạo)" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="enterpriseId">Khách hàng (*)</label>
                                        <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn khách hàng --</option>
                                            <c:forEach var="customer" items="${customerList}">
                                                <option value="${customer.id}">${customer.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="contractId">Hợp đồng</label>
                                        <select id="contractId" name="contractId" class="form-control">
                                            <option value="">-- Chọn hợp đồng --</option>
                                            <c:forEach var="contract" items="${contractList}">
                                                <option value="${contract.id}">${contract.contractCode}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn loại phiếu --</option>
                                            <c:forEach var="service" items="${serviceList}">
                                                <option value="${service.id}">${service.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- Địa chỉ Section -->
                                    <div class="form-group full-width">
                                        <div class="address-section">
                                            <h3>Địa chỉ thực hiện công việc</h3>
                                            <div class="address-grid">
                                                <div class="form-group">
                                                    <label for="province">Tỉnh/Thành phố (*)</label>
                                                    <%-- ĐÂY LÀ DẠNG ĐÚNG --%>
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
                                            <div class="form-group">
                                                <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                                <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group full-width">
                                        <label for="description">Mô tả chung (*)</label>
                                        <textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>

                                <table class="device-table">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị</th>
                                            <th>Serial Number</th>
                                            <th>Mô tả sự cố của thiết bị</th>
                                            <th class="action-col"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="device-tbody">
                                        <%-- JavaScript sẽ chèn các dòng <tr> vào đây --%>
                                    </tbody>
                                </table>

                                <div class="device-table-actions">
                                    <button type="button" id="addDeviceBtn" class="btn btn-secondary">
                                        <i data-feather="plus"></i>Thêm thiết bị
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="sidebar-column">
                            <div class="detail-card sidebar-form">
                                <h2 class="card-title">Chi tiết Giao việc</h2>
                                <div class="sidebar-form-row">
                                    <label for="priority">Mức độ ưu tiên</label>
                                    <select id="priority" name="priority" class="form-control">
                                        <option value="medium">Thông thường</option>
                                        <option value="high">Cao</option>
                                        <option value="urgent">Khẩn cấp</option>
                                    </select>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="employeeId">Gán cho nhân viên</label>
                                    <select id="employeeId" name="employeeId" class="form-control" required>
                                        <option value="" disabled selected>-- Chọn kỹ thuật viên --</option>
                                        <c:forEach var="employee" items="${employeeList}">
                                            <option value="${employee.id}">${employee.lastName} ${employee.middleName} ${employee.firstName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="sidebar-form-row">
                                    <label>Ngày tạo</label>
                                    <input type="date" id="createdDate" name="createdDate" class="form-control" readonly>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="scheduled_date">Ngày bắt đầu (*)</label>
                                    <input type="date" id="scheduled_date" name="scheduled_date" value="${scheduled_date}" class="form-control" required>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="end_date">Ngày kết thúc</label>
                                    <input type="date" id="end_date" name="end_date" value="${end_date}" class="form-control">
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="start_time">Giờ bắt đầu</label>
                                    <input type="time" id="start_time" name="start_time" value="${start_time}" class="form-control">
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="end_time">Giờ kết thúc</label>
                                    <input type="time" id="end_time" name="end_time" value="${end_time}" class="form-control">
                                </div>
                                <div class="sidebar-form-row">
                                    <label>Chi phí</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="isBillable" value="true"> Có</label>
                                        <label><input type="radio" name="isBillable" value="false" checked> Không</label>
                                    </div>
                                </div>
                                <div id="amount-group" class="sidebar-form-row" style="display: none;">
                                    <label for="amount">Số tiền (VND)</label>
                                    <input type="number" id="amount" name="amount" class="form-control" value="0">
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <%-- Đặt khối script này ở cuối file /jsp/customerSupport/createTicket.jsp, trước thẻ </body> --%>
        <script>
document.addEventListener('DOMContentLoaded', function () {
const provinceSelect = document.getElementById('province');
const districtSelect = document.getElementById('district');
const wardSelect = document.getElementById('ward');

// Lấy context path từ URL hiện tại
const pathArray = window.location.pathname.split('/');
const contextPath = pathArray.length > 1 ? '/' + pathArray[1] : '';

provinceSelect.addEventListener('change', function () {
const provinceId = this.value;
console.log('Selected Province ID:', provinceId); // Debug - kiểm tra có giá trị không

// Reset dropdowns
districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
districtSelect.disabled = true;
wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
wardSelect.disabled = true;

// Kiểm tra provinceId có giá trị và không rỗng
if (provinceId && provinceId.trim() !== '') {
districtSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
districtSelect.disabled = false;

// SỬA: Bỏ /ISP490_SU25_G4 thừa
const url = `/ISP490_SU25_G4/ticket?action=getDistricts&provinceId=${provinceId}`;
console.log('Request URL:', url); // Debug - kiểm tra URL

fetch(url)
.then(response => {
console.log('Response status:', response.status);
console.log('Response URL:', response.url);

if (!response.ok) {
throw new Error(`HTTP ${response.status}: ${response.statusText}`);
}
return response.json();
})
.then(data => {
console.log('Districts data received:', data); // Debug

districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';

if (Array.isArray(data) && data.length > 0) {
data.forEach(function (district) {
const option = new Option(district.name, district.id);
districtSelect.add(option);
});
} else {
districtSelect.innerHTML = '<option value="" disabled>-- Không có dữ liệu --</option>';
}
})
.catch(error => {
console.error('Lỗi khi tải danh sách Quận/Huyện:', error);
districtSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
});
} else {
console.log('Province ID is empty or invalid'); // Debug
}
});

districtSelect.addEventListener('change', function () {
const districtId = this.value;
console.log('Selected District ID:', districtId); // Debug

wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
wardSelect.disabled = true;

if (districtId && districtId.trim() !== '') {
wardSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
wardSelect.disabled = false;

// SỬA: Bỏ /ISP490_SU25_G4 thừa
const url = `/ISP490_SU25_G4/ticket?action=getWards&districtId=${districtId}`;
console.log('Ward request URL:', url); // Debug

fetch(url)
.then(response => {
if (!response.ok) {
throw new Error(`HTTP ${response.status}: ${response.statusText}`);
}
return response.json();
})
.then(data => {
console.log('Wards data received:', data); // Debug

wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';

if (Array.isArray(data) && data.length > 0) {
data.forEach(function (ward) {
const option = new Option(ward.name, ward.id);
wardSelect.add(option);
});
} else {
wardSelect.innerHTML = '<option value="" disabled>-- Không có dữ liệu --</option>';
}
})
.catch(error => {
console.error('Lỗi khi tải danh sách Phường/Xã:', error);
wardSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
});
}
});
});
</script>


        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>