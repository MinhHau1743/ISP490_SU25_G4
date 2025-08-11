package vn.edu.fpt.dao;

import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.MaintenanceAssignments;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Address;
import java.sql.Types;

public class MaintenanceScheduleDAO extends DBContext {

    public List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        List<MaintenanceSchedule> schedules = new ArrayList<>();

        // THAY ĐỔI 1: Cập nhật câu SQL để JOIN với các bảng địa chỉ
        String sql
                = "SELECT "
                + " ms.id, "
                + " ms.technical_request_id, "
                + " ms.color, "
                + " ms.scheduled_date, "
                + " ms.end_date, "
                + " ms.start_time, "
                + " ms.end_time, "
                + " ms.address_id, "
                + " ms.status, "
                + " ms.created_at, "
                + " ms.updated_at, "
                + " ms.created_by, "
                + " tr.title AS request_title, "
                + " tr.description AS request_description, "
                + " a.street_address, "
                + " p.name AS province_name, "
                + " d.name AS district_name, "
                + " w.name AS ward_name "
                + "FROM MaintenanceSchedules ms "
                + "LEFT JOIN TechnicalRequests tr ON ms.technical_request_id = tr.id "
                + "LEFT JOIN Addresses a ON ms.address_id = a.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "ORDER BY ms.scheduled_date DESC, ms.start_time ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // THAY ĐỔI 2: Gọi hàm trợ giúp để xử lý việc mapping
                MaintenanceSchedule schedule = mapResultSetToSchedule(rs);
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public boolean updateScheduleByDragDrop(int id, LocalDate scheduledDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        // Chỉ cần MỘT câu lệnh UPDATE
        String sql = "UPDATE MaintenanceSchedules SET scheduled_date = ?, end_date = ?, start_time = ?, end_time = ?, updated_at = NOW() WHERE id = ?";

        // Sử dụng try-with-resources để quản lý tài nguyên
        try (Connection conn = DBContext.getConnection(); // Thay thế bằng cách lấy connection của bạn
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Set scheduled_date (bắt buộc)
            pstmt.setObject(1, scheduledDate);

            // 2. Set endDate (có thể null)
            if (endDate != null) {
                pstmt.setObject(2, endDate);
            } else {
                pstmt.setNull(2, Types.DATE); // Dùng setNull để đảm bảo CSDL nhận giá trị NULL
            }

            // 3. Set startTime (có thể null)
            if (startTime != null) {
                pstmt.setObject(3, startTime);
            } else {
                pstmt.setNull(3, Types.TIME); // Rất quan trọng khi chuyển sang "Cả ngày"
            }

            // 4. Set endTime (có thể null)
            if (endTime != null) {
                pstmt.setObject(4, endTime);
            } else {
                pstmt.setNull(4, Types.TIME); // Rất quan trọng khi chuyển sang "Cả ngày"
            }

            // 5. Set id cho điều kiện WHERE
            pstmt.setInt(5, id);

            // Thực thi và kiểm tra số dòng bị ảnh hưởng
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Luôn ghi log lỗi để dễ dàng debug
            return false;
        }
    }

    public boolean deleteMaintenanceSchedule(int scheduleId) {
        String sql = "DELETE FROM MaintenanceSchedules WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0; // Trả về true nếu xóa thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public MaintenanceSchedule getMaintenanceScheduleById(int id) {
        MaintenanceSchedule schedule = null;

        String sql = "SELECT "
                + "    ms.*, "
                + "    a.street_address, a.province_id, a.district_id, a.ward_id, "
                + "    p.name AS province_name, "
                + "    d.name AS district_name, "
                + "    w.name AS ward_name "
                + "FROM MaintenanceSchedules ms "
                + "LEFT JOIN Addresses a ON ms.address_id = a.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "WHERE ms.id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    schedule = new MaintenanceSchedule();

                    // Các trường từ bảng MaintenanceSchedules
                    schedule.setId(rs.getInt("id"));
                    schedule.setTechnicalRequestId(rs.getInt("technical_request_id"));
                    schedule.setTitle(rs.getString("title"));
                    schedule.setColor(rs.getString("color"));
                    java.sql.Date schDate = rs.getDate("scheduled_date");
                    if (schDate != null) {
                        schedule.setScheduledDate(schDate.toLocalDate());
                    }
                    java.sql.Date endDate = rs.getDate("end_date");
                    if (endDate != null) {
                        schedule.setEndDate(endDate.toLocalDate());
                    }
                    java.sql.Time startTime = rs.getTime("start_time");
                    if (startTime != null) {
                        schedule.setStartTime(startTime.toLocalTime());
                    }
                    java.sql.Time endTime = rs.getTime("end_time");
                    if (endTime != null) {
                        schedule.setEndTime(endTime.toLocalTime());
                    }
                    int addrId = rs.getInt("address_id");
                    schedule.setAddressId(rs.wasNull() ? null : addrId);
                    schedule.setStatus(rs.getString("status"));
                    schedule.setNotes(rs.getString("notes"));
                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        schedule.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        schedule.setUpdatedAt(updatedAt.toLocalDateTime());
                    }

