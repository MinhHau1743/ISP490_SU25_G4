<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Đặt trang hiện tại là 'dashboard' để menu được active đúng --%>
<c:set var="currentPage" value="dashboard" />
<c:set var="user" value="${sessionScope.user}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển - DPCRM</title>

    <%-- Các link CSS và font --%>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <%-- Các file CSS chung --%>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/header.css">
    <link rel="stylesheet" href="css/mainMenu.css">
    <link rel="stylesheet" href="css/report.css">

    <%-- Thư viện biểu đồ Chart.js --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <%-- ======================================================= --%>
    <%-- === KHỐI STYLE CỦA BẠN ĐÃ ĐƯỢC GIỮ LẠI NGUYÊN VẸN === --%>
    <%-- ======================================================= --%>
    <style>
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
            grid-template-columns: repeat(4, 1fr); /* Lưới 4 cột */
        }
        .main-chart-card {
            grid-column: 1 / -1; /* Trải dài toàn bộ 4 cột */
            height: 350px;
        }
        .secondary-chart-card {
            grid-column: span 2; /* Trải dài 2 cột */
            height: 350px;
        }
        .kpi-card {
            grid-column: span 2; /* Trải dài 2 cột */
        }
        .quick-filters {
            display: flex;
            gap: 8px;
            align-items: center;
            margin-left: auto;
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
            border-color: #007bff;
            color: #007bff;
            background-color: #e7f1ff;
        }
        .quick-filter-btn.active {
            background-color: #007bff;
            color: #ffffff;
            border-color: #007bff;
        }
        .no-data-message {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100%;
            color: #868e96;
            font-size: 14px;
            font-style: italic;
            text-align: center;
            padding: 20px;
        }
    </style>
