package vn.edu.fpt.controller; // Hoặc package controller của bạn

import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet để xử lý việc hiển thị danh sách nhân viên.
 */
// Chỉ sửa đổi urlPatterns để khớp với link "/listEmployee" trong sidebar
@WebServlet(name = "ListAllEmployeesServlet", urlPatterns = {"/listEmployee"})
public class ListAllEmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDAO userDAO = new UserDAO();

        // Giữ nguyên tên phương thức gốc
        List<User> allEmployeesList = userDAO.getAllEmployeesRole();

        // Đặt danh sách vào request với tên là "employeeList" để JSP sử dụng
        request.setAttribute("employeeList", allEmployeesList);

        // Giữ nguyên đường dẫn file JSP 
        request.getRequestDispatcher("/jsp/admin/listAllEmployees.jsp").forward(request, response);
    }
}
