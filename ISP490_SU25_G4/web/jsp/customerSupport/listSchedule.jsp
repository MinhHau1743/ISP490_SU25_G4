<%--
    Document   : listSchedule.jsp
    Created on : Jun 21, 2025
    Author     : NGUYEN MINH / Gemini
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="currentPage" value="dashboard" />
<c:set var="viewMode" value="${param.viewMode != null ? param.viewMode : 'day-view'}" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch bảo trì</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
        <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listSchedule.css">
    </head>
    <body>
        <div class="app-container">
            <%-- 1. Menu chính sẽ được cố định bên trái --%>
            <jsp:include page="/mainMenu.jsp"/>

            <%-- 2. Panel chính chứa Header và Nội dung chính --%>
            <div class="main-panel">
                <%-- Header nằm ở trên cùng của panel chính --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Lịch bảo trì"/>
                </jsp:include>
                <div class="content-wrapper">
                    <section class="main-content-body">
                        <div class="calendar-toolbar">

                            <div class="view-toggle">
                                <button id="view-day-btn" class="btn-toggle <c:if test="${viewMode == 'day-view'}">active</c:if>" data-view="day-view">Ngày</button>
                                <button id="view-week-btn" class="btn-toggle <c:if test="${viewMode == 'week-view'}">active</c:if>" data-view="week-view">Tuần</button>
                                <button id="view-month-btn" class="btn-toggle <c:if test="${viewMode == 'month-view'}">active</c:if>" data-view="month-view">Tháng</button>
                                <button id="view-list-btn" class="btn-toggle <c:if test="${viewMode == 'list-view'}">active</c:if>" data-view="list-view">Danh sách</button>
                                </div>
                                <!-- HIDDEN FORM TO SUBMIT NEXT/PREV DAY -->
                                <form id="dayNavForm" method="get" action="schedule">
                                    <input type="hidden" name="action" value="listSchedule">
                                    <input type="hidden" name="controllerDay" id="controllerDay">
                                    <input type="hidden" name="currentDay" id="currentDay">
                                    <input type="hidden" name="viewMode" id="viewMode">
                                </form>


                                <!-- WEEK NAVIGATION -->
                                <div class="week-nav">
                                    <button class="btn-nav" id="prevDayBtn"><i data-feather="chevron-left"></i></button>
                                    <span class="date-range" id="currentDate" data-date="${isoDayDate}">
                                    ${displayDate}
                                </span>
                                <button class="btn-nav" id="nextDayBtn"><i data-feather="chevron-right"></i></button>
                            </div>
                            <div class="toolbar-spacer"></div>
                        </div>
                        <div class="calendar-content">
                            <div class="calendar-left">
                                <div id="day-view" class="calendar-view <c:if test="${viewMode == 'day-view'}">active</c:if>">
                                        <div class="day-nav">
                                            <span class="date">${dayDate}</span>
                                    </div>
                                    <div class="day-header"><h3>${dayHeader}</h3></div>
                                    <div class="time-grid">
                                        <c:forEach var="label" items="${dayTimeLabels}" varStatus="status">
                                            <div class="time-label">${label}</div>
                                            <c:set var="startTime" value="${dayStartTimes[status.index]}"/>
                                            <div class="${startTime == '' ? 'time-slot all-day-slot' : 'time-slot'}" data-date="${isoDayDate}"
                                                 <c:if test="${startTime != ''}">data-start-time="${startTime}"</c:if>
                                                     ondragover="allowDrop(event)" ondrop="drop(event)">
                                                 <c:forEach var="schedule" items="${schedules}">
                                                     <c:set var="slotHHmm" value="${startTime != '' ? fn:substring(startTime,0,5) : ''}" />
                                                     <c:set var="schHHmm"  value="${schedule.startTime != null ? fn:substring(schedule.startTime.toString(),0,5) : ''}" />
                                                     <c:set var="slotMin" value="${slotHHmm != '' ? fn:substring(slotHHmm,3,5) : ''}" />
                                                     <c:set var="schMin"  value="${schHHmm  != '' ? fn:substring(schHHmm,3,5)  : ''}" />
                                                     <c:set var="sameHour" value="${slotHHmm != '' && fn:substring(schHHmm,0,2) == fn:substring(slotHHmm,0,2)}" />
                                                     <c:set var="inThisSlot"
                                                            value="${
                                                            sameHour and (
                                                                (slotMin == '00' and schMin lt  '30') or
                                                                (slotMin == '30' and schMin ge '30')
                                                                )
                                                            }" />
                                                     <c:if test="${ schedule.scheduledDate.toString() == isoDayDate
                                                                    && (slotHHmm == '' ? schedule.startTime == null : inThisSlot) }">
                                                           <div class="event ${startTime == '' ? 'all-day' : ''}"
                                                                id="event-${schedule.id}"
                                                                data-schedule-id="${schedule.id}"
                                                                data-start-time="${schedule.startTime}"
                                                                data-end-time="${schedule.endTime}"
                                                                draggable="true"
                                                                ondragstart="drag(event)"
                                                                onclick="showDetails(this)"
                                                                style="background-color:${schedule.color};">
                                                               <div class="event-title">
                                                                   <c:if test="${schedule.technicalRequestId != null}">
                                                                       <i class="bi bi-tools" style="color:#FFFFFF;" title="Yêu cầu kỹ thuật"></i>
                                                                   </c:if>
                                                                   <c:if test="${schedule.campaignId != null}">
                                                                       <i class="bi bi-flag-fill" style="color:#FFFFFF;" title="Lịch chiến dịch"></i>
                                                                   </c:if>
                                                                   ${schedule.title}
                                                               </div>
                                                               <span class="event-time">${schedule.startTime != null ? schHHmm : 'Cả ngày'}</span>
                                                           </div>
                                                     </c:if>
                                                 </c:forEach>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>


                                <div id="week-view" class="calendar-view <c:if test="${viewMode == 'week-view'}">active</c:if>">
                                        <div class="day-header-row">
                                            <div class="day-header-cell" style="width:63px; min-width:63px;"></div>
                                        <c:forEach var="label" items="${dayHeaders}" varStatus="status">
                                            <div class="day-header-cell">${label}</div>
                                        </c:forEach>
                                    </div>

                                    <div class="time-grid">
                                        <!-- Row ALL DAY: label + 7 day-cells (all-day-slot) -->
                                        <div class="time-label">Cả ngày</div>
                                        <c:forEach var="weekDate" items="${weekDates}" varStatus="ws">
                                            <div class="all-day-slot"
                                                 data-date="${weekDate}"
                                                 ondragover="allowDrop(event)" ondrop="drop(event)">
                                                <c:forEach var="schedule" items="${schedules}">
                                                    <c:if test="${schedule.startTime == null && schedule.scheduledDate.equals(weekDate)}">
                                                        <div class="event all-day" id="event-${schedule.id}"
                                                             style="background-color: ${schedule.color};"
                                                             data-schedule-id="${schedule.id}" draggable="true"
                                                             ondragstart="drag(event)" onclick="showDetails(this)">
                                                            <span class="event-time">Cả ngày</span>
                                                            <br>${schedule.title}
                                                            <div class="resize-handle"></div>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:forEach>

                                        <!-- Các dòng slot giờ (label + 7 ô/ngày) -->
                                        <c:forEach var="hour" items="${hours}" varStatus="status">
                                            <div class="time-label">${hourLabels[status.index]}</div>
                                            <c:forEach var="day" items="${days}" varStatus="ds">
                                                <div class="time-slot" 
                                                     data-start-time="${hour}"
                                                     data-date="${weekDates[ds.index]}"
                                                     ondragover="allowDrop(event)" ondrop="drop(event)">
                                                    <c:forEach var="schedule" items="${schedules}">
                                                        <c:set var="hourHHmm" value="${fn:substring(hour,0,5)}" />
                                                        <c:set var="schHHmm"  value="${schedule.startTime != null ? fn:substring(schedule.startTime.toString(),0,5) : ''}" />
                                                        <c:set var="slotMin"  value="${fn:substring(hourHHmm,3,5)}" />
                                                        <c:set var="schMin"   value="${schHHmm != '' ? fn:substring(schHHmm,3,5) : ''}" />
                                                        <c:set var="sameHour" value="${fn:substring(schHHmm,0,2) == fn:substring(hourHHmm,0,2)}" />
                                                        <c:set var="inThisSlot"
                                                               value="${
                                                               sameHour and (
                                                                   (slotMin == '00' and schMin lt  '30') or
                                                                   (slotMin == '30' and schMin ge '30')
                                                                   )
                                                               }" />

                                                        <c:if test="${ fn:substring(schedule.scheduledDate.toString(),0,10) == weekDates[ds.index]
                                                                       && schedule.startTime != null
                                                                       && inThisSlot }">
                                                              <div class="event"
                                                                   id="event-${schedule.id}"
                                                                   data-schedule-id="${schedule.id}"
                                                                   data-start-time="${schedule.startTime}"        
                                                                   data-end-time="${schedule.endTime}"
                                                                   draggable="true"
                                                                   ondragstart="drag(event)"
                                                                   onclick="showDetails(this)"
                                                                   style="background-color: ${schedule.color};">
                                                                  <span class="event-time">${hour.substring(0,5)}</span>
                                                                  <br>
                                                                  <c:if test="${schedule.technicalRequestId != null}">
                                                                      <i class="bi bi-tools" style="color:#FFFFFF;" title="Yêu cầu kỹ thuật"></i>
                                                                  </c:if>
                                                                  <c:if test="${schedule.campaignId != null}">
                                                                      <i class="bi bi-flag-fill" style="color:#FFFFFF;" title="Lịch chiến dịch"></i>
                                                                  </c:if>${schedule.title}
                                                              </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>
                                    <div style="height: 80px;"></div>
                                </div>

                                <div id="month-view" class="calendar-view">
                                    <div class="month-grid">
                                        <div class="month-grid-header">Thứ Hai</div><div class="month-grid-header">Thứ Ba</div><div class="month-grid-header">Thứ Tư</div><div class="month-grid-header">Thứ Năm</div><div class="month-grid-header">Thứ Sáu</div><div class="month-grid-header">Thứ Bảy</div><div class="month-grid-header">Chủ Nhật</div>
                                        <c:forEach var="dayNum" items="${dayNumbers}" varStatus="status">
                                            <div class="month-day ${isCurrentMonths[status.index] ? '' : 'other-month'}" data-date="${monthDates[status.index]}">
                                                <div class="day-number">${dayNum}</div>
                                                <div class="tasks-list">
                                                    <c:forEach var="schedule" items="${schedules}">
                                                        <c:if test="${schedule.scheduledDate.equals(monthDates[status.index])}">
                                                            <div class="task-item status-${fn:toLowerCase(schedule.statusName)}"
                                                                 data-task-id="${schedule.id}"
                                                                 data-schedule-id="${schedule.id}"
                                                                 data-item-name="${schedule.title}"
                                                                 data-scheduled-date="${schedule.scheduledDate}"
                                                                 data-end-date="${schedule.endDate}"
                                                                 title="${schedule.title}"
                                                                 onclick="showDetails(this)"
                                                                 draggable="true"
                                                                 ondragstart="drag(event)"
                                                                 style="background-color: ${schedule.color}; color: white;">
                                                                <c:if test="${schedule.technicalRequestId != null}">
                                                                    <i class="bi bi-tools" style="color:#FFFFFF;" title="Yêu cầu kỹ thuật"></i>
                                                                </c:if>
                                                                <c:if test="${schedule.campaignId != null}">
                                                                    <i class="bi bi-flag-fill" style="color:#FFFFFF;" title="Lịch chiến dịch"></i>
                                                                </c:if>
                                                                ${fn:substring(schedule.title, 0, 10)}...
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div id="list-view" class="calendar-view <c:if test="${viewMode == 'list-view'}">active</c:if>">    
                                        <div class="list-grid">
                                            <h2 class="view-title">Tất cả lịch bảo trì</h2>
                                            <p class="schedule-count">Có tổng <strong>${schedules.size()}</strong> lịch</p>

                                        <div class="grouped-schedule-list">
                                            <c:forEach var="dateGroup" items="${groupedSchedules}">

                                                <div class="date-group-header">
                                                    <h3 class="date-title">${dateGroup.key}</h3>
                                                </div>

                                                <div class="date-group-content">
                                                    <c:forEach var="schedule" items="${dateGroup.value}">
                                                        <div class="event-item" data-schedule-id="${schedule.id}" onclick="showDetails(this)">

                                                            <div class="event-time-col">
                                                                <c:if test="${schedule.startTime != null}">
                                                                    ${schedule.startTime}
                                                                </c:if>
                                                                <c:if test="${schedule.startTime == null}">
                                                                    Cả ngày
                                                                </c:if>
                                                            </div>

                                                            <div class="event-details-col">
                                                                <div class="event-color-dot" style="background-color: ${schedule.color};"></div>
                                                                <div class="event-info">
                                                                    <span class="event-title">${schedule.title}</span>
                                                                </div>
                                                            </div>

                                                        </div>
                                                    </c:forEach>
                                                </div>

                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="event-details" id="event-details-panel">
                                <div class="actions">
                                    <a href="#" id="complete-schedule-btn" onclick="openMarkAsCompleteModal(event)" title="Hoàn thành">
                                        <i class="bi bi-check2-circle"></i>
                                    </a>
                                    <%-- ADD THE VIEW BUTTON HERE --%>
                                    <a href="#" title="Xem chi tiết"><i class="bi bi-eye" aria-label="View Icon"></i></a>
                                    <a href="#" title="Sửa"><i class="bi bi-pencil" aria-label="Edit Icon"></i></a>
                                    <a href="#" onclick="closeDetails()" title="Đóng"><i class="bi bi-x-lg" aria-label="Close Icon"></i></a>
                                </div>
                                <div class="event-header">
                                    <span class="dot"></span>
                                    <span class="event-time-detail"></span>
                                    <span class="event-title"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-hash" aria-label="ID Icon"></i> ID: <span class="event-id"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-calendar" aria-label="Date Icon"></i> <span class="event-date"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-clock" aria-label="Time Icon"></i> <span class="event-time-range"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-geo-alt" aria-label="Location Icon"></i> <span class="event-location"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-pencil-square" aria-label="Notes Icon"></i> <span class="event-notes"></span>
                                </div>
                                <div class="event-info">
                                    <i class="bi bi-people" aria-label="Assignments Icon"></i> 
                                    <span class="event-assignments"></span>
                                </div>

                                <div class="event-info">
                                    <i class="bi bi-activity" aria-label="Status Icon"></i> 
                                    <span class="event-label">Trạng thái:</span>
                                    <span class="event-status badge px-2 py-1" style="font-size: 13px;"></span>
                                </div>
                            </div>

                            <%-- File: listSchedule.jsp --%>

                            <!-- Popup xác nhận hoàn thành -->
                            <div class="modal fade confirm-modal" id="markCompleteConfirmModal" tabindex="-1"
                                 role="dialog" aria-labelledby="markCompleteTitle" aria-hidden="true"
                                 data-backdrop="static" data-keyboard="false">
                                <div class="modal-dialog modal-dialog-centered modal-sm" role="document">
                                    <div class="modal-content shadow-lg border-0 rounded-lg">

                                        <div class="modal-body text-center pb-2 pt-4">
                                            <div class="modal-icon mb-3">
                                                <i class="bi bi-check2-circle" aria-hidden="true"></i>
                                            </div>
                                            <h5 class="modal-title mb-1 font-weight-bold" id="markCompleteTitle">
                                                Xác nhận hoàn thành
                                            </h5>
                                            <p class="text-muted mb-0">
                                                Bạn có chắc chắn muốn đánh dấu lịch trình này là
                                                <strong>Hoàn thành</strong> không?
                                            </p>
                                            <small class="text-muted d-block mt-1">
                                                Hành động này không thể hoàn tác trực tiếp từ giao diện.
                                            </small>
                                        </div>

                                        <div class="modal-footer border-0 pt-0 pb-4 px-4">
                                            <button type="button" class="btn btn-light" data-dismiss="modal">Hủy</button>
                                            <button type="button" class="btn btn-teal" id="confirmCompleteBtn">
                                                <span class="btn-label">Xác nhận</span>
                                                <span class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
                                            </button>
                                        </div>

                                    </div>
                                </div>
                            </div>

                    </section>
                </div>
            </div>
        </div>
        <script>

            const currentView = '${viewMode}';
            const isoDayDate = '${isoDayDate}';
            const weekDates = [<c:forEach items="${weekDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];
            const monthDates = [<c:forEach items="${monthDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];
            var contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            feather.replace();
            let draggingEvent = null;
            let lastSlot = null;
            document.addEventListener('DOMContentLoaded', () => {
            const weekNav = document.querySelector('.week-nav');
            // Lấy view mode từ nguồn chắc chắn đúng!
            var currentView = document.getElementById('pageViewMode')?.textContent
                    || localStorage.getItem('selectedView')
                    || "day-view"; // fallback

            if (weekNav) {
            if (currentView === 'list-view') {
            weekNav.classList.add('hidden');
            } else {
            weekNav.classList.remove('hidden');
            }
            }
            });
            const schedules = [
            <c:forEach var="schedule" items="${schedules}" varStatus="status">
            {
            id: ${schedule.id},
                    title: "${schedule.title}",
                    scheduledDate: "${schedule.scheduledDate}",
                    endDate: "${schedule.endDate != null ? schedule.endDate : ''}",
                    startTime: "${schedule.startTime != null ? schedule.startTime : ''}",
                    endTime: "${schedule.endTime != null ? schedule.endTime : ''}",
                    location: "${schedule.address.fullAddress != null ? fn:escapeXml(schedule.address.fullAddress) : 'Không xác định'}",
                    statusName: "${schedule.statusName}",
                    notes: "${schedule.notes}",
                    // THÊM PHẦN ASSIGNMENTS VÀO ĐÂY
                    assignments: [
                <c:forEach var="assignment" items="${schedule.assignments}" varStatus="assignStatus">
                    {
                    fullName: "${assignment.fullName}"
                    }<c:if test="${!assignStatus.last}">,</c:if>
                </c:forEach>
                    ]
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
            ];
            var contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            feather.replace();
// Gọi AJAX tới backend khi sự kiện drop hoàn tất
            function safeTime(val) {
            // Trả về null nếu rỗng/bất hợp lệ, chỉ nhận đúng định dạng 'HH:mm'
            if (typeof val !== 'string' || !val.trim()) return null;
            return /^([01]\d|2[0-3]):([0-5]\d)$/.test(val.trim()) ? val.trim() : null;
            }
// File: listSchedule.js hoặc trong thẻ <script>

            function updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime) {
            const url = contextPath + '/schedule?action=updateScheduleTime';
            // Chuẩn hóa lại startTime/endTime trước khi gửi đi:
            const payload = {
            id: parseInt(scheduleId, 10),
                    scheduledDate: newScheduledDate || null,
                    endDate: newEndDate || null,
                    startTime: safeTime(newStartTime),
                    endTime: safeTime(newEndTime)
            };
            console.log('Updating event:', payload);
            $.ajax({
            url: url,
                    type: 'POST',
                    contentType: 'application/json; charset=UTF-8',
                    dataType: 'json',
                    data: JSON.stringify(payload),
                    success: function (response) {
                    console.log('Update successful:', response);
                    showToast('Cập nhật lịch trình thành công!', 'success');
                    setTimeout(() => { location.reload(); }, 800);
                    },
                    error: function (xhr) {
                    console.error('Update failed:', {status: xhr.status, text: xhr.responseText});
                    let errorMessage = 'Có lỗi khi cập nhật lịch trình!';
                    // Hiện chi tiết lỗi trả về để debug
                    if (xhr.responseJSON && xhr.responseJSON.message) errorMessage = xhr.responseJSON.message;
                    else if (xhr.statusText) errorMessage += ' ' + xhr.statusText;
                    showToast(errorMessage, 'error');
                    }
            });
            }



            let isInteracting = false;
            let scrollSpeed = 0;
            let scrollContainer = null;
            let scrollInterval = null;
            function startAutoScroll() {
            scrollContainer = document.querySelector('.calendar-left');
            if (!scrollContainer)
                    return;
            if (!scrollInterval) {
            scrollInterval = setInterval(() => {
            if (scrollSpeed !== 0) {
            scrollContainer.scrollTop += scrollSpeed;
            }
            }, 20);
            }
            }



            // ========== IMPROVED AUTO-SCROLL FUNCTIONS ==========

// Khởi tạo auto-scroll với tốc độ nhanh hơn
            function startAutoScroll() {
            scrollContainer = document.querySelector('.calendar-left');
            if (!scrollContainer) return;
            if (!scrollInterval) {
            scrollInterval = setInterval(() => {
            if (scrollSpeed !== 0) {
            scrollContainer.scrollTop += scrollSpeed;
            }
            }, 10); // Giảm interval xuống 10ms để mượt hơn
            }
            }

// Cập nhật hướng cuộn dựa trên vị trí chuột
            function updateScrollDirection(ev) {
            if (!scrollContainer) return;
            lastMouseY = ev.clientY;
            const rect = scrollContainer.getBoundingClientRect();
            const edgeSize = 80; // Tăng vùng edge lên 80px
            const maxSpeed = 15; // Tốc độ cuộn tối đa
            const minSpeed = 3; // Tốc độ cuộn tối thiểu

            const distFromTop = ev.clientY - rect.top;
            const distFromBottom = rect.bottom - ev.clientY;
            // Kiểm tra nếu chuột ở gần edge trên
            if (distFromTop < edgeSize && distFromTop >= 0) {
            const intensity = 1 - (distFromTop / edgeSize);
            scrollSpeed = - Math.max(minSpeed, maxSpeed * intensity);
            }
            // Kiểm tra nếu chuột ở gần edge dưới
            else if (distFromBottom < edgeSize && distFromBottom >= 0) {
            const intensity = 1 - (distFromBottom / edgeSize);
            scrollSpeed = Math.max(minSpeed, maxSpeed * intensity);
            }
            // Kiểm tra nếu chuột ra ngoài container (trên hoặc dưới)
            else if (ev.clientY < rect.top) {
            // Chuột ở trên container
            const distanceAbove = rect.top - ev.clientY;
            const intensity = Math.min(1, distanceAbove / 100); // Tăng tốc độ khi ra xa hơn
            scrollSpeed = - Math.max(minSpeed, maxSpeed * intensity);
            }
            else if (ev.clientY > rect.bottom) {
            // Chuột ở dưới container
            const distanceBelow = ev.clientY - rect.bottom;
            const intensity = Math.min(1, distanceBelow / 100); // Tăng tốc độ khi ra xa hơn
            scrollSpeed = Math.max(minSpeed, maxSpeed * intensity);
            }
            else {
            // Chuột ở giữa, không cần cuộn
            scrollSpeed = 0;
            }
            }

// Dừng auto-scroll và làm sạch event listeners
            function stopAutoScroll() {
            scrollSpeed = 0;
            if (scrollInterval) {
            clearInterval(scrollInterval);
            scrollInterval = null;
            }
            document.removeEventListener('dragover', updateScrollDirection);
            document.removeEventListener('dragend', stopAutoScroll);
            document.removeEventListener('drop', stopAutoScroll);
            }


            function parseTime(timeStr) {
            if (!timeStr)
                    return 0;
            const [h, m] = timeStr.split(':').map(Number);
            return h * 60 + m;
            }

            function formatTime(minutes) {
            const h = Math.floor(minutes / 60) % 24;
            const m = minutes % 60;
            return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
                }

                function addDays(dateStr, days) {
                const date = new Date(dateStr);
                date.setDate(date.getDate() + days);
                return date.toISOString().split('T')[0];
                }


// ========== DRAG AND DROP FUNCTIONS ==========

// Initialize drag and drop events
                document.querySelectorAll('.event').forEach(ev => {
                ev.addEventListener('dragstart', function (e) {
                draggingEvent = ev;
                setTimeout(() => {
                ev.style.opacity = '0.5';
                }, 0);
                });
                ev.addEventListener('dragend', function (e) {
                draggingEvent = null;
                if (lastSlot) lastSlot.classList.remove('slot-active');
                ev.style.opacity = '1';
                lastSlot = null;
                // Clean up is-other-event class
                document.querySelectorAll('.event').forEach(event => {
                event.classList.remove('is-other-event');
                });
                setTimeout(() => isInteracting = false, 0);
                stopAutoScroll();
                });
                });
                function bindSlotDnD(slot) {
                slot.addEventListener('dragover', function (e) {
                e.preventDefault();
                if (!draggingEvent) return;
                // Dọn các bản sao cùng id (chừa element đang kéo)
                let allDups = document.querySelectorAll('.event[id="' + draggingEvent.id + '"]');
                allDups.forEach(ev => {
                if (ev !== draggingEvent) {
                ev.parentElement && ev.parentElement.removeChild(ev);
                }
                });
                // Move vào slot hiện tại
                if (draggingEvent.parentElement !== slot) {
                slot.appendChild(draggingEvent);
                }

                // Highlight slot
                if (lastSlot && lastSlot !== slot) lastSlot.classList.remove('slot-active');
                slot.classList.add('slot-active');
                lastSlot = slot;
                // Update scroll direction for auto-scroll
                if (isInteracting) {
                updateScrollDirection(e);
                }
                });
                slot.addEventListener('dragleave', function () {
                if (lastSlot === slot) {
                slot.classList.remove('slot-active');
                lastSlot = null;
                }
                });
                slot.addEventListener('drop', function (e) {
                e.preventDefault();
                if (!draggingEvent) return;
                slot.classList.remove('slot-active');
                draggingEvent.style.opacity = '1';
                draggingEvent = null;
                lastSlot = null;
                // Stop auto-scroll immediately
                stopAutoScroll();
                // Call the main drop function
                drop(e);
                });
                }

                document.querySelectorAll('.time-slot, .all-day-slot').forEach(bindSlotDnD);
// Không cho kéo khi hoàn thành, cả hàm trong file js nữa
                function setScheduleCompletedUI(scheduleId) {
                const sid = String(scheduleId);
                // Cập nhật model cục bộ
                const sch = schedules.find(s => String(s.id) === sid);
                if (sch) {
                sch.statusName = 'Hoàn thành';
                sch.statusId = 3; // tùy hệ thống của bạn
                }

                // Day/Week view: các .event
                document.querySelectorAll(`#event-${sid}, .event[data-schedule-id="${sid}"]`)
                        .forEach(el => {
                        el.classList.add('is-completed');
                        el.setAttribute('draggable', 'false'); // ngừng kéo
                        });
                // Month/List view: các item theo data-schedule-id
                document.querySelectorAll(`.task-item[data-schedule-id="${sid}"], .event-item[data-schedule-id="${sid}"]`)
                        .forEach(el => el.classList.add('is-completed'));
                // Panel chi tiết (nếu đang mở)
                const panel = document.getElementById('event-details-panel');
                if (panel && panel.classList.contains('show')) {
                const openedId = panel.querySelector('.event-id')?.textContent?.trim();
                if (openedId === sid) {
                const statusSpan = panel.querySelector('.event-status');
                if (statusSpan) {
                statusSpan.textContent = 'Hoàn thành';
                statusSpan.className = 'event-status badge px-2 py-1 badge-completed';
                }
                }
                }
                }

                function drag(ev) {
                if (ev.target.classList.contains('is-completed')) {
                ev.preventDefault();
                showToast && showToast('Không thể kéo lịch đã hoàn thành', 'warning');
                return;
                }
                ev.dataTransfer.setData("text/plain", ev.target.id);
                ev.dataTransfer.effectAllowed = "move";
                isInteracting = true;
                // Start auto-scroll
                startAutoScroll();
                // Listen for mouse movement to update scroll speed
                document.addEventListener('dragover', updateScrollDirection);
                document.addEventListener('dragend', stopAutoScroll);
                document.addEventListener('drop', stopAutoScroll);
                // Disable other events to avoid conflicts
                const draggedId = ev.target.id;
                document.querySelectorAll('.event').forEach(event => {
                if (event.id !== draggedId) {
                event.classList.add('is-other-event');
                }
                });
                }

// Improved allowDrop function
                function allowDrop(ev) {
                ev.preventDefault();
                if (isInteracting) {
                updateScrollDirection(ev);
                }
                }

                document.addEventListener('DOMContentLoaded', function () {
                document.querySelectorAll('#week-view .event, #day-view .event').forEach(function (eventDiv) {
                const st = eventDiv.dataset.startTime || null;
                const et = eventDiv.dataset.endTime || null;
                applyEventHeight(eventDiv, st, et);
                });
                });
                function applyEventHeight(el, startTime, endTime) {
                if (startTime) {
                const h = getEventHeight(startTime, endTime, 30, 40, 40); // slot=30', cao=40px
                el.style.setProperty('height', h + 'px', 'important');
                } else {
                // all-day: bỏ height inline để CSS .event.all-day chi phối
                el.style.removeProperty('height');
                }
                }

// ====================================================================
                /**
                 * Tính chiều cao event dựa trên thời lượng.
                 * Nếu thiếu startTime hoặc endTime thì trả về chiều cao mặc định + 40px.
                 * Luôn cộng thêm 40px vào chiều cao event.
                 */
                function getEventHeight(startTime, endTime, slotMinutes = 30, slotHeight = 40, extraPx = 40) {
                if (!startTime || !endTime) return slotHeight + extraPx;
                const [sh, sm] = startTime.split(':').map(Number);
                const [eh, em] = endTime.split(':').map(Number);
                let totalMinutes = (eh * 60 + em) - (sh * 60 + sm);
                if (totalMinutes <= 0) totalMinutes += 24 * 60; // hỗ trợ event qua đêm
                const slotCount = totalMinutes / slotMinutes;
                return Math.max(slotHeight, slotCount * slotHeight) + extraPx;
                }

                document.addEventListener('DOMContentLoaded', function () {
                document.querySelectorAll('#week-view .event, #day-view .event').forEach(function(eventDiv) {
                if (eventDiv.classList.contains('all-day')) return;
                const startTime = eventDiv.dataset.startTime;
                const endTime = eventDiv.dataset.endTime;
                // Luôn cộng thêm 40px cho mỗi event
                const height = getEventHeight(startTime, endTime, 30, 40, 40);
                eventDiv.style.setProperty('height', height + 'px', 'important');
                });
                });
// Main drop function with auto-scroll cleanup
                function drop(ev) {
                ev.preventDefault();
                stopAutoScroll();
                // helper nội bộ: set height theo giờ hoặc bỏ height khi all-day
                function applyEventHeight(el, startTime, endTime) {
                if (startTime) {
                // slot=30', slotHeight=40px, extra=40px (đúng như code trước đó của bạn)
                const h = getEventHeight(startTime, endTime, 30, 40, 40);
                el.style.setProperty('height', h + 'px', 'important');
                } else {
                el.style.removeProperty('height'); // all-day để CSS tự co giãn
                }
                }

                const data = ev.dataTransfer.getData("text");
                let eventElement = document.getElementById(data);
                if (!eventElement) return;
                // Dọn class "is-other-event"
                document.querySelectorAll('.event').forEach(e => e.classList.remove('is-other-event'));
                // Xoá các bản sao dư cùng id (chừa element đang kéo)
                document.querySelectorAll('#' + CSS.escape(data)).forEach((el) => {
                if (el !== eventElement && el.parentNode) el.parentNode.removeChild(el);
                });
                // Xác định slot mục tiêu
                let slot = ev.target.closest('.time-slot, .all-day-slot, .all-day-event-container, .month-day');
                if (!slot) return;
                // Chuyển DOM sang slot mới
                if (eventElement.parentNode) eventElement.parentNode.removeChild(eventElement);
                slot.appendChild(eventElement);
                // Nếu trước đó có slot-active, bỏ đi
                if (typeof lastSlot !== 'undefined' && lastSlot) {
                lastSlot.classList.remove('slot-active');
                lastSlot = null;
                }

                const scheduleId = eventElement.id.split('-')[1];
                const scheduleToUpdate = schedules.find(s => s.id == scheduleId);
                if (!scheduleToUpdate) return;
                let newScheduledDate = null, newEndDate = null, newStartTime = null, newEndTime = null;
                // --- Tính ngày / giờ mới dựa trên loại slot ---
                const view = slot.closest('.calendar-view') ? slot.closest('.calendar-view').id : null;
                if (slot.classList.contains('all-day-event-container') && view === 'week-view') {
                // All-day hàng 7 cột trong week-view
                const rect = slot.getBoundingClientRect();
                const dayWidth = rect.width / 7;
                const x = ev.clientX - rect.left;
                let startCol = Math.floor(x / dayWidth) + 1;
                startCol = Math.max(1, Math.min(startCol, 7));
                eventElement.style.gridColumn = `${startCol} / span 1`; // cố định vào cột tương ứng

                newScheduledDate = weekDates[startCol - 1];
                newStartTime = null;
                // chuyển sang all-day
                eventElement.classList.add('all-day');
                eventElement.dataset.startTime = '';
                eventElement.dataset.endTime = '';
                // nếu có grid-column khi từ all-day sang time-slot rồi quay lại: giữ hợp lệ
                applyEventHeight(eventElement, null, null);
                const t = eventElement.querySelector('.event-time');
                if (t) t.textContent = 'Cả ngày';
                } else if (slot.classList.contains('all-day-slot') || slot.classList.contains('month-day')) {
                // All-day trong day/week view (all-day-slot) hoặc month view (month-day)
                newScheduledDate = slot.dataset.date;
                newStartTime = null;
                eventElement.classList.add('all-day');
                eventElement.dataset.startTime = '';
                eventElement.dataset.endTime = '';
                // rời khỏi time-slot => bỏ grid-column nếu có
                eventElement.style.removeProperty('grid-column');
                applyEventHeight(eventElement, null, null);
                const t = eventElement.querySelector('.event-time');
                if (t) t.textContent = 'Cả ngày';
                } else if (slot.classList.contains('time-slot')) {
                // Slot theo giờ
                newScheduledDate = slot.dataset.date;
                newStartTime = slot.dataset.startTime || null;
                eventElement.classList.remove('all-day');
                // rời all-day => bỏ grid-column nếu còn
                eventElement.style.removeProperty('grid-column');
                // tạm set startTime; endTime sẽ tính bên dưới
                eventElement.dataset.startTime = newStartTime || '';
                } else {
                // Fallback: có date nhưng không xác định, xem như all-day
                newScheduledDate = slot.dataset.date || newScheduledDate;
                newStartTime = null;
                eventElement.classList.add('all-day');
                eventElement.dataset.startTime = '';
                eventElement.dataset.endTime = '';
                eventElement.style.removeProperty('grid-column');
                applyEventHeight(eventElement, null, null);
                const t = eventElement.querySelector('.event-time');
                if (t) t.textContent = 'Cả ngày';
                }

                // --- Tính toán newEndDate dựa trên khoảng thời gian ngày cũ ---
                if (scheduleToUpdate.scheduledDate && scheduleToUpdate.endDate) {
                const oldStartDate = new Date(scheduleToUpdate.scheduledDate);
                const oldEndDate = new Date(scheduleToUpdate.endDate);
                const dayDifference = Math.round((oldEndDate - oldStartDate) / (24 * 60 * 60 * 1000));
                if (dayDifference > 0) {
                const newStartDate = new Date(newScheduledDate);
                newStartDate.setDate(newStartDate.getDate() + dayDifference);
                newEndDate = newStartDate.toISOString().split('T')[0];
                } else {
                newEndDate = newScheduledDate; // event 1 ngày
                }
                } else {
                newEndDate = newScheduledDate; // không có endDate cũ => coi là 1 ngày
                }

                // --- Cập nhật UI / model ---
                if (newStartTime) {
                // Có giờ: tính lại duration
                let durationMinutes = 0;
                if (scheduleToUpdate.startTime && scheduleToUpdate.endTime) {
                const [sh, sm] = scheduleToUpdate.startTime.split(':').map(Number);
                const [eh, em] = scheduleToUpdate.endTime.split(':').map(Number);
                durationMinutes = (eh * 60 + em) - (sh * 60 + sm);
                if (durationMinutes <= 0) durationMinutes += 24 * 60; // hỗ trợ qua đêm
                } else {
                durationMinutes = 30; // fallback 30'
                }

                let [nh, nm] = newStartTime.split(':').map(Number);
                let newEndTotalMin = nh * 60 + nm + durationMinutes;
                let newEndH = Math.floor(newEndTotalMin / 60) % 24;
                let newEndM = newEndTotalMin % 60;
                newEndTime = String(newEndH).padStart(2, '0') + ":" + String(newEndM).padStart(2, '0');
                // Cập nhật text hiển thị
                const eventTimeElement = eventElement.querySelector('.event-time');
                if (eventTimeElement) eventTimeElement.textContent = newStartTime + " - " + newEndTime;
                // Cập nhật dataset + height
                eventElement.dataset.startTime = newStartTime || '';
                eventElement.dataset.endTime = newEndTime || '';
                applyEventHeight(eventElement, newStartTime, newEndTime);
                // Cập nhật model cục bộ
                scheduleToUpdate.scheduledDate = newScheduledDate;
                scheduleToUpdate.startTime = newStartTime;
                scheduleToUpdate.endTime = newEndTime;
                scheduleToUpdate.endDate = newEndDate;
                scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');
                // Gọi AJAX
                if (typeof updateEvent === "function" && scheduleId && newScheduledDate) {
                updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime);
                }
                } else {
                // All-day
                const eventTimeElement = eventElement.querySelector('.event-time');
                if (eventTimeElement) eventTimeElement.textContent = 'Cả ngày';
                // Dataset + height
                eventElement.dataset.startTime = '';
                eventElement.dataset.endTime = '';
                applyEventHeight(eventElement, null, null);
                // Model cục bộ
                scheduleToUpdate.scheduledDate = newScheduledDate;
                scheduleToUpdate.startTime = null;
                scheduleToUpdate.endTime = null;
                scheduleToUpdate.endDate = newEndDate;
                scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');
                // AJAX (giờ = null)
                if (typeof updateEvent === "function" && scheduleId && newScheduledDate) {
                updateEvent(scheduleId, newScheduledDate, newEndDate, null, null);
                }
                }

                // Làm mới panel chi tiết nếu đang mở đúng event
                const detailsPanel = document.getElementById('event-details-panel');
                if (detailsPanel && detailsPanel.classList.contains('show') &&
                        detailsPanel.querySelector('.event-id').textContent == scheduleId) {
                const updatedSchedule = schedules.find(s => s.id == scheduleId);
                if (updatedSchedule) showDetails(eventElement, updatedSchedule);
                }
                }


// Chạy khi DOM load xong:
                document.addEventListener('DOMContentLoaded', function () {
                });
                function initResize(e) {
                e.preventDefault();
                isInteracting = true;
                const eventElement = e.target.parentElement;
                const container = eventElement.parentElement;
                const rect = container.getBoundingClientRect();
                const numDays = 7; // Số cột
                const dayWidth = rect.width / numDays;
                let startCol = parseInt(eventElement.dataset.startCol) || 1;
                let currentSpan = parseInt(eventElement.dataset.span) || 1;
                function resize(e) {
                const x = e.clientX - rect.left;
                let newEndCol = Math.ceil(x / dayWidth) + 1;
                let newSpan = newEndCol - startCol;
                if (newSpan < 1) newSpan = 1;
                newEndCol = startCol + newSpan;
                if (newEndCol > numDays + 1) newEndCol = numDays + 1;
                eventElement.style.gridColumn = startCol + ' / ' + newEndCol;
                eventElement.dataset.span = newEndCol - startCol;
                }

                function stopResize() {
                window.removeEventListener('mousemove', resize);
                window.removeEventListener('mouseup', stopResize);
                setTimeout(() => isInteracting = false, 0);
                // Cập nhật data backend sau resize
                const scheduleId = eventElement.id.split('-')[1];
                const newStartCol = parseInt(eventElement.dataset.startCol);
                const newSpan = parseInt(eventElement.dataset.span);
                const newScheduledDate = weekDates[newStartCol - 1];
                const newEndDate = (newSpan > 1) ? weekDates[newStartCol + newSpan - 2] : null;
                updateEvent(scheduleId, newScheduledDate, newEndDate, null, null); // all-day (giờ = null)
                }

                window.addEventListener('mousemove', resize);
                window.addEventListener('mouseup', stopResize);
                }


                function showDetails(element) {
                if (isInteracting)
                        return;
                const detailsPanel = document.getElementById('event-details-panel');
                if (!detailsPanel)
                        return;
                let scheduleId = element.dataset.scheduleId || element.id?.split('-')[1];
                if (!scheduleId)
                        return;
                const schedule = schedules.find(s => s.id == scheduleId);
                if (schedule) {
                // Cập nhật các thông tin chi tiết của lịch trình
                detailsPanel.querySelector('.event-id').textContent = schedule.id;
                detailsPanel.querySelector('.event-title').textContent = schedule.title;
                detailsPanel.querySelector('.event-time-detail').textContent = schedule.startTime ? schedule.startTime : 'Cả ngày';
                detailsPanel.querySelector('.event-date').textContent = schedule.scheduledDate + (schedule.endDate ? ' - ' + schedule.endDate : '');
                detailsPanel.querySelector('.event-time-range').textContent = schedule.startTime ? schedule.startTime + (schedule.endTime ? ' - ' + schedule.endTime : '') : 'Cả ngày';
                detailsPanel.querySelector('.event-location').textContent = schedule.location || 'Không xác định';
                detailsPanel.querySelector('.event-notes').textContent = schedule.notes || 'Không có ghi chú';
                // --- BẮT ĐẦU PHẦN CẬP NHẬT ĐỂ HIỂN THỊ THẺ NHÂN VIÊN ---

                const assignmentsContainer = detailsPanel.querySelector('.event-assignments');
                assignmentsContainer.innerHTML = ''; // Bắt đầu bằng việc xóa sạch nội dung cũ
                const statusSpan = detailsPanel.querySelector('.event-status');
                if (statusSpan) {
                // 1) Chuẩn hoá về tiếng Việt
                const fromName = (schedule.statusName || '').trim();
                const mapEnToVi = {
                'Upcoming': 'Sắp tới',
                        'In Progress': 'Đang thực hiện',
                        'Completed': 'Hoàn thành',
                        'Overdue': 'Quá hạn',
                        'Cancelled': 'Đã hủy'
                };
                const mapIdToVi = { 1: 'Sắp tới', 2: 'Đang thực hiện', 3: 'Hoàn thành', 4: 'Quá hạn', 5: 'Đã hủy' };
                const statusVi =
                        fromName
                        ? (mapEnToVi[fromName] || fromName)                       // nếu có statusName -> đổi EN->VI
                        : (mapIdToVi[schedule.statusId] || 'Sắp tới'); // fallback theo ID

                // 2) Gắn class badge theo trạng thái (đổi màu tại đây)
                const classByStatus = {
                'Sắp tới': 'badge-upcoming',
                        'Đang thực hiện': 'badge-inprogress',
                        'Hoàn thành': 'badge-completed',
                        'Quá hạn': 'badge-cancelled', // hoặc tạo riêng badge-overdue nếu muốn
                        'Đã hủy': 'badge-cancelled'
                };
                statusSpan.textContent = statusVi;
                statusSpan.className = 'event-status badge px-2 py-1 ' + (classByStatus[statusVi] || 'badge-upcoming');
                }

                // Hàm trợ giúp để lấy 2 chữ cái đầu của tên
                function getInitials(name) {
                if (!name || typeof name !== 'string' || name.trim() === '') {
                return '?'; // Trả về '?' nếu tên không hợp lệ
                }
                const parts = name.trim().split(' ').filter(p => p); // Tách tên và loại bỏ các khoảng trắng thừa
                if (parts.length > 1) {
                // Lấy chữ cái đầu của từ đầu tiên và từ cuối cùng
                return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
                } else if (parts.length === 1) {
                // Lấy 2 chữ cái đầu nếu chỉ có một từ
                return parts[0].substring(0, 2).toUpperCase();
                }
                return '?';
                }

                if (schedule.assignments && schedule.assignments.length > 0) {
                // Lặp qua danh sách nhân viên được phân công
                schedule.assignments.forEach(assignment => {
                const fullName = assignment.fullName || 'Chưa xác định';
                // 1. Tạo thẻ chứa chính (div.employee-tag)
                const tag = document.createElement('div');
                tag.className = 'employee-tag';
                // 2. Tạo avatar (span.avatar)
                const avatar = document.createElement('span');
                avatar.className = 'avatar';
                avatar.textContent = getInitials(fullName); // Dùng hàm để lấy chữ cái đầu

                // 3. Tạo tên nhân viên (span.employee-name)
                const nameSpan = document.createElement('span');
                nameSpan.className = 'employee-name';
                nameSpan.textContent = fullName;
                // 4. Gắn avatar và tên vào trong thẻ tag
                tag.appendChild(avatar);
                tag.appendChild(nameSpan);
                // 5. Gắn thẻ tag hoàn chỉnh vào vùng chứa
                assignmentsContainer.appendChild(tag);
                });
                } else {
                // Nếu không có ai được phân công, hiển thị thông báo
                assignmentsContainer.innerHTML = '<span style="font-style: italic; color: #888;">Không có phân công</span>';
                }

                // --- KẾT THÚC PHẦN CẬP NHẬT ---

                // Cập nhật link cho nút Sửa
                const viewBtn = detailsPanel.querySelector('a[title="Xem chi tiết"]');
                viewBtn.href = contextPath + '/schedule?action=viewScheduleDetail&id=' + encodeURIComponent(schedule.id);
                detailsPanel.classList.add('show');
                const editBtn = detailsPanel.querySelector('a[title="Sửa"]');
                editBtn.href = contextPath + '/schedule?action=updateSchedule&id=' + encodeURIComponent(schedule.id);
                detailsPanel.classList.add('show');
                }
                }


                function closeDetails() {
                const detailsPanel = document.getElementById('event-details-panel');
                if (detailsPanel)
                        detailsPanel.classList.remove('show');
                }

                // View toggle và lưu localStorage
                document.querySelectorAll('.btn-toggle').forEach(button => {
                button.addEventListener('click', () => {
                const viewId = button.getAttribute('data-view');
                document.querySelectorAll('.calendar-view').forEach(view => view.classList.remove('active'));
                document.getElementById(viewId).classList.add('active');
                document.querySelectorAll('.btn-toggle').forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');
                localStorage.setItem('selectedView', viewId);
                const form = document.getElementById("dayNavForm");
                const viewModeInput = document.getElementById("viewMode");
                const currentDayInput = document.getElementById("currentDay");
                currentDayInput.value = document.getElementById("currentDate").getAttribute("data-date");
                viewModeInput.value = viewId;
                form.submit();
                });
                });
                // Init view từ localStorage
                document.addEventListener('DOMContentLoaded', () => {
                const savedView = localStorage.getItem('selectedView') || 'day-view';
                document.querySelectorAll('.calendar-view').forEach(view => {
                view.classList.remove('active');
                });
                const selectedViewElement = document.getElementById(savedView);
                if (selectedViewElement)
                        selectedViewElement.classList.add('active');
                document.querySelectorAll('.btn-toggle').forEach(button => {
                button.classList.remove('active');
                if (button.getAttribute('data-view') === savedView) {
                button.classList.add('active');
                }
                });
                });
                // Init các event khác
                document.addEventListener('DOMContentLoaded', () => {
                document.querySelectorAll('#week-view .event.all-day').forEach(event => {
                if (!event.querySelector('.resize-handle')) {
                const handle = document.createElement('div');
                handle.classList.add('resize-handle');
                event.appendChild(handle);
                handle.addEventListener('mousedown', initResize);
                handle.addEventListener('click', (e) => e.stopPropagation());
                }
                if (!event.dataset.startCol) {
                const col = event.style.gridColumn || '1 / 2';
                const [start, end] = col.split('/').map(s => parseInt(s.trim()));
                event.dataset.startCol = start;
                event.dataset.span = end - start;
                }
                });
                document.addEventListener('dragend', (e) => {
                if (e.target.classList.contains('event')) {
                setTimeout(() => isInteracting = false, 0);
                }
                });
                document.querySelectorAll('#month-view .task-item').forEach(item => {
                item.id = item.dataset.taskId ? 'task-' + item.dataset.taskId : 'task-' + Math.random().toString(36).substring(7);
                item.draggable = true;
                item.addEventListener('dragstart', drag);
                item.addEventListener('dragend', (e) => {
                setTimeout(() => isInteracting = false, 0);
                });
                });
                document.querySelectorAll('#month-view .month-day').forEach(day => {
                day.addEventListener('dragover', allowDrop);
                day.addEventListener('drop', drop);
                });
                // Lấy ngày hiện tại và highlight
                const today = new Date().toISOString().split('T')[0];
                // Highlight ngày hiện tại trong week-view
                const weekHeaders = document.querySelectorAll('#week-view .day-header-cell:not(:first-child)');
                weekHeaders.forEach((header, index) => {
                if (weekDates[index] === today) {
                header.classList.add('today-highlight');
                }
                });
                // Highlight ngày hiện tại trong month-view với hình tròn quanh số
                const monthDays = document.querySelectorAll('#month-view .month-day');
                monthDays.forEach((day) => {
                if (day.dataset.date === today) {
                day.classList.add('today-highlight');
                }
                });
                });
                // Navigation (prev/next) với xử lý viewMode
                document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("dayNavForm");
                const controllerDayInput = document.getElementById("controllerDay");
                const currentDayInput = document.getElementById("currentDay");
                const viewModeInput = document.getElementById("viewMode");
                const currentDateElem = document.getElementById("currentDate");
                const currentFullDate = currentDateElem.getAttribute("data-date");
                let currentViewMode = localStorage.getItem("selectedView") || "day-view";
                function calculateNewDate(baseDateStr, offsetDays) {
                const date = new Date(baseDateStr);
                date.setDate(date.getDate() + offsetDays);
                return date.toISOString().split("T")[0];
                }

                function handleNav(direction) {
                let newDate = currentFullDate;
                controllerDayInput.value = "";
                if (currentViewMode === "week-view") {
                newDate = calculateNewDate(currentFullDate, direction === "prev" ? - 7 : 7);
                } else {
                controllerDayInput.value = direction;
                }

                currentDayInput.value = newDate;
                viewModeInput.value = currentViewMode;
                localStorage.setItem("selectedView", currentViewMode);
                form.submit();
                }

                document.getElementById("prevDayBtn").addEventListener("click", function () {
                handleNav("prev");
                });
                document.getElementById("nextDayBtn").addEventListener("click", function () {
                handleNav("next");
                });
                });
                document.addEventListener('DOMContentLoaded', function() {
                // Tìm đến phần tử hiển thị ngày
                const dateDisplay = document.getElementById('currentDate');
                // Nếu phần tử tồn tại, khởi tạo Flatpickr
                if (dateDisplay) {
                flatpickr(dateDisplay, {
                // Tùy chọn cho bộ chọn ngày
                dateFormat: "Y-m-d", // Định dạng ngày mà Flatpickr sẽ trả về (năm-tháng-ngày)
                        defaultDate: dateDisplay.dataset.date, // Lấy ngày mặc định từ thuộc tính data-date

                        // Hàm sẽ chạy KHI người dùng chọn một ngày mới
                        onChange: function(selectedDates, dateStr, instance) {
                        // selectedDates: mảng các đối tượng Date được chọn
                        // dateStr: ngày được chọn dưới dạng chuỗi (theo dateFormat ở trên)

                        console.log('Ngày được chọn:', dateStr);
                        // 1. Cập nhật giá trị cho các input ẩn trong form
                        document.getElementById('controllerDay').value = dateStr;
                        document.getElementById('currentDay').value = dateStr;
                        // 2. Tự động submit form để tải lại trang với ngày mới
                        document.getElementById('dayNavForm').submit();
                        }
                });
                }
                });
                /* Helpers */
                function toDate(d){ return new Date((d || '') + 'T00:00:00'); }
                function dstr(d){ return d.toISOString().split('T')[0]; }
                function clamp(d, lo, hi){ return d < lo ? lo : (d > hi ? hi : d); }

                /* Lấy DOM tuần theo lưới month-view (mỗi 7 ô = 1 tuần) */
                function getMonthWeeksFromDom(){
                const cells = Array.from(document.querySelectorAll('#month-view .month-day'));
                const weeks = [];
                for (let i = 0; i < cells.length; i += 7) weeks.push(cells.slice(i, i + 7));
                return weeks;
                }

                document.addEventListener('DOMContentLoaded', function () {
                function getDayDiff(start, end) {
                if (!start) return 1;
                if (!end) end = start;
                const s = new Date(start);
                const e = new Date(end);
                if (Number.isNaN(s.getTime()) || Number.isNaN(e.getTime())) return 1;
                // chuẩn hóa về 00:00 để tránh lệch múi giờ
                s.setHours(0, 0, 0, 0);
                e.setHours(0, 0, 0, 0);
                return Math.floor((e - s) / (1000 * 60 * 60 * 24)) + 1; // inclusive
                }

                document.querySelectorAll('#month-view .task-item').forEach(function(item, index){
                const start = item.getAttribute('data-scheduled-date');
                const end = item.getAttribute('data-end-date');
                const days = Math.max(1, getDayDiff(start, end));
                console.log('task#' + index, {
                start, end, days, id: item.id, title: item.getAttribute('data-item-name')
                });
                });
                });
                document.querySelectorAll('#month-view .month-day').forEach(day => {
                day.addEventListener('dragover', function(e) {
                e.preventDefault();
                if (isInteracting) updateScrollDirection(e);
                });
                day.addEventListener('drop', drop);
                });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
