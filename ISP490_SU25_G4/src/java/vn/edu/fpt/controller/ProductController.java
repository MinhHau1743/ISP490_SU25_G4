package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author phamh Front Controller duy nhất để quản lý tất cả các hoạt động CRUD
 * cho Sản phẩm. Sử dụng một tham số "action" để điều hướng đến phương thức xử
 * lý phù hợp.
 */
@WebServlet(name = "ProductController", urlPatterns = {"/product"})
@MultipartConfig
public class ProductController extends HttpServlet {

    private static final String UPLOAD_DIR = "D:/New folder/ISP490_SU25_G4/web/image"; // Nên cấu hình ngoài
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                deleteProducts(request, response);
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

    // ===================================================================================
    // HÀM VALIDATE TÁI SỬ DỤNG
    // ===================================================================================
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
        } else if (isCreating && dao.isProductCodeExists(productCode)) {
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
                double price = Double.parseDouble(priceRaw.replace(",", "").replace(".", ""));
                if (price < 0) {
                    errors.add("Giá phải là một số không âm!");
                }
            } catch (NumberFormatException ex) {
                errors.add("Giá không hợp lệ!");
            }
        }
        return errors;
    }

    // ===================================================================================
    // CÁC PHƯƠNG THỨC HÀNH ĐỘNG (ACTION METHODS)
    // ===================================================================================
    private void listProducts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (Logic của listProducts không đổi)
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
        Double minPrice = null, maxPrice = null;
        if (minPriceStr != null && !minPriceStr.isEmpty()) {
            minPrice = Double.parseDouble(minPriceStr);
        }
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            maxPrice = Double.parseDouble(maxPriceStr);
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
        // ... (Logic của viewProductDetail không đổi)
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

    private void processCreateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ProductDAO dao = new ProductDAO();
            HttpSession session = request.getSession();

            String name = request.getParameter("name");
            String productCode = request.getParameter("productCode");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price");
            String description = request.getParameter("description");
            Part filePart = request.getPart("image");
            String userName = (String) session.getAttribute("userName");

            // Sử dụng hàm validate chung
            List<String> errors = validateProductInput(name, productCode, origin, priceRaw, dao, true);

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
            p.setPrice(Double.parseDouble(priceRaw.replace(",", "").replace(".", "")));
            p.setDescription(description);
            p.setIsDeleted(false);
            p.setCreatedAt(LocalDateTime.now().format(dtf));
            p.setCreatedBy(userName);
            int newId = dao.insertProduct(p);

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
                imageFileName = newId + "_product.jpg";
                File defaultImage = new File(UPLOAD_DIR, "na.jpg");
                File newImage = new File(UPLOAD_DIR, imageFileName);
                if (defaultImage.exists()) {
                    Files.copy(defaultImage.toPath(), newImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    imageFileName = "na.jpg";
                }
            }
            dao.updateProductImage(newId, imageFileName);

            response.sendRedirect(request.getContextPath() + "/product?action=list");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ... (Logic của showEditForm không đổi)
        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/product?action=list");
            return;
        }
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
        String userName = (String) session.getAttribute("userName");
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
            String priceRaw = request.getParameter("price");
            String description = request.getParameter("description");
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));
            Part filePart = request.getPart("image");

            // Sử dụng hàm validate chung, không cần kiểm tra mã sản phẩm tồn tại
            List<String> errors = validateProductInput(name, oldProduct.getProductCode(), origin, priceRaw, dao, false);

            if (!errors.isEmpty()) {
                request.setAttribute("editErrors", errors);
                // Gửi lại đối tượng cũ với các giá trị người dùng đã nhập để hiển thị lại form
                oldProduct.setName(name);
                oldProduct.setOrigin(origin);
                try {
                    oldProduct.setPrice(Double.parseDouble(priceRaw.replace(",", "").replace(".", "")));
                } catch (Exception e) {
                }
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

            Product p = new Product();
            p.setId(id);
            p.setName(name);
            p.setProductCode(oldProduct.getProductCode());
            p.setImage(imageFileName);
            p.setOrigin(origin);
            p.setPrice(Double.parseDouble(priceRaw.replace(",", "").replace(".", "")));
            p.setDescription(description);
            p.setIsDeleted(isDeleted);
            p.setCreatedAt(oldProduct.getCreatedAt());
            p.setCreatedBy(oldProduct.getCreatedBy());
            p.setUpdatedAt(LocalDateTime.now().format(dtf));
            p.setUpdatedBy(userName);

            boolean success = dao.editProduct(p);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/product?action=list&cache=" + System.currentTimeMillis());
            } else {
                request.setAttribute("product", p);
                request.setAttribute("editError", "Cập nhật thất bại!");
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    private void deleteProducts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ... (Logic của deleteProducts không đổi)
        String[] selectedProducts = request.getParameterValues("id");
        if (selectedProducts != null && selectedProducts.length > 0) {
            ProductDAO products = new ProductDAO();
            for (String idStr : selectedProducts) {
                try {
                    int productId = Integer.parseInt(idStr);
                    deleteImageByPattern(productId);
                    products.deleteProduct(productId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        response.sendRedirect(request.getContextPath() + "/product?action=list");
    }

    private void deleteImageByPattern(int productId) {
        // ... (Logic của deleteImageByPattern không đổi)
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
