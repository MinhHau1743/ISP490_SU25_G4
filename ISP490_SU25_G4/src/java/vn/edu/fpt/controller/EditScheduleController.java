package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.User;

@WebServlet(name = "EditScheduleController", urlPatterns = {"/updateSchedule"})
public class EditScheduleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Use DAOs
        MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
        TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
        AddressDAO addressDAO = new AddressDAO();
        UserDAO userDAO = new UserDAO();

        try {
            // 1. Lấy và validate ID từ URL
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing schedule ID");
                return;
            }
            int id = Integer.parseInt(idStr);

            // 2. Lấy dữ liệu lịch trình chính
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Schedule not found");
                return;
            }

            // 3. Lấy dữ liệu cần thiết cho các dropdown
            List<User> assignments = userDAO.getAllTechnicalStaffIdAndFullName();
            List<TechnicalRequest> technicalRequests = technicalDAO.getAllTechnicalRequestsIdAndTitle();
            List<Province> provinces = addressDAO.getAllProvinces();
            List<Integer> assignedUserIds = scheduleDAO.getAssignedUserIdsByScheduleId(id);

// 2. Chuyển đổi List thành Map để tra cứu nhanh hơn
            Map<Integer, Boolean> assignedUserMap = new HashMap<>();
            for (Integer userId : assignedUserIds) {
                assignedUserMap.put(userId, true);
            }
// 3. Gửi Map này sang JSP thay vì List
            request.setAttribute("assignedUserMap", assignedUserMap);
            request.setAttribute("assignments", assignments);
            request.setAttribute("schedule", schedule);
            request.setAttribute("technicalRequests", technicalRequests);
            request.setAttribute("provinces", provinces);

            // 5. Tải sẵn danh sách Quận/Huyện và Phường/Xã nếu lịch trình đã có địa chỉ
            if (schedule.getFullAddress() != null) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(schedule.getFullAddress().getProvinceId()));
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(schedule.getFullAddress().getDistrictId()));
            }

            // 6. Chuyển hướng đến trang JSP
            request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid schedule ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
        AddressDAO addressDAO = new AddressDAO();
        TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();

        try {
            // === 1. LẤY DỮ LIỆU TỪ FORM ===
            int id = Integer.parseInt(request.getParameter("id"));
            String technicalRequestIdStr = request.getParameter("technicalRequestId");
            String title = request.getParameter("title");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduledDate");
            String endDateStr = request.getParameter("endDate");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String status = request.getParameter("status");
            String notes = request.getParameter("notes");

            // Dữ liệu địa chỉ mới
            String streetAddress = request.getParameter("streetAddress");
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            // === 2. VALIDATE DỮ LIỆU ===
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tiêu đề.");
            }
            if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu.");
            }
            if (provinceIdStr == null || provinceIdStr.isEmpty()
                    || districtIdStr == null || districtIdStr.isEmpty()
                    || wardIdStr == null || wardIdStr.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, và Phường/Xã.");
            }

            // === 3. PARSE VÀ CẬP NHẬT ĐỐI TƯỢNG ===
            // Lấy đối tượng schedule hiện tại từ DB để cập nhật
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                throw new Exception("Lịch bảo trì không còn tồn tại.");
            }

            // Parse và validate ngày/giờ
            LocalDate scheduledDate = LocalDate.parse(scheduledDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;
            LocalTime startTime = (startTimeStr != null && !startTimeStr.isEmpty()) ? LocalTime.parse(startTimeStr) : null;
            LocalTime endTime = (endTimeStr != null && !endTimeStr.isEmpty()) ? LocalTime.parse(endTimeStr) : null;

            if (endDate != null && endDate.isBefore(scheduledDate)) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            }
            if (startTime != null && endTime != null && (endDate == null || endDate.isEqual(scheduledDate)) && !endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu nếu trong cùng một ngày.");
            }

            // Tạo hoặc tìm địa chỉ và lấy ID
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);

            // Cập nhật các thuộc tính của đối tượng schedule
            // (Các phần lấy parameter và tạo đối tượng schedule ở trên...)
            if (technicalRequestIdStr != null && !technicalRequestIdStr.isEmpty()) {
                schedule.setTechnicalRequestId(Integer.parseInt(technicalRequestIdStr));
            } else {
                schedule.setTechnicalRequestId(null);
            }
            schedule.setTitle(title);
            schedule.setColor(color);
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setAddressId(addressId);
            schedule.setStatus(status);
            schedule.setNotes(notes);

