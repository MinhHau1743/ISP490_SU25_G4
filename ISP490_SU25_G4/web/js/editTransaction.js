document.addEventListener('DOMContentLoaded', function () {
    feather.replace();
    // Lấy các phần tử DOM
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    const deviceList = document.getElementById('device-list');
    const statusSelect = document.getElementById('status');
    const prioritySelect = document.getElementById('priority');

    // Hàm để tạo một hàng thiết bị (có dropdown)
    function appendDeviceFrame(device = {}) {
        const row = document.createElement('tr');
        const currentDeviceIndex = deviceIndex++; // deviceIndex được lấy từ file JSP

        // Tạo chuỗi HTML cho các thẻ <option>
        let optionsHTML = '<option value="">-- Chọn thiết bị --</option>';
        if (allProducts && allProducts.length > 0) { // allProducts được lấy từ file JSP
            allProducts.forEach(product => {
                const isSelected = product.name === device.deviceName ? 'selected' : '';
                optionsHTML += `<option value="${product.name}" ${isSelected}>${product.name}</option>`;
            });
        }

        // Tạo toàn bộ HTML cho một hàng của bảng
        row.innerHTML = `
            <td>
                <select name="deviceName_${currentDeviceIndex}" class="form-control-table">
                    ${optionsHTML}
                </select>
            </td>
            <td>
                <input type="text" name="deviceSerial_${currentDeviceIndex}" class="form-control-table" value="${device.serialNumber || ''}">
            </td>
            <td>
                <textarea name="deviceNote_${currentDeviceIndex}" class="form-control-table" rows="1">${device.problemDescription || ''}</textarea>
            </td>
            <td>
                <button type="button" class="btn-remove-device" title="Xóa dòng"><i data-feather="x-circle"></i></button>
            </td>
        `;

        deviceList.appendChild(row);
        feather.replace(); // Cập nhật icon
    }

    // Xử lý nút "Thêm thiết bị"
    addDeviceBtn.addEventListener('click', function () {
        appendDeviceFrame(); // Thêm một hàng trống
    });

    // Xử lý nút "Xóa"
    deviceList.addEventListener('click', function (e) {
        const removeBtn = e.target.closest('.btn-remove-device');
        if (removeBtn) {
            removeBtn.closest('tr').remove();
        }
    });

    // Hiển thị các thiết bị đã có của phiếu khi tải trang
    if (existingDevices && existingDevices.length > 0) { // existingDevices được lấy từ file JSP
        existingDevices.forEach(device => {
            appendDeviceFrame(device);
        });
    } else {
        // Nếu không có thiết bị nào, thêm 1 dòng trống
        appendDeviceFrame();
    }

    // Xử lý radio button cho phần chi phí
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function () {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'block' : 'none';
        });
    });

    // --- LOGIC ĐỔI MÀU CHO SELECT BOX ---

    // Danh sách tất cả các class có thể có để dễ dàng xóa bỏ
    const allStatusClasses = ['status-new', 'status-assigned', 'status-in-progress', 'status-resolved', 'status-closed', 'status-rejected'];
    const allPriorityClasses = ['priority-critical', 'priority-high', 'priority-medium', 'priority-low'];

    // Hàm chung để cập nhật màu cho ô select
    function updateSelectColor(selectElement, classList) {
        // Xóa tất cả các class màu cũ
        selectElement.classList.remove(...classList);

        // Thêm class màu mới dựa trên giá trị được chọn
        const selectedValue = selectElement.value;
        if (selectedValue) {
            selectElement.classList.add(selectedValue);
        }
    }

    // Áp dụng cho ô Trạng thái
    if (statusSelect) {
        updateSelectColor(statusSelect, allStatusClasses); // Đặt màu ban đầu
        statusSelect.addEventListener('change', () => updateSelectColor(statusSelect, allStatusClasses));
    }

    // Áp dụng cho ô Mức độ ưu tiên
    if (prioritySelect) {
        updateSelectColor(prioritySelect, allPriorityClasses); // Đặt màu ban đầu
        prioritySelect.addEventListener('change', () => updateSelectColor(prioritySelect, allPriorityClasses));
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');

    // Lấy từ APP_CONFIG do JSP cung cấp, fallback sang pathname nếu thiếu
    const contextPath =
            (window.APP_CONFIG && window.APP_CONFIG.contextPath) ||
            (function () {
                const seg = window.location.pathname.split('/')[1] || '';
                return seg ? '/' + seg : '';
            })();

    provinceSelect.addEventListener('change', function () {
        const provinceId = this.value?.trim();
        console.log('Selected Province ID:', provinceId);

        // reset
        districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
        districtSelect.disabled = true;
        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
        wardSelect.disabled = true;

        if (!provinceId) {
            console.log('Province ID is empty or invalid');
            return;
        }

        districtSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
        districtSelect.disabled = false;

        const url = `${contextPath}/ticket?action=getDistricts&provinceId=${encodeURIComponent(provinceId)}`;
        console.log('Request URL:', url);

        fetch(url)
                .then(r => {
                    console.log('Response status:', r.status, 'URL:', r.url);
                    if (!r.ok)
                        throw new Error(`HTTP ${r.status}: ${r.statusText}`);
                    return r.json();
                })
                .then(data => {
                    console.log('Districts data received:', data);
                    districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';

                    if (Array.isArray(data) && data.length) {
                        for (const district of data) {
                            districtSelect.add(new Option(district.name, district.id));
                        }
                    } else {
                        districtSelect.innerHTML = '<option value="" disabled>-- Không có dữ liệu --</option>';
                    }
                })
                .catch(err => {
                    console.error('Lỗi khi tải danh sách Quận/Huyện:', err);
                    districtSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
                });
    });

    districtSelect.addEventListener('change', function () {
        const districtId = this.value?.trim();
        console.log('Selected District ID:', districtId);

        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
        wardSelect.disabled = true;

        if (!districtId)
            return;

        wardSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
        wardSelect.disabled = false;

        const url = `${contextPath}/ticket?action=getWards&districtId=${encodeURIComponent(districtId)}`;
        console.log('Ward request URL:', url);

        fetch(url)
                .then(r => {
                    if (!r.ok)
                        throw new Error(`HTTP ${r.status}: ${r.statusText}`);
                    return r.json();
                })
                .then(data => {
                    console.log('Wards data received:', data);
                    wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';

                    if (Array.isArray(data) && data.length) {
                        for (const ward of data) {
                            wardSelect.add(new Option(ward.name, ward.id));
                        }
                    } else {
                        wardSelect.innerHTML = '<option value="" disabled>-- Không có dữ liệu --</option>';
                    }
                })
                .catch(err => {
                    console.error('Lỗi khi tải danh sách Phường/Xã:', err);
                    wardSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
                });
    });
});
