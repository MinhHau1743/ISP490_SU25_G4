/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.EnterpriseContact;

/**
 *
 * @author ducanh
 */
public class EnterpriseDAO extends DBContext {

    // Phương thức này nhận Connection để có thể tham gia vào transaction
    public int insertEnterprise(Connection conn, String name, int customerTypeId, int addressId) throws SQLException {
        // Tạo mã khách hàng duy nhất, ví dụ: KH-timestamp
        String enterpriseCode = "KH-" + System.currentTimeMillis();

        // Theo DB schema, fax và bank_number là NOT NULL, ta sẽ để giá trị tạm thời
        String sql = "INSERT INTO Enterprises (enterprise_code, name, fax, bank_number, customer_type_id, address_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enterpriseCode);
            ps.setString(2, name);
            ps.setString(3, "N/A"); // Giá trị tạm
            ps.setString(4, "N/A"); // Giá trị tạm
            ps.setInt(5, customerTypeId);
            ps.setInt(6, addressId);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating enterprise failed, no ID obtained.");
                }
            }
        }
    }

    // Tương tự, tạo các phương thức insert cho EnterpriseContacts và EnterpriseAssignments
    public void insertEnterpriseContact(Connection conn, int enterpriseId, String fullName, String phone, String email) throws SQLException {
        String sql = "INSERT INTO EnterpriseContacts (enterprise_id, full_name, phone_number, email, is_primary_contact) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setString(2, fullName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setBoolean(5, true); // Đánh dấu là người liên hệ chính
            ps.executeUpdate();
        }
    }

    public void insertEnterpriseAssignment(Connection conn, int enterpriseId, int userId) throws SQLException {
        String sql = "INSERT INTO EnterpriseAssignments (enterprise_id, user_id, assignment_type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setInt(2, userId);
            ps.setString(3, "account_manager"); // Gán vai trò mặc định
            ps.executeUpdate();
        }
    }
}
