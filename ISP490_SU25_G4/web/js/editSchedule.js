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