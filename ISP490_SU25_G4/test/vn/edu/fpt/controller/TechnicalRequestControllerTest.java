package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Bộ Unit Test cho TechnicalRequestController, tập trung vào kiểm thử logic xử
 * lý dữ liệu đầu vào.
 */
public class TechnicalRequestControllerTest {

    // --- Khai báo các đối tượng giả lập (Mock) ---
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    // --- Mock các DAO ---
    @Mock
    private TechnicalRequestDAO technicalRequestDAO;
    @Mock
    private ScheduleDAO scheduleDAO;
    @Mock
    private AddressDAO addressDAO;
    @Mock
    private StatusDAO statusDAO;
    @Mock
    private EnterpriseDAO enterpriseDAO;
    @Mock
    private ContractDAO contractDAO;

    // Controller sẽ được "tiêm" các mock DAO vào
    private TechnicalRequestController controller;
    // === BẮT ĐẦU DÁN CODE VÀO ĐÂY ===

    /**
     * Hàm tiện ích để tạo một đối tượng User giả lập cho session.
     *
     * @param id ID của người dùng giả
     * @param roleName Vai trò của người dùng (ví dụ: "Admin")
     * @return một đối tượng User giả.
     */
    private User createMockUser(int id, String roleName) {
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setRoleName(roleName);
        return mockUser;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo controller bằng constructor mới để tiêm các mock DAO.
        controller = new TechnicalRequestController(
                technicalRequestDAO, scheduleDAO, addressDAO,
                enterpriseDAO, null, statusDAO, contractDAO // FeedbackDAO không dùng trong 2 hàm này
        );

        // Thiết lập các hành vi chung cho mock
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/app");

        // Luôn giả lập là người dùng đã đăng nhập cho các test case này
        User mockUser = new User();
        mockUser.setId(1);
        when(session.getAttribute("user")).thenReturn(mockUser);
    }

    // ===================================================================================
    // ## Test chức năng Tạo mới Phiếu yêu cầu (createTicket)
    // ===================================================================================
    /**
     * [N] Test tạo mới thành công với dữ liệu đầu vào hợp lệ.
     */
    @Test
    public void testCreateTicket_Normal_ValidInput() throws Exception {
        System.out.println("Testing: [N] Create Ticket - Valid Input");
        // Arrange
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Yêu cầu hợp lệ");
        when(request.getParameter("description")).thenReturn("Mô tả hợp lệ");
        when(request.getParameter("priority")).thenReturn("medium");
        when(request.getParameter("status")).thenReturn("Sắp tới");
        when(request.getParameter("scheduled_date")).thenReturn("2025-08-22");
        when(request.getParameter("province")).thenReturn("1");
        when(request.getParameter("district")).thenReturn("1");
        when(request.getParameter("ward")).thenReturn("1");
        when(request.getParameter("streetAddress")).thenReturn("Địa chỉ hợp lệ");
        when(request.getParameter("isBillable")).thenReturn("false");

        // Giả lập DAO hoạt động thành công
        when(technicalRequestDAO.createTechnicalRequest(any(), any())).thenReturn(1);

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect("ticket?action=list&create=success");
    }

    /**
     * [A] Test tạo mới thất bại do thiếu trường bắt buộc (Tiêu đề).
     */
    @Test
    public void testCreateTicket_Abnormal_MissingTitle() throws Exception {
        System.out.println("Testing: [A] Create Ticket - Missing Title");
        // Arrange
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn(""); // Title trống

        // Act
        controller.doPost(request, response);

        // Assert: Controller nên bắt lỗi và chuyển hướng về trang tạo với thông báo lỗi
        verify(response).sendRedirect(contains("ticket?action=create&error="));
        verify(technicalRequestDAO, never()).createTechnicalRequest(any(), any()); // Đảm bảo không gọi DAO
    }

    /**
     * [B] Test tạo mới thất bại do định dạng ngày không hợp lệ.
     */
    @Test
    public void testCreateTicket_Boundary_InvalidDateFormat() throws Exception {
        System.out.println("Testing: [B] Create Ticket - Invalid Date Format");
        // Arrange
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Yêu cầu");
        when(request.getParameter("scheduled_date")).thenReturn("ngay-khong-hop-le"); // Định dạng sai

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect(contains("ticket?action=create&error=DateTimeParseException"));
    }

