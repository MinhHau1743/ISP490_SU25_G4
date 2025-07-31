/*
 * File: listContract.js
 * Phiên bản hoàn thiện: Tích hợp đầy đủ logic cho bộ lọc, modal xóa,
 * và nút "Xóa lọc" hoạt động chính xác.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Hàm phụ để lấy đường dẫn gốc của ứng dụng (ví dụ: /ISP490_SU25_G4)
    function getContextPath() {
        return window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    }

    // Kích hoạt các icon trên trang
    feather.replace();

    // ===================================
    // === 1. XỬ LÝ TỰ ĐỘNG ẨN THÔNG BÁO ===
    // ===================================
    const alertBox = document.querySelector('.alert');
    if (alertBox) {
        setTimeout(() => {
            alertBox.classList.add('fade-out');
            setTimeout(() => {
                alertBox.style.display = 'none';
            }, 500);
        }, 5000);
    }

    // ===================================
    // === 2. KHÔI PHỤC: XỬ LÝ BẬT/TẮT BỘ LỌC ===
    // ===================================
    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');

    if (filterBtn && filterContainer) {
        // Mở/đóng vùng lọc khi nhấn nút "Bộ lọc"
        filterBtn.addEventListener('click', function () {
            filterContainer.classList.toggle('open');
            this.classList.toggle('active');
        });

        // Tự động mở vùng lọc nếu URL có chứa tham số lọc
        const urlParams = new URLSearchParams(window.location.search);
        const hasFilters = urlParams.get('searchQuery') || urlParams.get('status') || urlParams.get('startDateFrom') || urlParams.get('startDateTo');
        
        // Chỉ mở khi có giá trị thực sự (không phải chuỗi rỗng)
        if (hasFilters) {
            filterContainer.classList.add('open');
            filterBtn.classList.add('active');
        }
    }

    // ===================================================
    // === 3. XỬ LÝ NÚT "XÓA LỌC"                    ===
    // ===================================================
    const resetFilterBtn = document.getElementById('resetFilterBtn');
    if (resetFilterBtn) {
        resetFilterBtn.addEventListener('click', function (event) {
            event.preventDefault();
            // Chuyển hướng về trang gốc để xóa các tham số lọc
            window.location.href = getContextPath() + '/listContract';
        });
    }

    // ===================================
    // === 4. XỬ LÝ MODAL XÁC NHẬN XÓA ===
    // ===================================
    const deleteModal = document.getElementById('deleteConfirmModal');
    if (deleteModal) {
        const tableContainer = document.querySelector('.data-table-container');
        const confirmDeleteBtn = deleteModal.querySelector('#confirmDeleteBtn');
        const deleteMessage = deleteModal.querySelector('#deleteMessage');
        const closeDeleteModalBtns = deleteModal.querySelectorAll('.close-modal-btn');

        if (tableContainer && confirmDeleteBtn && deleteMessage && closeDeleteModalBtns.length > 0) {
            tableContainer.addEventListener('click', function (e) {
                const deleteBtn = e.target.closest('.delete-btn');
                if (deleteBtn) {
                    const id = deleteBtn.dataset.id;
                    const name = deleteBtn.dataset.name;
                    deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa hợp đồng <strong>${name}</strong> không?`;
                    confirmDeleteBtn.href = `${getContextPath()}/deleteContract?id=${id}`;
                    deleteModal.style.display = 'flex';
                }
            });

            const closeModal = () => {
                deleteModal.style.display = 'none';
            };
            closeDeleteModalBtns.forEach(btn => btn.addEventListener('click', closeModal));
            deleteModal.addEventListener('click', (e) => {
                if (e.target === deleteModal) {
                    closeModal();
                }
            });
        }
    }
});