document.addEventListener('DOMContentLoaded', function () {
    // --- 1. KHAI BÁO BIẾN & LẤY DỮ LIỆU BAN ĐẦU ---
    feather.replace();
    const contextPath = window.contextPath || '';
    
    // Lấy dữ liệu ban đầu từ các biến global mà JSP đã cung cấp
    const existingDevices = window.EXISTING_DEVICES || [];
    const initialContractProducts = window.CONTRACT_PRODUCTS || [];
    const preselectedAddress = window.PRESELECTED_ADDRESS || {};

    // Form và các thành phần chính
    const form = document.getElementById('editTicketForm');
    
    // Modals
    const contractModal = document.getElementById('contractModal');
    const btnChooseContract = document.getElementById('btnChooseContract');
    const closeModalContractBtn = contractModal.querySelector('.close-modal');
    const contractTable = document.getElementById('contract-table');
    const alertModal = document.getElementById('alertModal');
    const alertModalMessage = document.getElementById('alertModalMessage');
    const closeAlertModalBtns = document.querySelectorAll('.close-alert-modal');
    
    // Các trường trong Form
    const contractIdInput = document.getElementById('contractId');
    const contractCodeDisplay = document.getElementById('contractCodeDisplay');
    const enterpriseIdInput = document.getElementById('enterpriseId');
    const enterpriseNameDisplay = document.getElementById('enterpriseNameDisplay');
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');
    
    // Bảng thiết bị
    const addDeviceBtn = document.getElementById('addProductBtn');
    const deviceTbody = document.getElementById('device-tbody');
    let deviceRowCounter = 0;

    // Cache dữ liệu sản phẩm của hợp đồng hiện tại
    let currentContractProducts = initialContractProducts;


    // --- 2. CÁC HÀM TIỆN ÍCH ---
    function showAlert(message) {
        alertModalMessage.textContent = message;
        alertModal.style.display = 'block';
    }
    function closeAlertModal() {
        alertModal.style.display = 'none';
    }


    // --- 3. KHỞI TẠO TRẠNG THÁI BAN ĐẦU ---

    // Điền các thiết bị đã có vào bảng
    if (existingDevices.length > 0) {
        existingDevices.forEach(device => appendDeviceRow(device));
    }

    // Tải sẵn danh sách quận/huyện và phường/xã cho địa chỉ đã có
    if (preselectedAddress.provinceId) {
        loadDistricts(preselectedAddress.provinceId, preselectedAddress.districtId, preselectedAddress.wardId);
    }
    
    // Kích hoạt màu đã chọn
    const colorPalette = document.querySelector('.color-palette');
    const hiddenColorInput = document.getElementById('color');
    const initialColor = hiddenColorInput.value;
    if (initialColor) {
        const activeSwatch = colorPalette.querySelector(`.color-swatch[data-color="${initialColor}"]`);
        if (activeSwatch) activeSwatch.classList.add('active');
    }


    // --- 4. XỬ LÝ SỰ KIỆN ---

    btnChooseContract.addEventListener('click', () => { contractModal.style.display = 'block'; });
    closeModalContractBtn.addEventListener('click', () => { contractModal.style.display = 'none'; });
    closeAlertModalBtns.forEach(btn => btn.addEventListener('click', closeAlertModal));
    window.addEventListener('click', (event) => {
        if (event.target == contractModal) contractModal.style.display = 'none';
        if (event.target == alertModal) closeAlertModal();
    });
    
    contractTable.addEventListener('click', function(event) {
        const selectButton = event.target.closest('.btn-select-contract');
        if (selectButton) {
            const row = selectButton.closest('tr');
            const selectedContractId = row.getAttribute('data-contract-id');
            handleContractSelection(selectedContractId);
        }
    });

    addDeviceBtn.addEventListener('click', () => {
        if (!contractIdInput.value) { showAlert('Vui lòng chọn một hợp đồng.'); return; }
        if (currentContractProducts.length === 0) { showAlert('Hợp đồng này không có sản phẩm nào.'); return; }
        appendDeviceRow();
    });

    deviceTbody.addEventListener('click', function(event) {
        if (event.target.closest('.remove-device-btn')) {
            event.target.closest('tr').remove();
        }
    });
    
    provinceSelect.addEventListener('change', function () { loadDistricts(this.value); });
    districtSelect.addEventListener('change', function () { loadWards(this.value); });
    
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function() {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'flex' : 'none';
        });
    });

    colorPalette.addEventListener('click', function(e) {
        if (e.target.classList.contains('color-swatch')) {
            hiddenColorInput.value = e.target.dataset.color;
            this.querySelectorAll('.color-swatch').forEach(sw => sw.classList.remove('active'));
            e.target.classList.add('active');
        }
    });

    form.addEventListener('submit', function(event) {
        event.preventDefault(); 
        if (deviceTbody.rows.length === 0) {
            showAlert('Bạn phải thêm ít nhất một thiết bị liên quan.');
            return;
        }
        
        const isFormValid = form.checkValidity();
        form.reportValidity();
        if (isFormValid) {
            form.submit();
        }
    });


    // --- 5. CÁC HÀM LOGIC & AJAX ---

    async function handleContractSelection(selectedContractId) {
        if (!selectedContractId) return;
        try {
            const contractResponse = await fetch(`${contextPath}/ticket?action=getContractDetails&contractId=${selectedContractId}`);
            if (!contractResponse.ok) throw new Error('Lỗi tải chi tiết hợp đồng.');
            const contractData = await contractResponse.json();
            
            contractIdInput.value = contractData.id;
            contractCodeDisplay.value = contractData.contractCode;
            enterpriseIdInput.value = contractData.enterprise.id;
            enterpriseNameDisplay.value = contractData.enterprise.name;

            const productsResponse = await fetch(`${contextPath}/ticket?action=getProductsByContract&contractId=${selectedContractId}`);
            if (!productsResponse.ok) throw new Error('Lỗi tải sản phẩm.');
            currentContractProducts = await productsResponse.json();

            deviceTbody.innerHTML = '';
            deviceRowCounter = 0;
            
            contractModal.style.display = 'none';
        } catch (error) {
            console.error('Lỗi khi chọn hợp đồng:', error);
            showAlert(error.message);
        }
    }

    function appendDeviceRow(device = {}) {
        deviceRowCounter++;
        const newRow = deviceTbody.insertRow();
        const productOptionsHTML = currentContractProducts
            .map(p => `<option value="${p.name}" ${p.name === device.deviceName ? 'selected' : ''}>${p.name}</option>`)
            .join('');
        newRow.innerHTML = `
            <td>
                <select name="deviceName_${deviceRowCounter}" class="form-control device-name-select" required>
                    <option value="" disabled ${!device.deviceName ? 'selected' : ''}>-- Chọn sản phẩm --</option>
                    ${productOptionsHTML}
                </select>
            </td>
            <td><input type="text" name="deviceSerial_${deviceRowCounter}" class="form-control" value="${device.serialNumber || ''}"></td>
            <td><input type="text" name="deviceNote_${deviceRowCounter}" class="form-control" value="${device.problemDescription || ''}"></td>
            <td class="action-col"><button type="button" class="btn-icon btn-danger remove-device-btn"><i data-feather="trash-2"></i></button></td>
        `;
        feather.replace();
    }

    function resetDropdown(selectElement, defaultText) {
        selectElement.innerHTML = `<option value="" disabled selected>-- ${defaultText} --</option>`;
        selectElement.disabled = true;
    }

    function populateDropdown(selectElement, data, selectedId) {
        const defaultText = `Chọn ${selectElement.id === 'district' ? 'Quận/Huyện' : 'Phường/Xã'}`;
        resetDropdown(selectElement, defaultText);
        data.forEach(item => {
            const option = new Option(item.name, item.id);
            if (item.id == selectedId) {
                option.selected = true;
            }
            selectElement.add(option);
        });
        selectElement.disabled = false;
    }

    function loadDistricts(provinceId, districtIdToSelect, wardIdToSelect) {
        resetDropdown(districtSelect, 'Chọn Quận/Huyện');
        resetDropdown(wardSelect, 'Chọn Phường/Xã');
        if (!provinceId) return;
        
        fetch(`${contextPath}/ticket?action=getDistricts&provinceId=${provinceId}`)
            .then(response => response.json())
            .then(data => {
                populateDropdown(districtSelect, data, districtIdToSelect);
                if (districtIdToSelect) {
                    loadWards(districtIdToSelect, wardIdToSelect);
                }
            })
            .catch(error => console.error('Lỗi tải Quận/Huyện:', error));
    }

    function loadWards(districtId, wardIdToSelect) {
        resetDropdown(wardSelect, 'Chọn Phường/Xã');
        if (!districtId) return;

        fetch(`${contextPath}/ticket?action=getWards&districtId=${districtId}`)
            .then(response => response.json())
            .then(data => {
                populateDropdown(wardSelect, data, wardIdToSelect);
            })
            .catch(error => console.error('Lỗi tải Phường/Xã:', error));
    }
});