<%-- 
    Document   : editProductDetail
    Created on : Jun 17, 2025
    Author     : Hai Huy
    Description: Restructured layout, HTML5 validation, save-loading UX, and live image preview.
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
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
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
        <%-- Tái sử dụng CSS trang create để đồng bộ layout --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createProduct.css">
        
        <style>
            .error-message {
                color: #dc3545;
                font-size: 0.875rem;
                margin-top: 0.25rem;
                display: block;
            }
            .form-control.error {
                border-color: #dc3545;
            }
        </style>
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

                <section class="content-body">
                    <div class="form-container">
                        <form action="product?action=processEdit" method="POST" class="product-form" enctype="multipart/form-data" novalidate>
                            <input type="hidden" name="id" value="${product.id}">
                            <div class="form-main-layout">
                                <div class="product-image-section">
                                    <label for="productImageUpload" class="image-placeholder" id="imagePreviewContainer">
                                        <i id="imageIcon" data-feather="image" style="width: 48px; height: 48px; display: none;"></i>
                                        <img id="productImagePreview"
                                             src="${pageContext.request.contextPath}/image/${not empty product.image ? product.image : 'na.jpg'}"
                                             alt="Ảnh sản phẩm"
                                             style="width: 100%; height: 100%; object-fit: contain; image-rendering: -webkit-optimize-contrast;"
                                             decoding="async"
                                             loading="lazy"
                                             fetchpriority="low"
                                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/image/na.jpg';" />

                                    </label>
                                    <input type="file" name="image" id="productImageUpload" class="visually-hidden" accept="image/*">
                                    <label for="productImageUpload" class="btn-upload">Đổi ảnh</label>
                                    
                                    <%-- Hiển thị lỗi ảnh --%>
                                    <c:if test="${not empty imageError}">
                                        <span class="error-message">${imageError}</span>
                                    </c:if>
                                </div>

                                <div class="product-details-section">
                                    <fieldset class="form-fieldset">
                                        <legend>Thông tin sản phẩm</legend>
                                        <div class="details-grid">
                                            <div class="form-group">
                                                <label class="form-label" for="productName">Tên sản phẩm (*)</label>
                                                <input type="text" id="productName" name="name" class="form-control ${not empty nameError ? 'error' : ''}"
                                                       value="${product.name}" required title="Vui lòng nhập tên sản phẩm.">
                                                <%-- Hiển thị lỗi tên sản phẩm --%>
                                                <c:if test="${not empty nameError}">
                                                    <span class="error-message">${nameError}</span>
                                                </c:if>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                <input type="text" id="productCode" name="productCode" class="form-control"
                                                       value="${product.productCode}" readonly>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="price">Giá bán (VNĐ) (*)</label>
                                                <input type="text" id="price" name="price" class="form-control ${not empty priceError ? 'error' : ''}"
                                                       value="<fmt:formatNumber value='${product.price}' type='number' groupingUsed='false' />"
                                                       inputmode="numeric" pattern="[0-9,.]*" min="0" required
                                                       title="Vui lòng chỉ nhập số không âm.">
                                                <%-- Hiển thị lỗi giá --%>
                                                <c:if test="${not empty priceError}">
                                                    <span class="error-message">${priceError}</span>
                                                </c:if>
                                            </div>

                                            <div class="form-group">
                                                <label class="form-label" for="origin">Xuất xứ (*)</label>
                                                <input type="text" id="origin" name="origin" class="form-control ${not empty originError ? 'error' : ''}"
                                                       value="${product.origin}" required title="Vui lòng nhập xuất xứ.">
                                                <%-- Hiển thị lỗi xuất xứ --%>
                                                <c:if test="${not empty originError}">
                                                    <span class="error-message">${originError}</span>
                                                </c:if>
                                            </div>

                                            <div class="form-group full-width">
                                                <label class="form-label" for="description">Mô tả</label>
                                                <textarea id="description" name="description" class="form-control ${not empty descriptionError ? 'error' : ''}" rows="4"
                                                          placeholder="Nhập mô tả chi tiết cho sản phẩm...">${product.description}</textarea>
                                                <%-- Hiển thị lỗi mô tả --%>
                                                <c:if test="${not empty descriptionError}">
                                                    <span class="error-message">${descriptionError}</span>
                                                </c:if>
                                            </div>
                                        </div>
                                    </fieldset>
                                </div>
                            </div>

                            <div class="form-actions">
                                <a href="product?action=list" class="btn-form"><i data-feather="x"></i><span>Hủy</span></a>
                                <%-- thêm id cho nút submit để điều khiển trạng thái --%>
                                <button type="submit" id="btnSaveEdit" class="btn-form primary">
                                    <i data-feather="save"></i><span>Lưu thay đổi</span>
                                </button>
                            </div>
                        </form>
                    </div>
                </section>
            </main>
        </div>

        <%-- Overlay loading (đặt trước </body>) --%>
        <div id="savingOverlay" class="loading-overlay" aria-hidden="true">
            <div class="loading-card">
                <div class="spinner-border" role="status" aria-hidden="true"></div>
                <span>Đang lưu thay đổi…</span>
            </div>
        </div>

        <script>
            feather.replace();

            // ====== Chặn double-submit + overlay ======
            document.getElementById('btnSaveEdit').addEventListener('click', function() {
                // Hiển thị overlay loading
                document.getElementById('savingOverlay').style.display = 'flex';
                
                // Vô hiệu hóa nút submit để tránh double submit
                this.disabled = true;
                
                // Tự động submit form
                this.closest('form').submit();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/editProductDetail.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>