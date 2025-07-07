/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.sql.Date; // SỬA LỖI: Import java.sql.Date thay vì LocalDate
import java.time.LocalDateTime;

/**
 * Lớp này ánh xạ trực tiếp đến bảng 'Contracts' trong cơ sở dữ liệu.
 *
 * @author datnt
 */
public class Contract {

    private long id;
    private String contractCode;
    private String contractName;
    private long enterpriseId;
//    private Long contractTypeId;
    private Long createdById;
    
    // SỬA LỖI: Đổi kiểu dữ liệu từ LocalDate sang java.sql.Date
    private Date startDate; 
    private Date endDate;   
    private Date signedDate;
    
    private String status;
    private BigDecimal totalValue;
    private String notes;
    private String fileUrl;
    private Long renewedFromContractId;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Các thuộc tính không có trong DB nhưng hữu ích cho việc hiển thị
    private String enterpriseName;
    private String createdByName;

    // Constructor mặc định
    public Contract() {
    }

    // Constructor đầy đủ (ví dụ)
    // SỬA LỖI: Cập nhật kiểu dữ liệu trong constructor
    public Contract(long id, String contractCode, String contractName, long enterpriseId, Long contractTypeId, Long createdById, Date startDate, Date endDate, Date signedDate, String status, BigDecimal totalValue, String notes, String fileUrl, Long renewedFromContractId, boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.enterpriseId = enterpriseId;
//        this.contractTypeId = contractTypeId;
        this.createdById = createdById;
        this.startDate = startDate;
        this.endDate = endDate;
        this.signedDate = signedDate;
        this.status = status;
        this.totalValue = totalValue;
        this.notes = notes;
        this.fileUrl = fileUrl;
        this.renewedFromContractId = renewedFromContractId;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

//    public Long getContractTypeId() {
//        return contractTypeId;
//    }
//
//    public void setContractTypeId(Long contractTypeId) {
//        this.contractTypeId = contractTypeId;
//    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    // SỬA LỖI: Cập nhật Getters và Setters cho các trường Date
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Date signedDate) {
        this.signedDate = signedDate;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getRenewedFromContractId() {
        return renewedFromContractId;
    }

    public void setRenewedFromContractId(Long renewedFromContractId) {
        this.renewedFromContractId = renewedFromContractId;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters and setters cho các trường JOIN
    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}