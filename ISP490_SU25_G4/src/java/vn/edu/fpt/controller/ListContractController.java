package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.model.Contract;

import java.io.IOException;
import java.util.List;

/**
 * Servlet controller để hiển thị danh sách hợp đồng với các chức năng tìm kiếm,
 * lọc và phân trang.
 *
 * @author datnt (Refactored by Gemini)
 */
@WebServlet(name = "ListContractController", urlPatterns = {"/listContract"})
public class ListContractController extends HttpServlet {

    private static final int PAGE_SIZE = 10; // Số hợp đồng trên mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Nhận các tham số lọc và phân trang từ request
        String searchQuery = request.getParameter("searchQuery");
        String status = request.getParameter("status");
        String startDateFrom = request.getParameter("startDateFrom");
        String startDateTo = request.getParameter("startDateTo");
        String pageStr = request.getParameter("page");

        // 2. Xử lý giá trị mặc định cho trang, mặc định là trang 1
        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                // Ghi log lỗi nếu cần và giữ nguyên trang là 1
                System.err.println("Lỗi phân tích số trang: " + e.getMessage());
            }
        }

        // 3. Tương tác với DAO để lấy dữ liệu
        ContractDAO dao = new ContractDAO();
        List<Contract> contractList = dao.getContracts(searchQuery, status, startDateFrom, startDateTo, page, PAGE_SIZE);
        int totalContracts = dao.getContractCount(searchQuery, status, startDateFrom, startDateTo);

        // 4. Tính toán số trang
        int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);

        // 5. Đặt các thuộc tính vào request để JSP có thể truy cập
        // Đảm bảo tên thuộc tính ("contractList") chính xác tuyệt đối
        request.setAttribute("contractList", contractList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalContracts", totalContracts);

        // Gửi lại các tham số lọc để JSP giữ lại trạng thái trên form
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("status", status);
        request.setAttribute("startDateFrom", startDateFrom);
        request.setAttribute("startDateTo", startDateTo);

        // 6. Chuyển tiếp request đến trang JSP để hiển thị
        request.getRequestDispatcher("/jsp/chiefOfStaff/listContract.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu form dùng POST, cũng xử lý như GET
        doGet(request, response);
    }
}
