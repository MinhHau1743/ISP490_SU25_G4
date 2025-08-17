// File: src/main/java/vn/edu/fpt/controller/CustomerController.java
package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.model.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.dao.UserDAO;

/**
 * Controller tổng hợp quản lý tất cả các hoạt động liên quan đến Khách hàng
 * (Enterprise). Sử dụng một Servlet duy nhất để điều hướng các action dựa trên
 * URL. Actions: /list, /view, /create, /edit, /delete, /getDistricts,
 * /getWards, /searchSuggestions
 */
@WebServlet(name = "CustomerController", urlPatterns = {"/customer/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class CustomerController extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";
    private static final int PAGE_SIZE = 10;

    /**
     * Điều hướng các yêu cầu GET dựa trên đường dẫn URL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            action = "/list"; // Mặc định là action list
        }

        if (action.equals("/create") || action.equals("/edit")) {
            if (!hasWritePermission(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
                return;
            }
        }

        // Các action AJAX không cần xác thực session ở đây vì chúng chỉ trả về dữ liệu JSON
        if (!isAjaxRequest(action)) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
        }

        request.setCharacterEncoding("UTF-8");

        try {
            switch (action) {
                case "/list":
                    listCustomers(request, response);
                    break;
                case "/view":
                    viewCustomer(request, response);
                    break;
                case "/create":
                    showCreateForm(request, response);
                    break;
                case "/edit":
                    showEditForm(request, response);
                    break;
                case "/getDistricts":
                    getDistricts(request, response);
                    break;
                case "/getWards":
                    getWards(request, response);
                    break;
                case "/searchSuggestions":
                    getSearchSuggestions(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hành động không hợp lệ.");
            }
        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý yêu cầu GET trong CustomerController", e);
        }
    }

    /**
     * Điều hướng các yêu cầu POST dựa trên đường dẫn URL.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getPathInfo();

        // Tất cả các action trong doPost đều là hành động Ghi (create, edit, delete)
        if (!hasWritePermission(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }
        try {
            switch (action) {
                case "/create":
                    handleCreateCustomer(request, response);
                    break;
                case "/edit":
                    handleEditCustomer(request, response);
                    break;
                case "/delete":
                    handleDeleteCustomer(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hành động không hợp lệ.");
            }
        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý yêu cầu POST trong CustomerController", e);
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Lấy tham số lọc và phân trang
            String searchQuery = request.getParameter("search");
            String customerTypeId = request.getParameter("customerTypeId");
            String employeeId = request.getParameter("employeeId");
            String provinceId = request.getParameter("provinceId");
            String districtId = request.getParameter("districtId");
            String wardId = request.getParameter("wardId");
            String pageStr = request.getParameter("page");

            int page = 1;
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1; // Mặc định về trang 1 nếu tham số không hợp lệ
                }
            }

            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            // Lấy danh sách khách hàng đã được phân trang
            List<Enterprise> customerList = enterpriseDAO.getPaginatedActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId, page, PAGE_SIZE);

            // Đếm tổng số khách hàng thỏa mãn điều kiện lọc để tính tổng số trang
            int totalCustomers = enterpriseDAO.countActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);

            int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

            // Kiểm tra và đặt thông báo "Không tìm thấy kết quả"
            boolean isAction = (searchQuery != null && !searchQuery.isEmpty()) || (customerTypeId != null && !customerTypeId.isEmpty()) || (employeeId != null && !employeeId.isEmpty()) || (provinceId != null && !provinceId.isEmpty());
            if (isAction && customerList.isEmpty()) {
                request.setAttribute("noResultsFound", true);
            }

            // Lấy dữ liệu cho các dropdown bộ lọc
            request.setAttribute("allProvinces", new EnterpriseDAO().getAllProvinces());
            request.setAttribute("allCustomerTypes", new EnterpriseDAO().getAllCustomerTypes());
            request.setAttribute("allEmployees", new UserDAO().getAllEmployees());

            // Gửi dữ liệu sang JSP
            request.setAttribute("customerList", customerList);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);

            // Gửi lại các giá trị đã lọc để hiển thị trên form
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedCustomerTypeId", customerTypeId);
            request.setAttribute("selectedEmployeeId", employeeId);
            request.setAttribute("selectedProvinceId", provinceId);
            request.setAttribute("selectedDistrictId", districtId);
            request.setAttribute("selectedWardId", wardId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách khách hàng: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/listCustomer.jsp").forward(request, response);
    }

    private void viewCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Không tạo session mới nếu chưa có
        if (session != null && session.getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage"); // Xóa attribute sau khi dùng
        }
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/list");
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            Enterprise customer = enterpriseDAO.getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng với ID cung cấp.");
            } else {
                request.setAttribute("recentRequests", new TechnicalRequestDAO().getRecentRequestsByEnterprise(enterpriseId, 3));
                // Thêm số 5 để giới hạn số lượng hợp đồng lấy về
                request.setAttribute("recentContracts", new ContractDAO().getRecentContractsByEnterpriseId(enterpriseId, 5));;
            }
            request.setAttribute("customer", customer);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID khách hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/viewCustomerDetail.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("successMessage") == null) {
            try {
                request.setAttribute("provinces", new EnterpriseDAO().getAllProvinces());
                request.setAttribute("customerTypes", new EnterpriseDAO().getAllCustomerTypes());
                request.setAttribute("employees", new UserDAO().getAllEmployees());
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Không thể tải dữ liệu cần thiết: " + e.getMessage());
            }
        }
        request.getRequestDispatcher("/jsp/sales/createCustomer.jsp").forward(request, response);
    }

    // File: src/main/java/vn/edu/fpt/controller/CustomerController.java
    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String customerName = request.getParameter("customerName");

        try {
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            //--- KHỐI VALIDATE DỮ LIỆU ĐẦU VÀO (ĐẦY ĐỦ) ---
            // 1. Kiểm tra các trường văn bản (text) và sự tồn tại
            if (customerName == null || customerName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp không được để trống.");
                showCreateForm(request, response);
                return;
            }
            if (enterpriseDAO.isNameExists(customerName, null)) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp '" + customerName + "' đã tồn tại.");
                showCreateForm(request, response);
                return;
            }
            String hotline = request.getParameter("hotline");
            if (hotline == null || hotline.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Hotline của doanh nghiệp.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("businessEmail") == null || request.getParameter("businessEmail").trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Email của doanh nghiệp.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("streetAddress") == null || request.getParameter("streetAddress").trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ cụ thể (số nhà, tên đường).");
                showCreateForm(request, response);
                return;
            }

            // 2. Kiểm tra các trường lựa chọn (select)
            if (request.getParameter("province") == null || request.getParameter("province").isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Tỉnh/Thành phố.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("district") == null || request.getParameter("district").isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Quận/Huyện.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("ward") == null || request.getParameter("ward").isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn Phường/Xã.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("employeeId") == null || request.getParameter("employeeId").isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                showCreateForm(request, response);
                return;
            }
            if (request.getParameter("customerGroup") == null || request.getParameter("customerGroup").isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhóm khách hàng.");
                showCreateForm(request, response);
                return;
            }

            // 3. Kiểm tra định dạng
            if (!isFormatValidVietnamesePhoneNumber(hotline)) {
                request.setAttribute("errorMessage", "Hotline không đúng định dạng của Việt Nam.");
                showCreateForm(request, response);
                return;
            }
            String taxCode = request.getParameter("taxCode");
            if (taxCode != null && !taxCode.trim().isEmpty() && !isFormatValidVietnameseTaxCode(taxCode)) {
                request.setAttribute("errorMessage", "Mã số thuế không đúng định dạng (10 hoặc 13 số).");
                showCreateForm(request, response);
                return;
            }
            String phone = request.getParameter("phone");
            if (phone != null && !phone.trim().isEmpty() && !isFormatValidVietnamesePhoneNumber(phone)) {
                request.setAttribute("errorMessage", "Số điện thoại người liên hệ không đúng định dạng.");
                showCreateForm(request, response);
                return;
            }

            //--- KẾT THÚC VALIDATE ---
            //--- XỬ LÝ UPLOAD AVATAR ---
            String avatarDbPath = null;
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

            //--- LẤY DỮ LIỆU VÀ LƯU VÀO DATABASE ---
            String businessEmail = request.getParameter("businessEmail");
            String streetAddress = request.getParameter("streetAddress");
            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String email = request.getParameter("email");
            String bankNumber = request.getParameter("bankNumber");
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            int customerGroupId = Integer.parseInt(request.getParameter("customerGroup"));

            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            int newAddressId = enterpriseDAO.insertAddress(conn, streetAddress, wardId, districtId, provinceId);
            int newEnterpriseId = enterpriseDAO.insertEnterprise(conn, customerName, businessEmail, hotline, customerGroupId, newAddressId, taxCode, bankNumber, avatarDbPath);
            if (fullName != null && !fullName.trim().isEmpty()) {
                enterpriseDAO.insertEnterpriseContact(conn, newEnterpriseId, fullName, position, phone, email);
            }
            enterpriseDAO.insertAssignment(conn, newEnterpriseId, employeeId, "account_manager");

            conn.commit();
            request.setAttribute("successMessage", "Đã thêm thành công khách hàng '" + customerName + "'!");
            request.setAttribute("redirectUrl", request.getContextPath() + "/customer/list");
            showCreateForm(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            request.setAttribute("errorMessage", "Tạo khách hàng thất bại: " + e.getMessage());
            showCreateForm(request, response);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // File: src/main/java/vn/edu/fpt/controller/CustomerController.java
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String idStr = request.getParameter("id");

        // Nếu không có, thử lấy từ 'enterpriseId' (dùng khi form POST bị lỗi và forward lại đây)
        if (idStr == null || idStr.isEmpty()) {
            idStr = request.getParameter("enterpriseId");
        }

        if (idStr == null || idStr.isEmpty()) { // Sửa ở đây để kiểm tra sau khi đã thử cả hai tham số
            response.sendRedirect(request.getContextPath() + "/customer/list");
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO(); // Chuyển khai báo DAO vào đây cho an toàn
            Enterprise customer = enterpriseDAO.getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng.");
                listCustomers(request, response);
                return;
            }

            request.setAttribute("customer", customer);
            request.setAttribute("allProvinces", enterpriseDAO.getAllProvinces());
            request.setAttribute("districtsForCustomer", enterpriseDAO.getDistrictsByProvinceId(customer.getProvinceId()));
            request.setAttribute("wardsForCustomer", enterpriseDAO.getWardsByDistrictId(customer.getDistrictId()));
            request.setAttribute("allCustomerTypes", enterpriseDAO.getAllCustomerTypes());
            request.setAttribute("allEmployees", new UserDAO().getAllEmployees());

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu để chỉnh sửa: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/editCustomerDetail.jsp").forward(request, response);
    }

    // File: src/main/java/vn/edu/fpt/controller/CustomerController.java
    private void handleEditCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        int enterpriseId = Integer.parseInt(request.getParameter("enterpriseId"));
        String customerName = request.getParameter("customerName");

        try {
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            //--- KHỐI VALIDATE DỮ LIỆU ĐẦU VÀO (ĐẦY ĐỦ) ---
            // 1. Kiểm tra các trường văn bản và sự tồn tại
            if (customerName == null || customerName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp không được để trống.");
                showEditForm(request, response);
                return;
            }
            if (enterpriseDAO.isNameExists(customerName, enterpriseId)) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp '" + customerName + "' đã được sử dụng.");
                showEditForm(request, response);
                return;
            }
            String hotline = request.getParameter("hotline");
            if (hotline == null || hotline.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng không để trống Hotline.");
                showEditForm(request, response);
                return;
            }
            // ... Thêm các kiểm tra tương tự cho businessEmail, streetAddress ...

            // 2. Kiểm tra định dạng
            if (!isFormatValidVietnamesePhoneNumber(hotline)) {
                request.setAttribute("errorMessage", "Hotline không đúng định dạng của Việt Nam.");
                showEditForm(request, response);
                return;
            }
            String taxCode = request.getParameter("taxCode");
            if (taxCode != null && !taxCode.trim().isEmpty() && !taxCode.equalsIgnoreCase("N/A") && !isFormatValidVietnameseTaxCode(taxCode)) {
                request.setAttribute("errorMessage", "Mã số thuế không đúng định dạng.");
                showEditForm(request, response);
                return;
            }
            String phone = request.getParameter("phone");
            if (phone != null && !phone.trim().isEmpty() && !phone.equalsIgnoreCase("N/A") && !isFormatValidVietnamesePhoneNumber(phone)) {
                request.setAttribute("errorMessage", "Số điện thoại người liên hệ không đúng định dạng.");
                showEditForm(request, response);
                return;
            }

            //--- KẾT THÚC VALIDATE ---
            //--- XỬ LÝ UPLOAD AVATAR MỚI (NẾU CÓ) ---
            String avatarDbPath = request.getParameter("existingAvatarUrl");
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

            //--- LẤY DỮ LIỆU VÀ CẬP NHẬT VÀO DATABASE ---
            int addressId = Integer.parseInt(request.getParameter("addressId"));
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));
            String streetAddress = request.getParameter("streetAddress");

            Enterprise enterpriseToUpdate = new Enterprise();
            enterpriseToUpdate.setId(enterpriseId);
            enterpriseToUpdate.setName(customerName);
            enterpriseToUpdate.setBusinessEmail(request.getParameter("businessEmail"));
            enterpriseToUpdate.setHotline(hotline);
            enterpriseToUpdate.setTaxCode(taxCode);
            enterpriseToUpdate.setBankNumber(request.getParameter("bankNumber"));
            enterpriseToUpdate.setCustomerTypeId(Integer.parseInt(request.getParameter("customerGroup")));
            enterpriseToUpdate.setAvatarUrl(avatarDbPath);

            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String email = request.getParameter("email");

            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            enterpriseDAO.updateEnterprise(conn, enterpriseToUpdate);
            enterpriseDAO.updateAddress(conn, addressId, streetAddress, wardId, districtId, provinceId);
            if (fullName != null && !fullName.trim().isEmpty()) {
                if (enterpriseDAO.primaryContactExists(conn, enterpriseId)) {
                    enterpriseDAO.updatePrimaryContact(conn, enterpriseId, fullName, position, phone, email);
                } else {
                    enterpriseDAO.insertEnterpriseContact(conn, enterpriseId, fullName, position, phone, email);
                }
            }
            enterpriseDAO.updateMainAssignment(conn, enterpriseId, employeeId);

            conn.commit();

            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Đã cập nhật thông tin khách hàng '" + customerName + "' thành công!");
            response.sendRedirect(request.getContextPath() + "/customer/view?id=" + enterpriseId);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            request.setAttribute("errorMessage", "Lưu thay đổi thất bại: " + e.getMessage());
            showEditForm(request, response);
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String customerIdStr = request.getParameter("customerId");

        if (customerIdStr == null || customerIdStr.isEmpty()) {
            session.setAttribute("errorMessage", "Không có ID khách hàng để xóa.");
        } else {
            try {
                int customerId = Integer.parseInt(customerIdStr);
                boolean success = new EnterpriseDAO().softDeleteEnterprise(customerId);
                if (success) {
                    session.setAttribute("successMessage", "Đã xóa khách hàng thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không tìm thấy khách hàng để xóa hoặc đã có lỗi xảy ra.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("errorMessage", "ID khách hàng không hợp lệ.");
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMessage", "Lỗi khi xóa khách hàng: " + e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/customer/list");
    }

    // ===================================================================================
    // CÁC PHƯƠNG THỨC HỖ TRỢ (AJAX)
    // ===================================================================================
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

    private void getSearchSuggestions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String query = request.getParameter("query");
        List<String> suggestions = Collections.emptyList();
        if (query != null && query.trim().length() >= 2) {
            try {
                suggestions = new EnterpriseDAO().getCustomerNameSuggestions(query);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(suggestions));
            out.flush();
        }
    }

    private boolean isAjaxRequest(String action) {
        if (action == null) {
            return false;
        }
        return action.equals("/getDistricts") || action.equals("/getWards") || action.equals("/searchSuggestions");
    }

    // File: src/main/java/vn/edu/fpt/controller/CustomerController.java
// ... (bên trong class CustomerController)
    /**
     * Kiểm tra định dạng Mã số thuế (MST) của Việt Nam. MST hợp lệ là một chuỗi
     * 10 chữ số (cho doanh nghiệp) hoặc 13 chữ số theo định dạng XXXXXXXXXX-XXX
     * (cho chi nhánh/đơn vị phụ thuộc).
     *
     * @param taxCode Chuỗi mã số thuế cần kiểm tra.
     * @return {@code true} nếu taxCode có định dạng hợp lệ, ngược lại
     * {@code false}.
     */
    private boolean isFormatValidVietnameseTaxCode(String taxCode) {
        if (taxCode == null || taxCode.trim().isEmpty()) {
            return false; // Không kiểm tra chuỗi rỗng ở đây
        }
        // Regex: 10 chữ số HOẶC (10 chữ số, dấu gạch ngang, 3 chữ số)
        return taxCode.matches("^(\\d{10}|\\d{10}-\\d{3})$");
    }

    /**
     * Kiểm tra định dạng số điện thoại/hotline của Việt Nam. Các định dạng hợp
     * lệ bao gồm:
     * <ul>
     * <li>Số di động: 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09.</li>
     * <li>Số máy bàn: 10 chữ số, bắt đầu bằng 02.</li>
     * <li>Hotline tổng đài: Bắt đầu bằng 1800 hoặc 1900, theo sau là 4-6 chữ
     * số.</li>
     * </ul>
     *
     * @param phoneNumber Chuỗi số điện thoại cần kiểm tra.
     * @return {@code true} nếu phoneNumber có định dạng hợp lệ, ngược lại
     * {@code false}.
     */
    private boolean isFormatValidVietnamesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false; // Không kiểm tra chuỗi rỗng ở đây
        }
        // Regex: (Số di động 10 số) HOẶC (Số máy bàn 10 số) HOẶC (Hotline 1800/1900)
        return phoneNumber.matches("^(0(2\\d{8}|[35789]\\d{8})|(1800|1900)\\d{4,6})$");
    }

    /**
     * Kiểm tra xem người dùng hiện tại có quyền Ghi (Thêm, Sửa, Xóa) hay không.
     *
     * @param request HttpServletRequest để lấy session
     * @return true nếu là Admin hoặc Kinh doanh, ngược lại là false
     */
    // Thay thế toàn bộ phương thức cũ bằng phương thức này
    private boolean hasWritePermission(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            System.out.println("DEBUG PERMISSION: Khong tim thay session hoac user object.");
            return false;
        }
        User user = (User) session.getAttribute("user");
        String roleName = user.getRoleName();

        // Thêm .trim() để code an toàn hơn, tự động xóa khoảng trắng thừa
        if (roleName != null) {
            roleName = roleName.trim();
        }

        return "Admin".equalsIgnoreCase(roleName) || "Kinh doanh".equalsIgnoreCase(roleName);
    }
}
