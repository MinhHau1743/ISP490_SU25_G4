package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;
import vn.edu.fpt.common.EmailUtil;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;

/**
 * Controller trung tâm xử lý tất cả các hành động liên quan đến xác thực người
 * dùng. Bao gồm: Đăng nhập, Đăng xuất, Quên mật khẩu, Xác thực OTP, và Đặt lại
 * mật khẩu. URL: /auth
 */
@WebServlet(name = "AuthenticationController", urlPatterns = {"/auth"})
public class AuthenticationController extends HttpServlet {

    // Regex cơ bản để kiểm tra định dạng email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    /**
     * Xử lý các yêu cầu GET. Chủ yếu cho các hành động không thay đổi dữ liệu
     * như đăng xuất, gửi lại mã OTP, hoặc hiển thị các trang.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("login.jsp"); // Mặc định về trang login
            return;
        }

        switch (action) {
            case "logout":
                handleLogout(request, response);
                break;
            case "resendOTP":
                handleResendOTP(request, response);
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
        }
    }

    /**
     * Xử lý các yêu cầu POST. Chủ yếu cho các hành động gửi form và thay đổi dữ
     * liệu.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        switch (action) {
            case "login":
                handleLogin(request, response);
                break;
            case "forgotPassword":
                handleForgotPassword(request, response);
                break;
            case "verifyOTP":
                handleVerifyOTP(request, response);
                break;
            case "resetPassword":
                handleResetPassword(request, response);
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
        }
    }

    // =========================================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ (HANDLER METHODS)
    // =========================================================================
    /**
     * Xử lý đăng nhập.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(email, password);

        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            if (user.isRequireChangePassword() == 1) {
                session.setAttribute("email", email); // Cần email cho việc đổi mật khẩu bắt buộc
                response.sendRedirect("resetPassword.jsp");
            } else {
                response.sendRedirect("dashboard.jsp"); // Chuyển đến trang dashboard sau khi đăng nhập thành công
            }
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * Xử lý đăng xuất.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("login.jsp");
    }

    /**
     * Xử lý yêu cầu quên mật khẩu (bước 1).
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Định dạng email không hợp lệ.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        if (!dao.emailExists(email)) {
            request.setAttribute("error", "Email không tồn tại trong hệ thống.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiresAt", LocalDateTime.now().plusMinutes(5));

        try {
            EmailUtil.sendOTP(email, otp);
            response.sendRedirect("verifyOTP.jsp");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Không gửi được email xác thực. Vui lòng thử lại sau.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
        }
    }

    /**
     * Xử lý xác thực mã OTP (bước 2).
     */
    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String otpInput = request.getParameter("otp");
        HttpSession session = request.getSession();

        if (otpInput == null || !otpInput.matches("\\d{6}")) {
            request.setAttribute("error", "Mã OTP phải là 6 chữ số.");
            request.getRequestDispatcher("verifyOTP.jsp").forward(request, response);
            return;
        }

        String sessionOTP = (String) session.getAttribute("otp");
        LocalDateTime expiresAt = (LocalDateTime) session.getAttribute("otpExpiresAt");

        if (sessionOTP != null && sessionOTP.equals(otpInput) && expiresAt != null && LocalDateTime.now().isBefore(expiresAt)) {
            response.sendRedirect("resetPassword.jsp");
        } else {
            request.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
            request.getRequestDispatcher("verifyOTP.jsp").forward(request, response);
        }
    }

    /**
     * Xử lý gửi lại mã OTP.
     */
    private void handleResendOTP(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (email != null && !email.isEmpty()) {
            try {
                String newOtp = String.valueOf(new Random().nextInt(900000) + 100000);
                EmailUtil.sendOTP(email, newOtp);

                session.setAttribute("otp", newOtp);
                session.setAttribute("otpExpiresAt", LocalDateTime.now().plusMinutes(5));
                request.setAttribute("message", "Một mã OTP mới đã được gửi đến email của bạn!");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Không thể gửi lại mã OTP. Vui lòng thử lại sau.");
            }
            request.getRequestDispatcher("verifyOTP.jsp").forward(request, response);
        } else {
            response.sendRedirect("forgotPassword.jsp");
        }
    }

    /**
     * Xử lý đặt lại mật khẩu mới (bước 3).
     */
 
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Kiểm tra session có tồn tại không
        if (session == null) {
            response.sendRedirect("login.jsp?error=session_expired");
            return;
        }

        String email = (String) session.getAttribute("email");

        // Kiểm tra xem email có tồn tại trong session không (bắt buộc cho cả 2 luồng)
        if (email == null) {
            request.setAttribute("error", "Phiên của bạn đã hết hạn. Vui lòng thử lại từ đầu.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // --- Validation mật khẩu ---
        if (newPassword == null || newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu không khớp hoặc bị bỏ trống!");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }
        if (!isValidPassword(newPassword)) {
            request.setAttribute("error", "Mật khẩu phải dài ít nhất 8 ký tự, chứa chữ hoa, chữ thường và số.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        boolean updated = dao.updatePassword(email, newPassword);

        if (updated) {
            // KIỂM TRA XEM ĐÂY LÀ LUỒNG NÀO
            User loggedInUser = (User) session.getAttribute("user");

            if (loggedInUser != null) {
                // === LUỒNG 1: NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP (ĐỔI MK LẦN ĐẦU) ===
                // Cập nhật lại cờ require_change_password trong DB
                dao.setRequireChangePasswordFlag(email, 0);

                // Dọn dẹp session
                session.removeAttribute("email"); // Chỉ xóa email, giữ lại "user"

                // Chuyển thẳng đến trang dashboard
                response.sendRedirect("dashboard.jsp");

            } else {
                // === LUỒNG 2: NGƯỜI DÙNG CHƯA ĐĂNG NHẬP (QUÊN MẬT KHẨU) ===
                // Dọn dẹp session
                session.removeAttribute("otp");
                session.removeAttribute("otpExpiresAt");
                session.removeAttribute("email");

                // Yêu cầu đăng nhập lại với mật khẩu mới
                request.setAttribute("success", "Cập nhật mật khẩu thành công! Vui lòng đăng nhập lại.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } else {
            request.setAttribute("error", "Đã có lỗi xảy ra phía máy chủ. Vui lòng thử lại.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }

    // =========================================================================
    // PHƯƠNG THỨC HỖ TRỢ (HELPER UTILITIES)
    // =========================================================================
    /**
     * Kiểm tra độ mạnh của mật khẩu.
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*");
    }

    @Override
    public String getServletInfo() {
        return "Central Authentication Controller";
    }
}
