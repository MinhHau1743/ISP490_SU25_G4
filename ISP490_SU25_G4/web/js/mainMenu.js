/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener('DOMContentLoaded', function() {

    const toggleBtn = document.querySelector('.sidebar .toggle-btn');
    const appContainer = document.querySelector('.app-container');

    if (toggleBtn && appContainer) {
        toggleBtn.addEventListener('click', function() {
            appContainer.classList.toggle('sidebar-collapsed');

            if (appContainer.classList.contains('sidebar-collapsed')) {
                const openDropdowns = document.querySelectorAll('.nav-item-dropdown.open');
                openDropdowns.forEach(dropdown => {
                    dropdown.classList.remove('open');
                });
            }
        });
    }

    const dropdownLinks = document.querySelectorAll('.sidebar-nav .nav-item-dropdown > a');

    dropdownLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const parentLi = this.parentElement;
            parentLi.classList.toggle('open');
        });
    });

});


