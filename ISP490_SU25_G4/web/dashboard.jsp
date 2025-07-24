<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Đặt trang hiện tại là 'dashboard' để menu được active đúng --%>
<c:set var="currentPage" value="dashboard" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bảng điều khiển - DPCRM</title>

        <%-- Các link CSS và script cần thiết --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">

        <%-- Dùng lại CSS của trang report cho các thẻ thống kê --%>
        <link rel="stylesheet" href="css/report.css"> 
        <style>
            /* Thêm vào thẻ <style> trong file dashboard.jsp */

            .quick-filters {
                display: flex;
                gap: 8px; /* Khoảng cách giữa các nút */
                align-items: center;
                margin-left: auto; /* Đẩy bộ lọc về phía bên phải */
                padding-right: 20px;
            }

            .quick-filter-btn {
                display: inline-block;
                padding: 8px 16px;
                border: 1px solid #dee2e6;
                border-radius: 20px; /* Bo tròn để tạo hình viên thuốc */
                background-color: #f8f9fa;
                color: #495057;
                font-size: 14px;
                font-weight: 500;
                text-decoration: none;
                transition: all 0.2s ease-in-out;
            }

            .quick-filter-btn:hover {
                border-color: #007bff;
                color: #007bff;
                background-color: #e7f1ff;
            }

            /* Đây là style cho nút đang được chọn */
            .quick-filter-btn.active {
                background-color: #007bff; /* Màu xanh dương chủ đạo */
                color: #ffffff; /* Chữ trắng */
                border-color: #007bff;
                box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <%-- Include Menu chính --%>
            <jsp:include page="mainMenu.jsp"/>

            <div class="content-wrapper">
                <%-- Header của trang --%>
                <header class="main-top-bar">
                    <div class="page-title">Bảng điều khiển</div>

                    <%-- THÊM BỘ LỌC NHANH VÀO ĐÂY --%>
                    <%-- Trong file dashboard.jsp, thay thế thẻ <form> cũ trong <div class="quick-filters"> --%>
                    <div class="quick-filters">
                        <a href="dashboard?period=last7days" class="quick-filter-btn ${period == 'last7days' || empty period ? 'active' : ''}">7 ngày qua</a>
                        <a href="dashboard?period=thismonth" class="quick-filter-btn ${period == 'thismonth' ? 'active' : ''}">Tháng này</a>
                        <a href="dashboard?period=lastmonth" class="quick-filter-btn ${period == 'lastmonth' ? 'active' : ''}">Tháng trước</a>
                        <a href="dashboard?period=thisyear" class="quick-filter-btn ${period == 'thisyear' ? 'active' : ''}">Năm nay</a>
                    </div>
                </header>

                <%-- Phần thân của nội dung --%>
                <section class="main-content-body">
                    <c:if test="${not empty errorMessage}">
                        <p style="color: red;">${errorMessage}</p>
                    </c:if>

                    <%-- Lưới hiển thị các thẻ thống kê --%>
                    <div class="report-grid">
                        <%-- Thẻ Doanh thu --%>
                        <div class="report-card">
                            <div class="report-card-header"><i data-feather="dollar-sign" class="icon"></i><h3>Doanh thu (${summaryPeriod})</h3></div>
                            <div class="report-card-body">
                                <div class="revenue-summary">
                                    <p class="total-revenue"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                                </div>
                            </div>
                        </div>

                        <%-- Thẻ Khách hàng --%>
                        <div class="report-card">
                            <div class="report-card-header"><i data-feather="users" class="icon"></i><h3>Thống kê Khách hàng</h3></div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item"><span class="label">Khách hàng mới (${summaryPeriod})</span><span class="value success">+ <fmt:formatNumber value="${newCustomers}"/></span></li>
                                    <li class="kpi-item"><span class="label">Tổng số khách hàng</span><span class="value"><fmt:formatNumber value="${totalCustomers}"/></span></li>
                                </ul>
                            </div>
                        </div>

                        <%-- Thẻ Hợp đồng --%>
                        <div class="report-card">
                            <div class="report-card-header"><i data-feather="briefcase" class="icon"></i><h3>Tình trạng Hợp đồng</h3></div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item"><span class="label">Đang hiệu lực</span><span class="value success"><fmt:formatNumber value="${contractStatus.active}"/></span></li>
                                    <li class="kpi-item"><span class="label">Sắp hết hạn</span><span class="value warning"><fmt:formatNumber value="${contractStatus.expiring}"/></span></li>
                                    <li class="kpi-item"><span class="label">Đã hết hạn</span><span class="value danger"><fmt:formatNumber value="${contractStatus.expired}"/></span></li>
                                </ul>
                            </div>
                        </div>

                        <%-- Thẻ Sửa chữa --%>
                        <div class="report-card">
                            <div class="report-card-header"><i data-feather="tool" class="icon"></i><h3>Yêu cầu sửa chữa (${summaryPeriod})</h3></div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item"><span class="label">Đã hoàn thành</span><span class="value success"><fmt:formatNumber value="${requestStatus.completed}"/></span></li>
                                    <li class="kpi-item"><span class="label">Đang tiến hành</span><span class="value warning"><fmt:formatNumber value="${requestStatus.in_progress}"/></span></li>
                                    <li class="kpi-item"><span class="label">Chờ xử lý</span><span class="value"><fmt:formatNumber value="${requestStatus.pending}"/></span></li>
                                </ul>
                            </div>
                        </div>

                        <%-- Thẻ Sản phẩm nổi bật --%>
                        <div class="report-card">
                            <div class="report-card-header"><i data-feather="package" class="icon"></i><h3>Sản phẩm nổi bật (${summaryPeriod})</h3></div>
                            <div class="report-card-body">
                                <ul class="product-list">
                                    <c:forEach var="product" items="${topProducts}" varStatus="loop">
                                        <li class="product-item">
                                            <span class="rank">#${loop.count}</span>
                                            <div class="info">
                                                <p class="name"><c:out value="${product.name}"/></p>
                                                <p class="sales"><c:out value="${product.sales}"/> lượt mua</p>
                                            </div>
                                        </li>
                                    </c:forEach>
                                    <c:if test="${empty topProducts}"><p style="text-align: center; color: #888;">Không có dữ liệu.</p></c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace(); // Kích hoạt icon
            });
        </script>
        <script src="js/mainMenu.js"></script>
    </body>
</html>