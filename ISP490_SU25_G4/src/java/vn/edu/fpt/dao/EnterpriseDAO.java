package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.CustomerType;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.EnterpriseContact;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;

/**
 * Lớp truy cập dữ liệu cho Enterprise (Đã được tái cấu trúc để đơn giản hóa,
 * giống UserDAO).
 */
public class EnterpriseDAO {

    /**
     * Constructor mặc định, không cần tham số.
     */
    public EnterpriseDAO() {
        // Constructor này giờ sẽ trống
    }

    public List<Enterprise> getAllEnterprises() throws SQLException {
        List<Enterprise> list = new ArrayList<>();
        String sql = "SELECT * FROM Enterprises WHERE is_deleted = 0 ORDER BY name";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Enterprise enterprise = new Enterprise();
                enterprise.setId(rs.getInt("id"));
                enterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                enterprise.setName(rs.getString("name"));
                enterprise.setTaxCode(rs.getString("tax_code"));
                enterprise.setHotline(rs.getString("hotline"));
                enterprise.setBankNumber(rs.getString("bank_number"));
                enterprise.setBusinessEmail(rs.getString("business_email"));
                enterprise.setCustomerTypeId(rs.getInt("customer_type_id"));
                enterprise.setAddressId(rs.getInt("address_id"));
                enterprise.setAvatarUrl(rs.getString("avatar_url"));
                list.add(enterprise);
            }
        }
        return list;
    }

    public boolean isNameExists(String name, Integer customerIdToExclude) throws Exception {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT 1 FROM Enterprises WHERE TRIM(LOWER(name)) = TRIM(LOWER(?)) AND is_deleted = 0";
        params.add(name);

        if (customerIdToExclude != null) {
            sql += " AND id != ?";
            params.add(customerIdToExclude);
        }
        sql += " LIMIT 1";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài để quản lý transaction
    public int insertEnterprise(Connection conn, String name, String businessEmail, String hotline, int customerTypeId, int addressId, String taxCode, String bankNumber, String avatarUrl) throws SQLException {
        String enterpriseCode = "KH-" + System.currentTimeMillis();
        String sql = "INSERT INTO Enterprises (enterprise_code, name, business_email, hotline, bank_number, tax_code, customer_type_id, address_id, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enterpriseCode);
            ps.setString(2, name);
            ps.setString(3, businessEmail);
            ps.setString(4, hotline);
            ps.setString(5, bankNumber);
            ps.setString(6, taxCode);
            ps.setInt(7, customerTypeId);
            ps.setInt(8, addressId);
            ps.setString(9, avatarUrl);
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

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public void insertEnterpriseContact(Connection conn, int enterpriseId, String fullName, String position, String phone, String email) throws SQLException {
        String sql = "INSERT INTO EnterpriseContacts (enterprise_id, full_name, position, phone_number, email, is_primary_contact) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setString(2, fullName);
            ps.setString(3, position);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setBoolean(6, true);
            ps.executeUpdate();
        }
    }

    public Enterprise getEnterpriseById(int enterpriseId) throws Exception {
        Enterprise enterprise = null;
        String sql = "SELECT "
                + "    e.*, "
                + "    a.province_id, a.district_id, a.ward_id, a.street_address, "
                + "    CONCAT_WS(', ', a.street_address, w.name, d.name, p.name) AS full_address, "
                + "    ct.name AS customer_type_name, "
                + "    (SELECT ec.phone_number FROM EnterpriseContacts ec WHERE ec.enterprise_id = e.id AND ec.is_primary_contact = 1 LIMIT 1) AS primary_phone, "
                + "    (SELECT ec.email FROM EnterpriseContacts ec WHERE ec.enterprise_id = e.id AND ec.is_primary_contact = 1 LIMIT 1) AS primary_email "
                + "FROM Enterprises e "
                + "LEFT JOIN CustomerTypes ct ON e.customer_type_id = ct.id "
                + "LEFT JOIN Addresses a ON e.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "WHERE e.id = ? AND e.is_deleted = 0";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    enterprise = new Enterprise();
                    enterprise.setId(rs.getInt("id"));
                    enterprise.setName(rs.getString("name"));
                    enterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                    enterprise.setTaxCode(rs.getString("tax_code"));
                    enterprise.setBankNumber(rs.getString("bank_number"));
                    enterprise.setAvatarUrl(rs.getString("avatar_url"));
                    enterprise.setCustomerTypeId(rs.getInt("customer_type_id"));
                    enterprise.setAddressId(rs.getInt("address_id"));
                    enterprise.setBusinessEmail(rs.getString("business_email"));
                    enterprise.setHotline(rs.getString("hotline"));
                    enterprise.setCreatedAt(rs.getTimestamp("created_at"));
                    enterprise.setCustomerTypeName(rs.getString("customer_type_name"));
                    enterprise.setFullAddress(rs.getString("full_address"));
                    enterprise.setProvinceId(rs.getInt("province_id"));
                    enterprise.setDistrictId(rs.getInt("district_id"));
                    enterprise.setWardId(rs.getInt("ward_id"));
                    enterprise.setStreetAddress(rs.getString("street_address"));

                    UserDAO userDAO = new UserDAO(); // Tự tạo UserDAO khi cần
                    enterprise.setContacts(getContactsByEnterpriseId(enterpriseId));
                    enterprise.setAssignedUsers(userDAO.getAssignedUsersForEnterprise(enterpriseId));
                }
            }
        }
        return enterprise;
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public boolean updateEnterprise(Connection conn, Enterprise enterprise) throws SQLException {
        String sql = "UPDATE Enterprises SET name = ?, business_email = ?, tax_code = ?, hotline = ?, bank_number = ?, customer_type_id = ?, avatar_url = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enterprise.getName());
            ps.setString(2, enterprise.getBusinessEmail());
            ps.setString(3, enterprise.getTaxCode());
            ps.setString(4, enterprise.getHotline());
            ps.setString(5, enterprise.getBankNumber());
            ps.setInt(6, enterprise.getCustomerTypeId());
            ps.setString(7, enterprise.getAvatarUrl());
            ps.setInt(8, enterprise.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean softDeleteEnterprise(int enterpriseId) throws Exception {
        String sql = "UPDATE Enterprises SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String> getCustomerNameSuggestions(String query) throws Exception {
        List<String> suggestions = new ArrayList<>();
        String sql = "SELECT name AS suggestion FROM Enterprises WHERE name LIKE ? AND is_deleted = 0 LIMIT 15";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("suggestion"));
                }
            }
        }
        return suggestions;
    }

    private void buildCustomerWhereClause(StringBuilder sql, List<Object> params, String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND e.name LIKE ? ");
            params.add("%" + searchQuery.trim() + "%");
        }
        if (customerTypeId != null && !customerTypeId.isEmpty()) {
            sql.append("AND e.customer_type_id = ? ");
            params.add(Integer.parseInt(customerTypeId));
        }
        if (employeeId != null && !employeeId.isEmpty()) {
            sql.append("AND e.id IN (SELECT ea.enterprise_id FROM EnterpriseAssignments ea WHERE ea.user_id = ?) ");
            params.add(Integer.parseInt(employeeId));
        }
        if (provinceId != null && !provinceId.isEmpty()) {
            sql.append("AND a.province_id = ? ");
            params.add(Integer.parseInt(provinceId));
        }
        if (districtId != null && !districtId.isEmpty()) {
            sql.append("AND a.district_id = ? ");
            params.add(Integer.parseInt(districtId));
        }
        if (wardId != null && !wardId.isEmpty()) {
            sql.append("AND a.ward_id = ? ");
            params.add(Integer.parseInt(wardId));
        }
    }

    public int countActiveEnterprises(String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT e.id) FROM Enterprises e LEFT JOIN Addresses a ON e.address_id = a.id WHERE e.is_deleted = 0 ");
        buildCustomerWhereClause(sql, params, searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Enterprise> getPaginatedActiveEnterprises(String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId, int page, int pageSize) throws Exception {
        List<Enterprise> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT e.id AS enterprise_id, e.name AS enterprise_name, e.enterprise_code, e.avatar_url, e.business_email, e.hotline, CONCAT_WS(', ', a.street_address, w.name, d.name, p.name) AS full_address, GROUP_CONCAT(DISTINCT CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) SEPARATOR ', ') AS assigned_users FROM Enterprises e LEFT JOIN Addresses a ON e.address_id = a.id LEFT JOIN Wards w ON a.ward_id = w.id LEFT JOIN Districts d ON a.district_id = d.id LEFT JOIN Provinces p ON a.province_id = p.id LEFT JOIN EnterpriseAssignments ea ON e.id = ea.enterprise_id LEFT JOIN Users u ON ea.user_id = u.id AND u.is_deleted = 0 WHERE e.is_deleted = 0 ");
        buildCustomerWhereClause(sql, params, searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);
        sql.append(" GROUP BY e.id ORDER BY e.name LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enterprise enterprise = new Enterprise();
                    enterprise.setId(rs.getInt("enterprise_id"));
                    enterprise.setName(rs.getString("enterprise_name"));
                    enterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                    enterprise.setHotline(rs.getString("hotline"));
                    enterprise.setBusinessEmail(rs.getString("business_email"));
                    enterprise.setFullAddress(rs.getString("full_address"));
                    enterprise.setAvatarUrl(rs.getString("avatar_url"));
                    User tempUser = new User();
                    tempUser.setFirstName(rs.getString("assigned_users"));
                    enterprise.setAssignedUsers(List.of(tempUser));
                    list.add(enterprise);
                }
            }
        }
        return list;
    }

    public List<Province> getAllProvinces() throws Exception {
        List<Province> provinces = new ArrayList<>();
        String sql = "SELECT id, name FROM Provinces ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                provinces.add(new Province(rs.getInt("id"), rs.getString("name")));
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
                    districts.add(new District(rs.getInt("id"), rs.getString("name")));
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
                    wards.add(new Ward(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return wards;
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
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

    // Giữ nguyên vì nhận Connection từ bên ngoài
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

    public List<CustomerType> getAllCustomerTypes() throws Exception {
        List<CustomerType> types = new ArrayList<>();
        String sql = "SELECT id, name FROM CustomerTypes ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                types.add(new CustomerType(rs.getInt("id"), rs.getString("name")));
            }
        }
        return types;
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public boolean primaryContactExists(Connection conn, int enterpriseId) throws SQLException {
        String sql = "SELECT 1 FROM EnterpriseContacts WHERE enterprise_id = ? AND is_primary_contact = 1 LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public void insertAssignment(Connection conn, int enterpriseId, int userId, String assignmentType) throws SQLException {
        String sql = "INSERT INTO EnterpriseAssignments (enterprise_id, user_id, assignment_type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setInt(2, userId);
            ps.setString(3, assignmentType);
            ps.executeUpdate();
        }
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public boolean updateMainAssignment(Connection conn, int enterpriseId, int newUserId) throws SQLException {
        String sql = "UPDATE EnterpriseAssignments SET user_id = ? WHERE enterprise_id = ? AND assignment_type = 'account_manager'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newUserId);
            ps.setInt(2, enterpriseId);
            return ps.executeUpdate() > 0;
        }
    }

    // Giữ nguyên vì nhận Connection từ bên ngoài
    public boolean updatePrimaryContact(Connection conn, int enterpriseId, String fullName, String position, String phone, String email) throws SQLException {
        String sql = "UPDATE EnterpriseContacts SET full_name = ?, position = ?, phone_number = ?, email = ? WHERE enterprise_id = ? AND is_primary_contact = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, position);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt(5, enterpriseId);
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

    public List<Enterprise> getAllActiveEnterprisesSimple() throws Exception {
        List<Enterprise> enterpriseList = new ArrayList<>();
        String sql = "SELECT id, name FROM Enterprises WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Enterprise enterprise = new Enterprise();
                enterprise.setId(rs.getInt("id"));
                enterprise.setName(rs.getString("name"));
                enterpriseList.add(enterprise);
            }
        }
        return enterpriseList;
    }
}
