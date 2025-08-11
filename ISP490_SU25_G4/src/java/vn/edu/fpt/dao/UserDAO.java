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
import vn.edu.fpt.model.MaintenanceAssignments;

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
        // Sửa SQL: Đã bỏ "AND u.is_deleted = 0"
        String query = "SELECT "
                + "u.*, p.name AS position_name, d.name AS department_name, r.name AS role_name, "
                + "a.street_address, w.name AS ward_name, dist.name AS district_name, prov.name AS province_name, "
                + "a.ward_id, a.district_id, a.province_id, u.position_id "
                + "FROM Users u "
                + "LEFT JOIN Positions p ON u.position_id = p.id "
                + "LEFT JOIN Departments d ON u.department_id = d.id "
                + "LEFT JOIN Roles r ON u.role_id = r.id "
                + "LEFT JOIN Addresses a ON u.address_id = a.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Districts dist ON a.district_id = dist.id "
                + "LEFT JOIN Provinces prov ON a.province_id = prov.id "
                + "WHERE u.id = ?";

        // Tối ưu: Dùng try-with-resources để tự động đóng kết nối
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    // Gán các trường từ bảng Users
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setLastName(rs.getString("last_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setAvatarUrl(rs.getString("avatar_url"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    user.setPhoneNumber(rs.getString("phone_number"));

                    Date dobSql = rs.getDate("date_of_birth");
                    if (dobSql != null) {
                        user.setDateOfBirth(dobSql.toLocalDate());
                    }

                    user.setGender(rs.getString("gender"));
                    user.setIdentityCardNumber(rs.getString("identity_card_number"));
                    user.setNotes(rs.getString("notes"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    user.setUpdatedAt(rs.getTimestamp("updated_at"));
                    user.setIsDeleted(rs.getInt("is_deleted")); // Lấy cả trạng thái is_deleted

                    // Gán các trường từ các bảng JOIN
                    user.setRoleName(rs.getString("role_name"));
                    user.setPositionName(rs.getString("position_name"));
                    user.setDepartmentName(rs.getString("department_name"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setPositionId(rs.getInt("position_id"));

                    // Gán địa chỉ
                    user.setStreetAddress(rs.getString("street_address"));
                    user.setWardId(rs.getObject("ward_id") != null ? rs.getInt("ward_id") : null);
                    user.setDistrictId(rs.getObject("district_id") != null ? rs.getInt("district_id") : null);
                    user.setProvinceId(rs.getObject("province_id") != null ? rs.getInt("province_id") : null);
                    user.setWardName(rs.getString("ward_name"));
                    user.setDistrictName(rs.getString("district_name"));
                    user.setProvinceName(rs.getString("province_name"));

                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy user hoặc có lỗi
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
        User singleUser = userDAO.getUserById(testUserId);

        // In toàn bộ nhân viên kỹ thuật
        List<User> techStaff = userDAO.getAllTechnicalStaffIdAndFullName();
        for (User user : techStaff) {
            System.out.println(user);
        }

        // Kiểm tra và in kết quả chi tiết user có id là testUserId
        if (singleUser != null) {
            System.out.println("User found:");
            System.out.println("ID: " + singleUser.getId());
            System.out.println("Email: " + singleUser.getEmail());
            System.out.println("Full Name: " + singleUser.getLastName() + " " + singleUser.getMiddleName() + " " + singleUser.getFirstName());
            System.out.println("Avatar URL: " + singleUser.getAvatarUrl());
            System.out.println("Employee Code: " + singleUser.getEmployeeCode());
            System.out.println("Phone Number: " + singleUser.getPhoneNumber());
            System.out.println("Date of Birth: " + singleUser.getDateOfBirth());
            System.out.println("Gender: " + singleUser.getGender());
            System.out.println("Identity Card Number: " + singleUser.getIdentityCardNumber());
            System.out.println("Notes: " + singleUser.getNotes());
            System.out.println("Status: " + singleUser.getStatus());
            System.out.println("Created At: " + singleUser.getCreatedAt());
            System.out.println("Updated At: " + singleUser.getUpdatedAt());
            System.out.println("Role Name: " + singleUser.getRoleName());
            System.out.println("Position Name: " + singleUser.getPositionName());
            System.out.println("Department Name: " + singleUser.getDepartmentName());
            System.out.println("Street Address: " + singleUser.getStreetAddress());
            System.out.println("Ward Name: " + singleUser.getWardName());
            System.out.println("District Name: " + singleUser.getDistrictName());
            System.out.println("Province Name: " + singleUser.getProvinceName());
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

    /**
     * === SỬA ĐỔI 1: TÌM KIẾM CẢ ADMIN === Tìm kiếm nhân viên theo tên hoặc mã
     * (không phân biệt hoa-thường). BỎ điều kiện lọc is_deleted để hiển thị cả
     * nhân viên bị vô hiệu hóa. BỎ điều kiện lọc Admin.
     */
    public List<User> searchEmployeesByName(String keyword, int page, int pageSize) {
        List<User> list = new ArrayList<>();
        // Sửa SQL: Đã xóa "AND u.is_deleted = 0"
        String sql = "SELECT u.*, d.name AS departmentName, p.name AS positionName, r.name AS roleName "
                + "FROM Users u "
                + "LEFT JOIN Departments d ON u.department_id = d.id "
                + "LEFT JOIN Positions p ON u.position_id = p.id "
                + "LEFT JOIN Roles r ON u.role_id = r.id "
                + "WHERE (LOWER(CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name)) LIKE ? "
                + "OR LOWER(u.employee_code) LIKE ?) "
                + "ORDER BY u.id " // Sắp xếp lại theo ID cho nhất quán
                + "LIMIT ? OFFSET ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            int offset = (page - 1) * pageSize;

            ps.setString(1, searchKeyword);
            ps.setString(2, searchKeyword);
            ps.setInt(3, pageSize);
            ps.setInt(4, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmployeeCode(rs.getString("employee_code"));
                user.setDepartmentName(rs.getString("departmentName"));
                user.setPositionName(rs.getString("positionName"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setRoleName(rs.getString("roleName"));
                user.setIsDeleted(rs.getInt("is_deleted")); // Lấy trạng thái
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * === SỬA ĐỔI 2: ĐẾM KẾT QUẢ TÌM KIẾM BAO GỒM CẢ ADMIN === Đếm tổng số nhân
     * viên khớp với từ khóa (không phân biệt hoa-thường).
     */
    public int countSearchedEmployees(String keyword) {
        // Sửa SQL: Đã xóa "AND u.is_deleted = 0"
        String sql = "SELECT COUNT(u.id) FROM Users u "
                + "WHERE (LOWER(CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name)) LIKE ? OR LOWER(u.employee_code) LIKE ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchKeyword = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, searchKeyword);
            ps.setString(2, searchKeyword);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * === SỬA ĐỔI 3: ĐẾM TỔNG SỐ NHÂN VIÊN BAO GỒM CẢ ADMIN === Đếm tổng số
     * nhân viên trong hệ thống.
     */
    public int getTotalEmployeeCount() {
        // Sửa SQL: Xóa các điều kiện lọc
        String sql = "SELECT COUNT(id) FROM Users";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * === SỬA ĐỔI 4: LẤY DANH SÁCH BAO GỒM CẢ ADMIN === Lấy danh sách tất cả
     * nhân viên có phân trang.
     */
    public List<User> getAllEmployeesPaginated(int page, int pageSize) {
        List<User> employeeList = new ArrayList<>();
        // Sửa SQL: Đã xóa "WHERE r.name <> 'Admin' AND u.is_deleted = 0"
        String sql = "SELECT u.*, r.name as role_name, p.name as position_name, d.name as department_name "
                + "FROM users u "
                + "JOIN roles r ON u.role_id = r.id "
                + "LEFT JOIN positions p ON u.position_id = p.id "
                + "LEFT JOIN departments d ON u.department_id = d.id "
                + "ORDER BY u.id "
                + "LIMIT ? OFFSET ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            int offset = (page - 1) * pageSize;

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setFirstName(rs.getString("first_name"));
                user.setAvatarUrl(rs.getString("avatar_url"));
                user.setEmployeeCode(rs.getString("employee_code"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setIsDeleted(rs.getInt("is_deleted"));
                user.setRoleName(rs.getString("role_name"));
                user.setPositionName(rs.getString("position_name"));
                user.setDepartmentName(rs.getString("department_name"));
                employeeList.add(user);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách nhân viên phân trang: " + e.getMessage());
            e.printStackTrace();
        }
        return employeeList;
    }

    /**
     * Thực hiện xóa mềm một người dùng bằng cách cập nhật cột is_deleted = 1.
     *
     * @param userId ID của người dùng cần xóa.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean softDeleteUserById(int userId) {
        String sql = "UPDATE Users SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = getConnection(); // Sử dụng hàm getConnection() của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái xóa mềm (is_deleted) của một người dùng.
     *
     * @param userId ID của người dùng cần cập nhật.
     * @param isDeleted Trạng thái mới (0: hoạt động, 1: vô hiệu hóa).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateSoftDeleteStatus(int userId, boolean isDeleted) {
        String sql = "UPDATE Users SET is_deleted = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, isDeleted);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSoftDeleteStatus(int userId, int isDeleted) {
        String sql = "UPDATE Users SET is_deleted = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isDeleted);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setIsDeleted(rs.getInt("is_deleted"));
                    // Bạn có thể set thêm các thuộc tính khác nếu cần
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tìm kiếm một người dùng (bao gồm cả những người đã bị xóa mềm) bằng email
     * hoặc số CMND/CCCD.
     *
     * @param email Email để tìm kiếm.
     * @param idCard Số CMND/CCCD để tìm kiếm.
     * @return Đối tượng User nếu tìm thấy, ngược lại trả về null.
     */
    public User findUserByEmailOrIdCard(String email, String idCard) {
        // Câu lệnh SQL tìm kiếm trên cả hai trường, không phân biệt trạng thái isDeleted
        String sql = "SELECT * FROM Users WHERE email = ? OR identity_card_number = ?";
        User user = null;

        try (Connection conn = new DBContext().getConnection(); // Giả sử DBContext là lớp kết nối của bạn
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, idCard);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    // Ánh xạ dữ liệu từ ResultSet sang đối tượng User
                    user.setId(rs.getInt("id"));
                    user.setLastName(rs.getString("lastName"));
                    user.setMiddleName(rs.getString("middleName"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phoneNumber"));
                    user.setIdentityCardNumber(rs.getString("identity_card_number"));
                    user.setIsDeleted(rs.getInt("isDeleted")); // Lấy cả trạng thái isDeleted
                    // Thêm các trường khác nếu cần
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return user;
    }

    /**
     * Kích hoạt lại một tài khoản người dùng đã bị xóa mềm.
     *
     * @param userId ID của người dùng cần kích hoạt lại.
     * @return true nếu kích hoạt thành công, false nếu có lỗi.
     */
    public boolean reactivateUser(int userId) {
        String sql = "UPDATE Users SET isDeleted = 0 WHERE id = ?";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            // executeUpdate() trả về số dòng bị ảnh hưởng
            int affectedRows = ps.executeUpdate();

            // Nếu có ít nhất một dòng bị ảnh hưởng, coi như thành công
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật phòng ban và chức vụ cho một người dùng.
     *
     * @param userId ID của người dùng.
     * @param departmentId ID của phòng ban mới.
     * @param positionId ID của chức vụ mới.
     * @return true nếu cập nhật thành công, false nếu có lỗi.
     */
    public boolean updateUserDepartmentAndPosition(int userId, int departmentId, int positionId) {
        // Giả sử bảng trung gian của bạn có tên là User_Department_Position
        String sql = "UPDATE User_Department_Position SET departmentId = ?, positionId = ? WHERE userId = ?";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            ps.setInt(2, positionId);
            ps.setInt(3, userId);

            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllTechnicalStaffIdAndFullName() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id, "
                + "TRIM(CONCAT(u.last_name, ' ', IFNULL(u.middle_name, ''), ' ', u.first_name)) AS full_name "
                + "FROM Users u "
                + "JOIN Positions p ON u.position_id = p.id "
                + "WHERE u.is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            // Không cần setString cho vị trí nữa
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    list.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *  * Lấy danh sách nhân viên theo một vai trò (role) cụ thể.  * @param
     * roleName Tên của vai trò (ví dụ: "Chánh văn phòng").  * @return Danh sách
     * các nhân viên có vai trò đó.
 
     */
    public List<User> getEmployeesByDepartment(String roleName) { // Đổi tên tham số cho rõ nghĩa

        List<User> list = new ArrayList<>();

// SỬA LỖI: Đã thay đổi câu lệnh SQL để JOIN với bảng 'roles'
// và sử dụng cột 'role_id' cho đúng với cấu trúc CSDL.
        String sql = "SELECT u.id, u.first_name, u.middle_name, u.last_name "
                + "FROM Users u "
                + "JOIN roles r ON u.role_id = r.id " // Sửa từ 'Departments' sang 'roles' và 'department_id' sang 'role_id'
                + "WHERE r.name = ? AND u.is_deleted = 0 AND u.status = 'active'"; // Thêm điều kiện chỉ lấy nhân viên 'active'
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roleName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    list.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
