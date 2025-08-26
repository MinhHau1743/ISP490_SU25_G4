<%-- File: /jsp/customerSupport/editTransaction.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Phiếu - ${ticket.requestCode}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createTicket.css?v=<%= System.currentTimeMillis()%>">
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://unpkg.com/feather-icons"></script>

        <style>
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.4);
            }
            .modal-content {
                background-color: #fefefe;
                margin: 10% auto;
                padding: 20px;
                border: 1px solid #888;
                width: 60%;
                border-radius: 8px;
                box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
            }
            .modal-header {
                padding-bottom: 10px;
                border-bottom: 1px solid #e5e5e5;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .modal-body {
                padding-top: 15px;
                max-height: 400px;
                overflow-y: auto;
            }
            .close-modal {
                color: #aaa;
                float: right;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }
            .contract-table {
                width: 100%;
                border-collapse: collapse;
            }
            .contract-table th, .contract-table td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            .product-search-item {
                display: flex;
                justify-content: space-between;
                padding: 10px;
                border-bottom: 1px solid #eee;
                cursor: pointer;
            }
            .product-search-item:hover {
                background-color: #f5f5f5;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <form id="editTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${ticket.id}">
                    <input type="hidden" id="contractId" name="contractId" value="${ticket.contractId}">
                    <input type="hidden" name="reporterId" value="${ticket.reporterId}">
                    <input type="hidden" name="scheduleId" value="${schedule.id}">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=view&id=${ticket.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy bỏ</span></a>
                        <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                    </div>

                    <div class="detail-layout">
                        <%-- Main Column --%>
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Chỉnh sửa thông tin phiếu</h2>
                                <div class="form-grid">
                                    <div class="form-group full-width">
                                        <label for="title">Tiêu đề (*)</label>
                                        <input type="text" id="title" name="title" class="form-control" value="${ticket.title}" required>
                                    </div>
                                    <div class="form-group"><label>Mã Phiếu</label><input type="text" class="form-control" value="${ticket.requestCode}" readonly></div>
                                    <div class="form-group">
                                        <label for="contractCodeDisplay">Hợp đồng (*)</label>
                                        <div class="input-group">
                                            <input type="text" id="contractCodeDisplay" class="form-control" 
                                                   value="${not empty ticket.contractCode ? ticket.contractCode : '-- Vui lòng chọn hợp đồng --'}" 
                                                   readonly required>
                                            <button type="button" id="btnChooseContract" class="btn btn-secondary">Chọn</button>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="enterpriseNameDisplay">Khách hàng (*)</label>
                                        <input type="hidden" id="enterpriseId" name="enterpriseId" value="${ticket.enterpriseId}" required>
                                        <input type="text" id="enterpriseNameDisplay" class="form-control" value="${ticket.enterpriseName}" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <c:forEach var="service" items="${serviceList}"><option value="${service.id}" ${service.id == ticket.serviceId ? 'selected' : ''}>${service.name}</option></c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group full-width">
                                        <div class="address-section">
                                            <h3>Địa chỉ thực hiện công việc</h3>
                                            <div class="address-grid">
                                                <div class="form-group">
                                                    <label for="province">Tỉnh/Thành phố (*)</label>
                                                    <select id="province" name="province" class="form-control" required>
                                                    <c:forEach var="p" items="${provinces}"><option value="${p.id}" ${p.id == schedule.address.provinceId ? 'selected' : ''}>${p.name}</option></c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="district">Quận/Huyện (*)</label>
                                                    <select id="district" name="district" class="form-control" required>
                                                    <c:forEach var="d" items="${districts}"><option value="${d.id}" ${d.id == schedule.address.districtId ? 'selected' : ''}>${d.name}</option></c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="ward">Phường/Xã (*)</label>
                                                    <select id="ward" name="ward" class="form-control" required>
                                                    <c:forEach var="w" items="${wards}"><option value="${w.id}" ${w.id == schedule.address.wardId ? 'selected' : ''}>${w.name}</option></c:forEach>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                                <input type="text" id="streetAddress" name="streetAddress" value="${schedule.address.streetAddress}" class="form-control" required>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group full-width"><label for="description">Mô tả chung (*)</label><textarea id="description" name="description" class="form-control" rows="4" required>${ticket.description}</textarea></div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <table class="device-table">
                                    <thead><tr><th>Tên thiết bị</th><th>Serial Number</th><th>Mô tả sự cố</th><th class="action-col"></th></tr></thead>
                                    <tbody id="device-tbody"></tbody>
                                </table>
                                <div class="device-table-actions"><button type="button" id="addProductBtn" class="btn btn-secondary"><i data-feather="plus"></i>Thêm sản phẩm</button></div>
                            </div>
                        </div>
                        <%-- Sidebar Column --%>
                        <div class="sidebar-column">
                            <div class="detail-card sidebar-form">
                                <h2 class="card-title">Chi tiết Giao việc</h2>
                                <div class="sidebar-form-row">
                                    <label for="status">Trạng thái</label>
                                    <select id="status" name="status" class="form-control">
                                        <c:forEach var="st" items="${statusList}"><option value="${st.statusName}" ${st.statusName == ticket.status ? 'selected' : ''}>${st.statusName}</option></c:forEach>
                                    </select>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="priority">Mức độ ưu tiên</label>
                                    <select id="priority" name="priority" class="form-control">
                                        <option value="critical" ${ticket.priority == 'critical' ? 'selected' : ''}>Khẩn cấp</option>
                                        <option value="high" ${ticket.priority == 'high' ? 'selected' : ''}>Cao</option>
                                        <option value="medium" ${ticket.priority == 'medium' ? 'selected' : ''}>Thông thường</option>
                                        <option value="low" ${ticket.priority == 'low' ? 'selected' : ''}>Thấp</option>
                                    </select>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="employeesId">Nhân viên phụ trách</label>
                                    <select id="employeesId" name="employeesId" class="form-control" required>
                                        <c:forEach var="employee" items="${employeeList}"><option value="${employee.id}" ${employee.id == ticket.assignedToId ? 'selected' : ''}>${employee.lastName} ${employee.middleName} ${employee.firstName}</option></c:forEach>
                                    </select>
                                </div>
                                <div class="sidebar-form-row"><label>Ngày tạo</label><input type="text" class="form-control" value="<fmt:formatDate value='${ticket.createdAt}' pattern='dd/MM/yyyy HH:mm' />" readonly></div>
                                <div class="sidebar-form-row"><label>Người tạo</label><input type="text" value="${ticket.reporterName}" class="form-control" readonly ></div>
                                
                                <%-- CÁC TRƯỜNG NGÀY GIỜ CẦN VALIDATE --%>
                                <div class="sidebar-form-row row-2col">
                                    <label>Khoảng ngày</label>
                                    <div class="control"><input type="date" id="scheduled_date" name="scheduled_date" value="${schedule.scheduledDate}" class="form-control"><div class="field-hint">Bắt đầu</div></div>
                                    <div class="control"><input type="date" id="end_date" name="end_date" value="${schedule.endDate}" class="form-control"><div class="field-hint">Kết thúc</div></div>
                                </div>
                                <div class="sidebar-form-row row-2col">
                                    <label>Khung giờ</label>
                                    <div class="control"><input type="time" id="start_time" name="start_time" value="${schedule.startTime}" class="form-control"><div class="field-hint">Từ</div></div>
                                    <div class="control"><input type="time" id="end_time" name="end_time" value="${schedule.endTime}" class="form-control"><div class="field-hint">Đến</div></div>
                                </div>
                                
                                <div class="sidebar-form-row">
                                    <label>Màu sắc</label>
                                    <div class="color-palette">
                                        <span class="color-swatch" data-color="#007bff" style="background-color: #007bff;"></span>
                                        <span class="color-swatch" data-color="#dc3545" style="background-color: #dc3545;"></span>
                                        <span class="color-swatch" data-color="#28a745" style="background-color: #28a745;"></span>
                                        <span class="color-swatch" data-color="#ffc107" style="background-color: #ffc107;"></span>
                                        <span class="color-swatch" data-color="#fd7e14" style="background-color: #fd7e14;"></span>
                                        <span class="color-swatch" data-color="#17a2b8" style="background-color: #17a2b8;"></span>
                                        <span class="color-swatch" data-color="#6610f2" style="background-color: #6610f2;"></span>
                                        <span class="color-swatch" data-color="#343a40" style="background-color: #343a40;"></span>
                                    </div>
                                    <input type="hidden" id="color" name="color" value="${not empty schedule.color ? schedule.color : '#007bff'}">
                                </div>
                                <div class="sidebar-form-row"><label>Chi phí dự kiến</label><div class="radio-group"><label><input type="radio" name="isBillable" value="true" ${ticket.isBillable ? 'checked' : ''}> Có</label><label><input type="radio" name="isBillable" value="false" ${!ticket.isBillable ? 'checked' : ''}> Không</label></div></div>
                                <div id="amount-group" class="sidebar-form-row" style="display: ${ticket.isBillable ? 'flex' : 'none'};"><label for="amount">Số tiền (VND)</label><input type="number" id="amount" name="amount" class="form-control" value="${ticket.estimatedCost}"></div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <div id="contractModal" class="modal">
            <div class="modal-content">
                <div class="modal-header"><h2>Chọn Hợp đồng</h2><span class="close-modal">&times;</span></div>
                <div class="modal-body">
                    <table id="contract-table" class="contract-table">
                        <thead><tr><th>Mã Hợp đồng</th><th>Tên Khách hàng</th><th></th></tr></thead>
                        <tbody>
                            <c:forEach var="contract" items="${contractList}"><tr data-contract-id="${contract.id}" data-contract-code="${contract.contractCode}"><td>${contract.contractCode}</td><td>${contract.enterprise.name}</td><td><button type="button" class="btn btn-sm btn-primary btn-select-contract">Chọn</button></td></tr></c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div id="alertModal" class="modal">
            <div class="modal-content" style="width: 350px;">
                <div class="modal-header"><h2 id="alertModalTitle">Thông báo</h2><span class="close-alert-modal">&times;</span></div>
                <div class="modal-body" style="text-align: center;"><p id="alertModalMessage" style="font-size: 16px;"></p><div style="margin-top: 20px;"><button type="button" class="btn btn-primary close-alert-modal">OK</button></div></div>
            </div>
        </div>
        <div id="productSearchModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Chọn sản phẩm</h3>
                    <button type="button" class="close-modal" id="closeProductModalBtn"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body">
                    <div class="search-bar-container"><input type="text" id="productSearchInput" class="form-control" placeholder="Tìm kiếm sản phẩm..."></div>
                    <div id="productList" class="product-list-container"></div>
                </div>
            </div>
        </div>

        <script>
            window.contextPath = '<%= request.getContextPath()%>';
            window.EXISTING_DEVICES = JSON.parse('${existingDevicesJson}');
            window.CONTRACT_PRODUCTS = JSON.parse('${contractProductsJson}');
            window.PRESELECTED_ADDRESS = {
                provinceId: '${schedule.address.provinceId}',
                districtId: '${schedule.address.districtId}',
                wardId: '${schedule.address.wardId}'
            };
            
            // ============== SCRIPT VALIDATION NGÀY GIỜ ==============
            document.addEventListener('DOMContentLoaded', function () {
                // Lấy các element input theo ID
                const scheduledDate = document.getElementById('scheduled_date');
                const endDate = document.getElementById('end_date');
                const startTime = document.getElementById('start_time');
                const endTime = document.getElementById('end_time');

                function updateDateTimeConstraints() {
                    // 1. Ngày kết thúc không được nhỏ hơn ngày bắt đầu
                    if (scheduledDate.value) {
                        endDate.min = scheduledDate.value;
                    } else {
                        endDate.removeAttribute('min');
                    }
                    
                    // 2. Nếu là cùng một ngày, giờ kết thúc không được nhỏ hơn giờ bắt đầu
                    if (startTime.value && scheduledDate.value && endDate.value && scheduledDate.value === endDate.value) {
                        endTime.min = startTime.value;
                    } else {
                        // Nếu khác ngày hoặc không có đủ thông tin, xóa bỏ ràng buộc về giờ
                        endTime.removeAttribute('min');
                    }
                }
                
                // Gắn sự kiện 'change' để gọi hàm cập nhật mỗi khi người dùng thay đổi giá trị
                if(scheduledDate) scheduledDate.addEventListener('change', updateDateTimeConstraints);
                if(endDate) endDate.addEventListener('change', updateDateTimeConstraints);
                if(startTime) startTime.addEventListener('change', updateDateTimeConstraints);
                
                // Gọi hàm một lần khi trang được tải xong để thiết lập quy tắc ban đầu
                updateDateTimeConstraints();
            });
            // =======================================================
        </script>

        <script src="${pageContext.request.contextPath}/js/editTransaction.js?v=<%= System.currentTimeMillis()%>"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>