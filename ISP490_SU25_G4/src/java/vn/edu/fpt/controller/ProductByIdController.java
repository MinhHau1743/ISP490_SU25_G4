package vn.edu.fpt.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

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

        ProductDAO dao = new ProductDAO();
        Product product = dao.getProductById(id);

        if (product != null) {
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
