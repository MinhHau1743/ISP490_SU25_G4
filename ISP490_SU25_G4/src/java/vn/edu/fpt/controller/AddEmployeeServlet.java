/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.PositionDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.Position;
import vn.edu.fpt.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.edu.fpt.common.EmailService;
import vn.edu.fpt.common.EmailSender;
import vn.edu.fpt.common.GmailSender;

/**
 *
 * @author minhh
 */
@WebServlet(name = "AddEmployeeServlet", urlPatterns = {"/admin/employees/add"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AddEmployeeServlet extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "uploads" + File.separator + "avatars";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DepartmentDAO departmentDAO = new DepartmentDAO();
        PositionDAO positionDAO = new PositionDAO();

        List<Department> departmentList = departmentDAO.getAllDepartments();
        List<Position> positionList = positionDAO.getAllPositions();

        request.setAttribute("departmentList", departmentList);
        request.setAttribute("positionList", positionList);

        request.getRequestDispatcher("/jsp/admin/addEmployee.jsp").forward(request, response);
    }

    // Xử lý yêu cầu POST: Xử lý dữ liệu từ form được gửi lên
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Xử lý lưu file ảnh
            String avatarUrl = null;
            // Lấy đường dẫn thực của thư mục upload
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                Part filePart = request.getPart("avatar"); // Lấy file từ form qua name="avatar"
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                if (fileName != null && !fileName.isEmpty()) {
                    // Tạo tên file duy nhất để tránh bị ghi đè
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    filePart.write(uploadPath + File.separator + uniqueFileName);
                    // Lưu lại đường dẫn tương đối để lưu vào database
                    avatarUrl = UPLOAD_DIRECTORY + "/" + uniqueFileName;
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi khi tải ảnh lên.");
                doGet(request, response);
                return;
            }

            // 2. Lấy các thông tin khác từ form
            UserDAO userDAO = new UserDAO();
            User newUser = new User();

            // Với Servlet 3.0+, bạn có thể dùng request.getParameter() ngay cả với form multipart
            String fullName = request.getParameter("employeeName");
            String[] nameParts = fullName.split("\\s+");
            if (nameParts.length > 0) {
                newUser.setLastName(nameParts[0]);
                if (nameParts.length > 2) {
                    newUser.setMiddleName(nameParts[1]);
                    newUser.setFirstName(nameParts[2]);
                } else if (nameParts.length == 2) {
                    newUser.setFirstName(nameParts[1]);
                    newUser.setMiddleName("");
                } else {
                    newUser.setFirstName("");
                    newUser.setMiddleName("");
                }
            }

            newUser.setPhoneNumber(request.getParameter("phone"));
            newUser.setEmail(request.getParameter("email"));
            newUser.setNotes(request.getParameter("notes"));
            newUser.setIdentityCardNumber(request.getParameter("idCard"));
            newUser.setGender(request.getParameter("gender"));
            newUser.setAvatarUrl(avatarUrl); // Gán đường dẫn ảnh đã upload

            String dobString = request.getParameter("dob");
            if (dobString != null && !dobString.isEmpty()) {
                newUser.setDateOfBirth(LocalDate.parse(dobString));
            }

            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            int roleId = 2; // Giả sử roleId mặc định là 2 (CSKH)

            // 3. Gọi DAO để thêm nhân viên vào CSDL
            boolean success = userDAO.addEmployee(newUser, departmentId, positionId, roleId);

            // 4. Chuyển hướng
            if (success) {
                EmailSender emailSender = new GmailSender();
                EmailService emailService = new EmailService(emailSender);

                String subject = "Chào mừng bạn gia nhập công ty!";
                String message = "<h3>Xin chào " + newUser.getFullName() + ",</h3>"
                        + "<p>Tài khoản của bạn đã được tạo thành công.</p>"
                        + "<p>Thông tin đăng nhập:</p>"
                        + "<ul>"
                        + "<li>Email: " + newUser.getEmail() + "</li>"
                        + "<li>Mật khẩu mặc định: DPCRM@12345</li>"
                        + "</ul>"
                        + "<p>Vui lòng đổi mật khẩu sau khi đăng nhập.</p>";

                emailService.sendEmailAsync(newUser.getEmail(), subject, message);

                response.sendRedirect(request.getContextPath() + "/admin/employees/list?add=success");
            } else {
                try {
                    throw new Exception("Lỗi khi thực hiện thêm nhân viên.");
                } catch (Exception ex) {
                    request.setAttribute("errorMessage", "Định dạng dữ liệu không hợp lệ: " + ex.getMessage());
                    doGet(request, response);
                }
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Định dạng dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        }
    }
}
