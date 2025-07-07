<%--
    Document   : createContract.jsp
    Created on : Jun 20, 2025
    Author     : NGUYEN MINH (Final version by Gemini on Jul 08, 2025)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<%-- Đặt biến để đánh dấu trang hiện tại, hữu ích cho việc active menu --%>
<c:set var="currentPageJsp" value="listContract" />

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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createContract.css">

    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp" />

            <main class="main-content">
                <form class="page-content" action="${pageContext.request.contextPath}/createContract" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/createContract" class="back-link">
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
                                        <div class="form-group"><label for="contractName">Tên hợp đồng (*)</label><input type="text" id="contractName" name="contractName" class="form-control" placeholder="VD: Hợp đồng bảo trì điều hòa quý 3" required></div>
                                        <div class="form-group"><label for="contractCode">Mã hợp đồng</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="(Tự động tạo)" readonly></div>
                                        <div class="form-group">
                                            <label for="enterpriseId">Khách hàng (*)</label>
                                            <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                                <option value="" disabled selected>-- Chọn khách hàng --</option>
                                                <c:forEach var="enterprise" items="${enterpriseList}">
                                                    <option value="${enterprise.id}">${enterprise.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                      
                                    </div>
                                    <div class="form-group">
                                        <label for="notes">Mô tả / Điều khoản chính</label>
                                        <textarea id="notes" name="notes" class="form-control" rows="5" placeholder="Nhập các ghi chú hoặc điều khoản quan trọng của hợp đồng..."></textarea>
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
                                            <tr>
                                                <td colspan="3" style="text-align: right;">Tổng phụ</td>
                                                <td id="subTotal" style="text-align: right;">0</td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td colspan="3" style="text-align: right;">VAT (10%)</td>
                                                <td id="vatAmount" style="text-align: right;">0</td>
                                                <td></td>
                                            </tr>
                                            <tr>
                                                <td colspan="3" style="text-align: right; font-size: 16px;"><strong>Tổng cộng</strong></td>
                                                <td id="grandTotal" class="grand-total" style="text-align: right;"><strong>0</strong></td>
                                                <td></td>
                                            </tr>
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
                                    <div class="form-group"><label for="contractValue">Giá trị Hợp đồng (VND)</label><input type="text" id="contractValue" name="totalValue" class="form-control" value="0" readonly></div>
                                    <div class="date-grid">
                                        <div class="form-group date-sign"><label for="signedDate">Ngày ký (*)</label><input type="date" id="signedDate" name="signedDate" class="form-control" required></div>
                                        <div class="form-group date-effective"><label for="startDate">Ngày hiệu lực (*)</label><input type="date" id="startDate" name="startDate" class="form-control" required></div>
                                        <div class="form-group date-expiry"><label for="endDate">Ngày hết hạn (*)</label><input type="date" id="endDate" name="endDate" class="form-control" required></div>
                                    </div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h3 class="card-title">Quản lý</h3>
                                <div class="card-body">
                                    <div class="form-group"><label for="status">Trạng thái</label><select id="status" name="status" class="form-control"><option value="pending" selected>Chờ duyệt</option><option value="active">Còn hiệu lực</option></select></div>
                                    <div class="form-group">
                                        <label for="createdById">Nhân viên phụ trách</label>
                                        <select id="createdById" name="createdById" class="form-control">
                                            <option value="" disabled selected>-- Chọn nhân viên --</option>
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}">${employee.firstName} ${employee.lastName}</option>
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

        <%-- Modal (cửa sổ pop-up) để chọn sản phẩm --%>
        <div id="productSearchModal" class="modal-overlay" style="display: none;">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Chọn sản phẩm</h3>
                    <button type="button" class="close-modal-btn" id="closeProductModalBtn"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body">
                    <div class="search-bar-container">
                        <input type="text" id="productSearchInput" class="form-control" placeholder="Tìm kiếm sản phẩm theo tên...">
                    </div>
                    <div id="productList" class="product-list-container">
                        <c:if test="${empty productList}">
                            <div class="empty-list-message" style="text-align: center;">
                                Không có sản phẩm nào để hiển thị.
                            </div>
                        </c:if>
                        <c:if test="${not empty productList}">
                            <c:forEach var="product" items="${productList}">
                                <div class="product-search-item" data-id="${product.id}" data-name="${product.name}" data-price="${product.price}">
                                    <div class="product-search-info">
                                        <div class="name">${product.name}</div>
                                        <div class="code">${product.productCode}</div>
                                    </div>
                                    <div class="product-search-price">
                                        <fmt:formatNumber value="${product.price}" pattern="###,###"/> ₫
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/createContract.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>