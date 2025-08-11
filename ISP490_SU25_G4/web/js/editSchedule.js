document.addEventListener('DOMContentLoaded', () => {
    feather.replace();

    // Xử lý logic cho ngày và giờ
    const startDateInput = document.getElementById('scheduledDate');
    const endDateInput = document.getElementById('endDate');
    const startTimeInput = document.getElementById('startTime');
    const endTimeInput = document.getElementById('endTime');
    const startTimeGroup = startTimeInput.closest('.form-group');
    const endTimeGroup = endTimeInput.closest('.form-group');

    // --- BẮT ĐẦU PHẦN THÊM MỚI ---
    // Thiết lập ngày tối thiểu cho scheduledDate là ngày hôm nay
    const todayString = new Date().toISOString().split('T')[0];
    startDateInput.min = todayString;

    // Nếu giá trị ban đầu (khi chỉnh sửa) là một ngày trong quá khứ,
    // tự động cập nhật nó thành ngày hôm nay để đảm bảo tính hợp lệ.
    if (startDateInput.value && startDateInput.value < todayString) {
        startDateInput.value = todayString;
    }
    // Đảm bảo min của endDate cũng được cập nhật theo
    if (!endDateInput.min || endDateInput.min < startDateInput.value) {
        endDateInput.min = startDateInput.value;
    }
    // --- KẾT THÚC PHẦN THÊM MỚI ---

    // Hàm kiểm tra và ẩn/hiện trường giờ
    function toggleTimeFields() {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;

        // Nếu có cả ngày bắt đầu và ngày kết thúc khác nhau
        if (startDate && endDate && startDate !== endDate) {
            // Ẩn trường giờ
            startTimeGroup.style.display = 'none';
            endTimeGroup.style.display = 'none';
            // Xóa giá trị giờ
            startTimeInput.value = '';
            endTimeInput.value = '';
        } else {
            // Hiện trường giờ
            startTimeGroup.style.display = 'block';
            endTimeGroup.style.display = 'block';
        }
    }

 // Đảm bảo endDate > scheduledDate
startDateInput.addEventListener('change', () => {
    // 1. Lấy ngày bắt đầu và tạo đối tượng Date
    const startDate = new Date(startDateInput.value);

    // 2. Tính toán ngày tiếp theo (ngày sớm nhất có thể chọn cho endDate)
    const nextDay = new Date(startDate);
    nextDay.setDate(startDate.getDate() + 1);

    // 3. Định dạng lại thành chuỗi 'YYYY-MM-DD'
    const minEndDateString = nextDay.toISOString().split('T')[0];

    // 4. Đặt ngày tối thiểu cho ô endDate
    endDateInput.min = minEndDateString;

    // 5. (Quan trọng) Nếu ngày kết thúc hiện tại nhỏ hơn ngày tối thiểu mới,
    //    hãy cập nhật nó thành ngày tối thiểu đó.
    if (endDateInput.value && endDateInput.value < minEndDateString) {
        endDateInput.value = minEndDateString;
    }

    // Gọi hàm khác (giữ nguyên)
    toggleTimeFields();
});


    // Xử lý khi endDate thay đổi
    endDateInput.addEventListener('change', () => {
        toggleTimeFields();
    });

    // Đảm bảo endTime hợp lý khi startTime thay đổi
    startTimeInput.addEventListener('change', () => {
        if (startTimeInput.value && !endTimeInput.value) {
            const startTime = new Date(`1970-01-01T${startTimeInput.value}`);
            startTime.setMinutes(startTime.getMinutes() + 60); // Mặc định thêm 1 giờ
            endTimeInput.value = startTime.toTimeString().slice(0, 5);
        }
    });

    // Đảm bảo endTime >= startTime nếu cùng ngày
    endTimeInput.addEventListener('change', () => {
        if (startTimeInput.value && endTimeInput.value && startDateInput.value === endDateInput.value) {
            const startTime = startTimeInput.value;
            const endTime = endTimeInput.value;
            if (endTime < startTime) {
                endTimeInput.value = startTime;
            }
        }
    });

    // Kiểm tra trạng thái ban đầu khi trang load
    toggleTimeFields();
});

