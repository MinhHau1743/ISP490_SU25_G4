<%--
    Document   : listplan
    Created on : Jul 21, 2025, 11:32:12 PM
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Đặt trang hiện tại là 'support' để làm nổi bật mục "Hỗ trợ Kỹ thuật" trong menu --%>
<c:set var="currentPage" value="support" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách kế hoạch - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">

        <%-- Thêm file CSS cho bảng, nên tạo file mới css/table.css --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listPlan.css">


    </head>
    <body>
        <div class="app-container">
            <%-- Include Main Menu --%>
            <jsp:include page="/mainMenu.jsp"/>

            <div class="content-area"> <%-- Thêm một wrapper để chứa header và content --%>

                <%-- Header chính của ứng dụng, sử dụng header.css của bạn --%>
                <header class="main-top-bar">
                    <section class="page-header">
                        <div class="header-left">
                            <h1 class="page-main-title">
                                Chiến dịch

                            </h1>
                            <div class="breadcrumb">
                                <a href="#">Trang chủ</a> > Chiến dịch
                            </div>

                        </div>

                    </section>
                </header>

                <%-- Nội dung chính của trang --%>
                <main class="main-content">



                    <%-- Lưới thống kê --%>
                    <section class="stats-grid">
                        <div class="stat-card">
                            <div class="icon-container icon-yellow"><i data-feather="mail"></i></div>
                            <div class="info">
                                <div class="title">Chiến dịch</div>
                                <div class="value">474</div>
                            </div>
                            <div class="percentage">+5.62%</div>
                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-blue"><i data-feather="send"></i></div>
                            <div class="info">
                                <div class="title">Đã gửi</div>
                                <div class="value">454</div>
                            </div>
                            <div class="percentage">+4.12%</div>
                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-red"><i data-feather="eye"></i></div>
                            <div class="info">
                                <div class="title">Đã mở</div>
                                <div class="value">650</div>
                            </div>
                            <div class="percentage">+3.14%</div>
                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-green"><i data-feather="check-circle"></i></div>
                            <div class="info">
                                <div class="title">Hoàn thành</div>
                                <div class="value">650</div>
                            </div>
                            <div class="percentage">+6.27%</div>
                        </div>
                    </section>

                    <%-- Tabs điều hướng --%>
                    <nav class="campaign-tabs">
                        <ul>
                            <li><a href="#">Chiến dịch hoạt động 24</a></li>
                            <li><a href="#">Chiến dịch đã hoàn thành</a></li>
                            <li><a href="#" class="active">Chiến dịch đã lưu trữ</a></li>
                        </ul>
                    </nav>

                    <%-- Thanh công cụ của bảng --%>
                    <section class="campaign-toolbar">
                        <div class="toolbar-top">
                            <div class="search-bar">
                                <i data-feather="search"></i>
                                <input type="text" placeholder="Tìm kiếm chiến dịch...">
                            </div>
                            <a href="#" class="btn-add-campaign">
                                <i data-feather="plus"></i>
                                Thêm chiến dịch mới
                            </a>
                        </div>
                        <div class="toolbar-bottom">
                            <div class="filter-group">
                                <button class="filter-btn"><i data-feather="bar-chart-2" style="transform: rotate(90deg);"></i> Sắp xếp theo</button>
                                <button class="filter-btn"><i data-feather="calendar"></i> 23 Th06 25 - 22 Th07 25</button>
                                <button class="filter-btn"><i data-feather="filter"></i> Bộ lọc</button>
                            </div>
                            <button class="filter-btn btn-manage-columns">
                                <i data-feather="columns"></i> Quản lý cột
                            </button>
                        </div>
                    </section>

                    <%-- Bảng dữ liệu --%>
                    <section class="data-table-container">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th><input type="checkbox"> Tên <i data-feather="arrow-down-up" class="sort-icon"></i></th>
                                    <th>Loại <i data-feather="arrow-down-up" class="sort-icon"></i></th>
                                    <th>Tiến trình <i data-feather="arrow-down-up" class="sort-icon"></i></th>
                                    <th>Thành viên <i data-feather="arrow-down-up" class="sort-icon"></i></th>
                                    <th>Trạng thái <i data-feather="arrow-down-up" class="sort-icon"></i></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><input type="checkbox"> <strong>Phân phối</strong></td>
                                    <td>Quan hệ công chúng</td>
                                    <td class="progress-cell">
                                        <div class="progress-item"><span class="progress-value">40.5%</span><span class="progress-label">Đã mở</span></div>
                                        <div class="progress-item"><span class="progress-value">30.5%</span><span class="progress-label">Hủy ĐK</span></div>
                                        <div class="progress-item"><span class="progress-value">70.5%</span><span class="progress-label">Đã giao</span></div>
                                    </td>
                                    <td class="members-cell">
                                        <div class="avatar-stack">
                                            <div class="avatar"><img src="https://i.pravatar.cc/40?img=1" alt="avatar"></div>
                                            <div class="avatar"><img src="https://i.pravatar.cc/40?img=2" alt="avatar"></div>
                                            <div class="avatar"><img src="https://i.pravatar.cc/40?img=3" alt="avatar"></div>
                                            <div class="avatar avatar-more">3+</div>
                                        </div>
                                    </td>
                                    <td>Đã lưu trữ</td>
                                </tr>
                                <tr>
                                    <td><input type="checkbox"> <strong>Ra mắt sản phẩm mới</strong></td>
                                    <td>Tiếp thị</td>
                                    <td class="progress-cell">
                                        <div class="progress-item"><span class="progress-value">65.2%</span><span class="progress-label">Đã mở</span></div>
                                        <div class="progress-item"><span class="progress-value">10.1%</span><span class="progress-label">Hủy ĐK</span></div>
                                        <div class="progress-item"><span class="progress-value">92.0%</span><span class="progress-label">Đã giao</span></div>
                                    </td>
                                    <td class="members-cell">
                                        <div class="avatar-stack">
                                            <div class="avatar"><img src="https://i.pravatar.cc/40?img=4" alt="avatar"></div>
                                            <div class="avatar"><img src="https://i.pravatar.cc/40?img=5" alt="avatar"></div>
                                        </div>
                                    </td>
                                    <td>Đã lưu trữ</td>
                                </tr>
                            </tbody>
                        </table>
                    </section>
                </main>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace({
                    width: '1em',
                    height: '1em'
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>