// File: src/main/java/vn/edu/fpt/controller/campaign/AddCampaignServlet.java
package vn.edu.fpt.controller.campaign;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.dao.CampaignTypeDAO;
import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.MaintenanceAssignmentDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.dao.StatusDAO;
import vn.edu.fpt.dao.UserDAO;

import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Status;
import vn.edu.fpt.model.User;

@WebServlet(name = "AddCampaignServlet", urlPatterns = {"/create-campaign"})
public class AddCampaignServlet extends HttpServlet {

    // DAO
    private CampaignDAO campaignDAO;
    private CampaignTypeDAO campaignTypeDAO;
    private EnterpriseDAO enterpriseDAO;
    private MaintenanceScheduleDAO scheduleDAO;
    private MaintenanceAssignmentDAO assignmentDAO;
    private AddressDAO addressDAO;
    private StatusDAO statusDAO;
    private UserDAO userDAO;

    // Utils
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        campaignDAO = new CampaignDAO();
        campaignTypeDAO = new CampaignTypeDAO();
        enterpriseDAO = new EnterpriseDAO();
        scheduleDAO = new MaintenanceScheduleDAO();
        assignmentDAO = new MaintenanceAssignmentDAO();
        addressDAO = new AddressDAO();
        statusDAO = new StatusDAO();
        userDAO = new UserDAO();
    }

    // =========================== GET ===========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("getDistricts".equals(action) || "getWards".equals(action)) {
            handleAddressAPI(request, response);
        } else {
            showCreateCampaignPage(request, response);
        }
    }

    // =========================== POST ===========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Require login
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Validate inputs
        Map<String, String> errors = validateCampaignData(request);
        if (!errors.isEmpty()) {
            handleFormError(request, response, null, "Vui lòng kiểm tra lại các thông tin đã nhập.", errors);
            return;
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // 1) Address
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress").trim();

            Address address = new Address();
            address.setProvinceId(provinceId);
            address.setDistrictId(districtId);
            address.setWardId(wardId);
            address.setStreetAddress(streetAddress);

            int finalAddressId = addressDAO.findOrCreateAddress(address, conn);

            // 2) Campaign (DB đã bỏ cột status → KHÔNG set status ở Campaign)
            Campaign campaign = new Campaign();
            String campaignCode = "CAMP-" + System.currentTimeMillis();
            campaign.setCampaignCode(campaignCode);
            campaign.setName(request.getParameter("name").trim());
            campaign.setTypeId(Integer.parseInt(request.getParameter("typeId")));
            campaign.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            campaign.setDescription(request.getParameter("description")); // null/empty OK
            campaign.setCreatedBy(currentUser.getId());

            int newCampaignId = campaignDAO.addCampaignAndReturnId(campaign, conn);

            // 3) Schedule (đặt trạng thái từ Statuses.status_id)
            MaintenanceSchedule schedule = new MaintenanceSchedule();
            schedule.setCampaignId(newCampaignId);
            schedule.setAddressId(finalAddressId);

            int statusId = Integer.parseInt(request.getParameter("statusId"));
            schedule.setStatusId(statusId);

            // Dates (ISO từ <input type="date">)
            schedule.setScheduledDate(LocalDate.parse(request.getParameter("scheduledDate")));

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isBlank()) {
                schedule.setEndDate(LocalDate.parse(endDateStr.trim()));
            }

            // Times (optional, ISO HH:mm)
            String startTimeStr = request.getParameter("startTime");
            if (startTimeStr != null && !startTimeStr.isBlank()) {
                schedule.setStartTime(LocalTime.parse(startTimeStr.trim()));
            }
            String endTimeStr = request.getParameter("endTime");
            if (endTimeStr != null && !endTimeStr.isBlank()) {
                schedule.setEndTime(LocalTime.parse(endTimeStr.trim()));
            }

            // Color (optional, đã validate pattern)
            String color = request.getParameter("color");
            if (color != null && !color.isBlank()) {
                schedule.setColor(color.trim());
            }

            int newScheduleId = scheduleDAO.addMaintenanceScheduleAndReturnId(schedule, conn);

            // 4) Assignment
            int assignedUserId = Integer.parseInt(request.getParameter("assignedUserId"));
            assignmentDAO.addAssignment(newScheduleId, assignedUserId, conn);

            conn.commit();
            response.sendRedirect(request.getContextPath() + "/list-campaign?create_status=success");

        } catch (Exception e) {
            e.printStackTrace();
            handleFormError(request, response, conn, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), null);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // ====================== HELPERS (PAGE & API) ======================
    private void showCreateCampaignPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("campaignTypes", campaignTypeDAO.getAllCampaignTypes());
            request.setAttribute("enterpriseList", enterpriseDAO.getAllEnterprises());
            request.setAttribute("statusList", statusDAO.getAllStatuses());       // từ bảng Statuses
            request.setAttribute("userList", userDAO.getAllActiveUsers());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            request.getRequestDispatcher("/jsp/customerSupport/addNewCampaignWithSchedule.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu cho form. Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    private void handleAddressAPI(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("action");
        String idStr = request.getParameter("id");
        Object data = Collections.emptyList();

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                if ("getDistricts".equals(type)) {
                    data = addressDAO.getDistrictsByProvinceId(id);
                } else if ("getWards".equals(type)) {
                    data = addressDAO.getWardsByDistrictId(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush();
        }
    }

    // ====================== VALIDATION ======================
    private Map<String, String> validateCampaignData(HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        // --- Name ---
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Tên chiến dịch không được để trống.");
        } else {
            String trimmed = name.trim();
            if (trimmed.length() < 3 || trimmed.length() > 255) {
                errors.put("name", "Tên chiến dịch phải từ 3 đến 255 ký tự.");
            }
        }

        // --- Description (optional) ---
        String description = request.getParameter("description");
        if (description != null && description.length() > 4000) {
            errors.put("description", "Mô tả quá dài (tối đa 4000 ký tự).");
        }

        // --- Color (#RRGGBB) (optional) ---
        String color = request.getParameter("color");
        if (color != null && !color.isBlank() && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            errors.put("color", "Màu sắc không hợp lệ. Vui lòng nhập theo định dạng #RRGGBB.");
        }

        // --- Required IDs (positive integers) ---
        validateId(request.getParameter("typeId"), "typeId", "Vui lòng chọn Loại chiến dịch.", errors);
        validateId(request.getParameter("enterpriseId"), "enterpriseId", "Vui lòng chọn Khách hàng.", errors);
        validateId(request.getParameter("statusId"), "statusId", "Vui lòng chọn Trạng thái.", errors);
        validateId(request.getParameter("assignedUserId"), "assignedUserId", "Vui lòng chọn Nhân viên.", errors);
        validateId(request.getParameter("province"), "province", "Vui lòng chọn Tỉnh/Thành phố.", errors);
        validateId(request.getParameter("district"), "district", "Vui lòng chọn Quận/Huyện.", errors);
        validateId(request.getParameter("ward"), "ward", "Vui lòng chọn Phường/Xã.", errors);

        // --- Street Address ---
        String streetAddress = request.getParameter("streetAddress");
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            errors.put("streetAddress", "Địa chỉ cụ thể không được để trống.");
        } else if (streetAddress.trim().length() > 255) {
            errors.put("streetAddress", "Địa chỉ cụ thể quá dài (tối đa 255 ký tự).");
        }

        // --- Dates ---
        LocalDate scheduledDate = null;
        try {
            String scheduledDateStr = request.getParameter("scheduledDate");
            if (scheduledDateStr == null || scheduledDateStr.isBlank()) {
                errors.put("scheduledDate", "Ngày bắt đầu không được để trống.");
            } else {
                scheduledDate = LocalDate.parse(scheduledDateStr.trim()); // yyyy-MM-dd
            }
        } catch (DateTimeParseException e) {
            errors.put("scheduledDate", "Định dạng ngày bắt đầu không hợp lệ (yyyy-MM-dd).");
        }

        LocalDate endDate = null;
        String endDateStr = request.getParameter("endDate");
        if (endDateStr != null && !endDateStr.isBlank()) {
            try {
                endDate = LocalDate.parse(endDateStr.trim()); // yyyy-MM-dd
            } catch (DateTimeParseException e) {
                errors.put("endDate", "Định dạng ngày kết thúc không hợp lệ (yyyy-MM-dd).");
            }
        }

        // --- Times (optional) ---
        LocalTime startTime = null;
        LocalTime endTime = null;

        String startTimeStr = request.getParameter("startTime");
        if (startTimeStr != null && !startTimeStr.isBlank()) {
            try {
                startTime = LocalTime.parse(startTimeStr.trim()); // HH:mm
            } catch (DateTimeParseException e) {
                errors.put("startTime", "Giờ bắt đầu không hợp lệ (HH:mm).");
            }
        }

        String endTimeStr = request.getParameter("endTime");
        if (endTimeStr != null && !endTimeStr.isBlank()) {
            try {
                endTime = LocalTime.parse(endTimeStr.trim()); // HH:mm
            } catch (DateTimeParseException e) {
                errors.put("endTime", "Giờ kết thúc không hợp lệ (HH:mm).");
            }
        }

        // --- Business rules ---
        // 1) endDate >= scheduledDate (nếu cả hai có)
        if (scheduledDate != null && endDate != null && endDate.isBefore(scheduledDate)) {
            errors.put("endDate", "Ngày kết thúc không được sớm hơn ngày bắt đầu.");
        }

        // 2) Nếu có endTime mà không có startTime -> báo lỗi
        if (endTime != null && startTime == null) {
            errors.put("startTime", "Bạn đã nhập giờ kết thúc, vui lòng nhập giờ bắt đầu.");
        }

        // 3) Nếu cùng ngày (không có endDate hoặc endDate == scheduledDate) và có đủ 2 giờ -> endTime phải sau startTime
        boolean sameDay = (scheduledDate != null) && (endDate == null || endDate.equals(scheduledDate));
        if (sameDay && startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            errors.put("endTime", "Giờ kết thúc phải sau giờ bắt đầu trong cùng ngày.");
        }

        return errors;
    }

    /**
     * ID phải là số nguyên dương.
     */
    private void validateId(String idStr, String fieldName, String message, Map<String, String> errors) {
        if (idStr == null || idStr.isBlank()) {
            errors.put(fieldName, message);
            return;
        }
        try {
            int v = Integer.parseInt(idStr.trim());
            if (v <= 0) {
                errors.put(fieldName, "Giá trị chọn không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            errors.put(fieldName, "Giá trị chọn không hợp lệ.");
        }
    }

    // ====================== ERROR HANDLING ======================
    private void handleFormError(HttpServletRequest request, HttpServletResponse response,
            Connection conn, String errorMessage, Map<String, String> fieldErrors)
            throws ServletException, IOException {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        request.setAttribute("errorMessage", errorMessage);
        if (fieldErrors != null) {
            request.setAttribute("fieldErrors", fieldErrors);
        }

        // Giữ lại giá trị người dùng đã nhập
        request.setAttribute("param", request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0])));

        showCreateCampaignPage(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Create Campaign + First Schedule + Assignment (with strict server-side validation).";
    }
}
