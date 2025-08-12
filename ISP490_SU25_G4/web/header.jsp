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
                <span class="notification-badge">4</span>
            </button>

            <%-- Dropdown Thông báo (giữ nguyên) --%>
            <div class="notification-dropdown">
                <%-- ... nội dung dropdown thông báo của bạn ... --%>
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