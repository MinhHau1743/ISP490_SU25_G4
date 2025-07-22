<%--
    Document   : mainMenu.jsp
    Description: Thanh điều hướng chính của ứng dụng (phiên bản tối ưu).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Thêm taglib functions --%>

<%-- 
    Lấy biến 'menuHighlight' từ các trang con (nếu có).
    Nếu không có, sẽ lấy tự động từ đường dẫn URL.
--%>
<c:if test="${empty menuHighlight}">
    <c:set var="rawPage" value="${pageContext.request.servletPath.substring(1)}" />
    <c:set var="menuHighlight" value="${fn:replace(rawPage, '.jsp', '')}" />
</c:if>

<aside class="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
        <button class="toggle-btn"><i data-feather="chevrons-left"></i></button>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <%-- ===== NHÓM CHỨC NĂNG CHÍNH ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/dashboard.jsp" class="${menuHighlight == 'dashboard' ? 'active' : ''}">
                    <i data-feather="grid"></i><span>Tổng quan</span>
                </a>
            </li>

            <%-- ===== NHÓM QUẢN LÝ ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/listCustomer" class="${fn:containsIgnoreCase(menuHighlight, 'Customer') ? 'active' : ''}">
                    <i data-feather="users"></i><span>Khách hàng</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/listContract" class="${fn:containsIgnoreCase(menuHighlight, 'Contract') ? 'active' : ''}">
                    <i data-feather="file-text"></i><span>Hợp đồng</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ProductController" class="${fn:containsIgnoreCase(menuHighlight, 'Product') ? 'active' : ''}">
                    <i data-feather="box"></i><span>Hàng hóa</span>
                </a>
            </li>

            <%-- ===== NHÓM QUẢN TRỊ (CHỈ ADMIN THẤY) ===== --%>
            <c:if test="${sessionScope.user.roleName == 'Admin'}">
                <li>
                    <%-- SỬA ĐỔI Ở ĐÂY: Kiểm tra biến 'menuHighlight' --%>
                    <a href="${pageContext.request.contextPath}/listEmployee" class="${fn:containsIgnoreCase(menuHighlight, 'Employee') ? 'active' : ''}">
                        <i data-feather="briefcase"></i><span>Nhân viên</span>
                    </a>
                </li>
            </c:if>

            <%-- ===== NHÓM NGHIỆP VỤ ===== --%>
            <c:set var="isSupportSection" value="${fn:containsIgnoreCase(menuHighlight, 'Ticket')}" />
            <li class="nav-item-dropdown ${isSupportSection ? 'open' : ''}">
                <a href="#" class="${isSupportSection ? 'active' : ''}">
                    <i data-feather="tool"></i><span>Hỗ trợ Kỹ thuật</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/listTicket" class="${menuHighlight == 'listTicket' ? 'active' : ''}">Danh sách Phiếu</a></li>
                    <li><a href="${pageContext.request.contextPath}/createTicket" class="${menuHighlight == 'createTicket' ? 'active' : ''}">Tạo Phiếu mới</a></li>
                </ul>
            </li>

            <c:set var="isReportSection" value="${fn:containsIgnoreCase(menuHighlight, 'Report')}" />
            <li class="nav-item-dropdown ${isReportSection ? 'open' : ''}">
                <a href="#" class="${isReportSection ? 'active' : ''}">
                    <i data-feather="pie-chart"></i><span>Báo cáo</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/dailyReport" class="${menuHighlight == 'dailyReport' ? 'active' : ''}">Báo cáo hàng ngày</a></li>
                    <li><a href="${pageContext.request.contextPath}/monthlyReport" class="${menuHighlight == 'monthlyReport' ? 'active' : ''}">Báo cáo hàng tháng</a></li>
                </ul>
            </li>

            <li class="main-menu-item">
                <a href="${pageContext.request.contextPath}/list-plan" class="main-menu-link">
                    <i data-feather="clipboard"></i>
                    <span>Kế hoạch</span>
                </a>
            </li>

            <%-- ===== NHÓM HỆ THỐNG & TÀI KHOẢN ===== --%>
            <c:set var="isProfileSection" value="${fn:containsIgnoreCase(menuHighlight, 'Profile') || fn:containsIgnoreCase(menuHighlight, 'Password')}" />
            <li class="nav-item-dropdown ${isProfileSection ? 'open' : ''}">
                <a href="#" class="${isProfileSection ? 'active' : ''}">
                    <i data-feather="settings"></i><span>Tài khoản</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewProfile" class="${menuHighlight == 'viewProfile' ? 'active' : ''}">Thông tin cá nhân</a></li>
                    <li><a href="${pageContext.request.contextPath}/changePassword" class="${menuHighlight == 'changePassword' ? 'active' : ''}">Đổi mật khẩu</a></li>
                </ul>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/logout">
                    <i data-feather="log-out"></i><span>Đăng xuất</span>
                </a>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <p>© 2025 DPCRM from ISP490_SU25_GR4</p>
    </div>
</aside>