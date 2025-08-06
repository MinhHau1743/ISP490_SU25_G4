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
import java.time.format.DateTimeParseException;
import java.util.List;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.TechnicalRequest;

@WebServlet("/createSchedule")
public class CreateScheduleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
        AddressDAO addressDAO = new AddressDAO(); // Dùng AddressDAO

        try {
            // Lấy danh sách cho cả 2 dropdown
            List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle();
            List<Province> provinces = addressDAO.getAllProvinces(); // Lấy danh sách tỉnh thành

            // Đặt cả 2 danh sách vào request
            request.setAttribute("technicalRequests", technicalRequests);
            request.setAttribute("provinces", provinces);

            request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
        AddressDAO addressDAO = new AddressDAO();
        TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();

        try {
            // === 1. GET FORM DATA ===
            String technicalRequestIdStr = request.getParameter("technical_request_id");
            String title = request.getParameter("title");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduled_date");
            String endDateStr = request.getParameter("end_date");
            String startTimeStr = request.getParameter("start_time");
            String endTimeStr = request.getParameter("end_time");
            String status = request.getParameter("status");
            String notes = request.getParameter("notes");

            // New Address Data
            String streetAddress = request.getParameter("streetAddress");
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            // === 2. VALIDATE DATA ===
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tiêu đề.");
            }
            if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu.");
            }
            if (provinceIdStr == null || provinceIdStr.trim().isEmpty()
                    || districtIdStr == null || districtIdStr.trim().isEmpty()
                    || wardIdStr == null || wardIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, và Phường/Xã.");
            }

            // === 3. PARSE AND CREATE OBJECTS ===
            LocalDate scheduledDate = LocalDate.parse(scheduledDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;
            LocalTime startTime = (startTimeStr != null && !startTimeStr.isEmpty()) ? LocalTime.parse(startTimeStr) : null;
            LocalTime endTime = (endTimeStr != null && !endTimeStr.isEmpty()) ? LocalTime.parse(endTimeStr) : null;

            // Business Logic Validation
            if (scheduledDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày bắt đầu không được ở trong quá khứ.");
            }
            if (endDate != null && endDate.isBefore(scheduledDate)) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            }
            if (startTime != null && endTime != null && (endDate == null || endDate.isEqual(scheduledDate)) && !endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu nếu trong cùng một ngày.");
            }

            // Find or create address and get its ID
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);

            // Create MaintenanceSchedule object
            MaintenanceSchedule schedule = new MaintenanceSchedule();
            if (technicalRequestIdStr != null && !technicalRequestIdStr.isEmpty()) {
                schedule.setTechnicalRequestId(Integer.parseInt(technicalRequestIdStr));
            }
            schedule.setTitle(title);
            schedule.setColor(color);
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setAddressId(addressId);
            schedule.setStatus(status != null ? status : "upcoming");
            schedule.setNotes(notes);

            // === 4. SAVE TO DATABASE ===
            boolean success = scheduleDAO.addMaintenanceSchedule(schedule);

            if (success) {
                response.sendRedirect("listSchedule");
            } else {
                throw new Exception("Không thể tạo lịch bảo trì do lỗi cơ sở dữ liệu.");
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {
            request.setAttribute("error", e.getMessage());
            forwardToForm(request, response, technicalDAO);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
            forwardToForm(request, response, technicalDAO);
        }
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response, TechnicalRequestDAO technicalDAO)
            throws ServletException, IOException {

        // Persist all user-entered data
        request.setAttribute("title", request.getParameter("title"));
        request.setAttribute("color", request.getParameter("color"));
        request.setAttribute("scheduled_date", request.getParameter("scheduled_date"));
        request.setAttribute("end_date", request.getParameter("end_date"));
        request.setAttribute("start_time", request.getParameter("start_time"));
        request.setAttribute("end_time", request.getParameter("end_time"));
        request.setAttribute("status", request.getParameter("status"));
        request.setAttribute("notes", request.getParameter("notes"));
        request.setAttribute("technical_request_id", request.getParameter("technical_request_id"));
        request.setAttribute("streetAddress", request.getParameter("streetAddress"));
        request.setAttribute("provinceId", request.getParameter("province"));
        request.setAttribute("districtId", request.getParameter("district"));
        request.setAttribute("wardId", request.getParameter("ward"));

        // Reload data for dropdowns
        try {
            AddressDAO addressDAO = new AddressDAO();
            request.setAttribute("technicalRequests", technicalDAO.getAllTechnicalRequestsIdAndTitle());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            if (request.getParameter("province") != null && !request.getParameter("province").isEmpty()) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(Integer.parseInt(request.getParameter("province"))));
            }
            if (request.getParameter("district") != null && !request.getParameter("district").isEmpty()) {
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(Integer.parseInt(request.getParameter("district"))));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
    }
}
