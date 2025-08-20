package vn.edu.fpt.common;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.NotificationDAO;
import vn.edu.fpt.model.User;

import java.io.IOException;

@WebFilter("/*") // Áp dụng filter cho tất cả các request
public class NotificationFilter implements Filter {

    private NotificationDAO notificationDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.notificationDAO = new NotificationDAO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        // Chỉ tải thông báo nếu người dùng đã đăng nhập
        if (session != null && session.getAttribute("user") != null) {
            // Lấy 5 thông báo mới nhất và tổng số thông báo
            request.setAttribute("latestNotifications", notificationDAO.getLatestNotifications(5));
            request.setAttribute("notificationCount", notificationDAO.getTotalNotificationCount());
        }

        // Chuyển request đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}