package vn.edu.fpt.model;

import java.sql.Timestamp;
import java.util.Date; // ĐÃ THÊM: import java.util.Date

/**
 * Lớp Model này đại diện cho một chiến dịch (Campaign). Cấu trúc đã được cập
 * nhật để phù hợp với CSDL đã cải tiến.
 */
public class Campaign {

    // Các trường tương ứng với cột trong DB
    private int campaignId;
    private String name;
    private String description;
    private int enterpriseId;
    private String status;
    private int typeId;
    private int createdBy;
    private Integer updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ĐÃ THÊM: Các trường cho ngày bắt đầu và kết thúc lấy từ MaintenanceSchedules
    private Date startDate;
    private Date endDate;

    // Các thuộc tính bổ sung để chứa đối tượng liên quan (giúp hiển thị trên JSP)
    private User creator; // Đối tượng người tạo chiến dịch
    private String enterpriseName; // Tên khách hàng
    private String typeName; // Tên loại chiến dịch

    /**
     * Constructor mặc định
     */
    public Campaign() {
    }

    // --- Getters and Setters ---
    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
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

    // ---- BẮT ĐẦU PHẦN ĐÃ THÊM ----
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
    // ---- KẾT THÚC PHẦN ĐÃ THÊM ----

    // --- Getters & Setters cho các thuộc tính đối tượng bổ sung ---
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
