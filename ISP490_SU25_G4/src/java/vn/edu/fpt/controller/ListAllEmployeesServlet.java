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
 * Servlet để xử lý việc hiển thị danh sách nhân viên, hỗ trợ tìm kiếm và phân
 * trang.
 */
// URL pattern khớp với action của form tìm kiếm và link sidebar
@WebServlet(name = "ListAllEmployeesServlet", urlPatterns = {"/listEmployee"})
public class ListAllEmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Hỗ trợ tiếng Việt cho tham số tìm kiếm
        request.setCharacterEncoding("UTF-8");

        UserDAO userDAO = new UserDAO();
        List<User> employeeList;

        // 1. Lấy các tham số từ request (tìm kiếm & phân trang)
        String searchQuery = request.getParameter("searchQuery");
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");

        // 2. Thiết lập giá trị mặc định cho phân trang
        int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
        int pageSize = (sizeStr == null || sizeStr.isEmpty()) ? 12 : Integer.parseInt(sizeStr); // Mặc định 12 nhân viên/trang

        int totalEmployees;

        // 3. Kiểm tra xem người dùng có đang tìm kiếm hay không
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // Chế độ TÌM KIẾM
            employeeList = userDAO.searchEmployeesByName(searchQuery, page, pageSize);
            totalEmployees = userDAO.countSearchedEmployees(searchQuery);
            // Gửi lại từ khóa tìm kiếm để hiển thị trên ô input
            request.setAttribute("searchQuery", searchQuery);
        } else {
            // Chế độ XEM TẤT CẢ (mặc định)
            employeeList = userDAO.getAllEmployeesPaginated(page, pageSize);
            totalEmployees = userDAO.getTotalEmployeeCount();
        }

        // 4. Tính toán tổng số trang
        int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);
        if (totalPages == 0) {
            totalPages = 1; // Đảm bảo luôn có ít nhất 1 trang
        }

        // 5. Đặt các thuộc tính vào request để JSP sử dụng
        request.setAttribute("employeeList", employeeList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);

        // 6. Chuyển tiếp đến trang JSP
        request.getRequestDispatcher("/jsp/admin/listAllEmployees.jsp").forward(request, response);
    }
}
