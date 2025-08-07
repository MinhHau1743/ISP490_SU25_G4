/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import com.sun.jdi.connect.spi.Connection;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Ward;

/**
 *
 * @author datnt
 */
@WebServlet(name = "EditProfileController", urlPatterns = {"/editProfile"})
@MultipartConfig
public class EditProfileController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EditProfileController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditProfileController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        System.out.println("\n2. === TRONG EditProfileController (doGet) ===");

        if (session == null || session.getAttribute("user") == null) {
            System.out.println(">>> LỖI: Không tìm thấy user trong session.");
            response.sendRedirect("login.jsp");
            return;
        }

        User loggedInUser = (User) session.getAttribute("user");

        // Lấy id từ parameter nếu có, hoặc từ session
        String idParam = request.getParameter("id");
        int userId = loggedInUser.getId();
        if (idParam != null) {
            try {
                userId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                System.out.println(">>> LỖI: id không hợp lệ.");
                response.sendRedirect("error.jsp");
                return;
            }
        }

        EnterpriseDAO addressDAO = new EnterpriseDAO();
        UserDAO userDAO = new UserDAO();
        User userToEdit = userDAO.getUserById(userId);

        if (userToEdit == null) {
            System.out.println(">>> LỖI: Không tìm thấy user trong DB.");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Load danh sách tỉnh/thành
            List<Province> provinces = addressDAO.getAllProvinces();
            request.setAttribute("provinces", provinces);

            // Nếu user đã có địa chỉ, load luôn danh sách quận/huyện và phường/xã tương ứng
            List<District> districts = new ArrayList<>();
            List<Ward> wards = new ArrayList<>();

            if (userToEdit.getProvinceId() != null && userToEdit.getProvinceId() > 0) {
                districts = addressDAO.getDistrictsByProvinceId(userToEdit.getProvinceId());
                request.setAttribute("districts", districts);
            }
            if (userToEdit.getDistrictId() != null && userToEdit.getDistrictId() > 0) {
                wards = addressDAO.getWardsByDistrictId(userToEdit.getDistrictId());
                request.setAttribute("wards", wards);
            }

            // Load list departments
            DepartmentDAO departmentDAO = new DepartmentDAO();
            List<Department> departments = departmentDAO.getAllDepartments();
            request.setAttribute("departments", departments);

            request.setAttribute("user", userToEdit);
            // Update session để sync
            session.setAttribute("user", userToEdit);

            request.getRequestDispatcher("editProfile.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        java.sql.Connection conn = null;
        UserDAO userDAO = new UserDAO();
        EnterpriseDAO addressDAO = new EnterpriseDAO();

        // Lấy dữ liệu từ form
        String employeeCode = request.getParameter("employeeCode");
        String lastName = request.getParameter("lastName");
        String middleName = request.getParameter("middleName");
        String firstName = request.getParameter("firstName");
        String phoneNumber = request.getParameter("phoneNumber");

        String department = request.getParameter("departmentId");
        String position = request.getParameter("position");
        String notes = request.getParameter("notes");

        String identityCardNumber = request.getParameter("identityCardNumber");
        String dateOfBirthStr = request.getParameter("dateOfBirth");
        LocalDate dateOfBirth = null;
        try {
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                dateOfBirth = LocalDate.parse(dateOfBirthStr); // Định dạng yyyy-MM-dd
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace(); // hoặc xử lý lỗi hợp lệ hơn
        }

        String gender = request.getParameter("gender");

        String address = request.getParameter("address");
        String email = request.getParameter("email");

        int cityId = Integer.parseInt(request.getParameter("province"));
        int districtId = Integer.parseInt(request.getParameter("district"));
        int wardId = Integer.parseInt(request.getParameter("ward"));
        // (Tuỳ bạn lưu ID, nếu cần)
        int userId = Integer.parseInt(request.getParameter("id")); // nếu bạn truyền id ẩn theo form

        // Gọi DAO hoặc service để cập nhật vào DB
        User user = new User();
        user.setId(userId);
        user.setEmployeeCode(employeeCode);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setFirstName(firstName);
        user.setPhoneNumber(phoneNumber);
        int departmentId = Integer.parseInt(department);
        user.setDepartmentId(departmentId);

        int positionId = userDAO.getPositionIdByName(position);
        user.setPositionId(positionId);

        int newAddressId = 0;  // Khởi tạo mặc định là 0 (hoặc giá trị không hợp lệ nếu cần)
        try {
            newAddressId = userDAO.insertAddress1(address, wardId, districtId, cityId);
        } catch (SQLException ex) {
            Logger.getLogger(EditProfileController.class.getName()).log(Level.SEVERE, null, ex);
            // Xử lý lỗi: Có thể redirect đến trang lỗi hoặc set newAddressId = user.getAddressId() cũ nếu cần
            // Ví dụ: response.sendRedirect("error.jsp"); return;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EditProfileController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        user.setAddressId(newAddressId);
        user.setNotes(notes);
        user.setIdentityCardNumber(identityCardNumber);
        user.setDateOfBirth(dateOfBirth);
        user.setGender(gender);
        user.setEmail(email);

        // Cập nhật avatar (nếu có file upload)
        Part avatarPart = request.getPart("avatar");
        if (avatarPart != null && avatarPart.getSize() > 0) {
            String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("/uploads/avatars");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String avatarPath = uploadPath + File.separator + fileName;
            avatarPart.write(avatarPath);

            // Lưu đường dẫn vào user
            user.setAvatarUrl("uploads/avatars/" + fileName);
        }

        userDAO.updateUser(user);

        // THÊM: Reload user từ DB để có dữ liệu đầy đủ (ID + name từ join)
        User updatedUser = userDAO.getUserById(userId);  // Giả sử getUserById join và set name cho display

        // Cập nhật session với updatedUser
        HttpSession session = request.getSession();
        Object userIDObj = session.getAttribute("userID");
        // nếu đăng nhập lần đầu thì xóa session check đăng nhập rồi đi đến dashboard
        if (userIDObj != null) {
            session.removeAttribute("ProductController");
            session.removeAttribute("userID");
            response.sendRedirect("dashboard.jsp");
        } else {
            session.setAttribute("user", updatedUser);

            // Chuyển về viewProfile
            response.sendRedirect("viewProfile");
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
