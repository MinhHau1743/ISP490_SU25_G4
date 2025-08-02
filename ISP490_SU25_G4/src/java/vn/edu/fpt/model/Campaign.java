package vn.edu.fpt.model; // Gói của bạn

import java.sql.Timestamp;
import java.util.Date;

public class Campaign {
    private int campaignId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status;
    private int createdBy;
    private User user; // Đối tượng User của người tạo
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer updatedBy; // Thêm trường updatedBy (sử dụng Integer để cho phép giá trị null nếu cần)
    
    // THAY ĐỔI 1: Thêm trường để lưu tên tệp đính kèm
    private String attachmentFileName;

    // Constructors
    public Campaign() {}

    // Constructor đầy đủ (cập nhật để bao gồm updatedBy và attachmentFileName)
    public Campaign(int campaignId, String name, String description, Date startDate, Date endDate, String status, 
                    int createdBy, Integer updatedBy, Timestamp createdAt, Timestamp updatedAt, String attachmentFileName) {
        this.campaignId = campaignId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attachmentFileName = attachmentFileName; // Gán attachmentFileName
    }

    // Getters and Setters
    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { 
        this.startDate = startDate;
    }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { 
        this.endDate = endDate;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUpdatedBy() { return updatedBy; } 
    public void setUpdatedBy(Integer updatedBy) { 
        this.updatedBy = updatedBy;
    }

    // THAY ĐỔI 2: Thêm Getter và Setter cho attachmentFileName
    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }


    @Override
    public String toString() {
        return "Campaign{" +
                "campaignId=" + campaignId +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", attachmentFileName='" + attachmentFileName + '\'' + // Thêm vào toString
                '}';
    }
}