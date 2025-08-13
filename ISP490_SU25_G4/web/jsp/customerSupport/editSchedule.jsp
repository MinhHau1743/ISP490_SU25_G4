<%--
    Document   : editSchedule.jsp
    Created on : Jun 21, 2025
    Author     : NGUYEN MINH / Gemini (Refactored)
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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createSchedule.css"> <%-- Can reuse create CSS --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
    </head>
    <body>
        <div class="app-container">

            <jsp:include page="/mainMenu.jsp"/>

            <div class="main-panel">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Chỉnh sửa lịch bảo trì"/>
                </jsp:include>

                <main class="content-wrapper">
                    <section class="main-content-body">
                        <div class="page-header">
                            <h1>Chỉnh sửa Lịch bảo trì #${schedule.id}</h1>

                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/schedule?action=updateSchedule" method="post" class="form-container">
                            <input type="hidden" name="id" value="${schedule.id}">

                            <div class="form-grid">

                                <div class="form-group">
                                    <label for="title">Tiêu đề (*)</label>
                                    <input type="text" id="title" name="title" class="form-control" value="${schedule.title}" required>
                                </div>
                                <div class="form-group">
                                    <label for="statusId">Trạng thái</label>
                                    <select id="statusId" name="statusId" class="form-control" required>
                                        <option value="" disabled
                                                ${empty schedule.statusId ? 'selected="selected"' : ''}>
                                            -- Chọn trạng thái --
                                        </option>

                                        <!-- Lặp qua List<Status> đã setAttribute -->
                                        <c:forEach var="st" items="${statusList}">
                                            <option value="${st.id}"
                                                    ${st.id == schedule.statusId ? 'selected="selected"' : ''}>
                                                ${st.statusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="scheduledDate">Ngày bắt đầu (*)</label>
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
                                <div class="address-grid">
                                    <div class="form-group">
                                        <label for="province">Tỉnh/Thành phố (*)</label>
                                        <select id="province" name="province" class="form-control" required>
                                            <option value="">-- Chọn Tỉnh/Thành --</option>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" ${p.id == schedule.provinceId ? 'selected' : ''}>${p.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="district">Quận/Huyện (*)</label>
                                        <select id="district" name="district" class="form-control" required>
                                            <option value="">-- Chọn Quận/Huyện --</option>
                                            <c:forEach var="d" items="${districts}">
                                                <option value="${d.id}" ${d.id == schedule.districtId ? 'selected' : ''}>${d.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="ward">Phường/Xã (*)</label>
                                        <select id="ward" name="ward" class="form-control" required>
                                            <option value="">-- Chọn Phường/Xã --</option>
                                            <c:forEach var="w" items="${wards}">
                                                <option value="${w.id}" ${w.id == schedule.wardId ? 'selected' : ''}>${w.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="streetAddress">Địa chỉ cụ thể</label>
                                    <input type="text" id="streetAddress" name="streetAddress" class="form-control" value="${schedule.streetAddress}" placeholder="Số nhà, tên đường...">
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
                                    <input type="hidden" id="color" name="color"
                                           value="${param.color != null ? param.color : (not empty schedule.color ? schedule.color : '#007bff')}">

                                </div>

                                <div class="form-group form-group-full-width">
                                    <label for="notes">Ghi chú</label>
                                    <textarea id="notes" name="notes" class="form-control" rows="4" placeholder="Thêm mô tả chi tiết...">${schedule.notes}</textarea>
                                </div>
                            </div>

                            <div class="form-actions">
                                <a href="schedule" class="btn btn-secondary">Hủy bỏ</a>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </div>
                        </form>
                    </section>
                </main>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
        <script src="https://unpkg.com/feather-icons"></script>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
        <script>
            window.PRESELECTED_ADDRESS = {
                provinceId: '${param.province != null ? param.province : schedule.provinceId}',
                districtId: '${param.district != null ? param.district : schedule.districtId}',
                wardId: '${param.ward != null ? param.ward : schedule.wardId}'
            };
            window.ADDR_CONTEXT_PATH = '${pageContext.request.contextPath}';
            // Tất cả users có thể chọn
            // Tạo dữ liệu an toàn
            window.SCHEDULE_USERS = [];
            <c:forEach var="u" items="${assignments}">
            window.SCHEDULE_USERS.push({
                id: '${u.id}',
                name: '<c:out value="${u.fullName}"/>'
            });
            </c:forEach>

            window.ASSIGNED_USERS = [];
            <c:forEach var="entry" items="${assignedUserMap}">
            window.ASSIGNED_USERS.push({
                id: '${entry.key}',
                name: '<c:out value="${entry.value}"/>'
            });
            </c:forEach>
        </script>

        <script src="${pageContext.request.contextPath}/js/addressHandler.js"></script>
        <script src="${pageContext.request.contextPath}/js/editSchedule.js"></script> <%-- Can reuse create script --%>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>