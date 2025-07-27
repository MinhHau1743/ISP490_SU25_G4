<%--
    Document   : createSchedule.jsp
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
        <title>Thêm mới Lịch bảo trì - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">

        <style>
            html, body { height: 100%; font-family: 'Inter', sans-serif; margin: 0; background-color: #f9fafb; }
            .content-wrapper { display: flex; flex-direction: column; flex-grow: 1; overflow: hidden; }
            .main-content-body { flex-grow: 1; overflow-y: auto; padding: 24px 32px; }
            
            /* CSS CHUNG CHO FORM VÀ VIEW DETAILS */
            .page-header {
                display: flex; justify-content: space-between; align-items: center;
                margin-bottom: 24px; padding-bottom: 16px; border-bottom: 1px solid #e5e7eb;
            }
            .page-header h1 { font-size: 24px; margin: 0; }
            .page-header .actions .btn { padding: 8px 16px; border-radius: 8px; text-decoration: none; display: inline-flex; align-items: center; gap: 8px; font-weight: 600; }
            .btn-edit { background-color: #3b82f6; color: white; }
            .btn-delete { background-color: #ef4444; color: white; }

            .form-container, .details-container {
                background-color: white;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            }
            
            .form-grid, .details-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 24px 32px;
            }

            .form-group, .detail-item { margin-bottom: 20px; }
            .form-group label, .detail-label { display: block; font-weight: 600; margin-bottom: 8px; color: #374151; }
            
            .form-control {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                font-size: 14px;
                box-sizing: border-box;
            }
            .form-control:focus { border-color: #8B4513; box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.2); outline: none; }
            textarea.form-control { min-height: 120px; resize: vertical; }

            .detail-value { font-size: 16px; color: #111827; }
            .detail-value .status { padding: 4px 12px; border-radius: 16px; font-size: 12px; font-weight: 600;}
            .detail-value .status.status-upcoming { background-color: #fff8e1; color: #ffa000; }
            .detail-value .status.status-inprogress { background-color: #e3f2fd; color: #1565c0; }
            .detail-value .status.status-completed { background-color: #e3f4e3; color: #2e7d32; }

            .checkbox-group { display: flex; flex-wrap: wrap; gap: 16px; }
            .checkbox-item { display: flex; align-items: center; gap: 8px; }

            .form-actions {
                margin-top: 32px; padding-top: 24px;
                border-top: 1px solid #e5e7eb;
                display: flex; justify-content: flex-end; gap: 12px;
            }
            .form-actions .btn { padding: 10px 20px; border-radius: 8px; font-weight: 600; text-decoration: none; }
            .btn-secondary { background-color: #e5e7eb; color: #374151; border: none; }
            .btn-primary { background-color: #8B4513; color: white; border: none; }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                <header class="main-top-bar"></header>
                <section class="main-content-body">
                    <div class="page-header">
                        <h1>Lên lịch bảo trì mới</h1>
                    </div>
                    
                    <form action="/your-app/schedule/create" method="post" class="form-container">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="job-name">Tên công việc</label>
                                <input type="text" id="job-name" name="jobName" class="form-control" placeholder="VD: Bảo trì hệ thống điều hòa" required>
                            </div>
                             <div class="form-group">
                                <label for="customer">Khách hàng</label>
                                <select id="customer" name="customerId" class="form-control">
                                    <option value="1">Công ty CP Xây dựng An Phát</option>
                                    <option value="2">Tòa nhà Keangnam</option>
                                    <option value="3">Khách sạn Grand Plaza</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="start-date">Ngày bắt đầu</label>
                                <input type="date" id="start-date" name="startDate" class="form-control" required>
                            </div>
                             <div class="form-group">
                                <label for="end-date">Ngày kết thúc</label>
                                <input type="date" id="end-date" name="endDate" class="form-control">
                            </div>
                            <div class="form-group">
                                <label>Nhân viên phụ trách</label>
                                <div class="checkbox-group">
                                    <div class="checkbox-item"><input type="checkbox" id="nv1" name="staff" value="1"><label for="nv1">Nguyễn Văn An</label></div>
                                    <div class="checkbox-item"><input type="checkbox" id="nv2" name="staff" value="2"><label for="nv2">Trần Bình</label></div>
                                    <div class="checkbox-item"><input type="checkbox" id="nv3" name="staff" value="3"><label for="nv3">Đội kỹ thuật 1</label></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="status">Trạng thái</label>
                                <select id="status" name="status" class="form-control">
                                    <option value="upcoming" selected>Sắp tới</option>
                                    <option value="inprogress">Đang thực hiện</option>
                                    <option value="completed">Hoàn thành</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="description">Mô tả chi tiết</label>
                            <textarea id="description" name="description" class="form-control" rows="5" placeholder="Thêm các ghi chú hoặc mô tả chi tiết về công việc..."></textarea>
                        </div>
                        
                        <div class="form-actions">
                            <a href="/your-app/schedule/list" class="btn btn-secondary">Hủy bỏ</a>
                            <button type="submit" class="btn btn-primary">Lưu lại</button>
                        </div>
                    </form>
                </section>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => feather.replace());
        </script>
    </body>
</html>