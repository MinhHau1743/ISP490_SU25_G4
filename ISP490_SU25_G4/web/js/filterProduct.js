/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    const filterButton = document.getElementById('filterBtn');
    const filterContainer = document.getElementById('filterContainer');

    if (filterButton && filterContainer) {
        filterButton.addEventListener('click', function () {
            // Lấy trạng thái hiển thị hiện tại
            const isHidden = filterContainer.style.display === 'none';

            // Thay đổi trạng thái
            if (isHidden) {
                filterContainer.style.display = 'flex';
                filterButton.classList.add('active'); // Thêm class active để có thể đổi màu nếu muốn
            } else {
                filterContainer.style.display = 'none';
                filterButton.classList.remove('active');
            }
        });
    }
});

