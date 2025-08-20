package vn.edu.fpt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.EmailServiceFeedback;
import vn.edu.fpt.common.LocalDateAdapter;
import vn.edu.fpt.common.LocalDateTimeAdapter;
import vn.edu.fpt.common.NotificationService;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TechnicalRequestController extends HttpServlet {

    // --- CÁC DAO ĐÃ ĐƯỢC CHUYỂN THÀNH FIELD ---
    private final TechnicalRequestDAO technicalRequestDAO;
    private final ScheduleDAO scheduleDAO;
    private final AddressDAO addressDAO;
    private final EnterpriseDAO enterpriseDAO;
    private final FeedbackDAO feedbackDAO;
    private final StatusDAO statusDAO;
    private final ContractDAO contractDAO;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    /**
     * Constructor mặc định: Được sử dụng bởi Tomcat khi ứng dụng chạy thật. Nó
     * tự khởi tạo các DAO thật.
     */
    public TechnicalRequestController() {
        this.technicalRequestDAO = new TechnicalRequestDAO();
        this.scheduleDAO = new ScheduleDAO();
        this.addressDAO = new AddressDAO();
        this.enterpriseDAO = new EnterpriseDAO();
        this.feedbackDAO = new FeedbackDAO();
        this.statusDAO = new StatusDAO();
        this.contractDAO = new ContractDAO();
    }

    /**
     * Constructor cho Unit Test: Cho phép "tiêm" các DAO giả (mock) từ bên
     * ngoài.
     */
    public TechnicalRequestController(TechnicalRequestDAO technicalRequestDAO, ScheduleDAO scheduleDAO, AddressDAO addressDAO, EnterpriseDAO enterpriseDAO, FeedbackDAO feedbackDAO, StatusDAO statusDAO, ContractDAO contractDAO) {
        this.technicalRequestDAO = technicalRequestDAO;
        this.scheduleDAO = scheduleDAO;
        this.addressDAO = addressDAO;
        this.enterpriseDAO = enterpriseDAO;
        this.feedbackDAO = feedbackDAO;
        this.statusDAO = statusDAO;
        this.contractDAO = contractDAO;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        try {
            switch (action) {
                case "create":
                    showCreateForm(request, response);
                    break;
                case "view":
                    viewTicket(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "getProducts":
                    getProducts(request, response);
                    break;
                case "delete":
                    deleteTicket(request, response);
                    break;
                case "getDistricts":
                    getDistricts(request, response);
                    break;
                case "getWards":
                    getWards(request, response);
                    break;
                case "sendSurvey":
                    handleSendSurvey(request, response);
                    break;
                case "getContractDetails":
                    getContractDetails(request, response);
                    break;
                case "getProductsByContract":
                    getProductsByContract(request, response);
                    break;
                case "list":
                default:
                    listTickets(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi xử lý yêu cầu GET trong TicketController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            createTicket(request, response);
        } else if ("update".equals(action)) {
            updateTicket(request, response);
        }
    }

    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String query = request.getParameter("query");
        String status = request.getParameter("status");
        String serviceIdStr = request.getParameter("serviceId");
        int serviceId = 0;
        if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
            try {
                serviceId = Integer.parseInt(serviceIdStr);
            } catch (NumberFormatException e) {
                /* Bỏ qua */ }
        }
        int page = 1;
        final int LIMIT = 9;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int totalItems = technicalRequestDAO.getTotalFilteredRequestCount(query, status, serviceId);
        int totalPages = (int) Math.ceil((double) totalItems / LIMIT);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }
        int offset = (page - 1) * LIMIT;

        List<TechnicalRequest> transactionList = technicalRequestDAO.getFilteredTechnicalRequests(query, status, serviceId, LIMIT, offset);
        List<Service> serviceList = technicalRequestDAO.getAllServices();
        List<String> statusList = technicalRequestDAO.getDistinctStatuses();

        request.setAttribute("transactions", transactionList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("statusList", statusList);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("serviceList", serviceList);
        request.setAttribute("activeMenu", "ticket");
        request.getRequestDispatcher("/jsp/customerSupport/listTransaction.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("customerList", technicalRequestDAO.getAllEnterprises());
        request.setAttribute("employeeList", technicalRequestDAO.getAllTechnicians());
        request.setAttribute("serviceList", technicalRequestDAO.getAllServices());
        request.setAttribute("contractList", technicalRequestDAO.getAllActiveContracts());
        request.setAttribute("provinces", addressDAO.getAllProvinces());
        request.setAttribute("statusList", statusDAO.getAllStatuses());
        request.setAttribute("activeMenu", "createTicket");
        request.getRequestDispatcher("/jsp/customerSupport/createTicket.jsp").forward(request, response);
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        TechnicalRequest ticket = technicalRequestDAO.getTechnicalRequestById(id);
        if (ticket != null) {
            MaintenanceSchedule schedule = scheduleDAO.getScheduleByTechnicalRequestId(id);
            boolean hasFeedback = feedbackDAO.feedbackExistsForTechnicalRequest(id);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String scheduledDateFormatted = (schedule != null && schedule.getScheduledDate() != null) ? schedule.getScheduledDate().format(dateFormatter) : "";
            String endDateFormatted = (schedule != null && schedule.getEndDate() != null) ? schedule.getEndDate().format(dateFormatter) : "";

            request.setAttribute("ticket", ticket);
            request.setAttribute("schedule", schedule);
            request.setAttribute("hasFeedback", hasFeedback);
            request.setAttribute("scheduledDateFormatted", scheduledDateFormatted);
            request.setAttribute("endDateFormatted", endDateFormatted);
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            if (schedule != null && schedule.getAddress() != null && schedule.getAddress().getProvinceId() > 0) {
                Address scheduleAddress = schedule.getAddress();
                request.setAttribute("districts", enterpriseDAO.getDistrictsByProvinceId(scheduleAddress.getProvinceId()));
                if (scheduleAddress.getDistrictId() > 0) {
                    request.setAttribute("wards", enterpriseDAO.getWardsByDistrictId(scheduleAddress.getDistrictId()));
                }
            }
            request.getRequestDispatcher("/jsp/customerSupport/viewTransaction.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        TechnicalRequest existingTicket = technicalRequestDAO.getTechnicalRequestById(id);
        if (existingTicket != null) {
            List<TechnicalRequestDevice> existingDevices = existingTicket.getDevices();
            String existingDevicesJson = (existingDevices != null) ? gson.toJson(existingDevices) : "[]";
            List<Product> contractProducts = new ArrayList<>();
            if (existingTicket.getContractId() != null) {
                contractProducts = contractDAO.getProductsByContractId(existingTicket.getContractId());
            }
            String contractProductsJson = (contractProducts != null) ? gson.toJson(contractProducts) : "[]";

            request.setAttribute("ticket", existingTicket);
            request.setAttribute("contractList", technicalRequestDAO.getAllActiveContracts());
            request.setAttribute("employeeList", technicalRequestDAO.getAllTechnicians());
            request.setAttribute("serviceList", technicalRequestDAO.getAllServices());
            request.setAttribute("statusList", statusDAO.getAllStatuses());
            request.setAttribute("existingDevicesJson", existingDevicesJson);
            request.setAttribute("contractProductsJson", contractProductsJson);

            MaintenanceSchedule schedule = scheduleDAO.getScheduleByTechnicalRequestId(id);
            request.setAttribute("schedule", schedule);
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            if (schedule != null && schedule.getAddress() != null && schedule.getAddress().getProvinceId() > 0) {
                Address scheduleAddress = schedule.getAddress();
                request.setAttribute("districts", enterpriseDAO.getDistrictsByProvinceId(scheduleAddress.getProvinceId()));
                if (scheduleAddress.getDistrictId() > 0) {
                    request.setAttribute("wards", enterpriseDAO.getWardsByDistrictId(scheduleAddress.getDistrictId()));
                }
            }
            request.getRequestDispatcher("/jsp/customerSupport/editTransaction.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
        }
    }

    private void updateTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            User currentUser = (User) session.getAttribute("user");

            TechnicalRequest updatedRequest = new TechnicalRequest();
            MaintenanceSchedule schedule = new MaintenanceSchedule();

            int ticketId = Integer.parseInt(request.getParameter("id"));
            updatedRequest.setId(ticketId);
            String scheduleIdStr = request.getParameter("scheduleId");
            if (scheduleIdStr != null && !scheduleIdStr.isEmpty()) {
                schedule.setId(Integer.parseInt(scheduleIdStr));
            }

            updatedRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            updatedRequest.setContractId(Integer.parseInt(request.getParameter("contractId")));
            updatedRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            updatedRequest.setTitle(request.getParameter("title"));
            updatedRequest.setDescription(request.getParameter("description"));
            updatedRequest.setPriority(request.getParameter("priority"));
            updatedRequest.setReporterId(currentUser.getId());

            String employeeIdStr = request.getParameter("employeesId");
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                updatedRequest.setAssignedToId(Integer.parseInt(employeeIdStr));
            }

            boolean isBillable = "true".equals(request.getParameter("isBillable"));
            updatedRequest.setIsBillable(isBillable);
            updatedRequest.setEstimatedCost(isBillable ? Double.parseDouble(request.getParameter("amount")) : 0);

            String statusName = request.getParameter("status");
            schedule.setStatusId(statusDAO.getIdByName(statusName));
            schedule.setScheduledDate(LocalDate.parse(request.getParameter("scheduled_date")));
            String endDateStr = request.getParameter("end_date");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                schedule.setEndDate(LocalDate.parse(endDateStr));
            }
            String startTimeStr = request.getParameter("start_time");
            if (startTimeStr != null && !startTimeStr.isEmpty()) {
                schedule.setStartTime(LocalTime.parse(startTimeStr));
            }
            String endTimeStr = request.getParameter("end_time");
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                schedule.setEndTime(LocalTime.parse(endTimeStr));
            }
            int addressId = addressDAO.findOrCreateAddress(
                    request.getParameter("streetAddress"),
                    Integer.parseInt(request.getParameter("ward")),
                    Integer.parseInt(request.getParameter("district")),
                    Integer.parseInt(request.getParameter("province"))
            );
            schedule.setAddressId(addressId);
            schedule.setColor(request.getParameter("color"));

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            String deviceName;
            while ((deviceName = request.getParameter("deviceName_" + i)) != null) {
                if (!deviceName.trim().isEmpty()) {
                    TechnicalRequestDevice device = new TechnicalRequestDevice();
                    device.setDeviceName(deviceName);
                    device.setSerialNumber(request.getParameter("deviceSerial_" + i));
                    device.setProblemDescription(request.getParameter("deviceNote_" + i));
                    devices.add(device);
                }
                i++;
            }

            boolean success = technicalRequestDAO.updateTechnicalRequestAndSchedule(updatedRequest, schedule, devices);

            if (success) {
                Enterprise enterprise = enterpriseDAO.getEnterpriseById(updatedRequest.getEnterpriseId());
                if (enterprise != null) {
                    NotificationService.notifyUpdateTechnicalRequest(currentUser, updatedRequest, enterprise.getName());
                }
            }

            response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&update=" + (success ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
            String ticketId = request.getParameter("id");
            response.sendRedirect(request.getContextPath() + "/ticket?action=edit&id=" + ticketId + "&error=unknown");
        }
    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            User currentUser = (User) session.getAttribute("user");

            TechnicalRequest newRequest = new TechnicalRequest();

            newRequest.setReporterId(currentUser.getId());
            newRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            newRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            String contractIdStr = request.getParameter("contractId");
            if (contractIdStr != null && !contractIdStr.isEmpty()) {
                newRequest.setContractId(Integer.valueOf(contractIdStr));
            }
            String employeeIdStr = request.getParameter("employeesId");
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                newRequest.setAssignedToId(Integer.parseInt(employeeIdStr));
            }
            newRequest.setTitle(request.getParameter("title"));
            newRequest.setDescription(request.getParameter("description"));
            newRequest.setPriority(request.getParameter("priority"));
            boolean isBillable = Boolean.parseBoolean(request.getParameter("isBillable"));
            newRequest.setIsBillable(isBillable);
            String amountStr = request.getParameter("amount");
            newRequest.setEstimatedCost(isBillable && amountStr != null && !amountStr.isEmpty() ? Double.parseDouble(amountStr) : 0);

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            String deviceName;
            while ((deviceName = request.getParameter("deviceName_" + i)) != null) {
                if (!deviceName.trim().isEmpty()) {
                    TechnicalRequestDevice device = new TechnicalRequestDevice();
                    device.setDeviceName(deviceName);
                    device.setSerialNumber(request.getParameter("deviceSerial_" + i));
                    device.setProblemDescription(request.getParameter("deviceNote_" + i));
                    devices.add(device);
                }
                i++;
            }

            Integer newRequestId = technicalRequestDAO.createTechnicalRequest(newRequest, devices);

            if (newRequestId != null) {
                MaintenanceSchedule schedule = new MaintenanceSchedule();
                schedule.setTechnicalRequestId(newRequestId);
                String statusName = request.getParameter("status");
                schedule.setStatusId(statusDAO.getIdByName(statusName));
                schedule.setScheduledDate(LocalDate.parse(request.getParameter("scheduled_date")));
                String endDateStr = request.getParameter("end_date");
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    schedule.setEndDate(LocalDate.parse(endDateStr));
                }
                String startTimeStr = request.getParameter("start_time");
                if (startTimeStr != null && !startTimeStr.isEmpty()) {
                    schedule.setStartTime(LocalTime.parse(startTimeStr));
                }
                String endTimeStr = request.getParameter("end_time");
                if (endTimeStr != null && !endTimeStr.isEmpty()) {
                    schedule.setEndTime(LocalTime.parse(endTimeStr));
                }
                int addressId = addressDAO.findOrCreateAddress(
                        request.getParameter("streetAddress"),
                        Integer.parseInt(request.getParameter("ward")),
                        Integer.parseInt(request.getParameter("district")),
                        Integer.parseInt(request.getParameter("province"))
                );
                schedule.setAddressId(addressId);
                schedule.setColor(request.getParameter("color"));

                List<Integer> employeeIds = new ArrayList<>();
                if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                    employeeIds.add(Integer.parseInt(employeeIdStr));
                }
                scheduleDAO.addScheduleWithAssignments(schedule, employeeIds);

                Enterprise enterprise = enterpriseDAO.getEnterpriseById(newRequest.getEnterpriseId());
                if (enterprise != null) {
                    TechnicalRequest createdRequest = technicalRequestDAO.getTechnicalRequestById(newRequestId);
                    if (createdRequest != null) {
                        NotificationService.notifyNewTechnicalRequest(currentUser, createdRequest, enterprise.getName());
                    }
                }
                response.sendRedirect("ticket?action=list&create=success");
            } else {
                response.sendRedirect("ticket?action=list&create=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=create&error=" + e.getClass().getSimpleName());
        }
    }

    // --- CÁC HÀM GET AJAX VÀ HÀM PHỤ ---
    private void getProducts(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<Product> products = technicalRequestDAO.getAllProducts();
        response.getWriter().write(gson.toJson(products));
    }

    private void deleteTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = technicalRequestDAO.deleteTechnicalRequest(id);
            scheduleDAO.deleteMaintenanceSchedule(id);
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&delete=" + (success ? "success" : "failed"));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=deleteFailed");
        }
    }

    private void getDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String provinceIdStr = request.getParameter("provinceId");
        List<District> districts = Collections.emptyList();
        if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
            try {
                districts = enterpriseDAO.getDistrictsByProvinceId(Integer.parseInt(provinceIdStr));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(districts));
            out.flush();
        }
    }

    private void getWards(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String districtIdStr = request.getParameter("districtId");
        List<Ward> wards = Collections.emptyList();
        if (districtIdStr != null && !districtIdStr.trim().isEmpty()) {
            try {
                wards = enterpriseDAO.getWardsByDistrictId(Integer.parseInt(districtIdStr));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(wards));
            out.flush();
        }
    }

    private void handleSendSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int ticketId = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest ticket = technicalRequestDAO.getTechnicalRequestById(ticketId);
            if (ticket != null && ticket.getEnterpriseEmail() != null && !ticket.getEnterpriseEmail().trim().isEmpty()) {
                String recipientEmail = ticket.getEnterpriseEmail();
                String enterpriseName = ticket.getEnterpriseName();
                String requestCode = ticket.getRequestCode();
                emailExecutor.submit(() -> {
                    try {
                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                        String surveyLink = baseUrl + "/feedback?action=create&technicalRequestId=" + ticketId;
                        String subject = "Mời bạn đánh giá chất lượng dịch vụ cho yêu cầu #" + requestCode;
                        String body = "<html><body><h3>Kính gửi Quý khách hàng " + enterpriseName + ",</h3><p>Yêu cầu hỗ trợ kỹ thuật với mã số <strong>" + requestCode + "</strong> của Quý khách đã được xử lý xong.</p><p>Chúng tôi rất mong nhận được những ý kiến đóng góp quý báu của Quý khách để cải thiện chất lượng dịch vụ. Vui lòng dành chút thời gian để thực hiện khảo sát bằng cách nhấn vào nút bên dưới:</p><div style='text-align: center; margin: 25px 0;'><a href=\"" + surveyLink + "\" style='background-color:#2563eb;color:white;padding:12px 25px;text-align:center;text-decoration:none;display:inline-block;border-radius:8px;font-size:16px;'><strong>Thực hiện khảo sát</strong></a></div><p>Trân trọng cảm ơn,<br><strong>Đội ngũ DPCRM</strong>.</p></body></html>";
                        EmailServiceFeedback.sendMail(recipientEmail, subject, body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&surveySent=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&surveySent=false");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=unknown");
        }
    }

    private void getContractDetails(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int contractId = Integer.parseInt(request.getParameter("contractId"));
        Contract contract = contractDAO.getContractWithCustomerById(contractId);
        response.getWriter().write(gson.toJson(contract));
    }

    private void getProductsByContract(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int contractId = Integer.parseInt(request.getParameter("contractId"));
        List<Product> products = contractDAO.getProductsByContractId(contractId);
        response.getWriter().write(gson.toJson(products));
    }
}
