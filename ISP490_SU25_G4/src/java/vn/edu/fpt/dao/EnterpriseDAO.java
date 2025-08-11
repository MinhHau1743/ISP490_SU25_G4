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
import vn.edu.fpt.model.CustomerType;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.EnterpriseContact;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;

/**
 *
 * @author ducanh
 */
public class EnterpriseDAO extends DBContext {

    // Phương thức này nhận Connection để có thể tham gia vào transaction
    public int insertEnterprise(Connection conn, String name, String businessEmail, String hotline, int customerTypeId, int addressId, String taxCode, String bankNumber, String avatarUrl) throws SQLException {
        // Tạo mã khách hàng duy nhất, ví dụ: KH-timestamp
        String enterpriseCode = "KH-" + System.currentTimeMillis();

        // ĐÃ SỬA: Bỏ cột `fax` và thay bằng `hotline` trong câu lệnh SQL
        String sql = "INSERT INTO Enterprises (enterprise_code, name, business_email, hotline, bank_number, tax_code, customer_type_id, address_id, avatar_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, enterpriseCode);
            ps.setString(2, name);
            ps.setString(3, businessEmail);
            ps.setString(4, hotline); // ĐÃ SỬA: Gán giá trị cho cột hotline
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

    public List<Enterprise> getAllActiveEnterprises(String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId) throws Exception {
        Map<Integer, Enterprise> enterpriseMap = new HashMap<>();
        List<Object> params = new ArrayList<>();

        // ĐÃ SỬA: Thay e.fax bằng e.hotline trong danh sách cột SELECT
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT e.id AS enterprise_id, e.name AS enterprise_name, e.enterprise_code, e.avatar_url AS enterprise_avatar, e.hotline, "
                + "CONCAT_WS(', ', a.street_address, w.name, d.name, p.name) AS full_address, "
                + "ct.name AS customer_type_name, "
                + "u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.avatar_url AS user_avatar "
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

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // CHỈ TÌM KIẾM THEO TÊN DOANH NGHIỆP
            sql.append("AND e.name LIKE ? ");
            String searchPattern = "%" + searchQuery + "%";
            params.add(searchPattern);
        }

        if (customerTypeId != null && !customerTypeId.isEmpty()) {
            sql.append("AND e.customer_type_id = ? ");
            params.add(Integer.parseInt(customerTypeId));
        }

        if (employeeId != null && !employeeId.isEmpty()) {
            sql.append("AND ea.user_id = ? ");
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

        sql.append(" ORDER BY e.name, u.id");

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int enterpriseId = rs.getInt("enterprise_id");
                    Enterprise enterprise = enterpriseMap.computeIfAbsent(enterpriseId, id -> {
                        Enterprise newEnterprise = new Enterprise();
                        try {
                            newEnterprise.setId(rs.getInt("enterprise_id"));
                            newEnterprise.setName(rs.getString("enterprise_name"));
                            newEnterprise.setEnterpriseCode(rs.getString("enterprise_code"));
                            newEnterprise.setHotline(rs.getString("hotline")); // ĐÃ SỬA: Dùng setHotline()
                            newEnterprise.setCustomerTypeName(rs.getString("customer_type_name"));
                            newEnterprise.setFullAddress(rs.getString("full_address"));
                            newEnterprise.setAvatarUrl(rs.getString("enterprise_avatar"));
                            newEnterprise.setAssignedUsers(new ArrayList<>());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        return newEnterprise;
                    });

                    if (rs.getObject("user_id") != null) {
                        User assignee = new User();
                        assignee.setId(rs.getInt("user_id"));
                        assignee.setFirstName(rs.getString("first_name"));
                        assignee.setLastName(rs.getString("last_name"));
                        assignee.setMiddleName(rs.getString("middle_name"));
                        assignee.setAvatarUrl(rs.getString("user_avatar"));

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
                    enterprise.setHotline(rs.getString("hotline")); // ĐÃ SỬA: Đọc từ cột hotline

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
                    enterprise.setContacts(getContactsByEnterpriseId(enterpriseId));
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
        // ĐÃ SỬA: Cập nhật cột hotline thay vì fax
        String sql = "UPDATE Enterprises SET name = ?, business_email = ?, tax_code = ?, hotline = ?, bank_number = ?, customer_type_id = ?, avatar_url = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, enterprise.getName());
            ps.setString(2, enterprise.getBusinessEmail());
            ps.setString(3, enterprise.getTaxCode());
            ps.setString(4, enterprise.getHotline()); // ĐÃ SỬA: Lấy giá trị từ getHotline()
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
     * Lấy danh sách gợi ý tìm kiếm từ nhiều trường: tên doanh nghiệp, fax, tên
     * nhân viên, và địa chỉ. Sử dụng UNION để kết hợp kết quả từ các nguồn khác
     * nhau.
     *
     * @param query Chuỗi ký tự người dùng nhập vào.
     * @return Một danh sách các chuỗi gợi ý duy nhất.
     * @throws Exception
     */
    public List<String> getCustomerNameSuggestions(String query) throws Exception {
        List<String> suggestions = new ArrayList<>();

        String sql = "SELECT name AS suggestion FROM Enterprises WHERE name LIKE ? AND is_deleted = 0 LIMIT 15";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern); // Chỉ cần set 1 tham số

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suggestions.add(rs.getString("suggestion"));
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

    /**
     * Helper method to build dynamic WHERE clause for customer filtering.
     */
    private void buildCustomerWhereClause(StringBuilder sql, List<Object> params, String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // CHỈ TÌM KIẾM THEO TÊN DOANH NGHIỆP
            sql.append("AND e.name LIKE ? ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
        }
        if (customerTypeId != null && !customerTypeId.isEmpty()) {
            sql.append("AND e.customer_type_id = ? ");
            params.add(Integer.parseInt(customerTypeId));
        }
        if (employeeId != null && !employeeId.isEmpty()) {
            // This requires a subquery or a join to check assignments
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

    /**
     * Counts the total number of active enterprises based on filter criteria.
     */
    public int countActiveEnterprises(String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId) throws Exception {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT e.id) "
                + "FROM Enterprises e "
                + "LEFT JOIN Addresses a ON e.address_id = a.id "
                + "WHERE e.is_deleted = 0 ");

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

    /**
     * Retrieves a paginated list of active enterprises based on filter
     * criteria.
     */
    public List<Enterprise> getPaginatedActiveEnterprises(String searchQuery, String customerTypeId, String employeeId, String provinceId, String districtId, String wardId, int page, int pageSize) throws Exception {
        Map<Integer, Enterprise> enterpriseMap = new HashMap<>();
        List<Object> params = new ArrayList<>();

        // ĐÃ SỬA: Thay e.fax bằng e.hotline
        StringBuilder sql = new StringBuilder(
                "SELECT e.id AS enterprise_id, e.name AS enterprise_name, e.enterprise_code, e.avatar_url, e.business_email, e.hotline, "
                + "CONCAT_WS(', ', a.street_address, w.name, d.name, p.name) AS full_address, "
                + "GROUP_CONCAT(DISTINCT CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) SEPARATOR ', ') AS assigned_users "
                + "FROM Enterprises e "
                + "LEFT JOIN Addresses a ON e.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "LEFT JOIN EnterpriseAssignments ea ON e.id = ea.enterprise_id "
                + "LEFT JOIN Users u ON ea.user_id = u.id AND u.is_deleted = 0 "
                + "WHERE e.is_deleted = 0 "
        );

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
                    enterprise.setHotline(rs.getString("hotline")); // ĐÃ SỬA: Dùng hotline thay vì fax
                    enterprise.setBusinessEmail(rs.getString("business_email"));
                    enterprise.setFullAddress(rs.getString("full_address"));
                    enterprise.setAvatarUrl(rs.getString("avatar_url"));

                    // Gán danh sách nhân viên phụ trách (tạm thời dưới dạng String)
                    User tempUser = new User();
                    tempUser.setFirstName(rs.getString("assigned_users"));
                    enterprise.setAssignedUsers(List.of(tempUser));

                    enterpriseMap.put(enterprise.getId(), enterprise);
                }
            }
        }
        return new ArrayList<>(enterpriseMap.values());
    }

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

    /**
     * Kiểm tra xem một doanh nghiệp đã có người liên hệ chính hay chưa.
     *
     * @param conn Connection để tham gia vào transaction
     * @param enterpriseId ID của doanh nghiệp
     * @return true nếu đã có, false nếu chưa có
     * @throws SQLException
     */
    public boolean primaryContactExists(Connection conn, int enterpriseId) throws SQLException {
        // Câu lệnh SQL chỉ đơn giản là đếm xem có dòng nào khớp điều kiện không.
        // LIMIT 1 giúp tối ưu, dừng tìm kiếm ngay khi thấy kết quả đầu tiên.
        String sql = "SELECT 1 FROM EnterpriseContacts WHERE enterprise_id = ? AND is_primary_contact = 1 LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Gán ID của doanh nghiệp vào câu lệnh
            ps.setInt(1, enterpriseId);

            try (ResultSet rs = ps.executeQuery()) {
                // rs.next() sẽ trả về true nếu tìm thấy ít nhất một bản ghi (tức là đã tồn tại).
                // Ngược lại, nó sẽ trả về false.
                return rs.next();
            }
        }
    }

    /**
     * Gán một nhân viên phụ trách cho một doanh nghiệp. Phương thức này nhận
     * một đối tượng Connection để có thể tham gia vào một transaction.
     *
     * @param conn Connection từ transaction
     * @param enterpriseId ID của doanh nghiệp
     * @param userId ID của nhân viên được gán
     * @param assignmentType Vai trò phụ trách (ví dụ: 'account_manager',
     * 'sales_lead')
     * @throws SQLException nếu có lỗi khi thực thi câu lệnh SQL
     */
    public void insertAssignment(Connection conn, int enterpriseId, int userId, String assignmentType) throws SQLException {
        // Câu lệnh SQL để chèn một bản ghi phân công mới
        String sql = "INSERT INTO EnterpriseAssignments (enterprise_id, user_id, assignment_type) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            ps.setInt(2, userId);
            ps.setString(3, assignmentType);
            ps.executeUpdate();
        }
    }

