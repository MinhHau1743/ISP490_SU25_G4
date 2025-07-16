<%-- 
    Document   : listSchedule
    Created on : Jul 16, 2025, 10:51:09 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="dashboard" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch bảo trì theo tuần - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        
        <link rel="stylesheet" href="../../css/style.css">
        <link rel="stylesheet" href="../../css/header.css">
        <link rel="stylesheet" href="../../css/mainMenu.css">
        <link rel="stylesheet" href="../../css/pagination.css">
        <link rel="stylesheet" href="../../css/listSchedule.css">
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
                            <button id="view-week-btn" class="btn-toggle active" data-view="week-view">Xem theo tuần</button>
                            <button id="view-month-btn" class="btn-toggle" data-view="month-view">Xem theo tháng</button>
                        </div>
                        <div class="week-nav">
                            <button class="btn-nav"><i data-feather="chevron-left"></i></button>
                            <span class="date-range">Tuần 25 (16/06 - 22/06/2025)</span>
                            <button class="btn-nav"><i data-feather="chevron-right"></i></button>
                        </div>
                        <div class="toolbar-spacer"></div>
                        <button class="btn-primary">
                            <i data-feather="plus"></i>
                            Lên lịch bảo trì
                        </button>
                    </div>

                    <div id="week-view" class="calendar-view active">
                        <div class="calendar-grid">
                            <div class="calendar-day">
                                <div class="day-header"><h3>Thứ Hai</h3><p>16/06</p></div>
                                <div class="day-body">
                                    <div class="maintenance-card status-inprogress">
                                        <div class="card-content"><p class="title">Kiểm tra hệ thống PCCC</p><p class="info"><i data-feather="briefcase"></i> Tòa nhà Keangnam</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 1</p></div>
                                        <div class="card-actions">
                                            <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                            <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                            <a href="#" class="delete-trigger" data-item-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Xóa"><i data-feather="trash-2"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="calendar-day">
                                <div class="day-header"><h3>Thứ Ba</h3><p>17/06</p></div>
                                <div class="day-body">
                                     <div class="maintenance-card status-upcoming">
                                        <div class="card-content"><p class="title">Bảo trì điều hòa trung tâm</p><p class="info"><i data-feather="briefcase"></i> Công ty An Phát</p><p class="info"><i data-feather="user"></i> Nguyễn Văn An</p></div>
                                        <div class="card-actions">
                                            <a href="#" title="Xem"><i data-feather="eye"></i></a>
                                            <a href="#" title="Sửa"><i data-feather="edit-2"></i></a>
                                            <a href="#" class="delete-trigger" data-item-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Xóa"><i data-feather="trash-2"></i></a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="calendar-day"><div class="day-header"><h3>Thứ Tư</h3><p>18/06</p></div><div class="day-body"></div></div>
                            <div class="calendar-day"><div class="day-header"><h3>Thứ Năm</h3><p>19/06</p></div><div class="day-body"><div class="maintenance-card status-completed"><div class="card-content"><p class="title">Bảo dưỡng thang máy</p><p class="info"><i data-feather="briefcase"></i> Khách sạn Grand Plaza</p><p class="info"><i data-feather="users"></i> Đội kỹ thuật số 2</p></div><div class="card-actions"><a href="#" title="Xem"><i data-feather="eye"></i></a><a href="#" title="Sửa"><i data-feather="edit-2"></i></a> <a href="#" class="delete-trigger" data-item-id="103" data-item-name="Bảo dưỡng thang máy" title="Xóa"><i data-feather="trash-2"></i></a></div></div></div></div>
                            <div class="calendar-day"><div class="day-header"><h3>Thứ Sáu</h3><p>20/06</p></div><div class="day-body"></div></div>
                            <div class="calendar-day"><div class="day-header"><h3>Thứ Bảy</h3><p>21/06</p></div><div class="day-body"></div></div>
                            <div class="calendar-day"><div class="day-header"><h3>Chủ Nhật</h3><p>22/06</p></div><div class="day-body"></div></div>
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
                            <div class="month-day"><div class="day-number">16</div><div class="tasks-list"><div class="task-item status-inprogress" data-task-id="101" data-item-name="Kiểm tra hệ thống PCCC" title="Kiểm tra hệ thống PCCC">Kiểm tra PCCC</div></div></div>
                            <div class="month-day"><div class="day-number">17</div><div class="tasks-list"><div class="task-item status-upcoming" data-task-id="102" data-item-name="Bảo trì điều hòa trung tâm" title="Bảo trì điều hòa trung tâm">Bảo trì điều hòa</div></div></div>
                            <div class="month-day"><div class="day-number">18</div></div>
                            <div class="month-day"><div class="day-number">19</div><div class="tasks-list"><div class="task-item status-completed" data-task-id="103" data-item-name="Bảo dưỡng thang máy" title="Bảo dưỡng thang máy">Bảo dưỡng thang máy</div></div></div>
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
                </section>
            </div>
        </div>

            <script src="../../js/listSchedule.js"></script>
        <script src="../../js/mainMenu.js"></script>
    </body>
</html>
