document.addEventListener('DOMContentLoaded', function () {
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');
    if (!provinceSelect || !districtSelect || !wardSelect) return;
    const contextPath = window.ADDR_CONTEXT_PATH || '';
    // Lấy các giá trị được chọn sẵn
    const preset = window.PRESELECTED_ADDRESS || {};

    // fetchAndPopulate nhận preselectedId làm tham số truyền riêng từng lần
    const fetchAndPopulate = (selectElement, url, preselectedId, onLoaded) => {
        selectElement.innerHTML = '<option value="">-- Đang tải... --</option>';
        selectElement.disabled = true;
        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                let placeholder = '-- Chọn Quận/Huyện --';
                if (selectElement.id === 'ward') placeholder = '-- Chọn Phường/Xã --';
                if (selectElement.id === 'province') placeholder = '-- Chọn Tỉnh/Thành --';
                selectElement.innerHTML = `<option value="">${placeholder}</option>`;
                data.forEach(item => {
                    const option = new Option(item.name, item.id);
                    selectElement.add(option);
                });
                selectElement.disabled = false;
                // Nếu có giá trị set sẵn thì chọn, không thì thôi
                if (preselectedId) {
                    selectElement.value = preselectedId;
                }
                if (onLoaded) onLoaded();
            })
            .catch(error => {
                console.error(`Lỗi khi tải ${selectElement.id}:`, error);
                selectElement.innerHTML = `<option value="">-- Lỗi tải dữ liệu --</option>`;
            });
    };

    // Tải districts khi đã chọn province
    function loadDistricts(selectedId, loadWardsCallback) {
        const provinceId = provinceSelect.value;
        if (!provinceId) {
            districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
            districtSelect.disabled = true;
            return;
        }
        fetchAndPopulate(districtSelect, `${contextPath}/getDistricts?provinceId=${provinceId}`, selectedId, loadWardsCallback);
    }

    // Tải wards khi đã chọn district
    function loadWards(selectedId) {
        const districtId = districtSelect.value;
        if (!districtId) {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
            return;
        }
        fetchAndPopulate(wardSelect, `${contextPath}/getWards?districtId=${districtId}`, selectedId);
    }

    // Khi chọn province mới
    provinceSelect.addEventListener('change', function () {
        loadDistricts(null, function () {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
        });
    });

    // Khi chọn district mới
    districtSelect.addEventListener('change', function () {
        loadWards(null);
    });

    // Khi load trang lần đầu (form edit): tự động load đúng district, ward đã chọn 
    if (provinceSelect.value) {
        loadDistricts(preset.districtId, function () {
            if (districtSelect.value) {
                loadWards(preset.wardId);
            }
        });
    }
});
