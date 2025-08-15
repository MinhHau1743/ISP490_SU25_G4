package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Ward;
import vn.edu.fpt.model.Address;

/**
 * Lớp DAO quản lý các truy vấn liên quan đến địa chỉ. Phiên bản này đã được sửa
 * lại để nhất quán trong việc quản lý Connection.
 *
 * @author ducanh (updated by AI)
 */
public class AddressDAO extends DBContext {

    public List<Province> getAllProvinces() throws Exception {
        List<Province> provinces = new ArrayList<>();
        String sql = "SELECT id, name FROM Provinces ORDER BY name";
        // ## FIX: Sử dụng getConnection() được kế thừa thay vì tạo đối tượng mới ##
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    // ## FIX: Phương thức này giờ sẽ tự quản lý Connection ##
    public int insertAddress(String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String sql = "INSERT INTO Addresses (street_address, ward_id, district_id, province_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    // ## FIX: Phương thức này giờ sẽ tự quản lý Connection ##
    public boolean updateAddress(int addressId, String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String sql = "UPDATE Addresses SET street_address = ?, ward_id = ?, district_id = ?, province_id = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, streetAddress);
            ps.setInt(2, wardId);
            ps.setInt(3, districtId);
            ps.setInt(4, provinceId);
            ps.setInt(5, addressId);
            return ps.executeUpdate() > 0;
        }
    }

    // ## FIX: Sửa lại để gọi đúng phương thức insertAddress mới ##
    public int findOrCreateAddress(String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String findSql = "SELECT id FROM Addresses WHERE "
                + "street_address = ? AND ward_id = ? AND district_id = ? AND province_id = ?";

        try (Connection conn = getConnection(); PreparedStatement psFind = conn.prepareStatement(findSql)) {

            psFind.setString(1, streetAddress);
            psFind.setInt(2, wardId);
            psFind.setInt(3, districtId);
            psFind.setInt(4, provinceId);

            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    // Địa chỉ đã tồn tại, trả về ID của nó
                    return rs.getInt("id");
                } else {
                    // Nếu không tìm thấy, tạo mới bằng phương thức đã được sửa
                    return insertAddress(streetAddress, wardId, districtId, provinceId);
                }
            }
        }
    }
    
    // =========================================================================
// MỚI: Thêm 2 phương thức NẠP CHỒNG để hỗ trợ transaction
// =========================================================================
    /**
     * Phiên bản NẠP CHỒNG của findOrCreateAddress, nhận vào Connection. Chỉ
     * dùng cho các servlet cần transaction.
     */
    public int findOrCreateAddress(Address address, Connection conn) throws SQLException {
        String findSql = "SELECT id FROM Addresses WHERE province_id = ? AND district_id = ? AND ward_id = ? AND street_address = ?";

        try (PreparedStatement psFind = conn.prepareStatement(findSql)) {
            psFind.setInt(1, address.getProvinceId());
            psFind.setInt(2, address.getDistrictId());
            psFind.setInt(3, address.getWardId());
            psFind.setString(4, address.getStreetAddress());

            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    // Gọi đến phiên bản insertAddress hỗ trợ transaction
                    return insertAddress(address, conn);
                }
            }
        }
    }

    /**
     * Phiên bản NẠP CHỒNG của insertAddress, nhận vào Connection.
     */
    public int insertAddress(Address address, Connection conn) throws SQLException {
        String sql = "INSERT INTO Addresses (street_address, ward_id, district_id, province_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, address.getStreetAddress());
            ps.setInt(2, address.getWardId());
            ps.setInt(3, address.getDistrictId());
            ps.setInt(4, address.getProvinceId());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Tạo địa chỉ thất bại, không nhận được ID.");
                }
            }
        }
    }
}
