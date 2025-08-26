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
 * dùng. Bao gồm: Đăng nhập, Đăng xuất, Quên mật khẩu, Xác thực OTP, Đặt lại mật
 * khẩu, và Đổi mật khẩu (khi đã đăng nhập). URL: /auth
 */
@WebServlet(name = "AuthenticationController", urlPatterns = {"/auth"})
public class AuthenticationController extends HttpServlet {

    private final UserDAO userDao;

    /**
     * Constructor mặc định, được server (Tomcat) sử dụng khi chạy ứng dụng thật.
     * Nó tự khởi tạo một UserDAO thật để kết nối vào CSDL.
     */
    public AuthenticationController() {
        this.userDao = new UserDAO();
    }

    /**
     * Constructor dùng cho Unit Test.
     * Nó cho phép chúng ta "tiêm" (inject) một UserDAO giả (mock) từ bên ngoài.
     */
    public AuthenticationController(UserDAO userDao) {
        this.userDao = userDao;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        switch (action) {
            case "logout":
                handleLogout(request, response);
                break;
            case "resendOTP":
                handleResendOTP(request, response);
                break;
            case "changePassword":
                handleShowChangePassword(request, response);
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
        }
    }

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
            case "changePassword":
                handleChangePassword(request, response);
                break;
            default:
                response.sendRedirect("login.jsp");
                break;
        }
    }

    // =========================================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ (HANDLER METHODS)
    // =========================================================================
    
    private void handleShowChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String email = null;
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                email = user.getEmail();
            }
        }
        if (session == null || email == null) {
            // Chưa đăng nhập hoặc session hết hạn
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // Nếu cần truyền email sang trang đổi mật khẩu
        request.setAttribute("email", email);
        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        User user = this.userDao.login(email, password);
        
        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userRole", user.getRoleName());
            session.setAttribute("userId", user.getId());
            if (user.isRequireChangePassword() == 1) {
                session.setAttribute("email", email);
                response.sendRedirect("resetPassword.jsp");
            } else {
                response.sendRedirect("dashboard");
            }
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("login.jsp");
    }

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            request.setAttribute("error", "Email không hợp lệ hoặc bị bỏ trống.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }
        
        if (!this.userDao.emailExists(email)) {
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
            request.setAttribute("error", "Không gửi được email. Vui lòng thử lại sau.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
        }
    }

    private void handleVerifyOTP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void handleResendOTP(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp?error=session_expired");
            return;
        }
        String email = (String) session.getAttribute("email");
        if (email == null) {
            request.setAttribute("error", "Phiên của bạn đã hết hạn. Vui lòng thử lại từ đầu.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
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
        
        boolean updated = this.userDao.updatePassword(email, newPassword);
        
        if (updated) {
            User loggedInUser = (User) session.getAttribute("user");
            if (loggedInUser != null) {
                this.userDao.setRequireChangePasswordFlag(email, 0);
                session.removeAttribute("email");
                response.sendRedirect("dashboard");
            } else {
                session.invalidate();
                request.setAttribute("success", "Cập nhật mật khẩu thành công! Vui lòng đăng nhập lại.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Đã có lỗi xảy ra phía máy chủ. Vui lòng thử lại.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String email = request.getParameter("email");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmNewPassword");

        if (!this.userDao.checkPassword(email, currentPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }
        if (newPassword == null || newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu mới không khớp!");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }
        if (!isValidPassword(newPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu mới không đủ mạnh.");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }

        boolean updated = this.userDao.updatePassword(email, newPassword);
        if (updated) {
            request.setAttribute("sucessfullyMessage", "Đổi mật khẩu thành công!");
        } else {
            request.setAttribute("errorMessage", "Có lỗi xảy ra, không thể cập nhật mật khẩu.");
        }
        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*");
    }

    @Override
    public String getServletInfo() {
        return "Central Authentication Controller";
    }
}