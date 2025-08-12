package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.PositionDAO;
import vn.edu.fpt.dao.RoleDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Position;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Ward;

@WebServlet(name = "ProfileController", urlPatterns = {"/profile"})
@MultipartConfig
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        try {
            switch (action) {
                case "edit":
                    showEditForm(request, response);
                    break;
                // ## FIX: Bổ sung các case để xử lý AJAX request cho dropdown ##
                case "getDistricts":
                    getDistricts(request, response);
                    break;
                case "getWards":
                    getWards(request, response);
                    break;
                case "view":
                default:
                    showViewForm(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi xảy ra trong ProfileController", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("update".equals(action)) {
            handleUpdateProfile(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/profile?action=view");
        }
    }

    private void showViewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User userProfile = userDAO.getUserById(loggedInUser.getId());

            if (userProfile != null) {
                request.setAttribute("profile", userProfile);
                request.getRequestDispatcher("/viewProfile.jsp").forward(request, response);
            } else {
                session.invalidate();
                response.sendRedirect("login.jsp?error=User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Không thể tải thông tin người dùng từ DB", e);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // ## FIX: Bọc các lời gọi DAO trong try-catch để xử lý "checked exception" ##
        try {
            UserDAO userDAO = new UserDAO();
            User userToEdit = userDAO.getUserById(loggedInUser.getId());

            if (userToEdit != null) {
                AddressDAO addressDAO = new AddressDAO();
                DepartmentDAO departmentDAO = new DepartmentDAO();
                PositionDAO positionDAO = new PositionDAO();
                RoleDAO roleDAO = new RoleDAO();

                List<Province> provinces = addressDAO.getAllProvinces();
                List<Department> departments = departmentDAO.getAllDepartments();
                List<Position> positions = positionDAO.getAllPositions();
                List<Role> roles = roleDAO.getAllRoles();

                request.setAttribute("user", userToEdit);
                request.setAttribute("provinces", provinces);
                request.setAttribute("departments", departments);
                request.setAttribute("positions", positions);
                request.setAttribute("roles", roles);

                request.getRequestDispatcher("/editProfile.jsp").forward(request, response);
            } else {
                response.sendRedirect("404.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?action=view&error=loadEditFormFailed");
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 1. Lấy toàn bộ dữ liệu từ form
            int userId = Integer.parseInt(request.getParameter("id"));
            String lastName = request.getParameter("lastName");
            String middleName = request.getParameter("middleName");
            String firstName = request.getParameter("firstName");
            String phoneNumber = request.getParameter("phoneNumber");
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            String notes = request.getParameter("notes");
            String identityCardNumber = request.getParameter("identityCardNumber");
            String dateOfBirthStr = request.getParameter("dateOfBirth");
            Date dateOfBirth = (dateOfBirthStr == null || dateOfBirthStr.isEmpty()) ? null : Date.valueOf(dateOfBirthStr);
            String gender = request.getParameter("gender");
            String streetAddress = request.getParameter("streetAddress");
            int provinceId = Integer.parseInt(request.getParameter("provinceId"));
            int districtId = Integer.parseInt(request.getParameter("districtId"));
            int wardId = Integer.parseInt(request.getParameter("wardId"));

            // 2. Tạo đối tượng User để chứa thông tin cập nhật
            User userToUpdate = new User();
            userToUpdate.setId(userId);

            // Giữ lại các giá trị không thay đổi từ form
            userToUpdate.setEmail(loggedInUser.getEmail());

            // ## FIX: Thêm dòng này để giữ lại mã nhân viên ##
            userToUpdate.setEmployeeCode(loggedInUser.getEmployeeCode());

            // Gán các giá trị được cập nhật từ form
            userToUpdate.setLastName(lastName);
            userToUpdate.setMiddleName(middleName);
            userToUpdate.setFirstName(firstName);
            userToUpdate.setPhoneNumber(phoneNumber);
            userToUpdate.setDepartmentId(departmentId);
            userToUpdate.setPositionId(positionId);
            userToUpdate.setRoleId(roleId);
            userToUpdate.setNotes(notes);
            userToUpdate.setIdentityCardNumber(identityCardNumber);
            userToUpdate.setGender(gender);
            if (dateOfBirth != null) {
                userToUpdate.setDateOfBirth(dateOfBirth.toLocalDate());
            }

            // 3. Khai báo các DAO
            UserDAO userDAO = new UserDAO();
            AddressDAO addressDAO = new AddressDAO();

            // 4. Xử lý địa chỉ
            Integer addressId = loggedInUser.getAddressId(); // Dùng Integer cho an toàn
            if (addressId != null && addressId > 0) {
                addressDAO.updateAddress(addressId, streetAddress, wardId, districtId, provinceId);
                userToUpdate.setAddressId(addressId);
            } else {
                int newAddressId = addressDAO.insertAddress(streetAddress, wardId, districtId, provinceId);
                userToUpdate.setAddressId(newAddressId);
            }

            // 5. Xử lý upload avatar
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                filePart.write(uploadPath + File.separator + fileName);
                userToUpdate.setAvatarUrl("uploads/" + fileName);
            } else {
                userToUpdate.setAvatarUrl(loggedInUser.getAvatarUrl());
            }

            // 6. Cập nhật vào database
            userDAO.updateUser(userToUpdate);

            // 7. Cập nhật session và chuyển hướng
            User updatedUser = userDAO.getUserById(userId);
            session.setAttribute("user", updatedUser);
            response.sendRedirect(request.getContextPath() + "/profile?action=view&update=success");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã có lỗi xảy ra: " + e.getMessage());
            showEditForm(request, response);
        }
    }

    // ## FIX: Thêm 2 phương thức để xử lý AJAX request ##
    private void getDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String provinceIdStr = request.getParameter("provinceId");
        List<District> districts = new ArrayList<>();
        if (provinceIdStr != null && !provinceIdStr.isEmpty()) {
            try {
                int provinceId = Integer.parseInt(provinceIdStr);
                districts = new AddressDAO().getDistrictsByProvinceId(provinceId);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        response.getWriter().write(new Gson().toJson(districts));
    }

    private void getWards(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String districtIdStr = request.getParameter("districtId");
        List<Ward> wards = new ArrayList<>();
        if (districtIdStr != null && !districtIdStr.isEmpty()) {
            try {
                int districtId = Integer.parseInt(districtIdStr);
                wards = new AddressDAO().getWardsByDistrictId(districtId);
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        response.getWriter().write(new Gson().toJson(wards));
    }

    @Override
    public String getServletInfo() {
        return "Single Controller for Profile viewing and editing";
    }
}
