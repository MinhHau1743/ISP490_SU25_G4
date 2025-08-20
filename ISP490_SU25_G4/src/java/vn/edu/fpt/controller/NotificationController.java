package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.NotificationDAO;
import vn.edu.fpt.model.Notification;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "NotificationController", urlPatterns = {"/notifications"})
public class NotificationController extends HttpServlet {

    private static final int PAGE_SIZE = 15; // 15 thông báo mỗi trang
    private NotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        notificationDAO = new NotificationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        switch (action) {
            case "list":
            default:
                listNotifications(request, response);
                break;
        }
    }

    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy số trang từ request, mặc định là trang 1
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && pageStr.matches("\\d+")) {
                page = Integer.parseInt(pageStr);
            }

            // Lấy danh sách thông báo cho trang hiện tại
            List<Notification> notificationList = notificationDAO.getPaginatedNotifications(page, PAGE_SIZE);

            // Lấy tổng số thông báo để tính toán phân trang
            int totalNotifications = notificationDAO.getTotalNotificationCount();
            int totalPages = (int) Math.ceil((double) totalNotifications / PAGE_SIZE);

            // Gửi dữ liệu sang JSP
            request.setAttribute("notificationList", notificationList);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/listNotifications.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Có thể chuyển hướng đến trang lỗi
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tải danh sách thông báo.");
        }
    }
}
