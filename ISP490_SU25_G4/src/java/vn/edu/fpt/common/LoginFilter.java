package vn.edu.fpt.common;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginFilter implements Filter {

@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String path = req.getRequestURI().toLowerCase();
    String loginURI = req.getContextPath() + "/login.jsp";
    boolean isLoginRequest = req.getRequestURI().equals(loginURI);
    boolean isStaticResource = path.endsWith(".css") || 
                              path.endsWith(".js") || 
                              path.endsWith(".png") || 
                              path.endsWith(".jpg") || 
                              path.endsWith(".jpeg") || 
                              path.endsWith(".gif") || 
                              path.endsWith(".svg") || 
                              path.endsWith(".woff") || 
                              path.endsWith(".ttf") || 
                              path.endsWith(".ico");

    HttpSession session = req.getSession(false);

    if (isLoginRequest || isStaticResource || (session != null && session.getAttribute("email") != null)) {
        // Nếu là trang login, tài nguyên tĩnh hoặc đã đăng nhập thì cho phép tiếp tục
        chain.doFilter(request, response);
    } else {
        // Nếu chưa đăng nhập, chuyển hướng về login.jsp
        res.sendRedirect(loginURI);
    }
}


}
