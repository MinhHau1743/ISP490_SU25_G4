<%-- 
    Document    : viewProductDetail (Hardcoded Preview)
    Created on  : Jun 22, 2025
    Author      : NGUYEN MINH (Static version by Gemini)
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

        <title>Chi tiết - Máy nén điều hòa Daikin 12000BTU</title>

        <%-- Sử dụng đường dẫn tương đối để dễ xem trước --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/viewProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">
                        <div class="detail-header">
                            <a href="ProductController" class="back-link">
                                <i data-feather="arrow-left"></i>
                                <span>Quay lại danh sách</span>
                            </a>
                        </div>

<<<<<<< Updated upstream
                        <c:if test="${not empty product}">
                            <div class="product-view-container">
                                <div class="product-gallery">
                                    <div class="main-image">
                                        <%-- Hiển thị ảnh đầu tiên trong danh sách làm ảnh chính --%>
                                        <img id="mainProductImage" src="${pageContext.request.contextPath}/image/${imageFileName}" alt="Main image of ${product.name}">
                                    </div>
                                    <div class="thumbnail-list">
                                        <%-- Lặp qua danh sách ảnh để hiển thị thumbnail --%>
                                        <c:forEach var="imgUrl" items="" varStatus="loop">
                                            <div class="thumbnail-item" data-large-src="">
                                                <img src="" alt="Thumbnail  for ${product.name}">
                                            </div>
                                        </c:forEach>
                                    </div>
=======
                        <%-- BẮT ĐẦU PHẦN DỮ LIỆU CỨNG --%>
                        <div class="product-view-container">
                            <div class="product-gallery">
                                <div class="main-image">
                                    <img id="mainProductImage" src="https://via.placeholder.com/600x600/0D9488/FFFFFF?text=Daikin+Compressor" alt="Main image of Máy nén điều hòa Daikin">
>>>>>>> Stashed changes
                                </div>
                                <div class="thumbnail-list">
                                    <%-- Thumbnail 1 --%>
                                    <div class="thumbnail-item active" data-large-src="https://via.placeholder.com/600x600/0D9488/FFFFFF?text=Daikin+Compressor">
                                        <img src="https://via.placeholder.com/100x100/0D9488/FFFFFF?text=Anh+1" alt="Thumbnail 1">
                                    </div>
                                    <%-- Thumbnail 2 --%>
                                    <div class="thumbnail-item" data-large-src="https://via.placeholder.com/600x600/16A34A/FFFFFF?text=Side+View">
                                        <img src="https://via.placeholder.com/100x100/16A34A/FFFFFF?text=Anh+2" alt="Thumbnail 2">
                                    </div>
<<<<<<< Updated upstream

                        

                                    <div class="view-actions">
                                        <%-- Link sửa sản phẩm với ID động --%>
                                        <a href="ProductController?service=getProductToEdit&id=${product.id}&image=${imageFileName}" class="btn btn-primary"><i data-feather="edit"></i> Sửa thông tin</a>
=======
                                    <%-- Thumbnail 3 --%>
                                    <div class="thumbnail-item" data-large-src="https://via.placeholder.com/600x600/F59E0B/FFFFFF?text=Top+View">
                                        <img src="https://via.placeholder.com/100x100/F59E0B/FFFFFF?text=Anh+3" alt="Thumbnail 3">
