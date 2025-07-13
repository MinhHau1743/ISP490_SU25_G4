<%-- 
    Document   : viewTransaction
    Created on : Jun 14, 2025, 1:33:31 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="listTransaction" />


<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết giao dịch - ${ticket.requestCode}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <%-- Tái sử dụng CSS từ file viewTransaction.css của bạn --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewTransaction.css?v=<%= System.currentTimeMillis()%>">

        <script src="https://unpkg.com/feather-icons"></script>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div class="page-content">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link">
                            <i data-feather="arrow-left"></i>
                            <span>Quay lại danh sách</span>
                        </a>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <%-- Các chức năng Sửa, In sẽ được phát triển sau --%>

                            <a href="${pageContext.request.contextPath}/ticket?action=edit&id=${ticket.id}" class="btn btn-primary"><i data-feather="edit-2"></i>Sửa</a>
                            <a href="#" class="btn btn-primary"><i data-feather="printer"></i>In Phiếu</a>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin chung</h2>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <span class="label">Mã phiếu</span>
                                        <div class="value">${ticket.requestCode}</div>
                                    </div>

                                    <div class="info-item">
                                        <span class="label">Khách hàng</span>
                                        <div class="value">${ticket.enterpriseName}</div>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">Mã hợp đồng</span>
                                        <div class="value">${not empty ticket.contractCode ? ticket.contractCode : 'Không có'}</div>
                                    </div>
                                    <div class="info-item">
                                        <span class="label">Loại phiếu</span>
                                        <div class="value">${ticket.serviceName}</div>
                                    </div>
                                    <div class="info-item full-width">
                                        <span class="label">Mô tả chi tiết</span>
                                        <div class="value">${ticket.description}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <%-- SỬA LỖI Ở ĐÂY: Thay "devices" thành "products" --%>
                                <c:if test="${empty ticket.devices}">
                                    <p>Không có thiết bị nào được ghi nhận cho yêu cầu này.</p>
                                </c:if>
                                <c:if test="${not empty ticket.devices}">
                                    <table class="device-table">
                                        <thead>
                                            <tr>
                                                <th class="col-device-name">Tên thiết bị</th>
                                                <th class="col-serial">Mã thiết bị</th>
                                                <th class="col-description">Mô tả sự cố</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%-- SỬA LỖI Ở ĐÂY: Lặp qua "products" và hiển thị đúng thuộc tính --%>
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
                                            <c:when test="${ticket.status == 'new'}"><span class="status-pill status-new">Mới</span></c:when>
                                            <c:when test="${ticket.status == 'assigned'}"><span class="status-pill status-assigned">Đã giao</span></c:when>
                                            <c:when test="${ticket.status == 'in_progress'}"><span class="status-pill status-in-progress">Đang xử lý</span></c:when>
                                            <c:when test="${ticket.status == 'resolved'}"><span class="status-pill status-resolved">Đã xử lý</span></c:when>
                                            <c:when test="${ticket.status == 'closed'}"><span class="status-pill status-closed">Đã đóng</span></c:when>
                                            <c:when test="${ticket.status == 'rejected'}"><span class="status-pill status-rejected">Từ chối</span></c:when>
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
                                    <span class="value">${not empty ticket.assignedToName ? ticket.assignedToName : 'Chưa gán'}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Người tạo phiếu</span>
                                    <span class="value">${ticket.reporterName}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Ngày tạo</span>
                                    <span class="value">
                                        <c:if test="${ticket.createdAt != null}">
                                            <fmt:formatDate value="${ticket.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                        </c:if>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Tính phí</span>
                                    <span class="value">${ticket.isBillable ? 'Có' : 'Không (Bảo hành)'}</span>
                                </div>
                                <c:if test="${ticket.isBillable}">
                                    <div class="info-item">
                                        <span class="label">Chi phí dự kiến</span>
                                        <%-- Hiển thị giá trị gốc để kiểm tra, thay vì dùng fmt:formatNumber --%>
                                        <span class="value">${ticket.estimatedCost} VND</span>
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