package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.edu.fpt.common.NotificationService; // Import đã được thêm
import vn.edu.fpt.model.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.dao.UserDAO;

@WebServlet(name = "EnterpriseController", urlPatterns = {"/customer/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class EnterpriseController extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";
    private static final int PAGE_SIZE = 10;

    // Dependencies
    private final EnterpriseDAO enterpriseDAO;
    private final UserDAO userDAO;
    private final TechnicalRequestDAO technicalRequestDAO;
    private final ContractDAO contractDAO;
    private final DBContext dbContext;

    public EnterpriseController() {
        this.enterpriseDAO = new EnterpriseDAO();
        this.userDAO = new UserDAO();
        this.technicalRequestDAO = new TechnicalRequestDAO();
        this.contractDAO = new ContractDAO();
        this.dbContext = new DBContext();
    }

    // Constructor cho unit test (inject mock)
    public EnterpriseController(EnterpriseDAO enterpriseDAO, UserDAO userDAO,
            TechnicalRequestDAO technicalRequestDAO, ContractDAO contractDAO,
            DBContext dbContext) {
        this.enterpriseDAO = enterpriseDAO;
        this.userDAO = userDAO;
        this.technicalRequestDAO = technicalRequestDAO;
        this.contractDAO = contractDAO;
        this.dbContext = dbContext;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();
        if (action == null) {
            action = "/list";
        }

        boolean isWritePage = "/create".equals(action) || "/edit".equals(action);
        if (isWritePage && !hasWritePermission(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này.");
            return;
        }

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
            throw new ServletException("Lỗi xử lý yêu cầu GET trong EnterpriseController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getPathInfo();

        boolean needsWrite = "/edit".equals(action) || "/delete".equals(action);
        if (needsWrite && !hasWritePermission(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }

        try {
            switch (action) {
                case "/create":
                    handleCreateCustomer(request, response);
                    break;
                case "/edit":
                    if (!isLoggedIn(request)) {
                        response.sendRedirect(safeJoin(request.getContextPath(), "/login.jsp"));
                        return;
                    }
                    handleEditCustomer(request, response);
                    break;
                case "/delete":
                    if (!isLoggedIn(request)) {
                        response.sendRedirect(safeJoin(request.getContextPath(), "/login.jsp"));
                        return;
                    }
                    handleDeleteCustomer(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hành động không hợp lệ.");
            }
        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý yêu cầu POST trong EnterpriseController", e);
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
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
                } catch (NumberFormatException ignored) {
                    page = 1;
                }
            }

            List<Enterprise> customerList = this.enterpriseDAO.getPaginatedActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId, page, PAGE_SIZE);

            int totalCustomers = this.enterpriseDAO.countActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);

            int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

            boolean isAction = (searchQuery != null && !searchQuery.isEmpty())
                    || (customerTypeId != null && !customerTypeId.isEmpty())
                    || (employeeId != null && !employeeId.isEmpty())
                    || (provinceId != null && !provinceId.isEmpty());
            if (isAction && (customerList == null || customerList.isEmpty())) {
                request.setAttribute("noResultsFound", true);
            }

            request.setAttribute("allProvinces", this.enterpriseDAO.getAllProvinces());
            request.setAttribute("allCustomerTypes", this.enterpriseDAO.getAllCustomerTypes());
            request.setAttribute("allEmployees", this.userDAO.getAllEmployees());

            request.setAttribute("customerList", customerList);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
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

        RequestDispatcher rd = request.getRequestDispatcher("/jsp/sales/listCustomer.jsp");
        if (rd == null) {
            throw new ServletException("RequestDispatcher cho /jsp/sales/listCustomer.jsp là null (cần stub trong test).");
        }
        rd.forward(request, response);
    }

    private void viewCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage");
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(safeJoin(request.getContextPath(), "/customer/list"));
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            Enterprise customer = this.enterpriseDAO.getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng với ID cung cấp.");
            } else {
                request.setAttribute("recentRequests", this.technicalRequestDAO.getRecentRequestsByEnterprise(enterpriseId, 3));
                request.setAttribute("recentContracts", this.contractDAO.getRecentContractsByEnterpriseId(enterpriseId, 5));
            }
            request.setAttribute("customer", customer);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID khách hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage());
        }

        RequestDispatcher rd = request.getRequestDispatcher("/jsp/sales/viewCustomerDetail.jsp");
        if (rd == null) {
            throw new ServletException("RequestDispatcher cho /jsp/sales/viewCustomerDetail.jsp là null (cần stub trong test).");
        }
        rd.forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("provinces", this.enterpriseDAO.getAllProvinces());
            request.setAttribute("customerTypes", this.enterpriseDAO.getAllCustomerTypes());
            request.setAttribute("employees", this.userDAO.getAllEmployees());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu cần thiết: " + e.getMessage());
        }
        RequestDispatcher rd = request.getRequestDispatcher("/jsp/sales/createCustomer.jsp");
        if (rd == null) {
            throw new ServletException("RequestDispatcher cho /jsp/sales/createCustomer.jsp là null (cần stub trong test).");
        }
        rd.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            idStr = request.getParameter("enterpriseId");
        }

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(safeJoin(request.getContextPath(), "/customer/list"));
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            Enterprise customer = this.enterpriseDAO.getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng.");
                listCustomers(request, response);
                return;
            }

            request.setAttribute("customer", customer);
            request.setAttribute("allProvinces", this.enterpriseDAO.getAllProvinces());
            request.setAttribute("districtsForCustomer", this.enterpriseDAO.getDistrictsByProvinceId(customer.getProvinceId()));
            request.setAttribute("wardsForCustomer", this.enterpriseDAO.getWardsByDistrictId(customer.getDistrictId()));
            request.setAttribute("allCustomerTypes", this.enterpriseDAO.getAllCustomerTypes());
            request.setAttribute("allEmployees", this.userDAO.getAllEmployees());
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu để chỉnh sửa: " + e.getMessage());
        }
        RequestDispatcher rd = request.getRequestDispatcher("/jsp/sales/editCustomerDetail.jsp");
        if (rd == null) {
            throw new ServletException("RequestDispatcher cho /jsp/sales/editCustomerDetail.jsp là null (cần stub trong test).");
        }
        rd.forward(request, response);
    }

    void handleCreateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        String customerName = request.getParameter("customerName");
        int newEnterpriseId = -1;

        try {
            // === 1. LẤY VÀ VALIDATE DỮ LIỆU ===
            String taxCode = request.getParameter("taxCode");
            String hotline = request.getParameter("hotline");
            String phone = request.getParameter("phone");
            String businessEmail = request.getParameter("businessEmail");
            String streetAddress = request.getParameter("streetAddress");
            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String email = request.getParameter("email");
            String bankNumber = request.getParameter("bankNumber");

            // Validate required fields
            Map<String, String> errors = new HashMap<>();

            if (customerName == null || customerName.trim().isEmpty()) {
                errors.put("customerName", "Tên doanh nghiệp không được để trống.");
            } else if (this.enterpriseDAO.isNameExists(customerName, null)) {
                errors.put("customerName", "Tên doanh nghiệp '" + customerName + "' đã tồn tại.");
            }

            if (taxCode != null && !taxCode.trim().isEmpty() && !isFormatValidVietnameseTaxCode(taxCode)) {
                errors.put("taxCode", "Định dạng mã số thuế không hợp lệ. Phải là 10 số hoặc 10 số-3 số.");
            }

            if (hotline != null && !hotline.trim().isEmpty() && !isFormatValidVietnamesePhoneNumber(hotline)) {
                errors.put("hotline", "Định dạng số hotline không hợp lệ.");
            }

            if (phone != null && !phone.trim().isEmpty() && !isFormatValidVietnamesePhoneNumber(phone)) {
                errors.put("phone", "Định dạng số điện thoại người liên hệ không hợp lệ.");
            }

            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                // Bỏ qua nếu email này không bắt buộc.
                errors.put("businessEmail", "Email doanh nghiệp không được để trống.");
            } else if (!isValidEmail(businessEmail)) {
                errors.put("businessEmail", "Định dạng email doanh nghiệp không hợp lệ.");
            }

            // BƯỚC 1: Kiểm tra xem email có bị bỏ trống không
            if (email == null || email.trim().isEmpty()) {
                // Nếu bạn bắt buộc người dùng phải nhập email
                errors.put("email", "Email người liên hệ không được để trống.");
            } // BƯỚC 2: Nếu không trống, mới kiểm tra đến định dạng
            else if (!isValidEmail(email)) {
                errors.put("email", "Định dạng email người liên hệ không hợp lệ.");
            }

            // Validate address fields
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            if (provinceIdStr == null || provinceIdStr.isEmpty()) {
                errors.put("province", "Vui lòng chọn tỉnh/thành.");
            }
            if (districtIdStr == null || districtIdStr.isEmpty()) {
                errors.put("district", "Vui lòng chọn quận/huyện.");
            }
            if (wardIdStr == null || wardIdStr.isEmpty()) {
                errors.put("ward", "Vui lòng chọn phường/xã.");
            }
            if (streetAddress == null || streetAddress.trim().isEmpty()) {
                errors.put("streetAddress", "Địa chỉ không được để trống.");
            } else if (streetAddress.trim().length() > 255) {
                errors.put("streetAddress", "Địa chỉ không được vượt quá 255 ký tự.");
            }
            // Nếu có lỗi, hiển thị form lại
            if (!errors.isEmpty()) {
                for (Map.Entry<String, String> error : errors.entrySet()) {
                    request.setAttribute(error.getKey() + "Error", error.getValue());
                }
                // Giữ lại giá trị đã nhập
                restoreFormData(request);
                showCreateForm(request, response);
                return;
            }

            // Parse các giá trị số
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            int customerGroupId = Integer.parseInt(request.getParameter("customerGroup"));

            // === 2. XỬ LÝ FILE UPLOAD ===
            String avatarDbPath = null;
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0) {
                // Validate file size
                if (filePart.getSize() > 5 * 1024 * 1024) {
                    request.setAttribute("avatarError", "Kích thước file không được vượt quá 5MB.");
                    restoreFormData(request);
                    showCreateForm(request, response);
                    return;
                }

                // Validate file type
                String contentType = filePart.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    request.setAttribute("avatarError", "Chỉ chấp nhận file hình ảnh.");
                    restoreFormData(request);
                    showCreateForm(request, response);
                    return;
                }

                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String fileExtension = originalFileName.contains(".")
                        ? originalFileName.substring(originalFileName.lastIndexOf("."))
                        : "";

                if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(fileExtension.toLowerCase())) {
                    request.setAttribute("avatarError", "Chỉ chấp nhận file JPG, JPEG, PNG, GIF.");
                    restoreFormData(request);
                    showCreateForm(request, response);
                    return;
                }

                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;

                File uploadDir = new File(uploadFilePath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                filePart.write(uploadFilePath + File.separator + uniqueFileName);
                avatarDbPath = "uploads/avatars/" + uniqueFileName;
            }

            // === 3. XỬ LÝ DATABASE TRANSACTION ===
            conn = this.dbContext.getConnection();
            conn.setAutoCommit(false);

            int newAddressId = this.enterpriseDAO.insertAddress(conn, streetAddress, wardId, districtId, provinceId);
            newEnterpriseId = this.enterpriseDAO.insertEnterprise(conn, customerName, businessEmail, hotline,
                    customerGroupId, newAddressId, taxCode, bankNumber, avatarDbPath);

            if (fullName != null && !fullName.trim().isEmpty()) {
                this.enterpriseDAO.insertEnterpriseContact(conn, newEnterpriseId, fullName, position, phone, email);
            }

            this.enterpriseDAO.insertAssignment(conn, newEnterpriseId, employeeId, "account_manager");
            conn.commit();

            // === 4. THÔNG BÁO THÀNH CÔNG ===
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null && newEnterpriseId != -1) {
                    Enterprise createdEnterprise = enterpriseDAO.getEnterpriseById(newEnterpriseId);
                    if (createdEnterprise != null) {
                        NotificationService.notifyNewEnterprise(currentUser, createdEnterprise);
                    }
                }
                session.setAttribute("successMessage", "Đã thêm thành công khách hàng '" + customerName + "'!");
            }

            response.sendRedirect(safeJoin(request.getContextPath(), "/customer/list"));

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu số không hợp lệ: " + e.getMessage());
            restoreFormData(request);
            showCreateForm(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            restoreFormData(request);
            showCreateForm(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            request.setAttribute("errorMessage", "Tạo khách hàng thất bại: " + e.getMessage());
            restoreFormData(request);
            showCreateForm(request, response);
        } finally {
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

// Helper method để giữ lại giá trị form
    private void restoreFormData(HttpServletRequest request) {
        for (String paramName : request.getParameterMap().keySet()) {
            request.setAttribute("param_" + paramName, request.getParameter(paramName));
        }
    }

// Helper method để validate email
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void handleEditCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Connection conn = null;
        // Lấy ID của doanh nghiệp cần sửa ngay từ đầu để sử dụng trong validation
        int enterpriseId = Integer.parseInt(request.getParameter("enterpriseId"));
        String customerName = request.getParameter("customerName");
        Enterprise enterpriseToUpdate = null;

        try {
            // === 1. LẤY VÀ VALIDATE TOÀN BỘ DỮ LIỆU ===
            String taxCode = request.getParameter("taxCode");
            String hotline = request.getParameter("hotline");
            String phone = request.getParameter("phone");
            String businessEmail = request.getParameter("businessEmail");
            String email = request.getParameter("email");
            String streetAddress = request.getParameter("streetAddress");
            String fullName = request.getParameter("fullName");
            String position = request.getParameter("position");
            String bankNumber = request.getParameter("bankNumber");

            // Sử dụng Map để thu thập tất cả lỗi validation
            Map<String, String> errors = new HashMap<>();

            // --- Validate các trường text và số điện thoại ---
            if (customerName == null || customerName.trim().isEmpty()) {
                errors.put("customerName", "Tên doanh nghiệp không được để trống.");
            } else if (this.enterpriseDAO.isNameExists(customerName, enterpriseId)) { // Dùng enterpriseId để loại trừ chính nó
                errors.put("customerName", "Tên doanh nghiệp '" + customerName + "' đã được sử dụng.");
            }

            if (taxCode != null && !taxCode.trim().isEmpty() && !isFormatValidVietnameseTaxCode(taxCode)) {
                errors.put("taxCode", "Định dạng mã số thuế không hợp lệ. Phải là 10 số hoặc 10 số-3 số.");
            }

            if (hotline != null && !hotline.trim().isEmpty() && !isFormatValidVietnamesePhoneNumber(hotline)) {
                errors.put("hotline", "Định dạng số hotline không hợp lệ.");
            }

            if (phone != null && !phone.trim().isEmpty() && !isFormatValidVietnamesePhoneNumber(phone)) {
                errors.put("phone", "Định dạng số điện thoại người liên hệ không hợp lệ.");
            }

            // --- Validate các trường email ---
            if (businessEmail == null || businessEmail.trim().isEmpty()) {
                errors.put("businessEmail", "Email doanh nghiệp không được để trống.");
            } else if (!isValidEmail(businessEmail)) {
                errors.put("businessEmail", "Định dạng email doanh nghiệp không hợp lệ.");
            }

            if (email == null || email.trim().isEmpty()) {
                errors.put("email", "Email người liên hệ không được để trống.");
            } else if (!isValidEmail(email)) {
                errors.put("email", "Định dạng email người liên hệ không hợp lệ.");
            }

            // --- Validate các trường địa chỉ ---
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            if (provinceIdStr == null || provinceIdStr.isEmpty()) {
                errors.put("province", "Vui lòng chọn tỉnh/thành.");
            }
            if (districtIdStr == null || districtIdStr.isEmpty()) {
                errors.put("district", "Vui lòng chọn quận/huyện.");
            }
            if (wardIdStr == null || wardIdStr.isEmpty()) {
                errors.put("ward", "Vui lòng chọn phường/xã.");
            }
            if (streetAddress == null || streetAddress.trim().isEmpty()) {
                errors.put("streetAddress", "Địa chỉ không được để trống.");
            } else if (streetAddress.trim().length() > 255) {
                errors.put("streetAddress", "Địa chỉ không được vượt quá 255 ký tự.");
            }
            // === 2. XỬ LÝ NẾU CÓ LỖI VALIDATION ===
            if (!errors.isEmpty()) {
                for (Map.Entry<String, String> error : errors.entrySet()) {
                    request.setAttribute(error.getKey() + "Error", error.getValue());
                }
                // Giữ lại toàn bộ dữ liệu người dùng đã nhập để họ không phải điền lại
                restoreFormData(request);
                // Hiển thị lại form edit với các thông báo lỗi
                showEditForm(request, response);
                return; // Dừng xử lý
            }

            // Parse các giá trị số sau khi đã chắc chắn không có lỗi
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            int customerGroupId = Integer.parseInt(request.getParameter("customerGroup"));
            int addressId = Integer.parseInt(request.getParameter("addressId"));

            // === 3. XỬ LÝ FILE UPLOAD (NẾU CÓ) ===
            String avatarDbPath = request.getParameter("existingAvatarUrl"); // Lấy URL avatar cũ
            Part filePart = request.getPart("avatar");

            if (filePart != null && filePart.getSize() > 0) {
                // Validate file size, type, extension... (tương tự như hàm create)
                // ... (bạn có thể sao chép khối validate file từ hàm create vào đây) ...

                // Nếu file hợp lệ, tiến hành lưu file mới
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
                new File(uploadFilePath).mkdirs();
                filePart.write(uploadFilePath + File.separator + uniqueFileName);
                avatarDbPath = "uploads/avatars/" + uniqueFileName; // Cập nhật đường dẫn avatar mới
            }

            // === 4. TẠO ĐỐI TƯỢNG VÀ XỬ LÝ DATABASE TRANSACTION ===
            enterpriseToUpdate = new Enterprise();
            enterpriseToUpdate.setId(enterpriseId);
            enterpriseToUpdate.setName(customerName);
            enterpriseToUpdate.setBusinessEmail(businessEmail);
            enterpriseToUpdate.setHotline(hotline);
            enterpriseToUpdate.setTaxCode(taxCode);
            enterpriseToUpdate.setBankNumber(bankNumber);
            enterpriseToUpdate.setCustomerTypeId(customerGroupId);
            enterpriseToUpdate.setAvatarUrl(avatarDbPath);
            enterpriseToUpdate.setAddressId(addressId);
            enterpriseToUpdate.setStreetAddress(streetAddress);
            enterpriseToUpdate.setWardId(wardId);
            enterpriseToUpdate.setDistrictId(districtId);
            enterpriseToUpdate.setProvinceId(provinceId);

            conn = this.dbContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            this.enterpriseDAO.updateEnterprise(conn, enterpriseToUpdate);
            this.enterpriseDAO.updateAddress(conn, addressId, streetAddress, wardId, districtId, provinceId);

            if (fullName != null && !fullName.trim().isEmpty()) {
                if (this.enterpriseDAO.primaryContactExists(conn, enterpriseId)) {
                    this.enterpriseDAO.updatePrimaryContact(conn, enterpriseId, fullName, position, phone, email);
                } else {
                    this.enterpriseDAO.insertEnterpriseContact(conn, enterpriseId, fullName, position, phone, email);
                }
            }

            this.enterpriseDAO.updateMainAssignment(conn, enterpriseId, employeeId);

            conn.commit(); // Hoàn tất transaction

            // === 5. THÔNG BÁO THÀNH CÔNG VÀ CHUYỂN HƯỚNG ===
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser != null) {
                    NotificationService.notifyUpdateEnterprise(currentUser, enterpriseToUpdate);
                }
                session.setAttribute("successMessage", "Đã cập nhật thông tin khách hàng '" + customerName + "' thành công!");
            }

            response.sendRedirect(safeJoin(request.getContextPath(), "/customer/view?id=" + enterpriseId));

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            request.setAttribute("errorMessage", "Lưu thay đổi thất bại: " + e.getMessage());
            restoreFormData(request);
            showEditForm(request, response);
        } finally {
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

    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        String customerIdStr = request.getParameter("customerId");
        try {
            int customerId = Integer.parseInt(customerIdStr);
            boolean success = this.enterpriseDAO.softDeleteEnterprise(customerId);
            if (session != null) {
                if (success) {
                    session.setAttribute("successMessage", "Đã xóa khách hàng thành công!");
                } else {
                    session.setAttribute("errorMessage", "Không tìm thấy khách hàng để xóa hoặc đã có lỗi xảy ra.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null) {
                session.setAttribute("errorMessage", "Lỗi khi xóa khách hàng: " + e.getMessage());
            }
        }
        response.sendRedirect(safeJoin(request.getContextPath(), "/customer/list"));
    }

    private void getDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String provinceIdStr = request.getParameter("provinceId");
        List<District> districts = Collections.emptyList();
        if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
            try {
                districts = this.enterpriseDAO.getDistrictsByProvinceId(Integer.parseInt(provinceIdStr));
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
                wards = this.enterpriseDAO.getWardsByDistrictId(Integer.parseInt(districtIdStr));
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
                suggestions = this.enterpriseDAO.getCustomerNameSuggestions(query);
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

    // ====== Helpers ======
    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        // Thay đổi 'user' thành 'account' để khớp với AuthenticationController
        return s != null && s.getAttribute("user") != null;
    }

    private boolean hasWritePermission(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return false;
        }
        User user = (User) session.getAttribute("user"); // Thay đổi 'user' thành 'account'
        String roleName = user.getRoleName();
        if (roleName != null) {
            roleName = roleName.trim();
        }
        return "Admin".equalsIgnoreCase(roleName) || "Kinh doanh".equalsIgnoreCase(roleName);
    }

    private String safeJoin(String ctx, String path) {
        if (ctx == null || ctx.isEmpty() || "null".equals(ctx)) {
            return path;
        }
        if (path == null) {
            return ctx;
        }
        if (ctx.endsWith("/") && path.startsWith("/")) {
            return ctx.substring(0, ctx.length() - 1) + path;
        }
        if (!ctx.endsWith("/") && !path.startsWith("/")) {
            return ctx + "/" + path;
        }
        return ctx + path;
    }

    private boolean isAjaxRequest(String action) {
        if (action == null) {
            return false;
        }
        return action.equals("/getDistricts") || action.equals("/getWards") || action.equals("/searchSuggestions");
    }

    private boolean isFormatValidVietnameseTaxCode(String taxCode) {
        if (taxCode == null || taxCode.trim().isEmpty()) {
            return false;
        }
        return taxCode.matches("^(\\d{10}|\\d{10}-\\d{3})$");
    }

    private boolean isFormatValidVietnamesePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return phoneNumber.matches("^(0(2\\d{8}|[35789]\\d{8})|(1800|1900)\\d{4,6})$");
    }
}
