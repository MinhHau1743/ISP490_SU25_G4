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

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Be+Vietnam+Pro:wght@600&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/listProduct.css">
        <link rel="stylesheet" href="../../css/pagination.css">
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
                        <form action="product" method="get">
                            <div class="table-toolbar">
                                <div class="search-box">
                                    <i data-feather="search" class="feather-search"></i>
                                    <%-- Giữ lại giá trị tìm kiếm cũ --%>
                                    <input type="text" name="searchQuery" placeholder="Tìm kiếm tên, mã SP..." value="${param.searchQuery}">
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
                                            <%-- Giả sử bạn có một list tên là 'categoryList' được truyền từ servlet --%>
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
                            <c:forEach var="p" items="${productList}">
                                <div class="product-card">
                                    <div class="card-image">
                                        <a href="productDetail?id=${p.id}">
                                            <%-- Sử dụng imageUrl từ đối tượng sản phẩm, nếu không có thì dùng ảnh mặc định --%>
                                            <img src="${not empty p.imageUrl ? p.imageUrl : 'https://placehold.co/400x300/E0E0E0/757575?text=No+Image'}" alt="${p.name}">
                                        </a>
                                    </div>
                                    <div class="card-content">
                                        <div class="card-header">
                                            <a href="productDetail?id=${p.id}" class="product-name-link">
                                                <span class="product-name-header">${p.name}</span>
                                            </a>
                                            <%-- Logic hiển thị trạng thái sản phẩm --%>
                                            <c:choose>
                                                <c:when test="${p.status == 'IN_STOCK'}">
                                                    <span class="status-pill status-instock">Còn hàng</span>
                                                </c:when>
                                                <c:when test="${p.status == 'LOW_STOCK'}">
                                                    <span class="status-pill status-lowstock">Sắp hết hàng</span>
                                                </c:when>
                                                <c:when test="${p.status == 'OUT_OF_STOCK'}">
                                                    <span class="status-pill status-outofstock">Hết hàng</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-pill">${p.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="card-body">
                                            <div class="card-info-row"><i data-feather="tag"></i><span class="info-value">Mã: ${p.productCode}</span></div>
                                            <div class="card-info-row"><i data-feather="layers"></i><span class="info-value">Danh mục: ${p.categoryName}</span></div>
                                            <div class="card-info-row"><i data-feather="package"></i><span class="info-value">Xuất xứ: ${p.originName}</span></div>
                                        </div>
                                        <div class="card-footer">
                                            <div class="product-price-footer">
                                                <i data-feather="dollar-sign"></i>
                                                <span><fmt:formatNumber value="${p.price}" type="currency" currencyCode="VND"/></span>
                                            </div>
                                            <div class="action-buttons">
                                                <a href="../technicalSupport/viewProductDetail.jsp" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="../technicalSupport/editProductDetail.jsp" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger-btn" data-id="${p.id}" data-name="${p.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                            <%-- Kết thúc vòng lặp --%>

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

        <script src="../../js/listProduct.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>