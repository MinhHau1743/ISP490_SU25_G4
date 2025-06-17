<%-- 
    Document   : menu
    Created on : Jun 6, 2025, 1:54:15 PM
    Author     : NGUYEN MINH
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="rawPage" value="${pageContext.request.servletPath.substring(1)}" />
<c:set var="currentPage" value="${rawPage.replace('.jsp', '')}" />

<aside class="sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/image/logo.png" alt="logo" class="logo-img">
        <button class="toggle-btn"><i data-feather="chevrons-left"></i></button>
    </div>

    <nav class="sidebar-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/dashboardAdmin.jsp" class="${currentPage == 'dashboardAdmin' ? 'active' : ''}"><i data-feather="grid"></i><span>Tổng quan</span></a></li>

            <li><a href="${pageContext.request.contextPath}/customer.jsp" class="${currentPage == 'customer' ? 'active' : ''}"><i data-feather="users"></i><span>Khách hàng</span></a></li>

            <%-- Mục "Hàng hóa" (dropdown) 
            <c:set var="isProductSection" value="${currentPage == 'viewProducts' || currentPage == 'addProducts'}" />
            <li class="nav-item-dropdown ${isProductSection ? 'open' : ''}">
                <a href="#" class="${isProductSection ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewProducts.jsp" class="${currentPage == 'viewProducts' ? 'active' : ''}">Xem Hàng hóa</a></li>
                    <li><a href="${pageContext.request.contextPath}/addProducts.jsp" class="${currentPage == 'addProducts' ? 'active' : ''}">Thêm Hàng hóa</a></li>
                </ul>
            </li>--%>
            <li><a href="${pageContext.request.contextPath}/listProduct.jsp" class="${currentPage == 'listProduct' ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span></a></li>


            <c:set var="isTransactionSection" value="${currentPage == 'viewTransactions' || currentPage == 'addTransactions'}" />
            <li class="nav-item-dropdown ${isTransactionSection ? 'open' : ''}">
                <a href="#" class="${isTransactionSection ? 'active' : ''}"><i data-feather="repeat"></i><span>Giao dịch</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewTransactions.jsp" class="${currentPage == 'viewTransactions' ? 'active' : ''}">Xem Giao dịch</a></li>
                    <li><a href="${pageContext.request.contextPath}/addTransactions.jsp" class="${currentPage == 'addTransactions' ? 'active' : ''}">Tạo Giao dịch</a></li>
                </ul>
            </li>

            <li><a href="${pageContext.request.contextPath}/listEmployee.jsp" class="${currentPage == 'listEmployee' ? 'active' : ''}"><i data-feather="briefcase"></i><span>Nhân viên</span></a></li>

            <%-- Mục "Báo cáo" (dropdown) --%>
            <c:set var="isReportSection" value="${currentPage == 'dailyReport' || currentPage == 'monthlyReport'}" />
            <li class="nav-item-dropdown ${isReportSection ? 'open' : ''}">
                <a href="#" class="${isReportSection ? 'active' : ''}"><i data-feather="pie-chart"></i><span>Báo cáo</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/dailyReport.jsp" class="${currentPage == 'dailyReport' ? 'active' : ''}">Báo cáo hàng ngày</a></li>
                    <li><a href="${pageContext.request.contextPath}/monthlyReport.jsp" class="${currentPage == 'monthlyReport' ? 'active' : ''}">Báo cáo hàng tháng</a></li>
                </ul>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <p>© 2025 DPCRM from ISP490_SU25_GR4</p>
    </div>
</aside>