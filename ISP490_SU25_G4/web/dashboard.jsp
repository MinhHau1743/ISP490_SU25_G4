<%--
    Document   : dashboard.jsp
    Description: Trang tổng quan và báo cáo nhanh (đã được tối ưu và đồng bộ).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- 1. Khai báo sử dụng layout chính và đặt tiêu đề cho trang --%>
<meta name="decorator" content="main">
<title>Bảng điều khiển</title>

<%-- 2. (Tùy chọn) Chèn CSS hoặc Script riêng vào <head> của layout --%>
<content tag="head">
    <%-- Thư viện biểu đồ Chart.js --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <%-- Các style này chỉ dành riêng cho trang dashboard --%>
    <style>
        .welcome-header {
            margin-bottom: 24px;
        }
        .welcome-header h1 {
            font-size: 24px;
            font-weight: 600;
        }
        .welcome-header p {
            font-size: 16px;
            color: #6c757d;
        }
        .dashboard-grid {
            display: grid;
            gap: 24px;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        }
        .main-chart-card {
            grid-column: 1 / -1; /* Luôn chiếm toàn bộ chiều rộng */
        }
        .kpi-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .kpi-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .kpi-item:last-child {
            border-bottom: none;
        }
        .kpi-item .label {
            color: #6c757d;
        }
        .kpi-item .value {
            font-weight: 600;
            font-size: 1.2rem;
        }
        .value.success {
            color: #28a745;
        }
        .value.warning {
            color: #ffc107;
        }
        .value.danger  {
            color: #dc3545;
        }
        .no-data-message {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 250px;
            color: #868e96;
            font-style: italic;
        }
    </style>
</content>

<%-- 3. (Tùy chọn) Thêm các nút bấm vào header của layout --%>
<content tag="header_actions">
    <div class="d-flex align-items-center gap-2">
        <a href="dashboard?period=last7days" class="btn btn-sm ${param.period == 'last7days' || empty param.period ? 'btn-primary' : 'btn-outline-primary'}">7 ngày qua</a>
        <a href="dashboard?period=thismonth" class="btn btn-sm ${param.period == 'thismonth' ? 'btn-primary' : 'btn-outline-primary'}">Tháng này</a>
        <a href="dashboard?period=lastmonth" class="btn btn-sm ${param.period == 'lastmonth' ? 'btn-primary' : 'btn-outline-primary'}">Tháng trước</a>
        <a href="dashboard?period=thisyear" class="btn btn-sm ${param.period == 'thisyear' ? 'btn-primary' : 'btn-outline-primary'}">Năm nay</a>
    </div>
</content>

<%-- 4. Đây là nội dung chính (phần <body>) của trang --%>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<%-- Lời chào mừng --%>
<div class="welcome-header">
    <h1>Chào mừng quay trở lại, <c:out value="${sessionScope.user.firstName} ${sessionScope.user.lastName}"/>!</h1>
    <p>Đây là tổng quan nhanh về hoạt động trong <strong>${summaryPeriod}</strong>.</p>
</div>

