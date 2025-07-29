package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.model.MaintenanceSchedule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.TechnicalRequest;

@WebServlet(name = "EditScheduleController", urlPatterns = {"/updateSchedule"})
public class EditScheduleController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy tham số id từ URL
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing schedule ID");
                return;
            }

            int id = Integer.parseInt(idStr);
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
            MaintenanceSchedule schedule = dao.getMaintenanceScheduleById(id);

            if (schedule == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Schedule not found");
                return;
            }

            // Lấy danh sách TechnicalRequests để hiển thị trong dropdown
            List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle(); 

            // Đặt dữ liệu vào request scope
            request.setAttribute("schedule", schedule);
            request.setAttribute("technicalRequests", technicalRequests);

            // Chuyển hướng đến editSchedule.jsp
            request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid schedule ID format");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy dữ liệu từ form
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String technicalRequestIdStr = request.getParameter("technicalRequestId");
            Integer technicalRequestId = technicalRequestIdStr.isEmpty() ? null : Integer.parseInt(technicalRequestIdStr);
            String color = request.getParameter("color");
            LocalDate scheduledDate = LocalDate.parse(request.getParameter("scheduledDate"));
            String endDateStr = request.getParameter("endDate");
            LocalDate endDate = endDateStr.isEmpty() ? null : LocalDate.parse(endDateStr);
            String startTimeStr = request.getParameter("startTime");
            LocalTime startTime = startTimeStr.isEmpty() ? null : LocalTime.parse(startTimeStr);
            String endTimeStr = request.getParameter("endTime");
            LocalTime endTime = endTimeStr.isEmpty() ? null : LocalTime.parse(endTimeStr);
            String location = request.getParameter("location");
            String status = request.getParameter("status");
            String notes = request.getParameter("notes");

            // Kiểm tra dữ liệu bắt buộc
            if (title == null || title.trim().isEmpty() || scheduledDate == null || status == null || status.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields: title, scheduledDate, or status");
                return;
            }

            // Tạo đối tượng MaintenanceSchedule
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            MaintenanceSchedule schedule = dao.getMaintenanceScheduleById(id);
            if (schedule == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Schedule not found");
                return;
            }

            // Cập nhật các trường
            schedule.setTitle(title);
            schedule.setTechnicalRequestId(technicalRequestId);
            schedule.setColor(color != null && !color.isEmpty() ? color : "#007bff"); // Mặc định màu nếu không có
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setLocation(location != null && !location.isEmpty() ? location : null);
            schedule.setStatus(status);
            schedule.setNotes(notes != null && !notes.isEmpty() ? notes : null);

            // Cập nhật vào cơ sở dữ liệu
            boolean updated = dao.updateMaintenanceSchedule(schedule);
            if (updated) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.sendRedirect(request.getContextPath() + "/listSchedule");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update schedule");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format for ID or technicalRequestId");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
}
