package vn.edu.fpt.dao;

import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.model.Status;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static vn.edu.fpt.dao.DBContext.getConnection;

public class StatusDAO {

    /**
     * Lấy tất cả các trạng thái từ cơ sở dữ liệu.
     *
     * @return một danh sách (List) các đối tượng Status.
     */
    public List<Status> getAllStatuses() {
        List<Status> list = new ArrayList<>();
        String sql = "SELECT id, status_name FROM Statuses ORDER BY id ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Status status = new Status();
                status.setId(rs.getInt("id"));
                status.setStatusName(rs.getString("status_name"));
                list.add(status);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return list;
    }

    // Bạn có thể thêm các phương thức khác ở đây nếu cần, ví dụ:
    // public Status getStatusById(int id) { ... }
    public Status getStatusById(int statusId) {
        String sql = "SELECT * FROM Statuses WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Status status = new Status();
                    status.setId(rs.getInt("id"));
                    status.setStatusName(rs.getString("status_name"));
                    return status;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return null; // Trả về null nếu có lỗi hoặc không tìm thấy
    }

    /**
     * Lấy id theo status_name. Trả về null nếu không thấy. (Dùng connection sẵn
     * có)
     */
    public Integer getIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM Statuses WHERE status_name = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    /**
     * Overload: tự mở connection từ DBContext.
     */
    public Integer getIdByName(String name) {
        try (Connection conn = DBContext.getConnection()) {
            return getIdByName(name, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // --- ĐÃ THÊM: lấy Status theo tên, dùng kết nối bên ngoài (không đóng conn) ---
    public Status getStatusByName(String name, Connection conn) {
        if (name == null || conn == null) {
            return null;
        }

        String sql = "SELECT id, status_name "
                + "FROM Statuses "
                + "WHERE LOWER(TRIM(status_name)) = LOWER(TRIM(?))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Status s = new Status();
                    s.setId(rs.getInt("id"));
                    s.setStatusName(rs.getString("status_name"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
