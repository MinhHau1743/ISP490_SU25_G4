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
                request.setAttribute("recentContracts", new ContractDAO().getRecentContractsByEnterpriseId(enterpriseId, 5));
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

    private void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection conn = null;
        String customerName = request.getParameter("customerName");

        try {

            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            if (enterpriseDAO.isNameExists(customerName, null)) {
                request.setAttribute("errorMessage", "Khách hàng với tên '" + customerName + "' đã tồn tại. Vui lòng chọn một tên khác.");
                // Chuyển tiếp lại form tạo mới để hiển thị lỗi
                showCreateForm(request, response);
                return; // Dừng xử lý
            }

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

            // === PHẦN 2: LẤY VÀ VALIDATE CÁC THAM SỐ TỪ FORM ===
            // Lấy các tham số mới bắt buộc
            String hotline = request.getParameter("hotline");
            String businessEmail = request.getParameter("businessEmail");

            if (hotline == null || hotline.trim().isEmpty()) {
                // ĐÃ SỬA: Cập nhật thông báo lỗi
                request.setAttribute("errorMessage", "Vui lòng nhập Hotline của doanh nghiệp.");
                doGet(request, response);
                return;
            }
            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Email của doanh nghiệp.");
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

            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String taxCode = request.getParameter("taxCode");
            String bankNumber = request.getParameter("bankNumber");

            int provinceId = Integer.parseInt(request.getParameter("province"));
            int districtId = Integer.parseInt(request.getParameter("district"));
            int wardId = Integer.parseInt(request.getParameter("ward"));

            String employeeIdStr = request.getParameter("employeeId");

            if (employeeIdStr == null || employeeIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                doGet(request, response);
                return;
            }
            int employeeId = Integer.parseInt(employeeIdStr);

            String customerGroupIdStr = request.getParameter("customerGroup");
            if (customerGroupIdStr == null || customerGroupIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhóm khách hàng.");
                doGet(request, response);
                return;
            }
            int customerGroupId = Integer.parseInt(customerGroupIdStr);

            // Start a database transaction
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            // 1. Insert address and get the new ID
            int newAddressId = enterpriseDAO.insertAddress(conn, streetAddress, wardId, districtId, provinceId);
            // 2. Insert enterprise and get the new ID
            int newEnterpriseId = enterpriseDAO.insertEnterprise(conn, customerName, businessEmail, hotline, customerGroupId, newAddressId, taxCode, bankNumber, avatarDbPath);
            // Chỉ thêm người liên hệ nếu có thông tin được nhập
            if (fullName != null && !fullName.trim().isEmpty()) {
                enterpriseDAO.insertEnterpriseContact(conn, newEnterpriseId, fullName, position, phone, email);
            }
            // 4. Assign the responsible employee
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

    private void handleEditCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int enterpriseId = Integer.parseInt(request.getParameter("enterpriseId"));
        String customerName = request.getParameter("customerName");
        Connection conn = null;

        try {
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            if (enterpriseDAO.isNameExists(customerName, enterpriseId)) {
                request.setAttribute("errorMessage", "Tên khách hàng '" + customerName + "' đã được sử dụng bởi một khách hàng khác. Vui lòng chọn một tên khác.");
                // Tải lại form chỉnh sửa với thông báo lỗi
                showEditForm(request, response);
                return; // Dừng xử lý
            }

            int addressId = Integer.parseInt(request.getParameter("addressId"));
            if (customerName == null || customerName.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Tên doanh nghiệp không được để trống.");
                showEditForm(request, response);
                return;
            }

            String hotline = request.getParameter("hotline");
            if (hotline == null || hotline.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Hotline của doanh nghiệp.");
                showEditForm(request, response);
                return;
            }

            String businessEmail = request.getParameter("businessEmail");
            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng nhập Email của doanh nghiệp.");
                showEditForm(request, response);
                return;
            }

            String employeeIdStr = request.getParameter("employeeId");
            if (employeeIdStr == null || employeeIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhân viên phụ trách.");
                showEditForm(request, response);
                return;
            }

            String customerGroupIdStr = request.getParameter("customerGroup");
            if (customerGroupIdStr == null || customerGroupIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Vui lòng chọn nhóm khách hàng.");
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

            int employeeId = Integer.parseInt(employeeIdStr);
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);

            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            String avatarDbPath = request.getParameter("existingAvatarUrl");
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0 && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().isEmpty()) {
                // Logic upload file...
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

            Enterprise enterpriseToUpdate = new Enterprise();
            enterpriseToUpdate.setId(enterpriseId);
            enterpriseToUpdate.setName(request.getParameter("customerName"));
            enterpriseToUpdate.setBusinessEmail(request.getParameter("businessEmail"));
            // ĐÃ SỬA: Gọi đúng phương thức setHotline() thay vì setFax()
            enterpriseToUpdate.setHotline(request.getParameter("hotline"));
            enterpriseToUpdate.setTaxCode(request.getParameter("taxCode"));
            enterpriseToUpdate.setBankNumber(request.getParameter("bankNumber"));
            enterpriseToUpdate.setCustomerTypeId(Integer.parseInt(request.getParameter("customerGroup")));
            enterpriseToUpdate.setAvatarUrl(avatarDbPath);

            // Dòng này được gọi ở dưới rồi, gọi 2 lần sẽ thừa
            // new EnterpriseDAO().updateEnterprise(conn, enterpriseToUpdate);
            if (fullName != null && !fullName.trim().isEmpty()) {
                if (new EnterpriseDAO().primaryContactExists(conn, enterpriseId)) {
                    new EnterpriseDAO().updatePrimaryContact(conn, enterpriseId, fullName, position, phone, email);
                } else {
                    new EnterpriseDAO().insertEnterpriseContact(conn, enterpriseId, fullName, position, phone, email);
                }
            }

            new EnterpriseDAO().updateEnterprise(conn, enterpriseToUpdate);
            new EnterpriseDAO().updateAddress(conn, addressId, streetAddress, wardId, districtId, provinceId);
            // Dòng này đã được xử lý trong logic if/else ở trên
            // new EnterpriseDAO().updatePrimaryContact(conn, enterpriseId, fullName, position, phone, email); 
            new EnterpriseDAO().updateMainAssignment(conn, enterpriseId, employeeId);

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

        // IN RA ĐỂ KIỂM TRA GIÁ TRỊ THỰC TẾ
        System.out.println("-------------------------------------------");
        System.out.println("DEBUG PERMISSION: Kiem tra quyen...");
        System.out.println("Role name lay tu session: '" + roleName + "'"); // Dùng '' để dễ thấy khoảng trắng
        System.out.println("So sanh voi 'Admin': " + "Admin".equalsIgnoreCase(roleName));
        System.out.println("So sanh voi 'Kinh doanh': " + "Kinh doanh".equalsIgnoreCase(roleName));
        System.out.println("-------------------------------------------");

        // Thêm .trim() để code an toàn hơn, tự động xóa khoảng trắng thừa
        if (roleName != null) {
            roleName = roleName.trim();
        }

        return "Admin".equalsIgnoreCase(roleName) || "Kinh doanh".equalsIgnoreCase(roleName);
    }
}
