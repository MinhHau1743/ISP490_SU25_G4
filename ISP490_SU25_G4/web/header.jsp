<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- 
    File: header.jsp
    Description: Thanh header trên cùng, bao gồm tiêu đề trang, menu thông báo và menu người dùng.
--%>

<header class="main-top-bar">
    <div class="page-title">${param.pageTitle}</div>
    
    <div class="header-actions">
        <%-- Nút Thông báo --%>
        <div class="notification-wrapper">
            <button class="notification-btn" type="button" aria-label="Notifications">
                <i data-feather="bell"></i>
                <span class="notification-badge">4</span>
            </button>
            
            <%-- Dropdown Thông báo --%>
            <div class="notification-dropdown">
                <div class="notification-header">
                    <h3 class="notification-title">Notifications</h3>
                    <%-- Nút cài đặt đã được xóa bỏ theo yêu cầu --%>
                </div>
                <ul class="notification-list">
                    <%-- Mẫu thông báo 1 --%>
                    <li class="notification-item">
                        <div class="notification-avatar user-avatar-img">
                            <img src="https://i.pravatar.cc/40?u=morgan" alt="Morgan Freeman">
                        </div>
                        <div class="notification-content">
                            <p class="notification-text">
                                <strong>Morgan Freeman</strong> accepted your invitation to join the team
                            </p>
                            <span class="notification-tag collaboration">Collaboration</span>
                            <span class="notification-time">Today, 10:14 PM</span>
                        </div>
                    </li>
                    <%-- Mẫu thông báo 2 --%>
                    <li class="notification-item">
                        <div class="notification-avatar icon-avatar message">
                            <i data-feather="message-square"></i>
                        </div>
                        <div class="notification-content">
                            <p class="notification-text">New message received from <strong>Alan Rickman</strong></p>
                            <span class="notification-time">Today, 7:51 AM</span>
                        </div>
                    </li>
                </ul>
                <div class="notification-footer">
                    <a href="#">View all notifications</a>
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
                        <a href="${pageContext.request.contextPath}/logout" class="sign-out-link">Đăng xuất</a>
                    </div>
                </div>

                <div class="dropdown-divider"></div>

                <a href="${pageContext.request.contextPath}/viewProfile" class="dropdown-item">
                    <i data-feather="user"></i>
                    <span>Thông tin cá nhân</span>
                </a>
                
                <div class="dropdown-divider"></div>

                <div class="dropdown-section-title">Quản lý tài khoản</div>
                <a href="${pageContext.request.contextPath}/changePassword" class="dropdown-item">
                    <i data-feather="lock"></i>
                    <span>Đổi mật khẩu</span>
                </a>
            </div>
        </div>
    </div>
</header>
