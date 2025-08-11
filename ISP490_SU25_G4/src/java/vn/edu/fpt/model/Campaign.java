package vn.edu.fpt.model;

import java.sql.Timestamp;

/**
 * Lớp Model này đại diện cho một chiến dịch (Campaign).
 * Cấu trúc đã được cập nhật để phù hợp với CSDL đã cải tiến.
 */
public class Campaign {

    // Các trường tương ứng với cột trong DB
    private int campaignId;
    private String name;
    private String description;
    private int enterpriseId;
    private int statusId;
    private int typeId;
    private int createdBy;
    private Integer updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Các thuộc tính bổ sung để chứa đối tượng liên quan (giúp hiển thị trên JSP)
    private User creator; // Đối tượng người tạo chiến dịch
    private Status status; // Đối tượng trạng thái
    private String enterpriseName; // Tên khách hàng
    private String typeName; // Tên loại chiến dịch

    /**
     * Constructor mặc định
     */
    public Campaign() {
    }

    // Getters and Setters
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

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
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

    // --- Getters & Setters cho các thuộc tính đối tượng bổ sung ---
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Campaign{" +
                "campaignId=" + campaignId +
                ", name='" + name + '\'' +
                ", enterpriseId=" + enterpriseId +
                ", statusId=" + statusId +
                ", typeId=" + typeId +
                ", createdBy=" + createdBy +
                '}';
    }
}