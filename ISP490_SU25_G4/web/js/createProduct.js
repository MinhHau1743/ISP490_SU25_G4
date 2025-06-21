document.getElementById('price').addEventListener('input', function (e) {
    // Lấy số, bỏ hết ký tự không phải số
    let val = e.target.value.replace(/\D/g, '');
    // Format lại với dấu chấm mỗi 3 số
    e.target.value = val.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
});