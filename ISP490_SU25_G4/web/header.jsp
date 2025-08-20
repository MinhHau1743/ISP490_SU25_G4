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
                <c:if test="${not empty notificationCount && notificationCount > 0}">
                    <span class="notification-badge">${notificationCount}</span>
                </c:if>
            </button>

            <%-- Dropdown Thông báo --%>
            <div class="notification-dropdown">
                <div class="dropdown-header">
                    <h4>Thông báo</h4>
                    <%-- <a href="#" class="mark-as-read">Đánh dấu đã đọc</a> --%>
                </div>
                <div class="notification-list">

                    <%-- Vòng lặp để hiển thị thông báo --%>
                    <c:forEach var="noti" items="${latestNotifications}">
                        <a href="${pageContext.request.contextPath}/${noti.linkUrl}" class="notification-item">

                            <%-- Chọn icon và màu sắc dựa trên loại thông báo --%>
                            <c:set var="iconBg" value="#e3f2fd"/>
                            <c:set var="iconColor" value="#2196f3"/>
                            <c:set var="iconName" value="info"/>

                            <c:if test="${noti.notificationType == 'ENTERPRISE'}">
                                <c:set var="iconBg" value="#e8eaf6"/>
                                <c:set var="iconColor" value="#3f51b5"/>
                                <c:set var="iconName" value="briefcase"/>
                            </c:if>
                            <c:if test="${noti.notificationType == 'CONTRACT'}">
                                <c:set var="iconBg" value="#e0f7fa"/>
                                <c:set var="iconColor" value="#00796b"/>
                                <c:set var="iconName" value="file-text"/>
                            </c:if>
                            <c:if test="${noti.notificationType == 'TECH_REQUEST'}">
                                <c:set var="iconBg" value="#fff3e0"/>
                                <c:set var="iconColor" value="#ff9800"/>
                                <c:set var="iconName" value="tool"/>
                            </c:if>
                            <c:if test="${noti.notificationType == 'CAMPAIGN'}">
                                <c:set var="iconBg" value="#ede7f6"/>
                                <c:set var="iconColor" value="#5e35b1"/>
                                <c:set var="iconName" value="volume-2"/>
                            </c:if>
                            <c:if test="${noti.notificationType == 'FEEDBACK'}">
                                <c:set var="iconBg" value="#e8f5e9"/>
                                <c:set var="iconColor" value="#4caf50"/>
                                <c:set var="iconName" value="message-square"/>
                            </c:if>

                            <div class="notification-icon" style="background-color: ${iconBg}; color: ${iconColor};">
                                <i data-feather="${iconName}"></i>
                            </div>
                            <div class="notification-content">
                                <p class="notification-title">${noti.title}</p>
                                <p class="notification-details">${noti.message}</p>
                                <p class="notification-time">${noti.relativeTime}</p>
                            </div>
                        </a>
                    </c:forEach>

                    <c:if test="${empty latestNotifications}">
                        <div class="notification-empty">
                            <i data-feather="check-circle"></i>
                            <p>Không có thông báo mới</p>
                        </div>
                    </c:if>

                </div>
                <%-- Sửa link ở footer của dropdown thông báo --%>
                <div class="dropdown-footer">
                    <a href="${pageContext.request.contextPath}/notifications">Xem tất cả thông báo</a>
                </div>
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
                        <a href="${pageContext.request.contextPath}/auth?action=logout" class="sign-out-link">Đăng xuất</a>
                    </div>
                </div>

                <div class="dropdown-divider"></div>

                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                    <i data-feather="user"></i>
                    <span>Thông tin cá nhân</span>
                </a>

                <a href="${pageContext.request.contextPath}/auth?action=changePassword" class="dropdown-item">
                    <i data-feather="lock"></i>
                    <span>Đổi mật khẩu</span>
                </a>

                <c:if test="${sessionScope.user.roleName == 'Admin'}">
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