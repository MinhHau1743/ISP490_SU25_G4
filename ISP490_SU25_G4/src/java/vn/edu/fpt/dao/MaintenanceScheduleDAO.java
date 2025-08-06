package vn.edu.fpt.dao;

import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.MaintenanceAssignments;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceScheduleDAO extends DBContext {

    public List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        List<MaintenanceSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM MaintenanceSchedules";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceSchedule schedule = new MaintenanceSchedule();
                schedule.setId(rs.getInt("id"));
                schedule.setTechnicalRequestId(rs.getInt("technical_request_id"));
                schedule.setTitle(rs.getString("title"));
                schedule.setColor(rs.getString("color"));
                // Xử lý ngày tháng (MySQL -> LocalDate/LocalDateTime/LocalTime)
                Date scheduledDate = rs.getDate("scheduled_date");
                schedule.setScheduledDate(scheduledDate != null ? scheduledDate.toLocalDate() : null);

                Date endDate = rs.getDate("end_date");
                schedule.setEndDate(endDate != null ? endDate.toLocalDate() : null);

                Time startTime = rs.getTime("start_time");
                schedule.setStartTime(startTime != null ? startTime.toLocalTime() : null);

                Time endTime = rs.getTime("end_time");
                schedule.setEndTime(endTime != null ? endTime.toLocalTime() : null);

                schedule.setLocation(rs.getString("location"));
                schedule.setStatus(rs.getString("status"));
                schedule.setNotes(rs.getString("notes"));

                Timestamp createdAt = rs.getTimestamp("created_at");
                schedule.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

                Timestamp updatedAt = rs.getTimestamp("updated_at");
                schedule.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public boolean updateScheduleByDragDrop(
            int id,
            LocalDate scheduledDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        try (Connection conn = new DBContext().getConnection()) {

            // --- Nếu cần auto endDate hoặc endTime ---
            boolean autoEndDate = (endDate == null && scheduledDate != null);
            boolean autoEndTime = (endTime == null && startTime != null);

            // Chỉ truy vấn DB nếu cần tính duration
            if (autoEndDate || autoEndTime) {
                String sqlGetOld = "SELECT scheduled_date, end_date, start_time, end_time FROM MaintenanceSchedules WHERE id = ?";
                try (PreparedStatement psGet = conn.prepareStatement(sqlGetOld)) {
                    psGet.setInt(1, id);
                    try (ResultSet rs = psGet.executeQuery()) {
                        if (rs.next()) {
                            // --- Tính endDate mới nếu cần ---
                            if (autoEndDate) {
                                Date oldSchDate = rs.getDate("scheduled_date");
                                Date oldEndDate = rs.getDate("end_date");
                                if (oldSchDate != null && oldEndDate != null) {
                                    // duration (số ngày), kể cả âm (nếu dữ liệu sai)
                                    long duration = oldEndDate.toLocalDate().toEpochDay() - oldSchDate.toLocalDate().toEpochDay();
                                    if (duration >= 0) {
                                        endDate = scheduledDate.plusDays(duration);
                                    }
                                }
                            }

                            // --- Tính endTime mới nếu cần ---
                            if (autoEndTime) {
                                Time oldStart = rs.getTime("start_time");
                                Time oldEnd = rs.getTime("end_time");
                                if (oldStart != null && oldEnd != null) {
                                    long durationSec = oldEnd.toLocalTime().toSecondOfDay() - oldStart.toLocalTime().toSecondOfDay();
                                    // Sửa lại để endTime mới luôn hợp lệ nếu duration > 0
                                    if (durationSec > 0) {
                                        endTime = startTime.plusSeconds(durationSec);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String sql = "UPDATE MaintenanceSchedules SET scheduled_date = ?, end_date = ?, start_time = ?, end_time = ?, updated_at = NOW() WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(scheduledDate));

                if (endDate != null) {
                    ps.setDate(2, Date.valueOf(endDate));
                } else {
                    ps.setNull(2, java.sql.Types.DATE);
                }

                if (startTime != null) {
                    ps.setTime(3, Time.valueOf(startTime));
                } else {
                    ps.setNull(3, java.sql.Types.TIME);
                }

                if (endTime != null) {
                    ps.setTime(4, Time.valueOf(endTime));
                } else {
                    ps.setNull(4, java.sql.Types.TIME);
                }

                ps.setInt(5, id);

                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        String sql = "SELECT * FROM MaintenanceSchedules WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                schedule = mapResultSetToSchedule(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedule;
    }

    public boolean addMaintenanceSchedule(MaintenanceSchedule schedule) {
        String sql = "INSERT INTO MaintenanceSchedules "
                + "(technical_request_id, title, color, scheduled_date, end_date, start_time, end_time, location, status, notes, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getTechnicalRequestId());
            ps.setString(2, schedule.getTitle());
            ps.setString(3, schedule.getColor());
            ps.setObject(4, schedule.getScheduledDate());
            ps.setObject(5, schedule.getEndDate());
            ps.setObject(6, schedule.getStartTime());
            ps.setObject(7, schedule.getEndTime());
            ps.setString(8, schedule.getLocation());
            ps.setString(9, schedule.getStatus());
            ps.setString(10, schedule.getNotes());

            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMaintenanceSchedule(MaintenanceSchedule schedule) {
        if (schedule.getId() <= 0) {
            System.out.println("Error: ID is required for update.");
            return false;
        }

        String sql = "UPDATE MaintenanceSchedules SET technical_request_id = ?, title = ?,color = ?, scheduled_date = ?, end_date = ?, start_time = ?, end_time = ?, location = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getTechnicalRequestId());
            ps.setString(2, schedule.getTitle());
            ps.setString(3, schedule.getColor());
            ps.setObject(4, schedule.getScheduledDate());
            ps.setObject(5, schedule.getEndDate());
            ps.setObject(6, schedule.getStartTime());
            ps.setObject(7, schedule.getEndTime());
            ps.setString(8, schedule.getLocation());
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

    private MaintenanceSchedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setId(rs.getInt("id"));
        schedule.setTechnicalRequestId(rs.getInt("technical_request_id"));
        schedule.setTitle(rs.getString("title"));
        schedule.setColor(rs.getString("color"));
        schedule.setScheduledDate(rs.getObject("scheduled_date", LocalDate.class));
        schedule.setEndDate(rs.getObject("end_date", LocalDate.class));
        schedule.setStartTime(rs.getObject("start_time", LocalTime.class));
        schedule.setEndTime(rs.getObject("end_time", LocalTime.class));
        schedule.setLocation(rs.getString("location"));
        schedule.setStatus(rs.getString("status"));
        schedule.setNotes(rs.getString("notes"));
        schedule.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        schedule.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
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

    public static void main(String[] args) {
        MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
        List<MaintenanceSchedule> schedules = dao.getAllMaintenanceSchedules();

        System.out.println("Danh sách lịch bảo trì:");
        for (MaintenanceSchedule ms : schedules) {
            System.out.println("ID: " + ms.getId());
            System.out.println("Title: " + ms.getTitle());
            System.out.println("Title: " + ms.getColor());
            System.out.println("Scheduled Date: " + ms.getScheduledDate());
            System.out.println("Start Time: " + ms.getStartTime());
            System.out.println("End Time: " + ms.getEndTime());
            System.out.println("Location: " + ms.getLocation());
            System.out.println("Status: " + ms.getStatus());
            System.out.println("Created At: " + ms.getCreatedAt());
            System.out.println("Updated At: " + ms.getUpdatedAt());
            System.out.println("----------------------");
        }
        List<MaintenanceAssignments> list = dao.getAllMaintenanceAssignments();
        for (MaintenanceAssignments ma : list) {
            System.out.println("id: " + ma.getId()
                    + ", schedule_id: " + ma.getMaintenanceScheduleId()
                    + ", user_id: " + ma.getUserId()
                    + ", user_name: " + ma.getFullName());
        }
    }
}
