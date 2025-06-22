/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import vn.edu.fpt.model.EnterpriseContact;

/**
 *
 * @author ducanh
 */
public class EnterpriseContactDAO {
    public boolean updateContact(Connection conn, EnterpriseContact contact) throws SQLException {
    // Chỉ cập nhật người liên hệ chính dựa trên enterprise_id
    String sql = "UPDATE EnterpriseContacts SET full_name = ?, position = ?, email = ?, phone_number = ? " +
                 "WHERE enterprise_id = ? AND is_primary_contact = 1";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, contact.getFullName());
        ps.setString(2, contact.getPosition());
        ps.setString(3, contact.getEmail());
        ps.setString(4, contact.getPhoneNumber());
        ps.setInt(5, contact.getEnterpriseId());
        return ps.executeUpdate() > 0;
    }
}
}
