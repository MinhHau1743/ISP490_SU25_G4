package vn.edu.fpt.dao;

import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.MaintenanceAssignments;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Address;
import java.sql.Types;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Status;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;

public class ScheduleDAO extends DBContext {

    public List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        List<MaintenanceSchedule> schedules = new ArrayList<>();
        String sql = "SELECT "
                + "ms.id, "
                + "ms.technical_request_id, "
                + "ms.campaign_id, "
                + "ms.color, "
                + "ms.scheduled_date, "
                + "ms.end_date, "
                + "ms.start_time, "
                + "ms.end_time, "
                + "ms.address_id, "
                + "ms.status_id, "
                + "s.status_name, "
                + "a.street_address, "
                + "p.name AS province_name, "
                + "d.name AS district_name, "
                + "w.name AS ward_name, "
                + "tr.title AS request_title, "
                + "tr.description AS request_description, "
                + "c.name AS campaign_title, " // thêm campaign title
                + "c.description AS campaign_description " // thêm campaign description
                + "FROM MaintenanceSchedules ms "
                + "LEFT JOIN Addresses a ON ms.address_id = a.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id "
                + "LEFT JOIN Districts d ON a.district_id = d.id "
                + "LEFT JOIN Wards w ON a.ward_id = w.id "
                + "LEFT JOIN Statuses s ON ms.status_id = s.id "
                + "LEFT JOIN TechnicalRequests tr ON ms.technical_request_id = tr.id "
                + "LEFT JOIN Campaigns c ON ms.campaign_id = c.campaign_id";
        try (
                Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MaintenanceSchedule schedule = new MaintenanceSchedule();
                schedule.setId(rs.getInt("id"));
                schedule.setTechnicalRequestId(rs.getObject("technical_request_id", Integer.class));
                schedule.setCampaignId(rs.getObject("campaign_id") != null ? rs.getInt("campaign_id") : null);
                schedule.setColor(rs.getString("color"));
                schedule.setScheduledDate(rs.getObject("scheduled_date", LocalDate.class));
                schedule.setEndDate(rs.getObject("end_date", LocalDate.class));
                schedule.setStartTime(rs.getObject("start_time", LocalTime.class));
                schedule.setEndTime(rs.getObject("end_time", LocalTime.class));
                schedule.setStatusId(rs.getObject("status_id", Integer.class));
                schedule.setStatusName(rs.getString("status_name"));

                // ==== Lấy title/notes ưu tiên campaign, nếu null thì lấy request ====
                String requestTitle = rs.getString("request_title");
                String requestDescription = rs.getString("request_description");
                String campaignTitle = rs.getString("campaign_title");
                String campaignDescription = rs.getString("campaign_description");
                schedule.setTitle((campaignTitle != null && !campaignTitle.trim().isEmpty()) ? campaignTitle : requestTitle);
                schedule.setNotes((campaignDescription != null && !campaignDescription.trim().isEmpty()) ? campaignDescription : requestDescription);
                // ==== END ====

                // Địa chỉ (nếu có addressId)
                Integer addressId = rs.getObject("address_id", Integer.class);
                schedule.setAddressId(addressId);
                if (addressId != null) {
                    Address addr = new Address();
                    addr.setId(addressId);
                    addr.setStreetAddress(rs.getString("street_address"));
                    String fullAddress = String.format("%s, %s, %s, %s",
                            rs.getString("street_address"),
                            rs.getString("ward_name"),
                            rs.getString("district_name"),
                            rs.getString("province_name")
                    );
                    addr.setFullAddress(fullAddress);
                    schedule.setAddress(addr);
                }

                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

// File: ScheduleDAO.java
    // Trong file ScheduleDAO.java
    public boolean markAsCompleted(int scheduleId) {
        // Cách 1: Tốt nhất - Lấy ID động (yêu cầu có StatusDAO)
        StatusDAO statusDAO = new StatusDAO();
        Integer completedStatusId = statusDAO.getIdByName("Hoàn thành"); // Giả sử bạn có hàm này trong StatusDAO

        if (completedStatusId == null) {
            System.err.println("Không tìm thấy trạng thái 'Hoàn thành'");
            return false;
        }

        String sql = "UPDATE MaintenanceSchedules SET status_id = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, completedStatusId); // Sử dụng ID động
            ps.setInt(2, scheduleId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

// ---- File StatusDAO.java (Ví dụ hàm cần thêm) ----
    public Integer getIdByName(String statusName) {
        String sql = "SELECT id FROM Statuses WHERE status_name = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu không tìm thấy
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
        // Sửa lại điều kiện WHERE thành id
        String sql = "DELETE FROM MaintenanceSchedules WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, scheduleId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Status> getAllStatuses() {
        List<Status> list = new ArrayList<>();

        // cột trong DB là status_name → đặt alias name cho dễ đọc
        final String sql = "SELECT id, status_name AS name FROM Statuses ORDER BY id";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Status s = new Status(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(s);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();       // hoặc log bằng logger
        }
        return list;
    }

    public MaintenanceSchedule getMaintenanceScheduleById(int id) {
        MaintenanceSchedule schedule = null;

        // Câu lệnh SQL được đơn giản hóa, không JOIN với Assignments nữa
        final String sql = """
SELECT
  ms.id, ms.technical_request_id, ms.campaign_id, ms.color,
  ms.scheduled_date, ms.end_date, ms.start_time, ms.end_time,
  ms.address_id, ms.status_id, s.status_name,
  a.street_address, a.ward_id, a.district_id, a.province_id,
  p.name AS province_name, d.name AS district_name, w.name AS ward_name,
  tr.title AS technical_request_title, tr.description AS technical_request_description,
  c.name AS campaign_title, c.description AS campaign_description,
  ms.created_at, ms.updated_at
FROM MaintenanceSchedules ms
LEFT JOIN Statuses  s ON s.id = ms.status_id
LEFT JOIN Addresses a ON a.id = ms.address_id
LEFT JOIN Provinces p ON a.province_id = p.id
LEFT JOIN Districts d ON a.district_id = d.id
LEFT JOIN Wards     w ON a.ward_id     = w.id
LEFT JOIN TechnicalRequests tr ON ms.technical_request_id = tr.id
LEFT JOIN Campaigns c ON ms.campaign_id = c.campaign_id
WHERE ms.id = ?
""";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    schedule = new MaintenanceSchedule();
                    // Lấy giá trị như cũ...
                    schedule.setId(rs.getInt("id"));
                    schedule.setTechnicalRequestId((Integer) rs.getObject("technical_request_id"));
                    schedule.setCampaignId((Integer) rs.getObject("campaign_id"));
                    schedule.setColor(rs.getString("color"));
                    schedule.setStatusId((Integer) rs.getObject("status_id"));
                    schedule.setStatusName(rs.getString("status_name"));
                    // Lấy title/notes ưu tiên từ campaign, nếu không có thì lấy technical_request
                    String campaignTitle = rs.getString("campaign_title");
                    String campaignDescription = rs.getString("campaign_description");
                    String technicalTitle = rs.getString("technical_request_title");
                    String technicalDesc = rs.getString("technical_request_description");
                    schedule.setTitle(
                            (campaignTitle != null && !campaignTitle.trim().isEmpty()) ? campaignTitle : technicalTitle
                    );
                    schedule.setNotes(
                            (campaignDescription != null && !campaignDescription.trim().isEmpty()) ? campaignDescription : technicalDesc
                    );
                    // Tiếp tục các trường thời gian
                    schedule.setScheduledDate(rs.getObject("scheduled_date", java.time.LocalDate.class));
                    schedule.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
                    schedule.setStartTime(rs.getObject("start_time", java.time.LocalTime.class));
                    schedule.setEndTime(rs.getObject("end_time", java.time.LocalTime.class));
                    schedule.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                    schedule.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));
                    // Địa chỉ
                    Address address = new Address();
                    address.setId(rs.getInt("address_id"));
                    address.setStreetAddress(rs.getString("street_address"));
                    address.setProvinceId(rs.getInt("province_id"));
                    address.setDistrictId(rs.getInt("district_id"));
                    address.setWardId(rs.getInt("ward_id"));
                    address.setProvince(new Province(rs.getInt("province_id"), rs.getString("province_name")));
                    address.setDistrict(new District(rs.getInt("district_id"), rs.getString("district_name")));
                    address.setWard(new Ward(rs.getInt("ward_id"), rs.getString("ward_name")));
                    schedule.setAddress(address);
                }

            }

