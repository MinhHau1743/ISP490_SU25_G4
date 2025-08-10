/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Thêm import cho Part nếu bạn có ý định xử lý file upload trong tương lai
// import jakarta.servlet.http.Part;
import java.sql.Date;
import java.util.List;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.User;

// *******************************************************************
// THAY ĐỔI QUAN TRỌNG: THÊM ANNOTATION @MultipartConfig
// Điều này cho phép Servlet xử lý các request có enctype="multipart/form-data"
// *******************************************************************
@WebServlet(name = "AddCampaignServlet", urlPatterns = {"/add-campaign"})
@jakarta.servlet.annotation.MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB - Kích thước tệp tối thiểu để lưu vào đĩa
    maxFileSize = 1024 * 1024 * 10, // 10MB - Kích thước tối đa cho mỗi tệp
    maxRequestSize = 1024 * 1024 * 50 // 50MB - Kích thước tối đa của toàn bộ request
)
public class AddCampaignServlet extends HttpServlet {

    private CampaignDAO campaignDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        campaignDAO = new CampaignDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuẩn bị dữ liệu cho dropdown "Người tạo"
        List<User> userList = campaignDAO.getAllUsers();
        request.setAttribute("userList", userList);
        
        request.getRequestDispatcher("/jsp/customerSupport/addNewCampaign.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo nhận tiếng Việt

        // Lấy dữ liệu từ form
        String name = request.getParameter("campaignName");
        String description = request.getParameter("description");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String createdByStr = request.getParameter("createdBy"); // Lấy ID người tạo
        
        // Loại chiến dịch và đối tượng mục tiêu không được lưu vào DB Campaigns theo schema hiện tại
        // String campaignType = request.getParameter("campaignType");
        // String period = request.getParameter("period");
        // String targetAudience = request.getParameter("targetAudience"); 
        
        // Xử lý upload file (hiện tại vẫn bị bỏ qua, nhưng giờ Servlet đã có thể xử lý multipart request)
        // Nếu bạn muốn xử lý file upload thực sự, bạn sẽ cần uncomment và viết code ở đây:
        // try {
        //    Part filePart = request.getPart("attachment"); // Lấy Part của file
        //    if (filePart != null && filePart.getSize() > 0) {
        //        String fileName = filePart.getSubmittedFileName();
        //        // Lưu file vào thư mục trên server, ví dụ:
        //        // String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        //        // File uploadDir = new File(uploadPath);
        //        // if (!uploadDir.exists()) uploadDir.mkdir();
        //        // filePart.write(uploadPath + File.separator + fileName);
        //        // Cập nhật thông tin file vào campaign object hoặc database
        //    }
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     errorMessage += "Lỗi khi tải lên tệp: " + e.getMessage() + "<br>";
        //     isValid = false;
        // }
        

        // Validate và chuyển đổi dữ liệu
        Date startDate = null;
        Date endDate = null;
        Integer createdBy = null; // Dùng Integer để có thể null nếu không parse được
        
        boolean isValid = true;
        String errorMessage = "";

        if (name == null || name.trim().isEmpty()) {
            errorMessage += "Tên chiến dịch không được để trống.<br>";
            isValid = false;
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage += "Mô tả không được để trống.<br>";
            isValid = false;
        }

        try {
            startDate = Date.valueOf(startDateStr);
        } catch (IllegalArgumentException e) {
            errorMessage += "Ngày bắt đầu không hợp lệ.<br>";
            isValid = false;
        }

        try {
            endDate = Date.valueOf(endDateStr);
        } catch (IllegalArgumentException e) {
            errorMessage += "Ngày kết thúc không hợp lệ.<br>";
            isValid = false;
        }
        
        if (startDate != null && endDate != null && startDate.after(endDate)) {
            errorMessage += "Ngày bắt đầu không được sau ngày kết thúc.<br>";
            isValid = false;
        }
        
        try {
            if (createdByStr != null && !createdByStr.isEmpty()) {
                createdBy = Integer.parseInt(createdByStr);
            } else {
                errorMessage += "Người tạo không được để trống.<br>";
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errorMessage += "ID người tạo không hợp lệ.<br>";
            isValid = false;
        }


        if (isValid) {
            // Tạo đối tượng Campaign
            Campaign newCampaign = new Campaign();
            newCampaign.setName(name);
            newCampaign.setDescription(description);
            newCampaign.setStartDate(startDate);
            newCampaign.setEndDate(endDate);
            newCampaign.setStatus("pending"); // Đặt trạng thái mặc định
            newCampaign.setCreatedBy(createdBy);
            newCampaign.setUpdatedBy(createdBy); // Ban đầu updated_by cũng là người tạo

            // Gọi DAO để thêm vào DB
            boolean success = campaignDAO.addCampaign(newCampaign);

            if (success) {
                // Chuyển hướng về trang danh sách chiến dịch nếu thành công
                response.sendRedirect(request.getContextPath() + "/list-campaign?success=true");
                
            } else {
                // Quay lại form nếu thất bại và hiển thị lỗi
                errorMessage = "Có lỗi xảy ra khi thêm chiến dịch. Vui lòng thử lại.";
                request.setAttribute("errorMessage", errorMessage);
                
                // Cần load lại danh sách user cho dropdown nếu quay lại form
                List<User> userList = campaignDAO.getAllUsers();
                request.setAttribute("userList", userList);
                
                request.getRequestDispatcher("/jsp/customerSupport/addNewCampaign.jsp").forward(request, response);
            }
        } else {
            // Nếu dữ liệu không hợp lệ, quay lại form và hiển thị lỗi
            request.setAttribute("errorMessage", errorMessage);
            
            // Cần load lại danh sách user cho dropdown nếu quay lại form
            List<User> userList = campaignDAO.getAllUsers();
            request.setAttribute("userList", userList);
            
            request.getRequestDispatcher("/jsp/customerSupport/addNewCampaign.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet để thêm chiến dịch mới";
    }
}