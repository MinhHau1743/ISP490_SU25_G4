<%--
    Document   : viewCustomerDetail
    Author     : anhndhe172050
    Description: Displays detailed information about a single customer, with a redesigned action footer.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- This directive tells the JSP engine to evaluate Expression Language (EL) --%>
<%@ page isELIgnored="false" %>

<c:set var="currentPage" value="listCustomer" />
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
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <c:choose>
                    <c:when test="${not empty customer}">
                        <div class="page-content">
                            <%-- Đoạn code mới đã được cập nhật --%>
                            <div class="detail-header">
                                <a href="${BASE_URL}/listCustomer" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>

                                <%-- Các nút hành động được chuyển lên đây --%>
                                <div class="header-actions">
                                    <a href="${BASE_URL}/editCustomer?id=${customer.id}" class="btn btn-secondary"><i data-feather="edit-2"></i>Sửa</a>
                                    <a href="#" class="btn btn-danger delete-trigger-btn" data-id="<c:out value='${customer.id}'/>" data-name="<c:out value='${customer.name}'/>"><i data-feather="trash-2"></i>Xóa</a>
                                </div>
                            </div>
                            <%-- Customer details content --%>
                            <div class="detail-layout">
                                <div class="main-column">
                                    <div class="profile-header-card detail-card">
                                        <div class="card-body">
                                            <c:choose>
                                                <c:when test="${not empty customer.avatarUrl}"><img src="${BASE_URL}/${customer.avatarUrl}" alt="Avatar" class="customer-avatar" style="object-fit: cover;"></c:when>
                                                <c:otherwise><img src="https://placehold.co/80x80/E0F7FA/00796B?text=<c:out value='${customer.name.substring(0,1)}'/>" alt="Avatar" class="customer-avatar"></c:otherwise>
                                            </c:choose>
                                            <div class="customer-main-info">
                                                <h2 class="name"><c:out value="${customer.name}"/></h2>
                                                <p class="code">Mã KH: <c:out value="${customer.enterpriseCode}"/></p>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin Doanh nghiệp & Liên hệ</h3>
                                        <div class="card-body info-grid">
                                            <c:if test="${not empty customer.contacts}">
                                                <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                            </c:if>
                                            <div class="info-item"><span class="label"><i data-feather="user"></i>Người đại diện</span><span class="value"><c:out value="${primaryContact.fullName}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="briefcase"></i>Chức vụ</span><span class="value"><c:out value="${primaryContact.position}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="phone"></i>Số điện thoại</span><span class="value"><c:out value="${customer.contacts[0].phoneNumber}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="mail"></i>Email</span><span class="value"><a href="mailto:<c:out value='${customer.contacts[0].email}'/>"><c:out value="${customer.contacts[0].email}"/></a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="hash"></i>Mã số thuế</span><span class="value"><c:out value="${not empty customer.taxCode ? customer.taxCode : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="credit-card"></i>Số tài khoản</span><span class="value"><c:out value="${not empty customer.bankNumber ? customer.bankNumber : 'N/A'}"/></span></div>
                                            <div class="info-item full-width"><span class="label"><i data-feather="map-pin"></i>Địa chỉ</span><span class="value"><c:out value="${customer.fullAddress}"/></span></div>
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
                                                        <span>- <c:out value="${user.fullName}"/></span>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </div>
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
                    <form id="deleteForm" action="${BASE_URL}/deleteCustomer" method="POST" style="margin:0;">
                        <input type="hidden" id="customerIdToDelete" name="customerId">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // Logic for the delete confirmation modal
                const modal = document.getElementById('deleteConfirmModal');
                if (modal) {
                    const deleteMessage = document.getElementById('deleteMessage');
                    const customerIdInput = document.getElementById('customerIdToDelete');
                    const cancelBtn = document.getElementById('cancelDeleteBtn');
                    const deleteButton = document.querySelector('.delete-trigger-btn');

                    if (deleteButton) {
                        deleteButton.addEventListener('click', function (event) {
                            event.preventDefault(); // Prevent the link from adding '#' to the URL

                            const customerId = this.getAttribute('data-id');
                            const customerName = this.getAttribute('data-name') || "khách hàng này";

                            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa khách hàng <strong>"${customerName}"</strong>? Hành động này không thể hoàn tác.`;
                            customerIdInput.value = customerId;

                            modal.classList.add('show');
                            feather.replace();
                        });
                    }

                    const closeModal = () => modal.classList.remove('show');
                    cancelBtn.addEventListener('click', closeModal);
                    modal.addEventListener('click', event => {
                        if (event.target === modal)
                            closeModal();
                    });
                }
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
