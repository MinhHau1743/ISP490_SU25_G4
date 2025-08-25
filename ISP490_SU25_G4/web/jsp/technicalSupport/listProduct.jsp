<%-- 
    Document   : listProduct
    Created on : Jun 17, 2025
    Author     : Hai Huy
    Description: Restructured layout and added HTML5 validation.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="currentPage" value="listProduct" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh sách Sản phẩm</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listProduct.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Danh sách Sản phẩm"/>
                </jsp:include>

                <c:if test="${not empty error}">
                    <div id="customAlert" class="alert alert-danger alert-dismissible fade show" role="alert" 
                         style="max-width: 1600px; margin: 0 24px 20px; padding-right: 3rem; position: relative;">
                        ${error}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close" 
                                style="position: absolute; top: 10px; right: 10px;">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <div class="progress mt-2" style="height: 4px;">
                            <div id="alertProgressBar" class="progress-bar bg-danger" role="progressbar" 
                                 style="width: 100%; transition: width 5s linear;"></div>
                        </div>
                    </div>
                </c:if>

                <div class="page-content">
                    <div class="content-card">
                        <form action="product" method="get">
                            <input type="hidden" name="action" value="list">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <input type="text" name="keyword" placeholder="Tìm kiếm tên, mã SP..." value="${keyword}">
                                    <i data-feather="search"></i>
                                </div>
                                <button type="button" class="filter-button" id="filterBtn">
                                    <i data-feather="filter"></i><span>Bộ lọc</span>
                                </button>
                                <div class="toolbar-actions">
                                    <%-- Chỉ Admin hoặc Kĩ thuật mới thấy nút Thêm --%>
                                    <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kĩ thuật'}">
                                        <a href="product?action=create" class="btn btn-primary">
                                            <i data-feather="plus"></i><span>Thêm Sản phẩm</span>
                                        </a>
                                    </c:if>
                                    <div class="btn-group ml-3 view-toggle">
                                        <button type="button" class="btn btn-outline-secondary btn-sm active" id="gridViewBtn" title="Xem dạng lưới">
                                            <i data-feather="grid"></i>
                                        </button>
                                        <button type="button" class="btn btn-outline-secondary btn-sm" id="tableViewBtn" title="Xem dạng bảng">
                                            <i data-feather="list"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div class="filter-container" id="filterContainer" style="display: none;">
                                <div class="filter-controls">
                                    <div class="filter-group">
                                        <label>Khoảng giá (VNĐ)</label>
                                        <div class="price-inputs">
                                            <input type="text" name="minPrice" placeholder="Từ" value="${minPrice}">
                                            <span>-</span>
                                            <input type="text" name="maxPrice" placeholder="Đến" value="${maxPrice}">
                                        </div>
                                    </div>
                                    <div class="filter-group">
                                        <label for="origin-filter">Xuất xứ</label>
                                        <select id="origin-filter" name="origin">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="originItem" items="${originList}">
                                                <option value="${originItem}" ${origin == originItem ? 'selected' : ''}>${originItem}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <a href="product?action=list" class="btn-reset-filter">Xóa lọc</a>
                                    <button type="submit" class="btn-apply-filter">Áp dụng</button>
                                </div>
                            </div>
                        </form>

                        <div class="product-grid">
                            <div class="view-mode-container">
                                <div id="productList" class="view-mode-grid" style="display: flex;">
                                    <c:forEach var="p" items="${productList}">
                                        <div class="product-card">
                                            <div class="card-image">
                                                <img class="modal-img"
                                                     src="${pageContext.request.contextPath}/image/${empty p.image ? 'na.jpg' : p.image}?v=${p.updatedAt.time}"
                                                     alt="Ảnh sản phẩm"
                                                     style="width: 100%; height: auto;"
                                                     decoding="async" loading="lazy" fetchpriority="low"
                                                     onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'" />
                                            </div>
                                            <div class="card-content">
                                                <div class="card-header"><span class="product-name-header">${p.name}</span></div>
                                                <div class="card-body">
                                                    <div class="card-info-row"><i data-feather="tag"></i><span>Mã: ${p.productCode}</span></div>
                                                    <div class="card-info-row"><i data-feather="package"></i><span>Xuất xứ: ${p.origin}</span></div>
                                                    <div class="card-info-row"><i data-feather="align-left"></i><span>Mô tả: ${p.description}</span></div>
                                                    <div class="card-info-row"><i data-feather="calendar"></i><span>Ngày tạo: <fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                                                    <div class="card-info-row"><i data-feather="refresh-cw"></i><span>Cập nhật: <fmt:formatDate value="${p.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></span></div>
                                                    <div class="card-info-row"><i data-feather="user-plus"></i><span>Người tạo: ${p.createdByName}</span></div>
                                                    <div class="card-info-row"><i data-feather="edit"></i><span>Người cập nhật: ${p.updatedByName}</span></div>
                                                </div>
                                                <div class="card-footer">
                                                    <div class="product-price-footer price-ellipsis">
                                                        <span>
                                                            <fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND" minFractionDigits="0" maxFractionDigits="0"/>
                                                        </span>
                                                    </div>
                                                    <div class="action-buttons">
                                                        <%-- Nút Xem --%>
                                                        <a href="product?action=view&id=${p.id}" title="Xem chi tiết" class="action-view">
                                                            <i data-feather="eye"></i>
                                                        </a>

                                                        <%-- Nút Sửa (hiển thị cho Admin và Kĩ thuật) --%>
                                                        <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kĩ thuật'}">
                                                            <a href="product?action=edit&id=${p.id}" title="Chỉnh sửa" class="action-edit">
                                                                <i data-feather="edit-2"></i>
                                                            </a>
                                                        </c:if>

                                                        <%-- Nút Xóa (chỉ hiển thị cho Admin) --%>
                                                        <c:if test="${sessionScope.userRole == 'Admin'}">
                                                            <a href="#" data-id="${p.id}" data-name="${p.name}" title="Xóa" class="action-delete delete-trigger-btn">
                                                                <i data-feather="trash-2"></i>
                                                            </a>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <c:if test="${empty productList}">
                                        <p style="text-align: center; color: gray;">Không tìm thấy sản phẩm nào phù hợp.</p>
                                    </c:if>
                                </div>

                                <div id="productTable" class="view-mode-table" style="display: none;">
                                    <table class="table table-bordered table-hover w-100">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>Tên</th>
                                                <th>Mã SP</th>
                                                <th>Xuất xứ</th>
                                                <th>Giá</th>
                                                <th>Ngày tạo</th>
                                                <th>Cập nhật</th>
                                                <th>Người tạo</th>
                                                <th>Người cập nhật</th>
                                                <th>Hành động</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${productList}">
                                                <tr>
                                                    <td>${p.name}</td>
                                                    <td>${p.productCode}</td>
                                                    <td>${p.origin}</td>
                                                    <td><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></td>
                                                    <td><fmt:formatDate value="${p.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                    <td><fmt:formatDate value="${p.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                    <td>${p.createdByName}</td>
                                                    <td>${p.updatedByName}</td>
                                                    <td class="text-center">
                                                        <div class="action-buttons" role="group" aria-label="Hành động sản phẩm">
                                                            <a href="product?action=view&id=${p.id}" title="Xem chi tiết" class="action-view">
                                                                <i data-feather="eye"></i>
                                                            </a>
                                                            <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kĩ thuật'}">
                                                                <a href="product?action=edit&id=${p.id}" title="Chỉnh sửa" class="action-edit">
                                                                    <i data-feather="edit-2"></i>
                                                                </a>
                                                            </c:if>
                                                            <c:if test="${sessionScope.userRole == 'Admin'}">
                                                                <a href="#" data-id="${p.id}" data-name="${p.name}" title="Xóa" class="action-delete delete-trigger-btn">
                                                                    <i data-feather="trash-2"></i>
                                                                </a>
                                                            </c:if>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <jsp:include page="/pagination.jsp"/>
                    </div>
                </div>
            </main>
        </div>

        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header"><h3 class="modal-title">Xác nhận xóa</h3><button class="close-modal-btn"><i data-feather="x"></i></button></div>
                <div class="modal-body"><i data-feather="alert-triangle" class="warning-icon"></i><p id="deleteMessage">Bạn có chắc chắn muốn xóa sản phẩm này?</p></div>
                <div class="modal-footer"><button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button><a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a></div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // Logic cho modal xóa (nếu bạn muốn dùng action=delete thay vì JS)
                // Bạn có thể cần sửa lại script này để phù hợp hơn
                const deleteButtons = document.querySelectorAll('.delete-trigger-btn');
                const modal = document.getElementById('deleteConfirmModal');
                const confirmBtn = document.getElementById('confirmDeleteBtn');
                const cancelBtn = document.getElementById('cancelDeleteBtn');
                const closeBtn = modal.querySelector('.close-modal-btn');
                const deleteMessage = document.getElementById('deleteMessage');

                deleteButtons.forEach(button => {
                    button.addEventListener('click', function (e) {
                        e.preventDefault();
                        const productId = this.getAttribute('data-id');
                        const productName = this.getAttribute('data-name'); // Lấy tên sản phẩm
                        deleteMessage.textContent = `Bạn có chắc chắn muốn xóa sản phẩm "${productName}"?`;
                        confirmBtn.href = `product?action=delete&id=${productId}`;
                        modal.style.display = 'flex';
                    });
                });

                const closeModal = () => modal.style.display = 'none';
                cancelBtn.addEventListener('click', closeModal);
                closeBtn.addEventListener('click', closeModal);
                modal.addEventListener('click', function (e) {
                    if (e.target === modal) {
                        closeModal();
                    }
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/listProduct.js"></script>
    </body>
</html>