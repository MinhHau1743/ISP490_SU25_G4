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
import vn.edu.fpt.model.User;

/**
 *
 * @author minhh
 */
@WebServlet(name = "ViewProfileController", urlPatterns = {"/viewProfile"})
public class ViewProfileController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
         // --- BẮT ĐẦU ĐOẠN CODE KIỂM TRA SESSION ---
    HttpSession session = request.getSession();

    // THÊM DÒNG NÀY VÀO
    System.out.println("\n1. === TRONG ViewProfileController (doGet) ===");
    System.out.println("User trong session là: " + session.getAttribute("user"));
        // 1. Lấy user đang đăng nhập từ session
//        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user"); // Giả sử khi đăng nhập bạn đã lưu user vào session với key là "account"

        // 2. Kiểm tra xem người dùng đã đăng nhập chưa
        if (loggedInUser == null) {
            // Nếu chưa, chuyển hướng về trang đăng nhập
            response.sendRedirect("login.jsp");
            return; // Dừng việc thực thi tiếp theo
    }

        // 3. Gọi DAO để lấy thông tin chi tiết nhất của user từ DB
        UserDAO userDAO = new UserDAO();
        User userProfile = userDAO.getUserById(loggedInUser.getId());
   
        // 4. Gửi đối tượng user (userProfile) sang cho trang JSP
        if (userProfile != null) {
            // Đặt đối tượng vào request attribute với tên là "profile"
            request.setAttribute("profile", userProfile);
            
            // 5. Chuyển tiếp (forward) yêu cầu đến trang viewProfile.jsp để hiển thị
            request.getRequestDispatcher("viewProfile.jsp").forward(request, response);
        } else {
            // Xử lý trường hợp hiếm gặp: user có trong session nhưng không có trong DB
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h1>Lỗi: Không tìm thấy thông tin người dùng.</h1>");
        }
    }

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
