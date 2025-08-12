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

        // FIX 1: Thêm kiểm tra để đảm bảo `pinIcon` tồn tại trước khi sử dụng.
        // Đây là cách để code an toàn hơn và không gây ra lỗi "Cannot read properties of null".
        if (pinIcon) { 
            const updateMenu = (isCollapsed) => {
                const iconName = isCollapsed ? 'lock' : 'unlock';
                const title = isCollapsed ? 'Mở rộng menu' : 'Thu gọn menu';

                pinIcon.setAttribute('data-feather', iconName);
                pinBtn.setAttribute('title', title);

                // Vẽ lại toàn bộ icon trên trang để cập nhật icon của nút pin
                feather.replace();
            };

            pinBtn.addEventListener('click', () => {
                const willCollapse = !appContainer.classList.contains('sidebar-collapsed');
                appContainer.classList.toggle('sidebar-collapsed');
                updateMenu(willCollapse);
            });

            // Thiết lập trạng thái ban đầu
            updateMenu(appContainer.classList.contains('sidebar-collapsed'));
        } else {
            // In ra thông báo lỗi nếu không tìm thấy icon bên trong nút pin
            console.error("Không tìm thấy thẻ <i> cho icon bên trong phần tử '.pin-btn'.");
        }
    }

    // --- PHẦN 3: LOGIC CHO CÁC DROPDOWN TRÊN HEADER (Giữ nguyên, code này đã tốt) ---
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