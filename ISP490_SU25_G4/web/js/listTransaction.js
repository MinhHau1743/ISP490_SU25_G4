/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {

    feather.replace();

    // Định nghĩa các biến cho modal
    // Tất cả các biến và hàm sử dụng chúng phải nằm trong DOMContentLoaded
    const modal = document.getElementById('delete-confirm-modal');
    const cancelBtn = document.getElementById('cancel-delete-btn');
    const confirmBtn = document.getElementById('confirm-delete-btn');
    const itemNameSpan = document.getElementById('item-to-delete-name');
    const deleteLinks = document.querySelectorAll('.delete-link');

    let requestIdToDelete = null;

    // Gán sự kiện cho tất cả các link xóa
    deleteLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            requestIdToDelete = this.dataset.id;
            const requestName = this.dataset.name;

            if (itemNameSpan) {
                itemNameSpan.textContent = requestName;
            }

            if (modal) {
                modal.style.display = 'flex';
                setTimeout(() => modal.classList.add('show'), 10);
                feather.replace();
            }
        });
    });

    // Hàm đóng modal
    function closeModal() {
        if (modal) {
            modal.classList.remove('show');
            setTimeout(() => modal.style.display = 'none', 200);
        }
    }

    // Gán sự kiện cho các nút và overlay
    if (cancelBtn) {
        cancelBtn.addEventListener('click', closeModal);
    }

    if (confirmBtn) {
        confirmBtn.addEventListener('click', function () {
            if (requestIdToDelete) {
                // SỬ DỤNG BIẾN TOÀN CỤC "window.APP_CONTEXT_PATH" ĐÃ ĐƯỢC TẠO TRONG JSP
                window.location.href = window.APP_CONTEXT_PATH + '/ticket?action=delete&id=' + requestIdToDelete;
            }
        });
    }

    if (modal) {
        modal.addEventListener('click', function (event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    }
});

