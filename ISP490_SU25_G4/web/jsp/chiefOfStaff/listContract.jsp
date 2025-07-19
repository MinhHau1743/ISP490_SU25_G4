<%--
    Document   : listContract.jsp
    Created on : Jun 25, 2025, 11:20:00 PM
    Author     : NGUYEN MINH (Simplified version as of Jul 07, 2025)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<c:set var="currentPageJsp" value="listContract" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách Hợp đồng</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listContract.css">
    </head>
    <body>
        <div class="app-container">
            <%-- Phần menu chính --%>
            <jsp:include page="../../mainMenu.jsp"/>

            <main class="main-content">
                <div class="page-content" style="padding-bottom: 0;">
                    <%-- Đặt ở đầu file listContract.jsp --%>
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success">
                            <i data-feather="check-circle"></i>
                            <span>${sessionScope.successMessage}</span>
                        </div>
                        <c:remove var="successMessage" scope="session" />
                    </c:if>

                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger">
                            <i data-feather="alert-triangle"></i>
                            <span>${sessionScope.errorMessage}</span>
                        </div>
                        <c:remove var="errorMessage" scope="session" />
                    </c:if>
                </div>
                <header class="page-header">
                    <div class="title-section">
                        <h1 class="title">Danh sách Hợp đồng</h1>
                        
                    </div>
                    <div class="header-actions">
                        <button class="notification-btn"><i data-feather="bell"></i></button>
                    </div>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <form action="listContract" method="get">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <input type="text" name="searchQuery" placeholder="Tìm mã, tên hợp đồng..." value="${searchQuery}">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn"><i data-feather="filter"></i><span>Bộ lọc</span></button>
                                <div class="toolbar-actions">
                                    <%-- ========================================================== --%>
                                    <%-- Bắt đầu phân quyền nút "Tạo Hợp đồng" --%>
                                    <c:choose>
                                        <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                            <a href="${pageContext.request.contextPath}/createContract" class="btn btn-primary"><i data-feather="plus"></i>Tạo Hợp đồng</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#" class="btn btn-primary disabled-action" data-error="Bạn không có quyền tạo hợp đồng mới."><i data-feather="plus"></i>Tạo Hợp đồng</a>
                                        </c:otherwise>
                                    </c:choose>
                                    <%-- Kết thúc phân quyền nút "Tạo Hợp đồng" --%>
                                    <%-- ========================================================== --%>
                                </div>
                            </div>

                            <%-- Vùng chứa bộ lọc --%>
                            <div class="filter-container" id="filterContainer">
                                <div class="filter-controls">
                                    <div class="filter-group">
                                        <label for="status-filter">Trạng thái</label>
                                        <select id="status-filter" name="status">
                                            <option value="">Tất cả</option>
                                            <option value="pending" ${status == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                            <option value="active" ${status == 'active' ? 'selected' : ''}>Còn hiệu lực</option>
                                            <option value="expiring" ${status == 'expiring' ? 'selected' : ''}>Sắp hết hạn</option>
                                            <option value="expired" ${status == 'expired' ? 'selected' : ''}>Đã hết hạn</option>
                                            <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label>Ngày hiệu lực</label>
                                        <div class="date-inputs">
                                            <input type="date" name="startDateFrom" value="${startDateFrom}">
                                            <input type="date" name="startDateTo" value="${startDateTo}">
                                        </div>
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <a href="listContract" class="btn-reset-filter">Xóa lọc</a>
                                    <button type="submit" class="btn-apply-filter">Áp dụng</button>
                                </div>
                            </div>
                        </form>

                        <div class="data-table-container">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Mã Hợp đồng</th>
                                        <th>Tên Hợp đồng</th>
                                        <th>Khách hàng</th>
                                        <th>Ngày hiệu lực</th>
                                        <th>Ngày hết hạn</th>
                                        <th>Giá trị</th>
                                        <th>Trạng thái</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty contractList}">
                                        <tr>
                                            <td colspan="8" style="text-align: center; padding: 40px;">Không có hợp đồng nào phù hợp.</td>
                                        </tr>
                                    </c:if>

                                    <%-- SỬA LỖI: Thêm các thẻ <c:if> để kiểm tra giá trị null trước khi định dạng --%>
                                    <c:forEach var="contract" items="${contractList}">
                                        <tr>
                                            <td><%-- ========================================================== --%>
                                                <%-- Bắt đầu phân quyền link "Mã Hợp đồng" --%>
                                                <c:choose>
                                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                        <a href="listContract?action=view&id=${contract.id}" class="contract-code">${contract.contractCode}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="contract-code">${contract.contractCode}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <%-- Kết thúc phân quyền link "Mã Hợp đồng" --%>
                                                <%-- ========================================================== --%>
                                            </td>
                                            <td>${contract.contractName}</td>
                                            <td class="customer-name">${contract.enterpriseName}</td>
                                            <td>
                                                <c:if test="${not empty contract.startDate}">
                                                    <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:if test="${not empty contract.endDate}">
                                                    <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/>
                                                </c:if>
                                            </td>
                                            <td class="contract-value">
                                                <c:if test="${not empty contract.totalValue}">
                                                    <fmt:formatNumber value="${contract.totalValue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${contract.status == 'active'}"><span class="status-pill status-active">Còn hiệu lực</span></c:when>
                                                    <c:when test="${contract.status == 'pending'}"><span class="status-pill status-pending">Chờ duyệt</span></c:when>
                                                    <c:when test="${contract.status == 'expiring'}"><span class="status-pill status-expiring">Sắp hết hạn</span></c:when>
                                                    <c:when test="${contract.status == 'expired'}"><span class="status-pill status-expired">Đã hết hạn</span></c:when>
                                                    <c:when test="${contract.status == 'cancelled'}"><span class="status-pill status-cancelled">Đã hủy</span></c:when>
                                                    <c:otherwise><span class="status-pill">${contract.status}</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="table-actions">
                                                <%-- ========================================================== --%>
                                                <%-- Bắt đầu phân quyền các nút hành động --%>
                                                <c:choose>
                                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                        <a href="${pageContext.request.contextPath}/viewContract?id=${contract.id}" title="Xem"><i data-feather="eye"></i></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <a href="#" class="disabled-action" data-error="Bạn không có quyền xem chi tiết hợp đồng." title="Xem"><i data-feather="eye"></i></a>
                                                        </c:otherwise>
                                                    </c:choose>

                                                <c:choose>
                                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                        <a href="${pageContext.request.contextPath}/editContract?id=${contract.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <a href="#" class="disabled-action" data-error="Bạn không có quyền sửa hợp đồng." title="Sửa"><i data-feather="edit-2"></i></a>
                                                        </c:otherwise>
                                                    </c:choose>

                                                <c:choose>
                                                    <c:when test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                        <button type="button" class="delete-btn" 
                                                                data-id="${contract.id}" 
                                                                data-name="${contract.contractCode}" 
                                                                title="Xóa">
                                                            <i data-feather="trash-2"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="disabled-action" data-error="Bạn không có quyền xóa hợp đồng." title="Xóa">
                                                            <i data-feather="trash-2"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                                <%-- Kết thúc phân quyền các nút hành động --%>
                                                <%-- ========================================================== --%>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <%-- Phần phân trang --%>
                        <jsp:include page="../../pagination.jsp"/>
                    </div>
                </div>
                <%-- =============================================== --%>
                <%-- MODAL XÁC NHẬN XÓA                             --%>
                <%-- =============================================== --%>
                <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
                    <div class="modal-content" style="max-width: 420px;">
                        <div class="modal-header">
                            <h3 class="modal-title">Xác nhận xóa</h3>
                            <button type="button" class="close-modal-btn"><i data-feather="x"></i></button>
                        </div>
                        <div class="modal-body" style="text-align: center; padding: 24px;">
                            <p id="deleteMessage">Bạn có chắc chắn muốn xóa mục này?</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary close-modal-btn">Hủy</button>
                            <a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <%-- ========================================================== --%>
        <%-- Thêm Script xử lý thông báo lỗi (chỉ cần thêm 1 lần) --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                document.body.addEventListener('click', function (event) {
                    // Tìm phần tử cha gần nhất có class 'disabled-action'
                    const disabledAction = event.target.closest('.disabled-action');
                    if (disabledAction) {
                        event.preventDefault(); // Ngăn hành động mặc định
                        const errorMessage = disabledAction.getAttribute('data-error') || 'Bạn không có quyền thực hiện chức năng này.';
                        alert(errorMessage); // Hiển thị thông báo
                    }
                });
            });
        </script>
        <%-- ========================================================== --%>
        <script src="${pageContext.request.contextPath}/js/listContract.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>