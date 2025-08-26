/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();

    // --- 1. LOGIC CHUYỂN ĐỔI VIEW (TUẦN/THÁNG) ---


    // --- 2. LOGIC POPOVER CHO LỊCH THÁNG ---

    document.addEventListener('click', function (event) {
        if (popover && popover.style.display === 'block' && !popover.contains(event.target) && !event.target.closest('.task-item')) {
            popover.style.display = 'none';
        }
    });

    // --- 3. LOGIC MODAL XÁC NHẬN XÓA (DÙNG CHUNG) ---
    const modal = document.getElementById('delete-confirm-modal');
    if (modal) {
        const closeBtns = modal.querySelectorAll('.modal-close-btn, .btn-cancel');
        const itemNameSpan = document.getElementById('item-name-to-delete');
        const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

        const openDeleteModal = (id, name) => {
            itemNameSpan.textContent = `"${name}"`;
            // Thay thế URL này bằng action xóa thực tế của bạn
            confirmDeleteBtn.href = `/your-app/schedule/delete?id=${id}`;
            modal.style.display = 'flex';
            feather.replace(); // Re-render icons in modal
        };

        const closeDeleteModal = () => {
            modal.style.display = 'none';
        };

        // Lắng nghe sự kiện click trên toàn bộ body
        document.body.addEventListener('click', function (event) {
            // Tìm phần tử trigger xóa gần nhất
            const deleteTrigger = event.target.closest('.delete-trigger');
            if (deleteTrigger) {
                event.preventDefault();
                if (popover && popover.style.display === 'block') {
                    popover.style.display = 'none'; // Đóng popover nếu nó đang mở
                }
                const itemId = deleteTrigger.getAttribute('data-item-id');
                const itemName = deleteTrigger.getAttribute('data-item-name');
                openDeleteModal(itemId, itemName);
            }
        });

        closeBtns.forEach(btn => btn.addEventListener('click', (e) => {
                e.preventDefault();
                closeDeleteModal();
            }));
    }
});

// File: listSchedule.js or inside a <script> tag

/**
 * Mở modal xác nhận.
 * Lấy ID từ panel và lưu tạm vào nút "Xác nhận" của modal.
 */
function openMarkAsCompleteModal(ev) {
    ev.preventDefault();

    const detailsPanel = document.getElementById('event-details-panel');
    const scheduleId = detailsPanel.querySelector('.event-id')?.textContent?.trim();
    if (!scheduleId) {
        alert('Không thể xác định ID của lịch trình.');
        return;
    }

    const confirmBtn = document.getElementById('confirmCompleteBtn');
    // reset trạng thái nút xác nhận mỗi lần mở
    confirmBtn.disabled = false;
    const sp = confirmBtn.querySelector('.spinner-border');
    const lbl = confirmBtn.querySelector('.btn-label');
    if (sp)
        sp.classList.add('d-none');
    if (lbl)
        lbl.classList.remove('d-none');

    confirmBtn.setAttribute('data-schedule-id', scheduleId);
    $('#markCompleteConfirmModal').modal('show');
}

/** Gắn listener 1 lần cho nút Xác nhận trong modal */
(function bindConfirmOnce() {
    const confirmBtn = document.getElementById('confirmCompleteBtn');
    if (!confirmBtn || confirmBtn.__bound)
        return;

    confirmBtn.addEventListener('click', async function () {
        const scheduleId = this.getAttribute('data-schedule-id');
        if (!scheduleId)
            return;

        // spinner trên nút xác nhận
        this.disabled = true;
        const sp = this.querySelector('.spinner-border');
        const lbl = this.querySelector('.btn-label');
        if (sp)
            sp.classList.remove('d-none');
        if (lbl)
            lbl.classList.add('d-none');

        try {
            await markScheduleAsComplete(scheduleId);
            $('#markCompleteConfirmModal').modal('hide');
        } catch (err) {
            alert(err?.message || 'Có lỗi xảy ra khi cập nhật.');
        } finally {
            this.disabled = false;
            if (sp)
                sp.classList.add('d-none');
            if (lbl)
                lbl.classList.remove('d-none');
        }
    });

    confirmBtn.__bound = true; // tránh gắn nhiều lần
})();

/** Gọi API cập nhật + cập nhật UI */
async function markScheduleAsComplete(scheduleId) {
    const base = (typeof contextPath === 'string' && contextPath) || ('/' + (location.pathname.split('/')[1] || ''));
    const url = `${base}/schedule?action=markAsComplete`;

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: {'Content-Type': 'application/json;charset=UTF-8'},
            body: JSON.stringify({id: Number(scheduleId)}),
            signal: controller.signal
        });

        let data = {};
        try {
            data = await res.json();
        } catch {
        }

        if (!res.ok) {
            throw new Error(data?.message || `Cập nhật thất bại (HTTP ${res.status})`);
        }

        // Cập nhật giao diện (đã có sẵn trong code của bạn)
        setScheduleCompletedUI(scheduleId);
        // showToast && showToast('Đã đánh dấu Hoàn thành', 'success');

    } finally {
        clearTimeout(timeoutId);
    }
}

