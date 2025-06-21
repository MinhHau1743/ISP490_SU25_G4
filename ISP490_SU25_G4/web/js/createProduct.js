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