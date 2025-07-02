/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// js/delete-modal-handler.js

/**
 * Script xử lý chung cho hộp thoại xác nhận xóa.
 * Script này sẽ tìm tất cả các nút có class '.delete-trigger-btn'
 * và gắn sự kiện click để hiển thị hộp thoại xác nhận.
 */
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('deleteConfirmModal');

    // Nếu không có hộp thoại (modal) trên trang, không làm gì cả.
    if (!modal) {
        return;
    }

    // Lấy các thành phần bên trong hộp thoại
    const deleteMessage = document.getElementById('deleteMessage');
    const customerIdInput = document.getElementById('customerIdToDelete');
    const cancelBtn = document.getElementById('cancelDeleteBtn');

    // Lấy TẤT CẢ các nút có chức năng xóa trên trang
    const deleteButtons = document.querySelectorAll('.delete-trigger-btn');

    // Hàm để hiển thị hộp thoại
    const showModal = (event) => {
        // Ngăn chặn hành vi mặc định của thẻ <a>
        event.preventDefault();

        // Lấy phần tử được click (luôn là nút đã được gắn sự kiện)
        const button = event.currentTarget;
        const customerId = button.dataset.id;
        const customerName = button.dataset.name || "đối tượng này";

        // Cập nhật nội dung hộp thoại
        deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa <strong>"${customerName}"</strong>? Hành động này không thể hoàn tác.`;
        customerIdInput.value = customerId;

        // Hiển thị hộp thoại
        modal.classList.add('show');

        // Cập nhật lại icon trong hộp thoại (nếu cần)
        if (typeof feather !== 'undefined') {
            feather.replace();
        }
    };

    // Gắn sự kiện `showModal` cho mỗi nút xóa tìm thấy
    deleteButtons.forEach(button => {
        button.addEventListener('click', showModal);
    });

    // --- Logic đóng hộp thoại ---
    const closeModal = () => {
        modal.classList.remove('show');
    };

    // Nút "Hủy" hoặc click ra ngoài sẽ đóng hộp thoại
    cancelBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', function (event) {
        if (event.target === modal) {
            closeModal();
        }
    });
});
