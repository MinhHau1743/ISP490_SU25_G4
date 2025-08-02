<%--
    Document    : dashboard.jsp
    Description : Trang tổng quan (phiên bản độc lập, đã được chuẩn hóa và sửa lỗi).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%-- Khai báo JSTL đúng chuẩn cho Tomcat 11 / Jakarta EE --%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bảng điều khiển - DPCRM</title>

    <%-- 1. Nạp Font chữ, Icons và CSS --%>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Nunito:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/feather-icons"></script>
    
    <%-- Nạp các tệp CSS chính của ứng dụng --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/sidebar.css">

    <%-- Thư viện biểu đồ --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
    <%-- CSS riêng của trang dashboard --%>
    <style>
        .wrapper { display: flex; }
        .main-content { flex-grow: 1; padding: 2rem; transition: margin-left .3s; margin-left: 260px; }
        body.sidebar-collapsed .main-content { margin-left: 80px; }
        .welcome-header { margin-bottom: 24px; }
        .welcome-header h1 { font-size: 24px; font-weight: 600; }
        .welcome-header p { font-size: 16px; color: #6c757d; }
        .dashboard-grid { display: grid; gap: 24px; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); }
        .main-chart-card { grid-column: 1 / -1; }
        .kpi-list { list-style: none; padding: 0; margin: 0; }
        .kpi-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
        .kpi-item:last-child { border-bottom: none; }
        .kpi-item .label { color: #6c757d; }
        .kpi-item .value { font-weight: 600; font-size: 1.2rem; }
        .value.success { color: #28a745; }
        .value.warning { color: #ffc107; }
        .value.danger { color: #dc3545; }
        .no-data-message { display: flex; justify-content: center; align-items: center; height: 250px; color: #868e96; font-style: italic; }
    </style>
</head>
<body>
    <div class="wrapper">
        <%-- 2. Nhúng menu vào trang --%>
        <%-- Đảm bảo bạn đã di chuyển tệp mainMenu.jsp vào thư mục /jsp/ --%>
        <jsp:include page="/jsp/mainMenu.jsp" />

        <main class="main-content">
            <header class="header">
                <h1 class="page-title">Bảng điều khiển</h1>
                 <div class="d-flex align-items-center gap-2">
                    <a href="dashboard?period=last7days" class="btn btn-sm ${param.period == 'last7days' || empty param.period ? 'btn-primary' : 'btn-outline-primary'}">7 ngày qua</a>
                    <a href="dashboard?period=thismonth" class="btn btn-sm ${param.period == 'thismonth' ? 'btn-primary' : 'btn-outline-primary'}">Tháng này</a>
                    <a href="dashboard?period=lastmonth" class="btn btn-sm ${param.period == 'lastmonth' ? 'btn-primary' : 'btn-outline-primary'}">Tháng trước</a>
                    <a href="dashboard?period=thisyear" class="btn btn-sm ${param.period == 'thisyear' ? 'btn-primary' : 'btn-outline-primary'}">Năm nay</a>
                </div>
            </header>

            <%-- 3. Nội dung chính của trang Dashboard --%>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="welcome-header">
                <h1>Chào mừng quay trở lại, <c:out value="${sessionScope.user.firstName} ${sessionScope.user.lastName}"/>!</h1>
                <p>Đây là tổng quan nhanh về hoạt động trong <strong>${summaryPeriod}</strong>.</p>
            </div>
            
            <div class="dashboard-grid">
                <%-- Biểu đồ Doanh thu --%>
                <div class="card main-chart-card">
                    <div class="card-header"><i data-feather="trending-up"></i>Xu hướng Doanh thu</div>
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

                <%-- Các thẻ KPI --%>
                <div class="card">
                    <div class="card-header"><i data-feather="activity"></i>Chỉ số chính</div>
                    <div class="card-body">
                        <ul class="kpi-list">
                            <li class="kpi-item"><span class="label">Tổng doanh thu</span><span class="value success"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></span></li>
                            <li class="kpi-item"><span class="label">Khách hàng mới</span><span class="value success">+ <fmt:formatNumber value="${newCustomers}"/></span></li>
                        </ul>
                    </div>
                </div>
                <div class="card">
                    <div class="card-header"><i data-feather="briefcase"></i>Tình trạng Hợp đồng</div>
                    <div class="card-body">
                        <ul class="kpi-list">
                            <li class="kpi-item"><span class="label">Đang hiệu lực</span><span class="value success"><fmt:formatNumber value="${contractStatus.active}"/></span></li>
                            <li class="kpi-item"><span class="label">Sắp hết hạn</span><span class="value warning"><fmt:formatNumber value="${contractStatus.expiring}"/></span></li>
                            <li class="kpi-item"><span class="label">Đã hết hạn</span><span class="value danger"><fmt:formatNumber value="${contractStatus.expired}"/></span></li>
                        </ul>
                    </div>
                </div>
                <div class="card">
                    <div class="card-header"><i data-feather="tool"></i>Yêu cầu sửa chữa</div>
                    <div class="card-body">
                        <ul class="kpi-list">
                            <li class="kpi-item"><span class="label">Đã hoàn thành</span><span class="value success"><fmt:formatNumber value="${requestStatus.completed}"/></span></li>
                            <li class="kpi-item"><span class="label">Đang tiến hành</span><span class="value warning"><fmt:formatNumber value="${requestStatus.in_progress}"/></span></li>
                            <li class="kpi-item"><span class="label">Chờ xử lý</span><span class="value"><fmt:formatNumber value="${requestStatus.pending}"/></span></li>
                        </ul>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <%-- 4. JavaScript --%>
    <script src="${pageContext.request.contextPath}/js/sidebar.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            feather.replace(); // Kích hoạt icon

            let revenueTrendData = [];
            try { revenueTrendData = JSON.parse('${revenueTrendJson}'); } catch (e) { console.error("Lỗi phân tích dữ liệu doanh thu:", e); }

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
                            fill: true,
                            tension: 0.3
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    // Sửa lỗi xung đột cú pháp JSP và JS
                                    label: context => `\${context.dataset.label}: \${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y)}`
                                }
                            }
                        }
                    }
                });
            }
        });
    </script>
</body>
</html>
