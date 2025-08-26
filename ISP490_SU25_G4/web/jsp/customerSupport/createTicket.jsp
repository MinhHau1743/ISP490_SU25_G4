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
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- CSS cho Modal và Validation --%>
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
            .close-modal:hover, .close-modal:focus {
                color: black;
                text-decoration: none;
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
            .contract-table tr:nth-child(even){
                background-color: #f2f2f2;
            }
            .contract-table tr:hover {
                background-color: #e9ecef;
            }
            .is-invalid {
                border-color: #dc3545 !important;
            }
            .error-message {
                color: #dc3545;
                font-size: 0.875em;
                margin-top: 0.25rem;
                height: 1em;
            }
            .input-group {
                display: flex;
            }
            .input-group .form-control {
                flex-grow: 1;
                border-top-right-radius: 0;
                border-bottom-right-radius: 0;
            }
            .input-group .btn {
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <%-- ĐÃ XÓA 'novalidate' ĐỂ BẬT VALIDATION CỦA TRÌNH DUYỆT --%>
                <form id="createTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" id="contractId" name="contractId">
                    <input type="hidden" name="reporterId" value="${user.id}">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại</span></a>
                        <button type="submit" class="btn btn-primary"><i data-feather="plus-circle"></i>Tạo Phiếu</button>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin Phiếu Giao Việc</h2>
                                <div class="form-grid">
                                    <div class="form-group full-width">
                                        <label for="title">Tiêu đề (*)</label>
                                        <input type="text" id="title" name="title" class="form-control" required>
                                        <div class="error-message" id="title-error"></div>
                                    </div>

                                    <div class="form-group">
                                        <label>Mã Phiếu</label>
                                        <input type="text" class="form-control" value="(Tự động tạo)" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label for="contractSelection">Hợp đồng (*)</label>
                                        <div class="input-group">
                                            <input type="text" id="contractCodeDisplay" class="form-control" placeholder="-- Chọn hợp đồng từ danh sách --" readonly required>
                                            <button type="button" id="btnChooseContract" class="btn btn-secondary">Chọn</button>
                                        </div>
                                        <div class="error-message" id="contractId-error"></div>
                                    </div>

                                    <div class="form-group">
                                        <label for="enterpriseNameDisplay">Khách hàng (*)</label>
                                        <input type="hidden" id="enterpriseId" name="enterpriseId" required>
                                        <input type="text" id="enterpriseNameDisplay" class="form-control" placeholder="-- Tự động điền theo hợp đồng --" readonly>
                                        <div class="error-message" id="enterpriseId-error"></div>
                                    </div>

                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn loại phiếu --</option>
                                            <c:forEach var="service" items="${serviceList}">
                                                <option value="${service.id}">${service.name}</option>
                                            </c:forEach>
                                        </select>
                                        <div class="error-message" id="serviceId-error"></div>
                                    </div>

                                    <div class="form-group full-width">
                                        <div class="address-section">
                                            <h3>Địa chỉ thực hiện công việc</h3>
                                            <div class="address-grid">
                                                <div class="form-group">
                                                    <label for="province">Tỉnh/Thành phố (*)</label>
                                                    <select id="province" name="province" class="form-control" required>
                                                        <option value="" disabled selected>-- Chọn Tỉnh/Thành --</option>
                                                        <c:forEach var="p" items="${provinces}">
                                                            <option value="${p.id}">${p.name}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <div class="error-message" id="province-error"></div>
                                                </div>
                                                <div class="form-group">
                                                    <label for="district">Quận/Huyện (*)</label>
                                                    <select id="district" name="district" class="form-control" required disabled>
                                                        <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                                    </select>
                                                    <div class="error-message" id="district-error"></div>
                                                </div>
                                                <div class="form-group">
                                                    <label for="ward">Phường/Xã (*)</label>
                                                    <select id="ward" name="ward" class="form-control" required disabled>
                                                        <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                                    </select>
                                                    <div class="error-message" id="ward-error"></div>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                                <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                                <div class="error-message" id="streetAddress-error"></div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group full-width">
                                        <label for="description">Mô tả chung (*)</label>
                                        <textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea>
                                        <div class="error-message" id="description-error"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <table class="device-table">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị (Sản phẩm từ hợp đồng)</th>
                                            <th>Serial Number</th>
                                            <th>Mô tả sự cố</th>
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
                                        <option value="medium" selected>Thông thường</option>
                                        <option value="high">Cao</option>
                                        <option value="critical">Khẩn cấp</option>
                                        <option value="low">Thấp</option>
                                    </select>
                                </div>

                                <div class="sidebar-form-row">
                                    <label for="status">Trạng thái</label>
                                    <select id="status" name="status" class="form-control">
                                        <%-- Lặp qua danh sách trạng thái từ Controller --%>
                                        <c:forEach var="st" items="${statusList}">
                                            <%-- Dùng status_name cho cả value và phần hiển thị --%>
                                            <%-- Chọn "Mới tạo" làm giá trị mặc định --%>
                                            <option value="${st.statusName}" ${st.statusName == 'Mới tạo' ? 'selected' : ''}>
                                                ${st.statusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="sidebar-form-row">
                                    <label for="employeesId">Nhân viên phụ trách (*)</label>
                                    <select id="employeesId" name="employeesId" class="form-control" required>
                                        <option value="" disabled selected>-- Chọn nhân viên kỹ thuật --</option>
                                        <c:forEach var="employee" items="${employeeList}">
                                            <option value="${employee.id}">${employee.lastName} ${employee.middleName} ${employee.firstName}</option>
                                        </c:forEach>
                                    </select>
                                    <div class="error-message" id="employeesId-error"></div>
                                </div>

                                <div class="sidebar-form-row">
                                    <label>Ngày tạo</label>
                                    <input type="date" id="createdDate" name="createdDate" class="form-control" readonly>
                                </div>

                                <div class="sidebar-form-row">
                                    <label>Người tạo</label>
                                    <input type="text" value="${user.lastName} ${user.middleName} ${user.firstName}" class="form-control" readonly>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khoảng ngày (*)</label>
                                    <div class="control">
                                        <input type="date" id="scheduled_date" name="scheduled_date" class="form-control" required>
                                        <div class="field-hint">Bắt đầu</div>
                                        <div class="error-message" id="scheduled_date-error"></div>
                                    </div>
                                    <div class="control">
                                        <input type="date" id="end_date" name="end_date" class="form-control">
                                        <div class="field-hint">Kết thúc</div>
                                        <div class="error-message" id="end_date-error"></div>
                                    </div>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khung giờ</label>
                                    <div class="control">
                                        <input type="time" id="start_time" name="start_time" class="form-control">
                                        <div class="field-hint">Từ</div>
                                    </div>
                                    <div class="control">
                                        <input type="time" id="end_time" name="end_time" class="form-control">
                                        <div class="field-hint">Đến</div>
                                    </div>
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
                                    <input type="hidden" id="color" name="color" value="#007bff">
                                </div>

                                <div class="sidebar-form-row">
                                    <label>Chi phí dự kiến</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="isBillable" value="true"> Có</label>
                                        <label><input type="radio" name="isBillable" value="false" checked> Không</label>
                                    </div>
                                </div>

                                <div id="amount-group" class="sidebar-form-row" style="display:none;">
                                    <label for="amount">Số tiền (VND)</label>
                                    <input type="number" id="amount" name="amount" class="form-control" min="0" value="0">
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <div id="contractModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Chọn Hợp đồng</h2>
                    <span class="close-modal">&times;</span>
                </div>
                <div class="modal-body">
                    <table id="contract-table" class="contract-table">
                        <thead>
                            <tr>
                                <th>Mã Hợp đồng</th>
                                <th>Tên Khách hàng</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="contract" items="${contractList}">
                                <tr data-contract-id="${contract.id}" data-contract-code="${contract.contractCode}">
                                    <td>${contract.contractCode}</td>
                                    <td>${contract.enterprise.name}</td>
                                    <td><button type="button" class="btn btn-sm btn-primary btn-select-contract">Chọn</button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <%-- ================= THÊM MODAL THÔNG BÁO MỚI TẠI ĐÂY ================= --%>
        <div id="alertModal" class="modal">
            <div class="modal-content" style="width: 350px;"> <%-- Thu nhỏ chiều rộng cho giống hộp thoại --%>
                <div class="modal-header">
                    <h2 id="alertModalTitle">Thông báo</h2>
                    <span class="close-alert-modal">&times;</span>
                </div>
                <div class="modal-body" style="text-align: center;">
                    <p id="alertModalMessage" style="font-size: 16px;"></p> <%-- Nội dung sẽ được JS điền vào --%>
                    <div style="margin-top: 20px;">
                        <button type="button" class="btn btn-primary close-alert-modal">OK</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            window.contextPath = '<%= request.getContextPath()%>';
        </script>
        <script src="${pageContext.request.contextPath}/js/createTicket.js" defer></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js" defer></script>
    </body>
</html>