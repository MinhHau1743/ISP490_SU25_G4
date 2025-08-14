<%--
    Document   : addNewCampaign.jsp
    Created on : Aug 15, 2025
    Author     : DPCRM Assistant (Refactored)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentPage" value="campaigns" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm chiến dịch & Lịch trình - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/addNewCampaign.css">

        <script src="https://unpkg.com/feather-icons"></script>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <div class="content-area">
                <main class="main-content">
                    <div class="modal-overlay">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h2>Thêm chiến dịch mới</h2>
                                <button type="button" class="modal-close-btn" onclick="window.location.href = '${BASE_URL}/campaigns'">
                                    <i data-feather="x"></i>
                                </button>
                            </div>

                            <div id="formErrorContainer" class="error-message" style="display:none;"></div>

                            <form id="campaignForm" action="${BASE_URL}/create-campaign" method="post">

                                <h3 class="sub-header">Thông tin chiến dịch</h3>
                                <div class="form-group">
                                    <label for="campaignName">Tên chiến dịch <span class="required-star">*</span></label>
                                    <input type="text" id="campaignName" name="name" placeholder="Ví dụ: Tri ân khách hàng cuối năm..." value="${param.name}">
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="campaignType">Loại Chiến dịch <span class="required-star">*</span></label>
                                        <select id="campaignType" name="typeId" required>
                                            <option value="">-- Chọn loại chiến dịch --</option>
                                            <c:forEach var="type" items="${campaignTypes}"><option value="${type.id}" ${param.typeId == type.id ? 'selected' : ''}>${type.typeName}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label for="enterpriseId">Khách hàng <span class="required-star">*</span></label>
                                            <select id="enterpriseId" name="enterpriseId" required>
                                                <option value="">-- Chọn khách hàng --</option>
                                            <c:forEach var="enterprise" items="${enterpriseList}"><option value="${enterprise.id}" ${param.enterpriseId == enterprise.id ? 'selected' : ''}>${enterprise.name}</option></c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="statusId">Trạng thái ban đầu <span class="required-star">*</span></label>
                                            <select id="statusId" name="statusId" required>
                                                <option value="">-- Chọn trạng thái --</option>
                                            <c:forEach var="status" items="${statusList}"><option value="${status.id}" ${param.statusId == status.id ? 'selected' : ''}>${status.statusName}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group"></div>
                                    </div>
                                    <div class="form-group">
                                        <label for="description">Mô tả chiến dịch</label>
                                        <textarea id="description" name="description" rows="3" placeholder="Nhập mô tả chi tiết cho chiến dịch...">${param.description}</textarea>
                                </div>

                                <h3 class="sub-header">Thông tin lịch trình</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="scheduledDate">Ngày bắt đầu <span class="required-star">*</span></label>
                                        <input type="date" id="scheduledDate" name="scheduledDate" value="${param.scheduledDate}" required>
                                    </div>
                                    <div class="form-group">
                                        <label for="endDate">Ngày kết thúc</label>
                                        <input type="date" id="endDate" name="endDate" value="${param.endDate}">
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="startTime">Giờ bắt đầu</label>
                                        <input type="time" id="startTime" name="startTime" value="${param.startTime}">
                                    </div>
                                    <div class="form-group">
                                        <label for="endTime">Giờ kết thúc</label>
                                        <input type="time" id="endTime" name="endTime" value="${param.endTime}">
                                    </div>
                                </div>

                                <div class="form-group-container">
                                    <div class="form-row-x3">
                                        <div class="form-group">
                                            <label for="province">Tỉnh/Thành phố (*)</label>
                                            <select id="province" name="province" required>
                                                <option value="" disabled selected>-- Chọn Tỉnh/Thành --</option>
                                                <c:forEach var="p" items="${provinces}"><option value="${p.id}">${p.name}</option></c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="district">Quận/Huyện (*)</label>
                                                <select id="district" name="district" required disabled>
                                                    <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="ward">Phường/Xã (*)</label>
                                                <select id="ward" name="ward" required disabled>
                                                    <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="streetAddress">Địa chỉ cụ thể (*)</label>
                                            <input type="text" id="streetAddress" name="streetAddress" placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." required>
                                        </div>
                                    </div>

                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="assignedUserId">Nhân viên thực hiện <span class="required-star">*</span></label>
                                            <select id="assignedUserId" name="assignedUserId" required>
                                                <option value="">-- Chọn nhân viên --</option>
                                            <c:forEach var="user" items="${userList}"><option value="${user.id}" ${param.assignedUserId == user.id ? 'selected' : ''}>${user.lastName} ${user.firstName}</option></c:forEach>
                                            </select>
                                        </div>
                                        <div class="form-group">
                                            <label>Màu sắc hiển thị</label>
                                            <div class="color-picker-container" id="colorPicker">
                                                <input type="hidden" name="color" id="selectedColor" value="${not empty param.color ? param.color : '#009688'}">
                                            <div class="color-dot" data-color="#009688" style="background-color: #009688;"></div>
                                            <div class="color-dot" data-color="#dc3545" style="background-color: #dc3545;"></div>
                                            <div class="color-dot" data-color="#28a745" style="background-color: #28a745;"></div>
                                            <div class="color-dot" data-color="#ffc107" style="background-color: #ffc107;"></div>
                                            <div class="color-dot" data-color="#fd7e14" style="background-color: #fd7e14;"></div>
                                            <div class="color-dot" data-color="#17a2b8" style="background-color: #17a2b8;"></div>
                                            <div class="color-dot" data-color="#6f42c1" style="background-color: #6f42c1;"></div>
                                            <div class="color-dot" data-color="#343a40" style="background-color: #343a40;"></div>
                                        </div>
                                    </div>
                                </div>

                                <div class="modal-footer">
                                    <button type="submit" class="btn-save-campaign">Lưu và tạo lịch trình</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </main>
            </div>
        </div>

        <script>
            // Đợi cho toàn bộ trang HTML được tải xong rồi mới chạy JavaScript
            document.addEventListener('DOMContentLoaded', function () {

                // Kích hoạt thư viện icon Feather
                feather.replace({width: '1em', height: '1em'});

                // ==========================================================
                // KHỐI 1: XỬ LÝ ĐỊA CHỈ ĐỘNG
                // ==========================================================
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');

                if (provinceSelect) {
                    provinceSelect.addEventListener('change', function () {
                        const provinceId = this.value;

                        districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                        districtSelect.disabled = true;
                        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                        wardSelect.disabled = true;

                        if (provinceId) {
                            fetch('${BASE_URL}/create-campaign?action=getDistricts&id=' + provinceId)
                                    .then(response => {
                                        if (!response.ok)
                                            throw new Error('Network response was not ok');
                                        return response.json();
                                    })
                                    .then(data => {
                                        districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                                        data.forEach(function (district) {
                                            const option = document.createElement('option');
                                            option.value = district.id;
                                            option.textContent = district.name;
                                            districtSelect.appendChild(option);
                                        });
                                        districtSelect.disabled = false;
                                    })
                                    .catch(error => {
                                        console.error('Lỗi khi tải quận/huyện:', error);
                                        districtSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                                        districtSelect.disabled = false;
                                    });
                        }
                    });
                }

                if (districtSelect) {
                    districtSelect.addEventListener('change', function () {
                        const districtId = this.value;
                        wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                        wardSelect.disabled = true;

                        if (districtId) {
                            fetch('${BASE_URL}/create-campaign?action=getWards&id=' + districtId)
                                    .then(response => {
                                        if (!response.ok)
                                            throw new Error('Network response was not ok');
                                        return response.json();
                                    })
                                    .then(data => {
                                        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                                        data.forEach(function (ward) {
                                            const option = document.createElement('option');
                                            option.value = ward.id;
                                            option.textContent = ward.name;
                                            wardSelect.appendChild(option);
                                        });
                                        wardSelect.disabled = false;
                                    })
                                    .catch(error => {
                                        console.error('Lỗi khi tải phường/xã:', error);
                                        wardSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                                        wardSelect.disabled = false;
                                    });
                        }
                    });
                }

                // ==========================================================
                // KHỐI 2: VALIDATE FORM TRƯỚC KHI SUBMIT
                // ==========================================================
                const campaignForm = document.getElementById('campaignForm');
                const errorContainer = document.getElementById('formErrorContainer');

                if (campaignForm) {
                    campaignForm.addEventListener('submit', function (event) {
                        event.preventDefault();
                        let errors = [];
                        const requiredFields = [
                            {id: 'campaignName', name: 'Tên chiến dịch'},
                            {id: 'campaignType', name: 'Loại chiến dịch'},
                            {id: 'enterpriseId', name: 'Khách hàng'},
                            {id: 'statusId', name: 'Trạng thái ban đầu'},
                            {id: 'scheduledDate', name: 'Ngày bắt đầu'},
                            {id: 'assignedUserId', name: 'Nhân viên thực hiện'},
                            {id: 'province', name: 'Tỉnh/Thành phố'},
                            {id: 'district', name: 'Quận/Huyện'},
                            {id: 'ward', name: 'Phường/Xã'},
                            {id: 'streetAddress', name: 'Địa chỉ cụ thể'}
                        ];

                        requiredFields.forEach(function (field) {
                            const input = document.getElementById(field.id);
                            if (!input || !input.value || (typeof input.value === 'string' && input.value.trim() === '')) {
                                errors.push(`Vui lòng nhập/chọn thông tin cho trường "${field.name}".`);
                            }
                        });

                        const scheduledDateVal = document.getElementById('scheduledDate').value;
                        const endDateVal = document.getElementById('endDate').value;
                        if (scheduledDateVal && endDateVal && new Date(endDateVal) < new Date(scheduledDateVal)) {
                            errors.push('Lỗi: Ngày kết thúc không được sớm hơn ngày bắt đầu.');
                        }

                        if (errors.length > 0) {
                            errorContainer.innerHTML = '<strong>Vui lòng sửa các lỗi sau:</strong><br>' + errors.join('<br>');
                            errorContainer.style.display = 'block';
                            errorContainer.scrollIntoView({behavior: 'smooth', block: 'start'});
                        } else {
                            errorContainer.style.display = 'none';
                            campaignForm.submit();
                        }
                    });
                }

                // ==========================================================
                // KHỐI 3: XỬ LÝ COLOR PICKER
                // ==========================================================
                const colorPicker = document.getElementById('colorPicker');
                const selectedColorInput = document.getElementById('selectedColor');

                if (colorPicker && selectedColorInput) {
                    function setInitialColor() {
                        const initialColor = selectedColorInput.value;
                        const dots = colorPicker.querySelectorAll('.color-dot');
                        let found = false;
                        dots.forEach(dot => {
                            if (dot.dataset.color.toLowerCase() === initialColor.toLowerCase()) {
                                dot.classList.add('selected');
                                dot.innerHTML = feather.icons.check.toSvg({'stroke-width': 3, 'color': 'white'});
                                found = true;
                            }
                        });
                        if (!found && dots.length > 0) {
                            dots[0].classList.add('selected');
                            dots[0].innerHTML = feather.icons.check.toSvg({'stroke-width': 3, 'color': 'white'});
                            selectedColorInput.value = dots[0].dataset.color;
                        }
                    }
                    setInitialColor();

                    colorPicker.addEventListener('click', function (event) {
                        const clickedDot = event.target.closest('.color-dot');
                        if (!clickedDot)
                            return;

                        const allDots = colorPicker.querySelectorAll('.color-dot');
                        allDots.forEach(dot => {
                            dot.classList.remove('selected');
                            dot.innerHTML = '';
                        });

                        clickedDot.classList.add('selected');
                        clickedDot.innerHTML = feather.icons.check.toSvg({'stroke-width': 3, 'color': 'white'});
                        selectedColorInput.value = clickedDot.dataset.color;
                    });
                }
            });
        </script>

        <script src="${BASE_URL}/js/mainMenu.js"></script>
    </body>
</html>