<%-- 
    Document   : editTransaction
    Created on : Jun 14, 2025, 1:33:12 PM
    Author     : NGUYEN MINH
--%>

<%-- File: /jsp/customerSupport/createTicket.jsp --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo Phiếu Giao Việc Mới</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createTicket.css">
        <script src="https://unpkg.com/feather-icons"></script>

    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <div id="product-suggestion-box" class="suggestion-box"></div>
                <form class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="update">
                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại</span></a>
                        <button type="submit" class="btn btn-primary"><i data-feather="plus-circle"></i>Lưu thay đổi</button>
                    </div>
                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin Phiếu Giao Việc</h2>
                                <div class="form-grid">
                                    <div class="form-group full-width">
                                        <label>Tiêu đề</label>
                                        <input type="text" name="title" class="form-control" value="${ticket.title}">
                                    </div>
                                    <div class="form-group">
                                        <label>Mã Phiếu</label>
                                        <input type="text" class="form-control" value="${ticket.requestCode}" readonly>
                                    </div>
                                    <div class="form-group">
                                        <label for="enterpriseId">Khách hàng (*)</label>
                                        <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn khách hàng --</option>
                                            <c:forEach var="customer" items="${customerList}">
                                                <option value="${customer.id}" ${customer.id == ticket.enterpriseId ? 'selected' : ''}>${customer.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="contractId">Hợp đồng</label>
                                        <select id="contractId" name="contractId" class="form-control">
                                            <option value="">-- Chọn hợp đồng --</option>
                                            <c:forEach var="contract" items="${contractList}">
                                                <option value="${contract.id}" ${contract.id == ticket.contractId ? 'selected' : ''}>${contract.contractCode}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn loại phiếu --</option>
                                            <c:forEach var="service" items="${serviceList}">
                                                <option value="${service.id}" ${service.id == ticket.serviceId ? 'selected' : ''}>${service.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <!-- Địa chỉ Section -->
                                    <div class="form-group full-width">
                                        <div class="address-section">
                                            <h3>Địa chỉ thực hiện công việc</h3>
                                            <div class="address-grid">
                                                <div class="form-group">
                                                    <label for="province">Tỉnh/Thành phố (*)</label>
                                                    <%-- ĐÂY LÀ DẠNG ĐÚNG --%>
                                                    <select id="province" name="province" class="form-control" required>
                                                        <option value="" disabled selected>-- Chọn Tỉnh/Thành --</option>
                                                        <c:forEach var="p" items="${provinces}">
                                                            <option value="${p.id}" ${p.id == schedule.provinceId ? 'selected' : ''}>${p.name}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="district">Quận/Huyện (*)</label>
                                                    <select id="district" name="district" class="form-control" required disabled>
                                                        <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="ward">Phường/Xã (*)</label>
                                                    <select id="ward" name="ward" class="form-control" required disabled>
                                                        <option value="" disabled selected>-- Chọn Phường/Xã --</option>

                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                                <input type="text" id="streetAddress" name="streetAddress" value="${schedule.streetAddress}" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group full-width">
                                        <label for="description">Mô tả chung (*)</label>
                                        <textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required>${ticket.description}</textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>

                                <table class="device-table">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị</th>
                                            <th>Serial Number</th>
                                            <th>Mô tả sự cố của thiết bị</th>
                                            <th class="action-col"></th>
                                        </tr>
                                    </thead>
                                    <tbody id="device-tbody">
                                        <%-- JavaScript sẽ chèn các dòng <tr> vào đây --%>
                                    </tbody>
                                </table>

                                <div class="device-table-actions">
                                    <button type="button" id="addDeviceBtn" class="btn btn-secondary">
                                        <i data-feather="plus"></i>Thêm thiết bị
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="sidebar-column">
                            <div class="detail-card sidebar-form">
                                <h2 class="card-title">Chi tiết Giao việc</h2>

                                <div class="sidebar-form-row">
                                    <label for="priority">Mức độ ưu tiên</label>
                                    <div class="input-with-icon">
                                        <select id="priority" name="priority" class="form-control">
                                            <option value="medium" ${ticket.priority == 'medium' ? 'selected' : ''}>Thông thường</option>
                                            <option value="high" ${ticket.priority == 'high' ? 'selected' : ''}>Cao</option>
                                            <option value="urgent" ${ticket.priority == 'urgent' ? 'selected' : ''}>Khẩn cấp</option>
                                        </select>

                                    </div>
                                </div>

                                <div class="sidebar-form-row">
                                    <label for="status">Trạng thái</label>
                                    <div class="input-with-icon">
                                        <select id="status" name="status" class="form-control" required>
                                            <option value="new" ${ticket.status == 'new' ? 'selected' : ''}>Mới tạo</option>
                                            <option value="in_progress" ${ticket.status == 'in_progress' ? 'selected' : ''}>Đang thực hiện</option>
                                            <option value="resolved" ${ticket.status == 'resolved' ? 'selected' : ''}>Đã giải quyết</option>
                                            <option value="closed" ${ticket.status == 'closed' ? 'selected' : ''}>Đã đóng</option>
                                        </select>

                                    </div>
                                </div>

                                <div class="sidebar-form-row">
                                    <label for="employeeId">Gán cho</label>
                                    <div class="input-with-icon">
                                        <select id="employeeId" name="employeeId" class="form-control" required>
                                            <option value="" disabled ${empty schedule.assignedUserId ? 'selected' : ''}>-- Chọn kỹ thuật viên --</option>
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}" ${employee.id == ticket.reporterId ? 'selected' : ''}>${employee.lastName} ${employee.middleName} ${employee.firstName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>


                                <div class="sidebar-form-row">
                                    <label>Ngày tạo</label>
                                    <div class="input-with-icon">
                                        <input type="date" id="createdDate" name="createdDate" class="form-control" readonly>
                                    </div>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khoảng ngày</label>
                                    <div class="control">
                                        <input type="date" id="scheduled_date" name="scheduled_date" value="${scheduled_date}" class="form-control" required>
                                        <div class="field-hint">Bắt đầu</div>
                                    </div>
                                    <div class="control">
                                        <input type="date" id="end_date" name="end_date" value="${end_date}" class="form-control">
                                        <div class="field-hint">Kết thúc</div>
                                    </div>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khung giờ</label>
                                    <div class="control">
                                        <input type="time" id="start_time" name="start_time" value="${start_time}" class="form-control">
                                        <div class="field-hint">Từ</div>
                                    </div>
                                    <div class="control">
                                        <input type="time" id="end_time" name="end_time" value="${end_time}" class="form-control">
                                        <div class="field-hint">Đến</div>
                                    </div>
                                </div>

                                <div class="sidebar-form-row">
                                    <label>Chi phí dự kiến</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="isBillable" value="true"> Có</label>
                                        <label><input type="radio" name="isBillable" value="false" checked> Không</label>
                                    </div>
                                </div>

                                <div id="amount-group" class="sidebar-form-row" style="display:none;">
                                    <label for="amount">Số tiền (VND)</label>
                                    <input type="number" id="amount" name="amount" class="form-control" min="0" value="0">
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <script>
            window.contextPath = '<%= request.getContextPath()%>';
            window.PRESELECTED_ADDRESS = {
                provinceId: '${param.province != null ? param.province : schedule.provinceId}',
                districtId: '${param.district != null ? param.district : schedule.districtId}',
                wardId: '${param.ward != null ? param.ward : schedule.wardId}'
            };
        </script>

        <script src="${pageContext.request.contextPath}/js/editTransaction.js?v=<%= System.currentTimeMillis()%>"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
