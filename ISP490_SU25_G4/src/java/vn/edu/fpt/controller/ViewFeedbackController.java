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

@WebServlet(name = "ViewFeedbackController", urlPatterns = {"/viewFeedback"})
public class ViewFeedbackController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy ID của feedback từ URL
            int id = Integer.parseInt(request.getParameter("id"));
            
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            FeedbackView feedback = feedbackDAO.getFeedbackById(id);

            if (feedback != null) {
                request.setAttribute("feedback", feedback);
                request.getRequestDispatcher("/jsp/customerSupport/viewFeedback.jsp").forward(request, response);
            } else {
                // Nếu không tìm thấy, quay về trang danh sách với thông báo lỗi
                response.sendRedirect("listFeedback?error=notFound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi (ví dụ ID không hợp lệ), quay về trang danh sách
            response.sendRedirect("listFeedback?error=invalidId");
        }
    }
}