    // ===================================================================================
    // ## Test chức năng Cập nhật Phiếu yêu cầu (updateTicket)
    // ===================================================================================
    /**
     * [N] Test cập nhật thành công với dữ liệu đầu vào hợp lệ.
     */
    @Test
    public void testUpdateTicket_Normal_ValidInput() throws Exception {
        System.out.println("Testing: [N] Update Ticket - Valid Input");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("scheduleId")).thenReturn("202");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("contractId")).thenReturn("101");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Tiêu đề đã cập nhật");
        when(request.getParameter("description")).thenReturn("Mô tả đã cập nhật");
        when(request.getParameter("priority")).thenReturn("medium");
        when(request.getParameter("status")).thenReturn("Đang thực hiện");
        when(request.getParameter("scheduled_date")).thenReturn("2025-08-23");
        when(request.getParameter("province")).thenReturn("1");
        when(request.getParameter("district")).thenReturn("1");
        when(request.getParameter("ward")).thenReturn("1");
        when(request.getParameter("streetAddress")).thenReturn("123 Đường XYZ");
        when(request.getParameter("isBillable")).thenReturn("false");

        // Giả lập DAO cập nhật thành công
        when(technicalRequestDAO.updateTechnicalRequestAndSchedule(any(), any(), any())).thenReturn(true);

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect(request.getContextPath() + "/ticket?action=view&id=101&update=success");
    }

    /**
     * [A] Test cập nhật thất bại do ID không phải là số.
     */
    @Test
    public void testUpdateTicket_Abnormal_InvalidIdFormat() throws Exception {
        System.out.println("Testing: [A] Update Ticket - Invalid ID Format");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("invalid-id"); // ID không phải số

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect(request.getContextPath() + "/ticket?action=edit&id=invalid-id&error=unknown");
        verify(technicalRequestDAO, never()).updateTechnicalRequestAndSchedule(any(), any(), any()); // Đảm bảo không gọi DAO
    }

    /**
     * [B] Test cập nhật thất bại do số tiền (amount) không phải là số.
     */
    @Test
    public void testUpdateTicket_Boundary_InvalidAmountFormat() throws Exception {
        System.out.println("Testing: [B] Update Ticket - Invalid Amount Format");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("isBillable")).thenReturn("true");
        when(request.getParameter("amount")).thenReturn("mot-tram-nghin"); // Sai định dạng số

        // Các tham số hợp lệ khác
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("contractId")).thenReturn("101");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Tiêu đề");
        when(request.getParameter("scheduled_date")).thenReturn("2025-08-22");

        // Act
        controller.doPost(request, response);

        // Assert
        verify(response).sendRedirect(request.getContextPath() + "/ticket?action=edit&id=101&error=unknown");
    }

    /**
     * [B] Test tạo mới thành công với dữ liệu hợp lệ nhưng không có thiết bị
     * nào.
     */
    @Test
    public void testCreateTicket_Boundary_NoDevicesAttached() throws Exception {
        System.out.println("Testing: [B] Create Ticket - No Devices");
        // Arrange
        when(request.getParameter("action")).thenReturn("create");
        when(session.getAttribute("user")).thenReturn(createMockUser(1, "Admin"));

        // Dữ liệu hợp lệ, nhưng không có tham số deviceName_1
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Yêu cầu không có thiết bị");
        when(request.getParameter("description")).thenReturn("Mô tả hợp lệ");
        when(request.getParameter("priority")).thenReturn("low");
        when(request.getParameter("status")).thenReturn("Sắp tới");
        when(request.getParameter("scheduled_date")).thenReturn("2025-08-22");
        when(request.getParameter("province")).thenReturn("1");
        when(request.getParameter("district")).thenReturn("1");
        when(request.getParameter("ward")).thenReturn("1");
        when(request.getParameter("streetAddress")).thenReturn("Địa chỉ hợp lệ");
        when(request.getParameter("isBillable")).thenReturn("false");

        when(technicalRequestDAO.createTechnicalRequest(any(), any())).thenReturn(1);

        // Act
        controller.doPost(request, response);

        // Assert
        // Xác minh rằng controller vẫn xử lý thành công và chuyển hướng đúng
        verify(response).sendRedirect("ticket?action=list&create=success");
    }

    /**
     * [A] Test cập nhật thất bại do thiếu ID khách hàng (enterpriseId).
     */
    @Test
    public void testUpdateTicket_Abnormal_MissingEnterpriseId() throws Exception {
        System.out.println("Testing: [A] Update Ticket - Missing Enterprise ID");
        // Arrange
        when(request.getParameter("action")).thenReturn("update");
        when(session.getAttribute("user")).thenReturn(createMockUser(1, "Admin"));
        when(request.getParameter("id")).thenReturn("101");

        // Thiếu enterpriseId
        when(request.getParameter("enterpriseId")).thenReturn(""); // ID khách hàng trống

        // Các tham số hợp lệ khác để tránh các lỗi không liên quan
        when(request.getParameter("serviceId")).thenReturn("1");
        when(request.getParameter("title")).thenReturn("Tiêu đề");
        when(request.getParameter("scheduled_date")).thenReturn("2025-08-22");

        // Act
        controller.doPost(request, response);

        // Assert
        // Controller sẽ bắt lỗi NumberFormatException và chuyển hướng về trang edit với thông báo lỗi
        verify(response).sendRedirect(contains("/ticket?action=edit&id=101&error=unknown"));
        verify(technicalRequestDAO, never()).updateTechnicalRequestAndSchedule(any(), any(), any());
    }

}
