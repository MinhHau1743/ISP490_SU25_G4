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
                                    <%-- Phân quyền nút Sửa --%>
                                    <c:choose>
                                        <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kinh doanh'}">
                                            <a href="${BASE_URL}/editCustomer?id=${customer.id}" class="btn btn-secondary"><i data-feather="edit-2"></i>Sửa</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#" class="btn btn-secondary disabled-action" data-error="Bạn không có quyền sửa khách hàng."><i data-feather="edit-2"></i>Sửa</a>
                                        </c:otherwise>
                                    </c:choose>

                                    <%-- Phân quyền nút Xóa --%>
                                    <c:choose>
                                        <c:when test="${sessionScope.userRole == 'Admin'}">
                                            <a href="#" class="btn btn-danger delete-trigger-btn" data-id="<c:out value='${customer.id}'/>" data-name="<c:out value='${customer.name}'/>"><i data-feather="trash-2"></i>Xóa</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#" class="btn btn-danger disabled-action" data-error="Bạn không có quyền xóa khách hàng."><i data-feather="trash-2"></i>Xóa</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <%-- Customer details content --%>
                            <%-- BẮT ĐẦU KHỐI CODE CẬP NHẬT --%>
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

                                    <%-- Thông tin doanh nghiệp --%>
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin doanh nghiệp</h3>
                                        <div class="card-body info-grid">
                                            <div class="info-item"><span class="label"><i data-feather="phone-call"></i>Fax/Hotline</span><span class="value"><c:out value="${not empty customer.fax ? customer.fax : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="at-sign"></i>Email doanh nghiệp</span><span class="value"><a href="mailto:<c:out value='${customer.businessEmail}'/>"><c:out value="${not empty customer.businessEmail ? customer.businessEmail : 'N/A'}"/></a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="hash"></i>Mã số thuế</span><span class="value"><c:out value="${not empty customer.taxCode ? customer.taxCode : 'N/A'}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="credit-card"></i>Số tài khoản</span><span class="value"><c:out value="${not empty customer.bankNumber ? customer.bankNumber : 'N/A'}"/></span></div>
                                            <div class="info-item full-width"><span class="label"><i data-feather="map-pin"></i>Địa chỉ</span><span class="value"><c:out value="${customer.fullAddress}"/></span></div>
                                        </div>
                                    </div>

                                    <%-- Thông tin người đại diện --%>
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin người đại diện</h3>
                                        <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                        <div class="card-body info-grid">
                                            <div class="info-item"><span class="label"><i data-feather="user"></i>Họ và tên</span><span class="value"><c:out value="${primaryContact.fullName}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="briefcase"></i>Chức vụ</span><span class="value"><c:out value="${primaryContact.position}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="phone"></i>Số điện thoại</span><span class="value"><c:out value="${primaryContact.phoneNumber}"/></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="mail"></i>Email</span><span class="value"><a href="mailto:<c:out value='${primaryContact.email}'/>"><c:out value="${not empty primaryContact.email ? primaryContact.email : 'N/A'}"/></a></span></div>
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
                                            <div class="info-item">
                                                <span class="label">Ngày tham gia</span>
                                                <span class="value"><fmt:formatDate value="${customer.createdAt.time}" pattern="dd/MM/yyyy"/></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%-- KẾT THÚC KHỐI CODE CẬP NHẬT --%>


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
                // Kích hoạt icon
                feather.replace();

                // Script xử lý click vào nút bị vô hiệu hóa
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
        <script>
            feather.replace();
        </script>
    </body>
</html>
