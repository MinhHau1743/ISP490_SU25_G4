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
    try {
        String error = null;

        // Lấy dữ liệu từ form request
        String technicalRequestIdStr = request.getParameter("technical_request_id");
        String title = request.getParameter("title");
        String color = request.getParameter("color");
        String scheduledDateStr = request.getParameter("scheduled_date");
        String endDateStr = request.getParameter("end_date");
        String startTimeStr = request.getParameter("start_time");
        String endTimeStr = request.getParameter("end_time");
        String location = request.getParameter("location");
        String status = request.getParameter("status");
        String notes = request.getParameter("notes");

        // 1. Validate các trường bắt buộc
        if (technicalRequestIdStr == null || technicalRequestIdStr.trim().isEmpty()) {
            error = "Vui lòng chọn yêu cầu kỹ thuật.";
        } else if (title == null || title.trim().isEmpty()) {
            error = "Vui lòng nhập tiêu đề.";
        } else if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
            error = "Vui lòng chọn ngày bắt đầu.";
        } else if (location == null || location.trim().isEmpty()) {
            error = "Vui lòng nhập địa điểm.";
        } else if (status == null || status.trim().isEmpty()) {
            error = "Vui lòng chọn trạng thái.";
        }

        // Khởi tạo các biến để parse
        int technicalRequestId = 0;
        LocalDate scheduledDate = null;
        LocalDate endDate = null;
        LocalTime startTime = null;
        LocalTime endTime = null;

        // 2. Validate định dạng và parse dữ liệu
        if (error == null) {
            try {
                // Parse technical request ID
                technicalRequestId = Integer.parseInt(technicalRequestIdStr);
                if (technicalRequestId <= 0) {
                    error = "ID yêu cầu kỹ thuật không hợp lệ.";
                }
                
                // Parse ngày
                scheduledDate = LocalDate.parse(scheduledDateStr);
                if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                    endDate = LocalDate.parse(endDateStr);
                }
                
                // Parse giờ (chỉ khi có)
                if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                    startTime = LocalTime.parse(startTimeStr);
                }
                if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                    endTime = LocalTime.parse(endTimeStr);
                }
                
            } catch (NumberFormatException ex) {
                error = "ID yêu cầu kỹ thuật phải là số nguyên.";
            } catch (Exception ex) {
                error = "Định dạng ngày hoặc giờ không đúng.";
            }
        }

        // 3. Validate logic nghiệp vụ
        if (error == null) {
            // Kiểm tra ngày không được ở quá khứ
            if (scheduledDate.isBefore(LocalDate.now())) {
                error = "Ngày bắt đầu không được ở quá khứ.";
            }
            
            // Kiểm tra ngày kết thúc phải >= ngày bắt đầu
            if (endDate != null && endDate.isBefore(scheduledDate)) {
                error = "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.";
            }
            
            // Kiểm tra giờ kết thúc > giờ bắt đầu (nếu cùng ngày hoặc không có end_date)
            if (startTime != null && endTime != null) {
                if (scheduledDate.equals(endDate) || endDate == null) {
                    if (!endTime.isAfter(startTime)) {
                        error = "Giờ kết thúc phải sau giờ bắt đầu.";
                    }
                }
            }
            
            // Validate độ dài các trường
            if (title.length() > 255) {
                error = "Tiêu đề không được vượt quá 255 ký tự.";
            }
            if (location.length() > 255) {
                error = "Địa điểm không được vượt quá 255 ký tự.";
            }
            if (notes != null && notes.length() > 1000) {
                error = "Ghi chú không được vượt quá 1000 ký tự.";
            }
            
            // Validate status có trong danh sách cho phép
            if (!status.equals("upcoming") && !status.equals("inprogress") && !status.equals("completed")) {
                error = "Trạng thái không hợp lệ.";
            }
            
            // Validate color format (nếu có)
            if (color != null && !color.trim().isEmpty()) {
                if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
                    error = "Mã màu không đúng định dạng.";
                }
            }
        }


        // 5. Nếu có lỗi, trả về form với thông báo lỗi
        if (error != null) {
            request.setAttribute("error", error);
            
            // Giữ lại dữ liệu đã nhập để user không phải nhập lại
            request.setAttribute("title", title);
            request.setAttribute("color", color);
            request.setAttribute("scheduled_date", scheduledDateStr);
            request.setAttribute("end_date", endDateStr);
            request.setAttribute("start_time", startTimeStr);
            request.setAttribute("end_time", endTimeStr);
            request.setAttribute("location", location);
            request.setAttribute("status", status);
            request.setAttribute("notes", notes);
            request.setAttribute("technical_request_id", technicalRequestIdStr);
            
            // Lấy lại danh sách technical requests để hiển thị dropdown
            try {
                List<TechnicalRequest> schedules = technicalDAO.getAllTechnicalRequestsIdAndTitle();
                request.setAttribute("schedules", schedules);
            } catch (SQLException ex) {
                Logger.getLogger(CreateScheduleController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
            return;
        }

        // 6. Tạo đối tượng MaintenanceSchedule và lưu vào DB
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

        // Gọi phương thức thêm mới
        boolean success = scheduleDAO.addMaintenanceSchedule(schedule);

        if (success) {
            response.sendRedirect("listSchedule");
        } else {
            request.setAttribute("error", "Không thể tạo lịch bảo trì. Vui lòng thử lại.");
            
            // Lấy lại danh sách để hiển thị dropdown
            try {
                List<TechnicalRequest> schedules = technicalDAO.getAllTechnicalRequestsIdAndTitle();
                request.setAttribute("schedules", schedules);
            } catch (SQLException ex) {
                Logger.getLogger(CreateScheduleController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
        }

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        
        // Lấy lại danh sách để hiển thị dropdown
        try {
            List<TechnicalRequest> schedules = technicalDAO.getAllTechnicalRequestsIdAndTitle();
            request.setAttribute("schedules", schedules);
        } catch (SQLException ex) {
            Logger.getLogger(CreateScheduleController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
    }
}

}
