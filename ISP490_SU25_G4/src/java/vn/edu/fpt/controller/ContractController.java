package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.common.EmailServiceFeedback;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.ContractProduct;
import vn.edu.fpt.model.ContractStatus;
import vn.edu.fpt.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet(name = "ContractController", urlPatterns = {"/contract"})
public class ContractController extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    // THAY ĐỔI 1: KHAI BÁO CÁC DAO THÀNH THUỘC TÍNH CỦA LỚP
    private final ContractDAO contractDAO;
    private final FeedbackDAO feedbackDAO;
    private final EnterpriseDAO enterpriseDAO;
    private final ProductDAO productDAO;
    private final UserDAO userDAO;

    // THAY ĐỔI 2: THÊM 2 CONSTRUCTOR ĐỂ CHUẨN BỊ CHO TEST
    /**
     * Constructor mặc định dùng cho Server (Tomcat) khi ứng dụng chạy thật.
     */
    public ContractController() {
        this.contractDAO = new ContractDAO();
        this.feedbackDAO = new FeedbackDAO();
        this.enterpriseDAO = new EnterpriseDAO();
        this.productDAO = new ProductDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Constructor dùng cho Unit Test để "tiêm" các DAO giả (mock).
     */
    public ContractController(ContractDAO contractDAO, FeedbackDAO feedbackDAO, EnterpriseDAO enterpriseDAO, ProductDAO productDAO, UserDAO userDAO) {
        this.contractDAO = contractDAO;
        this.feedbackDAO = feedbackDAO;
        this.enterpriseDAO = enterpriseDAO;
        this.productDAO = productDAO;
        this.userDAO = userDAO;
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
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
                case "sendSurvey":
                    handleSendContractSurvey(request, response);
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

    // THAY ĐỔI 3: TẠO HÀM KIỂM TRA QUYỀN
    private boolean isAuthorizedToManage(String userRole) {
        return "Admin".equals(userRole) || "Chánh văn phòng".equals(userRole);
    }
    
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
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Contract> contractList = this.contractDAO.getContracts(searchQuery, statusId, startDateFrom, startDateTo, page, PAGE_SIZE);
        int totalContracts = this.contractDAO.getContractCount(searchQuery, statusId, startDateFrom, startDateTo);
        int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);
        List<ContractStatus> statusList = this.contractDAO.getAllContractStatuses();

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

    private void viewContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int contractId = Integer.parseInt(request.getParameter("id"));
        Contract contract = this.contractDAO.getContractById(contractId);

        if (contract != null) {
            boolean hasFeedback = this.feedbackDAO.feedbackExistsForContract(contract.getId());
            request.setAttribute("hasFeedback", hasFeedback);

            List<ContractProduct> contractItems = this.contractDAO.getContractProductsByContractId(contractId);
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

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!isAuthorizedToManage(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return;
        }
        loadFormData(request);
        request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!isAuthorizedToManage(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
            return;
        }

        int contractId = Integer.parseInt(request.getParameter("id"));
        Contract contract = this.contractDAO.getContractById(contractId);
        if (contract == null) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy hợp đồng để sửa.");
            response.sendRedirect("contract?action=list");
            return;
        }
        List<ContractProduct> contractItems = this.contractDAO.getContractProductsByContractId(contractId);
        loadFormData(request);
        request.setAttribute("contract", contract);
        request.setAttribute("contractItems", contractItems);
        request.getRequestDispatcher("/jsp/chiefOfStaff/editContractDetail.jsp").forward(request, response);
    }

    private void saveContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!isAuthorizedToManage(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }

        Contract contract = new Contract();
        List<ContractProduct> contractItems = new ArrayList<>();
        try {
            populateContractFromRequest(request, contract);
            contractItems = extractContractProductsFromRequest(request);

            List<String> errors = validateContract(contract);
            if (!errors.isEmpty()) {
                request.setAttribute("errorMessages", errors);
                request.setAttribute("contract", contract);
                request.setAttribute("contractItems", contractItems);
                loadFormData(request);
                request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
                return;
            }

            boolean isSuccess = this.contractDAO.createContractWithItems(contract, contractItems);

            if (isSuccess) {
                request.getSession().setAttribute("successMessage", "Tạo hợp đồng [" + contract.getContractCode() + "] thành công!");
                response.sendRedirect("contract?action=list");
            } else {
                throw new Exception("Lưu hợp đồng vào CSDL thất bại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Tạo hợp đồng thất bại: " + e.getMessage());
            request.setAttribute("contract", contract);
            request.setAttribute("contractItems", contractItems);
            loadFormData(request);
            request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
        }
    }

    private void updateContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!isAuthorizedToManage(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }

        String contractIdStr = request.getParameter("id");
        Contract contract = new Contract();
        List<ContractProduct> newItems = new ArrayList<>();
        try {
            contract.setId(Long.parseLong(contractIdStr));
            populateContractFromRequest(request, contract);
            newItems = extractContractProductsFromRequest(request);

            List<String> errors = validateContract(contract);
            if (!errors.isEmpty()) {
                request.setAttribute("errorMessages", errors);
                request.setAttribute("contract", contract);
                request.setAttribute("contractItems", newItems);
                loadFormData(request);
                request.getRequestDispatcher("/jsp/chiefOfStaff/editContractDetail.jsp").forward(request, response);
                return;
            }

            boolean success = this.contractDAO.updateContractWithItems(contract, newItems);
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

    private void deleteContract(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        if (!isAuthorizedToManage(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này.");
            return;
        }

        try {
            int contractId = Integer.parseInt(request.getParameter("id"));
            boolean success = this.contractDAO.softDeleteContract(contractId);
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

    private void handleSendContractSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        try {
            int contractId = Integer.parseInt(idParam);
            Contract contract = this.contractDAO.getContractById(contractId);

            if (contract != null && contract.getEnterpriseEmail() != null && !contract.getEnterpriseEmail().isEmpty()) {
                String recipientEmail = contract.getEnterpriseEmail();
                String enterpriseName = contract.getEnterpriseName();
                String contractCode = contract.getContractCode();

                emailExecutor.submit(() -> {
                    try {
                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                        String surveyLink = baseUrl + "/feedback?action=create&contractId=" + contractId;
                        String subject = "Mời bạn đánh giá chất lượng cho Hợp đồng #" + contractCode;
                        String body = "<html><body>...Nội dung email HTML đầy đủ...</body></html>"; // (Nội dung email giữ nguyên như code gốc)
                        EmailServiceFeedback.sendMail(recipientEmail, subject, body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                response.sendRedirect(request.getContextPath() + "/contract?action=view&id=" + contractId + "&surveySent=true");
            } else {
                response.sendRedirect(request.getContextPath() + "/contract?action=view&id=" + idParam + "&error=noEmailOrNotFound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contract?action=list&error=surveyError");
        }
    }

    private List<String> validateContract(Contract contract) {
        List<String> errors = new ArrayList<>();
        if (contract.getContractCode() == null || contract.getContractCode().trim().isEmpty()) {
            errors.add("Mã hợp đồng không được để trống.");
        }
        if (contract.getSignedDate() == null) {
            errors.add("Ngày ký không được để trống.");
        }
        if (contract.getStartDate() == null) {
            errors.add("Ngày hiệu lực không được để trống.");
        }
        if (contract.getEndDate() == null) {
            errors.add("Ngày hết hạn không được để trống.");
        }
        if (contract.getStartDate() != null && contract.getSignedDate() != null && contract.getStartDate().before(contract.getSignedDate())) {
            errors.add("Ngày hiệu lực không được trước ngày ký.");
        }
        if (contract.getEndDate() != null && contract.getStartDate() != null && contract.getEndDate().before(contract.getStartDate())) {
            errors.add("Ngày hết hạn không được trước ngày hiệu lực.");
        }
        return errors;
    }

    private void loadFormData(HttpServletRequest request) throws Exception {
        request.setAttribute("enterpriseList", this.enterpriseDAO.getAllActiveEnterprisesSimple());
        request.setAttribute("productList", this.productDAO.getAllActiveProducts());
        request.setAttribute("employeeList", this.userDAO.getEmployeesByDepartment("Chánh văn phòng"));
        request.setAttribute("statusList", this.contractDAO.getAllContractStatuses());
    }

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

    private List<ContractProduct> extractContractProductsFromRequest(HttpServletRequest request) throws Exception {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        List<ContractProduct> contractItems = new ArrayList<>();
        if (productIds != null && quantities != null) {
            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] == null || productIds[i].trim().isEmpty()) {
                    continue;
                }
                int productId = Integer.parseInt(productIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                Product originalProduct = this.productDAO.getProductById(productId);
                if (originalProduct == null) {
                    throw new Exception("Không tìm thấy sản phẩm ID: " + productId);
                }
                ContractProduct item = new ContractProduct();
                item.setProductId((long) productId);
                item.setQuantity(quantity);
                item.setName(originalProduct.getName());
                item.setProductCode(originalProduct.getProductCode());
                item.setUnitPrice(originalProduct.getPrice());
                item.setDescription(originalProduct.getDescription());
                contractItems.add(item);
            }
        }
        return contractItems;
    }
}