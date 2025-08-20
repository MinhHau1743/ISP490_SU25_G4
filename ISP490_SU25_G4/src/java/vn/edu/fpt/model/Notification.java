package vn.edu.fpt.model;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class Notification {

    private int id;
    private String title;
    private String message;
    private String linkUrl;
    private String notificationType;
    private Timestamp createdAt;
    private Integer createdById;
    private User createdBy; // Optional: To hold user info

    public Notification() {
    }
     /**
     * PHƯƠNG THỨC MỚI: Tính toán thời gian tương đối từ lúc tạo thông báo đến hiện tại.
     * @return Chuỗi mô tả thời gian (ví dụ: "5 phút trước").
     */
    public String getRelativeTime() {
        if (createdAt == null) {
            return "";
        }
        
        Instant now = Instant.now();
        Instant then = createdAt.toInstant();
        Duration duration = Duration.between(then, now);

        if (duration.toMinutes() < 1) {
            return "Vừa xong";
        }
        if (duration.toMinutes() < 60) {
            return duration.toMinutes() + " phút trước";
        }
        if (duration.toHours() < 24) {
            return duration.toHours() + " giờ trước";
        }
        if (duration.toDays() < 30) {
            return duration.toDays() + " ngày trước";
        }
        if (duration.toDays() < 365) {
            return (duration.toDays() / 30) + " tháng trước";
        }
        return (duration.toDays() / 365) + " năm trước";
    }

    // --- Getters and Setters for all fields ---
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}