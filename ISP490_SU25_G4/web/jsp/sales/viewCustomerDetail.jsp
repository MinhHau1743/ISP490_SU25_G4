<%--
    Document    : viewCustomerDetail
    Author      : anhndhe172050
    Description: Displays detailed information about a single customer, including their recent contracts.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- This directive tells the JSP engine to evaluate Expression Language (EL) --%>
<%@ page isELIgnored="false" %>

<c:set var="currentPage" value="customer/list" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Khách hàng - <c:out value="${customer.name}"/></title>
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/viewCustomerDetail.css">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <c:choose>
                    <c:when test="${not empty customer}">
                        <div class="page-content">
                            <div class="detail-header">
                                <a href="${BASE_URL}/customer/list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>
                                        <%-- /jsp/sales/viewCustomerDetail.jsp --%>

                                <div class="header-actions">
                                    <%-- Nút Sửa: Chỉ Admin và Kinh doanh có quyền --%>
                                    <c:if test="${sessionScope.user.roleName == 'Admin' || sessionScope.user.roleName == 'Kinh doanh'}">
                                        <a href="${BASE_URL}/customer/edit?id=${customer.id}" class="btn btn-secondary">
                                            <i data-feather="edit-2"></i>Sửa
                                        </a>
                                    </c:if>

                                    <%-- Nút Xóa: Chỉ Admin có quyền --%>
                                    <c:if test="${sessionScope.user.roleName == 'Admin'}">
                                        <a href="#" class="btn btn-danger delete-trigger-btn" data-id="<c:out value='${customer.id}'/>" data-name="<c:out value='${customer.name}'/>">
                                            <i data-feather="trash-2"></i>Xóa
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                            <c:if test="${not empty successMessage}">
                                <div class="message success" style="margin: 0 24px 16px; padding: 16px; border-radius: 8px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; font-weight: 500;">
                                    <i data-feather="check-circle" style="vertical-align: -4px; margin-right: 8px;"></i>
                                    <strong>Thành công!</strong> ${successMessage}
                                </div>
                                <%-- Script để feather.replace() hoạt động cho icon mới thêm --%>
                                <script>
                                    document.addEventListener('DOMContentLoaded', function () {
                                        feather.replace();
                                    });
                                </script>
                            </c:if>

                            <div class="detail-layout">
                                <div class="main-column">
                                    <div class="profile-header-card detail-card">
                                        <div class="card-body">
                                            <c:choose>
                                                <c:when test="${not empty customer.avatarUrl}">
                                                    <img src="${BASE_URL}/${customer.avatarUrl}" alt="Avatar" class="customer-avatar" style="object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="https://placehold.co/80x80/E0F7FA/00796B?text=<c:out value='${customer.name.substring(0,1)}'/>" alt="Avatar" class="customer-avatar">
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="customer-main-info">
                                                <h2 class="name"><c:out value="${customer.name}"/></h2>
                                                <p class="code">Mã KH: <c:out value="${customer.enterpriseCode}"/></p>
                                            </div>
                                        </div>
                                    </div>

                                    <%-- Enterprise Information --%>
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin doanh nghiệp</h3>
                                        <div class="card-body info-grid">
                                            <%-- ĐÃ SỬA: Thay thế 'fax' bằng 'hotline' ở cả nhãn và giá trị --%>
                                            <div class="info-item"><span class="label"><i data-feather="phone-call"></i>Hotline</span><span class="value"><c:out value="${not empty customer.hotline ? customer.hotline : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="at-sign"></i>Email doanh nghiệp</span><span class="value"><a href="mailto:<c:out value='${customer.businessEmail}'/>"><c:out value="${not empty customer.businessEmail ? customer.businessEmail : 'N/A'}"/></a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="hash"></i>Mã số thuế</span><span class="value"><c:out value="${not empty customer.taxCode ? customer.taxCode : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="credit-card"></i>Số tài khoản</span><span class="value"><c:out value="${not empty customer.bankNumber ? customer.bankNumber : 'N/A'}"/></span></div>
                                            <div class="info-item full-width"><span class="label"><i data-feather="map-pin"></i>Địa chỉ</span><span class="value"><c:out value="${customer.fullAddress}"/></span></div>
                                        </div>
                                    </div>

                                    <%-- Representative Information --%>
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin người đại diện</h3>
                                        <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                        <div class="card-body info-grid">
                                            <div class="info-item"><span class="label"><i data-feather="user"></i>Họ và tên</span><span class="value"><c:out value="${not empty primaryContact.fullName ? primaryContact.fullName : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="briefcase"></i>Chức vụ</span><span class="value"><c:out value="${not empty primaryContact.position ? primaryContact.position : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="phone"></i>Số điện thoại</span><span class="value"><c:out value="${not empty primaryContact.phoneNumber ? primaryContact.phoneNumber : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="mail"></i>Email</span><span class="value"><a href="mailto:<c:out value='${primaryContact.email}'/>"><c:out value="${not empty primaryContact.email ? primaryContact.email : 'N/A'}"/></a></span></div>
                                        </div>
                                    </div>

                                    <%-- Recent Contracts Section --%>
                                    <div class="detail-card">
                                        <h3 class="card-title">Hợp đồng đã ký kết</h3>
                                        <div class="card-body" style="padding: 0;">
                                            <c:choose>
                                                <c:when test="${not empty recentContracts}">
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
                                                            <c:forEach var="contract" items="${recentContracts}">
                                                                <tr>
                                                                    <td><a href="${BASE_URL}/contract?action=view&id=${contract.id}" class="contract-code"><c:out value="${contract.contractCode}"/></a></td>
                                                                    <td><c:out value="${contract.contractName}"/></td>
                                                                    <td><fmt:formatDate value="${contract.signedDate}" pattern="dd/MM/yyyy"/></td>
                                                                    <td><fmt:formatNumber value="${contract.totalValue}" type="currency" currencyCode="VND"/></td>
                                                                    <td>
                                                                        <%-- ===== KHẮC PHỤC LỖI Ở ĐÂY ===== --%>
                                                                        <%-- Đổi tất cả contract.status thành contract.statusName --%>
                                                                        <c:choose>
                                                                            <c:when test="${contract.statusName == 'Còn hiệu lực'}"><span class="status-pill status-active">Còn hiệu lực</span></c:when>
                                                                            <c:when test="${contract.statusName == 'Sắp hết hạn'}"><span class="status-pill status-expiring">Sắp hết hạn</span></c:when>
                                                                            <c:when test="${contract.statusName == 'Đã hết hạn'}"><span class="status-pill status-expired">Đã hết hạn</span></c:when>
                                                                            <c:otherwise><span class="status-pill"><c:out value="${contract.statusName}"/></span></c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </c:when>
                                                <c:otherwise>
                                                    <p style="padding: 20px; text-align: center; color: #6b7280;">Không có hợp đồng nào gần đây.</p>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                </div>

                                <div class="sidebar-column">
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin bổ sung</h3>
                                        <div class="card-body">
                                            <div class="info-item"><span class="label">Nhóm khách hàng</span><span class="value"><c:out value="${customer.customerTypeName}"/></span></div>
                                            <div class="info-item">
                                                <span class="label">Nhân viên phụ trách</span>
                                                <div class="value assignees-list">
                                                    <c:forEach var="user" items="${customer.assignedUsers}">
                                                        <span><c:out value="${user.fullNameCombined}"/></span>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                            <div class="info-item">
                                                <span class="label">Ngày tham gia</span>
                                                <span class="value"><fmt:formatDate value="${customer.createdAt}" pattern="dd/MM/yyyy"/></span>
                                            </div>
                                        </div>
                                    </div>                                        

                                    <div class="detail-card">
                                        <h3 class="card-title">Giao dịch gần đây</h3>
                                        <c:choose>
                                            <c:when test="${not empty recentRequests}">
                                                <ul class="transaction-history-list">
                                                    <c:forEach var="r" items="${recentRequests}">
                                                        <li class="transaction-item">
                                                            <div class="transaction-info">
                                                                <a href="${pageContext.request.contextPath}/ticket?action=view&id=${r.id}" class="code"><c:out value="${r.requestCode}"/></a>
                                                                <p class="type"><c:out value="${r.serviceName}"/></p>
                                                            </div>
                                                            <c:choose>
                                                                <c:when test="${r.status == 'new'}"><span class="status-pill status-new">Mới</span></c:when>
                                                                <c:when test="${r.status == 'assigned'}"><span class="status-pill status-assigned">Đã giao</span></c:when>
                                                                <c:when test="${r.status == 'in_progress'}"><span class="status-pill status-in-progress">Đang xử lý</span></c:when>
                                                                <c:when test="${r.status == 'resolved'}"><span class="status-pill status-resolved">Đã xử lý</span></c:when>
                                                                <c:when test="${r.status == 'closed'}"><span class="status-pill status-closed">Đã đóng</span></c:when>
                                                                <c:when test="${r.status == 'rejected'}"><span class="status-pill status-rejected">Từ chối</span></c:when>
                                                                <c:otherwise><span class="status-pill">${r.status}</span></c:otherwise>
                                                            </c:choose>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </c:when>
                                            <c:otherwise>
                                                <p style="padding: 0 16px;">Không có giao dịch gần đây.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="page-content" style="text-align: center; padding-top: 50px;">
                            <h2>Không tìm thấy khách hàng</h2>
                            <p><c:out value="${errorMessage}"/></p>
                            <a href="${BASE_URL}/listCustomer" class="btn btn-primary">Quay lại danh sách</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>

        <%-- Delete Confirmation Modal --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <i data-feather="alert-triangle" class="warning-icon"></i>
                <h3 class="modal-title">Xác nhận Xóa</h3>
                <p id="deleteMessage"></p>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <form id="deleteForm" action="${BASE_URL}/customer/delete" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
                document.body.addEventListener('click', function (event) {
                    const disabledLink = event.target.closest('.disabled-action');
                    if (disabledLink) {
                        event.preventDefault();
                        const errorMessage = disabledLink.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.';
                        alert(errorMessage);
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/delete-modal-handler.js"></script>
    </body>
</html>