package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Random;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.common.EmailUtil;

@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/ForgotPasswordController"})
public class ForgotPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển về trang quên mật khẩu
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        UserDAO dao = new UserDAO();

        // Kiểm tra email nhập vào
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email có tồn tại trong hệ thống không
        if (!dao.emailExists(email)) {
            request.setAttribute("error", "Email không tồn tại.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        // Sinh OTP 6 số
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        // Lưu thông tin vào session
        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiresAt", LocalDateTime.now().plusMinutes(5));

        // Gửi OTP qua email, xử lý lỗi gửi mail
        try {
            EmailUtil.sendOTP(email, otp);
            response.sendRedirect("verifyForgotPassword.jsp");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Không gửi được email xác thực. Vui lòng thử lại sau.");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Forgot password controller";
    }
}
