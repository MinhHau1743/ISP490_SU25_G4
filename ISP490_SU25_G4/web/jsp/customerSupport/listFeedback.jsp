<%--
    Document   : listFeedback.jsp
    Created on : Jul 22, 2025
    Author     : NGUYEN MINH / Gemini
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Phản hồi - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <%-- SỬA 1: Dùng contextPath cho tất cả các đường dẫn --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">

        <style>
            /* CSS của bạn giữ nguyên, không cần thay đổi */
            html, body {
                height: 100%;
                font-family: 'Inter', sans-serif;
                margin: 0;
                background-color: #f9fafb;
            }
            .content-wrapper {
                display: flex;
                flex-direction: column;
                flex-grow: 1;
                overflow: hidden;
            }
            .main-content-body {
                flex-grow: 1;
                overflow-y: auto;
                padding: 24px 32px;
            }

            .page-header {
                margin-bottom: 24px;
            }
            .page-header h1 {
                font-size: 28px;
                font-weight: 700;
                color: #111827;
                margin: 0;
            }

            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 20px;
                margin-bottom: 24px;
            }
            .stat-card {
                background-color: #fff;
                padding: 20px;
                border-radius: 12px;
                border: 1px solid #e5e7eb;
                display: flex;
                align-items: center;
                gap: 16px;
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
                font-size: 14px;
                margin-bottom: 4px;
            }
            .stat-card .info .value {
                font-size: 24px;
                font-weight: 700;
                color: #111827;
            }

            .toolbar-container {
                background-color: #fff;
                border-radius: 12px;
                border: 1px solid #e5e7eb;
                padding: 16px 20px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 24px;
            }
            .search-box {
                position: relative;
            }
            .search-box .feather-search {
                position: absolute;
                left: 12px;
                top: 50%;
                transform: translateY(-50%);
                color: #9ca3af;
            }
            .search-box input {
                padding: 9px 12px 9px 40px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                width: 280px;
            }
            .actions-group {
                display: flex;
                gap: 12px;
            }
            .btn {
                padding: 9px 16px;
                border-radius: 8px;
                font-weight: 600;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                cursor: pointer;
                text-decoration: none;
                border: 1px solid #d1d5db;
                background-color: #fff;
            }
            .btn-primary {
                background-color: #3b82f6;
                color: #fff;
                border-color: #3b82f6;
            }

            .list-panel {
                background-color: #fff;
                border-radius: 12px;
                border: 1px solid #e5e7eb;
            }

            .list-header {
                display: flex;
                padding: 16px 24px;
                border-bottom: 1px solid #d1d5db;
                ; /* SỬA Ở ĐÂY: Dùng đường kẻ đậm hơn */
                font-size: 12px;
                color: #6b7280;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                font-weight: 600;
                align-items: center;
                gap: 8px;
            }
            .list-header .col {
                font-size: 12px;
                color: #6b7280;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .feedback-list-container {
                display: flex;
                flex-direction: column;
            }

            .feedback-card {
                padding: 0 24px;
                border-bottom: 1px solid #e5e7eb; /* Đường kẻ đậm hơn với màu xám nhạt */
                transition: background-color 0.2s ease-in-out;
            }
            .feedback-card:last-child {
                border-bottom: none;
            }
            .feedback-card:hover {
                background-color: #f9fafb;
            }

            .main-row {
                display: flex;
                padding: 16px 0;
                align-items: center;
            }
            .comment-row {
                padding: 0 0 16px 0;
                display: flex;
                align-items: flex-start;
                gap: 12px;
                color: #4b5563;
                font-size: 14px;
            }
            .comment-row p {
                margin: 0;
                font-style: italic;
            }

            .col {
                padding: 0 8px;
            }
            .col-customer {
                flex: 0 0 30%;
            }
            .col-service  {
                flex: 0 0 25%;
            }
            .col-rating   {
                flex: 0 0 20%;
            }
            .col-status   {
                flex: 0 0 15%;
            }
            .col-date     {
                flex: 0 0 10%;
                text-align: right;
            }

            .customer-cell {
                display: flex;
                align-items: center;
                gap: 12px;
            }
            .customer-avatar {
                width: 36px;
                height: 36px;
                border-radius: 50%;
                background-color: #e0e7ff;
                color: #4338ca;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: 600;
                text-transform: uppercase;
            }
            .customer-name {
                font-weight: 600;
                color: #111827;
            }

            .rating-cell .feather-star {
                width: 18px;
                height: 18px;
            }
            .rating-cell .star-filled {
                color: #facc15;
                fill: #facc15;
            }
            .rating-cell .star-empty {
                color: #d1d5db;
            }

            .status-tag {
                padding: 4px 10px;
                border-radius: 16px;
                font-weight: 600;
                font-size: 12px;
                display: inline-block;
            }
            .status-good {
                background-color: #dcfce7;
                color: #16a34a;
            }
            .status-normal {
                background-color: #fef9c3;
                color: #a16207;
            }
            .status-bad {
                background-color: #fee2e2;
                color: #b91c1c;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <div class="content-wrapper">

                <section class="main-content-body">
                    <div class="page-header">
                        <h1>Phản hồi Khách hàng</h1>
                    </div>

                    <%-- SỬA 2: Hiển thị các số liệu thống kê động --%>
                    <div class="stats-grid">
                        <div class="stat-card"><div class="icon-wrapper total"><i data-feather="message-square"></i></div><div class="info"><div class="title">Tổng phản hồi</div><div class="value"><fmt:formatNumber value="${totalCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper good"><i data-feather="trending-up"></i></div><div class="info"><div class="title">Rất hài lòng</div><div class="value"><fmt:formatNumber value="${goodCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper normal"><i data-feather="minus"></i></div><div class="info"><div class="title">Bình thường</div><div class="value"><fmt:formatNumber value="${normalCount}" type="number"/></div></div></div>
                        <div class="stat-card"><div class="icon-wrapper bad"><i data-feather="trending-down"></i></div><div class="info"><div class="title">Chưa hài lòng</div><div class="value"><fmt:formatNumber value="${badCount}" type="number"/></div></div></div>
                    </div>

                    <div class="toolbar-container">
                        <div class="search-box"><i data-feather="search" style="width:18px;"></i><input type="text" placeholder="Tìm kiếm theo khách hàng, dịch vụ..."></div>
                        <div class="actions-group">
                            <button class="btn"><i data-feather="filter" style="width:16px;"></i> Lọc</button>
                        </div>
                    </div>

                    <div class="list-panel">
                        <div class="list-header">
                            <div class="col col-customer"><i data-feather="users"></i> KHÁCH HÀNG</div>
                            <div class="col col-service"><i data-feather="tool"></i> DỊCH VỤ</div>
                            <div class="col col-rating"><i data-feather="star"></i> ĐÁNH GIÁ</div>
                            <div class="col col-status"><i data-feather="activity"></i> TRẠNG THÁI</div>
                            <div class="col col-date"><i data-feather="calendar"></i> NGÀY</div>
                        </div>
                        <div class="feedback-list-container">

                            <%-- SỬA 3: Dùng vòng lặp để hiển thị danh sách --%>
                            <c:if test="${empty feedbackList}">
                                <p style="text-align: center; padding: 40px; color: #6b7280;">Không có phản hồi nào để hiển thị.</p>
                            </c:if>

                            <c:forEach var="fb" items="${feedbackList}">
                                <div class="feedback-card">
                                    <div class="main-row">
                                        <div class="col col-customer">
                                            <div class="customer-cell">
                                                <%-- Bọc thẻ a quanh avatar và tên --%>
                                                <a href="${pageContext.request.contextPath}/viewFeedback?id=${fb.id}" style="text-decoration: none; display: flex; align-items: center; gap: 12px;">
                                                    <div class="customer-avatar">${fn:substring(fb.enterpriseName, 0, 1)}</div>
                                                    <div class="customer-name">${fb.enterpriseName}</div>
                                                </a>
                                            </div>
                                        </div>
                                        <div class="col col-service">${fb.serviceName}</div>
                                        <div class="col col-rating rating-cell">
                                            <c:forEach begin="1" end="5" var="i">
                                                <c:choose>
                                                    <c:when test="${i <= fb.rating}">
                                                        <i data-feather="star" class="star-filled"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i data-feather="star" class="star-empty"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                        <div class="col col-status">
                                            <c:choose>
                                                <c:when test="${fb.rating >= 4}"><span class="status-tag status-good">Rất hài lòng</span></c:when>
                                                <c:when test="${fb.rating == 3}"><span class="status-tag status-normal">Bình thường</span></c:when>
                                                <c:otherwise><span class="status-tag status-bad">Chưa hài lòng</span></c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="col col-date">
                                            <fmt:formatDate value="${fb.createdAt}" pattern="dd/MM/yyyy" />
                                        </div>
                                    </div>
                                    <c:if test="${not empty fb.comment}">
                                        <div class="comment-row">
                                            <i data-feather="message-circle" style="width: 18px; flex-shrink: 0;"></i>
                                            <p>"${fb.comment}"</p>
                                        </div>
                                    </c:if>
                                </div>
                            </c:forEach>

                        </div>
                    </div>

                </section>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace({'stroke-width': 1.5});
            });
        </script>
    </body>
</html>