<%-- 
    Document   : editTransaction
    Created on : Jun 14, 2025, 1:33:12 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="listTransaction" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa giao dịch - ${transaction.transactionCode}</title>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/editTransaction.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <form class="page-content" action="updateTransaction" method="post">
                    <input type="hidden" name="transactionId" value="${transaction.id}"/>

                    <div class="detail-header">
                        <a href="viewTransaction.jsp" class="back-link"><i data-feather="arrow-left"></i><span>Hủy bỏ</span></a>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <!-- CỘT CHÍNH (BÊN TRÁI) -->
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Chỉnh sửa thông tin chung</h2>
                                <div class="form-grid">
                                    <div class="form-group"><label for="transactionCode">Mã giao dịch</label><input type="text" id="transactionCode" name="transactionCode" class="form-control" value="${transaction.transactionCode}" readonly></div>
                                    <div class="form-group"><label for="contractCode">Mã hợp đồng</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="${transaction.contractCode}"></div>
                                    <div class="form-group"><label for="customer">Khách hàng</label><input type="text" id="customer" name="customer" class="form-control" value="${transaction.customer}"></div>
                                    <div class="form-group"><label for="type">Loại giao dịch</label><select id="type" name="type" class="form-control"><option value="Hỗ trợ sự cố" ${transaction.type == 'Hỗ trợ sự cố' ? 'selected' : ''}>Hỗ trợ sự cố</option><option value="Bảo trì định kỳ" ${transaction.type == 'Bảo trì định kỳ' ? 'selected' : ''}>Bảo trì định kỳ</option></select></div>
                                    <div class="form-group full-width"><label for="description">Mô tả chi tiết</label><textarea id="description" name="description" class="form-control">${transaction.description}</textarea></div>
                                </div>
                            </div>
                            <div class="detail-card"><h2 class="card-title">Các thiết bị liên quan</h2><%-- Nội dung bảng thiết bị ở đây --%></div>
                        </div>

                        <!-- CỘT PHỤ (BÊN PHẢI) -->
                        <div class="sidebar-column">
                            <div class="detail-card sidebar-form">
                                <h2 class="card-title">Chi tiết Trạng thái</h2>

                                <div class="form-group">
                                    <div class="sidebar-form-row">
                                        <label for="status">Trạng thái</label>
                                        <select id="status" name="status" class="form-control">
                                            <option value="completed" ${transaction.status == 'completed' ? 'selected' : ''}>Đã hoàn thành</option>
                                            <option value="processing" ${transaction.status == 'processing' ? 'selected' : ''}>Đang xử lý</option>
                                            <option value="pending" ${transaction.status == 'pending' ? 'selected' : ''}>Chờ vật tư</option>
                                            <option value="cancelled" ${transaction.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="sidebar-form-row">
                                        <label for="priority">Mức độ ưu tiên</label>
                                        <select id="priority" name="priority" class="form-control">
                                            <option ${transaction.priority == 'Khẩn cấp' ? 'selected' : ''}>Khẩn cấp</option>
                                            <option ${transaction.priority == 'Cao' ? 'selected' : ''}>Cao</option>
                                            <option ${transaction.priority == 'Thông thường' ? 'selected' : ''}>Thông thường</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="sidebar-form-row">
                                        <label for="employeeId">Nhân viên phụ trách</label>
                                        <select id="employeeId" name="employeeId" class="form-control">
                                            <c:forEach var="emp" items="${employeeList}"><option value="${emp.id}" ${transaction.employeeId == emp.id ? 'selected' : ''}>${emp.name}</option></c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="sidebar-form-row">
                                            <label for="createdDate">Ngày tạo</label>
                                            <input type="date" id="createdDate" name="createdDate" class="form-control" value="${transaction.createdDate}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="sidebar-form-row">
                                        <label>Chi phí</label>
                                        <div class="radio-group">
                                            <label><input type="radio" id="billableYes" name="isBillable" value="true" ${transaction.isBillable ? 'checked' : ''}> Có</label>
                                            <label><input type="radio" id="billableNo" name="isBillable" value="false" ${!transaction.isBillable ? 'checked' : ''}> Không</label>
                                        </div>
                                    </div>

                                    <%-- THAY ĐỔI: Thêm ô nhập số tiền, mặc định ẩn --%>
                                    <div id="amount-group" class="form-group hidden-field">
                                        <label for="amount">Số tiền (VND)</label>
                                        <input type="number" id="amount" name="amount" class="form-control" value="${transaction.amount}">
                                    </div>
                                </div>
                            </div>
                        </div>
                </form>
            </main>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script src="../../js/mainMenu.js"></script>
        <script src="../../js/editTransaction.js"></script>
    </body>
</html>
