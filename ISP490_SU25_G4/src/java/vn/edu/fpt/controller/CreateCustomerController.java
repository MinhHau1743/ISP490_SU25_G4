// File: src/java/vn/edu/fpt/controller/CreateCustomerController.java
package vn.edu.fpt.controller;

import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CustomerTypeDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.model.CustomerType;

import vn.edu.fpt.model.Province;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CreateCustomerController", urlPatterns = {"/createCustomer"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class CreateCustomerController extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Tải dữ liệu cần thiết cho các dropdown
            AddressDAO addressDAO = new AddressDAO();
            CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();
            UserDAO userDAO = new UserDAO();

            List<Province> provinces = addressDAO.getAllProvinces();
            List<CustomerType> customerTypes = customerTypeDAO.getAllCustomerTypes();
            List<User> employees = userDAO.getAllEmployees();

            request.setAttribute("provinces", provinces);
            request.setAttribute("customerTypes", customerTypes);
            request.setAttribute("employees", employees); // JSP đang comment, nhưng ta vẫn gửi qua

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu cần thiết: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/createCustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
            // Lấy dữ liệu từ form
            String customerName = request.getParameter("customerName");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress");
            int customerGroupId = Integer.parseInt(request.getParameter("customerGroup"));

            // Bắt đầu transaction
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            String employeeIdStr = request.getParameter("employeeId");
            if (employeeIdStr == null || employeeIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                // Tải lại dữ liệu cho form và hiển thị lại trang với thông báo lỗi
                doGet(request, response);
                return; // Dừng xử lý ngay lập tức
            }
            int employeeId = Integer.parseInt(employeeIdStr);

            AddressDAO addressDAO = new AddressDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            EnterpriseAssignmentDAO assignmentDAO = new EnterpriseAssignmentDAO(); // KHỞI TẠO DAO MỚI

            // 1. Thêm địa chỉ và lấy ID trả về
            int newAddressId = addressDAO.insertAddress(conn, streetAddress, wardId, districtId, provinceId);

            // 2. Thêm doanh nghiệp và lấy ID trả về
            int newEnterpriseId = enterpriseDAO.insertEnterprise(conn, customerName, customerGroupId, newAddressId);

            // 3. Thêm người liên hệ chính cho doanh nghiệp (giữ nguyên)
            enterpriseDAO.insertEnterpriseContact(conn, newEnterpriseId, customerName, phone, email);

            // 4. Phân công nhân viên phụ trách (SỬ DỤNG DAO MỚI)
            // Vì giao diện chỉ có 1 dropdown, ta sẽ gán một vai trò mặc định
            String assignmentType = "account_manager"; // Ví dụ: người quản lý chính
            assignmentDAO.insertAssignment(conn, newEnterpriseId, employeeId, assignmentType);

            // Nếu không có lỗi, commit transaction
            conn.commit();

            // Chuyển hướng về trang danh sách
            response.sendRedirect("listCustomer");

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi, rollback transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Gửi thông báo lỗi về lại trang tạo mới
            request.setAttribute("errorMessage", "Tạo khách hàng thất bại: " + e.getMessage());
            // Tải lại dữ liệu cho form
            doGet(request, response);
        } finally {
            // Đóng connection
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
