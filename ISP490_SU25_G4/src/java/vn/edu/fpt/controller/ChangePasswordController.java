package vn.edu.fpt.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets; // <<=== THÊM IMPORT CHO StandardCharsets
import org.mindrot.jbcrypt.BCrypt;

import vn.edu.fpt.dao.DBContext;

@WebServlet(name = "changePassword", urlPatterns = {"/changePassword"})
public class ChangePasswordController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ChangePasswordController.class.getName());

    // Hàm để hash mật khẩu (ví dụ dùng SHA-256)
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển người dùng đến trang có form đổi mật khẩu
        // Đảm bảo người dùng đã đăng nhập trước khi cho phép vào trang này
        HttpSession session = request.getSession(false);
        String email = null;

        if (session != null) {
            email = (String) session.getAttribute("email");
        }

        if (session == null || email == null) {
            // Chưa đăng nhập hoặc session hết hạn
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        boolean responseCommitted = false; // biến cờ kiểm tra đã forward/redirect chưa

        HttpSession session = request.getSession(false);
        String email;

        // <<=== SỬA Ở ĐÂY: Xử lý logic session quan trọng
        if (session != null && request.getParameter("email") != null) {
            email = request.getParameter("email");
        } else {
            request.setAttribute("errorMessage", "Phiên làm việc đã hết hạn hoặc bạn chưa đăng nhập. Vui lòng đăng nhập lại.");
            try {
                request.getRequestDispatcher("/login.jsp").forward(request, response); // Giả sử login.jsp ở web root
                // Hoặc response.sendRedirect(request.getContextPath() + "/loginPageURL");
            } catch (IllegalStateException e) {
                LOGGER.log(Level.SEVERE, "Response already committed before forwarding to login from doPost changePassword.", e);
            }
            responseCommitted = true; // Đánh dấu đã xử lý response
            return; // Kết thúc xử lý
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        if (currentPassword == null || currentPassword.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ thông tin.");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            responseCommitted = true;
            return;
        }

        // TODO: Thêm các validate khác cho mật khẩu mới (độ dài, độ phức tạp,...)
        DBContext dbContext = new DBContext();
        Connection conn = null;
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        String message = null;
        String messageType = "errorMessage"; // Mặc định là lỗi

        try {
            // Kết nối đến cơ sở dữ liệu
            conn = dbContext.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu từ DBContext.");
            }

            // Tắt chế độ tự động commit để kiểm soát transaction
            conn.setAutoCommit(false);

            // Mã hoá mật khẩu hiện tại do người dùng nhập
            String hashedOldPasswordFromUser = hashPassword(currentPassword);
            String storedHashedPassword = null;

            // Lấy mật khẩu đã mã hoá từ cơ sở dữ liệu dựa trên email
            String sqlSelect = "SELECT password_hash FROM users WHERE email = ?";
            pstmtSelect = conn.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, email);
            rs = pstmtSelect.executeQuery();

            // Kiểm tra người dùng có tồn tại không
            if (rs.next()) {
                storedHashedPassword = rs.getString("password_hash");
            } else {
                conn.rollback(); // Hoàn tác nếu không tìm thấy người dùng
                request.setAttribute("errorMessage", "Người dùng không tồn tại.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            // Kiểm tra mật khẩu hiện tại có đúng không (dùng BCrypt để so sánh)
            if (!BCrypt.checkpw(currentPassword, storedHashedPassword)) {
                conn.rollback(); // Hoàn tác nếu mật khẩu sai
                request.setAttribute("currentPassword", currentPassword);
                request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            // Kiểm tra độ mạnh của mật khẩu mới
            if (!isValidPassword(confirmNewPassword)) {
                request.setAttribute("currentPassword", currentPassword);
                request.setAttribute("errorMessage", "Mật khẩu không đủ mạnh!");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                return;
            }

            // So sánh mật khẩu mới và mật khẩu xác nhận
            if (!newPassword.equals(confirmNewPassword)) {
                request.setAttribute("currentPassword", currentPassword);
                request.setAttribute("errorMessage", "Mật khẩu mới và xác nhận mật khẩu không khớp.");
                request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                responseCommitted = true;
                return;
            }

            // Nếu không có lỗi nào xảy ra thì tiến hành cập nhật mật khẩu
            if (message == null) {
                // Mã hoá mật khẩu mới
                String hashedNewPassword = hashPassword(newPassword);
                String sqlUpdate = "UPDATE users SET password_hash = ? WHERE email = ?";
                pstmtUpdate = conn.prepareStatement(sqlUpdate);
                pstmtUpdate.setString(1, hashedNewPassword);
                pstmtUpdate.setString(2, email);

                // Thực hiện cập nhật vào cơ sở dữ liệu
                int rowsAffected = pstmtUpdate.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit nếu cập nhật thành công
                    request.setAttribute("sucessfullyMessage", "Đổi mật khẩu thành công!");
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                } else {
                    conn.rollback(); // Hoàn tác nếu không cập nhật được
                    message = "Đổi mật khẩu thất bại. Không có thay đổi nào được thực hiện hoặc người dùng không được tìm thấy để cập nhật.";
                }
            }

        } catch (SQLException e) {
            // Bắt lỗi SQL và rollback nếu có lỗi
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException se) {
                LOGGER.log(Level.SEVERE, "Lỗi rollback khi SQLException", se);
            }
            LOGGER.log(Level.SEVERE, "Lỗi SQL: " + e.getMessage(), e); // Ghi log lỗi chi tiết
            message = "Lỗi hệ thống: Có lỗi xảy ra với cơ sở dữ liệu. ";

        } finally {
            // Đóng các tài nguyên liên quan đến cơ sở dữ liệu
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
                if (conn != null) {
                    // Khôi phục lại chế độ tự động commit nếu đã thay đổi
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đóng tài nguyên DB: ", e);
            }

            // Chỉ forward nếu response chưa được commit (ví dụ, chưa forward ở các đoạn return kiểm tra đầu vào)
            // và nếu có message để hiển thị (tức là có lỗi xảy ra hoặc thành công)
            if (!responseCommitted && message != null) {
                request.setAttribute(messageType, message);
                try {
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                } catch (IllegalStateException e) {
                    LOGGER.log(Level.SEVERE, "Response already committed before final forward in changePassword.", e);
                }
            } else if (!responseCommitted && message == null) {
                // Trường hợp không có lỗi nào xảy ra trong try-catch nhưng cũng không có message thành công
                // (ví dụ: logic code có vấn đề, không set message)
                // Hoặc có thể bạn muốn mặc định forward đến trang nào đó
                LOGGER.warning("No message set and response not committed in changePassword doPost. Forwarding to form.");
                request.setAttribute("errorMessage", "Đã có lỗi không xác định xảy ra. Vui lòng thử lại.");
                try {
                    request.getRequestDispatcher("changePassword.jsp").forward(request, response);
                } catch (IllegalStateException e) {
                    LOGGER.log(Level.SEVERE, "Response already committed before default error forward in changePassword.", e);
                }
            }
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*");
    }

    @Override
    public String getServletInfo() {
        return "Servlet để thay đổi mật khẩu người dùng, sử dụng DBContext";
    }
}
