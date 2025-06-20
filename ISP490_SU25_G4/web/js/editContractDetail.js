/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    const statusSelect = document.getElementById('status');
    if (statusSelect) {
        const updateStatusColor = () => {
            statusSelect.classList.remove('status-active', 'status-expired');
            const selectedValue = statusSelect.value;
            if (selectedValue === 'active') {
                statusSelect.classList.add('status-active');
            } else if (selectedValue === 'expired') {
                statusSelect.classList.add('status-expired');
            }
        };
        updateStatusColor();
        statusSelect.addEventListener('change', updateStatusColor);
    }
});

