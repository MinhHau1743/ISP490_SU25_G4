/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Cần dùng session
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.model.InternalNote;
// import vn.edu.fpt.model.User; // Import model User của bạn

import java.io.IOException;

@WebServlet(name = "AddInternalNoteController", urlPatterns = {"/addNote"})
public class AddInternalNoteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy thông tin từ form
        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
        String noteText = request.getParameter("noteText");
        
        // Lấy thông tin người dùng từ session
        HttpSession session = request.getSession();
        // User user = (User) session.getAttribute("user"); // Giả sử bạn lưu user trong session
        
        // !! LƯU Ý QUAN TRỌNG:
        // Dòng code dưới đây chỉ là giả định. Bạn cần thay thế bằng cách lấy user ID thật
        // từ session sau khi người dùng đăng nhập.
        int userId = 1; // << THAY THẾ BẰNG user.getId() 

        if (noteText != null && !noteText.trim().isEmpty()) {
            InternalNote note = new InternalNote();
            note.setFeedbackId(feedbackId);
            note.setNoteText(noteText);
            note.setUserId(userId); 

            FeedbackDAO dao = new FeedbackDAO();
            dao.addInternalNote(note);
        }

        // Chuyển hướng người dùng trở lại trang chi tiết feedback
        response.sendRedirect("viewFeedback?id=" + feedbackId);
    }
}