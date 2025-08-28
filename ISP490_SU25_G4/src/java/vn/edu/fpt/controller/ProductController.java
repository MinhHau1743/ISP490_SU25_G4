package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
@MultipartConfig
public class ProductController extends HttpServlet {

    private static final String UPLOAD_DIR = "D:/ISP/ISP490_SU25_G4/web/image"; // Nên cấu hình ngoài web.xml hoặc file properties

    // THAY ĐỔI 1: Khai báo productDao như một thuộc tính (field) của class
    private final ProductDAO productDao;

    // THAY ĐỔI 2: Thêm Constructor mặc định để ứng dụng chạy bình thường
    // Nó sẽ tự khởi tạo ProductDAO thật khi servlet được tạo ra bởi server
    public ProductController() {
        this.productDao = new ProductDAO();
    }

    // THAY ĐỔI 3: Thêm Constructor dùng cho Unit Test
    // Constructor này cho phép chúng ta "tiêm" một ProductDAO giả (mock) từ bên ngoài
    public ProductController(ProductDAO productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list"; // Hành động mặc định
        }

        switch (action) {
            case "view":
                viewProductDetail(request, response);
                break;
            case "create":
                showCreateForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            case "list":
            default:
                listProducts(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return;
        }

        switch (action) {
            case "processCreate":
                processCreateProduct(request, response);
                break;
            case "processEdit":
                processEditProduct(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/product?action=list");
                break;
        }
    }

    private List<String> validateProductInput(String name, String productCode, String origin, String priceRaw, ProductDAO dao, boolean isCreating) {
        // Khởi tạo một danh sách rỗng để chứa các thông báo lỗi.
        List<String> errors = new ArrayList<>();

        // 1. Kiểm tra Tên sản phẩm: không được null hoặc chỉ chứa khoảng trắng.
        if (name == null || name.trim().isEmpty()) {
            errors.add("Tên sản phẩm không được để trống!");
        }

        // 2. Kiểm tra Mã sản phẩm.
        if (productCode == null || productCode.trim().isEmpty()) {
            errors.add("Mã sản phẩm không được để trống!");
        } else if (isCreating && dao.isProductCodeExists(productCode)) {
            // Chỉ kiểm tra mã tồn tại khi đang ở chế độ "tạo mới" (isCreating = true).
            errors.add("Mã sản phẩm đã tồn tại!");
        }

        // 3. Kiểm tra Xuất xứ: không được null hoặc trống.
        if (origin == null || origin.trim().isEmpty()) {
            errors.add("Xuất xứ không được để trống!");
        }

        // 4. Kiểm tra Giá.
        if (priceRaw == null || priceRaw.trim().isEmpty()) {
            errors.add("Giá không được để trống!");
        } else {
            // Nếu giá không trống, thử chuyển đổi nó thành số.
            try {
                // Làm sạch chuỗi giá bằng cách loại bỏ các dấu phân cách ('.' và ',') trước khi chuyển đổi.
                BigDecimal price = new BigDecimal(priceRaw.replace(".", "").replace(",", ""));
                // So sánh giá với 0. Nếu nhỏ hơn 0, báo lỗi.
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Giá phải là một số không âm!");
                }
            } catch (NumberFormatException ex) {
                // Nếu việc chuyển đổi chuỗi sang số thất bại (ví dụ: người dùng nhập "abc"), báo lỗi.
                errors.add("Giá không hợp lệ! Vui lòng chỉ nhập số.");
            }
        }

        // Trả về danh sách lỗi. Nếu không có lỗi nào, danh sách này sẽ rỗng.
        return errors;
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // === XỬ LÝ PHÂN TRANG ===
        int page = 1; // Trang mặc định là 1.
        int pageSize = 10; // Số sản phẩm trên mỗi trang.
        String pageRaw = request.getParameter("page"); // Lấy tham số 'page' từ URL.

        // Nếu tham số 'page' tồn tại và không rỗng, thử chuyển nó thành số nguyên.
        if (pageRaw != null && !pageRaw.isEmpty()) {
            try {
                page = Integer.parseInt(pageRaw);
            } catch (NumberFormatException e) {
                // Nếu chuyển đổi thất bại (người dùng nhập chữ), quay về trang 1.
                page = 1;
            }
        }

        // === LẤY CÁC THAM SỐ LỌC (FILTER) ===
        String keyword = request.getParameter("keyword");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String origin = request.getParameter("origin");

        // Chuyển đổi giá từ chuỗi sang BigDecimal để truy vấn.
        BigDecimal minPrice = null, maxPrice = null;
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            // Nếu người dùng nhập giá không phải là số, thì bỏ qua, không lọc theo giá.
        }

