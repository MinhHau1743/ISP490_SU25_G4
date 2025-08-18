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
import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;
import static org.mockito.Mockito.*;

/**
 * Bộ Unit Test đầy đủ và hoàn thiện cho AuthenticationController. Sử dụng JUnit
 * 4 và Mockito.
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

    // Controller sẽ được khởi tạo trong setUp()
    private AuthenticationController authController;

    @Before
    public void setUp() {
        // Khởi tạo tất cả các @Mock
        MockitoAnnotations.openMocks(this);

        // Thiết lập các hành vi chung cho mock
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session); // Sửa lỗi NullPointerException
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("");

        // QUAN TRỌNG: Khởi tạo controller bằng tay và "tiêm" userDao giả vào.
        authController = new AuthenticationController(userDao);
    }

    // ===================================================================================
    // ## Test chức năng Đăng nhập (Login)
    // ===================================================================================
    @Test
    public void testLogin_Success_RedirectsToDashboard() throws ServletException, IOException {
        System.out.println("Testing: Login Success -> Redirect to Dashboard");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("password123");

        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRequireChangePassword(0);

        when(userDao.login("test@example.com", "password123")).thenReturn(mockUser);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(session).setAttribute("user", mockUser);
        verify(response).sendRedirect("dashboard.jsp");
    }

    @Test
    public void testLogin_Success_ForceChangePassword() throws ServletException, IOException {
        System.out.println("Testing: Login Success -> Force Change Password");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("new@example.com");
        when(request.getParameter("password")).thenReturn("password123");

        User mockUser = new User();
        mockUser.setEmail("new@example.com");
        mockUser.setRequireChangePassword(1);

        when(userDao.login("new@example.com", "password123")).thenReturn(mockUser);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(session).setAttribute("user", mockUser);
        verify(session).setAttribute("email", "new@example.com");
        verify(response).sendRedirect("resetPassword.jsp");
    }

    @Test
    public void testLogin_Failed_WrongCredentials() throws ServletException, IOException {
        System.out.println("Testing: Login Failed (Wrong Credentials) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("test@example.com");
        when(request.getParameter("password")).thenReturn("wrongpassword");

        when(userDao.login(anyString(), anyString())).thenReturn(null);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(request).setAttribute("error", "Email hoặc mật khẩu không đúng!");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testLogin_Failed_EmptyInput() throws ServletException, IOException {
        System.out.println("Testing: Login Failed (Empty Input) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn(""); // Email trống
        when(request.getParameter("password")).thenReturn("somepassword");

        // Act
        authController.doPost(request, response);

        // Verify
        verify(userDao, never()).login(anyString(), anyString()); // Đảm bảo không gọi DB
        verify(request).setAttribute("error", "Vui lòng nhập đầy đủ email và mật khẩu.");
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Quên mật khẩu & OTP
    // ===================================================================================
    @Test
    public void testForgotPassword_Success_RedirectsToVerifyOTP() throws ServletException, IOException {
        System.out.println("Testing: Forgot Password Success -> Redirect to Verify OTP");
        // Arrange
        when(request.getParameter("action")).thenReturn("forgotPassword");
        when(request.getParameter("email")).thenReturn("existing@example.com");

        when(userDao.emailExists("existing@example.com")).thenReturn(true);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(session).setAttribute(eq("email"), eq("existing@example.com"));
        verify(session).setAttribute(eq("otp"), anyString());
        verify(response).sendRedirect("verifyOTP.jsp");
    }

    @Test
    public void testForgotPassword_Failed_EmailNotFound() throws ServletException, IOException {
        System.out.println("Testing: Forgot Password Failed (Email Not Found) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("forgotPassword");
        when(request.getParameter("email")).thenReturn("nonexisting@example.com");

        when(userDao.emailExists("nonexisting@example.com")).thenReturn(false);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(request).setAttribute("error", "Email không tồn tại trong hệ thống.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testVerifyOTP_Success() throws ServletException, IOException {
        System.out.println("Testing: Verify OTP Success -> Redirect to Reset Password");
        // Arrange
        when(request.getParameter("action")).thenReturn("verifyOTP");
        when(request.getParameter("otp")).thenReturn("123456");

        when(session.getAttribute("otp")).thenReturn("123456");
        when(session.getAttribute("otpExpiresAt")).thenReturn(LocalDateTime.now().plusMinutes(1));

        // Act
        authController.doPost(request, response);

        // Verify
        verify(response).sendRedirect("resetPassword.jsp");
    }

    @Test
    public void testVerifyOTP_Failed_WrongCode() throws ServletException, IOException {
        System.out.println("Testing: Verify OTP Failed (Wrong Code) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("verifyOTP");
        when(request.getParameter("otp")).thenReturn("654321");

        when(session.getAttribute("otp")).thenReturn("123456");
        when(session.getAttribute("otpExpiresAt")).thenReturn(LocalDateTime.now().plusMinutes(1));

        // Act
        authController.doPost(request, response);

        // Verify
        verify(request).setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn.");
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Đặt lại & Đổi mật khẩu
    // ===================================================================================
    @Test
    public void testResetPassword_Success() throws ServletException, IOException {
        System.out.println("Testing: Reset Password Success -> Forward to Login with Success Message");
        // Arrange
        when(request.getParameter("action")).thenReturn("resetPassword");
        when(request.getParameter("newPassword")).thenReturn("NewPassword123");
        when(request.getParameter("confirmPassword")).thenReturn("NewPassword123");

        when(session.getAttribute("email")).thenReturn("existing@example.com");
        when(userDao.updatePassword(anyString(), anyString())).thenReturn(true);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(userDao).updatePassword("existing@example.com", "NewPassword123");
        verify(session).invalidate();
        verify(request).setAttribute("success", "Cập nhật mật khẩu thành công! Vui lòng đăng nhập lại.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testResetPassword_Failed_PasswordsDoNotMatch() throws ServletException, IOException {
        System.out.println("Testing: Reset Password Failed (Passwords Don't Match) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("resetPassword");
        when(request.getParameter("newPassword")).thenReturn("NewPassword123");
        when(request.getParameter("confirmPassword")).thenReturn("WrongPassword456");

        when(session.getAttribute("email")).thenReturn("existing@example.com");

        // Act
        authController.doPost(request, response);

        // Verify
        verify(userDao, never()).updatePassword(anyString(), anyString());
        verify(request).setAttribute("error", "Mật khẩu không khớp hoặc bị bỏ trống!");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testChangePassword_Success() throws ServletException, IOException {
        System.out.println("Testing: Change Password Success -> Forward with Success Message");
        // Arrange
        when(request.getParameter("action")).thenReturn("changePassword");
        when(request.getParameter("email")).thenReturn("user@example.com");
        when(request.getParameter("currentPassword")).thenReturn("oldPassword123");
        when(request.getParameter("newPassword")).thenReturn("NewStrongPassword1");
        when(request.getParameter("confirmNewPassword")).thenReturn("NewStrongPassword1");

        User mockUser = new User();
        when(session.getAttribute("user")).thenReturn(mockUser);

        when(userDao.checkPassword("user@example.com", "oldPassword123")).thenReturn(true);
        when(userDao.updatePassword("user@example.com", "NewStrongPassword1")).thenReturn(true);

        // Act
        authController.doPost(request, response);

        // Verify
        verify(userDao).checkPassword("user@example.com", "oldPassword123");
        verify(userDao).updatePassword("user@example.com", "NewStrongPassword1");
        verify(request).setAttribute("sucessfullyMessage", "Đổi mật khẩu thành công!");
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Đăng xuất (Logout)
    // ===================================================================================
    @Test
    public void testLogout_InvalidatesSessionAndRedirects() throws IOException, ServletException {
        System.out.println("Testing: Logout -> Invalidate Session and Redirect");
        // Arrange
        when(request.getParameter("action")).thenReturn("logout");

        // Act
        authController.doGet(request, response);

        // Verify
        verify(session).invalidate();
        verify(response).sendRedirect("login.jsp");
    }

    /**
     * Mục đích: Kiểm tra trường hợp đặt lại mật khẩu thất bại do mật khẩu mới
     * quá yếu.
     */
    @Test
    public void testResetPassword_Failed_WeakPassword() throws ServletException, IOException {
        System.out.println("Testing: Reset Password Failed (Weak Password) -> Forward with Error");
        // Arrange
        when(request.getParameter("action")).thenReturn("resetPassword");
        when(request.getParameter("newPassword")).thenReturn("12345"); // Mật khẩu yếu, không đủ 8 ký tự
        when(request.getParameter("confirmPassword")).thenReturn("12345");

        when(session.getAttribute("email")).thenReturn("existing@example.com");

        // Act
        authController.doPost(request, response);

        // Verify
        verify(userDao, never()).updatePassword(anyString(), anyString()); // Đảm bảo không gọi DB
        verify(request).setAttribute("error", "Mật khẩu phải dài ít nhất 8 ký tự, chứa chữ hoa, chữ thường và số.");
        verify(requestDispatcher).forward(request, response);
    }

    /**
     * Mục đích: Kiểm tra chức năng gửi lại mã OTP hoạt động thành công.
     */
    @Test
    public void testResendOTP_Success() throws ServletException, IOException {
        System.out.println("Testing: Resend OTP Success -> Forward to Verify OTP with Message");
        // Arrange
        when(request.getParameter("action")).thenReturn("resendOTP");
        when(session.getAttribute("email")).thenReturn("existing@example.com");

        // Act
        authController.doGet(request, response);

        // Verify
        // Kiểm tra một mã OTP MỚI đã được đặt vào session
        verify(session).setAttribute(eq("otp"), anyString());
        verify(session).setAttribute(eq("otpExpiresAt"), any(LocalDateTime.class));

        // Kiểm tra thông báo thành công được gửi cho người dùng
        verify(request).setAttribute("message", "Một mã OTP mới đã được gửi đến email của bạn!");

        // Kiểm tra người dùng được đưa trở lại trang xác thực OTP
        verify(requestDispatcher).forward(request, response);
    }
}
