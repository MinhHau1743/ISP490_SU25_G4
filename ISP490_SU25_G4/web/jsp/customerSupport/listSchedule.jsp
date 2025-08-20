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
           // =============================================
// File: listSchedule.js (bản người dùng gửi)
// Ghi chú: Chỉ thêm COMMENT giải thích theo từng hàm/khối.
// KHÔNG thay đổi, xóa, hay thêm logic/chức năng mới.
// =============================================

const currentView = '${viewMode}';
const isoDayDate = '${isoDayDate}';
const weekDates = [<c:forEach items="${weekDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];
const monthDates = [<c:forEach items="${monthDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];
var contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
feather.replace();
let draggingEvent = null;
let lastSlot = null;

// Khởi tạo hiển thị phần điều hướng tuần theo view hiện tại
// (Ẩn khi là list-view, hiện khi là day/week/month-view)
document.addEventListener('DOMContentLoaded', () => {
  const weekNav = document.querySelector('.week-nav');
  // Lấy view mode từ DOM -> localStorage -> mặc định 'day-view'
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

// Dữ liệu lịch trình render từ server vào mảng schedules
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
    // Danh sách phân công nhân sự cho lịch này
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

// Lặp lại contextPath & feather.replace() như trong mã gốc (giữ nguyên)
var contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
feather.replace();

// -----------------------------
// Hàm: safeTime(val)
// Mục đích: Chuẩn hóa giá trị giờ 'HH:mm'. Trả về null nếu rỗng/không hợp lệ.
// -----------------------------
function safeTime(val) {
  if (typeof val !== 'string' || !val.trim()) return null;
  return /^([01]\d|2[0-3]):([0-5]\d)$/.test(val.trim()) ? val.trim() : null;
}

// Cờ chặn việc gọi update trùng nhau
let isUpdating = false;

// -----------------------------
// Hàm: updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime)
// Mục đích: Gọi AJAX cập nhật thời gian lịch trình lên backend.
// Chú ý: Có cờ isUpdating để chống gọi trùng.
// -----------------------------
function updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime) {
  if (isUpdating) {
    console.log('Update already in progress, skipping...');
    return;
  }

  isUpdating = true;
  const url = contextPath + '/schedule?action=updateScheduleTime';
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
    },
    error: function (xhr) {
      console.error('Update failed:', {status: xhr.status, text: xhr.responseText});
      let errorMessage = 'Có lỗi khi cập nhật lịch trình!';
      if (xhr.responseJSON && xhr.responseJSON.message) errorMessage = xhr.responseJSON.message;
      else if (xhr.statusText) errorMessage += ' ' + xhr.statusText;
      showToast(errorMessage, 'error');
    },
    complete: function() {
      // Reset cờ dù thành công hay lỗi
      isUpdating = false;
    }
  });
}

// Biến & timer cho auto-scroll khi kéo thả
let isInteracting = false;
let scrollSpeed = 0;
let scrollContainer = null;
let scrollInterval = null;

// -----------------------------
// Hàm: startAutoScroll()
// Mục đích: Khởi tạo interval cuộn dọc .calendar-left khi kéo thả chạm mép.
// -----------------------------
function startAutoScroll() {
  scrollContainer = document.querySelector('.calendar-left');
  if (!scrollContainer) return;
  if (!scrollInterval) {
    scrollInterval = setInterval(() => {
      if (scrollSpeed !== 0) {
        scrollContainer.scrollTop += scrollSpeed;
      }
    }, 10); // 10ms như code gốc để cuộn mượt hơn
  }
}

