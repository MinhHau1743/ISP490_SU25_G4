package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.ContractProduct;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.User;

@WebServlet(name = "EditContractController", urlPatterns = {"/editContract"})
public class EditContractController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contractIdStr = request.getParameter("id");

        try {
            int contractId = Integer.parseInt(contractIdStr);

            ContractDAO contractDAO = new ContractDAO();
            EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
            UserDAO userDAO = new UserDAO();
            ProductDAO productDAO = new ProductDAO();

            Contract contract = contractDAO.getContractById(contractId);
            List<ContractProduct> contractItems = contractDAO.getContractProductsByContractId(contractId);
            List<Enterprise> enterpriseList = enterpriseDAO.getAllActiveEnterprisesSimple();
            List<User> employeeList = userDAO.getAllEmployees();
            List<Product> productList = productDAO.getAllActiveProducts();

            request.setAttribute("contract", contract);
            request.setAttribute("contractItems", contractItems);
            request.setAttribute("enterpriseList", enterpriseList);
            request.setAttribute("employeeList", employeeList);
            request.setAttribute("productList", productList);

            request.getRequestDispatcher("/jsp/chiefOfStaff/editContractDetail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("listContract");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // Lấy ID trước để có thể chuyển hướng lại nếu lỗi
        String contractIdStr = request.getParameter("id");
        
        try {
            Contract contract = new Contract();
            contract.setId(Long.parseLong(contractIdStr));
            
            // Lấy các giá trị từ form và kiểm tra null/rỗng trước khi parse
            contract.setContractCode(request.getParameter("contractCode"));
            contract.setContractName(request.getParameter("contractName"));
            contract.setStatus(request.getParameter("status"));
            contract.setNotes(request.getParameter("notes"));

            String enterpriseIdStr = request.getParameter("enterpriseId");
            if (enterpriseIdStr != null && !enterpriseIdStr.isEmpty()) {
                contract.setEnterpriseId(Long.parseLong(enterpriseIdStr));
            }

            String createdByIdStr = request.getParameter("createdById");
            if (createdByIdStr != null && !createdByIdStr.isEmpty()) {
                contract.setCreatedById(Long.parseLong(createdByIdStr));
            }

            String signedDateStr = request.getParameter("signedDate");
            if (signedDateStr != null && !signedDateStr.isEmpty()) {
                contract.setSignedDate(Date.valueOf(signedDateStr));
            }
            
            String startDateStr = request.getParameter("startDate");
            if (startDateStr != null && !startDateStr.isEmpty()) {
                contract.setStartDate(Date.valueOf(startDateStr));
            }

            String endDateStr = request.getParameter("endDate");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                contract.setEndDate(Date.valueOf(endDateStr));
            }
            
            String totalValueStr = request.getParameter("totalValue");
            if (totalValueStr != null && !totalValueStr.isEmpty()){
                contract.setTotalValue(new BigDecimal(totalValueStr.replace(",", "")));
            } else {
                contract.setTotalValue(BigDecimal.ZERO); // Gán giá trị mặc định nếu rỗng
            }
            
            // Xử lý danh sách sản phẩm
            String[] productIds = request.getParameterValues("productId");
            String[] quantities = request.getParameterValues("quantity");
            List<ContractProduct> newItems = new ArrayList<>();
            ProductDAO productDAO = new ProductDAO();

            if (productIds != null && quantities != null) {
                for (int i = 0; i < productIds.length; i++) {
                    int productId = Integer.parseInt(productIds[i]);
                    int quantity = Integer.parseInt(quantities[i]);
                    Product originalProduct = productDAO.getProductById(productId);
                    
                    ContractProduct item = new ContractProduct();
                    item.setProductId((long) productId);
                    item.setQuantity(quantity);
                    item.setName(originalProduct.getName());
                    item.setProductCode(originalProduct.getProductCode());
                    item.setUnitPrice(new BigDecimal(originalProduct.getPrice()));
                    item.setDescription(originalProduct.getDescription());
                    newItems.add(item);
                }
            }
            
            // Gọi DAO để cập nhật
            ContractDAO contractDAO = new ContractDAO();
            boolean success = contractDAO.updateContractWithItems(contract, newItems);
            
            if(success) {
                 request.getSession().setAttribute("successMessage", "Cập nhật hợp đồng ["+ contract.getContractCode() +"] thành công!");
            } else {
                 request.getSession().setAttribute("errorMessage", "Cập nhật hợp đồng thất bại.");
            }
            
            response.sendRedirect("listContract");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMessage", "Đã có lỗi xảy ra khi cập nhật: " + e.getMessage());
            // Chuyển hướng lại trang edit nếu có lỗi, thay vì trang list
            response.sendRedirect("editContract?id=" + contractIdStr);
        }
    }
}
