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
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.model.FeedbackView;

import java.io.IOException;
import java.util.List;
import vn.edu.fpt.model.InternalNote;

@WebServlet(name = "ViewFeedbackController", urlPatterns = {"/viewFeedback"})
public class ViewFeedbackController extends HttpServlet {

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String idParam = request.getParameter("id");

    if (idParam == null || idParam.trim().isEmpty()) {
        response.sendRedirect("listFeedback?error=missingId");
        return;
    }

    try {
        int id = Integer.parseInt(idParam);
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        FeedbackView feedback = feedbackDAO.getFeedbackById(id);

        // Chỉ kiểm tra 1 lần duy nhất
        if (feedback != null) {
            // Lấy danh sách ghi chú
            List<InternalNote> notes = feedbackDAO.getNotesByFeedbackId(id);
            
            // Gửi tất cả dữ liệu cần thiết sang JSP
            request.setAttribute("internalNotes", notes);
            request.setAttribute("feedback", feedback);
            
            // Forward MỘT LẦN DUY NHẤT
            request.getRequestDispatcher("/jsp/customerSupport/viewFeedback.jsp").forward(request, response);
        } else {
            // Nếu không tìm thấy, chuyển hướng
            response.sendRedirect("listFeedback?error=notFound");
        }
        
    } catch (NumberFormatException e) {
        response.sendRedirect("listFeedback?error=invalidId");
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("listFeedback?error=serverError");
    }
}
}
