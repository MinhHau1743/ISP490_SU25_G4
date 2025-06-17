<%-- 
    Document   : editProductDetail
    Created on : Jun 17, 2025, 10:28:17 AM
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
        <title>Chỉnh sửa - ${product.name}</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/editProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">

                        <form action="product" method="post" enctype="multipart/form-data">
                            <%-- Các input ẩn quan trọng để xác định hành động và đối tượng --%>
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${product.id}">

                            <div class="edit-header">
                                <h1 class="page-title">Chỉnh sửa Sản phẩm</h1>
                                <div class="action-buttons">
                                    <a href="viewProductDetail.jsp" class="btn btn-secondary">Hủy</a>
                                    <button type="submit" class="btn btn-primary">
                                        <i data-feather="save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </div>

                            <c:if test="${not empty product}">
                                <div class="product-edit-container">
                                    <div class="image-column">
                                        <div class="form-section">
                                            <h2 class="form-section-title">Hình ảnh sản phẩm</h2>
                                            <div class="image-list">
                                                <c:forEach var="imgUrl" items="${product.images}">
                                                    <div class="image-item">
                                                        <img src="${imgUrl}" alt="Product Image">
                                                        <button type="button" class="delete-img-btn" title="Xóa ảnh"><i data-feather="trash-2"></i></button>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <label for="imageUpload" class="upload-box" style="margin-top: 16px;">
                                                <i data-feather="upload-cloud"></i>
                                                <p>Tải lên ảnh mới</p>
                                            </label>
                                            <input type="file" id="imageUpload" name="newImages" multiple>
                                            <small style="color: var(--text-tertiary); font-size: 12px; margin-top: 8px;">Logic xóa và tải ảnh mới cần được xử lý ở backend.</small>
                                        </div>

                                        <div class="form-section">
                                            <h2 class="form-section-title">Mô tả chi tiết</h2>
                                            <div class="form-group full-width">
                                                <textarea name="longDescription" class="form-control" rows="10">${product.longDescription}</textarea>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-column">
                                        <div class="form-section">
                                            <h2 class="form-section-title">Thông tin chung</h2>
                                            <div class="form-group full-width">
                                                <label class="form-label" for="productName">Tên sản phẩm</label>
                                                <input type="text" id="productName" name="name" class="form-control" value="${product.name}" required>
                                            </div>
                                            <div class="form-group full-width">
                                                <label class="form-label" for="shortDescription">Mô tả ngắn</label>
                                                <textarea id="shortDescription" name="shortDescription" class="form-control" rows="3">${product.shortDescription}</textarea>
                                            </div>
                                            <div class="form-grid">
                                                <div class="form-group">
                                                    <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                    <input type="text" id="productCode" name="productCode" class="form-control" value="${product.productCode}">
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label" for="price">Giá bán (VNĐ)</label>
                                                    <input type="number" id="price" name="price" class="form-control" value="${product.price}">
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label" for="stockQuantity">Số lượng tồn kho</label>
                                                    <input type="number" id="stockQuantity" name="stockQuantity" class="form-control" value="${product.stockQuantity}">
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label" for="status">Trạng thái</label>
                                                    <select id="status" name="status" class="form-control">
                                                        <option value="IN_STOCK" ${product.status == 'IN_STOCK' ? 'selected' : ''}>Còn hàng</option>
                                                        <option value="OUT_OF_STOCK" ${product.status == 'OUT_OF_STOCK' ? 'selected' : ''}>Hết hàng</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label" for="category">Danh mục</label>
                                                    <select id="category" name="categoryId" class="form-control">
                                                        <c:forEach var="cat" items="${categoryList}">
                                                            <option value="${cat.id}" ${product.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label" for="origin">Xuất xứ</label>
                                                    <select id="origin" name="originId" class="form-control">
                                                        <c:forEach var="ori" items="${originList}">
                                                            <option value="${ori.id}" ${product.originId == ori.id ? 'selected' : ''}>${ori.name}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-section">
                                            <h2 class="form-section-title">Thông số kỹ thuật</h2>
                                            <table class="specs-table-edit">
                                                <thead>
                                                    <tr><th>Tên thông số</th><th>Giá trị</th><th></th></tr>
                                                </thead>
                                                <tbody id="specs-tbody">
                                                    <c:forEach var="spec" items="${product.specifications}">
                                                        <tr>
                                                            <td><input type="text" name="spec_key" class="form-control" value="${spec.key}"></td>
                                                            <td><input type="text" name="spec_value" class="form-control" value="${spec.value}"></td>
                                                            <td><button type="button" class="btn-delete-spec" title="Xóa thông số"><i data-feather="x-circle"></i></button></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                            <button type="button" id="add-spec-btn" class="btn-add-spec">
                                                <i data-feather="plus"></i> Thêm thông số
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${empty product}">
                                <p>Không tìm thấy sản phẩm để chỉnh sửa.</p>
                            </c:if>
                        </form>
                    </div>
                </div>
            </main>
        </div>

        <script src="../../js/editProductDetail.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
