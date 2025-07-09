package vn.edu.fpt.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductCategory;

@WebServlet(name = "GetProductByIdServlet", urlPatterns = {"/getProductById"})
public class ProductByIdController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
            return;
        }
        int id = Integer.parseInt(idStr);
        ProductCategoriesDAO productCategories = new ProductCategoriesDAO();
        ProductDAO dao = new ProductDAO();
        Product product = dao.getProductById(id);
        List<ProductCategory> categories = productCategories.getAllCategories();
        Map<Integer, String> categoryMap = new HashMap<>();
        for (ProductCategory c : categories) {
            categoryMap.put(c.getId(), c.getName());
        }
        if (product != null) {
            request.setAttribute("categoryMap", categoryMap);
            request.setAttribute("product", product);
            request.getRequestDispatcher("/jsp/technicalSupport/viewProductDetail.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/404.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
