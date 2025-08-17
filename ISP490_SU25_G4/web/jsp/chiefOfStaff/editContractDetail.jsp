<%--
    Document    : editContractDetail.jsp
    Created on  : Jul 09, 2025
    Author      : NGUYEN MINH (Final Version by Gemini)
    Description : Final version with targeted font-fix, restored modal, and inline validation.
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

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

        <%-- Tải 2 font: "Inter" cho toàn trang và "Be Vietnam Pro" để sửa lỗi hiển thị --%>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@700&display=swap" rel="stylesheet">
        
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editContractDetail.css">

        <style>
            /* CSS cho validation lỗi inline */
            .error-text {
                color: #d9534f;
                font-size: 13px;
                display: none;
                margin-top: 5px;
            }
            .form-control.is-invalid, .btn-add-item.is-invalid {
                border-color: #d9534f !important;
            }
            /* CSS để áp dụng font sửa lỗi có mục tiêu */
            .font-fix-vietnamese {
                font-family: 'Be Vietnam Pro', sans-serif;
            }
        </style>
    </head>
    <body data-context-path="${pageContext.request.contextPath}">
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <div class="page-content">
                    <form class="page-content" id="editContractForm" action="contract" method="post">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="id" value="${contract.id}">

                        <div class="detail-header">
                            <a href="${pageContext.request.contextPath}/contract?action=view&id=${contract.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy</span></a>
                            <div class="action-buttons"><button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button></div>
                        </div>

                        <c:if test="${not empty errorMessages}">
                            <div class="server-error-container">
                                <strong>Vui lòng sửa các lỗi sau:</strong>
                                <ul><c:forEach var="error" items="${errorMessages}"><li>${error}</li></c:forEach></ul>
                            </div>
                        </c:if>

                        <div class="detail-layout">
                            <div class="main-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thông tin Hợp đồng</h3>
                                    <div class="card-body">
                                        <div class="info-grid">
                                            <div class="form-group"><label class="form-label" for="contractCode">Mã hợp đồng (*)</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="${contract.contractCode}" required title="Vui lòng nhập mã hợp đồng."></div>
                                            <div class="form-group"><label class="form-label" for="contractName">Tên hợp đồng (*)</label><input type="text" id="contractName" name="contractName" class="form-control" value="${contract.contractName}" required title="Vui lòng nhập tên hợp đồng."></div>
                                            <div class="form-group"><label class="form-label" for="enterpriseId">Khách hàng (*)</label><select id="enterpriseId" name="enterpriseId" class="form-control" required title="Vui lòng chọn khách hàng."><c:forEach var="e" items="${enterpriseList}"><option value="${e.id}" <c:if test="${contract.enterpriseId == e.id}">selected</c:if>>${e.name}</option></c:forEach></select></div>
                                        </div>
                                        <div class="form-group" style="margin-top: 16px;"><label class="form-label" for="notes">Mô tả / Điều khoản</label><textarea id="notes" name="notes" class="form-control" rows="5">${contract.notes}</textarea></div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                    <h3 class="card-title">Chi tiết Hàng hóa / Dịch vụ</h3>
                                    <div class="card-body">
                                        <table class="item-list-table-edit">
                                            <thead><tr><th>Sản phẩm</th><th style="width: 15%;">Số lượng</th><th style="width: 20%; text-align: right;">Đơn giá</th><th style="width: 20%; text-align: right;">Thành tiền</th><th style="width: 5%;"></th></tr></thead>
                                            <tbody id="contract-item-list"><c:forEach var="item" items="${contractItems}"><tr class="product-row"><td>${item.name}<input type="hidden" name="productId" value="${item.productId}"></td><td><input type="number" name="quantity" class="form-control quantity-input" value="${item.quantity}" min="1"></td><td class="money-cell unit-price" data-price="${item.unitPrice}"><fmt:formatNumber value="${item.unitPrice}" pattern="###,###"/></td><td class="money-cell line-total"><fmt:formatNumber value="${item.unitPrice * item.quantity}" pattern="###,###"/></td><td><button type="button" class="btn-delete-item"><i data-feather="trash-2"></i></button></td></tr></c:forEach></tbody>
                                        </table>
                                        <div class="summary-wrapper" style="margin-top: 24px;">
                                            <div class="summary-row"><span class="summary-label">Tổng phụ</span><span class="summary-value" id="subTotal">0</span></div>
                                            <div class="summary-row"><span class="summary-label">VAT (10%)</span><span class="summary-value" id="vatAmount">0</span></div>
                                            <div class="summary-row grand-total-row"><span class="summary-label font-fix-vietnamese">Tổng cộng</span><span class="summary-value" id="grandTotal">0</span></div>
                                        </div>
                                        <input type="hidden" id="contractValue" name="totalValue" value="${contract.totalValue}">
                                        <button type="button" class="btn-add-item" id="addProductBtn"><i data-feather="plus"></i> Thêm sản phẩm</button>
                                        <span class="error-text"></span>
                                    </div>
                                </div>
                            </div>
                            <div class="sidebar-column">
                                <div class="detail-card">
                                    <h3 class="card-title">Thời hạn</h3>
                                    <div class="card-body">
                                        <div class="date-grid">
                                            <div class="form-group"><label class="form-label" for="signedDate">Ngày ký (*)</label><input type="date" id="signedDate" name="signedDate" class="form-control" value="<fmt:formatDate value='${contract.signedDate}' pattern='yyyy-MM-dd' />" required title="Vui lòng chọn ngày ký."><span class="error-text"></span></div>
                                            <div class="form-group"><label class="form-label" for="startDate">Ngày hiệu lực (*)</label><input type="date" id="startDate" name="startDate" class="form-control" value="<fmt:formatDate value='${contract.startDate}' pattern='yyyy-MM-dd' />" required title="Vui lòng chọn ngày hiệu lực."><span class="error-text"></span></div>
                                            <div class="form-group"><label class="form-label" for="endDate">Ngày hết hạn (*)</label><input type="date" id="endDate" name="endDate" class="form-control" value="<fmt:formatDate value='${contract.endDate}' pattern='yyyy-MM-dd' />" required title="Vui lòng chọn ngày hết hạn."><span class="error-text"></span></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="detail-card">
                                    <h3 class="card-title">Quản lý</h3>
                                    <div class="card-body">
                                        <div class="form-group"><label class="form-label" for="statusId">Trạng thái (*)</label><select id="statusId" name="statusId" class="form-control status-select" required title="Vui lòng chọn trạng thái."><c:forEach var="status" items="${statusList}"><option value="${status.id}" <c:if test="${contract.statusId == status.id}">selected</c:if>>${status.name}</option></c:forEach></select></div>
                                        <div class="form-group"><label class="form-label" for="createdById">Nhân viên phụ trách (*)</label><select id="createdById" name="createdById" class="form-control" required title="Vui lòng chọn nhân viên phụ trách."><c:forEach var="e" items="${employeeList}"><option value="${e.id}" <c:if test="${contract.createdById == e.id}">selected</c:if>>${e.fullNameCombined}</option></c:forEach></select></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </main>
        </div>

        <%-- Modal for product selection --%>
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

        <script src="${pageContext.request.contextPath}/js/editContractDetail.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const form = document.getElementById('editContractForm');

                const showError = (input, message) => {
                    const parent = input.parentElement;
                    const error = parent.querySelector('.error-text');
                    input.classList.add('is-invalid');
                    if (error) {
                        error.textContent = message;
                        error.style.display = 'block';
                    }
                };
                const clearErrors = () => {
                    document.querySelectorAll('.is-invalid').forEach(input => input.classList.remove('is-invalid'));
                    document.querySelectorAll('.error-text').forEach(error => {
                        error.textContent = '';
                        error.style.display = 'none';
                    });
                };
                form.addEventListener('submit', function (event) {
                    event.preventDefault();
                    clearErrors();
                    let isCustomValid = true;
                    if (!form.checkValidity()) {
                        form.reportValidity();
                        return;
                    }
                    const signedDateInput = document.getElementById('signedDate');
                    const startDateInput = document.getElementById('startDate');
                    const endDateInput = document.getElementById('endDate');
                    const itemList = document.getElementById('contract-item-list');
                    const addProductBtn = document.getElementById('addProductBtn');
                    if (startDateInput.value && signedDateInput.value && startDateInput.value < signedDateInput.value) {
                        isCustomValid = false;
                        showError(startDateInput, 'Ngày hiệu lực không được trước ngày ký.');
                    }
                    if (endDateInput.value && startDateInput.value && endDateInput.value < startDateInput.value) {
                        isCustomValid = false;
                        showError(endDateInput, 'Ngày hết hạn không được trước ngày hiệu lực.');
                    }
                    if (itemList.children.length === 0) {
                        isCustomValid = false;
                        showError(addProductBtn, 'Hợp đồng phải có ít nhất một sản phẩm.');
                    }
                    if (isCustomValid) {
                        form.submit();
                    }
                });
            });
        </script>
        <script>feather.replace();</script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
