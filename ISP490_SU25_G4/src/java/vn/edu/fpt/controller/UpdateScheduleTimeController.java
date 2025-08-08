package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.json.JSONException;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@WebServlet(name = "UpdateScheduleTimeController", urlPatterns = {"/updateScheduleTime"})
public class UpdateScheduleTimeController extends HttpServlet {

    // Khoảng thời gian mặc định cho một sự kiện khi được kéo vào slot có giờ
    // Ví dụ: 60 phút
    private static final int DEFAULT_DURATION_MINUTES = 60;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đảm bảo đọc/ghi UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        JSONObject jsonResponse = new JSONObject();

        try {
            // --- Đọc JSON từ body ---
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (sb.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Request body trống.");
                writeResponse(response, jsonResponse);
                return;
            }

            JSONObject jsonRequest = new JSONObject(sb.toString());

            // --- Lấy và xác thực dữ liệu đầu vào ---
            // ID và scheduledDate là bắt buộc
            int id = jsonRequest.getInt("id");
            LocalDate scheduledDate = LocalDate.parse(jsonRequest.getString("scheduledDate"));

            // Các trường có thể là null
            LocalDate endDate = parseNullableDate(jsonRequest, "endDate");
            LocalTime startTime = parseNullableTime(jsonRequest, "startTime");
            LocalTime endTime = parseNullableTime(jsonRequest, "endTime");

            // --- ÁP DỤNG LOGIC SERVER ---

            // 1. Nếu sự kiện được kéo vào slot CÓ GIỜ (startTime có giá trị)
            //    nhưng frontend không gửi endTime, server sẽ tự tính toán.
            if (startTime != null && endTime == null) {
                endTime = startTime.plusMinutes(DEFAULT_DURATION_MINUTES);
            }
            
            // 2. Nếu sự kiện được kéo vào slot CẢ NGÀY, frontend sẽ gửi startTime là null.
            //    Lúc này, logic trên sẽ không chạy, giữ nguyên startTime và endTime là null, điều này là CHÍNH XÁC.

            // 3. Validate logic ngày: Nếu endDate có giá trị nhưng lại trước scheduledDate thì vô hiệu hóa nó.
            if (endDate != null && endDate.isBefore(scheduledDate)) {
                endDate = null;
            }

            // --- Gọi DAO để cập nhật vào CSDL ---
            MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
            boolean success = dao.updateScheduleByDragDrop(id, scheduledDate, endDate, startTime, endTime);

            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Lịch trình đã được cập nhật thành công.");
                
                // Trả về payload chứa dữ liệu đã được server chuẩn hóa.
                // Frontend có thể dùng payload này để cập nhật lại giao diện một cách chính xác.
                JSONObject payload = new JSONObject();
                payload.put("id", id);
                payload.put("scheduledDate", scheduledDate.toString());
                payload.put("endDate", endDate != null ? endDate.toString() : JSONObject.NULL);
                payload.put("startTime", startTime != null ? startTime.toString() : JSONObject.NULL);
                payload.put("endTime", endTime != null ? endTime.toString() : JSONObject.NULL);
                jsonResponse.put("payload", payload);
                
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Cập nhật thất bại. Lịch trình không tồn tại hoặc có lỗi cơ sở dữ liệu.");
            }

            writeResponse(response, jsonResponse);

        } catch (JSONException | DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeError(response, "Dữ liệu gửi lên không hợp lệ: " + e.getMessage(), e);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeError(response, "Đã có lỗi không mong muốn xảy ra ở máy chủ.", e);
        }
    }

    // ---------- Helper Methods ----------
    private LocalDate parseNullableDate(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) return null;
        String val = json.optString(key, "").trim();
        return val.isEmpty() ? null : LocalDate.parse(val);
    }

    private LocalTime parseNullableTime(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) return null;
        String val = json.optString(key, "").trim();
        return val.isEmpty() ? null : LocalTime.parse(val);
    }

    private void writeResponse(HttpServletResponse response, JSONObject json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
            out.flush();
        }
    }
    
    private void writeError(HttpServletResponse response, String message, Exception e) throws IOException {
        // Ghi log lỗi đầy đủ ở server để debug
        e.printStackTrace(); 
        
        JSONObject err = new JSONObject();
        err.put("status", "error");
        err.put("message", message);
        writeResponse(response, err);
    }
}