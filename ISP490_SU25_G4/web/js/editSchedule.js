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

    // Đảm bảo endDate >= scheduledDate
    startDateInput.addEventListener('change', () => {
        if (endDateInput.value && endDateInput.value < startDateInput.value) {
            endDateInput.value = startDateInput.value;
        }
        endDateInput.min = startDateInput.value;
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
    // Debug dữ liệu trước
    console.log('SCHEDULE_USERS:', window.SCHEDULE_USERS);
    console.log('ASSIGNED_USERS:', window.ASSIGNED_USERS);
    
    // Lấy dữ liệu từ window
    var users = window.SCHEDULE_USERS || [];
    var assignedUsers = window.ASSIGNED_USERS || [];
    
    // Debug dữ liệu sau khi lấy
    console.log('users after assignment:', users);
    console.log('assignedUsers after assignment:', assignedUsers);
    
    // Kiểm tra từng assignedUser
    assignedUsers.forEach(function(user, index) {
        console.log('assignedUser[' + index + ']:', user);
        console.log('- id:', user.id, typeof user.id);
        console.log('- name:', user.name, typeof user.name);
    });
    
    var selectedUsers = [...assignedUsers]; // Copy dữ liệu user đã được phân công
    
    console.log('selectedUsers after copy:', selectedUsers);
    
    // Hiển thị user cũ ngay khi load
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
        console.log('Adding user:', user);
        // Kiểm tra xem user đã được chọn chưa
        if (!selectedUsers.some(selected => selected.id === user.id)) {
            selectedUsers.push(user);
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
        console.log('Removing user with id:', userId);
        selectedUsers = selectedUsers.filter(function (user) {
            return user.id !== userId;
        });
        updateSelectedTags();
        if ($('#userDropdown').is(':visible'))
            showAvailableUsers();
    }

   function updateSelectedTags() {
    console.log('updateSelectedTags called with selectedUsers:', selectedUsers);
    
    var container = $('#selectedTags');
    container.empty();

    selectedUsers.forEach(function (user, index) {
        console.log('Creating tag for user[' + index + ']:', user);
        
        // Xử lý trường hợp name là boolean hoặc không hợp lệ
        var userName = user.name;
        var userId = user.id;
        
        if (typeof userName === 'boolean') {
            console.warn('user.name is boolean:', userName);
            // Tìm user name từ SCHEDULE_USERS dựa trên ID
            var foundUser = users.find(u => u.id === userId);
            userName = foundUser ? foundUser.name : 'User ID: ' + userId;
        } else if (!userName || userName === 'undefined' || userName === 'null') {
            console.warn('user.name is invalid:', userName);
            var foundUser = users.find(u => u.id === userId);
            userName = foundUser ? foundUser.name : 'User ID: ' + userId;
        }
        
        console.log('Final userName:', userName);
        
        var tag = $('<div class="tag">')
                .append(
                        $('<span class="tag-avatar">')
                        .css('background', getColorByName(userName))
                        .text(getInitials(userName))
                        )
                .append($('<span style="margin-left:5px">').text(userName))
                .append(
                        $('<span class="tag-close">').text('×').click(function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    removeUser(userId);
                })
                        );
        container.append(tag);
    });

    // Xóa tất cả các input hidden cũ có name="assignedUserIds"
    $('input[name="assignedUserIds"]').remove();

    // Tạo một input hidden cho mỗi user ID
    var form = $('#selectedTags').closest('form');
    selectedUsers.forEach(function (user) {
        if (user && user.id) {
            console.log('Creating hidden input for user.id:', user.id);
            form.append('<input type="hidden" name="assignedUserIds" value="' + user.id + '">');
        }
    });
}


    // Đóng dropdown khi click bên ngoài
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.tag-input-wrapper').length) {
            $('#userDropdown').hide();
        }
    });

    // Xử lý phím Enter và Esc
    $('#userSearch').keydown(function (e) {
        var $dropdown = $('#userDropdown');
        var $items = $dropdown.find('.dropdown-item:not(.disabled)');
        if (e.keyCode === 13) {
            e.preventDefault();
            var $firstItem = $items.first();
            if ($firstItem.length)
                $firstItem.click();
        } else if (e.keyCode === 27) {
            $dropdown.hide();
            $(this).blur();
        }
    });
});