// -----------------------------
// Hàm: updateScrollDirection(ev)
// Mục đích: Xác định hướng/tốc độ cuộn theo vị trí chuột so với vùng container.
// -----------------------------
function updateScrollDirection(ev) {
  if (!scrollContainer) return;
  lastMouseY = ev.clientY;
  const rect = scrollContainer.getBoundingClientRect();
  const edgeSize = 80; // vùng mép
  const maxSpeed = 15; // tốc độ tối đa
  const minSpeed = 3;  // tốc độ tối thiểu

  const distFromTop = ev.clientY - rect.top;
  const distFromBottom = rect.bottom - ev.clientY;

  if (distFromTop < edgeSize && distFromTop >= 0) {
    const intensity = 1 - (distFromTop / edgeSize);
    scrollSpeed = - Math.max(minSpeed, maxSpeed * intensity);
  }
  else if (distFromBottom < edgeSize && distFromBottom >= 0) {
    const intensity = 1 - (distFromBottom / edgeSize);
    scrollSpeed = Math.max(minSpeed, maxSpeed * intensity);
  }
  else if (ev.clientY < rect.top) {
    const distanceAbove = rect.top - ev.clientY;
    const intensity = Math.min(1, distanceAbove / 100);
    scrollSpeed = - Math.max(minSpeed, maxSpeed * intensity);
  }
  else if (ev.clientY > rect.bottom) {
    const distanceBelow = ev.clientY - rect.bottom;
    const intensity = Math.min(1, distanceBelow / 100);
    scrollSpeed = Math.max(minSpeed, maxSpeed * intensity);
  }
  else {
    scrollSpeed = 0;
  }
}

// -----------------------------
// Hàm: stopAutoScroll()
// Mục đích: Dừng cuộn tự động và tháo các listener liên quan tới kéo thả.
// -----------------------------
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

// -----------------------------
// Hàm tiện ích: parseTime(timeStr)
// Mục đích: chuyển 'HH:mm' -> tổng phút.
// -----------------------------
function parseTime(timeStr) {
  if (!timeStr) return 0;
  const [h, m] = timeStr.split(':').map(Number);
  return h * 60 + m;
}

// -----------------------------
// Hàm tiện ích: formatTime(minutes)
// Mục đích: chuyển tổng phút -> chuỗi 'HH:mm'.
// -----------------------------
function formatTime(minutes) {
  const h = Math.floor(minutes / 60) % 24;
  const m = minutes % 60;
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
}

// -----------------------------
// Hàm tiện ích: addDays(dateStr, days)
// Mục đích: cộng số ngày vào yyyy-mm-dd và trả về chuỗi ngày mới.
// -----------------------------
function addDays(dateStr, days) {
  const date = new Date(dateStr);
  date.setDate(date.getDate() + days);
  return date.toISOString().split('T')[0];
}

// Khởi tạo dragstart/dragend cho mỗi .event ở day/week view
// (Chỉ thêm comment, giữ nguyên lắng nghe sự kiện)
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
    // Xóa trạng thái mờ các event khác
    document.querySelectorAll('.event').forEach(event => {
      event.classList.remove('is-other-event');
    });
    setTimeout(() => isInteracting = false, 0);
    stopAutoScroll();
  });
});

// -----------------------------
// Hàm: bindSlotDnD(slot)
// Mục đích: Gắn các sự kiện dragover/dragleave/drop cho từng ô slot.
// -----------------------------
function bindSlotDnD(slot) {
  slot.addEventListener('dragover', function (e) {
    e.preventDefault();
    if (!draggingEvent) return;

    // Loại bỏ các bản sao (trùng id) còn lại khi đang kéo
    let allDups = document.querySelectorAll('.event[id="' + draggingEvent.id + '"]');
    allDups.forEach(ev => {
      if (ev !== draggingEvent) {
        ev.parentElement && ev.parentElement.removeChild(ev);
      }
    });

    // Đưa phần tử đang kéo vào slot hiện tại
    if (draggingEvent.parentElement !== slot) {
      slot.appendChild(draggingEvent);
    }

    // Hiệu ứng highlight slot
    if (lastSlot && lastSlot !== slot) lastSlot.classList.remove('slot-active');
    slot.classList.add('slot-active');
    lastSlot = slot;

    // Cập nhật hướng cuộn khi đang tương tác
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
    // Dừng auto-scroll rồi gọi hàm drop xử lý chính
    stopAutoScroll();
    drop(e);
  });
}

// Gắn DnD cho tất cả time-slot & all-day-slot hiện có
document.querySelectorAll('.time-slot, .all-day-slot').forEach(bindSlotDnD);

