// js/createTicket.js

document.addEventListener('DOMContentLoaded', function () {
    // --- 1. KHAI BÁO & THIẾT LẬP BAN ĐẦU ---
    feather.replace();

    const enterpriseSelect = document.getElementById('enterpriseId');
    const deviceTBody = document.getElementById('device-tbody');
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    let deviceIndex = 1;
    let allProducts = [];

    // Thiết lập các giá trị ban đầu cho form
    document.getElementById('createdDate').valueAsDate = new Date();
    document.querySelectorAll('input[name="isBillable"]').forEach(radio => {
        radio.addEventListener('change', function () {
            document.getElementById('amount-group').style.display = this.value === 'true' ? 'flex' : 'none';
        });
    });

    // Tải danh sách sản phẩm
    loadAllProducts();

    // --- 2. CÁC HÀM XỬ LÝ SỰ KIỆN ---
    enterpriseSelect.addEventListener('change', function () {
        resetDeviceList();
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
                .then(response => {
                    if (!response.ok)
                        throw new Error('Lỗi tải sản phẩm');
                    return response.json();
                })
                .then(products => {
                    allProducts = products;
                    appendDeviceFrame();
                })
                .catch(error => {
                    console.error(error);
                    appendDeviceFrame();
                });
    }

    function appendDeviceFrame() {
        const row = document.createElement('tr');
        const currentDeviceIndex = deviceIndex++;

        let optionsHTML = '<option value="" selected>-- Chọn thiết bị --</option>';
        if (allProducts && allProducts.length > 0) {
            allProducts.forEach(product => {
                optionsHTML += `<option value="${product.name}">${product.name}</option>`;
            });
        }

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

        deviceTBody.appendChild(row);
        feather.replace();
    }

    function resetDeviceList() {
        deviceTBody.innerHTML = '';
        deviceIndex = 1;
    }
});