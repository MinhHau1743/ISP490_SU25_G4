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
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Ward;

/**
 *
 * @author ducanh
 */
public class AddressDAO extends DBContext {

    public List<Province> getAllProvinces() throws Exception {
        List<Province> provinces = new ArrayList<>();
        String sql = "SELECT id, name FROM Provinces ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Province p = new Province();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                provinces.add(p);
            }
        }
        return provinces;
    }

    public List<District> getDistrictsByProvinceId(int provinceId) throws Exception {
        List<District> districts = new ArrayList<>();
        String sql = "SELECT id, name FROM Districts WHERE province_id = ? ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, provinceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    District d = new District();
                    d.setId(rs.getInt("id"));
                    d.setName(rs.getString("name"));
                    districts.add(d);
                }
            }
        }
        return districts;
    }

    public List<Ward> getWardsByDistrictId(int districtId) throws Exception {
        List<Ward> wards = new ArrayList<>();
        String sql = "SELECT id, name FROM Wards WHERE district_id = ? ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, districtId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ward w = new Ward();
                    w.setId(rs.getInt("id"));
                    w.setName(rs.getString("name"));
                    wards.add(w);
                }
            }
        }
        return wards;
    }

    // Phương thức này nhận Connection để có thể tham gia vào transaction
    public int insertAddress(Connection conn, String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String sql = "INSERT INTO Addresses (street_address, ward_id, district_id, province_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, streetAddress);
            ps.setInt(2, wardId);
            ps.setInt(3, districtId);
            ps.setInt(4, provinceId);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        }
    }
   

    /**
     * Updates an existing address in the database. This method should be part
     * of a transaction.
     *
     * @param conn The transaction's database connection.
     * @param addressId The ID of the address to update.
     * @param streetAddress The new street address.
     * @param wardId The new ward ID.
     * @param districtId The new district ID.
     * @param provinceId The new province ID.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    public boolean updateAddress(Connection conn, int addressId, String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String sql = "UPDATE Addresses SET street_address = ?, ward_id = ?, district_id = ?, province_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, streetAddress);
            ps.setInt(2, wardId);
            ps.setInt(3, districtId);
            ps.setInt(4, provinceId);
            ps.setInt(5, addressId);
            return ps.executeUpdate() > 0;
        }
    }
    public int findOrCreateAddress(String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        // Step 1: Try to find an existing address with the exact same details.
        String findSql = "SELECT id FROM Addresses WHERE " +
                         "street_address = ? AND ward_id = ? AND district_id = ? AND province_id = ?";
        
        try (Connection conn = getConnection(); // Use the inherited getConnection()
             PreparedStatement psFind = conn.prepareStatement(findSql)) {

            psFind.setString(1, streetAddress);
            psFind.setInt(2, wardId);
            psFind.setInt(3, districtId);
            psFind.setInt(4, provinceId);

            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    // Address found, return its ID.
                    return rs.getInt("id");
                } else {
                    // Step 2: If no address was found, create a new one.
                    return insertAddress(conn, streetAddress, wardId, districtId, provinceId);
                }
            }
        }
    }
}
