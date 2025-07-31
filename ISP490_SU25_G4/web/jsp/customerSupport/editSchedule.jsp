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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editSchedule.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                <c:if test="${not empty error}">
                    <div class="alert alert-warning alert-dismissible fade show">
                        <button type="button" class="close" data-dismiss="alert">&times;</button>
                        <strong></strong>${error}
                    </div>
                </c:if>
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
        <script src="${pageContext.request.contextPath}/js/editSchedule.js"></script>               
    </body>
</html>
