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

        <!-- Bootstrap & Feather Icons -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://unpkg.com/feather-icons"></script>
        <!-- Fonts -->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <!-- Styles -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listProduct.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    </head>
    <body>
        <style>

        </style>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header mb-4">
                    <div class="title-section">
                        <div class="title">Danh sách Sản phẩm</div>
                        <div class="breadcrumb">Sản phẩm / <span>Danh sách</span></div>

                    </div>
                    <button class="notification-btn"><i data-feather="bell"></i><span class="notification-badge"></span></button>
                </header>
                <c:if test="${not empty error}">
                    <div id="customAlert" class="alert alert-danger alert-dismissible fade show" role="alert" 
                         style="max-width: 1600px; margin-bottom: 20px; padding-right: 3rem; position: relative;">
                        ${error}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close" 
                                style="position: absolute; top: 10px; right: 10px;">
                            <span aria-hidden="true">&times;</span>
                        </button>

                        <!-- Progress bar -->
                        <div class="progress mt-2" style="height: 4px;">
                            <div id="alertProgressBar" class="progress-bar bg-danger" role="progressbar" 
                                 style="width: 100%; transition: width 5s linear;"></div>
                        </div>
                    </div>
                </c:if>

                <div class="page-content">
                    <div class="content-card">
                        <!-- Form tìm kiếm & lọc -->
                        <form action="ProductController" method="get">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search"></i>
                                    <input type="text" name="keyword" placeholder="Tìm kiếm tên, mã SP..." value="${param.keyword}">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn">
                                    <i data-feather="filter"></i><span>Bộ lọc</span>
                                </button>
                                <div class="toolbar-actions">
                                    <a href="createProduct" class="btn btn-primary">
                                        <i data-feather="plus"></i><span>Thêm Sản phẩm</span>
                                    </a>
                                    <!-- Nút mở modal -->
                                    <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addGroupModal">
                                        <i data-feather="plus-square"></i><span> Thêm nhóm hàng</span>
                                    </button>


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

                            <!-- Bộ lọc -->
                            <div class="filter-container" id="filterContainer" style="display: none;">
                                <div class="filter-controls">
                                    <div class="filter-group">
                                        <label>Khoảng giá (VNĐ)</label>
                                        <div class="price-inputs">
                                            <input type="text" name="minPrice" placeholder="Từ" value="${param.minPrice}">
                                            <span>-</span>
                                            <input type="text" name="maxPrice" placeholder="Đến" value="${param.maxPrice}">
                                        </div>
                                    </div>
                                    <div class="filter-group">
                                        <label for="origin-filter">Xuất xứ</label>
                                        <select id="origin-filter" name="origin">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="origin" items="${originList}">
                                                <option value="${origin}" ${param.origin == origin ? 'selected' : ''}>${origin}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label for="category-filter">Nhóm hàng</label>
                                        <select id="category-filter" name="categoryId">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="category" items="${categories}">
                                                <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <a href="ProductController" class="btn-reset-filter">Xóa lọc</a>
                                    <button type="submit" class="btn-apply-filter">Áp dụng</button>
                                </div>
                            </div>
                        </form>

                        <div class="product-grid">
                            <div class="view-mode-container">
                                <!-- Dạng lưới -->
                                <div id="productList" class="view-mode-grid" style="display: flex;">
                                    <c:forEach var="p" items="${productList}">
                                        <div class="product-card">
                                            <div class="card-image">
                                                <img class="modal-img" src="${pageContext.request.contextPath}/image/${p.image}" alt="Ảnh sản phẩm"
                                                     style="width: 100%; height: auto;"
                                                     onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'" />
                                            </div>
                                            <div class="card-content">
                                                <div class="card-header"><span class="product-name-header">${p.name}</span></div>
                                                <div class="card-body">
                                                    <div class="card-info-row"><i data-feather="tag"></i><span>Mã: ${p.productCode}</span></div>
                                                    <div class="card-info-row"><i data-feather="layers"></i><span>Danh mục: ${categoryMap[p.categoryId]}</span></div>
                                                    <div class="card-info-row"><i data-feather="package"></i><span>Xuất xứ: ${p.origin}</span></div>
                                                    <div class="card-info-row"><i data-feather="align-left"></i><span>Mô tả: ${p.description}</span></div>
                                                    <div class="card-info-row"><i data-feather="calendar"></i><span>Ngày tạo: ${p.createdAt}</span></div>
                                                    <div class="card-info-row"><i data-feather="refresh-cw"></i><span>Cập nhật: ${p.updatedAt}</span></div>
                                                    <div class="card-info-row"><i data-feather="user-plus"></i><span>Người tạo: ${p.createdBy}</span></div>
                                                    <div class="card-info-row"><i data-feather="edit"></i><span>Người cập nhật: ${p.updatedBy}</span></div>
                                                </div>
                                                <div class="card-footer">
                                                    <div class="product-price-footer price-ellipsis">
                                                        <span>
                                                            <fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND" minFractionDigits="0" maxFractionDigits="0"/>
                                                        </span>
                                                    </div>

                                                    <div class="action-buttons">
                                                        <a href="getProductById?id=${p.id}" title="Xem chi tiết" class="action-view">
                                                            <i data-feather="eye" style="stroke: #17a2b8;"></i>
                                                        </a>
                                                        <a href="editProduct?id=${p.id}" title="Chỉnh sửa" class="action-edit">
                                                            <i data-feather="edit-2" style="stroke: #ffc107;"></i>
                                                        </a>
                                                        <a href="deleteProduct" data-id="${p.id}" data-name="${p.name}" title="Xóa" class="action-delete delete-trigger-btn">
                                                            <i data-feather="trash-2" style="stroke: #dc3545;"></i>
                                                        </a>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <c:if test="${empty productList}">
                                        <p style="text-align: center; color: gray;">Không tìm thấy sản phẩm nào phù hợp.</p>
                                    </c:if>
                                </div>

                                <!-- Dạng bảng -->
                                <div id="productTable" class="view-mode-table" style="display: none;">
                                    <table class="table table-bordered table-hover w-100">
                                        <thead class="thead-light">
                                            <tr>
                                                <th>Tên</th>
                                                <th>Mã SP</th>
                                                <th>Danh mục</th>
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
                                                    <td>${categoryMap[p.categoryId]}</td>
                                                    <td>${p.origin}</td>
                                                    <td><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></td>
                                                    <td>${p.createdAt}</td>
                                                    <td>${p.updatedAt}</td>
                                                    <td>${p.updatedBy}</td>
                                                    <td>${p.updatedBy}</td>
                                                    <td class="text-center">
                                                        <div class="btn-group" role="group" aria-label="Hành động sản phẩm">
                                                            <!-- Xem -->
                                                            <a href="getProductById?id=${p.id}" title="Xem" class="btn btn-sm btn-info" data-toggle="tooltip">
                                                                <i data-feather="eye"></i>
                                                            </a>

                                                            <!-- Sửa -->
                                                            <a href="editProduct?id=${p.id}" title="Sửa" class="btn btn-sm btn-warning text-white" data-toggle="tooltip">
                                                                <i data-feather="edit-2"></i>
                                                            </a>

                                                            <!-- Xóa -->
                                                            <a href="deleteProduct" data-id="${p.id}" title="Xóa" class="btn btn-sm btn-danger delete-trigger-btn" data-toggle="tooltip">
                                                                <i data-feather="trash-2"></i>
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <jsp:include page="../../pagination.jsp"/>
                        <jsp:include page="../technicalSupport/createGroupProduct.jsp" />
                    </div>
                </div>
            </main>
        </div>

        <!-- Modal xác nhận xóa -->
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header"><h3 class="modal-title">Xác nhận xóa</h3><button class="close-modal-btn"><i data-feather="x"></i></button></div>
                <div class="modal-body"><i data-feather="alert-triangle" class="warning-icon"></i><p id="deleteMessage">Bạn có chắc chắn muốn xóa sản phẩm này?</p></div>
                <div class="modal-footer"><button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button><a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a></div>
            </div>
        </div>

        <!-- Scripts -->
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/listProduct.js"></script>

    </body>
</html>