// -----------------------------
// Hàm: setScheduleCompletedUI(scheduleId)
// Mục đích: Đánh dấu lịch đã hoàn thành -> cập nhật model & UI, chặn kéo.
// -----------------------------
function setScheduleCompletedUI(scheduleId) {
  const sid = String(scheduleId);
  // Cập nhật dữ liệu cục bộ
  const sch = schedules.find(s => String(s.id) === sid);
  if (sch) {
    sch.statusName = 'Hoàn thành';
    sch.statusId = 3; // theo hệ thống
  }

  // Day/Week view: vô hiệu kéo và thêm class
  document.querySelectorAll(`#event-${sid}, .event[data-schedule-id="${sid}"]`)
    .forEach(el => {
      el.classList.add('is-completed');
      el.setAttribute('draggable', 'false');
    });

  // Month/List view: thêm class
  document.querySelectorAll(`.task-item[data-schedule-id="${sid}"], .event-item[data-schedule-id="${sid}"]`)
    .forEach(el => el.classList.add('is-completed'));

  // Panel chi tiết nếu đang mở đúng event -> cập nhật trạng thái
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

// -----------------------------
// Hàm: drag(ev)
// Mục đích: Xử lý bắt đầu kéo 1 event, chặn nếu đã hoàn thành, bật auto-scroll.
// -----------------------------
function drag(ev) {
  if (ev.target.classList.contains('is-completed')) {
    ev.preventDefault();
    showToast && showToast('Không thể kéo lịch đã hoàn thành', 'warning');
    return;
  }
  ev.dataTransfer.setData("text/plain", ev.target.id);
  ev.dataTransfer.effectAllowed = "move";
  isInteracting = true;
  startAutoScroll();
  document.addEventListener('dragover', updateScrollDirection);
  document.addEventListener('dragend', stopAutoScroll);
  document.addEventListener('drop', stopAutoScroll);

  const draggedId = ev.target.id;
  document.querySelectorAll('.event').forEach(event => {
    if (event.id !== draggedId) {
      event.classList.add('is-other-event');
    }
  });
}

// -----------------------------
// Hàm: allowDrop(ev)
// Mục đích: Cho phép thả trong vùng hợp lệ và cập nhật hướng cuộn khi đang tương tác.
// -----------------------------
function allowDrop(ev) {
  ev.preventDefault();
  if (isInteracting) {
    updateScrollDirection(ev);
  }
}

// Khởi tạo chiều cao cho event khi DOM sẵn sàng (day/week)
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('#week-view .event, #day-view .event').forEach(function (eventDiv) {
    const st = eventDiv.dataset.startTime || null;
    const et = eventDiv.dataset.endTime || null;
    applyEventHeight(eventDiv, st, et);
  });
});

// -----------------------------
// Hàm: applyEventHeight(el, startTime, endTime)
// Mục đích: Set chiều cao inline cho event theo thời lượng; bỏ height nếu all-day.
// -----------------------------
function applyEventHeight(el, startTime, endTime) {
  if (startTime) {
    const h = getEventHeight(startTime, endTime, 30, 40, 40);
    el.style.setProperty('height', h + 'px', 'important');
  } else {
    el.style.removeProperty('height');
  }
}

// -----------------------------
// Hàm: getEventHeight(startTime, endTime, slotMinutes, slotHeight, extraPx)
// Mục đích: Tính chiều cao theo số slot thời gian (mặc định 30' = 40px) + extra.
// Ghi chú: Hỗ trợ event qua đêm (end < start -> +24h).
// -----------------------------
function getEventHeight(startTime, endTime, slotMinutes = 30, slotHeight = 40, extraPx = 40) {
  if (!startTime || !endTime) return slotHeight + extraPx;
  const [sh, sm] = startTime.split(':').map(Number);
  const [eh, em] = endTime.split(':').map(Number);
  let totalMinutes = (eh * 60 + em) - (sh * 60 + sm);
  if (totalMinutes <= 0) totalMinutes += 24 * 60; // qua đêm
  const slotCount = totalMinutes / slotMinutes;
  return Math.max(slotHeight, slotCount * slotHeight) + extraPx;
}

