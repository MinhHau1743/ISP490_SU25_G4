document.addEventListener('DOMContentLoaded', function () {
    // --- 1. KHAI BÁO & THIẾT LẬP BAN ĐẦU ---
    feather.replace();

    const enterpriseSelect = document.getElementById('enterpriseId');
    const contractSelect = document.getElementById('contractId');
    const deviceTBody = document.getElementById('device-tbody');
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    let deviceIndex = 1;
    let allProducts = []; // Biến để lưu danh sách tất cả sản phẩm

    // Thiết lập các giá trị ban đầu cho form
    document.getElementById('createdDate').valueAsDate = new Date();
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function () {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'flex' : 'none';
        });
    });

    // Tải danh sách sản phẩm ngay khi trang được mở
    loadAllProducts();

    // --- 2. CÁC HÀM XỬ LÝ SỰ KIỆN ---

    enterpriseSelect.addEventListener('change', function () {
        const enterpriseId = this.value;
        resetContractSelect();
        resetDeviceList();
        if (!enterpriseId) {
            appendDeviceFrame();
            return;
        }
        loadContractsForEnterprise(enterpriseId);
        appendDeviceFrame();
    });

    addDeviceBtn.addEventListener('click', () => {
        appendDeviceFrame();
    });

    deviceTBody.addEventListener('click', function (e) {
        const removeBtn = e.target.closest('.btn-remove-device');
        if (removeBtn) {
            removeBtn.closest('tr').remove();
            if (deviceTBody.children.length === 0) {
                appendDeviceFrame();
            }
        }
    });

    // --- 3. CÁC HÀM LOGIC ---

    function loadAllProducts() {
        const url = `${contextPath}/ticket?action=getProducts`;
        fetch(url)
            .then(handleFetchResponse)
            .then(products => {
                allProducts = products;
                appendDeviceFrame();
            })
            .catch(error => {
                console.error('Không thể tải danh sách sản phẩm:', error);
                appendDeviceFrame();
            });
    }

    function loadContractsForEnterprise(enterpriseId) {
        contractSelect.disabled = true;
        contractSelect.innerHTML = '<option value="">Đang tải hợp đồng...</option>';
        const url = `${contextPath}/ticket?action=getContracts&enterpriseId=${enterpriseId}`;

        fetch(url)
            .then(handleFetchResponse)
            .then(contracts => {
                populateDropdown(contractSelect, contracts, 'hợp đồng');
                contractSelect.disabled = false;
            })
            .catch(error => handleFetchError(contractSelect, error, 'hợp đồng'));
    }

    // *** HÀM ĐÃ ĐƯỢC SỬA LỖI ***
    // Hàm này giờ sẽ tạo thẳng thẻ <select> bằng HTML string, đảm bảo hoạt động.
    function appendDeviceFrame() {
        const row = document.createElement('tr');
        const currentDeviceIndex = deviceIndex++;

        // 1. Tạo chuỗi HTML cho các thẻ <option>
        let optionsHTML = '<option value="" selected>-- Chọn thiết bị --</option>';
        if (allProducts && allProducts.length > 0) {
            allProducts.forEach(product => {
                // Chú ý: Dùng `product.name` cho cả value và text hiển thị
                optionsHTML += `<option value="${product.name}">${product.name}</option>`;
            });
        }

        // 2. Tạo toàn bộ HTML cho một hàng của bảng
        row.innerHTML = `
            <td>
                <select name="deviceName_${currentDeviceIndex}" class="form-control-table">
                    ${optionsHTML}
                </select>
            </td>
            <td>
                <input type="text" name="deviceSerial_${currentDeviceIndex}" class="form-control-table" placeholder="VD: DKN-12345">
            </td>
            <td>
                <textarea name="deviceNote_${currentDeviceIndex}" class="form-control-table" rows="1" placeholder="VD: Không lạnh, chảy nước"></textarea>
            </td>
            <td class="action-col">
                <button type="button" class="btn-remove-device" title="Xóa thiết bị">
                    <i data-feather="x"></i>
                </button>
            </td>
        `;

        // 3. Thêm hàng vào bảng và cập nhật icon
        deviceTBody.appendChild(row);
        feather.replace();
    }


    function resetDeviceList() {
        deviceTBody.innerHTML = '';
        deviceIndex = 1;
    }

    function resetContractSelect() {
        contractSelect.innerHTML = '<option value="">-- Vui lòng chọn khách hàng trước --</option>';
        contractSelect.disabled = true;
    }

    // --- 4. HÀM TIỆN ÍCH CHUNG ---
    function handleFetchResponse(response) {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Lỗi không xác định từ Server');
            });
        }
        return response.json();
    }
    
    function populateDropdown(selectElement, items, itemName) {
        selectElement.innerHTML = '';
        selectElement.add(new Option(`-- Chọn ${itemName} (nếu có) --`, ''));
        if (items && items.length > 0) {
            items.forEach(item => {
                selectElement.add(new Option(item.contractCode || item.name, item.id));
            });
        } else {
            selectElement.options[0].text = `-- Không có ${itemName} nào --`;
        }
    }

    function handleFetchError(selectElement, error, itemName) {
        console.error(`Lỗi khi tải ${itemName}:`, error);
        selectElement.innerHTML = '';
        selectElement.add(new Option(`Lỗi tải ${itemName}`, ''));
        selectElement.disabled = true;
    }
});