    /**
     * Lấy danh sách tất cả nhân viên được gán cho một doanh nghiệp. Hàm này sẽ
     * hữu ích cho trang xem chi tiết khách hàng.
     *
     * @param enterpriseId ID của doanh nghiệp
     * @return Danh sách các đối tượng User được gán
     * @throws Exception nếu có lỗi kết nối hoặc thực thi
     */
    public List<User> getAssignedUsersForEnterprise(int enterpriseId) throws Exception {
        List<User> assignedUsers = new ArrayList<>();
        // JOIN 3 bảng để lấy thông tin chi tiết của nhân viên được gán
        String sql = "SELECT u.*, ea.assignment_type "
                + "FROM Users u "
                + "JOIN EnterpriseAssignments ea ON u.id = ea.user_id "
                + "WHERE ea.enterprise_id = ? AND u.is_deleted = 0";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);

            try (ResultSet rs = ps.executeQuery()) {
                // Ta cần một hàm để tạo đối tượng User từ ResultSet trong UserDAO
                // Tạm thời, ta sẽ làm trực tiếp ở đây
                while (rs.next()) {
                    User user = new User(); // Giả sử UserDAO có hàm extractUserFromResultSet
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setEmail(rs.getString("email"));
                    // Thêm thuộc tính assignment_type vào một trường tạm nào đó nếu cần
                    // user.setTempAssignmentType(rs.getString("assignment_type"));
                    assignedUsers.add(user);
                }
            }
        }
        return assignedUsers;
    }

    public boolean updateMainAssignment(Connection conn, int enterpriseId, int newUserId) throws SQLException {
        // Cập nhật người phụ trách chính (account_manager)
        String sql = "UPDATE EnterpriseAssignments SET user_id = ? WHERE enterprise_id = ? AND assignment_type = 'account_manager'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newUserId);
            ps.setInt(2, enterpriseId);
            return ps.executeUpdate() > 0;
        }
    }

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

}
