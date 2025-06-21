/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // Xử lý thêm/xóa thông số kỹ thuật
    const addSpecBtn = document.getElementById('add-spec-btn');
    const specsTbody = document.getElementById('specs-tbody');

    if (addSpecBtn && specsTbody) {
        addSpecBtn.addEventListener('click', function () {
            const newRow = document.createElement('tr');
            newRow.innerHTML = `
                            <td><input type="text" name="spec_key" class="form-control" placeholder="ví dụ: Màn hình"></td>
                            <td><input type="text" name="spec_value" class="form-control" placeholder="ví dụ: 6.7 inch"></td>
                            <td><button type="button" class="btn-delete-spec" title="Xóa thông số"><i data-feather="x-circle"></i></button></td>
                        `;
            specsTbody.appendChild(newRow);
            feather.replace(); // Phải gọi lại để render icon mới
        });

        specsTbody.addEventListener('click', function (e) {
            // Tìm nút xóa được click (kể cả click vào icon bên trong)
            const deleteButton = e.target.closest('.btn-delete-spec');
            if (deleteButton) {
                // Tìm hàng `<tr>` cha và xóa nó
                deleteButton.closest('tr').remove();
            }
        });
    }
});
document.getElementById('imageUpload').addEventListener('change', function (event) {
    const file = event.target.files[0];
    if (file) {
        // Tạo URL tạm thời cho ảnh vừa chọn
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('productImagePreview').src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});
document.getElementById('price').addEventListener('input', function (e) {
    // Lấy số, bỏ hết ký tự không phải số
    let val = e.target.value.replace(/\D/g, '');
    // Format lại với dấu chấm mỗi 3 số
    e.target.value = val.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
});