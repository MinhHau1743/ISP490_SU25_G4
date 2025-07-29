<%--
    Document   : editSchedule.jsp
    Created on : Jun 21, 2025
    Author     : NGUYEN MINH / Updated by Grok
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="dashboard" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Lịch bảo trì - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">

        <style>
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
                font-size: 24px;
                margin: 0;
                color: #111827;
            }

            .form-container {
                background-color: white;
                padding: 32px;
                border-radius: 12px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            }
            .form-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 24px 32px;
            }
            .form-group {
                margin-bottom: 20px;
            }
            .form-group.full-width {
                grid-column: 1 / -1;
            }
            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #374151;
                font-size: 14px;
            }
            .form-control {
                width: 100%;
                padding: 10px 12px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                font-size: 14px;
                box-sizing: border-box;
            }
            .form-control:focus {
                border-color: #8B4513;
                box-shadow: 0 0 0 2px rgba(139, 69, 19, 0.2);
                outline: none;
            }
            textarea.form-control {
                min-height: 120px;
                resize: vertical;
            }
            .color-picker {
                width: 40px;
                height: 40px;
                padding: 0;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }
            .form-actions {
                margin-top: 32px;
                padding-top: 24px;
                border-top: 1px solid #e5e7eb;
                display: flex;
                justify-content: flex-end;
                gap: 12px;
            }
            .form-actions .btn {
                padding: 10px 20px;
                border-radius: 8px;
                font-weight: 600;
                text-decoration: none;
                border: none;
                cursor: pointer;
            }
            .btn-secondary {
                background-color: #e5e7eb;
                color: #374151;
            }
            .btn-primary {
                background-color: #8B4513;
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                <section class="main-content-body">
                    <div class="page-header">
                        <h1>Chỉnh sửa Lịch bảo trì</h1>
                    </div>

                    <form action="${pageContext.request.contextPath}/updateSchedule" method="post" class="form-container">
                        <input type="hidden" name="id" value="${schedule.id}">

                        <div class="form-grid">
                            <div class="form-group">
                                <label for="title">Tên công việc <span style="color: red;">*</span></label>
                                <input type="text" id="title" name="title" class="form-control" value="${schedule.title}" required>
                            </div>
                            <div class="form-group">
                                <label for="technicalRequestId">ID Yêu cầu kỹ thuật</label>
                                <select id="technicalRequestId" name="technicalRequestId" class="form-control">
                                    <option value="">Không liên kết</option>
                                    <c:forEach var="request" items="${technicalRequests}">
                                        <option value="${request.id}" <c:if test="${request.id == schedule.technicalRequestId}">selected</c:if>>${request.title}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="scheduledDate">Ngày bắt đầu <span style="color: red;">*</span></label>
                                <input type="date" id="scheduledDate" name="scheduledDate" class="form-control" value="${schedule.scheduledDate}" required>
                            </div>
                            <div class="form-group">
                                <label for="endDate">Ngày kết thúc</label>
                                <input type="date" id="endDate" name="endDate" class="form-control" value="${schedule.endDate}">
                            </div>
                            <div class="form-group">
                                <label for="startTime">Giờ bắt đầu</label>
                                <input type="time" id="startTime" name="startTime" class="form-control" value="${schedule.startTime}">
                            </div>
                            <div class="form-group">
                                <label for="endTime">Giờ kết thúc</label>
                                <input type="time" id="endTime" name="endTime" class="form-control" value="${schedule.endTime}">
                            </div>
                            <div class="form-group">
                                <label for="location">Địa điểm</label>
                                <input type="text" id="location" name="location" class="form-control" value="${schedule.location}">
                            </div>
                            <div class="form-group">
                                <label for="status">Trạng thái <span style="color: red;">*</span></label>
                                <select id="status" name="status" class="form-control" required>
                                    <option value="upcoming" <c:if test="${schedule.status == 'upcoming'}">selected</c:if>>Sắp tới</option>
                                    <option value="inprogress" <c:if test="${schedule.status == 'inprogress'}">selected</c:if>>Đang thực hiện</option>
                                    <option value="completed" <c:if test="${schedule.status == 'completed'}">selected</c:if>>Hoàn thành</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="color">Màu sắc</label>
                                    <input type="color" id="color" name="color" class="form-control color-picker" value="${schedule.color != null ? schedule.color : '#007bff'}">
                            </div>
                            <div class="form-group full-width">
                                <label for="notes">Ghi chú</label>
                                <textarea id="notes" name="notes" class="form-control" rows="5">${schedule.notes}</textarea>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/listSchedule" class="btn btn-secondary">Hủy bỏ</a>
                            <button type="submit" class="btn btn-primary">Cập nhật</button>
                        </div>
                    </form>
                </section>
            </div>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace();

                // Xử lý logic cho ngày và giờ
                const startDateInput = document.getElementById('scheduledDate');
                const endDateInput = document.getElementById('endDate');
                const startTimeInput = document.getElementById('startTime');
                const endTimeInput = document.getElementById('endTime');

                // Đảm bảo endDate >= scheduledDate
                startDateInput.addEventListener('change', () => {
                    if (endDateInput.value && endDateInput.value < startDateInput.value) {
                        endDateInput.value = startDateInput.value;
                    }
                    endDateInput.min = startDateInput.value;
                });

                // Đảm bảo endTime hợp lý khi startTime thay đổi
                startTimeInput.addEventListener('change', () => {
                    if (startTimeInput.value && !endTimeInput.value) {
                        const startTime = new Date(`1970-01-01T${startTimeInput.value}`);
                        startTime.setMinutes(startTime.getMinutes() + 60); // Mặc định thêm 1 giờ
                        endTimeInput.value = startTime.toTimeString().slice(0, 5);
                    }
                });

                // Đảm bảo endTime >= startTime nếu cùng ngày
                endTimeInput.addEventListener('change', () => {
                    if (startTimeInput.value && endTimeInput.value && startDateInput.value === endDateInput.value) {
                        const startTime = startTimeInput.value;
                        const endTime = endTimeInput.value;
                        if (endTime < startTime) {
                            endTimeInput.value = startTime;
                        }
                    }
                });
            });
        </script>
    </body>
</html>
