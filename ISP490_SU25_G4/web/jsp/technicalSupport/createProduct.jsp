<%-- 
    Document   : createProduct
    Created on : Jun 14, 2025, 1:36:08 PM
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
        <title>Thêm sản phẩm</title>

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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createProduct.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thêm sản phẩm</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>
                <c:if test="${not empty errors}">
                    <div class="alert alert-warning alert-dismissible">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <strong>${errors}</strong> 
                    </div>
                </c:if>


                <section class="content-body">
                    <div class="form-container">
                        <form action="createProduct" method="POST" class="product-form" enctype="multipart/form-data">
                            <input type="hidden" name="service" value="createProduct">
                            <div class="form-main-layout">
                                <div class="product-image-section">
                                    <label for="productImageUpload" class="image-placeholder" id="imagePreview">
                                        <i id="imageIcon" data-feather="image" style="width: 48px; height: 48px;"></i>
                                        <img id="productImagePreview" style="display: none; width: 100%; height: 100%; object-fit: contain;" />
                                    </label>
                                    <input type="file" name="image" id="productImageUpload" class="visually-hidden" accept="image/*">
                                    <label for="productImageUpload" class="btn-upload">Chọn ảnh</label>
                                </div>


                                <div class="product-details-section">
                                    <fieldset class="form-fieldset">
                                        <legend>Thông tin sản phẩm</legend>
                                        <div class="details-grid">

                                            <!-- Tên sản phẩm -->
                                            <div class="form-group">
                                                <label class="form-label" for="productName">Tên sản phẩm</label>
                                                <input type="text" id="productName" name="name" class="form-control"
                                                       value="${product.name}" required>
                                            </div>
                                            
                                            <div class="form-group">
                                                <label class="form-label" for="productCode">Mã sản phẩm</label>
                                                <input type="text" id="productCode" name="productCode" class="form-control"
                                                       value="${product.productCode}" required>
                                            </div>


                                            <!-- Giá bán -->
                                            <div class="form-group">
                                                <label class="form-label" for="price">Giá bán (VNĐ)</label>
                                                <input type="text" id="price" name="price" class="form-control"
                                                       value="<fmt:formatNumber value='${product.price}' type='number' groupingUsed='true' />"
                                                       inputmode="numeric" maxlength="15" required>
                                            </div>

                                            <!-- Hãng sản xuất -->
                                            <div class="form-group">
                                                <label class="form-label" for="origin">Xuất xứ</label>
                                                <input type="text" id="origin" name="origin" class="form-control"
                                                       value="${product.brand}" >
                                            </div>


                                            <!-- Mô tả -->
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
                                <a href="ProductController" class="btn-form"><i data-feather="x"></i><span>Hủy</span></a>
                                <button type="submit" class="btn-form primary"><i data-feather="save"></i><span>Lưu sản phẩm</span></button>
                            </div>
                        </form>
                    </div>
                </section>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/createProduct.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
