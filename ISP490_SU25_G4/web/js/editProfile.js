/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    const btnChooseAvatar = document.getElementById('btnChooseAvatar');
    const avatarUpload = document.getElementById('avatarUpload');
    const avatarPreview = document.getElementById('avatarPreview');

    // Kiểm tra xem các phần tử có tồn tại không trước khi thêm sự kiện
    if (btnChooseAvatar && avatarUpload && avatarPreview) {
        btnChooseAvatar.addEventListener('click', function () {
            avatarUpload.click(); // Kích hoạt input file ẩn
        });

        avatarUpload.addEventListener('change', function (event) {
            if (event.target.files && event.target.files[0]) {
                const reader = new FileReader();

                reader.onload = function (e) {
                    avatarPreview.src = e.target.result;
                }

                reader.readAsDataURL(event.target.files[0]);
            }
        });
    }
});
document.getElementById('btnChooseAvatar').addEventListener('click', function () {
    document.getElementById('avatarUpload').click();
});
document.getElementById('avatarUpload').addEventListener('change', function (event) {
    const [file] = event.target.files;
    if (file) {
        document.getElementById('avatarPreview').src = URL.createObjectURL(file);
    }
});

// Dynamic address loading with pre-selection
document.addEventListener('DOMContentLoaded', function () {
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');

    // User's existing values (from global variables)
    const userProvinceId = window.userProvinceId;  // Sử dụng global
    const userDistrictId = window.userDistrictId;
    const userWardId = window.userWardId;

    // Event listener for province change
    provinceSelect.addEventListener('change', function () {
        const provinceId = this.value;
        districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
        districtSelect.disabled = true;
        wardSelect.disabled = true;
        if (provinceId) {
            fetch(window.BASE_URL + '/getDistricts?provinceId=' + provinceId)  // Sử dụng global BASE_URL
                    .then(response => response.json())
                    .then(data => {
                        districtSelect.innerHTML = '<option value="" disabled selected>-- Chọn Quận/Huyện --</option>';
                        data.forEach(function (district) {
                            const option = document.createElement('option');
                            option.value = district.id;
                            option.textContent = district.name;
                            districtSelect.appendChild(option);
                        });
                        districtSelect.disabled = false;

                        // Pre-select district if matching user value
                        if (userDistrictId) {
                            districtSelect.value = userDistrictId;
                            districtSelect.dispatchEvent(new Event('change'));  // Trigger load wards
                        }
                    })
                    .catch(error => console.error('Error fetching districts:', error));
        }
    });

    // Event listener for district change
    districtSelect.addEventListener('change', function () {
        const districtId = this.value;
        wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
        wardSelect.disabled = true;
        if (districtId) {
            fetch(window.BASE_URL + '/getWards?districtId=' + districtId)  // Sử dụng global BASE_URL
                    .then(response => response.json())
                    .then(data => {
                        wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';
                        data.forEach(function (ward) {
                            const option = document.createElement('option');
                            option.value = ward.id;
                            option.textContent = ward.name;
                            wardSelect.appendChild(option);
                        });
                        wardSelect.disabled = false;

                        // Pre-select ward if matching user value
                        if (userWardId) {
                            wardSelect.value = userWardId;
                        }
                    })
                    .catch(error => console.error('Error fetching wards:', error));
        }
    });

    // On page load, if user has province, trigger auto-load
    if (userProvinceId) {
        provinceSelect.value = userProvinceId;  // Pre-select province
        provinceSelect.dispatchEvent(new Event('change'));  // Trigger load districts
    }
});


