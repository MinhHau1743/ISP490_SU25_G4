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
        
        FeedbackDAO feedbackDAO = new FeedbackDAO();

        // SỬA 1: Lấy các tham số lọc và tìm kiếm từ request
        String query = request.getParameter("query");
        String ratingFilter = request.getParameter("ratingFilter");
        
        // Đặt giá trị mặc định cho bộ lọc nếu người dùng vào trang lần đầu
        if (ratingFilter == null || ratingFilter.trim().isEmpty()) {
            ratingFilter = "all";
        }

        // SỬA 2: Gọi phương thức DAO mới để lấy danh sách đã được lọc
        List<FeedbackView> feedbackList = feedbackDAO.getFilteredFeedback(query, ratingFilter);

        // Phần lấy các số liệu thống kê giữ nguyên
        int totalCount = feedbackDAO.getTotalFeedbackCount();
        int goodCount = feedbackDAO.getFeedbackCountByRatingRange(4, 5); // 4-5 sao
        int normalCount = feedbackDAO.getFeedbackCountByRatingRange(3, 3); // 3 sao
        int badCount = feedbackDAO.getFeedbackCountByRatingRange(1, 2);   // 1-2 sao

        // Gửi tất cả dữ liệu sang JSP
        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("goodCount", goodCount);
        request.setAttribute("normalCount", normalCount);
        request.setAttribute("badCount", badCount);
        
        // Đặt thuộc tính để menu active đúng
        request.setAttribute("activeMenu", "feedback");

        // Chuyển tiếp đến trang JSP
        request.getRequestDispatcher("/jsp/customerSupport/listFeedback.jsp").forward(request, response);
    }
}