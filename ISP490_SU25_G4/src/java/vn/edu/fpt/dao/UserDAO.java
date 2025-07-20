/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Date;
import org.mindrot.jbcrypt.BCrypt;
import vn.edu.fpt.model.User;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;     // <-- Có thể cần cho các xử lý thời gian khác
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.sql.Types;
import static vn.edu.fpt.dao.DBContext.getConnection;

/**
 *
 * @author anhndhe172050
 */
public class UserDAO {

    /**
     * Xác thực thông tin đăng nhập và lấy đầy đủ thông tin người dùng. Phiên
     * bản này có chứa các dòng System.out.println để gỡ rối.
     *
     * @param email Email do người dùng nhập.
     * @param password Mật khẩu thô (chưa băm) do người dùng nhập.
     * @return Đối tượng User nếu thành công, ngược lại trả về null.
     */
    Connection conn = getConnection();

    public User login(String email, String password) {
        System.out.println("DAO: Bat dau ham login voi email: " + email);

        // Câu lệnh SQL hoàn chỉnh với LEFT JOIN để lấy tất cả thông tin liên quan
        String sql = "SELECT "
                + "    u.*, "
                + "    r.name AS role_name, "
                + "    p.name AS position_name, "
                + "    d.name AS department_name, "
                + "    a.street_address, "
                + "    w.name AS ward_name, "
                + "    dist.name AS district_name, "
                + "    prov.name AS province_name "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.role_id = r.id "
                + "LEFT JOIN Positions p ON u.position_id = p.id "
                + "LEFT JOIN Departments d ON u.department_id = d.id "
                + "LEFT JOIN Addresses a ON u.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts dist ON a.district_id = dist.id "
                + "LEFT JOIN Provinces prov ON a.province_id = prov.id "
                + "WHERE u.email = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("DAO: Ket noi CSDL that bai!");
                return null;
            }
            System.out.println("DAO: Da ket noi CSDL thanh cong.");

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DAO: Tim thay user '" + email + "' trong CSDL.");

                    // Kiểm tra xem tài khoản có bị xóa không
                    if (rs.getInt("is_deleted") == 1) {
                        System.err.println("DAO: Tài khoản này đã ngừng hoạt động hoặc đã bị xóa.");
                        return null; // Không cho đăng nhập
                    }
                    String hashedPassword = rs.getString("password_hash");
                    System.out.println("DAO: Kiem tra mat khau...");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        System.out.println("DAO: Mat khau dung! Dang tao user object.");

                        User user = new User();
                        // Lấy thông tin từ bảng Users
                        user.setId(rs.getInt("id"));
                        user.setEmail(rs.getString("email"));
                        user.setLastName(rs.getString("last_name"));
                        user.setMiddleName(rs.getString("middle_name"));
                        user.setFirstName(rs.getString("first_name"));
                        user.setAvatarUrl(rs.getString("avatar_url"));
                        user.setEmployeeCode(rs.getString("employee_code"));
                        user.setPhoneNumber(rs.getString("phone_number"));
                        if (rs.getDate("date_of_birth") != null) {
                            user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
                        }
                        user.setGender(rs.getString("gender"));
                        user.setIdentityCardNumber(rs.getString("identity_card_number"));
                        user.setNotes(rs.getString("notes"));
                        user.setIsDeleted(rs.getInt("is_deleted"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        user.setUpdatedAt(rs.getTimestamp("updated_at"));

                        // Lấy thông tin từ các bảng đã JOIN
                        user.setRoleName(rs.getString("role_name"));
                        user.setPositionName(rs.getString("position_name"));
                        user.setDepartmentName(rs.getString("department_name"));
                        user.setStreetAddress(rs.getString("street_address"));
                        user.setWardName(rs.getString("ward_name"));
                        user.setDistrictName(rs.getString("district_name"));
                        user.setProvinceName(rs.getString("province_name"));
                        user.setRequireChangePassword(rs.getInt("require_change_password"));
                        System.out.println("DAO: Tao user object thanh cong. Tra ve user.");
                        return user; // Đăng nhập thành công, trả về đối tượng User
                    } else {
                        System.err.println("DAO: Mat khau sai!");
                    }
                } else {
                    System.err.println("DAO: Khong tim thay user voi email nay.");
                }
            }
        } catch (Exception e) {
            System.err.println("DAO: Co loi Exception xay ra!");
            e.printStackTrace();
        }

        System.err.println("DAO: Tra ve null do co loi hoac sai thong tin.");
        return null; // Sai email, sai mật khẩu, hoặc có lỗi xảy ra
    }

