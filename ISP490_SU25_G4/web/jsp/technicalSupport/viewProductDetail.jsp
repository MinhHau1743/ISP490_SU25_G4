<%-- 
    Document   : viewProductDetail
    Created on : Jun 17, 2025, 10:14:47 AM
    Author     : Hai Huy
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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewProductDetail.css">
    </head>
    <body>
        <div class="app-container">
            <%-- Sửa: Dùng đường dẫn gốc để ổn định hơn --%>
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">
                    <div class="content-card">
                        <div class="detail-header">
                            <%-- Sửa: href trỏ về trang danh sách sản phẩm --%>
                            <a href="product?action=list" class="back-link">
                                <i data-feather="arrow-left"></i>
                                <span>Quay lại danh sách</span>
                            </a>
                        </div>

                        <c:if test="${not empty product}">
                            <div class="product-view-container">
                                <div class="product-gallery">
                                    <div class="main-image">
                                        <img id="mainProductImage" 
                                             src="${pageContext.request.contextPath}/image/${product.image}" 
                                             alt="Ảnh của ${product.name}"
                                             onerror="this.src='${pageContext.request.contextPath}/image/na.jpg'">
                                    </div>
                                    <%-- Bỏ: Phần thumbnail rỗng vì model hiện tại chỉ có 1 ảnh --%>
                                </div>

                                <div class="product-info">
                                    <div class="info-snippets">
                                        <div class="snippet"><span class="snippet-label">Tên sản phẩm</span><span class="snippet-value">${product.name}</span></div>
                                        <div class="snippet"><span class="snippet-label">Mã sản phẩm</span><span class="snippet-value">${product.productCode}</span></div>
                                        <div class="snippet"><span class="snippet-label">Giá</span><span class="snippet-value"><fmt:formatNumber value="${product.price}" type="currency" currencyCode="VND"/></span></div>
                                        <div class="snippet"><span class="snippet-label">Xuất xứ</span><span class="snippet-value">${product.origin}</span></div>
                                        <div class="snippet"><span class="snippet-label">Ngày tạo</span><span class="snippet-value">${product.createdAt}</span></div>
                                        <div class="snippet"><span class="snippet-label">Cập nhật</span><span class="snippet-value">${product.updatedAt}</span></div>
                                        <div class="snippet"><span class="snippet-label">Người tạo</span><span class="snippet-value">${product.createdByName}</span></div>
                                        <div class="snippet"><span class="snippet-label">Người cập nhật</span><span class="snippet-value">${product.updatedByName}</span></div>
                                    </div>

                                    <div class="view-actions">
                                        <%-- Chỉ Admin hoặc Kĩ thuật mới thấy nút Sửa --%>
                                        <c:if test="${sessionScope.userRole == 'Admin' || sessionScope.userRole == 'Kĩ thuật'}">
                                            <%-- Sửa: href trỏ đến action=edit của controller hợp nhất --%>
                                            <a href="product?action=edit&id=${product.id}" class="btn btn-primary"><i data-feather="edit"></i> Sửa thông tin</a>
                                        </c:if>
                                        </div>
                                </div>

                                <div class="product-details-tabs">
                                    <nav class="tab-nav">
                                        <span class="tab-link active">Mô tả chi tiết</span>
                                    </nav>
                                    <div class="tab-content active">
                                        <%-- Dùng c:out để tránh lỗi XSS và hiển thị đúng định dạng --%>
                                        <c:out value="${product.description}" escapeXml="false" />
                                    </div>
                                </div>
                            </div>
                        </c:if>

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

        <script>
            // Kích hoạt feather icons
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>