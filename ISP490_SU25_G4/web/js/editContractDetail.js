/**
 * File: editContractDetail.js
 * Author: NGUYEN MINH (Final Version by Gemini)
 * Description: Handles all client-side interactivity for the contract edit page.
 */
document.addEventListener('DOMContentLoaded', function () {

    // --- 1. Lấy ra tất cả các phần tử DOM cần thiết ---
    const itemListBody = document.getElementById('contract-item-list');
    const addProductBtn = document.getElementById('addProductBtn');
    const contractValueInput = document.getElementById('contractValue');

    const subTotalEl = document.getElementById('subTotal');
    const vatAmountEl = document.getElementById('vatAmount');
    const grandTotalEl = document.getElementById('grandTotal');

    const productModal = document.getElementById('productSearchModal');
    const closeProductModalBtn = document.getElementById('closeProductModalBtn');
    const productSearchInput = document.getElementById('productSearchInput');
    const productListContainer = document.getElementById('productList');

    const errorModal = document.getElementById('errorModal');
    const errorMessageText = document.getElementById('errorMessageText');
    const closeErrorModalBtn = document.getElementById('closeErrorModalBtn');
    const confirmErrorBtn = document.getElementById('confirmErrorBtn');

    // --- 2. Các hàm xử lý ---

    function formatCurrency(value) {
        return new Intl.NumberFormat('vi-VN').format(Math.round(value));
    }

    function updateTotals() {
        if (!itemListBody) return;
        let subtotal = 0;
        const rows = itemListBody.querySelectorAll('tr.product-row');
        rows.forEach(row => {
            const quantity = parseInt(row.querySelector('.quantity-input').value) || 0;
            const price = parseFloat(row.querySelector('.unit-price').dataset.price) || 0;
            const total = quantity * price;
            row.querySelector('.line-total').textContent = formatCurrency(total);
            subtotal += total;
        });
        const vat = subtotal * 0.10;
        const grandTotal = subtotal + vat;
        if (subTotalEl) subTotalEl.textContent = formatCurrency(subtotal);
        if (vatAmountEl) vatAmountEl.textContent = formatCurrency(vat);
        if (grandTotalEl) grandTotalEl.textContent = formatCurrency(grandTotal);
        if (contractValueInput) contractValueInput.value = grandTotal;
    }

    function addProductRow(id, name, price) {
        const newRowHTML = `
            <tr class="product-row">
                <td>
                    ${name}
                    <input type="hidden" name="productId" value="${id}">
                </td>
                <td><input type="number" name="quantity" class="form-control quantity-input" value="1" min="1"></td>
                <td class="money-cell unit-price" data-price="${price}">${formatCurrency(price)}</td>
                <td class="money-cell line-total">${formatCurrency(price)}</td>
                <td><button type="button" class="btn-delete-item"><i data-feather="trash-2"></i></button></td>
            </tr>`;
        itemListBody.insertAdjacentHTML('beforeend', newRowHTML);
        feather.replace();
        updateTotals();
    }

    function showErrorModal(message) {
        if (errorMessageText) errorMessageText.textContent = message;
        if (errorModal) errorModal.style.display = 'flex';
    }

    const closeModal = () => { if (productModal) productModal.style.display = 'none'; };
    const closeErrorModal = () => { if (errorModal) errorModal.style.display = 'none'; };

    // --- 3. Gán các sự kiện ---
    function bindEvents() {
        if (addProductBtn) addProductBtn.addEventListener('click', () => productModal.style.display = 'flex');
        
        if (closeProductModalBtn) closeProductModalBtn.addEventListener('click', closeModal);
        if (productModal) {
             productModal.addEventListener('click', (e) => { 
                if (e.target.closest('.product-search-item')) {
                    const item = e.target.closest('.product-search-item');
                    const productId = item.dataset.id;
                    if (document.querySelector(`input[name="productId"][value="${productId}"]`)) {
                        showErrorModal('Sản phẩm này đã có trong hợp đồng.');
                        return;
                    }
                    addProductRow(productId, item.dataset.name, parseFloat(item.dataset.price));
                    closeModal();
                } else if (e.target === productModal) {
                    closeModal();
                }
             });
        }
        
        if (productSearchInput) productSearchInput.addEventListener('keyup', (e) => {
            const searchTerm = e.target.value.toLowerCase();
            productListContainer.querySelectorAll('.product-search-item').forEach(item => {
                const name = item.dataset.name.toLowerCase();
                item.style.display = name.includes(searchTerm) ? 'flex' : 'none';
            });
        });
        
        if (itemListBody) {
            itemListBody.addEventListener('input', (e) => {
                if (e.target.classList.contains('quantity-input')) {
                    updateTotals();
                }
            });
            itemListBody.addEventListener('click', (e) => {
                if (e.target.closest('.btn-delete-item')) {
                    e.target.closest('tr').remove();
                    updateTotals();
                }
            });
        }
        
        if (closeErrorModalBtn) closeErrorModalBtn.addEventListener('click', closeErrorModal);
        if (confirmErrorBtn) confirmErrorBtn.addEventListener('click', closeErrorModal);
        if (errorModal) errorModal.addEventListener('click', (e) => { if (e.target === errorModal) closeErrorModal(); });
    }

    // --- 4. Khởi tạo ---
    function init() {
        bindEvents();
        updateTotals();
        feather.replace();
    }
    
    init();
});