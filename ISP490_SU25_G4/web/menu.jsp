<%-- 
    Document   : menu
    Created on : Jun 6, 2025, 1:54:15 PM
    Author     : minhnhn
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
            <li><a href="${pageContext.request.contextPath}/jsp/admin/dashboardAdmin.jsp" class="${currentPage == 'dashboardAdmin' ? 'active' : ''}"><i data-feather="grid"></i><span>Tổng quan</span></a></li>

            <li><a href="${pageContext.request.contextPath}/jsp/sales/listCustomer.jsp" class="${currentPage == 'listCustomer' ? 'active' : ''}"><i data-feather="users"></i><span>Khách hàng</span></a></li>
            <li><a href="${pageContext.request.contextPath}/jsp/chiefOfStaff/listContract.jsp" class="${currentPage == 'listContract' ? 'active' : ''}"><i data-feather="file-text"></i><span>Hợp đồng</span></a></li>

            <%-- Mục "Hàng hóa" (dropdown) 
            <c:set var="isProductSection" value="${currentPage == 'viewProducts' || currentPage == 'addProducts'}" />
            <li class="nav-item-dropdown ${isProductSection ? 'open' : ''}">
                <a href="#" class="${isProductSection ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/viewProducts.jsp" class="${currentPage == 'viewProducts' ? 'active' : ''}">Xem Hàng hóa</a></li>
                    <li><a href="${pageContext.request.contextPath}/addProducts.jsp" class="${currentPage == 'addProducts' ? 'active' : ''}">Thêm Hàng hóa</a></li>
                </ul>
            </li>--%>
            <li><a href="${pageContext.request.contextPath}/jsp/technicalSupport/listProduct.jsp" class="${currentPage == 'listProduct' ? 'active' : ''}"><i data-feather="box"></i><span>Hàng hóa</span></a></li>


            <c:set var="isTransactionSection" value="${currentPage == 'listTransaction' || currentPage == 'addTransaction'}" />
            <li class="nav-item-dropdown ${isTransactionSection ? 'open' : ''}">
                <a href="#" class="${isTransactionSection ? 'active' : ''}"><i data-feather="repeat"></i><span>Hóa đơn bảo trì</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/jsp/customerSupport/listTransaction.jsp" class="${currentPage == 'listTransaction' ? 'active' : ''}">Lịch sử</a></li>
                    <li><a href="${pageContext.request.contextPath}/jsp/customerSupport/addTransaction.jsp" class="${currentPage == 'addTransaction' ? 'active' : ''}">Tạo phiếu</a></li>
                </ul>
            </li>
            <c:set var="isEmployeeSection" value="${currentPage == 'listEmployeeCustomer' || currentPage == 'listEmployeeTechnical' || currentPage == 'listEmployeeAdmin'}" />
            <li class="nav-item-dropdown ${isEmployeeSection ? 'open active' : ''}">
                <a href="#" class="${isEmployeeSection ? 'active' : ''}"><i data-feather="briefcase"></i><span>Nhân viên</span><i data-feather="chevron-down" class="dropdown-icon"></i></a>
                <ul class="sub-menu">
                    <li><a href="${pageContext.request.contextPath}/jsp/admin/listEmployeeCustomer.jsp" class="${currentPage == 'listEmployeeCustomer' ? 'active' : ''}">Chăm sóc khách hàng</a></li>
                    <li><a href="${pageContext.request.contextPath}/jsp/admin/listEmployeeTechnical.jsp" class="${currentPage == 'listEmployeeTechnical' ? 'active' : ''}">Kỹ thuật</a></li>
                    <li><a href="${pageContext.request.contextPath}/jsp/admin/listEmployeeSales.jsp" class="${currentPage == 'listEmployeeSales' ? 'active' : ''}">Bộ phận kinh doanh</a></li>

                    <li><a href="${pageContext.request.contextPath}/jsp/admin/listEmployeeOffice.jsp" class="${currentPage == 'listEmployeeOffice' ? 'active' : ''}">Bộ phận văn phòng</a></li>
                </ul>
            </li>

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