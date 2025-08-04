<%--
    Document   : listFeedback.jsp
    Description: Trang danh sách phản hồi, đã được thêm header mới.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Phản hồi Khách hàng</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <%-- **THÊM MỚI:** Link đến file CSS của header --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listFeedback.css">

    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">

                <%-- **THAY ĐỔI:** Thêm header mới vào đây --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Phản hồi Khách hàng"/>
                </jsp:include>

                <div class="page-content">
                    <div class="stats-grid">
                        <div class="stat-card"><div class="icon-wrapper total"><i data-feather="message-square"></i></div><div class="info"><div class="title">Tổng phản hồi</div><div class="value"><fmt:formatNumber value="${totalCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper good"><i data-feather="trending-up"></i></div><div class="info"><div class="title">Rất hài lòng</div><div class="value"><fmt:formatNumber value="${goodCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper normal"><i data-feather="minus"></i></div><div class="info"><div class="title">Bình thường</div><div class="value"><fmt:formatNumber value="${normalCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper bad"><i data-feather="trending-down"></i></div><div class="info"><div class="title">Chưa hài lòng</div><div class="value"><fmt:formatNumber value="${badCount}" type="number"/></div></div></div>
                    </div>
                    <div class="content-card">
                        <form class="table-toolbar" action="${pageContext.request.contextPath}/listFeedback" method="get">
                            <div class="search-box">
                                <i data-feather="search" class="feather-search"></i>
                                <input type="text" name="query" placeholder="Tìm theo khách hàng, mã yêu cầu..." value="${param.query}">
                            </div>
                            <div class="filter-group">
                                <i data-feather="filter"></i>
                                <select name="ratingFilter" class="filter-select">
                                    <option value="all" ${param.ratingFilter == 'all' ? 'selected' : ''}>Tất cả mức độ</option>
                                    <option value="good" ${param.ratingFilter == 'good' ? 'selected' : ''}>Rất hài lòng (4-5 ★)</option>
                                    <option value="normal" ${param.ratingFilter == 'normal' ? 'selected' : ''}>Bình thường (3 ★)</option>
                                    <option value="bad" ${param.ratingFilter == 'bad' ? 'selected' : ''}>Chưa hài lòng (1-2 ★)</option>
                                </select>
                            </div>
                            <div class="toolbar-actions">
                                <button type="submit" class="btn btn-primary"><i data-feather="search"></i> Lọc / Tìm</button>
                                <a href="${pageContext.request.contextPath}/listFeedback" class="btn btn-secondary">
                                    <i data-feather="refresh-cw"></i> Reset
                                </a>
                            </div>
                        </form>
                        <div class="feedback-grid">
                            <c:if test="${empty feedbackList}">
                                <p style="grid-column: 1 / -1; text-align: center;">Không có phản hồi nào để hiển thị.</p>
                            </c:if>
                            <c:forEach var="fb" items="${feedbackList}">
                                <div class="feedback-card 
                                     <c:choose>
                                         <c:when test='${fb.rating >= 4}'>card-good</c:when>
                                         <c:when test='${fb.rating == 3}'>card-normal</c:when>
                                         <c:otherwise>card-bad</c:otherwise>
                                     </c:choose>
                                     ">
                                    <div class="card-header">
                                        <a href="${pageContext.request.contextPath}/viewFeedback?id=${fb.id}" class="customer-link">${fb.enterpriseName}</a>
                                        <c:choose>
                                            <c:when test="${fb.rating >= 4}"><span class="status-pill">Rất hài lòng</span></c:when>
                                            <c:when test="${fb.rating == 3}"><span class="status-pill">Bình thường</span></c:when>
                                            <c:otherwise><span class="status-pill">Chưa hài lòng</span></c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-body">
                                        <div class="card-info-row">
                                            <i data-feather="tag"></i>
                                            <span>${fb.requestCode}</span>
                                        </div>
                                        <div class="card-info-row rating-cell">
                                            <i data-feather="star"></i>
                                            <c:forEach begin="1" end="5" var="i">
                                                <i data-feather="star" class="${i <= fb.rating ? 'star-filled' : 'star-empty'}"></i>
                                            </c:forEach>
                                        </div>
                                        <c:if test="${not empty fb.comment}">
                                            <div class="card-info-row">
                                                <i data-feather="message-circle"></i>
                                                <span style="font-style: italic;">"${fb.comment}"</span>
                                            </div>
                                        </c:if>
                                        <div class="card-info-row">
                                            <i data-feather="calendar"></i>
                                            <span><fmt:formatDate value="${fb.createdAt}" pattern="HH:mm dd/MM/yyyy" /></span>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script src="https://unpkg.com/feather-icons"></script>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace({'stroke-width': 1.5});
            });
        </script>
        <%-- **THÊM MỚI:** Script cho menu hoạt động --%>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
