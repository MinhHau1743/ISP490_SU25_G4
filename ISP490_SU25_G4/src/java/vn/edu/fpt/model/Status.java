package vn.edu.fpt.model;

/**
 * Lớp Model này đại diện cho một trạng thái trong hệ thống. Tương ứng với bảng
 * 'Status' trong cơ sở dữ liệu.
 */
public class Status {

    private int id;
    private String statusName;

    /**
     * Constructor mặc định.
     */
    public Status() {
    }

    /**
     * Constructor đầy đủ tham số.
     *
     * @param id Mã định danh duy nhất của trạng thái.
     * @param statusName Tên của trạng thái (ví dụ: "Đang thực hiện", "Hoàn
     * thành").
     */
    public Status(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    /**
     * Phương thức toString() để dễ dàng gỡ lỗi.
     *
     * @return Chuỗi đại diện cho đối tượng Status.
     */
    @Override
    public String toString() {
        return "Status{"
                + "id=" + id
                + ", statusName='" + statusName + '\''
                + '}';
    }
}
