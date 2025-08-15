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

    // Giả sử bạn có đối tượng này được định nghĩa trong JSP từ server
    const PRESELECTED = window.PRESELECTED_ADDRESS || {};
    
    const contextPath = (function () {
        const seg = window.location.pathname.split('/')[1] || '';
        return seg ? '/' + seg : '';
    })();

    /**
     * Tải và điền dữ liệu cho dropdown Quận/Huyện.
     * @param {string} provinceId ID của Tỉnh/Thành phố.
     * @param {string | null} districtIdToSelect ID của Quận/Huyện cần được chọn sẵn.
     * @param {string | null} wardIdToSelect ID của Phường/Xã cần được chọn sẵn (để truyền cho hàm tiếp theo).
     */
    function loadDistricts(provinceId, districtIdToSelect, wardIdToSelect) {
        if (!provinceId) {
            districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
            districtSelect.disabled = true;
            return;
        }

        districtSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
        districtSelect.disabled = false;
        wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
        wardSelect.disabled = true;
        
        const url = `${contextPath}/schedule?action=getDistricts&provinceId=${encodeURIComponent(provinceId)}`;

        fetch(url)
            .then(r => r.ok ? r.json() : Promise.reject(r))
            .then(data => {
                districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                if (Array.isArray(data)) {
                    data.forEach(district => {
                        districtSelect.add(new Option(district.name, district.id));
                    });

                    // Nếu có districtId cần chọn, hãy chọn nó
                    if (districtIdToSelect) {
                        districtSelect.value = districtIdToSelect;
                        // THAY ĐỔI QUAN TRỌNG: Gọi trực tiếp hàm loadWards thay vì dispatchEvent
                        loadWards(districtIdToSelect, wardIdToSelect);
                    }
                }
            })
            .catch(err => {
                console.error('Lỗi khi tải Quận/Huyện:', err);
                districtSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
            });
    }
    function loadWards(districtId, wardIdToSelect) {
        if (!districtId) {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
            return;
        }

        wardSelect.innerHTML = '<option value="">-- Đang tải... --</option>';
        wardSelect.disabled = false;

        const url = `${contextPath}/schedule?action=getWards&districtId=${encodeURIComponent(districtId)}`;

        fetch(url)
            .then(r => r.ok ? r.json() : Promise.reject(r))
            .then(data => {
                wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                if (Array.isArray(data)) {
                    data.forEach(ward => {
                        wardSelect.add(new Option(ward.name, ward.id));
                    });
                    
                    // Nếu có wardId cần chọn, hãy chọn nó
                    if (wardIdToSelect) {
                        wardSelect.value = wardIdToSelect;
                    }
                }
            })
            .catch(err => {
                console.error('Lỗi khi tải Phường/Xã:', err);
                wardSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
            });
    }

    // --- Event Listeners cho người dùng tương tác ---
    
    provinceSelect.addEventListener('change', function () {
        loadDistricts(this.value, null, null); // Khi người dùng tự chọn, không cần chọn sẵn gì cả
    });

    districtSelect.addEventListener('change', function () {
        loadWards(this.value, null); // Tương tự
    });

    // --- Logic chạy khi tải trang lần đầu (cho form EDIT) ---
    
    if (PRESELECTED.provinceId) {
        provinceSelect.value = PRESELECTED.provinceId;
        // Bắt đầu chuỗi tải dữ liệu, truyền TẤT CẢ các ID đã lưu
        loadDistricts(PRESELECTED.provinceId, PRESELECTED.districtId, PRESELECTED.wardId);
    }
});
document.addEventListener('DOMContentLoaded', function () {
    const colorPalette = document.querySelector('.color-palette');
    const hiddenColorInput = document.getElementById('color');
    const swatches = colorPalette.querySelectorAll('.color-swatch');

    // Hàm để cập nhật trạng thái active
    function setActiveColor(selectedColor) {
        // 1. Cập nhật giá trị cho input ẩn
        hiddenColorInput.value = selectedColor;

        // 2. Cập nhật giao diện
        swatches.forEach(swatch => {
            if (swatch.dataset.color === selectedColor) {
                swatch.classList.add('active');
            } else {
                swatch.classList.remove('active');
            }
        });
    }

    // Xử lý sự kiện click vào một ô màu
    colorPalette.addEventListener('click', function (event) {
        const clickedSwatch = event.target;
        if (clickedSwatch.classList.contains('color-swatch')) {
            const selectedColor = clickedSwatch.dataset.color;
            setActiveColor(selectedColor);
        }
    });

    // Chạy lần đầu khi tải trang để highlight màu đã được lưu
    const initialColor = hiddenColorInput.value;
    if (initialColor) {
        setActiveColor(initialColor);
    }
});