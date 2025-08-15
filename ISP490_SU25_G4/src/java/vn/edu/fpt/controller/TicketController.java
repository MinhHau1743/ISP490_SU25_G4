package vn.edu.fpt.controller;

import com.google.gson.Gson;
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
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Service;
import vn.edu.fpt.model.Ward;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TicketController extends HttpServlet {

    private TechnicalRequestDAO dao;
    private final Gson gson = new Gson();
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void init() {
        dao = new TechnicalRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("--- TICKET CONTROLLER: YÊU CẦU MỚI ---");
        request.getParameterMap().forEach((key, value) -> {
            System.out.println("Tham số nhận được: " + key + " = " + String.join(", ", value));
        });
        System.out.println("------------------------------------");
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

                case "list":
                default:
                    listTickets(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra log server để debug
            throw new ServletException("Database access error.", e);
        } catch (Exception ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
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

        // 6. Gửi tất cả dữ liệu sang JSP
        request.setAttribute("customerList", dao.getAllEnterprises());
        request.setAttribute("employeeList", dao.getAllTechnicians());
        request.setAttribute("serviceList", dao.getAllServices());
        request.setAttribute("contractList", dao.getAllActiveContracts()); // <--- THÊM DÒNG NÀY
        request.setAttribute("provinces", provinces);
        request.setAttribute("activeMenu", "createTicket");
        request.getRequestDispatcher("/jsp/customerSupport/createTicket.jsp").forward(request, response);
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest ticket = dao.getTechnicalRequestById(id);

            if (ticket != null) {

                // --- PHẦN CODE CẬP NHẬT ---
                // 1. Khởi tạo FeedbackDAO
                FeedbackDAO feedbackDAO = new FeedbackDAO();

                // 2. Kiểm tra xem feedback đã tồn tại cho ticket này chưa
                boolean hasFeedback = feedbackDAO.feedbackExistsForTechnicalRequest(id);

                // 3. Gửi cả ticket và kết quả kiểm tra sang JSP
                request.setAttribute("ticket", ticket);
                request.setAttribute("hasFeedback", hasFeedback); // <-- Gửi biến này sang JSP

                request.getRequestDispatcher("/jsp/customerSupport/viewTransaction.jsp").forward(request, response);
                // --- KẾT THÚC PHẦN CẬP NHẬT ---

            } else {
                response.sendRedirect("ticket?action=list&error=notFound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ticket?action=list&error=invalidId");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException, Exception {
        try {
            MaintenanceScheduleDAO scheduleDao = new MaintenanceScheduleDAO();
            AddressDAO addressDAO = new AddressDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            List<Province> provinces = addressDAO.getAllProvinces();
            int id = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest existingTicket = dao.getTechnicalRequestById(id);
            if (existingTicket != null) {
                String allProductsJson = gson.toJson(dao.getAllProducts());
                String existingDevicesJson = gson.toJson(existingTicket.getDevices());

                request.setAttribute("ticket", existingTicket);
                request.setAttribute("contractList", dao.getAllActiveContracts());
                request.setAttribute("customerList", dao.getAllEnterprises());
                request.setAttribute("employeeList", dao.getAllTechnicians());
                request.setAttribute("serviceList", dao.getAllServices());

                MaintenanceSchedule schedule = dao.getScheduleByTechnicalRequestId(id);

                request.setAttribute("schedule", schedule);
                request.setAttribute("provinces", provinces);

                // Nạp sẵn quận/phường nếu có schedule
                if (schedule != null && schedule.getProvinceId() != null) {
                    request.setAttribute("districts", enterpriseDAO.getDistrictsByProvinceId(schedule.getProvinceId()));
                    if (schedule.getDistrictId() != null) {
                        request.setAttribute("wards", enterpriseDAO.getWardsByDistrictId(schedule.getDistrictId()));
                    } else {
                        request.setAttribute("wards", java.util.Collections.emptyList());
                    }
                } else {
                    request.setAttribute("districts", java.util.Collections.emptyList());
                    request.setAttribute("wards", java.util.Collections.emptyList());
                }
                List<Integer> assignedUserIds = scheduleDao.getAssignedUserIdsByScheduleId(schedule.getId());
                request.setAttribute("assignedUserIds", assignedUserIds);
                request.setAttribute("allProductsJson", allProductsJson);
                request.setAttribute("existingDevicesJson", existingDevicesJson);
                request.getRequestDispatcher("/jsp/customerSupport/editTransaction.jsp").forward(request, response);

            } else {
                response.sendRedirect("ticket?action=list&error=notFound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ticket?action=list&error=invalidId");
        }
    }

// 2. Thêm phương thức mới để xử lý việc cập nhật (POST)
    private void updateTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String ticketIdStr = request.getParameter("id");
            if (ticketIdStr == null || ticketIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=missingId");
                return;
            }
            int ticketId = Integer.parseInt(ticketIdStr);
            // 1. Lấy tất cả thông tin từ form
            TechnicalRequest oldTicket = dao.getTechnicalRequestById(ticketId);
            if (oldTicket == null) {
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=notFound");
                return;
            }
            String oldStatus = oldTicket.getStatus();
            TechnicalRequest updatedRequest = new TechnicalRequest();
            updatedRequest.setId(ticketId);
            updatedRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            updatedRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            updatedRequest.setAssignedToId(employeeId);
            updatedRequest.setAssignedUserIds(Collections.singletonList(employeeId));
            updatedRequest.setPriority(request.getParameter("priority"));
            String newStatus = request.getParameter("status");
            updatedRequest.setStatus(newStatus);

            String description = request.getParameter("description");
            updatedRequest.setDescription(description);
            // Tự động tạo title từ description
            updatedRequest.setTitle(description.length() > 100 ? description.substring(0, 100) + "..." : description);

            updatedRequest.setPriority(request.getParameter("priority"));
            updatedRequest.setStatus(request.getParameter("status"));

            boolean isBillable = Boolean.parseBoolean(request.getParameter("isBillable"));
            updatedRequest.setIsBillable(isBillable);
            if (isBillable) {
                String amountStr = request.getParameter("amount");
                updatedRequest.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                updatedRequest.setEstimatedCost(0);
            }

            // 2. Xử lý danh sách thiết bị một cách ổn định
            List<TechnicalRequestDevice> devices = new ArrayList<>();
            // Vòng lặp này sẽ chạy từ 1 đến N (ví dụ 100) để tìm tất cả các thiết bị có thể có
            // Cách này đảm bảo lấy được hết thiết bị kể cả khi người dùng xóa dòng ở giữa
            for (int i = 1; i < 100; i++) {
                String deviceName = request.getParameter("deviceName_" + i);
                // Nếu không tìm thấy deviceName, nghĩa là đã hết thiết bị, dừng vòng lặp
                if (deviceName == null) {
                    break;// Dừng lại khi không còn thiết bị
                }
                // Chỉ xử lý nếu deviceName không rỗng
                if (!deviceName.trim().isEmpty()) {
                    String serial = request.getParameter("deviceSerial_" + i);
                    String note = request.getParameter("deviceNote_" + i);

                    TechnicalRequestDevice device = new TechnicalRequestDevice();
                    device.setDeviceName(deviceName);
                    device.setSerialNumber(serial);
                    device.setProblemDescription(note);
                    devices.add(device);
                }
            }

            // 3. Gọi hàm update trong DAO
            boolean success = dao.updateTechnicalRequest(updatedRequest, devices);
            String surveySentParam = ""; // Chuẩn bị tham số cho URL chuyển hướng
            if (success) {
                // Điều kiện: Trạng thái MỚI là 'resolved' hoặc 'closed'
                // VÀ trạng thái CŨ KHÔNG PHẢI là 'resolved' hoặc 'closed'
                boolean shouldSendEmail = (newStatus.equals("resolved") || newStatus.equals("closed"))
                        && !(oldStatus.equals("resolved") || oldStatus.equals("closed"));

                if (shouldSendEmail) {
                    surveySentParam = "&surveySent=true"; // Gán giá trị nếu email được gửi

                    // Lấy thông tin cần thiết từ đối tượng `oldTicket` đã lấy ở trên
                    String recipientEmail = oldTicket.getEnterpriseEmail();
                    String enterpriseName = oldTicket.getEnterpriseName();
                    String requestCode = oldTicket.getRequestCode();

                    if (recipientEmail != null && !recipientEmail.trim().isEmpty()) {
                        // Gửi email trong luồng riêng để không làm chậm trải nghiệm người dùng
                        emailExecutor.submit(() -> {
                            try {
                                // Tạo link khảo sát động
                                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                                String surveyLink = baseUrl + "/feedback?action=create&technicalRequestId=" + ticketId;

                                // Soạn tiêu đề và nội dung email
                                String subject = "Mời bạn đánh giá chất lượng dịch vụ cho yêu cầu #" + requestCode;
                                String body = "<html>"
                                        + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                                        + "<h3>Kính gửi Quý khách hàng " + enterpriseName + ",</h3>"
                                        + "<p>Yêu cầu hỗ trợ kỹ thuật với mã số <strong>" + requestCode + "</strong> của Quý khách đã được xử lý xong.</p>"
                                        + "<p>Chúng tôi rất mong nhận được những ý kiến đóng góp quý báu của Quý khách để cải thiện chất lượng dịch vụ. Vui lòng dành chút thời gian để thực hiện khảo sát bằng cách nhấn vào nút bên dưới:</p>"
                                        + "<div style='text-align: center; margin: 25px 0;'>"
                                        + "<a href=\"" + surveyLink + "\" style='background-color:#2563eb;color:white;padding:12px 25px;text-align:center;text-decoration:none;display:inline-block;border-radius:8px;font-size:16px;'><strong>Thực hiện khảo sát</strong></a>"
                                        + "</div>"
                                        + "<p>Nếu nút bấm không hoạt động, vui lòng sao chép và dán đường dẫn sau vào trình duyệt của bạn:<br><a href='" + surveyLink + "'>" + surveyLink + "</a></p>"
                                        + "<p>Trân trọng cảm ơn,<br><strong>Đội ngũ DPCRM</strong>.</p>"
                                        + "</body>"
                                        + "</html>";

                                EmailServiceFeedback.sendMail(recipientEmail, subject, body);
                            } catch (Exception e) {
                                System.err.println("Lỗi nghiêm trọng xảy ra trong luồng gửi email:");
                                e.printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("CẢNH BÁO: Không thể gửi email khảo sát cho yêu cầu #" + requestCode + " vì doanh nghiệp '" + enterpriseName + "' không có địa chỉ email.");
                    }
                }
            }
            // 4. Chuyển hướng về trang xem chi tiết
            response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId
                    + "&update=" + (success ? "success" : "failed")
                    + surveySentParam);

        } catch (Exception e) {
            e.printStackTrace();
            String ticketId = request.getParameter("id");
            response.sendRedirect(request.getContextPath() + "/ticket?action=edit&id=" + ticketId + "&error=unknown");
        }
    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            MaintenanceSchedule schedule = new MaintenanceSchedule();
            MaintenanceScheduleDAO scheduleDAO = new MaintenanceScheduleDAO();
            TechnicalRequest newRequest = new TechnicalRequest();
            AddressDAO addressDAO = new AddressDAO();
            newRequest.setReporterId(1); // Giả sử ID người báo cáo là 1

            // --- Sửa lỗi NumberFormatException ---
            // Kiểm tra trước khi parse để tránh lỗi
            String enterpriseIdStr = request.getParameter("enterpriseId");
            String title = request.getParameter("title");
            if (enterpriseIdStr != null && !enterpriseIdStr.isEmpty()) {
                newRequest.setEnterpriseId(Integer.parseInt(enterpriseIdStr));
            }

            String serviceIdStr = request.getParameter("serviceId");
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                newRequest.setServiceId(Integer.parseInt(serviceIdStr));
            }

            String employeeIdStr = request.getParameter("employeesId");
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                int employeeId = Integer.parseInt(employeeIdStr);
                newRequest.setAssignedUserIds(Collections.singletonList(employeeId));
                schedule.setAssignedUserId(employeeId);
            }

            String contractIdStr = request.getParameter("contractId");
            if (contractIdStr != null && !contractIdStr.isEmpty()) {
                newRequest.setContractId(Integer.parseInt(contractIdStr));
            }
            String status = request.getParameter("status");
            newRequest.setStatus(status);
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");
            String streetAddress = request.getParameter("streetAddress");

            if (provinceIdStr == null || provinceIdStr.isBlank()
                    || districtIdStr == null || districtIdStr.isBlank()
                    || wardIdStr == null || wardIdStr.isBlank()) {
                throw new IllegalArgumentException("Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, và Phường/Xã.");
            }
            // --- Xử lý địa chỉ (tách ra khỏi block end_time) ---
            try {
                // --- Xử lý ngày giờ ---
                String scheduledDateStr = request.getParameter("scheduled_date");
                if (scheduledDateStr != null && !scheduledDateStr.isEmpty()) {
                    LocalDate scheduledDate = LocalDate.parse(scheduledDateStr);
                    schedule.setScheduledDate(scheduledDate);
                }

                String endDateStr = request.getParameter("end_date");
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    schedule.setEndDate(endDate);
                }

                String startTimeStr = request.getParameter("start_time");
                if (startTimeStr != null && !startTimeStr.isEmpty()) {
                    LocalTime startTime = LocalTime.parse(startTimeStr);
                    schedule.setStartTime(startTime);
                }

                String endTimeStr = request.getParameter("end_time");
                if (endTimeStr != null && !endTimeStr.isEmpty()) {
                    LocalTime endTime = LocalTime.parse(endTimeStr);
                    schedule.setEndTime(endTime);
                }

                if (provinceIdStr != null && !provinceIdStr.isEmpty()
                        && districtIdStr != null && !districtIdStr.isEmpty()
                        && wardIdStr != null && !wardIdStr.isEmpty()) {

                    int provinceId = Integer.parseInt(provinceIdStr);
                    int districtId = Integer.parseInt(districtIdStr);
                    int wardId = Integer.parseInt(wardIdStr);

                    // Tạo hoặc tìm địa chỉ trong database
                    int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);

                    // Gán addressId vào object schedule
                    schedule.setAddressId(addressId);

                    // Hoặc nếu bạn muốn gán vào newRequest:
                    // newRequest.setAddressId(addressId);
                    System.out.println("Address ID đã được gán: " + addressId);
                } else {
                    System.out.println("Thông tin địa chỉ không đầy đủ");
                }

            } catch (DateTimeParseException e) {
                System.err.println("Lỗi parse ngày/giờ: " + e.getMessage());
                // Xử lý lỗi ngày giờ
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse số: " + e.getMessage());
                // Xử lý lỗi parse số
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý dữ liệu: " + e.getMessage());
                e.printStackTrace();
            }

            // --- Giữ nguyên logic lấy mô tả ---
            String description = request.getParameter("description");
            newRequest.setDescription(description.length() > 100 ? description.substring(0, 100) + "..." : description);
            newRequest.setTitle(title);

            // --- Sửa lỗi logic Mức độ ưu tiên ---
            // So sánh với value tiếng Anh thay vì text tiếng Việt
            String priorityValue = request.getParameter("priority");
            if ("high".equals(priorityValue)) {
                newRequest.setPriority("high");
            } else if ("urgent".equals(priorityValue)) { // Giả sử value là 'urgent'
                newRequest.setPriority("critical");
            } else if ("low".equals(priorityValue)) { // Giả sử value là 'low'
                newRequest.setPriority("low");
            } else {
                newRequest.setPriority("medium"); // Mặc định
            }
            // --- Giữ nguyên logic các phần còn lại ---
            boolean isBillable = Boolean.parseBoolean(request.getParameter("isBillable"));
            newRequest.setIsBillable(isBillable);
            if (isBillable) {
                String amountStr = request.getParameter("amount");
                newRequest.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                newRequest.setEstimatedCost(0);
            }

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            String deviceName;
            while ((deviceName = request.getParameter("deviceName_" + i)) != null) {
                if (!deviceName.trim().isEmpty()) {
                    String serial = request.getParameter("deviceSerial_" + i);
                    String note = request.getParameter("deviceNote_" + i);

                    // Sửa lại bằng cách dùng setter để đảm bảo gán đúng giá trị
                    TechnicalRequestDevice device = new TechnicalRequestDevice();
                    device.setDeviceName(deviceName);
                    device.setSerialNumber(serial);
                    device.setProblemDescription(note); // Gán mô tả sự cố vào đúng trường

                    devices.add(device);
                }
                i++;
            }
            // Lấy một MẢNG các ID của nhân viên được chọn
            String[] selectedEmployeeIds = request.getParameterValues("employeesId");
            Integer newRequestId = dao.createTechnicalRequest(newRequest, devices);
            String color = request.getParameter("color");
            if (newRequestId != null) {
                // Tạo schedule: gán technicalRequestId từ newRequestId cho schedule
                schedule.setTechnicalRequestId(newRequestId);
                schedule.setColor(color);
                schedule.setStatusId(2);
                Integer firstUserId = null;

                if (selectedEmployeeIds != null && selectedEmployeeIds.length > 0) {
                    String raw = selectedEmployeeIds[0];
                    if (raw != null && !raw.isBlank()) {
                        try {
                            firstUserId = Integer.valueOf(raw.trim());   // có thể ném NumberFormatException
                        } catch (NumberFormatException ex) {
                            System.err.println("ID nhân viên không hợp lệ: " + raw);
                        }
                    }
                }

                /* -------- 2. Insert MaintenanceSchedule và gán nhân viên -------- */
                int scheduleId = scheduleDAO.addMaintenanceScheduleAndReturnId(schedule);

                if (scheduleId > 0 && firstUserId != null) {
                    // Phương thức DAO đã có: addMaintenanceAssignments(int scheduleId, List<Integer> userIds)
                    scheduleDAO.addMaintenanceAssignments(
                            scheduleId,
                            java.util.Collections.singletonList(firstUserId));
                    System.out.println("Đã gán nhân viên ID " + firstUserId + " cho lịch ID " + scheduleId);
                } else {
                    System.out.println("Không gán nhân viên nào cho lịch (scheduleId=" + scheduleId + ", firstUserId=" + firstUserId + ")");
                }

                // Lệnh chuyển hướng: thành công nếu cả hai ID đều có kết quả
                if (scheduleId > 0) {
                    response.sendRedirect("ticket?action=list&create=success");
                } else {
                    response.sendRedirect("ticket?action=list&create=failed");
                }
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

            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
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
}
