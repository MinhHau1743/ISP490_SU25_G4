<%-- 
    Document   : editProductDetail
    Created on : Jun 17, 2025
    Author     : NGUYEN MINH (Updated by Gemini)
    Description: Restructured layout and added HTML5 validation.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
        <%-- Sử dụng lại CSS của trang create để đồng bộ layout --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createProduct.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Chỉnh sửa sản phẩm</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>

                <c:if test="${not empty editErrors}">
                    <div class="alert alert-warning alert-dismissible" style="margin: 0 24px 20px;">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <strong>Vui lòng sửa các lỗi sau:</strong>
                        <ul>
                            <c:forEach var="error" items="${editErrors}">
                                <li>${error}</li>
                                </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <section class="content-body">
                    <div class="form-container">
                        <form action="product?action=processEdit" method="POST" class="product-form" enctype="multipart/form-data">
                            <input type="hidden" name="id" value="${product.id}">

                            <div class="form-main-layout">
                                <div class="product-image-section">
                                    <label for="productImageUpload" class="image-placeholder" id="imagePreviewContainer">
                                        <i id="imageIcon" data-feather="image" style="width: 48px; height: 48px; display: none;"></i>
                                        <img id="productImagePreview" 
                                             src="${pageContext.request.contextPath}/image/${not empty product.image ? product.image : 'na.jpg'}" 
                                             alt="Ảnh sản phẩm"
                                             style="width: 100%; height: 100%; object-fit: contain;"
                                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/image/na.jpg';" />
                                    </label>
                                    <input type="file" name="image" id="productImageUpload" class="visually-hidden" accept="image/*">
                                    <label for="productImageUpload" class="btn-upload">Đổi ảnh</label>
                                </div>

                                <div class="product-details-section">
                                    <fieldset class="form-fieldset">
                                        <legend>Thông tin sản phẩm</legend>
                                        <div class="details-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="productName">Tên sản phẩm (*)</label>
                                                <input type="text" id="productName" name="name" class="form-control" 
                                                       value="${product.name}" required title="Vui lòng nhập tên sản phẩm.">
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                <input type="text" id="productCode" name="productCode" class="form-control" 
                                                       value="${product.productCode}" readonly>
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="price">Giá bán (VNĐ) (*)</label>
                                                <input type="text" id="price" name="price" class="form-control"
                                                       value="<fmt:formatNumber value='${product.price}' type='number' groupingUsed='false' />"
                                                       inputmode="numeric" 
                                                       pattern="[0-9,.]*"
                                                       min="0"
                                                       required
                                                       title="Vui lòng chỉ nhập số không âm.">
                                            </div>
                                            <div class="form-group">
                                                <label class="form-label" for="origin">Xuất xứ (*)</label>
                                                <input type="text" id="origin" name="origin" class="form-control" 
                                                       value="${product.origin}" required title="Vui lòng nhập xuất xứ.">
                                            </div>
                                            <div class="form-group full-width">
                                                <label class="form-label" for="description">Mô tả</label>
                                                <textarea id="description" name="description" class="form-control" rows="4" 
                                                          placeholder="Nhập mô tả chi tiết cho sản phẩm...">${product.description}</textarea>
                                            </div>
                                        </div>
                                    </fieldset>
                                </div>
                            </div>

                            <div class="form-actions">
                                <a href="product?action=list" class="btn-form"><i data-feather="x"></i><span>Hủy</span></a>
                                <button type="submit" class="btn-form primary"><i data-feather="save"></i><span>Lưu thay đổi</span></button>
                            </div>
                        </form>
                    </div>
                </section>
            </main>
        </div>

        <script>
            feather.replace();

            // Script để xem trước ảnh khi tải lên
            const imageUpload = document.getElementById('productImageUpload');
            const imagePreview = document.getElementById('productImagePreview');
            const imageIcon = document.getElementById('imageIcon');

            imageUpload.onchange = function (evt) {
                const [file] = imageUpload.files;
                if (file) {
                    imagePreview.src = URL.createObjectURL(file);
                    imagePreview.style.display = 'block';
                    imageIcon.style.display = 'none';
                }
            };

            // Hiển thị ảnh hoặc icon lúc tải trang
            if (imagePreview.getAttribute('src') && imagePreview.getAttribute('src') !== '${pageContext.request.contextPath}/image/na.jpg') {
                imagePreview.style.display = 'block';
                imageIcon.style.display = 'none';
            } else {
                imagePreview.style.display = 'none';
                imageIcon.style.display = 'block';
            }
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
