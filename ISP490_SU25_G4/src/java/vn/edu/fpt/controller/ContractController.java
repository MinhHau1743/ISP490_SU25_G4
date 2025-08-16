package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.common.EmailServiceFeedback; // THÊM MỚI: Import dịch vụ gửi email
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.FeedbackDAO; // THÊM MỚI: Cần để kiểm tra feedback
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
import java.util.concurrent.ExecutorService; // THÊM MỚI
import java.util.concurrent.Executors;    // THÊM MỚI

@WebServlet(name = "ContractController", urlPatterns = {"/contract"})
public class ContractController extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    // THÊM MỚI: ExecutorService để gửi email mà không làm chậm hệ thống
    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

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
                // THÊM MỚI: Action để gửi email khảo sát
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

    private void listContracts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // ... Giữ nguyên không thay đổi ...
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
                /* Bỏ qua */ }
        }

        ContractDAO contractDAO = new ContractDAO();
        List<Contract> contractList = contractDAO.getContracts(searchQuery, statusId, startDateFrom, startDateTo, page, PAGE_SIZE);
        int totalContracts = contractDAO.getContractCount(searchQuery, statusId, startDateFrom, startDateTo);
        int totalPages = (int) Math.ceil((double) totalContracts / PAGE_SIZE);
        List<ContractStatus> statusList = contractDAO.getAllContractStatuses();

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
        ContractDAO contractDAO = new ContractDAO();
        Contract contract = contractDAO.getContractById(contractId);

        if (contract != null) {
            // SỬA ĐỔI: Thêm logic kiểm tra feedback
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            boolean hasFeedback = feedbackDAO.feedbackExistsForContract(contract.getId());
            request.setAttribute("hasFeedback", hasFeedback);
            // Kết thúc sửa đổi

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

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        loadFormData(request);
        request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // ... Giữ nguyên không thay đổi ...
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

    private void saveContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Contract contract = new Contract();
        List<ContractProduct> contractItems = new ArrayList<>();
        try {
            populateContractFromRequest(request, contract);
            contractItems = extractContractProductsFromRequest(request);

            // THÊM MỚI: Bước xác thực
            List<String> errors = validateContract(contract);
            if (!errors.isEmpty()) {
                request.setAttribute("errorMessages", errors); // Gửi lỗi về JSP
                request.setAttribute("contract", contract); // Gửi lại dữ liệu người dùng đã nhập
                request.setAttribute("contractItems", contractItems);
                loadFormData(request); // Tải lại các danh sách dropdown
                request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
                return; // Dừng xử lý
            }

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
            request.setAttribute("contract", contract); // Gửi lại dữ liệu nếu có lỗi
            request.setAttribute("contractItems", contractItems);
            loadFormData(request);
            request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
        }
    }

    private void updateContract(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String contractIdStr = request.getParameter("id");
        Contract contract = new Contract();
        List<ContractProduct> newItems = new ArrayList<>();
        try {
            contract.setId(Long.parseLong(contractIdStr));
            populateContractFromRequest(request, contract);
            newItems = extractContractProductsFromRequest(request);

            // THÊM MỚI: Bước xác thực
            List<String> errors = validateContract(contract);
            if (!errors.isEmpty()) {
                request.setAttribute("errorMessages", errors);
                request.setAttribute("contract", contract);
                request.setAttribute("contractItems", newItems);
                loadFormData(request);
                request.getRequestDispatcher("/jsp/chiefOfStaff/editContractDetail.jsp").forward(request, response);
                return; // Dừng xử lý
            }

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

    private void deleteContract(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ... Giữ nguyên không thay đổi ...
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

    // Trong file: ContractController.java
    private void handleSendContractSurvey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("\n--- DEBUG: Bắt đầu xử lý gửi khảo sát Hợp đồng ---");
        String idParam = request.getParameter("id");
        System.out.println("1. ID nhận được từ JSP: " + idParam);

        try {
            if (idParam == null || idParam.trim().isEmpty()) {
                System.out.println("LỖI: Không nhận được ID từ request.");
                response.sendRedirect(request.getContextPath() + "/contract?action=list&error=missingIdFromButton");
                return;
            }

            int contractId = Integer.parseInt(idParam);
            ContractDAO contractDAO = new ContractDAO();
            Contract contract = contractDAO.getContractById(contractId);
            System.out.println("2. Đã tìm thấy hợp đồng trong DB: " + (contract != null));

            if (contract != null && contract.getEnterpriseEmail() != null && !contract.getEnterpriseEmail().isEmpty()) {
                String recipientEmail = contract.getEnterpriseEmail();
                String enterpriseName = contract.getEnterpriseName();
                String contractCode = contract.getContractCode();

                emailExecutor.submit(() -> {
                    try {
                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                        String surveyLink = baseUrl + "/feedback?action=create&contractId=" + contractId;
                        System.out.println("3. Link được tạo để gửi email: " + surveyLink);

                        String subject = "Mời bạn đánh giá chất lượng cho Hợp đồng #" + contractCode;

                        // SỬA ĐỔI: Nội dung email đầy đủ và được định dạng HTML
                        String body = "<html>"
                                + "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                + "<div style='max-width: 600px; margin: 20px auto; padding: 25px; border: 1px solid #ddd; border-radius: 10px;'>"
                                + "<h2 style='color: #0056b3;'>Thư mời đánh giá dịch vụ</h2>"
                                + "<p>Kính gửi Quý khách hàng <strong>" + enterpriseName + "</strong>,</p>"
                                + "<p>Hợp đồng với mã số <strong>" + contractCode + "</strong> của Quý khách đã hoàn thành.</p>"
                                + "<p>Chúng tôi rất mong nhận được những ý kiến đóng góp quý báu của Quý khách để cải thiện chất lượng dịch vụ. Vui lòng dành chút thời gian để thực hiện khảo sát bằng cách nhấn vào nút bên dưới:</p>"
                                + "<div style='text-align: center; margin: 30px 0;'>"
                                + "<a href=\"" + surveyLink + "\" style='background-color:#2563eb; color:white; padding:14px 28px; text-align:center; text-decoration:none; display:inline-block; border-radius:8px; font-size:16px; font-weight:bold;'>Thực hiện khảo sát</a>"
                                + "</div>"
                                + "<p>Nếu có bất kỳ thắc mắc nào, xin vui lòng liên hệ lại với chúng tôi.</p>"
                                + "<p>Trân trọng cảm ơn,<br><strong>Đội ngũ DPCRM</strong>.</p>"
                                + "</div>"
                                + "</body></html>";

                        EmailServiceFeedback.sendMail(recipientEmail, subject, body);
                        System.out.println("4. Email khảo sát đã được gửi thành công tới: " + recipientEmail);

                    } catch (Exception e) {
                        System.err.println("Lỗi trong luồng gửi email:");
                        e.printStackTrace();
                    }
                });
                response.sendRedirect(request.getContextPath() + "/contract?action=view&id=" + contractId + "&surveySent=true");
            } else {
                System.out.println("LỖI: Không tìm thấy hợp đồng hoặc hợp đồng thiếu email.");
                response.sendRedirect(request.getContextPath() + "/contract?action=view&id=" + idParam + "&error=noEmailOrNotFound");
            }
        } catch (Exception e) {
            System.out.println("Exception xảy ra trong quá trình xử lý: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/contract?action=list&error=surveyError");
        }
    }

    // THÊM MỚI: Phương thức xác thực dữ liệu hợp đồng
    private List<String> validateContract(Contract contract) {
        List<String> errors = new ArrayList<>();

        // 1. Kiểm tra các trường bắt buộc
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

        // 2. Kiểm tra logic ngày tháng (chỉ kiểm tra nếu các ngày không null)
        if (contract.getStartDate() != null && contract.getSignedDate() != null && contract.getStartDate().before(contract.getSignedDate())) {
            errors.add("Ngày hiệu lực không được trước ngày ký.");
        }
        if (contract.getEndDate() != null && contract.getStartDate() != null && contract.getEndDate().before(contract.getStartDate())) {
            errors.add("Ngày hết hạn không được trước ngày hiệu lực.");
        }

        return errors;
    }

    private void loadFormData(HttpServletRequest request) throws Exception {
        // ... Giữ nguyên không thay đổi ...
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        ProductDAO productDAO = new ProductDAO();
        UserDAO userDAO = new UserDAO();
        ContractDAO contractDAO = new ContractDAO();
        request.setAttribute("enterpriseList", enterpriseDAO.getAllActiveEnterprisesSimple());
        request.setAttribute("productList", productDAO.getAllActiveProducts());
        request.setAttribute("employeeList", userDAO.getEmployeesByDepartment("Chánh văn phòng"));
        request.setAttribute("statusList", contractDAO.getAllContractStatuses());
    }

    private void populateContractFromRequest(HttpServletRequest request, Contract contract) {
        // ... Giữ nguyên không thay đổi ...
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
        // ... Giữ nguyên không thay đổi ...
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
                item.setUnitPrice(originalProduct.getPrice());
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
