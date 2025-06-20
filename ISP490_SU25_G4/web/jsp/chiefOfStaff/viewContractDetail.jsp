<%-- 
    Document   : viewContractDetail
    Created on : Jun 20, 2025, 8:56:26 AM
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
        <title>Chi tiết Hợp đồng - ${contract.contractCode}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">

        <link rel="stylesheet" href="../../css/viewContractDetail.css">


    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">

                <c:if test="${not empty contract}">
                    <div class="page-content">
                        <div class="detail-header">
                            <a href="listContract.jsp" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>

                            <div class="action-buttons" style="display: flex; gap: 8px;">
                                <a href="#" class="btn btn-primary"><i data-feather="printer"></i>In hợp đồng</a>
                                <a href="editContractDetail.jsp" class="btn btn-primary"><i data-feather="edit-2"></i>Sửa</a>
                                <a href="#" class="btn btn-primary delete-trigger-btn" data-id="${contract.id}" data-name="${contract.contractCode}"><i data-feather="trash-2"></i>Xóa</a>
                            </div>
                        </div>

                        <div class="detail-layout">
                            <div class="main-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin chung</h3>
                                    <div class="card-body">
                                        <div class="info-grid">
                                            <div class="info-item"><span class="label">Mã hợp đồng</span><span class="value" style="font-weight: 700; color: var(--primary-color, #0d9488);">${contract.contractCode}</span></div>
                                            <div class="info-item"><span class="label">Khách hàng</span><span class="value"><a href="../sales/viewCustomerDetail.jsp">${contract.customer.name}</a></span></div>
                                            <div class="info-item full-width"><span class="label">Tên hợp đồng</span><span class="value">${contract.name}</span></div>
                                            <div class="info-item full-width"><span class="label">Mô tả / Điều khoản</span><span class="value">${contract.description}</span></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-card">
                                    <h3 class="card-title">Chi tiết Hàng hóa / Dịch vụ</h3>
                                    <div class="card-body" style="padding: 0;">
                                        <table class="item-list-table">
                                            <thead>
                                                <tr>
                                                    <th>Sản phẩm / Dịch vụ</th>
                                                    <th>Số lượng</th>
                                                    <th>Đơn giá</th>
                                                    <th>Thành tiền</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="item" items="${contract.items}">
                                                    <tr>
                                                        <td class="product-name">${item.name}</td>
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
                                    <h3 class="card-title">Thời hạn & Giá trị</h3>
                                    <div class="card-body">
                                        <div class="info-item"><span class="label">Trạng thái</span>
                                            <span class="value">
                                                <c:choose>
                                                    <c:when test="${contract.status == 'active'}"><span class="status-pill status-active">Còn hiệu lực</span></c:when>
                                                    <c:when test="${contract.status == 'expiring'}"><span class="status-pill status-expiring">Sắp hết hạn</span></c:when>
                                                    <c:when test="${contract.status == 'expired'}"><span class="status-pill status-expired">Đã hết hạn</span></c:when>
                                                    <c:otherwise><span class="status-pill">${contract.status}</span></c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                        <div class="info-item"><span class="label">Ngày ký</span><span class="value"><fmt:formatDate value="${contract.signDate}" pattern="dd/MM/yyyy"/></span></div>
                                        <div class="info-item"><span class="label">Ngày hết hạn</span><span class="value"><fmt:formatDate value="${contract.expirationDate}" pattern="dd/MM/yyyy"/></span></div>
                                        <div class="info-item"><span class="label">Giá trị hợp đồng (Trước VAT)</span><span class="value" style="font-size: 18px; font-weight: 700;"><fmt:formatNumber value="${contract.subtotal}" type="currency" currencyCode="VND"/></span></div>
                                        <div class="info-item"><span class="label">Nhân viên phụ trách</span><span class="value">${contract.assignee.name}</span></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${empty contract}">
                    <div class="page-content" style="text-align: center; padding-top: 50px;">
                        <h2>Không tìm thấy hợp đồng</h2>
                        <p>Hợp đồng bạn yêu cầu không tồn tại hoặc đã bị xóa.</p>
                        <a href="listContract.jsp" class="btn btn-primary">Quay lại danh sách</a>
                    </div>
                </c:if>

            </main>
        </div>

        <%-- Modal xác nhận xóa --%>
        <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
            <div class="modal-content" style="background:white; border-radius:12px; max-width:400px;">
                <div class="modal-header" style="padding:16px 24px; border-bottom:1px solid #e5e7eb; display:flex; justify-content:space-between;">
                    <h3 class="modal-title" style="font-size:18px; font-weight:600;">Xác nhận xóa</h3>
                    <button class="close-modal-btn" style="background:none; border:none; cursor:pointer;"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body" style="padding:24px; text-align:center;">
                    <p id="deleteMessage">Bạn có chắc chắn muốn xóa hợp đồng này không?</p>
                </div>
                <div class="modal-footer" style="padding:16px 24px; background-color:#f9fafb; display:flex; justify-content:flex-end; gap:12px;">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a>
                </div>
            </div>
        </div>

        <script src="../../js/viewContractDetail.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
