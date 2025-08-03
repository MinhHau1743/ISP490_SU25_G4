<%--
    Document   : listContract.jsp
    Description: Trang danh sách hợp đồng đã được cập nhật.
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
    <body>
        <div class="app-container">
            <%-- **SỬA LỖI:** Sử dụng đường dẫn tuyệt đối từ gốc web --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">

                <%-- **THAY ĐỔI:** Thêm header mới vào đây --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Danh sách Hợp đồng"/>
                </jsp:include>

                <div class="page-content">
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
                        <form action="listContract" method="get" id="filterForm" autocomplete="off">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <input type="text" name="searchQuery" placeholder="Tìm mã, tên hợp đồng..." value="${searchQuery}" autocomplete="off">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn"><i data-feather="filter"></i><span>Bộ lọc</span></button>
                                <div class="toolbar-actions">
                                    <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                        <a href="${pageContext.request.contextPath}/createContract" class="btn btn-primary"><i data-feather="plus"></i>Tạo Hợp đồng</a>
                                    </c:if>
                                </div>
                            </div>

                            <div class="filter-container" id="filterContainer">
                                <%-- Nội dung bộ lọc của bạn... --%>
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
                                            <td>
                                                <a href="${pageContext.request.contextPath}/viewContract?id=${contract.id}" class="contract-code">${contract.contractCode}</a>
                                            </td>
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
                                                <a href="${pageContext.request.contextPath}/viewContract?id=${contract.id}" title="Xem"><i data-feather="eye"></i></a>
                                                    <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Chánh văn phòng'}">
                                                    <a href="${pageContext.request.contextPath}/editContract?id=${contract.id}" title="Sửa"><i data-feather="edit-2"></i></a>
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
                    <%-- Nội dung modal xác nhận xóa --%>
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