>>>>>>> Stashed changes
                                    </div>
                                    <%-- Thumbnail 4 --%>
                                    <div class="thumbnail-item" data-large-src="https://via.placeholder.com/600x600/EF4444/FFFFFF?text=Connector">
                                        <img src="https://via.placeholder.com/100x100/EF4444/FFFFFF?text=Anh+4" alt="Thumbnail 4">
                                    </div>
                                </div>
                            </div>

                            <div class="product-info">
                                <h1 class="product-title">Máy nén điều hòa Daikin 12000BTU</h1>
                                <p class="product-short-description">Máy nén (block) điều hòa Daikin, công suất 12000BTU, hàng chính hãng, bảo hành 12 tháng.</p>

                                <div class="price-section">
                                    3.500.000&nbsp;₫
                                </div>

                                <div class="info-snippets">
                                    <div class="snippet"><span class="snippet-label">Mã sản phẩm</span><span class="snippet-value">DK-COMP-12K</span></div>
                                    <div class="snippet"><span class="snippet-label">Danh mục</span><span class="snippet-value">Linh kiện điều hòa</span></div>
                                    <div class="snippet"><span class="snippet-label">Thương hiệu</span><span class="snippet-value">Daikin</span></div>
                                    <div class="snippet"><span class="snippet-label">Xuất xứ</span><span class="snippet-value">Nhật Bản</span></div>
                                    <div class="snippet"><span class="snippet-label">Bảo hành</span><span class="snippet-value">12 tháng</span></div>
                                    <div class="snippet"><span class="snippet-label">Tình trạng</span><span class="snippet-value status-instock">Còn hàng</span></div>
                                </div>

                                <div class="view-actions">
                                    <a href="editProductDetail.jsp?id=1" class="btn btn-primary"><i data-feather="edit"></i> Sửa thông tin</a>
                                </div>
                            </div>

                            <div class="product-details-tabs">
                                <nav class="tab-nav">
                                    <span class="tab-link active" data-tab="description">Mô tả chi tiết</span>
                                    <span class="tab-link" data-tab="specs">Thông số kỹ thuật</span>
                                </nav>
                                <div id="description" class="tab-content active">
                                    <h3>Tổng quan về sản phẩm</h3>
                                    <p>Máy nén điều hòa Daikin 12000BTU (còn gọi là block hoặc lốc) là bộ phận cốt lõi của hệ thống điều hòa không khí, có chức năng nén môi chất lạnh để lưu thông trong hệ thống.</p>
                                    <p>Sản phẩm được sản xuất trên dây chuyền công nghệ hiện đại của Nhật Bản, đảm bảo hiệu suất hoạt động cao, tiết kiệm điện năng và vận hành êm ái. Tương thích với nhiều dòng điều hòa dân dụng và thương mại của Daikin.</p>
                                    <h3>Tính năng nổi bật</h3>
                                    <ul>
                                        <li><strong>Công nghệ Inverter:</strong> Giúp tiết kiệm đến 40% điện năng so với máy nén thông thường.</li>
                                        <li><strong>Vận hành êm ái:</strong> Độ ồn thấp, không gây ảnh hưởng đến không gian sinh hoạt và làm việc.</li>
                                        <li><strong>Bền bỉ:</strong> Vỏ ngoài được sơn tĩnh điện chống ăn mòn, chịu được điều kiện thời tiết khắc nghiệt.</li>
                                        <li><strong>Hiệu suất cao:</strong> Đạt hiệu suất làm lạnh tối đa trong thời gian ngắn.</li>
                                    </ul>
                                </div>
                                <div id="specs" class="tab-content">
                                    <table class="specs-table">
                                        <tbody>
                                            <tr>
                                                <td>Công suất</td>
                                                <td>12000 BTU/h (1.5 HP)</td>
                                            </tr>
                                            <tr>
                                                <td>Loại gas sử dụng</td>
                                                <td>R-32</td>
                                            </tr>
                                            <tr>
                                                <td>Điện áp</td>
                                                <td>220-240V / 50Hz</td>
                                            </tr>
                                            <tr>
                                                <td>Dòng điện</td>
                                                <td>4.5 A</td>
                                            </tr>
                                            <tr>
                                                <td>Kích thước (D x R x C)</td>
                                                <td>25cm x 20cm x 30cm</td>
                                            </tr>
                                            <tr>
                                                <td>Trọng lượng</td>
                                                <td>15 kg</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <%-- KẾT THÚC PHẦN DỮ LIỆU CỨNG --%>

                    </div>
                </div>
            </main>
        </div>
        
        <script>
            // Phải gọi feather.replace() để các icon hiển thị
            document.addEventListener('DOMContentLoaded', function() {
                feather.replace();
            });
        </script>
        <script src="../../js/viewProductDetail.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>