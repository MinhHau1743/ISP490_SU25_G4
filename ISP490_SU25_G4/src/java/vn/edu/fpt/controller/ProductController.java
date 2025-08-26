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
import java.util.List;

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
            // === BƯỚC 1: XÁC THỰC & PHÂN QUYỀN (AUTHENTICATION & AUTHORIZATION) ===
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");

            // 1.1. Kiểm tra người dùng đã đăng nhập chưa.
            // Nếu `userId` không tồn tại trong session, tức là chưa đăng nhập.
            if (userId == null) {
                // Chuyển hướng về trang đăng nhập.
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return; // Dừng xử lý ngay lập tức.
            }

            // 1.2. Kiểm tra người dùng có đủ quyền hạn để thực hiện chức năng này không.
            // Phương thức isAuthorized() sẽ kiểm tra xem `userRole` có phải là 'Admin' hoặc 'Kỹ thuật' hay không.
            if (!isAuthorized(userRole)) {
                // Nếu không có quyền, trả về lỗi 403 (Forbidden - Cấm truy cập).
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thực hiện hành động này.");
                return; // Dừng xử lý.
            }

            // === BƯỚC 2: LẤY DỮ LIỆU TỪ FORM VÀ KIỂM TRA TÍNH HỢP LỆ (VALIDATION) ===
            // 2.1. Lấy các tham số dạng text từ request.
            // `sanitizeInput` là một phương thức giả định để làm sạch đầu vào (ví dụ: cắt bỏ khoảng trắng thừa).
            String name = sanitizeInput(request.getParameter("name"));
            String productCode = sanitizeInput(request.getParameter("productCode"));
            String origin = sanitizeInput(request.getParameter("origin"));
            String priceRaw = sanitizeInput(request.getParameter("price")); // Lấy giá ở dạng chuỗi thô.
            String description = sanitizeInput(request.getParameter("description"));

            // 2.2. Gọi phương thức validation để kiểm tra các quy tắc nghiệp vụ.
            // Ví dụ: không được để trống, mã sản phẩm không trùng, giá phải là số,...
            // `true` ở cuối để báo hiệu rằng đây là thao tác tạo mới (cần check trùng mã sản phẩm).
            List<String> errors = validateProductInput(name, productCode, origin, priceRaw, this.productDao, true);

            // 2.3. Lấy và kiểm tra file upload.
            Part filePart = null;
            try {
                // `request.getPart("image")` sẽ lấy phần file có name="image" từ form.
                filePart = request.getPart("image");
            } catch (IllegalStateException ignore) {
                // Bỏ qua lỗi này nếu form không phải là multipart/form-data (hiếm khi xảy ra nếu cấu hình đúng).
            }

            // Gọi phương thức riêng để kiểm tra file (kích thước, định dạng,...).
            String fileValidationError = validateUploadedFile(filePart);
            if (fileValidationError != null) {
                // Nếu có lỗi file, thêm vào danh sách lỗi chung.
                errors.add(fileValidationError);
            }

            // === BƯỚC 3: XỬ LÝ NẾU VALIDATION THẤT BẠI ===
            // Nếu danh sách `errors` không rỗng, tức là có ít nhất một lỗi.
            if (!errors.isEmpty()) {
                // Gửi lại các giá trị người dùng đã nhập để họ không phải điền lại từ đầu.
                request.setAttribute("errors", errors); // Danh sách các lỗi để hiển thị.
                request.setAttribute("name", name);
                request.setAttribute("productCode", productCode);
                request.setAttribute("origin", origin);
                request.setAttribute("price", priceRaw);
                request.setAttribute("description", description);

                // Forward trở lại trang tạo sản phẩm để hiển thị lỗi.
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return; // Dừng xử lý.
            }

            // === BƯỚC 4: TẠO ĐỐI TƯỢNG VÀ LƯU VÀO DATABASE ===
            // Nếu không có lỗi nào, tiến hành tạo đối tượng Product.
            Product product = new Product();
            product.setName(name);
            product.setProductCode(productCode);
            product.setOrigin(origin);
            // Chuyển đổi chuỗi giá đã làm sạch (bỏ dấu '.' và ',') thành kiểu BigDecimal.
            product.setPrice(new BigDecimal(priceRaw.replace(".", "").replace(",", "")));
            product.setDescription(description);
            product.setDeleted(false); // Mặc định sản phẩm mới không bị xóa.
            product.setCreatedBy(userId); // Ghi nhận người tạo.

            // 4.1. Gọi DAO để thêm sản phẩm vào database.
            // Phương thức này trả về ID của sản phẩm mới được tạo.
            int newId = this.productDao.insertProduct(product);

            // Nếu `newId` nhỏ hơn hoặc bằng 0, có nghĩa là việc insert vào DB đã thất bại.
            if (newId <= 0) {
                request.setAttribute("error", "Không thể tạo sản phẩm do lỗi từ phía server.");
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return; // Dừng xử lý.
            }

            // === BƯỚC 5: XỬ LÝ UPLOAD FILE ẢNH ===
            String imageFileName = "na.jpg"; // Tên file mặc định nếu không có ảnh nào được tải lên.

            // Chỉ xử lý nếu người dùng có chọn file (`filePart` khác null) và file có nội dung (`getSize() > 0`).
            if (filePart != null && filePart.getSize() > 0) {
                // Lấy tên file gốc từ client.
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                // Tạo tên file mới, an toàn hơn để tránh trùng lặp và các ký tự đặc biệt.
                imageFileName = createSafeFileName(originalFileName, newId);

                // Lấy đường dẫn thực tế đến thư mục /image trên server. Cách này giúp code chạy được trên mọi môi trường.
                String uploadDir = request.getServletContext().getRealPath("/image");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs(); // Tạo thư mục nếu nó chưa tồn tại.
                }
                String filePath = uploadDir + File.separator + imageFileName;

                // Sử dụng Java NIO (Files.copy) để ghi file, hiệu quả và an toàn hơn.
                // `try-with-resources` đảm bảo `InputStream` sẽ được tự động đóng sau khi hoàn tất.
                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 5.1. Cập nhật tên file ảnh vào sản phẩm vừa tạo trong database.
            this.productDao.updateProductImage(newId, imageFileName);

            // === BƯỚC 6: HOÀN TẤT VÀ CHUYỂN HƯỚNG ===
            // Đặt một thông báo thành công vào session để hiển thị ở trang danh sách.
            session.setAttribute("successMessage", "Sản phẩm đã được tạo thành công!");

            // Chuyển hướng người dùng về trang danh sách sản phẩm.
            // Thêm tham số `cache` với giá trị là thời gian hiện tại để trình duyệt không bị cache lại trang cũ.
            response.sendRedirect(request.getContextPath() + "/product?action=list&cache=" + System.currentTimeMillis());

        } catch (Exception e) {
            // Bắt tất cả các lỗi ngoại lệ không lường trước được.
            e.printStackTrace(); // In lỗi ra console của server để debug.
            request.setAttribute("error", "Có lỗi không mong muốn xảy ra: " + e.getMessage());
            // Chuyển hướng đến một trang lỗi chung.
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
            // === BƯỚC 1: XÁC THỰC & PHÂN QUYỀN (AUTHENTICATION & AUTHORIZATION) ===
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");

            // 1.1. Kiểm tra đăng nhập: Nếu không có userId trong session, chuyển về trang login.
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return; // Dừng xử lý.
            }

            // 1.2. Kiểm tra quyền hạn: Người dùng có quyền chỉnh sửa sản phẩm không?
            if (!isAuthorized(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền thực hiện hành động này.");
                return; // Dừng xử lý.
            }

            // === BƯỚC 2: KIỂM TRA ID SẢN PHẨM VÀ LẤY DỮ LIỆU CŨ TỪ DATABASE ===
            String idParam = request.getParameter("id");
            // 2.1. Kiểm tra xem ID có được gửi lên hay không.
            if (idParam == null || idParam.trim().isEmpty()) {
                // Nếu không có ID, không biết sửa sản phẩm nào, quay về trang danh sách.
                response.sendRedirect(request.getContextPath() + "/product?action=list");
                return;
            }

            int id;
            try {
                // 2.2. Chuyển đổi ID từ chuỗi sang số.
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                // Nếu ID không phải là số hợp lệ, coi như không tìm thấy sản phẩm.
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            // 2.3. Dùng ID để lấy thông tin sản phẩm hiện tại từ database.
            Product oldProduct = this.productDao.getProductById(id);
            // Nếu không tìm thấy sản phẩm nào với ID này, chuyển đến trang 404.
            if (oldProduct == null) {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            // === BƯỚC 3: LẤY VÀ LÀM SẠCH DỮ LIỆU MỚI TỪ FORM ===
            String name = sanitizeInput(request.getParameter("name"));
            String origin = sanitizeInput(request.getParameter("origin"));
            String priceRaw = sanitizeInput(request.getParameter("price"));
            if (priceRaw != null) {
                // Loại bỏ các ký tự phân cách tiền tệ để chuẩn bị cho việc chuyển đổi sang số.
                priceRaw = priceRaw.replace(",", "").replace(".", "");
            }
            String description = sanitizeInput(request.getParameter("description"));
            // `isDeleted` là một checkbox hoặc radio button, giá trị của nó sẽ là "true" nếu được chọn.
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

            // === BƯỚC 4: KIỂM TRA TÍNH HỢP LỆ CỦA DỮ LIỆU MỚI ===
            // Gọi lại hàm validate chung. `false` ở cuối để báo rằng đây là thao tác "sửa",
            // không cần kiểm tra trùng mã sản phẩm (vì mã sản phẩm không cho phép sửa).
            List<String> errors = validateProductInput(name, oldProduct.getProductCode(),
                    origin, priceRaw, this.productDao, false);

            // === BƯỚC 5: KIỂM TRA FILE ẢNH MỚI (NẾU CÓ) ===
            Part filePart = request.getPart("image"); // Lấy file từ request.
            String fileValidationError = validateUploadedFile(filePart); // Kiểm tra file (kích thước, loại file,...).
            if (fileValidationError != null) {
                // Nếu file không hợp lệ, thêm lỗi vào danh sách.
                errors.add(fileValidationError);
            }

            // === BƯỚC 6: NẾU VALIDATION THẤT BẠI, QUAY LẠI FORM VÀ HIỂN THỊ LỖI ===
            if (!errors.isEmpty()) {
                request.setAttribute("editErrors", errors); // Gửi danh sách lỗi sang JSP.

                // Cập nhật lại đối tượng `oldProduct` với các giá trị người dùng vừa nhập sai
                // để họ không phải điền lại từ đầu (cơ chế "sticky form").
                oldProduct.setName(name);
                oldProduct.setOrigin(origin);
                oldProduct.setDescription(description);

                try {
                    if (priceRaw != null && !priceRaw.trim().isEmpty()) {
                        oldProduct.setPrice(new BigDecimal(priceRaw));
                    }
                } catch (NumberFormatException e) {
                    // Nếu giá nhập sai định dạng, giữ lại giá trị cũ của sản phẩm.
                }

                request.setAttribute("product", oldProduct); // Gửi lại đối tượng đã cập nhật sang JSP.
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                        .forward(request, response);
                return; // Dừng xử lý.
            }

            // === BƯỚC 7: XỬ LÝ UPLOAD ẢNH MỚI (NẾU CÓ) ===
            String imageFileName = oldProduct.getImage(); // Mặc định giữ lại ảnh cũ.

            // Chỉ xử lý khi người dùng thực sự chọn một file mới (filePart tồn tại và có kích thước > 0).
            if (filePart != null && filePart.getSize() > 0) {
                // Xóa ảnh cũ trên server trước khi upload ảnh mới để tránh rác.
                deleteImageByPattern(id);

                // Tạo tên file mới an toàn và duy nhất để lưu trữ.
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                imageFileName = createSafeFileName(originalFileName, id);

                // Lấy đường dẫn thực tế đến thư mục /image trên server.
                // Cách này giúp ứng dụng chạy đúng trên mọi máy chủ mà không cần hardcode đường dẫn.
                String uploadDir = request.getServletContext().getRealPath("/image");
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs(); // Tạo thư mục nếu chưa có.
                }

                String filePath = uploadDir + File.separator + imageFileName;

                // Dùng Java NIO để ghi file. `try-with-resources` đảm bảo stream được đóng tự động.
                try (InputStream input = filePart.getInputStream()) {
                    Files.copy(input, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    // Nếu quá trình ghi file thất bại, báo lỗi và quay lại form.
                    errors.add("Không thể upload ảnh: " + e.getMessage());
                    request.setAttribute("editErrors", errors);
                    request.setAttribute("product", oldProduct);
                    request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                            .forward(request, response);
                    return;
                }
            }

            // === BƯỚC 8: CẬP NHẬT ĐỐI TƯỢNG PRODUCT TRONG BỘ NHỚ ===
            // Sau khi mọi thứ hợp lệ, cập nhật tất cả các trường của đối tượng `oldProduct`.
            oldProduct.setName(name);
            oldProduct.setOrigin(origin);
            oldProduct.setPrice(new BigDecimal(priceRaw));
            oldProduct.setDescription(description);
            oldProduct.setDeleted(isDeleted);
            oldProduct.setImage(imageFileName); // Cập nhật tên ảnh mới (hoặc giữ nguyên tên cũ).
            oldProduct.setUpdatedBy(userId); // Ghi nhận người cập nhật.

            // === BƯỚC 9: LƯU THAY ĐỔI VÀO DATABASE ===
            boolean success = this.productDao.editProduct(oldProduct);

            if (success) {
                // Nếu cập nhật DB thành công, đặt thông báo và chuyển hướng về trang danh sách.
                session.setAttribute("successMessage", "Sản phẩm đã được cập nhật thành công!");
                response.sendRedirect(request.getContextPath()
                        + "/product?action=list&cache=" + System.currentTimeMillis());
            } else {
                // Nếu cập nhật DB thất bại, quay lại form chỉnh sửa và báo lỗi.
                request.setAttribute("product", oldProduct);
                request.setAttribute("editError", "Cập nhật thất bại! Vui lòng thử lại.");
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            // Bắt các lỗi ngoại lệ không lường trước được, in log và chuyển đến trang lỗi chung.
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
