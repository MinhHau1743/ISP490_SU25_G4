<%--
    Document   : viewCustomerDetail
    Author     : anhndhe172050
    Description: Displays detailed information about a single customer.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="listCustomer" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Khách hàng - ${customer.name}</title>
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
                            <div class="detail-header">
                                <a href="${BASE_URL}/listCustomer" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại danh sách</span></a>
                                <div class="action-buttons">
                                    <a href="${BASE_URL}/editCustomer?id=${customer.id}" class="btn btn-secondary"><i data-feather="edit-2"></i>Sửa</a>
                                    <a href="#" class="btn btn-danger delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}"><i data-feather="trash-2"></i>Xóa</a>
                                </div>
                            </div>

                            <div class="detail-layout">
                                <div class="main-column">
                                    <div class="profile-header-card detail-card">
                                        <div class="card-body">

                                            <%-- === AVATAR DISPLAY LOGIC === --%>
                                            <c:choose>
                                                <%-- If avatarUrl exists, display the uploaded image --%>
                                                <c:when test="${not empty customer.avatarUrl}">
                                                    <img src="${BASE_URL}/${customer.avatarUrl}" alt="Avatar" class="customer-avatar" style="object-fit: cover;">
                                                </c:when>
                                                <%-- Otherwise, display the placeholder --%>
                                                <c:otherwise>
                                                    <img src="https://placehold.co/80x80/E0F7FA/00796B?text=${customer.name.substring(0,1)}" alt="Avatar" class="customer-avatar">
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="customer-main-info">
                                                <h2 class="name">${customer.name}</h2>
                                                <p class="code">Mã KH: ${customer.enterpriseCode}</p>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin Doanh nghiệp & Liên hệ</h3>
                                        <div class="card-body info-grid">
                                            <div class="info-item"><span class="label"><i data-feather="phone"></i>Số điện thoại</span><span class="value">${customer.primaryContactPhone}</span></div>
                                            <div class="info-item"><span class="label"><i data-feather="mail"></i>Email</span><span class="value"><a href="mailto:${customer.primaryContactEmail}">${customer.primaryContactEmail}</a></span></div>
                                            <div class="info-item"><span class="label"><i data-feather="hash"></i>Mã số thuế</span><span class="value">${not empty customer.taxCode ? customer.taxCode : 'N/A'}</span></div>
                                            <div class="info-item"><span class="label"><i data-feather="credit-card"></i>Số tài khoản</span><span class="value">${not empty customer.bankNumber ? customer.bankNumber : 'N/A'}</span></div>
                                            <div class="info-item full-width"><span class="label"><i data-feather="map-pin"></i>Địa chỉ</span><span class="value">${customer.fullAddress}</span></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="sidebar-column">
                                    <div class="detail-card">
                                        <h3 class="card-title">Thông tin bổ sung</h3>
                                        <div class="card-body">
                                            <div class="info-item"><span class="label">Nhóm khách hàng</span><span class="value">${customer.customerTypeName}</span></div>
                                            <div class="info-item">
                                                <span class="label">Nhân viên phụ trách</span>
                                                <div class="value assignees-list">
                                                    <c:forEach var="user" items="${customer.assignedUsers}">
                                                        <span>- ${user.fullName}</span>
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
                            <p>${errorMessage}</p>
                            <a href="${BASE_URL}/listCustomer" class="btn btn-primary">Quay lại danh sách</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
