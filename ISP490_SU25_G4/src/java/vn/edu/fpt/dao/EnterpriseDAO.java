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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.EnterpriseContact;
import vn.edu.fpt.model.User;

/**
 *
 * @author ducanh
 */
public class EnterpriseDAO extends DBContext {

    // Phương thức này nhận Connection để có thể tham gia vào transaction
    public int insertEnterprise(Connection conn, String name, String businessEmail, String hotline, int customerTypeId, int addressId, String taxCode, String bankNumber, String avatarUrl) throws SQLException {
        // Tạo mã khách hàng duy nhất, ví dụ: KH-timestamp
        String enterpriseCode = "KH-" + System.currentTimeMillis();

        // Theo DB schema, fax và bank_number là NOT NULL, ta sẽ để giá trị tạm thời
        String sql = "INSERT INTO Enterprises (enterprise_code, name, business_email, fax, bank_number, tax_code, customer_type_id, address_id, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enterpriseCode);
            ps.setString(2, name);
            ps.setString(3, businessEmail);
            ps.setString(4, hotline); // Giá trị tạm
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

    // Tương tự, tạo các phương thức insert cho EnterpriseContacts và EnterpriseAssignments
    public void insertEnterpriseContact(Connection conn, int enterpriseId, String fullName, String position, String phone, String email) throws SQLException {
        String sql = "INSERT INTO EnterpriseContacts (enterprise_id, full_name, position, phone_number, email, is_primary_contact) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setString(2, fullName);
            ps.setString(3, position);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setBoolean(6, true); // Đánh dấu là người liên hệ chính
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

    public List<Enterprise> getAllActiveEnterprises(String searchQuery) throws Exception {
        Map<Integer, Enterprise> enterpriseMap = new HashMap<>();
        boolean isSearching = (searchQuery != null && !searchQuery.trim().isEmpty());

        // Base query to fetch all enterprise details
        StringBuilder sql = new StringBuilder(
                "SELECT "
                + "    e.id AS enterprise_id, e.name AS enterprise_name, e.enterprise_code, e.avatar_url AS enterprise_avatar, "
                + "    CONCAT_WS(', ', a.street_address, w.name, d.name, p.name) AS full_address, "
                + "    ct.name AS customer_type_name, "
                + "    u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.avatar_url AS user_avatar, "
                + "    (SELECT ec.phone_number FROM EnterpriseContacts ec WHERE ec.enterprise_id = e.id AND ec.is_primary_contact = 1 LIMIT 1) AS primary_phone "
                + "FROM Enterprises e "
                + "LEFT JOIN CustomerTypes ct ON e.customer_type_id = ct.id "
                + "LEFT JOIN Addresses a ON e.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "LEFT JOIN EnterpriseAssignments ea ON e.id = ea.enterprise_id "
                + "LEFT JOIN Users u ON ea.user_id = u.id AND u.is_deleted = 0 "
                + "WHERE e.is_deleted = 0 "
        );

        // Dynamically build the WHERE clause for searching to find matching enterprise IDs
        if (isSearching) {
            sql.append(" AND e.id IN ( ");
            sql.append("   SELECT id FROM Enterprises WHERE name LIKE ? ");
            sql.append("   UNION "); // UNION automatically handles duplicates
            sql.append("   SELECT enterprise_id FROM EnterpriseContacts WHERE full_name LIKE ? ");
            sql.append("   UNION ");
            sql.append("   SELECT ea.enterprise_id FROM EnterpriseAssignments ea JOIN Users u ON ea.user_id = u.id WHERE CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) LIKE ? ");
            sql.append(" ) ");
        }

        sql.append(" ORDER BY e.name, u.id");

        // The try-with-resources statement now only handles Connection and PreparedStatement
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set parameters ONLY if searching
            if (isSearching) {
                String searchPattern = "%" + searchQuery + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
            }

            // Execute the query and process the results in a separate try block
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int enterpriseId = rs.getInt("enterprise_id");
                    // Use computeIfAbsent for cleaner and more efficient code
                    Enterprise enterprise = enterpriseMap.computeIfAbsent(enterpriseId, id -> {
                        Enterprise newEnterprise = new Enterprise();
                        try {
                            newEnterprise.setId(rs.getInt("enterprise_id"));
                            newEnterprise.setName(rs.getString("enterprise_name"));
                            newEnterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                            newEnterprise.setCustomerTypeName(rs.getString("customer_type_name"));
                            newEnterprise.setFullAddress(rs.getString("full_address"));
                            newEnterprise.setPrimaryContactPhone(rs.getString("primary_phone"));
                            newEnterprise.setAvatarUrl(rs.getString("enterprise_avatar"));
                            newEnterprise.setAssignedUsers(new ArrayList<>());
                        } catch (SQLException e) {
                            throw new RuntimeException(e); // Propagate SQL exception
                        }
                        return newEnterprise;
                    });

                    // If an assigned user exists for this row, add them to the list
                    if (rs.getObject("user_id") != null) {
                        User assignee = new User();
                        assignee.setId(rs.getInt("user_id"));
                        assignee.setFirstName(rs.getString("first_name"));
                        assignee.setLastName(rs.getString("last_name"));
                        assignee.setMiddleName(rs.getString("middle_name"));
                        assignee.setAvatarUrl(rs.getString("user_avatar"));

                        // Prevent adding duplicate users if an enterprise has multiple assignments
                        if (enterprise.getAssignedUsers().stream().noneMatch(u -> u.getId() == assignee.getId())) {
                            enterprise.getAssignedUsers().add(assignee);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(enterpriseMap.values());
    }

    /**
     * Retrieves a single enterprise by its ID, now including the avatar URL.
     *
     * @param enterpriseId The ID of the enterprise to retrieve.
     * @return An Enterprise object populated with details, or null if not
     * found.
     * @throws Exception if a database access error occurs.
     */
    public Enterprise getEnterpriseById(int enterpriseId) throws Exception {
        Enterprise enterprise = null;
        // === SQL UPDATED to select the raw ID fields from the Addresses table ===
        String sql = "SELECT "
                + "    e.*, " // Select all from Enterprises
                + "    a.province_id, a.district_id, a.ward_id, a.street_address, " // Select address IDs and street
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
                    // Set all fields from Enterprises table
                    enterprise.setId(rs.getInt("id"));
                    enterprise.setName(rs.getString("name"));
                    enterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                    enterprise.setTaxCode(rs.getString("tax_code"));
                    enterprise.setBankNumber(rs.getString("bank_number"));
                    enterprise.setAvatarUrl(rs.getString("avatar_url"));
                    enterprise.setCustomerTypeId(rs.getInt("customer_type_id"));
                    enterprise.setAddressId(rs.getInt("address_id"));

                    enterprise.setBusinessEmail(rs.getString("business_email"));
                    enterprise.setFax(rs.getString("fax"));

                    // === DÒNG QUAN TRỌNG BỊ THIẾU ===
                    enterprise.setCreatedAt(rs.getTimestamp("created_at"));

                    // Set joined fields
                    enterprise.setCustomerTypeName(rs.getString("customer_type_name"));
                    enterprise.setFullAddress(rs.getString("full_address"));

                    // === SET THE MISSING FIELDS FOR THE EDIT FORM ===
                    enterprise.setProvinceId(rs.getInt("province_id"));
                    enterprise.setDistrictId(rs.getInt("district_id"));
                    enterprise.setWardId(rs.getInt("ward_id"));
                    enterprise.setStreetAddress(rs.getString("street_address"));

                    // Fetch related data using other DAOs
                    enterprise.setContacts(new EnterpriseContactDAO().getContactsByEnterpriseId(enterpriseId));
                    enterprise.setAssignedUsers(new UserDAO().getAssignedUsersForEnterprise(enterpriseId));
                }
            }
        }
        return enterprise;
    }

    // Helper method to get assigned users
    private List<User> getAssignedUsersForEnterprise(Connection conn, int enterpriseId) throws Exception {
        List<User> assignedUsers = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u JOIN EnterpriseAssignments ea ON u.id = ea.user_id WHERE ea.enterprise_id = ? AND u.is_deleted = 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    assignedUsers.add(user);
                }
            }
        }
        return assignedUsers;
    }

    public boolean updateEnterprise(Connection conn, Enterprise enterprise) throws SQLException {
        String sql = "UPDATE Enterprises SET name = ?, business_email = ?, tax_code = ?, fax = ?, bank_number = ?, customer_type_id = ?, avatar_url = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enterprise.getName());
            ps.setString(2, enterprise.getBusinessEmail());
            ps.setString(3, enterprise.getTaxCode());
            ps.setString(4, enterprise.getFax());
            ps.setString(5, enterprise.getBankNumber());
            ps.setInt(6, enterprise.getCustomerTypeId());
            ps.setString(7, enterprise.getAvatarUrl()); // Thêm avatar_url
            ps.setInt(8, enterprise.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean softDeleteEnterprise(int enterpriseId) throws Exception {
        String sql = "UPDATE Enterprises SET is_deleted = 1 WHERE id = ?";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);

            // executeUpdate() returns the number of rows affected.
            // If it's greater than 0, the update was successful.
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a list of customer names for search suggestions.
     *
     * @param query The partial name to search for.
     * @return A list of up to 10 matching customer names.
     * @throws Exception
     */
    public List<String> getCustomerNameSuggestions(String query) throws Exception {
        List<String> suggestions = new ArrayList<>();
        // Lấy tối đa 10 kết quả phù hợp nhất
        String sql = "SELECT name FROM Enterprises WHERE name LIKE ? AND is_deleted = 0 LIMIT 10";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + query + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("name"));
                }
            }
        }
        return suggestions;
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
