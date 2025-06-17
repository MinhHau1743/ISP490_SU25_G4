/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();
    function updateSelectColor(selectElement) {
        const valueMap = {
            'completed': 'status-completed',
            'processing': 'status-processing',
            'pending': 'status-pending',
            'cancelled': 'status-cancelled',
            'Khẩn cấp': 'priority-urgent',
            'Cao': 'priority-high'
        };
        selectElement.className = 'form-control';
        const selectedText = selectElement.options[selectElement.selectedIndex].text;
        const colorClass = valueMap[selectedText] || valueMap[selectElement.value];
        if (colorClass) {
            selectElement.classList.add(colorClass);
        }
    }

    const statusSelect = document.getElementById('status');
    const prioritySelect = document.getElementById('priority');
    if (statusSelect) {
        updateSelectColor(statusSelect);
        statusSelect.addEventListener('change', () => updateSelectColor(statusSelect));
    }
    if (prioritySelect) {
        updateSelectColor(prioritySelect);
        prioritySelect.addEventListener('change', () => updateSelectColor(prioritySelect));
    }

    const radioBillableYes = document.getElementById('billableYes');
    const radioBillableNo = document.getElementById('billableNo');
    const amountGroup = document.getElementById('amount-group');
    const amountInput = document.getElementById('amount');

    function toggleAmountField() {
        if (radioBillableYes.checked) {
            amountGroup.classList.remove('hidden-field');
        } else {
            amountGroup.classList.add('hidden-field');
            amountInput.value = '';
        }
    }

    toggleAmountField();

    radioBillableYes.addEventListener('change', toggleAmountField);
    radioBillableNo.addEventListener('change', toggleAmountField);
});