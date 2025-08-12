<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<header class="main-top-bar">
    <div class="page-title">${param.pageTitle}</div>

    <div class="header-actions">
        <%-- Nút Thông báo --%>
        <div class="notification-wrapper">
            <button class="notification-btn" type="button" aria-label="Notifications">
                <i data-feather="bell"></i>
                <%-- Bạn có thể thay số 4 này bằng một biến động, ví dụ ${unreadCount} --%>
                <span class="notification-badge">4</span>
            </button>

            <%-- Dropdown Thông báo --%>
            <div class="notification-dropdown">
                <%-- ===== BẮT ĐẦU NỘI DUNG THÔNG BÁO MẪU ===== --%>
                <div class="dropdown-header">
                    <h4>Thông báo</h4>
                    <a href="#" class="mark-as-read">Đánh dấu đã đọc</a>
                </div>
                <div class="notification-list">

                    <%-- Thông báo 1: Hợp đồng mới --%>
                    <a href="#" class="notification-item unread">
                        <div class="notification-icon" style="background-color: #e0f7fa; color: #00796b;">
                            <i data-feather="file-text"></i>
                        </div>
                        <div class="notification-content">
                            <p class="notification-title">Hợp đồng mới được ký kết</p>
                            <p class="notification-details">Hợp đồng HD-9543 với FPT Software đã được phê duyệt.</p>
                            <p class="notification-time">5 phút trước</p>
                        </div>
                    </a>

                    <%-- Thông báo 2: Yêu cầu kỹ thuật --%>
                    <a href="#" class="notification-item unread">
                        <div class="notification-icon" style="background-color: #fff3e0; color: #ff9800;">
                            <i data-feather="tool"></i>
                        </div>
                        <div class="notification-content">
                            <p class="notification-title">Yêu cầu hỗ trợ mới</p>
                            <p class="notification-details">Khách hàng Vingroup báo cáo sự cố máy phát điện.</p>
                            <p class="notification-time">2 giờ trước</p>
                        </div>
                    </a>

                    <%-- Thông báo 3: Lịch hẹn --%>
                    <a href="#" class="notification-item">
                        <div class="notification-icon" style="background-color: #e8eaf6; color: #3f51b5;">
                            <i data-feather="calendar"></i>
                        </div>
                        <div class="notification-content">
                            <p class="notification-title">Nhắc nhở: Lịch bảo trì</p>
                            <p class="notification-details">Bạn có lịch kiểm tra định kỳ tại Viettel vào ngày mai.</p>
                            <p class="notification-time">Hôm qua, lúc 15:30</p>
                        </div>
                    </a>

                    <%-- Thông báo 4: Cập nhật hệ thống --%>
                    <a href="#" class="notification-item">
                        <div class="notification-icon" style="background-color: #e3f2fd; color: #2196f3;">
                            <i data-feather="info"></i>
                        </div>
                        <div class="notification-content">
                            <p class="notification-title">Hệ thống sẽ bảo trì</p>
                            <p class="notification-details">Hệ thống sẽ tạm dừng để nâng cấp vào 23:00 tối nay.</p>
                            <p class="notification-time">2 ngày trước</p>
                        </div>
                    </a>

                </div>
                <div class="dropdown-footer">
                    <a href="#">Xem tất cả thông báo</a>
                </div>
                <%-- ===== KẾT THÚC NỘI DUNG THÔNG BÁO MẪU ===== --%>
            </div>
        </div>

        <%-- Menu Dropdown cho Người dùng --%>
        <div class="user-profile-dropdown">
            <button class="user-profile-button" type="button">
                <span class="user-avatar-small">
                    <c:if test="${not empty sessionScope.user.firstName}">
                        ${fn:substring(sessionScope.user.firstName, 0, 1)}
                    </c:if>
                </span>
            </button>

            <div class="dropdown-content">
                <div class="dropdown-header">
                    <div class="user-avatar-large">
                        <c:if test="${not empty sessionScope.user.firstName}">
                            ${fn:substring(sessionScope.user.firstName, 0, 1)}${fn:substring(sessionScope.user.lastName, 0, 1)}
                        </c:if>
                    </div>
                    <div class="user-info">
                        <div class="user-name-large"><c:out value="${sessionScope.user.firstName} ${sessionScope.user.lastName}"/></div>
                        <div class="user-email"><c:out value="${sessionScope.user.email}"/></div>
                        <a href="${pageContext.request.contextPath}/logout" class="sign-out-link">Đăng xuất</a>
                    </div>
                </div>

                <%-- ===== NHÓM THÔNG TIN CÁ NHÂN VÀ MẬT KHẨU ===== --%>
                <div class="dropdown-divider"></div>

                <%-- ## FIX: Sửa đường dẫn trỏ đến Controller /profile ## --%>
                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                    <i data-feather="user"></i>
                    <span>Thông tin cá nhân</span>
                </a>

                <a href="${pageContext.request.contextPath}/changePassword" class="dropdown-item">
                    <i data-feather="lock"></i>
                    <span>Đổi mật khẩu</span>
                </a>

                <%-- ===== NHÓM QUẢN TRỊ (CHỈ ADMIN THẤY) ===== --%>
                <c:if test="${sessionScope.user.roleName == 'Admin'}"> <%-- Sửa lại điều kiện cho an toàn hơn --%>
                    <div class="dropdown-divider"></div>
                    <div class="dropdown-section-title">Quản trị</div>
                    <a href="${pageContext.request.contextPath}/employee" class="dropdown-item">
                        <i data-feather="briefcase"></i>
                        <span>Nhân viên</span>
                    </a>
                </c:if>

            </div>
        </div>
    </div>
</header>
