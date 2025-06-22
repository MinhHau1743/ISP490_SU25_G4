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

    public List<CustomerType> getAllCustomerTypes() throws Exception {
        List<CustomerType> types = new ArrayList<>();
        String sql = "SELECT id, name FROM CustomerTypes ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                CustomerType ct = new CustomerType();
                ct.setId(rs.getInt("id"));
                ct.setName(rs.getString("name"));
                types.add(ct);
            }
        }
        return types;
    }
}
