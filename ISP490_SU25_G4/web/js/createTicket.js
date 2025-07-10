/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    // --- 1. KHAI BÁO & THIẾT LẬP BAN ĐẦU ---
    feather.replace();

    const enterpriseSelect = document.getElementById('enterpriseId');
    const contractSelect = document.getElementById('contractId');
    const deviceTBody = document.getElementById('device-tbody');
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    let deviceIndex = 1;

    document.getElementById('createdDate').valueAsDate = new Date();
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function () {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'flex' : 'none';
        });
    });

    // --- 2. CÁC HÀM XỬ LÝ SỰ KIỆN ---

    enterpriseSelect.addEventListener('change', function () {
        const enterpriseId = this.value;
        resetContractSelect();
        resetDeviceList();
        if (!enterpriseId) {
            appendDeviceFrame(); // Thêm dòng trống nếu bỏ chọn khách hàng
            return;
        }
        loadContractsForEnterprise(enterpriseId);
    });

    contractSelect.addEventListener('change', function () {
        const contractId = this.value;
        resetDeviceList();
        if (!contractId) {
            appendDeviceFrame(); // Thêm dòng trống nếu bỏ chọn hợp đồng
            return;
        }
        loadProductsForContract(contractId);
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

    function loadProductsForContract(contractId) {
        const url = `${pageContext.request.contextPath}/ticket?action=getProducts&contractId=${contractId}`;
        fetch(url)
                .then(handleFetchResponse)
                .then(products => {
                    if (products && products.length > 0) {
                        products.forEach(product => {
                            appendDeviceFrame(product.productName, product.serialNumber, `Số lượng: ${product.quantity}`);
                        });
                    } else {
                        appendDeviceFrame();
                    }
                })
                .catch(error => {
                    console.error('Lỗi khi lấy sản phẩm:', error);
                    appendDeviceFrame();
                });
    }

    function appendDeviceFrame(name, serial, note) {
        const row = document.createElement('tr');

        // Sử dụng 'placeholder' để hiển thị ví dụ gợi ý
        // và để trống giá trị 'value' khi thêm dòng mới
        row.innerHTML = `
        <td>
            <input type="text" name="deviceName_${deviceIndex}" class="form-control-table" 
                   value="${name || ''}" 
                   placeholder="VD: Điều hòa Daikin">
        </td>
        <td>
            <input type="text" name="deviceSerial_${deviceIndex}" class="form-control-table" 
                   value="${serial || ''}" 
                   placeholder="VD: DKN-12345">
        </td>
        <td>
            <textarea name="deviceNote_${deviceIndex}" class="form-control-table" rows="1" 
                      placeholder="VD: Không lạnh, chảy nước">${note || ''}</textarea>
        </td>
        <td class="action-col">
            <button type="button" class="btn-remove-device" title="Xóa thiết bị">
                <i data-feather="x"></i>
            </button>
        </td>
    `;

        deviceTBody.appendChild(row);
        feather.replace(); // Cập nhật icon X
        deviceIndex++;
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

  
    appendDeviceFrame();

});

