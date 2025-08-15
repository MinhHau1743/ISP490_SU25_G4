<%--
    Document   : viewCampaignDetails
    Created on : Jul 30, 2025
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentPage" value="campaign" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết: ${campaign.name} - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/viewCampaignDetails.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <div class="main-content">
                <jsp:include page="/header.jsp"/>

                <div class="page-container">

                    <c:if test="${empty campaign}">
                        <div class="page-header">
                            <h1>Không tìm thấy chiến dịch</h1>
                        </div>
                        <div class="content-card">
                            <p>Chiến dịch bạn yêu cầu không tồn tại hoặc đã bị xóa.</p>
                            <div class="form-actions">
                                <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">
                                    <i data-feather="arrow-left" style="width:16px; height:16px;"></i> Quay lại danh sách
                                </a>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty campaign}">
                        <div class="page-header">
                            <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">
                                <i data-feather="arrow-left" style="width:16px; height:16px;"></i> Quay lại
                            </a>
                            <h1><c:out value="${campaign.name}"/></h1>
                        </div>

                        <div class="content-card">
                            <!-- 1) Thông tin chiến dịch -->
                            <h3 class="sub-header">Thông tin chiến dịch</h3>
                            <div class="detail-row" style="grid-template-columns: 1fr 1fr; gap: 24px;">
                                <div class="detail-group">
                                    <label>Khách hàng</label>
                                    <div class="detail-value">${campaign.enterpriseName}</div>
                                </div>
                                <div class="detail-group">
                                    <label>Loại chiến dịch</label>
                                    <div class="detail-value">${campaign.typeName}</div>
                                </div>
                            </div>

                            <div class="detail-row" style="grid-template-columns: 1fr 1fr; gap: 24px;">
                                <div class="detail-group">
                                    <label>Người thực hiện</label>
                                    <div class="detail-value">
                                        ${campaign.creator.lastName} ${campaign.creator.middleName} ${campaign.creator.firstName}
                                        <span style="color: var(--text-secondary); margin-left: 5px;">(${campaign.creator.employeeCode})</span>
                                    </div>
                                </div>

                                <!-- Map class màu cho trạng thái lịch -->
                                <c:set var="stLower" value="${fn:toLowerCase(maintenanceSchedule.statusName)}"/>
                                <c:set var="stClass"
                                       value="${
                                       stLower eq 'đã hủy' ? 'status-canceled' :
                                           (stLower eq 'quá hạn' ? 'status-overdue' : '')
                                       }"/>

                                <div class="detail-group">
                                    <label>Trạng thái</label>
                                    <div>
                                        <span class="status-pill ${stClass}">
                                            <c:choose>
                                                <c:when test="${maintenanceSchedule.statusName eq 'Sắp tới'}">
                                                    <i data-feather="clock"></i> Sắp tới
                                                </c:when>
                                                <c:when test="${maintenanceSchedule.statusName eq 'Đang thực hiện'}">
                                                    <i data-feather="play-circle"></i> Đang thực hiện
                                                </c:when>
                                                <c:when test="${maintenanceSchedule.statusName eq 'Hoàn thành'}">
                                                    <i data-feather="check-circle"></i> Hoàn thành
                                                </c:when>
                                                <c:when test="${maintenanceSchedule.statusName eq 'Quá hạn'}">
                                                    <i data-feather="alert-triangle"></i> Quá hạn
                                                </c:when>
                                                <c:when test="${maintenanceSchedule.statusName eq 'Đã hủy'}">
                                                    <i data-feather="x-circle"></i> Đã hủy
                                                </c:when>
                                                <c:otherwise>
                                                    ${maintenanceSchedule.statusName}
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-group">
                                <label>Mô tả</label>
                                <div class="detail-value description">
                                    ${not empty campaign.description ? campaign.description : 'Không có mô tả.'}
                                </div>
                            </div>

                            <!-- 2) Thông tin lịch trình & Địa điểm -->
                            <h3 class="sub-header">Thông tin lịch trình & Địa điểm</h3>
                            <c:choose>
                                <c:when test="${not empty maintenanceSchedule}">
                                    <div class="detail-row" style="grid-template-columns: 1fr 1fr; gap: 24px;">
                                        <div class="detail-group">
                                            <label>Thời gian bắt đầu</label>
                                            <div class="detail-value">
                                                ${maintenanceSchedule.startTime}
                                                ${scheduledDateStr}
                                            </div>
                                        </div>
                                        <div class="detail-group">
                                            <label>Thời gian kết thúc</label>
                                            <div class="detail-value">
                                                ${maintenanceSchedule.endTime}
                                                ${endDateStr}
                                            </div>
                                        </div>
                                    </div>

                                    <div class="detail-group">
                                        <label>Địa chỉ đầy đủ</label>
                                        <div class="detail-value description">
                                            <c:choose>
                                                <c:when test="${not empty maintenanceSchedule.address}">
                                                    ${maintenanceSchedule.address.streetAddress},
                                                    ${maintenanceSchedule.address.ward.name},
                                                    ${maintenanceSchedule.address.district.name},
                                                    ${maintenanceSchedule.address.province.name}
                                                </c:when>
                                                <c:otherwise>
                                                    Chưa cập nhật địa chỉ.
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="detail-group">
                                        <div class="detail-value" style="color: var(--text-secondary);">
                                            Chưa có lịch trình cho chiến dịch này.
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="form-actions">
                                <a href="${BASE_URL}/edit-campaign?id=${campaign.campaignId}" class="btn btn-primary">
                                    <i data-feather="edit" style="width: 16px; height: 16px;"></i> Sửa
                                </a>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