// Thiết lập height tất cả event (day/week) khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('#week-view .event, #day-view .event').forEach(function(eventDiv) {
    if (eventDiv.classList.contains('all-day')) return;
    const startTime = eventDiv.dataset.startTime;
    const endTime = eventDiv.dataset.endTime;
    const height = getEventHeight(startTime, endTime, 30, 40, 40);
    eventDiv.style.setProperty('height', height + 'px', 'important');
  });
});

// -----------------------------
// Hàm: drop(ev)
// Mục đích: Xử lý logic chính khi thả event vào ô đích: cập nhật DOM, dữ liệu, gọi updateEvent.
// -----------------------------
function drop(ev) {
  ev.preventDefault();
  stopAutoScroll();

  // helper nội bộ: set height theo giờ hoặc bỏ height khi all-day
  function applyEventHeight(el, startTime, endTime) {
    if (startTime) {
      const h = getEventHeight(startTime, endTime, 30, 40, 40);
      el.style.setProperty('height', h + 'px', 'important');
    } else {
      el.style.removeProperty('height');
    }
  }

  const data = ev.dataTransfer.getData("text");
  let eventElement = document.getElementById(data);
  if (!eventElement) return;

  // Xóa class đánh dấu các event khác
  document.querySelectorAll('.event').forEach(e => e.classList.remove('is-other-event'));

  // Xóa các bản sao trùng id (giữ lại phần tử đang kéo)
  document.querySelectorAll('#' + CSS.escape(data)).forEach((el) => {
    if (el !== eventElement && el.parentNode) el.parentNode.removeChild(el);
  });

  // Xác định ô đích hợp lệ
  let slot = ev.target.closest('.time-slot, .all-day-slot, .all-day-event-container, .month-day');
  if (!slot) return;

  // Chuyển phần tử sang slot mới
  if (eventElement.parentNode) eventElement.parentNode.removeChild(eventElement);
  slot.appendChild(eventElement);

  // Bỏ highlight nếu có
  if (typeof lastSlot !== 'undefined' && lastSlot) {
    lastSlot.classList.remove('slot-active');
    lastSlot = null;
  }

  const scheduleId = eventElement.id.split('-')[1];
  const scheduleToUpdate = schedules.find(s => s.id == scheduleId);
  if (!scheduleToUpdate) return;

  let newScheduledDate = null, newEndDate = null, newStartTime = null, newEndTime = null;

  // Xác định view hiện tại theo slot
  const view = slot.closest('.calendar-view') ? slot.closest('.calendar-view').id : null;

  if (slot.classList.contains('all-day-event-container') && view === 'week-view') {
    // Kéo thả all-day theo lưới 7 cột trong week-view
    const rect = slot.getBoundingClientRect();
    const dayWidth = rect.width / 7;
    const x = ev.clientX - rect.left;
    let startCol = Math.floor(x / dayWidth) + 1;
    startCol = Math.max(1, Math.min(startCol, 7));
    eventElement.style.gridColumn = `${startCol} / span 1`;

    newScheduledDate = weekDates[startCol - 1];
    newStartTime = null;
    eventElement.classList.add('all-day');
    eventElement.dataset.startTime = '';
    eventElement.dataset.endTime = '';
    applyEventHeight(eventElement, null, null);
    const t = eventElement.querySelector('.event-time');
    if (t) t.textContent = 'Cả ngày';
  }
  else if (slot.classList.contains('all-day-slot') || slot.classList.contains('month-day')) {
    // Thả vào all-day (day/week) hoặc ô ngày trong month-view
    newScheduledDate = slot.dataset.date;
    newStartTime = null;
    eventElement.classList.add('all-day');
    eventElement.dataset.startTime = '';
    eventElement.dataset.endTime = '';
    eventElement.style.removeProperty('grid-column');
    applyEventHeight(eventElement, null, null);
    const t = eventElement.querySelector('.event-time');
    if (t) t.textContent = 'Cả ngày';
  }
  else if (slot.classList.contains('time-slot')) {
    // Thả vào slot giờ -> cập nhật thời gian bắt đầu theo data-start-time
    newScheduledDate = slot.dataset.date;
    newStartTime = slot.dataset.startTime || null;
    eventElement.classList.remove('all-day');
    eventElement.style.removeProperty('grid-column');
    eventElement.dataset.startTime = newStartTime || '';
  }
  else {
    // Fallback: có date nhưng không xác định -> coi là all-day
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

  // Tính newEndDate dựa vào số ngày của lịch cũ (giữ nguyên độ dài multi-day)
  if (scheduleToUpdate.scheduledDate && scheduleToUpdate.endDate) {
    const oldStartDate = new Date(scheduleToUpdate.scheduledDate);
    const oldEndDate = new Date(scheduleToUpdate.endDate);
    const dayDifference = Math.round((oldEndDate - oldStartDate) / (24 * 60 * 60 * 1000));
    if (dayDifference > 0) {
      const newStartDate = new Date(newScheduledDate);
      newStartDate.setDate(newStartDate.getDate() + dayDifference);
      newEndDate = newStartDate.toISOString().split('T')[0];
    } else {
      newEndDate = newScheduledDate; // 1 ngày
    }
  } else {
    newEndDate = newScheduledDate; // không có endDate cũ -> coi 1 ngày
  }

  // Cập nhật UI + model + gọi AJAX tùy theo có giờ hay all-day
  if (newStartTime) {
    let durationMinutes = 0;
    if (scheduleToUpdate.startTime && scheduleToUpdate.endTime) {
      const [sh, sm] = scheduleToUpdate.startTime.split(':').map(Number);
      const [eh, em] = scheduleToUpdate.endTime.split(':').map(Number);
      durationMinutes = (eh * 60 + em) - (sh * 60 + sm);
      if (durationMinutes <= 0) durationMinutes += 24 * 60;
    } else {
      durationMinutes = 30; // fallback 30'
    }

    let [nh, nm] = newStartTime.split(':').map(Number);
    let newEndTotalMin = nh * 60 + nm + durationMinutes;
    let newEndH = Math.floor(newEndTotalMin / 60) % 24;
    let newEndM = newEndTotalMin % 60;
    newEndTime = String(newEndH).padStart(2, '0') + ":" + String(newEndM).padStart(2, '0');

    const eventTimeElement = eventElement.querySelector('.event-time');
    if (eventTimeElement) eventTimeElement.textContent = newStartTime + " - " + newEndTime;

    eventElement.dataset.startTime = newStartTime || '';
    eventElement.dataset.endTime = newEndTime || '';
    applyEventHeight(eventElement, newStartTime, newEndTime);

    // Model cục bộ
    scheduleToUpdate.scheduledDate = newScheduledDate;
    scheduleToUpdate.startTime = newStartTime;
    scheduleToUpdate.endTime = newEndTime;
    scheduleToUpdate.endDate = newEndDate;
    scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');

    // Gọi backend
    if (typeof updateEvent === "function" && scheduleId && newScheduledDate) {
      updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime);
    }
  } else {
    // All-day
    const eventTimeElement = eventElement.querySelector('.event-time');
    if (eventTimeElement) eventTimeElement.textContent = 'Cả ngày';
    eventElement.dataset.startTime = '';
    eventElement.dataset.endTime = '';
    applyEventHeight(eventElement, null, null);

    scheduleToUpdate.scheduledDate = newScheduledDate;
    scheduleToUpdate.startTime = null;
    scheduleToUpdate.endTime = null;
    scheduleToUpdate.endDate = newEndDate;
    scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');

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

// Chạy khi DOM load xong (khối trống theo mã gốc)
document.addEventListener('DOMContentLoaded', function () {
});

// -----------------------------
// Hàm: showDetails(element)
// Mục đích: Hiển thị panel chi tiết của lịch và render danh sách nhân sự được gán.
// -----------------------------
function showDetails(element) {
  if (isInteracting) return;
  const detailsPanel = document.getElementById('event-details-panel');
  if (!detailsPanel) return;

  let scheduleId = element.dataset.scheduleId || element.id?.split('-')[1];
  if (!scheduleId) return;

  const schedule = schedules.find(s => s.id == scheduleId);
  if (schedule) {
    // Gán chi tiết cơ bản
    detailsPanel.querySelector('.event-id').textContent = schedule.id;
    detailsPanel.querySelector('.event-title').textContent = schedule.title;
    detailsPanel.querySelector('.event-time-detail').textContent = schedule.startTime ? schedule.startTime : 'Cả ngày';
    detailsPanel.querySelector('.event-date').textContent = schedule.scheduledDate + (schedule.endDate ? ' - ' + schedule.endDate : '');
    detailsPanel.querySelector('.event-time-range').textContent = schedule.startTime ? schedule.startTime + (schedule.endTime ? ' - ' + schedule.endTime : '') : 'Cả ngày';
    detailsPanel.querySelector('.event-location').textContent = schedule.location || 'Không xác định';
    detailsPanel.querySelector('.event-notes').textContent = schedule.notes || 'Không có ghi chú';

    // Xử lý badge trạng thái -> chuẩn hóa tiếng Việt & class màu
    const assignmentsContainer = detailsPanel.querySelector('.event-assignments');
    assignmentsContainer.innerHTML = '';
    const statusSpan = detailsPanel.querySelector('.event-status');
    if (statusSpan) {
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
        fromName ? (mapEnToVi[fromName] || fromName)
                 : (mapIdToVi[schedule.statusId] || 'Sắp tới');

      const classByStatus = {
        'Sắp tới': 'badge-upcoming',
        'Đang thực hiện': 'badge-inprogress',
        'Hoàn thành': 'badge-completed',
        'Quá hạn': 'badge-cancelled', // hoặc badge-overdue nếu có
        'Đã hủy': 'badge-cancelled'
      };
      statusSpan.textContent = statusVi;
      statusSpan.className = 'event-status badge px-2 py-1 ' + (classByStatus[statusVi] || 'badge-upcoming');
    }

    // Helper nội bộ: lấy 2 ký tự viết tắt từ họ tên (VD: "Nguyễn Văn A" -> "NA")
    function getInitials(name) {
      if (!name || typeof name !== 'string' || name.trim() === '') {
        return '?';
      }
      const parts = name.trim().split(' ').filter(p => p);
      if (parts.length > 1) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
      } else if (parts.length === 1) {
        return parts[0].substring(0, 2).toUpperCase();
      }
      return '?';
    }

    // Render danh sách người được phân công
    if (schedule.assignments && schedule.assignments.length > 0) {
      schedule.assignments.forEach(assignment => {
        const fullName = assignment.fullName || 'Chưa xác định';
        const tag = document.createElement('div');
        tag.className = 'employee-tag';
        const avatar = document.createElement('span');
        avatar.className = 'avatar';
        avatar.textContent = getInitials(fullName);
        const nameSpan = document.createElement('span');
        nameSpan.className = 'employee-name';
        nameSpan.textContent = fullName;
        tag.appendChild(avatar);
        tag.appendChild(nameSpan);
        assignmentsContainer.appendChild(tag);
      });
    } else {
      assignmentsContainer.innerHTML = '<span style="font-style: italic; color: #888;">Không có phân công</span>';
    }

    // Gán link nút Xem/Sửa & mở panel
    const viewBtn = detailsPanel.querySelector('a[title="Xem chi tiết"]');
    viewBtn.href = contextPath + '/schedule?action=viewScheduleDetail&id=' + encodeURIComponent(schedule.id);
    detailsPanel.classList.add('show');
    const editBtn = detailsPanel.querySelector('a[title="Sửa"]');
    editBtn.href = contextPath + '/schedule?action=updateSchedule&id=' + encodeURIComponent(schedule.id);
    detailsPanel.classList.add('show');
  }
}

// -----------------------------
// Hàm: closeDetails()
// Mục đích: Đóng panel chi tiết nếu đang mở.
// -----------------------------
function closeDetails() {
  const detailsPanel = document.getElementById('event-details-panel');
  if (detailsPanel)
    detailsPanel.classList.remove('show');
}

// -----------------------------
// Khối: Toggle view & lưu localStorage
// Mục đích: Chuyển giữa day/week/month/list và submit form để server render tương ứng.
// -----------------------------
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

// Khởi tạo view đã lưu từ localStorage sau khi DOM sẵn sàng
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

// -----------------------------
// Khối: Khởi tạo các hành vi khác sau DOMContentLoaded
// Mục đích: Gắn resize-handle cho all-day event, xử lý dragend, cho phép kéo thả ở month-view,
//           highlight ngày hôm nay ở week-view & month-view.
// -----------------------------
document.addEventListener('DOMContentLoaded', () => {
  // Thêm nút kéo giãn cho all-day events (week-view)
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

  // Xóa trạng thái tương tác sau khi kéo xong
  document.addEventListener('dragend', (e) => {
    if (e.target.classList.contains('event')) {
      setTimeout(() => isInteracting = false, 0);
    }
  });

  // Cho phép kéo các task ở month-view
  document.querySelectorAll('#month-view .task-item').forEach(item => {
    item.id = item.dataset.taskId ? 'task-' + item.dataset.taskId : 'task-' + Math.random().toString(36).substring(7);
    item.draggable = true;
    item.addEventListener('dragstart', drag);
    item.addEventListener('dragend', (e) => {
      setTimeout(() => isInteracting = false, 0);
    });
  });

  // Cho phép thả vào từng ô ngày ở month-view
  document.querySelectorAll('#month-view .month-day').forEach(day => {
    day.addEventListener('dragover', allowDrop);
    day.addEventListener('drop', drop);
  });

  // Highlight ngày hôm nay
  const today = new Date().toISOString().split('T')[0];

  // Week-view: header cột hôm nay
  const weekHeaders = document.querySelectorAll('#week-view .day-header-cell:not(:first-child)');
  weekHeaders.forEach((header, index) => {
    if (weekDates[index] === today) {
      header.classList.add('today-highlight');
    }
  });

  // Month-view: ô ngày hôm nay
  const monthDays = document.querySelectorAll('#month-view .month-day');
  monthDays.forEach((day) => {
    if (day.dataset.date === today) {
      day.classList.add('today-highlight');
    }
  });
});

// -----------------------------
// Khối: Navigation prev/next với xử lý viewMode
// Mục đích: Điều hướng sang ngày/tuần trước/sau và submit form để server render.
// -----------------------------
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

// -----------------------------
// Khối: Khởi tạo Flatpickr cho phần tử hiển thị ngày hiện tại
// Mục đích: Cho phép chọn nhanh ngày -> submit form để đổi ngày.
// -----------------------------
document.addEventListener('DOMContentLoaded', function() {
  const dateDisplay = document.getElementById('currentDate');
  if (dateDisplay) {
    flatpickr(dateDisplay, {
      dateFormat: "Y-m-d",
      defaultDate: dateDisplay.dataset.date,
      onChange: function(selectedDates, dateStr, instance) {
        console.log('Ngày được chọn:', dateStr);
        document.getElementById('controllerDay').value = dateStr;
        document.getElementById('currentDay').value = dateStr;
        document.getElementById('dayNavForm').submit();
      }
    });
  }
});

// -----------------------------
// Nhóm helper xử lý ngày cho month-view (không chỉnh sửa logic, chỉ comment)
// -----------------------------
function toDate(d){ return new Date((d || '') + 'T00:00:00'); }
function dstr(d){ return d.toISOString().split('T')[0]; }
function clamp(d, lo, hi){ return d < lo ? lo : (d > hi ? hi : d); }

function getMonthWeeksFromDom(){
  const cells = Array.from(document.querySelectorAll('#month-view .month-day'));
  const weeks = [];
  for (let i = 0; i < cells.length; i += 7) weeks.push(cells.slice(i, i + 7));
  return weeks;
}

// Log thông tin task trong month-view khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', function () {
  function getDayDiff(start, end) {
    if (!start) return 1;
    if (!end) end = start;
    const s = new Date(start);
    const e = new Date(end);
    if (Number.isNaN(s.getTime()) || Number.isNaN(e.getTime())) return 1;
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

// Cho phép dragover & drop ở month-view để dùng chung drop()
document.querySelectorAll('#month-view .month-day').forEach(day => {
  day.addEventListener('dragover', function(e) {
    e.preventDefault();
    if (isInteracting) updateScrollDirection(e);
  });
  day.addEventListener('drop', drop);
});

// -----------------------------
// KẾT THÚC FILE — Chỉ thêm comment, không chỉnh sửa logic
// -----------------------------

        </script>
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
