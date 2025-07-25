<%-- 
    Document   : report
    Created on : Jul 21, 2025, 3:01:33 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Đặt trang hiện tại là 'report' để active đúng mục trong mainMenu --%>
<c:set var="currentPage" value="report" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo cáo - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- Các file CSS chung --%>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/report.css">

        <%-- Thư viện biểu đồ Chart.js --%>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <style>
            .detail-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                font-size: 14px;
            }
            .detail-table th, .detail-table td {
                border: 1px solid #e0e0e0;
                padding: 12px 15px;
                text-align: left;
                vertical-align: middle;
            }
            .detail-table th {
                background-color: #f8f9fa;
                font-weight: 600;
            }
            .detail-table tbody tr:nth-child(even) {
                background-color: #fdfdfd;
            }
            .detail-table tbody tr:hover {
                background-color: #f1f1f1;
            }
            .customer-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                margin-right: 12px;
                object-fit: cover;
            }
            .btn-primary {
                padding: 10px 20px;
                border: none;
                background-color: #007bff;
                color: white;
                border-radius: 5px;
                cursor: pointer;
                font-weight: 500;
            }
            .btn-primary:hover {
                background-color: #0056b3;
            }
            /* Thêm vào thẻ <style> trong report.jsp */
            .status-badge {
                padding: 3px 8px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
                color: #fff;
                text-transform: capitalize;
            }
            .status-active, .status-resolved, .status-closed {
                background-color: #28a745;
            } /* Xanh lá */
            .status-expiring, .status-in_progress, .status-assigned {
                background-color: #ffc107;
                color: #212529;
            } /* Vàng */
            .status-expired, .status-rejected {
                background-color: #dc3545;
            } /* Đỏ */
            .status-pending, .status-new {
                background-color: #6c757d;
            } /* Xám */

            /* Thêm vào thẻ <style> trong report.jsp */
            .nested-table {
                width: 100%;
                margin-top: 10px;
                background-color: #f8f9fa;
                border: 1px solid #dee2e6;
            }
            .nested-table th {
                background-color: #e9ecef;
                font-size: 13px;
                padding: 8px;
            }
            .nested-table td {
                font-size: 13px;
                padding: 8px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>

            <div class="content-wrapper">
                <header class="main-top-bar">
                    <div class="page-title">Báo cáo & Thống kê</div>
                </header>

                <section class="main-content-body">
                    <c:if test="${not empty errorMessage}">
                        <p style="color: red; font-weight: bold;">${errorMessage}</p>
                    </c:if>

                    <%-- Trong file report.jsp, thay thế toàn bộ form lọc cũ bằng form này --%>
                    <form action="report" method="GET" class="report-filters" id="reportFilterForm">
                        <div class="filter-group">
                            <label for="report-type">Loại báo cáo</label>
                            <select id="report-type" name="report-type" class="auto-submit-filter">
                                <option value="tongquan" ${reportType == 'tongquan' ? 'selected' : ''}>Tổng quan</option>
                                <option value="doanhthu" ${reportType == 'doanhthu' ? 'selected' : ''}>Doanh thu</option>
                                <option value="khachhang" ${reportType == 'khachhang' ? 'selected' : ''}>Khách hàng</option>
                                <option value="sanpham" ${reportType == 'sanpham' ? 'selected' : ''}>Sản phẩm</option>
                                <option value="hopdong" ${reportType == 'hopdong' ? 'selected' : ''}>Hợp đồng</option>
                                <option value="suachua" ${reportType == 'suachua' ? 'selected' : ''}>Sửa chữa</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label for="date-from">Từ ngày</label>
                            <input type="date" id="date-from" name="date-from" value="${selectedDateFrom}" class="auto-submit-filter">
                        </div>
                        <div class="filter-group">
                            <label for="date-to">Đến ngày</label>
                            <input type="date" id="date-to" name="date-to" value="${selectedDateTo}" class="auto-submit-filter">
                        </div>
                        <%-- NÚT "XEM BÁO CÁO" ĐÃ ĐƯỢC XÓA --%>
                    </form>

                    <c:choose>
                        <%-- ======================= CASE 1: BÁO CÁO DOANH THU ======================= --%>
                        <c:when test="${reportType == 'doanhthu'}">
                            <div class="report-grid" style="grid-template-columns: 1fr;">
                                <div class="report-card">
                                    <div class="report-card-header">
                                        <i data-feather="dollar-sign" class="icon"></i>
                                        <h3>Tổng doanh thu</h3>
                                    </div>
                                    <div class="report-card-body">
                                        <div class="revenue-summary">
                                            <p class="total-revenue"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                                            <p class="period">Từ ${selectedDateFrom} đến ${selectedDateTo}</p>
                                        </div>
                                    </div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header">
                                        <i data-feather="bar-chart-2" class="icon"></i>
                                        <h3>Biểu đồ xu hướng doanh thu</h3>
                                    </div>
                                    <div class="report-card-body">
                                        <canvas id="revenueChart"></canvas>
                                    </div>
                                </div>
                            </div>
                        </c:when>

                        <%-- ======================= CASE 2: BÁO CÁO KHÁCH HÀNG ======================= --%>
                        <c:when test="${reportType == 'khachhang'}">
                            <div class="report-grid">
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="user-plus" class="icon"></i><h3>Khách hàng mới</h3></div>
                                    <div class="report-card-body"><p class="total-revenue">+ <fmt:formatNumber value="${newCustomers}"/></p></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="users" class="icon"></i><h3>Tổng số khách hàng</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${totalCustomers}"/></p></div>
                                </div>
                            </div>
                            <div class="report-card" style="margin-top: 20px; width: 100%;">
                                <div class="report-card-header"><h3>Danh sách khách hàng mới</h3></div>
                                <div class="report-card-body">
                                    <table class="detail-table">
                                        <thead><tr><th>Mã KH</th><th>Tên doanh nghiệp</th><th>Ngày tham gia</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="customer" items="${newCustomersList}">
                                                <tr>
                                                    <td><c:out value="${customer.code}"/></td>
                                                    <td>
                                                        <img src="${not empty customer.avatar_url ? customer.avatar_url : 'images/default-avatar.png'}" class="customer-avatar" alt="Avatar">
                                                        <c:out value="${customer.name}"/>
                                                    </td>
                                                    <td><fmt:formatDate value="${customer.created_at}" pattern="dd/MM/yyyy"/></td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty newCustomersList}">
                                                <tr><td colspan="3" style="text-align: center;">Không có khách hàng mới trong khoảng thời gian này.</td></tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:when>

                        <%-- ======================= CASE 3: BÁO CÁO SẢN PHẨM ======================= --%>
                        <c:when test="${reportType == 'sanpham'}">
                            <div class="report-card" style="width: 100%;">
                                <div class="report-card-header"><i data-feather="package" class="icon"></i><h3>Thống kê sản phẩm/dịch vụ bán chạy</h3></div>
                                <div class="report-card-body">
                                    <table class="detail-table">
                                        <thead><tr><th>Hạng</th><th>Tên sản phẩm/dịch vụ</th><th>Số lượt mua</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="product" items="${topProducts}" varStatus="loop">
                                                <tr>
                                                    <td><b>#${loop.count}</b></td>
                                                    <td><c:out value="${product.name}"/></td>
                                                    <td><fmt:formatNumber value="${product.sales}"/></td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty topProducts}">
                                                <tr><td colspan="3" style="text-align: center;">Không có dữ liệu sản phẩm trong khoảng thời gian này.</td></tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:when>

                        <%-- Thêm 2 khối <c:when> này vào trong thẻ <c:choose> trong file report.jsp --%>

                        <%-- ======================= CASE 4: BÁO CÁO HỢP ĐỒNG ======================= --%>
                        <c:when test="${reportType == 'hopdong'}">
                            <div class="report-grid">
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="check-square" class="icon"></i><h3>Đang hiệu lực</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${contractStatus.active}"/></p></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="alert-triangle" class="icon"></i><h3>Sắp hết hạn</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${contractStatus.expiring}"/></p></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="x-circle" class="icon"></i><h3>Đã hết hạn</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${contractStatus.expired}"/></p></div>
                                </div>
                            </div>
                            <div class="report-card" style="margin-top: 20px; width: 100%;">
                                <div class="report-card-header"><h3>Danh sách hợp đồng</h3></div>
                                <div class="report-card-body">
                                    <table class="detail-table">
                                        <thead><tr><th>Mã HĐ</th><th>Khách hàng</th><th>Ngày BĐ</th><th>Ngày KT</th><th>Trạng thái</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="contract" items="${contractsList}">
                                                <tr>
                                                    <td><b><c:out value="${contract.code}"/></b></td>
                                                    <td><c:out value="${contract.enterprise_name}"/></td>
                                                    <td><fmt:formatDate value="${contract.start_date}" pattern="dd/MM/yyyy"/></td>
                                                    <td><fmt:formatDate value="${contract.end_date}" pattern="dd/MM/yyyy"/></td>
                                                    <td>
                                                        <span class="status-badge status-${fn:toLowerCase(contract.status)}">
                                                            <c:out value="${contract.status}"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty contractsList}">
                                                <tr><td colspan="5" style="text-align: center;">Không có hợp đồng nào trong khoảng thời gian này.</td></tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:when>

                        <%-- ======================= CASE 5: BÁO CÁO SỬA CHỮA ======================= --%>
                        <%-- Trong report.jsp, THAY THẾ TOÀN BỘ khối <c:when test="${reportType == 'suachua'}"> bằng khối này --%>
                        <c:when test="${reportType == 'suachua'}">
                            <div class="report-grid">
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="check-circle" class="icon"></i><h3>Đã hoàn thành</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${requestStatus.completed}"/></p></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="loader" class="icon"></i><h3>Đang tiến hành</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${requestStatus.in_progress}"/></p></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="inbox" class="icon"></i><h3>Chờ xử lý</h3></div>
                                    <div class="report-card-body"><p class="total-revenue"><fmt:formatNumber value="${requestStatus.pending}"/></p></div>
                                </div>
                            </div>
                            <div class="report-card" style="margin-top: 20px; width: 100%;">
                                <div class="report-card-header"><h3>Danh sách yêu cầu sửa chữa</h3></div>
                                <div class="report-card-body">
                                    <%-- Thêm dòng này vào trước thẻ table để kiểm tra --%>
                                    <p style="color: red; font-size: 20px; font-weight: bold;">
                                        Số yêu cầu tìm thấy: ${fn:length(requestsWithDevices)}
                                    </p>
                                    <table class="detail-table">
                                        <thead>
                                            <tr>
                                                <th style="width: 10%;">Mã YC</th>
                                                <th style="width: 25%;">Tiêu đề / Khách hàng</th>
                                                <th style="width: 15%;">Nhân viên xử lý</th>
                                                <th style="width: 10%;">Ngày tạo</th>
                                                <th style="width: 10%;">Trạng thái</th>
                                                <th style="width: 30%;">Chi tiết thiết bị</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="req" items="${requestsWithDevices}">
                                                <tr>
                                                    <td><b><c:out value="${req.code}"/></b></td>
                                                    <td>
                                                        <div><c:out value="${req.title}"/></div>
                                                        <div style="font-size: 12px; color: #6c757d;"><c:out value="${req.enterprise_name}"/></div>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty req.assigned_to}"><c:out value="${req.assigned_to}"/></c:when>
                                                            <c:otherwise><i style="color: #888;">Chưa phân công</i></c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td><fmt:formatDate value="${req.created_at}" pattern="dd/MM/yyyy"/></td>
                                                    <td>
                                                        <span class="status-badge status-${fn:replace(req.status, '_', '')}">
                                                            <c:out value="${req.status}"/>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty req.devices}">
                                                            <table class="nested-table">
                                                                <thead><tr><th>Tên thiết bị</th><th>Số Serial</th><th>Mô tả lỗi</th></tr></thead>
                                                                <tbody>
                                                                    <c:forEach var="device" items="${req.devices}">
                                                                        <tr>
                                                                            <td><c:out value="${device.device_name}"/></td>
                                                                            <td><c:out value="${device.serial_number}"/></td>
                                                                            <td><c:out value="${device.problem_description}"/></td>
                                                                        </tr>
                                                                    </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </c:if>
                                                        <c:if test="${empty req.devices}">
                                                            <i style="color: #888; font-size: 13px;">Không có thông tin thiết bị.</i>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty requestsWithDevices}">
                                                <tr><td colspan="6" style="text-align: center;">Không có yêu cầu nào trong khoảng thời gian này.</td></tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:when>

                        <%-- ======================= DEFAULT CASE: BÁO CÁO TỔNG QUAN ======================= --%>
                        <c:otherwise>
                            <div class="report-grid">
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="dollar-sign" class="icon"></i><h3>Doanh thu</h3></div>
                                    <div class="report-card-body"><div class="revenue-summary"><p class="total-revenue"><fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p></div></div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="users" class="icon"></i><h3>Thống kê Khách hàng</h3></div>
                                    <div class="report-card-body">
                                        <ul class="kpi-list">
                                            <li class="kpi-item"><span class="label">Khách hàng mới</span><span class="value success">+ <fmt:formatNumber value="${newCustomers}"/></span></li>
                                            <li class="kpi-item"><span class="label">Tổng số khách hàng</span><span class="value"><fmt:formatNumber value="${totalCustomers}"/></span></li>
                                            <li class="kpi-item"><span class="label">Khách hàng quay lại</span><span class="value"><fmt:formatNumber value="${returningCustomers}"/></span></li>
                                        </ul>
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
                                    <div class="report-card-header"><i data-feather="tool" class="icon"></i><h3>Tình trạng Sửa chữa</h3></div>
                                    <div class="report-card-body">
                                        <ul class="kpi-list">
                                            <li class="kpi-item"><span class="label">Đã hoàn thành</span><span class="value success"><fmt:formatNumber value="${requestStatus.completed}"/></span></li>
                                            <li class="kpi-item"><span class="label">Đang tiến hành</span><span class="value warning"><fmt:formatNumber value="${requestStatus.in_progress}"/></span></li>
                                            <li class="kpi-item"><span class="label">Chờ xử lý</span><span class="value"><fmt:formatNumber value="${requestStatus.pending}"/></span></li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="report-card">
                                    <div class="report-card-header"><i data-feather="package" class="icon"></i><h3>Sản phẩm nổi bật</h3></div>
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
                                            <c:if test="${empty topProducts}"><p>Không có dữ liệu.</p></c:if>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                        </c:otherwise>
                    </c:choose>
                </section>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // Chỉ chạy script vẽ biểu đồ nếu đang ở trang báo cáo doanh thu
                if ('${reportType}' === 'doanhthu') {
                    const ctx = document.getElementById('revenueChart').getContext('2d');
                    const revenueData = [<c:forEach var="item" items="${revenueTrend}">{date: '${item.date}', revenue: ${item.revenue}},</c:forEach>];

                    new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: revenueData.map(item => new Date(item.date).toLocaleDateString('vi-VN')),
                            datasets: [{
                                    label: 'Doanh thu',
                                    data: revenueData.map(item => item.revenue),
                                    borderColor: 'rgba(54, 162, 235, 1)',
                                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                                    borderWidth: 2,
                                    fill: true,
                                    tension: 0.1
                                }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {callback: value => new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'}).format(value)}
                                }
                            },
                            plugins: {
                                tooltip: {
                                    callbacks: {
                                        label: context => `\${context.dataset.label || ''}: \${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y)}`
                                    }
                                }
                            }
                        }
                    });
                }
            });
            </script>
        <%-- Thêm đoạn script này vào cuối file report.jsp, trước </body> --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Tìm đến form lọc bằng ID
                const filterForm = document.getElementById('reportFilterForm');

                // Tìm tất cả các input và select có class 'auto-submit-filter'
                const filterInputs = document.querySelectorAll('.auto-submit-filter');

                // Gắn sự kiện 'change' cho mỗi bộ lọc
                filterInputs.forEach(function (input) {
                    input.addEventListener('change', function () {
                        // Khi giá trị của bất kỳ bộ lọc nào thay đổi, tự động gửi form
                        filterForm.submit();
                    });
                });
            });
        </script>
        <script src="js/mainMenu.js"></script>
    </body>
</html>