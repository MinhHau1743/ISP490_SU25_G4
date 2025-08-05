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
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductCategory;

/**
 *
 * @author phamh
 */
@WebServlet(name = "ProductCreateServlet", urlPatterns = {"/createProduct"})
@MultipartConfig
public class ProductCreateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form tạo sản phẩm
        List<ProductCategory> categories = new ProductCategoriesDAO().getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ProductDAO dao = new ProductDAO();
            HttpSession session = request.getSession();
            List<String> errors = new ArrayList<>();
            String name = request.getParameter("name");
            String productCode = request.getParameter("productCode");
            String origin = request.getParameter("origin");
            String priceRaw = request.getParameter("price");
            String description = request.getParameter("description");
            Part filePart = request.getPart("image");
            String userName = (String) session.getAttribute("userName");
            double price = 0;

            // ==== VALIDATE INPUT ====
            if (name == null || name.trim().isEmpty()) {
                errors.add("Tên sản phẩm không được để trống!");
            }
            if (dao.isProductCodeExists(productCode)) {
                errors.add("Mã sản phẩm đã tồn tại!");
            }
            if (productCode == null || productCode.trim().isEmpty()) {
                errors.add("Mã sản phẩm không được để trống!");
            }

            if (origin == null || origin.trim().isEmpty()) {
                errors.add("Xuất xứ không được để trống!");
            }

            if (priceRaw == null || priceRaw.trim().isEmpty()) {
                errors.add("Giá không được để trống!");
            } else {
                try {
                    priceRaw = priceRaw.replace(",", "").replace(".", "");
                    price = Double.parseDouble(priceRaw);
                    if (price < 0) {
                        errors.add("Giá phải lớn hơn hoặc bằng 0!");
                    }
                } catch (NumberFormatException ex) {
                    errors.add("Giá không hợp lệ!");
                }
            }


            // === Nếu có lỗi validate ===
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.setAttribute("name", name);
                request.setAttribute("productCode", productCode);
                request.setAttribute("origin", origin);
                request.setAttribute("price", priceRaw);
                request.setAttribute("description", description);
                request.setAttribute("categories", new ProductCategoriesDAO().getAllCategories());
                request.getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp").forward(request, response);
                return;
            }

            // ==== TẠO MỚI SẢN PHẨM ====
            String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Product p = new Product();
            p.setName(name);
            p.setProductCode(productCode);
            p.setOrigin(origin);
            p.setPrice(price);
            p.setDescription(description);
            p.setIsDeleted(false);
            p.setCreatedAt(createdAt);
            p.setUpdatedAt(null);
            p.setCreatedBy(userName);
            int newId = dao.insertProduct(p);

            // ==== XỬ LÝ ẢNH ====
            // Đổi đường dẫn theo máy
            String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String imageFileName;
            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = submittedFileName.contains(".")
                        ? submittedFileName.substring(submittedFileName.lastIndexOf('.')).toLowerCase()
                        : "";
                imageFileName = newId + "_product" + extension;
                filePart.write(uploadDir + File.separator + imageFileName);
            } else {
                // Nếu không upload ảnh, sao chép file mặc định "na.jpg"
                imageFileName = newId + "_product.jpg";
                File defaultImage = new File(uploadDir, "na.jpg");
                File newImage = new File(uploadDir, imageFileName);

                if (defaultImage.exists()) {
                    Files.copy(defaultImage.toPath(), newImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    imageFileName = "na.jpg"; // fallback nếu file mặc định bị thiếu
                }
            }

            dao.updateProductImage(newId, imageFileName);

            // === Điều hướng đến trang loading redirect ===
            request.setAttribute("redirectUrl", request.getContextPath() + "/ProductController");
            request.getRequestDispatcher("/editLoading.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

}
