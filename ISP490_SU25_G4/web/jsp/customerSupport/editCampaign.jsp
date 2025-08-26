<%--
    Document   : editCampaign.jsp
    Description : Form chỉnh sửa chiến dịch, phiên bản cuối cùng với logic chọn một nhân viên và validation ngày giờ.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="currentPage" value="campaigns"/>
<c:set var="BASE_URL" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sửa chiến dịch: ${campaign.name} - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/addNewCampaign.css">

        <script src="https://unpkg.com/feather-icons"></script>
    </head>
    <body data-base-url="${BASE_URL}">
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <div class="main-content">
                <jsp:include page="/header.jsp"/>

                <div class="campaign-page-container">
                    <div class="page-header">
                        <h1>Chỉnh sửa chiến dịch</h1>
                        <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">
                            <i data-feather="arrow-left"></i> Quay lại
                        </a>
                    </div>

                    <div class="campaign-form-container">
                        <c:if test="${empty campaign}">
                            <div class="error-message">Không tìm thấy chiến dịch để chỉnh sửa.</div>
                        </c:if>

                        <c:if test="${not empty campaign}">
                            <form id="campaignForm" action="${BASE_URL}/update-campaign" method="post">

                                <input type="hidden" name="campaignId" value="${campaign.campaignId}">

                                <%-- Khối thiết lập biến để xử lý logic hiển thị trên form --%>
                                <c:set var="campaignName" value="${not empty param.name ? param.name : campaign.name}" />
                                <c:set var="campaignDescription" value="${not empty param.description ? param.description : campaign.description}" />
                                <c:set var="selectedTypeId" value="${not empty param.typeId ? param.typeId : campaign.typeId}" />
                                <c:set var="selectedEnterpriseId" value="${not empty param.enterpriseId ? param.enterpriseId : campaign.enterpriseId}" />

                                <c:set var="scheduledDate" value="${not empty param.scheduledDate ? param.scheduledDate : maintenanceSchedule.scheduledDate}" />
                                <c:set var="endDate" value="${not empty param.endDate ? param.endDate : maintenanceSchedule.endDate}" />
                                <c:set var="startTime" value="${not empty param.startTime ? param.startTime : fn:substring(maintenanceSchedule.startTime,0,5)}" />
                                <c:set var="endTime" value="${not empty param.endTime ? param.endTime : fn:substring(maintenanceSchedule.endTime,0,5)}" />

                                <c:set var="selectedProvinceId" value="${not empty param.province ? param.province : maintenanceSchedule.address.provinceId}" />
                                <c:set var="selectedDistrictId" value="${not empty param.district ? param.district : maintenanceSchedule.address.districtId}" />
                                <c:set var="selectedWardId" value="${not empty param.ward ? param.ward : maintenanceSchedule.address.wardId}" />
                                <c:set var="streetAddress" value="${not empty param.streetAddress ? param.streetAddress : maintenanceSchedule.address.streetAddress}" />

                                <c:set var="selectedStatusId" value="${not empty param.statusId ? param.statusId : maintenanceSchedule.statusId}" />
                                <c:set var="selectedColor" value="${not empty param.color ? param.color : (not empty maintenanceSchedule.color ? maintenanceSchedule.color : '#0d9488')}" />

                                <c:if test="${not empty maintenanceSchedule.assignedUserIds}">
                                    <c:set var="dbUserId" value="${maintenanceSchedule.assignedUserIds[0]}" />
                                </c:if>
                                <c:set var="selectedUserId" value="${not empty param.assignedUserId ? param.assignedUserId : dbUserId}" />


                                <%-- Khối hiển thị lỗi chung --%>
                                <div id="formErrorContainer" class="error-message" style="${not empty errorMessage || not empty fieldErrors ? '' : 'display:none;'}; margin-bottom:16px;">
                                    <c:if test="${not empty errorMessage}">
                                        <strong>${fn:escapeXml(errorMessage)}</strong>
                                    </c:if>
                                    <c:if test="${not empty fieldErrors}">
                                        <ul style="margin:8px 0 0 18px;">
                                            <c:forEach var="e" items="${fieldErrors}">
                                                <li>${fn:escapeXml(e.value)}</li>
                                            </c:forEach>
                                        </ul>
                                    </c:if>
                                </div>

                                <h3 class="sub-header">Thông tin chiến dịch</h3>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="campaignName">Tên chiến dịch <span class="required-star">*</span></label>
                                        <input type="text" id="campaignName" name="name" class="form-control" value="${campaignName}" required>
                                    </div>
                                    <div class="form-group">
                                        <label>Mã chiến dịch</label>
                                        <input type="text" class="form-control" value="${campaign.campaignCode}" readonly>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="campaignType">Loại Chiến dịch <span class="required-star">*</span></label>
                                        <select id="campaignType" name="typeId" class="form-control" required>
                                            <c:forEach var="type" items="${campaignTypes}">
                                                <option value="${type.id}" ${selectedTypeId == type.id ? 'selected' : ''}>
                                                    ${type.typeName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="enterpriseId">Khách hàng <span class="required-star">*</span></label>
                                        <select id="enterpriseId" name="enterpriseId" class="form-control" required>
                                            <c:forEach var="enterprise" items="${enterpriseList}">
                                                <option value="${enterprise.id}" ${selectedEnterpriseId == enterprise.id ? 'selected' : ''}>
                                                    ${enterprise.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="description">Mô tả chiến dịch</label>
                                    <textarea id="description" name="description" class="form-control" rows="3">${campaignDescription}</textarea>
                                </div>

                                <h3 class="sub-header">Thông tin lịch trình & Địa điểm</h3>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="scheduledDate">Ngày bắt đầu <span class="required-star">*</span></label>
                                        <input type="date" id="scheduledDate" name="scheduledDate" class="form-control" value="${scheduledDate}" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="endDate">Ngày kết thúc</label>
                                        <input type="date" id="endDate" name="endDate" class="form-control" value="${endDate}">
                                    </div>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="startTime">Giờ bắt đầu</label>
                                        <input type="time" id="startTime" name="startTime" class="form-control" value="${startTime}">
                                    </div>
                                    <div class="form-group">
                                        <label for="endTime">Giờ kết thúc</label>
                                        <input type="time" id="endTime" name="endTime" class="form-control" value="${endTime}">
                                    </div>
                                </div>

                                <div class="form-row-x3">
                                    <div class="form-group">
                                        <label for="province">Tỉnh/Thành phố <span class="required-star">*</span></label>
                                        <select id="province" name="province" class="form-control" required>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" ${selectedProvinceId == p.id ? 'selected' : ''}>
                                                    ${p.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="district">Quận/Huyện <span class="required-star">*</span></label>
                                        <select id="district" name="district" class="form-control" required ${empty districts and empty selectedDistrictId ? 'disabled' : ''}>
                                            <option value="" disabled>-- Chọn Quận/Huyện --</option>
                                            <c:if test="${not empty districts}">
                                                <c:forEach var="d" items="${districts}">
                                                    <option value="${d.id}" ${selectedDistrictId == d.id ? 'selected' : ''}>
                                                        ${d.name}
                                                    </option>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="ward">Phường/Xã <span class="required-star">*</span></label>
                                        <select id="ward" name="ward" class="form-control" required ${empty wards and empty selectedWardId ? 'disabled' : ''}>
                                            <option value="" disabled>-- Chọn Phường/Xã --</option>
                                            <c:if test="${not empty wards}">
                                                <c:forEach var="w" items="${wards}">
                                                    <option value="${w.id}" ${selectedWardId == w.id ? 'selected' : ''}>
                                                        ${w.name}
                                                    </option>
                                                </c:forEach>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="streetAddress">Địa chỉ cụ thể <span class="required-star">*</span></label>
                                    <input type="text" id="streetAddress" name="streetAddress" class="form-control" placeholder="Số nhà, tên đường..." value="${streetAddress}" required>
                                </div>

                                <h3 class="sub-header">Phân công & Hiển thị</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="assignedUserId">Nhân viên thực hiện <span class="required-star">*</span></label>
                                        <select id="assignedUserId" name="assignedUserId" class="form-control" required>
                                            <option value="" disabled ${empty selectedUserId ? 'selected' : ''}>-- Chọn nhân viên --</option>
                                            <c:forEach var="user" items="${userList}">
                                                <option value="${user.id}" ${selectedUserId == user.id ? 'selected' : ''}>
                                                    ${user.lastName} ${user.firstName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="statusId">Trạng thái <span class="required-star">*</span></label>
                                        <select id="statusId" name="statusId" class="form-control" required>
                                            <c:forEach var="status" items="${statusList}">
                                                <option value="${status.id}" ${selectedStatusId == status.id ? 'selected' : ''}>
                                                    ${status.statusName}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label>Màu sắc hiển thị</label>
                                    <div class="color-picker-container" id="colorPicker">
                                        <input type="hidden" name="color" id="selectedColor" value="${selectedColor}">
                                        <div class="color-dot" data-color="#0d9488" style="background-color:#0d9488;"></div>
                                        <div class="color-dot" data-color="#dc3545" style="background-color:#dc3545;"></div>
                                        <div class="color-dot" data-color="#28a745" style="background-color:#28a745;"></div>
                                        <div class="color-dot" data-color="#ffc107" style="background-color:#ffc107;"></div>
                                        <div class="color-dot" data-color="#fd7e14" style="background-color:#fd7e14;"></div>
                                        <div class="color-dot" data-color="#17a2b8" style="background-color:#17a2b8;"></div>
                                        <div class="color-dot" data-color="#6f42c1" style="background-color:#6f42c1;"></div>
                                        <div class="color-dot" data-color="#343a40" style="background-color:#343a40;"></div>
                                    </div>
                                </div>

                                <div class="form-actions">
                                    <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">Hủy</a>
                                    <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        
        <%-- PHẦN CODE ĐƯỢC THÊM VÀO ĐỂ VALIDATE --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace(); // Dành cho icon
                
                // Các hằng số cho các trường input ngày và giờ
                const scheduledDate = document.getElementById('scheduledDate');
                const endDate = document.getElementById('endDate');
                const startTime = document.getElementById('startTime');
                const endTime = document.getElementById('endTime');

                // Hàm cập nhật các quy tắc validation của trình duyệt
                function updateDateTimeConstraints() {
                    // 1. Luôn đảm bảo ngày kết thúc không thể chọn trước ngày bắt đầu
                    if (scheduledDate.value) {
                        endDate.min = scheduledDate.value;
                    } else {
                        endDate.removeAttribute('min');
                    }
                    
                    // 2. Nếu ngày bắt đầu và ngày kết thúc là MỘT, thì giờ kết thúc không thể trước giờ bắt đầu
                    if (startTime.value && scheduledDate.value && endDate.value && scheduledDate.value === endDate.value) {
                        endTime.min = startTime.value;
                    } else {
                        // Nếu khác ngày, xóa bỏ ràng buộc về giờ
                        endTime.removeAttribute('min');
                    }
                }
                
                // Gắn sự kiện 'change' để gọi hàm cập nhật mỗi khi người dùng thay đổi giá trị
                if(scheduledDate) scheduledDate.addEventListener('change', updateDateTimeConstraints);
                if(endDate) endDate.addEventListener('change', updateDateTimeConstraints);
                if(startTime) startTime.addEventListener('change', updateDateTimeConstraints);
                
                // Gọi hàm một lần khi trang được tải xong để thiết lập quy tắc ban đầu
                updateDateTimeConstraints();
            });
        </script>

        <script src="${BASE_URL}/js/mainMenu.js"></script>
        <script src="${BASE_URL}/js/editCampaign.js" defer></script>
    </body>
</html>