<%-- Lưới hiển thị chính --%>
<div class="dashboard-grid">

    <%-- Biểu đồ Doanh thu (Lớn) --%>
    <div class="card main-chart-card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span><i data-feather="trending-up" class="me-2"></i>Xu hướng Doanh thu</span>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty revenueTrendJson and revenueTrendJson ne '[]'}">
                    <canvas id="revenueTrendChart" style="height: 300px;"></canvas>
                    </c:when>
                    <c:otherwise>
                    <div class="no-data-message">Không có dữ liệu doanh thu trong khoảng thời gian này.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%-- Thẻ KPI Doanh thu và Khách hàng mới --%>
    <div class="card">
        <div class="card-header"><i data-feather="activity" class="me-2"></i>Chỉ số chính</div>
        <div class="card-body">
            <ul class="kpi-list">
                <li class="kpi-item">
                    <span class="label">Tổng doanh thu</span>
                    <span class="value success"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span>
                </li>
                <li class="kpi-item">
                    <span class="label">Khách hàng mới</span>
                    <span class="value success">+ <fmt:formatNumber value="${newCustomers}"/></span>
                </li>
            </ul>
        </div>
    </div>

    <%-- Thẻ Tình trạng Hợp đồng --%>
    <div class="card">
        <div class="card-header"><i data-feather="briefcase" class="me-2"></i>Tình trạng Hợp đồng</div>
        <div class="card-body">
            <ul class="kpi-list">
                <li class="kpi-item"><span class="label">Đang hiệu lực</span><span class="value success"><fmt:formatNumber value="${contractStatus.active}"/></span></li>
                <li class="kpi-item"><span class="label">Sắp hết hạn</span><span class="value warning"><fmt:formatNumber value="${contractStatus.expiring}"/></span></li>
                <li class="kpi-item"><span class="label">Đã hết hạn</span><span class="value danger"><fmt:formatNumber value="${contractStatus.expired}"/></span></li>
            </ul>
        </div>
    </div>

    <%-- Biểu đồ khách hàng mới --%>
    <div class="card">
        <div class="card-header"><i data-feather="user-plus" class="me-2"></i>Xu hướng Khách hàng mới</div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty customerTrendJson and customerTrendJson ne '[]'}">
                    <canvas id="customerTrendChart" style="height: 250px;"></canvas>
                    </c:when>
                    <c:otherwise>
                    <div class="no-data-message">Không có khách hàng mới trong khoảng thời gian này.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%-- Thẻ Tình trạng Sửa chữa --%>
    <div class="card">
        <div class="card-header"><i data-feather="tool" class="me-2"></i>Yêu cầu sửa chữa</div>
        <div class="card-body">
            <ul class="kpi-list">
                <li class="kpi-item"><span class="label">Đã hoàn thành</span><span class="value success"><fmt:formatNumber value="${requestStatus.completed}"/></span></li>
                <li class="kpi-item"><span class="label">Đang tiến hành</span><span class="value warning"><fmt:formatNumber value="${requestStatus.in_progress}"/></span></li>
                <li class="kpi-item"><span class="label">Chờ xử lý</span><span class="value"><fmt:formatNumber value="${requestStatus.pending}"/></span></li>
            </ul>
        </div>
    </div>
</div>

<%-- 5. Chèn script riêng của trang vào cuối <body> của layout --%>
<content tag="script">
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            // Dữ liệu từ Servlet (đặt trong try-catch để tránh lỗi nếu biến không tồn tại)
            let revenueTrendData = [];
            let customerTrendData = [];
            try {
                revenueTrendData = JSON.parse('${revenueTrendJson}');
            } catch (e) {
                console.error("Không thể phân tích dữ liệu doanh thu:", e);
            }
            try {
                customerTrendData = JSON.parse('${customerTrendJson}');
            } catch (e) {
                console.error("Không thể phân tích dữ liệu khách hàng:", e);
            }

            // === 1. BIỂU ĐỒ XU HƯỚNG DOANH THU ===
            const revenueCanvas = document.getElementById('revenueTrendChart');
            if (revenueCanvas && revenueTrendData && revenueTrendData.length > 0) {
                new Chart(revenueCanvas.getContext('2d'), {
                    type: 'line',
                    data: {
                        labels: revenueTrendData.map(d => new Date(d.date).toLocaleDateString('vi-VN')),
                        datasets: [{
                                label: 'Doanh thu',
                                data: revenueTrendData.map(d => d.revenue),
                                borderColor: 'rgba(0, 123, 255, 1)',
                                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                                borderWidth: 2,
                                fill: true,
                                tension: 0.3
                            }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {y: {beginAtZero: true, ticks: {callback: value => new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value)}}},
                        plugins: {
                            legend: {display: false},
                            tooltip: {
                                callbacks: {
                                    // SỬA LỖI TẠI ĐÂY: Cú pháp callback đã được sửa đúng
                                    label: context => `\${context.dataset.label}: \${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y)}`
                                }
                            }
                        }
                    }
                });
            }

            // === 2. BIỂU ĐỒ XU HƯỚNG KHÁCH HÀNG MỚI ===
            const customerCanvas = document.getElementById('customerTrendChart');
            if (customerCanvas && customerTrendData && customerTrendData.length > 0) {
                new Chart(customerCanvas.getContext('2d'), {
                    type: 'bar',
                    data: {
                        labels: customerTrendData.map(d => new Date(d.date).toLocaleDateString('vi-VN')),
                        datasets: [{
                                label: 'Khách hàng mới',
                                data: customerTrendData.map(d => d.count),
                                backgroundColor: 'rgba(255, 193, 7, 0.7)',
                                borderColor: 'rgba(255, 193, 7, 1)',
                                borderWidth: 1
                            }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {y: {beginAtZero: true, ticks: {stepSize: 1}}},
                        plugins: {legend: {display: false}}
                    }
                });
            }
        });
    </script>
</content>