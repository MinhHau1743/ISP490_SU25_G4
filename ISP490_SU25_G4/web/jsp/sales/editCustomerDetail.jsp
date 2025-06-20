<%-- 
    Document   : editCustomerDetail
    Created on : Jun 19, 2025, 8:36:21 PM
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
        <title>Chỉnh sửa Khách hàng - ${customer.name}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">

        <link rel="stylesheet" href="../../css/editCustomerDetail.css">


    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">

                <form class="page-content" action="customer" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="customerId" value="${customer.id}">

                    <div class="detail-header">
                        <a href="customer?action=view&id=${customer.id}" class="back-link">
                            <i data-feather="arrow-left"></i><span>Hủy</span>
                        </a>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-primary">
                                <i data-feather="save"></i>Lưu thay đổi
                            </button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="profile-header-card detail-card">
                                <div class="card-body">
                                    <img src="${not empty customer.avatarUrl ? customer.avatarUrl : 'https://placehold.co/80x80/E0F7FA/00796B?text=C'}" alt="Avatar" class="customer-avatar">
                                    <div class="customer-main-info" style="width: 100%;">
                                        <input type="text" name="name" class="form-control" value="${customer.name}" style="font-size: 22px; font-weight: 700; height: auto; padding: 4px 0; border: none; background: transparent;">
                                        <p class="code">Mã KH: ${customer.customerCode}</p>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Thông tin liên hệ</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group"><label for="phone">Số điện thoại</label><input type="tel" id="phone" name="phone" class="form-control" value="${customer.phone}"></div>
                                        <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" class="form-control" value="${customer.email}"></div>
                                        <div class="form-group"><label for="website">Website</label><input type="url" id="website" name="website" class="form-control" value="${customer.website}"></div>
                                        <div class="form-group"><label for="address">Địa chỉ</label><input type="text" id="address" name="address" class="form-control" value="${customer.address}"></div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Hợp đồng đã ký kết (Thông tin tham khảo)</h3>
                                <div class="card-body" style="padding: 0;">
                                    <table class="contract-table">
                                        <thead><tr><th>Mã Hợp đồng</th><th>Tên Hợp đồng</th><th>Ngày ký</th><th>Giá trị</th><th>Trạng thái</th></tr></thead>
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
                                    <div class="form-group">
                                        <label for="customerGroup">Nhóm khách hàng</label>
                                        <select id="customerGroup" name="customerType" class="form-control">
                                            <option value="VIP" ${customer.customerType == 'VIP' ? 'selected' : ''}>VIP</option>
                                            <option value="Thân thiết" ${customer.customerType == 'Thân thiết' ? 'selected' : ''}>Thân thiết</option>
                                            <option value="Tiềm năng" ${customer.customerType == 'Tiềm năng' ? 'selected' : ''}>Tiềm năng</option>
                                            <option value="Mới" ${customer.customerType == 'Vip' ? 'selected' : ''}>Vip</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="employeeId">Nhân viên phụ trách</label>
                                        <select id="employeeId" name="employeeId" class="form-control">
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}" ${customer.assignee.id == employee.id ? 'selected' : ''}>${employee.fullName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="joinDate">Ngày tham gia</label>
                                        <input type="date" id="joinDate" name="joinDate" class="form-control" value="<fmt:formatDate value="${customer.joinDate}" pattern="yyyy-MM-dd"/>">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>