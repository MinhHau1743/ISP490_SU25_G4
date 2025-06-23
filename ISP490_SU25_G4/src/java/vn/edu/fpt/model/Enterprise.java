/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ducanh
 */
public class Enterprise {

    private int id;
    private String enterpriseCode;
    private String name;
    private String taxCode;
    private String fax;
    private String bankNumber;

    private int industryId;
    private int customerTypeId;
    private int areaId;
    private int addressId;
    private String avatarUrl;

    private boolean isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Fields from joined tables for easy display
    private String fullAddress; // From Addresses table
    private String primaryContactPhone; // From EnterpriseContacts table
    private String customerTypeName; // From CustomerTypes table
    private List<User> assignedUsers; // List of assigned employees from Users table

    // === THÊM TRƯỜNG MỚI VÀO ĐÂY ===
    private String primaryContactEmail;
    private int provinceId;
    private int districtId;
    private int wardId;
    private String streetAddress;

// === THIS FIELD WAS MISSING ===
    private List<EnterpriseContact> contacts;

    public Enterprise() {
        // Initialize lists to prevent NullPointerExceptions
        this.assignedUsers = new ArrayList<>();
        this.contacts = new ArrayList<>();
    }

    public Enterprise(int id, String enterpriseCode, String name, String taxCode, String fax, String bankNumber, int industryId, int customerTypeId, int areaId, int addressId, String avatarUrl, boolean isDeleted, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.enterpriseCode = enterpriseCode;
        this.name = name;
        this.taxCode = taxCode;
        this.fax = fax;
        this.bankNumber = bankNumber;
        this.industryId = industryId;
        this.customerTypeId = customerTypeId;
        this.areaId = areaId;
        this.addressId = addressId;
        this.avatarUrl = avatarUrl;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getWardId() {
        return wardId;
    }

    public void setWardId(int wardId) {
        this.wardId = wardId;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPrimaryContactEmail() {
        return primaryContactEmail;
    }

    public void setPrimaryContactEmail(String primaryContactEmail) {
        this.primaryContactEmail = primaryContactEmail;
    }

    public List<EnterpriseContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<EnterpriseContact> contacts) {
        this.contacts = contacts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public int getIndustryId() {
        return industryId;
    }

    public void setIndustryId(int industryId) {
        this.industryId = industryId;
    }

    public int getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(int customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(String primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public String getCustomerTypeName() {
        return customerTypeName;
    }

    public void setCustomerTypeName(String customerTypeName) {
        this.customerTypeName = customerTypeName;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Enterprise{" + "id=" + id + ", enterpriseCode=" + enterpriseCode + ", name=" + name + ", taxCode=" + taxCode + ", fax=" + fax + ", bankNumber=" + bankNumber + ", industryId=" + industryId + ", customerTypeId=" + customerTypeId + ", areaId=" + areaId + ", addressId=" + addressId + ", avatarUrl=" + avatarUrl + ", isDeleted=" + isDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }

}
