<%--
    Document    : listCampaign.jsp
    Created on  : Aug 13, 2025
    Description : Trang quản lý danh sách chiến dịch (đã cập nhật: mã chiến dịch clickable, khách hàng không link).
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
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/listCampaign.css">
        <link rel="stylesheet" href="${BASE_URL}/css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Quản lý Chiến dịch"/>
                </jsp:include>


                <div class="page-content">

                    <!-- Stats -->
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

                    <!-- Filters -->
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

                                <!-- Trạng thái lấy từ Statuses -->
                                <div class="filter-group">
                                    <label for="statusFilter">Trạng thái</label>
                                    <select id="statusFilter" name="statusId">
                                        <option value="">Tất cả trạng thái</option>
                                        <c:forEach var="st" items="${statusList}">
                                            <option value="${st.id}" ${statusIdFilter == st.id ? 'selected' : ''}>${st.statusName}</option>
                                        </c:forEach>
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

                    <!-- Table -->
                    <section class="data-table-container content-card">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Mã chiến dịch</th>
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
                                        <!-- Mã chiến dịch (click sang trang chi tiết) -->
                                        <td>
                                            <a href="${BASE_URL}/view-campaign?id=${campaign.campaignId}">
                                                ${empty campaign.campaignCode ? '—' : campaign.campaignCode}
                                            </a>
                                        </td>

                                        <td><strong>${campaign.name}</strong></td>

                                        <!-- Khách hàng: bỏ link, chỉ hiển thị tên -->
                                        <td>${campaign.enterpriseName}</td>

                                        <td>${campaign.typeName}</td>

                                        <!-- Ngày từ MaintenanceSchedules -->
                                        <td><fmt:formatDate value="${campaign.scheduledDate}" pattern="dd/MM/yyyy" /></td>
                                        <td><fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" /></td>

                                        <!-- Trạng thái hiển thị theo Statuses.status_name, giữ màu class cũ -->
                                        <td>
                                            <c:set var="statusKey"
                                                   value="${campaign.statusName == 'Sắp tới' ? 'pending' :
                                                            (campaign.statusName == 'Đang thực hiện' ? 'active' :
                                                            (campaign.statusName == 'Hoàn thành' ? 'ended' : 'canceled'))}" />
                                            <span class="status-badge status-${statusKey}">
                                                ${campaign.statusName}
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

                    <jsp:include page="/pagination.jsp">
                        <jsp:param name="actionUrl" value="/list-campaign"/>
                    </jsp:include>

                </div>
            </main>
        </div>

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
                                                        confirmButtonText: 'Xoá',
                                                        cancelButtonText: 'Hủy'
                                                    }).then((result) => {
                                                        if (result.isConfirmed) {
                                                            const form = document.createElement('form');
                                                            form.method = 'POST';
                                                            form.action = '${BASE_URL}/delete-campaign';
                                                            const hiddenField = document.createElement('input');
                                                            hiddenField.type = 'hidden';
                                                            hiddenField.name = 'id'; 
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
