/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Import này cần thiết nếu bạn có xử lý multipart/form-data trong doPost
// import jakarta.servlet.annotation.MultipartConfig;
import vn.edu.fpt.dao.CampaignDAO;

/**
 *
 * @author minhh
 */
@WebServlet(name = "UpdateCampaignStatusServlet", urlPatterns = {"/update-campaign-status"})
// @MultipartConfig // Không cần thiết cho servlet này nếu chỉ xử lý form-urlencoded
public class UpdateCampaignStatusServlet extends HttpServlet {

    private CampaignDAO campaignDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        campaignDAO = new CampaignDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain"); // Trả về dạng văn bản đơn giản
        response.setCharacterEncoding("UTF-8");

        int campaignId = 0;
        // --- THAY ĐỔI Ở ĐÂY: Lấy trạng thái mới từ request thay vì cố định ---
        String newStatus = request.getParameter("status"); // Lấy trạng thái mới từ yêu cầu AJAX
        // ---------------------------------------------------------------------
        
        String idParam = request.getParameter("id"); // Lấy ID chiến dịch từ yêu cầu AJAX
        
        try {
            campaignId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.getWriter().write("Error: ID chiến dịch không hợp lệ.");
            return;
        }

        // Lấy ID người dùng hiện tại để cập nhật trường 'updated_by'
        // Trong một ứng dụng thực tế, bạn sẽ lấy ID này từ session của người dùng đã đăng nhập:
        // Integer updatedByUserId = (Integer) request.getSession().getAttribute("loggedInUserId");
        // Giả sử có một user với ID = 1 là người quản trị để test:
        Integer updatedByUserId = 1; // Placeholder, bạn có thể thay đổi hoặc làm cho nó nullable trong DB

        // Đảm bảo phương thức updateCampaignStatus trong CampaignDAO của bạn
        // chấp nhận newStatus (String) làm đối số thứ hai.
        boolean success = campaignDAO.updateCampaignStatus(campaignId, newStatus, updatedByUserId);

        if (success) {
            response.getWriter().write("Success");
        } else {
            response.getWriter().write("Error: Không thể cập nhật trạng thái chiến dịch.");
        }
    }
    
    // Thêm doGet nếu bạn muốn cho phép test trực tiếp qua URL trình duyệt (không khuyến khích cho cập nhật dữ liệu)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Có thể chuyển hướng về trang danh sách hoặc hiển thị thông báo lỗi
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Chỉ cho phép phương thức POST cho cập nhật trạng thái.");
    }

    @Override
    public String getServletInfo() {
        return "Servlet để cập nhật trạng thái chiến dịch.";
    }
}