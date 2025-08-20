package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.User;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Bộ Unit Test cho ContractController, tập trung vào kiểm thử logic xử lý dữ liệu đầu vào.
 */
public class ContractControllerTest {

    // --- Khai báo các đối tượng giả lập (Mock) ---
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;
    @Mock private RequestDispatcher requestDispatcher;

    // --- Mock các lớp DAO ---
    @Mock private ContractDAO contractDAO;
    @Mock private FeedbackDAO feedbackDAO;
    @Mock private EnterpriseDAO enterpriseDAO;
    @Mock private ProductDAO productDAO;
    @Mock private UserDAO userDAO;

    private ContractController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ContractController(contractDAO, feedbackDAO, enterpriseDAO, productDAO, userDAO);

        // Thiết lập các hành vi chung cho mock
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/app");
        
        // Luôn giả lập là người dùng có quyền đã đăng nhập cho các test case này
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setRoleName("Admin");
        when(session.getAttribute("user")).thenReturn(mockUser);
    }

    // ===================================================================================
    // ## Test chức năng Lưu hợp đồng mới (saveContract)
    // ===================================================================================

    /**
     * [N] Test tạo mới thành công với dữ liệu đầu vào hợp lệ.
     */
    @Test
    public void testSaveContract_Normal_ValidInput() throws Exception {
        System.out.println("Testing: [N] Save Contract - Valid Input");
        // Arrange
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("contractCode")).thenReturn("HD-VALID");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-20");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2026-09-01");
        when(request.getParameter("statusId")).thenReturn("1");
        when(request.getParameter("totalValue")).thenReturn("1000000");
        when(contractDAO.createContractWithItems(any(Contract.class), anyList())).thenReturn(true);

        // Act
        controller.doPost(request, response);

        // Assert
        verify(contractDAO).createContractWithItems(any(Contract.class), anyList());
        verify(response).sendRedirect("contract?action=list");
    }

    /**
     * [A] Test tạo mới thất bại do thiếu trường bắt buộc (Mã hợp đồng).
     */
    @Test
    public void testSaveContract_Abnormal_MissingRequiredField() throws Exception {
        System.out.println("Testing: [A] Save Contract - Missing Contract Code");
        // Arrange
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("contractCode")).thenReturn(""); // Mã hợp đồng trống
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-20");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2026-09-01");
        when(request.getParameter("statusId")).thenReturn("1");
        when(request.getParameter("totalValue")).thenReturn("1000000");
        
        // Act
        controller.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessages"), anyList());
        verify(requestDispatcher).forward(request, response); // Trả về form để sửa lỗi
        verify(contractDAO, never()).createContractWithItems(any(), any());
    }
    
    /**
     * [B] Test tạo mới thất bại do ngày hết hạn không hợp lệ (trước ngày bắt đầu).
     */
    @Test
    public void testSaveContract_Boundary_InvalidDateRange() throws Exception {
        System.out.println("Testing: [B] Save Contract - Invalid Date Range");
        // Arrange
        when(request.getParameter("action")).thenReturn("save");
        when(request.getParameter("contractCode")).thenReturn("HD-INVALID-DATE");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-20");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2025-08-30"); // Lỗi ở đây
        when(request.getParameter("statusId")).thenReturn("1");
        when(request.getParameter("totalValue")).thenReturn("100000");

        // Act
        controller.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessages"), anyList());
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Cập nhật hợp đồng (updateContract)
    // ===================================================================================

    /**
     * [N] Test cập nhật thành công với dữ liệu đầu vào hợp lệ.
     */
    @Test
    public void testUpdateContract_Normal_ValidInput() throws Exception {
        System.out.println("Testing: [N] Update Contract - Valid Input");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("contractCode")).thenReturn("HD-UPDATED");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-20");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2026-09-01");
        when(request.getParameter("statusId")).thenReturn("2");
        when(request.getParameter("totalValue")).thenReturn("999999");
        when(contractDAO.updateContractWithItems(any(Contract.class), anyList())).thenReturn(true);

        // Act
        controller.doPost(request, response);

        // Assert
        verify(contractDAO).updateContractWithItems(any(Contract.class), anyList());
        verify(response).sendRedirect("contract?action=list");
    }

    /**
     * [A] Test cập nhật thất bại do ID không phải là số.
     */
    @Test
    public void testUpdateContract_Abnormal_InvalidIdFormat() throws Exception {
        System.out.println("Testing: [A] Update Contract - Invalid ID Format");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("invalid-id"); // ID không phải số

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("contract?action=edit&id=invalid-id"));
        verify(contractDAO, never()).updateContractWithItems(any(), any());
    }

    /**
     * [B] Test cập nhật thất bại do một trường ID khác (enterpriseId) bị bỏ trống.
     */
    @Test
    public void testUpdateContract_Boundary_MissingForeignKey() throws Exception {
        System.out.println("Testing: [B] Update Contract - Missing Foreign Key");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("contractCode")).thenReturn("HD-FAIL");
        when(request.getParameter("enterpriseId")).thenReturn(""); // ID khách hàng trống
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-20");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2026-09-01");
        when(request.getParameter("statusId")).thenReturn("2");
        when(request.getParameter("totalValue")).thenReturn("999999");

        // Act
        controller.doPost(request, response);

        // Assert
        // Controller sẽ bắt NumberFormatException và trả về form với lỗi
        verify(response).sendRedirect(contains("contract?action=edit&id=101"));
    }
    
    
}