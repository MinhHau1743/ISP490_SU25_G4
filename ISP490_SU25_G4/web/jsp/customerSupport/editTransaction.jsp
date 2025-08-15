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
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
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
                                                            <option value="${p.id}" ${p.id == schedule.provinceId ? 'selected' : ''}>
                                                                ${p.name}
                                                            </option>
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
                                <div class="sidebar-form-row">
                                    <label for="employeeId">Nhân viên phụ trách</label>
                                    <div class="input-with-icon">
                                        <select id="employeeId2" name="employeesId" class="form-control" multiple required>
                                            <%-- Không cần option mặc định trong giao diện chọn nhiều --%>
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}" <c:if test="${assignedUserIds.contains(employee.id)}">selected</c:if>>${employee.lastName} ${employee.middleName} ${employee.firstName}</option>
                                            </c:forEach> 
                                        </select>
                                    </div>
                                </div>


                                <div class="sidebar-form-row">
                                    <label>Ngày tạo</label>
                                    <div class="input-with-icon">
                                        <input type="date" id="createdDate" 
                                               name="createdDate" class="form-control" readonly 
                                               value="<fmt:formatDate value='${ticket.createdAt}' pattern='yyyy-MM-dd' />">
                                    </div>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khoảng ngày</label>
                                    <div class="control">
                                        <input type="date" id="scheduled_date" name="scheduled_date" value="${schedule.scheduledDate}" class="form-control" required>
                                        <div class="field-hint">Bắt đầu</div>
                                    </div>
                                    <div class="control">
                                        <input type="date" id="end_date" name="end_date" value="${schedule.endDate}" class="form-control">
                                        <div class="field-hint">Kết thúc</div>
                                    </div>
                                </div>

                                <div class="sidebar-form-row row-2col">
                                    <label>Khung giờ</label>
                                    <div class="control">
                                        <input type="time" id="start_time" name="start_time" value="${schedule.startTime}" class="form-control">
                                        <div class="field-hint">Từ</div>
                                    </div>
                                    <div class="control">
                                        <input type="time" id="end_time" name="end_time" value="${schedule.endTime}" class="form-control">
                                        <div class="field-hint">Đến</div>
                                    </div>
                                </div>
                                <div class="sidebar-form-row">
                                    <label>Màu sắc</label>
                                    <div class="color-palette">
                                        <span class="color-swatch" data-color="#007bff" style="background-color: #007bff;"></span>
                                        <span class="color-swatch" data-color="#dc3545" style="background-color: #dc3545;"></span>
                                        <span class="color-swatch" data-color="#28a745" style="background-color: #28a745;"></span>
                                        <span class="color-swatch" data-color="#ffc107" style="background-color: #ffc107;"></span>
                                        <span class="color-swatch" data-color="#fd7e14" style="background-color: #fd7e14;"></span>
                                        <span class="color-swatch" data-color="#17a2b8" style="background-color: #17a2b8;"></span>
                                        <span class="color-swatch" data-color="#6610f2" style="background-color: #6610f2;"></span>
                                        <span class="color-swatch" data-color="#343a40" style="background-color: #343a40;"></span>
                                        <span class="color-swatch" data-color="#e83e8c" style="background-color: #e83e8c;"></span>
                                        <span class="color-swatch" data-color="#6c757d" style="background-color: #6c757d;"></span> 
                                        <span class="color-swatch" data-color="#20c997" style="background-color: #20c997;"></span> 
                                        <span class="color-swatch" data-color="#4B0082" style="background-color: #4B0082;"></span> 
                                        <span class="color-swatch" data-color="#ADFF2F" style="background-color: #ADFF2F;"></span> 
                                        <span class="color-swatch" data-color="#A52A2A" style="background-color: #A52A2A;"></span> 
                                        <span class="color-swatch" data-color="#FFD700" style="background-color: #FFD700;"></span> 
                                        <span class="color-swatch" data-color="#87CEEB" style="background-color: #87CEEB;"></span>
                                    </div>

                                    <%-- THÊM DÒNG NÀY VÀO --%>
                                    <%-- Giá trị mặc định là #007bff, hoặc lấy từ đối tượng nếu là form edit --%>
                                    <input type="hidden" id="color" name="color" value="${not empty schedule.color ? schedule.color : '#007bff'}">
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
        <script>
            window.PRESELECTED_ADDRESS = {
                provinceId: '${schedule.provinceId}',
                districtId: '${schedule.districtId}',
                wardId: '${schedule.wardId}'
            };
        </script>
        <script>
            $(document).ready(function () {
                // Gọi Select2 cho thẻ select có id là 'employeeId'
                $('#employeeId2').select2({
                    placeholder: "Chọn nhân viên phụ trách",
                    allowClear: true
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/editTransaction.js?v=<%= System.currentTimeMillis()%>"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>
