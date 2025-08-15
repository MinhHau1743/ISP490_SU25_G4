/* File: /js/editCampaign.js */
(function () {
    "use strict";
    // ===================================================================
    // PHẦN 1: CÁC HÀM TIỆN ÍCH (giữ nguyên)
    // ===================================================================
    const $ = (id) => document.getElementById(id);
    const enable = (el) => el && el.removeAttribute("disabled");
    const disable = (el) => el && el.setAttribute("disabled", "disabled");

    const pick = (obj, keys) => {
        for (const k of keys)
            if (obj && obj[k] != null)
                return obj[k];
        return null;
    };
    const validHexColor = (s) => /^#[0-9A-Fa-f]{6}$/.test(s || "");

    const normalizeId = (raw) => {
        if (raw == null)
            return "";
        const n = Number(String(raw).trim());
        return Number.isFinite(n) && n > 0 ? String(n) : "";
    };

    const ctx = document.body?.getAttribute("data-context") || "";
    const API_DISTRICTS = `${ctx}/customer/getDistricts?provinceId=`;
    const API_WARDS = `${ctx}/customer/getWards?districtId=`;

    async function fetchJSON(url) {
        const res = await fetch(url, {headers: {Accept: "application/json"}});
        if (!res.ok)
            throw new Error(`${res.status} ${res.statusText}`);
        return res.json();
    }

    function buildOptions(data, selectedValue, firstLabel, mapKeys) {
        const selVal = normalizeId(selectedValue);
        let html = `<option value="" disabled${
                selVal === "" ? " selected" : ""
                }>-- ${firstLabel} --</option>`;
        data.forEach((item) => {
            const id = normalizeId(pick(item, mapKeys.id));
            if (!id)
                return;
            const name = pick(item, mapKeys.name) || "Không tên";
            const sel = selVal !== "" && selVal === id ? " selected" : "";
            html += `<option value="${id}"${sel}>${name}</option>`;
        });
        return html;
    }

    function getInit(selectEl) {
        if (!selectEl)
            return "";
        if (selectEl.dataset && typeof selectEl.dataset.init !== "undefined") {
            const v = normalizeId(selectEl.dataset.init);
            if (v)
                return v;
        }
        return normalizeId(selectEl.value);
    }

    function waitForAddressSelects(timeoutMs = 2000) {
        return new Promise((resolve) => {
            const t0 = performance.now();
            const tick = () => {
                const province = $("province");
                const district = $("district");
                const ward = $("ward");
                if (province && district && ward)
                    return resolve({province, district, ward});
                if (performance.now() - t0 > timeoutMs)
                    return resolve({province, district, ward});
                requestAnimationFrame(tick);
            };
            tick();
        });
    }

    // ===================================================================
    // PHẦN 2: XỬ LÝ ĐỊA CHỈ ĐỘNG (viết lại cho rõ ràng và chính xác)
    // ===================================================================
    async function hydrateAddress(provinceSelect, districtSelect, wardSelect) {
        const INIT_PROVINCE = getInit(provinceSelect);
        const INIT_DISTRICT = getInit(districtSelect);
        const INIT_WARD = getInit(wardSelect);

        // --- Hàm tải danh sách Quận/Huyện ---
        async function loadDistricts(provinceId, selectedDistrictId) {
            const pid = normalizeId(provinceId);
            if (!pid)
                return;

            disable(districtSelect);
            disable(wardSelect);
            districtSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';
            wardSelect.innerHTML = '<option value="" disabled selected>-- Chọn Phường/Xã --</option>';

            try {
                const data = await fetchJSON(API_DISTRICTS + encodeURIComponent(pid));
                districtSelect.innerHTML = buildOptions(data, selectedDistrictId, "Chọn Quận/Huyện", {id: ["id"], name: ["name"]});
                enable(districtSelect);

                // Nếu sau khi tải xong, có một quận được chọn -> tự động tải phường
                const currentDistrictId = normalizeId(districtSelect.value);
                if (currentDistrictId) {
                    await loadWards(currentDistrictId, INIT_WARD);
                }
            } catch (e) {
                console.error("Lỗi tải danh sách Quận/Huyện:", e);
                districtSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                enable(districtSelect);
            }
        }

        // --- Hàm tải danh sách Phường/Xã ---
        async function loadWards(districtId, selectedWardId) {
            const did = normalizeId(districtId);
            if (!did)
                return;

            disable(wardSelect);
            wardSelect.innerHTML = '<option value="" disabled selected>-- Đang tải... --</option>';

            try {
                const data = await fetchJSON(API_WARDS + encodeURIComponent(did));
                wardSelect.innerHTML = buildOptions(data, selectedWardId, "Chọn Phường/Xã", {id: ["id"], name: ["name"]});
                enable(wardSelect);
            } catch (e) {
                console.error("Lỗi tải danh sách Phường/Xã:", e);
                wardSelect.innerHTML = '<option value="" disabled selected>-- Lỗi tải dữ liệu --</option>';
                enable(wardSelect);
            }
        }

        // --- Gắn sự kiện "change" cho người dùng ---
        provinceSelect.addEventListener("change", (e) => {
            loadDistricts(e.target.value, null); // Khi người dùng tự chọn, không cần chọn sẵn quận nào
        });
        districtSelect.addEventListener("change", (e) => {
            loadWards(e.target.value, null); // Tương tự, không cần chọn sẵn phường nào
        });

        // --- Logic xử lý khi tải trang lần đầu ---
        const hasServerDistricts = districtSelect.options.length > 1 && !districtSelect.disabled;
        const hasServerWards = wardSelect.options.length > 1 && !wardSelect.disabled;

        // Chỉ gọi API để tải huyện nếu tỉnh đã được chọn VÀ server chưa render sẵn danh sách huyện
        if (INIT_PROVINCE && !hasServerDistricts) {
            await loadDistricts(INIT_PROVINCE, INIT_DISTRICT);
        }
        // Tương tự, chỉ tải xã nếu huyện đã được chọn VÀ server chưa render sẵn danh sách xã
        else if (INIT_DISTRICT && !hasServerWards) {
            await loadWards(INIT_DISTRICT, INIT_WARD);
        }

        console.log("[Address Logic] Hoàn tất thiết lập địa chỉ.");
    }

    // ===================================================================
    // PHẦN 3: VALIDATION & COLOR PICKER (giữ nguyên)
    // ===================================================================
    function initValidation() {
        const form = $("campaignForm");
        const errBox = $("formErrorContainer");
        if (!form)
            return;

        const need = (id, name, list) => {
            const el = $(id);
            if (!el || !el.value || (typeof el.value === "string" && el.value.trim() === "")) {
                list.push(`Vui lòng nhập/chọn thông tin cho trường "${name}".`);
            }
        };

        form.addEventListener("submit", (e) => {
            e.preventDefault();
            const errs = [];
            [
                ["campaignName", "Tên chiến dịch"], ["campaignType", "Loại chiến dịch"],
                ["enterpriseId", "Khách hàng"], ["statusId", "Trạng thái"],
                ["scheduledDate", "Ngày bắt đầu"], ["assignedUserId", "Nhân viên thực hiện"],
                ["province", "Tỉnh/Thành phố"], ["district", "Quận/Huyện"],
                ["ward", "Phường/Xã"], ["streetAddress", "Địa chỉ cụ thể"],
            ].forEach(([id, label]) => need(id, label, errs));

            const color = $("selectedColor")?.value || "";
            if (color && !validHexColor(color))
                errs.push("Màu sắc không hợp lệ. Vui lòng nhập theo định dạng #RRGGBB.");

            const sd = $("scheduledDate")?.value || "";
            const ed = $("endDate")?.value || "";
            const st = $("startTime")?.value || "";
            const et = $("endTime")?.value || "";
            if (sd && ed) {
                const d1 = new Date(sd), d2 = new Date(ed);
                if (d2 < d1)
                    errs.push("Ngày kết thúc không được sớm hơn ngày bắt đầu.");
                else if (st && et && d2.getTime() === d1.getTime() && et < st)
                    errs.push("Giờ kết thúc không được sớm hơn giờ bắt đầu trong cùng ngày.");
            }

            if (errs.length) {
                if (errBox) {
                    errBox.innerHTML = "<strong>Vui lòng sửa các lỗi sau:</strong><br>" + errs.join("<br>");
                    errBox.style.display = "block";
                    errBox.scrollIntoView({behavior: "smooth", block: "start"});
                } else {
                    alert(errs.join("\n"));
                }
                return;
            }
            if (errBox)
                errBox.style.display = "none";
            form.submit();
        });
    }

    function initColorPicker() {
        const colorPicker = $("colorPicker");
        const selectedColorInput = $("selectedColor");
        if (!colorPicker || !selectedColorInput)
            return;

        const setTick = (el) => {
            try {
                if (window.feather?.icons?.check) {
                    el.innerHTML = window.feather.icons.check.toSvg({"stroke-width": 3, color: "white"});
                }
            } catch (_) {
            }
        };

        const initial = (selectedColorInput.value || "").toLowerCase();
        let found = false;
        colorPicker.querySelectorAll(".color-dot").forEach((dot) => {
            if ((dot.dataset.color || "").toLowerCase() === initial) {
                dot.classList.add("selected");
                setTick(dot);
                found = true;
            }
        });
        if (!found) {
            const first = colorPicker.querySelector(".color-dot");
            if (first) {
                first.classList.add("selected");
                setTick(first);
                selectedColorInput.value = first.dataset.color || "#0d9488";
            }
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
            selectedColorInput.value = clicked.dataset.color || "#0d9488";
        });
    }

    // ===================================================================
    // PHẦN 4: HÀM KHỞI ĐỘNG (giữ nguyên)
    // ===================================================================
    function boot() {
        try {
            window.feather?.replace({width: "1em", height: "1em"});
        } catch (_) {
        }

        waitForAddressSelects().then(async ({ province, district, ward }) => {
            if (!province || !district || !ward) {
                console.warn("[editCampaign] Không tìm thấy đủ 3 select province/district/ward trong DOM.");
                return;
            }
            await hydrateAddress(province, district, ward);
        });

        initValidation();
        initColorPicker();
    }

    if (document.readyState === "loading")
        document.addEventListener("DOMContentLoaded", boot);
    else
        boot();
})();