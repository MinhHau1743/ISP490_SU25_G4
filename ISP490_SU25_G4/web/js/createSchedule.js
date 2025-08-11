/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener('DOMContentLoaded', () => {
    // Xử lý logic cho ngày và giờ
    const startDateInput = document.getElementById('scheduled_date');
    const endDateInput = document.getElementById('end_date');
    const startTimeInput = document.getElementById('start_time');
    const endTimeInput = document.getElementById('end_time');
    const startTimeGroup = startTimeInput.closest('.form-group');
    const endTimeGroup = endTimeInput.closest('.form-group');

    // --- PHẦN ĐÃ CẬP NHẬT ---
    // Thiết lập ngày tối thiểu cho scheduled_date là ngày hôm nay
    const todayString = new Date().toISOString().split('T')[0];
    startDateInput.min = todayString;

    // Nếu giá trị ban đầu (từ server) là một ngày trong quá khứ,
    // tự động cập nhật nó thành ngày hôm nay để tránh lỗi.
    if (startDateInput.value && startDateInput.value < todayString) {
        startDateInput.value = todayString;
    }
    // Đảm bảo min của endDate cũng được cập nhật theo
    endDateInput.min = startDateInput.value;
    // --- HẾT PHẦN CẬP NHẬT ---

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

    // Đảm bảo endDate >= scheduledDate
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
    for (let i = 0; i < name.length; i++) code += name.charCodeAt(i);
    return colors[code % colors.length];
}

function getInitials(name) {
    // Lấy 2 chữ cái đầu của họ và tên cuối
    let ws = name.trim().split(' ');
    if (ws.length === 1) return ws[0][0].toUpperCase();
    return (ws[0][0] + ws[ws.length - 1][0]).toUpperCase();
}

$(document).ready(function () {
    // Lấy dữ liệu từ window, không cần forEach lồng nhau
    var users = window.SCHEDULE_USERS || [];
    var preselectedUserIds = window.SCHEDULE_SELECTED_USER_IDS || [];
    var selectedUsers = users.filter(u => preselectedUserIds.includes(u.id));
    updateSelectedTags();

    $('#userSearch').on('focus', function () {
        showAvailableUsers();
    });

    $('#userSearch').on('input', function () {
        var query = $(this).val().toLowerCase();
        var filtered = getAvailableUsers().filter(function (user) {
            return query === '' || user.name.toLowerCase().includes(query);
        });
        showDropdown(filtered);
    });

    function getAvailableUsers() {
        return users.filter(u =>
            !selectedUsers.some(selected => selected.id === u.id)
        );
    }

    function showAvailableUsers() {
        showDropdown(getAvailableUsers());
    }

    function showDropdown(filteredUsers) {
        var dropdown = $('#userDropdown');
        dropdown.empty();
        if (filteredUsers.length === 0) {
            dropdown.append('<div class="dropdown-item disabled" style="color: #999;">Đã chọn hết nhân viên</div>');
        } else {
            filteredUsers.forEach(function (user) {
                var item = $('<div class="dropdown-item">')
                    .append(
                        $('<span>')
                            .addClass('tag-avatar')
                            .css('background', getColorByName(user.name))
                            .text(getInitials(user.name))
                    )
                    .append($('<span style="margin-left:8px">').text(user.name))
                    .data('user', user)
                    .click(function (e) {
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
        selectedUsers.push(user);
        updateSelectedTags();
        $('#userSearch').val('');
        setTimeout(function () {
            showAvailableUsers();
            $('#userSearch').focus();
        }, 10);
    }

    function removeUser(userId) {
        selectedUsers = selectedUsers.filter(function (user) {
            return user.id !== userId;
        });
        updateSelectedTags();
        if ($('#userDropdown').is(':visible')) showAvailableUsers();
    }

    function updateSelectedTags() {
        var container = $('#selectedTags');
        container.empty();
        selectedUsers.forEach(function (user) {
            var tag = $('<div class="tag">')
                .append(
                    $('<span class="tag-avatar">')
                        .css('background', getColorByName(user.name))
                        .text(getInitials(user.name))
                )
                .append($('<span style="margin-left:5px">').text(user.name))
                .append(
                    $('<span class="tag-close">').text('×').click(function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        removeUser(user.id);
                    })
                );
            container.append(tag);
        });
        var userIds = selectedUsers.map(function (user) {
            return user.id;
        });
        $('#hiddenUserIds').val(userIds.join(','));
    }

    // Dismiss dropdown nếu click ngoài
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.tag-input-wrapper').length) {
            $('#userDropdown').hide();
        }
    });

    // Keyboard (Enter/Esc)
    $('#userSearch').keydown(function (e) {
        var $dropdown = $('#userDropdown');
        var $items = $dropdown.find('.dropdown-item:not(.disabled)');
        if (e.keyCode === 13) {
            e.preventDefault();
            var $firstItem = $items.first();
            if ($firstItem.length) $firstItem.click();
        } else if (e.keyCode === 27) {
            $dropdown.hide();
            $(this).blur();
        }
    });
});
