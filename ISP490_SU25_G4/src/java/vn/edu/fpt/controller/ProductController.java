package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal; // KHẮC PHỤC: Import BigDecimal
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;  // KHẮC PHỤC: Import Timestamp
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
@MultipartConfig
public class ProductController extends HttpServlet {

    private static final String UPLOAD_DIR = "D:/New folder/ISP490_SU25_G4/web/image"; // Nên cấu hình ngoài

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
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

    /**
     * Hàm kiểm tra dữ liệu đầu vào cho sản phẩm.
     *
     * @param name Tên sản phẩm
     * @param productCode Mã sản phẩm
     * @param origin Xuất xứ
     * @param priceRaw Giá dạng chuỗi
     * @param dao Đối tượng ProductDAO để kiểm tra
     * @param isCreating Cờ để xác định là đang tạo mới hay chỉnh sửa
     * @return Danh sách các lỗi. Rỗng nếu không có lỗi.
     */
    private List<String> validateProductInput(String name, String productCode, String origin, String priceRaw, ProductDAO dao, boolean isCreating) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Tên sản phẩm không được để trống!");
        }

        if (productCode == null || productCode.trim().isEmpty()) {
            errors.add("Mã sản phẩm không được để trống!");
        } else if (isCreating && dao.isProductCodeExists(productCode)) { // KHẮC PHỤC: Gọi đúng tên phương thức
            // Chỉ kiểm tra mã tồn tại khi tạo mới
            errors.add("Mã sản phẩm đã tồn tại!");
        }

        if (origin == null || origin.trim().isEmpty()) {
            errors.add("Xuất xứ không được để trống!");
        }

        if (priceRaw == null || priceRaw.trim().isEmpty()) {
            errors.add("Giá không được để trống!");
        } else {
            try {
                // Validate với BigDecimal
                BigDecimal price = new BigDecimal(priceRaw.replace(".", "").replace(",", ""));
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Giá phải là một số không âm!");
                }
            } catch (NumberFormatException ex) {
                errors.add("Giá không hợp lệ! Vui lòng chỉ nhập số.");
            }
        }
        return errors;
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO products = new ProductDAO();
        int page = 1;
        int pageSize = 10;
        String pageRaw = request.getParameter("page");
        if (pageRaw != null && !pageRaw.isEmpty()) try {
            page = Integer.parseInt(pageRaw);
        } catch (NumberFormatException e) {
            page = 1;
        }
        String keyword = request.getParameter("keyword");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String origin = request.getParameter("origin");

        // KHẮC PHỤC: Sử dụng BigDecimal cho giá
        BigDecimal minPrice = null, maxPrice = null;
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            // Có thể thêm thông báo lỗi nếu giá nhập vào không phải là số
        }

        if (origin != null && origin.trim().isEmpty()) {
            origin = null;
        }
        int totalProducts = products.countProductsWithFilter(keyword, minPrice, maxPrice, origin);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        List<Product> listProducts = products.getProductsWithFilter(keyword, minPrice, maxPrice, origin, page, pageSize);
        List<String> origins = products.getAllOrigins();
        request.setAttribute("originList", origins);
        request.setAttribute("productList", listProducts);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("origin", origin);
        request.getRequestDispatcher("jsp/technicalSupport/listProduct.jsp").forward(request, response);
    }

    private void viewProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            ProductDAO dao = new ProductDAO();
            Product product = dao.getProductById(id);
            if (product != null) {
                request.setAttribute("product", product);
                request.getRequestDispatcher("/jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
    }

    // Trong file controller/ProductController.java
    private void processCreateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("\n--- BẮT ĐẦU DEBUG: THÊM SẢN PHẨM ---");
        try {
            ProductDAO dao = new ProductDAO();
            HttpSession session = request.getSession();

            // DEBUG 1: In ra dữ liệu nhận được từ Form
            System.out.println("[CONTROLLER] 1. Dữ liệu nhận từ form:");
            String name = request.getParameter("name");
            String productCode = request.getParameter("productCode");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price");
            String description = request.getParameter("description");
            System.out.println("   - name: " + name);
            System.out.println("   - productCode: " + productCode);
            System.out.println("   - origin: " + origin);
            System.out.println("   - priceRaw: " + priceRaw);

            // DEBUG 2: In ra thông tin lấy từ Session
            System.out.println("[CONTROLLER] 2. Kiểm tra thông tin Session:");
            Integer userId = (Integer) session.getAttribute("userId"); // Sửa 'd' thành 'D'
            System.out.println("   - userId lấy được: " + userId);
            if (userId == null) {
                System.out.println("   -> LỖI: userId là null. Chuyển hướng về trang login.");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // DEBUG 3: In ra kết quả Validate
            System.out.println("[CONTROLLER] 3. Bắt đầu validate dữ liệu:");
            List<String> errors = validateProductInput(name, productCode, origin, priceRaw, dao, true);
            System.out.println("   - Số lỗi validate: " + errors.size());
            if (!errors.isEmpty()) {
                System.out.println("   - Chi tiết lỗi: " + errors);
                // ... code xử lý khi có lỗi validate (giữ nguyên)
                request.setAttribute("errors", errors);
                request.setAttribute("name", name);
                request.setAttribute("productCode", productCode);
                request.setAttribute("origin", origin);
                request.setAttribute("price", priceRaw);
                request.setAttribute("description", description);
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }
            System.out.println("   -> Validate thành công.");

            // DEBUG 4: In ra đối tượng Product trước khi truyền cho DAO
            System.out.println("[CONTROLLER] 4. Tạo đối tượng Product để lưu:");
            Product p = new Product();
            p.setName(name);
            p.setProductCode(productCode);
            p.setOrigin(origin);
            p.setPrice(new BigDecimal(priceRaw.replace(".", "").replace(",", "")));
            p.setDescription(description);
            p.setDeleted(false);
            p.setCreatedBy(userId);
            p.setUpdatedBy(userId);
            System.out.println("   - Dữ liệu chuẩn bị gửi đi: " + p.toString());

            // DEBUG 5: Gọi DAO
            System.out.println("[CONTROLLER] 5. Gọi dao.insertProduct(p) để lưu vào DB.");
            int newId = dao.insertProduct(p);
            System.out.println("[CONTROLLER]   -> Kết quả từ DAO (ID mới): " + newId);

            if (newId > 0) {
                System.out.println("[CONTROLLER] -> THÊM THÀNH CÔNG. Bắt đầu xử lý ảnh.");
                // ... Code xử lý ảnh giữ nguyên ...
                Part filePart = request.getPart("image");
                File uploadPath = new File(UPLOAD_DIR);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }
                String imageFileName;
                if (filePart != null && filePart.getSize() > 0) {
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.')).toLowerCase();
                    imageFileName = newId + "_product" + extension;
                    filePart.write(UPLOAD_DIR + File.separator + imageFileName);
                } else {
                    imageFileName = "na.jpg";
                }
                dao.updateProductImage(newId, imageFileName);
                System.out.println("[CONTROLLER] -> Xử lý ảnh xong. Chuyển hướng về trang danh sách.");
                response.sendRedirect(request.getContextPath() + "/product?action=list");
            } else {
                System.out.println("[CONTROLLER] -> LỖI: DAO không thể thêm sản phẩm.");
                request.setAttribute("error", "Không thể tạo sản phẩm do lỗi từ phía server.");
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.out.println("[CONTROLLER] --- CÓ LỖI NGOẠI LỆ XẢY RA ---");
            e.printStackTrace(); // In ra toàn bộ lỗi chi tiết
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        // ... (Logic không đổi)
        try {
            int id = Integer.parseInt(idRaw);
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);
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

    private void processEditProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        // KHẮC PHỤC: Lấy userId
        Integer userId = (Integer) session.getAttribute("userId"); // Sửa 'd' thành 'D'
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            ProductDAO dao = new ProductDAO();
            Product oldProduct = dao.getProductById(id);
            if (oldProduct == null) {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            String name = request.getParameter("name");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price").replace(",", "");
            String description = request.getParameter("description");
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));
            Part filePart = request.getPart("image");

            List<String> errors = validateProductInput(name, oldProduct.getProductCode(), origin, priceRaw, dao, false);

            if (!errors.isEmpty()) {
                request.setAttribute("editErrors", errors);
                // Gửi lại đối tượng cũ với các giá trị người dùng đã nhập để hiển thị lại form
                oldProduct.setName(name);
                oldProduct.setOrigin(origin);
                try {
                    oldProduct.setPrice(new BigDecimal(priceRaw.replace(".", "").replace(",", ""))); // KHẮC PHỤC
                } catch (Exception e) {
                    /* Bỏ qua nếu giá không hợp lệ */ }
                oldProduct.setDescription(description);
                request.setAttribute("product", oldProduct);
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
                return;
            }

            String imageFileName = oldProduct.getImage();
            if (filePart != null && filePart.getSize() > 0) {
                deleteImageByPattern(id); // Xóa ảnh cũ
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.')).toLowerCase();
                imageFileName = id + "_product_" + System.currentTimeMillis() + extension;
                filePart.write(UPLOAD_DIR + File.separator + imageFileName);
            }

            // Tạo đối tượng product mới để update
            oldProduct.setName(name);
            oldProduct.setImage(imageFileName);
            oldProduct.setOrigin(origin);
            oldProduct.setPrice(new BigDecimal(priceRaw)); // KHẮC PHỤC
            oldProduct.setDescription(description);
            oldProduct.setDeleted(isDeleted); // KHẮC PHỤC
            oldProduct.setUpdatedBy(userId); // KHẮC PHỤC
            // createdAt, createdBy không đổi. updatedAt sẽ được DAO xử lý.

            boolean success = dao.editProduct(oldProduct);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/product?action=list&cache=" + System.currentTimeMillis());
            } else {
                request.setAttribute("product", oldProduct);
                request.setAttribute("editError", "Cập nhật thất bại!");
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    // Trong file controller/ProductController.java
// Thay thế hoàn toàn phương thức cũ bằng phiên bản này
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("\n--- BẮT ĐẦU DEBUG: Controller.deleteProduct ---");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        // DEBUG 1: In ra thông tin Session và quyền hạn
        System.out.println("[CONTROLLER-DELETE] 1. Kiểm tra Session và Quyền:");
        System.out.println("   - userId lấy được từ session: " + userId);
        System.out.println("   - userRole lấy được từ session: '" + userRole + "'"); // In ra role để xem chính xác

        // Kiểm tra quyền Admin
        if (userId == null || !"Admin".equals(userRole)) {
            System.out.println("   -> LỖI: Không có quyền Admin hoặc chưa đăng nhập. Chuyển hướng và kết thúc.");
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return; // Dừng lại ở đây
        }
        System.out.println("   -> OK: Đã xác thực quyền Admin.");

        try {
            // DEBUG 2: In ra tham số ID từ URL
            String productIdStr = request.getParameter("id");
            System.out.println("[CONTROLLER-DELETE] 2. Lấy Product ID từ URL:");
            System.out.println("   - Tham số 'id' nhận được (dạng chuỗi): " + productIdStr);

            int id = Integer.parseInt(productIdStr);
            System.out.println("   - ID sau khi chuyển đổi (dạng số): " + id);

            ProductDAO dao = new ProductDAO();

            System.out.println("[CONTROLLER-DELETE] 3. Chuẩn bị gọi DAO.softDeleteProduct...");
            dao.softDeleteProduct(id, userId);
            System.out.println("[CONTROLLER-DELETE] 4. Đã gọi DAO thành công.");

        } catch (NumberFormatException e) {
            System.out.println("[CONTROLLER-DELETE] !!! LỖI: Không thể chuyển đổi ID sản phẩm sang dạng số. ID có thể bị null hoặc không hợp lệ.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("[CONTROLLER-DELETE] !!! LỖI NGOẠI LỆ KHÁC XẢY RA:");
            e.printStackTrace();
        }

        // Luôn chuyển hướng về trang danh sách
        System.out.println("[CONTROLLER-DELETE] 5. Chuyển hướng về trang danh sách.");
        response.sendRedirect("product?action=list");
    }

    private void deleteImageByPattern(int productId) {
        File folder = new File(UPLOAD_DIR);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.startsWith(productId + "_product"));
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Front Controller for Product Management";
    }
}
