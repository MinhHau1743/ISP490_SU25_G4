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
        <style>
            /* CSS cho các hộp thông báo */
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
                background-color: #dcfce7; /* green-100 */
                color: #166534; /* green-800 */
                border: 1px solid #86efac; /* green-300 */
            }
            .alert-danger {
                background-color: #fee2e2; /* red-100 */
                color: #991b1b; /* red-800 */
                border: 1px solid #fca5a5; /* red-300 */
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
                    <c:if test="${param.update == 'success'}">
                        <div class="alert alert-success">
                            <i data-feather="check-circle"></i>
                            <span>Cập nhật thông tin giao dịch thành công!</span>
                            <%-- Kiểm tra thêm nếu email đã được gửi --%>
                            <c:if test="${param.surveySent == 'true'}">
                                <strong>Email khảo sát đã được tự động gửi đến khách hàng.</strong>
                            </c:if>
                        </div>
                    </c:if>
                    <c:if test="${param.update == 'failed'}">
                        <div class="alert alert-danger">
                            <i data-feather="alert-triangle"></i>
                            <span>Đã có lỗi xảy ra. Cập nhật thất bại!</span>
                        </div>
                    </c:if>
                </div>
                <div class="page-content">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link">
                            <i data-feather="arrow-left"></i>
                            <span>Quay lại danh sách</span>
                        </a>
                        <div class="action-buttons" style="display: flex; gap: 8px;">
                            <%-- Các chức năng Sửa, In sẽ được phát triển sau --%>
                            <%-- Chỉ hiển thị nút phản hồi khi ticket đã được xử lý hoặc đã đóng --%>


                            <a href="${pageContext.request.contextPath}/ticket?action=edit&id=${ticket.id}" class="btn btn-primary"><i data-feather="edit-2"></i>Sửa</a>
                            <a href="#" class="btn btn-primary"><i data-feather="printer"></i>In Phiếu</a>
                            <c:choose>
                                <%-- Nếu đã có feedback thì hiển thị thông báo --%>
                                <c:when test="${hasFeedback}">
                                    <div style="display: flex; align-items: center; background-color: #eef2ff; color: #4338ca; padding: 8px 16px; border-radius: 8px; font-weight: 500;">
                                        <i data-feather="check-circle" style="width: 20px; height: 20px; margin-right: 8px;"></i>
                                        <span>Bạn đã gửi phản hồi.</span>
                                    </div>
                                </c:when>

                                <%-- Nếu chưa có, chỉ hiện nút khi ticket đã xong --%>
                                <c:otherwise>
                                    <c:if test="${ticket.status == 'resolved' || ticket.status == 'closed'}">
                                        <%-- Nút này giờ sẽ gọi action gửi mail mời khảo sát --%>
                                        <a href="${pageContext.request.contextPath}/ticket?action=sendSurvey&id=${ticket.id}" class="btn btn-teal">
                                            <i data-feather="mail"></i>Gửi Khảo Sát
                                        </a>
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
                                    <div class="form-group full-width">
                                        <div class="address-section">
                                            <h3>Địa chỉ thực hiện công việc</h3>

                                            <div class="address-grid">
                                                <div class="field">
                                                    <span class="label">Tỉnh/Thành phố:</span>
                                                    <span class="value">
                                                        <c:forEach var="p" items="${provinces}">
                                                            <c:if test="${p.id == schedule.address.provinceId}">${p.name}</c:if>
                                                        </c:forEach>
                                                    </span>
                                                </div>

                                                <div class="field">
                                                    <span class="label">Quận/Huyện:</span>
                                                    <span class="value">
                                                        <c:forEach var="d" items="${districts}">
                                                            <c:if test="${d.id == schedule.address.districtId}">${d.name}</c:if>
                                                        </c:forEach>
                                                    </span>
                                                </div>

                                                <div class="field">
                                                    <span class="label">Phường/Xã:</span>
                                                    <span class="value">
                                                        <c:forEach var="w" items="${wards}">
                                                            <c:if test="${w.id == schedule.address.wardId}">${w.name}</c:if>
                                                        </c:forEach>
                                                    </span>
                                                </div>

                                                <!-- ĐỊA CHỈ CỤ THỂ: span toàn hàng -->
                                                <div class="field full-row">
                                                    <span class="label">Địa chỉ cụ thể:</span>
                                                    <span class="value">${schedule.address.streetAddress}</span>
                                                </div>
                                            </div>
                                        </div>
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
                                    <div class="value">
                                        <c:set var="hasAssignedUsers" value="${false}" />
                                        <c:forEach var="employee" items="${employeeList}">
                                            <c:if test="${assignedUserIds.contains(employee.id)}">
                                                <span class="user-tag">${employee.lastName} ${employee.middleName} ${employee.firstName}</span>
                                                <c:set var="hasAssignedUsers" value="${true}" />
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${!hasAssignedUsers}">
                                            Chưa gán
                                        </c:if>
                                    </div>
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
                                    <label class="label"">Ngày bắt đầu (*)</label>
                                    <span class="value">${schedule.scheduledDate}</span>
                                </div>
                                <div class="info-item">
                                    <label class="label">Ngày kết thúc</label>
                                    <span class="value">${schedule.endDate}</span>
                                </div>
                                <div class="info-item">
                                    <label class="label">Giờ bắt đầu</label>
                                    <span class="value">${schedule.startTime}</span>
                                </div>
                                <div class="info-item">
                                    <label class="label">Giờ kết thúc</label>
                                    <span class="value">${schedule.endTime}</span>
                                </div>
                                <div class="info-item">
                                    <span class="label">Màu sắc</span>
                                    <div class="value">
                                        <div class="color-swatch-display" 
                                             style="background-color: ${not empty schedule.color ? schedule.color : '#6c757d'};">
                                        </div>
                                    </div>
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