class ToastNotification {
    constructor() {
        this.container = null;
        this.toasts = [];
        this.init();
    }

    init() {
        var el = document.getElementById('toast-container');
        if (!el) {
            el = document.createElement('div');
            el.id = 'toast-container';
            // styling tối thiểu, bạn có thể chuyển sang CSS
            el.style.position = 'fixed';
            el.style.top = '16px';
            el.style.right = '16px';
            el.style.zIndex = 1080;
            document.body.appendChild(el);
        }
        this.container = el;
    }

    show(message, type, title, duration) {
        type = (type == null) ? 'info' : type;
        duration = (typeof duration === 'number') ? duration : 3000;  // Mặc định 3000ms (3 giây)
        const toast = this.createToast(message, type, title, duration);
        this.container.appendChild(toast);
        this.toasts.push(toast);
        setTimeout(function () {
            toast.classList.add('show');
        }, 50);
        if (duration > 0) {
            const progressBar = toast.querySelector('.toast-progress');
            if (progressBar) {
                progressBar.style.width = '100%';
                progressBar.style.transition = 'width ' + duration + 'ms linear';
                setTimeout(function () {
                    progressBar.style.width = '0%';
                }, 50);  // <-- Dòng này là thời gian tự tắt/tự đóng!
            }
            setTimeout(() => {
                this.remove(toast);
            }, duration);
        }

        this.limitToasts();
        return toast;
    }

    createToast(message, type, title, duration) {
        const toast = document.createElement('div');
        toast.className = 'toast ' + type;
        const icons = {success: '✓', error: '✕', warning: '⚠', info: 'ℹ'};
        const icon = icons[type] || icons.info;
        const titleHtml = title ? ('<div class="toast-title">' + this.escapeHtml(title) + '</div>') : '';
        const progressHtml = (duration > 0) ? '<div class="toast-progress"></div>' : '';
        toast.innerHTML =
                '<div class="toast-icon">' + icon + '</div>' +
                '<div class="toast-content">' +
                titleHtml +
                '<div class="toast-message">' + this.escapeHtml(message) + '</div>' +
                '</div>' +
                '<button class="toast-close" title="Đóng" ' +
                'onclick="if(window.toastSystem){var p=this.parentElement;if(p){window.toastSystem.remove(p);}}">&times;</button>' +
                progressHtml;
        return toast;
    }

    escapeHtml(str) {
        return String(str).replace(/[&<>"']/g, function (s) {
            return ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'}[s]);
        });
    }

    remove(toast) {
        if (!toast || !toast.parentElement)
            return;
        toast.classList.remove('show');
        toast.classList.add('hide');
        setTimeout(() => {
            if (toast.parentElement)
                toast.parentElement.removeChild(toast);
            const i = this.toasts.indexOf(toast);
            if (i > -1)
                this.toasts.splice(i, 1);
        }, 300);
    }

    limitToasts() {
        while (this.toasts.length > 5) {
            this.remove(this.toasts[0]);
        }
    }

    clear() {
        // sao chép để không bị sửa mảng khi remove
        this.toasts.slice().forEach(t => this.remove(t));
    }
}

// Chỉ khởi tạo nếu chưa có
window.toastSystem = window.toastSystem || new ToastNotification();
// Hàm tiện dụng global
function showToast(message, type, title, duration) {
    return window.toastSystem.show(message, type, title, duration);
}

document.addEventListener('DOMContentLoaded', function () {
    const filterBtn = document.getElementById('filterToggleBtn');
    const filterContainer = document.getElementById('filterContainer');

    // --- PHẦN NÂNG CẤP BẮT ĐẦU ---

    // Hàm kiểm tra xem URL có chứa tham số lọc không
    const checkForActiveFilters = () => {
        const params = new URLSearchParams(window.location.search);
        
        // ⭐ QUAN TRỌNG: Thêm tất cả các tên (name) của các trường lọc vào đây
        const filterKeys = ['type', 'status']; 
        
        for (const key of filterKeys) {
            // Nếu tìm thấy bất kỳ tham số lọc nào có giá trị (không rỗng), trả về true
            if (params.has(key) && params.get(key) !== '') {
                return true;
            }
        }
        return false;
    };

    // Nếu phát hiện có lọc, tự động mở bộ lọc khi tải trang
    if (checkForActiveFilters()) {
        filterContainer.style.display = 'block'; // Hiển thị khu vực lọc
        filterBtn.classList.add('active'); // Kích hoạt trạng thái 'active' cho nút
    }

    // --- PHẦN NÂNG CẤP KẾT THÚC ---

    // Giữ lại logic bật/tắt khi người dùng tự nhấn nút
    if (filterBtn && filterContainer) {
        filterBtn.addEventListener('click', function () {
            const isHidden = filterContainer.style.display === 'none';
            if (isHidden) {
                filterContainer.style.display = 'block';
                filterBtn.classList.add('active');
            } else {
                filterContainer.style.display = 'none';
                filterBtn.classList.remove('active');
            }
        });
    }
});