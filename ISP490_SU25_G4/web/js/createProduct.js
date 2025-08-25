document.getElementById('price').addEventListener('input', function (e) {
    // Lấy số, bỏ hết ký tự không phải số
    let val = e.target.value.replace(/\D/g, '');
    // Format lại với dấu chấm mỗi 3 số
    e.target.value = val.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
});
document.getElementById('productImageUpload').addEventListener('change', function (event) {
    const file = event.target.files[0];
    const preview = document.getElementById('productImagePreview');
    const icon = document.getElementById('imageIcon');
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = "block";
            icon.style.display = "none";
        }
        reader.readAsDataURL(file);
    } else {
        // Nếu bỏ chọn file, quay lại icon mặc định
        preview.src = "";
        preview.style.display = "none";
        icon.style.display = "block";
    }
});


// ====== Chặn double-submit + overlay ======
document.addEventListener('DOMContentLoaded', function () {
    var MIN_LOADING_MS = 3000;

    var form = document.querySelector('.product-form');
    // Hỗ trợ cả tạo mới (btnSave) và cập nhật (btnSaveEdit)
    var btn = document.getElementById('btnSave') || document.getElementById('btnSave');
    var overlay = document.getElementById('savingOverlay');
    var submitted = false;

    if (!form)
        return;

    var customDelay = parseInt(form.getAttribute('data-delay-ms') || '', 10);
    if (!isNaN(customDelay) && customDelay >= 0) {
        MIN_LOADING_MS = customDelay;
    }

    form.addEventListener('submit', function (e) {
        if (submitted) {
            e.preventDefault();
            return false;
        }
        submitted = true;

        // Khóa nút + show spinner + overlay
        if (btn) {
            btn.disabled = true;
            btn.classList.add('disabled');
            btn.innerHTML =
                '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>' +
                '<span style="margin-left:8px">Đang lưu…</span>';
        }
        if (overlay)
            overlay.classList.add('show');

        // Trì hoãn submit một chút cho user cảm thấy mượt
        e.preventDefault();
        setTimeout(function () {
            form.submit();
        }, MIN_LOADING_MS);
    });

    // Khôi phục UI khi quay lại từ bfcache (back/forward cache)
    window.addEventListener('pageshow', function () {
        submitted = false;
        if (overlay)
            overlay.classList.remove('show');
        if (btn) {
            btn.disabled = false;
            btn.classList.remove('disabled');
            btn.innerHTML = '<i data-feather="save"></i><span>Lưu thay đổi</span>';
            if (window.feather) feather.replace();
        }
    });
});

// ====== XEM TRƯỚC ẢNH NGAY KHI CHỌN (INSTANT PREVIEW) ======
(function () {
    // ===== CẤU HÌNH THỜI GIAN TRỄ PREVIEW (ms) =====
    var PREVIEW_DELAY_MS = 1000; // ví dụ: 1500ms = 1.5s. Đổi số này để lâu/nhanh hơn.
    var fileInput = document.getElementById('productImageUpload');
    var imgPreview = document.getElementById('productImagePreview');
    var icon = document.getElementById('imageIcon');
    var container = document.getElementById('imagePreviewContainer');
    var fallbackSrc = '${pageContext.request.contextPath}/image/na.jpg';
    var lastObjectUrl = null;
    var spinnerEl = null;

    // Đảm bảo container có position để đặt spinner vào giữa
    if (container && getComputedStyle(container).position === 'static') {
        container.style.position = 'relative';
    }

    function addSpinner() {
        if (!container)
            return;
        if (spinnerEl)
            return; // đã có spinner
        spinnerEl = document.createElement('div');
        spinnerEl.className = 'img-loading';
        spinnerEl.style.position = 'absolute';
        spinnerEl.style.top = '50%';
        spinnerEl.style.left = '50%';
        spinnerEl.style.transform = 'translate(-50%, -50%)';
        spinnerEl.style.zIndex = '10';
        spinnerEl.innerHTML = '<div class="spinner-border" role="status" aria-hidden="true" style="width:1.75rem;height:1.75rem;"></div>';
        container.appendChild(spinnerEl);
    }

    function removeSpinner() {
        if (spinnerEl && spinnerEl.parentNode) {
            spinnerEl.parentNode.removeChild(spinnerEl);
        }
        spinnerEl = null;
    }

    function showIcon() {
        if (imgPreview)
            imgPreview.style.display = 'none';
        if (icon)
            icon.style.display = 'block';
    }
    function showImage() {
        if (icon)
            icon.style.display = 'none';
        if (imgPreview)
            imgPreview.style.display = 'block';
    }

    // Trạng thái ban đầu
    (function initState() {
        try {
            if (imgPreview && imgPreview.getAttribute('src') && imgPreview.getAttribute('src') !== fallbackSrc) {
                showImage();
            } else {
                showIcon();
            }
        } catch (e) {
            showIcon();
        }
    })();

    if (!fileInput)
        return;

    fileInput.addEventListener('change', function () {
        var file = fileInput.files && fileInput.files[0];
        if (!file) {
            if (!imgPreview.getAttribute('src') || imgPreview.getAttribute('src') === fallbackSrc)
                showIcon();
            else
                showImage();
            removeSpinner();
            return;
        }

        if (!file.type || !file.type.startsWith('image/')) {
            alert('Vui lòng chọn tệp hình ảnh hợp lệ.');
            fileInput.value = '';
            removeSpinner();
            return;
        }

        if (lastObjectUrl) {
            URL.revokeObjectURL(lastObjectUrl);
            lastObjectUrl = null;
        }

        // 👉 Ẩn ảnh cũ + icon ngay khi bắt đầu chọn file
        imgPreview.style.display = 'none';
        icon.style.display = 'none';
        addSpinner();

        var objectUrl = URL.createObjectURL(file);
        lastObjectUrl = objectUrl;

        setTimeout(function () {
            imgPreview.onload = function () {
                removeSpinner();
                showImage();
                setTimeout(function () {
                    if (lastObjectUrl) {
                        URL.revokeObjectURL(lastObjectUrl);
                        lastObjectUrl = null;
                    }
                }, 100);
            };
            imgPreview.onerror = function () {
                removeSpinner();
                imgPreview.src = fallbackSrc;
                showIcon();
            };
            imgPreview.src = objectUrl;
        }, PREVIEW_DELAY_MS);
    });
})();