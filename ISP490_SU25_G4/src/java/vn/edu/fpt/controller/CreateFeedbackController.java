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
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO; // ✔️ THÊM DAO NÀY
import vn.edu.fpt.model.Feedback;
import vn.edu.fpt.model.TechnicalRequest; // ✔️ THÊM MODEL NÀY

/**
 *
 * @author NGUYEN MINH
 */
@WebServlet(name = "CreateFeedbackController", urlPatterns = {"/createFeedback"})
public class CreateFeedbackController extends HttpServlet {

    /**
     * ✔️ SỬA LẠI HOÀN TOÀN: Lấy technicalRequestId từ URL, tìm thông tin
     * request và gửi sang JSP.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // 1. Lấy ID của Technical Request từ URL
            String idParam = request.getParameter("technicalRequestId");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID của yêu cầu kỹ thuật.");
                return;
            }
            int technicalRequestId = Integer.parseInt(idParam);

            // 2. Gọi TechnicalRequestDAO để lấy thông tin chi tiết
            //    Lưu ý: bạn cần có file TechnicalRequestDAO.java trong project
            TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
            TechnicalRequest technicalRequest = technicalDAO.getTechnicalRequestById(technicalRequestId);

            if (technicalRequest == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy yêu cầu kỹ thuật.");
                return;
            }

            // 3. Đặt đối tượng technicalRequest vào request để JSP sử dụng
            request.setAttribute("technicalRequest", technicalRequest);

            // 4. Chuyển tiếp đến trang JSP
            request.getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tải trang phản hồi.");
        }
    }

    /**
     * ✔️ SỬA LẠI: Xử lý dữ liệu POST từ form đã được điền sẵn thông tin.
     */
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    // SỬA ĐỔI: Bỏ hết phần xử lý JSON và PrintWriter

    try {
        // Lấy các tham số từ form
        String ratingParam = request.getParameter("rating");
        String comment = request.getParameter("comment");
        String enterpriseIdParam = request.getParameter("enterpriseId");
        String technicalRequestIdParam = request.getParameter("technicalRequestId");

        // === BƯỚC KIỂM TRA DỮ LIỆU (VALIDATION) ===
        if (ratingParam == null || ratingParam.equals("0")) {
            // Nếu validation thất bại, gửi lỗi lại trang form
            request.setAttribute("errorMessage", "Vui lòng chọn mức độ hài lòng của bạn.");
            // Gọi lại doGet để tải lại dữ liệu cho form
            doGet(request, response);
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
        newFeedback.setAppointmentId(null); // Không dùng đến

        FeedbackDAO feedbackDAO = new FeedbackDAO();
        boolean success = feedbackDAO.addFeedback(newFeedback);

        if (success) {
            // SỬA ĐỔI: Chuyển hướng đến trang danh sách nếu thành công
            response.sendRedirect(request.getContextPath() + "/ticket?action=list&feedback=success");
        } else {
            // Nếu lỗi, gửi lại form với thông báo lỗi
            request.setAttribute("errorMessage", "Lỗi khi lưu vào cơ sở dữ liệu. Vui lòng thử lại.");
            doGet(request, response);
        }
    } catch (Exception e) {
        e.printStackTrace();
        // Nếu có lỗi nghiêm trọng, gửi lại form với thông báo lỗi
        request.setAttribute("errorMessage", "Đã có lỗi không xác định xảy ra. Vui lòng thử lại.");
        doGet(request, response);
    }
}
}