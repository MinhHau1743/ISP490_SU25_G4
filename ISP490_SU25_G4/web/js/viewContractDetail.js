/*
 * File: viewContractDetail.js
 * Mô tả: Xử lý logic cho modal xác nhận xóa trên trang chi tiết hợp đồng.
 */
document.addEventListener('DOMContentLoaded', function () {
    // Kích hoạt icon
    if (typeof feather !== 'undefined') {
        feather.replace();
    }

    // ===================================
    // === XỬ LÝ MODAL XÁC NHẬN XÓA ===
    // ===================================
    const deleteModalDetail = document.getElementById('deleteConfirmModal');
    if (deleteModalDetail) {
        const contextPath = document.body.dataset.contextPath || '';
        const modalCloseBtn = deleteModalDetail.querySelector('#modalCloseBtn');
        const modalCancelBtn = deleteModalDetail.querySelector('#modalCancelBtn');
        const modalConfirmDeleteBtn = deleteModalDetail.querySelector('#modalConfirmDeleteBtn');
        const contractNameToDelete = deleteModalDetail.querySelector('#contractNameToDelete');
        const deleteButton = document.querySelector('.delete-btn'); // Nút xóa trên trang chi tiết

        const showModal = (id, name) => {
            if (contractNameToDelete)
                contractNameToDelete.textContent = name;
            if (modalConfirmDeleteBtn)
                modalConfirmDeleteBtn.href = `${contextPath}/contract?action=delete&id=${id}`;
            deleteModalDetail.style.display = 'flex';
        };

        const hideModal = () => {
            deleteModalDetail.style.display = 'none';
        };

        if (deleteButton) {
            deleteButton.addEventListener('click', function () {
                const contractId = this.dataset.id;
                const contractName = this.dataset.name;
                showModal(contractId, contractName);
            });
        }

        if (modalCloseBtn)
            modalCloseBtn.addEventListener('click', hideModal);
        if (modalCancelBtn)
            modalCancelBtn.addEventListener('click', hideModal);
        deleteModalDetail.addEventListener('click', (e) => {
            if (e.target === deleteModalDetail) {
                hideModal();
            }
        });
    }
});