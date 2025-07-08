package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.edu.fpt.dao.ContractDAO;

/**
 * Servlet này xử lý yêu cầu xóa mềm một hợp đồng.
 *
 * @author PC (edited by Gemini)
 */
@WebServlet(name = "DeleteContractController", urlPatterns = {"/deleteContract"})
public class DeleteContractController extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method. Xử lý yêu cầu xóa được gửi từ
     * link trong modal xác nhận.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        try {
            int contractId = Integer.parseInt(idStr);
            ContractDAO dao = new ContractDAO();

            // Gọi phương thức xóa mềm trong DAO
            boolean success = dao.softDeleteContract(contractId);

            // Đặt một thông báo vào session để hiển thị ở trang danh sách sau khi chuyển hướng
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã xóa hợp đồng thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Xóa hợp đồng thất bại do không tìm thấy ID hoặc lỗi CSDL.");
            }

        } catch (NumberFormatException e) {
            // Xử lý trường hợp ID không phải là số
            request.getSession().setAttribute("errorMessage", "ID hợp đồng không hợp lệ.");
        } catch (Exception e) {
            // Xử lý các lỗi khác
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xóa.");
        }

        // Sau khi xử lý xong, luôn chuyển hướng người dùng về trang danh sách
        response.sendRedirect("listContract");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Handles soft deletion of a contract.";
    }
}