</head>
<body>
    <%-- **SỬA LỖI LAYOUT:** Áp dụng cấu trúc HTML chuẩn với "app-container" và "main-content" --%>
    <div class="app-container">
        <jsp:include page="mainMenu.jsp"/>

        <main class="main-content">
            <header class="main-top-bar">
                <div class="page-title">Bảng điều khiển</div>
                <div class="quick-filters">
                    <a href="dashboard?period=last7days" class="quick-filter-btn ${period == 'last7days' ? 'active' : ''}">7 ngày qua</a>
                    <a href="dashboard?period=thismonth" class="quick-filter-btn ${period == 'thismonth' ? 'active' : ''}">Tháng này</a>
                    <a href="dashboard?period=lastmonth" class="quick-filter-btn ${period == 'lastmonth' ? 'active' : ''}">Tháng trước</a>
                    <a href="dashboard?period=thisyear" class="quick-filter-btn ${period == 'thisyear' ? 'active' : ''}">Năm nay</a>
                </div>
            </header>

            <section class="main-content-body">
                <%-- NỘI DUNG CỦA BẠN ĐƯỢC GIỮ NGUYÊN --%>
                <c:if test="${not empty errorMessage}"><p class="error-message">${errorMessage}</p></c:if>
                <div class="welcome-header">
                    <h1>Chào mừng quay trở lại, <c:out value="${user.firstName} ${user.lastName}"/>!</h1>
                    <p>Đây là tổng quan nhanh về hoạt động của bạn.</p>
                </div>
                <div class="dashboard-grid">
                    <div class="report-card main-chart-card">
                        <div class="report-card-header"><i data-feather="trending-up" class="icon"></i><h3>Xu hướng Doanh thu (${summaryPeriod})</h3></div>
                        <div class="report-card-body">
                            <c:choose>
                                <c:when test="${not empty revenueTrendJson and revenueTrendJson ne '[]'}"><canvas id="revenueTrendChart"></canvas></c:when>
                                <c:otherwise><div class="no-data-message"><p>Không có dữ liệu doanh thu trong khoảng thời gian này.</p></div></c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="report-card kpi-card">
                        <div class="report-card-header"><i data-feather="activity" class="icon"></i><h3>Chỉ số chính (${summaryPeriod})</h3></div>
                        <div class="report-card-body">
                            <ul class="kpi-list">
                                <li class="kpi-item" style="padding-bottom: 16px; border-bottom: 1px solid #f0f0f0;">
                                    <span class="label">Tổng doanh thu</span>
                                    <span class="value" style="font-size: 24px; color: #28a745;"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                                </li>
                                <li class="kpi-item" style="padding-top: 16px;">
                                    <span class="label">Khách hàng mới</span>
                                    <span class="value success" style="font-size: 24px;">+ <fmt:formatNumber value="${newCustomers}"/></span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="report-card secondary-chart-card">
                        <div class="report-card-header"><i data-feather="user-plus" class="icon"></i><h3>Xu hướng Khách hàng mới (${summaryPeriod})</h3></div>
                        <div class="report-card-body">
                            <c:choose>
                                <c:when test="${not empty customerTrendJson and customerTrendJson ne '[]'}"><canvas id="customerTrendChart"></canvas></c:when>
                                <c:otherwise><div class="no-data-message"><p>Không có khách hàng mới trong khoảng thời gian này.</p></div></c:otherwise>
                            </c:choose>
                        </div>
                    </div>
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
                </div>
            </section>
        </main>
    </div>

    <%-- **SỬA LỖI JAVASCRIPT:** Chuyển script xuống cuối trang và sửa lỗi cú pháp --%>
    <script src="https://unpkg.com/feather-icons"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            feather.replace();

            try {
                const revenueTrendJson = '${revenueTrendJson}';
                if (revenueTrendJson && revenueTrendJson.trim() !== '[]' && revenueTrendJson.trim() !== '') {
                    const revenueData = JSON.parse(revenueTrendJson);
                    const revenueCtx = document.getElementById('revenueTrendChart').getContext('2d');
                    new Chart(revenueCtx, {
                        type: 'line',
                        data: {
                            labels: revenueData.map(d => new Date(d.date).toLocaleDateString('vi-VN')),
                            datasets: [{
                                label: 'Doanh thu',
                                data: revenueData.map(d => d.revenue),
                                borderColor: 'rgba(13, 148, 136, 1)',
                                backgroundColor: 'rgba(13, 148, 136, 0.1)',
                                tension: 0.3, fill: true, borderWidth: 2
                            }]
                        },
                        options: {
                            responsive: true, maintainAspectRatio: false,
                            scales: { y: { beginAtZero: true } },
                            plugins: {
                                legend: { display: false },
                                tooltip: {
                                    callbacks: {
                                        label: function(context) {
                                            return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (e) { console.error("Lỗi vẽ biểu đồ doanh thu:", e); }

            try {
                const customerTrendJson = '${customerTrendJson}';
                if (customerTrendJson && customerTrendJson.trim() !== '[]' && customerTrendJson.trim() !== '') {
                    const customerData = JSON.parse(customerTrendJson);
                    const customerCtx = document.getElementById('customerTrendChart').getContext('2d');
                    new Chart(customerCtx, {
                        type: 'bar',
                        data: {
                            labels: customerData.map(d => new Date(d.date).toLocaleDateString('vi-VN')),
                            datasets: [{
                                label: 'Khách hàng mới',
                                data: customerData.map(d => d.count),
                                backgroundColor: 'rgba(13, 148, 136, 0.7)',
                                borderColor: 'rgba(13, 148, 136, 1)',
                                borderWidth: 1
                            }]
                        },
                         options: {
                            responsive: true, maintainAspectRatio: false,
                            scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
                            plugins: { legend: { display: false } }
                        }
                    });
                }
            } catch (e) { console.error("Lỗi vẽ biểu đồ khách hàng:", e); }
        });
    </script>
    <script src="js/mainMenu.js"></script>
</body>
</html>
