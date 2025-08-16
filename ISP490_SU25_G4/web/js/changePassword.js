/**
 * Hàm tiện ích để ẩn một element một cách mượt mà.
 * @param {HTMLElement} element - Element cần ẩn.
 * @param {number} duration - Thời gian của hiệu ứng (tính bằng mili giây).
 */
function fadeOutAndHide(element, duration = 500) {
    if (!element)
        return;

    // Thêm class để kích hoạt hiệu ứng transition trong CSS
    element.classList.add('fading-out');

    // Sau khi hiệu ứng kết thúc, ẩn hẳn element đi
    setTimeout(() => {
        element.style.display = 'none';
        element.classList.remove('fading-out'); // Dọn dẹp class
    }, duration);
}

// Chạy khi cấu trúc HTML của trang đã sẵn sàng
document.addEventListener("DOMContentLoaded", function () {
    const alertBox = document.getElementById("customAlert");
    const progressBar = document.getElementById("alertProgressBar");

    if (alertBox && progressBar) {
        // Bắt đầu hiệu ứng chạy của progress bar
        setTimeout(() => {
            progressBar.style.transition = 'width 5s linear'; // Đặt transition trong JS để kiểm soát
            progressBar.style.width = '0%';
        }, 100); // Đợi 100ms để trình duyệt kịp render trước khi bắt đầu transition

        // Sau 5.1 giây, cho alert mờ dần và biến mất
        setTimeout(() => {
            fadeOutAndHide(alertBox, 500); // Dùng hàm tiện ích
        }, 5100);
    }
});

// Chạy khi toàn bộ tài nguyên của trang (ảnh, css,...) đã tải xong
window.addEventListener("load", function () {
    const overlay = document.getElementById("loadingOverlay");
    fadeOutAndHide(overlay, 500); // Dùng lại hàm tiện ích
});