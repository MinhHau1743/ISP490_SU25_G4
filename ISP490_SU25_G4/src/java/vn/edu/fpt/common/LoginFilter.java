package vn.edu.fpt.common;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter này bảo vệ các trang yêu cầu đăng nhập. Nó cho phép truy cập vào các
 * trang công khai (login, feedback,...) và các tài nguyên tĩnh (CSS, JS,
 * images).
 */
@WebFilter("/*") // Áp dụng filter cho tất cả các request
public class LoginFilter implements Filter {

    // Danh sách các đường dẫn công khai không cần đăng nhập (Whitelist)
    // Đã thêm "/feedback" vào danh sách này
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
            "/auth",
            "/login.jsp",
            "/forgotpassword.jsp",
            "/verifyotp.jsp",
            "/resetpassword.jsp",
            "/404.jsp",
            "/feedback" // <- THÊM ĐƯỜNG DẪN CÔNG KHAI TẠI ĐÂY
    ));

// File: vn/edu/fpt/common/LoginFilter.java
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String path = req.getServletPath().toLowerCase();

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        boolean isStaticResource = path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff|ttf)$");

        // === THAY ĐỔI LOGIC KIỂM TRA TỪ STARTSWITH SANG ENDSWITH ===
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(publicPath -> path.endsWith(publicPath));

        if (isLoggedIn || isPublicPath || isStaticResource) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần khởi tạo gì đặc biệt
    }

    @Override
    public void destroy() {
        // Không cần dọn dẹp gì đặc biệt
    }
}
