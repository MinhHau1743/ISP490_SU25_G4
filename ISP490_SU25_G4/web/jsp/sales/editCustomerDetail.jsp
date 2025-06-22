<%--
    Document   : editCustomerDetail
    Author     : anhndhe172050
    Description: A professionally redesigned form for editing customer details.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page isELIgnored="false" %>

<c:set var="currentPage" value="listCustomer" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa: ${customer.name}</title>
    <link rel="stylesheet" href="${BASE_URL}/css/style.css">
    <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
    <link rel="stylesheet" href="${BASE_URL}/css/createCustomer.css"> 
    <script src="https://unpkg.com/feather-icons"></script>
    
    <style>
        .detail-layout {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 24px;
            align-items: flex-start;
        }
        .info-grid-2-col {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.25rem;
        }
        .form-group label {
            display: flex;
            align-items: center;
            font-weight: 500;
            margin-bottom: 8px;
            color: #4A5568; /* Slightly muted text color */
        }
        .form-group label i {
            margin-right: 8px;
            width: 16px;
            height: 16px;
        }
        @media (max-width: 992px) {
            .detail-layout { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <jsp:include page="/mainMenu.jsp"/>
        <main class="main-content">
            <c:choose>
                <c:when test="${not empty customer}">
                    <form class="page-content" action="${BASE_URL}/editCustomer" method="post">
                        <input type="hidden" name="enterpriseId" value="${customer.id}">
                        
                        <div class="detail-header">
                            <a href="${BASE_URL}/viewCustomer?id=${customer.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy</span></a>
                            <div class="action-buttons">
                                <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                            </div>
                        </div>

                        <c:if test="${not empty errorMessage}">
                            <div class="error-message">${errorMessage}</div>
                        </c:if>

                        <div class="detail-layout">
                            
                            <!-- CỘT CHÍNH (BÊN TRÁI) -->
                            <div class="main-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin Doanh nghiệp</h3>
                                    <div class="card-body">
                                        <div class="form-group">
                                            <label for="name"><i data-feather="briefcase"></i>Tên khách hàng (*)</label>
                                            <input type="text" id="name" name="name" class="form-control" value="${customer.name}" required>
                                        </div>
                                         <div class="info-grid-2-col">
                                            <div class="form-group">
                                                <label for="taxCode"><i data-feather="hash"></i>Mã số thuế</label>
                                                <input type="text" id="taxCode" name="taxCode" class="form-control" value="${customer.taxCode}">
                                            </div>
                                            <div class="form-group">
                                                <label for="bankNumber"><i data-feather="credit-card"></i>Số tài khoản ngân hàng</label>
                                                <input type="text" id="bankNumber" name="bankNumber" class="form-control" value="${customer.bankNumber}">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                     <h3 class="card-title">Địa chỉ</h3>
                                     <div class="card-body">
                                          <div class="form-group">
                                             <label for="fullAddress"><i data-feather="map-pin"></i>Địa chỉ đầy đủ</label>
                                             <input type="text" id="fullAddress" name="fullAddress" class="form-control" value="${customer.fullAddress}" placeholder="Hệ thống sẽ tự động tạo nếu để trống">
                                         </div>
                                     </div>
                                </div>
                            </div>
                            
                            <!-- CỘT PHỤ (BÊN PHẢI) -->
                            <div class="sidebar-column">
                                <div class="detail-card" style="margin-bottom: 24px;">
                                    <h3 class="card-title">Người liên hệ chính</h3>
                                    <c:if test="${not empty customer.contacts}">
                                        <c:set var="primaryContact" value="${customer.contacts[0]}"/>
                                    </c:if>
                                    <div class="card-body">
                                        <div class="form-group">
                                            <label for="contactName"><i data-feather="user"></i>Họ và tên</label>
                                            <input type="text" id="contactName" name="contactName" class="form-control" value="${primaryContact.fullName}">
                                        </div>
                                        <div class="form-group">
                                            <label for="contactPhone"><i data-feather="phone"></i>Số điện thoại</label>
                                            <input type="tel" id="contactPhone" name="contactPhone" class="form-control" value="${primaryContact.phoneNumber}">
                                        </div>
                                        <div class="form-group">
                                            <label for="contactEmail"><i data-feather="mail"></i>Email</label>
                                            <input type="email" id="contactEmail" name="contactEmail" class="form-control" value="${primaryContact.email}">
                                        </div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                    <h3 class="card-title">Phân loại & Phụ trách</h3>
                                    <div class="card-body">
                                        <div class="form-group">
                                            <label for="customerTypeId"><i data-feather="tag"></i>Nhóm khách hàng</label>
                                            <select id="customerTypeId" name="customerTypeId" class="form-control">
                                                <c:forEach var="type" items="${allCustomerTypes}">
                                                    <option value="${type.id}" ${customer.customerTypeId == type.id ? 'selected' : ''}>${type.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="employeeId"><i data-feather="user-check"></i>Nhân viên phụ trách</label>
                                            <c:if test="${not empty customer.assignedUsers}">
                                               <c:set var="assignedUserId" value="${customer.assignedUsers[0].id}"/>
                                            </c:if>
                                            <select id="employeeId" name="employeeId" class="form-control">
                                                <c:forEach var="emp" items="${allEmployees}">
                                                    <option value="${emp.id}" ${assignedUserId == emp.id ? 'selected' : ''}>${emp.fullName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
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
        document.addEventListener('DOMContentLoaded', () => feather.replace());
    </script>
    <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
</body>
</html>
