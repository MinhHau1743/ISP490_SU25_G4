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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Lấy đường dẫn của request, không cần chuyển về chữ thường nếu so sánh bằng startsWith
        String path = req.getServletPath();

        // Kiểm tra xem người dùng đã đăng nhập chưa
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        // Kiểm tra xem request có phải là tài nguyên tĩnh không (css, js, images,...)
        boolean isStaticResource = path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico|woff|ttf)$");

        // --- CẢI TIẾN LOGIC KIỂM TRA ĐƯỜNG DẪN CÔNG KHAI ---
        // Thay vì so sánh chính xác, ta kiểm tra xem path có "bắt đầu bằng" một public path nào không.
        // Điều này giúp xử lý các URL có tham số (VD: /feedback?action=create) một cách linh hoạt.
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(publicPath -> path.startsWith(publicPath));

        // Nếu đã đăng nhập, hoặc request đến trang công khai, hoặc là tài nguyên tĩnh -> cho phép đi tiếp
        if (isLoggedIn || isPublicPath || isStaticResource) {
            chain.doFilter(request, response);
        } else {
            // Nếu không, chuyển hướng về trang đăng nhập
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
