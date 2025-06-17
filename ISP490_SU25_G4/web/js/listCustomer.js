/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    // Kích hoạt icons
    feather.replace();

    // === LOGIC CHO MODAL XÓA ===
    const deleteModal = document.getElementById('deleteConfirmModal');
    // Chỉ chạy JS nếu tìm thấy modal trong HTML
    if (deleteModal) {
        const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        const deleteMessage = document.getElementById('deleteMessage');
        const closeBtn = deleteModal.querySelector('.close-modal-btn');
        const deleteTriggerButtons = document.querySelectorAll('.delete-trigger-btn');

        const openDeleteModal = (id, name) => {
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa khách hàng "<strong>${name}</strong>"?`;
            confirmDeleteBtn.href = `deleteCustomer?id=${id}`;
            deleteModal.classList.add('show');
            feather.replace();
        };

        const closeDeleteModal = () => {
            deleteModal.classList.remove('show');
        };

        deleteTriggerButtons.forEach(button => {
            button.addEventListener('click', function (event) {
                event.preventDefault();
                const customerId = this.getAttribute('data-id');
                const customerName = this.getAttribute('data-name');
                openDeleteModal(customerId, customerName);
            });
        });

        cancelDeleteBtn.addEventListener('click', closeDeleteModal);
        closeBtn.addEventListener('click', closeDeleteModal);
        deleteModal.addEventListener('click', (e) => {
            if (e.target === deleteModal) {
                closeDeleteModal();
            }
        });
    }
});

