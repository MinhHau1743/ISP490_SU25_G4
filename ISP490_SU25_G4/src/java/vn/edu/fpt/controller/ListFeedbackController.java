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

// SỬA 1: Đổi tên class và urlPatterns
@WebServlet(name = "ListFeedbackController", urlPatterns = {"/listFeedback"})
public class ListFeedbackController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Không cần tham số "action" nữa vì controller này chỉ làm một việc
        listFeedbacks(request, response);
    }

    private void listFeedbacks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        FeedbackDAO feedbackDAO = new FeedbackDAO();

        // 1. Lấy danh sách feedback
        List<FeedbackView> feedbackList = feedbackDAO.getAllFeedback();

        // 2. Lấy các số liệu thống kê
        int totalCount = feedbackDAO.getTotalFeedbackCount();
        int goodCount = feedbackDAO.getFeedbackCountByRatingRange(4, 5); // 4-5 sao
        int normalCount = feedbackDAO.getFeedbackCountByRatingRange(3, 3); // 3 sao
        int badCount = feedbackDAO.getFeedbackCountByRatingRange(1, 2);   // 1-2 sao

        // 3. Gửi tất cả dữ liệu sang JSP
        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("goodCount", goodCount);
        request.setAttribute("normalCount", normalCount);
        request.setAttribute("badCount", badCount);
        
        // Đặt thuộc tính để menu active đúng
        request.setAttribute("activeMenu", "feedback");

        // 4. Chuyển tiếp đến trang JSP
        request.getRequestDispatcher("/jsp/customerSupport/listFeedback.jsp").forward(request, response);
    }
}