        // Nếu người dùng chọn "Tất cả" (giá trị rỗng) cho xuất xứ, coi như không lọc.
        if (origin != null && origin.trim().isEmpty()) {
            origin = null;
        }

        // === TRUY VẤN DATABASE ===
        // Đếm tổng số sản phẩm thỏa mãn điều kiện lọc để tính tổng số trang.
        int totalProducts = this.productDao.countProductsWithFilter(keyword, minPrice, maxPrice, origin);
        // Tính tổng số trang, làm tròn lên. Ví dụ: 21 sản phẩm / 10 sp/trang = 2.1 => 3 trang.
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        // Lấy danh sách sản phẩm cho trang hiện tại.
        List<Product> listProducts = this.productDao.getProductsWithFilter(keyword, minPrice, maxPrice, origin, page, pageSize);
        // Lấy tất cả các "xuất xứ" có trong DB để hiển thị trong dropdown bộ lọc.
        List<String> origins = this.productDao.getAllOrigins();

        // === GỬI DỮ LIỆU SANG JSP ===
        // Đặt các dữ liệu đã lấy được vào request scope để JSP có thể truy cập.
        request.setAttribute("originList", origins);
        request.setAttribute("productList", listProducts);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        // Gửi lại các giá trị lọc để hiển thị lại trên form.
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("origin", origin);

