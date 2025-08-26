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

        // --- Lấy dữ liệu từ DAO (đã cập nhật) ---
        double totalRevenue = reportDAO.getTotalRevenue(dateFromStr, dateToStr);
        int newCustomers = reportDAO.getNewCustomerCount(dateFromStr, dateToStr);
        // THAY THẾ HAI DÒNG CŨ BẰNG HAI DÒNG MỚI:
        int completedRequests = reportDAO.getCompletedTechnicalRequestsCount(dateFromStr, dateToStr);
        int completedCampaigns = reportDAO.getCompletedCampaignsCount(dateFromStr, dateToStr);
        
        Map<String, Integer> contractStatusCounts = reportDAO.getContractStatusCounts(dateFromStr, dateToStr);
        List<Map<String, Object>> topProducts = reportDAO.getTopProducts(dateFromStr, dateToStr, 5);
        List<Map<String, Object>> revenueTrend = reportDAO.getRevenueTrend(dateFromStr, dateToStr);

        // Chuyển dữ liệu biểu đồ sang JSON
        Gson gson = new Gson();
        String revenueTrendJson = gson.toJson(revenueTrend);
        String topProductsJson = gson.toJson(topProducts);
        String contractStatusCountsJson = gson.toJson(contractStatusCounts);

        // --- Gửi dữ liệu sang JSP (đã cập nhật) ---
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("newCustomers", newCustomers);
        // THAY THẾ HAI DÒNG CŨ BẰNG HAI DÒNG MỚI:
        request.setAttribute("completedRequests", completedRequests);
        request.setAttribute("completedCampaigns", completedCampaigns);
        
        request.setAttribute("selectedPeriod", period); 
        request.setAttribute("summaryPeriod", summaryPeriod);

        // Gửi dữ liệu JSON sang JSP
        request.setAttribute("revenueTrendJson", revenueTrendJson);
        request.setAttribute("topProductsJson", topProductsJson);
        request.setAttribute("contractStatusCountsJson", contractStatusCountsJson); 

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
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
}