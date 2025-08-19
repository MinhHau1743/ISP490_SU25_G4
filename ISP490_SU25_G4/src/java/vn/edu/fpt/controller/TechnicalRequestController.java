package vn.edu.fpt.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.dao.AddressDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.edu.fpt.common.EmailServiceFeedback;
import vn.edu.fpt.common.LocalDateAdapter;
import vn.edu.fpt.common.LocalDateTimeAdapter;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.ScheduleDAO;
import vn.edu.fpt.dao.StatusDAO;
import vn.edu.fpt.model.Address;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Service;
import vn.edu.fpt.model.Ward;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TechnicalRequestController extends HttpServlet {

    private TechnicalRequestDAO dao;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void init() {
        dao = new TechnicalRequestDAO();
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
                // --- THÊM CÁC CASE MỚI ---
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
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra log server để debug
            throw new ServletException("Database access error.", e);
        } catch (Exception ex) {
            Logger.getLogger(TechnicalRequestController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, Exception {
        // 1. Lấy tất cả tham số lọc và phân trang
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
        final int LIMIT = 9; // Hiển thị 9 thẻ mỗi trang (3x3 grid)
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // 2. Lấy tổng số item với bộ lọc để tính toán số trang
        int totalItems = dao.getTotalFilteredRequestCount(query, status, serviceId);
        int totalPages = (int) Math.ceil((double) totalItems / LIMIT);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        // 3. Tính offset
        int offset = (page - 1) * LIMIT;

        // 4. Lấy danh sách giao dịch đã lọc
        List<TechnicalRequest> transactionList = dao.getFilteredTechnicalRequests(query, status, serviceId, LIMIT, offset);

        // 5. Lấy danh sách cho các dropdown bộ lọc
        List<Service> serviceList = dao.getAllServices();
        List<String> statusList = dao.getDistinctStatuses();
        request.setAttribute("transactions", transactionList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("statusList", statusList);
        request.setAttribute("totalItems", totalItems); // Gửi thêm tổng số item
        request.setAttribute("serviceList", serviceList); // Gửi danh sách dịch vụ
        request.setAttribute("activeMenu", "ticket");

        request.getRequestDispatcher("/jsp/customerSupport/listTransaction.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, Exception {
        AddressDAO addressDAO = new AddressDAO();
        List<Province> provinces = addressDAO.getAllProvinces();
        StatusDAO statusDAO = new StatusDAO();
        // 6. Gửi tất cả dữ liệu sang JSP
        request.setAttribute("customerList", dao.getAllEnterprises());
        request.setAttribute("employeeList", dao.getAllTechnicians());
        request.setAttribute("serviceList", dao.getAllServices());
        request.setAttribute("contractList", dao.getAllActiveContracts()); // <--- THÊM DÒNG NÀY
        request.setAttribute("provinces", provinces);
        request.setAttribute("activeMenu", "createTicket");
        request.setAttribute("statusList", statusDAO.getAllStatuses());
        request.getRequestDispatcher("/jsp/customerSupport/createTicket.jsp").forward(request, response);
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // 1. Khởi tạo tất cả các DAO cần thiết
            TechnicalRequestDAO ticketDAO = new TechnicalRequestDAO();
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            AddressDAO addressDAO = new AddressDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            FeedbackDAO feedbackDAO = new FeedbackDAO();

            // 2. Lấy đối tượng TechnicalRequest chính
            TechnicalRequest ticket = ticketDAO.getTechnicalRequestById(id);

            if (ticket != null) {
                // 3. Lấy dữ liệu liên quan
                MaintenanceSchedule schedule = scheduleDAO.getScheduleByTechnicalRequestId(id);
                boolean hasFeedback = feedbackDAO.feedbackExistsForTechnicalRequest(id);

                // 4. ĐỊNH DẠNG NGÀY THÁNG TỪ LOCALDATE SANG STRING
                // Tạo một formatter để định dạng ngày theo kiểu "dd/MM/yyyy"
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                String scheduledDateFormatted = "";
                String endDateFormatted = "";

                if (schedule != null) {
                    if (schedule.getScheduledDate() != null) {
                        scheduledDateFormatted = schedule.getScheduledDate().format(dateFormatter);
                    }
                    if (schedule.getEndDate() != null) {
                        endDateFormatted = schedule.getEndDate().format(dateFormatter);
                    }
                }

                // 5. Đặt tất cả dữ liệu làm thuộc tính cho request
                request.setAttribute("ticket", ticket);
                request.setAttribute("schedule", schedule); // Vẫn gửi schedule gốc để lấy startTime, endTime...
                request.setAttribute("hasFeedback", hasFeedback);

                // Gửi các chuỗi đã định dạng sang JSP
                request.setAttribute("scheduledDateFormatted", scheduledDateFormatted);
                request.setAttribute("endDateFormatted", endDateFormatted);

                // Xử lý logic địa chỉ và các danh sách khác
                request.setAttribute("provinces", addressDAO.getAllProvinces());
                if (schedule != null && schedule.getAddress() != null && schedule.getAddress().getProvinceId() > 0) {
                    Address scheduleAddress = schedule.getAddress();
                    request.setAttribute("districts", enterpriseDAO.getDistrictsByProvinceId(scheduleAddress.getProvinceId()));
                    if (scheduleAddress.getDistrictId() > 0) {
                        request.setAttribute("wards", enterpriseDAO.getWardsByDistrictId(scheduleAddress.getDistrictId()));
                    }
                }

                // 6. Chuyển tiếp đến trang view
                request.getRequestDispatcher("/jsp/customerSupport/viewTransaction.jsp").forward(request, response);

            } else {
                // Nếu không tìm thấy ticket, chuyển hướng về trang danh sách
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=invalidId");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi khi xem chi tiết phiếu.", e);
        }
    }

    // Trong file: vn/edu/fpt/controller/TechnicalRequestController.java
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // 1. Khởi tạo tất cả các DAO cần thiết
            TechnicalRequestDAO ticketDAO = new TechnicalRequestDAO();
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            AddressDAO addressDAO = new AddressDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            StatusDAO statusDAO = new StatusDAO();
            ContractDAO contractDAO = new ContractDAO();

            // 2. Lấy đối tượng TechnicalRequest chính
            TechnicalRequest existingTicket = ticketDAO.getTechnicalRequestById(id);

            if (existingTicket != null) {
                // 3. Lấy dữ liệu liên quan cho form

                // Lấy danh sách thiết bị đã có của phiếu
                List<TechnicalRequestDevice> existingDevices = existingTicket.getDevices();
                String existingDevicesJson = (existingDevices != null) ? gson.toJson(existingDevices) : "[]";

                // LẤY DANH SÁCH SẢN PHẨM TỪ HỢP ĐỒNG CỦA PHIẾU
                List<Product> contractProducts = new ArrayList<>();
                if (existingTicket.getContractId() != null) {
                    contractProducts = contractDAO.getProductsByContractId(existingTicket.getContractId());
                }
                String contractProductsJson = (contractProducts != null) ? gson.toJson(contractProducts) : "[]";

                // 4. Đặt tất cả dữ liệu làm thuộc tính cho request
                request.setAttribute("ticket", existingTicket);
                request.setAttribute("contractList", ticketDAO.getAllActiveContracts());
                request.setAttribute("employeeList", ticketDAO.getAllTechnicians());
                request.setAttribute("serviceList", ticketDAO.getAllServices());
                request.setAttribute("statusList", statusDAO.getAllStatuses());
                request.setAttribute("existingDevicesJson", existingDevicesJson);
                request.setAttribute("contractProductsJson", contractProductsJson);

                // 5. Lấy dữ liệu lịch trình và địa chỉ
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

                // 6. Chuyển tiếp đến trang edit
                request.getRequestDispatcher("/jsp/customerSupport/editTransaction.jsp").forward(request, response);

            } else {
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=editFailed");
        }
    }

// 2. Thêm phương thức mới để xử lý việc cập nhật (POST)
    // Trong file: vn/edu/fpt/controller/TechnicalRequestController.java
    private void updateTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // --- 1. Khởi tạo các DAO cần thiết ---
            StatusDAO statusDAO = new StatusDAO();
            AddressDAO addressDAO = new AddressDAO();

            // --- 2. Lấy dữ liệu và tạo các đối tượng ---
            TechnicalRequest updatedRequest = new TechnicalRequest();
            MaintenanceSchedule schedule = new MaintenanceSchedule();

            // Lấy ID của các bản ghi cần cập nhật
            int ticketId = Integer.parseInt(request.getParameter("id"));
            updatedRequest.setId(ticketId);
            // Giả sử schedule ID được lấy từ một trường ẩn trên form edit
            String scheduleIdStr = request.getParameter("scheduleId");
            if (scheduleIdStr != null && !scheduleIdStr.isEmpty()) {
                schedule.setId(Integer.parseInt(scheduleIdStr));
            }

            // --- 3. Thu thập dữ liệu từ form cho TechnicalRequest ---
            updatedRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            updatedRequest.setContractId(Integer.parseInt(request.getParameter("contractId")));
            updatedRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            updatedRequest.setTitle(request.getParameter("title"));
            updatedRequest.setDescription(request.getParameter("description"));
            updatedRequest.setPriority(request.getParameter("priority"));

            String employeeIdStr = request.getParameter("employeesId");
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                updatedRequest.setAssignedToId(Integer.parseInt(employeeIdStr));
            }

            boolean isBillable = "true".equals(request.getParameter("isBillable"));
            updatedRequest.setIsBillable(isBillable);
            if (isBillable) {
                updatedRequest.setEstimatedCost(Double.parseDouble(request.getParameter("amount")));
            } else {
                updatedRequest.setEstimatedCost(0);
            }

            // --- 4. Thu thập dữ liệu từ form cho MaintenanceSchedule ---
            String statusName = request.getParameter("status");
            Integer statusId = statusDAO.getIdByName(statusName);
            schedule.setStatusId(statusId);

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

            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress");
            int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);
            schedule.setAddressId(addressId);
            schedule.setColor(request.getParameter("color"));

            // --- 5. Thu thập dữ liệu thiết bị ---
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

            // --- 6. Gọi DAO để cập nhật tất cả trong 1 transaction ---
            boolean success = dao.updateTechnicalRequestAndSchedule(updatedRequest, schedule, devices);

            // --- 7. Chuyển hướng ---
            response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&update=" + (success ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
            String ticketId = request.getParameter("id");
            response.sendRedirect(request.getContextPath() + "/ticket?action=edit&id=" + ticketId + "&error=unknown");
        }
    }

