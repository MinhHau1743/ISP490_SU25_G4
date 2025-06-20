/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
                feather.replace();
                const today = new Date().toISOString().split('T')[0];
                const signDateInput = document.getElementById('signDate');
                if(signDateInput) signDateInput.value = today;

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

                const closeModal = () => { productModal.style.display = 'none'; };
                addProductBtn.addEventListener('click', () => { productModal.style.display = 'flex'; feather.replace(); });
                closeProductModalBtn.addEventListener('click', closeModal);
                productModal.addEventListener('click', (e) => { if(e.target === productModal) closeModal(); });

                // Lọc sản phẩm trong modal
                productSearchInput.addEventListener('keyup', function() {
                    const filter = this.value.toUpperCase();
                    const items = productListContainer.getElementsByClassName('product-search-item');
                    for (let i = 0; i < items.length; i++) {
                        const name = items[i].querySelector('.name').textContent.toUpperCase();
                        if (name.indexOf(filter) > -1) {
                            items[i].style.display = "";
                        } else {
                            items[i].style.display = "none";
                        }
                    }
                });

                productListContainer.addEventListener('click', (e) => {
                    const item = e.target.closest('.product-search-item');
                    if (!item) return;
                    
                    const existingItem = contractItemList.querySelector(`tr[data-id='${item.dataset.id}']`);
                    if (existingItem) {
                        alert('Sản phẩm này đã có trong hợp đồng.');
                        return;
                    }

                    const id = item.dataset.id;
                    const name = item.dataset.name;
                    const price = parseFloat(item.dataset.price);
                    const newRow = document.createElement('tr');
                    newRow.dataset.price = price;
                    newRow.dataset.id = id;
                    
                    newRow.innerHTML = `
                        <td class="product-name-cell">${name}<input type="hidden" name="productId" value="${id}"></td>
                        <td><input type="number" name="quantity" class="form-control item-quantity" value="1" min="1"></td>
                        <td class="item-price" style="text-align: right;">${price.toLocaleString('vi-VN')}</td>
                        <td class="item-total" style="text-align: right;">${price.toLocaleString('vi-VN')}</td>
                        <td style="text-align: center;"><button type="button" class="delete-item-btn"><i data-feather="trash-2" style="width:16px; height: 16px;"></i></button></td>
                    `;
                    contractItemList.appendChild(newRow);
                    feather.replace();
                    updateTotals();
                    closeModal();
                });

                contractItemList.addEventListener('click', (e) => {
                    if (e.target.closest('.delete-item-btn')) {
                        e.target.closest('tr').remove();
                        updateTotals();
                    }
                });
                contractItemList.addEventListener('input', (e) => {
                     if (e.target.classList.contains('item-quantity')) {
                        updateTotals();
                    }
                });

                function updateTotals() {
                    let subTotal = 0;
                    contractItemList.querySelectorAll('tr').forEach(row => {
                        const quantity = parseFloat(row.querySelector('.item-quantity').value) || 0;
                        const price = parseFloat(row.dataset.price) || 0; 
                        const total = quantity * price;
                        row.querySelector('.item-total').textContent = total.toLocaleString('vi-VN');
                        subTotal += total;
                    });
                    const vat = subTotal * 0.1;
                    const grandTotal = subTotal + vat;
                    subTotalEl.textContent = subTotal.toLocaleString('vi-VN');
                    vatAmountEl.textContent = vat.toLocaleString('vi-VN');
                    grandTotalEl.textContent = grandTotal.toLocaleString('vi-VN');
                    contractValueInput.value = grandTotal;
                }
                
                updateTotals();
            });

