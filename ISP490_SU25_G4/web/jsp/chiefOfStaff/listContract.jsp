<%--
    Document   : listContract.jsp
    Created on : Jun 25, 2025, 11:20:00 PM
    Author     : NGUYEN MINH (Final version as of Jun 30, 2025)
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
            <%-- Phần menu chính của bạn --%>
            <jsp:include page="../../mainMenu.jsp"/>
            
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <h1 class="title">Danh sách Hợp đồng</h1>
                        <div class="breadcrumb">Hợp đồng<span>/</span><span>Danh sách</span></div>
                    </div>
                    <div class="header-actions">
                        <button class="notification-btn"><i data-feather="bell"></i></button>
                    </div>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <form action="contract" method="get">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <input type="text" name="searchQuery" placeholder="Tìm mã, tên hợp đồng..." value="${param.searchQuery}">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn"><i data-feather="filter"></i><span>Bộ lọc</span></button>
                                <div class="toolbar-actions">
                                    <%-- Nút "Tạo Hợp đồng" điều hướng trực tiếp đến file jsp --%>
                                   <a href="${pageContext.request.contextPath}/jsp/chiefOfStaff/createContract.jsp" class="btn btn-primary"><i data-feather="plus"></i>Tạo Hợp đồng</a>
                                </div>
                            </div>
                            
                            <%-- Vùng chứa bộ lọc, được bật/tắt bởi JavaScript --%>
                            <div class="filter-container" id="filterContainer">
                                <div class="filter-controls">
                                    <div class="filter-group">
                                        <label for="status-filter">Trạng thái</label>
                                        <select id="status-filter" name="status">
                                            <option value="">Tất cả</option>
                                            <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                            <option value="active" ${param.status == 'active' ? 'selected' : ''}>Còn hiệu lực</option>
                                            <option value="expiring" ${param.status == 'expiring' ? 'selected' : ''}>Sắp hết hạn</option>
                                            <option value="expired" ${param.status == 'expired' ? 'selected' : ''}>Đã hết hạn</option>
                                            <option value="cancelled" ${param.status == 'cancelled' ? 'selected' : ''}>Đã hủy</option>
                                        </select>
                                    </div>

                                    <div class="filter-group">
                                        <label>Ngày hiệu lực (từ `start_date`)</label>
                                        <div class="date-inputs">
                                            <input type="date" name="startDateFrom" value="${param.startDateFrom}">
                                            <input type="date" name="startDateTo" value="${param.startDateTo}">
                                        </div>
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <a href="contract" class="btn-reset-filter">Xóa lọc</a>
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
                                    <%-- Hiển thị thông báo nếu không có dữ liệu --%>
                                    <c:if test="${empty contractList}">
                                        <tr>
                                            <td colspan="8" style="text-align: center; padding: 40px;">Không có hợp đồng nào phù hợp.</td>
                                        </tr>
                                    </c:if>

                                    <%-- Lặp qua danh sách hợp đồng do Servlet cung cấp --%>
                                    <c:forEach var="contract" items="${contractList}">
                                        <tr>
                                            <td><a href="contract?action=view&id=${contract.id}" class="contract-code">${contract.contractCode}</a></td>
                                            <td>${contract.contractName}</td>
                                            <%-- Giả định đối tượng enterprise được lồng trong contract --%>
                                            <td class="customer-name">${contract.enterprise.name}</td>
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
                                                    <c:otherwise><span class="status-pill">${contract.status}</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="table-actions">
                                                <a href="contract?action=view&id=${contract.id}" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="contract?action=edit&id=${contract.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <%-- Gọi hàm JS để xác nhận xóa --%>
                                                <a href="#" onclick="confirmDelete('${contract.id}', '${contract.contractCode}')" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        
                        <%-- Phần phân trang của bạn --%>
                        <jsp:include page="../../pagination.jsp"/>
                    </div>
                </div>
            </main>
        </div>
        
        <script src="${pageContext.request.contextPath}/js/listContract.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>