/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.model.ProductCategory;

/**
 *
 * @author phamh
 */
@WebServlet(name = "ProductCategoriesController", urlPatterns = {"/ProductCategories"})
public class ProductCategoriesController extends HttpServlet {

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
        processRequest(request, response);
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
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("groupName");

        ProductCategoriesDAO productCategories = new ProductCategoriesDAO();

        // Kiểm tra rỗng
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("error", "Tên nhóm không được để trống!");
            request.getRequestDispatcher("/ProductController").forward(request, response);
            return;
        }

        // Kiểm tra trùng
        if (productCategories.checkDuplicate(name.trim())) {
            request.setAttribute("error", "Tên nhóm đã tồn tại!");
            request.getRequestDispatcher("/ProductController").forward(request, response);
            return;
        }

        // Nếu hợp lệ, tiến hành thêm
        ProductCategory category = new ProductCategory();
        category.setName(name.trim());

        boolean success = productCategories.insertCategory(category);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/ProductController");
        } else {
            request.setAttribute("error", "Thêm danh mục thất bại!");
            request.getRequestDispatcher("/ProductController").forward(request, response);
        }
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
