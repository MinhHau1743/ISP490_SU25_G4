/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.User;

/**
 *
 * @author ducanh
 */
public class EnterpriseAssignmentDAO {

    /**
     * Gán một nhân viên phụ trách cho một doanh nghiệp. Phương thức này nhận
     * một đối tượng Connection để có thể tham gia vào một transaction.
     *
     * @param conn Connection từ transaction
     * @param enterpriseId ID của doanh nghiệp
     * @param userId ID của nhân viên được gán
     * @param assignmentType Vai trò phụ trách (ví dụ: 'account_manager',
     * 'sales_lead')
     * @throws SQLException nếu có lỗi khi thực thi câu lệnh SQL
     */
    public void insertAssignment(Connection conn, int enterpriseId, int userId, String assignmentType) throws SQLException {
        // Câu lệnh SQL để chèn một bản ghi phân công mới
        String sql = "INSERT INTO EnterpriseAssignments (enterprise_id, user_id, assignment_type) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setInt(2, userId);
            ps.setString(3, assignmentType);
            ps.executeUpdate();
        }
    }

    /**
     * Lấy danh sách tất cả nhân viên được gán cho một doanh nghiệp. Hàm này sẽ
     * hữu ích cho trang xem chi tiết khách hàng.
     *
     * @param enterpriseId ID của doanh nghiệp
     * @return Danh sách các đối tượng User được gán
     * @throws Exception nếu có lỗi kết nối hoặc thực thi
     */
    public List<User> getAssignedUsersForEnterprise(int enterpriseId) throws Exception {
        List<User> assignedUsers = new ArrayList<>();
        // JOIN 3 bảng để lấy thông tin chi tiết của nhân viên được gán
        String sql = "SELECT u.*, ea.assignment_type "
                + "FROM Users u "
                + "JOIN EnterpriseAssignments ea ON u.id = ea.user_id "
                + "WHERE ea.enterprise_id = ? AND u.is_deleted = 0";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);

            try (ResultSet rs = ps.executeQuery()) {
                // Ta cần một hàm để tạo đối tượng User từ ResultSet trong UserDAO
                // Tạm thời, ta sẽ làm trực tiếp ở đây
                while (rs.next()) {
                    User user = new User(); // Giả sử UserDAO có hàm extractUserFromResultSet
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setEmail(rs.getString("email"));
                    // Thêm thuộc tính assignment_type vào một trường tạm nào đó nếu cần
                    // user.setTempAssignmentType(rs.getString("assignment_type"));
                    assignedUsers.add(user);
                }
            }
        }
        return assignedUsers;
    }
}
