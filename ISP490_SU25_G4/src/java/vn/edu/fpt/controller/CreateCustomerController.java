// File: src/main/java/vn/edu/fpt/controller/CreateCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.CustomerType;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Handles the creation of a new customer. GET: Displays the form with necessary
 * data for dropdowns. POST: Processes the form submission, saves the data, and
 * shows a success message.
 */
@WebServlet(name = "CreateCustomerController", urlPatterns = {"/createCustomer"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class CreateCustomerController extends HttpServlet {

    // Thư mục lưu trữ ảnh, tương đối so với thư mục gốc của ứng dụng web
    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
        if (session == null || session.getAttribute("user") == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang login
            response.sendRedirect("login.jsp");
            return; // Dừng xử lý tiếp theo
        }

        // Only load data for dropdowns if we are not showing a success message.
        // This prevents unnecessary database queries when forwarding just to show the overlay.
        if (request.getAttribute("successMessage") == null) {
            try {
                AddressDAO addressDAO = new AddressDAO();
                CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();
                UserDAO userDAO = new UserDAO();

                List<Province> provinces = addressDAO.getAllProvinces();
                List<CustomerType> customerTypes = customerTypeDAO.getAllCustomerTypes();
                // Use the optimized method for dropdowns
                List<User> employees = userDAO.getAllEmployees();

                request.setAttribute("provinces", provinces);
                request.setAttribute("customerTypes", customerTypes);
                request.setAttribute("employees", employees);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Không thể tải dữ liệu cần thiết: " + e.getMessage());
            }
        }

        // Always forward to the JSP page.
        request.getRequestDispatcher("/jsp/sales/createCustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
        if (session == null || session.getAttribute("user") == null) {
            // Nếu chưa đăng nhập, trả về lỗi hoặc chuyển hướng
            // Chuyển hướng là tốt nhất để người dùng có thể đăng nhập lại
            response.sendRedirect("login.jsp");
            return; // Dừng xử lý tiếp theo
        }
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        // Get customer name early to use in success/error messages
        String customerName = request.getParameter("customerName");

        try {

            // === PHẦN 1: XỬ LÝ UPLOAD FILE ẢNH ===
            String avatarDbPath = null; // Đường dẫn để lưu vào DB
            Part filePart = request.getPart("avatar"); // Lấy file từ form

            // Kiểm tra xem người dùng có thực sự tải file lên không
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Tạo tên file duy nhất để tránh ghi đè
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

                // Lấy đường dẫn tuyệt đối đến thư mục gốc của ứng dụng web
                String applicationPath = request.getServletContext().getRealPath("");
                // Tạo đường dẫn đầy đủ đến thư mục lưu trữ
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                // Tạo thư mục nếu nó chưa tồn tại
                File fileSaveDir = new File(uploadFilePath);
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdirs();
                }

                // Ghi file vào thư mục
                filePart.write(uploadFilePath + File.separator + uniqueFileName);

                // Tạo đường dẫn tương đối để lưu vào DB (dùng dấu / cho web path)
                avatarDbPath = "uploads/avatars/" + uniqueFileName;
            }

            // Get other form parameters
            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String taxCode = request.getParameter("taxCode");
            String bankNumber = request.getParameter("bankNumber");

            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress");
            int customerGroupId = Integer.parseInt(request.getParameter("customerGroup"));
            String employeeIdStr = request.getParameter("employeeId");

            // Validate that an employee was selected
            if (employeeIdStr == null || employeeIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                doGet(request, response);
                return;
            }
            int employeeId = Integer.parseInt(employeeIdStr);

            // Start a database transaction
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            // Initialize DAOs
            AddressDAO addressDAO = new AddressDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            EnterpriseAssignmentDAO assignmentDAO = new EnterpriseAssignmentDAO();

            // 1. Insert address and get the new ID
            int newAddressId = addressDAO.insertAddress(conn, streetAddress, wardId, districtId, provinceId);
            // 2. Insert enterprise and get the new ID
            int newEnterpriseId = enterpriseDAO.insertEnterprise(conn, customerName, customerGroupId, newAddressId, taxCode, bankNumber, avatarDbPath);
            // 3. Insert primary contact for the enterprise
            enterpriseDAO.insertEnterpriseContact(conn, newEnterpriseId, fullName, position, phone, email);
            // 4. Assign the responsible employee
            assignmentDAO.insertAssignment(conn, newEnterpriseId, employeeId, "account_manager");

            // If all operations are successful, commit the transaction
            conn.commit();

            // --- SUCCESS LOGIC ---
            // 1. Set success message and redirect URL as request attributes
            request.setAttribute("successMessage", "Đã thêm thành công khách hàng '" + customerName + "'!");
            request.setAttribute("redirectUrl", request.getContextPath() + "/listCustomer");

            // 2. Forward back to the JSP to display the success overlay
            doGet(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // If any error occurs, rollback the transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Send error message back to the form
            request.setAttribute("errorMessage", "Tạo khách hàng thất bại: " + e.getMessage());
            doGet(request, response);
        } finally {
            // Close the connection
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
}
