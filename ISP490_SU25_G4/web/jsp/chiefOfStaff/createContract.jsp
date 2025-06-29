<%-- 
    Document   : createContract
    Created on : Jun 20, 2025, 8:57:27 AM
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
        <title>Tạo Hợp đồng mới</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/createContract.css">


    </head>
    <body>
        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>
            <main class="main-content">                

                <form class="page-content" action="contract" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="detail-header">
                        <a href="listContract.jsp" class="back-link">
                            <i data-feather="arrow-left"></i><span>Hủy</span>
                        </a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary">
                                <i data-feather="plus-circle"></i>Tạo Hợp đồng
                            </button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h3 class="card-title">Thông tin Hợp đồng</h3>
                                <div class="card-body">
                                    <div class="info-grid">
                                        <div class="form-group"><label for="contractName">Tên hợp đồng (*)</label><input type="text" id="contractName" name="name" class="form-control" placeholder="VD: Hợp đồng bảo trì điều hòa quý 3" required></div>
                                        <div class="form-group"><label for="contractCode">Mã hợp đồng</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="(Tự động tạo)" readonly></div>
                                        <div class="form-group">
                                            <label for="customerId">Khách hàng (*)</label>
                                            <select id="customerId" name="customerId" class="form-control" required>
                                                <option value="" disabled selected>-- Chọn khách hàng --</option>
                                                <c:forEach var="customer" items="${customerList}">
                                                    <option value="${customer.id}">${customer.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group"><label for="contractType">Loại hợp đồng</label><select id="contractType" name="type" class="form-control"><option value="maintenance" selected>Bảo trì</option><option value="supply">Cung cấp</option><option value="service">Dịch vụ</option></select></div>
                                    </div>
                                    <div class="form-group">
                                        <label for="description">Mô tả / Điều khoản chính</label>
                                        <textarea id="description" name="description" class="form-control" rows="5" placeholder="Nhập các ghi chú hoặc điều khoản quan trọng của hợp đồng..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h3 class="card-title">Chi tiết Hàng hóa / Dịch vụ</h3>
                                <div class="card-body">
                                    <table class="item-list-table">
                                        <thead>
                                            <tr>
                                                <th style="width: 40%;">Sản phẩm</th>
                                                <th style="width: 15%;">Số lượng</th>
                                                <th style="width: 20%;">Đơn giá (VND)</th>
                                                <th style="width: 20%;">Thành tiền (VND)</th>
                                                <th style="width: 5%;"></th>
                                            </tr>
                                        </thead>
                                        <tbody id="contract-item-list">
                                            <%-- Các sản phẩm được thêm vào đây bằng JavaScript --%>
                                        </tbody>
                                        <tfoot>
                                            <tr><td colspan="3" style="text-align: right;">Tổng phụ</td><td id="subTotal" style="text-align: right;">0</td><td></td></tr>
                                            <tr><td colspan="3" style="text-align: right;">VAT (10%)</td><td id="vatAmount" style="text-align: right;">0</td><td></td></tr>
                                            <tr><td colspan="3" style="text-align: right; font-size: 16px;">Tổng cộng</td><td id="grandTotal" class="grand-total" style="text-align: right;">0</td><td></td></tr>
                                        </tfoot>
                                    </table>
                                    <button type="button" class="add-item-btn" id="addProductBtn"><i data-feather="plus"></i> Thêm sản phẩm</button>
                                </div>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h3 class="card-title">Giá trị & Thời hạn</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="contractValue">Giá trị Hợp đồng (VND)</label><input type="text" id="contractValue" name="grandTotal" class="form-control" value="0" readonly></div>

                                    <div class="date-grid">
                                        <div class="form-group date-sign">
                                            <label for="signDate">Ngày ký (*)</label>
                                            <input type="date" id="signDate" name="signDate" class="form-control" required>
                                        </div>
                                        <div class="form-group date-effective">
                                            <label for="effectiveDate">Ngày hiệu lực</label>
                                            <input type="date" id="effectiveDate" name="effectiveDate" class="form-control">
                                        </div>
                                        <div class="form-group date-expiry">
                                            <label for="expiryDate">Ngày hết hạn</label>
                                            <input type="date" id="expiryDate" name="expiryDate" class="form-control">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h3 class="card-title">Quản lý</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="status">Trạng thái</label><select id="status" name="status" class="form-control"><option value="active" selected>Còn hiệu lực</option><option value="pending">Chờ ký</option></select></div>
                                    <div class="form-group">
                                        <label for="employeeId">Nhân viên phụ trách</label>
                                        <select id="employeeId" name="employeeId" class="form-control">
                                            <option value="" disabled selected>-- Chọn nhân viên --</option>
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}">${employee.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>

        <div id="productSearchModal" class="modal-overlay" style="display: none;">
            <div class="modal-content">
                <div class="modal-header"><h3 class="modal-title">Chọn sản phẩm</h3><button type="button" class="close-modal-btn" id="closeProductModalBtn"><i data-feather="x"></i></button></div>
                <div class="modal-body" style="padding: 16px;">
                    <input type="text" id="productSearchInput" class="form-control" placeholder="Tìm kiếm sản phẩm..." style="margin-bottom: 16px;">
                    <div id="productList">
                        <c:forEach var="product" items="${productList}">
                            <div class="product-search-item" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}">
                                <div class="product-search-info">
                                    <div class="name">${product.name}</div>
                                    <div class="code">${product.productCode}</div>
                                </div>
                                <div class="product-search-price"><fmt:formatNumber value="${product.price}" pattern="###,###"/></div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
        <script src="js/createContract.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>

