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
import vn.edu.fpt.dao.UserDAO;

/**
 *
 * @author minhh
 */
@WebServlet(name = "UpdateEmployeeStatusServlet", urlPatterns = {"/updateStatus"})
public class UpdateEmployeeStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            int newStatus = Integer.parseInt(request.getParameter("status"));

            UserDAO userDAO = new UserDAO();
            userDAO.updateSoftDeleteStatus(userId, newStatus);

        } catch (NumberFormatException e) {
            System.err.println("Lỗi tham số không hợp lệ.");
            e.printStackTrace();
        }
        
        // Lấy lại các tham số phân trang và tìm kiếm để quay về đúng trang
        String page = request.getParameter("page");
        String searchQuery = request.getParameter("searchQuery");
        
        StringBuilder redirectURL = new StringBuilder("listEmployee");
        if (page != null || searchQuery != null) {
            redirectURL.append("?");
        }
        if (page != null) {
            redirectURL.append("page=").append(page);
            if (searchQuery != null && !searchQuery.isEmpty()) {
                redirectURL.append("&");
            }
        }
        if (searchQuery != null && !searchQuery.isEmpty()) {
            redirectURL.append("searchQuery=").append(searchQuery);
        }
        
        response.sendRedirect(redirectURL.toString());
    }
}