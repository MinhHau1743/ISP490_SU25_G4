package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.CampaignType; // Giả sử bạn có model này
import vn.edu.fpt.model.Status;       // Giả sử bạn có model này

@WebServlet(name = "CampaignListServlet", urlPatterns = {"/list-campaign"})
public class CampaignListServlet extends HttpServlet {

    private static final int PAGE_SIZE = 5; // Tăng PAGE_SIZE cho hợp lý hơn
    private final CampaignDAO campaignDAO = new CampaignDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    request.setCharacterEncoding("UTF-8");

    try {
        // 1. Lấy tham số cho phân trang
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && pageStr.matches("\\d+")) {
            currentPage = Integer.parseInt(pageStr);
            if (currentPage < 1) currentPage = 1;
        }

        // 2. Lấy tham số cho tìm kiếm và tất cả các bộ lọc
        String searchTerm = request.getParameter("search") != null ? request.getParameter("search").trim() : "";
        String statusFilter = request.getParameter("status") != null ? request.getParameter("status").trim() : "";
        int typeIdFilter = 0;
        String typeIdStr = request.getParameter("typeId");
        if (typeIdStr != null && !typeIdStr.isEmpty() && !typeIdStr.equals("0")) {
            typeIdFilter = Integer.parseInt(typeIdStr);
        }
        // --- ĐÃ THÊM MỚI: Xử lý bộ lọc ngày tháng ---
        String startDateFilter = request.getParameter("startDate") != null ? request.getParameter("startDate").trim() : "";
        String endDateFilter = request.getParameter("endDate") != null ? request.getParameter("endDate").trim() : "";

        // 3. Lấy dữ liệu từ DAO (với đầy đủ các bộ lọc)
        List<Campaign> campaigns = campaignDAO.getCampaigns(currentPage, PAGE_SIZE, searchTerm, statusFilter, typeIdFilter, startDateFilter, endDateFilter);
        int totalRecords = campaignDAO.countCampaigns(searchTerm, statusFilter, typeIdFilter, startDateFilter, endDateFilter);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        // Lấy dữ liệu cho các thẻ thống kê
        int activeCampaigns = campaignDAO.countCampaigns("", "active", 0, "", "");
        int completedCampaigns = campaignDAO.countCampaigns("", "ended", 0, "", "");
        
        // Lấy dữ liệu cho dropdown "Loại chiến dịch"
        List<CampaignType> allCampaignTypes = campaignDAO.getAllCampaignTypes();

        // --- ĐÃ THÊM MỚI: Tạo query string cho phân trang ---
        StringBuilder queryString = new StringBuilder();
        if (!searchTerm.isEmpty()) {
            queryString.append("&search=").append(URLEncoder.encode(searchTerm, "UTF-8"));
        }
        if (!statusFilter.isEmpty()) {
            queryString.append("&status=").append(URLEncoder.encode(statusFilter, "UTF-8"));
        }
        if (typeIdFilter > 0) {
            queryString.append("&typeId=").append(typeIdFilter);
        }
        if (!startDateFilter.isEmpty()) {
            queryString.append("&startDate=").append(URLEncoder.encode(startDateFilter, "UTF-8"));
        }
        if (!endDateFilter.isEmpty()) {
            queryString.append("&endDate=").append(URLEncoder.encode(endDateFilter, "UTF-8"));
        }

        // 5. Đặt tất cả các thuộc tính vào request
        request.setAttribute("campaigns", campaigns);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("activeCampaigns", activeCampaigns);
        request.setAttribute("completedCampaigns", completedCampaigns);
        request.setAttribute("allCampaignTypes", allCampaignTypes);
        
        // Đặt lại các giá trị lọc để hiển thị trên form
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("typeIdFilter", typeIdFilter);
        request.setAttribute("startDateFilter", startDateFilter);
        request.setAttribute("endDateFilter", endDateFilter);
        
        // Đặt query string cho phân trang
        request.setAttribute("queryString", queryString.toString());

        // 6. Chuyển tiếp yêu cầu đến trang JSP
        request.getRequestDispatcher("/jsp/customerSupport/listCampaign.jsp").forward(request, response);

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("errorMessage", "Đã có lỗi xảy ra khi tải danh sách chiến dịch: " + e.getMessage());
        request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
    }
}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet responsible for listing, paginating, searching and filtering campaigns.";
    }
}
