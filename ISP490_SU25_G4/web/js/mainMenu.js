/*
  File: mainMenu.js
  Description: Hướng tiếp cận mới để đảm bảo icon luôn hoạt động.
*/
document.addEventListener('DOMContentLoaded', function () {

    // --- PHẦN 1: KHỞI TẠO VÀ VẼ ICON BAN ĐẦU ---
    feather.replace();

    // --- PHẦN 2: LOGIC CHO SIDEBAR VÀ NÚT PIN ---
    const appContainer = document.querySelector('.app-container');
    const pinBtn = document.querySelector('.pin-btn');

    if (appContainer && pinBtn) {
        const pinIcon = pinBtn.querySelector('i');

        // Hàm duy nhất để cập nhật trạng thái menu
        const updateMenu = (isCollapsed) => {
            const iconName = isCollapsed ? 'lock' : 'unlock';
            const title = isCollapsed ? 'Mở rộng menu' : 'Thu gọn menu';
            
            // Cập nhật thuộc tính trước
            pinIcon.setAttribute('data-feather', iconName);
            pinBtn.setAttribute('title', title);
            
            // Vẽ lại toàn bộ icon trên trang
            // Đây là cách đảm bảo icon của nút pin luôn được cập nhật
            feather.replace(); 
        };

        // Gán sự kiện click cho nút pin
        pinBtn.addEventListener('click', () => {
            const willCollapse = !appContainer.classList.contains('sidebar-collapsed');
            appContainer.classList.toggle('sidebar-collapsed');
            updateMenu(willCollapse);
        });

        // Thiết lập trạng thái ban đầu
        updateMenu(appContainer.classList.contains('sidebar-collapsed'));
    }

    // --- PHẦN 3: LOGIC CHO CÁC DROPDOWN TRÊN HEADER ---
    const userProfileButton = document.querySelector('.user-profile-button');
    const userDropdownContent = document.querySelector('.user-profile-dropdown .dropdown-content');
    const notificationButton = document.querySelector('.notification-btn');
    const notificationDropdown = document.querySelector('.notification-dropdown');
    
    const closeAllHeaderDropdowns = () => {
        if (userDropdownContent) userDropdownContent.classList.remove('show');
        if (notificationDropdown) notificationDropdown.classList.remove('show');
    };

    if (userProfileButton) {
        userProfileButton.addEventListener('click', (e) => {
            e.stopPropagation();
            const isShown = userDropdownContent.classList.contains('show');
            closeAllHeaderDropdowns();
            if (!isShown) userDropdownContent.classList.add('show');
        });
    }

    if (notificationButton) {
        notificationButton.addEventListener('click', (e) => {
            e.stopPropagation();
            const isShown = notificationDropdown.classList.contains('show');
            closeAllHeaderDropdowns();
            if (!isShown) notificationDropdown.classList.add('show');
        });
    }
    
    window.addEventListener('click', closeAllHeaderDropdowns);
});