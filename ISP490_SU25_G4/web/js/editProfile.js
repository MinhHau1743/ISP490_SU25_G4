// File: js/editProfile.js - PHIÊN BẢN SỬA HOÀN CHỈNH

document.addEventListener('DOMContentLoaded', function () {
    // --- PHẦN 1: XỬ LÝ XEM TRƯỚC ẢNH ĐẠI DIỆN ---
    const btnChooseAvatar = document.getElementById('btnChooseAvatar');
    const avatarUpload = document.getElementById('avatarUpload');
    const avatarPreview = document.getElementById('avatarPreview');

    if (btnChooseAvatar && avatarUpload && avatarPreview) {
        btnChooseAvatar.addEventListener('click', function () {
            avatarUpload.click(); // Kích hoạt input file ẩn
        });

        avatarUpload.addEventListener('change', function (event) {
            if (event.target.files && event.target.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    avatarPreview.src = e.target.result;
                };
                reader.readAsDataURL(event.target.files[0]);
            }
        });
    }

    // --- PHẦN 2: XỬ LÝ DROPDOWN ĐỊA CHỈ ĐỘNG ---
    const provinceSelect = document.getElementById('provinceId');
    const districtSelect = document.getElementById('districtId');
    const wardSelect = document.getElementById('wardId');

    // Lấy các giá trị đã được lưu của người dùng từ JSP
    const contextPath = window.BASE_URL || '';
    const selectedDistrictId = window.userDistrictId || '';
    const selectedWardId = window.userWardId || '';

    // Hàm để tải danh sách Quận/Huyện
    function loadDistricts(provinceId, defaultDistrictId) {
        if (!provinceId) {
            districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            districtSelect.disabled = true;
            wardSelect.disabled = true;
            return;
        }

        districtSelect.disabled = false;
        wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
        wardSelect.disabled = true;
        districtSelect.innerHTML = '<option value="">-- Đang tải... --</option>';

        // ## FIX: Sửa lại đường dẫn fetch cho đúng ##
        fetch(`${contextPath}/profile?action=getDistricts&provinceId=${provinceId}`)
                .then(response => response.json())
                .then(data => {
                    districtSelect.innerHTML = '<option value="">-- Chọn Quận/Huyện --</option>';
                    data.forEach(function (district) {
                        const option = document.createElement('option');
                        option.value = district.id;
                        option.textContent = district.name;
                        if (district.id == defaultDistrictId) {
                            option.selected = true;
                        }
                        districtSelect.appendChild(option);
                    });
                    // Sau khi tải xong, nếu có defaultDistrictId thì tự động tải phường/xã
                    if (defaultDistrictId) {
                        loadWards(defaultDistrictId, selectedWardId);
                    }
                })
                .catch(error => console.error('Lỗi khi tải quận/huyện:', error));
    }

    // Hàm để tải danh sách Phường/Xã
    function loadWards(districtId, defaultWardId) {
        if (!districtId) {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
            return;
        }

        wardSelect.disabled = false;
        wardSelect.innerHTML = '<option value="">-- Đang tải... --</option>';

        // ## FIX: Sửa lại đường dẫn fetch cho đúng ##
        fetch(`${contextPath}/profile?action=getWards&districtId=${districtId}`)
                .then(response => response.json())
                .then(data => {
                    wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
                    data.forEach(function (ward) {
                        const option = document.createElement('option');
                        option.value = ward.id;
                        option.textContent = ward.name;
                        if (ward.id == defaultWardId) {
                            option.selected = true;
                        }
                        wardSelect.appendChild(option);
                    });
                })
                .catch(error => console.error('Lỗi khi tải phường/xã:', error));
    }

    // Gán sự kiện 'change' cho Tỉnh/Thành
    provinceSelect.addEventListener('change', function () {
        loadDistricts(this.value, null); // Khi người dùng tự chọn, không có default
    });

    // Gán sự kiện 'change' cho Quận/Huyện
    districtSelect.addEventListener('change', function () {
        loadWards(this.value, null); // Khi người dùng tự chọn, không có default
    });

    // Xử lý khi trang được tải lần đầu
    // Nếu có Tỉnh được chọn sẵn, tự động tải danh sách Quận/Huyện
    if (provinceSelect.value) {
        loadDistricts(provinceSelect.value, selectedDistrictId);
    }
});