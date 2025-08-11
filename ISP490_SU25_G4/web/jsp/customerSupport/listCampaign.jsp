<%--
    Document   : listCampaign.jsp
    Description: Trang quản lý danh sách chiến dịch, phiên bản hoàn chỉnh với bộ lọc.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="campaigns" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Chiến dịch</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCampaign.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Quản lý Chiến dịch"/>
                </jsp:include>

                <div class="page-content">

                    <section class="stats-grid">
                        <div class="stat-card">
                            <div class="icon-container icon-yellow"><i data-feather="mail"></i></div>
                            <div class="info">
                                <div class="title">Tổng chiến dịch</div>
                                <div class="value"><fmt:formatNumber value="${totalCampaigns}" type="number"/></div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-red"><i data-feather="play-circle"></i></div>
                            <div class="info">
                                <div class="title">Đang chạy</div>
                                <div class="value"><fmt:formatNumber value="${activeCampaigns}" type="number"/></div>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-green"><i data-feather="check-circle"></i></div>
                            <div class="info">
                                <div class="title">Hoàn thành</div>
                                <div class="value"><fmt:formatNumber value="${completedCampaigns}" type="number"/></div>
                            </div>
                        </div>
                    </section>

                    <form action="${BASE_URL}/campaign" method="GET">
                        <input type="hidden" name="action" value="list"/>
                        <section class="campaign-toolbar">
                            <div class="toolbar-top">
                                <div class="search-bar">
                                    <i data-feather="search"></i>
                                    <input type="text" name="search" placeholder="Tìm theo tên chiến dịch, khách hàng..." value="${param.search}">
                                </div>
                                <a href="${BASE_URL}/campaign?action=create" class="btn-add-campaign">
                                    <i data-feather="plus"></i>
                                    Thêm chiến dịch
                                </a>
                            </div>

                            <div class="toolbar-bottom">
                                <div class="filter-group">
                                    <label for="campaignTypeFilter">Loại chiến dịch</label>
                                    <select id="campaignTypeFilter" name="typeId">
                                        <option value="">Tất cả các loại</option>
                                        <c:forEach var="type" items="${allCampaignTypes}">
                                            <option value="${type.id}" ${param.typeId == type.id ? 'selected' : ''}>${type.typeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="startDateFilter">Từ ngày</label>
                                    <input type="date" id="startDateFilter" name="startDate" value="${param.startDate}">
                                </div>
                                <div class="filter-group">
                                    <label for="endDateFilter">Đến ngày</label>
                                    <input type="date" id="endDateFilter" name="endDate" value="${param.endDate}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn-filter">Áp dụng</button>
                                    <a href="${BASE_URL}/campaign?action=list" class="btn-clear-filter">Xóa lọc</a>
                                </div>
                            </div>
                        </section>
                    </form>

                    <section class="data-table-container content-card">
                        <table class="data-table">
                            <thead>
                                <tr>    
                                    <th>ID</th>
                                    <th>Tên chiến dịch</th>
                                    <th>Khách hàng</th>
                                    <th>Loại chiến dịch</th>
                                    <th>Ngày bắt đầu</th>
                                    <th>Ngày kết thúc</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="campaign" items="${campaignList}">
                                    <tr>
                                        <td>${campaign.campaignId}</td>
                                        <td><strong>${campaign.name}</strong></td>
                                        <td><a href="${BASE_URL}/customer/view?id=${campaign.enterpriseId}">${campaign.enterpriseName}</a></td>
                                        <td>${campaign.typeName}</td>
                                        <td><fmt:formatDate value="${campaign.effectiveStartDate}" pattern="dd/MM/yyyy" /></td>
                                        <td><fmt:formatDate value="${campaign.effectiveEndDate}" pattern="dd/MM/yyyy" /></td>
                                        <td>
                                            <select class="status-select" onchange="updateCampaignStatus(${campaign.campaignId}, this.value, this)">
                                                <c:forEach var="status" items="${allStatuses}">
                                                    <option value="${status.id}" ${campaign.statusId == status.id ? 'selected' : ''}>
                                                        ${status.statusName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                        <td class="actions-cell">
                                            <a href="${BASE_URL}/campaign?action=view&id=${campaign.campaignId}" class="icon-btn" title="Xem"><i data-feather="eye"></i></a>
                                            <a href="${BASE_URL}/campaign?action=edit&id=${campaign.campaignId}" class="icon-btn" title="Sửa"><i data-feather="edit"></i></a>
                                            <button class="icon-btn" title="Xóa" onclick="confirmDelete(${campaign.campaignId})"><i data-feather="trash-2"></i></button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty campaignList}">
                                    <tr>
                                        <td colspan="8" style="text-align: center; padding: 20px;">Không tìm thấy chiến dịch nào.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </section>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>  
        <script src="https://unpkg.com/feather-icons"></script>
        <script src="${BASE_URL}/js/mainMenu.js"></script> 
        <script>
                                                // Các hàm JavaScript giữ nguyên như cũ...
        </script>   
    </body> 
</html>