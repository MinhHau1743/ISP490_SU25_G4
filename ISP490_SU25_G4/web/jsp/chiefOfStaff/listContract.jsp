<%--
    Document   : listContract.jsp
    Description: Trang danh sách hợp đồng đã được cập nhật logic phân quyền.
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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listContract.css">
    </head>
    <body data-context-path="${pageContext.request.contextPath}">
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Danh sách Hợp đồng"/>
                </jsp:include>

                <div class="page-content">
                    <%-- Phần hiển thị và xóa thông báo --%>
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

                    <div class="content-card">
                        <form action="contract" method="get" id="filterForm" autocomplete="off">
                            <input type="hidden" name="action" value="list">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <input type="text" name="searchQuery" placeholder="Tìm mã, tên hợp đồng..." value="${searchQuery}" autocomplete="off">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn"><i data-feather="filter"></i><span>Bộ lọc</span></button>
                                <div class="toolbar-actions">

                                    <%-- ======================================================= --%>
                                    <%-- PHÂN QUYỀN CHO NÚT "TẠO HỢP ĐỒNG"                      --%>
                                    <%-- ======================================================= --%>
                                    <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                        <a href="${pageContext.request.contextPath}/contract?action=create" class="btn btn-primary"><i data-feather="plus"></i>Tạo Hợp đồng</a>
                                    </c:if>

                                </div>
                            </div>

                            <div class="filter-container" id="filterContainer" style="display: none;">
                                <div class="filter-grid">
                                    <div class="filter-item">
                                        <label for="status">Trạng thái</label>
                                        <select name="status" id="status">
                                            <option value="">Tất cả trạng thái</option>
                                            <option value="active" ${status == 'active' ? 'selected' : ''}>Còn hiệu lực</option>
                                            <option value="pending" ${status == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                            <option value="expiring" ${status == 'expiring' ? 'selected' : ''}>Sắp hết hạn</option>
                                            <option value="expired" ${status == 'expired' ? 'selected' : ''}>Đã hết hạn</option>
                                            <option value="cancelled" ${status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>
                                    <div class="filter-item">
                                        <label for="startDateFrom">Ngày hiệu lực từ</label>
                                        <input type="date" name="startDateFrom" id="startDateFrom" value="${startDateFrom}">
                                    </div>
                                    <div class="filter-item">
                                        <label for="startDateTo">Đến ngày</label>
                                        <input type="date" name="startDateTo" id="startDateTo" value="${startDateTo}">
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-primary">Áp dụng</button>
                                    <a href="${pageContext.request.contextPath}/contract?action=list" class="btn btn-secondary">Xóa bộ lọc</a>
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
                                    <c:forEach var="contract" items="${contractList}">
                                        <tr>
                                            <td><a href="${pageContext.request.contextPath}/contract?action=view&id=${contract.id}" class="contract-code">${contract.contractCode}</a></td>
                                            <td>${contract.contractName}</td>
                                            <td class="customer-name">${contract.enterpriseName}</td>
                                            <td><fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/></td>
                                            <td><fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/></td>
                                            <td class="contract-value"><fmt:formatNumber value="${contract.totalValue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${contract.status == 'active'}"><span class="status-pill status-active">Còn hiệu lực</span></c:when>
                                                    <c:when test="${contract.status == 'pending'}"><span class="status-pill status-pending">Chờ duyệt</span></c:when>
                                                    <c:when test="${contract.status == 'expiring'}"><span class="status-pill status-expiring">Sắp hết hạn</span></c:when>
                                                    <c:when test="${contract.status == 'expired'}"><span class="status-pill status-expired">Đã hết hạn</span></c:when>
                                                    <c:when test="${contract.status == 'cancelled'}"><span class="status-pill status-cancelled">Đã hủy</span></c:when>
                                                </c:choose>
                                            </td>
                                            <td class="table-actions">
                                                <%-- Nút Xem luôn hiển thị --%>
                                                <a href="${pageContext.request.contextPath}/contract?action=view&id=${contract.id}" title="Xem"><i data-feather="eye"></i></a>

                                                <%-- ======================================================= --%>
                                                <%-- PHÂN QUYỀN CHO CÁC NÚT SỬA VÀ XÓA                     --%>
                                                <%-- ======================================================= --%>
                                                <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                    <a href="${pageContext.request.contextPath}/contract?action=edit&id=${contract.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <button type="button" class="delete-btn" data-id="${contract.id}" data-name="${contract.contractCode}" title="Xóa">
                                                        <i data-feather="trash-2"></i>
                                                    </button>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <jsp:include page="/pagination.jsp"/>
                    </div>
                </div>

                <div id="deleteConfirmModal" class="modal-overlay" style="display:none;">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h3 class="modal-title">Xác nhận Xóa</h3>
                            <button class="modal-close" id="modalCloseBtn">&times;</button>
                        </div>
                        <div class="modal-body">
                            <p>Bạn có chắc chắn muốn xóa hợp đồng <strong id="contractNameToDelete"></strong> không? Hành động này không thể hoàn tác.</p>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-secondary" id="modalCancelBtn">Hủy</button>
                            <a href="#" class="btn btn-danger" id="modalConfirmDeleteBtn">Xác nhận Xóa</a>
                        </div>
                    </div>
                </div>

            </main>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/listContract.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>