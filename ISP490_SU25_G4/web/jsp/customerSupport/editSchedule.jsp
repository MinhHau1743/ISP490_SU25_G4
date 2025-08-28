<%--
Document   : editSchedule.jsp
Created on : Jun 21, 2025
Author     : Hai Huy
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
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
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
                            <input type="hidden" name="technicalRequestId" value="${schedule.technicalRequestId}">
                            <input type="hidden" name="campaignId" value="${schedule.campaignId}">

                            <div class="form-grid">

                                <div class="form-group ${not empty titleError ? 'has-error' : ''}">
                                    <label for="title">Tiêu đề (*)</label>
                                    <input type="text" id="title" name="title" class="form-control" 
                                           value="${not empty param_title ? param_title : schedule.title}" required>
                                    <c:if test="${not empty titleError}">
                                        <span class="error-message">${titleError}</span>
                                    </c:if>
                                </div>

                                <div class="form-group">
                                    <label for="statusId">Trạng thái</label>
                                    <select id="statusId" name="statusId" class="form-control" required>
                                        <option value="" disabled ${empty schedule.statusId ? 'selected="selected"' : ''}>
                                            -- Chọn trạng thái --
                                        </option>
                                        <c:forEach var="st" items="${statusList}">
                                            <option value="${st.id}" 
                                                    ${(not empty param_statusId ? param_statusId : st.id) == schedule.statusId ? 'selected="selected"' : ''}>
                                                ${st.statusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-group ${not empty scheduledDateError ? 'has-error' : ''}">
                                    <label for="scheduledDate">Ngày bắt đầu (*)</label>
                                    <input type="date" id="scheduledDate" name="scheduledDate" class="form-control" 
                                           value="${not empty param_scheduledDate ? param_scheduledDate : schedule.scheduledDate}" required>
                                    <c:if test="${not empty scheduledDateError}">
                                        <span class="error-message">${scheduledDateError}</span>
                                    </c:if>
                                </div>

                                <div class="form-group ${not empty endDateError ? 'has-error' : ''}">
                                    <label for="endDate">Ngày kết thúc</label>
                                    <input type="date" id="endDate" name="endDate" class="form-control" 
                                           value="${not empty param_endDate ? param_endDate : schedule.endDate}">
                                    <c:if test="${not empty endDateError}">
                                        <span class="error-message">${endDateError}</span>
                                    </c:if>
                                </div>

                                <div class="form-group ${not empty startTimeError ? 'has-error' : ''}">
                                    <label for="startTime">Giờ bắt đầu</label>
                                    <input type="time" id="startTime" name="startTime" class="form-control" 
                                           value="${not empty param_startTime ? param_startTime : schedule.startTime}">
                                    <c:if test="${not empty startTimeError}">
                                        <span class="error-message">${startTimeError}</span>
                                    </c:if>
                                </div>

                                <div class="form-group ${not empty endTimeError ? 'has-error' : ''}">
                                    <label for="endTime">Giờ kết thúc</label>
                                    <input type="time" id="endTime" name="endTime" class="form-control" 
                                           value="${not empty param_endTime ? param_endTime : schedule.endTime}">
                                    <c:if test="${not empty endTimeError}">
                                        <span class="error-message">${endTimeError}</span>
                                    </c:if>
                                </div>

                                <div class="address-grid">
                                    <div class="form-group ${not empty provinceError ? 'has-error' : ''}">
                                        <label for="province">Tỉnh/Thành phố (*)</label>
                                        <select id="province" name="province" class="form-control" required>
                                            <option value="">-- Chọn Tỉnh/Thành --</option>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" 
                                                        ${p.id == (not empty param_province ? param_province : schedule.address.provinceId) ? 'selected' : ''}>
                                                    ${p.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty provinceError}">
                                            <span class="error-message">${provinceError}</span>
                                        </c:if>
                                    </div>

                                    <div class="form-group ${not empty districtError ? 'has-error' : ''}">
                                        <label for="district">Quận/Huyện (*)</label>
                                        <select id="district" name="district" class="form-control" required>
                                            <option value="">-- Chọn Quận/Huyện --</option>
                                            <c:forEach var="d" items="${districts}">
                                                <option value="${d.id}" 
                                                        ${d.id == (not empty param_district ? param_district : schedule.address.districtId) ? 'selected' : ''}>
                                                    ${d.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty districtError}">
                                            <span class="error-message">${districtError}</span>
                                        </c:if>
                                    </div>

                                    <div class="form-group ${not empty wardError ? 'has-error' : ''}">
                                        <label for="ward">Phường/Xã (*)</label>
                                        <select id="ward" name="ward" class="form-control" required>
                                            <option value="">-- Chọn Phường/Xã --</option>
                                            <c:forEach var="w" items="${wards}">
                                                <option value="${w.id}" 
                                                        ${w.id == (not empty param_ward ? param_ward : schedule.address.wardId) ? 'selected' : ''}>
                                                    ${w.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                        <c:if test="${not empty wardError}">
                                            <span class="error-message">${wardError}</span>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="streetAddress">Địa chỉ cụ thể</label>
                                    <input type="text" id="streetAddress" name="streetAddress" class="form-control" 
                                           value="${not empty param_streetAddress ? param_streetAddress : schedule.address.streetAddress}" 
                                           placeholder="Số nhà, tên đường...">
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
                                        <span class="color-swatch" data-color="#e83e8c" style="background-color: #e83e8c;"></span>
                                        <span class="color-swatch" data-color="#6c757d" style="background-color: #6c757d;"></span> 
                                        <span class="color-swatch" data-color="#20c997" style="background-color: #20c997;"></span> 
                                        <span class="color-swatch" data-color="#4B0082" style="background-color: #4B0082;"></span> 
                                        <span class="color-swatch" data-color="#ADFF2F" style="background-color: #ADFF2F;"></span> 
                                        <span class="color-swatch" data-color="#A52A2A" style="background-color: #A52A2A;"></span> 
                                        <span class="color-swatch" data-color="#FFD700" style="background-color: #FFD700;"></span> 
                                        <span class="color-swatch" data-color="#87CEEB" style="background-color: #87CEEB;"></span>
                                    </div>
                                    <input type="hidden" id="color" name="color"
                                           value="${not empty param_color ? param_color : (not empty schedule.color ? schedule.color : '#007bff')}">
                                </div>

                                <div class="form-group form-group-full-width">
                                    <label for="notes">Ghi chú</label>
                                    <textarea id="notes" name="notes" class="form-control" rows="4" 
                                              placeholder="Thêm mô tả chi tiết...">${not empty param_notes ? param_notes : schedule.notes}</textarea>
                                    <c:if test="${not empty notesError}">
                                        <span class="error-message">${notesError}</span>
                                    </c:if>
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
                provinceId: '${not empty param_province ? param_province : schedule.address.provinceId}',
                districtId: '${not empty param_district ? param_district : schedule.address.districtId}',
                wardId: '${not empty param_ward ? param_ward : schedule.address.wardId}'
            };
            window.ADDR_CONTEXT_PATH = '${pageContext.request.contextPath}';

            // Tất cả users có thể chọn
            window.SCHEDULE_USERS = [];
            <c:forEach var="u" items="${assignments}">
            window.SCHEDULE_USERS.push({
                id: '${u.id}',
                name: '<c:out value="${u.fullName1}"/>'
            });
            </c:forEach>

            window.ASSIGNED_USERS = [];
            <c:forEach var="entry" items="${assignedUserMap}">
            window.ASSIGNED_USERS.push({
                id: '${entry.key}',
                name: '<c:out value="${entry.value}"/>'
            });
            </c:forEach>

            // Script để chọn màu sắc (không phải validation)
            $(document).ready(function () {
                $('.color-swatch').click(function () {
                    var color = $(this).data('color');
                    $('#color').val(color);
                    $('.color-swatch').css('opacity', '0.7');
                    $(this).css('opacity', '1');
                });

                // Thiết lập màu đã chọn ban đầu
                var currentColor = $('#color').val();
                $('.color-swatch[data-color="' + currentColor + '"]').css('opacity', '1');
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/addressHandler.js"></script>
        <script src="${pageContext.request.contextPath}/js/editSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>