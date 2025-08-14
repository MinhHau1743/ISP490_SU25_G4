// File: src/main/java/vn/edu/fpt/controller/campaign/AddCampaignServlet.java
package vn.edu.fpt.controller.campaign;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Import các lớp DAO
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.dao.CampaignTypeDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.MaintenanceAssignmentDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.dao.StatusDAO;
import vn.edu.fpt.dao.UserDAO;

// Import các lớp Model và DBContext
import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.model.*;

@WebServlet(name = "AddCampaignServlet", urlPatterns = {"/create-campaign"})
public class AddCampaignServlet extends HttpServlet {

    // Khai báo DAO và Gson như biến thành viên để tối ưu hiệu năng
    private CampaignDAO campaignDAO;
    private CampaignTypeDAO campaignTypeDAO;
    private EnterpriseDAO enterpriseDAO;
    private MaintenanceScheduleDAO scheduleDAO;
    private MaintenanceAssignmentDAO assignmentDAO;
    private AddressDAO addressDAO;
    private StatusDAO statusDAO;
    private UserDAO userDAO;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        // Khởi tạo các DAO một lần duy nhất khi servlet bắt đầu
        campaignDAO = new CampaignDAO();
        campaignTypeDAO = new CampaignTypeDAO();
        enterpriseDAO = new EnterpriseDAO();
        scheduleDAO = new MaintenanceScheduleDAO();
        assignmentDAO = new MaintenanceAssignmentDAO();
        addressDAO = new AddressDAO();
        statusDAO = new StatusDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // "Tổng đài viên": Điều hướng yêu cầu dựa trên tham số 'action'
        if ("getDistricts".equals(action) || "getWards".equals(action)) {
            handleAddressAPI(request, response);
        } else {
            showCreateCampaignPage(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 1. Validate toàn bộ dữ liệu từ form
        Map<String, String> errors = validateCampaignData(request);

        // 2. Nếu có lỗi, quay lại form và hiển thị lỗi
        if (!errors.isEmpty()) {
            handleFormError(request, response, null, "Vui lòng kiểm tra lại các thông tin đã nhập.", errors);
            return;
        }

        // 3. Nếu không có lỗi, tiến hành xử lý transaction
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Lấy các giá trị đã được parse
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress");
            // Code mới đã sửa
            Address address = new Address();
            address.setProvinceId(provinceId);
            address.setDistrictId(districtId);
            address.setWardId(wardId);
            address.setStreetAddress(streetAddress);    
            int finalAddressId = addressDAO.findOrCreateAddress(address, conn);

            Campaign campaign = new Campaign();
            campaign.setName(request.getParameter("name"));
            campaign.setTypeId(Integer.parseInt(request.getParameter("typeId")));
            campaign.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            campaign.setDescription(request.getParameter("description"));
            campaign.setStatus("pending");
            int newCampaignId = campaignDAO.addCampaignAndReturnId(campaign, conn);

            MaintenanceSchedule schedule = new MaintenanceSchedule();
            schedule.setCampaignId(newCampaignId);
            schedule.setAddressId(finalAddressId);
            schedule.setStatusId(Integer.parseInt(request.getParameter("statusId")));
            schedule.setScheduledDate(LocalDate.parse(request.getParameter("scheduledDate")));
            schedule.setColor(request.getParameter("color"));

            String startTimeStr = request.getParameter("startTime");
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                schedule.setStartTime(LocalTime.parse(startTimeStr));
            }
            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                schedule.setEndDate(LocalDate.parse(endDateStr));
            }
            String endTimeStr = request.getParameter("endTime");
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                schedule.setEndTime(LocalTime.parse(endTimeStr));
            }

            int newScheduleId = scheduleDAO.addMaintenanceScheduleAndReturnId(schedule, conn);

            int assignedUserId = Integer.parseInt(request.getParameter("assignedUserId"));
            assignmentDAO.addAssignment(newScheduleId, assignedUserId, conn);

            conn.commit();
            response.sendRedirect(request.getContextPath() + "/campaigns?action=list&create_status=success");

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

    // ======================================================================
    // CÁC PHƯƠNG THỨC HỖ TRỢ (HELPER METHODS)
    // ======================================================================
    private void showCreateCampaignPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("campaignTypes", campaignTypeDAO.getAllCampaignTypes());
            request.setAttribute("enterpriseList", enterpriseDAO.getAllEnterprises());
            request.setAttribute("statusList", statusDAO.getAllStatuses());
            request.setAttribute("userList", userDAO.getAllActiveUsers());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            request.getRequestDispatcher("/jsp/customerSupport/addNewCampaignWithSchedule.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu cho form. Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
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
                int id = Integer.parseInt(idStr);
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

    private Map<String, String> validateCampaignData(HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Validate Tên chiến dịch
        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Tên chiến dịch không được để trống.");
        } else if (name.trim().length() > 255) {
            errors.put("name", "Tên chiến dịch không được vượt quá 255 ký tự.");
        }

        // Validate các ID từ dropdown
        validateId(request.getParameter("typeId"), "typeId", "Vui lòng chọn Loại chiến dịch.", errors);
        validateId(request.getParameter("enterpriseId"), "enterpriseId", "Vui lòng chọn Khách hàng.", errors);
        validateId(request.getParameter("statusId"), "statusId", "Vui lòng chọn Trạng thái.", errors);
        validateId(request.getParameter("assignedUserId"), "assignedUserId", "Vui lòng chọn Nhân viên.", errors);
        validateId(request.getParameter("province"), "province", "Vui lòng chọn Tỉnh/Thành phố.", errors);
        validateId(request.getParameter("district"), "district", "Vui lòng chọn Quận/Huyện.", errors);
        validateId(request.getParameter("ward"), "ward", "Vui lòng chọn Phường/Xã.", errors);

        // Validate Địa chỉ cụ thể
        String streetAddress = request.getParameter("streetAddress");
        if (streetAddress == null || streetAddress.trim().isEmpty()) {
            errors.put("streetAddress", "Địa chỉ cụ thể không được để trống.");
        }

        // Validate Ngày tháng
        LocalDate scheduledDate = null;
        try {
            String scheduledDateStr = request.getParameter("scheduledDate");
            if (scheduledDateStr == null || scheduledDateStr.isEmpty()) {
                errors.put("scheduledDate", "Ngày bắt đầu không được để trống.");
            } else {
                scheduledDate = LocalDate.parse(scheduledDateStr);
            }
        } catch (DateTimeParseException e) {
            errors.put("scheduledDate", "Định dạng ngày bắt đầu không hợp lệ.");
        }

        try {
            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endDateStr);
                if (scheduledDate != null && endDate.isBefore(scheduledDate)) {
                    errors.put("endDate", "Ngày kết thúc không được sớm hơn ngày bắt đầu.");
                }
            }
        } catch (DateTimeParseException e) {
            errors.put("endDate", "Định dạng ngày kết thúc không hợp lệ.");
        }

        return errors;
    }

    private void validateId(String idStr, String fieldName, String message, Map<String, String> errors) {
        if (idStr == null || idStr.isEmpty()) {
            errors.put(fieldName, message);
        } else {
            try {
                Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                errors.put(fieldName, "Giá trị chọn không hợp lệ.");
            }
        }
    }

    private void handleFormError(HttpServletRequest request, HttpServletResponse response, Connection conn, String errorMessage, Map<String, String> fieldErrors)
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
        request.setAttribute("param", request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0])));

        showCreateCampaignPage(request, response);
    }
}
