<%-- 
    Document   : report
    Created on : Jul 21, 2025, 3:01:33 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

        <%-- Các file CSS chung được kế thừa từ dashboard --%>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/pagination.css">
        <link rel="stylesheet" href="css/report.css">

       
    </head>
    <body>
        <div class="app-container">
            <%-- Include Main Menu --%>
            <jsp:include page="mainMenu.jsp"/>

            <div class="content-wrapper">
                <%-- Header của trang --%>
                <header class="main-top-bar">
                    <div class="page-title">Báo cáo & Thống kê</div>
                    <%-- Có thể thêm các nút hành động ở đây nếu cần --%>
                </header>

                <%-- Phần thân của nội dung --%>
                <section class="main-content-body">
                    
                    <%-- KHU VỰC LỌC DỮ LIỆU --%>
                    <div class="report-filters">
                        <div class="filter-group">
                            <label for="report-type">Loại báo cáo</label>
                            <select id="report-type">
                                <option>Tổng quan</option>
                                <option>Doanh thu</option>
                                <option>Khách hàng</option>
                                <option>Sản phẩm</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label for="date-from">Từ ngày</label>
                            <input type="date" id="date-from" value="2025-06-01">
                        </div>
                        <div class="filter-group">
                            <label for="date-to">Đến ngày</label>
                            <input type="date" id="date-to" value="2025-06-22">
                        </div>
                    </div>

                    <%-- LƯỚI HIỂN THỊ CÁC BÁO CÁO --%>
                    <div class="report-grid">

                        <div class="report-card">
                            <div class="report-card-header">
                                <i data-feather="dollar-sign" class="icon"></i>
                                <h3>Doanh thu sửa chữa</h3>
                            </div>
                            <div class="report-card-body">
                                <div class="revenue-summary">
                                    <p class="total-revenue">125.680.000 ₫</p>
                                    <p class="period">Trong tháng 6/2025</p>
                                </div>
                            </div>
                        </div>

                        <div class="report-card">
                            <div class="report-card-header">
                                <i data-feather="users" class="icon"></i>
                                <h3>Thống kê Khách hàng</h3>
                            </div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item">
                                        <span class="label">Khách hàng mới</span>
                                        <span class="value success">+ 32</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Tổng số khách hàng</span>
                                        <span class="value">1,250</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Khách hàng quay lại</span>
                                        <span class="value">15</span>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div class="report-card">
                            <div class="report-card-header">
                                <i data-feather="briefcase" class="icon"></i>
                                <h3>Tình trạng Hợp đồng</h3>
                            </div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item">
                                        <span class="label">Đang hiệu lực</span>
                                        <span class="value success">85</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Sắp hết hạn</span>
                                        <span class="value warning">12</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Đã hết hạn</span>
                                        <span class="value danger">5</span>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div class="report-card">
                            <div class="report-card-header">
                                <i data-feather="tool" class="icon"></i>
                                <h3>Tình trạng Sửa chữa</h3>
                            </div>
                            <div class="report-card-body">
                                <ul class="kpi-list">
                                    <li class="kpi-item">
                                        <span class="label">Đã hoàn thành</span>
                                        <span class="value success">112</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Đang tiến hành</span>
                                        <span class="value warning">25</span>
                                    </li>
                                    <li class="kpi-item">
                                        <span class="label">Chờ xử lý</span>
                                        <span class="value">18</span>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        
                        <div class="report-card">
                            <div class="report-card-header">
                                <i data-feather="package" class="icon"></i>
                                <h3>Sản phẩm / Dịch vụ nổi bật</h3>
                            </div>
                            <div class="report-card-body">
                                <ul class="product-list">
                                    <li class="product-item">
                                        <span class="rank">#1</span>
                                        <div class="info">
                                            <p class="name">Gói bảo trì toàn diện 1 năm</p>
                                            <p class="sales">78 lượt mua</p>
                                        </div>
                                    </li>
                                    <li class="product-item">
                                        <span class="rank">#2</span>
                                        <div class="info">
                                            <p class="name">Sửa chữa màn hình Laptop</p>
                                            <p class="sales">55 lượt</p>
                                        </div>
                                    </li>
                                    <li class="product-item">
                                        <span class="rank">#3</span>
                                        <div class="info">
                                            <p class="name">Nâng cấp RAM/SSD</p>
                                            <p class="sales">43 lượt</p>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>

                    </div>
                </section>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Kích hoạt thư viện icon, rất quan trọng!
                feather.replace();
            });
        </script>

        <%-- Script để xử lý các chức năng của menu chính --%>
        <script src="../js/mainMenu.js"></script>
    </body>
</html>
