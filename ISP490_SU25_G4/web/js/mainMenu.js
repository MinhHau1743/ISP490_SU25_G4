/*
  File: mainMenu.js
  Description: File JavaScript hoàn chỉnh để điều khiển tất cả các menu.
*/

document.addEventListener('DOMContentLoaded', function() {

    // --- LOGIC 1: ĐIỀU KHIỂN ĐÓNG/MỞ SIDEBAR BÊN TRÁI ---
    const toggleBtn = document.querySelector('.sidebar .toggle-btn');
    const appContainer = document.querySelector('.app-container');

    if (toggleBtn && appContainer) {
        toggleBtn.addEventListener('click', function() {
            appContainer.classList.toggle('sidebar-collapsed');

            // Khi thu gọn, tự động đóng các menu con đang mở
            if (appContainer.classList.contains('sidebar-collapsed')) {
                const openDropdowns = document.querySelectorAll('.sidebar-nav .nav-item-dropdown.open');
                openDropdowns.forEach(dropdown => {
                    dropdown.classList.remove('open');
                });
            }
        });
    }

    // --- LOGIC 2: ĐIỀU KHIỂN CÁC MENU CON TRONG SIDEBAR ---
    const sidebarDropdownLinks = document.querySelectorAll('.sidebar-nav .nav-item-dropdown > a');
    sidebarDropdownLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            // Chỉ cho phép click khi menu không bị thu gọn
            if (appContainer && !appContainer.classList.contains('sidebar-collapsed')) {
                event.preventDefault(); // Ngăn chuyển trang
                this.parentElement.classList.toggle('open');
            }
        });
    });

    // --- LOGIC 3: ĐIỀU KHIỂN CÁC DROPDOWN TRÊN HEADER (USER & NOTIFICATION) ---
    const userProfileButton = document.querySelector('.user-profile-button');
    const userDropdownContent = document.querySelector('.user-profile-dropdown .dropdown-content');
    
    const notificationButton = document.querySelector('.notification-btn');
    const notificationDropdown = document.querySelector('.notification-dropdown');

    // Hàm đóng tất cả các dropdown trên header
    function closeAllHeaderDropdowns() {
        if (userDropdownContent) userDropdownContent.classList.remove('show');
        if (notificationDropdown) notificationDropdown.classList.remove('show');
    }

    if (userProfileButton && userDropdownContent) {
        userProfileButton.addEventListener('click', function(event) {
            event.stopPropagation(); // Ngăn sự kiện click lan ra ngoài
            const isShown = userDropdownContent.classList.contains('show');
            closeAllHeaderDropdowns(); // Luôn đóng tất cả các dropdown khác trước
            if (!isShown) {
                userDropdownContent.classList.add('show'); // Chỉ mở nếu nó đang đóng
            }
        });
    }

    if (notificationButton && notificationDropdown) {
        notificationButton.addEventListener('click', function(event) {
            event.stopPropagation(); // Ngăn sự kiện click lan ra ngoài
            const isShown = notificationDropdown.classList.contains('show');
            closeAllHeaderDropdowns(); // Luôn đóng tất cả các dropdown khác trước
            if (!isShown) {
                notificationDropdown.classList.add('show'); // Chỉ mở nếu nó đang đóng
            }
        });
    }

    // Đóng tất cả dropdown trên header nếu người dùng click ra ngoài
    window.addEventListener('click', function(event) {
        closeAllHeaderDropdowns();
    });
});
