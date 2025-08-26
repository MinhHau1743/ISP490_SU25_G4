<%--
    Document   : createCampaign.jsp
    Created on : 27/08/2025
    Author     : DPCRM Assistant (Final Version - Detailed)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentPage" value="campaigns" />
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm chiến dịch mới - DPCRM</title>

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
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <div class="main-content">
                <jsp:include page="/header.jsp"/>

                <div class="campaign-page-container">

                    <div class="page-header">
                        <h1>Thêm chiến dịch mới</h1>
                        <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">
                            <i data-feather="arrow-left" style="width:16px; height:16px;"></i>
                            Quay lại
                        </a>
                    </div>

                    <div class="campaign-form-container">
                        <form id="campaignForm" action="${BASE_URL}/create-campaign" method="post">

                            <%-- Khối lỗi tổng hợp từ server và client --%>
                            <div id="formErrorContainer"
                                 class="error-message"
                                 style="${not empty errorMessage || not empty fieldErrors ? '' : 'display:none;'}; margin-bottom:16px;">
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
                                    <input type="text"
                                           id="campaignName"
                                           name="name"
                                           class="form-control ${not empty fieldErrors['name'] ? 'invalid' : ''}"
                                           placeholder="Ví dụ: Tri ân khách hàng cuối năm..."
                                           value="${param.name}" required>
                                    <c:if test="${not empty fieldErrors['name']}">
                                        <div class="field-error">${fieldErrors['name']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="campaignCode">Mã chiến dịch</label>
                                    <input type="text" id="campaignCode" name="campaignCode" class="form-control" placeholder="Tự động tạo" readonly>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="campaignType">Loại Chiến dịch <span class="required-star">*</span></label>
                                    <select id="campaignType"
                                            name="typeId"
                                            class="form-control ${not empty fieldErrors['typeId'] ? 'invalid' : ''}"
                                            required>
                                        <option value="">-- Chọn loại chiến dịch --</option>
                                        <c:forEach var="type" items="${campaignTypes}">
                                            <option value="${type.id}" ${param.typeId == type.id ? 'selected' : ''}>
                                                ${type.typeName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${not empty fieldErrors['typeId']}">
                                        <div class="field-error">${fieldErrors['typeId']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="enterpriseId">Khách hàng <span class="required-star">*</span></label>
                                    <select id="enterpriseId"
                                            name="enterpriseId"
                                            class="form-control ${not empty fieldErrors['enterpriseId'] ? 'invalid' : ''}"
                                            required>
                                        <option value="">-- Chọn khách hàng --</option>
                                        <c:forEach var="enterprise" items="${enterpriseList}">
                                            <option value="${enterprise.id}" ${param.enterpriseId == enterprise.id ? 'selected' : ''}>
                                                ${enterprise.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${not empty fieldErrors['enterpriseId']}">
                                        <div class="field-error">${fieldErrors['enterpriseId']}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="description">Mô tả chiến dịch</label>
                                <textarea id="description" name="description" class="form-control" rows="3" placeholder="Nhập mô tả chi tiết cho chiến dịch...">${param.description}</textarea>
                            </div>

                            <h3 class="sub-header">Thông tin lịch trình & Địa điểm</h3>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="scheduledDate">Ngày bắt đầu <span class="required-star">*</span></label>
                                    <input type="date"
                                           id="scheduledDate"
                                           name="scheduledDate"
                                           class="form-control ${not empty fieldErrors['scheduledDate'] ? 'invalid' : ''}"
                                           value="${param.scheduledDate}" required>
                                    <c:if test="${not empty fieldErrors['scheduledDate']}">
                                        <div class="field-error">${fieldErrors['scheduledDate']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="endDate">Ngày kết thúc</label>
                                    <input type="date"
                                           id="endDate"
                                           name="endDate"
                                           class="form-control ${not empty fieldErrors['endDate'] ? 'invalid' : ''}"
                                           value="${param.endDate}">
                                    <c:if test="${not empty fieldErrors['endDate']}">
                                        <div class="field-error">${fieldErrors['endDate']}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="startTime">Giờ bắt đầu <span class="required-star">*</span></label>
                                    <input type="time" id="startTime" name="startTime" class="form-control" value="${param.startTime}" required>
                                </div>
                                <div class="form-group">
                                    <label for="endTime">Giờ kết thúc</label>
                                    <input type="time" id="endTime" name="endTime" class="form-control" value="${param.endTime}">
                                </div>
                            </div>

                            <div class="form-row-x3">
                                <div class="form-group">
                                    <label for="province">Tỉnh/Thành phố <span class="required-star">*</span></label>
                                    <select id="province"
                                            name="province"
                                            class="form-control ${not empty fieldErrors['province'] ? 'invalid' : ''}"
                                            required>
                                        <option value="" disabled ${empty param.province ? 'selected' : ''}>-- Chọn Tỉnh/Thành --</option>
                                        <c:forEach var="p" items="${provinces}">
                                            <option value="${p.id}" ${param.province == p.id ? 'selected' : ''}>${p.name}</option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${not empty fieldErrors['province']}">
                                        <div class="field-error">${fieldErrors['province']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="district">Quận/Huyện <span class="required-star">*</span></label>
                                    <select id="district"
                                            name="district"
                                            class="form-control ${not empty fieldErrors['district'] ? 'invalid' : ''}"
                                            required disabled>
                                        <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                    </select>
                                    <c:if test="${not empty fieldErrors['district']}">
                                        <div class="field-error">${fieldErrors['district']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="ward">Phường/Xã <span class="required-star">*</span></label>
                                    <select id="ward"
                                            name="ward"
                                            class="form-control ${not empty fieldErrors['ward'] ? 'invalid' : ''}"
                                            required disabled>
                                        <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                    </select>
                                    <c:if test="${not empty fieldErrors['ward']}">
                                        <div class="field-error">${fieldErrors['ward']}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="streetAddress">Địa chỉ cụ thể <span class="required-star">*</span></label>
                                <input type="text"
                                       id="streetAddress"
                                       name="streetAddress"
                                       class="form-control ${not empty fieldErrors['streetAddress'] ? 'invalid' : ''}"
                                       placeholder="Nhập số nhà, tên đường, ngõ/hẻm..." value="${param.streetAddress}" required>
                                <c:if test="${not empty fieldErrors['streetAddress']}">
                                    <div class="field-error">${fieldErrors['streetAddress']}</div>
                                </c:if>
                            </div>

                            <h3 class="sub-header">Phân công & Hiển thị</h3>

                            <div class="form-row">
                                <div class="form-group">
                                    <label for="assignedUserId">Nhân viên thực hiện <span class="required-star">*</span></label>
                                    <select id="assignedUserId"
                                            name="assignedUserId"
                                            class="form-control ${not empty fieldErrors['assignedUserId'] ? 'invalid' : ''}"
                                            required>
                                        <option value="">-- Chọn nhân viên --</option>
                                        <c:forEach var="user" items="${userList}">
                                            <option value="${user.id}" ${param.assignedUserId == user.id ? 'selected' : ''}>
                                                ${user.lastName} ${user.firstName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${not empty fieldErrors['assignedUserId']}">
                                        <div class="field-error">${fieldErrors['assignedUserId']}</div>
                                    </c:if>
                                </div>
                                <div class="form-group">
                                    <label for="statusId">Trạng thái ban đầu <span class="required-star">*</span></label>
                                    <select id="statusId"
                                            name="statusId"
                                            class="form-control ${not empty fieldErrors['statusId'] ? 'invalid' : ''}"
                                            required>
                                        <option value="">-- Chọn trạng thái --</option>
                                        <c:forEach var="status" items="${statusList}">
                                            <option value="${status.id}" ${param.statusId == status.id ? 'selected' : ''}>
                                                ${status.statusName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <c:if test="${not empty fieldErrors['statusId']}">
                                        <div class="field-error">${fieldErrors['statusId']}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Màu sắc hiển thị</label>
                                <div class="color-picker-container" id="colorPicker">
                                    <input type="hidden" name="color" id="selectedColor" value="${not empty param.color ? param.color : '#0d9488'}">
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
                                <a href="${BASE_URL}/list-campaign" class="btn btn-secondary">Hủy bỏ</a>
                                <button type="submit" class="btn btn-primary">Lưu chiến dịch</button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace({width: '1em', height: '1em'});

                // ======= ĐỊA CHỈ ĐỘNG =======
                const provinceSelect = document.getElementById('province');
                const districtSelect = document.getElementById('district');
                const wardSelect = document.getElementById('ward');

                function loadDistricts(provinceId, selectedDistrictId) {
                    districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                    districtSelect.disabled = true;
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                    wardSelect.disabled = true;

                    fetch('${BASE_URL}/create-campaign?action=getDistricts&id=' + provinceId)
                            .then(r => {
                                if (!r.ok) throw new Error('Network error');
                                return r.json();
                            })
                            .then(data => {
                                districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                                data.forEach(d => {
                                    const opt = document.createElement('option');
                                    opt.value = d.id;
                                    opt.textContent = d.name;
                                    if (selectedDistrictId && String(selectedDistrictId) === String(d.id))
                                        opt.selected = true;
                                    districtSelect.appendChild(opt);
                                });
                                districtSelect.disabled = false;

                                const paramDistrict = '${param.district}';
                                if (paramDistrict)
                                    loadWards(paramDistrict, '${param.ward}');
                            })
                            .catch(() => {
                                districtSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                                districtSelect.disabled = false;
                            });
                }

                function loadWards(districtId, selectedWardId) {
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
                    wardSelect.disabled = true;

                    fetch('${BASE_URL}/create-campaign?action=getWards&id=' + districtId)
                            .then(r => {
                                if (!r.ok) throw new Error('Network error');
                                return r.json();
                            })
                            .then(data => {
                                wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                                data.forEach(w => {
                                    const opt = document.createElement('option');
                                    opt.value = w.id;
                                    opt.textContent = w.name;
                                    if (selectedWardId && String(selectedWardId) === String(w.id))
                                        opt.selected = true;
                                    wardSelect.appendChild(opt);
                                });
                                wardSelect.disabled = false;
                            })
                            .catch(() => {
                                wardSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                                wardSelect.disabled = false;
                            });
                }

                if (provinceSelect) {
                    provinceSelect.addEventListener('change', function () {
                        const provinceId = this.value;
                        if (provinceId) loadDistricts(provinceId, null);
                    });
                }
                if (districtSelect) {
                    districtSelect.addEventListener('change', function () {
                        const districtId = this.value;
                        if (districtId) loadWards(districtId, null);
                    });
                }

                const paramProvince = '${param.province}';
                if (paramProvince) {
                    loadDistricts(paramProvince, '${param.district}');
                }

                // ======= VALIDATION CLIENT-SIDE =======
                const campaignForm = document.getElementById('campaignForm');
                const errorContainer = document.getElementById('formErrorContainer');

                if (campaignForm) {
                    campaignForm.addEventListener('submit', function (event) {
                        const errors = [];

                        const requiredFields = [
                            {id: 'campaignName', name: 'Tên chiến dịch'},
                            {id: 'campaignType', name: 'Loại chiến dịch'},
                            {id: 'enterpriseId', name: 'Khách hàng'},
                            {id: 'statusId', name: 'Trạng thái ban đầu'},
                            {id: 'scheduledDate', name: 'Ngày bắt đầu'},
                            {id: 'startTime', name: 'Giờ bắt đầu'},
                            {id: 'assignedUserId', name: 'Nhân viên thực hiện'},
                            {id: 'province', name: 'Tỉnh/Thành phố'},
                            {id: 'district', name: 'Quận/Huyện'},
                            {id: 'ward', name: 'Phường/Xã'},
                            {id: 'streetAddress', name: 'Địa chỉ cụ thể'}
                        ];
                        requiredFields.forEach(f => {
                            const el = document.getElementById(f.id);
                            if (!el || !el.value || (typeof el.value === 'string' && el.value.trim() === '')) {
                                errors.push(`Vui lòng nhập/chọn thông tin cho trường "${f.name}".`);
                            }
                        });

                        const nameEl = document.getElementById('campaignName');
                        const nval = (nameEl?.value || '').trim();
                        if (nval && (nval.length < 3 || nval.length > 255)) {
                            errors.push('Tên chiến dịch phải từ 3 đến 255 ký tự.');
                        }

                        const addrEl = document.getElementById('streetAddress');
                        const aval = (addrEl?.value || '').trim();
                        if (aval && aval.length > 255) {
                            errors.push('Địa chỉ cụ thể quá dài (tối đa 255 ký tự).');
                        }

                        const scheduledDateVal = document.getElementById('scheduledDate').value || '';
                        const endDateVal = document.getElementById('endDate').value || '';
                        const startTimeVal = document.getElementById('startTime').value || '';
                        const endTimeVal = document.getElementById('endTime').value || '';

                        if (scheduledDateVal && endDateVal && new Date(endDateVal) < new Date(scheduledDateVal)) {
                            errors.push('Lỗi: Ngày kết thúc không được sớm hơn ngày bắt đầu.');
                        }

                        const sameDay = scheduledDateVal && (!endDateVal || (endDateVal === scheduledDateVal));
                        if (sameDay && startTimeVal && endTimeVal && endTimeVal <= startTimeVal) {
                            errors.push('Lỗi: Giờ kết thúc phải sau giờ bắt đầu trong cùng ngày.');
                        }

                        if (errors.length > 0) {
                            event.preventDefault();
                            errorContainer.innerHTML = '<strong>Vui lòng sửa các lỗi sau:</strong><br>' + errors.join('<br>');
                            errorContainer.style.display = 'block';
                            errorContainer.scrollIntoView({behavior: 'smooth', block: 'start'});
                        } else {
                            errorContainer.style.display = 'none';
                        }
                    });
                }

                // ======= COLOR PICKER =======
                const colorPicker = document.getElementById('colorPicker');
                const selectedColorInput = document.getElementById('selectedColor');

                if (colorPicker && selectedColorInput) {
                    function setInitialColor() {
                        const initialColor = (selectedColorInput.value || '').toLowerCase();
                        const dots = colorPicker.querySelectorAll('.color-dot');
                        let found = false;
                        dots.forEach(dot => {
                            if ((dot.dataset.color || '').toLowerCase() === initialColor) {
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
                        if (!clickedDot) return;
                        
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