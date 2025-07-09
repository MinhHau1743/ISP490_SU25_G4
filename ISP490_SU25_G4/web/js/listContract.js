/*
 * File: listContract.js
 * Phiên bản tối ưu: Gộp logic bộ lọc, xóa bằng modal, và tự động ẩn thông báo.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Kích hoạt icon
    feather.replace();

    // ===================================
    // === XỬ LÝ TỰ ĐỘNG ẨN THÔNG BÁO  ===
    // ===================================
    const alertBox = document.querySelector('.alert');
    if (alertBox) {
        // Đặt thời gian chờ là 5 giây (5000 mili giây)
        setTimeout(() => {
            alertBox.classList.add('fade-out');

            // Sau khi hiệu ứng mờ kết thúc (0.5s), ẩn hoàn toàn phần tử
            setTimeout(() => {
                alertBox.style.display = 'none';
            }, 500); // Thời gian này phải bằng với transition trong CSS

        }, 5000);
    }

    // ===================================
    // === XỬ LÝ BỘ LỌC                ===
    // ===================================
    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');

    if (filterBtn && filterContainer) {
        filterBtn.addEventListener('click', function () {
            filterContainer.classList.toggle('open');
            this.classList.toggle('active');
        });
        const urlParams = new URLSearchParams(window.location.search);
        const hasFilters = urlParams.has('searchQuery') || urlParams.has('status') || urlParams.has('startDateFrom') || urlParams.has('startDateTo');
        if (hasFilters) {
            filterContainer.classList.add('open');
            filterBtn.classList.add('active');
        }
    }

    // ===================================
    // === XỬ LÝ XÓA VỚI MODAL        ===
    // ===================================
    const tableContainer = document.querySelector('.data-table-container');
    const deleteModal = document.getElementById('deleteConfirmModal');

    if (tableContainer && deleteModal) {
        const confirmDeleteBtn = deleteModal.querySelector('#confirmDeleteBtn');
        const deleteMessage = deleteModal.querySelector('#deleteMessage');
        const closeDeleteModalBtns = deleteModal.querySelectorAll('.close-modal-btn');
        const deleteUrlBase = `${getContextPath()}/deleteContract`;

        tableContainer.addEventListener('click', function (e) {
            const deleteBtn = e.target.closest('.delete-btn');
            if (deleteBtn) {
                const id = deleteBtn.dataset.id;
                const name = deleteBtn.dataset.name;
                deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa hợp đồng <strong>${name}</strong> không?`;
                confirmDeleteBtn.href = `${deleteUrlBase}?id=${id}`;
                deleteModal.style.display = 'flex';
            }
        });

        const closeModal = () => {
            deleteModal.style.display = 'none';
        };
        closeDeleteModalBtns.forEach(btn => btn.addEventListener('click', closeModal));
        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal)
                closeModal();
        });
    }

    // Hàm phụ để lấy contextPath
    function getContextPath() {
        return window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));
    }
});