package vn.edu.fpt.dao;

import vn.edu.fpt.model.MaintenanceSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceScheduleDAO extends DBContext {

    public List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        List<MaintenanceSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM MaintenanceSchedules";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MaintenanceSchedule schedule = new MaintenanceSchedule();
                schedule.setId(rs.getInt("id"));
                schedule.setTechnicalRequestId(rs.getInt("technical_request_id"));
                schedule.setTitle(rs.getString("title"));

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
public static void main(String[] args) {
        MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
        List<MaintenanceSchedule> schedules = dao.getAllMaintenanceSchedules();

        System.out.println("Danh sách lịch bảo trì:");
        for (MaintenanceSchedule ms : schedules) {
            System.out.println("ID: " + ms.getId());
            System.out.println("Title: " + ms.getTitle());
            System.out.println("Scheduled Date: " + ms.getScheduledDate());
            System.out.println("Start Time: " + ms.getStartTime());
            System.out.println("End Time: " + ms.getEndTime());
            System.out.println("Location: " + ms.getLocation());
            System.out.println("Status: " + ms.getStatus());
            System.out.println("Created At: " + ms.getCreatedAt());
            System.out.println("Updated At: " + ms.getUpdatedAt());
            System.out.println("----------------------");
        }
    }
}

