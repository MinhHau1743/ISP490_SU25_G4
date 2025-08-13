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
//import vn.edu.fpt.dao.CampaignDAO;
//import vn.edu.fpt.model.Campaign;
//
///**
// *
// * @author minhh
// */
//@WebServlet("/edit-campaign") // Đây là URL mà nút "Sửa" trên listCampaign.jsp sẽ trỏ tới
//public class EditCampaignServlet extends HttpServlet {
//
//    private static final long serialVersionUID = 1L;
//    private CampaignDAO campaignDAO; // Giả định bạn có một DAO để tương tác với DB
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        // Khởi tạo CampaignDAO ở đây. Đảm bảo CampaignDAO của bạn có phương thức getCampaignById()
//        campaignDAO = new CampaignDAO(); 
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String idParam = request.getParameter("id");
//        if (idParam != null && !idParam.isEmpty()) {
//            try {
//                int campaignId = Integer.parseInt(idParam);
//                Campaign campaign = campaignDAO.getCampaignById(campaignId); // Lấy chiến dịch từ DB
//
//                if (campaign != null) {
//                    request.setAttribute("campaign", campaign);
//                    // Chuyển tiếp đến trang JSP chỉnh sửa
//                    request.getRequestDispatcher("/jsp/admin/editCampaign.jsp").forward(request, response);
//                } else {
//                    // Nếu không tìm thấy chiến dịch, chuyển hướng về trang danh sách với thông báo lỗi
//                    String errorMessage = "Không tìm thấy chiến dịch với ID: " + campaignId;
//                    response.sendRedirect(request.getContextPath() + "/list-campaign?error=" + 
//                                            java.net.URLEncoder.encode(errorMessage, "UTF-8"));
//                }
//            } catch (NumberFormatException e) {
//                // Xử lý lỗi nếu ID không phải số
//                String errorMessage = "ID chiến dịch không hợp lệ.";
//                response.sendRedirect(request.getContextPath() + "/list-campaign?error=" + 
//                                            java.net.URLEncoder.encode(errorMessage, "UTF-8"));
//            } catch (Exception e) {
//                // Xử lý các lỗi khác (ví dụ: lỗi DB khi lấy dữ liệu)
//                e.printStackTrace(); // Log lỗi để debug
//                String errorMessage = "Có lỗi xảy ra khi lấy chi tiết chiến dịch.";
//                response.sendRedirect(request.getContextPath() + "/list-campaign?error=" + 
//                                            java.net.URLEncoder.encode(errorMessage, "UTF-8"));
//            }
//        } else {
//            // Nếu không có ID, chuyển hướng về trang danh sách
//            String errorMessage = "Không có ID chiến dịch được cung cấp để chỉnh sửa.";
//            response.sendRedirect(request.getContextPath() + "/list-campaign?error=" + 
//                                    java.net.URLEncoder.encode(errorMessage, "UTF-8"));
//        }
//    }
//}