<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="dashboard" />
<c:set var="user" value="${sessionScope.user}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <%-- Phần head giữ nguyên --%>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bảng điều khiển</title>
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/report.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            .quick-filters {
                display: flex;
                gap: 8px;
                align-items: center;
                margin-bottom: 24px;
            }
            .quick-filter-btn {
                display: inline-block;
                padding: 8px 16px;
                border: 1px solid #dee2e6;
                border-radius: 20px;
                background-color: #f8f9fa;
                color: #495057;
                font-size: 14px;
                font-weight: 500;
                text-decoration: none;
                transition: all 0.2s ease-in-out;
            }
            .quick-filter-btn:hover {
                border-color: var(--primary-color);
                color: var(--primary-color);
                background-color: var(--primary-color-light);
            }
            .quick-filter-btn.active {
                background-color: var(--primary-color);
                color: #ffffff;
                border-color: var(--primary-color);
            }
            .welcome-header {
                margin-bottom: 24px;
            }
            .welcome-header h1 {
                font-size: 24px;
                font-weight: 600;
                color: #212529;
                margin: 0;
            }
            .welcome-header p {
                font-size: 16px;
                color: #6c757d;
                margin-top: 4px;
            }
            .dashboard-grid {
                display: grid;
                gap: 24px;
                grid-template-columns: repeat(4, 1fr);
            }
            .main-chart-card {
                grid-column: 1 / -1;
                height: 350px;
            }
            .secondary-chart-card {
                grid-column: span 2;
                height: 350px;
            }
            .no-data-message {
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100%;
                color: #868e96;
                font-size: 14px;
                text-align: center;
                padding: 20px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>
            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="pageTitle" value="Bảng điều khiển"/>
                </jsp:include>
                <section class="main-content-body">
                    <%-- Phần filter và welcome header giữ nguyên --%>
                    <div class="quick-filters">
                        <a href="dashboard?period=last7days" class="quick-filter-btn ${selectedPeriod == 'last7days' ? 'active' : ''}">7 ngày qua</a>
                        <a href="dashboard?period=thismonth" class="quick-filter-btn ${selectedPeriod == 'thismonth' ? 'active' : ''}">Tháng này</a>
                        <a href="dashboard?period=lastmonth" class="quick-filter-btn ${selectedPeriod == 'lastmonth' ? 'active' : ''}">Tháng trước</a>
                        <a href="dashboard?period=thisyear" class="quick-filter-btn ${selectedPeriod == 'thisyear' ? 'active' : ''}">Năm nay</a>
                    </div>

                    <div class="welcome-header">
                        <h1>Chào mừng quay trở lại, <c:out value="${user.firstName} ${user.lastName}"/>!</h1>
                        <p>Đây là tổng quan nhanh về hoạt động của bạn. Dữ liệu được tính cho: <strong>${summaryPeriod}</strong></p>
                    </div>

                    <div class="dashboard-grid">
                        <div class="report-card">
                            <div class="report-card-header"><h3>Tổng Doanh Thu</h3></div>
                            <div class="report-card-body"><span class="kpi-value"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span></div>
                        </div>
                        <div class="report-card">
                            <div class="report-card-header"><h3>Khách Hàng Mới</h3></div>
                            <div class="report-card-body"><span class="kpi-value">+<fmt:formatNumber value="${newCustomers}"/></span></div>
                        </div>

                        <%-- ================== THAY ĐỔI TẠI ĐÂY ================== --%>
                        <div class="report-card">
                            <div class="report-card-header"><h3>Yêu Cầu KT Hoàn Thành</h3></div>
                            <div class="report-card-body"><span class="kpi-value"><fmt:formatNumber value="${completedRequests}"/></span></div>
                        </div>
                        <div class="report-card">
                            <div class="report-card-header"><h3>Chiến Dịch Hoàn Thành</h3></div>
                            <div class="report-card-body"><span class="kpi-value"><fmt:formatNumber value="${completedCampaigns}"/></span></div>
                        </div>
                        <%-- ======================================================= --%>

                        <%-- Các biểu đồ giữ nguyên --%>
                        <div class="report-card main-chart-card">
                            <div class="report-card-header"><h3>Xu hướng Doanh thu</h3></div>
                            <div class="report-card-body">
                                <c:choose>
                                    <c:when test="${not empty revenueTrendJson and revenueTrendJson ne '[]'}"><canvas id="revenueTrendChart"></canvas></c:when>
                                    <c:otherwise><div class="no-data-message"><p>Không có dữ liệu doanh thu.</p></div></c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="report-card secondary-chart-card">
                            <div class="report-card-header"><h3>Top sản phẩm theo số lượng</h3></div>
                            <div class="report-card-body">
                                <c:choose>
                                    <c:when test="${not empty topProductsJson and topProductsJson ne '[]'}"><canvas id="topProductsChart"></canvas></c:when>
                                    <c:otherwise><div class="no-data-message"><p>Không có dữ liệu sản phẩm.</p></div></c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="report-card secondary-chart-card">
                            <div class="report-card-header"><h3>Tình trạng Hợp đồng</h3></div>
                            <div class="report-card-body">
                                <c:choose>
                                    <c:when test="${not empty contractStatusCountsJson and contractStatusCountsJson ne '{}'}"><canvas id="contractStatusChart"></canvas></c:when>
                                    <c:otherwise><div class="no-data-message"><p>Không có dữ liệu hợp đồng.</p></div></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
        </div>

        <%-- Phần script cuối trang giữ nguyên --%>
        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            const revenueTrendData = JSON.parse('${revenueTrendJson}');
            const topProductsData = JSON.parse('${topProductsJson}');
            const contractStatusData = JSON.parse('${contractStatusCountsJson}');

            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
                if (document.getElementById('revenueTrendChart') && revenueTrendData.length > 0) {
                    const revenueCtx = document.getElementById('revenueTrendChart').getContext('2d');
                    new Chart(revenueCtx, {type: 'line', data: {labels: revenueTrendData.map(d => new Date(d.date).toLocaleDateString('vi-VN')), datasets: [{label: 'Doanh thu', data: revenueTrendData.map(d => d.revenue), borderColor: 'rgba(13, 148, 136, 1)', backgroundColor: 'rgba(13, 148, 136, 0.1)', tension: 0.3, fill: true, borderWidth: 2}]}, options: {responsive: true, maintainAspectRatio: false, scales: {y: {beginAtZero: true, ticks: {callback: value => new Intl.NumberFormat('vi-VN').format(value)}}}, plugins: {legend: {display: false}, tooltip: {callbacks: {label: context => 'Doanh thu: ' + new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(context.parsed.y)}}}}});
                }

                if (document.getElementById('topProductsChart') && topProductsData.length > 0) {
                    const productCtx = document.getElementById('topProductsChart').getContext('2d');
                    new Chart(productCtx, {type: 'bar', data: {labels: topProductsData.map(p => p.name), datasets: [{label: 'Số lượng bán', data: topProductsData.map(p => p.sales), backgroundColor: ['#0d9488', '#0d7a6e', '#0a6055', '#08463c', '#062c23']}]}, options: {responsive: true, maintainAspectRatio: false, indexAxis: 'y', plugins: {legend: {display: false}}}});
                }

                if (document.getElementById('contractStatusChart') && Object.keys(contractStatusData).length > 0) {
                    const statusCtx = document.getElementById('contractStatusChart').getContext('2d');
                    new Chart(statusCtx, {type: 'doughnut', data: {labels: Object.keys(contractStatusData), datasets: [{data: Object.values(contractStatusData), backgroundColor: ['#28a745', '#ffc107', '#dc3545', '#6c757d', '#17a2b8', '#fd7e14']}]}, options: {responsive: true, maintainAspectRatio: false, plugins: {legend: {position: 'right'}}}});
                }
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>