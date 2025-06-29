/*
 * File: listContract.js
 * Phiên bản cải tiến, dễ bảo trì, logic chính xác và hiện đại.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Kích hoạt icon
    feather.replace();

    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');

    // Dừng thực thi nếu không tìm thấy các element cần thiết
    if (!filterBtn || !filterContainer) {
        console.error("Không tìm thấy nút lọc hoặc vùng chứa bộ lọc.");
        return;
    }

    // 1. Logic bật/tắt bộ lọc khi nhấn nút
    filterBtn.addEventListener('click', function () {
        // Thêm/xóa class, mọi việc về style sẽ do CSS xử lý
        filterContainer.classList.toggle('show');
        this.classList.toggle('active');
    });

    // 2. Logic tự động mở bộ lọc nếu URL có tham số lọc
    function checkAndShowFilters() {
        const urlParams = new URLSearchParams(window.location.search);
        
        // Dùng .has() để kiểm tra sự tồn tại của tham số, chính xác hơn .get()
        const hasFilters = urlParams.has('searchQuery') ||
                           urlParams.has('status') ||
                           urlParams.has('startDateFrom') ||
                           urlParams.has('startDateTo');
        
        // Nếu có bất kỳ tham số lọc nào
        if (hasFilters) {
            filterContainer.classList.add('show');
            filterBtn.classList.add('active');
        }
    }

    // Chạy hàm kiểm tra ngay khi tải trang
    checkAndShowFilters();
});

/**
 * Hàm xác nhận trước khi xóa (soft delete) một hợp đồng.
 * @param {string} id - ID của hợp đồng cần xóa.
 * @param {string} code - Mã của hợp đồng để hiển thị trong thông báo.
 */
function confirmDelete(id, code) {
    const confirmationMessage = `Bạn có chắc chắn muốn xóa hợp đồng "${code}"?\n\nHành động này sẽ đánh dấu hợp đồng là đã xóa và không thể hoàn tác dễ dàng.`;
    
    if (confirm(confirmationMessage)) {
        // Chuyển hướng đến servlet để xử lý việc "soft delete"
        window.location.href = `contract?action=delete&id=${id}`;
    }
}