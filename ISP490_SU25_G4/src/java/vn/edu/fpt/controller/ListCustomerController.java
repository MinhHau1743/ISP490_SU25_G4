// src/java/vn/edu/fpt/controller/ListCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CustomerTypeDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Enterprise;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ListCustomerController", urlPatterns = {"/listCustomer"})
public class ListCustomerController extends HttpServlet {

    private static final int PAGE_SIZE = 10; // Số khách hàng mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Lấy tham số lọc và phân trang
            String searchQuery = request.getParameter("search");
            String customerTypeId = request.getParameter("customerTypeId");
            String employeeId = request.getParameter("employeeId");
            String provinceId = request.getParameter("provinceId");
            String districtId = request.getParameter("districtId");
            String wardId = request.getParameter("wardId");
            String pageStr = request.getParameter("page");

            int page = 1;
            if (pageStr != null && !pageStr.isEmpty()) {
                try {
                    page = Integer.parseInt(pageStr);
                } catch (NumberFormatException e) {
                    page = 1; // Mặc định về trang 1 nếu tham số không hợp lệ
                }
            }

            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();

            // Lấy danh sách khách hàng đã được phân trang
            List<Enterprise> customerList = enterpriseDAO.getPaginatedActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId, page, PAGE_SIZE);

            // Đếm tổng số khách hàng thỏa mãn điều kiện lọc để tính tổng số trang
            int totalCustomers = enterpriseDAO.countActiveEnterprises(
                    searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);
            
            int totalPages = (int) Math.ceil((double) totalCustomers / PAGE_SIZE);

            // Kiểm tra và đặt thông báo "Không tìm thấy kết quả"
            boolean isAction = (searchQuery != null && !searchQuery.isEmpty()) || (customerTypeId != null && !customerTypeId.isEmpty()) || (employeeId != null && !employeeId.isEmpty()) || (provinceId != null && !provinceId.isEmpty());
            if (isAction && customerList.isEmpty()) {
                request.setAttribute("noResultsFound", true);
            }

            // Lấy dữ liệu cho các dropdown bộ lọc
            request.setAttribute("allProvinces", new AddressDAO().getAllProvinces());
            request.setAttribute("allCustomerTypes", new CustomerTypeDAO().getAllCustomerTypes());
            request.setAttribute("allEmployees", new UserDAO().getAllEmployees());

            // Gửi dữ liệu sang JSP
            request.setAttribute("customerList", customerList);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentPage", page);
            
            // Gửi lại các giá trị đã lọc để hiển thị trên form
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("selectedCustomerTypeId", customerTypeId);
            request.setAttribute("selectedEmployeeId", employeeId);
            request.setAttribute("selectedProvinceId", provinceId);
            request.setAttribute("selectedDistrictId", districtId);
            request.setAttribute("selectedWardId", wardId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải danh sách khách hàng: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/listCustomer.jsp").forward(request, response);
    }
}