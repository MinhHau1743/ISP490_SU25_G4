/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // Xử lý chuyển đổi ảnh thumbnail
    const mainImage = document.getElementById('mainProductImage');
    const thumbnails = document.querySelectorAll('.thumbnail-item');
    thumbnails.forEach(thumb => {
        thumb.addEventListener('click', function () {
            if (!mainImage || !document.querySelector('.thumbnail-item.active'))
                return;
            const largeSrc = this.dataset.largeSrc;
            if (!largeSrc)
                return;
            document.querySelector('.thumbnail-item.active').classList.remove('active');
            this.classList.add('active');
            mainImage.src = largeSrc;
        });
    });

    // Xử lý chuyển tab
    const tabLinks = document.querySelectorAll('.tab-link');
    tabLinks.forEach(link => {
        link.addEventListener('click', function () {
            const tabId = this.dataset.tab;
            const activeTabContent = document.getElementById(tabId);
            if (!activeTabContent || !document.querySelector('.tab-link.active') || !document.querySelector('.tab-content.active'))
                return;
            document.querySelector('.tab-link.active').classList.remove('active');
            document.querySelector('.tab-content.active').classList.remove('active');
            this.classList.add('active');
            activeTabContent.classList.add('active');
        });
    });
});

