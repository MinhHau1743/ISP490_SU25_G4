// File: vn/edu/fpt/controller/DeleteCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.EnterpriseDAO;

import java.io.IOException;

@WebServlet(name = "DeleteCustomerController", urlPatterns = {"/deleteCustomer"})
public class DeleteCustomerController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String customerIdStr = request.getParameter("customerId");

        if (customerIdStr == null || customerIdStr.isEmpty()) {
            session.setAttribute("errorMessage", "Không có ID khách hàng để xóa.");
            response.sendRedirect("listCustomer");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdStr);
            EnterpriseDAO dao = new EnterpriseDAO();
            boolean success = dao.softDeleteEnterprise(customerId);

            if (success) {
                session.setAttribute("successMessage", "Đã xóa khách hàng thành công!");
            } else {
                session.setAttribute("errorMessage", "Không tìm thấy khách hàng để xóa hoặc đã có lỗi xảy ra.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID khách hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Lỗi khi xóa khách hàng: " + e.getMessage());
        }
        
        response.sendRedirect("listCustomer");
    }
}
