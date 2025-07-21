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
import javax.mail.MessagingException;
import vn.edu.fpt.common.EmailService;
import vn.edu.fpt.common.EmailSender;
import vn.edu.fpt.common.GmailSender;

/**
 *
 * @author minhh
 */
@WebServlet(name = "AddEmployeeServlet", urlPatterns = {"/addEmployee"})
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

    /**
     * PHIÊN BẢN ĐÃ SỬA ĐỔI HOÀN TOÀN Xử lý logic: 1. Kiểm tra nhân viên đã tồn
     * tại chưa. 2. Nếu có và đã xóa mềm -> Kích hoạt lại. 3. Nếu có và đang
     * hoạt động -> Báo lỗi. 4. Nếu chưa có -> Thêm mới.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDAO userDAO = new UserDAO();
        String email = request.getParameter("email");
        String idCard = request.getParameter("idCard");

        // BƯỚC 1: KIỂM TRA SỰ TỒN TẠI CỦA NHÂN VIÊN QUA EMAIL HOẶC CMND/CCCD
        // Bạn cần tạo phương thức findUserByEmailOrIdCard trong UserDAO
        User existingUser = userDAO.findUserByEmailOrIdCard(email, idCard);

        if (existingUser != null) {
            // TRƯỜNG HỢP NHÂN VIÊN ĐÃ TỒN TẠI
            if (existingUser.getIsDeleted() == 1) {
                // Nếu bị xóa mềm -> Kích hoạt lại
                // Bạn cần tạo phương thức reactivateUser trong UserDAO
                boolean reactivated = userDAO.reactivateUser(existingUser.getId());
                if (reactivated) {
                    // Cập nhật lại phòng ban và chức vụ nếu cần
                    int departmentId = Integer.parseInt(request.getParameter("departmentId"));
                    int positionId = Integer.parseInt(request.getParameter("positionId"));
                    userDAO.updateUserDepartmentAndPosition(existingUser.getId(), departmentId, positionId);

                    // Gửi thông báo thành công qua session để hiển thị sau khi redirect
                    request.getSession().setAttribute("successMessage", "Đã kích hoạt lại và cập nhật thông tin cho nhân viên: " + existingUser.getFullName());
                } else {
                    request.getSession().setAttribute("errorMessage", "Có lỗi khi kích hoạt lại nhân viên.");
                }
                response.sendRedirect(request.getContextPath() + "/listEmployee");
                return; // Kết thúc xử lý
            } else {
                // Nếu đang hoạt động -> Báo lỗi trùng lặp
                request.setAttribute("errorMessage", "Nhân viên với Email hoặc CMND/CCCD này đã tồn tại.");
                // Tải lại form với thông báo lỗi
                doGet(request, response);
                return; // Kết thúc xử lý
            }
        }

        // TRƯỜNG HỢP NHÂN VIÊN HOÀN TOÀN MỚI -> TIẾN HÀNH THÊM MỚI
        try {
            // 1. Xử lý lưu file ảnh (giữ nguyên code cũ của bạn)
            String avatarUrl = null;
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            try {
                Part filePart = request.getPart("avatar");
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                if (fileName != null && !fileName.isEmpty()) {
                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    filePart.write(uploadPath + File.separator + uniqueFileName);
                    avatarUrl = UPLOAD_DIRECTORY.replace(File.separator, "/") + "/" + uniqueFileName;
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi khi tải ảnh lên: " + e.getMessage());
                doGet(request, response);
                return;
            }

            // 2. Lấy thông tin và tạo đối tượng User mới (giữ nguyên code cũ của bạn)
            User newUser = new User();

            // lay ve name
            String fullName = request.getParameter("employeeName");
            String[] nameParts = fullName.trim().split("\\s+");

            if (nameParts.length >= 1) {
                newUser.setLastName(nameParts[0]);

                if (nameParts.length >= 2) {
                    newUser.setFirstName(nameParts[nameParts.length - 1]);
                } else {
                    newUser.setFirstName("");
                }

                if (nameParts.length > 2) {
                    // Lấy phần còn lại làm tên đệm (middle name)
                    StringBuilder middleName = new StringBuilder();
                    for (int i = 1; i < nameParts.length - 1; i++) {
                        middleName.append(nameParts[i]).append(" ");
                    }
                    newUser.setMiddleName(middleName.toString().trim());
                } else {
                    newUser.setMiddleName("");
                }
            }

            newUser.setPhoneNumber(request.getParameter("phone"));
            newUser.setEmail(email); // Dùng lại biến email đã lấy
            newUser.setNotes(request.getParameter("notes"));
            newUser.setIdentityCardNumber(idCard); // Dùng lại biến idCard đã lấy
            newUser.setGender(request.getParameter("gender"));
            newUser.setAvatarUrl(avatarUrl);

            String dobString = request.getParameter("dob");
            if (dobString != null && !dobString.isEmpty()) {
                newUser.setDateOfBirth(LocalDate.parse(dobString));
            }

            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            int roleId = 2; // Giả sử roleId mặc định là 2 (CSKH)

            // 3. Gọi DAO để thêm nhân viên vào CSDL
            boolean success = userDAO.addEmployee(newUser, departmentId, positionId, roleId);

            // 4. Chuyển hướng và gửi email (giữ nguyên code cũ của bạn)
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

                try {
                    emailService.sendEmail(newUser.getEmail(), subject, message);
                } catch (MessagingException ex) {
                    Logger.getLogger(AddEmployeeServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.sendRedirect(request.getContextPath() + "/listEmployee");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi thêm nhân viên mới.");
                doGet(request, response);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            request.setAttribute("errorMessage", "Định dạng dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        }
    }
}