                    // Các trường bổ sung từ JOIN địa chỉ
                    schedule.setStreetAddress(rs.getString("street_address"));
                    int provinceId = rs.getInt("province_id");
                    schedule.setProvinceId(rs.wasNull() ? null : provinceId);
                    schedule.setProvinceName(rs.getString("province_name"));
                    int districtId = rs.getInt("district_id");
                    schedule.setDistrictId(rs.wasNull() ? null : districtId);
                    schedule.setDistrictName(rs.getString("district_name"));
                    int wardId = rs.getInt("ward_id");
                    schedule.setWardId(rs.wasNull() ? null : wardId);
                    schedule.setWardName(rs.getString("ward_name"));

                    // Nếu có thêm các trường khác, bổ sung tương tự ở đây!
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public int addMaintenanceScheduleAndReturnId(MaintenanceSchedule schedule) {
        String sql = "INSERT INTO MaintenanceSchedules "
                + "(technical_request_id, title, color, scheduled_date, end_date, start_time, end_time, address_id, status, notes, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Dùng setObject để tự động xử lý null và các kiểu dữ liệu java.time
            ps.setObject(1, schedule.getTechnicalRequestId());
            ps.setString(2, schedule.getTitle());
            ps.setString(3, schedule.getColor());
            ps.setObject(4, schedule.getScheduledDate());
            ps.setObject(5, schedule.getEndDate());
            ps.setObject(6, schedule.getStartTime());
            ps.setObject(7, schedule.getEndTime());
            ps.setObject(8, schedule.getAddressId());
            ps.setString(9, schedule.getStatus());
            ps.setString(10, schedule.getNotes());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // ID vừa insert
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Thất bại
    }

    public boolean updateMaintenanceSchedule(MaintenanceSchedule schedule) {
        if (schedule == null || schedule.getId() <= 0) {
            System.out.println("Error: A valid schedule object with an ID is required for an update.");
            return false;
        }

        // **SỬA ĐỔI**: Thay thế "location = ?" bằng "address_id = ?".
        String sql = "UPDATE MaintenanceSchedules SET "
                + "technical_request_id = ?, title = ?, color = ?, "
                + "scheduled_date = ?, end_date = ?, start_time = ?, end_time = ?, "
                + "address_id = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Sử dụng setObject để xử lý null và các kiểu java.time một cách an toàn
            ps.setObject(1, schedule.getTechnicalRequestId());
            ps.setString(2, schedule.getTitle());
            ps.setString(3, schedule.getColor());
            ps.setObject(4, schedule.getScheduledDate());
            ps.setObject(5, schedule.getEndDate());
            ps.setObject(6, schedule.getStartTime());
            ps.setObject(7, schedule.getEndTime());

            // **SỬA ĐỔI**: Sử dụng addressId thay vì location.
            ps.setObject(8, schedule.getAddressId());

            ps.setString(9, schedule.getStatus());
            ps.setString(10, schedule.getNotes());
            ps.setInt(11, schedule.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Thêm hàm private này vào trong class MaintenanceScheduleDAO
    private MaintenanceSchedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
    MaintenanceSchedule schedule = new MaintenanceSchedule();

    // Fields từ MaintenanceSchedules
    schedule.setId(rs.getInt("id"));
    schedule.setTechnicalRequestId(rs.getObject("technical_request_id", Integer.class));
    // schedule.setTitle(rs.getString("title")); // bỏ: lấy từ TechnicalRequests
    schedule.setColor(rs.getString("color"));
    schedule.setScheduledDate(rs.getObject("scheduled_date", java.time.LocalDate.class));
    schedule.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
    schedule.setStartTime(rs.getObject("start_time", java.time.LocalTime.class));
    schedule.setEndTime(rs.getObject("end_time", java.time.LocalTime.class));
    schedule.setStatus(rs.getString("status"));
    // schedule.setNotes(rs.getString("notes")); // bỏ: lấy từ TechnicalRequests.description
    schedule.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
    schedule.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));

    // Lấy title/description từ TechnicalRequests
    String requestTitle = nullSafeGet(rs, "request_title");
    String requestDescription = nullSafeGet(rs, "request_description");
    schedule.setTitle(requestTitle);
    schedule.setNotes(requestDescription);


    // Address
    Integer addressId = rs.getObject("address_id", Integer.class);
    schedule.setAddressId(addressId);

    if (addressId != null) {
        Address addr = new Address();
        addr.setId(addressId);
        addr.setStreetAddress(nullSafeGet(rs, "street_address"));

        String wardName = nullSafeGet(rs, "ward_name");
        String districtName = nullSafeGet(rs, "district_name");
        String provinceName = nullSafeGet(rs, "province_name");

        // Ghép địa chỉ, bỏ qua phần null/empty để tránh ", , ,"
        String fullAddress = joinNonEmpty(
            addr.getStreetAddress(), wardName, districtName, provinceName
        );
        addr.setFullAddress(fullAddress);

        schedule.setFullAddress(addr);
    }

