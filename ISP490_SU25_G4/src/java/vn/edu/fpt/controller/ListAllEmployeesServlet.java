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
@WebServlet(name = "ListEmployeeCskhServlet", urlPatterns = {"/admin/employees/cskh"})
public class ListAllEmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UserDAO userDAO = new UserDAO();
        
        // Lấy danh sách nhân viên từ CSDL với tên vai trò chính xác
        List<User> cskhEmployeeList = userDAO.getUsersByRoleName("Chăm sóc khách hàng");
        
        // **QUAN TRỌNG**: Đặt danh sách vào request với tên là "employeeList"
        // để khớp với file JSP.
        request.setAttribute("employeeList", cskhEmployeeList);
        
        // Chuyển tiếp tới file JSP trong thư mục /view/admin/
        // Hãy chắc chắn đường dẫn này đúng với cấu trúc thư mục của bạn
        request.getRequestDispatcher("/jsp/admin/listEmployeeCustomer.jsp").forward(request, response);
    }
}