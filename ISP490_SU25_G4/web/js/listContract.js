/*
 * File: listContract.js
 * Mô tả: Xử lý logic cho bộ lọc, modal xác nhận xóa và các hiệu ứng trên trang danh sách hợp đồng.
 * Phiên bản: Hoàn thiện và tối ưu.
 */
document.addEventListener('DOMContentLoaded', function () {

    // ===================================
    // === CÀI ĐẶT CHUNG ===
    // ===================================

    // LƯU Ý QUAN TRỌNG:
    // Để dòng code dưới đây hoạt động chính xác, bạn cần thêm thuộc tính data-context-path
    // vào thẻ <body> trong các file JSP của mình như sau:
    // <body data-context-path="${pageContext.request.contextPath}">
    const contextPath = document.body.dataset.contextPath || '';
    // Kích hoạt các icon trên trang
    feather.replace();


    // ===================================
    // === 1. TỰ ĐỘNG ẨN THÔNG BÁO ===
    // ===================================
    const alertElements = document.querySelectorAll('.alert');
    if (alertElements.length > 0) {
        alertElements.forEach(alertBox => {
            setTimeout(() => {
                alertBox.style.transition = 'opacity 0.5s ease';
                alertBox.style.opacity = '0';
                setTimeout(() => {
                    alertBox.remove();
                }, 500); // Chờ hiệu ứng mờ kết thúc rồi mới xóa
            }, 5000); // 5 giây
        });
    }


    // ===================================
    // === 2. BẬT/TẮT BỘ LỌC ===
    // ===================================
    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');

    if (filterBtn && filterContainer) {
        // Mở/đóng vùng lọc khi nhấn nút "Bộ lọc"
        filterBtn.addEventListener('click', function () {
            const isOpen = filterContainer.style.display === 'block';
            filterContainer.style.display = isOpen ? 'none' : 'block';
            this.classList.toggle('active', !isOpen);
        });

        // Tự động mở vùng lọc nếu URL có chứa tham số lọc
        const urlParams = new URLSearchParams(window.location.search);
        let hasFilters = false;
        for (const [key, value] of urlParams.entries()) {
            if (key !== 'action' && key !== 'page' && value) {
                hasFilters = true;
                break;
            }
        }

        if (hasFilters) {
            filterContainer.style.display = 'block';
            filterBtn.classList.add('active');
        }
    }


    // ===================================
    // === 3. MODAL XÁC NHẬN XÓA ===
    // ===================================
    const deleteModal = document.getElementById('deleteConfirmModal');

    // Chỉ chạy logic modal nếu modal tồn tại trên trang
    if (deleteModal) {
        const modalCloseBtn = document.getElementById('modalCloseBtn');
        const modalCancelBtn = document.getElementById('modalCancelBtn');
        const modalConfirmDeleteBtn = document.getElementById('modalConfirmDeleteBtn');
        const contractNameToDelete = document.getElementById('contractNameToDelete');
        const deleteButtons = document.querySelectorAll('.delete-btn');

        const showModal = (id, name) => {
            if (contractNameToDelete)
                contractNameToDelete.textContent = name;
            if (modalConfirmDeleteBtn)
                modalConfirmDeleteBtn.href = `${contextPath}/contract?action=delete&id=${id}`;
            deleteModal.style.display = 'flex';
        };

        const hideModal = () => {
            deleteModal.style.display = 'none';
        };

        // Gán sự kiện cho tất cả các nút xóa có class '.delete-btn'
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                const contractId = this.dataset.id;
                const contractName = this.dataset.name;
                showModal(contractId, contractName);
            });
        });

        // Gán sự kiện cho các nút đóng/hủy
        if (modalCloseBtn)
            modalCloseBtn.addEventListener('click', hideModal);
        if (modalCancelBtn)
            modalCancelBtn.addEventListener('click', hideModal);

        // Đóng modal khi click ra ngoài vùng nền đen
        deleteModal.addEventListener('click', (event) => {
            if (event.target === deleteModal) {
                hideModal();
            }
        });
    }
});