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
import vn.edu.fpt.model.CustomerType;
/**
 *
 * @author ducanh
 */
public class CustomerTypeDAO extends DBContext {
    /**
     * Retrieves all customer types from the database.
     * @return A list of CustomerType objects.
     */
    public List<CustomerType> getAllCustomerTypes() {
        List<CustomerType> list = new ArrayList<>();
        String sql = "SELECT id, name FROM CustomerTypes ORDER BY id";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CustomerType ct = new CustomerType();
                ct.setId(rs.getInt("id"));
                ct.setName(rs.getString("name"));
                list.add(ct);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customer types: " + e.getMessage());
        }
        return list;
    }
}
