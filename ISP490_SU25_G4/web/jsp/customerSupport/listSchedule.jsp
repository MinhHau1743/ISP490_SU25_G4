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
        <title>Lịch bảo trì - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listSchedule.css">
        <style>
            body {
                background: #fff;
                margin: 0;
                padding: 0;
            }

            /* Main app container with full-width white background */
            .app-container {
                background: #fff;
                width: 100%;
                min-height: 100vh;
                box-sizing: border-box;
            }

            /* Content wrapper with full-width white background */
            .content-wrapper {
                background: #fff;
                width: 100%;
                box-sizing: border-box;
            }

            /* Header section with full-width white background */
            .main-top-bar {
                background: #fff;
                padding: 10px 0;
                border-bottom: 1px solid #ddd;
                width: 100%;
                box-sizing: border-box;
            }

            /* Calendar toolbar with full-width white background, matching title section theme */
            .calendar-toolbar {
                background: #fff;
                padding: 15px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                flex-wrap: wrap;
                gap: 10px;
                width: 100%;
                box-sizing: border-box;
                position: sticky;
                top: 0;
                z-index: 1000;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            /* Pseudo-element to cover space above toolbar */
            .calendar-toolbar::before {
                content: "";
                position: absolute;
                top: -30px;
                left: 0;
                right: 0;
                height: 30px;
                background: #fff;
                z-index: -1;
            }

            /* Title styling */
            .calendar-toolbar .title {
                margin: 0;
                font-size: 20px;
                font-weight: 700;
                color: var(--text-primary);
                margin-bottom: 8px;
            }

            /* View toggle buttons */
            .calendar-toolbar .view-toggle button {
                background: #eee;
                border: none;
                padding: 5px 10px;
                margin: 0 2px;
                border-radius: 8px;
                cursor: pointer;
                transition: background 0.3s;
                color: var(--text-primary);
            }

            .calendar-toolbar .view-toggle button.active {
                background: #ddd;
            }

            /* Week navigation */
            .calendar-toolbar .week-nav {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .calendar-toolbar .week-nav .btn-nav {
                background: none;
                border: none;
                cursor: pointer;
                padding: 5px;
                color: var(--text-primary);
            }

            .calendar-toolbar .week-nav .date-range {
                font-weight: bold;
                color: var(--text-primary);
            }

            /* Toolbar spacer */
            .calendar-toolbar .toolbar-spacer {
                flex-grow: 1;
            }

            /* Add schedule button */
            .calendar-toolbar #add-schedule-btn {
                background: #007bff;
                color: white;
                border: none;
                padding: 8px 15px;
                border-radius: 8px;
                cursor: pointer;
                transition: background 0.3s;
            }

            .calendar-toolbar #add-schedule-btn:hover {
                background: #0056b3;
            }

            /* Calendar container */
            .calendar-content {
                display: flex;
                background: #fff;
                border-radius: 16px;
                overflow: visible;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                width: 100%;
                box-sizing: border-box;
            }

            /* Individual calendar views */
            #day-view, #week-view, #month-view, #list-view {
                background: #fff;
            }

            /* Time grid, month grid, and list grid */
            .time-grid, .month-grid, .list-grid {
                background: #fff;
            }

            /* Time slots */
            .time-slot {
                min-height: 60px;
                height: auto;
                border-bottom: 1px solid #eee;
                border-left: 1px solid #eee;
                border-radius: 8px;
                position: relative;
                display: flex;
                flex-direction: column;
                padding: 5px;
                background: #fff;
            }

            /* All-day slots */
            .all-day-slot {
                min-height: 40px;
                background: #fff;
                border-bottom: 1px solid #ddd;
                border-left: 1px solid #eee;
                border-radius: 8px;
                position: relative;
                display: flex;
                flex-direction: column;
                padding: 5px;
                height: auto;
            }

            /* All-day event container */
            .all-day-event-container {
                grid-row: 1;
                grid-column: 2 / -1;
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                min-height: 40px;
                height: auto;
                padding: 5px;
                background: #fff;
                border-bottom: 1px solid #ddd;
                border-radius: 8px;
                z-index: 1;
                position: relative;
            }

            /* Time grid setup */
            .time-grid {
                display: grid;
                border-top: 1px solid #ddd;
                position: relative;
                grid-auto-flow: dense;
                background: #fff;
            }

            #day-view .time-grid {
                grid-template-columns: auto 1fr;
            }

            #week-view .time-grid {
                grid-template-columns: auto repeat(7, 1fr);
                grid-auto-rows: minmax(60px, auto);
            }

            /* Time labels */
            .time-label {
                text-align: right;
                padding-right: 10px;
                color: #666;
                font-size: 12px;
                height: 60px;
                line-height: 60px;
                border-bottom: 1px solid #eee;
            }

            /* Events */
            .event {
                position: relative;
                left: 0;
                right: 0;
                background: teal;
                color: white;
                padding: 5px;
                border-radius: 10px;
                font-size: 14px;
                cursor: pointer;
                top: 0;
                height: auto;
                margin: 5px;
                width: calc(100% - 10px);
                box-sizing: border-box;
            }

            /* All-day events */
            .event.all-day {
                position: relative;
                border-radius: 10px;
                height: auto;
                top: 0;
                margin: 5px 0;
                width: 100%;
                box-sizing: border-box;
            }

            .event.all-day .resize-handle {
                position: absolute;
                right: -2.5px;
                top: 0;
                bottom: 0;
                width: 5px;
                cursor: ew-resize;
                background: transparent;
            }

            /* Event time text */
            .event-time {
                font-weight: bold;
            }

            /* Day navigation */
            .day-nav {
                display: flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 10px;
            }

            .date {
                margin: 0 10px;
                font-weight: bold;
            }

            /* Calendar left panel */
            .calendar-left {
                flex: 1;
                overflow-y: auto;
                overflow-x: auto;
                max-height: 80vh;
                position: relative;
            }

            /* Event details panel */
            .event-details {
                display: none;
                width: 350px;
                border-left: 1px solid #ddd;
                padding: 20px 20px 10px;
                background: #fff;
                box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
                position: sticky;
                top: 5px;
                align-self: flex-start;
                height: 80vh;
                overflow-y: auto;
                z-index: 10;
                border-radius: 12px;
            }

            .event-details.show {
                display: block;
            }

            .event-details .header {
                position: sticky;
                top: 0;
                background: #fff;
                z-index: 2;
                padding: 10px 0;
                border-bottom: 1px solid #ddd;
            }

            .event-details .actions {
                display: flex;
                gap: 10px;
                margin-bottom: 24px;
                margin-top: 16px;
                justify-content: flex-end;
            }

            .event-details .actions a {
                color: #666;
                cursor: pointer;
            }

            .event-details .event-time-detail {
                font-weight: bold;
                color: #666;
                margin-right: 10px;
            }

            .event-details .event-title {
                font-size: 18px;
                font-weight: bold;
            }

            .event-details .event-type {
                color: #666;
                margin-left: 10px;
            }

            .event-details .event-info {
                display: flex;
                align-items: center;
                margin-bottom: 10px;
                color: #333;
            }

            .event-details .event-info i {
                margin-right: 10px;
                color: #666;
            }

            .event-details .event-guests {
                margin-top: 20px;
            }

            .event-details .guests-list {
                display: flex;
                gap: 5px;
                flex-wrap: wrap;
            }

            .event-details .guest {
                display: flex;
                align-items: center;
                background: #e6f7ff;
                padding: 5px 10px;
                border-radius: 20px;
            }

            .event-details .guest img {
                width: 20px;
                height: 20px;
                border-radius: 50%;
                margin-right: 5px;
            }

            .event-header {
                margin-bottom: 10px;
            }

            .event-details .event-description,
            .event-details .event-notes,
            .event-details .event-logistics,
            .event-details .event-contacts,
            .event-details .event-agenda,
            .event-details .event-resources {
                margin-top: 15px;
                padding-bottom: 10px;
                font-size: 14px;
            }

            .event-details .dot {
                width: 10px;
                height: 10px;
                border-radius: 50%;
                background: purple;
                margin-right: 10px;
            }
            .event-info i {
                font-size: 1.5em; /* Điều chỉnh giá trị này để tăng hoặc giảm kích thước, ví dụ: 1.2em, 1.8em */
                margin-right: 0.5em; /* Điều chỉnh khoảng cách giữa icon và chữ nếu cần */
            }
            .actions i {
                font-size: 1.8em; /* Điều chỉnh kích thước tùy ý, ví dụ 24px */
                color: #666;     /* Tùy chỉnh màu sắc nếu muốn */
            }

            .actions a:hover i {
                color: #000; /* Đổi màu khi di chuột qua cho đẹp hơn */
            }
            .event:last-child {
                margin-bottom: 0;
            }

            /* Maintenance cards and task items */
            .maintenance-card, .task-item {
                background: #fff;
                border-radius: 12px;
                cursor: pointer;
                margin-bottom: 10px;
                padding: 10px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            /* Day header row */
            .day-header-row {
                position: sticky;
                top: 0;
                background: #fff;
                z-index: 2;
                display: grid;
                grid-template-columns: auto repeat(7, 1fr);
                text-align: center;
                border-bottom: 1px solid #ddd;
            }

            .day-header-cell {
                font-weight: bold;
                padding: 10px;
                border-right: 1px solid #eee;
            }

            .day-header-row .day-header-cell:first-child {
                position: sticky;
                left: 0;
                background: #fff;
                z-index: 3;
                border-right: 1px solid #eee;
            }

            /* Remove redundant border for adjacent all-day slots */
            .time-grid .all-day-slot + .all-day-slot {
                border-left: none;
            }

            /* Month grid */
            .month-grid {
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                grid-auto-rows: minmax(100px, auto);
                text-align: center;
                background: #fff;
            }

            .month-grid-header {
                font-weight: bold;
                border: 1px solid #ddd;
                padding: 5px;
                border-radius: 8px;
            }

            .month-day {
                position: relative;
                min-height: 100px;
                height: auto;
                border: 1px solid #ddd;
                padding-top: 25px;
                background: #fff;
                border-radius: 8px;
            }

            .day-number {
                position: absolute;
                top: 5px;
                right: 5px;
                font-size: 12px;
                color: #666;
            }

            .tasks-list {
                display: flex;
                flex-direction: column;
            }

            /* Month view events */
            #month-view .event {
                background: teal;
                color: white;
                padding: 5px;
                border-radius: 10px;
                font-size: 14px;
                cursor: pointer;
                margin: 2px;
                width: calc(100% - 10px);
                box-sizing: border-box;
            }

            #month-view .event.all-day {
                height: 20px;
                margin: 25px 0 2px 0;
                position: relative;
                top: 0;
                border-radius: 10px;
            }

            .resize-handle {
                position: absolute;
                right: -2.5px;
                top: 0;
                bottom: 0;
                width: 5px;
                cursor: ew-resize;
                background: transparent;
            }

            .time-grid {
                position: relative;
                min-height: 0;
            }

            .time-label {
                position: sticky;
                left: 0;
                background: #fff;
                z-index: 1;
                border-right: 1px solid #eee;
                text-align: right;
                padding-right: 10px;
                color: #666;
                font-size: 12px;
                height: 60px;
                line-height: 60px;
                border-bottom: 1px solid #eee;
            }

            .all-day-event-container {
                position: sticky;
                top: 0;
                z-index: 1;
                background: #fff;
            }

            .event[draggable="true"] {
                cursor: grab !important;
            }

            .event[draggable="true"]:active {
                cursor: grabbing !important;
            }

            /* Highlight cho ngày hiện tại trong week-view */
            .day-header-cell.today-highlight {
                background-color: #e6f7ff;
                border-bottom: 2px solid #007bff;
                box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
            }

            /* Highlight cho số ngày hiện tại trong month-view với hình tròn */
            .month-day.today-highlight .day-number {
                /* Thay đổi màu nền thành màu xanh mòng két rất nhạt */
                background-color: #e0f2f1;
                /* Thay đổi màu viền thành màu xanh mòng két đậm */
                border: 2px solid #008080;
                border-radius: 50%;
                width: 24px;
                height: 24px;
                line-height: 24px;
                text-align: center;
                /* Thay đổi màu chữ thành màu xanh mòng két đậm */
                color: #008080;
                font-weight: bold;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                position: absolute;
                top: 5px;
                right: 5px;
            }

            /* Loại bỏ highlight nền cho toàn ô trong month-view */
            .month-day.today-highlight {
                background-color: transparent;
                border: none;
                box-shadow: none;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>
            <div class="content-wrapper">
                <section class="main-content-body">
                    <div class="calendar-toolbar">
                        <h1 class="title">Lịch bảo trì</h1>
                        <div class="view-toggle">
                            <button id="view-day-btn" class="btn-toggle <c:if test="${viewMode == 'day-view'}">active</c:if>" data-view="day-view">Ngày</button>
                            <button id="view-week-btn" class="btn-toggle <c:if test="${viewMode == 'week-view'}">active</c:if>" data-view="week-view">Tuần</button>
                            <button id="view-month-btn" class="btn-toggle <c:if test="${viewMode == 'month-view'}">active</c:if>" data-view="month-view">Tháng</button>
                            <button id="view-list-btn" class="btn-toggle <c:if test="${viewMode == 'list-view'}">active</c:if>" data-view="list-view">Danh sách</button>
                            </div>
                            <!-- HIDDEN FORM TO SUBMIT NEXT/PREV DAY -->
                            <form id="dayNavForm" method="get" action="listSchedule">
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
                        <button class="btn-primary" id="add-schedule-btn">
                            <i data-feather="plus"></i>
                            Lên lịch bảo trì
                        </button>
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
                                        <div class="${startTime == '00:00' ? 'time-slot all-day-slot' : 'time-slot'}" data-date="${isoDayDate}"
                                             <c:if test="${startTime != '00:00'}">data-start-time="${startTime}"</c:if>
                                                 ondragover="allowDrop(event)" ondrop="drop(event)">
                                             <c:forEach var="schedule" items="${schedules}">
                                                 <c:if test="${schedule.scheduledDate.equals(today) && (startTime == '00:00' ? schedule.startTime == null : (schedule.startTime != null && schedule.startTime.toString() == startTime))}">
                                                     <div class="event ${startTime == '00:00' ? 'all-day' : ''}" id="event-${schedule.id}" data-schedule-id="${schedule.id}" draggable="true" ondragstart="drag(event)" ondragover="allowDrop(event)" onclick="showDetails(this)">
                                                         <span class="event-time">${schedule.startTime != null ? schedule.startTime : 'Cả ngày'}</span><br>${schedule.title}
                                                     </div>
                                                 </c:if>
                                             </c:forEach>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <div id="week-view" class="calendar-view <c:if test="${viewMode == 'week-view'}">active</c:if>">
                                    <div class="day-header-row">
                                        <div class="day-header-cell"></div>
                                    <c:forEach var="label" items="${dayHeaders}" varStatus="status">
                                        <div class="day-header-cell">${label}</div>
                                    </c:forEach>
                                </div>

                                <div class="time-grid">
                                    <!-- All-day row -->
                                    <div class="time-label">Cả ngày</div>
                                    <div class="all-day-event-container" ondragover="allowDrop(event)" ondrop="drop(event)">
                                        <c:forEach var="schedule" items="${schedules}">
                                            <c:if test="${schedule.startTime == null}">
                                                <c:forEach var="weekDate" items="${weekDates}" varStatus="ws">
                                                    <c:if test="${schedule.scheduledDate.equals(weekDate)}">
                                                        <div class="event all-day" style="grid-column: ${ws.index + 1} / span 1;" data-schedule-id="${schedule.id}" draggable="true" ondragstart="drag(event)" onclick="showDetails(this)">
                                                            ${schedule.title}
                                                            <div class="resize-handle"></div>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </c:if>
                                        </c:forEach>
                                    </div>

                                    <!-- Time slots per hour -->
                                    <c:forEach var="hour" items="${hours}">
                                        <div class="time-label">${hour}</div>
                                        <c:forEach var="day" items="${days}" varStatus="ds">
                                            <div class="time-slot" data-start-time="${hour}" data-day="${day}" data-date="${weekDates[ds.index]}" ondragover="allowDrop(event)" ondrop="drop(event)">
                                                <c:forEach var="schedule" items="${schedules}">
                                                    <c:if test="${schedule.scheduledDate.equals(weekDates[ds.index]) && schedule.startTime != null && schedule.startTime.toString() == hour}">
                                                        <div class="event" id="event-${schedule.id}" data-schedule-id="${schedule.id}" draggable="true" ondragstart="drag(event)" ondragover="allowDrop(event)" onclick="showDetails(this)">
                                                            <span class="event-time">${hour}</span><br>${schedule.title}
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </c:forEach>
                                    </c:forEach>
                                </div>
                            </div>

                            <div id="month-view" class="calendar-view <c:if test="${viewMode == 'month-view'}">active</c:if>">
                                    <div class="month-grid">
                                        <div class="month-grid-header">Thứ Hai</div><div class="month-grid-header">Thứ Ba</div><div class="month-grid-header">Thứ Tư</div><div class="month-grid-header">Thứ Năm</div><div class="month-grid-header">Thứ Sáu</div><div class="month-grid-header">Thứ Bảy</div><div class="month-grid-header">Chủ Nhật</div>
                                    <c:forEach var="dayNum" items="${dayNumbers}" varStatus="status">
                                        <div class="month-day ${isCurrentMonths[status.index] ? '' : 'other-month'}" data-date="${monthDates[status.index]}">
                                            <div class="day-number">${dayNum}</div>
                                            <div class="tasks-list">
                                                <c:forEach var="schedule" items="${schedules}">
                                                    <c:if test="${schedule.scheduledDate.equals(monthDates[status.index])}">
                                                        <div class="task-item status-${fn:toLowerCase(schedule.status)}" data-task-id="${schedule.id}" data-schedule-id="${schedule.id}" data-item-name="${schedule.title}" title="${schedule.title}" onclick="showDetails(this)" draggable="true" ondragstart="drag(event)">
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
                                        <h2>Tất cả lịch bảo trì</h2>
                                        <div class="maintenance-list">
                                        <c:forEach var="schedule" items="${schedules}">
                                            <div class="maintenance-card status-${fn:toLowerCase(schedule.status)}" data-schedule-id="${schedule.id}" onclick="showDetails(this)">
                                                <div class="card-content">
                                                    <p class="title">${schedule.title}</p>
                                                    <p class="info">
                                                        <i data-feather="calendar"></i>
                                                        <fmt:parseDate value="${schedule.scheduledDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date" />
                                                        <fmt:formatDate pattern="dd/MM/yyyy" value="${parsedDate}" />
                                                    </p>
                                                    <p class="info"><i data-feather="briefcase"></i> ${schedule.location}</p>
                                                    <p class="info"><i data-feather="users"></i> ${schedule.notes}</p>
                                                </div>
                                                <div class="card-actions">
                                                    <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                    <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                    <a href="#" class="delete-trigger" data-item-id="${schedule.id}" data-item-name="${schedule.title}" title="Xóa"><i data-feather="trash-2"></i></a>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="event-details" id="event-details-panel">
                            <div class="actions">
                                <a href="#" onclick="editEvent()" title="Sửa"><i class="bi bi-pencil" aria-label="Edit Icon"></i></a>
                                <a href="#" onclick="deleteEvent()" title="Xóa"><i class="bi bi-trash" aria-label="Delete Icon"></i></a>
                                <a href="#" onclick="closeDetails()" title="Đóng"><i class="bi bi-x-lg" aria-label="Close Icon"></i></a>
                            </div>
                            <div class="event-header">
                                <span class="dot"></span>
                                <span class="event-time-detail"></span>
                                <span class="event-title"></span>
                                <span class="event-type"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-hash" aria-label="ID Icon"></i> ID: <span class="event-id"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-link" aria-label="Link Icon"></i> Technical Request ID: <span class="event-technical-request-id"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-calendar" aria-label="Date Icon"></i> <span class="event-date"></span>
                            </div>
                            <div class="event-info">
                                <!-- Differentiated: Play icon for Start Time -->
                                <i class="bi bi-play-fill" aria-label="Start Time Icon"></i> <span class="event-time-start"></span>
                            </div>
                            <div class="event-info">
                                <!-- Differentiated: Stop icon for End Time -->
                                <i class="bi bi-stop-fill" aria-label="End Time Icon"></i> <span class="event-time-end"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-geo-alt" aria-label="Location Icon"></i> <span class="event-location"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-pencil-square" aria-label="Notes Icon"></i> <span class="event-notes"></span>
                            </div>
                            <div class="event-info">
                                <i class="bi bi-activity" aria-label="Status Icon"></i> Status:&nbsp; <span class="event-status"></span>
                            </div>
                            <div class="event-info">
                                <!-- Differentiated: Upload icon for Created At -->
                                <i class="bi bi-upload" aria-label="Created At Icon"></i>Created at:&nbsp; <span class="event-created-at"></span>
                            </div>
                            <div class="event-info">
                                <!-- Differentiated: Arrow-repeat icon for Updated At -->
                                <i class="bi bi-arrow-repeat" aria-label="Updated At Icon"></i> Updated at:&nbsp; <span class="event-updated-at"></span>
                            </div>
                        </div>

                        <div id="delete-confirm-modal" class="modal-overlay">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h3>Xác nhận xóa</h3>
                                    <button type="button" class="modal-close-btn"><i data-feather="x"></i></button>
                                </div>
                                <div class="modal-body">
                                    <i class="warning-icon" data-feather="alert-triangle" width="48" height="48"></i>
                                    <p>Bạn có chắc chắn muốn xóa công việc <br><strong id="item-name-to-delete"></strong>?</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-cancel">Hủy</button>
                                    <a href="#" id="confirm-delete-btn" class="btn btn-confirm-delete">Xóa</a>
                                </div>
                            </div>
                        </div>

                        <div id="add-schedule-modal" class="modal-overlay">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h3>Thêm lịch bảo trì</h3>
                                    <button type="button" class="modal-close-btn"><i data-feather="x"></i></button>
                                </div>
                                <div class="modal-body">
                                    <form id="add-schedule-form" action="addSchedule" method="post">
                                        <label for="title">Tiêu đề:</label>
                                        <input type="text" id="title" name="title" required>
                                        <label for="scheduled_date">Ngày bắt đầu (yyyy-MM-dd):</label>
                                        <input type="date" id="scheduled_date" name="scheduled_date" required>
                                        <label for="end_date">Ngày kết thúc (yyyy-MM-dd):</label>
                                        <input type="date" id="end_date" name="end_date">
                                        <label for="start_time">Giờ bắt đầu:</label>
                                        <input type="time" id="start_time" name="start_time">
                                        <label for="end_time">Giờ kết thúc:</label>
                                        <input type="time" id="end_time" name="end_time">
                                        <label for="location">Vị trí:</label>
                                        <input type="text" id="location" name="location" required>
                                        <label for="status">Trạng thái:</label>
                                        <select id="status" name="status" required>
                                            <option value="upcoming">Sắp tới</option>
                                            <option value="inprogress">Đang tiến hành</option>
                                            <option value="completed">Hoàn thành</option>
                                        </select>
                                        <label for="notes">Ghi chú:</label>
                                        <textarea id="notes" name="notes"></textarea>
                                        <input type="hidden" name="technicalRequestId" value="0">
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-cancel">Hủy</button>
                                    <button type="submit" form="add-schedule-form" class="btn btn-primary">Thêm</button>
                                </div>
                            </div>
                        </div>
                </section>
            </div>
        </div>

        <script>
            const currentView = '${viewMode}';
            const isoDayDate = '${isoDayDate}';
                    const weekDates = [<c:forEach items="${weekDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];
                    const monthDates = [<c:forEach items="${monthDates}" var="d" varStatus="status">'${d}'<c:if test="${!status.last}">,</c:if></c:forEach>];

            feather.replace();

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

            function updateScrollDirection(ev) {
                if (!scrollContainer)
                    return;
                const rect = scrollContainer.getBoundingClientRect();
                const edgeSize = 50;
                const maxSpeed = 20;
                const distTop = ev.clientY - rect.top;
                const distBottom = rect.bottom - ev.clientY;
                if (distTop < edgeSize) {
                    scrollSpeed = -Math.round(maxSpeed * (1 - distTop / edgeSize));
                } else if (distBottom < edgeSize) {
                    scrollSpeed = Math.round(maxSpeed * (1 - distBottom / edgeSize));
                } else {
                    scrollSpeed = 0;
                }
            }

            function stopAutoScroll() {
                scrollSpeed = 0;
                if (scrollInterval) {
                    clearInterval(scrollInterval);
                    scrollInterval = null;
                }
                document.removeEventListener('mousemove', updateScrollDirection);
                document.removeEventListener('dragend', stopAutoScroll);
                document.removeEventListener('drop', stopAutoScroll);
            }

            function drag(ev) {
                ev.dataTransfer.setData("text", ev.target.id);
                isInteracting = true;
                startAutoScroll();
                document.addEventListener('mousemove', updateScrollDirection);
                document.addEventListener('dragend', stopAutoScroll);
                document.addEventListener('drop', stopAutoScroll);
            }

            function allowDrop(ev) {
                ev.preventDefault();
            }

            function drop(ev) {
                ev.preventDefault();
                const data = ev.dataTransfer.getData("text");
                const eventElement = document.getElementById(data);
                if (!eventElement)
                    return;

                let slot = ev.target.closest('.time-slot, .all-day-slot, .all-day-event-container, .month-day');
                if (!slot)
                    return;
                const view = slot.closest('.calendar-view').id;

                let startCol, endCol;

                if (view === 'month-view') {
                    let tasksList = slot.querySelector('.tasks-list');
                    if (!tasksList) {
                        tasksList = document.createElement('div');
                        tasksList.classList.add('tasks-list');
                        slot.appendChild(tasksList);
                    }
                    tasksList.appendChild(eventElement);
                } else if (slot.classList.contains('all-day-slot') || slot.classList.contains('all-day-event-container')) {
                    eventElement.classList.add('all-day');
                    const timeText = eventElement.querySelector('.event-time');
                    if (timeText)
                        timeText.textContent = 'Cả ngày';

                    if (view === 'day-view') {
                        slot.appendChild(eventElement);
                        eventElement.style.gridColumn = '';
                        eventElement.style.gridRow = '';
                        eventElement.style.height = 'auto';
                        eventElement.style.top = '0';
                        const handle = eventElement.querySelector('.resize-handle');
                        if (handle)
                            handle.remove();
                    } else {
                        const container = document.querySelector('#week-view .all-day-event-container');
                        if (container) {
                            const rect = container.getBoundingClientRect();
                            const numDays = 7;
                            const dayWidth = rect.width / numDays;
                            const x = ev.clientX - rect.left;
                            startCol = Math.floor(x / dayWidth) + 1;

                            let span = 1;
                            if (eventElement.classList.contains('all-day') && eventElement.dataset.span) {
                                span = parseInt(eventElement.dataset.span);
                            }

                            endCol = startCol + span;
                            if (endCol > numDays + 1) {
                                endCol = numDays + 1;
                                startCol = endCol - span;
                            }
                            if (startCol < 1)
                                startCol = 1;

                            eventElement.style.gridColumn = startCol + ' / ' + endCol;
                            eventElement.dataset.startCol = startCol;
                            eventElement.dataset.span = endCol - startCol;

                            container.appendChild(eventElement);

                            if (!eventElement.querySelector('.resize-handle')) {
                                const handle = document.createElement('div');
                                handle.classList.add('resize-handle');
                                eventElement.appendChild(handle);
                                handle.addEventListener('mousedown', initResize);
                                handle.addEventListener('click', (e) => e.stopPropagation());
                            }
                        }
                    }
                } else {
                    slot.appendChild(eventElement);
                    eventElement.classList.remove('all-day');
                    eventElement.style.gridColumn = '';
                    eventElement.style.gridRow = '';
                    eventElement.style.height = 'auto';
                    eventElement.style.top = '0';
                    const time = slot.getAttribute('data-start-time');
                    const timeText = eventElement.querySelector('.event-time');
                    if (timeText && time)
                        timeText.textContent = time;
                    const handle = eventElement.querySelector('.resize-handle');
                    if (handle)
                        handle.remove();
                }

                // Cập nhật data backend sau khi drop
                const scheduleId = eventElement.id.split('-')[1];
                let newScheduledDate, newEndDate = null, newStartTime = null;

                if (view === 'day-view') {
                    newScheduledDate = slot.dataset.date;
                    newStartTime = slot.classList.contains('all-day-slot') ? null : slot.dataset.startTime;
                } else if (view === 'week-view') {
                    if (slot.classList.contains('all-day-event-container')) {
                        newScheduledDate = weekDates[startCol - 1];
                        newEndDate = (eventElement.dataset.span > 1) ? weekDates[endCol - 2] : null;
                        newStartTime = null;
                    } else {
                        newScheduledDate = slot.dataset.date;
                        newStartTime = slot.dataset.startTime;
                    }
                } else if (view === 'month-view') {
                    newScheduledDate = slot.dataset.date;
                    newStartTime = null;
                }

                if (scheduleId && newScheduledDate) {
                    updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime);
                }
            }

            function initResize(e) {
                e.preventDefault();
                isInteracting = true;
                const eventElement = e.target.parentElement;
                const container = eventElement.parentElement;
                const rect = container.getBoundingClientRect();
                const numDays = 7;
                const dayWidth = rect.width / numDays;
                let startCol = parseInt(eventElement.dataset.startCol) || 1;
                let currentSpan = parseInt(eventElement.dataset.span) || 1;

                function resize(e) {
                    const x = e.clientX - rect.left;
                    let newEndCol = Math.ceil(x / dayWidth) + 1;
                    let newSpan = newEndCol - startCol;
                    if (newSpan < 1)
                        newSpan = 1;
                    newEndCol = startCol + newSpan;
                    if (newEndCol > numDays + 1)
                        newEndCol = numDays + 1;
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
                    const newStartTime = null;
                    updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime);
                }

                window.addEventListener('mousemove', resize);
                window.addEventListener('mouseup', stopResize);
            }

            function updateEvent(id, scheduledDate, endDate, startTime) {
                fetch('updateSchedule', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({id: id, scheduledDate: scheduledDate, endDate: endDate, startTime: startTime}),
                }).then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Cập nhật thất bại!');
                    }
                }).catch(error => {
                    console.error('Error:', error);
                });
            }

            function showDetails(element) {
            if (isInteracting) return;
                    const detailsPanel = document.getElementById('event-details-panel');
                    if (!detailsPanel) {
            console.error('Event details panel not found');
                    return;
            }

            // Lấy scheduleId từ data-schedule-id hoặc id.split
            let scheduleId = element.dataset.scheduleId;
                    if (!scheduleId && element.id) {
            scheduleId = element.id.split('-')[1];
            }

            if (!scheduleId) {
            console.error('Schedule ID not found for element', element);
                    return;
            }

            // Tìm schedule
            const schedules = [
            <c:forEach var="schedule" items="${schedules}" varStatus="status">
            {
            id: ${schedule.id},
                    technicalRequestId: ${schedule.technicalRequestId},
                    title: "${schedule.title}",
                    scheduledDate: "${schedule.scheduledDate}",
                    endDate: "${schedule.endDate != null ? schedule.endDate : ''}",
                    startTime: "${schedule.startTime != null ? schedule.startTime : ''}",
                    endTime: "${schedule.endTime != null ? schedule.endTime : ''}",
                    location: "${schedule.location}",
                    status: "${schedule.status}",
                    notes: "${schedule.notes}",
                    createdAt: "${schedule.createdAt}",
                    updatedAt: "${schedule.updatedAt}"
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
            ];
                    const schedule = schedules.find(s => s.id == scheduleId);
                    if (schedule) {
            detailsPanel.querySelector('.event-id').textContent = schedule.id;
                    detailsPanel.querySelector('.event-technical-request-id').textContent = schedule.technicalRequestId || '0';
                    detailsPanel.querySelector('.event-time-detail').textContent = element.querySelector('.event-time')?.textContent || 'Cả ngày';
                    detailsPanel.querySelector('.event-title').textContent = schedule.title;
                    detailsPanel.querySelector('.event-type').textContent = schedule.status ? `(${schedule.status})` : '';
                    detailsPanel.querySelector('.event-date').textContent = schedule.scheduledDate + (schedule.endDate ? ' - ' + schedule.endDate : '');
                    detailsPanel.querySelector('.event-time-start').textContent = schedule.startTime;
                    detailsPanel.querySelector('.event-time-end').textContent = schedule.endTime;
                    detailsPanel.querySelector('.event-location').textContent = schedule.location || 'Không xác định';
                    detailsPanel.querySelector('.event-notes').textContent = schedule.notes || 'Không có ghi chú';
                    detailsPanel.querySelector('.event-status').textContent = schedule.status || 'Không xác định';
                    detailsPanel.querySelector('.event-created-at').textContent = schedule.createdAt || 'N/A';
                    detailsPanel.querySelector('.event-updated-at').textContent = schedule.updatedAt || 'N/A';
                    detailsPanel.classList.add('show');
            } else {
            console.error('Schedule not found for ID', scheduleId);
            }
            }

            function closeDetails() {
            const detailsPanel = document.getElementById('event-details-panel');
                    if (detailsPanel)
                    detailsPanel.classList.remove('show');
            }

            function editEvent() {
            alert('Edit event');
            }

            function deleteEvent() {
            alert('Delete event');
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
        </script>

        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>