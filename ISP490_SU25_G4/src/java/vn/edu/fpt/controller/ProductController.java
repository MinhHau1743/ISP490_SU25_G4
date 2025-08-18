package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ProductController", urlPatterns = {"/product"})
@MultipartConfig
public class ProductController extends HttpServlet {

    private static final String UPLOAD_DIR = "D:/New folder/ISP490_SU25_G4/web/image"; // Nên cấu hình ngoài web.xml hoặc file properties

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
        List<String> errors = new ArrayList<>();

        // Đây là logic validation chuẩn, không có dòng code thử nghiệm
        if (name == null || name.trim().isEmpty()) {
            errors.add("Tên sản phẩm không được để trống!");
        }
        if (productCode == null || productCode.trim().isEmpty()) {
            errors.add("Mã sản phẩm không được để trống!");
        } else if (isCreating && dao.isProductCodeExists(productCode)) {
            errors.add("Mã sản phẩm đã tồn tại!");
        }
        if (origin == null || origin.trim().isEmpty()) {
            errors.add("Xuất xứ không được để trống!");
        }
        if (priceRaw == null || priceRaw.trim().isEmpty()) {
            errors.add("Giá không được để trống!");
        } else {
            try {
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

        BigDecimal minPrice = null, maxPrice = null;
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            // Bỏ qua nếu giá nhập vào không phải là số
        }

        if (origin != null && origin.trim().isEmpty()) {
            origin = null;
        }

        int totalProducts = this.productDao.countProductsWithFilter(keyword, minPrice, maxPrice, origin);
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
        List<Product> listProducts = this.productDao.getProductsWithFilter(keyword, minPrice, maxPrice, origin, page, pageSize);
        List<String> origins = this.productDao.getAllOrigins();

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
            Product product = this.productDao.getProductById(id);
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

    private void processCreateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // Dòng kiểm tra quyền mới
            if (!isAuthorized(userRole)) {
                // Gửi lỗi 403 Forbidden nếu không có quyền
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
                return;
            }

            String name = request.getParameter("name");
            String productCode = request.getParameter("productCode");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price");
            String description = request.getParameter("description");

            List<String> errors = validateProductInput(name, productCode, origin, priceRaw, this.productDao, true);
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("name", name);
                request.setAttribute("productCode", productCode);
                request.setAttribute("origin", origin);
                request.setAttribute("price", priceRaw);
                request.setAttribute("description", description);
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }

            Product p = new Product();
            p.setName(name);
            p.setProductCode(productCode);
            p.setOrigin(origin);
            p.setPrice(new BigDecimal(priceRaw.replace(".", "").replace(",", "")));
            p.setDescription(description);
            p.setDeleted(false);
            p.setCreatedBy(userId);
            p.setUpdatedBy(userId);

            int newId = this.productDao.insertProduct(p);

            if (newId > 0) {
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
                this.productDao.updateProductImage(newId, imageFileName);
                response.sendRedirect(request.getContextPath() + "/product?action=list");
            } else {
                request.setAttribute("error", "Không thể tạo sản phẩm do lỗi từ phía server.");
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
            }
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

    private void processEditProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // Dòng kiểm tra quyền mới
            if (!isAuthorized(userRole)) {
                // Gửi lỗi 403 Forbidden nếu không có quyền
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
                return;
            }

            int id = Integer.parseInt(request.getParameter("id"));
            Product oldProduct = this.productDao.getProductById(id);
            if (oldProduct == null) {
                response.sendRedirect(request.getContextPath() + "/404.jsp");
                return;
            }

            String name = request.getParameter("name");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price").replace(",", "");
            String description = request.getParameter("description");
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

            List<String> errors = validateProductInput(name, oldProduct.getProductCode(), origin, priceRaw, this.productDao, false);

            if (!errors.isEmpty()) {
             
                request.setAttribute("editErrors", errors);
                oldProduct.setName(name);
                oldProduct.setOrigin(origin);
                try {
                    oldProduct.setPrice(new BigDecimal(priceRaw.replace(".", "").replace(",", "")));
                } catch (Exception e) {
                    /* Bỏ qua nếu giá không hợp lệ, giữ giá cũ */ }
                oldProduct.setDescription(description);
                request.setAttribute("product", oldProduct);
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
                return;
            }

            Part filePart = request.getPart("image");
            String imageFileName = oldProduct.getImage();
            if (filePart != null && filePart.getSize() > 0) {
                deleteImageByPattern(id);
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.')).toLowerCase();
                imageFileName = id + "_product_" + System.currentTimeMillis() + extension;
                filePart.write(UPLOAD_DIR + File.separator + imageFileName);
            }

            oldProduct.setName(name);
            oldProduct.setImage(imageFileName);
            oldProduct.setOrigin(origin);
            oldProduct.setPrice(new BigDecimal(priceRaw));
            oldProduct.setDescription(description);
            oldProduct.setDeleted(isDeleted);
            oldProduct.setUpdatedBy(userId);

            boolean success = this.productDao.editProduct(oldProduct);

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

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null || !isAuthorized(userRole)) { // Sử dụng phương thức mới
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return;
        }

        try {
            String productIdStr = request.getParameter("id");
            int id = Integer.parseInt(productIdStr);
            this.productDao.softDeleteProduct(id, userId);
        } catch (NumberFormatException e) {
            System.err.println("Lỗi xóa sản phẩm: ID không hợp lệ.");
        } catch (Exception e) {
            System.err.println("Lỗi ngoại lệ khi xóa sản phẩm:");
            e.printStackTrace();
        }

        // ...
        response.sendRedirect(request.getContextPath() + "/product?action=list");
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

    private boolean isAuthorized(String userRole) {
        // Trả về true nếu vai trò là "Admin" hoặc "Kỹ thuật"
        return "Admin".equals(userRole) || "Kỹ thuật".equals(userRole);
    }

    @Override
    public String getServletInfo() {
        return "Front Controller for Product Management";
    }
}
