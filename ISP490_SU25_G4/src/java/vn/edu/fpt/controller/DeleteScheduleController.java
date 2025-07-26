package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;

import java.io.IOException;

@WebServlet(name = "deleteScheduleController", urlPatterns = {"/deleteSchedule"})
public class DeleteScheduleController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Lấy ID của schedule cần xóa
            String scheduleIdStr = request.getParameter("scheduleId");
            
            if (scheduleIdStr == null || scheduleIdStr.trim().isEmpty()) {
                request.getSession().setAttribute("error", "ID lịch bảo trì không hợp lệ");
                response.sendRedirect(request.getContextPath() + "/listSchedule");
                return;
            }
            
            int scheduleId = Integer.parseInt(scheduleIdStr);
            
            // Gọi DAO để xóa
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            boolean deleteSuccess = dao.deleteMaintenanceSchedule(scheduleId);
            
            if (deleteSuccess) {
                // Xóa thành công
                request.getSession().setAttribute("success", "Xóa lịch bảo trì thành công!");
            } else {
                // Xóa thất bại
                request.getSession().setAttribute("error", "Không thể xóa lịch bảo trì. Vui lòng thử lại!");
            }
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID lịch bảo trì không hợp lệ");
            e.printStackTrace();
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Có lỗi xảy ra khi xóa lịch bảo trì: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Redirect về trang danh sách
        response.sendRedirect(request.getContextPath() + "/listSchedule");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng GET request thành POST
        doPost(request, response);
    }
}