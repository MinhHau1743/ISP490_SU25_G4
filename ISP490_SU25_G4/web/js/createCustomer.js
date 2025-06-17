/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // Tự động điền ngày hôm nay
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('joinDate').value = today;

    // Logic xem trước ảnh đại diện
    const avatarUpload = document.getElementById('avatarUpload');
    const avatarPreview = document.getElementById('avatarPreview');
    const btnChooseAvatar = document.getElementById('btnChooseAvatar');

    btnChooseAvatar.addEventListener('click', () => avatarUpload.click());

    avatarUpload.addEventListener('change', function () {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                avatarPreview.src = e.target.result;
            }
            reader.readAsDataURL(this.files[0]);
        }
    });
});

