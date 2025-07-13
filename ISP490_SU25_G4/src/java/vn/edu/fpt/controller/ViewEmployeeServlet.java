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
@WebServlet(name = "ViewEmployeeServlet", urlPatterns = {"/viewEmployee"})
public class ViewEmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        try {
            int employeeId = Integer.parseInt(idStr);
            UserDAO userDAO = new UserDAO();

            // Giả sử bạn có hàm getUserById trong UserDAO
            User employee = userDAO.getUserById(employeeId);

            if (employee != null) {
                // Đặt đối tượng employee vào request, JSP sẽ dùng tên "employee"
                request.setAttribute("employee", employee);

                // Chuyển đến trang JSP xem chi tiết
                request.getRequestDispatcher("/jsp/admin/viewEmployee.jsp").forward(request, response);
            } else {
                response.getWriter().println("Không tìm thấy nhân viên.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi (ví dụ: chuyển hướng về trang danh sách)
            response.sendRedirect(request.getContextPath() + "/listEmployee?error=true");
        }
    }

}
