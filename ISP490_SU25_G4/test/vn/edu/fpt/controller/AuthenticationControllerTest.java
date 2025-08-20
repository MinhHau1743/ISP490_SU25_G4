package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;
import static org.mockito.Mockito.*;

/**
 * Bộ Unit Test tập trung vào chức năng Đăng nhập và Quên mật khẩu của AuthenticationController.
 * Sử dụng JUnit 4 và Mockito với các test case N/A/B.
 */
public class AuthenticationControllerTest {

    // --- Khai báo các đối tượng giả lập (Mock) ---
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private UserDAO userDao; // DAO giả

    private AuthenticationController authController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("");
        authController = new AuthenticationController(userDao);
    }

    // ===================================================================================
    // ## Test chức năng Đăng nhập (Login)
    // ===================================================================================

    /**
     * [N] Test đăng nhập thành công, chuyển hướng đến trang dashboard.
     */
    @Test
    public void testLogin_Normal_Success() throws ServletException, IOException {
        System.out.println("Testing: [N] Login - Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("password123");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRequireChangePassword(0); // 0 = false

        when(userDao.login("test@example.com", "password123")).thenReturn(mockUser);

        // Act
        authController.doPost(request, response);

        // Assert
        verify(session).setAttribute("user", mockUser);
        verify(response).sendRedirect("dashboard.jsp");
    }

    /**
     * [N] Test đăng nhập thành công nhưng tài khoản bị bắt buộc đổi mật khẩu.
     */
    @Test
    public void testLogin_Normal_ForceChangePassword() throws ServletException, IOException {
        System.out.println("Testing: [N] Login - Force Change Password");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");

        User mockUser = new User();
        mockUser.setEmail("new@example.com");
        mockUser.setRequireChangePassword(1); // 1 = true

        when(userDao.login("new@example.com", "password123")).thenReturn(mockUser);

        // Act
        authController.doPost(request, response);

        // Assert
        verify(session).setAttribute("user", mockUser);
        verify(response).sendRedirect("resetPassword.jsp");
    }

    /**
     * [A] Test đăng nhập thất bại do sai email hoặc mật khẩu.
     */
    @Test
    public void testLogin_Abnormal_WrongCredentials() throws ServletException, IOException {
        System.out.println("Testing: [A] Login - Wrong Credentials");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        when(userDao.login(anyString(), anyString())).thenReturn(null);

        // Act
        authController.doPost(request, response);

        // Assert
        verify(request).setAttribute("error", "Email hoặc mật khẩu không đúng!");
        verify(requestDispatcher).forward(request, response);
    }

    /**
     * [B] Test đăng nhập thất bại do bỏ trống một trong các trường.
     */
    @Test
    public void testLogin_Boundary_EmptyInput() throws ServletException, IOException {
        System.out.println("Testing: [B] Login - Empty Input");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn(""); // Email trống
        when(request.getParameter("password")).thenReturn("somepassword");

        // Act
        authController.doPost(request, response);

        // Assert
        verify(userDao, never()).login(anyString(), anyString());
        verify(request).setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Quên mật khẩu (Forgot Password)
    // ===================================================================================

    /**
     * [N] Test yêu cầu quên mật khẩu thành công với email tồn tại.
     */
    @Test
    public void testForgotPassword_Normal_Success() throws ServletException, IOException {
        System.out.println("Testing: [N] Forgot Password - Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("forgotPassword");
        when(request.getParameter("email")).thenReturn("existing@example.com");

        when(userDao.emailExists("existing@example.com")).thenReturn(true);

        // Act
        authController.doPost(request, response);

        // Assert
        verify(session).setAttribute(eq("email"), eq("existing@example.com"));
        verify(session).setAttribute(eq("otp"), anyString());
        verify(session).setAttribute(eq("otpExpiresAt"), any(LocalDateTime.class));
        verify(response).sendRedirect("verifyOTP.jsp");
    }

    /**
     * [A] Test yêu cầu quên mật khẩu thất bại do email không tồn tại trong hệ thống.
     */
    @Test
    public void testForgotPassword_Abnormal_EmailNotFound() throws ServletException, IOException {
        System.out.println("Testing: [A] Forgot Password - Email Not Found");
        // Arrange
        when(request.getParameter("action")).thenReturn("forgotPassword");
        when(request.getParameter("email")).thenReturn("nonexisting@example.com");

        when(userDao.emailExists("nonexisting@example.com")).thenReturn(false);

        // Act
        authController.doPost(request, response);

        // Assert
        verify(request).setAttribute("error", "Email không tồn tại trong hệ thống.");
        verify(requestDispatcher).forward(request, response);
    }

    /**
     * [B] Test yêu cầu quên mật khẩu thất bại do không nhập email.
     */
    @Test
    public void testForgotPassword_Boundary_EmptyEmail() throws ServletException, IOException {
        System.out.println("Testing: [B] Forgot Password - Empty Email");
        // Arrange
        when(request.getParameter("action")).thenReturn("forgotPassword");
        when(request.getParameter("email")).thenReturn(""); // Email trống

        // Act
        authController.doPost(request, response);

        // Assert
        verify(userDao, never()).emailExists(anyString());
        // SỬA LẠI: Câu thông báo lỗi cho khớp với thực tế
        verify(request).setAttribute("error", "Email không hợp lệ hoặc bị bỏ trống.");
        verify(requestDispatcher).forward(request, response);
    }
    
    /**
     * [A] Test xác thực OTP thất bại do mã đã hết hạn. (Phương thức thay thế)
     */
    @Test
    public void testVerifyOTP_Abnormal_ExpiredCode() throws ServletException, IOException {
        System.out.println("Testing: [A] Verify OTP - Expired Code");
        // Arrange
        when(request.getParameter("action")).thenReturn("verifyOTP");
        when(request.getParameter("otp")).thenReturn("123456");

        // Giả lập OTP đúng, nhưng thời gian hết hạn đã ở trong quá khứ
        when(session.getAttribute("otp")).thenReturn("123456");
        when(session.getAttribute("otpExpiresAt")).thenReturn(LocalDateTime.now().minusMinutes(1)); // Đã hết hạn 1 phút trước

        // Act
        authController.doPost(request, response);

        // Assert
        verify(request).setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
        verify(requestDispatcher).forward(request, response);
    }
}