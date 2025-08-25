package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.dao.ScheduleDAO;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.dao.UserDAO;

import vn.edu.fpt.model.MaintenanceAssignments;
import vn.edu.fpt.model.MaintenanceSchedule;
import vn.edu.fpt.model.Province;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.dao.EnterpriseDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.District;
import vn.edu.fpt.model.Status;
import vn.edu.fpt.model.Ward;

@WebServlet(name = "scheduleController", urlPatterns = {"/schedule"})
public class ScheduleController extends HttpServlet {

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
    private final DateTimeFormatter dayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));
    private static final int DEFAULT_DURATION_MINUTES = 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("updateSchedule".equals(action)) {
            editFormSchedule(request, response);
            return;
        } else if ("updateScheduleTime".equals(action)) {
            updateScheduleTime(request, response);
            return;
        } else if ("viewScheduleDetail".equals(action)) {
            viewScheduleDetail(request, response);
            return;
        } else if ("getDistricts".equals(action)) {
            getDistricts(request, response);
            return;
        } else if ("getWards".equals(action)) {
            getWards(request, response);
            return;
        } else if ("markAsComplete".equals(action)) {
            handleMarkAsComplete(request, response);
            return;
        }
        // Mặc định (hoặc action=listSchedule): hiển thị lịch
        viewSchedule(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if ("updateSchedule".equals(action)) {
            handleEditSubmit(request, response);
            return;
        }
        // Nếu không khớp action, về GET
        doGet(request, response);
    }

    /* =========================
     * 1) VIEW/LIST SCHEDULE
     * ========================= */
    private void viewSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ScheduleDAO dao = new ScheduleDAO();
        UserDAO userDAO = new UserDAO();

        // === BƯỚC 1: LẤY CÁC THAM SỐ TỪ REQUEST ===
        // 1.1. Lấy tham số bộ lọc (ví dụ: "pending", "completed").
        String scheduleFilter = request.getParameter("scheduleFilter");
        // Nếu filter rỗng hoặc không có, coi như là lấy tất cả.
        if (scheduleFilter == null || scheduleFilter.trim().isEmpty() || "".equals(scheduleFilter.trim())) {
            scheduleFilter = null;
        }

        // 1.2. Lấy tham số để chỉ hiển thị lịch của người dùng đang đăng nhập.
        String userOnly = request.getParameter("userOnly");
        Integer userId = null;
        if ("true".equals(userOnly)) {
            HttpSession session = request.getSession(false); // Lấy session hiện tại, không tạo mới.
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                userId = user.getId();
            }
        }

        // 1.3. Lấy các tham số điều khiển ngày tháng và chế độ xem.
        String controllerDay = request.getParameter("controllerDay"); // Nút "prev" hoặc "next".
        String currentDayStr = request.getParameter("currentDay");   // Ngày hiện tại đang xem, định dạng yyyy-MM-dd.
        String viewMode = request.getParameter("viewMode");          // Chế độ xem: "day-view", "week-view",...

        // === BƯỚC 2: TÍNH TOÁN NGÀY CẦN HIỂN THỊ (TARGET DATE) ===
        // Mặc định là ngày hôm nay.
        LocalDate today = LocalDate.now();
        // Nếu có `currentDayStr` từ request, phân tích chuỗi đó để lấy ngày.
        if (currentDayStr != null && !currentDayStr.isEmpty()) {
            try {
                today = LocalDate.parse(currentDayStr, inputFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Lỗi phân tích chuỗi ngày: " + currentDayStr);
            }
        }

        // Dựa vào nút "prev" hoặc "next" và `viewMode`, điều chỉnh ngày `today` tới hoặc lùi.
        if ("prev".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.minusMonths(1); // Lùi 1 tháng.
            } else if ("week-view".equals(viewMode)) {
                today = today.minusDays(7);   // Lùi 1 tuần.
            } else {
                today = today.minusDays(1);   // Lùi 1 ngày.
            }
        } else if ("next".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.plusMonths(1);  // Tới 1 tháng.
            } else if ("week-view".equals(viewMode)) {
                today = today.plusDays(7);    // Tới 1 tuần.
            } else {
                today = today.plusDays(1);    // Tới 1 ngày.
            }
        }

        // === BƯỚC 3: CHUẨN BỊ CÁC CHUỖI HIỂN THỊ VÀ ĐỊNH DẠNG NGÀY THÁNG ===
        LocalDate now = LocalDate.now();
        boolean isToday = today.equals(now); // Kiểm tra `today` có phải là ngày hôm nay không.

        // Cấu hình tuần bắt đầu từ thứ Hai cho Việt Nam.
        WeekFields weekFields = WeekFields.of(new Locale("vi", "VN"));
        // Kiểm tra `today` có nằm trong tuần hiện tại không.
        boolean isThisWeek = (now.get(weekFields.weekOfWeekBasedYear()) == today.get(weekFields.weekOfWeekBasedYear()))
                && (now.get(weekFields.weekBasedYear()) == today.get(weekFields.weekBasedYear()));
        // Kiểm tra `today` có nằm trong tháng hiện tại không.
        boolean isThisMonth = today.getMonthValue() == now.getMonthValue() && today.getYear() == now.getYear();

        // Tạo chuỗi hiển thị chính (ví dụ: "Hôm nay", "Tuần này", "Tháng 8 2025", "25/08/2025 - 31/08/2025").
        String displayDate;
        DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("vi", "VN"));

        if ("week-view".equals(viewMode)) {
            if (isThisWeek) {
                displayDate = "Tuần này";
            } else {
                LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Thứ Hai của tuần.
                LocalDate endOfWeek = today.with(weekFields.dayOfWeek(), 7);   // Chủ Nhật của tuần.
                displayDate = startOfWeek.format(rangeFormatter) + " - " + endOfWeek.format(rangeFormatter);
            }
        } else if ("month-view".equals(viewMode)) {
            displayDate = isThisMonth ? "Tháng này" : today.format(monthFormatter);
        } else { // day-view
            displayDate = isToday ? "Hôm nay" : today.format(displayFormatter);
        }

        // Các chuỗi định dạng ngày khác cần cho JSP.
        String isoDayDate = today.toString(); // Định dạng yyyy-MM-dd, dùng để truyền lại cho request sau.
        String dayHeader = today.format(DateTimeFormatter.ofPattern("EEEE", new Locale("vi", "VN"))); // Ví dụ: "Thứ Hai"
        String dayDate = today.format(dayDateFormatter); // Ví dụ: "25/08"

        // === BƯỚC 4: TẠO DỮ LIỆU CẤU TRÚC CHO TỪNG CHẾ ĐỘ XEM ===
        // 4.1. DAY-VIEW: Tạo các mốc thời gian trong ngày (mỗi 30 phút).
        List<String> dayTimeLabels = new ArrayList<>(); // Nhãn hiển thị (e.g., "12:00 am").
        List<String> dayStartTimes = new ArrayList<>(); // Giá trị 24h (e.g., "00:00").
        dayTimeLabels.add("Cả ngày");
        dayStartTimes.add(""); // Slot cho các sự kiện cả ngày.
        // ... (Vòng lặp để tạo các mốc thời gian từ 00:00 đến 23:30)
        for (int h = 0; h < 24; h++) {
            // ...
        }

        // 4.2. WEEK-VIEW: Tạo danh sách 7 ngày trong tuần.
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Tìm ngày thứ Hai của tuần.
        List<String> dayHeaders = new ArrayList<>(); // Tiêu đề cột (e.g., "Th 2 25/08").
        List<LocalDate> weekDates = new ArrayList<>(); // Danh sách các đối tượng LocalDate trong tuần.
        for (int i = 0; i < 7; i++) {
            LocalDate d = startOfWeek.plusDays(i);
            weekDates.add(d);
            dayHeaders.add(d.format(DateTimeFormatter.ofPattern("EEE dd/MM", new Locale("vi", "VN"))));
        }

        // 4.3. MONTH-VIEW: Tạo một lưới lịch 5-6 tuần, bao gồm các ngày của tháng trước và sau.
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDate firstOfMonth = yearMonth.atDay(1); // Ngày đầu tiên của tháng.
        int firstDayValue = firstOfMonth.getDayOfWeek().getValue(); // Giá trị của ngày đầu tiên (Thứ Hai = 1, CN = 7).
        int prevDaysCount = firstDayValue - 1; // Số ngày cần lấy từ tháng trước để lấp đầy tuần đầu.

        List<String> dayNumbers = new ArrayList<>();      // Số ngày (e.g., "28", "1", "2").
        List<Boolean> isCurrentMonths = new ArrayList<>();// Cờ để xác định ngày có thuộc tháng đang xem không.
        List<LocalDate> monthDates = new ArrayList<>();   // Danh sách các đối tượng LocalDate cho mỗi ô trong lưới.

        // Lặp để thêm các ngày của tháng trước vào lưới.
        // ...
        // Lặp để thêm các ngày của tháng hiện tại vào lưới.
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            // ...
        }
        // Lặp để thêm các ngày của tháng sau vào lưới cho đủ 7 cột.

        // 4.4. TIMELINE CHUNG: Tạo danh sách các mốc thời gian 30 phút trong 24h.
        List<String> hours = new ArrayList<>();      // Định dạng 24h (e.g., "13:30").
        List<String> hourLabels = new ArrayList<>(); // Định dạng 12h am/pm (e.g., "01:30 pm").
        for (int h = 0; h < 24; h++) {
            // ...
        }

        // === BƯỚC 5: LẤY VÀ XỬ LÝ DỮ LIỆU LỊCH TRÌNH TỪ DATABASE ===
        // 5.1. Lấy danh sách lịch trình và danh sách tất cả các phân công.
        List<MaintenanceSchedule> schedules = dao.getFilteredMaintenanceSchedules(userId, scheduleFilter);
        List<MaintenanceAssignments> assignments = dao.getAllMaintenanceAssignments();

        // Nếu là chế độ xem danh sách, chỉ hiển thị các lịch trình từ hôm nay trở về sau.
        if ("list-view".equals(viewMode)) {
            schedules = schedules.stream()
                    .filter(sch -> sch.getScheduledDate() != null && !sch.getScheduledDate().isBefore(now))
                    .collect(Collectors.toList());
        }

        // 5.2. Nhóm các phân công theo ID của lịch trình để dễ dàng tra cứu.
        Map<Integer, List<MaintenanceAssignments>> assignmentMap = assignments.stream()
                .collect(Collectors.groupingBy(MaintenanceAssignments::getMaintenanceScheduleId));

        // 5.3. Gắn thông tin phân công (assignedUserIds) vào mỗi đối tượng lịch trình.
        for (MaintenanceSchedule schedule : schedules) {
            List<MaintenanceAssignments> assigns = assignmentMap.getOrDefault(schedule.getId(), Collections.emptyList());
            List<Integer> assignedUserIds = assigns.stream()
                    .map(MaintenanceAssignments::getUserId)
                    .collect(Collectors.toList());
            schedule.setAssignedUserIds(assignedUserIds);
            schedule.setAssignments(assigns);
        }

        // 5.4. Nhóm các lịch trình theo ngày để JSP dễ dàng hiển thị.
        Map<LocalDate, List<MaintenanceSchedule>> groupedSchedules = schedules.stream()
                .collect(Collectors.groupingBy(MaintenanceSchedule::getScheduledDate));

        // Cập nhật trạng thái của các lịch trình (ví dụ: "Quá hạn").
        updateScheduleStatuses(schedules);

        // === BƯỚC 6: GỬI TẤT CẢ DỮ LIỆU SANG JSP ===
        // Đặt tất cả các danh sách và biến đã chuẩn bị vào request attribute.
        request.setAttribute("groupedSchedules", groupedSchedules);
        request.setAttribute("schedules", schedules);
        request.setAttribute("hourLabels", hourLabels);
        // ... (và nhiều attribute khác cho từng chế độ xem)
        request.setAttribute("dayHeaders", dayHeaders);
        request.setAttribute("weekDates", weekDates);
        request.setAttribute("dayNumbers", dayNumbers);
        request.setAttribute("isCurrentMonths", isCurrentMonths);
        request.setAttribute("monthDates", monthDates);
        request.setAttribute("viewMode", viewMode != null ? viewMode : "day-view"); // Chế độ xem mặc định là "day-view".

        // Chuyển tiếp request đến trang JSP để render giao diện.
        request.getRequestDispatcher("/jsp/customerSupport/listSchedule.jsp").forward(request, response);
    }

    private void forwardToForm(HttpServletRequest request, HttpServletResponse response, ScheduleDAO scheduleDAO)
            throws ServletException, IOException {

        // giữ lại dữ liệu người dùng nhập
        request.setAttribute("title", request.getParameter("title"));
        request.setAttribute("color", request.getParameter("color"));
        request.setAttribute("scheduled_date", request.getParameter("scheduled_date"));
        request.setAttribute("end_date", request.getParameter("end_date"));
        request.setAttribute("start_time", request.getParameter("start_time"));
        request.setAttribute("end_time", request.getParameter("end_time"));
        request.setAttribute("status", request.getParameter("status"));
        request.setAttribute("notes", request.getParameter("notes"));
        request.setAttribute("technical_request_id", request.getParameter("technical_request_id"));
        request.setAttribute("streetAddress", request.getParameter("streetAddress"));
        request.setAttribute("provinceId", request.getParameter("province"));
        request.setAttribute("districtId", request.getParameter("district"));
        request.setAttribute("wardId", request.getParameter("ward"));

        // reload dropdowns
        try {
            AddressDAO addressDAO = new AddressDAO();
            request.setAttribute("technicalRequests", scheduleDAO.getAllTechnicalRequestsAndCampaignsIdAndTitle());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            String provinceParam = request.getParameter("province");
            String districtParam = request.getParameter("district");

            if (provinceParam != null && !provinceParam.isEmpty()) {
                request.setAttribute("districts",
                        addressDAO.getDistrictsByProvinceId(Integer.parseInt(provinceParam)));
            }
            if (districtParam != null && !districtParam.isEmpty()) {
                request.setAttribute("wards",
                        addressDAO.getWardsByDistrictId(Integer.parseInt(districtParam)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.getRequestDispatcher("jsp/customerSupport/createSchedule.jsp").forward(request, response);
    }

    private void editFormSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Use DAOs
        ScheduleDAO scheduleDAO = new ScheduleDAO();
        AddressDAO addressDAO = new AddressDAO();
        UserDAO userDAO = new UserDAO();

        try {
            // 1. Lấy và validate ID từ URL
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing schedule ID");
                return;
            }
            int id = Integer.parseInt(idStr);

            // 2. Lấy dữ liệu lịch trình chính
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Schedule not found");
                return;
            }
            updateSingleScheduleStatus(schedule);
            // 3. Lấy dữ liệu cần thiết cho các dropdown
            List<User> assignments = userDAO.getAllTechnicalStaffIdAndFullName();
            List<MaintenanceSchedule> ms = scheduleDAO.getAllTechnicalRequestsAndCampaignsIdAndTitle();
            List<Province> provinces = addressDAO.getAllProvinces();
            List<Integer> assignedUserIds = scheduleDAO.getAssignedUserIdsByScheduleId(id);
            List<Status> statusList = scheduleDAO.getAllStatuses();
// 2. Chuyển đổi List thành Map để tra cứu nhanh hơn
            Map<Integer, Boolean> assignedUserMap = new HashMap<>();
            for (Integer userId : assignedUserIds) {
                assignedUserMap.put(userId, true);
            }
// 3. Gửi Map này sang JSP thay vì List
            request.setAttribute("statusList", statusList);
            request.setAttribute("assignedUserMap", assignedUserMap);
            request.setAttribute("assignments", assignments);
            request.setAttribute("schedule", schedule);
            request.setAttribute("technicalRequests", ms);
            request.setAttribute("provinces", provinces);

            // 5. Tải sẵn danh sách Quận/Huyện và Phường/Xã nếu lịch trình đã có địa chỉ
            if (schedule.getAddress() != null) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(schedule.getAddress().getProvinceId()));
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(schedule.getAddress().getDistrictId()));
            }

            // 6. Chuyển hướng đến trang JSP
            request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid schedule ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }
    // Add this new method to your ScheduleController.java

    private void viewScheduleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Instantiate necessary DAOs
        ScheduleDAO scheduleDAO = new ScheduleDAO();
        UserDAO userDAO = new UserDAO(); // To get user names for display
        AddressDAO addressDAO = new AddressDAO();
        try {
            // 1. Get and validate the ID from the URL
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing schedule ID");
                return;
            }
            int id = Integer.parseInt(idStr);

            // 2. Fetch the main schedule data
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Schedule not found");
                return;
            }
            updateSingleScheduleStatus(schedule);
            // 3. Fetch related data for display purposes
            // Get the list of assigned user IDs
            List<Integer> assignedUserIds = scheduleDAO.getAssignedUserIdsByScheduleId(id);
            List<Province> provinces = addressDAO.getAllProvinces();
            // Get the full details of the assigned users (e.g., their names)
            List<User> assignedUsers = userDAO.getAllTechnicalStaffIdAndFullName(); // You may need to create this helper method in UserDAO
            // 4. Set attributes for the JSP
            request.setAttribute("assignedUserIds", assignedUserIds);
            request.setAttribute("schedule", schedule);
            request.setAttribute("employeeList", assignedUsers);
            request.setAttribute("provinces", provinces);
            // Note: You don't need to load all provinces/districts/wards for a view page,
            // as the full address is already inside the schedule.getAddress() object.
            // 5. Forward to the view JSP page
            request.getRequestDispatcher("jsp/customerSupport/viewSchedule.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid schedule ID format");
        } catch (Exception e) {
            e.printStackTrace(); // Log the full error
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private void handleEditSubmit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ScheduleDAO scheduleDAO = new ScheduleDAO();
        AddressDAO addressDAO = new AddressDAO();
        TechnicalRequestDAO technicalDAO = new TechnicalRequestDAO();
        TechnicalRequest tr = new TechnicalRequest();
        CampaignDAO campaignDAO = new CampaignDAO();
        try {
            // === 1. LẤY DỮ LIỆU TỪ FORM ===
            int id = Integer.parseInt(request.getParameter("id"));
            String technicalRequestIdStr = request.getParameter("technicalRequestId");
            String campaignIdIdStr = request.getParameter("campaignId");
            String title = request.getParameter("title");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduledDate");
            String endDateStr = request.getParameter("endDate");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String statusIdStr = request.getParameter("statusId");
            String notes = request.getParameter("notes");

            // Dữ liệu địa chỉ mới
            String streetAddress = request.getParameter("streetAddress");
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            // === 2. VALIDATE DỮ LIỆU ===
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập tiêu đề.");
            }
            if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ngày bắt đầu.");
            }
            if (provinceIdStr == null || provinceIdStr.isEmpty()
                    || districtIdStr == null || districtIdStr.isEmpty()
                    || wardIdStr == null || wardIdStr.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, và Phường/Xã.");
            }

            // === 3. PARSE VÀ CẬP NHẬT ĐỐI TƯỢNG ===
            // Lấy đối tượng schedule hiện tại từ DB để cập nhật
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                throw new Exception("Lịch bảo trì không còn tồn tại.");
            }

            // Parse và validate ngày/giờ
            LocalDate scheduledDate = LocalDate.parse(scheduledDateStr);
            LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr) : null;
            LocalTime startTime = (startTimeStr != null && !startTimeStr.isEmpty()) ? LocalTime.parse(startTimeStr) : null;
            LocalTime endTime = (endTimeStr != null && !endTimeStr.isEmpty()) ? LocalTime.parse(endTimeStr) : null;

            if (endDate != null && endDate.isBefore(scheduledDate)) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            }
            if (startTime != null && endTime != null && (endDate == null || endDate.isEqual(scheduledDate)) && !endTime.isAfter(startTime)) {
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu nếu trong cùng một ngày.");
            }

            // Tạo hoặc tìm địa chỉ và lấy ID
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);

            // Cập nhật các thuộc tính của đối tượng schedule
            // (Các phần lấy parameter và tạo đối tượng schedule ở trên...)
            if (technicalRequestIdStr != null && !technicalRequestIdStr.isEmpty()) {
                schedule.setTechnicalRequestId(Integer.valueOf(technicalRequestIdStr));
            } else {
                schedule.setTechnicalRequestId(null);
            }
            if (campaignIdIdStr != null && !campaignIdIdStr.isEmpty()) {
                schedule.setCampaignId(Integer.valueOf(campaignIdIdStr));
            } else {
                schedule.setCampaignId(null);
            }
            Integer statusId = null;
            if (statusIdStr != null && !statusIdStr.isBlank()) {
                statusId = Integer.valueOf(statusIdStr);
            }
            schedule.setColor(color);
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setAddressId(addressId);
            schedule.setStatusId(statusId);

