<%--
    Document   : createSchedule.jsp
    Created on : Jun 21, 2025
    Author     : NGUYEN MINH / Gemini (Refactored)
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="dashboard" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm mới Lịch bảo trì - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com  ">
        <link rel="preconnect" href="https://fonts.gstatic.com  " crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400  ;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css  ">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createSchedule.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="/mainMenu.jsp"/>

            <div class="main-panel">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Lên lịch bảo trì"/>
                </jsp:include>

                <main class="content-wrapper">
                    <section class="main-content-body">
                        <div class="page-header">
                            <h1>Lên lịch bảo trì mới</h1>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>

                        <form action="createSchedule" method="post" class="form-container">
                            <div class="form-grid">

                                <div class="form-group">
                                    <label for="title">Tiêu đề (*)</label>
                                    <input type="text" id="title" name="title" class="form-control" value="${title}" placeholder="VD: Bảo trì hệ thống điều hòa" required>
                                </div>

                                <div class="form-group">
                                    <label for="technical_request_id">Yêu cầu kỹ thuật</label>
                                    <select id="technical_request_id" name="technical_request_id" class="form-control">
                                        <option value="">-- Chọn yêu cầu kỹ thuật --</option>
                                        <c:forEach var="techRequest" items="${technicalRequests}">
                                            <option value="${techRequest.id}" ${techRequest.id == technical_request_id ? 'selected' : ''}>${techRequest.title}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="scheduled_date">Ngày bắt đầu (*)</label>
                                    <input type="date" id="scheduled_date" name="scheduled_date" value="${scheduled_date}" class="form-control" required>
                                </div>

                                <div class="form-group">
                                    <label for="end_date">Ngày kết thúc</label>
                                    <input type="date" id="end_date" name="end_date" value="${end_date}" class="form-control">
                                </div>

                                <div class="form-group">
                                    <label for="start_time">Giờ bắt đầu</label>
                                    <input type="time" id="start_time" name="start_time" value="${start_time}" class="form-control">
                                </div>

                                <div class="form-group">
                                    <label for="end_time">Giờ kết thúc</label>
                                    <input type="time" id="end_time" name="end_time" value="${end_time}" class="form-control">
                                </div>
                                <div class="address-grid">
                                    <div class="form-group">
                                        <label for="province">Tỉnh/Thành phố (*)</label>
                                        <select id="province" name="province" class="form-control" required>
                                            <option value="">-- Chọn Tỉnh/Thành --</option>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" ${p.id == provinceId ? 'selected' : ''}>${p.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="district">Quận/Huyện (*)</label>
                                        <select id="district" name="district" class="form-control" required>
                                            <option value="">-- Chọn Quận/Huyện --</option>
                                            <c:if test="${not empty districts}">
                                                <c:forEach var="d" items="${districts}">
                                                    <option value="${d.id}" ${d.id == districtId ? 'selected' : ''}>${d.name}</option>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="ward">Phường/Xã (*)</label>
                                        <select id="ward" name="ward" class="form-control" required>
                                            <option value="">-- Chọn Phường/Xã --</option>
                                            <c:if test="${not empty wards}">
                                                <c:forEach var="w" items="${wards}">
                                                    <option value="${w.id}" ${w.id == wardId ? 'selected' : ''}>${w.name}</option>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="streetAddress">Địa chỉ cụ thể</label>
                                    <input type="text" id="streetAddress" name="streetAddress" class="form-control" value="${streetAddress}" placeholder="Số nhà, tên đường...">
                                </div>
                                <div class="form-group">
                                    <label for="assignedUsers">Người phân công</label>
                                    <div class="tag-input-wrapper">
                                        <div class="selected-tags" id="selectedTags"></div>
                                        <input type="text" 
                                               id="userSearch" 
                                               class="form-control tag-input" 
                                               placeholder="Nhập tên để tìm nhân viên..."
                                               autocomplete="off">
                                        <div id="userDropdown" class="user-dropdown"></div>
                                    </div>
                                    <input type="hidden" name="assignedUserIds" id="hiddenUserIds">
                                </div>
                                <div class="form-group">
                                    <label for="status">Trạng thái</label>
                                    <select id="status" name="status" class="form-control">
                                        <option value="upcoming" ${status == 'upcoming' ? 'selected' : ''}>Sắp tới</option>
                                        <option value="inprogress" ${status == 'inprogress' ? 'selected' : ''}>Đang thực hiện</option>
                                        <option value="completed" ${status == 'completed' ? 'selected' : ''}>Hoàn thành</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>Màu sắc</label>
                                    <div class="color-palette">
                                        <span class="color-swatch" data-color="#007bff" style="background-color: #007bff;"></span>
                                        <span class="color-swatch" data-color="#dc3545" style="background-color: #dc3545;"></span>
                                        <span class="color-swatch" data-color="#28a745" style="background-color: #28a745;"></span>
                                        <span class="color-swatch" data-color="#ffc107" style="background-color: #ffc107;"></span>
                                        <span class="color-swatch" data-color="#fd7e14" style="background-color: #fd7e14;"></span>
                                        <span class="color-swatch" data-color="#17a2b8" style="background-color: #17a2b8;"></span>
                                        <span class="color-swatch" data-color="#6610f2" style="background-color: #6610f2;"></span>
                                        <span class="color-swatch" data-color="#343a40" style="background-color: #343a40;"></span>
                                    </div>
                                    <input type="hidden" id="color" name="color" value="${not empty color ? color : '#007bff'}">
                                </div>

                                <div class="form-group form-group-full-width">
                                    <label for="notes">Ghi chú</label>
                                    <textarea id="notes" name="notes" class="form-control" rows="4" placeholder="Thêm mô tả chi tiết về công việc...">${notes}</textarea>
                                </div>
                            </div>

                            <div class="form-actions">
                                <a href="listSchedule" class="btn btn-secondary">Hủy bỏ</a>
                                <button type="submit" class="btn btn-primary">Lưu lại</button>
                            </div>
                        </form>
                    </section>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js  "></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js  "></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js  "></script>
        <script src="https://unpkg.com/feather-icons  "></script>

        <script>
            window.PRESELECTED_ADDRESS = {
                provinceId: '${param.province != null ? param.province : schedule.provinceId}',
                districtId: '${param.district != null ? param.district : schedule.districtId}',
                wardId: '${param.ward != null ? param.ward : schedule.wardId}'
            };
            window.ADDR_CONTEXT_PATH = '${pageContext.request.contextPath}';
            window.SCHEDULE_USERS = [
            <c:forEach var="u" items="${assignments}" varStatus="status">
            {id: '${u.id}', name: '${u.fullName}'}<c:if test="${!status.last}">,</c:if>
            </c:forEach>
            ];
            window.SCHEDULE_SELECTED_USER_IDS = [
            <c:forEach var="id" items="${assignedUserIds}" varStatus="status">
            '${id}'<c:if test="${!status.last}">,</c:if>
            </c:forEach>
            ];
        </script>

        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script src="${pageContext.request.contextPath}/js/createSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/addressHandler.js"></script>
    </body>
</html> 