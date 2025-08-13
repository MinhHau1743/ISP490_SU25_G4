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

async function markScheduleAsComplete(ev) {
  ev?.preventDefault?.();

  const detailsPanel = document.getElementById('event-details-panel');
  if (!detailsPanel) return;

  // Lấy ID từ panel
  const rawId = detailsPanel.querySelector('.event-id')?.textContent?.trim();
  const scheduleId = Number.parseInt(rawId, 10);
  if (!Number.isFinite(scheduleId) || scheduleId <= 0) {
    alert('Không tìm thấy ID hợp lệ của lịch trình.');
    return;
  }

  // Nếu đã hoàn thành, chỉ đảm bảo UI đúng trạng thái rồi thoát
  const sch = Array.isArray(window.schedules)
    ? window.schedules.find(s => Number(s.id) === scheduleId)
    : null;
  const isAlreadyCompleted = sch
    ? (sch.statusId === 3 || (sch.statusName || '').toLowerCase().includes('hoàn thành'))
    : false;
  if (isAlreadyCompleted) {
    if (typeof setScheduleCompletedUI === 'function') {
      setScheduleCompletedUI(scheduleId);
    } else {
      document
        .querySelectorAll(
          `#event-${scheduleId}, .event[data-schedule-id="${scheduleId}"], .task-item[data-schedule-id="${scheduleId}"], .event-item[data-schedule-id="${scheduleId}"]`
        )
        .forEach(el => {
          el.classList.add('is-completed');
          el.style.opacity = '0.6';
          if (el.classList.contains('event')) el.setAttribute('draggable', 'false');
        });
      const statusSpan = detailsPanel.querySelector('.event-status');
      if (statusSpan) {
        statusSpan.textContent = 'Hoàn thành';
        statusSpan.className = 'event-status badge px-2 py-1 badge-completed';
      }
    }
    return;
  }

  // Khóa nút click
  const trigger = ev?.currentTarget || ev?.target;
  const prevHTML = trigger?.innerHTML;
  if (trigger && 'disabled' in trigger) {
    trigger.disabled = true;
    trigger.setAttribute('aria-busy', 'true');
    trigger.innerHTML = '<span class="spinner" aria-hidden="true"></span> Đang cập nhật...';
  }

  // Endpoint (fallback khi thiếu contextPath)
  const base =
    (typeof contextPath === 'string' && contextPath) ||
    ('/' + (location.pathname.split('/')[1] || ''));
  const url = `${base}/schedule?action=markAsComplete`;

  // Lưu badge cũ để revert khi lỗi
  const statusSpan = detailsPanel.querySelector('.event-status');
  const oldStatusText = statusSpan?.textContent;
  const oldStatusClass = statusSpan?.className;

  // Timeout bằng AbortController (10s)
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), 10_000);

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json;charset=UTF-8' },
      credentials: 'same-origin',
      body: JSON.stringify({ id: scheduleId }),
      signal: controller.signal
    });

    let data = {};
    try { data = await res.json(); } catch {}

    if (!res.ok) {
      const msg = data?.message || `Cập nhật thất bại (HTTP ${res.status})`;
      throw new Error(msg);
    }

    // --- CẬP NHẬT UI ---
    if (typeof setScheduleCompletedUI === 'function') {
      setScheduleCompletedUI(scheduleId);
    } else {
      // Badge panel chi tiết
      if (statusSpan) {
        statusSpan.textContent = 'Hoàn thành';
        statusSpan.className = 'event-status badge px-2 py-1 badge-completed';
      }
      // Model JS
      if (sch) {
        sch.statusName = 'Hoàn thành';
        sch.statusId = 3;
        sch.updatedAt = new Date().toISOString().slice(0, 19).replace('T', ' ');
      }
      // Mờ event + chặn kéo ở mọi view
      document
        .querySelectorAll(
          `#event-${scheduleId}, .event[data-schedule-id="${scheduleId}"], .task-item[data-schedule-id="${scheduleId}"], .event-item[data-schedule-id="${scheduleId}"]`
        )
        .forEach(el => {
          el.classList.add('is-completed');
          el.style.opacity = '0.6';
          if (el.classList.contains('event')) el.setAttribute('draggable', 'false');
        });
    }

    if (typeof showToast === 'function') {
      showToast(data?.message || 'Đã đánh dấu Hoàn thành', 'success');
    } else {
      console.log(data?.message || 'Đã đánh dấu Hoàn thành');
    }
  } catch (err) {
    // Revert badge nếu đã đổi
    if (statusSpan) {
      statusSpan.textContent = oldStatusText ?? '—';
      if (oldStatusClass) statusSpan.className = oldStatusClass;
    }
    alert(err?.message || 'Có lỗi xảy ra khi cập nhật.');
  } finally {
    clearTimeout(timeoutId);
    if (trigger && 'disabled' in trigger) {
      trigger.disabled = false;
      trigger.removeAttribute('aria-busy');
      if (prevHTML != null) trigger.innerHTML = prevHTML;
    }
  }
}

