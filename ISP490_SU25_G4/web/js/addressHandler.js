document.addEventListener('DOMContentLoaded', function () {
    // Lấy các phần tử dropdown
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');

    // Dừng lại nếu không tìm thấy các phần tử này trên trang
    if (!provinceSelect || !districtSelect || !wardSelect) {
        return;
    }

    // Lấy các giá trị được truyền từ JSP một cách an toàn
    // Cần có <script> trong JSP để tạo đối tượng 'addressConfig'
    const config = window.addressConfig || {};
    const contextPath = config.contextPath || '';
    const preselected = config.preselected || {};

    // Hàm chung để gọi API và điền dữ liệu vào dropdown
    const fetchAndPopulate = (selectElement, url, preselectedId) => {
        selectElement.innerHTML = '<option value="">-- Đang tải... --</option>';
        selectElement.disabled = true;

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.json();
            })
            .then(data => {
                const placeholder = selectElement.id === 'district' ? '-- Chọn Quận/Huyện --' : '-- Chọn Phường/Xã --';
                selectElement.innerHTML = `<option value="">${placeholder}</option>`;
                
                data.forEach(item => {
                    const option = new Option(item.name, item.id);
                    selectElement.add(option);
                });
                
                selectElement.disabled = false;

                // Nếu có giá trị được chọn sẵn, hãy chọn nó
                if (preselectedId) {
                    selectElement.value = preselectedId;
                    // Kích hoạt sự kiện 'change' để tải dropdown tiếp theo
                    selectElement.dispatchEvent(new Event('change'));
                }
            })
            .catch(error => {
                console.error(`Lỗi khi tải ${selectElement.id}:`, error);
                selectElement.innerHTML = `<option value="">-- Lỗi tải dữ liệu --</option>`;
            });
    };

    // Lắng nghe sự kiện thay đổi trên Tỉnh/Thành
    provinceSelect.addEventListener('change', function () {
        const provinceId = this.value;
        wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
        wardSelect.disabled = true;

        if (provinceId) {
            fetchAndPopulate(districtSelect, `${contextPath}/getDistricts?provinceId=${provinceId}`, preselected.districtId);
            // Xóa giá trị đã chọn sẵn của quận/huyện để không bị dùng lại
            preselected.districtId = null; 
        } else {
            districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
            districtSelect.disabled = true;
        }
    });

    // Lắng nghe sự kiện thay đổi trên Quận/Huyện
    districtSelect.addEventListener('change', function () {
        const districtId = this.value;

        if (districtId) {
            fetchAndPopulate(wardSelect, `${contextPath}/getWards?districtId=${districtId}`, preselected.wardId);
            // Xóa giá trị đã chọn sẵn của phường/xã
            preselected.wardId = null;
        } else {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
        }
    });

    // Xử lý khi tải trang (cho form edit)
    if (provinceSelect.value) {
        provinceSelect.dispatchEvent(new Event('change'));
    }
});