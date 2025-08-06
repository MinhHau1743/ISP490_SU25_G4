/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ReportDAO;

@WebServlet(name = "DashboardController", urlPatterns = {"/dashboard"})
public class DashboardController extends HttpServlet {

    // Trong DashboardController.java
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            ReportDAO reportDAO = new ReportDAO();

            // Lấy tham số giai đoạn từ request, mặc định là "Tháng này"
            String period = request.getParameter("period");
            if (period == null || period.isEmpty()) {
                period = "thismonth";
            }

            LocalDate today = LocalDate.now();
            LocalDate startDate; // Bỏ khởi tạo ở đây
            LocalDate endDate;   // Bỏ khởi tạo ở đây
            String summaryPeriod;

// THAY THẾ TOÀN BỘ KHỐI SWITCH CŨ BẰNG KHỐI NÀY
            switch (period) {
                case "last7days":
                    startDate = today.minusDays(6);
                    endDate = today; // Luôn là ngày hiện tại
                    summaryPeriod = "7 ngày qua";
                    break;

                case "lastmonth":
                    // Logic này đã đúng, giữ nguyên
                    startDate = today.minusMonths(1).withDayOfMonth(1);
                    endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                    summaryPeriod = "Tháng trước";
                    break;

                case "thisyear":
                    startDate = today.withDayOfYear(1);
                    endDate = today; // Luôn là ngày hiện tại
                    summaryPeriod = "Năm nay";
                    break;

                case "thismonth":
                default: // Mặc định là "Tháng này"
                    startDate = today.withDayOfMonth(1);
                    endDate = today; // LỖI SAI Ở ĐÂY: Cần phải gán lại endDate một cách tường minh
                    summaryPeriod = "Tháng này";
                    break;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            String dateFromStr = startDate.format(formatter);
            String dateToStr = endDate.format(formatter);

            // --- Logic gọi DAO vẫn giữ nguyên, không cần thay đổi ---
            double totalRevenue = reportDAO.getTotalRevenue(dateFromStr, dateToStr);
            int newCustomers = reportDAO.getNewCustomerCount(dateFromStr, dateToStr);
            int totalCustomers = reportDAO.getTotalCustomerCount();
            Map<String, Integer> contractStatusCounts = reportDAO.getContractStatusCounts(dateFromStr, dateToStr);
            Map<String, Integer> requestStatusCounts = reportDAO.getTechnicalRequestStatusCounts(dateFromStr, dateToStr);
            List<Map<String, Object>> topProducts = reportDAO.getTopProducts(dateFromStr, dateToStr, 3);

            // === THÊM MỚI: LẤY DỮ LIỆU CHO BIỂU ĐỒ ===
            List<Map<String, Object>> revenueTrend = reportDAO.getRevenueTrend(dateFromStr, dateToStr);
            List<Map<String, Object>> customerTrend = reportDAO.getNewCustomersTrend(dateFromStr, dateToStr);

            // Chuyển dữ liệu biểu đồ sang JSON
            Gson gson = new Gson();
            String revenueTrendJson = gson.toJson(revenueTrend);
            String customerTrendJson = gson.toJson(customerTrend);

            // --- GỬI DỮ LIỆU SANG JSP ---
            // Dữ liệu thống kê (giữ nguyên)
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("newCustomers", newCustomers);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("contractStatus", contractStatusCounts);
            request.setAttribute("requestStatus", requestStatusCounts);
            request.setAttribute("topProducts", topProducts);
            request.setAttribute("period", period);
            request.setAttribute("summaryPeriod", summaryPeriod);

            // === THÊM MỚI: Gửi dữ liệu biểu đồ sang JSP ===
            request.setAttribute("revenueTrendJson", revenueTrendJson);
            request.setAttribute("customerTrendJson", customerTrendJson);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu dashboard: " + e.getMessage());
        } finally {
            // Chuyển tiếp yêu cầu đến trang JSP để hiển thị
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
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
}