document.addEventListener('DOMContentLoaded', function () {
    // Tìm đến các phần tử cần thiết
    const colorPalette = document.querySelector('.color-palette');
    const hiddenColorInput = document.getElementById('color');

    // Nếu không tìm thấy các phần tử thì không làm gì cả
    if (!colorPalette || !hiddenColorInput) {
        return;
    }

    const colorSwatches = colorPalette.querySelectorAll('.color-swatch');

    // Hàm để cập nhật trạng thái "selected"
    const updateSelection = (selectedSwatch) => {
        // 1. Xóa class 'selected' khỏi tất cả các ô
        colorSwatches.forEach(swatch => {
            swatch.classList.remove('selected');
        });

        // 2. Thêm class 'selected' vào ô vừa được bấm
        selectedSwatch.classList.add('selected');

        // 3. Cập nhật giá trị cho input ẩn
        hiddenColorInput.value = selectedSwatch.dataset.color;
    };

    // Thiết lập trạng thái ban đầu khi tải trang (quan trọng cho form edit)
    const initialColor = hiddenColorInput.value;
    const initialSwatch = colorPalette.querySelector(`.color-swatch[data-color="${initialColor}"]`);
    if (initialSwatch) {
        initialSwatch.classList.add('selected');
    } else if (colorSwatches.length > 0) {
        // Nếu màu ban đầu không có trong palette, chọn màu đầu tiên
        colorSwatches[0].classList.add('selected');
        hiddenColorInput.value = colorSwatches[0].dataset.color;
    }

    // Gán sự kiện click cho mỗi ô màu
    colorSwatches.forEach(swatch => {
        swatch.addEventListener('click', function () {
            updateSelection(this);
        });
    });
});

function getColorByName(name) {
    // Hàm tạo màu tự sinh, kiểu Facebook/Google
    const colors = ['#1976d2', '#388e3c', '#d32f2f', '#fbc02d', '#0288d1', '#8e24aa', '#ffa726', '#43a047', '#e53935', '#3949ab'];
    let code = 0;
    for (let i = 0; i < name.length; i++)
        code += name.charCodeAt(i);
    return colors[code % colors.length];
}

function getInitials(name) {
    // Lấy 2 chữ cái đầu của họ và tên cuối
    let ws = name.trim().split(' ');
    if (ws.length === 1)
        return ws[0][0].toUpperCase();
    return (ws[0][0] + ws[ws.length - 1][0]).toUpperCase();
}

