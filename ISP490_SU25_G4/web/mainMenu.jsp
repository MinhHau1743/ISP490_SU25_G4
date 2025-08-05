<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Lấy tên của trang hoặc servlet hiện tại để làm nổi bật menu --%>
<c:set var="rawPage" value="${pageContext.request.servletPath.substring(1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<aside class="sidebar">
    <%-- PHẦN HEADER --%>
    <div class="sidebar-header">
        <div class="logo-wrapper">
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
            <span class="sidebar-title">DPCRM</span>
        </div>
        <%-- Nút ghim để thu/mở menu --%>
        <button class="pin-btn" title="Thu gọn menu">
            <i data-feather="unlock"></i>
        </button>
    </div>

    <%-- PHẦN MENU ĐIỀU HƯỚNG --%>
    <nav class="sidebar-nav">
        <ul>
            <%-- ===== NHÓM CHỨC NĂNG CHÍNH ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/dashboard" class="${currentPage == 'dashboard' ? 'active' : ''}">
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
            <li>
                <a href="${pageContext.request.contextPath}/listSchedule" class="${currentPage.contains('Schedule') ? 'active' : ''}">
                    <i data-feather="check-square"></i><span>Lịch bảo trì</span>
                </a>
            </li>

            <%-- ===== NHÓM NGHIỆP VỤ ===== --%>
            <li>
                <a href="${pageContext.request.contextPath}/ticket" class="${currentPage.contains('ticket') ? 'active' : ''}">
                    <i data-feather="tool"></i><span>Hỗ trợ Kỹ thuật</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/listFeedback" class="${currentPage.contains('Feedback') ? 'active' : ''}">
                    <i data-feather="message-square"></i><span>Phản hồi</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/report" class="${currentPage == 'report' ? 'active' : ''}">
                    <i data-feather="pie-chart"></i><span>Báo cáo</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/list-campaign" class="main-menu-link">
                    <i data-feather="clipboard"></i>
                    <span>Chiến dịch</span>
                </a>
            </li>
        </ul>
    </nav>
</aside>