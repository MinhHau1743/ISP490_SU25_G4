// File: vn/edu/fpt/controller/EditCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "EditCustomerController", urlPatterns = {"/editCustomer"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class EditCustomerController extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
        if (session == null || session.getAttribute("user") == null) {
            // Nếu chưa đăng nhập, trả về lỗi hoặc chuyển hướng
            // Chuyển hướng là tốt nhất để người dùng có thể đăng nhập lại
            response.sendRedirect("login.jsp");
            return; // Dừng xử lý tiếp theo
        }
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("listCustomer");
            return;
        }
        try {
            int enterpriseId = Integer.parseInt(idStr);
            Enterprise customer = new EnterpriseDAO().getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng.");
                request.getRequestDispatcher("/jsp/sales/listCustomer.jsp").forward(request, response);
                return;
            }

            AddressDAO addressDAO = new AddressDAO();
            request.setAttribute("customer", customer);
            request.setAttribute("allProvinces", addressDAO.getAllProvinces());
            request.setAttribute("districtsForCustomer", addressDAO.getDistrictsByProvinceId(customer.getProvinceId()));
            request.setAttribute("wardsForCustomer", addressDAO.getWardsByDistrictId(customer.getDistrictId()));
            request.setAttribute("allCustomerTypes", new CustomerTypeDAO().getAllCustomerTypes());
            request.setAttribute("allEmployees", new UserDAO().getAllEmployees());

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
        request.getRequestDispatcher("/jsp/sales/editCustomerDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
        if (session == null || session.getAttribute("user") == null) {
            // Nếu chưa đăng nhập, trả về lỗi hoặc chuyển hướng
            // Chuyển hướng là tốt nhất để người dùng có thể đăng nhập lại
            response.sendRedirect("login.jsp");
            return; // Dừng xử lý tiếp theo
        }
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        int enterpriseId = Integer.parseInt(request.getParameter("enterpriseId"));
        int addressId = Integer.parseInt(request.getParameter("addressId"));
        String existingAvatarUrl = request.getParameter("existingAvatarUrl");

        try {

            // *** THAY ĐỔI: BẮT ĐẦU KHỐI VALIDATION ***
            // 1. Lấy và validate tất cả các trường bắt buộc
            String customerName = request.getParameter("customerName");
            if (customerName == null || customerName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp không được để trống.");
                doGet(request, response);
                return;
            }

            String hotline = request.getParameter("hotline");
            if (hotline == null || hotline.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Fax/Hotline của doanh nghiệp.");
                doGet(request, response);
                return;
            }

            String businessEmail = request.getParameter("businessEmail");
            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Email của doanh nghiệp.");
                doGet(request, response);
                return;
            }

            String customerGroupIdStr = request.getParameter("customerGroup");
            if (customerGroupIdStr == null || customerGroupIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhóm khách hàng.");
                doGet(request, response);
                return;
            }

            String employeeIdStr = request.getParameter("employeeId");
            if (employeeIdStr == null || employeeIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                doGet(request, response);
                return;
            }

            String provinceIdStr = request.getParameter("province");
            if (provinceIdStr == null || provinceIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Tỉnh/Thành phố.");
                doGet(request, response);
                return;
            }

            String districtIdStr = request.getParameter("district");
            if (districtIdStr == null || districtIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Quận/Huyện.");
                doGet(request, response);
                return;
            }

            String wardIdStr = request.getParameter("ward");
            if (wardIdStr == null || wardIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Phường/Xã.");
                doGet(request, response);
                return;
            }

            String streetAddress = request.getParameter("streetAddress");
            if (streetAddress == null || streetAddress.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ cụ thể.");
                doGet(request, response);
                return;
            }

            // Chuyển đổi sang số sau khi đã qua validation
            int customerGroupId = Integer.parseInt(customerGroupIdStr);
            int employeeId = Integer.parseInt(employeeIdStr);
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            // *** KẾT THÚC KHỐI VALIDATION ***

            // 1. Handle File Upload
            String avatarDbPath = existingAvatarUrl;
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
                new File(uploadFilePath).mkdirs();
                filePart.write(uploadFilePath + File.separator + uniqueFileName);
                avatarDbPath = "uploads/avatars/" + uniqueFileName;
            }

            // *** THAY ĐỔI: Lấy các trường không bắt buộc và gán "N/A" nếu trống ***
            String taxCode = request.getParameter("taxCode");
            if (taxCode == null || taxCode.trim().isEmpty()) {
                taxCode = "N/A";
            }

            String bankNumber = request.getParameter("bankNumber");
            if (bankNumber == null || bankNumber.trim().isEmpty()) {
                bankNumber = "N/A";
            }

            String fullName = request.getParameter("fullName");
            if (fullName == null || fullName.trim().isEmpty()) {
                fullName = "N/A";
            }

            String position = request.getParameter("position");
            if (position == null || position.trim().isEmpty()) {
                position = "N/A";
            }

            String phone = request.getParameter("phone");
            if (phone == null || phone.trim().isEmpty()) {
                phone = "N/A";
            }

            String email = request.getParameter("email");
            if (email == null || email.trim().isEmpty()) {
                email = "N/A";
            }

            // 3. Start Transaction
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            // 4. Update Database
            // === SỬA LỖI TẠI ĐÂY: Sử dụng setters thay vì constructor không chính xác ===
            Enterprise enterpriseToUpdate = new Enterprise();
            enterpriseToUpdate.setId(enterpriseId);
            enterpriseToUpdate.setName(customerName);
            enterpriseToUpdate.setBusinessEmail(businessEmail);
            enterpriseToUpdate.setFax(hotline);
            enterpriseToUpdate.setTaxCode(taxCode);
            enterpriseToUpdate.setBankNumber(bankNumber);
            enterpriseToUpdate.setCustomerTypeId(customerGroupId);
            enterpriseToUpdate.setAvatarUrl(avatarDbPath); // Gán đường dẫn ảnh mới (hoặc cũ)

            new EnterpriseDAO().updateEnterprise(conn, enterpriseToUpdate);
            new AddressDAO().updateAddress(conn, addressId, streetAddress, wardId, districtId, provinceId);
            new EnterpriseContactDAO().updatePrimaryContact(conn, enterpriseId, fullName, position, phone, email);
            new EnterpriseAssignmentDAO().updateMainAssignment(conn, enterpriseId, employeeId);

            conn.commit();
            response.sendRedirect("viewCustomer?id=" + enterpriseId);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            request.setAttribute("errorMessage", "Lưu thay đổi thất bại: " + e.getMessage());
            doGet(request, response);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
