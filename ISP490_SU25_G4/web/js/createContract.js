/*
 * File: createContract.js
 * Mô tả: Xử lý logic cho trang tạo hợp đồng, đã sửa lỗi và tích hợp modal thông báo tùy chỉnh.
 * Phiên bản: Hoàn thiện.
 */
document.addEventListener('DOMContentLoaded', function () {

    // --- CÀI ĐẶT CHUNG ---
    const VAT_RATE = 0.10;
    // Lấy context path từ thẻ body để xây dựng URL an toàn
    const contextPath = document.body.dataset.contextPath || '';

    // --- LẤY CÁC PHẦN TỬ DOM ---
    // Các phần tử chính của trang
    const contractItemList = document.getElementById('contract-item-list');
    const subTotalEl = document.getElementById('subTotal');
    const vatAmountEl = document.getElementById('vatAmount');
    const grandTotalEl = document.getElementById('grandTotal');
    const contractValueInput = document.getElementById('contractValue');

    // Các phần tử của modal chọn sản phẩm
    const addProductBtn = document.getElementById('addProductBtn');
    const productModal = document.getElementById('productSearchModal');
    const closeProductModalBtn = document.getElementById('closeProductModalBtn');
    const productListContainer = document.getElementById('productList');
    const productSearchInput = document.getElementById('productSearchInput');

    // Các phần tử của modal báo lỗi
    const errorModal = document.getElementById('errorModal');
    const errorMessageText = document.getElementById('errorMessageText');
    const closeErrorModalBtn = document.getElementById('closeErrorModalBtn');
    const confirmErrorBtn = document.getElementById('confirmErrorBtn');

    // --- CÁC HÀM XỬ LÝ ---

    /** Hiển thị modal lỗi với một thông điệp tùy chỉnh */
    const showErrorModal = (message) => {
        if (errorModal && errorMessageText) {
            errorMessageText.textContent = message;
            errorModal.style.display = 'flex';
        }
    };

    /** Ẩn modal lỗi */
    const hideErrorModal = () => {
        if (errorModal) {
            errorModal.style.display = 'none';
        }
    };

    /** Mở modal chọn sản phẩm */
    const openProductModal = () => {
        if (productModal)
            productModal.style.display = 'flex';
    };

    /** Đóng modal chọn sản phẩm */
    const closeProductModal = () => {
        if (productModal)
            productModal.style.display = 'none';
    };

    /** Cập nhật tất cả các giá trị tổng */
    const updateTotals = () => {
        let subTotal = 0;
        const rows = contractItemList.querySelectorAll('tr.product-row');

        rows.forEach(row => {
            // Sửa lỗi: Lấy giá trị từ data-value của .line-total để đảm bảo tính toán chính xác
            const lineTotal = parseFloat(row.querySelector('.line-total')?.dataset.value || 0);
            subTotal += lineTotal;
        });

        const vat = subTotal * VAT_RATE;
        const grandTotal = subTotal + vat;

        if (subTotalEl)
            subTotalEl.textContent = subTotal.toLocaleString('vi-VN');
        if (vatAmountEl)
            vatAmountEl.textContent = vat.toLocaleString('vi-VN');
        if (grandTotalEl)
            grandTotalEl.innerHTML = `<strong>${grandTotal.toLocaleString('vi-VN')}</strong>`;
        if (contractValueInput)
            contractValueInput.value = grandTotal; // Gửi đi giá trị dạng số
    };

    /** Cập nhật thành tiền cho một dòng cụ thể */
    const updateLineTotal = (row) => {
        // Sửa lỗi: Class của input số lượng là 'quantity-input'
        const quantityInput = row.querySelector('.quantity-input');
        const unitPriceCell = row.querySelector('.unit-price');
        const lineTotalCell = row.querySelector('.line-total');

        if (quantityInput && unitPriceCell && lineTotalCell) {
            const quantity = parseFloat(quantityInput.value) || 0;
            const price = parseFloat(unitPriceCell.dataset.price) || 0;
            const total = quantity * price;

            lineTotalCell.textContent = total.toLocaleString('vi-VN');
            lineTotalCell.dataset.value = total; // Lưu giá trị số để tính toán
        }
        updateTotals(); // Gọi hàm cập nhật tổng sau mỗi lần thay đổi
    };

    /** Thêm một sản phẩm vào bảng chi tiết hợp đồng */
    const addProductToContract = (item) => {
        const id = item.dataset.id;

        if (contractItemList.querySelector(`input[name="productId"][value="${id}"]`)) {
            // Sửa lỗi: Sử dụng modal tùy chỉnh thay vì alert()
            showErrorModal('Sản phẩm này đã có trong hợp đồng.');
            return;
        }

        const name = item.dataset.name;
        const price = parseFloat(item.dataset.price);
        const newRow = document.createElement('tr');
        newRow.className = 'product-row'; // Thêm class để dễ nhận biết

        newRow.innerHTML = `
            <td>
                ${name}
                <input type="hidden" name="productId" value="${id}">
            </td>
            <td><input type="number" name="quantity" class="form-control quantity-input" value="1" min="1"></td>
            <td class="money-cell unit-price" data-price="${price}">${price.toLocaleString('vi-VN')}</td>
            <td class="money-cell line-total" data-value="${price}">${price.toLocaleString('vi-VN')}</td>
      <td><button type="button" class="btn-delete-item"><i data-feather="trash-2"></i></button></td>
    
        `;

        contractItemList.appendChild(newRow);
        feather.replace();
        updateTotals();
        closeProductModal();
    };

    /** Lọc danh sách sản phẩm trong modal */
    const filterProducts = (event) => {
        const filter = event.target.value.toLowerCase();
        const items = productListContainer.querySelectorAll('.product-search-item');
        items.forEach(item => {
            const name = item.querySelector('.name').textContent.toLowerCase();
            item.style.display = name.includes(filter) ? "" : "none";
        });
    };

    // --- GẮN CÁC SỰ KIỆN (EVENT LISTENERS) ---

    // Sự kiện cho modal sản phẩm
    if (addProductBtn)
        addProductBtn.addEventListener('click', openProductModal);
    if (closeProductModalBtn)
        closeProductModalBtn.addEventListener('click', closeProductModal);
    if (productSearchInput)
        productSearchInput.addEventListener('keyup', filterProducts);
    if (productModal)
        productModal.addEventListener('click', (e) => {
            if (e.target === productModal)
                closeProductModal();
        });
    if (productListContainer)
        productListContainer.addEventListener('click', (e) => {
            const item = e.target.closest('.product-search-item');
            if (item)
                addProductToContract(item);
        });

    // Sự kiện cho modal lỗi
    if (closeErrorModalBtn)
        closeErrorModalBtn.addEventListener('click', hideErrorModal);
    if (confirmErrorBtn)
        confirmErrorBtn.addEventListener('click', hideErrorModal);

    // Sử dụng event delegation cho bảng sản phẩm
    if (contractItemList) {
        contractItemList.addEventListener('input', (e) => {
            if (e.target.classList.contains('quantity-input')) {
                updateLineTotal(e.target.closest('tr'));
            }
        });
        contractItemList.addEventListener('click', (e) => {
            // Sửa lỗi: class của nút xóa là 'btn-delete-item'
            const deleteBtn = e.target.closest('.btn-delete-item');
            if (deleteBtn) {
                deleteBtn.closest('tr').remove();
                updateTotals();
            }
        });
    }

    // --- KHỞI TẠO ---
    const signDateInput = document.getElementById('signedDate');
    if (signDateInput) {
        signDateInput.value = new Date().toISOString().split('T')[0];
    }

    feather.replace();
    updateTotals(); // Tính tổng tiền lần đầu khi tải trang
});