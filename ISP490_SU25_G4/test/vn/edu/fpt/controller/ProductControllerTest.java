package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

/**
 * Bộ Unit Test cho ProductController. Tương thích với JUnit 4 và Mockito 5.
 */
public class ProductControllerTest {

    // --- Khai báo các đối tượng giả lập (Mock) ---
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession session;
    @Mock
    private Part filePart;
    @Mock
    private ProductDAO productDao;

    // Tự động "tiêm" các mock ở trên vào productController
    @InjectMocks
    private ProductController productController;

    /**
     * Phương thức này chạy trước MỖI bài test (@Test).
     */
    @Before
    public void setUp() {
        // Khởi tạo tất cả các @Mock và @InjectMocks trong class này
        MockitoAnnotations.openMocks(this);

        // Cài đặt các hành vi chung cho các mock
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("");
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testDoGet_WhenActionIsList_ShouldForwardToListPage() throws ServletException, IOException {
        System.out.println("Testing: doGet action=list -> Success");

        // Arrange
        when(request.getParameter("action")).thenReturn("list");
        when(productDao.getProductsWithFilter(any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(productDao.countProductsWithFilter(any(), any(), any(), any())).thenReturn(0);
        when(productDao.getAllOrigins()).thenReturn(new ArrayList<>());

        // Act
        productController.doGet(request, response);

        // Verify
        verify(request).getRequestDispatcher("jsp/technicalSupport/listProduct.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost_CreateProduct_Success() throws Exception {
        System.out.println("Testing: doPost action=processCreate -> Success");

        // Arrange
        when(request.getParameter("action")).thenReturn("processCreate");
        when(request.getParameter("name")).thenReturn("Laptop Test");
        when(request.getParameter("productCode")).thenReturn("LT123");
        when(request.getParameter("origin")).thenReturn("USA");
        when(request.getParameter("price")).thenReturn("25000000");
        when(request.getParameter("description")).thenReturn("Mô tả sản phẩm test");
        when(session.getAttribute("userId")).thenReturn(1);
        when(request.getPart("image")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(100L);
        when(filePart.getSubmittedFileName()).thenReturn("test_image.jpg");
        when(productDao.isProductCodeExists("LT123")).thenReturn(false);
        when(productDao.insertProduct(any(Product.class))).thenReturn(999);

        // Act
        productController.doPost(request, response);

        // Verify
        verify(productDao).insertProduct(any(Product.class));
        verify(productDao).updateProductImage(eq(999), anyString());
        verify(response).sendRedirect("/product?action=list");
    }

    @Test
    public void testDoPost_CreateProduct_ValidationError() throws ServletException, IOException {
        System.out.println("Testing: doPost action=processCreate -> Validation Error");

        // Arrange
        when(request.getParameter("action")).thenReturn("processCreate");
        // SỬA LỖI: Cố tình để trống tên sản phẩm để kiểm tra validation
        when(request.getParameter("name")).thenReturn("");

        // Các tham số khác có thể không cần thiết nếu validation dừng lại sớm
        when(request.getParameter("productCode")).thenReturn("LT123");
        when(session.getAttribute("userId")).thenReturn(1);

        // Act
        productController.doPost(request, response);

        // Verify
        verify(productDao, never()).insertProduct(any(Product.class));
        verify(request).setAttribute(eq("errors"), any(List.class));
        verify(request).getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testGetServletInfo() {
        System.out.println("Testing: getServletInfo");
        String expResult = "Front Controller for Product Management";
        String result = productController.getServletInfo();
        assertEquals(expResult, result);
    }
}
