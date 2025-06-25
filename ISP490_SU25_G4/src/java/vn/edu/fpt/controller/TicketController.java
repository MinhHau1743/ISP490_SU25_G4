// File: vn/edu/fpt/controller/TicketController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TicketController extends HttpServlet {

    private TechnicalRequestDAO technicalRequestDAO;

    @Override
    public void init() {
        // Khởi tạo DAO một lần khi servlet được load
        technicalRequestDAO = new TechnicalRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Nếu không có action, mặc định là hiển thị danh sách
        }

        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "list":
            default:
                listTickets(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            createTicket(request, response);
        }
    }

    /**
     * Lấy danh sách yêu cầu và chuyển đến trang listTransaction.jsp.
     */
    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<TechnicalRequest> transactionList = technicalRequestDAO.getAllTechnicalRequests();
        request.setAttribute("transactions", transactionList);
        // SỬA LẠI ĐƯỜNG DẪN cho khớp với file của bạn
        request.getRequestDispatcher("/jsp/customerSupport/listTransaction.jsp").forward(request, response);
    }

    /**
     * Chuẩn bị dữ liệu và hiển thị form tạo mới.
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("customerList", technicalRequestDAO.getAllEnterprises());
        request.setAttribute("employeeList", technicalRequestDAO.getAllTechnicians());
        request.setAttribute("serviceList", technicalRequestDAO.getAllServices());
        // SỬA LẠI ĐƯỜNG DẪN cho khớp với file của bạn
        request.getRequestDispatcher("/jsp/customerSupport/createTicket.jsp").forward(request, response);
    }

    /**
     * Xử lý dữ liệu gửi từ form tạo mới (đã bỏ qua đăng nhập).
     */
    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            TechnicalRequest newRequest = new TechnicalRequest();

            // === THAY ĐỔI THEO YÊU CẦU: GÁN CỨNG NGƯỜI TẠO PHIẾU ===
            // Gán cứng ID người tạo phiếu là 1.
            // BẠN CẦN ĐẢM BẢO CÓ USER VỚI ID=1 TRONG DATABASE.
            newRequest.setReporterId(1);

            // --- Phần còn lại của hàm được giữ nguyên ---
            newRequest.setEnterpriseId(Integer.parseInt(request.getParameter("customerId")));
            newRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            newRequest.setAssignedToId(Integer.parseInt(request.getParameter("employeeId")));

            String description = request.getParameter("description");
            newRequest.setDescription(description);

            if (description != null && description.length() > 100) {
                newRequest.setTitle(description.substring(0, 100) + "...");
            } else {
                newRequest.setTitle(description);
            }

            String priorityVie = request.getParameter("priority");
            String priorityDb = "medium";
            if ("Cao".equals(priorityVie)) {
                priorityDb = "high";
            } else if ("Khẩn cấp".equals(priorityVie)) {
                priorityDb = "critical";
            }
            newRequest.setPriority(priorityDb);

            newRequest.setIsBillable(Boolean.parseBoolean(request.getParameter("isBillable")));
            if (newRequest.isIsBillable()) {
                String amountStr = request.getParameter("amount");
                newRequest.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                newRequest.setEstimatedCost(0);
            }

            String contractCode = request.getParameter("contractCode");
            newRequest.setContractId(technicalRequestDAO.getContractIdByCode(contractCode));

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            while (request.getParameter("deviceName_" + i) != null) {
                String deviceName = request.getParameter("deviceName_" + i);
                if (deviceName != null && !deviceName.trim().isEmpty()) {
                    String serial = request.getParameter("deviceSerial_" + i);
                    String note = request.getParameter("deviceNote_" + i);
                    devices.add(new TechnicalRequestDevice(deviceName, serial, note));
                }
                i++;
            }

            boolean success = technicalRequestDAO.createTechnicalRequest(newRequest, devices);
            response.sendRedirect("ticket?action=list&create=" + (success ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=create&error=unknown");
        }
    }
}
