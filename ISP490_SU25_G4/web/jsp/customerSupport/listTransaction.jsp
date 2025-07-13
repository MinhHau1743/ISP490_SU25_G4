<%-- File: /view/customerSupport/listTransaction.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- CẬP NHẬT: Thêm thư viện JSTL format để định dạng ngày tháng --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch sử giao dịch</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- CẬP NHẬT: Sửa tất cả đường dẫn CSS để dùng contextPath --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listTransaction.css?v=<%= System.currentTimeMillis()%>"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <%-- CẬP NHẬT: Sửa đường dẫn để dùng contextPath --%>
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <div class="title">Lịch sử giao dịch</div>
                        <div class="breadcrumb">Yêu cầu / <span>Lịch sử giao dịch</span></div>
                    </div>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <%-- CẬP NHẬT: Sửa action của form và link "Tạo Phiếu" --%>
                        <form class="table-toolbar" action="${pageContext.request.contextPath}/ticket" method="get">
                            <input type="hidden" name="action" value="list">
                            <div class="search-box">
                                <i data-feather="search" class="feather-search"></i>
                                <input type="text" name="query" placeholder="Tìm kiếm theo mã phiếu, khách hàng...">
                            </div>
                            <%-- Phần lọc sẽ được phát triển sau --%>
                            <button type="submit" class="btn btn-secondary"><i data-feather="search"></i>Tìm kiếm</button>
                            <div class="toolbar-actions">
                                <a href="${pageContext.request.contextPath}/ticket?action=create" class="btn btn-primary"><i data-feather="plus"></i>Tạo Phiếu</a>
                            </div>
                        </form>

                        <div class="transaction-grid">
                            <c:if test="${empty transactions}">
                                <p style="grid-column: 1 / -1; text-align: center;">Không có giao dịch nào để hiển thị.</p>
                            </c:if>

                            <c:forEach var="tx" items="${transactions}">
                                <div class="transaction-card">
                                    <div class="card-header">
                                        <%-- CẬP NHẬT: Sử dụng requestCode từ model --%>
                                        <a href="${pageContext.request.contextPath}/ticket?action=view&id=${tx.id}" class="transaction-code-link">
                                            <span class="transaction-code">${tx.requestCode}</span>
                                        </a>

                                        <%-- CẬP NHẬT: Chuyển đổi status sang tiếng Việt và thêm class màu --%>
                                        <c:choose>
                                            <c:when test="${tx.status == 'new'}"><span class="status-pill status-new">Mới</span></c:when>
                                            <c:when test="${tx.status == 'assigned'}"><span class="status-pill status-assigned">Đã giao</span></c:when>
                                            <c:when test="${tx.status == 'in_progress'}"><span class="status-pill status-in-progress">Đang xử lý</span></c:when>
                                            <c:when test="${tx.status == 'resolved'}"><span class="status-pill status-resolved">Đã xử lý</span></c:when>
                                            <c:when test="${tx.status == 'closed'}"><span class="status-pill status-closed">Đã đóng</span></c:when>
                                            <c:when test="${tx.status == 'rejected'}"><span class="status-pill status-rejected">Từ chối</span></c:when>

                                            <c:otherwise><span class="status-pill">${tx.status}</span></c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-body">
                                        <div class="card-info-row">
                                            <i data-feather="briefcase"></i>
                                            <%-- CẬP NHẬT: Kiểm tra nếu hợp đồng có tồn tại --%>
                                            <span class="info-value">${not empty tx.contractCode ? tx.contractCode : 'Không có hợp đồng'}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="user"></i>
                                            <%-- CẬP NHẬT: Sử dụng enterpriseName từ model --%>
                                            <span class="info-value">${tx.enterpriseName}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="tool"></i>
                                            <%-- CẬP NHẬT: Sử dụng serviceName từ model --%>
                                            <span class="info-value">${tx.serviceName}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="calendar"></i>
                                            <%-- CẬP NHẬT: Định dạng lại ngày tạo cho đẹp hơn --%>
                                            <span class="info-value"><fmt:formatDate value="${tx.createdAt}" pattern="HH:mm dd/MM/yyyy" /></span>
                                        </div>
                                    </div>
                                    <div class="card-footer">
                                        <div class="billing-status">
                                            <%-- CẬP NHẬT: Sử dụng isIsBillable() theo model của bạn --%>
                                            <c:choose>
                                                <c:when test="${tx.isBillable}">
                                                    <i data-feather="dollar-sign" class="icon-billable" title="Có tính phí"></i>
                                                    <span class="cost-value">
                                                        <fmt:formatNumber value="${tx.estimatedCost}" type="number" maxFractionDigits="0"/> VND
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-feather="dollar-sign" class="icon-non-billable" title="Miễn phí (Bảo hành)"></i>
                                                    <%-- Thêm văn bản để làm rõ --%>
                                                    <span class="cost-value" style="color: #6c757d;">Miễn phí</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="action-buttons">
                                            <%-- Các link này sẽ được làm sau --%>
                                            <a href="${pageContext.request.contextPath}/ticket?action=view&id=${tx.id}" title="Xem chi tiết"><i data-feather="eye" class="icon-view"></i></a>

                                            <a href="${pageContext.request.contextPath}/ticket?action=edit&id=${tx.id}" title="Sửa"><i data-feather="edit-2" class="icon-edit"></i></a>
                                            <a href="#" onclick="return confirm('Xóa giao dịch ${tx.requestCode}?')" title="Xóa"><i data-feather="trash-2" class="icon-delete"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <%-- <jsp:include page="/view/pagination.jsp" /> --%>
                    </div>
                </div>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