            // 5. Nếu tìm thấy schedule, thực hiện truy vấn thứ hai để lấy TẤT CẢ nhân viên
            if (schedule != null) {
                List<Integer> assignedUserIds = getAssignedUserIdsByScheduleId(schedule.getId());
                schedule.setAssignedUserIds(assignedUserIds); // Giả sử có setter này trong model
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return schedule;
    }

    public int addScheduleWithAssignments(MaintenanceSchedule schedule,
            List<Integer> userIds) {
        final String INSERT_SCHEDULE_SQL = """
        INSERT INTO MaintenanceSchedules
        (technical_request_id, campaign_id, color,
         scheduled_date, end_date, start_time, end_time,
         address_id, status_id, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

        final String INSERT_ASSIGN_SQL
                = "INSERT INTO MaintenanceAssignments "
                + "(maintenance_schedule_id, user_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);                 // 1) BẮT ĐẦU GIAO DỊCH

            /* ---------- Insert MaintenanceSchedule ---------- */
            int scheduleId;
            try (PreparedStatement ps = conn.prepareStatement(
                    INSERT_SCHEDULE_SQL, Statement.RETURN_GENERATED_KEYS)) {

                ps.setObject(1, schedule.getTechnicalRequestId());
                ps.setObject(2, schedule.getCampaignId());
                ps.setString(3, schedule.getColor());
                ps.setObject(4, schedule.getScheduledDate());
                ps.setObject(5, schedule.getEndDate());
                ps.setObject(6, schedule.getStartTime());
                ps.setObject(7, schedule.getEndTime());
                ps.setObject(8, schedule.getAddressId());
                ps.setObject(9, schedule.getStatusId());

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Insert schedule failed, no rows affected.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Insert schedule failed, no ID obtained.");
                    }
                    scheduleId = rs.getInt(1);
                }
            }

            /* ---------- Insert MaintenanceAssignments ---------- */
            if (userIds != null && !userIds.isEmpty()) {
                try (PreparedStatement psAssign = conn.prepareStatement(INSERT_ASSIGN_SQL)) {
                    for (Integer uid : userIds) {
                        if (uid == null) {
                            continue;     // bỏ qua giá trị null
                        }
                        psAssign.setInt(1, scheduleId);
                        psAssign.setInt(2, uid);
                        psAssign.addBatch();
                    }
                    psAssign.executeBatch();
                }
            }

            conn.commit();                             // 2) COMMIT
            return scheduleId;

        } catch (SQLException ex) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
            ex.printStackTrace();
            return -1;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }

    public boolean updateMaintenanceSchedule(MaintenanceSchedule schedule) {
        final String SQL = """
        UPDATE MaintenanceSchedules
        SET technical_request_id = ?,
            campaign_id          = ?,
            color                = ?,
            scheduled_date       = ?,
            end_date             = ?,
            start_time           = ?,
            end_time             = ?,
            address_id           = ?,
            status_id            = ?,
            updated_at           = CURRENT_TIMESTAMP
        WHERE id = ?
        """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(SQL)) {

            // 1. Các FK có thể null ⇒ dùng setObject
            ps.setObject(1, schedule.getTechnicalRequestId());          // technical_request_id
            // cột campaign_id nằm ở tham số thứ 2
            if (schedule.getCampaignId() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, schedule.getCampaignId());
            }
            // campaign_id

            // 2. Các cột kiểu chuỗi
            ps.setString(3, schedule.getColor());                       // color

            // 3. Ngày – giờ
            ps.setObject(4, schedule.getScheduledDate());               // scheduled_date (NOT NULL)
            ps.setObject(5, schedule.getEndDate());                     // end_date
            ps.setObject(6, schedule.getStartTime());                   // start_time
            ps.setObject(7, schedule.getEndTime());                     // end_time

            // 4. Address & Status
            ps.setInt(8, schedule.getAddressId());                   // address_id (NOT NULL)
            ps.setObject(9, schedule.getStatusId());                    // status_id  (FK ⇒ INT)

            // 5. WHERE id = ?
            ps.setInt(10, schedule.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int addMaintenanceScheduleAndReturnId(MaintenanceSchedule schedule) {
        String sql = "INSERT INTO MaintenanceSchedules "
                + "(technical_request_id, campaign_id, color, scheduled_date, end_date, start_time, end_time, address_id, status_id, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, NULL)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, schedule.getTechnicalRequestId());
            ps.setObject(2, schedule.getCampaignId());
            ps.setString(3, schedule.getColor());
            ps.setObject(4, schedule.getScheduledDate());
            ps.setObject(5, schedule.getEndDate());
            ps.setObject(6, schedule.getStartTime());
            ps.setObject(7, schedule.getEndTime());
            ps.setObject(8, schedule.getAddressId());
            ps.setObject(9, schedule.getStatusId()); // status_id là int

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

    // Thêm hàm private này vào trong class ScheduleDAO
    private MaintenanceSchedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setId(rs.getInt("id"));
        schedule.setTechnicalRequestId(rs.getObject("technical_request_id", Integer.class));
        schedule.setCampaignId(rs.getObject("campaign_id") != null ? rs.getInt("campaign_id") : null);
        schedule.setColor(rs.getString("color"));
        schedule.setScheduledDate(rs.getObject("scheduled_date", LocalDate.class));
        schedule.setEndDate(rs.getObject("end_date", LocalDate.class));
        schedule.setStartTime(rs.getObject("start_time", LocalTime.class));
        schedule.setEndTime(rs.getObject("end_time", LocalTime.class));
        schedule.setStatusId(rs.getObject("status_id", Integer.class));
        schedule.setStatusName(rs.getString("status_name"));
        schedule.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
        schedule.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));
        // Lấy tất cả các trường
        String requestTitle = rs.getString("request_title");
        String requestDescription = rs.getString("request_description");
        String campaignTitle = rs.getString("campaign_title");
        String campaignDescription = rs.getString("campaign_description");

        // Ưu tiên campaign, nếu null thì lấy request
        schedule.setTitle(
                (campaignTitle != null && !campaignTitle.trim().isEmpty())
                ? campaignTitle
                : requestTitle
        );
        schedule.setNotes(
                (campaignDescription != null && !campaignDescription.trim().isEmpty())
                ? campaignDescription
                : requestDescription
        );
        // Địa chỉ
        Integer addressId = rs.getObject("address_id", Integer.class);
        schedule.setAddressId(addressId);
        if (addressId != null) {
            Address addr = new Address();
            addr.setId(addressId);
            addr.setStreetAddress(rs.getString("street_address"));
            String fullAddress = String.format("%s, %s, %s, %s",
                    rs.getString("street_address"),
                    rs.getString("ward_name"),
                    rs.getString("district_name"),
                    rs.getString("province_name")
            );
            addr.setFullAddress(fullAddress);
            schedule.setAddress(addr);
        }
        return schedule;
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

    // MỚI: Thêm phương thức NẠP CHỒNG (overloaded) để hỗ trợ transaction
// Chỉ dùng cho các servlet cần transaction như AddCampaignServlet
    public int addMaintenanceScheduleAndReturnId(MaintenanceSchedule schedule, Connection conn) throws SQLException {
        String sql = "INSERT INTO MaintenanceSchedules "
                + "(campaign_id, scheduled_date, status_id, color, address_id, start_time, end_time, end_date) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Sử dụng connection được truyền vào để đảm bảo transaction
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Dùng setObject để xử lý null an toàn cho các ID có thể không có
            if (schedule.getCampaignId() != null && schedule.getCampaignId() > 0) {
                ps.setInt(1, schedule.getCampaignId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            ps.setDate(2, java.sql.Date.valueOf(schedule.getScheduledDate()));
            ps.setInt(3, schedule.getStatusId());
            ps.setString(4, schedule.getColor());
            ps.setInt(5, schedule.getAddressId());

            // Xử lý các trường thời gian có thể bị null
            if (schedule.getStartTime() != null) {
                ps.setTime(6, java.sql.Time.valueOf(schedule.getStartTime()));
            } else {
                ps.setNull(6, java.sql.Types.TIME);
            }

            if (schedule.getEndTime() != null) {
                ps.setTime(7, java.sql.Time.valueOf(schedule.getEndTime()));
            } else {
                ps.setNull(7, java.sql.Types.TIME);
            }

            if (schedule.getEndDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(schedule.getEndDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Tạo lịch trình thất bại, không có dòng nào được thêm.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Trả về ID của lịch trình vừa tạo
                } else {
                    throw new SQLException("Tạo lịch trình thất bại, không lấy được ID.");
                }
            }
        }
    }

    // Lấy 1 lịch trình theo campaignId, kèm statusName và thông tin địa chỉ (JOIN Statuses + Addresses + Wards + Districts + Provinces)
// Nếu có nhiều lịch trình cùng campaign, lấy bản mới nhất theo scheduled_date (bạn có thể đổi tiêu chí ORDER BY nếu muốn).
    // TRONG FILE: ScheduleDAO.java
    // TRONG FILE: ScheduleDAO.java
    public MaintenanceSchedule getMaintenanceScheduleWithStatusByCampaignId(int campaignId) {
        // 1. Câu lệnh SQL chỉ để lấy thông tin Schedule, không JOIN với Assignments nữa
        final String sql = "SELECT "
                + "  ms.id, ms.campaign_id, ms.color, ms.scheduled_date, ms.end_date, "
                + "  ms.start_time, ms.end_time, ms.address_id, ms.status_id, "
                + "  s.status_name, "
                + "  a.street_address, a.full_address, a.ward_id, a.district_id, a.province_id, "
                + "  w.name AS ward_name, d.name AS district_name, p.name AS province_name "
                + "FROM MaintenanceSchedules ms "
                + "LEFT JOIN Statuses s ON s.id = ms.status_id "
                + "LEFT JOIN Addresses a ON a.id = ms.address_id "
                + "LEFT JOIN Wards w ON w.id = a.ward_id "
                + "LEFT JOIN Districts d ON d.id = a.district_id "
                + "LEFT JOIN Provinces p ON p.id = a.province_id "
                + "WHERE ms.campaign_id = ? "
                + "ORDER BY ms.scheduled_date DESC, ms.id DESC "
                + "LIMIT 1";

        MaintenanceSchedule schedule = null;

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {

                // Chỉ cần một lệnh if, không cần vòng lặp while ở đây
                if (rs.next()) {
                    schedule = new MaintenanceSchedule();
                    schedule.setId(rs.getInt("id"));
                    schedule.setCampaignId(rs.getInt("campaign_id"));
                    schedule.setColor(rs.getString("color"));

                    // Dùng getObject để an toàn hơn với các giá trị NULL
                    schedule.setScheduledDate(rs.getObject("scheduled_date", LocalDate.class));
                    schedule.setEndDate(rs.getObject("end_date", LocalDate.class));
                    schedule.setStartTime(rs.getObject("start_time", LocalTime.class));
                    schedule.setEndTime(rs.getObject("end_time", LocalTime.class));

                    schedule.setStatusId(rs.getInt("status_id"));
                    schedule.setStatusName(rs.getString("status_name"));

                    // Xử lý đối tượng Address
                    Integer addressId = (Integer) rs.getObject("address_id");
                    if (addressId != null) {
                        Address address = new Address();
                        address.setId(addressId);
                        address.setStreetAddress(rs.getString("street_address"));
                        address.setFullAddress(rs.getString("full_address"));
                        address.setWard(new Ward(rs.getInt("ward_id"), rs.getString("ward_name")));
                        address.setDistrict(new District(rs.getInt("district_id"), rs.getString("district_name")));
                        address.setProvince(new Province(rs.getInt("province_id"), rs.getString("province_name")));
                        schedule.setAddress(address);
                    }
                }
            }

            // 2. Nếu đã tìm thấy lịch trình, gọi hàm khác để lấy danh sách nhân viên
            if (schedule != null) {
                // Tái sử dụng hàm bạn đã có!
                List<Integer> assignedUserIds = getAssignedUserIdsByScheduleId(schedule.getId());
                schedule.setAssignedUserIds(assignedUserIds);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return schedule;
    }
// Cập nhật status_id cho một MaintenanceSchedule theo id

    public boolean updateStatusIdById(int scheduleId, int statusId) {
        final String sql = "UPDATE MaintenanceSchedules SET status_id = ? WHERE id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ps.setInt(2, scheduleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Code cbi cho upđate campaign Đổi status_id thành "Đã hủy" cho tất cả lịch
     * của campaign mà thời điểm kết thúc CHƯA QUA (future hoặc còn lại trong
     * hôm nay).
     *
     * @param campaignId id chiến dịch
     * @param canceledStatusId id status "Đã hủy"
     * @param conn connection đang dùng (transaction bên ngoài)
     * @return số dòng bị ảnh hưởng
     */
    public int cancelFutureSchedulesByCampaignId(int campaignId, int canceledStatusId, Connection conn) throws SQLException {
        // end_date có thì ưu tiên; không có thì fallback scheduled_date
        final String sql
                = "UPDATE MaintenanceSchedules "
                + "   SET status_id = ?, updated_at = CURRENT_TIMESTAMP "
                + " WHERE campaign_id = ? "
                + "   AND ( "
                + "         (end_date IS NOT NULL AND "
                + "             ( end_date > CURRENT_DATE "
                + "               OR (end_date = CURRENT_DATE AND (end_time IS NULL OR end_time >= CURRENT_TIME)) "
                + "             ) "
                + "         ) "
                + "         OR "
                + "         (end_date IS NULL AND "
                + "             ( scheduled_date > CURRENT_DATE "
                + "               OR (scheduled_date = CURRENT_DATE AND (end_time IS NULL OR end_time >= CURRENT_TIME)) "
                + "             ) "
                + "         ) "
                + "       )";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, canceledStatusId);
            ps.setInt(2, campaignId);
            return ps.executeUpdate();
        }
    }

    /**
     * Overload: tự tìm id “Đã hủy” và tự mở connection.
     */
    public int cancelFutureSchedulesByCampaignId(int campaignId) {
        int affected = 0;
        try (Connection conn = DBContext.getConnection()) {
            StatusDAO statusDAO = new StatusDAO();
            Integer cancelId = statusDAO.getIdByName("Đã hủy", conn);
            if (cancelId == null) {
                throw new SQLException("Không tìm thấy status 'Đã hủy' trong bảng Statuses");
            }
            affected = cancelFutureSchedulesByCampaignId(campaignId, cancelId, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affected;
    }

    /**
     * Overload: đã biết sẵn id 'Đã hủy', tự mở connection.
     */
    public int cancelFutureSchedulesByCampaignId(int campaignId, int canceledStatusId) {
        try (Connection conn = DBContext.getConnection()) {
            return cancelFutureSchedulesByCampaignId(campaignId, canceledStatusId, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    // Cập nhật lịch trình đại diện

    public void updateScheduleCore(MaintenanceSchedule s, Connection conn) throws SQLException {
        String sql = "UPDATE MaintenanceSchedules SET address_id=?, status_id=?, scheduled_date=?, "
                + "end_date=?, start_time=?, end_time=?, color=? WHERE id=?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getAddressId());
            ps.setInt(2, s.getStatusId());
            ps.setObject(3, s.getScheduledDate()); // LocalDate → JDBC 4.2
            ps.setObject(4, s.getEndDate());
            ps.setObject(5, s.getStartTime());     // LocalTime
            ps.setObject(6, s.getEndTime());
            ps.setString(7, s.getColor());
            ps.setInt(8, s.getId());
            ps.executeUpdate();
        }
    }

    public List<MaintenanceSchedule> getAllTechnicalRequestsAndCampaignsIdAndTitle() throws SQLException {
        List<MaintenanceSchedule> list = new ArrayList<>();
        String sql
                = "SELECT tr.id AS source_id, tr.title, 'request' AS type FROM TechnicalRequests tr "
                + "UNION ALL "
                + "SELECT c.campaign_id AS source_id, c.name, 'campaign' AS type FROM Campaigns c";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MaintenanceSchedule ms = new MaintenanceSchedule();
                    ms.setId(rs.getInt("source_id"));
                    ms.setTitle(rs.getString("title"));
                    // Nếu cần lưu type để phân biệt campaign/request
                    // ms.setType(rs.getString("type")); // Hoặc custom getter
                    list.add(ms);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        // Tạo instance DAO
        ScheduleDAO dao = new ScheduleDAO();
        // Lấy danh sách lịch trình
        List<MaintenanceSchedule> schedules = dao.getAllMaintenanceSchedules();

        if (schedules.isEmpty()) {
            System.out.println("Không có lịch trình bảo trì nào trong CSDL!");
        } else {
            System.out.println("Danh sách lịch trình bảo trì:");
            int idx = 1;
            for (MaintenanceSchedule sch : schedules) {
                System.out.println("--- #" + (idx++) + " -------------------------");
                System.out.println("ID: " + sch.getId());
                System.out.println("Title: " + sch.getTitle());
                System.out.println("Notes: " + sch.getNotes());
                System.out.println("Scheduled Date: " + sch.getScheduledDate());
                System.out.println("End Date: " + sch.getEndDate());
                System.out.println("Start Time: " + sch.getStartTime());
                System.out.println("End Time: " + sch.getEndTime());
                System.out.println("Status: " + sch.getStatusName());
                System.out.println("Technical Request ID: " + sch.getTechnicalRequestId());
                System.out.println("Campaign ID: " + sch.getCampaignId());
                System.out.println("Color: " + sch.getColor());
                if (sch.getAddress() != null) {
                    System.out.println("Address: " + sch.getAddress().getFullAddress());
                } else {
                    System.out.println("Address: (không có)");
                }
                System.out.println("Created At: " + sch.getCreatedAt());
                System.out.println("Updated At: " + sch.getUpdatedAt());
            }
        }
    }

    // Thêm phương thức này vào trong file ScheduleDAO.java của bạn
    public MaintenanceSchedule getScheduleByTechnicalRequestId(int technicalRequestId) {
        MaintenanceSchedule schedule = null;

        // SỬA LẠI SQL: Bổ sung JOIN tới Provinces, Districts, Wards và lấy các trường name
        String sql = "SELECT ms.*, a.*, s.status_name, "
                + "p.name AS province_name, d.name AS district_name, w.name AS ward_name "
                + "FROM MaintenanceSchedules ms "
                + "LEFT JOIN Addresses a ON ms.address_id = a.id "
                + "LEFT JOIN Statuses s ON ms.status_id = s.id "
                + "LEFT JOIN Provinces p ON a.province_id = p.id " // <-- JOIN BỔ SUNG
                + "LEFT JOIN Districts d ON a.district_id = d.id " // <-- JOIN BỔ SUNG
                + "LEFT JOIN Wards w ON a.ward_id = w.id " // <-- JOIN BỔ SUNG
                + "WHERE ms.technical_request_id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, technicalRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    schedule = new MaintenanceSchedule();

                    // Lấy thông tin từ bảng MaintenanceSchedules và Statuses (giữ nguyên)
                    schedule.setId(rs.getInt("id"));
                    schedule.setTechnicalRequestId(rs.getInt("technical_request_id"));
                    schedule.setCampaignId(rs.getObject("campaign_id") != null ? rs.getInt("campaign_id") : null);
                    schedule.setColor(rs.getString("color"));
                    schedule.setScheduledDate(rs.getObject("scheduled_date", LocalDate.class));
                    schedule.setEndDate(rs.getObject("end_date", LocalDate.class));
                    schedule.setStartTime(rs.getObject("start_time", LocalTime.class));
                    schedule.setEndTime(rs.getObject("end_time", LocalTime.class));
                    schedule.setAddressId(rs.getInt("address_id"));
                    schedule.setStatusId(rs.getInt("status_id"));
                    schedule.setStatusName(rs.getString("status_name"));

                    // SỬA LẠI PHẦN MAPPING: Thêm code để tạo đối tượng Province, District, Ward
                    int addressId = rs.getInt("address_id");
                    if (addressId > 0) {
                        Address address = new Address();
                        address.setId(addressId);
                        address.setStreetAddress(rs.getString("street_address"));
                        address.setWardId(rs.getInt("ward_id"));
                        address.setDistrictId(rs.getInt("district_id"));
                        address.setProvinceId(rs.getInt("province_id"));

                        // Tạo và gán các đối tượng con chứa tên
                        address.setWard(new Ward(rs.getInt("ward_id"), rs.getString("ward_name")));
                        address.setDistrict(new District(rs.getInt("district_id"), rs.getString("district_name")));
                        address.setProvince(new Province(rs.getInt("province_id"), rs.getString("province_name")));

                        schedule.setAddress(address);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedule;
    }

    // Trả về đối tượng schedule (hoặc null nếu không tìm thấy)
    // ... (các phương thức khác của bạn như getScheduleByTechnicalRequestId, addMaintenanceSchedule...)
    /**
     * Cập nhật danh sách nhân viên được phân công cho một lịch trình. Hoạt động
     * bằng cách xóa tất cả các phân công cũ và thêm lại danh sách mới.
     *
     * @param scheduleId ID của lịch trình cần cập nhật.
     * @param employeeIds Danh sách ID của các nhân viên mới.
     * @throws SQLException
     */
    public void updateMaintenanceAssignments(int scheduleId, List<Integer> employeeIds) throws SQLException {
        String deleteSql = "DELETE FROM MaintenanceAssignments WHERE schedule_id = ?";
        String insertSql = "INSERT INTO MaintenanceAssignments (schedule_id, user_id) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;

        try {
            conn = DBContext.getConnection(); // Lấy kết nối từ DBContext của bạn
            conn.setAutoCommit(false); // Bắt đầu một transaction

            // Bước 1: Xóa tất cả các phân công cũ
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, scheduleId);
            deleteStmt.executeUpdate();

            // Bước 2: Nếu có danh sách nhân viên mới thì thêm vào
            if (employeeIds != null && !employeeIds.isEmpty()) {
                insertStmt = conn.prepareStatement(insertSql);
                for (Integer employeeId : employeeIds) {
                    insertStmt.setInt(1, scheduleId);
                    insertStmt.setInt(2, employeeId);
                    insertStmt.addBatch(); // Thêm vào batch để thực thi hàng loạt
                }
                insertStmt.executeBatch();
            }

            conn.commit(); // Hoàn tất transaction

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // Hoàn tác nếu có lỗi
            }
            e.printStackTrace(); // In lỗi ra console để debug
            throw new SQLException("Lỗi khi cập nhật phân công nhân viên.", e);
        } finally {
            if (deleteStmt != null) {
                deleteStmt.close();
            }
            if (insertStmt != null) {
                insertStmt.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true); // Trả lại trạng thái auto-commit
                conn.close();
            }
        }
    }
}
