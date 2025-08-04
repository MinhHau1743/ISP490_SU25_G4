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
            String error = null;

            // Lấy dữ liệu từ form
            String idStr = request.getParameter("id");
            String title = request.getParameter("title");
            String technicalRequestIdStr = request.getParameter("technicalRequestId");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduledDate");
            String endDateStr = request.getParameter("endDate");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String location = request.getParameter("location");
            String status = request.getParameter("status");
            String notes = request.getParameter("notes");

            // 1. Validate các trường bắt buộc
            if (idStr == null || idStr.trim().isEmpty()) {
                error = "ID lịch bảo trì không được để trống.";
            } else if (title == null || title.trim().isEmpty()) {
                error = "Vui lòng nhập tiêu đề.";
            } else if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
                error = "Vui lòng chọn ngày bắt đầu.";
            } else if (status == null || status.trim().isEmpty()) {
                error = "Vui lòng chọn trạng thái.";
            }

            // Khởi tạo các biến để parse
            int id = 0;
            Integer technicalRequestId = null;
            LocalDate scheduledDate = null;
            LocalDate endDate = null;
            LocalTime startTime = null;
            LocalTime endTime = null;

            // 2. Validate định dạng và parse dữ liệu
            if (error == null) {
                try {
                    // Parse ID
                    id = Integer.parseInt(idStr);
                    if (id <= 0) {
                        error = "ID lịch bảo trì không hợp lệ.";
                    }

                    // Parse technical request ID (có thể null)
                    if (technicalRequestIdStr != null && !technicalRequestIdStr.trim().isEmpty()) {
                        technicalRequestId = Integer.parseInt(technicalRequestIdStr);
                        if (technicalRequestId <= 0) {
                            error = "ID yêu cầu kỹ thuật không hợp lệ.";
                        }
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
                    error = "ID phải là số nguyên hợp lệ.";
                } catch (Exception ex) {
                    error = "Định dạng ngày hoặc giờ không đúng.";
                }
            }

            // 3. Validate logic nghiệp vụ
            if (error == null) {
                // Kiểm tra ngày không được ở quá khứ (trừ khi đang edit lịch đã tồn tại)
                if (scheduledDate.isBefore(LocalDate.now())) {
                    // Có thể cho phép edit lịch quá khứ, tùy business logic
                    // error = "Ngày bắt đầu không được ở quá khứ.";
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
                if (location != null && location.length() > 255) {
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

            // 4. Validate schedule có tồn tại không
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            MaintenanceSchedule schedule = null;
            if (error == null) {
                schedule = dao.getMaintenanceScheduleById(id);
                if (schedule == null) {
                    error = "Lịch bảo trì không tồn tại.";
                }
            }

            // 6. Nếu có lỗi, trả về form với thông báo lỗi
            if (error != null) {
                request.setAttribute("error", error);

                // Giữ lại dữ liệu đã nhập để user không phải nhập lại
                if (schedule == null) {
                    // Tạo schedule tạm để giữ dữ liệu form
                    schedule = new MaintenanceSchedule();
                    schedule.setId(id);
                }
                schedule.setTitle(title);
                schedule.setTechnicalRequestId(technicalRequestId);
                schedule.setColor(color);
                schedule.setScheduledDate(scheduledDate);
                schedule.setEndDate(endDate);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                schedule.setLocation(location);
                schedule.setStatus(status);
                schedule.setNotes(notes);

                request.setAttribute("schedule", schedule);

                // Lấy lại danh sách technical requests để hiển thị dropdown
                try {
                    TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
                    List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle();
                    request.setAttribute("technicalRequests", technicalRequests);
                } catch (Exception ex) {
                    // Log error but continue
                    ex.printStackTrace();
                }

                request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
                return;
            }

            // 7. Cập nhật các trường
            schedule.setTitle(title);
            schedule.setTechnicalRequestId(technicalRequestId);
            schedule.setColor(color != null && !color.trim().isEmpty() ? color : "#007bff");
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setLocation(location != null && !location.trim().isEmpty() ? location : null);
            schedule.setStatus(status);
            schedule.setNotes(notes != null && !notes.trim().isEmpty() ? notes : null);

            // 8. Cập nhật vào cơ sở dữ liệu
            boolean updated = dao.updateMaintenanceSchedule(schedule);
            if (updated) {
                response.sendRedirect(request.getContextPath() + "/listSchedule");
            } else {
                request.setAttribute("error", "Không thể cập nhật lịch bảo trì. Vui lòng thử lại.");
                request.setAttribute("schedule", schedule);

                // Lấy lại danh sách technical requests
                try {
                    TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
                    List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle();
                    request.setAttribute("technicalRequests", technicalRequests);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());

            // Cố gắng lấy lại dữ liệu để hiển thị form
            try {
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
                    MaintenanceSchedule schedule = dao.getMaintenanceScheduleById(id);
                    request.setAttribute("schedule", schedule);
                }

                TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
                List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle();
                request.setAttribute("technicalRequests", technicalRequests);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
        }
    }
}
