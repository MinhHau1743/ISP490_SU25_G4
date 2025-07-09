document.addEventListener('DOMContentLoaded', function () {
    // Kích hoạt thư viện icon
    if (typeof feather !== 'undefined') {
        feather.replace();
    }

    // Logic cho modal xác nhận xóa
    const deleteModal = document.getElementById('deleteConfirmModal');
    const deleteTriggerBtn = document.querySelector('.delete-trigger-btn');

    if (deleteModal && deleteTriggerBtn) {
        const cancelDeleteBtn = deleteModal.querySelector('#cancelDeleteBtn');
        const confirmDeleteBtn = deleteModal.querySelector('#confirmDeleteBtn');
        const deleteMessage = deleteModal.querySelector('#deleteMessage');
        const closeBtn = deleteModal.querySelector('.close-modal-btn');

        deleteTriggerBtn.addEventListener('click', function (e) {
            e.preventDefault();

            // Lấy dữ liệu từ các thuộc tính data-* của nút
            const id = this.dataset.id;
            const name = this.dataset.name;
            const deleteUrl = this.dataset.deleteUrl; // Lấy URL xóa từ JSP

            // Cập nhật nội dung và đường dẫn cho modal
            deleteMessage.innerHTML = `Bạn có chắc chắn muốn xóa hợp đồng <strong>${name}</strong>?`;

            // SỬA Ở ĐÂY: Dùng URL đã lấy được thay vì viết cứng
            confirmDeleteBtn.href = `${deleteUrl}?id=${id}`;

            // Hiển thị modal
            deleteModal.style.display = 'flex';
        });

        // Hàm để đóng modal
        const closeModal = () => {
            deleteModal.style.display = 'none';
        };

        // Gán sự kiện click để đóng modal
        cancelDeleteBtn.addEventListener('click', closeModal);
        if (closeBtn) {
            closeBtn.addEventListener('click', closeModal);
        }

        // Đóng modal khi nhấn ra ngoài vùng nội dung
        deleteModal.addEventListener('click', e => {
            if (e.target === deleteModal) {
                closeModal();
            }
        });
    }


    // Hàm tính toán và cập nhật lại toàn bộ tổng tiền
    function updateTotals() {
        const itemList = document.getElementById('contract-item-list');
        const rows = itemList.querySelectorAll('tr');

        let subtotal = 0;

        // Lặp qua từng dòng sản phẩm để tính tổng phụ
        rows.forEach(row => {
            const lineTotalInput = row.querySelector('.line-total'); // Giả sử mỗi dòng có 1 input/span chứa thành tiền
            if (lineTotalInput) {
                // Lấy giá trị thành tiền của từng dòng và cộng dồn
                const lineTotal = parseFloat(lineTotalInput.dataset.value || 0);
                subtotal += lineTotal;
            }
        });

        const vatRate = 0.10; // 10% VAT
        const vatAmount = subtotal * vatRate;
        const grandTotal = subtotal + vatAmount;

        // Cập nhật giá trị hiển thị trên giao diện
        document.getElementById('subTotal').textContent = formatCurrency(subtotal);
        document.getElementById('vatAmount').textContent = formatCurrency(vatAmount);
        document.getElementById('grandTotal').innerHTML = `<strong>${formatCurrency(grandTotal)}</strong>`;

        // QUAN TRỌNG NHẤT: Cập nhật giá trị vào ô input ẩn để gửi đi
        // Giả sử bạn dùng giá trị sau VAT để lưu
        const contractValueInput = document.getElementById('contractValue');
        if (contractValueInput) {
            contractValueInput.value = grandTotal;
        }
    }

    // Hàm phụ để định dạng tiền tệ (ví dụ)
    function formatCurrency(value) {
        return new Intl.NumberFormat('vi-VN').format(value);
    }
});