document.addEventListener('DOMContentLoaded', function () {
    // --- 1. KHAI BÁO BIẾN & THIẾT LẬP BAN ĐẦU ---
    feather.replace();
    const contextPath = window.contextPath || '';

    // Form và các thành phần chính
    const form = document.getElementById('createTicketForm');
    
    // Modal Hợp đồng
    const contractModal = document.getElementById('contractModal');
    const btnChooseContract = document.getElementById('btnChooseContract');
    const closeModalBtn = contractModal.querySelector('.close-modal');
    const contractTable = document.getElementById('contract-table');

    // Modal Thông báo
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
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    const deviceTbody = document.getElementById('device-tbody');
    let deviceRowCounter = 0;

    // Cache dữ liệu
    let contractProducts = [];

    // --- 2. KHỞI TẠO ---
    document.getElementById('createdDate').valueAsDate = new Date();
    document.querySelector('.color-palette .color-swatch[data-color="#007bff"]').classList.add('active');


    // --- 3. CÁC HÀM TIỆN ÍCH ---

    function showAlert(message) {
        alertModalMessage.textContent = message;
        alertModal.style.display = 'block';
    }

    function closeAlertModal() {
        alertModal.style.display = 'none';
    }


    // --- 4. XỬ LÝ SỰ KIỆN ---

    btnChooseContract.addEventListener('click', () => { contractModal.style.display = 'block'; });
    closeModalBtn.addEventListener('click', () => { contractModal.style.display = 'none'; });
    
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
        if (!contractIdInput.value) {
            showAlert('Vui lòng chọn hợp đồng trước khi thêm thiết bị.');
            return;
        }
        if (contractProducts.length === 0) {
            showAlert('Hợp đồng này không có sản phẩm nào được định nghĩa.');
            return;
        }
        appendDeviceRow();
    });

    deviceTbody.addEventListener('click', function(event) {
        if (event.target.closest('.remove-device-btn')) {
            event.target.closest('tr').remove();
        }
    });
    
    // === CÁC SỰ KIỆN XỬ LÝ ĐỊA CHỈ ĐÃ ĐƯỢC THÊM LẠI ===
    provinceSelect.addEventListener('change', function () { loadDistricts(this.value); });
    districtSelect.addEventListener('change', function () { loadWards(this.value); });
    // =======================================================
    
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function() {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'flex' : 'none';
        });
    });

    document.querySelector('.color-palette').addEventListener('click', function(e) {
        if (e.target.classList.contains('color-swatch')) {
            document.getElementById('color').value = e.target.dataset.color;
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

        const contractCodeDisplay = document.getElementById('contractCodeDisplay');
        if (!document.getElementById('contractId').value) {
            contractCodeDisplay.setCustomValidity('Vui lòng chọn một hợp đồng từ danh sách.');
        } else {
            contractCodeDisplay.setCustomValidity('');
        }

        const deviceSelects = deviceTbody.querySelectorAll('.device-name-select');
        deviceSelects.forEach(select => {
            select.setCustomValidity('');
            if (!select.value) {
                select.setCustomValidity('Vui lòng chọn một sản phẩm hoặc xóa dòng này.');
            }
        });

        const startDate = document.getElementById('scheduled_date');
        const endDate = document.getElementById('end_date');
        endDate.setCustomValidity('');
        if (startDate.value && endDate.value && endDate.value < startDate.value) {
            endDate.setCustomValidity('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.');
        }

        const isFormValid = form.checkValidity();
        form.reportValidity();

        if (isFormValid) {
            contractCodeDisplay.setCustomValidity('');
            deviceSelects.forEach(select => select.setCustomValidity(''));
            endDate.setCustomValidity('');
            form.submit();
        }
    });


    // --- 5. CÁC HÀM LOGIC & AJAX ---

    async function handleContractSelection(selectedContractId) {
        if (!selectedContractId) return;
        try {
            const contractResponse = await fetch(`${contextPath}/ticket?action=getContractDetails&contractId=${selectedContractId}`);
            if (!contractResponse.ok) throw new Error('Không thể tải chi tiết hợp đồng.');
            const contractData = await contractResponse.json();
            
            contractIdInput.value = contractData.id;
            contractCodeDisplay.value = contractData.contractCode;
            enterpriseIdInput.value = contractData.enterprise.id;
            enterpriseNameDisplay.value = contractData.enterprise.name;

            const productsResponse = await fetch(`${contextPath}/ticket?action=getProductsByContract&contractId=${selectedContractId}`);
            if (!productsResponse.ok) throw new Error('Không thể tải danh sách sản phẩm.');
            contractProducts = await productsResponse.json();

            deviceTbody.innerHTML = '';
            deviceRowCounter = 0;
            
            contractModal.style.display = 'none';

        } catch (error) {
            console.error('Lỗi khi xử lý chọn hợp đồng:', error);
            showAlert('Đã xảy ra lỗi khi tải thông tin hợp đồng. Vui lòng thử lại.');
        }
    }

    function appendDeviceRow() {
        deviceRowCounter++;
        const newRow = deviceTbody.insertRow();
        const productOptionsHTML = contractProducts
            .map(p => `<option value="${p.name}">${p.name}</option>`)
            .join('');
        newRow.innerHTML = `
            <td>
                <select name="deviceName_${deviceRowCounter}" class="form-control device-name-select" required>
                    <option value="" disabled selected>-- Chọn sản phẩm --</option>
                    ${productOptionsHTML}
                </select>
            </td>
            <td><input type="text" name="deviceSerial_${deviceRowCounter}" class="form-control"></td>
            <td><input type="text" name="deviceNote_${deviceRowCounter}" class="form-control"></td>
            <td class="action-col">
                <button type="button" class="btn-icon btn-danger remove-device-btn">
                    <i data-feather="trash-2"></i>
                </button>
            </td>
        `;
        feather.replace();
    }

    // === CÁC HÀM XỬ LÝ ĐỊA CHỈ ĐÃ ĐƯỢC THÊM LẠI ===
    function resetDropdown(selectElement, defaultText) {
        selectElement.innerHTML = `<option value="" disabled selected>-- ${defaultText} --</option>`;
        selectElement.disabled = true;
    }

    function populateDropdown(selectElement, data) {
        const defaultText = `Chọn ${selectElement.id === 'district' ? 'Quận/Huyện' : 'Phường/Xã'}`;
        resetDropdown(selectElement, defaultText);
        data.forEach(item => {
            const option = new Option(item.name, item.id);
            selectElement.add(option);
        });
        selectElement.disabled = false;
    }

    function loadDistricts(provinceId) {
        resetDropdown(districtSelect, 'Chọn Quận/Huyện');
        resetDropdown(wardSelect, 'Chọn Phường/Xã');
        if (!provinceId) return;
        
        fetch(`${contextPath}/ticket?action=getDistricts&provinceId=${provinceId}`)
            .then(response => response.json())
            .then(data => populateDropdown(districtSelect, data))
            .catch(error => console.error('Lỗi tải Quận/Huyện:', error));
    }

    function loadWards(districtId) {
        resetDropdown(wardSelect, 'Chọn Phường/Xã');
        if (!districtId) return;

        fetch(`${contextPath}/ticket?action=getWards&districtId=${districtId}`)
            .then(response => response.json())
            .then(data => populateDropdown(wardSelect, data))
            .catch(error => console.error('Lỗi tải Phường/Xã:', error));
    }
    // =======================================================
});