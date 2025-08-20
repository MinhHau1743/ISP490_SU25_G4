package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp; // Nên dùng Timestamp để lấy đủ ngày giờ

/**
 * Lớp này ánh xạ đến bảng 'Contracts'. Đã được chỉnh sửa để loại bỏ các trường
 * không còn tồn tại trong CSDL và thay thế 'status' bằng 'statusId' và
 * 'statusName'.
 *
 * @author datnt (updated by AI)
 */
public class Contract {

    // --- CÁC TRƯỜNG KHỚP VỚI CSDL ---
    private long id;
    private String contractCode;
    private String contractName;
    private long enterpriseId; // Giữ nguyên tên biến
    private Long createdById;

    private Date startDate;
    private Date endDate;
    private Date signedDate;

    // Thay thế 'status' bằng hai trường mới
    private int statusId;
    private String statusName; // Thuộc tính này không có trong DB, dùng để hiển thị

    private BigDecimal totalValue;
    private String notes;
    private String fileUrl;
    private boolean isDeleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // --- CÁC TRƯỜNG PHỤ, LẤY TỪ JOIN ---
    private String enterpriseName;
    private String createdByName;
    private String enterpriseEmail;
    private String creatorName;
    private Enterprise enterprise;

    // --- CONSTRUCTORS ---
    public Contract() {
        // Constructor mặc định
    }

    // --- GETTERS AND SETTERS ---
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

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

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    // THÊM MỚI: Thêm cặp getter và setter cho trường mới
    public String getEnterpriseEmail() {
        return enterpriseEmail;
    }

    public void setEnterpriseEmail(String enterpriseEmail) {
        this.enterpriseEmail = enterpriseEmail;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    // === CẶP GETTER/SETTER MỚI ĐÃ ĐƯỢC THÊM VÀO ĐÂY ===
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
