<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="rawPage" value="${pageContext.request.requestURI.substring(pageContext.request.requestURI.lastIndexOf('/') + 1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<aside class="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
        <button class="toggle-btn"><i data-feather="chevrons-left"></i></button>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <%-- Mục "Tổng quan" --%>
            <li><a href="${pageContext.request.contextPath}/dashboard.jsp" class="${currentPage == 'dashboard' ? 'active' : ''}"><i data-feather="grid"></i><span>Tổng quan</span></a></li>

            <%-- Mục "Khách hàng" --%>
            <li><a href="${pageContext.request.contextPath}/listCustomer" class="${currentPage == 'listCustomer' ? 'active' : ''}"><i data-feather="user"></i><span>Khách hàng</span></a></li>
            <li><a href="${pageContext.request.contextPath}/listContract" class="${currentPage == 'listContract' ? 'active' : ''}"><i data-feather="file-text"></i><span>Hợp đồng</span></a></li>

            <%-- Mục "Hàng hóa" (dropdown) 
            <c:set var="isProductSection" value="${currentPage == 'viewProducts' || currentPage == 'addProducts'}" />
            <li class="nav-item-dropdown ${isProductSection ? 'open' : ''}">
                <a href="#" class="${isProductSection ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewProducts.jsp" class="${currentPage == 'viewProducts' ? 'active' : ''}">Xem Hàng hóa</a></li>
                    <li><a href="${pageContext.request.contextPath}/addProducts.jsp" class="${currentPage == 'addProducts' ? 'active' : ''}">Xem tồn kho</a></li>
                </ul>
            </li>--%>
            <li><a href="${pageContext.request.contextPath}/ProductController" class="${currentPage == 'listProduct' ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span></a></li>


            <%-- Mục "Hóa đơn" (dropdown) --%>
            <c:set var="isTransactionSection" value="${currentPage == 'listTransaction' || currentPage == 'addTransaction'}" />
            <li class="nav-item-dropdown ${isTransactionSection ? 'open' : ''}">
                <a href="#" class="${isTransactionSection ? 'active' : ''}"><i data-feather="repeat"></i><span>Hóa đơn bảo trì</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/jsp/customerSupport/listTransaction.jsp" class="${currentPage == 'listTransaction' ? 'active' : ''}">Lịch sử</a></li>
                    <li><a href="${pageContext.request.contextPath}/jsp/customerSupport/createTicket.jsp" class="${currentPage == 'createTicket' ? 'active' : ''}">Tạo phiếu</a></li>
                </ul>
            </li>

            <%-- Mục "Thông tin cá nhân" (dropdown) --%>
            <c:set var="isProfileSection" value="${currentPage == 'viewProfile' || currentPage == 'changePassword'}" />
            <li class="nav-item-dropdown ${isProfileSection ? 'open' : ''}">
                <a href="#" class="${isProfileSection ? 'active' : ''}">
                    <i data-feather="users"></i><span>Thông tin cá nhân</span><i data-feather="chevron-down" class="dropdown-icon"></i>
                </a>

                <ul class="sub-menu">
                    <li>
                        <a href="${pageContext.request.contextPath}/viewProfile" class="${currentPage == 'viewProfile' ? 'active' : ''}">
                            Xem thông tin cá nhân
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/changePassword.jsp" class="${currentPage == 'changePassword' ? 'active' : ''}">Đổi mật khẩu</a>
                    </li>

                </ul>
            </li>

            <%-- Mục "Báo cáo" (dropdown) --%>
            <c:set var="isReportSection" value="${currentPage == 'dailyReport' || currentPage == 'monthlyReport'}" />
            <li class="nav-item-dropdown ${isReportSection ? 'open' : ''}">
                <a href="#" class="${isReportSection ? 'active' : ''}"><i data-feather="pie-chart"></i><span>Báo cáo</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/jsp/dailyReport.jsp" class="${currentPage == 'dailyReport' ? 'active' : ''}">Báo cáo hàng ngày</a></li>
                    <li><a href="${pageContext.request.contextPath}/jsp/monthlyReport.jsp" class="${currentPage == 'monthlyReport' ? 'active' : ''}">Báo cáo hàng tháng</a></li>
                </ul>
            </li>
            <li><a href="${pageContext.request.contextPath}/jsp/logout.jsp" class="${currentPage == 'logout' ? 'active' : ''}"><i data-feather="user"></i><span>Đăng xuất</span></a></li>
        </ul>
    </nav>
    
    <div class="sidebar-footer">
        <p>© 2025 DPCRM from ISP490_SU25_GR4</p>
    </div>
</aside>