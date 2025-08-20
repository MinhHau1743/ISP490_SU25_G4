package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.NotificationService; // Import đã được thêm
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO; // Import đã được thêm
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.Enterprise; // Import đã được thêm
import vn.edu.fpt.model.Feedback;
import vn.edu.fpt.model.FeedbackView;
import vn.edu.fpt.model.InternalNote;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "FeedbackController", urlPatterns = {"/feedback"})
public class FeedbackController extends HttpServlet {

    /**
     * Handles all GET requests and routes them based on the 'action' parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

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
                case "success":
                    showSuccessPage(request, response);
                    break;
                case "list":
                default:
                    doList(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
        }
    }

    /**
     * Handles all POST requests, primarily for form submissions.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "create":
                    doCreateSubmit(request, response);
                    break;
                case "addNote":
                    doAddNote(request, response);
                    break;
                case "editNote":
                    doEditNote(request, response);
                    break;
                case "deleteNote":
                    doDeleteNote(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action for POST request.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
        }
    }

    // ----------------------------------------------------------------
    // PRIVATE METHODS FOR EACH ACTION
    // ----------------------------------------------------------------
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
        request.setAttribute("activeMenu", "feedback");
        request.getRequestDispatcher("/jsp/customerSupport/listFeedback.jsp").forward(request, response);
    }

    private void doView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
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

    private void doCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String techIdParam = request.getParameter("technicalRequestId");
        String contractIdParam = request.getParameter("contractId");

        try {
            FeedbackDAO feedbackDAO = new FeedbackDAO();

            if (techIdParam != null && !techIdParam.isEmpty()) {
                int technicalRequestId = Integer.parseInt(techIdParam);
                if (feedbackDAO.feedbackExistsForTechnicalRequest(technicalRequestId)) {
                    response.sendRedirect(request.getContextPath() + "/feedback?action=success");
                    return;
                }
                TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
                TechnicalRequest technicalRequest = technicalDAO.getTechnicalRequestById(technicalRequestId);
                if (technicalRequest == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Yêu cầu Kỹ thuật với ID được cung cấp.");
                    return;
                }
                request.setAttribute("technicalRequest", technicalRequest);

            } else if (contractIdParam != null && !contractIdParam.isEmpty()) {
                int contractId = Integer.parseInt(contractIdParam);
                if (feedbackDAO.feedbackExistsForContract(contractId)) {
                    response.sendRedirect(request.getContextPath() + "/feedback?action=success");
                    return;
                }
                ContractDAO contractDAO = new ContractDAO();
                Contract contract = contractDAO.getContractById(contractId);
                if (contract == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Hợp đồng với ID được cung cấp.");
                    return;
                }
                request.setAttribute("contract", contract);

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID của Yêu cầu Kỹ thuật hoặc Hợp đồng.");
                return;
            }
            request.getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Định dạng ID không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã có lỗi xảy ra phía máy chủ.");
        }
    }

    private void doCreateSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String ratingParam = request.getParameter("rating");
            String comment = request.getParameter("comment");
            String enterpriseIdParam = request.getParameter("enterpriseId");
            String technicalRequestIdParam = request.getParameter("technicalRequestId");
            String contractIdParam = request.getParameter("contractId");

            if (ratingParam == null || ratingParam.equals("0")) {
                request.setAttribute("errorMessage", "Vui lòng chọn một mức độ hài lòng.");
                doCreateForm(request, response);
                return;
            }
            if (enterpriseIdParam == null || enterpriseIdParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Enterprise ID is missing.");
                return;
            }

            Feedback newFeedback = new Feedback();
            newFeedback.setRating(Integer.parseInt(ratingParam));
            newFeedback.setComment(comment);
            newFeedback.setEnterpriseId(Integer.parseInt(enterpriseIdParam));

            if (technicalRequestIdParam != null && !technicalRequestIdParam.isEmpty()) {
                newFeedback.setTechnicalRequestId(Integer.parseInt(technicalRequestIdParam));
            }
            if (contractIdParam != null && !contractIdParam.isEmpty()) {
                newFeedback.setContractId(Integer.parseInt(contractIdParam));
            }

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            boolean success = feedbackDAO.addFeedback(newFeedback);

            if (success) {
                // === BẮT ĐẦU TẠO THÔNG BÁO HỆ THỐNG ===
                Feedback createdFeedback = feedbackDAO.getLastFeedbackByEnterpriseId(newFeedback.getEnterpriseId());
                if (createdFeedback != null) {
                    EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
                    Enterprise enterprise = enterpriseDAO.getEnterpriseById(createdFeedback.getEnterpriseId());
                    if (enterprise != null) {
                        // creator là null vì đây là hành động của khách hàng từ bên ngoài, không có user session
                        NotificationService.notifyNewFeedback(null, createdFeedback, enterprise.getName());
                    }
                }
                // === KẾT THÚC TẠO THÔNG BÁO HỆ THỐNG ===
                response.sendRedirect(request.getContextPath() + "/feedback?action=success");
            } else {
                request.setAttribute("errorMessage", "Lỗi khi lưu vào CSDL. Vui lòng thử lại.");
                doCreateForm(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra. Vui lòng thử lại.");
            doCreateForm(request, response);
        }
    }

    private void showSuccessPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/feedbackSuccess.jsp").forward(request, response);
    }

    private void doAddNote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
            String noteText = request.getParameter("noteText");

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để thêm ghi chú.");
                return;
            }
            User loggedInUser = (User) session.getAttribute("user");
            int userId = loggedInUser.getId();

            if (noteText == null || noteText.trim().isEmpty()) {
                response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteError=empty");
                return;
            }

            InternalNote note = new InternalNote();
            note.setFeedbackId(feedbackId);
            note.setUserId(userId);
            note.setNoteText(noteText);

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            feedbackDAO.addInternalNote(note);

            response.sendRedirect("feedback?action=view&id=" + feedbackId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể thêm ghi chú.");
        }
    }

    private void doEditNote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String noteIdRaw = request.getParameter("noteId");
        String feedbackIdRaw = request.getParameter("feedbackId");
        String noteText = request.getParameter("noteText");

        int feedbackId;
        if (feedbackIdRaw == null || feedbackIdRaw.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu feedbackId.");
            return;
        }
        try {
            feedbackId = Integer.parseInt(feedbackIdRaw);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "feedbackId không hợp lệ.");
            return;
        }

        int noteId;
        if (noteIdRaw == null || noteIdRaw.trim().isEmpty()) {
            response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteError=invalidNote");
            return;
        }
        try {
            noteId = Integer.parseInt(noteIdRaw);
        } catch (NumberFormatException ex) {
            response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteError=invalidNote");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để sửa ghi chú.");
            return;
        }
        
        if (noteText == null || noteText.trim().isEmpty()) {
            response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteError=empty");
            return;
        }

        try {
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            feedbackDAO.updateInternalNote(noteId, noteText);
            response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteUpdate=success");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể cập nhật ghi chú.");
        }
    }

    private void doDeleteNote(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int noteId = Integer.parseInt(request.getParameter("noteId"));
            int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));

            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để xóa ghi chú.");
                return;
            }
            
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            feedbackDAO.softDeleteInternalNote(noteId);

            response.sendRedirect("feedback?action=view&id=" + feedbackId + "&noteDelete=success");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xóa ghi chú.");
        }
    }
}