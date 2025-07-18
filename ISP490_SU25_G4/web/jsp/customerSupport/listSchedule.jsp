<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
            .time-grid {
                display: grid;
                border-top: 1px solid #ddd;
                position: relative;
            }
            #day-view .time-grid {
                grid-template-columns: auto 1fr;
            }
            #week-view .time-grid {
                grid-template-columns: auto repeat(7, 1fr);
            }
            .time-label {
                text-align: right;
                padding-right: 10px;
                color: #666;
                font-size: 12px;
                height: 30px;
                height: 60px;
                line-height: 60px;
                border-bottom: 1px solid #eee;
            }
            .time-slot {
                height: 60px;
                border-bottom: 1px solid #eee;
                border-left: 1px solid #eee;
                position: relative;
            }
            .all-day-slot {
                height: 40px;
                background: #f9f9f9;
                border-bottom: 1px solid #ddd;
                position: relative;
                border-left: 1px solid #eee;
            }
            .event {
                position: absolute;
                left: 5px;
                right: 5px;
                background: teal;
                color: white;
                padding: 5px;
                border-radius: 4px;
                font-size: 14px;
                cursor: pointer;
                top: 5px;
                height: 50px;
            }
            .event.all-day {
                grid-row: 1; /* hàng đầu tiên */
                grid-column: 2 / span 7; /* span toàn bộ các cột ngày */
                position: relative;
                height: auto;
                top: 0;
                grid-column: auto;
            }
            .event-time {
                font-weight: bold;
            }
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
            .calendar-toolbar .view-toggle button {
                background: #eee;
                border: none;
                padding: 5px 10px;
                margin: 0 2px;
                border-radius: 4px;
            }
            .calendar-toolbar .view-toggle button.active {
                background: #ddd;
            }
            .calendar-content {
                display: flex;
            }
            .calendar-left {
                flex: 1;
                overflow-y: auto;
            }
            .event-details {
                width: 350px;
                border-left: 1px solid #ddd;
                padding: 20px;
                background: #fff;
                display: none;
                box-shadow: -2px 0 5px rgba(0,0,0,0.1);
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
                border-radius: 20px;
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
            .maintenance-card, .task-item {
                cursor: pointer;
            }
            .day-header-row {
                display: grid;
                grid-template-columns: auto repeat(7, 1fr);
                text-align: center;
            }
            .day-header-cell {
                font-weight: bold;
                padding: 10px;
            }
            .time-grid .all-day-slot + .all-day-slot {
                border-left: none;
            }
            #week-view .time-grid {
                display: grid;
                grid-template-columns: auto repeat(7, 1fr); /* auto: label | 7 days */
            }

        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="../../mainMenu.jsp"/>

            <div class="content-wrapper">
                <header class="main-top-bar">
                </header>

                <section class="main-content-body">
                    <div class="calendar-toolbar">
                        <h1 class="title">Lịch bảo trì</h1>
                        <div class="view-toggle">
                            <button id="view-day-btn" class="btn-toggle active" data-view="day-view">Day</button>
                            <button id="view-week-btn" class="btn-toggle" data-view="week-view">Week</button>
                            <button id="view-month-btn" class="btn-toggle" data-view="month-view">Month</button>
                            <button id="view-list-btn" class="btn-toggle" data-view="list-view">List</button>
                        </div>
                        <div class="week-nav">
                            <button class="btn-nav"><i data-feather="chevron-left"></i></button>
                            <span class="date-range">Today</span>
                            <button class="btn-nav"><i data-feather="chevron-right"></i></button>
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
                                    <span class="date">July 18, 2025</span>
                                </div>
                                <div class="day-header"><h3>FRIDAY</h3></div>
                                <div class="time-grid">
                                    <div class="time-label">all-day</div>
                                    <div class="time-slot all-day-slot" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">1:00 am</div>
                                    <div class="time-slot" data-start-time="1:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="1:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">2:00 am</div>
                                    <div class="time-slot" data-start-time="2:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="2:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">3:00 am</div>
                                    <div class="time-slot" data-start-time="3:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="3:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">4:00 am</div>
                                    <div class="time-slot" data-start-time="4:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="4:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">5:00 am</div>
                                    <div class="time-slot" data-start-time="5:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="5:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">6:00 am</div>
                                    <div class="time-slot" data-start-time="6:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="6:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">7:00 am</div>
                                    <div class="time-slot" data-start-time="7:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="7:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">8:00 am</div>
                                    <div class="time-slot" data-start-time="8:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="8:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">9:00 am</div>
                                    <div class="time-slot" data-start-time="9:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="9:30" ondragover="allowDrop(event)" ondrop="drop(event)">
                                        <div class="event" id="event1" draggable="true" ondragstart="drag(event)" onclick="showDetails(this)">
                                            <span class="event-time">9:30</span><br>Follow-up call with client
                                        </div>
                                    </div>
                                    <div class="time-label">10:00 am</div>
                                    <div class="time-slot" data-start-time="10:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="10:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">11:00 am</div>
                                    <div class="time-slot" data-start-time="11:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="11:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">12:00 pm</div>
                                    <div class="time-slot" data-start-time="12:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="12:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">1:00 pm</div>
                                    <div class="time-slot" data-start-time="1:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="1:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">2:00 pm</div>
                                    <div class="time-slot" data-start-time="2:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="2:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">3:00 pm</div>
                                    <div class="time-slot" data-start-time="3:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="3:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">4:00 pm</div>
                                    <div class="time-slot" data-start-time="4:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="4:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">5:00 pm</div>
                                    <div class="time-slot" data-start-time="5:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="5:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">6:00 pm</div>
                                    <div class="time-slot" data-start-time="6:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="6:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">7:00 pm</div>
                                    <div class="time-slot" data-start-time="7:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="7:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">8:00 pm</div>
                                    <div class="time-slot" data-start-time="8:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="8:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">9:00 pm</div>
                                    <div class="time-slot" data-start-time="9:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="9:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">10:00 pm</div>
                                    <div class="time-slot" data-start-time="10:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="10:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">11:00 pm</div>
                                    <div class="time-slot" data-start-time="11:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label"></div>
                                    <div class="time-slot" data-start-time="11:30" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    <div class="time-label">12:00 am</div>
                                    <div class="time-slot" data-start-time="12:00" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                </div>
                            </div>

                            <div id="week-view" class="calendar-view active">
                                <div class="day-header-row">
                                    <div class="day-header-cell"></div>
                                    <c:forEach var="label" items="${dayHeaders}">
                                        <div class="day-header-cell">${label}</div>
                                    </c:forEach>

                                </div>

                                <div class="time-grid">
                                    <!-- All-day row -->
                                    <!-- Dòng all-day -->
                                    <div class="time-label">all-day</div>
                                    <c:forEach var="day" items="${days}">
                                        <div class="time-slot all-day-slot" data-day="${day}" ondragover="allowDrop(event)" ondrop="drop(event)"></div>
                                    </c:forEach>

                                    <!-- Time slots per hour -->
                                    <c:forEach var="hour" items="${hours}">
                                        <div class="time-label">${hour}</div>
                                        <c:forEach var="day" items="${days}">
                                            <div class="time-slot" data-start-time="${hour}" data-day="${day}" ondragover="allowDrop(event)" ondrop="drop(event)">
                                                <%-- Nếu có event khớp giờ & ngày thì in ra tại đây --%>
                                                <c:if test="${hour == '03:00' && day == 'fri'}">
                                                    <div class="event" id="grocery-event" draggable="true" ondragstart="drag(event)" onclick="showDetails(this)">
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
                                    <div class="month-day other-month"><div class="day-number">26</div></div>
                                    <div class="month-day other-month"><div class="day-number">27</div></div>
                                    <div class="month-day other-month"><div class="day-number">28</div></div>
                                    <div class="month-day other-month"><div class="day-number">29</div></div>
                                    <div class="month-day other-month"><div class="day-number">30</div></div>
                                    <div class="month-day other-month"><div class="day-number">31</div></div>
                                    <div class="month-day"><div class="day-number">1</div></div>
                                    <div class="month-day"><div class="day-number">2</div></div>
                                    <div class="month-day"><div class="day-number">3</div></div>
                                    <div class="month-day"><div class="day-number">4</div></div>
                                    <div class="month-day"><div class="day-number">5</div></div>
                                    <div class="month-day"><div class="day-number">6</div></div>
                                    <div class="month-day"><div class="day-number">7</div></div>
                                    <div class="month-day"><div class="day-number">8</div></div>
                                    <div class="month-day"><div class="day-number">9</div></div>
                                    <div class="month-day"><div class="day-number">10</div></div>
                                    <div class="month-day"><div class="day-number">11</div></div>
                                    <div class="month-day"><div class="day-number">12</div></div>
                                    <div class="month-day"><div class="day-number">13</div></div>
                                    <div class="month-day"><div class="day-number">14</div></div>
                                    <div class="month-day"><div class="day-number">15</div></div>
                                    <div class="month-day"><div class="day-number">16</div><div class="tasks-list"><div class="task-item status-inprogress" data-task-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Kiểm tra hệ thống PCCC" onclick="showDetails(this)">Kiểm tra PCCC</div></div></div>
                                    <div class="month-day"><div class="day-number">17</div><div class="tasks-list"><div class="task-item status-upcoming" data-task-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Bảo trì điều hòa trung tâm" onclick="showDetails(this)">Bảo trì điều hòa</div></div></div>
                                    <div class="month-day"><div class="day-number">18</div></div>
                                    <div class="month-day"><div class="day-number">19</div><div class="tasks-list"><div class="task-item status-completed" data-task-id="103" data-item-name="Bảo dưỡng thang máy" title="Bảo dưỡng thang máy" onclick="showDetails(this)">Bảo dưỡng thang máy</div></div></div>
                                    <div class="month-day"><div class="day-number">20</div></div>
                                    <div class="month-day"><div class="day-number">21</div></div>
                                    <div class="month-day"><div class="day-number">22</div></div>
                                    <div class="month-day"><div class="day-number">23</div></div>
                                    <div class="month-day"><div class="day-number">24</div></div>
                                    <div class="month-day"><div class="day-number">25</div></div>
                                    <div class="month-day"><div class="day-number">26</div></div>
                                    <div class="month-day"><div class="day-number">27</div></div>
                                    <div class="month-day"><div class="day-number">28</div></div>
                                    <div class="month-day"><div class="day-number">29</div></div>
                                    <div class="month-day"><div class="day-number">30</div></div>
                                    <div class="month-day other-month"><div class="day-number">1</div></div>
                                </div>
                            </div>

                            <div id="list-view" class="calendar-view">
                                <div class="list-grid">
                                    <h2>Tất cả lịch bảo trì</h2>
                                    <div class="maintenance-list">
                                        <div class="maintenance-card status-inprogress" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Kiểm tra hệ thống PCCC</p><p class="info"><i data-feather="calendar"></i> 16/06/2025</p><p class="info"><i data-feather="briefcase"></i> Tòa nhà Keangnam</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 1</p></div>
                                            <div class="card-actions">
                                                <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger" data-item-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                        <div class="maintenance-card status-upcoming" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Bảo trì điều hòa trung tâm</p><p class="info"><i data-feather="calendar"></i> 17/06/2025</p><p class="info"><i data-feather="briefcase"></i> Công ty An Phát</p><p class="info"><i data-feather="user"></i> Nguyễn Văn An</p></div>
                                            <div class="card-actions">
                                                <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                                <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                                <a href="#" class="delete-trigger" data-item-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Xóa"><i data-feather="trash-2"></i></a>
                                            </div>
                                        </div>
                                        <div class="maintenance-card status-completed" onclick="showDetails(this)">
                                            <div class="card-content"><p class="title">Bảo dưỡng thang máy</p><p class="info"><i data-feather="calendar"></i> 19/06/2025</p><p class="info"><i data-feather="briefcase"></i> Khách sạn Grand Plaza</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 2</p></div>
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

            function drag(ev) {
                ev.dataTransfer.setData("text", ev.target.id);
            }

            function allowDrop(ev) {
                ev.preventDefault();
            }

            function drop(ev) {
                ev.preventDefault();
                const data = ev.dataTransfer.getData("text");
                const eventElement = document.getElementById(data);

                // Nếu drop vào all-day
                if (ev.target.classList.contains('all-day-slot')) {
                    const grid = ev.target.closest('.time-grid');
                    eventElement.classList.add('all-day');

                    // Append trực tiếp vào .time-grid, không phải .time-slot
                    grid.appendChild(eventElement);

                    // Kéo dài full hàng bằng grid-column
                    eventElement.style.gridColumn = '2 / span 7';
                    eventElement.style.gridRow = '1';
                    eventElement.style.top = '0';
                    eventElement.style.height = '40px';

                    const timeText = eventElement.querySelector('.event-time');
                    if (timeText)
                        timeText.textContent = ''; // không cần giờ
                } else {
                    // Trường hợp kéo vào time-slot bình thường
                    ev.target.appendChild(eventElement);
                    eventElement.classList.remove('all-day');
                    eventElement.style.gridColumn = '';
                    eventElement.style.gridRow = '';
                    eventElement.style.height = '50px';
                    eventElement.style.top = '5px';

                    const timeText = eventElement.querySelector('.event-time');
                    const time = ev.target.getAttribute('data-start-time');
                    if (timeText && time)
                        timeText.textContent = time;
                }
            }



            function showDetails(element) {
                var detailsPanel = document.getElementById('event-details-panel');
                var timeDetail = detailsPanel.querySelector('.event-time-detail');
                var time = element.querySelector('.event-time') ? element.querySelector('.event-time').textContent : '';
                var title = element.textContent.replace(time, '').trim() || 'Event';
                timeDetail.textContent = time ? time + ' AM - 8:00 PM' : '9:30 AM - 8:00 PM';
                detailsPanel.querySelector('.event-title').textContent = title;
                detailsPanel.classList.add('show');
            }

            function closeDetails() {
                document.getElementById('event-details-panel').classList.remove('show');
            }

            function editEvent() {
                alert('Edit event');
            }

            function deleteEvent() {
                alert('Delete event');
            }

            // View toggle functionality
            document.querySelectorAll('.btn-toggle').forEach(button => {
                button.addEventListener('click', () => {
                    document.querySelectorAll('.calendar-view').forEach(view => view.classList.remove('active'));
                    document.getElementById(button.getAttribute('data-view')).classList.add('active');
                    document.querySelectorAll('.btn-toggle').forEach(btn => btn.classList.remove('active'));
                    button.classList.add('active');
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>