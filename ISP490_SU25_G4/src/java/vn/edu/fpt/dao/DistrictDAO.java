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
import vn.edu.fpt.model.District;

/**
 *
 * @author ducanh
 */
public class DistrictDAO extends DBContext {

    public List<District> getDistrictsByProvinceId(int provinceId) {
        List<District> list = new ArrayList<>();
        String sql = "SELECT id, name FROM Districts WHERE province_id = ? ORDER BY name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, provinceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    District d = new District();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
