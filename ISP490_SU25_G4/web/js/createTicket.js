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
// file: createTicket.js
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