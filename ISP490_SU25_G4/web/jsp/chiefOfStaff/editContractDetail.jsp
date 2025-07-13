<%--
    Document   : editContractDetail.jsp
    Created on : Jul 09, 2025
    Author     : NGUYEN MINH (Final Version by Gemini)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<c:set var="currentPage" value="listContract" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Hợp đồng - ${contract.contractCode}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editContractDetail.css">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <script src="https://unpkg.com/feather-icons"></script>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">

                    <form class="page-content" action="editContract" method="post">
                        <input type="hidden" name="id" value="${contract.id}">

                        <div class="detail-header">
                            <a href="${pageContext.request.contextPath}/listContract" class="back-link">
                                <i data-feather="arrow-left"></i><span>Hủy</span>
                            </a>
                            <div class="action-buttons">
                                <%-- ===== Bắt đầu phân quyền nút Lưu thay đổi ===== --%>
                                <c:choose>
                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                        <button type="submit" class="btn btn-primary">
                                            <i data-feather="save"></i>Lưu thay đổi
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-primary disabled-action" data-error="Bạn không có quyền sửa hợp đồng.">
                                            <i data-feather="save"></i>Lưu thay đổi
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                                <%-- ===== Kết thúc phân quyền nút Lưu thay đổi ===== --%>
                            </div>
                        </div>

                        <div class="detail-layout">
                            <div class="main-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin Hợp đồng</h3>
                                    <div class="card-body">
                                        <div class="info-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="contractCode">Mã hợp đồng</label>
                                                <input type="text" id="contractCode" name="contractCode" class="form-control" value="${contract.contractCode}" required>
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="contractName">Tên hợp đồng</label>
                                                <input type="text" id="contractName" name="contractName" class="form-control" value="${contract.contractName}" required>
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="enterpriseId">Khách hàng</label>
                                                <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                                    <c:forEach var="e" items="${enterpriseList}">
                                                        <option value="${e.id}" <c:if test="${contract.enterpriseId == e.id}">selected</c:if>>${e.name}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="form-group" style="margin-top: 16px;">
                                            <label class="form-label" for="notes">Mô tả / Điều khoản</label>
                                            <textarea id="notes" name="notes" class="form-control" rows="5">${contract.notes}</textarea>
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-card">
                                    <h3 class="card-title">Chi tiết Hàng hóa / Dịch vụ</h3>
                                    <div class="card-body">
                                        <table class="item-list-table-edit">
                                            <thead>
                                                <tr>
                                                    <th>Sản phẩm</th>
                                                    <th style="width: 15%;">Số lượng</th>
                                                    <th style="width: 20%; text-align: right;">Đơn giá</th>
                                                    <th style="width: 20%; text-align: right;">Thành tiền</th>
                                                    <th style="width: 5%;"></th>
                                                </tr>
                                            </thead>
                                            <tbody id="contract-item-list">
                                                <c:forEach var="item" items="${contractItems}">
                                                    <tr class="product-row">
                                                        <td>
                                                            ${item.name}
                                                            <input type="hidden" name="productId" value="${item.productId}">
                                                        </td>
                                                        <td><input type="number" name="quantity" class="form-control quantity-input" value="${item.quantity}" min="1"></td>
                                                        <td class="money-cell unit-price" data-price="${item.unitPrice}"><fmt:formatNumber value="${item.unitPrice}" pattern="###,###"/></td>
                                                        <td class="money-cell line-total"><fmt:formatNumber value="${item.unitPrice * item.quantity}" pattern="###,###"/></td>
                                                        <td><button type="button" class="btn-delete-item"><i data-feather="trash-2"></i></button></td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>

                                        <div class="summary-wrapper" style="margin-top: 24px;">
                                            <div class="summary-row"><span class="summary-label">Tổng phụ</span><span class="summary-value" id="subTotal">0</span></div>
                                            <div class="summary-row"><span class="summary-label">VAT (10%)</span><span class="summary-value" id="vatAmount">0</span></div>
                                            <div class="summary-row grand-total-row"><span class="summary-label">Tổng cộng</span><span class="summary-value" id="grandTotal">0</span></div>
                                        </div>
                                        <input type="hidden" id="contractValue" name="totalValue" value="0">

                                        <button type="button" class="btn-add-item" id="addProductBtn"><i data-feather="plus"></i> Thêm sản phẩm</button>
                                    </div>
                                </div>
                            </div>

                            <div class="sidebar-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thời hạn</h3>
                                    <div class="card-body">
                                        <div class="date-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="signedDate">Ngày ký</label>
                                                <input type="date" id="signedDate" name="signedDate" class="form-control" value="<fmt:formatDate value='${contract.signedDate}' pattern='yyyy-MM-dd' />">
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="startDate">Ngày hiệu lực</label>
                                                <input type="date" id="startDate" name="startDate" class="form-control" value="<fmt:formatDate value='${contract.startDate}' pattern='yyyy-MM-dd' />">
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="endDate">Ngày hết hạn</label>
                                                <input type="date" id="endDate" name="endDate" class="form-control" value="<fmt:formatDate value='${contract.endDate}' pattern='yyyy-MM-dd' />">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                    <h3 class="card-title">Quản lý</h3>
                                    <div class="card-body">
                                        <div class="form-group">
                                            <label class="form-label" for="status">Trạng thái</label>
                                            <select id="status" name="status" class="form-control status-select">
                                                <option value="pending" <c:if test="${contract.status == 'pending'}">selected</c:if>>Chờ duyệt</option>
                                                <option value="active" <c:if test="${contract.status == 'active'}">selected</c:if>>Còn hiệu lực</option>
                                                <option value="expired" <c:if test="${contract.status == 'expired'}">selected</c:if>>Đã hết hạn</option>
                                                <option value="cancelled" <c:if test="${contract.status == 'cancelled'}">selected</c:if>>Đã hủy</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="createdById">Nhân viên phụ trách</label>
                                                <select id="createdById" name="createdById" class="form-control">
                                                <c:forEach var="e" items="${employeeList}">
                                                    <option value="${e.id}" <c:if test="${contract.createdById == e.id}">selected</c:if>>${e.firstName} ${e.lastName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </main>
        </div>

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
                            <p style="text-align: center; color: #6b7280;">Không có sản phẩm nào.</p>
                        </c:if>
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
                    </div>
                </div>
            </div>
        </div>

        <div id="errorModal" class="modal-overlay" style="display: none;">
            <div class="modal-content" style="max-width: 420px;">
                <div class="modal-header">
                    <h3 class="modal-title" style="color: #dc2626;">Thông báo</h3>
                    <button type="button" class="close-modal-btn" id="closeErrorModalBtn"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body" style="text-align: center;">
                    <p id="errorMessageText" style="font-size: 16px;"></p>
                </div>
                <div class="modal-footer" style="padding: 16px 24px; justify-content: center;">
                    <button type="button" class="btn btn-primary" id="confirmErrorBtn">Đã hiểu</button>
                </div>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Kích hoạt các icon
                feather.replace();

                // Script xử lý click vào nút bị vô hiệu hóa
                document.body.addEventListener('click', function (event) {
                    const disabledAction = event.target.closest('.disabled-action');
                    if (disabledAction) {
                        event.preventDefault(); // Ngăn hành động mặc định
                        const errorMessage = disabledAction.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.';
                        alert(errorMessage); // Hiển thị thông báo
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/editContractDetail.js"></script>
        <script>
               feather.replace();
        </script>
    </body>
</html>