<%-- 
    Document   : createTicket
    Created on : Jun 20, 2025, 10:56:01 AM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="createTicket" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Phiếu Giao Việc Mới</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <%-- **SỬA LỖI Ở ĐÂY: Thêm lại script cho Feather Icons** --%>
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/createTicket.css"> 



    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <form id="createTicketForm" class="page-content" action="ticket" method="post">
                    <input type="hidden" name="action" value="create">

                    <div class="detail-header">
                        <a href="transaction" class="back-link"><i data-feather="arrow-left"></i><span>Hủy bỏ</span></a>
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
                                    <div class="form-group"><label for="contractCode">Mã hợp đồng (nếu có)</label><input type="text" id="contractCode" name="contractCode" class="form-control" placeholder="VD: HD-2024-150"></div>
                                    <div class="form-group"><label for="type">Loại phiếu</label><select id="type" name="type" class="form-control"><option value="Hỗ trợ sự cố" selected>Hỗ trợ sự cố</option><option value="Bảo trì định kỳ">Bảo trì định kỳ</option><option value="Lắp đặt mới">Lắp đặt mới</option><option value="Khảo sát">Khảo sát</option></select></div>
                                    <div class="form-group full-width"><label for="description">Mô tả chi tiết sự cố/yêu cầu (*)</label><textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea></div>
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
                                        </tr>
                                    </thead>
                                    <tbody id="device-list">
                                        <tr>
                                            <td><input type="text" name="deviceName_1" class="form-control-table" placeholder="VD: Điều hòa Daikin"></td>
                                            <td><input type="text" name="deviceSerial_1" class="form-control-table" placeholder="VD: DKN-12345"></td>
                                            <td><textarea name="deviceNote_1" class="form-control-table" rows="1" placeholder="VD: Không lạnh, chảy nước"></textarea></td>
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
                                <div class="card-body">
                                    <div class="sidebar-form-row">
                                        <label>Trạng thái</label>
                                        <input type="text" class="form-control" value="Mới" readonly>
                                    </div>
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
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}">${employee.name}</option>
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
                                            <label><input type="radio" id="billableYes" name="isBillable" value="true"> Có</label>
                                            <label><input type="radio" id="billableNo" name="isBillable" value="false" checked> Không (Bảo hành)</label>
                                        </div>
                                    </div>
                                    <div id="amount-group" class="form-group" style="display: none;">
                                        <label for="amount">Số tiền dự kiến (VND)</label>
                                        <input type="number" id="amount" name="amount" class="form-control" value="0">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <div id="successModal" class="modal-overlay">
            <div class="modal-content">
                <i data-feather="check-circle" class="success-icon"></i>
                <p class="success-message">Gửi yêu cầu thành công cho bộ phận kỹ thuật</p>
                <div class="progress-bar-container">
                    <div class="progress-bar-fill"></div>
                </div>
            </div>
        </div>

        <script src="../../js/createTicket.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>

