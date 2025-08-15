/*
 * Script dùng chung cho form Thêm mới và Chỉnh sửa Campaign.
 * Xử lý: Địa chỉ động, Color Picker, và Client-side Validation.
 */
(function () {
    "use strict";

    // ===================================================================
    // PHẦN 1: CÁC HÀM TIỆN ÍCH
    // ===================================================================
    const $ = (id) => document.getElementById(id);
    const enable = (el) => el && el.removeAttribute("disabled");
    const disable = (el) => el && el.setAttribute("disabled", "disabled");

    const normalizeId = (raw) => {
        if (raw == null)
            return "";
        const n = Number(String(raw).trim());
        return Number.isFinite(n) && n > 0 ? String(n) : "";
    };

    // SỬA LỖI: Lấy base URL từ thẻ body và trỏ API đến CampaignController
    const baseUrl = document.body.getAttribute('data-base-url') || '';
    const API_DISTRICTS = `${baseUrl}/edit-campaign?action=getDistricts&id=`;
    const API_WARDS = `${baseUrl}/edit-campaign?action=getWards&id=`;

    async function fetchJSON(url) {
        const res = await fetch(url, {headers: {'Accept': 'application/json'}});
        if (!res.ok)
            throw new Error(`${res.status} ${res.statusText}`);
        return res.json();
    }

    function buildOptions(data, selectedValue, firstLabel) {
        const selVal = normalizeId(selectedValue);
        let html = `<option value="" disabled ${selVal === "" ? " selected" : ""}>-- ${firstLabel} --</option>`;
        data.forEach((item) => {
            const id = normalizeId(item.id);
            if (!id)
                return;
            const name = item.name || "Không tên";
            const sel = selVal !== "" && selVal === id ? " selected" : "";
            html += `<option value="${id}"${sel}>${name}</option>`;
        });
        return html;
    }

    // ===================================================================
    // PHẦN 2: XỬ LÝ ĐỊA CHỈ ĐỘNG
    // ===================================================================
    async function hydrateAddress(provinceSelect, districtSelect, wardSelect) {
        const initialDistrict = normalizeId(districtSelect.value);
        const initialWard = normalizeId(wardSelect.value);

        async function loadDistricts(provinceId, selectedDistrictId) {
            if (!normalizeId(provinceId))
                return;
            disable(districtSelect);
            disable(wardSelect);
            districtSelect.innerHTML = '<option>Đang tải...</option>';
            wardSelect.innerHTML = '<option>-- Chọn Phường/Xã --</option>';

            try {
                const data = await fetchJSON(API_DISTRICTS + provinceId);
                districtSelect.innerHTML = buildOptions(data, selectedDistrictId, "Chọn Quận/Huyện");
                enable(districtSelect);
                if (normalizeId(districtSelect.value)) {
                    await loadWards(districtSelect.value, initialWard);
                }
            } catch (e) {
                console.error("Lỗi tải Quận/Huyện:", e);
                districtSelect.innerHTML = '<option>Lỗi tải dữ liệu</option>';
                enable(districtSelect);
            }
        }

        async function loadWards(districtId, selectedWardId) {
            if (!normalizeId(districtId))
                return;
            disable(wardSelect);
            wardSelect.innerHTML = '<option>Đang tải...</option>';

            try {
                const data = await fetchJSON(API_WARDS + districtId);
                wardSelect.innerHTML = buildOptions(data, selectedWardId, "Chọn Phường/Xã");
                enable(wardSelect);
            } catch (e) {
                console.error("Lỗi tải Phường/Xã:", e);
                wardSelect.innerHTML = '<option>Lỗi tải dữ liệu</option>';
                enable(wardSelect);
            }
        }

        provinceSelect.addEventListener("change", () => loadDistricts(provinceSelect.value, null));
        districtSelect.addEventListener("change", () => loadWards(districtSelect.value, null));

        // Logic khởi tạo cho trang Edit
        // Nếu server đã render sẵn quận/huyện thì không cần fetch lại
        if (districtSelect.options.length <= 1 && normalizeId(provinceSelect.value)) {
            loadDistricts(provinceSelect.value, initialDistrict);
        } else if (wardSelect.options.length <= 1 && normalizeId(districtSelect.value)) {
            loadWards(districtSelect.value, initialWard);
        }
    }

    // ===================================================================
    // PHẦN 3: VALIDATION & COLOR PICKER
    // ===================================================================
    function initColorPicker() {
        const colorPicker = $("colorPicker");
        const selectedColorInput = $("selectedColor");
        if (!colorPicker || !selectedColorInput)
            return;

        const setTick = (el) => {
            if (window.feather?.icons?.check) {
                el.innerHTML = window.feather.icons.check.toSvg({'stroke-width': 3, 'color': 'white'});
            }
        };

        const initialColor = (selectedColorInput.value || '').toLowerCase();
        let selectedDot = null;
        colorPicker.querySelectorAll(".color-dot").forEach((dot) => {
            if ((dot.dataset.color || '').toLowerCase() === initialColor) {
                selectedDot = dot;
            }
        });
        if (!selectedDot)
            selectedDot = colorPicker.querySelector(".color-dot");

        if (selectedDot) {
            selectedDot.classList.add("selected");
            setTick(selectedDot);
            selectedColorInput.value = selectedDot.dataset.color;
        }

        colorPicker.addEventListener("click", (ev) => {
            const clicked = ev.target.closest(".color-dot");
            if (!clicked)
                return;
            colorPicker.querySelectorAll(".color-dot").forEach((d) => {
                d.classList.remove("selected");
                d.innerHTML = "";
            });
            clicked.classList.add("selected");
            setTick(clicked);
            selectedColorInput.value = clicked.dataset.color;
        });
    }

    // ===================================================================
    // PHẦN 4: HÀM KHỞI ĐỘNG
    // ===================================================================
    function boot() {
        if (window.feather) {
            feather.replace({width: "1em", height: "1em"});
        }

        const province = $("province");
        const district = $("district");
        const ward = $("ward");

        if (province && district && ward) {
            hydrateAddress(province, district, ward);
        }

        initColorPicker();
        // Client-side validation có thể được thêm vào đây nếu cần
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", boot);
    } else {
        boot();
    }
})();