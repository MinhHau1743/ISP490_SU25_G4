document.addEventListener('DOMContentLoaded', function () {
    // ----------- FEATHER ICON ------------
    feather.replace();

    // ----------- DEVICE TABLE -------------
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    const deviceList = document.getElementById('device-list');
    let deviceIndex = window.deviceIndex || 1;

    function appendDeviceFrame(device = {}) {
        const row = document.createElement('tr');
        const currentDeviceIndex = deviceIndex++;
        let optionsHTML = '<option value="">-- Chọn thiết bị --</option>';
        if (window.allProducts && allProducts.length > 0) {
            allProducts.forEach(product => {
                const isSelected = product.name === device.deviceName ? 'selected' : '';
                optionsHTML += `<option value="${product.name}" ${isSelected}>${product.name}</option>`;
            });
        }
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
        feather.replace();
    }

    if (addDeviceBtn) {
        addDeviceBtn.addEventListener('click', function () {
            appendDeviceFrame();
        });
    }
    if (deviceList) {
        deviceList.addEventListener('click', function (e) {
            const removeBtn = e.target.closest('.btn-remove-device');
            if (removeBtn) removeBtn.closest('tr').remove();
        });
    }
    if (window.existingDevices && existingDevices.length > 0) {
        existingDevices.forEach(device => appendDeviceFrame(device));
    } else {
        appendDeviceFrame();
    }

    // --------- AMOUNT FIELD -----------
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function () {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'block' : 'none';
        });
    });

    // --------- STATUS/PRIORITY COLOR -----------
    const statusSelect = document.getElementById('status');
    const prioritySelect = document.getElementById('priority');
    const allStatusClasses = ['status-new', 'status-assigned', 'status-in-progress', 'status-resolved', 'status-closed', 'status-rejected'];
    const allPriorityClasses = ['priority-critical', 'priority-high', 'priority-medium', 'priority-low'];
    function updateSelectColor(selectElement, classList) {
        selectElement.classList.remove(...classList);
        const selectedValue = selectElement.value;
        if (selectedValue) selectElement.classList.add(selectedValue);
    }
    if (statusSelect) {
        updateSelectColor(statusSelect, allStatusClasses);
        statusSelect.addEventListener('change', () => updateSelectColor(statusSelect, allStatusClasses));
    }
    if (prioritySelect) {
        updateSelectColor(prioritySelect, allPriorityClasses);
        prioritySelect.addEventListener('change', () => updateSelectColor(prioritySelect, allPriorityClasses));
    }

    // ----------- ADDRESS SELECT CASCADING ----------
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');
    const PRESELECTED = window.PRESELECTED_ADDRESS || {};
    const contextPath = (function () {
        const seg = window.location.pathname.split('/')[1] || '';
        return seg ? '/' + seg : '';
    })();

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
                    if (districtIdToSelect) {
                        districtSelect.value = districtIdToSelect;
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
                    if (wardIdToSelect) wardSelect.value = wardIdToSelect;
                }
            })
            .catch(err => {
                console.error('Lỗi khi tải Phường/Xã:', err);
                wardSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
            });
    }

    if (provinceSelect) {
        provinceSelect.addEventListener('change', function () {
            loadDistricts(this.value, null, null);
        });
    }
    if (districtSelect) {
        districtSelect.addEventListener('change', function () {
            loadWards(this.value, null);
        });
    }
    if (PRESELECTED.provinceId && provinceSelect) {
        provinceSelect.value = PRESELECTED.provinceId;
        loadDistricts(PRESELECTED.provinceId, PRESELECTED.districtId, PRESELECTED.wardId);
    }

    // ------ COLOR PALETTE -----------
    const colorPalette = document.querySelector('.color-palette');
    const hiddenColorInput = document.getElementById('color');
    if (colorPalette && hiddenColorInput) {
        const swatches = colorPalette.querySelectorAll('.color-swatch');
        function setActiveColor(selectedColor) {
            hiddenColorInput.value = selectedColor;
            swatches.forEach(swatch => {
                if (swatch.dataset.color === selectedColor) {
                    swatch.classList.add('active');
                } else {
                    swatch.classList.remove('active');
                }
            });
        }
        colorPalette.addEventListener('click', function (event) {
            const clickedSwatch = event.target;
            if (clickedSwatch.classList.contains('color-swatch')) {
                const selectedColor = clickedSwatch.dataset.color;
                setActiveColor(selectedColor);
            }
        });
        const initialColor = hiddenColorInput.value;
        if (initialColor) setActiveColor(initialColor);
    }

});
