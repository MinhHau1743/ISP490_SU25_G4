<%-- 
    Document   : listCustomer
    Created on : Jun 17, 2025, 11:14:37 AM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="listCustomer" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách hàng</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/listCustomer.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <h1 class="title">Danh sách Khách hàng</h1>
                    </div>
                    <div class="header-actions">
                        <button class="notification-btn"><i data-feather="bell"></i></button>
                    </div>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <form class="table-toolbar">
                            <div class="search-box">
                                <i data-feather="search" class="feather-search"></i>
                                <input type="text" placeholder="Tìm kiếm khách hàng...">
                            </div>
                            <div class="toolbar-actions">
                                <a href="createCustomer.jsp" class="btn btn-primary"><i data-feather="plus"></i>Thêm Khách hàng</a>
                            </div>
                        </form>
                    </div>

                    <div class="customer-board-container">
                        <div class="customer-board">

                            <%-- Cột 1: Khách hàng VIP --%>
                            <div class="kanban-column vip">
                                <div class="kanban-column-header">
                                    <span class="status-dot"></span>
                                    <h2 class="column-title">Khách hàng VIP</h2>
                                    <span class="column-count">${customerColumns['vip'].size()}</span>
                                </div>
                                <div class="kanban-cards">
                                    <c:forEach var="customer" items="${customerColumns['vip']}">
                                        <div class="customer-kanban-card">
                                            <h3 class="card-title">${customer.name}</h3>
                                            <div class="card-info-row"><i data-feather="phone"></i><span>${customer.phone}</span></div>
                                            <div class="card-info-row"><i data-feather="map-pin"></i><span>${customer.address}</span></div>
                                            <div class="card-footer">
                                                <div class="card-assignees">
                                                    <c:forEach var="assignee" items="${customer.assignees}">
                                                        <img src="${assignee.avatarUrl}" title="${assignee.fullName}">
                                                    </c:forEach>
                                                </div>
                                                <div class="card-actions">
                                                    <a href="viewCustomer?id=${customer.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                                    <a href="editCustomer?id=${customer.id}" title="Sửa thông tin"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <%-- Cột 2: Khách hàng Thân thiết --%>
                            <div class="kanban-column loyal">
                                <div class="kanban-column-header">
                                    <span class="status-dot"></span>
                                    <h2 class="column-title">Khách hàng Thân thiết</h2>
                                    <span class="column-count">${customerColumns['loyal'].size()}</span>
                                </div>
                                <div class="kanban-cards">
                                    <c:forEach var="customer" items="${customerColumns['loyal']}">
                                        <div class="customer-kanban-card">
                                            <h3 class="card-title">${customer.name}</h3>
                                            <div class="card-info-row"><i data-feather="phone"></i><span>${customer.phone}</span></div>
                                            <div class="card-info-row"><i data-feather="map-pin"></i><span>${customer.address}</span></div>
                                            <div class="card-footer">
                                                <div class="card-assignees">
                                                    <c:forEach var="assignee" items="${customer.assignees}">
                                                        <img src="${assignee.avatarUrl}" title="${assignee.fullName}">
                                                    </c:forEach>
                                                </div>
                                                <div class="card-actions">
                                                    <a href="viewCustomer?id=${customer.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                                    <a href="editCustomer?id=${customer.id}" title="Sửa thông tin"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <%-- Cột 3: Khách hàng Tiềm năng --%>
                            <div class="kanban-column potential">
                                <div class="kanban-column-header">
                                    <span class="status-dot"></span>
                                    <h2 class="column-title">Khách hàng Tiềm năng</h2>
                                    <span class="column-count">${customerColumns['potential'].size()}</span>
                                </div>
                                <div class="kanban-cards">
                                    <c:forEach var="customer" items="${customerColumns['potential']}">
                                        <div class="customer-kanban-card">
                                            <h3 class="card-title">${customer.name}</h3>
                                            <div class="card-info-row"><i data-feather="phone"></i><span>${customer.phone}</span></div>
                                            <div class="card-info-row"><i data-feather="map-pin"></i><span>${customer.address}</span></div>
                                            <div class="card-footer">
                                                <div class="card-assignees">
                                                    <c:forEach var="assignee" items="${customer.assignees}">
                                                        <img src="${assignee.avatarUrl}" title="${assignee.fullName}">
                                                    </c:forEach>
                                                </div>
                                                <div class="card-actions">
                                                    <a href="viewCustomer?id=${customer.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                                    <a href="editCustomer?id=${customer.id}" title="Sửa thông tin"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </main>
        </div>

        <%-- Modal xác nhận xóa --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">Xác nhận Xóa Khách hàng</h3>
                    <button class="close-modal-btn"><i data-feather="x"></i></button>
                </div>
                <div class="modal-body">
                    <i data-feather="alert-triangle" class="warning-icon"></i>
                    <p id="deleteMessage">Bạn có chắc chắn muốn xóa khách hàng này không?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                    <a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a>
                </div>
            </div>
        </div>

        <script src="../../js/listCustomer.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>