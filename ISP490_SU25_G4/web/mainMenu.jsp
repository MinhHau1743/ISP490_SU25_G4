<%--
    Document    : mainMenu.jsp
    Description : Thanh điều hướng chính của ứng dụng (phiên bản tối ưu).
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Lấy tên của trang hoặc servlet hiện tại để làm nổi bật menu --%>
<c:set var="rawPage" value="${pageContext.request.servletPath.substring(1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<aside class="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
        <button class="toggle-btn"><i data-feather="chevrons-left"></i></button>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <%-- ===== NHÓM CHỨC NĂNG CHÍNH ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/dashboard.jsp" class="${currentPage == 'dashboard' ? 'active' : ''}">
                    <i data-feather="grid"></i><span>Tổng quan</span>
                </a>
            </li>

            <%-- ===== NHÓM QUẢN LÝ ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/listCustomer" class="${currentPage.contains('Customer') ? 'active' : ''}">
                    <i data-feather="users"></i><span>Khách hàng</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/listContract" class="${currentPage.contains('Contract') ? 'active' : ''}">
                    <i data-feather="file-text"></i><span>Hợp đồng</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/ProductController" class="${currentPage.contains('Product') ? 'active' : ''}">
                    <i data-feather="box"></i><span>Hàng hóa</span>
                </a>
            </li>

            <%-- ===== NHÓM QUẢN TRỊ (CHỈ ADMIN THẤY) ===== --%>
            <c:if test="${sessionScope.userRole == 'Admin'}">
                <li>
                    <%-- Đảm bảo đường dẫn ở đây là "/listEmployee" --%>
                    <a href="${pageContext.request.contextPath}/listEmployee" class="${currentPage.contains('Employee') ? 'active' : ''}">
                        <i data-feather="briefcase"></i><span>Nhân viên</span>
                    </a>
                </li>
            </c:if>

            <%-- ===== NHÓM NGHIỆP VỤ ===== --%>
            <c:set var="isSupportSection" value="${currentPage == 'listTicket' || currentPage == 'createTicket'}" />
            <li class="nav-item-dropdown ${isSupportSection ? 'open' : ''}">
                <a href="#" class="${isSupportSection ? 'active' : ''}">
                    <i data-feather="tool"></i><span>Hỗ trợ Kỹ thuật</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/listTicket" class="${currentPage == 'listTicket' ? 'active' : ''}">Danh sách Phiếu</a></li>
                    <li><a href="${pageContext.request.contextPath}/createTicket" class="${currentPage == 'createTicket' ? 'active' : ''}">Tạo Phiếu mới</a></li>
                </ul>
            </li>

            <c:set var="isReportSection" value="${currentPage == 'dailyReport' || currentPage == 'monthlyReport'}" />
            <li class="nav-item-dropdown ${isReportSection ? 'open' : ''}">
                <a href="#" class="${isReportSection ? 'active' : ''}">
                    <i data-feather="pie-chart"></i><span>Báo cáo</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/dailyReport" class="${currentPage == 'dailyReport' ? 'active' : ''}">Báo cáo hàng ngày</a></li>
                    <li><a href="${pageContext.request.contextPath}/monthlyReport" class="${currentPage == 'monthlyReport' ? 'active' : ''}">Báo cáo hàng tháng</a></li>
                </ul>
            </li>

            <%-- ===== NHÓM HỆ THỐNG & TÀI KHOẢN ===== --%>
            <c:set var="isProfileSection" value="${currentPage == 'viewProfile' || currentPage == 'changePassword'}" />
            <li class="nav-item-dropdown ${isProfileSection ? 'open' : ''}">
                <a href="#" class="${isProfileSection ? 'active' : ''}">
                    <i data-feather="settings"></i><span>Tài khoản</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewProfile" class="${currentPage == 'viewProfile' ? 'active' : ''}">Thông tin cá nhân</a></li>
                    <li><a href="${pageContext.request.contextPath}/changePassword" class="${currentPage == 'changePassword' ? 'active' : ''}">Đổi mật khẩu</a></li>
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