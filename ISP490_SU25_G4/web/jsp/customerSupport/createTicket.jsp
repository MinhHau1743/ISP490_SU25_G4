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
                <form id="createTicketForm" class="page-content" action="${pageContext.request.contextPath}/ticket" method="post">
                    <input type="hidden" name="action" value="create">
                    <div class="detail-header">
                        <a href="${pageContext.request.contextPath}/ticket?action=list" class="back-link"><i data-feather="arrow-left"></i><span>Quay lại</span></a>
                        <button type="submit" class="btn btn-primary"><i data-feather="plus-circle"></i>Tạo Phiếu</button>
                    </div>
                    <div class="detail-layout">
                        <div class="main-column">
                            <div class="detail-card">
                                <h2 class="card-title">Thông tin Phiếu Giao Việc</h2>
                                <div class="form-grid">
                                    <div class="form-group"><label>Mã Phiếu</label><input type="text" class="form-control" value="(Tự động tạo)" readonly></div>
                                    <div class="form-group">
                                        <label for="enterpriseId">Khách hàng (*)</label>
                                        <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn khách hàng --</option>
                                            <c:forEach var="customer" items="${customerList}">
                                                <option value="${customer.id}">${customer.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="contractId">Hợp đồng</label>
                                        <%-- Bỏ 'disabled' và dùng JSTL để lặp --%>
                                        <select id="contractId" name="contractId" class="form-control">
                                            <option value="">-- Chọn hợp đồng  --</option>
                                            <c:forEach var="contract" items="${contractList}">
                                                <option value="${contract.id}">${contract.contractCode}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn loại phiếu --</option>
                                            <c:forEach var="service" items="${serviceList}"><option value="${service.id}">${service.name}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group full-width"><label for="description">Mô tả chung (*)</label><textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea></div>
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
                                <div class="sidebar-form-row"><label for="priority">Mức độ ưu tiên</label><select id="priority" name="priority" class="form-control"><option>Thông thường</option><option>Cao</option><option>Khẩn cấp</option></select></div>
                                <div class="sidebar-form-row"><label for="employeeId">Gán cho nhân viên</label><select id="employeeId" name="employeeId" class="form-control" required><option value="" disabled selected>-- Chọn kỹ thuật viên --</option><c:forEach var="employee" items="${employeeList}"><option value="${employee.id}">${employee.lastName} ${employee.middleName} ${employee.firstName}</option></c:forEach></select></div>
                                    <div class="sidebar-form-row"><label>Ngày tạo</label><input type="date" id="createdDate" name="createdDate" class="form-control" readonly></div>
                                    <div class="sidebar-form-row"><label>Chi phí </label><div class="radio-group"><label><input type="radio" name="isBillable" value="true"> Có</label><label><input type="radio" name="isBillable" value="false" checked> Không</label></div></div>
                                    <div id="amount-group" class="sidebar-form-row" style="display: none;"><label for="amount">Số tiền (VND)</label><input type="number" id="amount" name="amount" class="form-control" value="0"></div>
                                </div>
                            </div>
                        </div>
                    </form>
                </main>
            </div>

            <script>const contextPath = '${pageContext.request.contextPath}';</script>    
        <script src="${pageContext.request.contextPath}/js/createTicket.js?v=<%= System.currentTimeMillis()%>"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>

    </body>
</html>