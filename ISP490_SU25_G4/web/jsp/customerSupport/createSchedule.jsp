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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createSchedule.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                
                <header class="main-top-bar"></header>
                <c:if test="${not empty error}">
                    <div class="alert alert-warning alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <strong></strong>${error}
                    </div>
                </c:if>
                <section class="main-content-body">
                    <div class="page-header">
                        <h1>Lên lịch bảo trì mới</h1>
                    </div>

                    <form action="createSchedule" method="post" class="form-container">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="title">Tiêu đề</label>
                                <input type="text" id="title" name="title" class="form-control" placeholder="VD: Bảo trì hệ thống điều hòa" required>
                            </div>
                            <div class="form-group">
                                <label for="technical_request_id">Yêu cầu kỹ thuật</label>
                                <select id="technical_request_id" name="technical_request_id" class="form-control">
                                    <option value="">-- Chọn yêu cầu kỹ thuật --</option>
                                    <c:forEach items="${schedules}" var="schedule">
                                        <option value="${schedule.id}">${schedule.title}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="scheduled_date">Ngày bắt đầu</label>
                                <input type="date" id="scheduled_date" name="scheduled_date" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label for="end_date">Ngày kết thúc</label>
                                <input type="date" id="end_date" name="end_date" class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="start_time">Giờ bắt đầu</label>
                                <input type="time" id="start_time" name="start_time" class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="end_time">Giờ kết thúc</label>
                                <input type="time" id="end_time" name="end_time" class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="location">Địa điểm</label>
                                <input type="text" id="location" name="location" class="form-control" placeholder="VD: Văn phòng chính">
                            </div>
                            <div class="form-group">
                                <label for="status">Trạng thái</label>
                                <select id="status" name="status" class="form-control">
                                    <option value="upcoming" selected>Sắp tới</option>
                                    <option value="inprogress">Đang thực hiện</option>
                                    <option value="completed">Hoàn thành</option>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="color">Màu sắc</label>
                                <input type="color" id="color" name="color" class="form-control color-picker" value="${schedule.color != null ? schedule.color : '#007bff'}">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="notes">Ghi chú</label>
                            <textarea id="notes" name="notes" class="form-control" rows="5" placeholder="Thêm các ghi chú hoặc mô tả chi tiết về công việc..."></textarea>
                        </div>

                        <div class="form-actions">
                            <a href="listSchedule" class="btn btn-secondary">Hủy bỏ</a>
                            <button type="submit" class="btn btn-primary">Lưu lại</button>
                        </div>
                    </form>
                </section>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/js/createSchedule.js"></script>
    </body>
</html>