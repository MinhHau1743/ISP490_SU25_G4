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

    public int insertEnterprise(Enterprise enterprise) {
        // Giả sử các cột fax, bank_number, tax_code đã được set là NULLABLE trong DB
        String sql = "INSERT INTO Enterprises (enterprise_code, name, industry_id, customer_type_id, area_id, address_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, enterprise.getEnterpriseCode());
            pstmt.setString(2, enterprise.getName());
            pstmt.setInt(3, 1); // industry_id (mặc định)
            pstmt.setInt(4, enterprise.getCustomerTypeId());
            pstmt.setInt(5, 1); // area_id (mặc định)
            pstmt.setInt(6, enterprise.getAddressId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public void insertEnterpriseContact(EnterpriseContact contact) {
        String sql = "INSERT INTO EnterpriseContacts (enterprise_id, full_name, position, email, phone_number, is_primary_contact) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, contact.getEnterpriseId());
            pstmt.setString(2, contact.getFullName());
            pstmt.setString(3, "Người liên hệ chính");
            pstmt.setString(4, contact.getEmail());
            pstmt.setString(5, contact.getPhoneNumber());
            pstmt.setBoolean(6, true);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
