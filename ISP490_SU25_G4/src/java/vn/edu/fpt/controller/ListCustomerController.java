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
import vn.edu.fpt.model.CustomerType;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ListCustomerController", urlPatterns = {"/listCustomer"})
public class ListCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Lấy tất cả các tham số lọc từ request
            String searchQuery = request.getParameter("search");
            String customerTypeId = request.getParameter("customerTypeId");
            String employeeId = request.getParameter("employeeId");
            String provinceId = request.getParameter("provinceId");
            String districtId = request.getParameter("districtId");
            String wardId = request.getParameter("wardId");

            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            // Truyền tất cả tham số lọc vào DAO
            List<Enterprise> allEnterprises = enterpriseDAO.getAllActiveEnterprises(searchQuery, customerTypeId, employeeId, provinceId, districtId, wardId);

            if (searchQuery != null && !searchQuery.trim().isEmpty() && allEnterprises.isEmpty()) {
                request.setAttribute("noResultsFound", true);
            }

            // Phân loại khách hàng vào các cột Kanban
            Map<String, List<Enterprise>> customerColumns = new HashMap<>();
            customerColumns.put("vip", new ArrayList<>());
            customerColumns.put("loyal", new ArrayList<>());
            customerColumns.put("potential", new ArrayList<>());
            customerColumns.put("other", new ArrayList<>());

            for (Enterprise enterprise : allEnterprises) {
                String typeName = enterprise.getCustomerTypeName() != null ? enterprise.getCustomerTypeName().toLowerCase() : "";
                if (typeName.contains("vip")) {
                    customerColumns.get("vip").add(enterprise);
                } else if (typeName.contains("thân thiết")) {
                    customerColumns.get("loyal").add(enterprise);
                } else if (typeName.contains("tiềm năng")) {
                    customerColumns.get("potential").add(enterprise);
                } else {
                    customerColumns.get("other").add(enterprise);
                }
            }
            
            // Lấy dữ liệu cho các dropdown bộ lọc
            AddressDAO addressDAO = new AddressDAO();
            CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();
            UserDAO userDAO = new UserDAO();

            request.setAttribute("allProvinces", addressDAO.getAllProvinces());
            request.setAttribute("allCustomerTypes", customerTypeDAO.getAllCustomerTypes());
            request.setAttribute("allEmployees", userDAO.getAllEmployees());

            // Gửi dữ liệu và các giá trị filter đã chọn sang JSP
            request.setAttribute("customerColumns", customerColumns);
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