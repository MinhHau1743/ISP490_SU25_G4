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

@WebServlet(name = "CreateContractController", urlPatterns = {"/createContract"})
public class CreateContractController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        ProductDAO productDAO = new ProductDAO();
        UserDAO userDAO = new UserDAO();

        try {
            List<Enterprise> enterpriseList = enterpriseDAO.getAllActiveEnterprisesSimple();
            List<Product> productList = productDAO.getAllActiveProducts();
            List<User> employeeList = userDAO.getAllEmployees();

            request.setAttribute("enterpriseList", enterpriseList);
            request.setAttribute("productList", productList);
            request.setAttribute("employeeList", employeeList);

            request.getRequestDispatcher("/jsp/chiefOfStaff/createContract.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể tải dữ liệu cho trang. Lỗi: " + e.getMessage());
            response.sendRedirect("errorPage.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        ContractDAO contractDAO = new ContractDAO();
        ProductDAO productDAO = new ProductDAO();

        try {
            // BƯỚC 1: LẤY THÔNG TIN HỢP ĐỒNG
            Contract contract = new Contract();
            contract.setContractCode(request.getParameter("contractCode")); // Lấy mã HĐ từ form
            contract.setContractName(request.getParameter("contractName"));
            contract.setEnterpriseId(Long.parseLong(request.getParameter("enterpriseId")));
            contract.setCreatedById(Long.parseLong(request.getParameter("createdById")));
            contract.setSignedDate(Date.valueOf(request.getParameter("signedDate")));
            contract.setStartDate(Date.valueOf(request.getParameter("startDate")));
            contract.setEndDate(Date.valueOf(request.getParameter("endDate")));
            contract.setStatus(request.getParameter("status"));
            contract.setNotes(request.getParameter("notes"));
            String totalValueStr = request.getParameter("totalValue").replace(",", "");
            contract.setTotalValue(new BigDecimal(totalValueStr));
//            String contractTypeIdStr = request.getParameter("contractTypeId");
//            if (contractTypeIdStr != null && !contractTypeIdStr.isEmpty()) {
//                contract.setContractTypeId(Long.parseLong(contractTypeIdStr));
//            }

            // BƯỚC 2: LẤY DANH SÁCH SẢN PHẨM VÀ TẠO SNAPSHOT
            String[] productIds = request.getParameterValues("productId");
            String[] quantities = request.getParameterValues("quantity");
            List<ContractProduct> contractItems = new ArrayList<>();
            if (productIds != null && quantities != null) {
                for (int i = 0; i < productIds.length; i++) {
                    int productId = Integer.parseInt(productIds[i]);
                    int quantity = Integer.parseInt(quantities[i]);
                    Product originalProduct = productDAO.getProductById(productId);
                    if (originalProduct == null) {
                        throw new Exception("Không tìm thấy sản phẩm với ID: " + productId);
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

            // BƯỚC 3: GỌI DAO ĐỂ LƯU TOÀN BỘ DỮ LIỆU
            boolean isSuccess = contractDAO.createContractWithItems(contract, contractItems);

            // BƯỚC 4: CHUYỂN HƯỚNG
            if (isSuccess) {
                request.getSession().setAttribute("successMessage", "Tạo hợp đồng [" + contract.getContractCode() + "] thành công!");
                response.sendRedirect("listContract");
            } else {
                throw new Exception("Lưu hợp đồng vào cơ sở dữ liệu thất bại.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Tạo hợp đồng thất bại: " + e.getMessage());
            doGet(request, response); // Quay lại form và báo lỗi
        }
    }
}
