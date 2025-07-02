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
import java.nio.file.StandardCopyOption;
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
