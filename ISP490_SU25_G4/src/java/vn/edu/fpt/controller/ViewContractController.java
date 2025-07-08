package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.ContractProduct;

/**
 * Servlet này xử lý việc hiển thị trang chi tiết của một hợp đồng. Phiên bản
 * này đã được cập nhật để tính toán giá trị trước khi gửi sang JSP.
 *
 * @author datnt (edited by Gemini)
 */
@WebServlet(name = "ViewContractController", urlPatterns = {"/viewContract"})
public class ViewContractController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contractIdStr = request.getParameter("id");

        if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
            response.sendRedirect("listContract");
            return;
        }

        try {
            int contractId = Integer.parseInt(contractIdStr);
            ContractDAO contractDAO = new ContractDAO();

            // 1. Lấy thông tin chính của hợp đồng
            Contract contract = contractDAO.getContractById(contractId);

            if (contract != null) {
                // 2. Lấy danh sách sản phẩm/dịch vụ thuộc hợp đồng
                List<ContractProduct> contractItems = contractDAO.getContractProductsByContractId(contractId);

                // =================================================================
                // BƯỚC 3: TÍNH TOÁN TỔNG PHỤ VÀ VAT TỪ TỔNG CỘNG
                // =================================================================
                BigDecimal grandTotal = contract.getTotalValue();
                BigDecimal subtotal = BigDecimal.ZERO;
                BigDecimal vatAmount = BigDecimal.ZERO;

                // Chỉ tính toán nếu grandTotal có giá trị hợp lệ
                if (grandTotal != null && grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal divisor = new BigDecimal("1.1");
                    // Tổng phụ = Tổng cộng / 1.1 (làm tròn 2 chữ số)
                    subtotal = grandTotal.divide(divisor, 2, RoundingMode.HALF_UP);
                    // Tiền VAT = Tổng cộng - Tổng phụ
                    vatAmount = grandTotal.subtract(subtotal);
                }
                // =================================================================

                // 4. Đặt tất cả các đối tượng vào request để JSP truy cập
                request.setAttribute("contract", contract);
                request.setAttribute("contractItems", contractItems);
                request.setAttribute("subtotal", subtotal);
                request.setAttribute("vatAmount", vatAmount);
                request.setAttribute("grandTotal", grandTotal);
            }

            // 5. Chuyển tiếp yêu cầu đến trang JSP
            request.getRequestDispatcher("/jsp/chiefOfStaff/viewContractDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("listContract");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi tải chi tiết hợp đồng: " + e.getMessage());
            request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to view the details of a specific contract.";
    }
}