        // Chuyển tiếp request đến trang JSP để render giao diện.
        request.getRequestDispatcher("jsp/technicalSupport/listProduct.jsp").forward(request, response);
    }

    private void viewProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lấy tham số 'id' từ URL.
        String idStr = request.getParameter("id");

        // Nếu không có ID, chuyển hướng đến trang lỗi 404.
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
            return; // Dừng xử lý.
        }
        try {
            // Chuyển đổi ID từ chuỗi sang số nguyên.
            int id = Integer.parseInt(idStr);
            // Gọi DAO để lấy thông tin sản phẩm từ database.
            Product product = this.productDao.getProductById(id);
            // Nếu tìm thấy sản phẩm (product không null).
            if (product != null) {
                // Đặt đối tượng product vào request scope.
                request.setAttribute("product", product);
                // Chuyển tiếp đến trang JSP hiển thị chi tiết.
                request.getRequestDispatcher("/jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
            } else {
                // Nếu không tìm thấy sản phẩm với ID tương ứng, chuyển hướng đến trang 404.
                response.sendRedirect(request.getContextPath() + "/404.jsp");
            }
        } catch (NumberFormatException e) {
            // Nếu ID không phải là một số hợp lệ, chuyển hướng đến trang 404.
            response.sendRedirect(request.getContextPath() + "/404.jsp");
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
    }

    private void processCreateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // === BƯỚC 1: XÁC THỰC & PHÂN QUYỀN ===
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");

            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            if (!isAuthorized(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thực hiện hành động này.");
                return;
            }

            // === BƯỚC 2: LẤY DỮ LIỆU TỪ FORM ===
            String name = sanitizeInput(request.getParameter("name"));
            String productCode = sanitizeInput(request.getParameter("productCode"));
            String origin = sanitizeInput(request.getParameter("origin"));
            String priceRaw = sanitizeInput(request.getParameter("price"));
            String description = sanitizeInput(request.getParameter("description"));

            // Map để chứa tất cả lỗi validation
            Map<String, String> errors = new HashMap<>();

            // === VALIDATION CÁC TRƯỜNG CƠ BẢN ===
            // Validate tên sản phẩm
            if (name == null || name.trim().isEmpty()) {
                errors.put("name", "Tên sản phẩm không được để trống.");
            } else if (name.length() > 255) {
                errors.put("name", "Tên sản phẩm không được vượt quá 255 ký tự.");
            } else if (!name.matches("^[\\p{L}\\p{N}\\s\\-\\.\\(\\)]+$")) {
                errors.put("name", "Tên sản phẩm chỉ được chứa chữ cái, số và các ký tự: - . ( )");
            }

            // Validate mã sản phẩm
            if (productCode == null || productCode.trim().isEmpty()) {
                errors.put("productCode", "Mã sản phẩm không được để trống.");
            } else if (productCode.length() < 3 || productCode.length() > 50) {
                errors.put("productCode", "Mã sản phẩm phải từ 3-50 ký tự.");
            } else if (!productCode.matches("^[A-Z0-9\\-_]+$")) {
                errors.put("productCode", "Mã sản phẩm chỉ được chứa chữ cái hoa, số, dấu gạch ngang và gạch dưới.");
            } else {
                // Kiểm tra trùng mã sản phẩm
                try {
                    if (this.productDao.isProductCodeExists(productCode)) {
                        errors.put("productCode", "Mã sản phẩm '" + productCode + "' đã tồn tại trong hệ thống.");
                    }
                } catch (Exception e) {
                    errors.put("productCode", "Lỗi kiểm tra mã sản phẩm: " + e.getMessage());
                }
            }

            // Validate xuất xứ
            if (origin == null || origin.trim().isEmpty()) {
                errors.put("origin", "Xuất xứ không được để trống.");
            } else if (origin.length() > 100) {
                errors.put("origin", "Xuất xứ không được vượt quá 100 ký tự.");
            }

            // Validate giá
            BigDecimal price = null;
            if (priceRaw == null || priceRaw.trim().isEmpty()) {
                errors.put("price", "Giá sản phẩm không được để trống.");
            } else {
                try {
                    // Làm sạch chuỗi giá (bỏ dấu phẩy, chấm phân cách nghìn)
                    String cleanPrice = priceRaw.replace(",", "").replace(".", "");
                    price = new BigDecimal(cleanPrice);

                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put("price", "Giá sản phẩm phải lớn hơn 0.");
                    } else if (price.compareTo(new BigDecimal("999999999")) > 0) {
                        errors.put("price", "Giá sản phẩm không được vượt quá 999,999,999 VNĐ.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("price", "Giá sản phẩm không hợp lệ. Vui lòng nhập số.");
                }
            }

            // Validate mô tả (không bắt buộc)
            if (description != null && description.length() > 1000) {
                errors.put("description", "Mô tả không được vượt quá 1000 ký tự.");
            }

            // === VALIDATION FILE UPLOAD ===
            Part filePart = null;
            try {
                filePart = request.getPart("image");
            } catch (IllegalStateException | ServletException e) {
                errors.put("image", "Lỗi khi xử lý file: " + e.getMessage());
            }

            if (filePart != null && filePart.getSize() > 0) {
                // Validate kích thước file (max 5MB)
                long maxFileSize = 5 * 1024 * 1024; // 5MB
                if (filePart.getSize() > maxFileSize) {
                    errors.put("image", "Kích thước file không được vượt quá 5MB.");
                }

                // Validate định dạng file
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null) {
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

                    if (!allowedExtensions.contains(fileExtension)) {
                        errors.put("image", "Chỉ chấp nhận file ảnh có định dạng: JPG, JPEG, PNG, GIF, WEBP.");
                    }
                }

                // Validate Content-Type
                String contentType = filePart.getContentType();
                if (contentType != null && !contentType.startsWith("image/")) {
                    errors.put("image", "File tải lên phải là file ảnh.");
                }
            }

            // === BƯỚC 3: XỬ LÝ NẾU VALIDATION THẤT BẠI ===
            if (!errors.isEmpty()) {
                // Đặt tất cả các giá trị đã nhập vào request để hiển thị lại trên form
                request.setAttribute("name", name != null ? name : "");
                request.setAttribute("productCode", productCode != null ? productCode : "");
                request.setAttribute("origin", origin != null ? origin : "");
                request.setAttribute("price", priceRaw != null ? priceRaw : "");
                request.setAttribute("description", description != null ? description : "");

                // Đặt các thông báo lỗi cụ thể cho từng trường
                for (Map.Entry<String, String> entry : errors.entrySet()) {
                    request.setAttribute(entry.getKey() + "Error", entry.getValue());
                }

                // Đặt danh sách lỗi tổng (nếu cần cho tương thích)
                request.setAttribute("errors", new ArrayList<>(errors.values()));

                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }

            // === BƯỚC 4: TẠO ĐỐI TƯỢNG VÀ LƯU VÀO DATABASE ===
            Product product = new Product();
            product.setName(name);
            product.setProductCode(productCode);
            product.setOrigin(origin);
            product.setPrice(price);
            product.setDescription(description);
            product.setDeleted(false);
            product.setCreatedBy(userId);

            int newId = this.productDao.insertProduct(product);

            if (newId <= 0) {
                // Nếu có lỗi khi insert, giữ lại giá trị đã nhập
                request.setAttribute("name", name);
                request.setAttribute("productCode", productCode);
                request.setAttribute("origin", origin);
                request.setAttribute("price", priceRaw);
                request.setAttribute("description", description);
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }

            // === BƯỚC 5: XỬ LÝ UPLOAD FILE ẢNH ===
            String imageFileName = "na.jpg";

            if (filePart != null && filePart.getSize() > 0) {
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                imageFileName = createSafeFileName(originalFileName, newId);

                String uploadDir = request.getServletContext().getRealPath("/image");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = uploadDir + File.separator + imageFileName;

                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            this.productDao.updateProductImage(newId, imageFileName);

            // === BƯỚC 6: HOÀN TẤT VÀ CHUYỂN HƯỚNG ===
            session.setAttribute("successMessage", "Sản phẩm đã được tạo thành công!");
            response.sendRedirect(request.getContextPath() + "/product?action=list&cache=" + System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi không mong muốn xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return;
        }
        try {
            int id = Integer.parseInt(idRaw);
            Product p = this.productDao.getProductById(id);
            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/product?action=list");
                return;
            }
            request.setAttribute("product", p);
            request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/product?action=list");
        }
    }

    private void processEditProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // === BƯỚC 1: XÁC THỰC & PHÂN QUYỀN ===
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");

            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            if (!isAuthorized(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thực hiện hành động này.");
                return;
            }

            // === BƯỚC 2: KIỂM TRA ID SẢN PHẨM VÀ LẤY DỮ LIỆU CŨ ===
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/product?action=list");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            Product oldProduct = this.productDao.getProductById(id);
            if (oldProduct == null) {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            // === BƯỚC 3: LẤY VÀ LÀM SẠCH DỮ LIỆU MỚI TỪ FORM ===
            String name = sanitizeInput(request.getParameter("name"));
            String origin = sanitizeInput(request.getParameter("origin"));
            String priceRaw = sanitizeInput(request.getParameter("price"));
            String description = sanitizeInput(request.getParameter("description"));
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

            // Map để chứa tất cả lỗi validation
            Map<String, String> errors = new HashMap<>();

            // === VALIDATION CÁC TRƯỜNG CƠ BẢN ===
            // Validate tên sản phẩm
            if (name == null || name.trim().isEmpty()) {
                errors.put("name", "Tên sản phẩm không được để trống.");
            } else if (name.length() > 255) {
                errors.put("name", "Tên sản phẩm không được vượt quá 255 ký tự.");
            } else if (!name.matches("^[\\p{L}\\p{N}\\s\\-\\.\\(\\)]+$")) {
                errors.put("name", "Tên sản phẩm chỉ được chứa chữ cái, số và các ký tự: - . ( )");
            }

            // Validate xuất xứ
            if (origin == null || origin.trim().isEmpty()) {
                errors.put("origin", "Xuất xứ không được để trống.");
            } else if (origin.length() > 100) {
                errors.put("origin", "Xuất xứ không được vượt quá 100 ký tự.");
            }

            // Validate giá
            BigDecimal price = null;
            if (priceRaw == null || priceRaw.trim().isEmpty()) {
                errors.put("price", "Giá sản phẩm không được để trống.");
            } else {
                try {
                    // Làm sạch chuỗi giá (bỏ dấu phẩy, chấm phân cách nghìn)
                    String cleanPrice = priceRaw.replace(",", "").replace(".", "");
                    price = new BigDecimal(cleanPrice);

                    if (price.compareTo(BigDecimal.ZERO) <= 0) {
                        errors.put("price", "Giá sản phẩm phải lớn hơn 0.");
                    } else if (price.compareTo(new BigDecimal("999999999")) > 0) {
                        errors.put("price", "Giá sản phẩm không được vượt quá 999,999,999 VNĐ.");
                    }
                } catch (NumberFormatException e) {
                    errors.put("price", "Giá sản phẩm không hợp lệ. Vui lòng nhập số.");
                }
            }

            // Validate mô tả (không bắt buộc)
            if (description != null && description.length() > 1000) {
                errors.put("description", "Mô tả không được vượt quá 1000 ký tự.");
            }

            // === VALIDATION FILE UPLOAD ===
            Part filePart = null;
            try {
                filePart = request.getPart("image");
            } catch (IllegalStateException | ServletException e) {
                errors.put("image", "Lỗi khi xử lý file: " + e.getMessage());
            }

            if (filePart != null && filePart.getSize() > 0) {
                // Validate kích thước file (max 5MB)
                long maxFileSize = 5 * 1024 * 1024; // 5MB
                if (filePart.getSize() > maxFileSize) {
                    errors.put("image", "Kích thước file không được vượt quá 5MB.");
                }

                // Validate định dạng file
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null) {
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

                    if (!allowedExtensions.contains(fileExtension)) {
                        errors.put("image", "Chỉ chấp nhận file ảnh có định dạng: JPG, JPEG, PNG, GIF, WEBP.");
                    }
                }

                // Validate Content-Type
                String contentType = filePart.getContentType();
                if (contentType != null && !contentType.startsWith("image/")) {
                    errors.put("image", "File tải lên phải là file ảnh.");
                }
            }

            // === BƯỚC 4: XỬ LÝ NẾU VALIDATION THẤT BẠI ===
            if (!errors.isEmpty()) {
                // Cập nhật lại đối tượng với các giá trị đã nhập
                oldProduct.setName(name);
                oldProduct.setOrigin(origin);
                oldProduct.setDescription(description);

                try {
                    if (price != null) {
                        oldProduct.setPrice(price);
                    }
                } catch (Exception e) {
                    // Giữ giá trị cũ nếu giá mới không hợp lệ
                }

                // Đặt các thông báo lỗi cụ thể cho từng trường
                for (Map.Entry<String, String> entry : errors.entrySet()) {
                    request.setAttribute(entry.getKey() + "Error", entry.getValue());
                }

                // Đặt danh sách lỗi tổng (nếu cần cho tương thích)
                request.setAttribute("editErrors", new ArrayList<>(errors.values()));
                request.setAttribute("product", oldProduct);

                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                        .forward(request, response);
                return;
            }

            // === BƯỚC 5: XỬ LÝ UPLOAD ẢNH MỚI (NẾU CÓ) ===
            String imageFileName = oldProduct.getImage();

            if (filePart != null && filePart.getSize() > 0) {
                // Xóa ảnh cũ trên server
                deleteImageByPattern(id);

                // Tạo tên file mới an toàn
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                imageFileName = createSafeFileName(originalFileName, id);

                String uploadDir = request.getServletContext().getRealPath("/image");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = uploadDir + File.separator + imageFileName;

                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    errors.put("image", "Không thể upload ảnh: " + e.getMessage());
                    request.setAttribute("editErrors", new ArrayList<>(errors.values()));
                    request.setAttribute("product", oldProduct);
                    request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                            .forward(request, response);
                    return;
                }
            }

            // === BƯỚC 6: CẬP NHẬT ĐỐI TƯỢNG PRODUCT ===
            oldProduct.setName(name);
            oldProduct.setOrigin(origin);
            oldProduct.setPrice(price);
            oldProduct.setDescription(description);
            oldProduct.setDeleted(isDeleted);
            oldProduct.setImage(imageFileName);
            oldProduct.setUpdatedBy(userId);

            // === BƯỚC 7: LƯU THAY ĐỔI VÀO DATABASE ===
            boolean success = this.productDao.editProduct(oldProduct);

            if (success) {
                session.setAttribute("successMessage", "Sản phẩm đã được cập nhật thành công!");
                response.sendRedirect(request.getContextPath()
                        + "/product?action=list&cache=" + System.currentTimeMillis());
            } else {
                request.setAttribute("product", oldProduct);
                request.setAttribute("editError", "Cập nhật thất bại! Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi không mong muốn xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // === BƯỚC 1: XÁC THỰC VÀ PHÂN QUYỀN ===
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        // Kiểm tra xem người dùng đã đăng nhập và có quyền hạn thực hiện thao tác xóa hay không.
        // Nếu không, chuyển hướng về trang danh sách sản phẩm một cách thầm lặng.
        if (userId == null || !isAuthorized(userRole)) {
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return; // Dừng xử lý.
        }

        // === BƯỚC 2: THỰC HIỆN XÓA MỀM ===
        try {
            // Lấy ID của sản phẩm cần xóa từ tham số URL.
            String productIdStr = request.getParameter("id");
            // Chuyển đổi ID từ chuỗi sang số nguyên.
            int id = Integer.parseInt(productIdStr);
            // Gọi DAO để thực hiện cập nhật trạng thái `isDeleted = true` trong database.
            // Truyền cả `userId` để ghi nhận ai là người thực hiện thao tác xóa.
            this.productDao.softDeleteProduct(id, userId);
        } catch (NumberFormatException e) {
            // Bắt lỗi nếu ID không phải là một số hợp lệ. Ghi lỗi ra System Error.
            System.err.println("Lỗi xóa sản phẩm: ID không hợp lệ.");
        } catch (Exception e) {
            // Bắt các lỗi khác có thể xảy ra trong quá trình tương tác với DB.
            System.err.println("Lỗi ngoại lệ khi xóa sản phẩm:");
            e.printStackTrace(); // In chi tiết lỗi ra console để debug.
        }

        // === BƯỚC 3: CHUYỂN HƯỚNG VỀ TRANG DANH SÁCH ===
        // Dù thành công hay thất bại, cuối cùng vẫn chuyển hướng người dùng về trang danh sách sản phẩm.
        response.sendRedirect(request.getContextPath() + "/product?action=list");
    }

    private void deleteImageByPattern(int productId) {
        // Tạo một đối tượng File trỏ đến thư mục upload.
        File folder = new File(UPLOAD_DIR);
        // Chỉ thực hiện nếu thư mục tồn tại và đúng là một thư mục.
        if (folder.exists() && folder.isDirectory()) {
            // Lấy danh sách tất cả các file trong thư mục mà có tên bắt đầu bằng "productId_product".
            // Ví dụ: nếu productId = 12, nó sẽ tìm các file như "12_product.jpg", "12_product_1687...png",...
            File[] files = folder.listFiles((dir, name) -> name.startsWith(productId + "_product"));

            // Nếu tìm thấy file nào khớp với mẫu.
            if (files != null) {
                // Lặp qua từng file và thực hiện xóa.
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    private boolean isAuthorized(String userRole) {
        // Trong trường hợp này, chỉ có "Admin" hoặc "Kỹ thuật" mới có quyền thực hiện các thao tác nhạy cảm.
        return "Admin".equals(userRole) || "Kỹ thuật".equals(userRole);
    }

    @Override
    public String getServletInfo() {
        return "Front Controller for Product Management";
    }

    private String handleFileUpload(Part filePart, int productId) throws IOException {
        // Nếu không có file, trả về ảnh mặc định
        if (filePart == null || filePart.getSize() == 0) {
            return "na.jpg";
        }

        // Tạo thư mục upload nếu chưa tồn tại
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Tạo tên file an toàn
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String safeFileName = createSafeFileName(originalFileName, productId);
        String filePath = UPLOAD_DIR + File.separator + safeFileName;

        // Lưu file với buffer tối ưu
        saveFileOptimized(filePart, filePath);

        return safeFileName;
    }

    /**
     * Lưu file với buffer size tối ưu và error handling
     */
    private void saveFileOptimized(Part filePart, String filePath) throws IOException {
        // Sử dụng try-with-resources để tự động đóng các stream sau khi dùng xong (kể cả khi có lỗi)
        try (InputStream input = filePart.getInputStream(); // Dùng BufferedInputStream để đọc dữ liệu nhanh và hiệu quả hơn nhờ bộ nhớ đệm (buffer)
                 BufferedInputStream bufferedInput = new BufferedInputStream(input, 16384); FileOutputStream output = new FileOutputStream(filePath); // Dùng BufferedOutputStream để ghi dữ liệu nhanh hơn (16KB buffer)
                 BufferedOutputStream bufferedOutput = new BufferedOutputStream(output, 16384)) {

            byte[] buffer = new byte[16384]; // Tạo mảng buffer 16KB
            int bytesRead;

            // Đọc từng block dữ liệu từ stream đầu vào và ghi ra file đầu ra cho đến hết
            while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                bufferedOutput.write(buffer, 0, bytesRead);
            }

            bufferedOutput.flush(); // Đảm bảo mọi dữ liệu còn lại trong buffer được ghi hết ra file

        } catch (IOException e) {
            // Nếu có lỗi khi ghi file (đứt mạng, ổ đĩa đầy, ...), xóa file tạm đã ghi
            File failedFile = new File(filePath);
            if (failedFile.exists()) {
                failedFile.delete();
            }
            // Ném lại exception sau khi đã cleanup
            throw new IOException("Lỗi khi lưu file: " + e.getMessage(), e);
        }
    }

    /**
     * Validate file upload với bảo mật
     */
    private String validateUploadedFile(Part filePart) {
        if (filePart == null) {
            return null; // Không bắt buộc upload file
        }

        // 1. Kiểm tra kích thước file
        long fileSize = filePart.getSize();
        if (fileSize > 10 * 1024 * 1024) { // 10MB limit
            return "File không được vượt quá 10MB!";
        }

        if (fileSize == 0) {
            return null; // File rỗng = không upload
        }

        // 2. Kiểm tra content type
        String contentType = filePart.getContentType();
        if (!isValidImageContentType(contentType)) {
            return "Chỉ chấp nhận file ảnh (JPG, PNG, GIF, WebP)!";
        }

        // 3. Kiểm tra extension từ filename
        String fileName = filePart.getSubmittedFileName();
        if (fileName == null || fileName.trim().isEmpty()) {
            return "Tên file không hợp lệ!";
        }

        String extension = getFileExtension(fileName).toLowerCase();
        if (!isValidImageExtension(extension)) {
            return "Extension file không được hỗ trợ: " + extension;
        }

        // 4. Kiểm tra tên file có ký tự đặc biệt nguy hiểm
        if (containsMaliciousChars(fileName)) {
            return "Tên file chứa ký tự không hợp lệ!";
        }

        return null; // Validation passed
    }

    /**
     * Kiểm tra Content-Type của file upload có phải là dạng ảnh được phép hay
     * không. Chỉ cho phép các dạng ảnh phổ biến: jpeg, jpg, png, gif, webp.
     *
     * @param contentType Kiểu MIME (Content-Type) của file (ví dụ: image/jpeg)
     * @return true nếu hợp lệ, false nếu không phải định dạng được cho phép
     */
    private boolean isValidImageContentType(String contentType) {
        if (contentType == null) {
            return false; // Không có content-type, không hợp lệ
        }
        return contentType.equals("image/jpeg")
                || contentType.equals("image/jpg")
                || contentType.equals("image/png")
                || contentType.equals("image/gif")
                || contentType.equals("image/webp");
    }

    /**
     * Kiểm tra phần mở rộng (đuôi file) của tên file upload có nằm trong danh
     * sách an toàn. Tránh nhận file lạ, chỉ nhận file ảnh với extension phổ
     * biến.
     *
     * @param extension Phần mở rộng của file, ví dụ ".jpg", ".png", ...
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean isValidImageExtension(String extension) {
        return extension.equals(".jpg")
                || extension.equals(".jpeg")
                || extension.equals(".png")
                || extension.equals(".gif")
                || extension.equals(".webp");
    }

    /**
     * Lấy extension (đuôi) từ một tên file. Nếu file không có dấu chấm, trả về
     * chuỗi rỗng.
     *
     * @param fileName Tên file gốc (có hoặc không có đuôi)
     * @return extension bao gồm cả dấu chấm, ví dụ ".jpg"
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return ""; // Không có đuôi file
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    /**
     * Tạo một tên file an toàn, duy nhất cho sản phẩm upload. Kết hợp productId
     * + timestamp (để không trùng nhau) + extension cho đúng loại file.
     *
     * @param originalFileName Tên file gốc người dùng upload lên
     * @param productId ID sản phẩm (dùng để phân biệt từng sản phẩm)
     * @return Tên file mới, ví dụ: "15_product_1726903852920.jpg"
     */
    private String createSafeFileName(String originalFileName, int productId) {
        String extension = getFileExtension(originalFileName);
        // Tạo tên file duy nhất theo cấu trúc: {id}_product_{thời gian tính bằng mili giây}.{đuôi}
        return productId + "_product_" + System.currentTimeMillis() + extension;
    }

    /**
     * Kiểm tra ký tự nguy hiểm trong filename
     */
    private boolean containsMaliciousChars(String fileName) {
        // Các ký tự nguy hiểm có thể dẫn đến path traversal
        String[] maliciousChars = {"..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|"};

        for (String malicious : maliciousChars) {
            if (fileName.contains(malicious)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Làm sạch chuỗi đầu vào để tránh lỗ hổng bảo mật XSS (Cross-Site
     * Scripting). Hàm này thay thế các ký tự đặc biệt trong HTML bằng mã an
     * toàn, giúp ngăn việc chèn mã độc vào input. Áp dụng cho dữ liệu nhận từ
     * người dùng trước khi hiển thị ra giao diện hoặc lưu vào database.
     *
     * @param input Chuỗi đầu vào cần làm sạch (VD: nội dung nhập từ form)
     * @return Chuỗi đã được "bọc" an toàn; nếu input là null thì trả về null
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null; // Nếu là null thì trả về luôn null, tránh lỗi khi xử lý
        }

        // Loại bỏ khoảng trắng đầu/cuối, sau đó thay thế các ký tự nguy hiểm thành mã HTML an toàn
        return input.trim()
                .replaceAll("<", "&lt;") // thay < thành &lt; để không bị coi là mở thẻ HTML
                .replaceAll(">", "&gt;") // thay > thành &gt; để không bị coi là đóng thẻ HTML
                .replaceAll("\"", "&quot;") // thay dấu nháy kép " thành &quot;
                .replaceAll("'", "&#x27;") // thay dấu nháy đơn ' thành mã HTML
                .replaceAll("/", "&#x2F;"); // thay dấu / để ngăn đóng nhanh thẻ
    }

}
