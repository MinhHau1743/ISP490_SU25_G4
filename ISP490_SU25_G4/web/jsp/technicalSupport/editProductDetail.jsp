<%-- 
    Document   : editProductDetail
    Created on : Jun 17, 2025, 10:28:17 AM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Thêm taglib 'fn' để xử lý chuỗi --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <%-- Sửa: Dùng đường dẫn gốc để ổn định hơn --%>
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">

                        <%-- Sửa: action trỏ đến controller hợp nhất với action tương ứng --%>
                        <form action="product?action=processEdit" method="post" enctype="multipart/form-data">

                            <%-- Bỏ: Input 'service' không còn cần thiết --%>
                            <input type="hidden" name="id" value="${product.id}">

                            <div class="edit-header">
                                <h1 class="page-title">Chỉnh sửa Sản phẩm</h1>
                                <div class="action-buttons">
                                    <%-- Sửa: Nút Hủy trỏ về trang danh sách sản phẩm --%>
                                    <a href="product?action=list" class="btn btn-secondary">Hủy</a>
                                    <button type="submit" class="btn btn-primary">
                                        <i data-feather="save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </div>
                            
                            <c:if test="${not empty editErrors}">
                                <div class="alert alert-warning alert-dismissible">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <ul>
                                        <c:forEach var="error" items="${editErrors}">
                                            <li>${error}</li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </c:if>

                            <c:if test="${not empty product}">
                                <div class="form-column">
                                    <div class="product-edit-container">
                                        <div class="image-column">
                                            <div class="form-section">
                                                <h2 class="form-section-title">Hình ảnh sản phẩm</h2>
                                                <div class="image-list" style="display: flex; gap: 24px; align-items: center; flex-wrap: wrap;">
                                                    <img id="productImagePreview"
                                                         src="${pageContext.request.contextPath}/image/${product.image}"
                                                         alt="Ảnh sản phẩm"
                                                         style="width: 260px; height: auto; border: 1px solid #ccc; border-radius: 8px;"
                                                         onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'" />

                                                    <label for="imageUpload" class="upload-box" style="cursor: pointer; width: 160px; height: 160px; display: flex; flex-direction: column; justify-content: center; align-items: center; border: 2px dashed #ccc; border-radius: 8px;">
                                                        <i data-feather="upload-cloud" style="width: 32px; height: 32px;"></i>
                                                        <p style="margin: 8px 0 0;">Tải lên ảnh mới</p>
                                                    </label>
                                                    <input type="file" id="imageUpload" name="image" accept="image/*" style="display: none;">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <%-- Sửa: Lấy tên ảnh từ đối tượng product --%>
                                    <input type="hidden" name="oldImage" value="${product.image}">

                                    <div class="form-section">
                                        <h2 class="form-section-title">Thông tin chung</h2>
                                        <div class="form-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="productName">Tên sản phẩm</label>
                                                <input type="text" id="productName" name="name" class="form-control" value="${product.name}" required>
                                            </div> 
                                            <div class="form-group">
                                                <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                <input type="text" id="productCode" class="form-control" value="${product.productCode}" readonly>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="price">Giá bán (VNĐ)</label>
                                                <input type="text" id="price" name="price" class="form-control"
                                                       value="<fmt:formatNumber value='${product.price}' type='number' groupingUsed='false' />" 
                                                       inputmode="numeric" maxlength="20" required>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="origin">Xuất xứ</label>
                                                <input type="text" id="origin" name="origin" class="form-control" value="${product.origin}">
                                            </div>
                                            
                                            <div class="form-group">
                                                <label class="form-label" for="createdAt">Ngày tạo</label>
                                                <%-- Sửa: Dùng fn:replace để đổi định dạng ngày cho đúng với input datetime-local --%>
                                                <input type="datetime-local" id="createdAt" name="createdAt" class="form-control"
                                                       value="${fn:replace(product.createdAt, ' ', 'T')}" readonly>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="updatedAt">Ngày cập nhật</label>
                                                <%-- Sửa: Dùng fn:replace để đổi định dạng ngày cho đúng với input datetime-local --%>
                                                <input type="datetime-local" id="updatedAt" name="updatedAt" class="form-control"
                                                       value="${fn:replace(product.updatedAt, ' ', 'T')}" readonly>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group full-width">
                                        <label class="form-label" for="description">Mô tả</label>
                                        <textarea id="description" name="description" class="form-control" rows="3">${product.description}</textarea>
                                    </div>
                                    
                                    <%-- Thêm input cho isDeleted để gửi về server --%>
                                    <input type="hidden" name="isDeleted" value="${product.isDeleted}">
                                </div>
                            </c:if>
                        </form>                    
                    </div>

                    <c:if test="${empty product}">
                        <p>Không tìm thấy sản phẩm để chỉnh sửa.</p>
                    </c:if>

                </div>
            </main>
        </div>

        <script>
            // Kích hoạt feather icons
            feather.replace();

            // Script để xem trước ảnh khi tải lên
            document.getElementById('imageUpload').onchange = function (evt) {
                var reader = new FileReader();
                reader.onload = function (e) {
                    document.getElementById('productImagePreview').src = e.target.result;
                };
                reader.readAsDataURL(this.files[0]);
            };
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>