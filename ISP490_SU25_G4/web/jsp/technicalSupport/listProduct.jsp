<%-- 
    Document   : listProduct
    Created on : Jun 14, 2025, 1:36:17 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="currentPage" value="listProduct" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách Hàng hóa</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Be+Vietnam+Pro:wght@600&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listProduct.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">

    </head>
    <body>

        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <div class="title">Danh sách Sản phẩm</div>
                        <div class="breadcrumb">Sản phẩm / <span>Danh sách</span></div>
                    </div>
                    <button class="notification-btn"><i data-feather="bell"></i><span class="notification-badge"></span></button>
                </header>

                <div class="page-content">
                    <div class="content-card">
                        <%-- Form tìm kiếm và lọc --%>
                        <form action="productController" method="get">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <%-- Giữ lại giá trị tìm kiếm cũ --%>
                                    <input type="text" name="searchQuery" id="searchProducts" placeholder="Tìm kiếm tên, mã SP..." value="${param.searchQuery}">
                                </div>
                                <button type="button" class="filter-button" id="filterBtn"><i data-feather="filter"></i><span>Bộ lọc</span></button>
                                <div class="toolbar-actions">
                                    <a href="../technicalSupport/createProduct.jsp" class="btn btn-primary">
                                        <i data-feather="plus"></i>
                                        <span>Thêm Sản phẩm</span>
                                    </a>
                                    <a href="../technicalSupport/createGroupProduct.jsp" class="btn btn-primary">
                                        <i data-feather="plus-square"></i>
                                        <span>Thêm nhóm hàng</span>
                                    </a>
                                </div>
                            </div>

                            <%-- Container của bộ lọc --%>
                            <div class="filter-container" id="filterContainer" style="display: none;">
                                <div class="filter-controls">
                                    <div class="filter-group">
                                        <label>Khoảng giá (VNĐ)</label>
                                        <div class="price-inputs">
                                            <%-- Giữ lại giá trị khoảng giá cũ --%>
                                            <input type="number" name="minPrice" placeholder="Từ" value="${param.minPrice}"><span>-</span><input type="number" name="maxPrice" placeholder="Đến" value="${param.maxPrice}">
                                        </div>
                                    </div>
                                    <div class="filter-group">
                                        <label for="origin-filter">Xuất xứ</label>
                                        <select id="origin-filter" name="originId">
                                            <option value="">Tất cả</option>
                                            <%-- Giả sử bạn có một list tên là 'originList' được truyền từ servlet --%>
                                            <c:forEach var="origin" items="${originList}">
                                                <option value="${origin.id}" ${param.originId == origin.id ? 'selected' : ''}>${origin.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label for="category-filter">Nhóm hàng</label>
                                        <select id="category-filter" name="categoryId">
                                            <option value="">Tất cả</option>
                                            <c:forEach var="category" items="${categoryList}">
                                                <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="filter-actions">
                                    <a href="product" class="btn-reset-filter">Xóa lọc</a>
                                    <button type="submit" class="btn-apply-filter">Áp dụng</button>
                                </div>
                            </div>
                        </form>

                        <%-- Lưới hiển thị sản phẩm --%>
                        <div class="product-grid">

                            <%-- KIỂM TRA NẾU KHÔNG CÓ SẢN PHẨM --%>
                            <c:if test="${empty productList}">
                                <p style="grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">Không tìm thấy sản phẩm nào phù hợp.</p>
                            </c:if>

                            <%-- LẶP QUA DANH SÁCH SẢN PHẨM VÀ HIỂN THỊ --%>
                            <div id="productList">
                                <c:forEach var="entry" items="${productImageMap}">
                                    <c:set var="p" value="${entry.key}" />
                                    <c:set var="imageFileName" value="${entry.value}" />

                                    <div class="product-card">
                                        <div class="card-image">
                                            <img id="myImg" src="${pageContext.request.contextPath}/image/${imageFileName}"
                                                 alt="Ảnh sản phẩm"
                                                 style="width: 100%; height: auto;"
                                                 onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'" />
                                        </div>

                                        <div class="card-content">
                                            <div class="card-header">
                                                <span class="product-name-header">${p.name}</span>
                                            </div>
                                            <div class="card-body">
                                                <div class="card-info-row"><i data-feather="tag"></i><span class="info-value">Mã: ${p.productCode}</span></div>
                                                <div class="card-info-row"><i data-feather="layers"></i><span class="info-value">Danh mục: ${p.getCategoryName()}</span></div>
                                                <div class="card-info-row"><i data-feather="package"></i><span class="info-value">Xuất xứ: ${p.origin}</span></div>
                                                <div class="card-info-row"><i data-feather="align-left"></i><span class="info-value">Mô tả: ${p.description}</span></div>
                                                <div class="card-info-row"><i data-feather="calendar"></i><span class="info-value">Ngày tạo: ${p.createdAt}</span></div>
                                                <div class="card-info-row"><i data-feather="refresh-cw"></i><span class="info-value">Cập nhật: ${p.updatedAt}</span></div>
                                                <div class="card-info-row"><i data-feather="shield"></i>
                                                    <span class="info-value">Đã xóa: 
                                                        <c:choose>
                                                            <c:when test="${p.isDeleted}">Có</c:when>
                                                            <c:otherwise>Không</c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                </div>
                                            </div>

                                            <div class="card-footer">
                                                <div class="product-price-footer">
                                                    <span><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></span>
                                                </div>
                                                <div class="action-buttons">
                                                    <a href="ProductController?service=getProductById&id=${p.id}" title="Xem"><i data-feather="eye"></i></a>
                                                    <a href="ProductController?service=getProductToEdit&id=${p.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger-btn" data-id="${p.id}" data-name="${p.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                                <div id="myModal" class="modal">
                                    <span class="close">&times;</span>
                                    <img class="modal-content" id="img01">
                                    <div id="caption"></div>
                                </div>
                                <p id="noResultMsg" style="display:none; grid-column: 1 / -1; text-align: center; color: var(--text-secondary);">
                                    Không tìm thấy sản phẩm nào phù hợp.
                                </p>
                            </div>

                        </div>

                        <jsp:include page="../../pagination.jsp"/>
                    </div>
                </div>
            </main>
        </div>

        <%-- Modal xác nhận xóa (giữ nguyên) --%>
        <div id="deleteConfirmModal" class="modal-overlay">
            <div class="modal-content">
                <div class="modal-header"><h3 class="modal-title">Xác nhận xóa</h3><button class="close-modal-btn"><i data-feather="x"></i></button></div>
                <div class="modal-body"><i data-feather="alert-triangle" class="warning-icon"></i><p id="deleteMessage">Bạn có chắc chắn muốn xóa sản phẩm này?</p></div>
                <div class="modal-footer"><button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button><a href="#" class="btn btn-danger" id="confirmDeleteBtn">Xóa</a></div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/listProduct.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>