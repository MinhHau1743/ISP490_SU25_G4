/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // --- 1. LOGIC CHUYỂN ĐỔI VIEW (TUẦN/THÁNG) ---


    // --- 2. LOGIC POPOVER CHO LỊCH THÁNG ---
    const popover = document.getElementById('month-task-popover');
    const taskItems = document.querySelectorAll('.task-item');
    taskItems.forEach(item => {
        item.addEventListener('click', function (event) {
            event.stopPropagation();
            const taskId = item.getAttribute('data-task-id');
            const taskName = item.getAttribute('data-item-name');

            document.getElementById('popover-view').href = `/your-app/schedule/view?id=${taskId}`;
            document.getElementById('popover-edit').href = `/your-app/schedule/edit?id=${taskId}`;
            const popoverDeleteBtn = document.getElementById('popover-delete');
            popoverDeleteBtn.setAttribute('data-item-id', taskId);
            popoverDeleteBtn.setAttribute('data-item-name', taskName);

            const rect = item.getBoundingClientRect();
            popover.style.display = 'block';
            popover.style.top = `${rect.bottom + window.scrollY + 5}px`;
            popover.style.left = `${rect.left + window.scrollX}px`;
            feather.replace(); // Re-render icons in popover
        });
    });
    document.addEventListener('click', function (event) {
        if (popover && popover.style.display === 'block' && !popover.contains(event.target) && !event.target.closest('.task-item')) {
            popover.style.display = 'none';
        }
    });

    // --- 3. LOGIC MODAL XÁC NHẬN XÓA (DÙNG CHUNG) ---
    const modal = document.getElementById('delete-confirm-modal');
    if (modal) {
        const closeBtns = modal.querySelectorAll('.modal-close-btn, .btn-cancel');
        const itemNameSpan = document.getElementById('item-name-to-delete');
        const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

        const openDeleteModal = (id, name) => {
            itemNameSpan.textContent = `"${name}"`;
            // Thay thế URL này bằng action xóa thực tế của bạn
            confirmDeleteBtn.href = `/your-app/schedule/delete?id=${id}`;
            modal.style.display = 'flex';
            feather.replace(); // Re-render icons in modal
        };

        const closeDeleteModal = () => {
            modal.style.display = 'none';
        };

        // Lắng nghe sự kiện click trên toàn bộ body
        document.body.addEventListener('click', function (event) {
            // Tìm phần tử trigger xóa gần nhất
            const deleteTrigger = event.target.closest('.delete-trigger');
            if (deleteTrigger) {
                event.preventDefault();
                if (popover && popover.style.display === 'block') {
                    popover.style.display = 'none'; // Đóng popover nếu nó đang mở
                }
                const itemId = deleteTrigger.getAttribute('data-item-id');
                const itemName = deleteTrigger.getAttribute('data-item-name');
                openDeleteModal(itemId, itemName);
            }
        });

        closeBtns.forEach(btn => btn.addEventListener('click', (e) => {
                e.preventDefault();
                closeDeleteModal();
            }));
    }
});

