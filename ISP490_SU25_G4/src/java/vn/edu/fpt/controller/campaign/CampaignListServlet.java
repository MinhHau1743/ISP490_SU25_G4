package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder; // Cần import này để encode URL
import java.util.List;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.model.Campaign;

/**
 * Servlet này xử lý việc hiển thị danh sách các chiến dịch (Campaign)
 * với chức năng phân trang và tìm kiếm theo tên.
 * Nó lấy dữ liệu từ CampaignDAO và chuyển tiếp đến trang JSP để hiển thị.
 *
 * @author minhh (đã được cải tiến)
 */
@WebServlet(name = "CampaignListServlet", urlPatterns = {"/list-campaign"})
public class CampaignListServlet extends HttpServlet {

    /**
     * Số lượng bản ghi (chiến dịch) hiển thị trên mỗi trang.
     */
    private static final int PAGE_SIZE = 3;
    private final CampaignDAO campaignDAO = new CampaignDAO();

    /**
     * Xử lý các yêu cầu HTTP GET và POST.
     *
     * @param request  đối tượng request của servlet
     * @param response đối tượng response của servlet
     * @throws ServletException nếu có lỗi đặc trưng của servlet
     * @throws IOException      nếu có lỗi I/O
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo hỗ trợ tiếng Việt trong tham số URL

        // 1. Lấy số trang hiện tại từ request parameter, mặc định là 1 nếu không có hoặc không hợp lệ.
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) {
                    currentPage = 1; // Đảm bảo số trang không bao giờ nhỏ hơn 1
                }
            } catch (NumberFormatException e) {
                // Giữ currentPage là 1 nếu tham số không phải là số
                currentPage = 1;
                System.err.println("Tham số 'page' không hợp lệ: " + pageStr);
            }
        }

        // 2. Lấy từ khóa tìm kiếm từ request parameter
        // THAY ĐỔI MỚI: Lấy tham số tìm kiếm
        String searchTerm = request.getParameter("search");
        // Đảm bảo searchTerm không phải là null để tránh NullPointerException trong DAO
        if (searchTerm == null) {
            searchTerm = "";
        }
        searchTerm = searchTerm.trim(); // Loại bỏ khoảng trắng thừa

        // 3. Lấy dữ liệu từ DAO
        // THAY ĐỔI MỚI: Truyền searchTerm vào phương thức DAO
        List<Campaign> campaigns = campaignDAO.getCampaigns(currentPage, PAGE_SIZE, searchTerm);

        // THAY ĐỔI MỚI: Lấy tổng số chiến dịch dựa trên từ khóa tìm kiếm
        int totalRecords = campaignDAO.countCampaigns(searchTerm);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        // 4. Đặt các thuộc tính vào request để chuyển sang view (JSP)
        request.setAttribute("campaigns", campaigns);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        // THAY ĐỔI MỚI: Đặt lại searchTerm vào request để giữ giá trị trong ô tìm kiếm
        request.setAttribute("searchTerm", searchTerm);
        
        // **QUAN TRỌNG**: Thêm baseUrl để component phân trang hoạt động đúng
        // Cần đảm bảo baseUrl bao gồm cả searchTerm khi có
        String baseUrl = request.getContextPath() + "/list-campaign";
        // THAY ĐỔI MỚI: Thêm searchTerm vào baseUrl nếu có, cần encode URL
        if (!searchTerm.isEmpty()) {
             // Sử dụng & thay vì ? nếu đã có tham số page, nhưng trong trường hợp này page luôn là param đầu tiên nếu có search.
             // Để đơn giản, ta sẽ xây dựng lại logic URL trong pagination.jsp
             // Chỉ cần truyền searchTerm thô và baseURL thô là đủ.
             // baseUrl += "?search=" + URLEncoder.encode(searchTerm, "UTF-8"); // Không cần làm phức tạp ở đây nữa
        }
        request.setAttribute("baseUrl", baseUrl);

        // 5. Chuyển tiếp yêu cầu đến trang JSP để hiển thị giao diện
        // Lưu ý: Đã đổi đường dẫn JSP của bạn thành /jsp/admin/listCampaign.jsp như đã thấy trong code Servlet trước
        request.getRequestDispatcher("/jsp/customerSupport/listCampaign.jsp").forward(request, response);
    }

    /**
     * Xử lý phương thức HTTP GET.
     *
     * @param request  đối tượng request của servlet
     * @param response đối tượng response của servlet
     * @throws ServletException nếu có lỗi đặc trưng của servlet
     * @throws IOException      nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Xử lý phương thức HTTP POST.
     *
     * @param request  đối tượng request của servlet
     * @param response đối tượng response của servlet
     * @throws ServletException nếu có lỗi đặc trưng của servlet
     * @throws IOException      nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Trả về mô tả ngắn của servlet.
     *
     * @return một chuỗi String chứa mô tả của servlet
     */
    @Override
    public String getServletInfo() {
        return "Servlet responsible for listing, paginating, and searching campaigns.";
    }
}