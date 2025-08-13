<%--
    Document    : listCampaign.jsp
    Created on  : Aug 13, 2025
    Author      : Gemini Assistant
    Description : Trang quản lý danh sách chiến dịch, phiên bản hoàn chỉnh với bộ lọc và giao diện nhất quán.
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

        <%-- Các thư viện CSS --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">

        <%-- Các file CSS tự định nghĩa --%>
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCampaign.css">

    </head>
    <body>
        <div class="app-container">
            <%-- Include Menu chính --%>
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <%-- Include Header và truyền vào tiêu đề trang --%>
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Quản lý Chiến dịch"/>
                </jsp:include>

                <div class="page-content">

                    <%-- Phần thống kê nhanh --%>
                    <section class="stats-grid">
                        <div class="stat-card">
                            <div class="icon-container icon-yellow"><i data-feather="mail"></i></div>
                            <div class="info">
                                <div class="title">Tổng chiến dịch</div>
                                <div class="value"><fmt:formatNumber value="${totalRecords}" type="number"/></div>
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

                    <%-- Form chứa bộ lọc và thanh tìm kiếm --%>
                    <form action="${BASE_URL}/list-campaign" method="GET">
                        <section class="campaign-toolbar">
                            <div class="toolbar-top">
                                <div class="search-bar">
                                    <i data-feather="search"></i>
                                    <input type="text" name="search" placeholder="Tìm theo tên chiến dịch, khách hàng..." value="${searchTerm}">
                                </div>
                                <a href="${BASE_URL}/create-campaign" class="btn-add-campaign">
                                    <i data-feather="plus"></i>
                                    Thêm chiến dịch
                                </a>
                            </div>

                            <div class="toolbar-bottom">
                                <div class="filter-group">
                                    <label for="campaignTypeFilter">Loại chiến dịch</label>
                                    <select id="campaignTypeFilter" name="typeId">
                                        <option value="0">Tất cả các loại</option>
                                        <c:forEach var="type" items="${allCampaignTypes}">
                                            <option value="${type.id}" ${typeIdFilter == type.id ? 'selected' : ''}>${type.typeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="statusFilter">Trạng thái</label>
                                    <select id="statusFilter" name="status">
                                        <option value="">Tất cả trạng thái</option>
                                        <option value="pending" ${statusFilter == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Đang hoạt động</option>
                                        <option value="ended" ${statusFilter == 'ended' ? 'selected' : ''}>Đã kết thúc</option>
                                        <option value="canceled" ${statusFilter == 'canceled' ? 'selected' : ''}>Đã hủy</option>
                                    </select>
                                </div>
                                <div class="filter-group">
                                    <label for="startDateFilter">Từ ngày</label>
                                    <input type="date" id="startDateFilter" name="startDate" value="${startDateFilter}">
                                </div>
                                <div class="filter-group">
                                    <label for="endDateFilter">Đến ngày</label>
                                    <input type="date" id="endDateFilter" name="endDate" value="${endDateFilter}">
                                </div>
                                <div class="filter-actions">
                                    <button type="submit" class="btn-filter">Áp dụng</button>
                                    <a href="${BASE_URL}/list-campaign" class="btn-clear-filter">Xóa lọc</a>
                                </div>
                            </div>
                        </section>
                    </form>

                    <%-- Bảng hiển thị dữ liệu --%>
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
                                <c:forEach var="campaign" items="${campaigns}">
                                    <tr>
                                        <td>${campaign.campaignId}</td>
                                        <td><strong>${campaign.name}</strong></td>
                                        <td><a href="${BASE_URL}/customer?action=view&id=${campaign.enterpriseId}">${campaign.enterpriseName}</a></td>
                                        <td>${campaign.typeName}</td>
                                        <td><fmt:formatDate value="${campaign.startDate}" pattern="dd/MM/yyyy" /></td>
                                        <td><fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" /></td>
                                        <td>
                                            <span class="status-badge status-${campaign.status}">
                                                <c:choose>
                                                    <c:when test="${campaign.status == 'pending'}">Chờ duyệt</c:when>
                                                    <c:when test="${campaign.status == 'active'}">Đang hoạt động</c:when>
                                                    <c:when test="${campaign.status == 'ended'}">Đã kết thúc</c:when>
                                                    <c:when test="${campaign.status == 'canceled'}">Đã hủy</c:when>
                                                    <c:otherwise>${campaign.status}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td class="actions-cell">
                                            <a href="${BASE_URL}/view-campaign?id=${campaign.campaignId}" class="icon-btn" title="Xem"><i data-feather="eye"></i></a>
                                            <a href="${BASE_URL}/edit-campaign?id=${campaign.campaignId}" class="icon-btn" title="Sửa"><i data-feather="edit"></i></a>
                                            <button class="icon-btn" title="Xóa" onclick="confirmDelete(${campaign.campaignId})"><i data-feather="trash-2"></i></button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty campaigns}">
                                    <tr>
                                        <td colspan="8" style="text-align: center; padding: 20px;">Không tìm thấy chiến dịch nào.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </section>

                    <%-- Include component phân trang --%>
                    <jsp:include page="/pagination.jsp"/>

                </div>
            </main>
        </div>

        <%-- Các thư viện JavaScript --%>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>  
        <script src="https://unpkg.com/feather-icons"></script>
        <script src="${BASE_URL}/js/mainMenu.js"></script> 
        <script>
                                                feather.replace();

                                                function confirmDelete(campaignId) {
                                                    Swal.fire({
                                                        title: 'Bạn có chắc chắn không?',
                                                        text: "Bạn sẽ không thể hoàn tác hành động này!",
                                                        icon: 'warning',
                                                        showCancelButton: true,
                                                        confirmButtonColor: '#d33',
                                                        cancelButtonColor: '#3085d6',
                                                        confirmButtonText: 'Vâng, xóa nó!',
                                                        cancelButtonText: 'Hủy'
                                                    }).then((result) => {
                                                        if (result.isConfirmed) {
                                                            const form = document.createElement('form');
                                                            form.method = 'POST';
                                                            form.action = '${BASE_URL}/delete-campaign'; // URL servlet xử lý xóa
                                                            const hiddenField = document.createElement('input');
                                                            hiddenField.type = 'hidden';
                                                            hiddenField.name = 'campaignId';
                                                            hiddenField.value = campaignId;
                                                            form.appendChild(hiddenField);
                                                            document.body.appendChild(form);
                                                            form.submit();
                                                        }
                                                    });
                                                }
        </script>   
    </body> 
</html>