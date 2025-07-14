/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.PositionDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.Position;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.util.List;


/**
 *
 * @author minhh
 */
@WebServlet(name = "EditEmployeeServlet", urlPatterns = {"/admin/employees/edit"})
@MultipartConfig // sửa cả ảnh đại diện
public class EditEmployeeServlet extends HttpServlet {

   // HIỂN THỊ FORM CHỈNH SỬA
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("id"));
            
            UserDAO userDAO = new UserDAO();
            DepartmentDAO departmentDAO = new DepartmentDAO();
            PositionDAO positionDAO = new PositionDAO();

            // Lấy thông tin của nhân viên cần sửa
            User employee = userDAO.getUserById(employeeId);
            
            // Lấy TẤT CẢ các phòng ban và chức vụ để đưa vào dropdown
            List<Department> departmentList = departmentDAO.getAllDepartments();
            List<Position> positionList = positionDAO.getAllPositions();

            // Đặt tất cả vào request
            request.setAttribute("employee", employee);
            request.setAttribute("departmentList", departmentList);
            request.setAttribute("positionList", positionList);
            
            // Chuyển đến trang JSP
            request.getRequestDispatcher("/jsp/admin/editEmployee.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi
        }
    }

    // XỬ LÝ VIỆC LƯU THAY ĐỔI
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy tất cả dữ liệu từ form
            int id = Integer.parseInt(request.getParameter("id"));
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int positionId = Integer.parseInt(request.getParameter("positionId"));
            

            // Tạo đối tượng User với thông tin đã cập nhật
            User userToUpdate = new User();
            userToUpdate.setId(id);
            userToUpdate.setFirstName(fullName); 
            userToUpdate.setPhoneNumber(phone);
            // set các trường khác...

            UserDAO userDAO = new UserDAO();
            // Gọi hàm cập nhật trong DAO (sẽ tạo ở bước 4)
            boolean success = userDAO.updateEmployee(userToUpdate, departmentId, positionId);
            
            // Chuyển hướng về trang danh sách
            response.sendRedirect(request.getContextPath() + "/admin/employees/list?update=" + (success ? "success" : "fail"));

        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi
        }
    }

}
