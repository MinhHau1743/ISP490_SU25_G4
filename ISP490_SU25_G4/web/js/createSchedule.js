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
document.addEventListener('DOMContentLoaded', function() {
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
        swatch.addEventListener('click', function() {
            updateSelection(this);
        });
    });
});