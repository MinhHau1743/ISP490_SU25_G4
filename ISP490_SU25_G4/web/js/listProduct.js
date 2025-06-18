/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
$(document).ready(function () {
    var typingTimer;
    var doneTypingInterval = 500; // 0.5 giây

    $("#searchProducts").on("keyup", function () {
        clearTimeout(typingTimer);
        typingTimer = setTimeout(function () {
            var value = $("#searchProducts").val().toLowerCase();
            $("#productList .product-card").filter(function () {
                $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
            });

            // Kiểm tra số card còn hiển thị
            if ($("#productList .product-card:visible").length === 0) {
                $("#noResultMsg").show();
            } else {
                $("#noResultMsg").hide();
            }
        }, doneTypingInterval);
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
// Lấy phần tử modal (hộp hiển thị ảnh lớn khi click)
var modal = document.getElementById("myModal");

// Lấy ảnh thu nhỏ mà người dùng sẽ click vào
var img = document.getElementById("myImg");

// Lấy phần tử ảnh bên trong modal (sẽ hiển thị ảnh phóng to)
var modalImg = document.getElementById("img01");

// Lấy phần tử chú thích (caption) để hiển thị mô tả ảnh
var captionText = document.getElementById("caption");

// Khi người dùng click vào ảnh thu nhỏ
img.onclick = function () {
    // Hiển thị modal (popup)
    modal.style.display = "block";

    // Gán ảnh lớn trong modal bằng ảnh thu nhỏ đã click
    modalImg.src = this.src;

    // Gán phần chú thích bằng nội dung 'alt' của ảnh thu nhỏ
    captionText.innerHTML = this.alt;
}

// Lấy phần tử dấu "×" dùng để đóng modal
var span = document.getElementsByClassName("close")[0];

// Khi người dùng click vào nút đóng (dấu "×")
span.onclick = function () {
    // Ẩn modal đi
    modal.style.display = "none";
}
