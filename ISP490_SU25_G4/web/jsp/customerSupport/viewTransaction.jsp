<%-- 
    Document   : viewTransaction
    Created on : Jun 14, 2025, 1:33:31 PM
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
        <title>Chi tiết giao dịch - ${transaction.transactionCode}</title>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/viewTransaction.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">

                    <div class="detail-header">
                        <a href="../customerSupport/listTransaction.jsp" class="back-link">
                            <i data-feather="arrow-left"></i>
                            <span>Quay lại danh sách</span>
                        </a>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <a href="../customerSupport/editTransaction.jsp" class="btn btn-secondary">
                                <i data-feather="edit-2"></i>Sửa
                            </a>
                            <a href="#" class="btn btn-primary">
                                <i data-feather="printer"></i>In Phiếu
                            </a>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin chung</h2>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <span class="label">Mã giao dịch</span>
                                        <div class="value">${transaction.transactionCode}</div>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">Mã hợp đồng</span>
                                        <div class="value">${transaction.contractCode}</div>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">Khách hàng</span>
                                        <div class="value">${transaction.customer}</div>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">Loại giao dịch</span>
                                        <div class="value">${transaction.type}</div>
                                    </div>
                                    <div class="info-item full-width">
                                        <span class="label">Mô tả chi tiết</span>
                                        <div class="value">${transaction.description}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <table class="device-table">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị</th>
                                            <th>Serial Number</th>
                                            <th>Ghi chú xử lý</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="device" items="${transaction.devices}">
                                            <tr>
                                                <td>${device.name}</td>
                                                <td>${device.serial}</td>
                                                <td>${device.notes}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- CỘT PHỤ (BÊN PHẢI) -->
                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h2 class="card-title">Chi tiết Trạng thái</h2>

                                <%-- BỐ CỤC MỚI CHO KHUNG BÊN PHẢI --%>
                                <div class="info-item">
                                    <span class="label">Trạng thái</span>
                                    <span class="value">
                                        <span class="status-pill ${transaction.statusClass}">${transaction.status}</span>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Mức độ ưu tiên</span>
                                    <span class="value">
                                        <span class="priority-pill ${transaction.priorityClass}">${transaction.priority}</span>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Nhân viên phụ trách</span>
                                    <span class="value">${transaction.employee}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Ngày tạo</span>
                                    <span class="value">${transaction.createdDate}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Tính phí?</span>
                                    <span class="value">
                                        <c:if test="${transaction.isBillable}">Có</c:if>
                                        <c:if test="${not transaction.isBillable}">Không (Bảo hành)</c:if>
                                        </span>
                                    </div>
                                </div>

                                <div class="detail-card">
                                    <h2 class="card-title">Lịch sử cập nhật</h2>
                                    <ul class="history-list">
                                    <c:forEach var="item" items="${transaction.history}">
                                        <li class="history-item">
                                            <div class="history-time">${item.time}</div>
                                            <div class="history-action"><strong>${item.user}</strong>: ${item.action}</div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // 1. Kích hoạt Feather Icons
                feather.replace();

                // 2. Logic cho nút Toggle Menu (Đóng/Mở Sidebar)
                const toggleBtn = document.querySelector('.toggle-btn');
                const appContainer = document.querySelector('.app-container');

                if (toggleBtn && appContainer) {
                    toggleBtn.addEventListener('click', function () {
                        appContainer.classList.toggle('sidebar-collapsed');
                    });
                }

                // 3. Logic cho các mục Dropdown trong Menu
                const dropdownLinks = document.querySelectorAll('.sidebar-nav .nav-item-dropdown > a');

                if (dropdownLinks.length > 0) {
                    dropdownLinks.forEach(function (link) {
                        link.addEventListener('click', function (event) {
                            // Ngăn thẻ <a> điều hướng khi href="#"
                            if (this.getAttribute('href') === '#') {
                                event.preventDefault();
                            }

                            const parentLi = this.parentElement;
                            if (parentLi) {
                                parentLi.classList.toggle('open');
                            }
                        });
                    });
                }
            });
        </script>
    </body>
</html>

