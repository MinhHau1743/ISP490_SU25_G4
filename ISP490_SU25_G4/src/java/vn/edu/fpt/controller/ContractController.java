package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.ContractDAO; // Chỉ cần DAO này cho các hoạt động liên quan đến hợp đồng
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.ContractProduct;
import vn.edu.fpt.model.ContractStatus;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller duy nhất quản lý tất cả các hoạt động CRUD cho Hợp đồng.
 * Phiên bản này đã được cập nhật để tương thích hoàn toàn với cấu trúc CSDL và DAO mới nhất.
 *
 * @author YourName (updated by AI)
 */
@WebServlet(name = "ContractController", urlPatterns = {"/contract"})
public class ContractController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    /**
     * Phương thức trung tâm để điều hướng các action (GET và POST).
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list"; // Mặc định là action "list"
        }

        try {
            switch (action) {
                case "list":
                    listContracts(request, response);
                    break;
                case "view":
                    viewContract(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "save":
                    saveContract(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updateContract(request, response);
                    break;
                case "delete":
                    deleteContract(request, response);
                    break;
                default:
                    listContracts(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/jsp/errorPage.jsp").forward(request, response);
        }
    }

    /**
     * Hiển thị danh sách hợp đồng có phân trang và bộ lọc.
     */
    private void listContracts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String searchQuery = request.getParameter("searchQuery");
        String statusId = request.getParameter("statusId");
        String startDateFrom = request.getParameter("startDateFrom");
        String startDateTo = request.getParameter("startDateTo");
        String pageStr = request.getParameter("page");

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) { /* Bỏ qua, dùng giá trị mặc định */ }
        }

        ContractDAO contractDAO = new ContractDAO();

        List<Contract> contractList = contractDAO.getContracts(searchQuery, statusId, startDateFrom, startDateTo, page, PAGE_SIZE);
        int totalContracts = contractDAO.getContractCount(searchQuery, statusId, startDateFrom, startDateTo);
        int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);
        List<ContractStatus> statusList = contractDAO.getAllContractStatuses(); // Gọi từ ContractDAO

        request.setAttribute("contractList", contractList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalContracts", totalContracts);
        request.setAttribute("statusList", statusList);

        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("selectedStatusId", statusId);
        request.setAttribute("startDateFrom", startDateFrom);
        request.setAttribute("startDateTo", startDateTo);

        request.getRequestDispatcher("/jsp/chiefOfStaff/listContract.jsp").forward(request, response);
    }

    /**
     * Hiển thị chi tiết một hợp đồng.
     */
    private void viewContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int contractId = Integer.parseInt(request.getParameter("id"));
        ContractDAO contractDAO = new ContractDAO();
        Contract contract = contractDAO.getContractById(contractId);

        if (contract != null) {
            List<ContractProduct> contractItems = contractDAO.getContractProductsByContractId(contractId);

            BigDecimal grandTotal = contract.getTotalValue();
            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal vatAmount = BigDecimal.ZERO;
            if (grandTotal != null && grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal divisor = new BigDecimal("1.1");
                subtotal = grandTotal.divide(divisor, 2, RoundingMode.HALF_UP);
                vatAmount = grandTotal.subtract(subtotal);
            }

            request.setAttribute("contract", contract);
            request.setAttribute("contractItems", contractItems);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("vatAmount", vatAmount);
            request.setAttribute("grandTotal", grandTotal);
        }
        request.getRequestDispatcher("/jsp/chiefOfStaff/viewContractDetail.jsp").forward(request, response);
    }

    /**
     * Hiển thị form để tạo mới hợp đồng.
     */
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadFormData(request);
        request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
    }

    /**
     * Hiển thị form để chỉnh sửa một hợp đồng đã có.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int contractId = Integer.parseInt(request.getParameter("id"));
        ContractDAO contractDAO = new ContractDAO();
        Contract contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy hợp đồng để sửa.");
            response.sendRedirect("contract?action=list");
            return;
        }

        List<ContractProduct> contractItems = contractDAO.getContractProductsByContractId(contractId);

        loadFormData(request);

        request.setAttribute("contract", contract);
        request.setAttribute("contractItems", contractItems);
        request.getRequestDispatcher("/jsp/chiefOfStaff/editContractDetail.jsp").forward(request, response);
    }

    /**
     * Lưu một hợp đồng mới vào cơ sở dữ liệu.
     */
    private void saveContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            Contract contract = new Contract();
            populateContractFromRequest(request, contract);
            List<ContractProduct> contractItems = extractContractProductsFromRequest(request);
            ContractDAO contractDAO = new ContractDAO();
            boolean isSuccess = contractDAO.createContractWithItems(contract, contractItems);

            if (isSuccess) {
                request.getSession().setAttribute("successMessage", "Tạo hợp đồng [" + contract.getContractCode() + "] thành công!");
                response.sendRedirect("contract?action=list");
            } else {
                throw new Exception("Lưu hợp đồng vào CSDL thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Tạo hợp đồng thất bại: " + e.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
        }
    }

    /**
     * Cập nhật một hợp đồng đã có trong cơ sở dữ liệu.
     */
    private void updateContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contractIdStr = request.getParameter("id");
        try {
            Contract contract = new Contract();
            contract.setId(Long.parseLong(contractIdStr));
            populateContractFromRequest(request, contract);
            List<ContractProduct> newItems = extractContractProductsFromRequest(request);
            ContractDAO contractDAO = new ContractDAO();
            boolean success = contractDAO.updateContractWithItems(contract, newItems);

            if (success) {
                request.getSession().setAttribute("successMessage", "Cập nhật hợp đồng [" + contract.getContractCode() + "] thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Cập nhật hợp đồng thất bại.");
            }
            response.sendRedirect("contract?action=list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật: " + e.getMessage());
            response.sendRedirect("contract?action=edit&id=" + contractIdStr);
        }
    }

    /**
     * Xóa mềm một hợp đồng.
     */
    private void deleteContract(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int contractId = Integer.parseInt(request.getParameter("id"));
            ContractDAO dao = new ContractDAO();
            boolean success = dao.softDeleteContract(contractId);
            if (success) {
                request.getSession().setAttribute("successMessage", "Đã xóa hợp đồng thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Xóa hợp đồng thất bại.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "ID hợp đồng không hợp lệ hoặc có lỗi xảy ra.");
        }
        response.sendRedirect("contract?action=list");
    }

    // --- CÁC PHƯƠNG THỨC HELPER ---

    /**
     * Helper để tải dữ liệu chung cho các form (create, edit).
     */
    private void loadFormData(HttpServletRequest request) throws Exception {
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        ProductDAO productDAO = new ProductDAO();
        UserDAO userDAO = new UserDAO();
        ContractDAO contractDAO = new ContractDAO();

        request.setAttribute("enterpriseList", enterpriseDAO.getAllActiveEnterprisesSimple());
        request.setAttribute("productList", productDAO.getAllActiveProducts());
        request.setAttribute("employeeList", userDAO.getEmployeesByDepartment("Chánh văn phòng"));
        request.setAttribute("statusList", contractDAO.getAllContractStatuses());
    }

    /**
     * Helper để lấy thông tin hợp đồng từ request và gán vào đối tượng Contract.
     */
    private void populateContractFromRequest(HttpServletRequest request, Contract contract) {
        contract.setContractCode(request.getParameter("contractCode"));
        contract.setContractName(request.getParameter("contractName"));
        contract.setEnterpriseId(Long.parseLong(request.getParameter("enterpriseId")));
        contract.setCreatedById(Long.parseLong(request.getParameter("createdById")));
        contract.setSignedDate(Date.valueOf(request.getParameter("signedDate")));
        contract.setStartDate(Date.valueOf(request.getParameter("startDate")));
        contract.setEndDate(Date.valueOf(request.getParameter("endDate")));
        contract.setStatusId(Integer.parseInt(request.getParameter("statusId")));
        contract.setNotes(request.getParameter("notes"));
        String totalValueStr = request.getParameter("totalValue").replace(",", "");
        contract.setTotalValue(new BigDecimal(totalValueStr));
    }

    /**
     * Helper để trích xuất danh sách sản phẩm từ request.
     */
    private List<ContractProduct> extractContractProductsFromRequest(HttpServletRequest request) throws Exception {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        List<ContractProduct> contractItems = new ArrayList<>();
        if (productIds != null && quantities != null) {
            ProductDAO productDAO = new ProductDAO();
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] == null || productIds[i].trim().isEmpty()) {
                    continue;
                }
                int productId = Integer.parseInt(productIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                Product originalProduct = productDAO.getProductById(productId);
                if (originalProduct == null) {
                    throw new Exception("Không tìm thấy sản phẩm ID: " + productId);
                }
                ContractProduct item = new ContractProduct();
                item.setProductId((long) productId);
                item.setQuantity(quantity);
                item.setName(originalProduct.getName());
                item.setProductCode(originalProduct.getProductCode());
                item.setUnitPrice(new BigDecimal(originalProduct.getPrice()));
                item.setDescription(originalProduct.getDescription());
                contractItems.add(item);
            }
        }
        return contractItems;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}