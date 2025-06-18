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
import java.io.File;
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

            String imageDir = "D:/New folder/ISP490_SU25_G4/web/image";
            String[] extensions = {"jpg", "jpeg", "png", "webp"};

            for (Product p : listProducts) {
                String foundFile = "default.jpg"; // mặc định

                for (String ext : extensions) {
                    String fileName = p.getId() + "_product." + ext;
                    File file = new File(imageDir, fileName);
                    if (file.exists()) {
                        foundFile = fileName;
                        break;
                    }
                }

                productImageMap.put(p, foundFile); // map sản phẩm với ảnh
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
            request.setAttribute("product", p);
            request.getRequestDispatcher("jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
        }
        if (service.equals("editProduct")) {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String productCode = request.getParameter("productCode");
            String origin = request.getParameter("origin");
            double price = Double.parseDouble(request.getParameter("price"));
            String description = request.getParameter("description");
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            boolean isDeleted = Boolean.parseBoolean(request.getParameter("isDeleted"));
            String createdAt = request.getParameter("createdAt");
            String updatedAt = request.getParameter("updatedAt");

            // Đường dẫn thư mục chứa ảnh
            String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Upload ảnh nếu có
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = id + "_" + Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                filePart.write(uploadDir + File.separator + fileName);
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

            ProductDAO dao = new ProductDAO();
            boolean success = dao.editProduct(p);

            // Sau khi cập nhật, kiểm tra xem có file ảnh nào tồn tại không (jpg, png, ...)
            String[] extensions = {"jpg", "jpeg", "png", "webp"};
            String foundFile = null;
            for (String ext : extensions) {
                String fileName = id + "_product." + ext;
                File file = new File(uploadDir, fileName);
                if (file.exists()) {
                    foundFile = fileName;
                    break;
                }
            }

            // Gán tên ảnh cho JSP nếu có
            if (foundFile != null) {
                request.setAttribute("imageFileName", foundFile);
            } else {
                request.setAttribute("imageFileName", "default.jpg");
            }

            if (success) {
                response.sendRedirect("ProductController?service=viewProductDetail&id=" + id);
            } else {
                request.setAttribute("product", p);
                request.setAttribute("editError", "Cập nhật thất bại!");
                request.getRequestDispatcher("jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
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
