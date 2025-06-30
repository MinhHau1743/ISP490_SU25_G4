import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String pathAfterContext = requestURI.substring(contextPath.length()).toLowerCase();

        boolean isLoginPage = pathAfterContext.equals("/login.jsp");
        boolean is404Request = pathAfterContext.equals("/404.jsp");
        boolean isLoginAction = pathAfterContext.equals("/logincontroller");
        boolean isForgotPassword = pathAfterContext.equals("/forgotpassword.jsp");
        boolean isForgotPasswordController = pathAfterContext.equals("/forgotpasswordcontroller");

        boolean isStaticResource = pathAfterContext.endsWith(".css") || 
                                  pathAfterContext.endsWith(".js") || 
                                  pathAfterContext.endsWith(".png") || 
                                  pathAfterContext.endsWith(".jpg") || 
                                  pathAfterContext.endsWith(".jpeg") || 
                                  pathAfterContext.endsWith(".gif") || 
                                  pathAfterContext.endsWith(".svg") || 
                                  pathAfterContext.endsWith(".woff") || 
                                  pathAfterContext.endsWith(".ico");

        HttpSession session = req.getSession(false);

        if (isLoginPage || isLoginAction || isForgotPassword || isStaticResource || 
            isForgotPasswordController || is404Request ||
            (session != null && session.getAttribute("user") != null)) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(contextPath + "/login.jsp");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}