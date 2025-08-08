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
        <style>
            body {
                background: #fff;
                margin: 0;
                padding: 0;
            }
            .app-container, .main-panel, .content-wrapper, .main-content-body {
                display: flex;
                flex-direction: column;
                flex-grow: 1;
            }
            /* Điều chỉnh chiều rộng menu nếu cần */
            .main-content-body {
                padding: 24px;
            }

            /* File: /css/style.css */

            /* 1. Thiết lập container chính để sắp xếp menu và nội dung */
            /* File: /css/style.css */

            /* --- SỬA LỖI LAYOUT CHỒNG CHÉO --- */

            /* 1. Thiết lập container chính */
            .app-container {
                display: grid;
                grid-template-columns: var(--menu-width) 1fr;
                transition: grid-template-columns var(--transition-speed) ease-in-out;
            }

            /* 2. Style cho panel chính (chứa header và content) */
            .main-panel {
                /* Đẩy toàn bộ panel sang phải để chừa chỗ cho menu */
                /* *** ĐIỀU CHỈNH 250px cho khớp với chiều rộng menu của bạn *** */
                margin-left: 250px;

                /* Panel sẽ chiếm toàn bộ không gian còn lại */
                width: calc(100% - 250px);
                display: flex;
                flex-direction: column; /* Xếp header và content theo chiều dọc */
                min-height: 100vh;
                width: calc(100% - 250px);
            }

            /* 3. Đảm bảo content-wrapper không cần margin nữa */
            .content-wrapper {
                flex-grow: 1; /* Cho phép content-wrapper lấp đầy không gian dọc còn lại */
                padding: 20px;
                background-color: #f4f7fa; /* Màu nền cho đẹp hơn */
            }

            /* 4. (Khuyến nghị) Style cho mainMenu và header */
            /* Bạn nên đặt những style này vào file CSS tương ứng của chúng */

            /* File: /css/mainMenu.css */
            /* Giả sử menu của bạn có class="main-menu" */
            .main-menu {
                position: fixed; /* Cố định menu khi cuộn */
                top: 0;
                left: 0;
                width: 250px; /* *** PHẢI KHỚP với margin-left của .main-panel *** */
                height: 100%;
                z-index: 1001; /* Luôn ở trên cùng */
                background: #fff;
                box-shadow: 1px 0 5px rgba(0,0,0,0.1);
            }

            /* File: /css/header.css */
            /* Giả sử header của bạn có class="main-header" */
            .main-header {
                position: sticky; /* "Dính" lại ở trên cùng khi cuộn trong .main-panel */
                top: 0;
                z-index: 2;
                background: #fff;
                box-shadow: 0 1px 4px rgba(0,0,0,0.1);
            }

            /* Content wrapper with full-width white background */
            .content-wrapper {
                background: #fff;
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
                z-index: 2;
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
                height: auto;
                border-bottom: 1px solid #bbb;
                border-left: 1px solid #bbb;
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
                border-bottom: 1px solid #bbb;
                position: relative;
                display: flex;
                flex-direction: column;
                padding: 5px;
                height: auto;
                z-index: 2;
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
                border-bottom: 1px solid #bbb;
                border-radius: 8px;
                z-index: 1;
                position: relative;
            }

            /* Time grid setup */
            .time-grid {
                display: grid;
                /* Change #ddd to #bbb or #ccc */
                border-top: 1px solid #bbb;
                position: relative;
                grid-auto-flow: dense;
                background: #fff;
            }

            #day-view .time-grid {
                grid-template-columns: auto 1fr;
            }
            :root{
                --slot-h: 30px;          /* 1 time-slot cao 30 px */
            }
            #week-view .time-slot{
                height: var(--slot-h);
            }

            #week-view .time-grid {
                grid-template-columns: auto repeat(7, 1fr);
                grid-auto-rows: minmax(30px, auto);
                display: grid;
                grid-template-rows: repeat(48, var(--slot-h));
            }
            #week-view .event{
                height: calc(var(--slot-h) * 2 - 2px);   /* trừ 2 px để khỏi đè đường kẻ */
            }

            /* Thêm vào cuối khối <style> của bạn */
            .event.is-other-event {
                pointer-events: none; /* Làm cho element này bị con trỏ chuột "xuyên qua" */
            }

            /* Events */
            .event {
                position: absolute;
                flex: 1;
                min-width: 100px; /* Giảm chiều rộng tối thiểu */
                left: 10px;  /* Cách lề trái */
                right: 10px; /* Cách lề phải */
                z-index: 1;
                /* SỬA ĐỔI CHÍNH: Giảm các giá trị để event nhỏ hơn */
                min-height: 10px;      /* Chiều cao tối thiểu của event */
                padding: 2px 6px;      /* Giảm đệm trên-dưới và trái-phải */
                font-size: 15px;       /* Giảm cỡ chữ */

                max-width: calc(50% - 3px); /* Tối đa 2 events trên 1 hàng */
                margin: 0;
                box-sizing: border-box;
                height: fit-content;
                border-radius: 4px;
                overflow: hidden;
                color: white;
                cursor: pointer;
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
                height: 80vh;
                position: relative;
            }

            /* Event details panel */
            .event-details {
                display: none;
                width: 350px;
                border-left: 1px solid #bbb;
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
                border-bottom: 1px solid #bbb;
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
                border-bottom: 1px solid #bbb;
            }

            .day-header-cell {
                font-weight: bold;
                padding: 10px;
                border-right: 1px solid #bbb;
            }

            .day-header-row .day-header-cell:first-child {
                position: sticky;
                left: 0;
                background: #fff;
                z-index: 3;
                border-right: 1px solid #bbb;
            }
            .day-header-cell.today-highlight {
                background-color: #e0f2f1;
                border-bottom: 2px solid #008080;
                box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
            }

            /* Month grid */
            .month-grid {
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                grid-auto-rows: minmax(80px, auto);
                text-align: center;
                background: #fff;
            }

            .month-grid-header {
                font-weight: bold;
                border: 1px solid #bbb;
                padding: 5px;
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
                border-right: 1px solid #bbb;
                text-align: right;
                padding-right: 10px;
                color: #666;
                font-size: 12px;
                height: 60px;
                line-height: 60px;
                border-bottom: 1px solid #bbb;
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
            /* Chỉ áp dụng cho list-view, không ảnh hưởng event-details */
            #list-view .view-title {
                margin-bottom: 8px;
                font-size: 1.8em;
                color: #333;
            }
            #list-view .schedule-count {
                margin-bottom: 24px;
                color: #666;
                font-size: 1em;
            }

            /* Header cho mỗi nhóm ngày */
            .date-group-header {
                background-color: #f7f8fa; /* Màu nền xám nhạt */
                padding: 12px 16px;
                border-bottom: 1px solid #e9ecef;
                border-top: 1px solid #e9ecef;
            }

            .date-group-header .date-title {
                margin: 0;
                font-size: 1rem;
                font-weight: 600;
                color: #343a40;
            }

            /* Kiểu dáng cho mỗi dòng sự kiện */
            .event-item {
                display: flex; /* Quan trọng: Dùng Flexbox để xếp các cột theo hàng ngang */
                align-items: center; /* Căn giữa các mục theo chiều dọc */
                padding: 14px 16px;
                border-bottom: 1px solid #e9ecef; /* Đường kẻ phân cách giữa các sự kiện */
                cursor: pointer;
                transition: background-color 0.2s ease;
            }

            .event-item:hover {
                background-color: #f8f9fa; /* Hiệu ứng khi di chuột qua */
            }

            /* Cột 1: Cột thời gian */
            .event-item .event-time-col {
                flex-basis: 150px; /* Chiều rộng cố định cho cột thời gian */
                flex-shrink: 0; /* Ngăn cột này bị co lại */
                color: #555;
                font-size: 0.9rem;
                padding-right: 16px; /* Khoảng cách với cột tiếp theo */
            }

            /* Cột 2: Cột thông tin chi tiết */
            .event-item .event-details-col {
                flex-grow: 1; /* Quan trọng: Cho phép cột này chiếm hết không gian còn lại */
                display: flex;
                align-items: center;
            }

            /* Chấm màu */
            .event-details-col .event-color-dot {
                width: 10px;
                height: 10px;
                border-radius: 50%;
                margin-right: 12px;
                flex-shrink: 0;
            }

            /* Thông tin chính (tiêu đề...) */
            .event-details-col .event-info {
                display: flex;
                flex-direction: column; /* Sắp xếp tiêu đề và thông tin phụ (nếu có) theo chiều dọc */
            }

            .event-info .event-title {
                font-weight: 500;
                color: #212529;
                margin-top: 8px;
            }
            .modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.6);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
                opacity: 0;
                visibility: hidden;
                transition: opacity 0.3s, visibility 0.3s;
                pointer-events: none;
            }
            .modal-overlay.show {
                opacity: 1;
                visibility: visible;
                display: flex;
                pointer-events: auto;
            }
            .modal-overlay.show .modal-content {
                transform: scale(1);
            }
            .modal-content {
                background-color: white;
                border-radius: 12px;
                width: 420px;
                box-shadow: 0 5px 20px rgba(0,0,0,0.2);
                overflow: hidden;
                transform: scale(0.9);
                transition: transform 0.3s;
                position: relative;
                z-index: 10000;
                max-width: 90vw;
                max-height: 90vh;
            }
            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 20px 20px 0;
            }
            .modal-header .close-modal-btn {
                background: none;
                border: none;
                cursor: pointer;
                padding: 5px;
                color: #666;
            }
            .modal-header .close-modal-btn:hover {
                color: #000;
            }
            .modal-body {
                padding: 20px;
                text-align: center;
                min-height: 100px;
            }
            .modal-title {
                font-size: 18px;
                font-weight: 600;
                margin: 0;
            }
            .warning-icon {
                width: 50px;
                height: 50px;
                color: #f59e0b;
                margin-bottom: 15px;
            }
            .modal-footer {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                padding: 15px 20px;
                background-color: #f7fafc;
            }
            #add-schedule-btn {
                background-color: #e0f2f1 !important;
                color: #000;
                border: none;
                transition: background-color 0.3s ease;
            }

            #add-schedule-btn:hover {
                background-color: #b2dfdb !important; /* Màu xanh nhạt đậm hơn khi hover */
                color: #000; /* Giữ màu chữ hoặc đổi tùy ý */
                cursor: pointer;
            }
            /* --- CSS ĐỂ CHIA HÀNG "CẢ NGÀY" THÀNH 7 Ô --- */
            .all-day-row-container {
                grid-column: 2 / -1;
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                border-bottom: 1px solid #bbb;
            }

            .week-nav.hidden {
                display: none;
            }
            /* Thêm vào cuối file CSS của bạn */
            #day-view .time-slot {
                height: 30px;
                border-bottom: 1px solid #bbb;
                border-left: 1px solid #bbb;
                position: relative;
                display: flex;
                flex-direction: row; /* Thay đổi từ column thành row để xếp ngang */
                flex-wrap: wrap; /* Cho phép xuống hàng nếu cần */
                gap: 4px; /* Khoảng cách giữa các events */
                padding: 2px 0;
                background: #fff;
                justify-content: flex-start;
                align-items: flex-start;
            }
            #day-view .time-label {
                height: 30px;
                line-height: 30px;
            }
            /* All-day slot cũng cần cải thiện tương tự */
            #day-view .all-day-slot {
                min-height: 10px;
                border-bottom: 1px solid #bbb;
                border-left: 1px solid #bbb;
                position: relative;
                display: flex;
                flex-direction: row; /* Thay đổi từ column thành row để xếp ngang */
                gap: 5px; /* Khoảng cách giữa các events */
                padding: 5px;
                background: #fff;
                justify-content: flex-start;
                align-items: flex-start;
                flex-wrap: nowrap;
            }

            /* Events trong day-view cần được điều chỉnh */
            #day-view .event {
                position: relative;
                flex-grow: 1;
                flex-basis: 0;
                width: auto;
                max-width: none;
                min-height: 22px;
                margin: 0;
                padding: 2px 6px;
                font-size: 11px;
                border-radius: 4px;
                box-sizing: border-box;
            }

            #day-view .time-slot:has(.event):not(
            .time-slot:has(.event) + .time-label + .time-slot:has(.event)
            ) + .time-label + .time-slot:has(.event)::before {
                content: "";
                display: block;
                flex-grow: 1;
                flex-basis: 0;
            }
            /* Đặt z-index cho .event của đúng slot có ::before */
            #day-view .time-slot:has(.event):not(
            .time-slot:has(.event) + .time-label + .time-slot:has(.event)
            ) + .time-label + .time-slot:has(.event) .event {
                z-index: 3;
                position: relative; /* cần để z-index hoạt động */
            }

            /* All-day events trong day-view */
            #day-view .event.all-day {
                position: relative;
                border-radius: 8px;
                flex: 1;
                min-width: 120px;
                max-width: calc(50% - 2.5px);
                margin: 0;
                box-sizing: border-box;
                height: fit-content;
                min-height: 10px;
                padding: 1px;
            }

            /* Khi có nhiều hơn 2 events, cho phép xuống hàng */
            #day-view .time-slot:has(.event:nth-child(3)) .event,
            #day-view .all-day-slot:has(.event:nth-child(3)) .event {
                max-width: calc(33.333% - 3.33px); /* 3 events trên 1 hàng */
            }

            #day-view .time-slot:has(.event:nth-child(4)) .event,
            #day-view .all-day-slot:has(.event:nth-child(4)) .event {
                max-width: calc(25% - 3.75px); /* 4 events trên 1 hàng */
            }

            /* Responsive: trên màn hình nhỏ, cho phép events chiếm toàn bộ chiều rộng */
            @media (max-width: 768px) {
                #day-view .event,
                #day-view .event.all-day {
                    max-width: 100%;
                    min-width: 100%;
                }

                #day-view .time-slot,
                #day-view .all-day-slot {
                    flex-direction: column;
                }
            }

            /* Cải thiện hiển thị text trong event nhỏ */
            #day-view .event .event-time {
                font-weight: bold;
                font-size: 11px;
                display: block;
                margin-bottom: 2px;
            }

            #day-view .event-title-text {
                font-size: 11px;
                line-height: 1.2;
                overflow: hidden;
                text-overflow: ellipsis;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
            }

            /* Hover effect cho events */
            #day-view .event:hover {
                transform: scale(1.02);
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                z-index: 10;
                transition: all 0.2s ease;
            }

            /* Dragging state */
            #day-view .event[draggable="true"] {
                cursor: grab;
            }

            #day-view .event[draggable="true"]:active {
                cursor: grabbing;
                opacity: 0.8;
            }
            .time-slot.slot-active {
                border: 2px solid #007bff;
                background: #e3f7fa;
                transition: background 0.2s, border-color 0.2s;
            }
            .event-info {
                display: flex;
                align-items: flex-start;
                margin-bottom: 12px;
            }
            .event-info .bi-people {
                margin-right: 12px;
                margin-top: 5px;
                font-size: 1.1rem;
            }
            .event-assignments {
                display: flex;
                flex-wrap: wrap; /* Cho phép các thẻ tự động xuống dòng */
                gap: 8px; /* Tạo khoảng cách giữa các thẻ */
            }

            /* Kiểu dáng cho mỗi thẻ nhân viên */
            .employee-tag {
                display: inline-flex;
                align-items: center;
                background-color: #f0f2f5; /* Màu nền xám nhạt */
                border-radius: 16px; /* Bo tròn để tạo hình con nhộng (pill) */
                padding: 4px 10px;
                font-size: 0.9rem;
                color: #333;
                font-weight: 500;
                border: 1px solid #e0e0e0;
            }

            /* Kiểu dáng cho avatar */
            .employee-tag .avatar {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 24px;
                height: 24px;
                border-radius: 50%; /* Bo tròn thành hình tròn */
                background-color: #007bff; /* Màu nền cho avatar */
                color: #fff; /* Màu chữ cái */
                font-weight: bold;
                font-size: 0.75rem;
                margin-right: 8px; /* Khoảng cách giữa avatar và tên */
            }

            /* Kiểu dáng cho tên nhân viên */
            .employee-tag .employee-name {
                white-space: nowrap; /* Ngăn tên bị xuống dòng */
            }

            /* Thêm các màu nền khác nhau cho avatar để sinh động hơn */
            .employee-tag:nth-child(2) .avatar {
                background-color: #28a745;
            } /* Xanh lá */
            .employee-tag:nth-child(3) .avatar {
                background-color: #dc3545;
            } /* Đỏ */
            .employee-tag:nth-child(4) .avatar {
                background-color: #ffc107;
            } /* Vàng */
            /* Thêm vào file CSS của bạn */
            .date-range {
                cursor: pointer;
                text-decoration: underline;
                text-decoration-style: dotted;
                text-decoration-thickness: 1px;
            }

            .date-range:hover {
                color: #007bff; /* Thay đổi màu khi di chuột qua */
            }

        </style>
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
                            <form action="schedule" method="get">
                                <input type="hidden" name="action" value="createSchedule">
                                <button class="btn-primary" id="add-schedule-btn" style="background-color: #e0f2f1; color: #000; border: none;">
                                    <i data-feather="plus"></i>
                                    Lên lịch bảo trì
                                </button>
                            </form>
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
                                                     <c:if test="${schedule.scheduledDate.equals(today) 
                                                                   && (startTime == '' ? schedule.startTime == null : 
                                                                   (schedule.startTime != null && schedule.startTime.toString() == startTime))}">
                                                           <div class="event ${startTime == '' ? 'all-day' : ''}" id="event-${schedule.id}"
                                                                data-schedule-id="${schedule.id}" data-start-time="${schedule.startTime}" draggable="true"
                                                                ondragstart="drag(event)" onclick="showDetails(this)" style="background-color: ${schedule.color};">
                                                               <span class="event-time">${schedule.startTime != null ? schedule.startTime : 'Cả ngày'}</span>
                                                               <br>${schedule.title}
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
                                                        <c:if test="${schedule.scheduledDate.equals(weekDates[ds.index]) 
                                                                      && schedule.startTime != null 
                                                                      && schedule.startTime.toString() == hour}">
                                                              <div class="event"
                                                                   id="event-${schedule.id}"
                                                                   data-schedule-id="${schedule.id}"
                                                                   draggable="true"
                                                                   ondragstart="drag(event)"
                                                                   onclick="showDetails(this)"
                                                                   style="background-color: ${schedule.color};">
                                                                  <span class="event-time">${hour.substring(0,5)}</span>
                                                                  <br>${schedule.title}
                                                              </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>
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
                                                            <div class="task-item status-${fn:toLowerCase(schedule.status)}" data-task-id="${schedule.id}" data-schedule-id="${schedule.id}" data-item-name="${schedule.title}" title="${schedule.title}" onclick="showDetails(this)" draggable="true" ondragstart="drag(event)" style="background-color: ${schedule.color}; color: white;">
                                                                ${fn:substring(schedule.title, 0, 10)}...
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div id="list-view" class="calendar-view active">
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
                                    <a href="#"  title="Sửa"><i class="bi bi-pencil" aria-label="Edit Icon"></i></a>
                                    <a href="#" onclick="openDeleteModal(event)" title="Xóa"><i class="bi bi-trash" aria-label="Delete Icon"></i></a>
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
                                    <i class="bi bi-link" aria-label="Link Icon"></i> Technical Request ID: <span class="event-technical-request-id"></span>
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
                                    <i class="bi bi-activity" aria-label="Status Icon"></i> Status:  <span class="event-status"></span>
                                </div>
                                <div class="event-info">
                                    <!-- Differentiated: Upload icon for Created At -->
                                    <i class="bi bi-upload" aria-label="Created At Icon"></i>Created at:  <span class="event-created-at"></span>
                                </div>
                                <div class="event-info">
                                    <!-- Differentiated: Arrow-repeat icon for Updated At -->
                                    <i class="bi bi-arrow-repeat" aria-label="Updated At Icon"></i> Updated at:  <span class="event-updated-at"></span>
                                </div>
                            </div>

                            <div id="deleteConfirmModal" class="modal-overlay">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h3 class="modal-title">Xác nhận xóa</h3>
                                        <button class="close-modal-btn"><i data-feather="x"></i></button>
                                    </div>
                                    <div class="modal-body">
                                        <i data-feather="alert-triangle" class="warning-icon"></i>
                                        <p id="deleteMessage"></p>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" id="cancelDeleteBtn">Hủy</button>
                                        <button type="button" class="btn btn-danger" id="confirmDeleteBtn">Xóa</button>
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
            document.querySelectorAll('.event').forEach(ev => {
            ev.addEventListener('dragstart', function (e) {
            draggingEvent = ev;
            setTimeout(() => {
            ev.style.opacity = '0.5';
            }, 0);
            });
            ev.addEventListener('dragend', function (e) {
            draggingEvent = null;
            if (lastSlot)
                    lastSlot.classList.remove('slot-active');
            ev.style.opacity = '1';
            lastSlot = null;
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
            // phần còn lại làm trong hàm drop(e) gốc của bạn
            drop(e);
            });
            }

            document.querySelectorAll('.time-slot, .all-day-slot').forEach(bindSlotDnD);
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
                    technicalRequestId: ${schedule.technicalRequestId},
                    title: "${schedule.title}",
                    scheduledDate: "${schedule.scheduledDate}",
                    endDate: "${schedule.endDate != null ? schedule.endDate : ''}",
                    startTime: "${schedule.startTime != null ? schedule.startTime : ''}",
                    endTime: "${schedule.endTime != null ? schedule.endTime : ''}",
                    location: "${schedule.fullAddress != null ? fn:escapeXml(schedule.fullAddress.fullAddress) : 'Không xác định'}",
                    status: "${schedule.status}",
                    notes: "${schedule.notes}",
                    createdAt: "${schedule.createdAt}",
                    updatedAt: "${schedule.updatedAt}",
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
// ====================================================================

            var contextPath = window.location.pathname.split('/')[1] ? '/' + window.location.pathname.split('/')[1] : '';
            feather.replace();
// Gọi AJAX tới backend khi sự kiện drop hoàn tất
            function updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime) {
            const url = contextPath + '/schedule?action=updateScheduleTime';
            console.log('Updating event:', {
            id: scheduleId,
                    scheduledDate: newScheduledDate,
                    endDate: newEndDate,
                    startTime: newStartTime,
                    endTime: newEndTime
            });
            $.ajax({
            url: url,
                    type: 'POST',
                    contentType: 'application/json; charset=UTF-8',
                    dataType: 'json',
                    data: JSON.stringify({
                    id: parseInt(scheduleId, 10),
                            scheduledDate: newScheduledDate,
                            endDate: newEndDate || null,
                            startTime: newStartTime || null,
                            endTime: newEndTime || null  // BE sẽ tự tính nếu null & có startTime
                    }),
                    success: function (response) {
                    console.log('Update successful:', response);
                    showToast('Cập nhật lịch trình thành công!', 'success');
                    // Nếu muốn mượt: có thể cập nhật UI theo response.payload thay vì reload
                    setTimeout(() => { location.reload(); }, 800);
                    },
                    error: function (xhr) {
                    console.error('Update failed:', {status: xhr.status, text: xhr.responseText});
                    let errorMessage = 'Có lỗi khi cập nhật lịch trình!';
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

            function updateScrollDirection(ev) {
            if (!scrollContainer)
                    return;
            const rect = scrollContainer.getBoundingClientRect();
            const edgeSize = 50;
            const maxSpeed = 20;
            const distTop = ev.clientY - rect.top;
            const distBottom = rect.bottom - ev.clientY;
            if (distTop < edgeSize) {
            scrollSpeed = - Math.round(maxSpeed * (1 - distTop / edgeSize));
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

                function drag(ev) {
                ev.dataTransfer.setData("text/plain", ev.target.id);
                ev.dataTransfer.effectAllowed = "move";
                isInteracting = true;
                // Bắt đầu tự động cuộn
                startAutoScroll();
                document.addEventListener('dragover', updateScrollDirection);
                document.addEventListener('dragend', stopAutoScroll);
                // Tạm vô hiệu các event khác để tránh bắt nhầm
                const draggedId = ev.target.id;
                document.querySelectorAll('.event').forEach(event => {
                if (event.id !== draggedId) {
                event.classList.add('is-other-event');
                }
                });
                }


                function allowDrop(ev) {
                ev.preventDefault();
                }

                function drop(ev) {
                ev.preventDefault();
                const data = ev.dataTransfer.getData("text");
                let eventElement = document.getElementById(data);
                if (!eventElement) return;
                // Xoá bản sao dư nếu có
                document.querySelectorAll('#' + CSS.escape(data)).forEach((el) => {
                if (el !== eventElement && el.parentNode) {
                el.parentNode.removeChild(el);
                }
                });
                // Xác định slot mục tiêu
                let slot = ev.target.closest('.time-slot, .all-day-slot, .all-day-event-container, .month-day');
                if (!slot) return;
                // Chuyển DOM sang slot mới
                if (eventElement.parentNode) {
                eventElement.parentNode.removeChild(eventElement);
                }
                slot.appendChild(eventElement);
                const scheduleId = eventElement.id.split('-')[1];
                let newScheduledDate = null, newEndDate = null, newStartTime = null, newEndTime = null;
                // --- Tính ngày / giờ mới dựa trên loại slot ---
                const view = slot.closest('.calendar-view').id;
                if (slot.classList.contains('all-day-event-container') && view === 'week-view') {
                // Hàng all-day dạng grid 7 cột
                const rect = slot.getBoundingClientRect();
                const dayWidth = rect.width / 7;
                const x = ev.clientX - rect.left;
                let startCol = Math.floor(x / dayWidth) + 1;
                startCol = Math.max(1, Math.min(startCol, 7));
                eventElement.style.gridColumn = `${startCol} / span 1`;
                newScheduledDate = weekDates[startCol - 1];
                newStartTime = null;
                // ✅ Chuẩn hoá CSS: all-day
                if (!eventElement.classList.contains('all-day')) eventElement.classList.add('all-day');
                } else if (slot.classList.contains('all-day-slot')) {
                // All-day trong day/week view dạng slot riêng
                newScheduledDate = slot.dataset.date;
                newStartTime = null;
                // ✅ Chuẩn hoá CSS: all-day
                if (!eventElement.classList.contains('all-day')) eventElement.classList.add('all-day');
                } else if (slot.classList.contains('time-slot') && !slot.classList.contains('all-day-slot')) {
                // Slot giờ
                newScheduledDate = slot.dataset.date;
                newStartTime = slot.dataset.startTime;
                // ✅ Bỏ class all-day nếu đang có
                eventElement.classList.remove('all-day');
                } else {
                // Fallback: có date nhưng không xác định cụ thể
                newScheduledDate = slot.dataset.date || newScheduledDate;
                newStartTime = null;
                eventElement.classList.add('all-day');
                }

                // --- Cập nhật UI / model ---
                const scheduleToUpdate = schedules.find(s => s.id == scheduleId);
                if (newStartTime) {
                // Nếu có giờ, tính endTime tạm để hiển thị, còn BE sẽ chốt lại (2 slot)
                let durationMinutes = 0;
                if (scheduleToUpdate && scheduleToUpdate.startTime && scheduleToUpdate.endTime) {
                const [sh, sm] = scheduleToUpdate.startTime.split(':').map(Number);
                const [eh, em] = scheduleToUpdate.endTime.split(':').map(Number);
                durationMinutes = (eh * 60 + em) - (sh * 60 + sm);
                } else {
                durationMinutes = 30; // hiển thị tạm; BE sẽ trả về chính xác sau
                }

                let [nh, nm] = newStartTime.split(':').map(Number);
                let newEndTotalMin = nh * 60 + nm + durationMinutes;
                let newEndH = Math.floor(newEndTotalMin / 60) % 24;
                let newEndM = newEndTotalMin % 60;
                newEndTime = ("0" + newEndH).slice( - 2) + ":" + ("0" + newEndM).slice( - 2);
                const eventTimeElement = eventElement.querySelector('.event-time');
                if (eventTimeElement) {
                eventTimeElement.textContent = newStartTime + " - " + newEndTime;
                }

                if (scheduleToUpdate) {
                scheduleToUpdate.scheduledDate = newScheduledDate;
                scheduleToUpdate.startTime = newStartTime;
                scheduleToUpdate.endTime = newEndTime;
                // Nếu trước đó có multi-day thì ước lượng endDate theo chênh lệch cũ
                if (scheduleToUpdate.scheduledDate && scheduleToUpdate.endDate) {
                let date1 = new Date(scheduleToUpdate.scheduledDate);
                let date2 = new Date(scheduleToUpdate.endDate);
                let durationDate = Math.round((date2 - date1) / (24 * 60 * 60 * 1000));
                if (durationDate > 0) {
                let slotDate = new Date(newScheduledDate);
                slotDate.setDate(slotDate.getDate() + durationDate);
                newEndDate = slotDate.toISOString().split('T')[0];
                }
                } else {
                newEndDate = null;
                }
                scheduleToUpdate.endDate = newEndDate || "";
                scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');
                }

                } else {
                // All-day
                const eventTimeElement = eventElement.querySelector('.event-time');
                if (eventTimeElement) {
                eventTimeElement.textContent = 'Cả ngày';
                }
                if (scheduleToUpdate) {
                scheduleToUpdate.scheduledDate = newScheduledDate;
                scheduleToUpdate.startTime = null;
                scheduleToUpdate.endTime = null;
                scheduleToUpdate.endDate = "";
                scheduleToUpdate.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');
                }
                }

                // --- Gọi AJAX: để BE tự tính endTime = startTime + 2 slot ---
                if (typeof updateEvent === "function" && scheduleId && newScheduledDate) {
                // ✅ Truyền endTime = null → backend tự cộng 2 slot
                updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, null);
                }

                // Nếu panel chi tiết đang mở, làm mới
                const detailsPanel = document.getElementById('event-details-panel');
                if (detailsPanel && detailsPanel.classList.contains('show') &&
                        detailsPanel.querySelector('.event-id').textContent == scheduleId) {
                const updatedSchedule = schedules.find(s => s.id == scheduleId);
                if (updatedSchedule) showDetails(eventElement, updatedSchedule);
                }

                stopAutoScroll();
                setTimeout(setRightHalfForMiddleEvents, 0);
                }

                // Các hàm phụ cho kéo thả
                function startAutoScroll() {
                scrollContainer = document.querySelector('.calendar-left');
                if (!scrollContainer || scrollInterval)
                        return;
                scrollInterval = setInterval(() => {
                if (scrollSpeed !== 0) {
                scrollContainer.scrollTop += scrollSpeed;
                }
                }, 20);
                }

                function stopAutoScroll() {
                clearInterval(scrollInterval);
                scrollInterval = null;
                isInteracting = false;
                scrollSpeed = 0;
                document.removeEventListener('dragover', updateScrollDirection);
                document.removeEventListener('dragend', stopAutoScroll);
                // === SỬA LỖI: Kích hoạt lại tất cả các event ===
                document.querySelectorAll('.event').forEach(event => {
                event.classList.remove('is-other-event');
                });
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
                scrollSpeed = - Math.round(maxSpeed * (1 - distTop / edgeSize));
                } else if (distBottom < edgeSize) {
                scrollSpeed = Math.round(maxSpeed * (1 - distBottom / edgeSize));
                } else {
                scrollSpeed = 0;
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
                const newEndTime = null; // All-day nên endTime null
                updateEvent(scheduleId, newScheduledDate, newEndDate, newStartTime, newEndTime);
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
                detailsPanel.querySelector('.event-technical-request-id').textContent = schedule.technicalRequestId || '0';
                detailsPanel.querySelector('.event-title').textContent = schedule.title;
                detailsPanel.querySelector('.event-time-detail').textContent = schedule.startTime ? schedule.startTime : 'Cả ngày';
                detailsPanel.querySelector('.event-date').textContent = schedule.scheduledDate + (schedule.endDate ? ' - ' + schedule.endDate : '');
                detailsPanel.querySelector('.event-time-range').textContent = schedule.startTime ? schedule.startTime + (schedule.endTime ? ' - ' + schedule.endTime : '') : 'Cả ngày';
                detailsPanel.querySelector('.event-location').textContent = schedule.location || 'Không xác định';
                detailsPanel.querySelector('.event-notes').textContent = schedule.notes || 'Không có ghi chú';
                detailsPanel.querySelector('.event-status').textContent = schedule.status || 'Không xác định';
                detailsPanel.querySelector('.event-created-at').textContent = schedule.createdAt || 'N/A';
                detailsPanel.querySelector('.event-updated-at').textContent = schedule.updatedAt || 'N/A';
                // --- BẮT ĐẦU PHẦN CẬP NHẬT ĐỂ HIỂN THỊ THẺ NHÂN VIÊN ---

                const assignmentsContainer = detailsPanel.querySelector('.event-assignments');
                assignmentsContainer.innerHTML = ''; // Bắt đầu bằng việc xóa sạch nội dung cũ

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
                const editBtn = detailsPanel.querySelector('a[title="Sửa"]');
                editBtn.href = contextPath + '/schedule?action=updateSchedule&id=' + encodeURIComponent(schedule.id);
                // Hiển thị panel chi tiết
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
                function openDeleteModal(e) {
                e.preventDefault(); // Ngăn chặn hành vi mặc định của link
                const detailsPanel = document.getElementById('event-details-panel');
                const scheduleId = detailsPanel.querySelector('.event-id').textContent;
                const title = detailsPanel.querySelector('.event-title').textContent;
                const message = `Bạn có chắc chắn muốn xóa lịch bảo trì ?`;
                document.getElementById('deleteMessage').textContent = message;
                document.getElementById('deleteConfirmModal').classList.add('show');
                }

// Hàm đóng modal
                function closeDeleteModal() {
                document.getElementById('deleteConfirmModal').classList.remove('show');
                }

// Event listener cho nút close và hủy
                document.querySelector('.close-modal-btn').addEventListener('click', closeDeleteModal);
                document.getElementById('cancelDeleteBtn').addEventListener('click', closeDeleteModal);
// Event listener cho nút xác nhận xóa
                document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
                const detailsPanel = document.getElementById('event-details-panel');
                const scheduleId = detailsPanel.querySelector('.event-id').textContent;
                fetch('deleteSchedule', {
                method: 'POST',
                        headers: {
                        'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({id: scheduleId}),
                }).then(response => {
                if (response.ok) {
                location.reload(); // Reload trang để cập nhật danh sách
                } else {
                alert('Xóa thất bại!');
                }
                }).catch(error => {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi xóa!');
                });
                closeDeleteModal(); // Đóng modal ngay lập tức
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
        </script>
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
        <script src="${pageContext.request.contextPath}/js/listSchedule.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>










