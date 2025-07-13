package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.model.Enterprise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to handle requests for the customer list page.
 */
@WebServlet(name = "ListCustomerController", urlPatterns = {"/listCustomer"})
public class ListCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới
        if (session == null || session.getAttribute("user") == null) {
            // Nếu chưa đăng nhập, trả về lỗi hoặc chuyển hướng
            // Chuyển hướng là tốt nhất để người dùng có thể đăng nhập lại
            response.sendRedirect("login.jsp");
            return; // Dừng xử lý tiếp theo
        }

        try {
            // === SỬA LỖI: Lấy tham số tìm kiếm từ request ===
            String searchQuery = request.getParameter("search");

            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            // Truyền tham số tìm kiếm vào DAO
            List<Enterprise> allEnterprises = enterpriseDAO.getAllActiveEnterprises(searchQuery);

            // Prepare a map to hold categorized customer lists for the Kanban board
            Map<String, List<Enterprise>> customerColumns = new HashMap<>();
            customerColumns.put("vip", new ArrayList<>());
            customerColumns.put("loyal", new ArrayList<>());
            customerColumns.put("potential", new ArrayList<>());
            customerColumns.put("other", new ArrayList<>()); // A fallback category

            // Categorize each enterprise based on its type name
            for (Enterprise enterprise : allEnterprises) {
                String typeName = enterprise.getCustomerTypeName() != null ? enterprise.getCustomerTypeName().toLowerCase() : "";

                if (typeName.contains("vip")) {
                    customerColumns.get("vip").add(enterprise);
                } else if (typeName.contains("thân thiết")) {
                    customerColumns.get("loyal").add(enterprise);
                } else if (typeName.contains("tiềm năng")) {
                    customerColumns.get("potential").add(enterprise);
                } else {
                    customerColumns.get("other").add(enterprise); // Add to 'other' if no match
                }
            }

            request.setAttribute("customerColumns", customerColumns);
            // Gửi lại từ khóa tìm kiếm để hiển thị trên ô input
            request.setAttribute("searchQuery", searchQuery);

        } catch (Exception e) {
            e.printStackTrace();
            // Set an error message to be displayed on the page
            request.setAttribute("errorMessage", "Không thể tải danh sách khách hàng: " + e.getMessage());
        }

        // Forward the request to the JSP page for rendering
        request.getRequestDispatcher("/jsp/sales/listCustomer.jsp").forward(request, response);
    }
}
