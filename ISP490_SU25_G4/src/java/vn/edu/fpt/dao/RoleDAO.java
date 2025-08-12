package vn.edu.fpt.dao;

import vn.edu.fpt.model.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO để quản lý các truy vấn liên quan đến bảng Roles.
 */
public class RoleDAO extends DBContext {

    /**
     * Lấy tất cả các vai trò từ cơ sở dữ liệu.
     * @return danh sách các đối tượng Role.
     */
    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String sql = "SELECT id, name FROM Roles ORDER BY id ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setName(rs.getString("name"));
                roleList.add(role);
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách vai trò.");
            e.printStackTrace();
        }
        return roleList;
    }
}