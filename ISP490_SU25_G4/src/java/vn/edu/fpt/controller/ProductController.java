/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.edu.fpt.dao.ProductDAO;
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

            // Tính tổng số trang
            int totalProducts = products.countAllProducts();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

            // Lấy danh sách sản phẩm theo trang
            List<Product> listProducts = products.viewAllProduct(page, pageSize);

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
            if (idRaw == null) {
                response.sendRedirect("ProductController");
                return;
            }
            int id = Integer.parseInt(idRaw);
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);
            if (p == null) {
                response.sendRedirect("ProductController");
                return;
            }
            request.setAttribute("product", p);
            request.getRequestDispatcher("jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
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
            List<ProductCategory> categoryList = dao.getAllCategories();
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
                String productCode = request.getParameter("productCode");
                String origin = request.getParameter("origin");
                double price = Double.parseDouble(request.getParameter("price"));
                String description = request.getParameter("description");
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                String createdAt = request.getParameter("createdAt");
                String updatedAt = request.getParameter("updatedAt");
                boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

                // Thư mục chứa ảnh
                String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                // Xử lý ảnh
                String imageFileName = null;
                Part filePart = request.getPart("image");
                String oldImage = request.getParameter("oldImage");

                if (filePart != null && filePart.getSize() > 0) {
                    // Lấy tên file gốc và extension
                    String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String extension = "";
                    int dotIndex = submittedFileName.lastIndexOf('.');
                    if (dotIndex >= 0) {
                        extension = submittedFileName.substring(dotIndex).toLowerCase(); // ví dụ: ".jpg"
                    }

                    // Tạo tên file ảnh duy nhất (tránh cache)
                    imageFileName = id + "_product_" + System.currentTimeMillis() + extension;

                    // Ghi ảnh vào thư mục
                    filePart.write(uploadDir + File.separator + imageFileName);

                    // Xoá ảnh cũ nếu có, không phải mặc định
                    if (oldImage != null && !oldImage.equals("na.jpg") && !oldImage.equals("default.jpg")) {
                        // Xoá tất cả ảnh theo pattern cũ (phòng trường hợp đổi tên nhiều lần)
                        File[] files = uploadPath.listFiles();
                        if (files != null) {
                            String prefix = id + "_product";
                            for (File file : files) {
                                if (file.getName().startsWith(prefix) && !file.getName().equals(imageFileName)) {
                                    file.delete();
                                }
                            }
                        }
                    }
                } else {
                    // Không upload ảnh mới, tìm ảnh cũ nếu có
                    File[] files = uploadPath.listFiles();
                    if (files != null) {
                        String prefix = id + "_product";
                        for (File file : files) {
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

                // Tạo đối tượng sản phẩm
                Product p = new Product();
                p.setId(id);
                p.setName(name);
                p.setProductCode(productCode);
                p.setOrigin(origin);
                p.setPrice(price);
                p.setDescription(description);
                p.setCategoryId(categoryId);
                p.setIsDeleted(isDeleted);
                p.setCreatedAt(createdAt);
                p.setUpdatedAt(updatedAt);

                // Gọi DAO để cập nhật sản phẩm (lưu ý cập nhật cả tên ảnh vào DB nếu có)
                ProductDAO dao = new ProductDAO();
                boolean success = dao.editProduct(p);

                // Gán lại ảnh cho view
                request.setAttribute("imageFileName", imageFileName);

                // Điều hướng sau khi cập nhật
                if (success) {
                    request.setAttribute("redirectUrl", "ProductController");
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
