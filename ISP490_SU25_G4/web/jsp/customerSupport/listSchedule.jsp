<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="currentPage" value="dashboard" />
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
                min-height: 100vh; /* Ensures it covers the full viewport height */
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
                padding: 10px 0; /* Add padding for spacing */
                border-bottom: 1px solid #ddd; /* Optional: subtle separation */
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

                /* Bổ sung dòng này để ::before định vị được */
                position: sticky; /* vẫn cần */
            }

            /* Thêm vào cuối file */
            .calendar-toolbar::before {
                content: "";
                position: absolute;
                top: -30px; /* Chiều cao nền cần lấp lên phía trên */
                left: 0;
                right: 0;
                height: 30px;
                background: #fff;
                z-index: -1; /* Đảm bảo nằm dưới nội dung chính */
            }

            /* Cần khai báo lại position: relative để pseudo-element hoạt động đúng */
            .calendar-toolbar {
                position: sticky;
            }


            /* Title styling (aligned with .page-header .title-section .title) */
            .calendar-toolbar .title {
                margin: 0;
                font-size: 20px; /* Matches the title font size */
                font-weight: 700; /* Matches the title font weight */
                color: var(--text-primary); /* Matches the title text color */
                margin-bottom: 8px; /* Matches the title margin-bottom */
            }

            /* View toggle buttons */
            .calendar-toolbar .view-toggle button {
                background: #eee;
                border: none;
                padding: 5px 10px;
                margin: 0 2px;
                border-radius: 8px; /* Increased for more rounded buttons */
                cursor: pointer;
                transition: background 0.3s;
                color: var(--text-primary); /* Align with title text color */
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
                color: var(--text-primary); /* Align with title text color */
            }

            .calendar-toolbar .week-nav .date-range {
                font-weight: bold;
                color: var(--text-primary); /* Align with title text color */
            }

            /* Toolbar spacer */
            .calendar-toolbar .toolbar-spacer {
                flex-grow: 1; /* Pushes the button to the right */
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

            /* Calendar container with rounded edges and white background */
            .calendar-content {
                display: flex;
                background: #fff;
                border-radius: 16px; /* Increased for more rounded edges */
                overflow: hidden; /* Ensures child elements respect rounded corners */
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Match toolbar shadow for consistency */
                width: 100%; /* Full width */
                box-sizing: border-box;
            }

            /* Individual calendar views with white background */
            #day-view, #week-view, #month-view, #list-view {
                background: #fff;
            }

            /* Time grid, month grid, and list grid with white background */
            .time-grid, .month-grid, .list-grid {
                background: #fff;
            }

            /* Time slots with rounded edges and white background */
            .time-slot {
                min-height: 60px; /* Minimum for empty slots */
                height: auto; /* Allow expansion for multiple events */
                border-bottom: 1px solid #eee;
                border-left: 1px solid #eee;
                border-radius: 8px; /* Added for rounded edges */
                position: relative;
                display: flex;
                flex-direction: column;
                padding: 5px;
                background: #fff;
            }

            /* All-day slots with rounded edges and white background */
            .all-day-slot {
                min-height: 40px;
                background: #fff; /* Changed from #f9f9f9 to match white background */
                border-bottom: 1px solid #ddd;
                border-left: 1px solid #eee;
                border-radius: 8px; /* Added for rounded edges */
                position: relative;
                display: flex;
                flex-direction: column;
                padding: 5px;
                height: auto;
            }

            /* All-day event container with rounded edges */
            .all-day-event-container {
                grid-row: 1;
                grid-column: 2 / -1;
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                min-height: 40px;
                height: auto;
                padding: 5px;
                background: #fff; /* Changed from #f9f9f9 to match white background */
                border-bottom: 1px solid #ddd;
                border-radius: 8px; /* Added for rounded edges */
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

            /* Events with more rounded edges */
            .event {
                position: relative;
                left: 0;
                right: 0;
                background: teal;
                color: white;
                padding: 5px;
                border-radius: 10px; /* Increased from 4px/8px for more rounded edges */
                font-size: 14px;
                cursor: pointer;
                top: 0;
                height: auto;
                margin: 5px;
                width: calc(100% - 10px);
                box-sizing: border-box;
            }

            /* All-day events with rounded edges */
            .event.all-day {
                position: relative;
                border-radius: 10px; /* Match .event for consistency */
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
            }

            /* Event details panel */
            .event-details {
                display: none;
                width: 350px;
                border-left: 1px solid #ddd;
                padding: 20px;
                background: #fff;
                box-shadow: -2px 0 5px rgba(0,0,0,0.1);
                position: sticky;
                top: 0;
                align-self: flex-start;
                height: fit-content;
                z-index: 10;
                border-radius: 12px; /* Added for rounded edges */
            }

            .event-details.show {
                display: block;
            }

            .event-details .header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            .event-details .actions {
                display: flex;
                gap: 10px;
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
                gap: 10px;
            }

            .event-details .guest {
                display: flex;
                align-items: center;
                background: #e6f7ff;
                padding: 5px 10px;
                border-radius: 20px; /* Already rounded, kept as is */
            }

            .event-details .guest img {
                width: 20px;
                height: 20px;
                border-radius: 50%;
                margin-right: 5px;
            }

            .event-details .event-description {
                margin-top: 20px;
                color: #333;
            }

            .event-details .dot {
                width: 10px;
                height: 10px;
                border-radius: 50%;
                background: purple;
                margin-right: 10px;
            }

            /* Remove margin for last event */
            .event:last-child {
                margin-bottom: 0;
            }

            /* Maintenance cards and task items with rounded edges */
            .maintenance-card, .task-item {
                background: #fff;
                border-radius: 12px; /* Increased for more rounded edges */
                cursor: pointer;
                margin-bottom: 10px;
                padding: 10px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow for depth */
            }

            /* Day header row */
            .day-header-row {
                display: grid;
                grid-template-columns: auto repeat(7, 1fr);
                text-align: center;
            }

            .day-header-cell {
                font-weight: bold;
                padding: 10px;
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
                border-radius: 8px; /* Added for rounded headers */
            }

            .month-day {
                position: relative;
                min-height: 100px;
                height: auto;
                border: 1px solid #ddd;
                padding-top: 25px;
                background: #fff;
                border-radius: 8px; /* Added for rounded month-day cells */
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
                border-radius: 10px; /* Increased for more rounded edges */
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
                border-radius: 10px; /* Match .event for consistency */
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
                            <button id="view-day-btn" class="btn-toggle active" data-view="day-view">Ngày</button>
                            <button id="view-week-btn" class="btn-toggle" data-view="week-view">Tuần</button>
                            <button id="view-month-btn" class="btn-toggle" data-view="month-view">Tháng</button>
                            <button id="view-list-btn" class="btn-toggle" data-view="list-view">Danh sách</button>
                        </div>
                        <!-- HIDDEN FORM TO SUBMIT NEXT/PREV DAY -->
                        <form id="dayNavForm" method="get" action="listSchedule">
                            <input type="hidden" name="controllerDay" id="controllerDay">
                            <input type="hidden" name="currentDay" id="currentDay">
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
                            <div id="day-view" class="calendar-view active">
                                <div class="day-nav">
                                    <span class="date">${dayDate}</span>
                                </div>
                                <div class="day-header"><h3>${dayHeader}</h3></div>
                                <div class="time-grid">
                                    <c:forEach var="label" items="${dayTimeLabels}" varStatus="status">
                                        <div class="time-label">${label}</div>
                                        <c:set var="startTime" value="${dayStartTimes[status.index]}"/>
                                        <div class="${startTime == 'all-day' ? 'time-slot all-day-slot' : 'time-slot'}"
                                             <c:if test="${startTime != 'all-day'}">data-start-time="${startTime}"</c:if>
                                                 ondragover="allowDrop(event)" ondrop="drop(event)">
                                             <c:if test="${startTime == '9:30'}">
                                                 <div class="event" id="event1" draggable="true" ondragstart="drag(event)" ondragover="allowDrop(event)" onclick="showDetails(this)">
                                                     <span class="event-time">9:30</span><br>Follow-up call with client
                                                 </div>
                                             </c:if>
                                             <c:if test="${startTime == '11:30'}">
                                                 <div class="event" id="event2" draggable="true" ondragstart="drag(event)" ondragover="allowDrop(event)" onclick="showDetails(this)">
                                                     <span class="event-time">11:30</span><br>Follow-up call with client
                                                 </div>
                                             </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <div id="week-view" class="calendar-view">
                                <div class="day-header-row">
                                    <div class="day-header-cell"></div>
                                    <c:forEach var="label" items="${dayHeaders}">
                                        <div class="day-header-cell">${label}</div>
                                    </c:forEach>
                                </div>

                                <div class="time-grid">
                                    <!-- All-day row -->
                                    <div class="time-label">all-day</div>
                                    <div class="all-day-event-container" ondragover="allowDrop(event)" ondrop="drop(event)">
                                        <!-- Events placed here with grid-column -->
                                        <c:forEach var="event" items="${allDayEvents}">
                                            <div class="event all-day" style="grid-column: ${event.startColumn} / ${event.endColumn};" draggable="true" ondragstart="drag(event)" onclick="showDetails(this)">
                                                ${event.title}
                                            </div>
                                        </c:forEach>
                                    </div>

                                    <!-- Time slots per hour -->
                                    <c:forEach var="hour" items="${hours}">
                                        <div class="time-label">${hour}</div>
                                        <c:forEach var="day" items="${days}">
                                            <div class="time-slot" data-start-time="${hour}" data-day="${day}" ondragover="allowDrop(event)" ondrop="drop(event)">
                                                <!-- Timed events -->
                                                <c:if test="${hour == '03:00' && day == 'fri'}">
                                                    <div class="event" id="grocery-event" draggable="true" ondragstart="drag(event)" ondragover="allowDrop(event)" onclick="showDetails(this)">
                                                        <span class="event-time">${hour}</span><br/>Grocery Day
                                                    </div>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </c:forEach>
                                </div>
                            </div>

                            <div id="month-view" class="calendar-view">
                                <div class="month-grid">
                                    <div class="month-grid-header">Thứ Hai</div><div class="month-grid-header">Thứ Ba</div><div class="month-grid-header">Thứ Tư</div><div class="month-grid-header">Thứ Năm</div><div class="month-grid-header">Thứ Sáu</div><div class="month-grid-header">Thứ Bảy</div><div class="month-grid-header">Chủ Nhật</div>
                                    <c:forEach var="dayNum" items="${dayNumbers}" varStatus="status">
                                        <div class="month-day ${isCurrentMonths[status.index] ? '' : 'other-month'}">
                                            <div class="day-number">${dayNum}</div>
                                            <c:if test="${dayNum == 16 && isCurrentMonths[status.index]}">
                                                <div class="tasks-list">
                                                    <div class="task-item status-inprogress" data-task-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Kiểm tra hệ thống PCCC" onclick="showDetails(this)">Kiểm tra PCCC</div>
                                                </div>
                                            </c:if>
                                            <c:if test="${dayNum == 17 && isCurrentMonths[status.index]}">
                                                <div class="tasks-list">
                                                    <div class="task-item status-upcoming" data-task-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Bảo trì điều hòa trung tâm" onclick="showDetails(this)">Bảo trì điều hòa</div>
                                                </div>
                                            </c:if>
                                            <c:if test="${dayNum == 19 && isCurrentMonths[status.index]}">
                                                <div class="tasks-list">
                                                    <div class="task-item status-completed" data-task-id="103" data-item-name="Bảo dưỡng thang máy" title="Bảo dưỡng thang máy" onclick="showDetails(this)">Bảo dưỡng thang máy</div>
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <div id="list-view" class="calendar-view">
                                <div class="list-grid">
                                    <h2>Tất cả lịch bảo trì</h2>
                                    <div class="maintenance-list">
                                        <div class="maintenance-card status-inprogress" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Kiểm tra hệ thống PCCC</p><p class="info"><i data-feather="calendar"></i> 16/07/2025</p><p class="info"><i data-feather="briefcase"></i> Tòa nhà Keangnam</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 1</p></div>
                                            <div class="card-actions">
                                                <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger" data-item-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                        <div class="maintenance-card status-upcoming" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Bảo trì điều hòa trung tâm</p><p class="info"><i data-feather="calendar"></i> 17/07/2025</p><p class="info"><i data-feather="briefcase"></i> Công ty An Phát</p><p class="info"><i data-feather="user"></i> Nguyễn Văn An</p></div>
                                            <div class="card-actions">
                                                <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger" data-item-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                        <div class="maintenance-card status-completed" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Bảo dưỡng thang máy</p><p class="info"><i data-feather="calendar"></i> 19/07/2025</p><p class="info"><i data-feather="briefcase"></i> Khách sạn Grand Plaza</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 2</p></div>
                                            <div class="card-actions">
                                                <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger" data-item-id="103" data-item-name="Bảo dưỡng thang máy" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="event-details" id="event-details-panel">
                            <div class="header">
                                <div>
                                    <span class="dot"></span>
                                    <span class="event-time-detail">9:30 AM - 8:00 PM</span>
                                    <span class="event-title">Awards Conference</span>
                                    <span class="event-type">Event</span>
                                </div>
                                <div class="actions">
                                    <a href="#" onclick="editEvent()" title="Sửa"><i data-feather="edit"></i></a>
                                    <a href="#" onclick="deleteEvent()" title="Xóa"><i data-feather="trash-2"></i></a>
                                    <a href="#" onclick="closeDetails()" title="Đóng"><i data-feather="x"></i></a>
                                </div>
                            </div>
                            <div class="event-info">
                                <i data-feather="calendar"></i> Aug 18, 2020 - Aug 19, 2020
                            </div>
                            <div class="event-info">
                                <i data-feather="clock"></i> 8:40 AM - 5:40 PM
                            </div>
                            <div class="event-info">
                                <i data-feather="map-pin"></i> Oslo, Canada
                            </div>
                            <div class="event-info">
                                <i data-feather="mail"></i> Meeting
                            </div>
                            <div class="event-info">
                                <i data-feather="eye"></i> Default Visibility
                            </div>
                            <div class="event-guests">
                                <i data-feather="users"></i> Guests:
                                <div class="guests-list">
                                    <div class="guest">Morgan Winston</div>
                                    <div class="guest">Charlie</div>
                                </div>
                            </div>
                            <div class="event-description">
                                <i data-feather="file-text"></i> Annual meeting with global branch teams & bosses about growth planning and fiscal year reports.
                            </div>
                        </div>
                    </div>

                    <div id="month-task-popover">
                        <a href="#" class="popover-action" id="popover-view"><i data-feather="eye"></i> Xem chi tiết</a>
                        <a href="#" class="popover-action" id="popover-edit"><i data-feather="edit-2"></i> Chỉnh sửa</a>
                        <a href="#" class="popover-action delete delete-trigger" id="popover-delete"><i data-feather="trash-2"></i> Xóa công việc</a>
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

                                    <label for="date">Ngày (dd/MM/yyyy):</label>
                                    <input type="date" id="date" name="date" required>

                                    <label for="start_time">Giờ bắt đầu:</label>
                                    <input type="time" id="start_time" name="start_time" required>

                                    <label for="end_time">Giờ kết thúc:</label>
                                    <input type="time" id="end_time" name="end_time" required>

                                    <label for="client">Khách hàng:</label>
                                    <input type="text" id="client" name="client" required>

                                    <label for="responsible">Người chịu trách nhiệm:</label>
                                    <input type="text" id="responsible" name="responsible" required>

                                    <label for="status">Trạng thái:</label>
                                    <select id="status" name="status" required>
                                        <option value="upcoming">Sắp tới</option>
                                        <option value="inprogress">Đang tiến hành</option>
                                        <option value="completed">Hoàn thành</option>
                                    </select>
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
            feather.replace();

            let isInteracting = false;

            function drag(ev) {
                ev.dataTransfer.setData("text", ev.target.id);
                isInteracting = true;
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
                        timeText.textContent = '';  // Remove time for all-day

                    if (view === 'day-view') {
                        // Append to slot for day view (no spanning)
                        slot.appendChild(eventElement);
                        eventElement.style.gridColumn = '';
                        eventElement.style.gridRow = '';
                        eventElement.style.height = 'auto';
                        eventElement.style.top = '0';
                        // Remove resize handle if present
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
                            let startCol = Math.floor(x / dayWidth) + 1;

                            let span = 1;
                            if (eventElement.classList.contains('all-day') && eventElement.dataset.span) {
                                span = parseInt(eventElement.dataset.span);
                            }

                            let endCol = startCol + span;
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

                            // Add resize handle if not present
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
                    // Normal time slot handling
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
                    // Remove resize handle if present
                    const handle = eventElement.querySelector('.resize-handle');
                    if (handle)
                        handle.remove();
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
                }

                window.addEventListener('mousemove', resize);
                window.addEventListener('mouseup', stopResize);
            }

            function showDetails(element) {
                if (isInteracting)
                    return;
                const detailsPanel = document.getElementById('event-details-panel');

                // (Optional) custom info filling
                const time = element.querySelector('.event-time')?.textContent || '';
                const title = element.querySelector('.title')?.textContent || element.textContent.trim();

                detailsPanel.querySelector('.event-time-detail').textContent = time;
                detailsPanel.querySelector('.event-title').textContent = title;

                detailsPanel.classList.add('show');
            }

            function closeDetails() {
                const detailsPanel = document.getElementById('event-details-panel');
                if (detailsPanel) {
                    detailsPanel.classList.remove('show');
                }
            }

            function editEvent() {
                alert('Edit event');
            }

            function deleteEvent() {
                alert('Delete event');
            }

            // View toggle functionality
            document.addEventListener('DOMContentLoaded', () => {
                // Xử lý khôi phục view từ localStorage
                const savedView = localStorage.getItem('selectedView') || 'day-view';

                document.querySelectorAll('.calendar-view').forEach(view => {
                    view.classList.remove('active');
                });

                const selectedViewElement = document.getElementById(savedView);
                if (selectedViewElement) {
                    selectedViewElement.classList.add('active');
                }
                document.querySelectorAll('.btn-toggle').forEach(button => {
                    button.classList.remove('active');
                    if (button.getAttribute('data-view') === savedView) {
                        button.classList.add('active');
                    }
                });
            });
// Gắn sự kiện lưu trạng thái
            document.querySelectorAll('.btn-toggle').forEach(button => {
                button.addEventListener('click', () => {
                    const viewId = button.getAttribute('data-view');
                    document.querySelectorAll('.calendar-view').forEach(view => view.classList.remove('active'));
                    document.getElementById(viewId).classList.add('active');
                    document.querySelectorAll('.btn-toggle').forEach(btn => btn.classList.remove('active'));
                    button.classList.add('active');
                    localStorage.setItem('selectedView', viewId);
                });
            });
            // Initialize resize handles for existing all-day events
            document.addEventListener('DOMContentLoaded', () => {
                document.querySelectorAll('#week-view .event.all-day').forEach(event => {
                    if (!event.querySelector('.resize-handle')) {
                        const handle = document.createElement('div');
                        handle.classList.add('resize-handle');
                        event.appendChild(handle);
                        handle.addEventListener('mousedown', initResize);
                        handle.addEventListener('click', (e) => e.stopPropagation());
                    }
                    // Set dataset if not present
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

                // Enable drag and drop for month view
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
            });
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("dayNavForm");
                const controllerDayInput = document.getElementById("controllerDay");
                const currentDayInput = document.getElementById("currentDay");

                // Lấy ngày hiện tại đang hiển thị từ một span (có thể gán thêm attribute data-date="2025-07-21")
                const currentDateElem = document.getElementById("currentDate");
                const currentFullDate = currentDateElem.getAttribute("data-date");  // bạn truyền từ backend ra dạng yyyy-MM-dd
                const realDate = parseDate(currentFullDate); // chuyển sang ISO nếu cần xử lý định dạng

                // Bắt sự kiện nút << (prev)
                document.getElementById("prevDayBtn").addEventListener("click", function () {
                    controllerDayInput.value = "prev";
                    currentDayInput.value = realDate;
                    form.submit();
                });

                // Bắt sự kiện nút >> (next)
                document.getElementById("nextDayBtn").addEventListener("click", function () {
                    controllerDayInput.value = "next";
                    currentDayInput.value = realDate;
                    form.submit();
                });

                // Hàm parse nếu cần (giả sử date hiển thị là "July 22, 2025")
                function parseDate(str) {
                    // Nếu bạn đã truyền đúng ISO từ backend thì chỉ cần return str
                    return str;
                }
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>