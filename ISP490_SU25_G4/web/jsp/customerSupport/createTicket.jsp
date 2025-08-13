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
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />

        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
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
                                    <div class="form-group full-width">
                                        <label>Tiêu đề</label>
                                        <input type="text" name="title" class="form-control" value="">
                                    </div>
                                    <div class="form-group">
                                        <label>Mã Phiếu</label>
                                        <input type="text" class="form-control" value="(Tự động tạo)" readonly>
                                    </div>
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
                                        <select id="contractId" name="contractId" class="form-control">
                                            <option value="">-- Chọn hợp đồng --</option>
                                            <c:forEach var="contract" items="${contractList}">
                                                <option value="${contract.id}">${contract.contractCode}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="serviceId">Loại phiếu (*)</label>
                                        <select id="serviceId" name="serviceId" class="form-control" required>
                                            <option value="" disabled selected>-- Chọn loại phiếu --</option>
                                            <c:forEach var="service" items="${serviceList}">
                                                <option value="${service.id}">${service.name}</option>
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
                                                            <option value="${p.id}">${p.name}</option>
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
                                                <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group full-width">
                                        <label for="description">Mô tả chung (*)</label>
                                        <textarea id="description" name="description" class="form-control" rows="4" placeholder="Mô tả rõ ràng vấn đề khách hàng đang gặp phải..." required></textarea>
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
                                            <option value="medium">Thông thường</option>
                                            <option value="high">Cao</option>
                                            <option value="urgent">Khẩn cấp</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="sidebar-form-row">
                                    <label for="status">Trạng thái</label>
                                    <div class="input-with-icon">
                                        <select id="status" name="status" class="form-control" required>
                                            <option value="new">Mới tạo</option>
                                            <option value="in_progress">Đang thực hiện</option>
                                            <option value="resolved">Đã giải quyết</option>
                                            <option value="closed">Đã đóng</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="sidebar-form-row">
                                    <label for="employeeId">Nhân viên phụ trách</label>
                                    <div class="input-with-icon">
                                        <select id="employeeId2" name="employeesId" class="form-control" multiple required>
                                            <%-- Không cần option mặc định trong giao diện chọn nhiều --%>
                                            <c:forEach var="employee" items="${employeeList}">
                                                <option value="${employee.id}">${employee.lastName} ${employee.middleName} ${employee.firstName}</option>
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
        <%-- Đặt khối script này ở cuối file /jsp/customerSupport/createTicket.jsp, trước thẻ </body> --%>
        <!-- Ở cuối file JSP, trước các script khác -->
        <script>
            window.contextPath = '<%= request.getContextPath()%>';
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
        <!-- nạp file js nghiệp vụ sau khi đã có APP_CONFIG -->
        <script src="${pageContext.request.contextPath}/js/createTicket.js" defer></script>
        <!-- file khác như mainMenu.js để sau cũng được -->
        <script src="${pageContext.request.contextPath}/js/mainMenu.js" defer></script>

    </body>
</html>