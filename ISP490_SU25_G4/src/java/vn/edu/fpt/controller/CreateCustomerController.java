// File: src/java/vn/edu/fpt/controller/CreateCustomerController.java
package vn.edu.fpt.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.CustomerTypeDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.ProvinceDAO;
import vn.edu.fpt.model.CustomerType;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.EnterpriseContact;
import vn.edu.fpt.model.Province;

@WebServlet(name = "CreateCustomerController", urlPatterns = {"/createCustomer"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class CreateCustomerController extends HttpServlet {

    private static final String UPLOAD_DIR = "uploads" + File.separator + "avatars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tải dữ liệu ban đầu cho form
        CustomerTypeDAO customerTypeDAO = new CustomerTypeDAO();
        ProvinceDAO provinceDAO = new ProvinceDAO();
        request.setAttribute("customerTypes", customerTypeDAO.getAllCustomerTypes());
        request.setAttribute("provinces", provinceDAO.getAllProvinces());
        request.getRequestDispatcher("/jsp/sales/createCustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy dữ liệu từ form
        String customerName = request.getParameter("customerName");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String streetAddress = request.getParameter("streetAddress");

        // <<< SỬA LỖI: Kiểm tra tất cả các giá trị trước khi parse >>>
        String provinceIdStr = request.getParameter("province");
        String districtIdStr = request.getParameter("district");
        String wardIdStr = request.getParameter("ward");
        String customerGroupIdStr = request.getParameter("customerGroup");

        if (provinceIdStr == null || districtIdStr == null || wardIdStr == null || customerGroupIdStr == null
                || provinceIdStr.trim().isEmpty() || districtIdStr.trim().isEmpty() || wardIdStr.trim().isEmpty() || customerGroupIdStr.trim().isEmpty()) {

            request.setAttribute("errorMessage", "Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, Phường/Xã và Nhóm khách hàng.");
            doGet(request, response);
            return;
        }

        int provinceId, districtId, wardId, customerTypeId;
        try {
            provinceId = Integer.parseInt(provinceIdStr);
            districtId = Integer.parseInt(districtIdStr);
            wardId = Integer.parseInt(wardIdStr);
            customerTypeId = Integer.parseInt(customerGroupIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu ID không hợp lệ. Vui lòng thử lại.");
            doGet(request, response);
            return;
        }

        // Xử lý upload file
        Part filePart = request.getPart("avatar");
        // ... (code xử lý upload file của bạn đặt ở đây nếu cần)

        // Các bước tiếp theo chỉ thực hiện khi dữ liệu hợp lệ
        AddressDAO addressDAO = new AddressDAO();
        int addressId = addressDAO.insertAddress(streetAddress, wardId, districtId, provinceId);

        if (addressId <= 0) {
            request.setAttribute("errorMessage", "Lỗi từ DB: Không thể tạo địa chỉ.");
            doGet(request, response);
            return;
        }

        EnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        String enterpriseCode = "KH-" + System.currentTimeMillis() % 100000;
        Enterprise enterprise = new Enterprise();
        enterprise.setEnterpriseCode(enterpriseCode);
        enterprise.setName(customerName);
        enterprise.setCustomerTypeId(customerTypeId);
        enterprise.setAddressId(addressId);

        int enterpriseId = enterpriseDAO.insertEnterprise(enterprise);

        if (enterpriseId <= 0) {
            request.setAttribute("errorMessage", "Lỗi từ DB: Không thể tạo thông tin khách hàng.");
            doGet(request, response);
            return;
        }

        EnterpriseContact contact = new EnterpriseContact();
        contact.setEnterpriseId(enterpriseId);
        contact.setFullName(customerName);
        contact.setPhoneNumber(phone);
        contact.setEmail(email);
        enterpriseDAO.insertEnterpriseContact(contact);

        response.sendRedirect(request.getContextPath() + "/listCustomer?create_status=success");
    }
}
