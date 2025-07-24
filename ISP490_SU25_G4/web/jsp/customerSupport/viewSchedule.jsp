<%--
    Document   : viewSchedule.jsp
    Created on : Jun 21, 2025
    Author     : NGUYEN MINH / Gemini
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="dashboard" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết Lịch bảo trì - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        
        <style>
            html, body {
                height: 100%;
                font-family: 'Inter', sans-serif;
                margin: 0;
                background-color: #f9fafb;
                color: #1f2937;
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
                padding: 32px 40px;
            }

            /* --- Header & Actions --- */
            .page-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 32px;
                padding-bottom: 24px;
                border-bottom: 1px solid #e5e7eb;
            }

            .page-header h1 {
                font-size: 28px;
                font-weight: 700;
                margin: 0;
                color: #111827;
            }

            .page-header .actions {
                display: flex;
                gap: 12px;
            }

            .page-header .actions .btn {
                padding: 9px 18px;
                border: 1px solid transparent;
                border-radius: 8px;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.2s ease;
            }
            .page-header .actions .btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            }

            .btn-edit {
                background-color: #3b82f6;
                color: white;
            }
            .btn-edit:hover { background-color: #2563eb; }

            .btn-delete {
                background-color: #ef4444;
                color: white;
            }
            .btn-delete:hover { background-color: #dc2626; }

            /* --- Details Container --- */
            .details-container {
                background-color: white;
                padding: 40px;
                border-radius: 16px;
                border: 1px solid #e5e7eb;
                box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.05), 0 2px 4px -2px rgb(0 0 0 / 0.05);
            }

            .details-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 32px 40px;
                padding-bottom: 32px;
                border-bottom: 1px solid #f3f4f6; /* Subtle separator */
            }

            .detail-item {
                /* No margin needed as gap handles it */
            }

            .detail-label {
                display: block;
                font-weight: 500;
                margin-bottom: 12px;
                color: #6b7280;
                font-size: 13px;
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }

            .detail-value {
                font-size: 16px;
                font-weight: 600;
                color: #111827;
            }

            /* --- Employee List Styling --- */
            .detail-value ul {
                list-style: none;
                padding: 0;
                margin: 0;
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
            }

            .detail-value ul li {
                background-color: #f3f4f6;
                color: #374151;
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: 500;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            /* --- Status Tag Styling --- */
            .detail-value .status {
                padding: 6px 14px;
                border-radius: 16px;
                font-size: 13px;
                font-weight: 600;
                display: inline-block;
            }

            .status-upcoming { background-color: #fef3c7; color: #92400e; }
            .status-inprogress { background-color: #dbeafe; color: #1e40af; }
            .status-completed { background-color: #dcfce7; color: #15803d; }

            /* --- Description Section --- */
            .description-section {
                padding-top: 32px;
            }

            .description-section .detail-value {
                font-weight: 400; /* Regular weight for long text */
                color: #374151;
                line-height: 1.7;
                max-width: 75ch; /* Optimal line length for readability */
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                
                <section class="main-content-body">
                    <div class="page-header">
                        <h1>Chi tiết: Bảo trì hệ thống điều hòa</h1>
                        <div class="actions">
                            <a href="editSchedule.jsp?id=102" class="btn btn-edit"><i data-feather="edit-2" style="width:18px; height:18px;"></i> Sửa</a>
                            <a href="#" class="btn btn-delete"><i data-feather="trash-2" style="width:18px; height:18px;"></i> Xóa</a>
                        </div>
                    </div>

                    <div class="details-container">
                        <div class="details-grid">
                            <div class="detail-item">
                                <p class="detail-label">Khách hàng</p>
                                <p class="detail-value">Công ty An Phát</p>
                            </div>
                            <div class="detail-item">
                                <p class="detail-label">Trạng thái</p>
                                <p class="detail-value"><span class="status status-upcoming">Sắp tới</span></p>
                            </div>
                             <div class="detail-item">
                                <p class="detail-label">Ngày bắt đầu</p>
                                <p class="detail-value">17/06/2025</p>
                            </div>
                             <div class="detail-item">
                                <p class="detail-label">Ngày kết thúc</p>
                                <p class="detail-value">18/06/2025</p>
                            </div>
                            
                             <div class="detail-item" style="grid-column: 1 / -1;">
                                <p class="detail-label">Nhân viên phụ trách</p>
                                <div class="detail-value">
                                    <ul>
                                        <li><i data-feather="user" style="width:16px; height:16px;"></i> Nguyễn Văn An</li>
                                        <li><i data-feather="user" style="width:16px; height:16px;"></i> Trần Bình</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        
                        <div class="description-section">
                            <p class="detail-label">Mô tả / Ghi chú</p>
                            <p class="detail-value">Kiểm tra và vệ sinh toàn bộ hệ thống điều hòa trung tâm tại tầng 5. Mang theo thang và các dụng cụ cần thiết.</p>
                        </div>
                    </div>
                </section>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => feather.replace());
        </script>
    </body>
</html>