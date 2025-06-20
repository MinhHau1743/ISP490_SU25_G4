/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    const deleteModal = document.getElementById('deleteConfirmModal');
    const deleteTriggerBtn = document.querySelector('.delete-trigger-btn');

    if (deleteModal && deleteTriggerBtn) {
        const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        const deleteMessage = document.getElementById('deleteMessage');
        const closeBtn = deleteModal.querySelector('.close-modal-btn');

        const customerId = deleteTriggerBtn.dataset.id;
        const customerName = deleteTriggerBtn.dataset.name;

        deleteTriggerBtn.addEventListener('click', function (e) {
            e.preventDefault();
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa khách hàng <strong>${customerName}</strong>? Hành động này không thể hoàn tác.`;
            confirmDeleteBtn.href = `customer?action=delete&id=${customerId}`;
            deleteModal.style.display = 'flex';
            feather.replace();
        });

        const closeModal = () => {
            deleteModal.style.display = 'none';
        };

        cancelDeleteBtn.addEventListener('click', closeModal);
        closeBtn.addEventListener('click', closeModal);
        deleteModal.addEventListener('click', e => {
            if (e.target === deleteModal) {
                closeModal();
            }
        });
    }
});

