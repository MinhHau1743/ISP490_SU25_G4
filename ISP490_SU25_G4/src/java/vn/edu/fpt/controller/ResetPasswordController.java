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
import vn.edu.fpt.dao.UserDAO;

/**
 *
 * @author NGUYEN MINH
 */
@WebServlet(name = "ResetPasswordController", urlPatterns = {"/ResetPasswordController"})
public class ResetPasswordController extends HttpServlet {

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
            out.println("<title>Servlet ResetPasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ResetPasswordController at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String param = request.getParameter("productController");

        if (email == null && param == null) {
            response.sendRedirect("forgotPassword.jsp"); // quay lại nếu chưa xác minh
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu không khớp!");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        if (!isValidPassword(newPassword)) {
            request.setAttribute("error", "Mật khẩu không đủ mạnh!");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        // --- XỬ LÝ KHI THÀNH CÔNG ---
        try {
            // 1. Tạo người dùng trong cơ sở dữ liệu
            dao.updatePassword(email, newPassword);

            // 3. Đặt thuộc tính "success" để gửi thông báo về cho JSP
            request.setAttribute("success", "Cập nhật mật khẩu thành công!");
            if (param != null) {
                boolean updated = dao.setRequireChangePasswordByEmail(email);
                if (updated) {
                    request.setAttribute("isProductController", true);
                }
            }
            session.removeAttribute("email");
            // 4. Forward về lại trang password.jsp để hiển thị thông báo
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);

        } catch (Exception e) {
            // Bắt các lỗi có thể xảy ra khi tương tác với database
            e.printStackTrace(); // In lỗi ra console để debug
            request.setAttribute("error", "Đã có lỗi xảy ra ở máy chủ. Vui lòng thử lại.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*");
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
