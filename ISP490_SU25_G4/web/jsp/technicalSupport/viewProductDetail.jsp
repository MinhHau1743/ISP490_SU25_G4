<%-- 
    Document   : viewProductDetail
    Created on : Jun 17, 2025, 10:14:47 AM
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

        <title>Chi tiết - ${not empty product ? product.name : "Sản phẩm không tồn tại"}</title>

        <%-- Các link và script giữ nguyên --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">
                        <div class="detail-header">
                            <a href="ProductController" class="back-link"> <%-- Sửa link thành /product servlet --%>
                                <i data-feather="arrow-left"></i>
                                <span>Quay lại danh sách</span>
                            </a>
                        </div>

                        <c:if test="${not empty product}">
                            <div class="product-view-container">
                                <div class="product-gallery">
                                    <div class="main-image">
                                        <%-- Hiển thị ảnh đầu tiên trong danh sách làm ảnh chính --%>
                                        <img id="mainProductImage" src="" alt="Main image of ${product.name}">
                                    </div>
                                    <div class="thumbnail-list">
                                        <%-- Lặp qua danh sách ảnh để hiển thị thumbnail --%>
                                        <c:forEach var="imgUrl" items="" varStatus="loop">
                                            <div class="thumbnail-item" data-large-src="">
                                                <img src="" alt="Thumbnail  for ${product.name}">
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="product-info">


                                    <%-- Định dạng giá tiền --%>
                                    <div class="price-section">
                                        <fmt:formatNumber value="${product.price}" type="currency" currencyCode="VND"/>
                                    </div>

                                    <div class="info-snippets">
                                        <div class="snippet"><span class="snippet-label">Mã sản phẩm</span><span class="snippet-value">${product.productCode}</span></div>
                                        <div class="snippet"><span class="snippet-label">Danh mục</span><span class="snippet-value">${product.categoryId}</span></div>
                                        <div class="snippet"><span class="snippet-label">Thương hiệu</span><span class="snippet-value">${product.origin}</span></div>
                                        <div class="snippet"><span class="snippet-label">Xuất xứ</span><span class="snippet-value">${product.description}</span></div>
                                        <div class="snippet"><span class="snippet-label">Bảo hành</span><span class="snippet-value">${product.createdAt}</span></div>
                                        <div class="snippet"><span class="snippet-label">Tồn kho</span><span class="snippet-value">${product.updatedAt}</span></div>
                                    </div>

                        

                                    <div class="view-actions">
                                        <%-- Link sửa sản phẩm với ID động --%>
                                        <a href="editProductDetail.jsp" class="btn btn-primary"><i data-feather="edit"></i> Sửa thông tin</a>
                                    </div>
                                </div>

                                <div class="product-details-tabs">
                                    <nav class="tab-nav">
                                        <span class="tab-link active" data-tab="description">Mô tả chi tiết</span>
                                        <span class="tab-link" data-tab="specs">Thông số kỹ thuật</span>
                                    </nav>
                                    <div id="description" class="tab-content active">
                                        <%-- Dùng c:out để hiển thị nội dung HTML từ database --%>
                                        <c:out value="${product.description}" escapeXml="false" />
                                    </div>
                                    <div id="specs" class="tab-content">
                                        <table class="specs-table">
                                            <tbody>
         
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <%-- Trường hợp không tìm thấy sản phẩm --%>
                        <c:if test="${empty product}">
                            <div style="text-align: center; padding: 60px 20px;">
                                <i data-feather="alert-circle" style="width: 48px; height: 48px; color: var(--error-color);"></i>
                                <h2 style="margin-top: 16px; color: var(--text-primary);">Không tìm thấy sản phẩm</h2>
                                <p style="color: var(--text-secondary);">Sản phẩm bạn đang tìm kiếm có thể đã bị xóa hoặc không tồn tại.</p>
                            </div>
                        </c:if>

                    </div>
                </div>
            </main>
        </div>


        <script src="${pageContext.request.contextPath}/js/viewProductDetail.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
