/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
document.addEventListener('DOMContentLoaded', function () {
    feather.replace();
    const today = new Date().toISOString().split('T')[0];
    const createdDateInput = document.getElementById('createdDate');
    if (createdDateInput) {
        createdDateInput.value = today;
    }

    const billableRadios = document.querySelectorAll('input[name="isBillable"]');
    const amountGroup = document.getElementById('amount-group');
    function toggleAmountField() {
        if (document.getElementById('billableYes').checked) {
            amountGroup.style.display = 'block';
        } else {
            amountGroup.style.display = 'none';
        }
    }
    billableRadios.forEach(radio => radio.addEventListener('change', toggleAmountField));
    toggleAmountField();

    const form = document.getElementById('createTicketForm');
    const modal = document.getElementById('successModal');
    if (form && modal) {
        form.addEventListener('submit', function (event) {
            // Logic gửi form thật sẽ được xử lý ở backend
            // Ở đây chỉ mô phỏng việc hiển thị modal
            event.preventDefault();
            modal.classList.add('show');
            feather.replace();
            setTimeout(function () {
                // modal.classList.remove('show');
                // window.location.href = 'transaction'; 
                form.submit(); // Gửi form thật sau khi hiệu ứng kết thúc
            }, 2500);
        });
    }

    // Logic thêm thiết bị
    const addDeviceBtn = document.getElementById('addDeviceBtn');
    const deviceList = document.getElementById('device-list');
    let deviceIndex = 2; // Bắt đầu từ 2 vì đã có 1 hàng sẵn

    addDeviceBtn.addEventListener('click', function () {
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
                        <td><input type="text" name="deviceName_${deviceIndex}" class="form-control-table" placeholder="Tên thiết bị"></td>
                        <td><input type="text" name="deviceSerial_${deviceIndex}" class="form-control-table" placeholder="Serial number"></td>
                        <td><textarea name="deviceNote_${deviceIndex}" class="form-control-table" rows="1" placeholder="Mô tả sự cố"></textarea></td>
                    `;
        deviceList.appendChild(newRow);
        deviceIndex++;
    });

});

