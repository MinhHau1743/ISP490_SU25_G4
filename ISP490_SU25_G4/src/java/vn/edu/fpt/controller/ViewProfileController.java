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

/**
 *
 * @author minhh
 */
@WebServlet(name = "ViewProfileController", urlPatterns = {"/viewProfile"})
public class ViewProfileController extends HttpServlet {

    /**
     * Xử lý yêu cầu GET để hiển thị trang thông tin cá nhân của người dùng.
     *
     * @param request đối tượng servlet request
     * @param response đối tượng servlet response
     * @throws ServletException nếu có lỗi đặc trưng của servlet
     * @throws IOException nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy session hiện tại
        HttpSession session = request.getSession();

        // 2. Lấy đối tượng User đã được lưu trong session khi đăng nhập
        // Giả sử bạn lưu user với key là "user"
        User loggedInUser = (User) session.getAttribute("user");

        // --- THÊM CÁC DÒNG DEBUG VÀO ĐÂY ---
        System.out.println("\n--- DEBUGGING ViewProfileController ---");
        if (loggedInUser != null) {
            System.out.println("User trong session: " + loggedInUser); // In ra để xem toString() của User
            System.out.println("ID của user trong session: " + loggedInUser.getId());
        } else {
            System.out.println("User trong session là NULL.");
        }
        System.out.println("--- END DEBUGGING ---\n");
        // --- KẾT THÚC PHẦN DEBUG ---

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 3. Kiểm tra xem người dùng đã đăng nhập chưa
        if (loggedInUser == null) {
            // Nếu chưa, chuyển hướng về trang đăng nhập
            response.sendRedirect("login.jsp");
            return; // Dừng thực thi
        }

        // 4. Khởi tạo UserDAO để truy vấn cơ sở dữ liệu
        UserDAO userDAO = new UserDAO();

        // 5. Gọi phương thức để lấy thông tin chi tiết nhất của người dùng từ DB
        // Phương thức này sẽ trả về một đối tượng User "phẳng" đã chứa đủ thông tin
        User userProfile = userDAO.getUserById(loggedInUser.getId());

        // 6. Xử lý kết quả trả về từ DAO
        if (userProfile != null) {
            // Nếu tìm thấy user, đặt đối tượng userProfile vào request scope
            request.setAttribute("profile", userProfile);

            // 7. Chuyển tiếp (forward) yêu cầu đến trang viewProfile.jsp để hiển thị
            request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
        } else {
            // Xử lý trường hợp hiếm gặp: user có trong session nhưng đã bị xóa khỏi DB
            // Hủy session cũ và thông báo lỗi
            session.invalidate();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>Lỗi: Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.</h1>");
            response.getWriter().println("<a href='login.jsp'>Quay về trang đăng nhập</a>");
        }
    }

    /**
     * Xử lý yêu cầu POST bằng cách gọi lại doGet. Điều này hữu ích nếu có một
     * form nào đó POST đến URL này để xem profile.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Trả về mô tả ngắn của servlet.
     */
    @Override
    public String getServletInfo() {
        return "Servlet for viewing user profile details.";
    }
}
