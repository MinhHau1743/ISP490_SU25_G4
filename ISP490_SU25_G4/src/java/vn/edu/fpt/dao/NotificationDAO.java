package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Notification;
import vn.edu.fpt.model.User;

/**
 * Lớp DAO (Data Access Object) cho việc tương tác với bảng 'notifications'
 * trong cơ sở dữ liệu. Cung cấp các phương thức CRUD (Tạo, Đọc, Cập nhật, Xóa)
 * cho thông báo.
 */
public class NotificationDAO extends DBContext {

    /**
     * Chèn một thông báo mới vào cơ sở dữ liệu.
     *
     * @param notification Đối tượng Notification chứa thông tin cần thêm.
     */
    public void addNotification(Notification notification) {
        String sql = "INSERT INTO notifications (title, message, link_url, notification_type, created_by_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notification.getTitle());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getLinkUrl());
            ps.setString(4, notification.getNotificationType());

            if (notification.getCreatedById() != null) {
                ps.setInt(5, notification.getCreatedById());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách các thông báo gần đây nhất, được sắp xếp theo thời gian
     * tạo.
     *
     * @param limit Số lượng thông báo tối đa cần lấy.
     * @return Một danh sách các đối tượng Notification.
     */
    public List<Notification> getLatestNotifications(int limit) {
        List<Notification> notifications = new ArrayList<>();
        // Câu lệnh SQL JOIN với bảng Users để lấy thông tin người tạo
        String sql = "SELECT n.id, n.title, n.message, n.link_url, n.notification_type, n.created_at, "
                + "u.id as user_id, u.first_name, u.last_name, u.avatar_url "
                + "FROM notifications n "
                + "LEFT JOIN Users u ON n.created_by_id = u.id "
                + "ORDER BY n.created_at DESC "
                + "LIMIT ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching latest notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }

    /**
     * Đếm tổng số lượng thông báo có trong hệ thống.
     *
     * @return Tổng số thông báo.
     */
    public int getTotalNotificationCount() {
        String sql = "SELECT COUNT(*) FROM notifications";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Hàm tiện ích để ánh xạ một hàng kết quả từ ResultSet sang đối tượng
     * Notification.
     *
     * @param rs ResultSet đang trỏ đến một hàng dữ liệu.
     * @return Đối tượng Notification được tạo từ dữ liệu.
     * @throws SQLException nếu có lỗi khi truy cập dữ liệu trong ResultSet.
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setLinkUrl(rs.getString("link_url"));
        notification.setNotificationType(rs.getString("notification_type"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));

        // Kiểm tra xem có thông tin người tạo hay không (có thể là NULL)
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            User creator = new User();
            creator.setId(userId);
            creator.setFirstName(rs.getString("first_name"));
            creator.setLastName(rs.getString("last_name"));
            creator.setAvatarUrl(rs.getString("avatar_url"));
            notification.setCreatedBy(creator);
            notification.setCreatedById(userId);
        }

        return notification;
    }

    // Thêm phương thức này vào bên trong lớp NotificationDAO
    /**
     * Lấy danh sách thông báo có phân trang.
     *
     * @param page Số trang hiện tại (bắt đầu từ 1).
     * @param pageSize Số lượng thông báo trên mỗi trang.
     * @return Một danh sách các đối tượng Notification.
     */
    public List<Notification> getPaginatedNotifications(int page, int pageSize) {
        List<Notification> notifications = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sql = "SELECT n.id, n.title, n.message, n.link_url, n.notification_type, n.created_at, "
                + "u.id as user_id, u.first_name, u.last_name, u.avatar_url "
                + "FROM notifications n "
                + "LEFT JOIN Users u ON n.created_by_id = u.id "
                + "ORDER BY n.created_at DESC "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Giả sử bạn đã có hàm mapResultSetToNotification từ trước
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paginated notifications: " + e.getMessage());
            e.printStackTrace();
        }
        return notifications;
    }
}
