// Chờ cho đến khi toàn bộ cấu trúc HTML của trang đã được tải xong
document.addEventListener('DOMContentLoaded', function () {

    // --- Hằng số ---
    const VAT_RATE = 0.10; // Tách riêng tỉ lệ VAT để dễ dàng thay đổi

    // --- Lấy ra các phần tử DOM ---
    const addProductBtn = document.getElementById('addProductBtn');
    const productModal = document.getElementById('productSearchModal');
    const closeProductModalBtn = document.getElementById('closeProductModalBtn');
    const productListContainer = document.getElementById('productList');
    const contractItemList = document.getElementById('contract-item-list');
    const subTotalEl = document.getElementById('subTotal');
    const vatAmountEl = document.getElementById('vatAmount');
    const grandTotalEl = document.getElementById('grandTotal');
    const contractValueInput = document.getElementById('contractValue');
    const productSearchInput = document.getElementById('productSearchInput');
    const signDateInput = document.getElementById('signDate');

    // --- Các hàm xử lý ---

    /** Mở modal chọn sản phẩm */
    const openModal = () => {
        if (productModal) {
            productModal.style.display = 'flex';
            feather.replace(); // Re-initialize icons inside modal
        }
    };

    /** Đóng modal chọn sản phẩm */
    const closeModal = () => {
        if (productModal) {
            productModal.style.display = 'none';
        }
    };

    /** Cập nhật tất cả các giá trị tổng */
    const updateTotals = () => {
        let subTotal = 0;
        const rows = contractItemList.querySelectorAll('tr');

        rows.forEach(row => {
            const quantity = parseFloat(row.querySelector('.item-quantity').value) || 0;
            const price = parseFloat(row.dataset.price) || 0;
            const total = quantity * price;

            // Cập nhật thành tiền cho từng dòng
            const itemTotalEl = row.querySelector('.item-total');
            if (itemTotalEl) {
                itemTotalEl.textContent = total.toLocaleString('vi-VN');
            }
            subTotal += total;
        });

        const vat = subTotal * VAT_RATE;
        const grandTotal = subTotal + vat;

        // Cập nhật các giá trị ở footer bảng
        if (subTotalEl) subTotalEl.textContent = subTotal.toLocaleString('vi-VN');
        if (vatAmountEl) vatAmountEl.textContent = vat.toLocaleString('vi-VN');
        if (grandTotalEl) grandTotalEl.innerHTML = `<strong>${grandTotal.toLocaleString('vi-VN')}</strong>`; // Dùng innerHTML để giữ đậm
        
        // Cập nhật giá trị vào input ẩn để gửi đi
        if (contractValueInput) contractValueInput.value = grandTotal;
    };

    /** Thêm một sản phẩm vào bảng chi tiết hợp đồng */
    const addProductToContract = (item) => {
        const id = item.dataset.id;
        
        // Kiểm tra sản phẩm đã tồn tại trong hợp đồng chưa
        if (contractItemList.querySelector(`tr[data-id='${id}']`)) {
            alert('Sản phẩm này đã có trong hợp đồng.');
            return;
        }

        const name = item.dataset.name;
        const price = parseFloat(item.dataset.price);
        const newRow = document.createElement('tr');
        newRow.dataset.id = id;
        newRow.dataset.price = price;

        newRow.innerHTML = `
            <td class="product-name-cell">${name}
                <input type="hidden" name="productId" value="${id}">
            </td>
            <td><input type="number" name="quantity" class="form-control item-quantity" value="1" min="1"></td>
            <td class="item-price" style="text-align: right;">${price.toLocaleString('vi-VN')}</td>
            <td class="item-total" style="text-align: right;">${price.toLocaleString('vi-VN')}</td>
            <td style="text-align: center;"><button type="button" class="delete-item-btn"><i data-feather="trash-2" style="width:16px; height: 16px;"></i></button></td>
        `;

        contractItemList.appendChild(newRow);
        feather.replace(); // Kích hoạt icon cho nút xóa mới
        updateTotals();
        closeModal();
    };
    
    /** Lọc danh sách sản phẩm trong modal */
    const filterProducts = (event) => {
        const filter = event.target.value.toUpperCase();
        const items = productListContainer.getElementsByClassName('product-search-item');
        Array.from(items).forEach(item => {
            const name = item.querySelector('.name').textContent.toUpperCase();
            item.style.display = name.includes(filter) ? "" : "none";
        });
    };

    // --- Gắn các sự kiện (Event Listeners) ---
    
    if (addProductBtn) addProductBtn.addEventListener('click', openModal);
    if (closeProductModalBtn) closeProductModalBtn.addEventListener('click', closeModal);
    if (productSearchInput) productSearchInput.addEventListener('keyup', filterProducts);

    if (productModal) {
        productModal.addEventListener('click', (e) => {
            if (e.target === productModal) closeModal();
        });
    }

    if (productListContainer) {
        productListContainer.addEventListener('click', (e) => {
            const item = e.target.closest('.product-search-item');
            if (item) addProductToContract(item);
        });
    }
    
    if (contractItemList) {
        // Sử dụng event delegation cho các sự kiện input và click
        contractItemList.addEventListener('input', (e) => {
            if (e.target.classList.contains('item-quantity')) {
                updateTotals();
            }
        });

        contractItemList.addEventListener('click', (e) => {
            const deleteBtn = e.target.closest('.delete-item-btn');
            if (deleteBtn) {
                deleteBtn.closest('tr').remove();
                updateTotals();
            }
        });
    }
    
    // --- Khởi tạo ---

    // Tự động điền ngày ký là ngày hôm nay
    if (signDateInput) {
        signDateInput.value = new Date().toISOString().split('T')[0];
    }
    
    // Khởi tạo các icon và tính toán tổng ban đầu
    feather.replace();
    updateTotals();
});