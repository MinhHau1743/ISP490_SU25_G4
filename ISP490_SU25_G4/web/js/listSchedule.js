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
  if (sp) sp.classList.add('d-none');
  if (lbl) lbl.classList.remove('d-none');

  confirmBtn.setAttribute('data-schedule-id', scheduleId);
  $('#markCompleteConfirmModal').modal('show');
}

/** Gắn listener 1 lần cho nút Xác nhận trong modal */
(function bindConfirmOnce(){
  const confirmBtn = document.getElementById('confirmCompleteBtn');
  if (!confirmBtn || confirmBtn.__bound) return;

  confirmBtn.addEventListener('click', async function() {
    const scheduleId = this.getAttribute('data-schedule-id');
    if (!scheduleId) return;

    // spinner trên nút xác nhận
    this.disabled = true;
    const sp = this.querySelector('.spinner-border');
    const lbl = this.querySelector('.btn-label');
    if (sp) sp.classList.remove('d-none');
    if (lbl) lbl.classList.add('d-none');

    try {
      await markScheduleAsComplete(scheduleId);
      $('#markCompleteConfirmModal').modal('hide');
    } catch (err) {
      alert(err?.message || 'Có lỗi xảy ra khi cập nhật.');
    } finally {
      this.disabled = false;
      if (sp) sp.classList.add('d-none');
      if (lbl) lbl.classList.remove('d-none');
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
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      body: JSON.stringify({ id: Number(scheduleId) }),
      signal: controller.signal
    });

    let data = {};
    try { data = await res.json(); } catch {}

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