// **Lưu ý:** Bạn cần thêm một trường input ẩn vào file `editTransaction.jsp` để lấy `scheduleId`
// <input type="hidden" name="scheduleId" value="${schedule.id}">
    // Trong file: vn/edu/fpt/controller/TechnicalRequestController.java
    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Khởi tạo các đối tượng cần thiết
            MaintenanceSchedule schedule = new MaintenanceSchedule();
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            TechnicalRequest newRequest = new TechnicalRequest();
            AddressDAO addressDAO = new AddressDAO();
            StatusDAO statusDAO = new StatusDAO();

            // Lấy thông tin từ form
            String enterpriseIdStr = request.getParameter("enterpriseId");
            String reporterIdStr = request.getParameter("reporterId");
            String serviceIdStr = request.getParameter("serviceId");
            String contractIdStr = request.getParameter("contractId");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String priority = request.getParameter("priority");
            boolean isBillable = Boolean.parseBoolean(request.getParameter("isBillable"));
            String amountStr = request.getParameter("amount");
            String statusName = request.getParameter("status");

            // Lấy ID nhân viên từ form (dạng chuỗi)
            String employeeIdStr = request.getParameter("employeesId");

            // --- Gán dữ liệu cho đối tượng TechnicalRequest ---
            newRequest.setReporterId(Integer.parseInt(reporterIdStr));
            if (enterpriseIdStr != null && !enterpriseIdStr.isEmpty()) {
                newRequest.setEnterpriseId(Integer.parseInt(enterpriseIdStr));
            }
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                newRequest.setServiceId(Integer.parseInt(serviceIdStr));
            }
            if (contractIdStr != null && !contractIdStr.isEmpty()) {
                newRequest.setContractId(Integer.valueOf(contractIdStr));
            }

            // === DÒNG CODE MỚI QUAN TRỌNG ĐƯỢC BỔ SUNG ===
            // Gán nhân viên phụ trách cho chính phiếu yêu cầu
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                newRequest.setAssignedToId(Integer.parseInt(employeeIdStr));
            }
            // ===============================================

            newRequest.setTitle(title);
            newRequest.setDescription(description);
            newRequest.setPriority(priority);
            newRequest.setIsBillable(isBillable);
            if (isBillable) {
                newRequest.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                newRequest.setEstimatedCost(0);
            }

            // Lấy danh sách thiết bị từ form
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

            // Tạo Technical Request trước để lấy ID
            Integer newRequestId = dao.createTechnicalRequest(newRequest, devices);

            if (newRequestId != null) {
                // --- Gán dữ liệu cho đối tượng MaintenanceSchedule ---
                schedule.setTechnicalRequestId(newRequestId);

                Integer statusId = statusDAO.getIdByName(statusName);
                schedule.setStatusId(statusId != null ? statusId : 2); // ID mặc định là 2 nếu không tìm thấy

                // Gán thông tin thời gian và địa chỉ...
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

                int provinceId = Integer.parseInt(request.getParameter("province"));
                int districtId = Integer.parseInt(request.getParameter("district"));
                int wardId = Integer.parseInt(request.getParameter("ward"));
                String streetAddress = request.getParameter("streetAddress");
                int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);
                schedule.setAddressId(addressId);
                schedule.setColor(request.getParameter("color"));

                // Gán nhân viên cho schedule (bảng MaintenanceAssignments)
                List<Integer> employeeIds = new ArrayList<>();
                if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                    employeeIds.add(Integer.parseInt(employeeIdStr));
                }

                scheduleDAO.addScheduleWithAssignments(schedule, employeeIds);
                response.sendRedirect("ticket?action=list&create=success");

            } else {
                response.sendRedirect("ticket?action=list&create=failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=create&error=" + e.getClass().getSimpleName());
        }
    }

    private void getContracts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            // YÊU CẦU CỦA BẠN: Lấy tất cả hợp đồng, không lọc theo khách hàng.
            List<Contract> contracts = dao.getAllActiveContracts();
            response.getWriter().write(gson.toJson(contracts));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Lỗi khi truy vấn cơ sở dữ liệu.\"}");
        }
    }

    private void getProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Product> products = dao.getAllProducts();
            response.getWriter().write(gson.toJson(products));
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Lỗi khi truy vấn danh sách sản phẩm.\"}");
        }
    }

    private void deleteTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean success = dao.deleteTechnicalRequest(id);
            // Chuyển hướng về trang danh sách với tham số báo kết quả

            ScheduleDAO dao = new ScheduleDAO();
            boolean deleteSuccess = dao.deleteMaintenanceSchedule(id);
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&delete=" + (success ? "success" : "failed"));
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, cũng chuyển hướng về trang danh sách với thông báo lỗi
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
                districts = new EnterpriseDAO().getDistrictsByProvinceId(Integer.parseInt(provinceIdStr));
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
                wards = new EnterpriseDAO().getWardsByDistrictId(Integer.parseInt(districtIdStr));
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

    // Thêm toàn bộ phương thức này vào cuối file TicketController.java
    private void handleSendSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int ticketId = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest ticket = dao.getTechnicalRequestById(ticketId);

            if (ticket != null) {
                String recipientEmail = ticket.getEnterpriseEmail();
                String enterpriseName = ticket.getEnterpriseName();
                String requestCode = ticket.getRequestCode();

                if (recipientEmail != null && !recipientEmail.trim().isEmpty()) {
                    // Gửi email trong luồng riêng để không làm chậm
                    emailExecutor.submit(() -> {
                        try {
                            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                            String surveyLink = baseUrl + "/feedback?action=create&technicalRequestId=" + ticketId;

                            String subject = "Mời bạn đánh giá chất lượng dịch vụ cho yêu cầu #" + requestCode;
                            String body = "<html>"
                                    + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                                    + "<h3>Kính gửi Quý khách hàng " + enterpriseName + ",</h3>"
                                    + "<p>Yêu cầu hỗ trợ kỹ thuật với mã số <strong>" + requestCode + "</strong> của Quý khách đã được xử lý xong.</p>"
                                    + "<p>Chúng tôi rất mong nhận được những ý kiến đóng góp quý báu của Quý khách để cải thiện chất lượng dịch vụ. Vui lòng dành chút thời gian để thực hiện khảo sát bằng cách nhấn vào nút bên dưới:</p>"
                                    + "<div style='text-align: center; margin: 25px 0;'>"
                                    + "<a href=\"" + surveyLink + "\" style='background-color:#2563eb;color:white;padding:12px 25px;text-align:center;text-decoration:none;display:inline-block;border-radius:8px;font-size:16px;'><strong>Thực hiện khảo sát</strong></a>"
                                    + "</div>"
                                    + "<p>Trân trọng cảm ơn,<br><strong>Đội ngũ DPCRM</strong>.</p>"
                                    + "</body>"
                                    + "</html>";

                            EmailServiceFeedback.sendMail(recipientEmail, subject, body);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    // Chuyển hướng ngay lập tức với thông báo thành công
                    response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&surveySent=true");

                } else {
                    // Nếu không có email, chuyển hướng với thông báo lỗi
                    response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&surveySent=false");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=unknown");
        }
    }

    // --- THÊM CÁC PHƯƠNG THỨC MỚI ---
    private void getContractDetails(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int contractId = Integer.parseInt(request.getParameter("contractId"));
        ContractDAO contractDAO = new ContractDAO();
        Contract contract = contractDAO.getContractWithCustomerById(contractId);
        response.getWriter().write(gson.toJson(contract));
    }

    private void getProductsByContract(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int contractId = Integer.parseInt(request.getParameter("contractId"));
        ContractDAO contractDAO = new ContractDAO();
        List<Product> products = contractDAO.getProductsByContractId(contractId);
        response.getWriter().write(gson.toJson(products));
    }
}
