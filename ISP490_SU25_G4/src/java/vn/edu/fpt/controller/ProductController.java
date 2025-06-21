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

            // Lấy thông tin phân trang từ request
            String pageRaw = request.getParameter("page");
            String sizeRaw = request.getParameter("size");
            if (pageRaw != null) {
                page = Integer.parseInt(pageRaw);
            }
            if (sizeRaw != null) {
                pageSize = Integer.parseInt(sizeRaw);
            }

            // Lấy tổng số sản phẩm để tính tổng số trang
            int totalProducts = products.countAllProducts();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            // Lấy danh sách sản phẩm theo trang
            List<Product> listProducts = products.viewAllProduct(page, pageSize);

            // Lấy toàn bộ danh mục, tạo Map categoryId -> categoryName
            List<ProductCategory> categories = productCategories.getAllCategories();
            Map<Integer, String> categoryMap = new HashMap<>();
            for (ProductCategory c : categories) {
                categoryMap.put(c.getId(), c.getName());
            }

            // Tạo map để lưu sản phẩm và ảnh tương ứng
            Map<Product, String> productImageMap = new LinkedHashMap<>();

            String imageDir = getServletContext().getRealPath("/image");
            String[] extensions = {"jpg", "jpeg", "png", "webp"};

            for (Product p : listProducts) {
                String foundFile = "default.jpg";
                long lastModified = 0;

                // Tìm tất cả file dạng id_product*.ext
                File dir = new File(imageDir);
                File[] files = dir.listFiles((d, name) -> {
                    for (String ext : extensions) {
                        if (name.startsWith(p.getId() + "_product") && name.toLowerCase().endsWith("." + ext)) {
                            return true;
                        }
                    }
                    return false;
                });

                if (files != null && files.length > 0) {
                    // Lấy file mới nhất (theo lastModified)
                    for (File file : files) {
                        if (file.lastModified() > lastModified) {
                            foundFile = file.getName();
                            lastModified = file.lastModified();
                        }
                    }
                    // Gắn query string để phá cache
                    foundFile = foundFile + "?" + lastModified;
                }
                // Nếu không tìm thấy file, vẫn để default.jpg
                productImageMap.put(p, foundFile);
            }

            // Gửi dữ liệu sang JSP
            request.setAttribute("productImageMap", productImageMap);
            request.setAttribute("categoryMap", categoryMap);
            request.setAttribute("categories", categories); // (dùng cho <select> nếu cần)
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);

            // Gửi thông báo nếu có
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

                // Chuẩn hóa giá
                if (priceRaw != null) {
                    priceRaw = priceRaw.replace(",", "").replace(".", "");
                }
                double price = Double.parseDouble(priceRaw);

                // Thời gian tạo
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String createdAt = LocalDateTime.now().format(dtf);

                // Tạo DAO
                ProductDAO dao = new ProductDAO();

                // Nếu không nhập productCode, tự sinh code không trùng
                if (productCode == null || productCode.trim().isEmpty()) {
                    productCode = generateRandomProductCode(dao);
                }

                // Tạo đối tượng sản phẩm (ID tự tăng)
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

                // B1: Insert và lấy ID mới
                int newId = dao.insertProduct(p);

                // B2: Xử lý ảnh
                String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                Part filePart = request.getPart("image");
                if (filePart != null && filePart.getSize() > 0) {
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int dotIndex = submittedFileName.lastIndexOf('.');
                    if (dotIndex >= 0) {
                        extension = submittedFileName.substring(dotIndex).toLowerCase();
                    }
                    String imageFileName = newId+"_product"+ extension;
                    filePart.write(uploadDir + File.separator + imageFileName);
                    // Không update vào DB, chỉ lưu file
                } else {
                    // Không có ảnh thì có thể copy file default.jpg sang product_{newId}.jpg hoặc bỏ qua bước này
                    // Ví dụ:
                    // Files.copy(Paths.get(uploadDir + File.separator + "default.jpg"), Paths.get(uploadDir + File.separator + "product_" + newId + ".jpg"));
                }

                // Chuyển trang hoặc show thông báo
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
            // Tìm ảnh hiện có theo ID (dạng id_product*.ext)
            String uploadDir = getServletContext().getRealPath("/image");
            String[] extensions = {"jpg", "jpeg", "png", "webp"};
            String foundFile = null;
            long lastModified = 0;

            File dir = new File(uploadDir);
            if (dir.exists()) {
                File[] files = dir.listFiles((d, name) -> {
                    for (String ext : extensions) {
                        if (name.startsWith(id + "_product") && name.toLowerCase().endsWith("." + ext)) {
                            return true;
                        }
                    }
                    return false;
                });
                if (files != null && files.length > 0) {
                    // Lấy file mới nhất (nếu có nhiều file)
                    for (File file : files) {
                        if (file.lastModified() > lastModified) {
                            foundFile = file.getName();
                            lastModified = file.lastModified();
                        }
                    }
                }
            }

            if (foundFile != null) {
                request.setAttribute("imageFileName", foundFile);
            } else {
                request.setAttribute("imageFileName", "na.jpg"); // ảnh mặc định nếu không có
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
// Tạo đối tượng sản phẩm
                p.setId(id);
                p.setName(name);
                p.setProductCode(productCode);
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
