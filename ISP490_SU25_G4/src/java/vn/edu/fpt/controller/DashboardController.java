package vn.edu.fpt.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ReportDAO;

@WebServlet(name = "DashboardController", urlPatterns = {"/dashboard"})
public class DashboardController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        ReportDAO reportDAO = new ReportDAO();
        String period = request.getParameter("period");
        if (period == null || period.isEmpty()) {
            period = "thismonth";
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;
        String summaryPeriod;

        switch (period) {
            case "last7days":
                startDate = today.minusDays(6);
                endDate = today;
                summaryPeriod = "7 ngày qua";
                break;
            case "lastmonth":
                startDate = today.minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                summaryPeriod = "Tháng trước";
                break;
            case "thisyear":
                startDate = today.withDayOfYear(1);
                endDate = today;
                summaryPeriod = "Năm nay";
                break;
            case "thismonth":
            default:
                startDate = today.withDayOfMonth(1);
                endDate = today;
                summaryPeriod = "Tháng này";
                break;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String dateFromStr = startDate.format(formatter);
        String dateToStr = endDate.format(formatter);

        // --- Lấy dữ liệu KPIs ---
        double totalRevenue = reportDAO.getTotalRevenue(dateFromStr, dateToStr);
        int newCustomers = reportDAO.getNewCustomerCount(dateFromStr, dateToStr);
        int completedRequests = reportDAO.getCompletedTechnicalRequestsCount(dateFromStr, dateToStr);
        int completedCampaigns = reportDAO.getCompletedCampaignsCount(dateFromStr, dateToStr);
        
        // --- Lấy dữ liệu cho các biểu đồ ---
        List<Map<String, Object>> revenueTrend = reportDAO.getRevenueTrend(dateFromStr, dateToStr);
        Map<String, Integer> techRequestStatus = reportDAO.getTechnicalRequestStatusDistribution(dateFromStr, dateToStr);
        Map<String, Integer> campaignTypes = reportDAO.getCampaignTypeDistribution(dateFromStr, dateToStr);

        // Chuyển dữ liệu sang JSON
        Gson gson = new Gson();
        String revenueTrendJson = gson.toJson(revenueTrend);
        String techRequestStatusJson = gson.toJson(techRequestStatus);
        String campaignTypesJson = gson.toJson(campaignTypes);

        // --- Gửi dữ liệu sang JSP ---
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("newCustomers", newCustomers);
        request.setAttribute("completedRequests", completedRequests);
        request.setAttribute("completedCampaigns", completedCampaigns);
        request.setAttribute("selectedPeriod", period);
        request.setAttribute("summaryPeriod", summaryPeriod);

        // Gửi dữ liệu JSON của các biểu đồ sang JSP
        request.setAttribute("revenueTrendJson", revenueTrendJson);
        request.setAttribute("techRequestStatusJson", techRequestStatusJson);
        request.setAttribute("campaignTypesJson", campaignTypesJson);
System.out.println("DEBUG - Campaign JSON Data: " + campaignTypesJson);
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}