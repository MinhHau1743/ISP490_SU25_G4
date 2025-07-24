document.addEventListener('DOMContentLoaded', function () {
    // 1. Format giá khi load trang
    document.querySelectorAll('.number-format').forEach(function (input) {
        // Format khi load
        if (input.value && !isNaN(input.value.replace(/[^0-9]/g, ''))) {
            let raw = input.value.replace(/[^0-9]/g, '');
            input.value = Number(raw).toLocaleString('vi-VN');
        }

        // Format khi nhập
        input.addEventListener('input', function () {
            let raw = input.value.replace(/[^0-9]/g, '');
            if (raw === '') {
                input.value = '';
                return;
            }
            input.value = Number(raw).toLocaleString('vi-VN');
            input.setSelectionRange(input.value.length, input.value.length);
        });

        // Bỏ dấu phẩy khi submit
        input.form && input.form.addEventListener('submit', function () {
            input.value = input.value.replace(/[^0-9]/g, '');
        });
    });

    // 2. Toggle dạng xem (lưới/bảng)
    const gridViewBtn = document.getElementById("gridViewBtn");
    const tableViewBtn = document.getElementById("tableViewBtn");
    const productList = document.getElementById("productList");
    const productTable = document.getElementById("productTable");

    if (gridViewBtn && tableViewBtn && productList && productTable) {
        function setActiveButton(mode) {
            if (mode === "grid") {
                gridViewBtn.classList.add("active");
                tableViewBtn.classList.remove("active");
                productList.style.display = "flex";
                productTable.style.display = "none";
            } else {
                tableViewBtn.classList.add("active");
                gridViewBtn.classList.remove("active");
                productList.style.display = "none";
                productTable.style.display = "block";
            }
            localStorage.setItem("productViewMode", mode);
            feather.replace();
        }

        gridViewBtn.addEventListener("click", () => setActiveButton("grid"));
        tableViewBtn.addEventListener("click", () => setActiveButton("table"));
//  Đặt mặc định là "grid"
        const savedView = localStorage.getItem("productViewMode") || "grid";
        setActiveButton(savedView);
    }

    // 3. feather icons
    if (typeof feather !== "undefined") {
        feather.replace();
    }

    // 4. Bộ lọc (filter)
    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('minPrice') || urlParams.has('maxPrice') || urlParams.has('originId') || urlParams.has('categoryId')) {
        if (filterContainer)
            filterContainer.style.display = 'flex';
        if (filterBtn)
            filterBtn.classList.add('active');
    } else {
        if (filterContainer)
            filterContainer.style.display = 'none';
    }

    if (filterBtn && filterContainer) {
        filterBtn.addEventListener('click', function () {
            const isHidden = filterContainer.style.display === 'none';
            filterContainer.style.display = isHidden ? 'flex' : 'none';
            this.classList.toggle('active', isHidden);
        });
    }

    // 5. Modal xác nhận xoá
    const deleteModal = document.getElementById('deleteConfirmModal');
    if (deleteModal) {
        const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        const deleteMessage = document.getElementById('deleteMessage');
        const closeBtn = deleteModal.querySelector('.close-modal-btn');
        const deleteTriggerButtons = document.querySelectorAll('.delete-trigger-btn');

        const openDeleteModal = (id, name, image) => {
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa sản phẩm '<strong>${name}</strong>'?`;
            confirmDeleteBtn.href = `deleteProduct?id=${id}`;
            deleteModal.classList.add('show');
            feather.replace();
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

    // 6. Modal xem ảnh lớn
    const modal = document.getElementById("myModal");
    const modalImg = document.getElementById("img01");
    const captionText = document.getElementById("caption");
    const thumbnails = document.querySelectorAll(".modal-img");

    if (modal && modalImg && captionText && thumbnails.length > 0) {
        thumbnails.forEach(function (img) {
            img.addEventListener("click", function () {
                modal.style.display = "block";
                modalImg.src = this.src;
                captionText.innerHTML = this.alt;
            });
        });

        const span = document.getElementsByClassName("close")[0];
        if (span) {
            span.onclick = function () {
                modal.style.display = "none";
            };
        }

        modal.addEventListener("click", function (e) {
            if (e.target === modal) {
                modal.style.display = "none";
            }
        });
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const alertBox = document.getElementById("customAlert");
    const progressBar = document.getElementById("alertProgressBar");
    if (alertBox && progressBar) {
        setTimeout(() => {
            progressBar.style.width = '0%';
        }, 100);
        setTimeout(() => {
            $(alertBox).alert('close');
        }, 5100);
    }
});
// Hide loading overlay when page fully loaded
window.addEventListener("load", function () {
    const overlay = document.getElementById("loadingOverlay");
    if (overlay) {
        overlay.style.opacity = "0";
        overlay.style.transition = "opacity 0.5s ease";
        setTimeout(() => overlay.style.display = "none", 500);
    }
});
// Sau 100ms để đảm bảo DOM render xong mới bắt đầu chạy progress
setTimeout(function () {
    document.getElementById("alertProgressBar").style.width = "0%";
}, 100);

// Sau 5s thì tự động ẩn alert
setTimeout(function () {
    const alertBox = document.getElementById("customAlert");
    if (alertBox) {
        $(alertBox).alert('close'); // Bootstrap dismiss
    }
}, 5100);