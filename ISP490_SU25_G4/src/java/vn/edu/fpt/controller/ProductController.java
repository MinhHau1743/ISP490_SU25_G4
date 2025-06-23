/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductCategory;

/**
 *
 * @author phamh
 */
@WebServlet(name = "ProductController", urlPatterns = {"/ProductController"})
@MultipartConfig
public class ProductController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String service = request.getParameter("service");
        ProductDAO products = new ProductDAO();
        ProductCategoriesDAO productCategories = new ProductCategoriesDAO();
        if (service == null) {
            service = "products";
        }
        if (service.equals("products")) {
            int page = 1;
            int pageSize = 10;

            String pageRaw = request.getParameter("page");
            String sizeRaw = request.getParameter("size");
            if (pageRaw != null) {
                page = Integer.parseInt(pageRaw);
            }
            if (sizeRaw != null) {
                pageSize = Integer.parseInt(sizeRaw);
            }

            // --- Lấy filter từ request ---
            String keyword = request.getParameter("keyword");
            String minPriceStr = request.getParameter("minPrice");
            String maxPriceStr = request.getParameter("maxPrice");
            String origin = request.getParameter("origin");
            String categoryIdStr = request.getParameter("categoryId");

            Double minPrice = null, maxPrice = null;
            Integer categoryId = null;
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            }
            if (origin != null && origin.trim().isEmpty()) {
                origin = null;
            }

            // --- Đếm tổng sản phẩm và tổng trang theo filter ---
            int totalProducts = products.countProductsWithFilter(keyword, minPrice, maxPrice, origin, categoryId);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            // --- Lấy danh sách sản phẩm theo filter và phân trang ---
            List<Product> listProducts = products.getProductsWithFilter(keyword, minPrice, maxPrice, origin, categoryId, page, pageSize);

            // --- Lấy toàn bộ danh mục, map categoryId -> name ---
            List<ProductCategory> categories = productCategories.getAllCategories();
            Map<Integer, String> categoryMap = new HashMap<>();
            for (ProductCategory c : categories) {
                categoryMap.put(c.getId(), c.getName());
            }
            request.setAttribute("productList", listProducts);
            request.setAttribute("categoryMap", categoryMap);
            request.setAttribute("categories", categories);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);

            // Gửi các filter sang JSP để giữ trạng thái filter (giữ lại trên giao diện)
            request.setAttribute("keyword", keyword);
            request.setAttribute("minPrice", minPriceStr);
            request.setAttribute("maxPrice", maxPriceStr);
            request.setAttribute("origin", origin);
            request.setAttribute("categoryId", categoryIdStr);

            String notification = (String) request.getAttribute("Notification");
            if (notification != null && !notification.isEmpty()) {
                request.setAttribute("Notification", notification);
            }

            request.getRequestDispatcher("jsp/technicalSupport/listProduct.jsp").forward(request, response);
        }

        if (service.equals("getProductById")) {
            String idRaw = request.getParameter("id");

            // Nếu id null hoặc rỗng thì chuyển đến trang tạo mới sản phẩm
            if (idRaw == null || idRaw.trim().isEmpty()) {
                // Nếu cần truyền categories cho trang tạo mới, làm ở đây:
                ProductCategoriesDAO categoriesDAO = new ProductCategoriesDAO();
                List<ProductCategory> categories = categoriesDAO.getAllCategories();
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }

            // Ngược lại, id hợp lệ -> xử lý xem/sửa sản phẩm
            int id = Integer.parseInt(idRaw);
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);
            if (p == null) {
                response.sendRedirect("ProductController");
                return;
            }

            request.setAttribute("product", p);
            // Nếu cần truyền categories cho trang chi tiết, làm ở đây:
            ProductCategoriesDAO categoriesDAO = new ProductCategoriesDAO();
            List<ProductCategory> categories = categoriesDAO.getAllCategories();
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
        }

        if (service.equals("createProduct")) {
            try {
                String name = request.getParameter("name");
                String productCode = request.getParameter("productCode");
                String origin = request.getParameter("origin");
                String priceRaw = request.getParameter("price");
                String description = request.getParameter("description");
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                Part filePart = request.getPart("image");

                // Normalize price
                if (priceRaw != null) {
                    priceRaw = priceRaw.replace(",", "").replace(".", "");
                }
                double price = Double.parseDouble(priceRaw);

                // Creation time
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String createdAt = LocalDateTime.now().format(dtf);

                // Create DAO
                ProductDAO dao = new ProductDAO();

                // Generate product code if empty
                if (productCode == null || productCode.trim().isEmpty()) {
                    productCode = generateRandomProductCode(dao);
                }

                // Create product object
                Product p = new Product();
                p.setName(name);
                p.setProductCode(productCode);
                p.setOrigin(origin);
                p.setPrice(price);
                p.setDescription(description);
                p.setCategoryId(categoryId);
                p.setIsDeleted(false);
                p.setCreatedAt(createdAt);
                p.setUpdatedAt(null);

                // B1: Insert and get new ID
                int newId = products.insertProduct(p);

                // B2: Process image
                String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                String imageFileName = null;
                if (filePart != null && filePart.getSize() > 0) {
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int dotIndex = submittedFileName.lastIndexOf('.');
                    if (dotIndex >= 0) {
                        extension = submittedFileName.substring(dotIndex).toLowerCase();
                    }
                    // Rename to id_product.extension format
                    imageFileName = newId + "_product" + extension;
                    filePart.write(uploadDir + File.separator + imageFileName);

                    // Update product with image filename in database
                    p.setId(newId);
                    p.setImage(imageFileName);
                    dao.updateProductImage(newId, imageFileName);
                } else {
                    // Set default image if no file uploaded
                    imageFileName = "default.jpg";
                    Files.copy(
                            Paths.get(uploadDir + File.separator + "default.jpg"),
                            Paths.get(uploadDir + File.separator + newId + "_product.jpg")
                    );
                    // Update product with default image filename in database
                    dao.updateProductImage(newId, newId + "_product.jpg");
                }

                // Redirect or show message
                request.setAttribute("redirectUrl", "ProductController");
                request.getRequestDispatcher("editLoading.jsp").forward(request, response);

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }

        if (service.equals("getProductToEdit")) {
            String idRaw = request.getParameter("id");
            if (idRaw == null) {
                response.sendRedirect("ProductController");
                return;
            }

            int id = Integer.parseInt(idRaw);
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);
            List<ProductCategory> categoryList = productCategories.getAllCategories();
            request.setAttribute("categories", categoryList);
            if (p == null) {
                response.sendRedirect("ProductController");
                return;
            }
            request.setAttribute("product", p);
            request.getRequestDispatcher("jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
        }

        if (service.equals("editProduct")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                String origin = request.getParameter("origin");
                String priceRaw = request.getParameter("price");
// Loại cả dấu phẩy và dấu chấm (nếu dùng kiểu VN)
                priceRaw = priceRaw.replace(",", "").replace(".", "");
                double price = Double.parseDouble(priceRaw);

                String description = request.getParameter("description");
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                String createdAt = request.getParameter("createdAt");
                String updatedAt = request.getParameter("updatedAt");
                boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

                // Thư mục chứa ảnh
                String uploadDir = "D:\\New folder\\ISP490_SU25_G4\\web\\image";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                // Xử lý ảnh
                String imageFileName = null;
                Part filePart = request.getPart("image");
                String oldImage = request.getParameter("oldImage");

                // Danh sách các file ảnh cũ cần xóa
                List<File> filesToDelete = new ArrayList<>();

                if (filePart != null && filePart.getSize() > 0) {
                    // Lấy tên file gốc và extension
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int dotIndex = submittedFileName.lastIndexOf('.');
                    if (dotIndex >= 0) {
                        extension = submittedFileName.substring(dotIndex).toLowerCase();
                    }

                    // Tạo tên file ảnh duy nhất (tránh cache)
                    imageFileName = id + "_product_" + System.currentTimeMillis() + extension;

                    // Ghi ảnh vào thư mục
                    filePart.write(uploadDir + File.separator + imageFileName);

                    // Tìm tất cả ảnh cũ của sản phẩm để xóa sau
                    File[] existingFiles = uploadPath.listFiles();
                    if (existingFiles != null) {
                        String prefix = id + "_product";
                        for (File file : existingFiles) {
                            if (file.getName().startsWith(prefix) && !file.getName().equals(imageFileName)) {
                                filesToDelete.add(file);
                            }
                        }
                    }
                } else {
                    // Không upload ảnh mới, tìm ảnh cũ nếu có
                    File[] existingFiles = uploadPath.listFiles();
                    if (existingFiles != null) {
                        String prefix = id + "_product";
                        for (File file : existingFiles) {
                            if (file.getName().startsWith(prefix)) {
                                imageFileName = file.getName();
                                break;
                            }
                        }
                    }
                }

                // Nếu vẫn không có ảnh → dùng mặc định
                if (imageFileName == null) {
                    imageFileName = "default.jpg";
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //Check product_code ở backend
                Product old = products.getProductById(id);
                String productCode = old.getProductCode();
                Product p = new Product();
                p.setId(id);
                p.setName(name);
                p.setProductCode(productCode);
                p.setImage(imageFileName);
                p.setOrigin(origin);
                p.setPrice(price);
                p.setDescription(description);
                p.setCategoryId(categoryId);
                p.setIsDeleted(isDeleted);
                p.setCreatedAt(createdAt);
                p.setUpdatedAt(LocalDateTime.now().format(dtf));

                // Gọi DAO để cập nhật sản phẩm
                ProductDAO dao = new ProductDAO();
                boolean success = dao.editProduct(p);

                // Xóa ảnh cũ SAU KHI cập nhật thành công
                if (success && !filesToDelete.isEmpty()) {
                    new Thread(() -> {
                        for (File file : filesToDelete) {
                            try {
                                if (file.exists()) {
                                    if (file.delete()) {
                                        System.out.println("Đã xóa ảnh cũ: " + file.getName());
                                    } else {
                                        System.err.println("Không thể xóa: " + file.getName());
                                    }
                                }
                            } catch (SecurityException e) {
                                System.err.println("Lỗi bảo mật khi xóa ảnh: " + e.getMessage());
                            }
                        }
                    }).start();
                }

                // Gán lại ảnh cho view (thêm cache-busting)
                long timestamp = System.currentTimeMillis();
                request.setAttribute("imageVersion", timestamp);
                request.setAttribute("imageFileName", imageFileName + "?v=" + timestamp);

                // Điều hướng sau khi cập nhật
                if (success) {
                    request.setAttribute("redirectUrl", "ProductController?cache=" + timestamp);
                    request.getRequestDispatcher("editLoading.jsp").forward(request, response);
                } else {
                    request.setAttribute("product", p);
                    request.setAttribute("editError", "Cập nhật thất bại!");
                    request.getRequestDispatcher("ProductController").forward(request, response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }

        if (service.equals("deleteProduct")) {
            String[] selectedProducts = request.getParameterValues("id");

            if (selectedProducts != null && selectedProducts.length > 0) {
                try {
                    for (String idStr : selectedProducts) {
                        int product_id = Integer.parseInt(idStr);

                        // Xóa ảnh dựa theo pattern
                        deleteImageByPattern(product_id);

                        // Xóa sản phẩm
                        products.deleteProduct(product_id);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            response.sendRedirect("ProductController");
        }

    }

    public String generateRandomProductCode(ProductDAO dao) {
        String code;
        Random random = new Random();
        do {
            int num = 100000 + random.nextInt(900000); // Tạo số ngẫu nhiên 6 chữ số
            code = "SP" + num;
        } while (dao.checkProductCodeExists(code));
        return code;
    }

    public void deleteImageByPattern(int productId) {
        String imageFolderPath = "D:\\New folder\\ISP490_SU25_G4\\web\\image";
        File folder = new File(imageFolderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.startsWith(productId + "_product")) {
                        boolean deleted = file.delete();
                    }
                }
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
