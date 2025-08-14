/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

/**
 *
 * @author ducanh
 */
public class Address {

    private int id;
    private String streetAddress;
    private int wardId;
    private int districtId;
    private int provinceId;
    private String fullAddress;
    private Province province;
    private District district;
    private Ward ward;
    // Constructor chỉ với street_address như trong form hiện tại

    public Address(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Address() {
    }

    public Address(int id, String streetAddress, int wardId, int districtId, int provinceId, String fullAddress) {
        this.id = id;
        this.streetAddress = streetAddress;
        this.wardId = wardId;
        this.districtId = districtId;
        this.provinceId = provinceId;
        this.fullAddress = fullAddress;
    }

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

    public int getWardId() {
        return wardId;
    }

    public void setWardId(int wardId) {
        this.wardId = wardId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    // VÀ THÊM CẶP GET/SET NÀY
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    // VÀ THÊM CẶP GET/SET NÀY
    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }
    // VÀ THÊM CẶP GET/SET NÀY
    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }
    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", streetAddress=" + streetAddress + ", wardId=" + wardId + ", districtId=" + districtId + ", provinceId=" + provinceId + ", fullAddress=" + fullAddress + '}';
    }

}
