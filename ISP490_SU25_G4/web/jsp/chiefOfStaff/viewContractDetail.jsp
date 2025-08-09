<%--
    Document    : viewContractDetail.jsp
    Created on  : Jun 20, 2025
    Author      : NGUYEN MINH (Fixed by Gemini)
    Description : Updated to use statusName from the database.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %> <%-- ADD THIS LIBRARY FOR STRING MANIPULATION --%>

<c:set var="currentPage" value="listContract" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Hợp đồng - ${not empty contract ? contract.contractCode : 'Không tìm thấy'}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewContractDetail.css">
    </head>
    <body data-context-path="${pageContext.request.contextPath}">
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <c:if test="${not empty contract}">
                    <div class="page-content">
                        <div class="detail-header">
                            <a href="${pageContext.request.contextPath}/contract?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>

                            <div class="action-buttons" style="display: flex; gap: 8px;">
                                <a href="#" class="btn btn-secondary"><i data-feather="printer"></i>In hợp đồng</a>

                                <c:if test="${contract.statusName == 'Đã hoàn thành' && (sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kinh doanh')}">
                                    <a href="${pageContext.request.contextPath}/jsp/customerSupport/createFeedback.jsp" class="btn btn-warning"><i data-feather="star"></i>Gửi đánh giá</a>
                                </c:if>

                                <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                    <a href="${pageContext.request.contextPath}/contract?action=edit&id=${contract.id}" class="btn btn-primary"><i data-feather="edit-2"></i>Sửa</a>
                                    <button type="button" class="btn btn-danger delete-btn" 
                                            data-id="${contract.id}" 
                                            data-name="${contract.contractCode}">
                                        <i data-feather="trash-2"></i>Xóa
                                    </button>
                                </c:if>
                            </div>
                        </div>

                        <div class="detail-layout">
                            <div class="main-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin chung</h3>
                                    <div class="card-body">
                                        <div class="info-grid">
                                            <div class="info-item"><span class="label">Mã hợp đồng</span><span class="value" style="font-weight: 700; color: var(--primary-color, #0d9488);">${contract.contractCode}</span></div>
                                            <div class="info-item"><span class="label">Khách hàng</span><span class="value">${contract.enterpriseName}</span></div>
                                            <div class="info-item full-width"><span class="label">Tên hợp đồng</span><span class="value">${contract.contractName}</span></div>
                                            <div class="info-item full-width"><span class="label">Mô tả / Điều khoản</span><span class="value notes">${not empty contract.notes ? contract.notes : 'Không có ghi chú'}</span></div>
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
                                                <c:forEach var="item" items="${contractItems}">
                                                    <tr>
                                                        <td class="product-name">${item.name}</td>
                                                        <td style="text-align: center;">${item.quantity}</td>
                                                        <td class="money-cell"><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫"/></td>
                                                        <td class="money-cell"><fmt:formatNumber value="${item.unitPrice * item.quantity}" type="currency" currencySymbol="₫"/></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                            <tfoot>
                                                <tr>
                                                    <td colspan="4" style="padding: 0; border: none;"> 
                                                        <div class="summary-wrapper">
                                                            <div class="summary-row"><span class="summary-label">Tổng phụ</span><span class="summary-value"><fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="₫"/></span></div>
                                                            <div class="summary-row"><span class="summary-label">VAT (10%)</span><span class="summary-value"><fmt:formatNumber value="${vatAmount}" type="currency" currencySymbol="₫"/></span></div>
                                                            <div class="summary-row grand-total-row"><span class="summary-label">Tổng cộng</span><span class="summary-value"><fmt:formatNumber value="${grandTotal}" type="currency" currencySymbol="₫"/></span></div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <div class="sidebar-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thời hạn & Giá trị</h3>
                                    <div class="card-body">

                                        <%-- ======================================================= --%>
                                        <%-- ## 1. UPDATE: STATUS DISPLAY                          --%>
                                        <%-- ======================================================= --%>
                                        <div class="info-item">
                                            <span class="label">Trạng thái</span>
                                            <span class="value">
                                                <%-- This now uses statusName directly from the database --%>
                                                <span class="status-pill status-${fn:toLowerCase(fn:replace(contract.statusName, ' ', '-'))}">${contract.statusName}</span>
                                            </span>
                                        </div>

                                        <div class="info-item"><span class="label">Ngày ký</span><span class="value"><fmt:formatDate value="${contract.signedDate}" pattern="dd/MM/yyyy"/></span></div>
                                        <div class="info-item"><span class="label">Ngày hết hạn</span><span class="value"><fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/></span></div>
                                        <div class="info-item"><span class="label">Giá trị hợp đồng</span><span class="value" style="font-size: 18px; font-weight: 700;"><fmt:formatNumber value="${grandTotal}" type="currency" currencySymbol="₫"/></span></div>
                                        <div class="info-item"><span class="label">Nhân viên phụ trách</span><span class="value">${contract.createdByName}</span></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <c:if test="${empty contract}">
                    <div class="page-content not-found">
                        <h2>Không tìm thấy hợp đồng</h2>
                        <p>Hợp đồng bạn yêu cầu không tồn tại hoặc đã bị xóa.</p>
                        <a href="${pageContext.request.contextPath}/contract?action=list" class="btn btn-primary">Quay lại danh sách</a>
                    </div>
                </c:if>
            </main>
        </div>

        <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Xác nhận Xóa</h3>
                    <button class="modal-close" id="modalCloseBtn">&times;</button>
                </div>
                <div class="modal-body">
                    <p>Bạn có chắc chắn muốn xóa hợp đồng <strong id="contractNameToDelete"></strong> không?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="modalCancelBtn">Hủy</button>
                    <a href="#" class="btn btn-danger" id="modalConfirmDeleteBtn">Xác nhận Xóa</a>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/viewContractDetail.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>