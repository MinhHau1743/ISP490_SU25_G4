///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
// */
//package vn.edu.fpt.controller;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import vn.edu.fpt.dao.UserDAO;
//
///**
// *
// * @author minhh
// */
//@WebServlet(name = "DeleteEmployeeServlet", urlPatterns = {"/deleteEmployee"})
//public class DeleteEmployeeServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        
//        // 1. Lấy ID của nhân viên từ parameter trên URL
//        String idStr = request.getParameter("id");
//        
//        if (idStr != null && !idStr.isEmpty()) {
//            try {
//                int userId = Integer.parseInt(idStr);
//                
//                // 2. Gọi DAO để thực hiện xóa mềm
//                UserDAO userDAO = new UserDAO();
//                boolean success = userDAO.softDeleteUserById(userId);
//                
//                if (success) {
//                    System.out.println("Xóa mềm thành công nhân viên có ID: " + userId);
//                } else {
//                    System.err.println("Xóa mềm thất bại cho nhân viên có ID: " + userId);
//                }
//                
//            } catch (NumberFormatException e) {
//                System.err.println("ID nhân viên không hợp lệ: " + idStr);
//                e.printStackTrace();
//            }
//        }
//        
//        // 3. Chuyển hướng người dùng trở lại trang danh sách nhân viên
//        response.sendRedirect(request.getContextPath() + "/listEmployee");
//    }
//}