/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

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

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    // Trong DashboardServlet.java
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
            LocalDate startDate = today;
            LocalDate endDate = today;
            String summaryPeriod = "Tháng này"; // Tiêu đề mặc định

            // Tính toán khoảng ngày dựa trên lựa chọn của người dùng
            switch (period) {
                case "last7days":
                    startDate = today.minusDays(6);
                    summaryPeriod = "7 ngày qua";
                    break;
                case "lastmonth":
                    startDate = today.minusMonths(1).withDayOfMonth(1);
                    endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                    summaryPeriod = "Tháng trước";
                    break;
                case "thisyear":
                    startDate = today.withDayOfYear(1);
                    summaryPeriod = "Năm nay";
                    break;
                case "thismonth":
                default: // Mặc định là tháng này
                    startDate = today.withDayOfMonth(1);
                    // endDate vẫn là ngày hôm nay
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

            // Đặt các thuộc tính vào request để JSP có thể truy cập
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("newCustomers", newCustomers);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("contractStatus", contractStatusCounts);
            request.setAttribute("requestStatus", requestStatusCounts);
            request.setAttribute("topProducts", topProducts);

            request.setAttribute("period", period); // Gửi lại lựa chọn để select box hiển thị đúng
            request.setAttribute("summaryPeriod", summaryPeriod); // Gửi lại tiêu đề giai đoạn

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
