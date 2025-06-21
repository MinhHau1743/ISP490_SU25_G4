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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">

                        <form action="ProductController" method="post" enctype="multipart/form-data">

                            <%-- Các input ẩn quan trọng để xác định hành động và đối tượng --%>
                            <input type="hidden" name="service" value="editProduct">
                            <input type="hidden" name="id" value="${product.id}">

                            <div class="edit-header">
                                <h1 class="page-title">Chỉnh sửa Sản phẩm</h1>
                                <div class="action-buttons">
                                    <a href="ProductController" class="btn btn-secondary">Hủy</a>
                                    <button type="submit" class="btn btn-primary">
                                        <i data-feather="save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </div>

                            <c:if test="${not empty product}">
                                <div class="form-column">
                                    <div class="product-edit-container">
                                        <div class="image-column">
                                            <div class="form-section">
                                                <h2 class="form-section-title">Hình ảnh sản phẩm</h2>

                                                <div class="image-list" style="display: flex; gap: 24px; align-items: center; flex-wrap: wrap;">
                                                    <!-- Ảnh đang có -->
                                                    <img id="productImagePreview"
                                                         src="${pageContext.request.contextPath}/image/${imageFileName}"
                                                         alt="Ảnh sản phẩm"
                                                         style="width: 260px; height: auto; border: 1px solid #ccc; border-radius: 8px;"
                                                         onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'" />

                                                    <!-- Ô tải ảnh -->
                                                    <label for="imageUpload" class="upload-box" style="cursor: pointer; width: 160px; height: 160px; display: flex; flex-direction: column; justify-content: center; align-items: center; border: 2px dashed #ccc; border-radius: 8px;">
                                                        <i data-feather="upload-cloud" style="width: 32px; height: 32px;"></i>
                                                        <p style="margin: 8px 0 0;">Tải lên ảnh mới</p>
                                                    </label>
                                                    <input type="file" id="imageUpload" name="image" style="display: none;">
                                                </div>

                                            </div>
                                        </div>

                                    </div>
                                    <input type="hidden" name="oldImage" value="${imageFileName}">



                                    <div class="form-section">
                                        <h2 class="form-section-title">Thông tin chung</h2>

                                        <div class="form-group full-width">
                                            <label class="form-label" for="productName">Tên sản phẩm</label>
                                            <input type="text" id="productName" name="name" class="form-control" value="${product.name}" required>
                                        </div>

                                        <div class="form-group full-width">
                                            <label class="form-label" for="description">Mô tả</label>
                                            <textarea id="description" name="description" class="form-control" rows="3">${product.description}</textarea>
                                        </div>

                                        <div class="form-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                <input type="text" id="productCode" name="productCode" class="form-control" value="${product.productCode}">
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="price">Giá bán (VNĐ)</label>
                                                <input type="text" id="price" name="price" class="form-control"
                                                       value="<fmt:formatNumber value='${product.price}' type='number' groupingUsed='true' />" 
                                                       inputmode="numeric" maxlength="20" required>
                                            </div>


                                            <div class="form-group">
                                                <label class="form-label" for="origin">Xuất xứ</label>
                                                <input type="text" id="origin" name="origin" class="form-control" value="${product.origin}">
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="categoryId">Danh mục</label>
                                                <select id="categoryId" name="categoryId" class="form-control" required>
                                                    <c:forEach var="c" items="${categories}">
                                                        <option value="${c.id}" <c:if test="${product.categoryId == c.id}">selected</c:if>>
                                                            ${c.name}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="createdAt">Ngày tạo</label>
                                                <input type="datetime-local" id="createdAt" name="createdAt" class="form-control"
                                                       value="${product.createdAt}" readonly>
                                            </div>

                                            <!-- Ngày cập nhật -->
                                            <div class="form-group">
                                                <label class="form-label" for="updatedAt">Ngày cập nhật</label>
                                                <input type="datetime-local" id="updatedAt" name="updatedAt" class="form-control"
                                                       value="${product.updatedAt}" readonly>
                                            </div>
                                        </div>
                                    </div>

                                    <input type="hidden" name="isDeleted" value="false">
                                    <div class="form-section">
                                        <h2 class="form-section-title">Thông số kỹ thuật</h2>
                                        <table class="specs-table-edit">
                                            <thead>
                                                <tr><th>Tên thông số</th><th>Giá trị</th><th></th></tr>
                                            </thead>
                                            <tbody id="specs-tbody">

                                            </tbody>
                                        </table>
                                        <button type="button" id="add-spec-btn" class="btn-add-spec">
                                            <i data-feather="plus"></i> Thêm thông số
                                        </button>
                                    </div>
                                </div>
                            </form>                   
                        </div>
                    </c:if>

                    <c:if test="${empty product}">
                        <p>Không tìm thấy sản phẩm để chỉnh sửa.</p>
                    </c:if>

                </div>
        </div>
    </main>
</div>

<script src="${pageContext.request.contextPath}/js/editProductDetail.js"></script>
<script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
</body>
</html>