// === 4. LƯU THAY ĐỔI VÀO DATABASE ===
            boolean scheduleUpdated = scheduleDAO.updateMaintenanceSchedule(schedule);
            // Lấy ID yêu cầu kỹ thuật GẮN VỚI schedule này (không phải từ form)
            if (schedule.getTechnicalRequestId() != null) {
                tr.setId(schedule.getTechnicalRequestId());  // ID của yêu cầu đã gắn
                tr.setTitle(title);
                tr.setDescription(notes);

                boolean ok = technicalDAO.updateTechnicalRequestTitleAndDesc(tr);
                if (!ok) {
                    request.setAttribute("error", "Cập nhật yêu cầu kỹ thuật thất bại!");
                    forwardToEditForm(request, response);
                    return;
                }
            }
            if (schedule.getCampaignId() != null) {
                Campaign campaign = new Campaign();
                campaign.setCampaignId(schedule.getCampaignId());
                campaign.setName(title);       // cùng form title
                campaign.setDescription(notes); // cùng form notes
                boolean ok = campaignDAO.updateCampaignTitleAndDesc(campaign);
                if (!ok) {
                    request.setAttribute("error", "Cập nhật Campaign thất bại!");
                    forwardToEditForm(request, response);
                    return;
                }
            }
// --- PHẦN TÍCH HỢP ---
// Chỉ tiếp tục cập nhật phân công nếu lịch trình đã được cập nhật thành công
            if (scheduleUpdated) {
                // 4.1. Lấy danh sách ID nhân viên mới từ form
                String[] assignedUserIdsStr = request.getParameterValues("assignedUserIds");
                List<Integer> newUserIds = new ArrayList<>();
                if (assignedUserIdsStr != null) {
                    for (String userIdStr : assignedUserIdsStr) {
                        try {
                            newUserIds.add(Integer.valueOf(userIdStr));
                        } catch (NumberFormatException e) {
                            // Bỏ qua các giá trị không hợp lệ hoặc log lỗi nếu cần
                            System.err.println("Invalid user ID format: " + userIdStr);
                        }
                    }
                }

                // 4.2. Gọi DAO để cập nhật bảng MaintenanceAssignments
                boolean assignmentsUpdated = scheduleDAO.updateAssignmentsForSchedule(schedule.getId(), newUserIds);

                // 4.3. Kiểm tra kết quả và chuyển hướng
                if (assignmentsUpdated) {
                    // CHỈ KHI CẢ HAI ĐỀU THÀNH CÔNG
                    response.sendRedirect(request.getContextPath() + "/schedule");
                } else {
                    // Trường hợp lịch trình cập nhật OK, nhưng phân công thất bại
                    // Cần có cơ chế xử lý lỗi tốt hơn, ví dụ: rollback hoặc báo lỗi cụ thể
                    throw new Exception("Cập nhật lịch bảo trì thành công, nhưng cập nhật phân công nhân viên thất bại.");
                }

            } else {
                // Trường hợp cập nhật lịch trình chính thất bại ngay từ đầu
                throw new Exception("Cập nhật lịch bảo trì thất bại do lỗi cơ sở dữ liệu.");
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {
            // Bắt lỗi validation hoặc lỗi parse
            request.setAttribute("error", e.getMessage());
            forwardToEditForm(request, response);
        } catch (Exception e) {
            // Bắt các lỗi chung khác
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
            forwardToEditForm(request, response);
        }
    }

    private void forwardToEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ScheduleDAO scheduleDAO = new ScheduleDAO();
        // Cố gắng lấy lại đối tượng schedule để điền form
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            request.setAttribute("schedule", schedule); // Luôn gửi lại đối tượng gốc
        } catch (Exception e) {
            // Bỏ qua nếu không lấy được
        }

        // Tải lại danh sách cho các dropdown
        try {
            AddressDAO addressDAO = new AddressDAO();
            request.setAttribute("technicalRequests", scheduleDAO.getAllTechnicalRequestsAndCampaignsIdAndTitle());
            request.setAttribute("provinces", addressDAO.getAllProvinces());

            // Tải lại quận/huyện và phường/xã nếu người dùng đã chọn
            if (request.getParameter("province") != null && !request.getParameter("province").isEmpty()) {
                request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(Integer.parseInt(request.getParameter("province"))));
            }
            if (request.getParameter("district") != null && !request.getParameter("district").isEmpty()) {
                request.setAttribute("wards", addressDAO.getWardsByDistrictId(Integer.parseInt(request.getParameter("district"))));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        request.getRequestDispatcher("jsp/customerSupport/editSchedule.jsp").forward(request, response);
    }

    private void updateScheduleTime(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // === BƯỚC 1: CẤU HÌNH REQUEST VÀ RESPONSE ===
        // Đảm bảo request được đọc với encoding UTF-8 để xử lý đúng tiếng Việt.
        request.setCharacterEncoding("UTF-8");
        // Báo cho client biết rằng response trả về là dữ liệu JSON với encoding UTF-8.
        response.setContentType("application/json; charset=UTF-8");

        JSONObject jsonResponse = new JSONObject(); // Đối tượng JSON để xây dựng câu trả lời.

        try {
            // === BƯỚC 2: ĐỌC VÀ PHÂN TÍCH DỮ LIỆU JSON TỪ REQUEST BODY ===
            StringBuilder sb = new StringBuilder();
            // Sử dụng try-with-resources để đảm bảo `reader` được tự động đóng.
            try (BufferedReader reader = request.getReader()) {
                String line;
                // Đọc toàn bộ nội dung của request body từng dòng một.
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            // Nếu request body trống, trả về lỗi Bad Request.
            if (sb.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Request body trống.");
                writeResponse(response, jsonResponse); // Gửi response và kết thúc.
                return;
            }

            // Chuyển đổi chuỗi đã đọc thành một đối tượng JSON.
            JSONObject jsonRequest = new JSONObject(sb.toString());

            // === BƯỚC 3: LẤY VÀ XÁC THỰC DỮ LIỆU ĐẦU VÀO ===
            // Lấy các trường bắt buộc. Nếu thiếu, getInt/getString sẽ ném ra JSONException.
            int id = jsonRequest.getInt("id");
            LocalDate scheduledDate = LocalDate.parse(jsonRequest.getString("scheduledDate"));

            // Lấy các trường có thể có hoặc không (nullable).
            // `parseNullable...` là các hàm helper để tránh lỗi nếu key không tồn tại trong JSON.
            LocalDate endDate = parseNullableDate(jsonRequest, "endDate");
            LocalTime startTime = parseNullableTime(jsonRequest, "startTime");
            LocalTime endTime = parseNullableTime(jsonRequest, "endTime");

            // === BƯỚC 4: ÁP DỤNG LOGIC NGHIỆP VỤ PHÍA SERVER ===
            // Logic này đảm bảo dữ liệu luôn nhất quán, ngay cả khi frontend gửi thiếu thông tin.
            // Kịch bản 1: Sự kiện được kéo vào một khung giờ cụ thể (có startTime).
            // Nếu frontend không gửi endTime (ví dụ: sự kiện chưa có thời lượng),
            // server sẽ tự động tính toán endTime bằng cách cộng thêm một khoảng thời gian mặc định.
            if (startTime != null && endTime == null) {
                endTime = startTime.plusMinutes(DEFAULT_DURATION_MINUTES);
            }

            // Kịch bản 2: Sự kiện được kéo vào khu vực "Cả ngày" (all-day).
            // Frontend sẽ gửi startTime là null. Logic ở trên sẽ không được kích hoạt,
            // giữ cho startTime và endTime là null, đây là hành vi đúng đắn cho sự kiện cả ngày.
            // Kịch bản 3: Validate logic về ngày.
            // Nếu endDate tồn tại nhưng lại trước scheduledDate, thì coi như endDate không hợp lệ và gán về null.
            if (endDate != null && endDate.isBefore(scheduledDate)) {
                endDate = null;
            }

            // === BƯỚC 5: CẬP NHẬT VÀO CƠ SỞ DỮ LIỆU ===
            ScheduleDAO dao = new ScheduleDAO();
            // Gọi DAO để cập nhật các trường đã được xử lý vào database.
            boolean success = dao.updateScheduleByDragDrop(id, scheduledDate, endDate, startTime, endTime);

            // === BƯỚC 6: XÂY DỰNG VÀ GỬI RESPONSE ===
            if (success) {
                // Nếu cập nhật thành công, trả về HTTP 200 OK.
                response.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Lịch trình đã được cập nhật thành công.");

                // Quan trọng: Trả về một "payload" chứa dữ liệu đã được server chuẩn hóa.
                // Frontend nên sử dụng payload này để cập nhật lại giao diện,
                // đảm bảo những gì người dùng thấy khớp với dữ liệu đã được server xử lý (ví dụ: endTime tự động).
                JSONObject payload = new JSONObject();
                payload.put("id", id);
                payload.put("scheduledDate", scheduledDate.toString());
                // Xử lý các giá trị có thể null.
                payload.put("endDate", endDate != null ? endDate.toString() : JSONObject.NULL);
                payload.put("startTime", startTime != null ? startTime.toString() : JSONObject.NULL);
                payload.put("endTime", endTime != null ? endTime.toString() : JSONObject.NULL);
                jsonResponse.put("payload", payload);

            } else {
                // Nếu cập nhật thất bại (ví dụ: không tìm thấy ID), trả về lỗi server.
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Cập nhật thất bại. Lịch trình không tồn tại hoặc có lỗi cơ sở dữ liệu.");
            }

            writeResponse(response, jsonResponse); // Gửi JSON response về cho client.

        } catch (JSONException | DateTimeParseException e) {
            // Bắt lỗi khi dữ liệu JSON từ client bị sai định dạng hoặc ngày tháng không hợp lệ.
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400
            writeError(response, "Dữ liệu gửi lên không hợp lệ: " + e.getMessage(), e);
        } catch (Exception e) {
            // Bắt tất cả các lỗi không lường trước khác (lỗi logic, lỗi kết nối DB,...).
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500
            writeError(response, "Đã có lỗi không mong muốn xảy ra ở máy chủ.", e);
        }
    }
    // ---------- Helper Methods ----------

    /**
     * Một hàm tiện ích (helper) để phân tích một chuỗi ngày tháng từ đối tượng
     * JSON. Hàm này được thiết kế để xử lý an toàn các trường hợp key không tồn
     * tại, giá trị là null, hoặc giá trị là chuỗi rỗng.
     *
     * @param json Đối tượng JSON chứa dữ liệu.
     * @param key Tên của key (trường) cần lấy giá trị ngày tháng.
     * @return Một đối tượng LocalDate nếu phân tích thành công, ngược lại trả
     * về null.
     */
    private LocalDate parseNullableDate(JSONObject json, String key) {
        // 1. Kiểm tra xem key có tồn tại trong JSON không và giá trị của nó có phải là null không.
        if (!json.has(key) || json.isNull(key)) {
            // Nếu không có key hoặc giá trị là null, trả về null ngay lập tức.
            return null;
        }
        // 2. Lấy giá trị dưới dạng chuỗi một cách an toàn.
        // `optString` sẽ trả về chuỗi rỗng "" thay vì ném lỗi nếu key không tồn tại (dù đã check ở trên).
        String val = json.optString(key, "").trim();

        // 3. Nếu chuỗi giá trị rỗng sau khi cắt khoảng trắng, trả về null.
        // Ngược lại, phân tích chuỗi thành đối tượng LocalDate.
        return val.isEmpty() ? null : LocalDate.parse(val);
    }

    /**
     * Tương tự như parseNullableDate, nhưng dùng để phân tích thời gian
     * (giờ:phút:giây). Xử lý an toàn các trường hợp key không tồn tại, giá trị
     * null hoặc chuỗi rỗng.
     *
     * @param json Đối tượng JSON chứa dữ liệu.
     * @param key Tên của key (trường) cần lấy giá trị thời gian.
     * @return Một đối tượng LocalTime nếu phân tích thành công, ngược lại trả
     * về null.
     */
    private LocalTime parseNullableTime(JSONObject json, String key) {
        // Logic hoàn toàn tương tự hàm parseNullableDate.
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
        String val = json.optString(key, "").trim();
        return val.isEmpty() ? null : LocalTime.parse(val);
    }

    /**
     * Một hàm tiện ích để ghi đối tượng JSONObject vào body của
     * HttpServletResponse. Việc tách hàm này ra giúp tránh lặp lại code
     * `try-with-resources` ở nhiều nơi.
     *
     * @param response Đối tượng HttpServletResponse để ghi dữ liệu vào.
     * @param json Đối tượng JSONObject cần gửi về cho client.
     * @throws IOException
     */
    private void writeResponse(HttpServletResponse response, JSONObject json) throws IOException {
        // Sử dụng `try-with-resources` để đảm bảo `PrintWriter` được tự động đóng
        // sau khi khối lệnh kết thúc, tránh rò rỉ tài nguyên.
        try (PrintWriter out = response.getWriter()) {
            // Chuyển đối tượng JSON thành chuỗi và ghi vào response.
            out.print(json.toString());
            // Đẩy (flush) buffer để đảm bảo dữ liệu được gửi đi ngay lập tức.
            out.flush();
        }
    }

    /**
     * Một hàm tiện ích để xử lý việc ghi log lỗi và gửi một response lỗi chuẩn
     * hóa về cho client.
     *
     * @param response Đối tượng HttpServletResponse.
     * @param message Một thông báo lỗi thân thiện với người dùng để hiển thị
     * trên client.
     * @param e Đối tượng Exception gốc, dùng để ghi log chi tiết trên server.
     * @throws IOException
     */
    private void writeError(HttpServletResponse response, String message, Exception e) throws IOException {
        // Quan trọng: In ra toàn bộ stack trace của lỗi trên console của server.
        // Điều này cực kỳ cần thiết cho việc gỡ lỗi (debug).
        e.printStackTrace();

        // Tạo một đối tượng JSON lỗi với cấu trúc nhất quán.
        JSONObject err = new JSONObject();
        err.put("status", "error");
        err.put("message", message); // Thông báo lỗi sẽ được gửi về client.

        // Gọi lại hàm `writeResponse` để gửi JSON lỗi này đi.
        writeResponse(response, err);
    }

    private void getDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // === BƯỚC 1: CẤU HÌNH RESPONSE ===
        // Thiết lập kiểu nội dung (Content-Type) là 'application/json' để trình duyệt
        // hiểu rằng đây là dữ liệu JSON.
        response.setContentType("application/json");
        // Thiết lập encoding là UTF-8 để đảm bảo tiếng Việt hiển thị chính xác.
        response.setCharacterEncoding("UTF-8");

        // === BƯỚC 2: LẤY VÀ XỬ LÝ THAM SỐ ===
        // Lấy giá trị của tham số 'provinceId' từ URL request.
        String provinceIdStr = request.getParameter("provinceId");

        // Khởi tạo một danh sách quận/huyện rỗng.
        // Đây sẽ là giá trị trả về mặc định nếu không có provinceId hoặc có lỗi xảy ra.
        List<District> districts = Collections.emptyList();

        // Chỉ thực hiện truy vấn nếu 'provinceId' được cung cấp và không phải là chuỗi rỗng.
        if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
            try {
                // Chuyển đổi provinceId từ chuỗi (String) sang số nguyên (int).
                int provinceId = Integer.parseInt(provinceIdStr);
                // Gọi DAO để thực hiện truy vấn database và lấy danh sách các quận/huyện tương ứng.
                // Lưu ý: `new EnterpriseDAO()` tạo một đối tượng mới mỗi lần. Cân nhắc sử dụng Dependency Injection.
                districts = new EnterpriseDAO().getDistrictsByProvinceId(provinceId);
            } catch (Exception e) {
                // Nếu có bất kỳ lỗi nào xảy ra (ví dụ: NumberFormatException, lỗi database),
                // in lỗi ra console của server để gỡ lỗi (debug).
                e.printStackTrace();
                // Thiết lập mã trạng thái HTTP là 500 (Internal Server Error) để báo lỗi cho client.
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        // === BƯỚC 3: CHUYỂN ĐỔI VÀ GỬI DỮ LIỆU JSON ===
        // Sử dụng try-with-resources để đảm bảo `PrintWriter` được tự động đóng.
        try (PrintWriter out = response.getWriter()) {
            // Sử dụng thư viện Gson để chuyển đổi danh sách đối tượng `districts` (List<District>)
            // thành một chuỗi JSON.
            String jsonResponse = new Gson().toJson(districts);
            // Ghi chuỗi JSON vào response.
            out.print(jsonResponse);
            // Đẩy (flush) buffer để đảm bảo dữ liệu được gửi đi ngay lập tức.
            out.flush();
        }
    }

    private void getWards(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // === BƯỚC 1: CẤU HÌNH RESPONSE ===
        // Báo cho trình duyệt biết rằng đây là dữ liệu JSON.
        response.setContentType("application/json");
        // Đảm bảo tiếng Việt (UTF-8) được hiển thị chính xác.
        response.setCharacterEncoding("UTF-8");

        // === BƯỚC 2: LẤY VÀ XỬ LÝ THAM SỐ ===
        // Lấy ID của quận/huyện từ tham số 'districtId' trong URL.
        String districtIdStr = request.getParameter("districtId");

        // Khởi tạo một danh sách phường/xã rỗng.
        // Nếu có lỗi hoặc không có ID, danh sách rỗng này sẽ được trả về.
        List<Ward> wards = Collections.emptyList();

        // Chỉ thực hiện truy vấn nếu 'districtId' được cung cấp.
        if (districtIdStr != null && !districtIdStr.trim().isEmpty()) {
            try {
                // Chuyển đổi ID từ chuỗi sang số nguyên.
                int districtId = Integer.parseInt(districtIdStr);
                // Gọi DAO để lấy danh sách các phường/xã từ database.
                wards = new EnterpriseDAO().getWardsByDistrictId(districtId);
            } catch (Exception e) {
                // Nếu có lỗi xảy ra (ví dụ: ID không phải là số, lỗi kết nối DB),
                // in lỗi ra console server để debug.
                e.printStackTrace();
                // Gửi mã lỗi 500 (Internal Server Error) về cho client.
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        // === BƯỚC 3: CHUYỂN ĐỔI VÀ GỬI DỮ LIỆU JSON ===
        // Sử dụng try-with-resources để đảm bảo `PrintWriter` được đóng tự động.
        try (PrintWriter out = response.getWriter()) {
            // Dùng thư viện Gson để chuyển đổi List<Ward> thành một chuỗi JSON.
            String jsonResponse = new Gson().toJson(wards);
            // Ghi chuỗi JSON vào response để gửi về cho client.
            out.print(jsonResponse);
            // Đảm bảo dữ liệu được gửi đi ngay lập tức.
            out.flush();
        }
    }

    private void updateScheduleStatuses(List<MaintenanceSchedule> schedules) {
        // Lấy thời gian hiện tại MỘT LẦN DUY NHẤT để đảm bảo tính nhất quán
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        // Duyệt qua từng lịch trình để cập nhật
        for (MaintenanceSchedule schedule : schedules) {

            // --- ƯU TIÊN SỐ 1: Nếu đã "Hoàn thành", không thay đổi nữa ---
            if (schedule.getStatusName() != null && schedule.getStatusName().equalsIgnoreCase("Hoàn thành")) {
                // Đặt lại ID cho chắc chắn
                schedule.setStatusId(3);
                continue; // Bỏ qua và chuyển sang lịch trình tiếp theo
            }

            // Lấy thông tin ngày giờ của lịch trình
            LocalDate scheduledDate = schedule.getScheduledDate();
            LocalDate endDate = schedule.getEndDate() != null ? schedule.getEndDate() : scheduledDate; // Nếu không có ngày kết thúc, coi như là trong ngày

            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            // Mặc định cho sự kiện cả ngày (all-day)
            LocalDateTime scheduleStartDateTime = scheduledDate.atStartOfDay(); // 00:00 của ngày bắt đầu
            LocalDateTime scheduleEndDateTime = endDate.atTime(LocalTime.MAX);   // 23:59:59 của ngày kết thúc

            // Nếu có giờ cụ thể, dùng giờ đó
            if (startTime != null) {
                scheduleStartDateTime = scheduledDate.atTime(startTime);
            }
            if (endTime != null) {
                scheduleEndDateTime = endDate.atTime(endTime);
            }

            // --- ÁP DỤNG CÁC QUY TẮC ĐỂ XÁC ĐỊNH TRẠNG THÁI ---
            // QUY TẮC 1: QUÁ HẠN
            // Nếu thời gian kết thúc của lịch trình đã qua VÀ nó chưa được "Hoàn thành"
            if (scheduleEndDateTime.isBefore(now)) {
                schedule.setStatusName("Quá hạn");
                schedule.setStatusId(4); // ID của "Quá hạn"
            } // QUY TẮC 2: ĐANG THỰC HIỆN
            // Nếu thời gian hiện tại nằm trong khoảng [bắt đầu, kết thúc]
            else if (!now.isBefore(scheduleStartDateTime) && !now.isAfter(scheduleEndDateTime)) {
                schedule.setStatusName("Đang thực hiện");
                schedule.setStatusId(2); // ID của "Đang thực hiện"
            } // QUY TẮC 3: SẮP TỚI
            // Nếu thời gian bắt đầu của lịch trình vẫn chưa tới
            else if (scheduleStartDateTime.isAfter(now)) {
                schedule.setStatusName("Sắp tới");
                schedule.setStatusId(1); // ID của "Sắp tới"
            }
        }
    }

    private void handleMarkAsComplete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        try {
            // Đọc body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String raw = sb.toString().trim();

            Integer scheduleId = null;

            // Ưu tiên JSON body nếu Content-Type là application/json
            String ct = request.getContentType();
            if (!raw.isEmpty() && ct != null && ct.toLowerCase().contains("application/json")) {
                // Dùng parser kiểu cũ (tương thích rộng)
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                com.google.gson.JsonElement je = parser.parse(raw);

                if (je.isJsonPrimitive()) {
                    // body = 123 hoặc "123"
                    if (je.getAsJsonPrimitive().isNumber()) {
                        scheduleId = je.getAsInt();
                    } else {
                        String s = je.getAsString();
                        if (s != null && !s.trim().isEmpty()) {
                            scheduleId = Integer.parseInt(s.trim());
                        }
                    }
                } else if (je.isJsonObject()) {
                    com.google.gson.JsonObject obj = je.getAsJsonObject();
                    if (obj.has("id")) {
                        scheduleId = Integer.parseInt(obj.get("id").getAsString().trim());
                    } else if (obj.has("scheduleId")) {
                        scheduleId = Integer.parseInt(obj.get("scheduleId").getAsString().trim());
                    } else if (obj.has("schedule") && obj.get("schedule").isJsonObject()) {
                        com.google.gson.JsonObject sch = obj.getAsJsonObject("schedule");
                        if (sch.has("id")) {
                            scheduleId = Integer.parseInt(sch.get("id").getAsString().trim());
                        }
                    }
                }
            }

            // Fallback: form-urlencoded ?id=123
            if (scheduleId == null) {
                String p = request.getParameter("id");
                if (p != null && !p.isBlank()) {
                    try {
                        scheduleId = Integer.parseInt(p.trim());
                    } catch (NumberFormatException ignore) {
                    }
                }
            }

            if (scheduleId == null || scheduleId <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Dữ liệu không hợp lệ: thiếu hoặc sai ID.\"}");
                return;
            }

            boolean ok = new ScheduleDAO().markAsCompleted(scheduleId);
            if (ok) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Cập nhật trạng thái thành công!\",\"id\":" + scheduleId + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Không tìm thấy lịch trình với ID đã cho.\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String msg = e.getMessage() == null ? "" : (" - " + e.getMessage().replace("\"", "\\\""));
            response.getWriter().write("{\"message\":\"Lỗi server: " + e.getClass().getSimpleName() + msg + "\"}");
        }
    }
// Thêm hàm private này vào trong class ScheduleController.java của bạn

    private void updateSingleScheduleStatus(MaintenanceSchedule schedule) {
        // Nếu không có lịch trình thì không làm gì cả
        if (schedule == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // --- ƯU TIÊN SỐ 1: Không thay đổi các trạng thái cuối cùng (Hoàn thành, Đã hủy) ---
        Integer currentStatusId = schedule.getStatusId();
        if (currentStatusId != null && (currentStatusId == 3 || currentStatusId == 5)) { // Giả sử 3=Hoàn thành, 5=Đã hủy
            return; // Bỏ qua, không cập nhật
        }

        // Lấy thông tin ngày giờ của lịch trình
        LocalDate scheduledDate = schedule.getScheduledDate();
        LocalDate endDate = schedule.getEndDate() != null ? schedule.getEndDate() : scheduledDate;
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        // Xác định thời điểm bắt đầu và kết thúc chính xác
        LocalDateTime scheduleStartDateTime = (startTime != null) ? scheduledDate.atTime(startTime) : scheduledDate.atStartOfDay();
        LocalDateTime scheduleEndDateTime = (endTime != null) ? endDate.atTime(endTime) : endDate.atTime(LocalTime.MAX);

        // --- ÁP DỤNG CÁC QUY TẮC ĐỂ XÁC ĐỊNH TRẠNG THÁI ---
        if (scheduleEndDateTime.isBefore(now)) {
            schedule.setStatusName("Quá hạn");
            schedule.setStatusId(4); // ID của "Quá hạn"
        } else if (!now.isBefore(scheduleStartDateTime) && !now.isAfter(scheduleEndDateTime)) {
            schedule.setStatusName("Đang thực hiện");
            schedule.setStatusId(2); // ID của "Đang thực hiện"
        } else {
            schedule.setStatusName("Sắp tới");
            schedule.setStatusId(1); // ID của "Sắp tới"
        }
    }
}
