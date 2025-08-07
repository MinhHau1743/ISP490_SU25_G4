<%-- 
    Document   : viewFeedback
    Created on : Jul 22, 2025, 9:00:26 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Phản hồi - ${feedback.enterpriseName}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <script src="https://unpkg.com/feather-icons"></script>

        <style>
            /* === BỐ CỤC CHUNG === */
            html, body {
                height: 100%;
                font-family: 'Inter', sans-serif;
                margin: 0;
            }
            .app-container {
                display: flex;
                height: 100vh;
            }
            .main-content {
                flex-grow: 1;
                display: flex;
                flex-direction: column;
                background-color: #f3f4f6;
            }
            .page-header {
                background-color: #ffffff;
                padding: 1.5rem 2rem;
                border-bottom: 1px solid #e5e7eb;
                display: flex;
                justify-content: space-between;
                align-items: center;
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
            .page-header .actions-group {
                display: flex;
                gap: 12px;
            }
            .page-content {
                padding: 1.5rem 2rem;
                flex-grow: 1;
                overflow-y: auto;
            }

            /* === MODIFICATION START: Thiết kế lại hệ thống nút bấm === */
            .btn {
                padding: 9px 16px;
                border-radius: 8px;
                font-weight: 600;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                cursor: pointer;
                text-decoration: none;
                border: 1px solid transparent; /* Mặc định trong suốt */
                transition: all 0.2s ease-in-out;
            }
            .btn span {
                line-height: 1;
            }
            
            /* Nút chính: nổi bật, có bóng mờ */
            .btn-primary {
                background-color: #2563eb;
                color: #fff;
                border-color: #2563eb;
                box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1);
            }
            .btn-primary:hover {
                background-color: #1d4ed8;
            }

            /* Nút phụ: nhẹ nhàng, nền trắng */
            .btn-secondary {
                background-color: #fff;
                color: #374151;
                border-color: #d1d5db;
            }
            .btn-secondary:hover {
                background-color: #f9fafb;
                border-color: #aab2bd;
            }
            /* === MODIFICATION END === */

            .details-layout {
                display: grid;
                grid-template-columns: 2fr 1fr;
                gap: 24px;
                align-items: flex-start;
            }
            .card {
                background-color: #fff;
                border: 1px solid #d1d5db;
                border-radius: 12px;
                overflow: hidden;
            }
            .main-content-column {
                display: flex;
                flex-direction: column;
                gap: 24px;
            }
            .feedback-main {
                padding: 24px;
            }
            .rating-display {
                display: flex;
                gap: 4px;
                margin-bottom: 16px;
            }
            .rating-display .feather-star {
                width: 24px;
                height: 24px;
            }
            .rating-display .star-filled {
                color: #facc15;
                fill: #facc15;
            }
            .rating-display .star-empty {
                color: #d1d5db;
            }
            .feedback-comment blockquote {
                margin: 0;
                padding-left: 20px;
                border-left: 4px solid #3b82f6;
                font-size: 18px;
                line-height: 1.7;
                color: #374151;
                font-style: italic;
            }
            .internal-notes {
                padding: 0;
            }
            .internal-notes .notes-content {
                padding: 24px;
            }
            
            /* === MODIFICATION START: Phân màu cho từng thanh tiêu đề === */
            .details-sidebar .card-header, .internal-notes h3 {
                padding: 12px 16px;
                font-weight: 600;
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 16px;
                margin: 0;
            }
            .details-sidebar .card-header i, .internal-notes h3 i {
                stroke-width: 2;
            }

            /* Màu cho Thảo luận (xanh dương) */
            .internal-notes h3 {
                background-color: #eff6ff; /* blue-50 */
                color: #1e40af; /* blue-800 */
                border-bottom: 1px solid #dbeafe; /* blue-200 */
            }
            .internal-notes h3 i { color: #1e40af; }
            
            /* Màu cho Trạng thái (xanh lá) */
            .card-status .card-header {
                background-color: #f0fdf4; /* green-50 */
                color: #166534; /* green-800 */
                border-bottom: 1px solid #dcfce7; /* green-200 */
            }
            .card-status .card-header i { color: #166534; }
            
            /* Màu cho Khách hàng (vàng cam) */
            .card-customer .card-header {
                background-color: #fffbeb; /* amber-50 */
                color: #92400e; /* amber-800 */
                border-bottom: 1px solid #fef3c7; /* amber-200 */
            }
            .card-customer .card-header i { color: #92400e; }

            /* Màu cho Công việc (tím) */
            .card-related-work .card-header {
                background-color: #eef2ff; /* indigo-50 */
                color: #3730a3; /* indigo-800 */
                border-bottom: 1px solid #e0e7ff; /* indigo-200 */
            }
            .card-related-work .card-header i { color: #3730a3; }
            /* === MODIFICATION END === */
            
            .details-sidebar {
                display: flex;
                flex-direction: column;
                gap: 24px;
            }
            .details-sidebar .card-body {
                padding: 16px;
            }
            .details-sidebar ul {
                list-style: none;
                padding: 0;
                margin: 0;
                display: flex;
                flex-direction: column;
                gap: 12px;
            }
            .details-sidebar ul li {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 14px;
            }
            .details-sidebar .label {
                color: #6b7280;
            }
            .details-sidebar .value {
                font-weight: 500;
                color: #111827;
            }
            .details-sidebar .status-tag {
                padding: 4px 10px;
                border-radius: 16px;
                font-weight: 600;
                font-size: 12px;
            }
            .details-sidebar .status-new {
                background-color: #dcfce7;
                color: #166534;
            }
        </style>
    </head>

    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="page-header">
                    <div class="title-section">
                        <div class="title">Chi tiết Phản hồi</div>
                        <div class="breadcrumb">Phản hồi / <span>${feedback.enterpriseName}</span></div>
                    </div>
                    <%-- MODIFICATION: Thiết kế lại nhóm nút hành động --%>
                    <div class="actions-group">
<a href="${pageContext.request.contextPath}/feedback?action=list" class="btn btn-secondary">
                            <i data-feather="arrow-left"></i>
                            <span>Quay lại</span>
                        </a>
                        <button class="btn btn-primary">
                            <i data-feather="archive"></i>
                            <span>Lưu trữ</span>
                        </button>
                    </div>
                </header>

                <div class="page-content">
                    <c:if test="${not empty feedback}">
                        <div class="details-layout">
                            <div class="main-content-column">
                                <div class="card feedback-main">
                                    <div class="rating-display">
                                        <c:forEach begin="1" end="5" var="i">
                                            <i data-feather="star" class="${i <= feedback.rating ? 'star-filled' : 'star-empty'}"></i>
                                        </c:forEach>
                                    </div>
                                    <div class="feedback-comment">
                                        <c:if test="${not empty feedback.comment}">
                                            <blockquote>"${feedback.comment}"</blockquote>
                                        </c:if>
                                        <c:if test="${empty feedback.comment}">
                                            <p style="font-style: italic; color: #9ca3af;">Khách hàng không để lại bình luận.</p>
                                        </c:if>
                                    </div>
                                </div>
                                <div class="card internal-notes">
                                    <h3><i data-feather="message-square"></i> Thảo luận nội bộ</h3>
                                    <div class="notes-content">
                                        <div class="notes-list" style="margin-bottom: 24px;">
                                            <c:if test="${empty internalNotes}">
                                                <p style="color: #9ca3af; font-style: italic;">Chưa có ghi chú nào.</p>
                                            </c:if>
                                            <c:forEach var="note" items="${internalNotes}">
                                                <div class="note-item" style="margin-bottom: 16px; border-bottom: 1px solid #f3f4f6; padding-bottom: 12px;">
                                                    <p style="margin: 0 0 8px 0;">${note.noteText}</p>
                                                    <div class="note-meta" style="font-size: 12px; color: #9ca3af;">
                                                        <strong>${note.userName}</strong> - 
                                                        <fmt:formatDate value="${note.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <div class="add-note-form">
<form action="${pageContext.request.contextPath}/feedback?action=addNote" method="POST">
                                                <input type="hidden" name="feedbackId" value="${feedback.id}">
                                                <textarea name="noteText" rows="3" placeholder="Thêm ghi chú nội bộ..." required style="width: 100%; box-sizing: border-box; padding: 12px; border: 1px solid #d1d5db; border-radius: 8px; margin-bottom: 12px; resize: vertical;"></textarea>
                                                <button type="submit" class="btn btn-primary" style="width: 100%;">Thêm ghi chú</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="details-sidebar">
                                <%-- MODIFICATION: Thêm class để phân màu --%>
                                <div class="card card-status">
                                    <div class="card-header"><i data-feather="activity"></i> Trạng thái</div>
                                    <div class="card-body">
                                        <ul>
                                            <li><span class="label">Trạng thái:</span> <span class="value"><span class="status-tag status-new">${feedback.status}</span></span></li>
                                            <li><span class="label">Ngày nhận:</span> <span class="value"><fmt:formatDate value="${feedback.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span></li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="card card-customer">
                                    <div class="card-header"><i data-feather="user"></i> Khách hàng</div>
                                    <div class="card-body">
                                        <ul>
                                            <li><span class="label">Tên:</span> <span class="value">${feedback.enterpriseName}</span></li>
                                            <li><span class="label">Email:</span> <span class="value">${not empty feedback.enterpriseEmail ? feedback.enterpriseEmail : 'N/A'}</span></li>
                                            <li><span class="label">SĐT:</span> <span class="value">${not empty feedback.enterprisePhone ? feedback.enterprisePhone : 'N/A'}</span></li>
                                        </ul>
                                    </div>
                                </div>
                                <div class="card card-related-work">
                                    <div class="card-header"><i data-feather="tool"></i> Công việc liên quan</div>
                                    <div class="card-body">
                                        <ul>
                                            <li><span class="label">Dịch vụ:</span> <span class="value">${not empty feedback.serviceName ? feedback.serviceName : 'N/A'}</span></li>
                                            <li><span class="label">Nhân viên:</span> <span class="value">${not empty feedback.technicianName ? feedback.technicianName : 'N/A'}</span></li>
                                            <li>
                                                <span class="label">Mã YC:</span> 
                                                <a href="${pageContext.request.contextPath}/ticket?action=view&id=${feedback.relatedRequestId}" class="value" style="color: #2563eb; text-decoration: underline;">
                                                   ${not empty feedback.requestCode ? feedback.requestCode : 'N/A'}
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${empty feedback}">
                        <p>Không tìm thấy thông tin phản hồi.</p>
                    </c:if>
                </div>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace({'stroke-width': 1.7}); // Tăng độ dày icon cho dễ nhìn hơn
            });
        </script>
    </body>
</html>