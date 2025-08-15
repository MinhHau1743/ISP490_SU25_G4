package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MaintenanceAssignmentDAO {

    /**
     * Gán một nhân viên cho một lịch trình bảo trì. Phương thức này được thiết
     * kế để chạy bên trong một transaction do servlet quản lý.
     *
     * @param scheduleId ID của lịch trình.
     * @param userId ID của nhân viên được gán.
     * @param conn Đối tượng Connection được truyền từ servlet để thực hiện giao
     * dịch.
     * @return true nếu gán thành công.
     * @throws SQLException nếu có lỗi SQL xảy ra, để servlet có thể rollback.
     */
    public boolean addAssignment(int scheduleId, int userId, Connection conn) throws SQLException {
        String sql = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        // Không dùng try-with-resources cho Connection ở đây vì nó được quản lý bởi servlet
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setInt(2, userId);

            int affectedRows = ps.executeUpdate();

            // Nếu có 1 dòng được thêm vào thì trả về true
            return affectedRows > 0;
        }
        // Nếu có lỗi, SQLException sẽ được ném ra và được xử lý ở servlet
    }

    // Bạn có thể thêm các phương thức khác ở đây nếu cần, ví dụ:
    // public List<User> getUsersByScheduleId(int scheduleId) { ... }
    // public boolean removeAssignment(int scheduleId, int userId) { ... }
}
