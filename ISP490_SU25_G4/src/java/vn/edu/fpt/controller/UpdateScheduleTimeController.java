package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import vn.edu.fpt.dao.MaintenanceScheduleDAO; // Đảm bảo import đúng DAO của bạn

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import org.json.JSONException;

@WebServlet(name = "updateScheduleController", urlPatterns = {"/updateScheduleTime"})
public class UpdateScheduleTimeController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- 1. Thiết lập phản hồi JSON ---
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // --- 2. Đọc JSON từ request body ---
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());

            // --- 3. Lấy dữ liệu từ JSON ---
            int id = jsonRequest.getInt("id");
            LocalDate scheduledDate = LocalDate.parse(jsonRequest.getString("scheduledDate"));
            
            // Xử lý các giá trị có thể là null một cách an toàn hơn
            LocalDate endDate = parseNullableDate(jsonRequest, "endDate");
            LocalTime startTime = parseNullableTime(jsonRequest, "startTime");
            LocalTime endTime = parseNullableTime(jsonRequest, "endTime");

            // --- 4. Cập nhật vào cơ sở dữ liệu ---
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            boolean success = dao.updateScheduleByDragDrop(id, scheduledDate, endDate, startTime, endTime);

            // --- 5. Tạo và gửi phản hồi JSON ---
            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Lịch trình đã được cập nhật thành công.");
                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            } else {
                // Nếu không thành công, trả về lỗi server vì đây là một lỗi logic hoặc DB
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Cập nhật thất bại. Không tìm thấy lịch trình hoặc có lỗi xảy ra ở CSDL.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            }

        } catch (JSONException | DateTimeParseException e) {
            // Bắt lỗi khi dữ liệu JSON không hợp lệ hoặc sai định dạng ngày/giờ
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Lỗi 400
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Dữ liệu gửi lên không hợp lệ: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra log để gỡ lỗi
        } catch (Exception e) {
            // Bắt các lỗi không mong muốn khác (ví dụ: lỗi kết nối CSDL trong DAO)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Lỗi 500
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Đã có lỗi không mong muốn xảy ra ở máy chủ.");
            e.printStackTrace(); // Rất quan trọng: In lỗi chi tiết ra log của server
        }

        out.print(jsonResponse.toString());
        out.flush();
    }

    // --- CÁC HÀM HỖ TRỢ ĐỂ CODE GỌN HƠN ---
    private LocalDate parseNullableDate(JSONObject json, String key) {
        if (json.isNull(key) || json.optString(key).isEmpty()) {
            return null;
        }
        return LocalDate.parse(json.getString(key));
    }

    private LocalTime parseNullableTime(JSONObject json, String key) {
        if (json.isNull(key) || json.optString(key).isEmpty()) {
            return null;
        }
        return LocalTime.parse(json.getString(key));
    }
}