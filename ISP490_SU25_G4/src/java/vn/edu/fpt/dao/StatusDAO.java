package vn.edu.fpt.dao;

import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.model.Status;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
