package vn.edu.fpt.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Model cho Lịch trình bảo trì/công việc. Phiên bản đã được dọn dẹp và sửa lỗi
 * đồng bộ dữ liệu địa chỉ.
 *
 * @author phamh
 */
public class MaintenanceSchedule {

    private int id;
    private Integer technicalRequestId;
    private Integer campaignId;
    private String color;
    private LocalDate scheduledDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer statusId;
    private String statusName; // Dùng để hiển thị, không có trong CSDL
    private Integer assignedUserId; // Dùng để hiển thị, không có trong CSDL

    // =====================================================================
    // THAY ĐỔI QUAN TRỌNG: Chỉ giữ lại 2 trường address và addressId
    // và thêm logic để chúng luôn đồng bộ với nhau.
    // =====================================================================
    private Integer addressId;
    private Address address;

    public MaintenanceSchedule() {
    }

    // ----- CÁC GETTER/SETTER KHÁC GIỮ NGUYÊN -----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTechnicalRequestId() {
        return technicalRequestId;
    }

    public void setTechnicalRequestId(Integer technicalRequestId) {
        this.technicalRequestId = technicalRequestId;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public Integer getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Integer assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    // =====================================================================
    // CÁC GETTER/SETTER ĐỊA CHỈ ĐÃ ĐƯỢC SỬA LẠI
    // =====================================================================
    public Address getAddress() {
        return address;
    }

    /**
     * Khi set một đối tượng Address, đồng thời cập nhật luôn cả addressId. Đây
     * là phương thức chính để gán địa chỉ.
     */
    public void setAddress(Address address) {
        this.address = address;
        if (address != null && address.getId() > 0) {
            this.addressId = address.getId();
        } else {
            this.addressId = null;
        }
    }

    /**
     * Getter này sẽ ưu tiên lấy ID từ đối tượng Address (nếu có) để đảm bảo
     * luôn trả về ID đúng nhất.
     */
    public Integer getAddressId() {
        if (this.address != null && this.address.getId() > 0) {
            return this.address.getId();
        }
        return this.addressId;
    }

    /**
     * Setter này vẫn được giữ lại để tương thích với code cũ, nó sẽ xóa đối
     * tượng address để tránh gây nhiễu.
     */
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
        // Khi chỉ set ID, ta không chắc đối tượng address có còn khớp không,
        // nên tốt nhất là xóa nó đi.
        if (this.address != null && this.address.getId() != addressId) {
            this.address = null;
        }
    }
}
