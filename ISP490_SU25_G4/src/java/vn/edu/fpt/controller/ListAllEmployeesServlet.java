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
 * Servlet để xử lý việc hiển thị danh sách nhân viên Chăm sóc khách hàng.
 */
// Đặt một URL rõ ràng cho servlet này
@WebServlet(name = "ListAllEmployeesServlet", urlPatterns = {"/admin/employees/list"})
public class ListAllEmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();

        // Gọi phương thức mới để lấy TẤT CẢ nhân viên
        List<User> allEmployeesList = userDAO.getAllEmployeesRole();

        // Đặt danh sách vào request với tên là "employeeList" để JSP sử dụng
        request.setAttribute("employeeList", allEmployeesList);

        // Chuyển tiếp tới một file JSP chung (bạn sẽ tạo ở bước 3)
        request.getRequestDispatcher("/jsp/admin/listAllEmployees.jsp").forward(request, response);
    }
}