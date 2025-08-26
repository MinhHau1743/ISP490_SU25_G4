<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Lấy tên của trang hoặc servlet hiện tại để làm nổi bật menu (Phần này đã rất tốt) --%>
<c:set var="rawPage" value="${pageContext.request.servletPath.substring(1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<aside class="sidebar">
    <%-- PHẦN HEADER --%>
    <div class="sidebar-header">
        <div class="logo-wrapper">
            <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
            <span class="sidebar-title">DPCRM</span>
        </div>
      
    </div>
    <%-- PHẦN MENU ĐIỀU HƯỚNG --%>
    <nav class="sidebar-nav">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/dashboard" class="${currentPage == 'dashboard' ? 'active' : ''}">
                    <i data-feather="grid"></i><span>Tổng quan</span>
                </a>
            </li>

            <%-- Đoạn này đã đúng logic --%>
            <li>
                <a href="${pageContext.request.contextPath}/customer" class="${currentPage.contains('Customer') ? 'active' : ''}">
                    <i data-feather="users"></i><span>Khách hàng</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/contract" class="${currentPage.contains('Contract') ? 'active' : ''}">
                    <i data-feather="file-text"></i><span>Hợp đồng</span>
                </a>
            </li>
            <li>
                <%-- SỬA LẠI ĐIỀU KIỆN KIỂM TRA ĐỂ NHẬN DIỆN CẢ CHỮ HOA VÀ THƯỜNG --%>
                <a href="${pageContext.request.contextPath}/ticket" 
                   class="${currentPage.contains('Transaction') || currentPage.contains('Transaction') ? 'active' : ''}">
                    <i data-feather="tool"></i><span>Hỗ trợ Kỹ thuật</span>
                </a>
            </li>
            <li>
                <%-- 1. Thêm logic kiểm tra currentPage để gán class 'active' --%>
                <%-- 2. Gợi ý: Đổi icon thành 'send' hoặc 'megaphone' sẽ hợp với "Chiến dịch" hơn --%>
                <a href="${pageContext.request.contextPath}/list-campaign" class="${currentPage.contains('Campaign') || currentPage.contains('campaign') ? 'active' : ''}">
                    <i data-feather="send"></i>
                    <span>Chiến dịch</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/feedback" class="${currentPage.contains('Feedback') ? 'active' : ''}">
                    <i data-feather="message-square"></i><span>Phản hồi</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/schedule" class="${currentPage.contains('Schedule') ? 'active' : ''}">
                    <i data-feather="check-square"></i><span>Lịch bảo trì</span>
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/product" class="${currentPage.contains('Product') ? 'active' : ''}">
                    <i data-feather="box"></i><span>Hàng hóa</span>
                </a>
            </li>
            <%-- ===== NHÓM NGHIỆP VỤ ===== --%>
            <%-- Trong file mainMenu.jsp --%>
            <li>
                <a href="${pageContext.request.contextPath}/report" class="${currentPage == 'report' ? 'active' : ''}">
                    <i data-feather="pie-chart"></i><span>Báo cáo</span>
                </a>
            </li>

            <%-- ===== SỬA LỖI Ở ĐÂY ===== --%>

        </ul>
    </nav>
</aside>