// === 4. LƯU THAY ĐỔI VÀO DATABASE ===
            boolean scheduleUpdated = scheduleDAO.updateMaintenanceSchedule(schedule);

// --- PHẦN TÍCH HỢP ---
// Chỉ tiếp tục cập nhật phân công nếu lịch trình đã được cập nhật thành công
            if (scheduleUpdated) {
                // 4.1. Lấy danh sách ID nhân viên mới từ form
                String[] assignedUserIdsStr = request.getParameterValues("assignedUserIds");
                List<Integer> newUserIds = new ArrayList<>();
                if (assignedUserIdsStr != null) {
                    for (String userIdStr : assignedUserIdsStr) {
                        try {
                            newUserIds.add(Integer.parseInt(userIdStr));
                        } catch (NumberFormatException e) {
                            // Bỏ qua các giá trị không hợp lệ hoặc log lỗi nếu cần
                            System.err.println("Invalid user ID format: " + userIdStr);
                        }
                    }
                }

                // 4.2. Gọi DAO để cập nhật bảng MaintenanceAssignments
                boolean assignmentsUpdated = scheduleDAO.updateAssignmentsForSchedule(schedule.getId(), newUserIds);

                // 4.3. Kiểm tra kết quả và chuyển hướng
                if (assignmentsUpdated) {
                    // CHỈ KHI CẢ HAI ĐỀU THÀNH CÔNG
                    response.sendRedirect(request.getContextPath() + "/listSchedule");
                } else {
                    // Trường hợp lịch trình cập nhật OK, nhưng phân công thất bại
                    // Cần có cơ chế xử lý lỗi tốt hơn, ví dụ: rollback hoặc báo lỗi cụ thể
                    throw new Exception("Cập nhật lịch bảo trì thành công, nhưng cập nhật phân công nhân viên thất bại.");
                }

            } else {
                // Trường hợp cập nhật lịch trình chính thất bại ngay từ đầu
                throw new Exception("Cập nhật lịch bảo trì thất bại do lỗi cơ sở dữ liệu.");
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {
            // Bắt lỗi validation hoặc lỗi parse
            request.setAttribute("error", e.getMessage());
            forwardToForm(request, response, technicalDAO);
        } catch (Exception e) {
            // Bắt các lỗi chung khác
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
            forwardToForm(request, response, technicalDAO);
        }
    }

    // Hàm trợ giúp để tránh lặp code khi có lỗi
    private void forwardToForm(HttpServletRequest request, HttpServletResponse response, TechnicalRequestDAO technicalDAO)
            throws ServletException, IOException {

        // Cố gắng lấy lại đối tượng schedule để điền form
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            request.setAttribute("schedule", schedule); // Luôn gửi lại đối tượng gốc
        } catch (Exception e) {
            // Bỏ qua nếu không lấy được
        }

        // Tải lại danh sách cho các dropdown
        try {
            AddressDAO addressDAO = new AddressDAO();
            request.setAttribute("technicalRequests", technicalDAO.getAllTechnicalRequestsIdAndTitle());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            // Tải lại quận/huyện và phường/xã nếu người dùng đã chọn
            if (request.getParameter("province") != null && !request.getParameter("province").isEmpty()) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(Integer.parseInt(request.getParameter("province"))));
            }
            if (request.getParameter("district") != null && !request.getParameter("district").isEmpty()) {
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(Integer.parseInt(request.getParameter("district"))));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
    }
}
