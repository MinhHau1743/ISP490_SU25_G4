<%-- File: /view/customerSupport/listTransaction.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listTransaction.css?v=<%= System.currentTimeMillis()%>"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">

                <%-- **THAY ĐỔI:** Thêm header mới vào đây --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Lịch sử Giao dịch"/>
                </jsp:include>

                <div class="page-content">
                    <c:if test="${param.feedback == 'success'}">
                        <div class="alert alert-success">
                            <i data-feather="star"></i>
                            <span>Phản hồi của khách hàng đã được ghi nhận thành công.</span>
                        </div>
                    </c:if>
                    <div class="content-card">
                        <form class="table-toolbar" action="${pageContext.request.contextPath}/ticket" method="get">
                            <input type="hidden" name="action" value="list">
                            <div class="search-group">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <input type="text" name="query" placeholder="Tìm theo mã phiếu, khách hàng..." value="${param.query}">
                                </div>
                                <button type="submit" class="btn btn-primary"><i data-feather="search"></i> Tìm kiếm</button>
                            </div>
                            <div class="toolbar-actions">
                                <a href="${pageContext.request.contextPath}/ticket?action=list" class="btn btn-secondary">
                                    <i data-feather="refresh-cw"></i> Reset
                                </a>
                            </div>
                        </form>

                        <div class="transaction-grid">
                            <c:if test="${empty transactions}">
                                <p style="grid-column: 1 / -1; text-align: center;">Không có giao dịch nào để hiển thị.</p>
                            </c:if>

                            <c:forEach var="tx" items="${transactions}">
                                <div class="transaction-card">
                                    <div class="card-header">
                                        <a href="${pageContext.request.contextPath}/ticket?action=view&id=${tx.id}" class="transaction-code-link">
                                            <span class="transaction-code">${tx.requestCode}</span>
                                        </a>
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
                                            <span class="info-value">${not empty tx.contractCode ? tx.contractCode : 'Không có hợp đồng'}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="user"></i>
                                            <span class="info-value">${tx.enterpriseName}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="tool"></i>
                                            <span class="info-value">${tx.serviceName}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="calendar"></i>
                                            <span class="info-value"><fmt:formatDate value="${tx.createdAt}" pattern="HH:mm dd/MM/yyyy" /></span>
                                        </div>
                                    </div>
                                    <div class="card-footer">
                                        <div class="billing-status">
                                            <c:choose>
                                                <c:when test="${tx.isBillable}">
                                                    <i data-feather="dollar-sign" class="icon-billable" title="Có tính phí"></i>
                                                    <span class="cost-value">
                                                        <fmt:formatNumber value="${tx.estimatedCost}" type="number" maxFractionDigits="0"/> VND
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <i data-feather="dollar-sign" class="icon-non-billable" title="Miễn phí (Bảo hành)"></i>
                                                    <span class="cost-value" style="color: #6c757d;">Miễn phí</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/ticket?action=view&id=${tx.id}" title="Xem chi tiết"><i data-feather="eye" class="icon-view"></i></a>
                                            <a href="${pageContext.request.contextPath}/ticket?action=edit&id=${tx.id}" title="Sửa"><i data-feather="edit-2" class="icon-edit"></i></a>
                                            <a href="javascript:void(0);" class="delete-link" data-id="${tx.id}" data-name="${tx.requestCode}" title="Xóa">
                                                <i data-feather="trash-2" class="icon-delete"></i>
                                            </a>                                        
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${totalPages > 1}">
                                <div class="pagination">
                                    <c:if test="${currentPage > 1}"><a href="${pageContext.request.contextPath}/ticket?action=list&page=${currentPage - 1}&query=${param.query}">&laquo;</a></c:if>
                                    <c:if test="${currentPage == 1}"><a href="#" class="disabled">&laquo;</a></c:if>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${currentPage eq i}"><a href="#" class="active">${i}</a></c:when>
                                            <c:otherwise><a href="${pageContext.request.contextPath}/ticket?action=list&page=${i}&query=${param.query}">${i}</a></c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    <c:if test="${currentPage < totalPages}"><a href="${pageContext.request.contextPath}/ticket?action=list&page=${currentPage + 1}&query=${param.query}">&raquo;</a></c:if>
                                    <c:if test="${currentPage == totalPages}"><a href="#" class="disabled">&raquo;</a></c:if>
                                    </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <div class="modal-overlay" id="delete-confirm-modal">
            <div class="modal-content">
                <div class="modal-icon"><i data-feather="alert-triangle"></i></div>
                <h3 class="modal-title">Xác nhận xóa</h3>
                <p class="modal-message">Bạn có chắc chắn muốn xóa phiếu yêu cầu <br> <strong id="item-to-delete-name" style="color: #d32f2f; font-size: 1.1em;"></strong>?</p>
                <div class="modal-actions">
                    <button class="modal-btn btn-cancel" id="cancel-delete-btn">Hủy</button>
                    <button class="modal-btn btn-confirm-delete" id="confirm-delete-btn">Xóa</button>
                </div>
            </div>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script>
            window.APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
        </script>
        <script src="${pageContext.request.contextPath}/js/listTransaction.js"></script>
    </body>
</html>
