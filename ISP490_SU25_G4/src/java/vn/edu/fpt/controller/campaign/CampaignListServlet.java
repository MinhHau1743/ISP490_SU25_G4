package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.dao.StatusDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.CampaignType;
import vn.edu.fpt.model.Status;

@WebServlet(name = "CampaignListServlet", urlPatterns = {"/list-campaign"})
public class CampaignListServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 5;

    private final CampaignDAO campaignDAO = new CampaignDAO();
    private final StatusDAO statusDAO = new StatusDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // -------- 1) Phân trang --------
            int pageSize = DEFAULT_PAGE_SIZE;
            String sizeStr = trimOrEmpty(request.getParameter("size"));
            if (!sizeStr.isEmpty()) {
                try {
                    int s = Integer.parseInt(sizeStr);
                    if (s > 0 && s <= 100) {
                        pageSize = s;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            int currentPage = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && pageStr.matches("\\d+")) {
                currentPage = Math.max(1, Integer.parseInt(pageStr));
            }

            // -------- 2) Tìm kiếm + bộ lọc (status theo Statuses.id) --------
            String searchTerm = trimOrEmpty(request.getParameter("search"));

            Integer statusIdFilter = null; // FK -> Statuses.id
            String statusIdStr = request.getParameter("statusId");
            if (statusIdStr != null && !statusIdStr.trim().isEmpty()) {
                try {
                    statusIdFilter = Integer.valueOf(statusIdStr.trim());
                } catch (NumberFormatException ignored) {
                }
            }

            int typeIdFilter = 0;
            String typeIdStr = request.getParameter("typeId");
            if (typeIdStr != null && !typeIdStr.trim().isEmpty() && !"0".equals(typeIdStr.trim())) {
                try {
                    typeIdFilter = Integer.parseInt(typeIdStr.trim());
                } catch (NumberFormatException ignored) {
                }
            }

            // yyyy-MM-dd (optional)
            String startDateFilter = trimOrEmpty(request.getParameter("startDate"));
            String endDateFilter = trimOrEmpty(request.getParameter("endDate"));

            // -------- 3) Đếm trước để clamp trang --------
            int totalRecords = campaignDAO.countCampaigns(
                    searchTerm, statusIdFilter, typeIdFilter, startDateFilter, endDateFilter
            );
            int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            // -------- 4) Lấy danh sách --------
            List<Campaign> campaigns = campaignDAO.getCampaigns(
                    currentPage, pageSize, searchTerm, statusIdFilter, typeIdFilter, startDateFilter, endDateFilter
            );

            // -------- 5) Thống kê nhanh theo Statuses --------
            int activeCampaigns = 0;
            int completedCampaigns = 0;

            // Dùng id theo tên trạng thái
            Integer doingId = statusDAO.getIdByName("Đang thực hiện");
            Integer doneId = statusDAO.getIdByName("Hoàn thành");

            if (doingId != null) {
                activeCampaigns = campaignDAO.countCampaigns("", doingId, 0, "", "");
            }
            if (doneId != null) {
                completedCampaigns = campaignDAO.countCampaigns("", doneId, 0, "", "");
            }

            // -------- 6) Dữ liệu dropdown --------
            List<CampaignType> allCampaignTypes = campaignDAO.getAllCampaignTypes();
            List<Status> statusList = statusDAO.getAllStatuses();

            // -------- 7) Giữ query string cho phân trang --------
            StringBuilder queryString = new StringBuilder();
            if (!searchTerm.isEmpty()) {
                queryString.append("&search=").append(URLEncoder.encode(searchTerm, StandardCharsets.UTF_8.name()));
            }
            if (statusIdFilter != null) {
                queryString.append("&statusId=").append(statusIdFilter);
            }
            if (typeIdFilter > 0) {
                queryString.append("&typeId=").append(typeIdFilter);
            }
            if (!startDateFilter.isEmpty()) {
                queryString.append("&startDate=").append(URLEncoder.encode(startDateFilter, StandardCharsets.UTF_8.name()));
            }
            if (!endDateFilter.isEmpty()) {
                queryString.append("&endDate=").append(URLEncoder.encode(endDateFilter, StandardCharsets.UTF_8.name()));
            }
            if (pageSize != DEFAULT_PAGE_SIZE) {
                queryString.append("&size=").append(pageSize);
            }

            // -------- 8) Set attributes cho JSP --------
            request.setAttribute("campaigns", campaigns);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);

            request.setAttribute("activeCampaigns", activeCampaigns);
            request.setAttribute("completedCampaigns", completedCampaigns);

            request.setAttribute("allCampaignTypes", allCampaignTypes);
            request.setAttribute("statusList", statusList);

            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("statusIdFilter", statusIdFilter);
            request.setAttribute("typeIdFilter", typeIdFilter);
            request.setAttribute("startDateFilter", startDateFilter);
            request.setAttribute("endDateFilter", endDateFilter);
            request.setAttribute("pageSize", pageSize);

            request.setAttribute("queryString", queryString.toString());

            // -------- 9) Forward --------
            request.getRequestDispatcher("/jsp/customerSupport/listCampaign.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage",
                    "Đã có lỗi xảy ra khi tải danh sách chiến dịch: " + e.getMessage());
            request.getRequestDispatcher("/jsp/error.jsp").forward(request, response);
        }
    }

    private static String trimOrEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    @Override
    public String getServletInfo() {
        return "List/Paginate/Search/Filter campaigns (status filter by Statuses.id).";
    }
}
