/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.number-format').forEach(function (input) {
        // Format value khi load trang
        if (input.value && !isNaN(input.value.replace(/[^0-9]/g, ''))) {
            let raw = input.value.replace(/[^0-9]/g, '');
            input.value = Number(raw).toLocaleString('vi-VN');
        }

        // Format realtime khi nhập
        input.addEventListener('input', function (e) {
            let raw = input.value.replace(/[^0-9]/g, '');
            if (raw === '') {
                input.value = '';
                return;
            }
            input.value = Number(raw).toLocaleString('vi-VN');
            input.setSelectionRange(input.value.length, input.value.length);
        });

        // Khi submit, bỏ dấu phẩy
        input.form && input.form.addEventListener('submit', function () {
            input.value = input.value.replace(/[^0-9]/g, '');
        });
    });
});



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

        const openDeleteModal = (id, name, image) => {
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa sản phẩm '<strong>${name}</strong>'?`;
            // Cập nhật link xóa trong servlet của bạn
            confirmDeleteBtn.href = `ProductController?service=deleteProduct&id=${id}&image=${image}`;
            deleteModal.classList.add('show');
            feather.replace(); // Phải gọi lại để render icon X và alert-triangle trong modal
        };

        const closeDeleteModal = () => deleteModal.classList.remove('show');

        deleteTriggerButtons.forEach(button => {
            button.addEventListener('click', function (event) {
                event.preventDefault();
                openDeleteModal(this.getAttribute('data-id'), this.getAttribute('data-name'), this.getAttribute('data-image'));
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
// Lấy modal, ảnh lớn và caption
var modal = document.getElementById("myModal");
var modalImg = document.getElementById("img01");
var captionText = document.getElementById("caption");

// Sửa thành: Chọn tất cả ảnh bằng CLASS
var thumbnails = document.querySelectorAll(".modal-img");

// Gắn sự kiện cho từng ảnh
thumbnails.forEach(function (img) {
    img.addEventListener("click", function () {
        modal.style.display = "block";
        modalImg.src = this.src; // Hiển thị ảnh click
        captionText.innerHTML = this.alt;
    });
});

// Đóng modal
var span = document.getElementsByClassName("close")[0];
span.onclick = function () {
    modal.style.display = "none";
};

// (Optional) Đóng khi click ngoài ảnh
modal.addEventListener("click", function (e) {
    if (e.target === modal) {
        modal.style.display = "none";
    }
});

