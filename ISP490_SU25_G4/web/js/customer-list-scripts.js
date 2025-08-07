/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// File: src/main/webapp/js/customer-list-scripts.js
document.addEventListener('DOMContentLoaded', function () {
    // --- KHỞI TẠO CHUNG ---
    if (typeof feather !== 'undefined') {
        feather.replace();
    }
    const filterContainer = document.getElementById('filter-container');
    const filterToggleBtn = document.getElementById('filter-toggle-btn');
    const clearFilterBtn = document.getElementById('clear-filter-btn'); // Lấy nút xóa lọc
    const searchInput = document.getElementById('searchInput');
    const suggestionsContainer = document.getElementById('suggestionsContainer');
    const provinceSelect = document.getElementById('province');
    const districtSelect = document.getElementById('district');
    const wardSelect = document.getElementById('ward');

    // Lấy các biến toàn cục được khai báo trong JSP
    const BASE_URL = window.APP_CONFIG.BASE_URL;
    const SELECTED_DISTRICT_ID = window.APP_CONFIG.SELECTED_DISTRICT_ID;
    const SELECTED_WARD_ID = window.APP_CONFIG.SELECTED_WARD_ID;

    // --- MODULE 1: XỬ LÝ HIỂN THỊ BỘ LỌC ---
    function setupFilterToggle() {
        if (!filterContainer || !filterToggleBtn)
            return;

        const urlParams = new URLSearchParams(window.location.search);
        const hasActiveFilters = urlParams.get('provinceId') || urlParams.get('customerTypeId') || urlParams.get('employeeId');

        // === SỬA LỖI TẠI ĐÂY: Logic giữ bộ lọc mở ===
        // 1. Kiểm tra xem có cờ "giữ mở" trong sessionStorage không
        if (sessionStorage.getItem('keepFilterOpen') === 'true') {
            filterContainer.style.display = 'flex';
            sessionStorage.removeItem('keepFilterOpen'); // Xóa cờ đi sau khi đã dùng
        }
        // 2. Nếu không có cờ, kiểm tra xem có tham số lọc active không
        else if (hasActiveFilters) {
            filterContainer.style.display = 'flex';
        }

        filterToggleBtn.addEventListener('click', () => {
            const isHidden = filterContainer.style.display === 'none' || filterContainer.style.display === '';
            filterContainer.style.display = isHidden ? 'flex' : 'none';
        });

        // 3. Thêm sự kiện cho nút "Xóa lọc"
        if (clearFilterBtn) {
            clearFilterBtn.addEventListener('click', function () {
                // Trước khi chuyển trang, đặt cờ vào sessionStorage
                sessionStorage.setItem('keepFilterOpen', 'true');
            });
        }
    }

    // --- MODULE 2: XỬ LÝ DROPDOWN ĐỊA CHỈ PHỤ THUỘC (Giữ nguyên) ---
    async function fetchAndPopulate(url, selectElement, preselectId) {
        selectElement.innerHTML = '<option value="">Tất cả</option>';
        if (!url) {
            selectElement.disabled = true;
            return;
        }
        try {
            const response = await fetch(url);
            if (!response.ok)
                throw new Error('Network error');
            const data = await response.json();

            data.forEach(item => {
                const option = new Option(item.name, item.id);
                if (item.id == preselectId) {
                    option.selected = true;
                }
                selectElement.add(option);
            });
            selectElement.disabled = false;
        } catch (error) {
            console.error('Failed to fetch address data:', error);
            selectElement.disabled = true;
        }
    }

    async function setupAddressDropdowns() {
        if (!provinceSelect || !districtSelect || !wardSelect)
            return;
        const initialProvinceId = provinceSelect.value;
        provinceSelect.addEventListener('change', async () => {
            const provinceId = provinceSelect.value;
            const url = provinceId ? `${BASE_URL}/customer/getDistricts?provinceId=${provinceId}` : null;
            await fetchAndPopulate(url, districtSelect, null);
            wardSelect.innerHTML = '<option value="">Tất cả</option>';
            wardSelect.disabled = true;
        });
        districtSelect.addEventListener('change', async () => {
            const districtId = districtSelect.value;
            const url = districtId ? `${BASE_URL}/customer/getWards?districtId=${districtId}` : null;
            await fetchAndPopulate(url, wardSelect, null);
        });

        if (initialProvinceId) {
            const districtUrl = `${BASE_URL}/customer/getDistricts?provinceId=${initialProvinceId}`;
            await fetchAndPopulate(districtUrl, districtSelect, SELECTED_DISTRICT_ID);

            if (SELECTED_DISTRICT_ID) {
                const wardUrl = `${BASE_URL}/customer/getWards?districtId=${SELECTED_DISTRICT_ID}`;
                await fetchAndPopulate(wardUrl, wardSelect, SELECTED_WARD_ID);
            }
        }
    }

    // --- MODULE 3: XỬ LÝ GỢI Ý TÌM KIẾM (Giữ nguyên) ---
    function setupSearchSuggestions() {
        if (!searchInput || !suggestionsContainer)
            return;
        let debounceTimer;
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                const query = searchInput.value;
                if (query.length < 2) {
                    suggestionsContainer.style.display = 'none';
                    return;
                }
                fetchSuggestions(query);
            }, 300);
        });
        document.addEventListener('click', (e) => {
            if (!suggestionsContainer.contains(e.target) && !searchInput.contains(e.target)) {
                suggestionsContainer.style.display = 'none';
            }
        });
    }

    async function fetchSuggestions(query) {
        try {
            const response = await fetch(`${BASE_URL}/customer/searchSuggestions?query=${encodeURIComponent(query)}`);
            if (!response.ok)
                throw new Error('Network error');
            const suggestions = await response.json();

            suggestionsContainer.innerHTML = '';
            if (suggestions.length > 0) {
                suggestions.forEach(text => {
                    const item = document.createElement('div');
                    item.className = 'suggestion-item';
                    item.textContent = text;
                    item.onclick = () => {
                        searchInput.value = text;
                        suggestionsContainer.style.display = 'none';
                        document.getElementById('filterForm').submit();
                    };
                    suggestionsContainer.appendChild(item);
                });
                suggestionsContainer.style.display = 'block';
            } else {
                suggestionsContainer.style.display = 'none';
            }
        } catch (error) {
            console.error('Failed to fetch suggestions:', error);
            suggestionsContainer.style.display = 'none';
        }
    }

    // --- GỌI CÁC HÀM KHỞI TẠO ---
    setupFilterToggle();
    setupAddressDropdowns();
    setupSearchSuggestions();
});