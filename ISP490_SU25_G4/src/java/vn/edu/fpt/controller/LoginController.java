package vn.edu.fpt.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to login page
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy dữ liệu từ form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Gọi DAO xử lý đăng nhập
        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null) {
            // Đăng nhập thành công
            HttpSession session = request.getSession(true);

            // Lưu thông tin người dùng vào session
            session.setAttribute("user", user);
            session.setAttribute("email", email);                      // ✔ Email đăng nhập
            session.setAttribute("userID", user.getId());             // ✔ ID người dùng
            session.setAttribute("userRole", user.getRoleName());     // ✔ Vai trò người dùng
            session.setAttribute("userName", user.getFullNameCombined());     // ✔ Tên đầy đủ

            // Kiểm tra có bắt đổi mật khẩu hay không
            int requireChange = user.isRequireChangePassword(); // giả sử trả về 0/1
            if (requireChange == 1) {
                // Lần đầu đăng nhập → chuyển đến trang đổi mật khẩu
                session.setAttribute("fromLogin", true); // dùng flag nếu cần
                response.sendRedirect("resetPassword.jsp");
            } else {
                // Đăng nhập bình thường
                // Trong LoginServlet, sau khi xác thực thành công
                response.sendRedirect("dashboard"); // Chuyển hướng đến /dashboard, KHÔNG phải dashboard.jsp
            }
        } else {
            // Sai email hoặc mật khẩu
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý đăng nhập";
    }
}
