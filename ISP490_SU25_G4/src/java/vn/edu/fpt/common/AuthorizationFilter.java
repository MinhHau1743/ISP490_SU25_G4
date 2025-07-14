/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package vn.edu.fpt.common;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*") // Áp dụng filter cho tất cả các URL
public class AuthorizationFilter implements Filter {

    // Danh sách các URL mà vai trò "Kinh doanh" ĐƯỢC PHÉP truy cập
    private static final List<String> KINH_DOANH_ALLOWED_URLS = Arrays.asList(
            "/listCustomer",
            "/createCustomer",
            "/editCustomer",
            "/deleteCustomer",
            "/viewCustomer",
            "/searchSuggestions",
            "/getDistricts",
            "/getWards",
            "/dashboard.jsp",
            "/resetPassword.jsp",
            "/viewProfile",
            "/changePassword.jsp",
            "/listContract",
            "/viewContract",
            "/logout.jsp"
    );

    // Danh sách quyền cho vai trò CHÁNH VĂN PHÒNG
    private static final List<String> CHANH_VAN_PHONG_ALLOWED_URLS = Arrays.asList(
            // Quyền giống Kinh doanh
            "/listCustomer", "/viewCustomerDetail",
            "/searchSuggestions", "/getDistricts", "/getWards",
            "/dashboard.jsp", "/resetPassword.jsp", "/viewProfile", "/changePassword.jsp", "/logout.jsp",
            // Thêm quyền quản lý Hợp đồng
            "/listContract", "/createContract", "/editContract", "/deleteContract", "/viewContract"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Bỏ qua filter cho các tài nguyên tĩnh (CSS, JS, images) và trang login
        if (path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/image/") || path.equals("/LoginController") || path.equals("/login.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Nếu chưa đăng nhập, chuyển về trang login
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        String userRole = (String) session.getAttribute("userRole");

        // Vai trò "Admin" được truy cập mọi nơi
        if ("Admin".equals(userRole)) {
            chain.doFilter(request, response);
            return;
        }

        // Kiểm tra quyền cho vai trò "Chánh văn phòng"
        if ("Chánh văn phòng".equals(userRole)) {
            boolean allowed = CHANH_VAN_PHONG_ALLOWED_URLS.stream().anyMatch(path::startsWith);
            if (allowed) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp");
            }
            return; // Dừng tại đây sau khi xử lý
        }

        // Kiểm tra quyền cho vai trò "Kinh doanh"
        if ("Kinh doanh".equals(userRole)) {
            boolean allowed = KINH_DOANH_ALLOWED_URLS.stream().anyMatch(path::startsWith);
            if (allowed) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp");
            }
            return; // Dừng tại đây sau khi xử lý
        }

        // Mặc định, nếu vai trò không được xử lý ở trên, từ chối truy cập
        httpResponse.sendRedirect(contextPath + "/access-denied.jsp");
    }
}
