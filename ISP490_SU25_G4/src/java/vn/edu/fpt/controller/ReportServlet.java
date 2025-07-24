/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ReportDAO;

@WebServlet(name = "ReportServlet", urlPatterns = {"/report"})
public class ReportServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // Lấy loại báo cáo, mặc định là "tongquan"
            String reportType = request.getParameter("report-type");
            if (reportType == null || reportType.isEmpty()) {
                reportType = "tongquan";
            }

            // Lấy tham số ngày tháng, nếu không có thì lấy tháng hiện tại
            String dateFromStr = request.getParameter("date-from");
            String dateToStr = request.getParameter("date-to");

            LocalDate today = LocalDate.now();
            if (dateFromStr == null || dateFromStr.isEmpty()) {
                dateFromStr = today.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if (dateToStr == null || dateToStr.isEmpty()) {
                // Sửa lại để lấy ngày hiện tại thay vì ngày cuối tháng
                dateToStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }

            ReportDAO reportDAO = new ReportDAO();

            // Trong ReportServlet.java, cập nhật lại khối switch
// Dựa vào loại báo cáo để lấy dữ liệu tương ứng
            switch (reportType) {
                case "doanhthu":
                    request.setAttribute("totalRevenue", reportDAO.getTotalRevenue(dateFromStr, dateToStr));
                    request.setAttribute("revenueTrend", reportDAO.getRevenueTrend(dateFromStr, dateToStr));
                    break;
                case "khachhang":
                    request.setAttribute("newCustomers", reportDAO.getNewCustomerCount(dateFromStr, dateToStr));
                    request.setAttribute("totalCustomers", reportDAO.getTotalCustomerCount());
                    request.setAttribute("newCustomersList", reportDAO.getNewCustomersList(dateFromStr, dateToStr));
                    break;
                case "sanpham":
                    request.setAttribute("topProducts", reportDAO.getTopProducts(dateFromStr, dateToStr, 10));
                    break;

                // THÊM 2 CASE MỚI VÀO ĐÂY
                case "hopdong":
                    request.setAttribute("contractStatus", reportDAO.getContractStatusCounts(dateFromStr, dateToStr));
                    request.setAttribute("contractsList", reportDAO.getContractsList(dateFromStr, dateToStr));
                    break;
                case "suachua":
                    request.setAttribute("requestStatus", reportDAO.getTechnicalRequestStatusCounts(dateFromStr, dateToStr));
                    // THAY ĐỔI DÒNG NÀY:
                    request.setAttribute("requestsWithDevices", reportDAO.getTechnicalRequestsWithDevices(dateFromStr, dateToStr));
                    break;

                default: // "tongquan"
                    request.setAttribute("totalRevenue", reportDAO.getTotalRevenue(dateFromStr, dateToStr));
                    request.setAttribute("newCustomers", reportDAO.getNewCustomerCount(dateFromStr, dateToStr));
                    request.setAttribute("totalCustomers", reportDAO.getTotalCustomerCount());
                    request.setAttribute("returningCustomers", reportDAO.getReturningCustomerCount(dateFromStr, dateToStr));
                    request.setAttribute("contractStatus", reportDAO.getContractStatusCounts(dateFromStr, dateToStr));
                    request.setAttribute("requestStatus", reportDAO.getTechnicalRequestStatusCounts(dateFromStr, dateToStr));
                    request.setAttribute("topProducts", reportDAO.getTopProducts(dateFromStr, dateToStr, 3));
                    break;
            }

            // Gửi các tham số đã chọn lại cho JSP
            request.setAttribute("reportType", reportType);
            request.setAttribute("selectedDateFrom", dateFromStr);
            request.setAttribute("selectedDateTo", dateToStr);

        } catch (SQLException e) {
            e.printStackTrace();
            // Có thể chuyển hướng đến trang lỗi
            request.setAttribute("errorMessage", "Lỗi khi truy vấn cơ sở dữ liệu: " + e.getMessage());
        } finally {
            // Chuyển tiếp yêu cầu đến trang JSP để hiển thị
            request.getRequestDispatcher("report.jsp").forward(request, response);
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
        return "Servlet for handling business reports";
    }
}
