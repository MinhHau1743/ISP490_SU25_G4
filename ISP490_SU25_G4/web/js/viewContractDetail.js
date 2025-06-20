/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // Logic cho modal xóa
    const deleteModal = document.getElementById('deleteConfirmModal');
    const deleteTriggerBtn = document.querySelector('.delete-trigger-btn');

    if (deleteModal && deleteTriggerBtn) {
        const cancelDeleteBtn = deleteModal.querySelector('#cancelDeleteBtn');
        const confirmDeleteBtn = deleteModal.querySelector('#confirmDeleteBtn');
        const deleteMessage = deleteModal.querySelector('#deleteMessage');
        const closeBtn = deleteModal.querySelector('.close-modal-btn');

        deleteTriggerBtn.addEventListener('click', function (e) {
            e.preventDefault();
            const id = this.dataset.id;
            const name = this.dataset.name;
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa hợp đồng <strong>${name}</strong>?`;
            confirmDeleteBtn.href = `contract?action=delete&id=${id}`;
            deleteModal.style.display = 'flex';
            feather.replace();
        });

        const closeModal = () => {
            deleteModal.style.display = 'none';
        };

        cancelDeleteBtn.addEventListener('click', closeModal);
        closeBtn.addEventListener('click', closeModal);
        deleteModal.addEventListener('click', e => {
            if (e.target === deleteModal)
                closeModal();
        });
    }
});
