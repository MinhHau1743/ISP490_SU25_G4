// Import các thư viện cần thiết ở đầu file
package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import vn.edu.fpt.dao.DepartmentDAO;
import vn.edu.fpt.dao.PositionDAO;
import vn.edu.fpt.dao.UserDAO;
import vn.edu.fpt.model.Department;
import vn.edu.fpt.model.Position;
import vn.edu.fpt.model.User;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import vn.edu.fpt.common.EmailService;
import vn.edu.fpt.common.EmailSender;
import vn.edu.fpt.common.GmailSender;
// (Bạn có thể cần thêm các import khác cho List, ArrayList,...)

// Annotation này định nghĩa URL cho Servlet. Đây chính là đường link bạn sẽ dùng.
@WebServlet(name = "ListPlanServlet", urlPatterns = {"/list-plan"})
public class ListPlanServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ---- PHẦN LOGIC CỦA BẠN SẼ Ở ĐÂY ----
        // Ví dụ:
        // 1. Kết nối cơ sở dữ liệu.
        // 2. Lấy danh sách các gói hỗ trợ (List<Plan>).
        // 3. Đặt danh sách đó vào request để gửi sang JSP.
        // request.setAttribute("planList", listOfPlansFromDatabase);
        
        // ---- Chuyển tiếp yêu cầu đến file JSP để hiển thị ----
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/admin/listPlan.jsp");
        dispatcher.forward(request, response);
    }
}