$(document).ready(function () {
    // ====== LẤY & CHUẨN HOÁ DỮ LIỆU ======
    console.log('SCHEDULE_USERS (raw):', window.SCHEDULE_USERS);
    console.log('ASSIGNED_USERS (raw):', window.ASSIGNED_USERS);

    // Lấy dữ liệu từ window
    var users = Array.isArray(window.SCHEDULE_USERS) ? window.SCHEDULE_USERS.slice() : [];
    var assignedUsers = Array.isArray(window.ASSIGNED_USERS) ? window.ASSIGNED_USERS.slice() : [];

    // Chuẩn hoá users: ép id về string, giữ name là string an toàn
    users = users.map(function (u) {
        var id = u && (u.id ?? u.userId);
        var name =
                (u && typeof u.name === 'string' && u.name) ||
                (u && u.fullName) ||
                (u && u.username) ||
                '';
        return {id: String(id), name: String(name)};
    });

    // Tạo map tra cứu nhanh theo id
    var userById = {};
    users.forEach(function (u) {
        userById[u.id] = u;
    });

    // Chuẩn hoá assignedUsers: ép id về string, nếu name boolean/invalid thì map lại từ users theo id
    // sau khi đã có `users` và userById
    assignedUsers = assignedUsers.map(function (u) {
        var id = String(u && (u.id ?? u.userId));
        // Luôn ưu tiên tên từ SCHEDULE_USERS
        if (userById[id] && userById[id].name) {
            return {id, name: userById[id].name};
        }
        // fallback cuối
        var fallback =
                (typeof u.fullName === 'string' && u.fullName) ||
                (typeof u.username === 'string' && u.username) ||
                ('User ID: ' + id);
        return {id, name: fallback};
    });


    // Danh sách user đã chọn lúc load trang
    var selectedUsers = assignedUsers.slice();

    console.log('users (normalized):', users);
    console.log('assignedUsers (normalized):', assignedUsers);
    console.log('selectedUsers (init):', selectedUsers);

    // ====== KHỞI TẠO UI BAN ĐẦU ======
    updateSelectedTags();

    $('#userSearch').on('focus', function () {
        showAvailableUsers();
    });

    $('#userSearch').on('input', function () {
        var query = ($(this).val() || '').toLowerCase().trim();
        var filtered = getAvailableUsers().filter(function (user) {
            return query === '' || (user.name || '').toLowerCase().includes(query);
        });
        showDropdown(filtered);
    });

    // ====== HÀM TIỆN ÍCH ======
    function sameId(a, b) {
        return String(a) === String(b);
    }

    function getAvailableUsers() {
        return users.filter(function (u) {
            return !selectedUsers.some(function (selected) {
                return sameId(selected.id, u.id);
            });
        });
    }

    function showAvailableUsers() {
        showDropdown(getAvailableUsers());
    }

    function showDropdown(filteredUsers) {
        var dropdown = $('#userDropdown');
        dropdown.empty();

        if (!filteredUsers.length) {
            dropdown.append('<div class="dropdown-item disabled" style="color:#999;">Đã chọn hết nhân viên</div>');
        } else {
            filteredUsers.forEach(function (user) {
                var name = String(user.name || '');
                var item = $('<div class="dropdown-item">')
                        .append(
                                $('<span>').addClass('tag-avatar')
                                .css('background', getColorByName(name))
                                .text(getInitials(name))
                                )
                        .append($('<span style="margin-left:8px">').text(name))
                        .data('user', user)
                        .on('click', function (e) {
                            e.preventDefault();
                            e.stopPropagation();
                            addUser($(this).data('user'));
                        });

                dropdown.append(item);
            });
        }
        dropdown.show();
    }

    function addUser(user) {
        if (!user)
            return;
        var id = String(user.id ?? user.userId);
        var name = (typeof user.name === 'string' && user.name) ? user.name : (userById[id] ? userById[id].name : ('User ID: ' + id));
        var normalized = {id: String(id), name: String(name)};

        console.log('Adding user (normalized):', normalized);

        if (!selectedUsers.some(function (s) {
            return sameId(s.id, normalized.id);
        })) {
            selectedUsers.push(normalized);
            updateSelectedTags();
            $('#userSearch').val('');
            setTimeout(function () {
                if ($('#userDropdown').is(':visible')) {
                    showAvailableUsers();
                }
                $('#userSearch').focus();
            }, 10);
        }
    }

    function removeUser(userId) {
        var id = String(userId);
        console.log('Removing user with id:', id);
        selectedUsers = selectedUsers.filter(function (user) {
            return !sameId(user.id, id);
        });
        updateSelectedTags();
        if ($('#userDropdown').is(':visible'))
            showAvailableUsers();
    }

    function resolveUserNameById(userId) {
        var id = String(userId);
        if (userById[id] && userById[id].name)
            return userById[id].name;
        return 'User ID: ' + id;
    }

    function sanitizeName(name, userId) {
        if (typeof name === 'boolean' || !name || name === 'undefined' || name === 'null') {
            return resolveUserNameById(userId);
        }
        return String(name);
    }

    function updateSelectedTags() {
        console.log('updateSelectedTags with selectedUsers:', selectedUsers);

        var container = $('#selectedTags');
        container.empty();

        selectedUsers.forEach(function (user, index) {
            var userId = String(user && (user.id ?? user.userId));
            var userName = sanitizeName(user && user.name, userId);

            console.log('Tag[' + index + ']:', {id: userId, name: userName});

            var tag = $('<div class="tag">')
                    .append(
                            $('<span class="tag-avatar">')
                            .css('background', getColorByName(userName))
                            .text(getInitials(userName))
                            )
                    .append($('<span style="margin-left:5px">').text(userName))
                    .append(
                            $('<span class="tag-close">')
                            .text('×')
                            .on('click', function (e) {
                                e.preventDefault();
                                e.stopPropagation();
                                removeUser(userId);
                            })
                            );

            container.append(tag);
        });

        // Xoá toàn bộ hidden input cũ
        $('input[name="assignedUserIds"]').remove();

        // Tạo lại hidden input theo selectedUsers
        var form = $('#selectedTags').closest('form');
        selectedUsers.forEach(function (user) {
            if (user && user.id != null) {
                var id = String(user.id);
                console.log('Create hidden input for:', id);
                form.append('<input type="hidden" name="assignedUserIds" value="' + id + '">');
            }
        });
    }

    // Đóng dropdown khi click ngoài
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.tag-input-wrapper').length) {
            $('#userDropdown').hide();
        }
    });

    // Xử lý Enter chọn item đầu, Esc đóng
    $('#userSearch').on('keydown', function (e) {
        var $dropdown = $('#userDropdown');
        var $items = $dropdown.find('.dropdown-item:not(.disabled)');
        if (e.keyCode === 13) { // Enter
            e.preventDefault();
            var $firstItem = $items.first();
            if ($firstItem.length)
                $firstItem.click();
        } else if (e.keyCode === 27) { // Esc
            $dropdown.hide();
            $(this).blur();
        }
    });

    // ====== GHI CHÚ ======
    // - Yêu cầu có các hàm getColorByName(name) và getInitials(name) ở phạm vi global.
    // - Trên server/JSP, đảm bảo window.SCHEDULE_USERS và window.ASSIGNED_USERS là JSON chuẩn.
});


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
        fetchAndPopulate(districtSelect, `${contextPath}/schedule?action=getDistricts&provinceId=${provinceId}`, selectedId, loadWardsCallback);
    }

    // Tải wards khi đã chọn district
    function loadWards(selectedId) {
        const districtId = districtSelect.value;
        if (!districtId) {
            wardSelect.innerHTML = '<option value="">-- Chọn Phường/Xã --</option>';
            wardSelect.disabled = true;
            return;
        }
        fetchAndPopulate(wardSelect, `${contextPath}/schedule?action=getWards&districtId=${districtId}`, selectedId);
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
