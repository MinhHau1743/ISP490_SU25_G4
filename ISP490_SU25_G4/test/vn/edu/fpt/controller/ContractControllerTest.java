package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import vn.edu.fpt.model.Product; // Cần import lớp này

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Bộ Unit Test đầy đủ cho ContractController, sử dụng JUnit 4 và Mockito.
 * Áp dụng phương pháp phân loại test case N/A/B:
 * - N: Normal (Trường hợp thông thường)
 * - A: Abnormal (Trường hợp bất thường, lỗi)
 * - B: Boundary (Trường hợp cận biên)
 */
public class ContractControllerTest {

    // --- Khai báo các đối tượng giả lập (Mock) ---
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    // --- Mock các lớp DAO mà Controller phụ thuộc ---
    @Mock
    private ContractDAO contractDAO;
    @Mock
    private FeedbackDAO feedbackDAO;
    @Mock
    private EnterpriseDAO enterpriseDAO;
    @Mock
    private ProductDAO productDAO;
    @Mock
    private UserDAO userDAO;

    // Controller sẽ được khởi tạo trong setUp()
    private ContractController controller;

    @Before
    public void setUp() {
        // Khởi tạo tất cả các @Mock
        MockitoAnnotations.openMocks(this);

        // QUAN TRỌNG: Khởi tạo controller bằng tay và "tiêm" các DAO giả vào.
        // Điều này cho phép chúng ta kiểm soát hoàn toàn hành vi của lớp dữ liệu.
        controller = new ContractController(contractDAO, feedbackDAO, enterpriseDAO, productDAO, userDAO);

        // Thiết lập các hành vi chung cho mock
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/app"); // Giả lập context path
    }

    // ===================================================================================
    // ## Test chức năng Hiển thị danh sách hợp đồng (listContracts)
    // ===================================================================================

