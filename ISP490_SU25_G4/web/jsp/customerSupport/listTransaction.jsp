<%-- 
    Document   : listTransaction
    Created on : Jun 14, 2025, 1:33:23 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentPage" value="listTransaction" />


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
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/listTransaction.css"> 
        <link rel="stylesheet" href="../../css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <div class="title">Lịch sử giao dịch</div>
                        <div class="breadcrumb">Hóa đơn / <span>Lịch sử giao dịch</span></div>
                    </div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <form class="table-toolbar" action="your-servlet-url-for-filtering" method="get">
                            <div class="search-box">
                                <i data-feather="search" class="feather-search"></i>
                                <input type="text" name="query" placeholder="Tìm kiếm...">
                            </div>
                            <div class="filter-group">
                                <label for="type-filter">Loại giao dịch</label>
                                <select id="type-filter" name="type">
                                    <option value="">Tất cả</option>
                                    <option value="Hỗ trợ sự cố">Hỗ trợ sự cố</option>
                                    <option value="Bảo trì định kỳ">Bảo trì định kỳ</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="status-filter">Trạng thái</label>
                                <select id="status-filter" name="status">
                                    <option value="">Tất cả</option>
                                    <option value="completed">Đã hoàn thành</option>
                                    <option value="processing">Đang xử lý</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-secondary"><i data-feather="search"></i>Tìm kiếm</button>
                            <div class="toolbar-actions">
                                <a href="createTicket.jsp" class="btn btn-primary"><i data-feather="plus"></i>Tạo Phiếu</a>
                            </div>
                        </form>

                        <div class="transaction-grid">
                            <c:if test="${empty transactions}">
                                <p style="grid-column: 1 / -1; text-align: center;">Không có giao dịch nào để hiển thị.</p>
                            </c:if>

                            <%-- Vòng lặp JSTL để hiển thị dữ liệu thật từ database --%>
                            <c:forEach var="tx" items="${transactions}">
                                <div class="transaction-card">
                                    <div class="card-header">
                                        <a href="../customerSupport/viewTransaction.jsp?id=${tx.id}" class="transaction-code-link">
                                            <span class="transaction-code">${tx.transactionCode}</span>
                                        </a>
                                        <span class="status-pill ${tx.statusClass}">${tx.status}</span>
                                    </div>
                                    <div class="card-body">
                                        <div class="card-info-row">
                                            <i data-feather="briefcase"></i>
                                            <span class="info-value">${tx.contractCode}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="user"></i>
                                            <span class="info-value">${tx.customer}</span>
                                        </div>
                                        <div class="card-info-row">
                                            <i data-feather="tool"></i>
                                            <span class="info-value">${tx.type}</span>
                                        </div>
                                    </div>
                                    <div class="card-footer">
                                        <div class="billing-status">
                                            <c:if test="${tx.isBillable}">
                                                <i data-feather="dollar-sign" class="icon-billable" title="Có tính phí"></i>
                                            </c:if>
                                            <c:if test="${not tx.isBillable}">
                                                <i data-feather="dollar-sign" class="icon-non-billable" title="Miễn phí (Bảo hành/SLA)"></i>
                                            </c:if>
                                        </div>
                                        <div class="action-buttons">
                                            <a href="../customerSupport/viewTransaction.jsp?id=${tx.id}" title="Xem chi tiết"><i data-feather="eye" class="icon-view"></i></a>
                                            <a href="../customerSupport/editTransaction?id=${tx.id}" title="Sửa"><i data-feather="edit-2" class="icon-edit"></i></a>
                                            <a href="deleteTransaction?id=${tx.id}" onclick="return confirm('Xóa giao dịch ${tx.transactionCode}?')" title="Xóa"><i data-feather="trash-2" class="icon-delete"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <jsp:include page="../../pagination.jsp" />
                    </div>
                </div>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="../../js/mainMenu.js"></script>
        <script src="../../js/editProfile.js"></script>
    </body>
</html>

