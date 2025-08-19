<%-- File: /jsp/customerSupport/viewTransaction.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết phiếu - ${ticket.requestCode}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewTransaction.css?v=<%= System.currentTimeMillis()%>">
        <script src="https://unpkg.com/feather-icons"></script>
        <style>
            .alert {
                padding: 1rem;
                margin-bottom: 1.5rem;
                border-radius: 8px;
                display: flex;
                align-items: center;
                gap: 12px;
                font-weight: 500;
            }
            .alert-success {
                background-color: #dcfce7;
                color: #166534;
                border: 1px solid #86efac;
            }
            .alert-danger {
                background-color: #fee2e2;
                color: #991b1b;
                border: 1px solid #fca5a5;
            }
            .alert .feather {
                width: 20px;
                height: 20px;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content" style="padding-top: 0; padding-bottom: 0;">
                    <%-- Các alert message --%>
                </div>

                <div class="page-content">
                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link">
                            <i data-feather="arrow-left"></i>
                            <span>Quay lại danh sách</span>
                        </a>
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/ticket?action=edit&id=${ticket.id}" class="btn btn-primary"><i data-feather="edit-2"></i>Sửa</a>
                            <c:choose>
                                <c:when test="${hasFeedback}">
                                    <div class="feedback-sent-notice"><i data-feather="check-circle"></i><span>Đã nhận phản hồi</span></div>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${ticket.status == 'Đã giải quyết' || ticket.status == 'Đã đóng'}">
                                        <a href="${pageContext.request.contextPath}/ticket?action=sendSurvey&id=${ticket.id}" class="btn btn-teal"><i data-feather="mail"></i>Gửi Khảo Sát</a>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin chung</h2>
                                <div class="info-grid">
                                    <div class="info-item"><span class="label">Mã phiếu</span><div class="value">${ticket.requestCode}</div></div>
                                    <div class="info-item"><span class="label">Khách hàng</span><div class="value">${ticket.enterpriseName}</div></div>
                                    <div class="info-item"><span class="label">Mã hợp đồng</span><div class="value">${not empty ticket.contractCode ? ticket.contractCode : 'Không có'}</div></div>
                                    <div class="info-item"><span class="label">Loại phiếu</span><div class="value">${ticket.serviceName}</div></div>
                                    <div class="info-item full-width">
                                        <span class="label">Địa chỉ thực hiện</span>
                                        <div class="value">
                                            <c:if test="${not empty schedule.address}">${schedule.address.streetAddress}, ${schedule.address.ward.name}, ${schedule.address.district.name}, ${schedule.address.province.name}</c:if>
                                            <c:if test="${empty schedule.address}">Chưa có thông tin địa chỉ.</c:if>
                                            </div>
                                        </div>
                                        <div class="info-item full-width"><span class="label">Mô tả chi tiết</span><div class="value">${ticket.description}</div></div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <c:if test="${empty ticket.devices}"><p>Không có thiết bị nào được ghi nhận cho yêu cầu này.</p></c:if>
                                <c:if test="${not empty ticket.devices}">
                                    <table class="device-table">
                                        <thead><tr><th>Tên thiết bị</th><th>Mã thiết bị</th><th>Mô tả sự cố</th></tr></thead>
                                        <tbody>
                                            <c:forEach var="device" items="${ticket.devices}">
                                                <tr>
                                                    <td><div class="cell-content">${device.deviceName}</div></td>
                                                    <td><div class="cell-content">${device.serialNumber}</div></td>
                                                    <td><div class="cell-content">${device.problemDescription}</div></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                            </div>
                        </div>

                        <div class="sidebar-column">
                            <div class="detail-card">
                                <h2 class="card-title">Chi tiết Trạng thái</h2>
                                <div class="info-item">
                                    <span class="label">Trạng thái</span>
                                    <span class="value">
                                        <c:choose>
                                            <c:when test="${ticket.status == 'Mới tạo'}"><span class="status-pill status-new">Mới tạo</span></c:when>
                                            <c:when test="${ticket.status == 'Đang thực hiện'}"><span class="status-pill status-in-progress">Đang thực hiện</span></c:when>
                                            <c:when test="${ticket.status == 'Đã giải quyết'}"><span class="status-pill status-resolved">Đã giải quyết</span></c:when>
                                            <c:when test="${ticket.status == 'Đã đóng'}"><span class="status-pill status-closed">Đã đóng</span></c:when>
                                            <c:when test="${ticket.status == 'Đã hủy'}"><span class="status-pill status-rejected">Đã hủy</span></c:when>
                                            <c:otherwise><span class="status-pill">${ticket.status}</span></c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Mức độ ưu tiên</span>
                                    <span class="value">
                                        <c:choose>
                                            <c:when test="${ticket.priority == 'critical'}"><span class="priority-pill priority-critical">Khẩn cấp</span></c:when>
                                            <c:when test="${ticket.priority == 'high'}"><span class="priority-pill priority-high">Cao</span></c:when>
                                            <c:when test="${ticket.priority == 'medium'}"><span class="priority-pill priority-medium">Thông thường</span></c:when>
                                            <c:when test="${ticket.priority == 'low'}"><span class="priority-pill priority-low">Thấp</span></c:when>
                                            <c:otherwise><span class="priority-pill">${ticket.priority}</span></c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Nhân viên phụ trách</span>
                                    <div class="value">${not empty ticket.assignedToName ? ticket.assignedToName : 'Chưa gán'}</div>
                                </div>
                                <div class="info-item">
                                    <span class="label">Người tạo phiếu</span>
                                    <span class="value">${ticket.reporterName}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Ngày tạo</span>
                                    <span class="value"><c:if test="${ticket.createdAt != null}"><fmt:formatDate value="${ticket.createdAt}" pattern="HH:mm dd/MM/yyyy" /></c:if></span>
                                    </div>

                                <c:if test="${not empty schedule}">
                                    <%-- Thêm style để đặt màu nền --%>
                                  
                                        <div class="info-item">
                                            <span style="font-weight: bold;">Lịch hẹn</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Ngày bắt đầu</span>
                                            <span class="value">${scheduledDateFormatted}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Giờ bắt đầu</span>
                                            <span class="value">${not empty schedule.startTime ? schedule.startTime : 'N/A'}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Ngày kết thúc</span>
                                            <span class="value">${endDateFormatted}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Giờ kết thúc</span>
                                            <span class="value">${not empty schedule.endTime ? schedule.endTime : 'N/A'}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="label">Màu sắc</span>
                                            <div class="value">
                                                <div class="color-swatch-display" 
                                                     style="background-color: ${not empty schedule.color ? schedule.color : '#6c757d'};">
                                                </div>
                                            </div>
                                        </div>
                                   
                                </c:if>

                                <div class="info-item">
                                    <span class="label">Tính phí</span>
                                    <span class="value">${ticket.isBillable ? 'Có' : 'Không (Bảo hành)'}</span>
                                </div>
                                <c:if test="${ticket.isBillable}">
                                    <div class="info-item">
                                        <span class="label">Chi phí dự kiến</span>
                                        <span class="value"><fmt:formatNumber value="${ticket.estimatedCost}" type="currency" currencySymbol="VND" maxFractionDigits="0" /></span>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>