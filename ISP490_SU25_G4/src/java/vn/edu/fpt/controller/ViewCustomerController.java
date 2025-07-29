package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.dao.ContractDAO; // Import a class mới
import vn.edu.fpt.model.Contract;   // Import a class mới

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ViewCustomerController", urlPatterns = {"/viewCustomer"})
public class ViewCustomerController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("listCustomer");
            return;
        }

        try {
            int enterpriseId = Integer.parseInt(idStr);
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            Enterprise customer = enterpriseDAO.getEnterpriseById(enterpriseId);

            if (customer == null) {
                request.setAttribute("errorMessage", "Không tìm thấy khách hàng với ID cung cấp.");
            } else {
                // Lấy 3 yêu cầu kỹ thuật gần nhất
                TechnicalRequestDAO requestDAO = new TechnicalRequestDAO();
                List<TechnicalRequest> recentRequests = requestDAO.getRecentRequestsByEnterprise(enterpriseId, 3);
                request.setAttribute("recentRequests", recentRequests);
                
                // *** LOGIC MỚI: LẤY 5 HỢP ĐỒNG GẦN NHẤT ***
                ContractDAO contractDAO = new ContractDAO();
                List<Contract> recentContracts = contractDAO.getRecentContractsByEnterpriseId(enterpriseId, 5);
                request.setAttribute("recentContracts", recentContracts);
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