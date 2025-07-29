package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.model.MaintenanceSchedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@WebServlet(name = "updateScheduleController", urlPatterns = {"/updateScheduleTime"})
public class UpdateScheduleTimeController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đọc JSON từ request body
        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONObject json = new JSONObject(jsonBuilder.toString());

        // Lấy dữ liệu từ JSON
        int id = json.getInt("id");
        LocalDate scheduledDate = LocalDate.parse(json.getString("scheduledDate"));
        LocalDate endDate = json.isNull("endDate") ? null : LocalDate.parse(json.getString("endDate"));
        LocalTime startTime = json.isNull("startTime") ? null : LocalTime.parse(json.getString("startTime"));

        // Cập nhật vào cơ sở dữ liệu
        MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
        MaintenanceSchedule schedule = dao.getMaintenanceScheduleById(id); // Giả sử có phương thức này để lấy schedule cũ
        if (schedule != null) {
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            dao.updateMaintenanceSchedule(schedule); // Giả sử có phương thức update
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}