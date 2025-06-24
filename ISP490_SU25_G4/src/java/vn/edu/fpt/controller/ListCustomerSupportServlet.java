/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;

/**
 *
 * @author minhh
 */
@WebServlet(name = "ListCustomerSupportServlet", urlPatterns = {"/ListCustomerSupportServlet"})
public class ListCustomerSupportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
          UserDAO userDAO = new UserDAO();
        // Sau khi thêm import, dòng này sẽ hết báo lỗi
        List<User> cskhList = userDAO.getUsersByRoleName("Chăm sóc khách hàng");
        
        request.setAttribute("listUsers", cskhList);
        request.getRequestDispatcher("/listEmployeeCustomer.jsp").forward(request, response);
    }
    }

