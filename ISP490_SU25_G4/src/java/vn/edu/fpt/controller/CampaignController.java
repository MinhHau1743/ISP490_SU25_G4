package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Front Controller duy nhất cho tất cả các chức năng liên quan đến Campaign.
 * Gộp logic từ các servlet: List, Add, Edit, View, Delete.
 */
@WebServlet(name = "CampaignController", urlPatterns = {
    "/list-campaign", // Hiển thị danh sách
    "/add-campaign", // Hiển thị form thêm mới (GET)
    "/create-campaign", // Xử lý thêm mới (POST)
    "/edit-campaign", // Hiển thị form sửa (GET)
    "/update-campaign", // Xử lý cập nhật (POST)
    "/delete-campaign", // Xử lý xóa
    "/view-campaign" // Xem chi tiết
})
public class CampaignController extends HttpServlet {

    // ====================== KHAI BÁO CHUNG ======================
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final ZoneId VN_TZ = ZoneId.of("Asia/Bangkok");
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CampaignDAO campaignDAO;
    private CampaignTypeDAO campaignTypeDAO;
    private MaintenanceScheduleDAO scheduleDAO;
    private MaintenanceAssignmentDAO assignmentDAO;
    private EnterpriseDAO enterpriseDAO;
    private StatusDAO statusDAO;
    private UserDAO userDAO;
    private AddressDAO addressDAO;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        campaignDAO = new CampaignDAO();
        campaignTypeDAO = new CampaignTypeDAO();
        scheduleDAO = new MaintenanceScheduleDAO();
        assignmentDAO = new MaintenanceAssignmentDAO();
        enterpriseDAO = new EnterpriseDAO();
        statusDAO = new StatusDAO();
        userDAO = new UserDAO();
        addressDAO = new AddressDAO();
    }

    // ====================== BỘ ĐIỀU PHỐI (DISPATCHER) ======================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        String apiAction = request.getParameter("action");
        if (apiAction != null && (apiAction.equals("getDistricts") || apiAction.equals("getWards"))) {
            handleAddressAPI(request, response);
            return;
        }

        switch (action) {
            case "/list-campaign":
                handleListCampaign(request, response);
                break;
            case "/create-campaign":
                showCreateCampaignPage(request, response);
                break;
            case "/edit-campaign":
                showEditCampaignPage(request, response);
                break;
            case "/view-campaign":
                handleViewCampaign(request, response);
                break;
            case "/delete-campaign":
                handleDeleteCampaign(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getServletPath();

        switch (action) {
            case "/create-campaign":
                handleCreateCampaign(request, response);
                break;
            case "/update-campaign":
                handleUpdateCampaign(request, response);
                break;
            case "/delete-campaign":
                handleDeleteCampaign(request, response);
                break;
            case "/list-campaign":
                handleListCampaign(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // =====================================================================
    // ===== LOGIC TỪ CampaignListServlet =====
    // =====================================================================
    private void handleListCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            int pageSize = DEFAULT_PAGE_SIZE;
            String sizeStr = trimOrEmpty(request.getParameter("size"));
            if (!sizeStr.isEmpty()) {
                try {
                    int s = Integer.parseInt(sizeStr);
                    if (s > 0 && s <= 100) {
                        pageSize = s;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            int currentPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && pageStr.matches("\\d+")) {
                currentPage = Math.max(1, Integer.parseInt(pageStr));
            }

            String searchTerm = trimOrEmpty(request.getParameter("search"));
            Integer statusIdFilter = safeInt(request.getParameter("statusId"));

            String typeIdStr = request.getParameter("typeId");
            int typeIdFilter = 0;
            if (typeIdStr != null && !typeIdStr.trim().isEmpty()) {
                try {
                    int parsedId = Integer.parseInt(typeIdStr.trim());
                    if (parsedId > 0) {
                        typeIdFilter = parsedId;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            String startDateFilter = trimOrEmpty(request.getParameter("startDate"));
            String endDateFilter = trimOrEmpty(request.getParameter("endDate"));

            int totalRecords = campaignDAO.countCampaigns(searchTerm, statusIdFilter, typeIdFilter, startDateFilter, endDateFilter);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            List<Campaign> campaigns = campaignDAO.getCampaigns(currentPage, pageSize, searchTerm, statusIdFilter, typeIdFilter, startDateFilter, endDateFilter);

            int activeCampaigns = campaignDAO.countCampaignsByStatusName("Đang thực hiện");
            int completedCampaigns = campaignDAO.countCampaignsByStatusName("Hoàn thành");

            List<CampaignType> allCampaignTypes = campaignTypeDAO.getAllCampaignTypes();
            List<Status> statusList = statusDAO.getAllStatuses();

            StringBuilder queryString = new StringBuilder();
            if (!searchTerm.isEmpty()) {
                queryString.append("&search=").append(URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.name()));
            }
            if (statusIdFilter != null) {
                queryString.append("&statusId=").append(statusIdFilter);
            }
            if (typeIdFilter > 0) {
                queryString.append("&typeId=").append(typeIdFilter);
            }
            if (!startDateFilter.isEmpty()) {
                queryString.append("&startDate=").append(URLEncoder.encode(startDateFilter, StandardCharsets.UTF_8.name()));
            }
            if (!endDateFilter.isEmpty()) {
                queryString.append("&endDate=").append(URLEncoder.encode(endDateFilter, StandardCharsets.UTF_8.name()));
            }
            if (pageSize != DEFAULT_PAGE_SIZE) {
                queryString.append("&size=").append(pageSize);
            }

            request.setAttribute("campaigns", campaigns);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("activeCampaigns", activeCampaigns);
            request.setAttribute("completedCampaigns", completedCampaigns);
            request.setAttribute("allCampaignTypes", allCampaignTypes);
            request.setAttribute("statusList", statusList);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("statusIdFilter", statusIdFilter);
            request.setAttribute("typeIdFilter", typeIdFilter);
            request.setAttribute("startDateFilter", startDateFilter);
            request.setAttribute("endDateFilter", endDateFilter);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("queryString", queryString.toString());

            request.getRequestDispatcher("/jsp/customerSupport/listCampaign.jsp").forward(request, response);
        } catch (Exception e) {
            handleException(request, response, e, "tải danh sách chiến dịch");
        }
    }

    // =====================================================================
    // ===== LOGIC TỪ AddCampaignServlet =====
    // =====================================================================
    private void showCreateCampaignPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            loadFormDropdowns(request);
            request.getRequestDispatcher("/jsp/customerSupport/addNewCampaign.jsp").forward(request, response);
        } catch (Exception e) {
            handleException(request, response, e, "tải dữ liệu cho form thêm mới");
        }
    }

    private void handleCreateCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Map<String, String> errors = validateCampaignData(request, false);
        if (!errors.isEmpty()) {
            handleFormError(request, response, null, "Vui lòng kiểm tra lại các thông tin đã nhập.", errors);
            return;
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Trong handleCreateCampaign của CampaignController.java
// ...
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress").trim();

// MỚI: Gọi các hàm vừa thêm ở Bước 1 để lấy tên
            String wardName = addressDAO.getWardNameById(wardId, conn);
            String districtName = addressDAO.getDistrictNameById(districtId, conn);
            String provinceName = addressDAO.getProvinceNameById(provinceId, conn);

// MỚI: Ghép chuỗi địa chỉ đầy đủ theo đúng thứ tự
            String fullAddress = streetAddress + ", " + wardName + ", " + districtName + ", " + provinceName;

            Address address = new Address();
            address.setProvinceId(provinceId);
            address.setDistrictId(districtId);
            address.setWardId(wardId);
            address.setStreetAddress(streetAddress);
            address.setFullAddress(fullAddress); // MỚI: Gán chuỗi địa chỉ đầy đủ vào đối tượng

            int finalAddressId = addressDAO.findOrCreateAddress(address, conn);
// ...

            Campaign campaign = new Campaign();
            campaign.setCampaignCode("CAMP-" + System.currentTimeMillis());
            campaign.setName(request.getParameter("name").trim());
            campaign.setTypeId(Integer.parseInt(request.getParameter("typeId")));
            campaign.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            campaign.setDescription(request.getParameter("description"));
            campaign.setCreatedBy(currentUser.getId());
            int newCampaignId = campaignDAO.addCampaignAndReturnId(campaign, conn);

            MaintenanceSchedule schedule = new MaintenanceSchedule();
            schedule.setCampaignId(newCampaignId);
            schedule.setAddressId(finalAddressId);
            schedule.setStatusId(Integer.parseInt(request.getParameter("statusId")));
            schedule.setScheduledDate(LocalDate.parse(request.getParameter("scheduledDate")));

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isBlank()) {
                schedule.setEndDate(LocalDate.parse(endDateStr.trim()));
            }
            String startTimeStr = request.getParameter("startTime");
            if (startTimeStr != null && !startTimeStr.isBlank()) {
                schedule.setStartTime(LocalTime.parse(startTimeStr.trim()));
            }
            String endTimeStr = request.getParameter("endTime");
            if (endTimeStr != null && !endTimeStr.isBlank()) {
                schedule.setEndTime(LocalTime.parse(endTimeStr.trim()));
            }
            String color = request.getParameter("color");
            if (color != null && !color.isBlank()) {
                schedule.setColor(color.trim());
            }

            int newScheduleId = scheduleDAO.addMaintenanceScheduleAndReturnId(schedule, conn);
            int assignedUserId = Integer.parseInt(request.getParameter("assignedUserId"));
            assignmentDAO.addAssignment(newScheduleId, assignedUserId, conn);

            conn.commit();
            response.sendRedirect(request.getContextPath() + "/list-campaign?create_status=success");
        } catch (Exception e) {
            handleFormError(request, response, conn, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), null);
        } finally {
            closeConnection(conn);
        }
    }

    // =====================================================================
    // ===== LOGIC TỪ EditCampaignServlet =====
    // =====================================================================
    private void showEditCampaignPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (id <= 0) {
                throw new NumberFormatException();
            }

            Campaign campaign = campaignDAO.getCampaignById(id);
            if (campaign == null) {
                handleException(request, response, new Exception("Không tìm thấy chiến dịch."), "tải dữ liệu sửa");
                return;
            }
            MaintenanceSchedule ms = scheduleDAO.getMaintenanceScheduleWithStatusByCampaignId(id);

            request.setAttribute("campaign", campaign);
            request.setAttribute("maintenanceSchedule", ms);
            loadFormDropdowns(request);

            if (ms != null && ms.getAddress() != null) {
                Integer provinceId = ms.getAddress().getProvinceId();
                Integer districtId = ms.getAddress().getDistrictId();
                if (provinceId != null) {
                    request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(provinceId));
                }
                if (districtId != null) {
                    request.setAttribute("wards", addressDAO.getWardsByDistrictId(districtId));
                }
            }
            request.getRequestDispatcher("/jsp/customerSupport/editCampaign.jsp").forward(request, response);
        } catch (Exception e) {
            handleException(request, response, e, "tải dữ liệu form Edit Campaign");
        }
    }

    private void handleUpdateCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> errors = validateCampaignData(request, true);
        Integer campaignId = safeInt(request.getParameter("campaignId"));

        if (!errors.isEmpty()) {
            // Nạp lại dữ liệu cũ để hiển thị form lỗi
            request.setAttribute("campaign", campaignDAO.getCampaignById(campaignId));
            request.setAttribute("maintenanceSchedule", scheduleDAO.getMaintenanceScheduleWithStatusByCampaignId(campaignId));
            handleFormError(request, response, null, "Vui lòng kiểm tra lại các thông tin đã nhập.", errors);
            return;
        }

        User currentUser = (User) request.getSession().getAttribute("user");
        Integer updatedBy = (currentUser != null) ? currentUser.getId() : null;

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            Address address = new Address();
            address.setProvinceId(Integer.parseInt(request.getParameter("province")));
            address.setDistrictId(Integer.parseInt(request.getParameter("district")));
            address.setWardId(Integer.parseInt(request.getParameter("ward")));
            address.setStreetAddress(request.getParameter("streetAddress").trim());
            int addressId = addressDAO.findOrCreateAddress(address, conn);

            Campaign c = new Campaign();
            c.setCampaignId(campaignId);
            c.setName(request.getParameter("name").trim());
            c.setTypeId(Integer.parseInt(request.getParameter("typeId")));
            c.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            c.setDescription(request.getParameter("description"));
            c.setUpdatedBy(updatedBy);
            campaignDAO.updateCampaignCore(c, conn);

            MaintenanceSchedule rep = scheduleDAO.getMaintenanceScheduleWithStatusByCampaignId(campaignId);
            if (rep == null) {
                throw new IllegalStateException("Không tìm thấy lịch trình để cập nhật.");
            }

            rep.setAddressId(addressId);
            rep.setStatusId(Integer.parseInt(request.getParameter("statusId")));
            rep.setScheduledDate(LocalDate.parse(request.getParameter("scheduledDate")));
            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isBlank()) {
                rep.setEndDate(LocalDate.parse(endDateStr.trim()));
            } else {
                rep.setEndDate(null);
            }
            String startTimeStr = request.getParameter("startTime");
            if (startTimeStr != null && !startTimeStr.isBlank()) {
                rep.setStartTime(LocalTime.parse(startTimeStr.trim()));
            } else {
                rep.setStartTime(null);
            }
            String endTimeStr = request.getParameter("endTime");
            if (endTimeStr != null && !endTimeStr.isBlank()) {
                rep.setEndTime(LocalTime.parse(endTimeStr.trim()));
            } else {
                rep.setEndTime(null);
            }
            rep.setColor(request.getParameter("color"));
            scheduleDAO.updateScheduleCore(rep, conn);

            int assignedUserId = Integer.parseInt(request.getParameter("assignedUserId"));
            assignmentDAO.upsertAssignment(rep.getId(), assignedUserId, conn);

            conn.commit();
            response.sendRedirect(request.getContextPath() + "/view-campaign?id=" + campaignId + "&success=" + URLEncoder.encode("Cập nhật thành công", StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            String errRedirect = request.getContextPath() + "/edit-campaign?id=" + campaignId + "&error=" + URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8.name());
            handleFormError(request, response, conn, null, null); // Chỉ để rollback và đóng connection
            response.sendRedirect(errRedirect);
        } finally {
            closeConnection(conn);
        }
    }

    // =====================================================================
    // ===== LOGIC TỪ DeleteCampaignServlet =====
    // =====================================================================
    private void handleDeleteCampaign(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String redirectUrl = request.getContextPath() + "/list-campaign";
        try {
            int campaignId = Integer.parseInt(request.getParameter("id"));
            boolean success = campaignDAO.softDeleteCampaignById(campaignId);
            if (success) {
                redirectUrl += "?success=" + URLEncoder.encode("Đã xóa chiến dịch thành công!", StandardCharsets.UTF_8.name());
            } else {
                redirectUrl += "?error=" + URLEncoder.encode("Xóa chiến dịch thất bại.", StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectUrl += "?error=" + URLEncoder.encode("Lỗi hệ thống khi xóa chiến dịch.", StandardCharsets.UTF_8.name());
        }
        response.sendRedirect(redirectUrl);
    }

    // =====================================================================
    // ===== LOGIC TỪ ViewCampaignDetailServlet =====
    // =====================================================================
    private void handleViewCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int campaignId = Integer.parseInt(request.getParameter("id"));
            Campaign campaign = campaignDAO.getCampaignById(campaignId);
            if (campaign == null) {
                handleException(request, response, new Exception("Không tìm thấy chiến dịch."), "xem chi tiết");
                return;
            }

            MaintenanceSchedule ms = scheduleDAO.getMaintenanceScheduleWithStatusByCampaignId(campaignId);

            if (ms != null) {
                String statusName = ms.getStatusName() != null ? ms.getStatusName().trim() : "";
                boolean isClosed = statusName.equalsIgnoreCase("Hoàn thành") || statusName.equalsIgnoreCase("Đã hủy");

                if (!isClosed) {
                    LocalDate today = LocalDate.now(VN_TZ);
                    LocalTime now = LocalTime.now(VN_TZ);
                    LocalDate baseDate = ms.getEndDate() != null ? ms.getEndDate() : ms.getScheduledDate();
                    LocalTime effectiveEndTime = ms.getEndTime() != null ? ms.getEndTime() : LocalTime.of(23, 59);

                    if (baseDate != null && (baseDate.isBefore(today) || (baseDate.isEqual(today) && effectiveEndTime.isBefore(now)))) {
                        ms.setStatusName("Quá hạn");
                    }
                }

                if (ms.getScheduledDate() != null) {
                    request.setAttribute("scheduledDateStr", ms.getScheduledDate().format(DMY));
                }
                if (ms.getEndDate() != null) {
                    request.setAttribute("endDateStr", ms.getEndDate().format(DMY));
                }
            }

            request.setAttribute("campaign", campaign);
            request.setAttribute("maintenanceSchedule", ms);
            request.getRequestDispatcher("/jsp/customerSupport/viewCampaignDetails.jsp").forward(request, response);
        } catch (Exception e) {
            handleException(request, response, e, "xem chi tiết chiến dịch");
        }
    }

    // =====================================================================
    // ===== CÁC PHƯƠNG THỨC HELPER CHUNG =====
    // =====================================================================
    private void loadFormDropdowns(HttpServletRequest request) throws Exception {
        request.setAttribute("campaignTypes", campaignTypeDAO.getAllCampaignTypes());
        request.setAttribute("enterpriseList", enterpriseDAO.getAllEnterprises());
        request.setAttribute("statusList", statusDAO.getAllStatuses());
        request.setAttribute("userList", userDAO.getUsersByRoleName("Chăm sóc khách hàng"));
        request.setAttribute("provinces", addressDAO.getAllProvinces());
    }

    private void handleAddressAPI(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object data = Collections.emptyList();
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if ("getDistricts".equals(request.getParameter("action"))) {
                data = addressDAO.getDistrictsByProvinceId(id);
            } else if ("getWards".equals(request.getParameter("action"))) {
                data = addressDAO.getWardsByDistrictId(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(data));
            out.flush();
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
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        if (fieldErrors != null) {
            request.setAttribute("fieldErrors", fieldErrors);
        }

        try {
            loadFormDropdowns(request);
            Integer p = safeInt(request.getParameter("province"));
            if (p != null) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(p));
            }
            Integer d = safeInt(request.getParameter("district"));
            if (d != null) {
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(d));
            }
        } catch (Exception e) {
            handleException(request, response, e, "nạp lại dữ liệu form");
            return;
        }

        String requestPath = request.getServletPath();
        if ("/create-campaign".equals(requestPath)) {
            request.getRequestDispatcher("/jsp/customerSupport/addNewCampaign.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/jsp/customerSupport/editCampaign.jsp").forward(request, response);
        }
    }

    private Map<String, String> validateCampaignData(HttpServletRequest request, boolean isEdit) {
        Map<String, String> errors = new HashMap<>();
        String name = request.getParameter("name");
        if (name == null || name.trim().length() < 3 || name.trim().length() > 255) {
            errors.put("name", "Tên chiến dịch phải từ 3–255 ký tự.");
        }
        if (isEdit) {
            validateId(request.getParameter("campaignId"), "campaignId", "Thiếu ID chiến dịch.", errors);
        }
        validateId(request.getParameter("typeId"), "typeId", "Vui lòng chọn Loại chiến dịch.", errors);
        validateId(request.getParameter("enterpriseId"), "enterpriseId", "Vui lòng chọn Khách hàng.", errors);
        validateId(request.getParameter("statusId"), "statusId", "Vui lòng chọn Trạng thái.", errors);
        validateId(request.getParameter("assignedUserId"), "assignedUserId", "Vui lòng chọn Nhân viên.", errors);
        validateId(request.getParameter("province"), "province", "Vui lòng chọn Tỉnh/Thành.", errors);
        validateId(request.getParameter("district"), "district", "Vui lòng chọn Quận/Huyện.", errors);
        validateId(request.getParameter("ward"), "ward", "Vui lòng chọn Phường/Xã.", errors);

        String street = request.getParameter("streetAddress");
        if (street == null || street.trim().isEmpty() || street.trim().length() > 255) {
            errors.put("streetAddress", "Địa chỉ cụ thể không hợp lệ.");
        }
        String color = request.getParameter("color");
        if (color != null && !color.isBlank() && !color.matches("^#[0-9A-Fa-f]{6}$")) {
            errors.put("color", "Màu sắc không hợp lệ (#RRGGBB).");
        }

        LocalDate scheduledDate = null, endDate = null;
        LocalTime startTime = null, endTime = null;
        try {
            scheduledDate = LocalDate.parse(request.getParameter("scheduledDate"));
        } catch (Exception e) {
            errors.put("scheduledDate", "Ngày bắt đầu không hợp lệ (yyyy-MM-dd).");
        }
        try {
            String ed = request.getParameter("endDate");
            if (ed != null && !ed.isBlank()) {
                endDate = LocalDate.parse(ed);
            }
        } catch (Exception e) {
            errors.put("endDate", "Ngày kết thúc không hợp lệ (yyyy-MM-dd).");
        }
        try {
            String st = request.getParameter("startTime");
            if (st != null && !st.isBlank()) {
                startTime = LocalTime.parse(st);
            }
        } catch (DateTimeParseException e) {
            errors.put("startTime", "Giờ bắt đầu không hợp lệ (HH:mm).");
        }
        try {
            String et = request.getParameter("endTime");
            if (et != null && !et.isBlank()) {
                endTime = LocalTime.parse(et);
            }
        } catch (DateTimeParseException e) {
            errors.put("endTime", "Giờ kết thúc không hợp lệ (HH:mm).");
        }

        if (scheduledDate != null && endDate != null && endDate.isBefore(scheduledDate)) {
            errors.put("endDate", "Ngày kết thúc không được sớm hơn ngày bắt đầu.");
        }
        if (scheduledDate != null && endDate != null && endDate.isEqual(scheduledDate) && startTime != null && endTime != null && endTime.isBefore(startTime)) {
            errors.put("endTime", "Giờ kết thúc không được sớm hơn giờ bắt đầu trong cùng ngày.");
        }
        return errors;
    }

    private void validateId(String idStr, String fieldName, String message, Map<String, String> errors) {
        if (idStr == null || idStr.isBlank()) {
            errors.put(fieldName, message);
            return;
        }
        try {
            if (Integer.parseInt(idStr.trim()) <= 0) {
                errors.put(fieldName, "Giá trị chọn không hợp lệ.");
            }
        } catch (NumberFormatException e) {
            errors.put(fieldName, "Giá trị chọn không hợp lệ.");
        }
    }

    private Integer safeInt(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trimOrEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e, String action)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Lỗi khi " + action + ": " + e.getMessage());
        request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Front Controller for all Campaign-related actions.";
    }
}
