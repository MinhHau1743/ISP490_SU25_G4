/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();
    const filterBtn = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');
    if (filterBtn && filterContainer) {
        filterBtn.addEventListener('click', function () {
            const isHidden = filterContainer.style.display === 'none';
            filterContainer.style.display = isHidden ? 'flex' : 'none';
            // Cập nhật để phù hợp với display:flex
            if (!isHidden) {
                this.style.backgroundColor = '';
                this.style.borderColor = '';
            } else {
                this.style.backgroundColor = '#f9fafb';
                this.style.borderColor = '#0d9488';
            }
        });

        // Tự động mở bộ lọc nếu có tham số trên URL
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('status') || urlParams.get('type') || urlParams.get('signDateFrom')) {
            filterContainer.style.display = 'flex';
            filterBtn.style.backgroundColor = '#f9fafb';
            filterBtn.style.borderColor = '#0d9488';
        }
    }
});

function confirmDelete(id, code) {
    if (confirm(`Bạn có chắc chắn muốn xóa hợp đồng '${code}' không?`)) {
        window.location.href = `contract?action=delete&id=${id}`;
    }
}

