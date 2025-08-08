//package vn.edu.fpt.controller;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//import vn.edu.fpt.dao.DepartmentDAO;
//import vn.edu.fpt.dao.PositionDAO;
//import vn.edu.fpt.dao.UserDAO;
//import vn.edu.fpt.model.Department;
//import vn.edu.fpt.model.Position;
//import vn.edu.fpt.model.User;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
///**
// *
// * @author minhh
// */
//
///**
// * Servlet này xử lý việc hiển thị và cập nhật thông tin nhân viên.
// */
//@WebServlet(name = "EditEmployeeServlet", urlPatterns = {"/editEmployee"})
//@MultipartConfig(
//        fileSizeThreshold = 1024 * 1024,      
//        maxFileSize = 1024 * 1024 * 10,     
//        maxRequestSize = 1024 * 1024 * 15   
//)
//public class EditEmployeeServlet extends HttpServlet {
//
//    private static final String UPLOAD_DIRECTORY = "uploads";
//
//    /**
//     * Hiển thị form chỉnh sửa thông tin nhân viên.
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//        throws ServletException, IOException {
//    try {
//        int employeeId = Integer.parseInt(request.getParameter("id"));
//
//        UserDAO userDAO = new UserDAO();
//        DepartmentDAO departmentDAO = new DepartmentDAO();
//        PositionDAO positionDAO = new PositionDAO();
//
//        User employee = userDAO.getUserById(employeeId);
//
//        // KIỂM TRA ĐÚNG: Phải tồn tại VÀ đang hoạt động (isDeleted == 0)
//        if (employee != null && employee.getIsDeleted() == 0) {
//            List<Department> departmentList = departmentDAO.getAllDepartments();
//            List<Position> positionList = positionDAO.getAllPositions();
//
//            request.setAttribute("employee", employee);
//            request.setAttribute("departments", departmentList);
//            request.setAttribute("positions", positionList);
//
//            request.getRequestDispatcher("/jsp/admin/editEmployee.jsp").forward(request, response);
//        } else {
//            // Xử lý khi nhân viên không tồn tại hoặc đã bị xóa
//            request.getSession().setAttribute("errorMessage", "Không thể sửa. Nhân viên không tồn tại hoặc đã bị vô hiệu hóa.");
//            response.sendRedirect(request.getContextPath() + "/listEmployee");
//        }
//
//    } catch (NumberFormatException e) {
//        request.getSession().setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
//        response.sendRedirect(request.getContextPath() + "/listEmployee");
//    } catch (Exception e) {
//        e.printStackTrace();
//        request.getSession().setAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại.");
//        response.sendRedirect(request.getContextPath() + "/listEmployee");
//    }
//}
//
//    /**
//     * Xử lý việc lưu các thay đổi và chuyển hướng về trang danh sách nhân viên.
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        String employeeIdStr = request.getParameter("id");
//
//        try {
//            // 1. Lấy dữ liệu từ form
//            int id = Integer.parseInt(employeeIdStr);
//            String lastName = request.getParameter("lastName");
//            String middleName = request.getParameter("middleName");
//            String firstName = request.getParameter("firstName");
//            String phoneNumber = request.getParameter("phoneNumber");
//            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
//            int positionId = Integer.parseInt(request.getParameter("positionId"));
//            String notes = request.getParameter("notes");
//            String identityCardNumber = request.getParameter("identityCardNumber");
//            String gender = request.getParameter("gender");
//            LocalDate dateOfBirth = LocalDate.parse(request.getParameter("dateOfBirth"));
//
//            // 2. Xử lý tải file avatar
//            String avatarUrl = saveAvatar(request);
//
//            // 3. Cập nhật đối tượng User
//            UserDAO userDAO = new UserDAO();
//            User userToUpdate = userDAO.getUserById(id);
//
//            userToUpdate.setLastName(lastName);
//            userToUpdate.setMiddleName(middleName);
//            userToUpdate.setFirstName(firstName);
//            userToUpdate.setPhoneNumber(phoneNumber);
//            userToUpdate.setDepartmentId(departmentId);
//            userToUpdate.setPositionId(positionId);
//            userToUpdate.setNotes(notes);
//            userToUpdate.setIdentityCardNumber(identityCardNumber);
//            userToUpdate.setDateOfBirth(dateOfBirth);
//            userToUpdate.setGender(gender);
//
//            if (avatarUrl != null && !avatarUrl.isEmpty()) {
//                userToUpdate.setAvatarUrl(avatarUrl);
//            }
//
//            // 4. Gọi DAO để cập nhật CSDL
//            boolean success = userDAO.updateEmployee(userToUpdate);
//
//            // 5. Chuyển hướng về trang danh sách nhân viên (listEmployee)
//            // =================== DÒNG THAY ĐỔI QUAN TRỌNG ===================
//            String redirectURL = request.getContextPath() + "/listEmployee"; 
//            // ===============================================================
//            
//            if (success) {
//                redirectURL += "?update=success";
//            } else {
//                redirectURL += "?update=fail";
//            }
//            response.sendRedirect(redirectURL);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() + "/listEmployee?update=error");
//        }
//    }
//
//    /**
//     * Lưu file avatar và trả về đường dẫn tương đối.
//     */
//    private String saveAvatar(HttpServletRequest request) throws IOException, ServletException {
//        Part filePart = request.getPart("avatar");
//        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//
//        if (fileName == null || fileName.isEmpty()) {
//            return null;
//        }
//
//        String applicationPath = request.getServletContext().getRealPath("");
//        String uploadFilePath = applicationPath + File.separator + UPLOAD_DIRECTORY + File.separator + "avatars";
//
//        File uploadDir = new File(uploadFilePath);
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//
//        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
//        filePart.write(uploadFilePath + File.separator + uniqueFileName);
//
//        return UPLOAD_DIRECTORY + "/avatars/" + uniqueFileName;
//    }
//}
