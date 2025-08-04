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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.model.ContractProduct;
import vn.edu.fpt.model.Service;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TicketController extends HttpServlet {

    private TechnicalRequestDAO dao;
    private final Gson gson = new Gson();

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
                case "delete": // <-- THÊM CASE NÀY
                    deleteTicket(request, response);
                    break;

                case "list":
                default:
                    listTickets(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra log server để debug
            throw new ServletException("Database access error.", e);
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

    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
    // 1. Lấy tất cả tham số lọc và phân trang
    String query = request.getParameter("query");
    String status = request.getParameter("status");
    String serviceIdStr = request.getParameter("serviceId");
    int serviceId = 0;
    if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
        try { serviceId = Integer.parseInt(serviceIdStr); } catch (NumberFormatException e) { /* Bỏ qua */ }
    }
    
    int page = 1;
    final int LIMIT = 9; // Hiển thị 9 thẻ mỗi trang (3x3 grid)
    String pageStr = request.getParameter("page");
    if (pageStr != null && !pageStr.isEmpty()) {
        try { page = Integer.parseInt(pageStr); } catch (NumberFormatException e) { page = 1; }
    }

    // 2. Lấy tổng số item với bộ lọc để tính toán số trang
    int totalItems = dao.getTotalFilteredRequestCount(query, status, serviceId);
    int totalPages = (int) Math.ceil((double) totalItems / LIMIT);
    if (totalPages == 0) totalPages = 1;
    if (page > totalPages) page = totalPages;

    // 3. Tính offset
    int offset = (page - 1) * LIMIT;

    // 4. Lấy danh sách giao dịch đã lọc
    List<TechnicalRequest> transactionList = dao.getFilteredTechnicalRequests(query, status, serviceId, LIMIT, offset);

    // 5. Lấy danh sách cho các dropdown bộ lọc
    List<Service> serviceList = dao.getAllServices();
    List<String> statusList = dao.getDistinctStatuses();

    // 6. Gửi tất cả dữ liệu sang JSP
    request.setAttribute("transactions", transactionList);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("currentPage", page);
    request.setAttribute("totalItems", totalItems); // Gửi thêm tổng số item
    request.setAttribute("serviceList", serviceList); // Gửi danh sách dịch vụ
    request.setAttribute("statusList", statusList);   // Gửi danh sách trạng thái
    request.setAttribute("activeMenu", "ticket");

    request.getRequestDispatcher("/jsp/customerSupport/listTransaction.jsp").forward(request, response);
}

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        request.setAttribute("customerList", dao.getAllEnterprises());
        request.setAttribute("employeeList", dao.getAllTechnicians());
        request.setAttribute("serviceList", dao.getAllServices());
        request.setAttribute("contractList", dao.getAllActiveContracts()); // <--- THÊM DÒNG NÀY

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

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest existingTicket = dao.getTechnicalRequestById(id);

            if (existingTicket != null) {
                // Dùng Gson để chuyển đổi danh sách thành chuỗi JSON an toàn
                String allProductsJson = gson.toJson(dao.getAllProducts());
                String existingDevicesJson = gson.toJson(existingTicket.getDevices());

                // Gửi dữ liệu sang JSP
                request.setAttribute("ticket", existingTicket);
                request.setAttribute("customerList", dao.getAllEnterprises());
                request.setAttribute("employeeList", dao.getAllTechnicians());
                request.setAttribute("serviceList", dao.getAllServices());

                // *** THAY ĐỔI CÁCH GỬI DỮ LIỆU SANG JSP ***
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
            int ticketId = Integer.parseInt(request.getParameter("id"));

            // 1. Lấy tất cả thông tin từ form
            TechnicalRequest updatedRequest = new TechnicalRequest();
            updatedRequest.setId(ticketId);
            updatedRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            updatedRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            updatedRequest.setAssignedToId(Integer.parseInt(request.getParameter("employeeId")));

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
                    break;
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

            // 4. Chuyển hướng về trang xem chi tiết
            response.sendRedirect(request.getContextPath() + "/ticket?action=view&id=" + ticketId + "&update=" + (success ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
            String ticketId = request.getParameter("id");
            response.sendRedirect(request.getContextPath() + "/ticket?action=edit&id=" + ticketId + "&error=unknown");
        }
    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            TechnicalRequest newRequest = new TechnicalRequest();
            newRequest.setReporterId(1); // Giả sử ID người báo cáo là 1

            // --- Sửa lỗi NumberFormatException ---
            // Kiểm tra trước khi parse để tránh lỗi
            String enterpriseIdStr = request.getParameter("enterpriseId");
            if (enterpriseIdStr != null && !enterpriseIdStr.isEmpty()) {
                newRequest.setEnterpriseId(Integer.parseInt(enterpriseIdStr));
            }

            String serviceIdStr = request.getParameter("serviceId");
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                newRequest.setServiceId(Integer.parseInt(serviceIdStr));
            }

            String employeeIdStr = request.getParameter("employeeId");
            if (employeeIdStr != null && !employeeIdStr.isEmpty()) {
                newRequest.setAssignedToId(Integer.parseInt(employeeIdStr));
            }

            String contractIdStr = request.getParameter("contractId");
            if (contractIdStr != null && !contractIdStr.isEmpty()) {
                newRequest.setContractId(Integer.parseInt(contractIdStr));
            }

            // --- Giữ nguyên logic lấy mô tả ---
            String description = request.getParameter("description");
            newRequest.setDescription(description);
            newRequest.setTitle(description.length() > 100 ? description.substring(0, 100) + "..." : description);

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
            boolean success = dao.createTechnicalRequest(newRequest, devices);

            // Lệnh chuyển hướng này giờ sẽ chạy được
            response.sendRedirect("ticket?action=list&create=" + (success ? "success" : "failed"));

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
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&delete=" + (success ? "success" : "failed"));
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, cũng chuyển hướng về trang danh sách với thông báo lỗi
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&error=deleteFailed");
        }
    }
}
