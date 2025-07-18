package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import vn.edu.fpt.model.ProductCategory;

@WebServlet(name = "ProductEditController", urlPatterns = {"/editProduct"})
@MultipartConfig
public class ProductEditController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");

        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ProductController?service=products");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw);
            ProductDAO dao = new ProductDAO();
            Product p = dao.getProductById(id);

            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/ProductController?service=products");
                return;
            }

            List<ProductCategory> categoryList = new ProductCategoriesDAO().getAllCategories();
            request.setAttribute("product", p);
            request.setAttribute("categories", categoryList);
            request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ProductController?service=products");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");
        try {
            List<String> errors = new ArrayList<>();
            int id = 0;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (Exception ex) {
                errors.add("ID sản phẩm không hợp lệ!");
            }

            String name = request.getParameter("name");
            if (name == null || name.trim().isEmpty()) {
                errors.add("Tên sản phẩm không được để trống!");
            }

            String origin = request.getParameter("origin");
            if (origin == null || origin.trim().isEmpty()) {
                errors.add("Xuất xứ không được để trống!");
            }

            String priceRaw = request.getParameter("price");
            double price = 0;
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

            String description = request.getParameter("description");
            String categoryIdRaw = request.getParameter("categoryId");
            int categoryId = 0;
            if (categoryIdRaw == null || categoryIdRaw.trim().isEmpty()) {
                errors.add("Danh mục không được để trống!");
            } else {
                try {
                    categoryId = Integer.parseInt(categoryIdRaw);
                } catch (NumberFormatException ex) {
                    errors.add("Danh mục không hợp lệ!");
                }
            }

            String createdAt = request.getParameter("createdAt");
            String updatedAt = request.getParameter("updatedAt");
            boolean isDeleted = "true".equals(request.getParameter("isDeleted"));

            Part filePart = null;
            try {
                filePart = request.getPart("image");
            } catch (Exception ignored) {
            }

            String oldImage = request.getParameter("oldImage");

            if (!errors.isEmpty()) {
                Product p = new Product();
                p.setId(id);
                p.setName(name);
                p.setOrigin(origin);
                p.setPrice(price);
                p.setDescription(description);
                p.setCategoryId(categoryId);
                p.setIsDeleted(isDeleted);
                p.setCreatedAt(createdAt);
                p.setUpdatedAt(updatedAt);
                p.setImage(oldImage);

                request.setAttribute("product", p);
                request.setAttribute("editErrors", errors);
                request.setAttribute("categories", new ProductCategoriesDAO().getAllCategories());
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);
                return;
            }
            request.getRequestDispatcher("/editLoading.jsp").forward(request, response);
            // Đổi đường dẫn theo máy
            String uploadDir = "D:/New folder/ISP490_SU25_G4/web/image";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String imageFileName = null;
            List<File> filesToDelete = new ArrayList<>();

            if (filePart != null && filePart.getSize() > 0) {
                String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = "";
                int dotIndex = submittedFileName.lastIndexOf('.');
                if (dotIndex >= 0) {
                    extension = submittedFileName.substring(dotIndex).toLowerCase();
                }
                imageFileName = id + "_product_" + System.currentTimeMillis() + extension;
                filePart.write(uploadDir + File.separator + imageFileName);

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

            if (imageFileName == null) {
                imageFileName = "default.jpg";
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ProductDAO dao = new ProductDAO();
            Product old = dao.getProductById(id);
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
            p.setUpdated_by(userName);
            boolean success = dao.editProduct(p);

            if (success && !filesToDelete.isEmpty()) {
                new Thread(() -> {
                    for (File file : filesToDelete) {
                        try {
                            if (file.exists()) {
                                file.delete();
                            }
                        } catch (SecurityException ignored) {
                        }
                    }
                }).start();
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ProductController?service=products&cache=" + System.currentTimeMillis());
            } else {
                request.setAttribute("product", p);
                request.setAttribute("editError", "Cập nhật thất bại!");
                request.setAttribute("categories", new ProductCategoriesDAO().getAllCategories());
                request.getRequestDispatcher("/jsp/technicalSupport/editProductDetail.jsp").forward(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
