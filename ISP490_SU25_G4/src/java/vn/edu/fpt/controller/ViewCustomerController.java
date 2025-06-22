// File: src/main/java/vn/edu/fpt/controller/ViewCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.model.Enterprise;

import java.io.IOException;

@WebServlet(name = "ViewCustomerController", urlPatterns = {"/viewCustomer"})
public class ViewCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("listCustomer"); // Redirect if no ID is provided
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            EnterpriseDAO dao = new EnterpriseDAO();
            Enterprise customer = dao.getEnterpriseById(enterpriseId);

            if (customer == null) {
                // Customer not found, set an error message and forward
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng với ID cung cấp.");
            }
            
            request.setAttribute("customer", customer);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID khách hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/viewCustomerDetail.jsp").forward(request, response);
    }
}
