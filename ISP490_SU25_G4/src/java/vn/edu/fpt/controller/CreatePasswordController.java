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
 * @author ducanh
 */
@WebServlet(name = "CreatePasswordController", urlPatterns = {"/CreatePasswordController"})
public class CreatePasswordController extends HttpServlet {

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
            out.println("<title>Servlet CreatePasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CreatePasswordController at " + request.getContextPath() + "</h1>");
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
        // Khi người dùng vào trang bằng phương thức GET, chỉ cần hiển thị trang password.jsp
        request.getRequestDispatcher("password.jsp").forward(request, response);
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
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        String lastName = (String) session.getAttribute("lastName");
        String middleName = (String) session.getAttribute("middleName");
        String firstName = (String) session.getAttribute("firstName");

        if (email == null) {
            // Nếu không có thông tin trong session, chuyển về trang đăng ký
            response.sendRedirect("register.jsp");
            return;
        }

        // --- KIỂM TRA DỮ LIỆU ---
        if (!password.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu không khớp! Vui lòng nhập lại.");
            request.getRequestDispatcher("password.jsp").forward(request, response);
            return;
        }

        if (!isValidPassword(password)) {
            request.setAttribute("error", "Mật khẩu không đủ mạnh! (Tối thiểu 8 ký tự, có chữ hoa, thường và số).");
            request.getRequestDispatcher("password.jsp").forward(request, response);
            return;
        }

        // --- XỬ LÝ KHI THÀNH CÔNG ---
        try {
            // 1. Tạo người dùng trong cơ sở dữ liệu
            dao.createUser(lastName, middleName, firstName, email, password);

            // 2. Xóa các thuộc tính không cần thiết khỏi session
            session.removeAttribute("email");
            session.removeAttribute("lastName");
            session.removeAttribute("middleName");
            session.removeAttribute("firstName");

            // 3. Đặt thuộc tính "success" để gửi thông báo về cho JSP
            request.setAttribute("success", "Cập nhật mật khẩu thành công!");

            // 4. Forward về lại trang password.jsp để hiển thị thông báo
            request.getRequestDispatcher("password.jsp").forward(request, response);

        } catch (Exception e) {
            // Bắt các lỗi có thể xảy ra khi tương tác với database
            e.printStackTrace(); // In lỗi ra console để debug
            request.setAttribute("error", "Đã có lỗi xảy ra ở máy chủ. Vui lòng thử lại.");
            request.getRequestDispatcher("password.jsp").forward(request, response);
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return password.matches(".*[A-Z].*")
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
