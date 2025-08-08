/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

// Merged imports from all servlets
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import vn.edu.fpt.common.EmailService;
import vn.edu.fpt.common.EmailSender;
import vn.edu.fpt.common.GmailSender;
import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.PositionDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.Position;
import vn.edu.fpt.model.User;

/**
 * A centralized controller to handle all employee-related actions. This servlet
 * manages listing, viewing, adding, editing, deleting, and updating the status
 * of employees.
 */
@WebServlet(name = "EmployeeController", urlPatterns = {"/employee"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class EmployeeController extends HttpServlet {

    /**
     * Handles GET requests and routes them to the appropriate method based on
     * the 'action' parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "view":
                    viewEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "updateStatus":
                    updateEmployeeStatus(request, response);
                    break;
                case "list":
                default:
                    listEmployees(request, response);
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, "Error in doGet action=" + action, e);
            request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect("employee?action=list");
        }
    }

    /**
     * Handles POST requests for adding and updating employee data.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "add":
                    addEmployee(request, response);
                    break;
                case "edit":
                    updateEmployee(request, response);
                    break;
                default:
                    response.sendRedirect("employee?action=list");
                    break;
            }
        } catch (Exception e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, "Error in doPost action=" + action, e);
            request.getSession().setAttribute("errorMessage", "An error occurred during submission: " + e.getMessage());
            response.sendRedirect("employee?action=list");
        }
    }

    //==========================================================================
    // ACTION METHODS
    //==========================================================================
    /**
     * Displays the list of all employees with pagination and search. Original:
     * ListAllEmployeesServlet
     */
    private void listEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        String searchQuery = request.getParameter("searchQuery");
        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");

        int page = (pageStr == null || pageStr.isEmpty()) ? 1 : Integer.parseInt(pageStr);
        int pageSize = (sizeStr == null || sizeStr.isEmpty()) ? 12 : Integer.parseInt(sizeStr);

        List<User> employeeList;
        int totalEmployees;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            employeeList = userDAO.searchEmployeesByName(searchQuery, page, pageSize);
            totalEmployees = userDAO.countSearchedEmployees(searchQuery);
            request.setAttribute("searchQuery", searchQuery);
        } else {
            employeeList = userDAO.getAllEmployeesPaginated(page, pageSize);
            totalEmployees = userDAO.getTotalEmployeeCount();
        }

        int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        request.setAttribute("employeeList", employeeList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("/jsp/admin/listAllEmployees.jsp").forward(request, response);
    }

    /**
     * Shows the form to add a new employee. Original: AddEmployeeServlet
     * (doGet)
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DepartmentDAO departmentDAO = new DepartmentDAO();
        PositionDAO positionDAO = new PositionDAO();
        request.setAttribute("departmentList", departmentDAO.getAllDepartments());
        request.setAttribute("positionList", positionDAO.getAllPositions());
        request.getRequestDispatcher("/jsp/admin/addEmployee.jsp").forward(request, response);
    }

    /**
     * Processes the submission of the new employee form. Original:
     * AddEmployeeServlet (doPost)
     */
    private void addEmployee(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        String email = request.getParameter("email");
        String idCard = request.getParameter("idCard");

        User existingUser = userDAO.findUserByEmailOrIdCard(email, idCard);

        if (existingUser != null) {
            if (existingUser.getIsDeleted() == 1) { // Soft-deleted
                userDAO.reactivateUser(existingUser.getId());
                int departmentId = Integer.parseInt(request.getParameter("departmentId"));
                int positionId = Integer.parseInt(request.getParameter("positionId"));
                userDAO.updateUserDepartmentAndPosition(existingUser.getId(), departmentId, positionId);
                request.getSession().setAttribute("successMessage", "Reactivated and updated employee: " + existingUser.getFullName());
                response.sendRedirect("employee?action=list");
                return;
            } else { // Active user
                request.setAttribute("errorMessage", "Employee with this Email or ID Card already exists.");
                showAddForm(request, response);
                return;
            }
        }

        try {
            User newUser = new User();
            // Parse full name
            String fullName = request.getParameter("employeeName");
            String[] nameParts = fullName.trim().split("\\s+");
            if (nameParts.length >= 1) {
                newUser.setLastName(nameParts[0]);
                newUser.setFirstName(nameParts.length >= 2 ? nameParts[nameParts.length - 1] : "");
                if (nameParts.length > 2) {
                    StringBuilder middleName = new StringBuilder();
                    for (int i = 1; i < nameParts.length - 1; i++) {
                        middleName.append(nameParts[i]).append(" ");
                    }
                    newUser.setMiddleName(middleName.toString().trim());
                } else {
                    newUser.setMiddleName("");
                }
            }

            // Set other properties
            newUser.setPhoneNumber(request.getParameter("phone"));
            newUser.setEmail(email);
            newUser.setNotes(request.getParameter("notes"));
            newUser.setIdentityCardNumber(idCard);
            newUser.setGender(request.getParameter("gender"));
            newUser.setAvatarUrl(saveAvatar(request, "avatar"));

            String dobString = request.getParameter("dob");
            if (dobString != null && !dobString.isEmpty()) {
                newUser.setDateOfBirth(LocalDate.parse(dobString));
            }

            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            int roleId = 2; // Default role ID

            if (userDAO.addEmployee(newUser, departmentId, positionId, roleId)) {
                sendWelcomeEmail(newUser);
                request.getSession().setAttribute("successMessage", "Successfully added new employee: " + newUser.getFullName());
                response.sendRedirect("employee?action=list");
            } else {
                request.setAttribute("errorMessage", "Failed to add new employee.");
                showAddForm(request, response);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            request.setAttribute("errorMessage", "Invalid data format: " + e.getMessage());
            showAddForm(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error uploading file: " + e.getMessage());
            showAddForm(request, response);
        }
    }

    /**
     * Shows the form to edit an existing employee. Original:
     * EditEmployeeServlet (doGet)
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("id"));
            UserDAO userDAO = new UserDAO();
            User employee = userDAO.getUserById(employeeId);

            if (employee != null && employee.getIsDeleted() == 0) {
                request.setAttribute("employee", employee);
                request.setAttribute("departments", new DepartmentDAO().getAllDepartments());
                request.setAttribute("positions", new PositionDAO().getAllPositions());
                request.getRequestDispatcher("/jsp/admin/editEmployee.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Cannot edit. Employee not found or disabled.");
                response.sendRedirect("employee?action=list");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid employee ID.");
            response.sendRedirect("employee?action=list");
        }
    }

    /**
     * Processes the submission of the employee edit form. Original:
     * EditEmployeeServlet (doPost)
     */
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            UserDAO userDAO = new UserDAO();
            User userToUpdate = userDAO.getUserById(id);

            if (userToUpdate == null) {
                request.getSession().setAttribute("errorMessage", "Employee not found for update.");
                response.sendRedirect("employee?action=list");
                return;
            }

            // Update user object with form data
            userToUpdate.setLastName(request.getParameter("lastName"));
            userToUpdate.setMiddleName(request.getParameter("middleName"));
            userToUpdate.setFirstName(request.getParameter("firstName"));
            userToUpdate.setPhoneNumber(request.getParameter("phoneNumber"));
            userToUpdate.setDepartmentId(Integer.parseInt(request.getParameter("departmentId")));
            userToUpdate.setPositionId(Integer.parseInt(request.getParameter("positionId")));
            userToUpdate.setNotes(request.getParameter("notes"));
            userToUpdate.setIdentityCardNumber(request.getParameter("identityCardNumber"));
            userToUpdate.setGender(request.getParameter("gender"));
            userToUpdate.setDateOfBirth(LocalDate.parse(request.getParameter("dateOfBirth")));

            String avatarUrl = saveAvatar(request, "avatar");
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                userToUpdate.setAvatarUrl(avatarUrl);
            }

            if (userDAO.updateEmployee(userToUpdate)) {
                request.getSession().setAttribute("successMessage", "Employee information updated successfully.");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update employee information.");
            }
            response.sendRedirect("employee?action=list");

        } catch (Exception e) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, "Error updating employee", e);
            request.getSession().setAttribute("errorMessage", "An error occurred during the update.");
            response.sendRedirect("employee?action=list");
        }
    }

    /**
     * Displays the details of a single employee. Original: ViewEmployeeServlet
     */
    private void viewEmployee(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("id"));
            UserDAO userDAO = new UserDAO();
            User employee = userDAO.getUserById(employeeId);

            if (employee != null && employee.getIsDeleted() == 0) {
                request.setAttribute("employee", employee);
                request.getRequestDispatcher("/jsp/admin/viewEmployee.jsp").forward(request, response);
            } else {
                request.getSession().setAttribute("errorMessage", "Employee not found or has been disabled.");
                response.sendRedirect("employee?action=list");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid employee ID.");
            response.sendRedirect("employee?action=list");
        }
    }

    /**
     * Soft-deletes an employee by setting isDeleted=1. Original:
     * DeleteEmployeeServlet
     */
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            UserDAO userDAO = new UserDAO();
            if (userDAO.softDeleteUserById(userId)) {
                request.getSession().setAttribute("successMessage", "Employee disabled successfully.");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to disable employee.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid employee ID.");
        }
        response.sendRedirect("employee?action=list");
    }

    /**
     * Updates an employee's status (isDeleted = 0 for active, 1 for inactive).
     * Original: UpdateEmployeeStatusServlet
     */
    private void updateEmployeeStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            int newStatus = Integer.parseInt(request.getParameter("status"));

            if (new UserDAO().updateSoftDeleteStatus(userId, newStatus)) {
                String statusMsg = (newStatus == 0) ? "activated" : "disabled";
                request.getSession().setAttribute("successMessage", "Employee status successfully " + statusMsg + ".");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update employee status.");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid parameter for status update.");
        }

        // Redirect back, preserving search and pagination
        String page = request.getParameter("page");
        String searchQuery = request.getParameter("searchQuery");
        StringBuilder redirectURL = new StringBuilder("employee?action=list");
        if (page != null) {
            redirectURL.append("&page=").append(page);
        }
        if (searchQuery != null) {
            redirectURL.append("&searchQuery=").append(java.net.URLEncoder.encode(searchQuery, "UTF-8"));
        }

        response.sendRedirect(redirectURL.toString());
    }

    //==========================================================================
    // HELPER METHODS
    //==========================================================================
    /**
     * Saves an uploaded avatar and returns its relative path for web access.
     */
    private String saveAvatar(HttpServletRequest request, String partName) throws IOException, ServletException {
        Part filePart = request.getPart(partName);
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (fileName.isEmpty()) {
            return null;
        }

        String uploadPath = getServletContext().getRealPath("") + File.separator + "image";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        filePart.write(uploadPath + File.separator + uniqueFileName);

        return "image/" + uniqueFileName;
    }

    /**
     * Sends a welcome email to a newly created user.
     */
    private void sendWelcomeEmail(User newUser) {
        try {
            EmailSender emailSender = new GmailSender();
            EmailService emailService = new EmailService(emailSender);

            String subject = "Welcome to the Company!";
            String message = "<h3>Hello " + newUser.getFullName() + ",</h3>"
                    + "<p>Your account has been created successfully.</p>"
                    + "<p>Login details:</p>"
                    + "<ul><li>Email: " + newUser.getEmail() + "</li>"
                    + "<li>Default Password: DPCRM@12345</li></ul>"
                    + "<p>Please change your password after logging in.</p>";

            emailService.sendEmail(newUser.getEmail(), subject, message);
        } catch (MessagingException ex) {
            Logger.getLogger(EmployeeController.class.getName()).log(Level.SEVERE, "Failed to send welcome email", ex);
        }
    }
}
