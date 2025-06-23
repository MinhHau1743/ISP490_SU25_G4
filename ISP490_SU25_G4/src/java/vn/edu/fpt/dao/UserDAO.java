/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import vn.edu.fpt.model.User;

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

                    if (!"active".equalsIgnoreCase(rs.getString("status")) || rs.getBoolean("is_deleted")) {
                        System.err.println("DAO: User bi khoa hoac da xoa.");
                        return null; // Không cho đăng nhập nếu tài khoản không hoạt động
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
                        user.setStatus(rs.getString("status"));
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

// // Lấy all thông tin user dựa vào ID
//    public User getUserById(int userId) {
//        
//        String sql = "SELECT * FROM Users WHERE id = ?";
//        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                // Sử dụng lại logic tương tự hàm login để lấy đầy đủ thông tin
//                User user = new User();
//
//                user.setId(rs.getInt("id"));
//                user.setLastName(rs.getString("last_name"));
//                user.setMiddleName(rs.getString("middle_name"));
//                user.setFirstName(rs.getString("first_name"));
//                user.setEmail(rs.getString("email"));
//                user.setPasswordHash(rs.getString("password_hash"));
//                user.setRole(rs.getString("role"));
//                user.setStatus(rs.getString("status"));                
//                user.setEmployeeCode(rs.getString("employee_code"));
//                user.setPosition(rs.getString("position"));
//                user.setDepartment(rs.getString("department"));
//                user.setPhoneNumber(rs.getString("phone_number"));
//                user.setNotes(rs.getString("notes")); 
//                user.setAvatarUrl(rs.getString("avatar_url"));
//                user.setIdentityCardNumber(rs.getString("identity_card_number"));
//                
//                if (rs.getDate("date_of_birth") != null) {
//                    user.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
//                }
//                
//                user.setGender(rs.getString("gender"));
//                user.setAddress(rs.getString("address"));
//                user.setWard(rs.getString("ward"));
//                user.setDistrict(rs.getString("district"));
//                user.setCity(rs.getString("city"));
//                user.setSocialMediaLink(rs.getString("social_media_link"));
//                user.setIsDeleted(rs.getBoolean("is_deleted"));
//                user.setCreatedAt(rs.getTimestamp("created_at"));
//                user.setUpdatedAt(rs.getTimestamp("updated_at"));
//                
//                
//                
//
//                return user;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null; // Không tìm thấy user
//    }
    // Lấy danh sách nhân viên để hiển thị trong dropdown
    public List<User> getAllEmployees() throws Exception {
        List<User> employees = new ArrayList<>();
        String sql = "SELECT id, first_name, middle_name, last_name FROM Users WHERE is_deleted = 0 AND status = 'active'";
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
}
