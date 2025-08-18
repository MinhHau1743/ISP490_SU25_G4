package vn.edu.fpt.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.UserDAO;

import java.sql.Connection;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EnterpriseControllerTest {

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private DBContext dbContext;
    @Mock private Connection conn;
    @Mock private EnterpriseDAO enterpriseDAO;
    @Mock private UserDAO userDAO;

    // Bỏ việc khai báo controller ở đây
    // private EnterpriseController controller;

    @Before
    public void setUp() {
        // Chỉ khởi tạo mock, không làm gì khác
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDoPost_WhenCreatingEnterprise_ShouldSucceed() throws Exception {
        // === BƯỚC QUAN TRỌNG: KHỞI TẠO CONTROLLER Ở ĐÂY ===
        // Khởi tạo controller ngay bên trong test case
        EnterpriseController controller = new EnterpriseController(enterpriseDAO, userDAO, null, null, dbContext);

        // 1. Dàn dựng (Arrange)
        // Thiết lập các mock cơ bản
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new Object());
        when(dbContext.getConnection()).thenReturn(conn);
        
        // Thiết lập các mock cho luồng create
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getContextPath()).thenReturn("/project-name");
        when(request.getParameter("customerName")).thenReturn("Final Corp");
        when(request.getParameter("fullName")).thenReturn("Final User");
        when(request.getParameter("province")).thenReturn("1");
        when(request.getParameter("district")).thenReturn("2");
        when(request.getParameter("ward")).thenReturn("3");
        when(request.getParameter("customerGroup")).thenReturn("5");

        // Thiết lập kịch bản cho DAO
        doReturn(false).when(enterpriseDAO).isNameExists(anyString(), any());
        doReturn(100).when(enterpriseDAO).insertAddress(any(), any(), anyInt(), anyInt(), anyInt());
        doReturn(200).when(enterpriseDAO).insertEnterprise(any(), any(), any(), any(), anyInt(), anyInt(), any(), any(), any());
        doNothing().when(enterpriseDAO).insertEnterpriseContact(any(), anyInt(), any(), any(), any(), any());

        // 2. Hành động (Act)
        controller.doPost(request, response);

        // 3. Xác thực (Assert)
        verify(response).sendRedirect("/project-name/customer/list");
        verify(conn).commit();
    }
}