    return schedule;
}

// Tiện ích: đọc String an toàn, trả về null nếu cột không tồn tại hoặc null
private String nullSafeGet(ResultSet rs, String column) {
    try {
        String v = rs.getString(column);
        return (v != null && !v.trim().isEmpty()) ? v : null;
    } catch (SQLException e) {
        return null;
    }
}

// Tiện ích: nối các phần không rỗng bằng ", "
private String joinNonEmpty(String... parts) {
    StringBuilder sb = new StringBuilder();
    for (String p : parts) {
        if (p != null && !p.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(p.trim());
        }
    }
    return sb.toString();
}


    public List<MaintenanceAssignments> getAllMaintenanceAssignments() {
        List<MaintenanceAssignments> list = new ArrayList<>();
        String sql = "SELECT ma.id, ma.maintenance_schedule_id, ma.user_id, "
                + "TRIM(CONCAT(u.last_name, ' ', IFNULL(u.middle_name, ''), ' ', u.first_name)) AS full_name "
                + "FROM MaintenanceAssignments ma "
                + "JOIN Users u ON ma.user_id = u.id";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceAssignments ma = new MaintenanceAssignments();
                ma.setId(rs.getInt("id"));
                ma.setMaintenanceScheduleId(rs.getInt("maintenance_schedule_id"));
                ma.setUserId(rs.getInt("user_id"));
                ma.setFullName(rs.getString("full_name"));
                list.add(ma);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addMaintenanceAssignments(int maintenanceScheduleId, List<Integer> userIds) {
        String sql = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int userId : userIds) {
                ps.setInt(1, maintenanceScheduleId);
                ps.setInt(2, userId);
                ps.addBatch();
            }
            int[] affectedRows = ps.executeBatch();
            for (int count : affectedRows) {
                if (count == 0) {
                    return false; // Nếu có user nào không thêm được
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getAssignedUserIdsByScheduleId(int scheduleId) {
        // 1. Khởi tạo một danh sách rỗng để lưu kết quả
        List<Integer> userIds = new ArrayList<>();

        // 2. Câu lệnh SQL để lấy user_id dựa trên maintenance_schedule_id
        String sql = "SELECT user_id FROM MaintenanceAssignments WHERE maintenance_schedule_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // 3. Gán giá trị cho tham số trong câu lệnh SQL
            ps.setInt(1, scheduleId);

            try (ResultSet rs = ps.executeQuery()) {
                // 4. Lặp qua tất cả các dòng kết quả trả về
                while (rs.next()) {
                    // Lấy giá trị từ cột "user_id" và thêm vào danh sách
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Nên sử dụng logger trong thực tế
        }

        // 5. Trả về danh sách ID người dùng
        return userIds;
    }

    public boolean updateAssignmentsForSchedule(int scheduleId, List<Integer> newUserIds) {
        // Câu lệnh SQL
        String deleteSql = "DELETE FROM MaintenanceAssignments WHERE maintenance_schedule_id = ?";
        String insertSql = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            // 1. Bắt đầu một transaction, tắt chế độ tự động commit
            conn.setAutoCommit(false);

            // --- BƯỚC 1: Xóa tất cả các phân công cũ ---
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, scheduleId);
                psDelete.executeUpdate();
            }

            // --- BƯỚC 2: Thêm lại các phân công mới (nếu có) ---
            if (newUserIds != null && !newUserIds.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    // Sử dụng batch update để tăng hiệu năng
                    for (Integer userId : newUserIds) {
                        psInsert.setInt(1, scheduleId);
                        psInsert.setInt(2, userId);
                        psInsert.addBatch(); // Thêm câu lệnh vào batch
                    }
                    psInsert.executeBatch(); // Thực thi tất cả các câu lệnh trong batch
                }
            }

            // 2. Nếu mọi thứ thành công, commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi
            // 3. Nếu có lỗi, rollback tất cả các thay đổi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // 4. Luôn luôn trả lại chế độ auto-commit và đóng kết nối
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
        MaintenanceSchedule schedule = dao.getMaintenanceScheduleById(26);
        List<MaintenanceSchedule> assignedUserIds = dao.getAllMaintenanceSchedules();
        System.out.println(assignedUserIds);
        if (schedule != null) {
            System.out.println("ID: " + schedule.getId());
            System.out.println("Title: " + schedule.getTitle());
            System.out.println("Street: " + schedule.getStreetAddress());
            System.out.println("Province: " + schedule.getProvinceId());
            System.out.println("District: " + schedule.getDistrictId());
            System.out.println("Ward: " + schedule.getWardId());
        }
        System.out.println("Danh sách lịch bảo trì:");

    }
}
