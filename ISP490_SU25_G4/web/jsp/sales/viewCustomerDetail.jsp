<%-- 
    Document   : viewCustomerDetail
    Created on : Jun 17, 2025, 12:17:10 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="listCustomer" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Khách hàng - ${customer.name}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/viewCustomerDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">

                <%-- Chỉ hiển thị nội dung nếu đối tượng customer tồn tại --%>
                <c:if test="${not empty customer}">
                    <div class="page-content">
                        <div class="detail-header">
                            <a href="customer" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>
                            <div class="action-buttons" style="display: flex; gap: 8px;">
                                <a href="editCustomer?id=${customer.id}" class="btn btn-secondary"><i data-feather="edit-2"></i>Sửa</a>
                                <a href="#" class="btn btn-danger delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}"><i data-feather="trash-2"></i>Xóa</a>
                            </div>
                        </div>

                        <div class="detail-layout">
                            <div class="main-column">
                                <div class="profile-header-card detail-card">
                                    <div class="card-body">
                                        <img src="${not empty customer.avatarUrl ? customer.avatarUrl : 'https://placehold.co/80x80/E0F7FA/00796B?text=C'}" alt="Avatar" class="customer-avatar">
                                        <div class="customer-main-info">
                                            <h2 class="name">${customer.name}</h2>
                                            <p class="code">Mã KH: ${customer.customerCode}</p>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin liên hệ</h3>
                                    <div class="card-body">
                                        <div class="info-grid">
                                            <div class="info-item"><span class="label"><i data-feather="phone"></i>Số điện thoại</span><span class="value">${customer.phone}</span></div>
                                            <div class="info-item"><span class="label"><i data-feather="mail"></i>Email</span><span class="value"><a href="mailto:${customer.email}">${customer.email}</a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="globe"></i>Website</span><span class="value"><a href="http://${customer.website}" target="_blank">${customer.website}</a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="map-pin"></i>Địa chỉ</span><span class="value">${customer.address}</span></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-card">
                                    <h3 class="card-title">Hợp đồng đã ký kết</h3>
                                    <div class="card-body" style="padding: 0;">
                                        <table class="contract-table">
                                            <thead>
                                                <tr>
                                                    <th>Mã Hợp đồng</th>
                                                    <th>Tên Hợp đồng</th>
                                                    <th>Ngày ký</th>
                                                    <th>Giá trị</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="contract" items="${customer.contracts}">
                                                    <tr>
                                                        <td><a href="#" class="contract-code">${contract.contractCode}</a></td>
                                                        <td>${contract.name}</td>
                                                        <td><fmt:formatDate value="${contract.signDate}" pattern="dd/MM/yyyy"/></td>
                                                        <td><fmt:formatNumber value="${contract.value}" type="currency" currencyCode="VND"/></td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${contract.status == 'active'}"><span class="status-pill status-active">Còn hiệu lực</span></c:when>
                                                                <c:when test="${contract.status == 'expired'}"><span class="status-pill status-expired">Đã hết hạn</span></c:when>
                                                                <c:otherwise><span class="status-pill">${contract.status}</span></c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>

                            <div class="sidebar-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin bổ sung</h3>
                                    <div class="card-body">
                                        <div class="info-item"><span class="label">Nhóm khách hàng</span><span class="value">${customer.customerType}</span></div>
                                        <div class="info-item"><span class="label">Nhân viên phụ trách</span><span class="value">${customer.assignee.name}</span></div>
                                        <div class="info-item"><span class="label">Ngày tham gia</span><span class="value"><fmt:formatDate value="${customer.joinDate}" pattern="dd/MM/yyyy"/></span></div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                    <h3 class="card-title">Giao dịch gần đây</h3>
                                    <ul class="transaction-history-list">
                                        <c:forEach var="tx" items="${customer.recentTransactions}">
                                            <li class="transaction-item">
                                                <div class="transaction-info">
                                                    <a href="#" class="code">${tx.transactionCode}</a>
                                                    <p class="type">${tx.type}</p>
                                                </div>
                                                <c:choose>
                                                    <c:when test="${tx.status == 'processing'}"><span class="status-pill status-processing">Đang xử lý</span></c:when>
                                                    <c:when test="${tx.status == 'completed'}"><span class="status-pill status-completed">Hoàn thành</span></c:when>
                                                    <c:otherwise><span class="status-pill">${tx.status}</span></c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <%-- Hiển thị thông báo nếu không tìm thấy customer --%>
                <c:if test="${empty customer}">
                    <div class="page-content" style="text-align: center; padding-top: 50px;">
                        <h2>Không tìm thấy khách hàng</h2>
                        <p>Khách hàng bạn yêu cầu không tồn tại hoặc đã bị xóa.</p>
                        <a href="listCustomer.jsp" class="btn btn-primary">Quay lại danh sách</a>
                    </div>
                </c:if>
            </main>
        </div>

        <%-- Modal xác nhận xóa --%>
        <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
            <%-- ... Nội dung modal giữ nguyên ... --%>
        </div>
        <script src="../../js/viewCustomerDetail.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>