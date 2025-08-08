///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package vn.edu.fpt.controller;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import vn.edu.fpt.dao.UserDAO;
//import vn.edu.fpt.model.User;
//
///**
// *
// * @author minhh
// */
//@WebServlet(name = "ViewEmployeeServlet", urlPatterns = {"/viewEmployee"})
//public class ViewEmployeeServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String idStr = request.getParameter("id");
//
//        try {
//            int employeeId = Integer.parseInt(idStr);
//            UserDAO userDAO = new UserDAO();
//            User employee = userDAO.getUserById(employeeId);
//
//            // Kiểm tra xem nhân viên có tồn tại VÀ đang hoạt động không
//            if (employee != null && employee.getIsDeleted() == 0) {
//                // Đặt đối tượng vào request
//                request.setAttribute("employee", employee);
//
//                // Chuyển tiếp đến trang JSP với đường dẫn ĐÚNG
//                request.getRequestDispatcher("/jsp/admin/viewEmployee.jsp").forward(request, response);
//                
//            } else {
//                // Nếu không tìm thấy hoặc nhân viên đã bị xóa, quay về trang danh sách với thông báo
//                String message = (employee == null) ? "Không tìm thấy nhân viên với ID này." : "Nhân viên này đã bị vô hiệu hóa.";
//                request.getSession().setAttribute("errorMessage", message);
//                response.sendRedirect(request.getContextPath() + "/listEmployee");
//            }
//
//        } catch (NumberFormatException e) {
//            // Xử lý khi ID không hợp lệ
//            request.getSession().setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
//            response.sendRedirect(request.getContextPath() + "/listEmployee");
//        } catch (Exception e) {
//            // Xử lý các lỗi ngoài dự kiến
//            e.printStackTrace(); // In lỗi ra console của server để debug
//            request.getSession().setAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại.");
//            response.sendRedirect(request.getContextPath() + "/listEmployee");
//        }
//    }
//}