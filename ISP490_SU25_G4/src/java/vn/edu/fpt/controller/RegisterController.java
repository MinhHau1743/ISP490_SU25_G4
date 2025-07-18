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
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.common.EmailUtil;

/**
 *
 * @author ducanh
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/RegisterController"})
public class RegisterController extends HttpServlet {

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
            out.println("<title>Servlet RegisterController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        UserDAO dao = new UserDAO();
        String email = request.getParameter("email");

        // Lấy giá trị của checkbox "terms"
        String terms = request.getParameter("terms");

        // === VALIDATE CHECKBOX ===
        // Nếu checkbox không được tick, tham số 'terms' sẽ là null.
        if (terms == null) {
            request.setAttribute("error", "Bạn phải đồng ý với điều khoản dịch vụ để đăng ký.");
            // Giữ lại email người dùng đã nhập để họ không cần nhập lại
            request.setAttribute("email", email);
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return; // Dừng xử lý
        }

        if (dao.emailExists(email)) {
            request.setAttribute("error", "Email đã được đăng kí");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        HttpSession session = request.getSession();
        session.setAttribute("email", email);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiresAt", LocalDateTime.now().plusMinutes(5));

        EmailUtil.sendOTP(email, otp);
        response.sendRedirect("verifyOTP.jsp");
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
