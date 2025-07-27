package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.TechnicalRequest;

@WebServlet("/createSchedule")
public class CreateScheduleController extends HttpServlet {

    private MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
    private TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<TechnicalRequest> schedules = technicalDAO.getAllTechnicalRequestsIdAndTitle();
            request.setAttribute("schedules", schedules);
            request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(CreateScheduleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy dữ liệu từ form request (theo tên input truyền lên)
        
        try {
            int technicalRequestId = Integer.parseInt(request.getParameter("technical_request_id"));
            String title = request.getParameter("title");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduled_date"); // "yyyy-MM-dd"
            String endDateStr = request.getParameter("end_date");             // có thể null
            String startTimeStr = request.getParameter("start_time");         // "HH:mm:ss"
            String endTimeStr = request.getParameter("end_time");             // "HH:mm:ss"
            String location = request.getParameter("location");
            String status = request.getParameter("status");
            String notes = request.getParameter("notes");

            // Parse ngày giờ từ String sang LocalDate / LocalTime
            LocalDate scheduledDate = scheduledDateStr != null && !scheduledDateStr.isEmpty() ? LocalDate.parse(scheduledDateStr) : null;
            LocalDate endDate = endDateStr != null && !endDateStr.isEmpty() ? LocalDate.parse(endDateStr) : null;
            LocalTime startTime = startTimeStr != null && !startTimeStr.isEmpty() ? LocalTime.parse(startTimeStr) : null;
            LocalTime endTime = endTimeStr != null && !endTimeStr.isEmpty() ? LocalTime.parse(endTimeStr) : null;

            // Tạo đối tượng MaintenanceSchedule
            MaintenanceSchedule schedule = new MaintenanceSchedule();
            schedule.setTechnicalRequestId(technicalRequestId);
            schedule.setTitle(title);
            schedule.setColor(color);
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setLocation(location);
            schedule.setStatus(status);
            schedule.setNotes(notes);

            // Gọi phương thức thêm mới (add)
            boolean success = scheduleDAO.addMaintenanceSchedule(schedule);

            if (success) {
                response.sendRedirect("listSchedule");
            } else {
                request.setAttribute("error", "Failed to create schedule");
                request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid input data: " + e.getMessage());
            request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
        }
    }
}
