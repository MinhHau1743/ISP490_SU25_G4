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

/**
 *
 * @author NGUYEN MINH
 */
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.Feedback;
import vn.edu.fpt.model.FeedbackView;
import vn.edu.fpt.model.InternalNote;
import vn.edu.fpt.model.TechnicalRequest;

import java.io.IOException;
import java.util.List;

// CORE CHANGE 1: A single servlet with a single URL pattern
@WebServlet(name = "FeedbackController", urlPatterns = {"/feedback"})
public class FeedbackController extends HttpServlet {

    /**
     * Handles all GET requests and routes them based on the 'action' parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORE CHANGE 2: The "Router" for GET requests
        String action = request.getParameter("action");
        
        // Default to the list view if no action is specified
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "view":
                    doView(request, response);
                    break;
                case "create":
                    doCreateForm(request, response);
                    break;
                    
                case "list":
                default:
                    doList(request, response);
                    break;
            }
        } catch (Exception e) {
            // Generic error handler for any uncaught exceptions in the action methods
            e.printStackTrace(); // Log the error for debugging
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    /**
     * Handles all POST requests, primarily for form submissions.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    // Lấy action từ request
    String action = request.getParameter("action");

    // Mặc định là chuỗi rỗng để tránh NullPointerException
    if (action == null) {
        action = ""; 
    }

    try {
        // Sử dụng switch để định tuyến cho các action của POST
        switch (action) {
            case "create":
                doCreateSubmit(request, response);
                break;
            
            // ✔️ THÊM CASE NÀY VÀO
            case "addNote":
                doAddNote(request, response);
                break;

            default:
                // Xử lý các action không hợp lệ cho POST
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for POST request.");
                break;
        }
    } catch (Exception e) {
        // Xử lý lỗi chung cho các hành động POST
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
    }
    }
    
    // ----------------------------------------------------------------
    // PRIVATE METHODS FOR EACH ACTION
    // ----------------------------------------------------------------

    /**
     * ACTION: Displays a list of all feedbacks with filtering.
     * (Logic from the old ListFeedbackController)
     */
    private void doList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        FeedbackDAO feedbackDAO = new FeedbackDAO();

        String query = request.getParameter("query");
        String ratingFilter = request.getParameter("ratingFilter");
        
        if (ratingFilter == null || ratingFilter.trim().isEmpty()) {
            ratingFilter = "all";
        }

        List<FeedbackView> feedbackList = feedbackDAO.getFilteredFeedback(query, ratingFilter);

        int totalCount = feedbackDAO.getTotalFeedbackCount();
        int goodCount = feedbackDAO.getFeedbackCountByRatingRange(4, 5);
        int normalCount = feedbackDAO.getFeedbackCountByRatingRange(3, 3);
        int badCount = feedbackDAO.getFeedbackCountByRatingRange(1, 2);

        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("goodCount", goodCount);
        request.setAttribute("normalCount", normalCount);
        request.setAttribute("badCount", badCount);
        request.setAttribute("activeMenu", "feedback"); // For UI highlighting

        request.getRequestDispatcher("/jsp/customerSupport/listFeedback.jsp").forward(request, response);
    }

    /**
     * ACTION: Displays details for a single feedback item.
     * (Logic from the old ViewFeedbackController)
     */
    private void doView(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        
        // CORE CHANGE 4: Update redirect URLs to use the new unified pattern
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("feedback?action=list&error=missingId");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            FeedbackView feedback = feedbackDAO.getFeedbackById(id);

            if (feedback != null) {
                List<InternalNote> notes = feedbackDAO.getNotesByFeedbackId(id);
                request.setAttribute("internalNotes", notes);
                request.setAttribute("feedback", feedback);
                request.getRequestDispatcher("/jsp/customerSupport/viewFeedback.jsp").forward(request, response);
            } else {
                response.sendRedirect("feedback?action=list&error=notFound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("feedback?action=list&error=invalidId");
        }
    }

    /**
     * ACTION: Shows the form to create a new feedback.
     * (Logic from the old CreateFeedbackController's doGet)
     */
    private void doCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("technicalRequestId");
        if (idParam == null || idParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing technicalRequestId.");
            return;
        }

        try {
            int technicalRequestId = Integer.parseInt(idParam);
            TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
            
            // Dòng này có thể ném ra SQLException
            TechnicalRequest technicalRequest = technicalDAO.getTechnicalRequestById(technicalRequestId);

            if (technicalRequest == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Technical request not found.");
                return;
            }
            
            request.setAttribute("technicalRequest", technicalRequest);
            request.getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // Bắt lỗi nếu idParam không phải là số
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid technicalRequestId format.");
        } catch (java.sql.SQLException e) {
            // ✔️ THÊM KHỐI CATCH NÀY ĐỂ XỬ LÝ LỖI DATABASE
            // Bắt lỗi nếu có sự cố khi truy vấn cơ sở dữ liệu
            e.printStackTrace(); // In lỗi ra console cho lập trình viên xem
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "A database error occurred.");
        }
    }

    /**
     * ACTION: Processes the submission of the new feedback form.
     * (Logic from the old CreateFeedbackController's doPost)
     */
    private void doCreateSubmit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String ratingParam = request.getParameter("rating");
            String comment = request.getParameter("comment");
            String enterpriseIdParam = request.getParameter("enterpriseId");
            String technicalRequestIdParam = request.getParameter("technicalRequestId");

            if (ratingParam == null || ratingParam.equals("0")) {
                request.setAttribute("errorMessage", "Please select a rating.");
                // We must call the form display method again to show the error
                doCreateForm(request, response); 
                return;
            }

            int rating = Integer.parseInt(ratingParam);
            int enterpriseId = Integer.parseInt(enterpriseIdParam);
            int technicalRequestId = Integer.parseInt(technicalRequestIdParam);

            Feedback newFeedback = new Feedback();
            newFeedback.setRating(rating);
            newFeedback.setComment(comment);
            newFeedback.setEnterpriseId(enterpriseId);
            newFeedback.setTechnicalRequestId(technicalRequestId);
            newFeedback.setAppointmentId(null);

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            boolean success = feedbackDAO.addFeedback(newFeedback);

            if (success) {
                // Redirect to a confirmation page or list view
                response.sendRedirect(request.getContextPath() + "/ticket?action=list&feedback=success");
            } else {
                request.setAttribute("errorMessage", "Error saving to the database. Please try again.");
                doCreateForm(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            // Call the GET handler to reload the form with necessary data
            doCreateForm(request, response);
        }
    }
    private void doAddNote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
            String noteText = request.getParameter("noteText");
            
            // Lấy thông tin người dùng từ session
            HttpSession session = request.getSession();
            // User loggedInUser = (User) session.getAttribute("user");
            
            // !! LƯU Ý: Dòng dưới đây là giả định. 
            // Bạn cần thay thế bằng cách lấy ID người dùng thật từ session.
            // Ví dụ: int userId = loggedInUser.getId();
            int userId = 1; 

            if (noteText == null || noteText.trim().isEmpty()) {
                // Nếu text rỗng, không làm gì, chỉ redirect lại
                response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteError=empty");
                return;
            }

            InternalNote note = new InternalNote();
            note.setFeedbackId(feedbackId);
            note.setUserId(userId);
            note.setNoteText(noteText);

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            feedbackDAO.addInternalNote(note);

            // Sau khi thêm thành công, redirect lại trang chi tiết feedback
            response.sendRedirect("feedback?action=view&id=" + feedbackId);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not add note.");
        }
    }
}