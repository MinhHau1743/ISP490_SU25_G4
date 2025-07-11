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

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String pathAfterContext = requestURI.substring(contextPath.length()).toLowerCase();

        boolean isLoginPage = pathAfterContext.equals("/login.jsp");
        boolean is404Request = pathAfterContext.equals("/404.jsp");
        boolean isLoginAction = pathAfterContext.equals("/logincontroller");
        boolean verifyForgotPassword = pathAfterContext.equals("/verifyforgotpassword.jsp") || pathAfterContext.equals("/VerifyForgotPassword.jsp");
        boolean isForgotPassword = pathAfterContext.equals("/forgotpassword.jsp") || pathAfterContext.equals("/ForgotPassword.jsp");
        boolean verifyOTP = pathAfterContext.equals("/verifyotp.jsp") || pathAfterContext.equals("/VerifyOTP.jsp");
        boolean resetPassword = pathAfterContext.equals("/resetpassword.jsp") || pathAfterContext.equals("/ResetPassword.jsp");
        boolean verifyOTPController = pathAfterContext.equals("/verifyotpcontroller") || pathAfterContext.equals("/VerifyOTPController");
        boolean verifyResetOTPController = pathAfterContext.equals("/verifyresetotpcontroller") || pathAfterContext.equals("/VerifyResetOTPController");
        boolean isForgotPasswordController = pathAfterContext.equals("/forgotpasswordcontroller") || pathAfterContext.equals("/ForgotPasswordController");

        boolean isStaticResource = pathAfterContext.endsWith(".css")
                || pathAfterContext.endsWith(".js")
                || pathAfterContext.endsWith(".png")
                || pathAfterContext.endsWith(".jpg")
                || pathAfterContext.endsWith(".jpeg")
                || pathAfterContext.endsWith(".gif")
                || pathAfterContext.endsWith(".svg")
                || pathAfterContext.endsWith(".woff")
                || pathAfterContext.endsWith(".ico");

        HttpSession session = req.getSession(false);

        if (isLoginPage || isLoginAction || isForgotPassword || isStaticResource || verifyForgotPassword || verifyOTPController
                || isForgotPasswordController || is404Request || verifyResetOTPController || verifyOTP || resetPassword
                || (session != null && session.getAttribute("user") != null)) {
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
