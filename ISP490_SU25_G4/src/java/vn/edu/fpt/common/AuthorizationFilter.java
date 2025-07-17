//package vn.edu.fpt.common;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Filter này kiểm tra quyền truy cập của người dùng cho mọi yêu cầu. Nó sử dụng
// * một cấu trúc Map để quản lý quyền một cách tập trung, giúp code sạch sẽ và dễ
// * mở rộng hơn.
// */
//@WebFilter("/*")
//public class AuthorizationFilter implements Filter {
//
//    // Danh sách các URL mà vai trò "Kinh doanh" ĐƯỢC PHÉP truy cập
//    private static final List<String> KINH_DOANH_ALLOWED_URLS = Arrays.asList(
//            "/listCustomer",
//            "/createCustomer",
//            "/editCustomer",
//            "/deleteCustomer",
//            "/viewCustomer",
//            "/searchSuggestions",
//            "/getDistricts",
//            "/getWards",
//            "/dashboard.jsp",
//            "/resetPassword.jsp",
//            "/viewProfile",
//            "/changePassword.jsp",
//            "/listContract",
//            "/viewContract",
//            "/logout.jsp"
//    );
//
//    // Danh sách quyền cho vai trò CHÁNH VĂN PHÒNG
//    private static final List<String> CHANH_VAN_PHONG_ALLOWED_URLS = Arrays.asList(
//            // Quyền giống Kinh doanh
//            "/listCustomer", "/viewCustomerDetail",
//            "/searchSuggestions", "/getDistricts", "/getWards",
//            "/dashboard.jsp", "/resetPassword.jsp", "/viewProfile", "/changePassword.jsp", "/logout.jsp",
//            // Thêm quyền quản lý Hợp đồng
//            "/listContract", "/createContract", "/editContract", "/deleteContract", "/viewContract"
//    );
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        String contextPath = httpRequest.getContextPath();
//        String path = httpRequest.getRequestURI().substring(contextPath.length());
//
//        // 1. Bỏ qua filter cho các tài nguyên công khai (public)
//        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/image/")
//                || path.equals("/LoginController") || path.equals("/login.jsp")
//                || path.equals("/access-denied.jsp")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        HttpSession session = httpRequest.getSession(false);
//
//        // 2. Nếu chưa đăng nhập (không có session hoặc user), chuyển về trang login
//        if (session == null || session.getAttribute("user") == null) {
//            httpResponse.sendRedirect(contextPath + "/login.jsp");
//            return;
//        }
//
//        // 3. Xử lý phân quyền dựa trên vai trò
//        String userRole = (String) session.getAttribute("userRole");
//
//        // Admin có toàn quyền truy cập
//        if ("Admin".equals(userRole)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Lấy danh sách các URL được phép của vai trò hiện tại
//        List<String> allowedUrls = rolePermissions.get(userRole);
//        boolean isAllowed = false;
//
//        if (allowedUrls != null) {
//            // Kiểm tra xem path hiện tại có nằm trong danh sách được phép không
//            isAllowed = allowedUrls.stream().anyMatch(url -> path.startsWith(url));
//        }
//
//        // 4. Cho phép hoặc từ chối truy cập
//        if (isAllowed) {
//            chain.doFilter(request, response);
//        } else {
//            httpResponse.sendRedirect(contextPath + "/access-denied.jsp");
//        }
//    }
//
//    @Override
//    public void destroy() {
//        // Có thể dọn dẹp tài nguyên ở đây nếu cần
//    }
//}
