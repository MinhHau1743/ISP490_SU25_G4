<%-- File: /view/customerSupport/createTicket.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Phiếu Giao Việc Mới</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- CẬP NHẬT: Sửa tất cả đường dẫn CSS để dùng contextPath --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createTicket.css"> 
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            
            <main class="main-content">
                <form id="createTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="create">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Hủy bỏ</span></a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary"><i data-feather="plus-circle"></i>Tạo Phiếu</button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin Phiếu Giao Việc</h2>
                                <div class="form-grid">
                                    <div class="form-group"><label>Mã Phiếu</label><input type="text" class="form-control" value="(Tự động tạo)" readonly></div>
                                    
                                    <div class="form-group">
                                        <label for="customerId">Khách hàng (*)</label>
                                        <select id="customerId" name="customerId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn khách hàng --</option>
                                            <c:forEach var="customer" items="${customerList}">
                                                <option value="${customer.id}">${customer.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label for="contractCode">Mã hợp đồng (nếu có)</label>
                                        <input type="text" id="contractCode" name="contractCode" class="form-control" placeholder="VD: HD-2024-150">
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
                                    
                                    <div class="form-group full-width">
                                        <label for="description">Mô tả chi tiết sự cố/yêu cầu (*)</label>
                                        <textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <table class="device-table-edit">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị</th>
                                            <th>Serial Number</th>
                                            <th style="width: 40%;">Mô tả sự cố của thiết bị</th>
                                            <th style="width: 50px;"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="device-list">
                                        <tr>
                                            <td><input type="text" name="deviceName_1" class="form-control-table" placeholder="VD: Điều hòa Daikin"></td>
                                            <td><input type="text" name="deviceSerial_1" class="form-control-table" placeholder="VD: DKN-12345"></td>
                                            <td><textarea name="deviceNote_1" class="form-control-table" rows="1" placeholder="VD: Không lạnh, chảy nước"></textarea></td>
                                            <td></td>
                                        </tr>
                                    </tbody>
                                </table>
                                <div class="device-table-actions">
                                    <button type="button" id="addDeviceBtn" class="btn btn-secondary"><i data-feather="plus"></i>Thêm thiết bị</button>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card sidebar-form">
                                <h2 class="card-title">Chi tiết Giao việc</h2>
                                <div class="sidebar-form-row">
                                    <label for="priority">Mức độ ưu tiên</label>
                                    <select id="priority" name="priority" class="form-control">
                                        <option>Thông thường</option>
                                        <option selected>Cao</option>
                                        <option>Khẩn cấp</option>
                                    </select>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="employeeId">Gán cho nhân viên (*)</label>
                                    <select id="employeeId" name="employeeId" class="form-control" required>
                                        <option value="" disabled selected>-- Chọn kỹ thuật viên --</option>
                                        <%-- SỬA LỖI Ở ĐÂY: Hiển thị trực tiếp các thành phần tên --%>
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
                                    <label>Chi phí dự kiến</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="isBillable" value="true"> Có</label>
                                        <label><input type="radio" name="isBillable" value="false" checked> Không</label>
                                    </div>
                                </div>
                                <div id="amount-group" class="sidebar-form-row" style="display: none;">
                                    <label for="amount">Số tiền dự kiến (VND)</label>
                                    <input type="number" id="amount" name="amount" class="form-control" value="0">
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

                // Tự động điền ngày hiện tại
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('createdDate').value = today;

                // Script để thêm dòng thiết bị mới
                const addDeviceBtn = document.getElementById('addDeviceBtn');
                const deviceList = document.getElementById('device-list');
                let deviceIndex = 2;

                addDeviceBtn.addEventListener('click', function () {
                    const newRow = document.createElement('tr');
                    newRow.innerHTML = `
                        <td><input type="text" name="deviceName_${deviceIndex}" class="form-control-table" placeholder="Tên thiết bị"></td>
                        <td><input type="text" name="deviceSerial_${deviceIndex}" class="form-control-table" placeholder="Serial number"></td>
                        <td><textarea name="deviceNote_${deviceIndex}" class="form-control-table" rows="1" placeholder="Mô tả sự cố"></textarea></td>
                        <td><button type="button" class="btn-remove-device" title="Xóa dòng"><i data-feather="x-circle"></i></button></td>
                    `;
                    deviceList.appendChild(newRow);
                    feather.replace(); // Phải gọi lại để icon feather được render
                    deviceIndex++;
                });
                
                // Script để xóa một dòng thiết bị
                deviceList.addEventListener('click', function(e) {
                    const removeBtn = e.target.closest('.btn-remove-device');
                    if(removeBtn) {
                        removeBtn.closest('tr').remove();
                    }
                });

                // Script để ẩn/hiện ô nhập số tiền
                const billableRadios = document.querySelectorAll('input[name="isBillable"]');
                const amountGroup = document.getElementById('amount-group');
                billableRadios.forEach(radio => {
                    radio.addEventListener('change', function() {
                        amountGroup.style.display = (this.value === 'true') ? 'block' : 'none';
                    });
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/createTicket.js"></script>
    </body>
</html>