//    public void createUser(String lastName, String middleName, String firstName, String email, String rawPassword) {
//        // Câu lệnh SQL đã được sửa để dùng các cột tên mới
//        String sql = "INSERT INTO Users (last_name, middle_name, first_name, email, password_hash, role, status) VALUES (?, ?, ?, ?, ?, 'cskh', 'active')";
//
//        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            // Mã hóa mật khẩu
//            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//            // Thiết lập các tham số cho PreparedStatement theo đúng thứ tự
//            ps.setString(1, lastName);
//            ps.setString(2, middleName);
//            ps.setString(3, firstName);
//            ps.setString(4, email);
//            ps.setString(5, hashedPassword);
//
//            // Thực thi câu lệnh
//            ps.executeUpdate();
//
//        } catch (Exception e) {
//            // In ra lỗi để dễ dàng gỡ rối
//            e.printStackTrace();
//        }
//    }
    public void updateUser(User user) {
        String sql = "UPDATE Users SET "
                + "email = ?, "
                + "last_name = ?, "
                + "middle_name = ?, "
                + "first_name = ?, "
                + "avatar_url = ?, "
                + "employee_code = ?, "
                + "phone_number = ?, "
                + "date_of_birth = ?, "
                + "gender = ?, "
                + "identity_card_number = ?, "
                + "notes = ?, "
                + "address_id = ?, "
                + "position_id = ?, "
                + "department_id = ?, "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getMiddleName());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getAvatarUrl());
            ps.setString(6, user.getEmployeeCode());
            ps.setString(7, user.getPhoneNumber());

            if (user.getDateOfBirth() != null) {
                ps.setDate(8, Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            ps.setString(9, user.getGender());
            ps.setString(10, user.getIdentityCardNumber());
            ps.setString(11, user.getNotes());

            if (user.getAddressId() > 0) {
                ps.setInt(12, user.getAddressId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }

            if (user.getPositionId() > 0) {
                ps.setInt(13, user.getPositionId());
            } else {
                ps.setNull(13, Types.INTEGER);
            }

            if (user.getDepartmentId() > 0) {
                ps.setInt(14, user.getDepartmentId());
            } else {
                ps.setNull(14, Types.INTEGER);
            }

            ps.setInt(15, user.getId()); // WHERE id = ?

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getDepartmentIdByName(String name) {
        String sql = "SELECT id FROM departments WHERE name = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }

    public int getPositionIdByName(String name) {
        String sql = "SELECT id FROM positions WHERE name = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }

    public int getAddressIdByDetails(String street, String provinceName, String districtName, String wardName) {
        String sql = "SELECT a.id FROM addresses a "
                + "JOIN wards w ON a.ward_id = w.id "
                + "JOIN districts d ON a.district_id = d.id "
                + "JOIN provinces p ON a.province_id = p.id "
                + "WHERE a.street_address = ? AND w.name = ? AND d.name = ? AND p.name = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, street);
            ps.setString(2, wardName);
            ps.setString(3, districtName);
            ps.setString(4, provinceName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không tìm thấy
    }

    public void updatePassword(String email, String rawPassword) {
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
            ps.setString(1, hashed);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT id FROM Users WHERE email = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // có tồn tại
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
//
//    public boolean updateUserProfile(User user) {
//        // Câu lệnh SQL để cập nhật các trường có thể chỉnh sửa
//        String sql = "UPDATE Users SET "
//                + "last_name = ?, middle_name = ?, first_name = ?, email = ?, phone_number = ?, "
//                + "department = ?, position = ?, notes = ?, identity_card_number = ?, date_of_birth = ?, "
//                + "gender = ?, address = ?, ward = ?, district = ?, city = ?, social_media_link = ?, "
//                + "avatar_url = ? "
//                + "WHERE id = ?";
//
//        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            // Thiết lập các tham số cho câu lệnh PreparedStatement
//            ps.setString(1, user.getLastName());
//            ps.setString(2, user.getMiddleName());
//            ps.setString(3, user.getFirstName());
//            ps.setString(4, user.getEmail());
//            ps.setString(5, user.getPhoneNumber());
//            ps.setString(6, user.getDepartment());
//            ps.setString(7, user.getPosition());
//            ps.setString(8, user.getNotes());
//            ps.setString(9, user.getIdentityCardNumber());
//
//            // Chuyển đổi từ kiểu java.time.LocalDate sang java.sql.Date
//            if (user.getDateOfBirth() != null) {
//                ps.setDate(10, java.sql.Date.valueOf(user.getDateOfBirth()));
//            } else {
//                ps.setNull(10, java.sql.Types.DATE);
//            }
//
//            ps.setString(11, user.getGender());
//            ps.setString(12, user.getAddress());
//            ps.setString(13, user.getWard());
//            ps.setString(14, user.getDistrict());
//            ps.setString(15, user.getCity());
//            ps.setString(16, user.getSocialMediaLink());
//            ps.setString(17, user.getAvatarUrl());
//
//            // Điều kiện WHERE
//            ps.setInt(18, user.getId());
//
//            // Thực thi lệnh update và kiểm tra xem có hàng nào được cập nhật không
//            int rowsAffected = ps.executeUpdate();
//            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false; // Trả về false nếu có lỗi xảy ra
//        }
//    }

//  Lấy all thông tin user dựa vào ID
    public int insertAddress1(String streetAddress, int wardId, int districtId, int provinceId) throws SQLException {
        String sql = "INSERT INTO Addresses (street_address, ward_id, district_id, province_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

    public User getUserById(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Câu lệnh SQL sử dụng LEFT JOIN để gom thông tin từ các bảng liên quan, bổ sung lấy ID địa chỉ
        String query = "SELECT "
                + "u.id, u.email, u.last_name, u.middle_name, u.first_name, "
                + "u.avatar_url, u.employee_code, u.phone_number, u.date_of_birth, u.gender, "
                + "u.identity_card_number, u.notes, u.created_at, u.updated_at, u.department_id, "
                + "p.name AS position_name, "
                + "d.name AS department_name, "
                + "r.name AS role_name, "
                + "a.street_address, a.ward_id, a.district_id, a.province_id, " // Thêm các trường ID địa chỉ
                + "w.name AS ward_name, "
                + "dist.name AS district_name, "
                + "prov.name AS province_name "
                + "FROM Users u "
                + "LEFT JOIN Positions p ON u.position_id = p.id "
                + "LEFT JOIN Departments d ON u.department_id = d.id "
                + "LEFT JOIN Roles r ON u.role_id = r.id "
                + "LEFT JOIN Addresses a ON u.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts dist ON a.district_id = dist.id "
                + "LEFT JOIN Provinces prov ON a.province_id = prov.id "
                + "WHERE u.id = ? AND u.is_deleted = 0";

        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();

                // === Gán các trường từ bảng Users ===
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setFirstName(rs.getString("first_name"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setEmployeeCode(rs.getString("employee_code"));
                user.setPhoneNumber(rs.getString("phone_number"));

                // Chuyển đổi java.sql.Date sang java.time.LocalDate
                Date dobSql = rs.getDate("date_of_birth");
                if (dobSql != null) {
                    user.setDateOfBirth(dobSql.toLocalDate());
                }

                user.setGender(rs.getString("gender"));
                user.setIdentityCardNumber(rs.getString("identity_card_number"));
                user.setNotes(rs.getString("notes"));

                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));

                // === Gán các trường từ các bảng JOIN (DTO style) ===
                user.setRoleName(rs.getString("role_name"));
                user.setPositionName(rs.getString("position_name"));
                user.setDepartmentName(rs.getString("department_name"));
                user.setDepartmentId(rs.getInt("department_id"));

                // === Gán địa chỉ (cả ID và tên) ===
                user.setStreetAddress(rs.getString("street_address"));
                user.setWardId(rs.getObject("ward_id") != null ? rs.getInt("ward_id") : null);
                user.setDistrictId(rs.getObject("district_id") != null ? rs.getInt("district_id") : null);
                user.setProvinceId(rs.getObject("province_id") != null ? rs.getInt("province_id") : null);
                user.setWardName(rs.getString("ward_name"));
                user.setDistrictName(rs.getString("district_name"));
                user.setProvinceName(rs.getString("province_name"));

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Lấy danh sách nhân viên để hiển thị trong dropdown
    public List<User> getAllEmployees() throws Exception {
        List<User> employees = new ArrayList<>();
        String sql = "SELECT id, first_name, middle_name, last_name FROM Users WHERE is_deleted = 0 AND status = 'active' AND role_id = '3' OR role_id = '1'";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFirstName(rs.getString("first_name"));
                u.setMiddleName(rs.getString("middle_name"));
                u.setLastName(rs.getString("last_name"));
                employees.add(u);
            }
        }
        return employees;
    }

    public List<User> getAssignedUsersForEnterprise(int enterpriseId) throws Exception {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.id, u.first_name, u.last_name, u.middle_name, u.avatar_url "
                + "FROM Users u "
                + "JOIN EnterpriseAssignments ea ON u.id = ea.user_id "
                + "WHERE ea.enterprise_id = ? AND u.is_deleted = 0";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    public List<User> getUsersByRoleName(String roleName) {
        List<User> userList = new ArrayList<>();
        // Câu lệnh SQL được cập nhật để LEFT JOIN thêm positions và departments
        String sql = "SELECT u.*, r.name as role_name, p.name as position_name, d.name as department_name "
                + "FROM users u "
                + "JOIN roles r ON u.role_id = r.id "
                + "LEFT JOIN positions p ON u.position_id = p.id "
                + "LEFT JOIN departments d ON u.department_id = d.id "
                + "WHERE r.name = ? AND u.is_deleted = 0";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    // Gán các trường cơ bản từ bảng Users
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setStatus(rs.getString("status"));

                    // Gán các trường lấy từ bảng JOIN
                    user.setRoleName(rs.getString("role_name"));
                    user.setPositionName(rs.getString("position_name")); // Dữ liệu mới
                    user.setDepartmentName(rs.getString("department_name")); // Dữ liệu mới

                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách user theo vai trò: " + e.getMessage());
            e.printStackTrace();
        }
        return userList;
    }

    public List<User> getAllEmployeesRole() {
        List<User> employeeList = new ArrayList<>();
        // Câu lệnh SQL JOIN các bảng và loại trừ vai trò 'Admin'
        String sql = "SELECT u.*, r.name as role_name, p.name as position_name, d.name as department_name "
                + "FROM users u "
                + "JOIN roles r ON u.role_id = r.id "
                + "LEFT JOIN positions p ON u.position_id = p.id "
                + "LEFT JOIN departments d ON u.department_id = d.id "
                + "WHERE r.name <> 'Admin' AND u.is_deleted = 0 " // Loại trừ Admin và user đã bị xóa
                + "ORDER BY u.id"; // Sắp xếp cho nhất quán

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                // Gán các trường cơ bản từ bảng Users
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setFirstName(rs.getString("first_name"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setEmployeeCode(rs.getString("employee_code"));
                user.setPhoneNumber(rs.getString("phone_number"));

                user.setIsDeleted(rs.getInt("is_deleted"));

                // Gán các trường lấy từ bảng JOIN
                user.setRoleName(rs.getString("role_name"));
                user.setPositionName(rs.getString("position_name"));
                user.setDepartmentName(rs.getString("department_name"));

                employeeList.add(user);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách tất cả nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
        return employeeList;
    }

    public boolean addEmployee(User user, int departmentId, int positionId, int roleId) {
        String defaultPassword = "Fpt@12345";
        String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());

        // Tạo mã nhân viên duy nhất
        String employeeCode = "NV" + System.currentTimeMillis() % 100000;

        String sql = "INSERT INTO users (email, password_hash, last_name, middle_name, first_name, "
                + "avatar_url, employee_code, phone_number, date_of_birth, gender, "
                + "identity_card_number, notes, address_id, position_id, department_id, role_id, "
                + "is_deleted, created_at, updated_at, require_change_password) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getMiddleName());
            ps.setString(5, user.getFirstName());
            ps.setString(6, user.getAvatarUrl());
            ps.setString(7, employeeCode);
            ps.setString(8, user.getPhoneNumber());

            if (user.getDateOfBirth() != null) {
                ps.setDate(9, java.sql.Date.valueOf(user.getDateOfBirth()));
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }

            ps.setString(10, user.getGender());
            ps.setString(11, user.getIdentityCardNumber());
            ps.setString(12, user.getNotes());

            // Address ID có thể null
            if (user.getAddressId() > 0) {
                ps.setInt(13, user.getAddressId());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }

            ps.setInt(14, positionId);
            ps.setInt(15, departmentId);
            ps.setInt(16, roleId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            System.out.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setRequireChangePasswordByEmail(String email) {
        String sql = "UPDATE Users SET require_change_password = 0 WHERE email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // trả về true nếu có ít nhất 1 dòng được cập nhật
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // Giả sử UserDAO chứa phương thức getUserById
        UserDAO userDAO = new UserDAO();

        // ID người dùng để test, thay đổi thành ID thực tế trong DB của bạn
        int testUserId = 8;  // Ví dụ: ID = 1

        // Gọi phương thức getUserById
        User user = userDAO.getUserById(testUserId);

        // Kiểm tra và in kết quả
        if (user != null) {
            System.out.println("User found:");
            System.out.println("ID: " + user.getId());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Full Name: " + user.getLastName() + " " + user.getMiddleName() + " " + user.getFirstName());
            System.out.println("Avatar URL: " + user.getAvatarUrl());
            System.out.println("Employee Code: " + user.getEmployeeCode());
            System.out.println("Phone Number: " + user.getPhoneNumber());
            System.out.println("Date of Birth: " + user.getDateOfBirth());
            System.out.println("Gender: " + user.getGender());
            System.out.println("Identity Card Number: " + user.getIdentityCardNumber());
            System.out.println("Notes: " + user.getNotes());
            System.out.println("Status: " + user.getStatus());
            System.out.println("Created At: " + user.getCreatedAt());
            System.out.println("Updated At: " + user.getUpdatedAt());
            System.out.println("Role Name: " + user.getRoleName());
            System.out.println("Position Name: " + user.getPositionName());
            System.out.println("Department Name: " + user.getDepartmentName());
            System.out.println("Street Address: " + user.getStreetAddress());
            System.out.println("Ward Name: " + user.getWardName());
            System.out.println("District Name: " + user.getDistrictName());
            System.out.println("Province Name: " + user.getProvinceName());
        } else {
            System.out.println("No user found with ID: " + testUserId);
        }
    }

    public boolean updateEmployee(User user, int departmentId, int positionId) {
        // Câu lệnh UPDATE chỉ cập nhật những trường cho phép sửa
        String sql = "UPDATE users SET "
                + "last_name = ?, middle_name = ?, first_name = ?, phone_number = ?, email = ?, "
                + "department_id = ?, position_id = ?, notes = ?, identity_card_number = ?, "
                + "date_of_birth = ?, gender = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Giả sử bạn đã có logic tách fullName thành last/middle/first
            ps.setString(1, user.getLastName());
            ps.setString(2, user.getMiddleName());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getEmail());
            ps.setInt(6, departmentId);
            ps.setInt(7, positionId);
            ps.setString(8, user.getNotes());
            ps.setString(9, user.getIdentityCardNumber());
            ps.setDate(10, java.sql.Date.valueOf(user.getDateOfBirth()));
            ps.setString(11, user.getGender());

            // ID cho điều kiện WHERE
            ps.setInt(12, user.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin của một nhân viên trong cơ sở dữ liệu. Phương thức này
     * sử dụng SQL động để chỉ cập nhật trường avatar_url nếu một đường dẫn mới
     * được cung cấp trong đối tượng User.
     *
     * @param user Đối tượng User chứa thông tin mới. ID của user phải được set.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateEmployee(User user) {
        // Xây dựng câu lệnh SQL động để không cập nhật avatar nếu không có file mới
        StringBuilder sqlBuilder = new StringBuilder("UPDATE Users SET ");
        sqlBuilder.append("last_name = ?, middle_name = ?, first_name = ?, phone_number = ?, ");
        sqlBuilder.append("date_of_birth = ?, gender = ?, identity_card_number = ?, notes = ?, ");
        sqlBuilder.append("position_id = ?, department_id = ?, updated_at = CURRENT_TIMESTAMP");

        // Chỉ thêm phần cập nhật avatar nếu có URL mới và không rỗng
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            sqlBuilder.append(", avatar_url = ?");
        }

        // Mệnh đề WHERE là quan trọng nhất để cập nhật đúng người dùng
        sqlBuilder.append(" WHERE id = ?");

        String sql = sqlBuilder.toString();
        System.out.println("Executing SQL: " + sql); // In ra để kiểm tra câu lệnh SQL

        try (Connection conn = new DBContext().getConnection(); // Thay DBContext bằng lớp kết nối của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1; // Biến đếm vị trí tham số '?'

            // Set các tham số theo đúng thứ tự trong câu lệnh SQL
            ps.setString(paramIndex++, user.getLastName());
            ps.setString(paramIndex++, user.getMiddleName());
            ps.setString(paramIndex++, user.getFirstName());
            ps.setString(paramIndex++, user.getPhoneNumber());

            // Chuyển đổi từ java.time.LocalDate sang java.sql.Date
            ps.setDate(paramIndex++, java.sql.Date.valueOf(user.getDateOfBirth()));

            ps.setString(paramIndex++, user.getGender());
            ps.setString(paramIndex++, user.getIdentityCardNumber());
            ps.setString(paramIndex++, user.getNotes());
            ps.setInt(paramIndex++, user.getPositionId());
            ps.setInt(paramIndex++, user.getDepartmentId());

            // Set tham số cho avatar nếu có
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                ps.setString(paramIndex++, user.getAvatarUrl());
            }

            // Set tham số cuối cùng cho ID trong mệnh đề WHERE
            ps.setInt(paramIndex++, user.getId());

            // Thực thi câu lệnh UPDATE và kiểm tra số dòng bị ảnh hưởng
            int result = ps.executeUpdate();

            // Nếu result > 0, có nghĩa là có ít nhất 1 dòng đã được cập nhật thành công
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhân viên trong DAO: " + e.getMessage());
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi xảy ra
        }
    }


}
