package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.fpt.dao.ProductDAO;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "ProductDeleteController", urlPatterns = {"/deleteProduct"})
public class ProductDeleteController extends HttpServlet {

    private final String imageFolderPath = "D:/New folder/ISP490_SU25_G4/web/image";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] selectedProducts = request.getParameterValues("id");

        if (selectedProducts != null && selectedProducts.length > 0) {
            ProductDAO products = new ProductDAO();

            for (String idStr : selectedProducts) {
                try {
                    int productId = Integer.parseInt(idStr);

                    // Xóa ảnh theo pattern
                    deleteImageByPattern(productId);

                    // Xóa sản phẩm trong DB
                    products.deleteProduct(productId);

                } catch (NumberFormatException e) {
                    e.printStackTrace(); // hoặc log lỗi cụ thể từng id
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/ProductController?service=products");
    }

    private void deleteImageByPattern(int productId) {
        File folder = new File(imageFolderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.startsWith(productId + "_product")) {
                        file.delete(); // có thể thêm kiểm tra deleted == false để log nếu cần
                    }
                }
            }
        }
    }
}
