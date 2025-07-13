<%-- 
    Document   : editTransaction
    Created on : Jun 14, 2025, 1:33:12 PM
    Author     : NGUYEN MINH
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="listTransaction" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Phiếu - ${ticket.requestCode}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/createTicket.css?v=<%= System.currentTimeMillis()%>">
        
        <script src="https://unpkg.com/feather-icons"></script>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <form id="editTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${ticket.id}">

                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=view&id=${ticket.id}" class="back-link"><i data-feather="arrow-left"></i><span>Hủy bỏ</span></a>
                        <div class="action-buttons">
                            <button type="submit" class="btn btn-primary"><i data-feather="save"></i>Lưu thay đổi</button>
                        </div>
                    </div>

                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Chỉnh sửa thông tin phiếu</h2>
                                <div class="form-grid">
                                    <div class="form-group"><label>Mã Phiếu</label><input type="text" class="form-control" value="${ticket.requestCode}" readonly></div>
                                    <div class="form-group">
                                        <label for="enterpriseId">Khách hàng (*)</label>
                                        <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                            <c:forEach var="customer" items="${customerList}"><option value="${customer.id}" ${customer.id == ticket.enterpriseId ? 'selected' : ''}>${customer.name}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group"><label for="contractCode">Mã hợp đồng</label><input type="text" id="contractCode" name="contractCode" class="form-control" value="${ticket.contractCode}"></div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <c:forEach var="service" items="${serviceList}"><option value="${service.id}" ${service.id == ticket.serviceId ? 'selected' : ''}>${service.name}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group full-width"><label for="description">Mô tả chung (*)</label><textarea id="description" name="description" class="form-control" rows="4" required>${ticket.description}</textarea></div>
                                </div>
                            </div>
                            <div class="detail-card">
                                <h2 class="card-title">Các thiết bị liên quan</h2>
                                <table class="device-table-edit">
                                    <thead>
                                        <tr>
                                            <th>Tên thiết bị</th>
                                            <th>Serial Number</th>
                                            <th style="width: 40%;">Mô tả sự cố</th>
                                            <th style="width: 50px;"></th>
                                        </tr>
                                    </thead>
                                    <%-- Phần thân table sẽ được JS tự động điền vào --%>
                                    <tbody id="device-list">
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
                                <div class="sidebar-form-row"><label for="status">Trạng thái</label>
                                    <select id="status" name="status" class="form-control">
                                        <option value="new" ${ticket.status == 'new' ? 'selected' : ''}>Mới</option>
                                        <option value="assigned" ${ticket.status == 'assigned' ? 'selected' : ''}>Đã giao</option>
                                        <option value="in_progress" ${ticket.status == 'in_progress' ? 'selected' : ''}>Đang xử lý</option>
                                        <option value="resolved" ${ticket.status == 'resolved' ? 'selected' : ''}>Đã xử lý</option>
                                        <option value="closed" ${ticket.status == 'closed' ? 'selected' : ''}>Đã đóng</option>
                                        <option value="rejected" ${ticket.status == 'rejected' ? 'selected' : ''}>Từ chối</option>
                                    </select>
                                </div>
                                <div class="sidebar-form-row"><label for="priority">Mức độ ưu tiên</label>
                                    <select id="priority" name="priority" class="form-control">
                                        <%-- THÊM thuộc tính `value` cho mỗi option --%>
                                        <option value="critical" ${ticket.priority == 'critical' ? 'selected' : ''}>Khẩn cấp</option>
                                        <option value="high" ${ticket.priority == 'high' ? 'selected' : ''}>Cao</option>
                                        <option value="medium" ${ticket.priority == 'medium' ? 'selected' : ''}>Thông thường</option>
                                        <option value="low" ${ticket.priority == 'low' ? 'selected' : ''}>Thấp</option>
                                    </select>
                                </div>
                                <div class="sidebar-form-row"><label for="employeeId">Gán cho nhân viên (*)</label>
                                    <select id="employeeId" name="employeeId" class="form-control" required>
                                        <c:forEach var="employee" items="${employeeList}"><option value="${employee.id}" ${employee.id == ticket.assignedToId ? 'selected' : ''}>${employee.lastName} ${employee.middleName} ${employee.firstName}</option></c:forEach>
                                        </select>
                                    </div>
                                    <div class="sidebar-form-row">
                                        <label>Ngày tạo</label>
                                    <fmt:formatDate value="${ticket.createdAt}" pattern="HH:mm dd/MM/yyyy" var="formattedDate"/>
                                    <input type="text" class="form-control" value="${formattedDate}" readonly>
                                </div>
                                <div class="sidebar-form-row"><label>Chi phí dự kiến</label><div class="radio-group"><label><input type="radio" name="isBillable" value="true" ${ticket.isBillable ? 'checked' : ''}> Có</label><label><input type="radio" name="isBillable" value="false" ${!ticket.isBillable ? 'checked' : ''}> Không</label></div></div>
                                <div id="amount-group" class="sidebar-form-row" style="display: ${ticket.isBillable ? 'block' : 'none'};"><label for="amount">Số tiền dự kiến (VND)</label><input type="number" id="amount" name="amount" class="form-control" value="${ticket.estimatedCost}"></div>
                            </div>
                        </div>
                    </div>
                </form>
            </main>
        </div>
        <script>
            // Nhận chuỗi JSON từ Controller và để JavaScript tự phân tích
            const allProducts = ${allProductsJson};
            const existingDevices = ${existingDevicesJson};

            // Index bắt đầu cho các thiết bị mới sẽ được thêm vào
            // Chúng ta có thể khởi tạo nó ở đây hoặc trong file JS
            let deviceIndex = 1;
        </script>

        <script src="${pageContext.request.contextPath}/js/editTransaction.js?v=<%= System.currentTimeMillis()%>"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
