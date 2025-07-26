package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "deleteScheduleController", urlPatterns = {"/deleteSchedule"})
public class DeleteScheduleController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            // Đọc JSON từ request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());
            int scheduleId = json.getInt("id");
            
            // Gọi DAO để xóa
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            boolean deleteSuccess = dao.deleteMaintenanceSchedule(scheduleId);
            
            if (deleteSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\": \"success\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\": \"error\", \"message\": \"Không thể xóa lịch bảo trì\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\": \"error\", \"message\": \"Có lỗi xảy ra khi xóa lịch bảo trì: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Không hỗ trợ GET, trả lỗi
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().print("{\"status\": \"error\", \"message\": \"Phương thức không được hỗ trợ\"}");
    }
}