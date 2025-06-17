/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
                feather.replace();

                // Logic cho nút filter
                const filterBtn = document.getElementById('filterBtn');
                const filterContainer = document.getElementById('filterContainer');

                // Hiển thị bộ lọc nếu có tham số lọc trên URL
                const urlParams = new URLSearchParams(window.location.search);
                if (urlParams.has('minPrice') || urlParams.has('maxPrice') || urlParams.has('originId') || urlParams.has('categoryId')) {
                    if (filterContainer)
                        filterContainer.style.display = 'flex'; // Sử dụng flex để khớp với CSS
                    if (filterBtn)
                        filterBtn.classList.add('active');
                } else {
                    if (filterContainer)
                        filterContainer.style.display = 'none';
                }

                if (filterBtn && filterContainer) {
                    filterBtn.addEventListener('click', function () {
                        const isHidden = filterContainer.style.display === 'none';
                        filterContainer.style.display = isHidden ? 'flex' : 'none'; // Đổi thành flex
                        this.classList.toggle('active', isHidden);
                    });
                }

                // Logic cho modal xóa (không thay đổi)
                const deleteModal = document.getElementById('deleteConfirmModal');
                if (deleteModal) {
                    const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
                    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
                    const deleteMessage = document.getElementById('deleteMessage');
                    const closeBtn = deleteModal.querySelector('.close-modal-btn');
                    const deleteTriggerButtons = document.querySelectorAll('.delete-trigger-btn');

                    const openDeleteModal = (id, name) => {
                        deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa sản phẩm '<strong>${name}</strong>'?`;
                        // Cập nhật link xóa trong servlet của bạn
                        confirmDeleteBtn.href = `product?action=delete&id=${id}`;
                        deleteModal.classList.add('show');
                        feather.replace(); // Phải gọi lại để render icon X và alert-triangle trong modal
                    };

                    const closeDeleteModal = () => deleteModal.classList.remove('show');

                    deleteTriggerButtons.forEach(button => {
                        button.addEventListener('click', function (event) {
                            event.preventDefault();
                            openDeleteModal(this.getAttribute('data-id'), this.getAttribute('data-name'));
                        });
                    });

                    cancelDeleteBtn.addEventListener('click', closeDeleteModal);
                    closeBtn.addEventListener('click', closeDeleteModal);
                    deleteModal.addEventListener('click', e => {
                        if (e.target === deleteModal)
                            closeDeleteModal();
                    });
                }
            });