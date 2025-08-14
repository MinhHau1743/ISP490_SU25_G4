package vn.edu.fpt.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Model Campaign (đã đồng bộ với DB mới): - Không lưu status text trong bảng
 * Campaigns. - Trạng thái lấy từ MaintenanceSchedules.status_id ->
 * Statuses.status_name. - Ngày bắt đầu/kết thúc hiển thị lấy từ lịch trình mới
 * nhất.
 */
public class Campaign {

    // ====== Fields map trực tiếp từ bảng Campaigns ======
    private int campaignId;
    private String campaignCode;
    private String name;
    private String description;
    private int enterpriseId;
    private int typeId;
    private int createdBy;
    private Integer updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ====== Dữ liệu bổ sung từ bảng liên quan (để hiển thị) ======
    // Lịch trình mới nhất
    private Date startDate;   // = scheduled_date
    private Date endDate;     // = end_date

    // Trạng thái từ Statuses (qua MaintenanceSchedules.status_id)
    private Integer statusId;
    private String statusName;

    // Thông tin hiển thị thêm
    private User creator;         // Users
    private String enterpriseName;
    private String typeName;

    // ====== Constructors ======
    public Campaign() {
    }

    // ====== Getters / Setters ======
    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignCode() {
        return campaignCode;
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
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

    // Lịch trình (mới nhất)
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

    // Trạng thái (qua Statuses)
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    // Thông tin hiển thị thêm
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

    // ====== Aliases để tương thích JSP/DAO cũ ======
    /**
     * Alias cho scheduled_date để không phải đổi JSP (list dùng
     * ${campaign.scheduledDate}).
     */
    public Date getScheduledDate() {
        return startDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.startDate = scheduledDate;
    }

    /**
     * Alias cho status text nếu code cũ còn gọi getStatus()/setStatus(...).
     */
    public String getStatus() {
        return statusName;
    }

    public void setStatus(String status) {
        this.statusName = status;
    }

    @Override
    public String toString() {
        return "Campaign{"
                + "campaignId=" + campaignId
                + ", campaignCode='" + campaignCode + '\''
                + ", name='" + name + '\''
                + ", enterpriseId=" + enterpriseId
                + ", typeId=" + typeId
                + ", statusId=" + statusId
                + ", statusName='" + statusName + '\''
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + '}';
    }
}
