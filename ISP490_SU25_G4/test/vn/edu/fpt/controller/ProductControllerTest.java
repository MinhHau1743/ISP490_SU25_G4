package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.model.Product;
import static org.mockito.Mockito.*;

/**
 * Bộ Unit Test đầy đủ và hoàn thiện cho ProductController. Yêu cầu: Cả 'Admin'
 * và 'Kỹ thuật' đều có quyền Thêm/Sửa/Xóa.
 */
public class ProductControllerTest {

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

    @InjectMocks
    private ProductController productController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("");
        when(request.getSession()).thenReturn(session);
    }

    // ===================================================================================
    // ## Test các chức năng xem và liệt kê (Read-only)
    // ===================================================================================
    @Test
    public void testDoGet_WhenActionIsList_ShouldForwardToListPage() throws ServletException, IOException {
        System.out.println("Testing: doGet action=list -> Success");
        when(request.getParameter("action")).thenReturn("list");
        when(productDao.getProductsWithFilter(any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(new ArrayList<>());
        when(productDao.countProductsWithFilter(any(), any(), any(), any())).thenReturn(0);
        when(productDao.getAllOrigins()).thenReturn(new ArrayList<>());

        productController.doGet(request, response);

        verify(request).getRequestDispatcher("jsp/technicalSupport/listProduct.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ViewProduct_Success() throws ServletException, IOException {
        System.out.println("Testing: doGet action=view with valid ID -> Success");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("1");
        Product mockProduct = new Product();
        mockProduct.setId(1);
        when(productDao.getProductById(1)).thenReturn(mockProduct);

        productController.doGet(request, response);

        verify(request).setAttribute("product", mockProduct);
        verify(request).getRequestDispatcher("/jsp/technicalSupport/viewProductDetail.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet_ViewProduct_NotFound() throws ServletException, IOException {
        System.out.println("Testing: doGet action=view with non-existent ID -> Redirect to 404");
        when(request.getParameter("action")).thenReturn("view");
        when(request.getParameter("id")).thenReturn("999");
        when(productDao.getProductById(999)).thenReturn(null);

        productController.doGet(request, response);

        verify(response).sendRedirect("/404.jsp");
    }

    // ===================================================================================
    // ## Test chức năng Thêm sản phẩm (Create)
    // ===================================================================================
    @Test
    public void testDoPost_CreateProduct_Success_AsAdmin() throws Exception {
        System.out.println("Testing: doPost action=processCreate as Admin -> Success");
        // Arrange
        when(request.getParameter("action")).thenReturn("processCreate");
        when(request.getParameter("name")).thenReturn("Laptop Test");
        when(request.getParameter("productCode")).thenReturn("LT123");
        when(request.getParameter("price")).thenReturn("25000000");
        when(request.getParameter("origin")).thenReturn("USA");
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("Admin");
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
        when(request.getParameter("action")).thenReturn("processCreate");
        when(request.getParameter("name")).thenReturn(""); // Tên trống -> lỗi
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("userRole")).thenReturn("Admin");

        productController.doPost(request, response);

        verify(productDao, never()).insertProduct(any(Product.class));
        verify(request).setAttribute(eq("errors"), any(List.class));
        verify(request).getRequestDispatcher("/jsp/technicalSupport/createProduct.jsp");
    }

    // ===================================================================================
    // ## Test chức năng Sửa sản phẩm (Edit)
    // ===================================================================================
    @Test
    public void testDoPost_EditProduct_Success_AsTechnician() throws Exception {
        System.out.println("Testing: doPost action=processEdit as Technician -> Success");

        // --- PHẦN THIẾT LẬP (ARRANGE) ---
        when(request.getParameter("action")).thenReturn("processEdit");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getParameter("name")).thenReturn("Updated Name");
        when(request.getParameter("price")).thenReturn("15000000");
        when(request.getParameter("origin")).thenReturn("Japan");
        when(request.getParameter("description")).thenReturn("Updated description");

        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("userRole")).thenReturn("Kỹ thuật");

        // Giả lập đối tượng Product cũ trả về từ DB
        Product oldProduct = new Product();
        oldProduct.setId(1);
        oldProduct.setProductCode("SP001"); // Thêm dòng này để cung cấp mã sản phẩm
        when(productDao.getProductById(1)).thenReturn(oldProduct);

        when(request.getPart("image")).thenReturn(filePart);
        when(filePart.getSize()).thenReturn(0L);
        when(productDao.editProduct(any(Product.class))).thenReturn(true);

        // --- HÀNH ĐỘNG (ACT) ---
        productController.doPost(request, response);

        // --- XÁC MINH (VERIFY) ---
        verify(productDao).editProduct(any(Product.class));
        verify(response).sendRedirect(contains("/product?action=list"));
    }

    // ===================================================================================
    // ## Test chức năng Xóa sản phẩm (Delete)
    // ===================================================================================
    @Test
    public void testDoGet_DeleteProduct_Success_AsAdmin() throws IOException, ServletException {
        System.out.println("Testing: doGet action=delete as Admin -> Success");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");
        when(session.getAttribute("userId")).thenReturn(10);
        when(session.getAttribute("userRole")).thenReturn("Admin");

        productController.doGet(request, response);

        verify(productDao).softDeleteProduct(1, 10);
        verify(response).sendRedirect(request.getContextPath() + "/product?action=list");
    }

    @Test
    public void testDoGet_DeleteProduct_Success_AsTechnician() throws IOException, ServletException {
        System.out.println("Testing: doGet action=delete as Technician -> Success");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("2");
        when(session.getAttribute("userId")).thenReturn(11);
        when(session.getAttribute("userRole")).thenReturn("Kỹ thuật");

        productController.doGet(request, response);

        verify(productDao).softDeleteProduct(2, 11);
        verify(response).sendRedirect(request.getContextPath() + "/product?action=list");
    }

    // ===================================================================================
    // ## Test các trường hợp phân quyền và bảo mật (Security)
    // ===================================================================================
    @Test
    public void testDoGet_DeleteProduct_Failed_UnauthorizedRole() throws IOException, ServletException {
        System.out.println("Testing: doGet action=delete as unauthorized role -> Failed");
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("3");
        when(session.getAttribute("userId")).thenReturn(12);
        when(session.getAttribute("userRole")).thenReturn("Kinh doanh"); // Vai trò không được phép

        productController.doGet(request, response);

        verify(productDao, never()).softDeleteProduct(anyInt(), anyInt());
        verify(response).sendRedirect(request.getContextPath() + "/product?action=list");
    }

    @Test
    public void testDoPost_CreateProduct_Failed_UnauthorizedRole() throws Exception {
        System.out.println("Testing: doPost action=processCreate with unauthorized role -> Forbidden");
        when(request.getParameter("action")).thenReturn("processCreate");
        when(session.getAttribute("userId")).thenReturn(12);
        when(session.getAttribute("userRole")).thenReturn("Kinh doanh"); // Vai trò không được phép

        productController.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
        verify(productDao, never()).insertProduct(any(Product.class));
    }

    @Test
    public void testDoPost_Action_Failed_NotLoggedIn() throws Exception {
        System.out.println("Testing: doPost for create/edit when not logged in -> Redirect to login");
        when(request.getParameter("action")).thenReturn("processCreate");
        when(session.getAttribute("userId")).thenReturn(null); // Giả lập chưa đăng nhập

        productController.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/login.jsp");
        verify(productDao, never()).insertProduct(any(Product.class));
    }
}
