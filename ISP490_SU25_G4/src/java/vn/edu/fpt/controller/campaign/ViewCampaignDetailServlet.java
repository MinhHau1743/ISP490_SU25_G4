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
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.model.Campaign;

/**
 *
 * @author minhh
 */
@WebServlet(name = "ViewCampaignDetailServlet", urlPatterns = {"/view-campaign-detail"})
public class ViewCampaignDetailServlet extends HttpServlet {

    private CampaignDAO campaignDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        campaignDAO = new CampaignDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // Lấy campaignId từ request parameter
        String campaignIdStr = request.getParameter("id");
        int campaignId = 0;
        try {
            campaignId = Integer.parseInt(campaignIdStr);
        } catch (NumberFormatException e) {
            // Xử lý lỗi nếu ID không hợp lệ (ví dụ: chuyển hướng về trang danh sách hoặc hiển thị lỗi)
            request.setAttribute("errorMessage", "ID chiến dịch không hợp lệ.");
            request.getRequestDispatcher("/jsp/admin/viewCampaignDetails.jsp").forward(request, response);
            return;
        }

        // Lấy thông tin chiến dịch từ DAO
        Campaign campaign = campaignDAO.getCampaignById(campaignId);

        // Đặt đối tượng Campaign vào request để JSP có thể truy cập
        request.setAttribute("campaign", campaign);

        // Chuyển tiếp đến trang JSP hiển thị chi tiết
        request.getRequestDispatcher("/jsp/customerSupport/viewCampaignDetails.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Có thể không cần xử lý POST cho trang xem chi tiết,
        // hoặc chuyển hướng về doGet nếu người dùng cố gắng POST
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet để xem chi tiết chiến dịch.";
    }
}