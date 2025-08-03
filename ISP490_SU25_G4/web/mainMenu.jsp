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
            
            <%-- ===== NHÓM QUẢN TRỊ (CHỈ ADMIN THẤY) ===== --%>
            <c:if test="${sessionScope.userRole == 'Admin'}">
                <li>
                    <a href="${pageContext.request.contextPath}/listEmployee" class="${currentPage.contains('Employee') ? 'active' : ''}">
                        <i data-feather="briefcase"></i><span>Nhân viên</span>
                    </a>
                </li>
            </c:if>

            <%-- ===== NHÓM NGHIỆP VỤ ===== --%>
            <c:set var="isSupportSection" value="${currentPage.contains('ticket')}" />
            <li class="nav-item-dropdown ${isSupportSection ? 'open' : ''}">
                <a href="#" class="${isSupportSection ? 'active' : ''}">
                    <i data-feather="tool"></i><span>Hỗ trợ Kỹ thuật</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/ticket" class="${currentPage == 'ticket' ? 'active' : ''}">Danh sách Phiếu</a></li>
                    <li><a href="${pageContext.request.contextPath}/ticket?action=create" class="${currentPage == 'createTicket' ? 'active' : ''}">Tạo Phiếu mới</a></li>
                </ul>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/listFeedback" class="${currentPage.contains('Feedback') ? 'active' : ''}">
                    <i data-feather="message-square"></i><span>Phản hồi</span>
                </a>
            </li>
            <li class="nav-item ${currentPage == 'report' ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/report" class="${currentPage == 'report' ? 'active' : ''}">
                    <i data-feather="pie-chart"></i><span>Báo cáo</span>
                </a>
            </li>
            <li class="main-menu-item">
                <a href="${pageContext.request.contextPath}/list-campaign" class="main-menu-link">
                    <i data-feather="clipboard"></i>
                    <span>Chiến dịch</span>
                </a>
            </li>

            <%-- ===== CÁC MỤC TÀI KHOẢN VÀ ĐĂNG XUẤT ĐÃ ĐƯỢC XÓA BỎ ===== --%>
            
        </ul>
    </nav>

    <div class="sidebar-footer">
        <p>© 2025 DPCRM from ISP490_SU25_GR4</p>
    </div>
</aside>
