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

        <style>
            /* CSS for action footer and modal */
            .page-content {
                display: flex;
                flex-direction: column;
                min-height: calc(100vh - 64px);
            }
            .detail-layout {
                flex-grow: 1;
            }
            .page-footer-actions {
                background-color: #ffffff;
                border-top: 1px solid #e2e8f0;
                padding: 16px 24px;
                margin-top: 32px;
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                box-shadow: 0 -4px 10px -5px rgba(0,0,0,0.05);
            }
            .modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(31, 41, 55, 0.6);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 1050;
                opacity: 0;
                visibility: hidden;
                transition: opacity 0.3s;
            }
            .modal-overlay.show {
                opacity: 1;
                visibility: visible;
            }
            .modal-content {
                background: white;
                padding: 32px;
                border-radius: 12px;
                width: 90%;
                max-width: 450px;
                text-align: center;
                transform: scale(0.95);
                transition: transform 0.3s;
                box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            }
            .modal-overlay.show .modal-content {
                transform: scale(1);
            }
            .warning-icon {
                color: #f97316;
                width: 48px;
                height: 48px;
                margin-bottom: 16px;
            }
            .modal-title {
                font-size: 1.25rem;
                font-weight: 700;
                color: #1f2937;
                margin-bottom: 8px;
            }
            #deleteMessage {
                color: #4b5563;
                line-height: 1.6;
                margin: 16px 0;
                font-size: 1rem;
            }
            #deleteMessage strong {
                color: #dc2626;
                font-weight: 600;
            }
            .modal-footer {
                display: flex;
                justify-content: center;
                gap: 12px;
                margin-top: 24px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <c:choose>
                    <c:when test="${not empty customer}">
                        <div class="page-content">
                            <div class="detail-header">
                                <a href="${BASE_URL}/listCustomer" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>
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

                            <!-- Action Footer -->
                            <div class="page-footer-actions">
                                <a href="${BASE_URL}/editCustomer?id=${customer.id}" class="btn btn-secondary"><i data-feather="edit-2"></i>Sửa</a>
                                <a href="#" class="btn btn-danger delete-trigger-btn" data-id="<c:out value='${customer.id}'/>" data-name="<c:out value='${customer.name}'/>"><i data-feather="trash-2"></i>Xóa</a>
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
    </body>
</html>
