// File: vn/edu/fpt/controller/EditCustomerController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.*;
import vn.edu.fpt.model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "EditCustomerController", urlPatterns = {"/editCustomer"})
public class EditCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("listCustomer");
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();
            UserDAO userDAO = new UserDAO();

            // Lấy thông tin khách hàng hiện tại để điền vào form
            Enterprise customer = enterpriseDAO.getEnterpriseById(enterpriseId);
            
            // Lấy danh sách tất cả các loại khách hàng và nhân viên cho dropdowns
            List<CustomerType> allCustomerTypes = customerTypeDAO.getAllCustomerTypes();
            List<User> allEmployees = userDAO.getAllEmployees();

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng.");
            }
            
            request.setAttribute("customer", customer);
            request.setAttribute("allCustomerTypes", allCustomerTypes);
            request.setAttribute("allEmployees", allEmployees);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu để chỉnh sửa: " + e.getMessage());
        }

        request.getRequestDispatcher("/jsp/sales/editCustomerDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        Connection conn = null;
        int enterpriseId = Integer.parseInt(request.getParameter("enterpriseId"));
        
        try {
            // Lấy dữ liệu đã được chỉnh sửa từ form
            String customerName = request.getParameter("name");
            String taxCode = request.getParameter("taxCode");
            String bankNumber = request.getParameter("bankNumber");
            int customerTypeId = Integer.parseInt(request.getParameter("customerTypeId"));
            
            // Thông tin liên hệ chính
            String contactName = request.getParameter("contactName");
            String contactPhone = request.getParameter("contactPhone");
            String contactEmail = request.getParameter("contactEmail");

            // Nhân viên phụ trách mới
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            
            // Bắt đầu transaction
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);
            
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            EnterpriseContactDAO contactDAO = new EnterpriseContactDAO();
            EnterpriseAssignmentDAO assignmentDAO = new EnterpriseAssignmentDAO();

            // 1. Cập nhật bảng Enterprises
            Enterprise enterpriseToUpdate = new Enterprise();
            enterpriseToUpdate.setId(enterpriseId);
            enterpriseToUpdate.setName(customerName);
            enterpriseToUpdate.setTaxCode(taxCode);
            enterpriseToUpdate.setBankNumber(bankNumber);
            enterpriseToUpdate.setCustomerTypeId(customerTypeId);
            enterpriseDAO.updateEnterprise(conn, enterpriseToUpdate);

            // 2. Cập nhật bảng EnterpriseContacts (người liên hệ chính)
            EnterpriseContact contactToUpdate = new EnterpriseContact();
            contactToUpdate.setEnterpriseId(enterpriseId);
            contactToUpdate.setFullName(contactName);
            contactToUpdate.setPhoneNumber(contactPhone);
            contactToUpdate.setEmail(contactEmail);
            contactDAO.updateContact(conn, contactToUpdate);
            
            // 3. Cập nhật bảng EnterpriseAssignments
            assignmentDAO.updateMainAssignment(conn, enterpriseId, employeeId);

            conn.commit();
            
            // Chuyển hướng về trang xem chi tiết sau khi lưu thành công
            response.sendRedirect("viewCustomer?id=" + enterpriseId);

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            request.setAttribute("errorMessage", "Lưu thay đổi thất bại: " + e.getMessage());
            doGet(request, response); // Quay lại trang edit với thông báo lỗi
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}
