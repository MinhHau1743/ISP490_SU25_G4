/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.EnterpriseContact;
import java.sql.ResultSet;

/**
 *
 * @author ducanh
 */
public class EnterpriseContactDAO {

    public boolean updateContact(Connection conn, EnterpriseContact contact) throws SQLException {
        // Chỉ cập nhật người liên hệ chính dựa trên enterprise_id
        String sql = "UPDATE EnterpriseContacts SET full_name = ?, position = ?, email = ?, phone_number = ? "
                + "WHERE enterprise_id = ? AND is_primary_contact = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contact.getFullName());
            ps.setString(2, contact.getPosition());
            ps.setString(3, contact.getEmail());
            ps.setString(4, contact.getPhoneNumber());
            ps.setInt(5, contact.getEnterpriseId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates the primary contact information for an enterprise.
     */
    public boolean updatePrimaryContact(Connection conn, int enterpriseId, String phone, String email) throws SQLException {
        String sql = "UPDATE EnterpriseContacts SET phone_number = ?, email = ? WHERE enterprise_id = ? AND is_primary_contact = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            ps.setInt(3, enterpriseId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<EnterpriseContact> getContactsByEnterpriseId(int enterpriseId) throws Exception {
        List<EnterpriseContact> contactList = new ArrayList<>();
        String sql = "SELECT * FROM EnterpriseContacts WHERE enterprise_id = ? ORDER BY is_primary_contact DESC, full_name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EnterpriseContact contact = new EnterpriseContact();
                    contact.setId(rs.getInt("id"));
                    contact.setEnterpriseId(rs.getInt("enterprise_id"));
                    contact.setFullName(rs.getString("full_name"));
                    contact.setPosition(rs.getString("position"));
                    contact.setEmail(rs.getString("email"));
                    contact.setPhoneNumber(rs.getString("phone_number"));
                    contact.setIsPrimaryContact(rs.getBoolean("is_primary_contact"));
                    contact.setNotes(rs.getString("notes"));
                    contactList.add(contact);
                }
            }
        }
        return contactList;
    }
}
