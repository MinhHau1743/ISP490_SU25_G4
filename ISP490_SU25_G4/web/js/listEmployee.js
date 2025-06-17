/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();
    const toggleBtn = document.querySelector('.toggle-btn');
    const appContainer = document.querySelector('.app-container');
    if (toggleBtn && appContainer) {
        toggleBtn.addEventListener('click', () => appContainer.classList.toggle('sidebar-collapsed'));
    }

    const dropdownLinks = document.querySelectorAll('.sidebar-nav .nav-item-dropdown > a');
    dropdownLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            if (this.getAttribute('href') === '#') {
                event.preventDefault();
            }
            this.parentElement.classList.toggle('open');
        });
    });
});

