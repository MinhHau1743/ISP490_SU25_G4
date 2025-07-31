<%--
    Document   : listFeedback.jsp
    Created on : Jul 22, 2025
    Author     : NGUYEN MINH / Gemini
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Phản hồi Khách hàng - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">

        <style>
            /* === BỐ CỤC CHUNG === */
            .main-content {
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                background-color: #f3f4f6;
            }
            .page-header {
                background-color: #fff;
                padding: 1.5rem 2rem;
                border-bottom: 1px solid #e5e7eb;
            }
            .page-header .title-section .title {
                font-size: 1.75rem;
                font-weight: 700;
                color: #111827;
                margin: 0;
            }
            .page-header .breadcrumb {
                font-size: 0.875rem;
                color: #6b7280;
            }
            .page-header .breadcrumb span {
                font-weight: 500;
                color: #374151;
            }
            .page-content {
                padding: 1.5rem 2rem;
                flex-grow: 1;
                overflow-y: auto;
            }
            .content-card {
                background-color: #fff;
                border-radius: 0.75rem;
                border: 1px solid #e5e7eb;
                padding: 1.5rem;
            }

            /* === KHỐI THỐNG KÊ === */
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 1.5rem;
                margin-bottom: 1.5rem;
            }
            .stat-card {
                background-color: #fff;
                padding: 1.25rem;
                border-radius: 0.75rem;
                border: 1px solid #e5e7eb;
                display: flex;
                align-items: center;
                gap: 1rem;
            }
            .stat-card .icon-wrapper {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            .stat-card .icon-wrapper.total {
                background-color: #ffedd5;
                color: #f97316;
            }
            .stat-card .icon-wrapper.good {
                background-color: #dcfce7;
                color: #22c55e;
            }
            .stat-card .icon-wrapper.normal {
                background-color: #fef9c3;
                color: #eab308;
            }
            .stat-card .icon-wrapper.bad {
                background-color: #fee2e2;
                color: #ef4444;
            }
            .stat-card .info .title {
                color: #6b7280;
                font-size: 0.875rem;
                margin-bottom: 0.25rem;
            }
            .stat-card .info .value {
                font-size: 1.5rem;
                font-weight: 700;
                color: #111827;
            }

            /* === THANH CÔNG CỤ TÌM KIẾM === */
            .table-toolbar {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                margin-bottom: 1.5rem;
            }
            .search-box {
                position: relative;
                flex-grow: 1;
            }
            .search-box .feather-search {
                position: absolute;
                left: 12px;
                top: 50%;
                transform: translateY(-50%);
                color: #9ca3af;
            }
            .search-box input {
                width: 100%;
                box-sizing: border-box;
                padding: 0.625rem 0.75rem 0.625rem 2.5rem;
                border: 1px solid #d1d5db;
                border-radius: 0.5rem;
            }
            .btn {
                padding: 0.625rem 1rem;
                border-radius: 0.5rem;
                font-weight: 600;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                cursor: pointer;
                text-decoration: none;
                border: 1px solid #d1d5db;
                background-color: #fff;
            }
            .btn-secondary {
                color: #374151;
            }
            .btn-primary {
                background-color: #3b82f6;
                color: #fff;
                border-color: #3b82f6;
            }
            .toolbar-actions {
                margin-left: auto;
            }

            /* === GIAO DIỆN LƯỚI THẺ FEEDBACK === */
            .feedback-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
                gap: 1.5rem;
            }
            .feedback-card {
                border-radius: 0.75rem;
                overflow: hidden;
                transition: box-shadow 0.2s ease;
                border: 1px solid #e5e7eb;
                background-color: #fff;
            }
            .feedback-card:hover {
                box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
            }

            /* THAY ĐỔI: CSS MÀU SẮC CHO HEADER CỦA THẺ */
            .card-good .card-header {
                background-color: #22c55e;
                color: #ffffff;
            }
            .card-good .card-header .status-pill {
                background-color: #ffffff;
                color: #16a34a;
            }
            .card-normal .card-header {
                background-color: #eab308;
                color: #ffffff;
            }
            .card-normal .card-header .status-pill {
                background-color: #ffffff;
                color: #a16207;
            }
            .card-bad .card-header {
                background-color: #ef4444;
                color: #ffffff;
            }
            .card-bad .card-header .status-pill {
                background-color: #ffffff;
                color: #b91c1c;
            }
            .feedback-card .card-header .customer-link {
                color: inherit;
            }

            .card-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 1rem 1.25rem;
                border-bottom: 1px solid #e5e7eb;
            }
            .customer-link {
                text-decoration: none;
                color: #111827;
                font-weight: 600;
            }
            .status-pill {
                padding: 0.25rem 0.75rem;
                border-radius: 9999px;
                font-size: 0.75rem;
                font-weight: 600;
            }
            /* Các style status cũ không cần nữa vì đã có style mới ở trên */
            .card-body {
                padding: 1.25rem;
                display: flex;
                flex-direction: column;
                gap: 1rem;
            }
            .card-info-row {
                display: flex;
                align-items: flex-start;
                gap: 0.75rem;
                color: #4b5563;
            }
            .card-info-row i {
                width: 16px;
                height: 16px;
                margin-top: 2px;
                flex-shrink: 0;
            }
            .rating-cell .feather-star {
                width: 16px;
                height: 16px;
            }
            .rating-cell .star-filled {
                color: #facc15;
                fill: #facc15;
            }
            .rating-cell .star-empty {
                color: #d1d5db;
            }
            .card-footer {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                padding: 1rem 1.25rem;
                border-top: 1px solid #e5e7eb;
                background-color: #f9fafb;
            }
            .action-buttons {
                display: flex;
                gap: 1rem;
            }
            .action-buttons a {
                color: #6b7280;
            }
            .action-buttons a:hover {
                color: #3b82f6;
            }
            .action-buttons a.delete-action:hover {
                color: #ef4444;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <div class="title">Phản hồi Khách hàng</div>
                        <div class="breadcrumb">Phản hồi / <span>Danh sách Phản hồi</span></div>
                    </div>
                </header>
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
                                <input type="text" name="query" placeholder="Tìm kiếm theo khách hàng, mã yêu cầu...">
                            </div>
                            <button type="submit" class="btn btn-secondary"><i data-feather="search"></i>Tìm kiếm</button>
                            <div class="toolbar-actions"></div>
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
                                    <div class="card-footer">
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/viewFeedback?id=${fb.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                                            <a href="${pageContext.request.contextPath}/editFeedback?id=${fb.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                                            <a href="${pageContext.request.contextPath}/deleteFeedback?id=${fb.id}" title="Xóa" class="delete-action" onclick="return confirm('Bạn có chắc chắn muốn xóa phản hồi này không?')"><i data-feather="trash-2"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace({'stroke-width': 1.5});
            });
        </script>
    </body>
</html>