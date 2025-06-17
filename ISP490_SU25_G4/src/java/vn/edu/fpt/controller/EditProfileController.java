/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.User;

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
// THÊM DÒNG NÀY VÀO
        System.out.println("\n2. === TRONG EditProfileController (doGet) ===");
        System.out.println("User trong session là: " + session.getAttribute("user"));

        // Kiểm tra user có trong session không
        if (session != null && session.getAttribute("user") != null) {
            // Lấy user từ session để hiển thị trên form edit
            User userToEdit = (User) session.getAttribute("user");
            request.setAttribute("user", userToEdit); // Đặt user vào request để trang jsp có thể lấy

            // Chuyển tiếp đến trang JSP
            request.getRequestDispatcher("editProfile.jsp").forward(request, response);
        } else {
            // Nếu không có user trong session, đá về trang đăng nhập
            System.out.println(">>> LỖI: Không tìm thấy user trong session khi vào doGet của EditProfile.");
            response.sendRedirect("login.jsp");
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

        HttpSession session = request.getSession(false); // Dùng false để không tạo session mới nếu chưa có

//        // THÊM CÁC DÒNG NÀY ĐỂ DEBUG
//        System.out.println("\n--- UPDATE PROFILE SUBMITTED ---");
//        if (session != null) {
//            System.out.println("Session ID KHI CẬP NHẬT: " + session.getId());
//            // In ra đối tượng user để xem nó có thật sự là null không
//            System.out.println("User lấy từ session: " + session.getAttribute("user"));
//        } else {
//            System.out.println("SESSION KHÔNG TỒN TẠI KHI CẬP NHẬT!");
//        }
//        // THÊM DÒNG NÀY VÀO
//        System.out.println("\n3. === TRONG EditProfileController (doPost) ===");
//        System.out.println("User trong session là: " + session.getAttribute("user"));

        User currentUser = (User) session.getAttribute("user");

        // Nếu người dùng chưa đăng nhập, không cho phép cập nhật
        if (currentUser == null) {
            response.sendRedirect("dashboard.jsp");
            return;
        }

        try {
            // Lấy tất cả thông tin từ form
            String lastName = request.getParameter("lastName");
            String middleName = request.getParameter("middleName");
            String firstName = request.getParameter("firstName");
            String phoneNumber = request.getParameter("phoneNumber");
            String department = request.getParameter("department");
            String position = request.getParameter("position");
            String notes = request.getParameter("notes");
            String identityCardNumber = request.getParameter("identityCardNumber");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            String gender = request.getParameter("gender");
            String address = request.getParameter("address");
            String ward = request.getParameter("ward");
            String district = request.getParameter("district");
            String city = request.getParameter("city");
            String socialMediaLink = request.getParameter("socialMediaLink");
            String email = request.getParameter("email");

            // Xử lý file ảnh đại diện
            Part part = request.getPart("avatar");
            String avatarUrl = currentUser.getAvatarUrl();

            if (part != null && part.getSize() > 0) {
                String realPath = request.getServletContext().getRealPath("/uploads");
                String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                Path uploadPath = Paths.get(realPath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                part.write(realPath + "/" + fileName);
                avatarUrl = "uploads/" + fileName;
            }

            // Cập nhật đối tượng User
            currentUser.setLastName(lastName);
            currentUser.setMiddleName(middleName);
            currentUser.setFirstName(firstName);
            currentUser.setPhoneNumber(phoneNumber);
            currentUser.setDepartment(department);
            currentUser.setPosition(position);
            currentUser.setNotes(notes);
            currentUser.setIdentityCardNumber(identityCardNumber);
            if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                currentUser.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
            }
            currentUser.setGender(gender);
            currentUser.setAddress(address);
            currentUser.setWard(ward);
            currentUser.setDistrict(district);
            currentUser.setCity(city);
            currentUser.setSocialMediaLink(socialMediaLink);
            currentUser.setEmail(email);
            currentUser.setAvatarUrl(avatarUrl);

            // Gọi DAO để cập nhật vào database
            UserDAO userDAO = new UserDAO();
            boolean isUpdated = userDAO.updateUserProfile(currentUser);

            if (isUpdated) {
                session.setAttribute("user", currentUser);
                // Bạn của bạn (người làm trang view) sẽ xử lý việc hiển thị thông báo này
                response.sendRedirect("viewProfile?id=" + currentUser.getId() + "&status=success");
            } else {
                // Bạn của bạn (người làm trang edit) sẽ xử lý việc hiển thị thông báo này
                response.sendRedirect("editProfile?id=" + currentUser.getId() + "&status=fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("editProfile?id=" + currentUser.getId() + "&status=error");
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