    /**
     * [N] Test hiển thị danh sách hợp đồng thành công với dữ liệu.
     */
    @Test
    public void testListContracts_Normal_ShouldDisplayContracts() throws Exception {
        System.out.println("Testing: [N] List Contracts - Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("list");
        when(request.getParameter("page")).thenReturn("1");

        List<Contract> sampleList = new ArrayList<>();
        sampleList.add(new Contract());
        sampleList.add(new Contract());

        when(contractDAO.getContracts(any(), any(), any(), any(), eq(1), anyInt())).thenReturn(sampleList);
        when(contractDAO.getContractCount(any(), any(), any(), any())).thenReturn(2);

        // Act
        controller.doGet(request, response);

        // Verify
        verify(request).setAttribute("contractList", sampleList);
        verify(request).setAttribute("currentPage", 1);
        verify(request).setAttribute("totalPages", 1);
        verify(requestDispatcher).forward(request, response);
    }

    /**
     * [B] Test hiển thị danh sách hợp đồng khi không có dữ liệu trả về.
     */
    @Test
    public void testListContracts_Boundary_ShouldHandleEmptyList() throws Exception {
        System.out.println("Testing: [B] List Contracts - Empty List");
        // Arrange
        when(request.getParameter("action")).thenReturn("list");
        when(request.getParameter("page")).thenReturn("1");

        // Giả lập DAO trả về danh sách rỗng
        when(contractDAO.getContracts(any(), any(), any(), any(), eq(1), anyInt())).thenReturn(Collections.emptyList());
        when(contractDAO.getContractCount(any(), any(), any(), any())).thenReturn(0);

        // Act
        controller.doGet(request, response);

        // Verify
        verify(request).setAttribute("contractList", Collections.emptyList());
        verify(request).setAttribute("totalContracts", 0);
        verify(request).setAttribute("totalPages", 0);
        verify(requestDispatcher).forward(request, response);
    }
    
    // ===================================================================================
    // ## Test chức năng Xem chi tiết hợp đồng (viewContract)
    // ===================================================================================

    /**
     * [N] Test xem chi tiết một hợp đồng tồn tại.
     */
    @Test
    public void testViewContract_Normal_ShouldDisplayDetails() throws Exception {
        System.out.println("Testing: [N] View Contract - Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("101");
        
        Contract mockContract = new Contract();
        mockContract.setId(101L);
        mockContract.setTotalValue(new BigDecimal("1100.00")); // Giá trị có VAT
        
        when(contractDAO.getContractById(101)).thenReturn(mockContract);
        when(feedbackDAO.feedbackExistsForContract(101L)).thenReturn(false);

        // Act
        controller.doGet(request, response);
        
        // Verify
        verify(contractDAO).getContractById(101);
        verify(request).setAttribute("contract", mockContract);
        verify(request).setAttribute(eq("subtotal"), any(BigDecimal.class)); // Kiểm tra đã tính toán subtotal
        verify(requestDispatcher).forward(request, response);
    }
    
    /**
     * [A] Test xem chi tiết một hợp đồng không tồn tại.
     */
    @Test
    public void testViewContract_Abnormal_ContractNotFound() throws Exception {
        System.out.println("Testing: [A] View Contract - Not Found");
        // Arrange
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("999");
        
        when(contractDAO.getContractById(999)).thenReturn(null); // Giả lập không tìm thấy

        // Act
        controller.doGet(request, response);
        
        // Verify
        verify(contractDAO).getContractById(999);
        // Controller vẫn forward, nhưng JSP sẽ xử lý việc hiển thị "không tìm thấy"
        verify(request, never()).setAttribute(eq("contract"), any()); 
        verify(requestDispatcher).forward(request, response);
    }

    // ===================================================================================
    // ## Test chức năng Xóa hợp đồng (deleteContract)
    // ===================================================================================

    /**
     * [N] Test xóa hợp đồng thành công bởi người dùng có quyền.
     */
    @Test
    public void testDeleteContract_Normal_AuthorizedUser() throws Exception {
        System.out.println("Testing: [N] Delete Contract - Authorized");
        // Arrange
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("123");
        when(session.getAttribute("userRole")).thenReturn("Admin"); // Có quyền
        when(contractDAO.softDeleteContract(123)).thenReturn(true);

        // Act
        controller.doGet(request, response);

        // Verify
        verify(contractDAO).softDeleteContract(123);
        verify(session).setAttribute("successMessage", "Đã xóa hợp đồng thành công!");
        verify(response).sendRedirect("contract?action=list");
    }

    /**
     * [A] Test xóa hợp đồng thất bại do không có quyền.
     */
    @Test
    public void testDeleteContract_Abnormal_UnauthorizedUser() throws Exception {
        System.out.println("Testing: [A] Delete Contract - Unauthorized");
        // Arrange
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("123");
        when(session.getAttribute("userRole")).thenReturn("Nhân viên"); // Không có quyền

        // Act
        controller.doGet(request, response);

        // Verify
        // Xác minh rằng controller đã gửi lỗi 403 Forbidden
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
        // Quan trọng: Đảm bảo không có lệnh xóa nào được gọi đến DAO
        verify(contractDAO, never()).softDeleteContract(anyInt());
    }

    // ===================================================================================
    // ## Test chức năng Lưu hợp đồng mới (saveContract)
    // ===================================================================================

    /**
     * [N] Test lưu hợp đồng mới thành công với dữ liệu hợp lệ và quyền Admin.
     */
    @Test
    public void testSaveContract_Normal_ValidDataAndAuthorized() throws Exception {
        System.out.println("Testing: [N] Save Contract - Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("save");
        when(session.getAttribute("userRole")).thenReturn("Admin");
        
        // Giả lập dữ liệu hợp lệ từ form
        when(request.getParameter("contractCode")).thenReturn("HD-001");
        when(request.getParameter("contractName")).thenReturn("Hợp đồng ABC");
        when(request.getParameter("enterpriseId")).thenReturn("1");
        when(request.getParameter("createdById")).thenReturn("10");
        when(request.getParameter("signedDate")).thenReturn("2025-08-18");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2026-09-01");
        when(request.getParameter("statusId")).thenReturn("1");
        when(request.getParameter("notes")).thenReturn("Ghi chú");
        when(request.getParameter("totalValue")).thenReturn("5000000");
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1"}); // Dữ liệu sản phẩm
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"10"});

        // ====> PHẦN SỬA LỖI <====
        // Dạy cho productDAO giả lập: Khi có ai hỏi sản phẩm ID 1,
        // hãy trả về một đối tượng Product hợp lệ.
        Product mockProduct = new Product();
        mockProduct.setId(1);
        mockProduct.setName("Sản phẩm Test");
        mockProduct.setProductCode("SP-TEST");
        mockProduct.setPrice(new BigDecimal("500000"));
        when(productDAO.getProductById(1)).thenReturn(mockProduct);
        // ====> KẾT THÚC PHẦN SỬA LỖI <====

        // Giả lập DAO tạo hợp đồng thành công
        when(contractDAO.createContractWithItems(any(Contract.class), anyList())).thenReturn(true);

        // Act
        controller.doPost(request, response);

        // Verify
        verify(contractDAO).createContractWithItems(any(Contract.class), anyList());
        verify(session).setAttribute(eq("successMessage"), anyString());
        verify(response).sendRedirect("contract?action=list");
    }

    /**
     * [A] Test lưu hợp đồng mới thất bại do dữ liệu không hợp lệ (ngày sai).
     */
    @Test
    public void testSaveContract_Abnormal_InvalidDate() throws Exception {
        System.out.println("Testing: [A] Save Contract - Invalid Date");
        // Arrange
        when(request.getParameter("action")).thenReturn("save");
        when(session.getAttribute("userRole")).thenReturn("Chánh văn phòng");
        
        // Giả lập dữ liệu không hợp lệ: ngày hết hạn trước ngày hiệu lực
        when(request.getParameter("contractCode")).thenReturn("HD-002");
        when(request.getParameter("contractName")).thenReturn("Hợp đồng XYZ");
        when(request.getParameter("enterpriseId")).thenReturn("2");
        when(request.getParameter("createdById")).thenReturn("11");
        when(request.getParameter("signedDate")).thenReturn("2025-08-18");
        when(request.getParameter("startDate")).thenReturn("2025-09-01");
        when(request.getParameter("endDate")).thenReturn("2025-08-30"); // Lỗi ở đây
        when(request.getParameter("statusId")).thenReturn("1");
        when(request.getParameter("notes")).thenReturn("");
        when(request.getParameter("totalValue")).thenReturn("100000");

        // Act
        controller.doPost(request, response);

        // Verify
        // Đảm bảo không có lệnh lưu nào được gọi đến DAO
        verify(contractDAO, never()).createContractWithItems(any(Contract.class), anyList());
        // Kiểm tra xem controller có gửi lại thông báo lỗi không
        verify(request).setAttribute(eq("errorMessages"), anyList());
        // Người dùng được trả về trang tạo hợp đồng để sửa lỗi
        verify(requestDispatcher).forward(request, response);
    }
}