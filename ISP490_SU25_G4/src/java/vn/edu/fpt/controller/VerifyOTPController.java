/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
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
import vn.edu.fpt.common.EmailUtil;

/**
 *
 * @author ducanh
 */
@WebServlet(name = "VerifyOTPController", urlPatterns = {"/VerifyOTPController"})
public class VerifyOTPController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet VerifyOTPController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet VerifyOTPController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Chỉ thực hiện khi người dùng thực sự muốn gửi lại mã
        if ("resend".equals(action)) {
            HttpSession session = request.getSession();
            String email = (String) session.getAttribute("email");

            // Đảm bảo email vẫn tồn tại trong session
            if (email != null && !email.isEmpty()) {
                try {
                    // === LOGIC TẠO VÀ GỬI LẠI OTP (Lấy từ code của bạn) ===
                    // 1. Tạo mã OTP mới
                    String newOtp = String.valueOf(new Random().nextInt(900000) + 100000);

                    // 2. Gửi mã mới qua email
                    EmailUtil.sendOTP(email, newOtp);

                    // 3. Cập nhật lại OTP và thời gian hết hạn trong session
                    session.setAttribute("otp", newOtp);
                    session.setAttribute("otpExpiresAt", LocalDateTime.now().plusMinutes(5));

                    // 4. Gửi thông báo tới người dùng
                    request.setAttribute("message", "Một mã OTP mới đã được gửi đến email của bạn!");

                } catch (Exception e) {
                    // Xử lý nếu có lỗi trong quá trình gửi email
                    e.printStackTrace();
                    request.setAttribute("error", "Không thể gửi lại mã OTP. Vui lòng thử lại sau.");
                }
            } else {
                // Trường hợp session hết hạn hoặc không có email
                request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng thực hiện lại từ đầu.");
                // Chuyển về trang đăng ký vì đó là nơi bắt đầu luồng
                response.sendRedirect("register.jsp");
                return; // Dừng xử lý để tránh forward ở cuối
            }
        }

        // Luôn chuyển người dùng đến trang xác minh OTP
        request.getRequestDispatcher("verifyOTP.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String otpInput = request.getParameter("otp");
        HttpSession session = request.getSession();
        String sessionOTP = (String) session.getAttribute("otp");
        LocalDateTime expiresAt = (LocalDateTime) session.getAttribute("otpExpiresAt");

        if (sessionOTP != null && sessionOTP.equals(otpInput) && LocalDateTime.now().isBefore(expiresAt)) {
            response.sendRedirect("password.jsp");
        } else {
            request.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
            request.getRequestDispatcher("verifyOTP.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
