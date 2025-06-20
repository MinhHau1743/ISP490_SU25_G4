<%-- 
    Document   : editContractDetail
    Created on : Jun 20, 2025, 8:56:40 AM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="listContract" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Hợp đồng - ${contract.contractCode}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/editContractDetail.css">


    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">

                <form class="page-content" action="contract" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${contract.id}">
                    <div class="detail-header">
                        <a href="viewContractDetail.jsp" class="back-link">
                            <i data-feather="arrow-left"></i><span>Hủy</span>
                        </a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary">
                                <i data-feather="save"></i>Lưu thay đổi
                            </button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin Hợp đồng</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <%-- Tên hợp đồng giờ là readonly --%>
                                        <div class="form-group"><label for="contractName">Tên hợp đồng</label><input type="text" id="contractName" name="name" class="form-control" value="${contract.name}" readonly></div>
                                        <div class="form-group"><label for="contractCode">Mã hợp đồng</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="${contract.contractCode}" readonly></div>
                                        <div class="form-group">
                                            <label for="customerId">Khách hàng</label>
                                            <select id="customerId" class="form-control" disabled>
                                                <option selected>${contract.customer.name}</option>
                                            </select>
                                            <%-- Thêm input ẩn để gửi customerId lên server --%>
                                            <input type="hidden" name="customerId" value="${contract.customer.id}" />
                                        </div>
                                        <div class="form-group">
                                            <label for="contractType">Loại hợp đồng</label>
                                            <select id="contractType" name="type" class="form-control">
                                                <option value="maintenance" ${contract.type == 'maintenance' ? 'selected' : ''}>Bảo trì</option>
                                                <option value="supply" ${contract.type == 'supply' ? 'selected' : ''}>Cung cấp</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group" style="margin-top: 16px;"><label for="description">Mô tả / Điều khoản chính</label><textarea id="description" name="description" class="form-control" rows="5">${contract.description}</textarea></div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Hàng hóa / Dịch vụ đã chốt (Chỉ xem)</h3>
                                <div class="card-body">
                                    <table class="item-list-table">
                                        <%-- Phần này giữ nguyên vì là read-only --%>
                                        <thead>
                                            <tr>
                                                <th style="width: 50%;">Sản phẩm</th>
                                                <th style="width: 15%; text-align: center;">Số lượng</th>
                                                <th style="width: 20%; text-align: right;">Đơn giá</th>
                                                <th style="width: 20%; text-align: right;">Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${contract.items}">
                                                <tr>
                                                    <td class="product-name-cell">${item.name}</td>
                                                    <td style="text-align: center;">${item.quantity}</td>
                                                    <td class="money-cell"><fmt:formatNumber value="${item.unitPrice}" type="currency" currencyCode="VND"/></td>
                                                    <td class="money-cell"><fmt:formatNumber value="${item.totalPrice}" type="currency" currencyCode="VND"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                        <tfoot>
                                            <tr><td colspan="3">Tổng phụ</td><td class="money-cell"><fmt:formatNumber value="${contract.subtotal}" type="currency" currencyCode="VND"/></td></tr>
                                            <tr><td colspan="3">VAT (10%)</td><td class="money-cell"><fmt:formatNumber value="${contract.vatAmount}" type="currency" currencyCode="VND"/></td></tr>
                                            <tr class="grand-total"><td colspan="3">Tổng cộng</td><td class="money-cell"><fmt:formatNumber value="${contract.grandTotal}" type="currency" currencyCode="VND"/></td></tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h3 class="card-title">Giá trị & Thời hạn</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="contractValue">Giá trị Hợp đồng (VND)</label><input type="text" id="contractValue" name="grandTotal" class="form-control" value="<fmt:formatNumber value='${contract.grandTotal}' pattern='###,###,###'/>" readonly></div>

                                    <div class="date-grid">
                                        <div class="form-group date-sign">
                                            <label for="signDate">Ngày ký (*)</label>
                                            <input type="date" id="signDate" name="signDate" class="form-control" value="<fmt:formatDate value='${contract.signDate}' pattern='yyyy-MM-dd' />" required>
                                        </div>
                                        <div class="form-group date-effective">
                                            <label for="effectiveDate">Ngày hiệu lực</label>
                                            <input type="date" id="effectiveDate" name="effectiveDate" class="form-control" value="<fmt:formatDate value='${contract.effectiveDate}' pattern='yyyy-MM-dd' />">
                                        </div>
                                        <div class="form-group date-expiry">
                                            <label for="expiryDate">Ngày hết hạn</label>
                                            <input type="date" id="expiryDate" name="expiryDate" class="form-control" value="<fmt:formatDate value='${contract.expirationDate}' pattern='yyyy-MM-dd' />">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h3 class="card-title">Quản lý</h3>
                                <div class="card-body">
                                    <div class="form-group">
                                        <label for="status">Trạng thái</label>
                                        <%-- Thêm class 'status-select' để JS có thể tìm thấy --%>
                                        <select id="status" name="status" class="form-control status-select">
                                            <option value="active" ${contract.status == 'active' ? 'selected' : ''}>Còn hiệu lực</option>
                                            <option value="expired" ${contract.status == 'expired' ? 'selected' : ''}>Đã hết hạn</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="employeeId">Nhân viên phụ trách</label>
                                        <select id="employeeId" name="employeeId" class="form-control">
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}" ${contract.assignee.id == employee.id ? 'selected' : ''}>${employee.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <script src="../../js/editContractDetail.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
