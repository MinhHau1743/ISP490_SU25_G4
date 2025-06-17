<%-- 
    Document   : createProduct
    Created on : Jun 14, 2025, 1:36:08 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

        <link rel="stylesheet" href="../../css/style.css">        
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/createProduct.css">
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

                <section class="content-body">
                    <div class="form-container">
                        <form action="createProduct" method="POST" class="product-form" enctype="multipart/form-data">
                            <div class="form-main-layout">
                                <div class="product-image-section">
                                    <label for="productImageUpload" class="image-placeholder" id="imagePreview">
                                        <i data-feather="image" style="width: 48px; height: 48px;"></i>
                                    </label>
                                    <input type="file" name="productImage" id="productImageUpload" class="visually-hidden" accept="image/*">
                                    <label for="productImageUpload" class="btn-upload">Chọn ảnh</label>
                                </div>

                                <div class="product-details-section">
                                    <fieldset class="form-fieldset">
                                        <legend>Thông tin sản phẩm</legend>
                                        <div class="details-grid">
                                            <div class="form-group"><label for="productCode">Mã hàng</label><input type="text" id="productCode" name="productCode" placeholder="Tự động tạo hoặc nhập"></div>
                                            <div class="form-group"><label for="productBrand">Hãng sản xuất</label><input type="text" id="productBrand" name="productBrand" placeholder="VD: Apple, Samsung"></div>
                                            <div class="form-group"><label for="productGroup">Nhóm hàng</label><input type="text" id="productGroup" name="productGroup" placeholder="VD: Điện tử, Thời trang"></div>
                                            <div class="form-group"><label for="productOrigin">Nước sản xuất</label><input type="text" id="productOrigin" name="productOrigin" placeholder="VD: Việt Nam, Mỹ"></div>
                                            <div class="form-group full-width"><label for="productPacking">Quy cách đóng gói</label><input type="text" id="productPacking" name="productPacking" placeholder="VD: Hộp 1 sản phẩm"></div>
                                            <div class="form-group full-width"><label for="productDescription">Mô tả</label><textarea id="productDescription" name="productDescription" placeholder="Nhập mô tả chi tiết cho sản phẩm..."></textarea></div>
                                        </div>
                                    </fieldset>
                                </div>
                            </div>

                            <div class="form-main-layout">
                                <div class="product-details-section">
                                    <fieldset class="form-fieldset">
                                        <legend>Giá & Tồn kho</legend>
                                        <div class="details-grid">
                                            <div class="form-group"><label for="productQuantity">Số lượng tồn kho</label><input type="number" id="productQuantity" name="productQuantity" value="0"></div>
                                            <div class="form-group"><label for="productPrice">Giá bán (VNĐ)</label><input type="number" id="productPrice" name="productPrice" value="0"></div>
                                            <div class="form-group"><label for="productWeight">Trọng lượng (gram)</label><input type="number" id="productWeight" name="productWeight" value="0"></div>
                                        </div>
                                    </fieldset>
                                </div>
                            </div>

                            <div class="form-actions">
                                <button type="button" class="btn-form"><i data-feather="x"></i><span>Hủy</span></button>
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
        <script src="../../js/createProduct.js"></script>

        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
