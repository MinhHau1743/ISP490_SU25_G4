package vn.edu.fpt.model;

/**
 * Model cho Address. Phiên bản đã được sửa lại để đồng bộ hóa giữa các trường
 * ID (provinceId, ...) và các đối tượng tương ứng (province, ...).
 */
public class Address {

    private int id;
    private String streetAddress;
    private String fullAddress;

    // Các trường ID
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;

    // Các đối tượng tương ứng
    private Province province;
    private District district;
    private Ward ward;

    public Address() {
    }

    // Constructor tiện lợi (đã thêm ở các bước trước)
    public Address(int provinceId, int districtId, int wardId, String streetAddress) {
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        this.streetAddress = streetAddress;
    }

    // =====================================================================
    // CÁC GETTER/SETTER CƠ BẢN
    // =====================================================================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    // =====================================================================
    // CÁC GETTER/SETTER ĐÃ ĐƯỢC ĐỒNG BỘ HÓA
    // =====================================================================
    // --- Province ---
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
        // Tự động cập nhật ID khi gán đối tượng
        if (province != null) {
            this.provinceId = province.getId();
        }
    }

    public Integer getProvinceId() {
        // Luôn ưu tiên lấy ID từ đối tượng để đảm bảo chính xác
        if (this.province != null) {
            return this.province.getId();
        }
        return this.provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    // --- District ---
    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
        if (district != null) {
            this.districtId = district.getId();
        }
    }

    public Integer getDistrictId() {
        if (this.district != null) {
            return this.district.getId();
        }
        return this.districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    // --- Ward ---
    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
        if (ward != null) {
            this.wardId = ward.getId();
        }
    }

    public Integer getWardId() {
        if (this.ward != null) {
            return this.ward.getId();
        }
        return this.wardId;
    }

    public void setWardId(Integer wardId) {
        this.wardId = wardId;
    }
}
