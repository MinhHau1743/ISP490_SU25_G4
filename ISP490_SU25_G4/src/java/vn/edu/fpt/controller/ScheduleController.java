package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.sql.SQLException;
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

        // 1. Lấy ngày hiện tại hoặc từ frontend
        String controllerDay = request.getParameter("controllerDay"); // "prev", "next", or null
        String currentDayStr = request.getParameter("currentDay");    // yyyy-MM-dd
        String viewMode = request.getParameter("viewMode");      // "day-view", "week-view", "month-view", "list-view"

        LocalDate today = LocalDate.now();
        if (currentDayStr != null && !currentDayStr.isEmpty()) {
            try {
                today = LocalDate.parse(currentDayStr, inputFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Failed to parse currentDayStr: " + currentDayStr);
            }
        }

        if ("prev".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.minusMonths(1);
            } else if ("week-view".equals(viewMode)) {
                today = today.minusDays(7);
            } else {
                today = today.minusDays(1);
            }
        } else if ("next".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.plusMonths(1);
            } else if ("week-view".equals(viewMode)) {
                today = today.plusDays(7);
            } else {
                today = today.plusDays(1);
            }
        }

        LocalDate now = LocalDate.now();
        boolean isToday = today.equals(now);

        // Tuần/tháng hiện tại
        WeekFields weekFields = WeekFields.of(new Locale("vi", "VN"));
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        int currentWeekYear = now.get(weekFields.weekBasedYear());
        int targetWeek = today.get(weekFields.weekOfWeekBasedYear());
        int targetWeekYear = today.get(weekFields.weekBasedYear());
        boolean isThisWeek = (currentWeek == targetWeek) && (currentWeekYear == targetWeekYear);

        boolean isThisMonth = today.getMonthValue() == now.getMonthValue() && today.getYear() == now.getYear();

        // Hiển thị range theo view
        String displayDate;
        DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("vi", "VN"));

        if ("week-view".equals(viewMode)) {
            if (isThisWeek) {
                displayDate = "Tuần này";
            } else {
                LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1);
                LocalDate endOfWeek = today.with(weekFields.dayOfWeek(), 7);
                displayDate = startOfWeek.format(rangeFormatter) + " - " + endOfWeek.format(rangeFormatter);
            }
        } else if ("month-view".equals(viewMode)) {
            displayDate = isThisMonth ? "Tháng này" : today.format(monthFormatter);
        } else {
            displayDate = isToday ? "Hôm nay" : today.format(displayFormatter);
        }

        String isoDayDate = today.toString(); // yyyy-MM-dd
        String dayHeader = today.format(DateTimeFormatter.ofPattern("EEEE", new Locale("vi", "VN")));
        String dayDate = today.format(dayDateFormatter);

        // 2. Day slots (mỗi 30')
        List<String> dayTimeLabels = new ArrayList<>();
        List<String> dayStartTimes = new ArrayList<>();

        dayTimeLabels.add("Cả ngày");
        dayStartTimes.add("");

// 12:00 am và 12:30 am đầu tiên
        dayTimeLabels.add("12:00 am");
        dayStartTimes.add("00:00");
        dayTimeLabels.add("12:30 am");
        dayStartTimes.add("00:30");

// Sáng: 01:00 am đến 11:30 am
        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(String.format("%02d:00 am", h));
            dayStartTimes.add(String.format("%02d:00", h));
            dayTimeLabels.add(String.format("%02d:30 am", h));
            dayStartTimes.add(String.format("%02d:30", h));
        }

// 12:00 pm và 12:30 pm (buổi trưa)
        dayTimeLabels.add("12:00 pm");
        dayStartTimes.add("12:00");
        dayTimeLabels.add("12:30 pm");
        dayStartTimes.add("12:30");

// Chiều: 01:00 pm đến 11:30 pm (13h-23h)
        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(String.format("%02d:00 pm", h));
            dayStartTimes.add(String.format("%02d:00", h + 12));
            dayTimeLabels.add(String.format("%02d:30 pm", h));
            dayStartTimes.add(String.format("%02d:30", h + 12));
        }

// Cuối cùng: 23:30 pm (nếu muốn slot cuối cho nửa đêm)
        dayTimeLabels.add("11:30 pm");
        dayStartTimes.add("23:30");

        // 3. Week view
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1);
        List<String> days = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun");
        DateTimeFormatter weekHeaderFormatter = DateTimeFormatter.ofPattern("EEE dd/MM", new Locale("vi", "VN"));
        List<String> dayHeaders = new ArrayList<>();
        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = startOfWeek.plusDays(i);
            weekDates.add(d);
            dayHeaders.add(d.format(weekHeaderFormatter));
        }

        // 4. Month view
        int year = today.getYear();
        int month = today.getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        int firstDayValue = firstDayOfWeek.getValue(); // 1=MON .. 7=SUN
        int prevDaysCount = firstDayValue - 1;

        LocalDate lastOfPrevMonth = firstOfMonth.minusDays(1);
        int prevMonthDays = lastOfPrevMonth.getDayOfMonth();

        List<String> dayNumbers = new ArrayList<>();
        List<Boolean> isCurrentMonths = new ArrayList<>();
        List<LocalDate> monthDates = new ArrayList<>();

        YearMonth prevMonth = yearMonth.minusMonths(1);
        LocalDate prevStartDay = lastOfPrevMonth.minusDays(prevDaysCount - 1);
        int prevDay = prevMonthDays - prevDaysCount + 1;
        for (int i = 0; i < prevDaysCount; i++) {
            dayNumbers.add(String.valueOf(prevDay + i));
            isCurrentMonths.add(false);
            monthDates.add(prevStartDay.plusDays(i));
        }
        for (int d = 1; d <= daysInMonth; d++) {
            dayNumbers.add(String.valueOf(d));
            isCurrentMonths.add(true);
            monthDates.add(LocalDate.of(year, month, d));
        }
        int totalDays = dayNumbers.size();
        int nextDaysCount = (7 - (totalDays % 7)) % 7;
        YearMonth nextMonth = yearMonth.plusMonths(1);
        LocalDate nextStart = nextMonth.atDay(1);
        for (int i = 1; i <= nextDaysCount; i++) {
            dayNumbers.add(String.valueOf(i));
            isCurrentMonths.add(false);
            monthDates.add(nextStart.plusDays(i - 1));
        }

        // 5. Timeline giờ (week/month)
        List<String> hours = new ArrayList<>();
        List<String> hourLabels = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            hours.add(String.format("%02d:00", h));
            hours.add(String.format("%02d:30", h));

            String amPm;
            int displayHour;
            if (h == 0) {
                displayHour = 12;
                amPm = "am";
            } else if (h < 12) {
                displayHour = h;
                amPm = "am";
            } else if (h == 12) {
                displayHour = 12;
                amPm = "pm";
            } else {
                displayHour = h - 12;
                amPm = "pm";
            }

            // Slot giờ chẵn
            hourLabels.add(String.format("%02d:00 %s", displayHour, amPm));
            // Slot phút 30: label luôn đầy đủ, không để trống
            hourLabels.add(String.format("%02d:30 %s", displayHour, amPm));
        }
        HttpSession session = request.getSession();
        // 6. Dữ liệu lịch + assignments
        Integer userId = (Integer) session.getAttribute("userId");
        String mySchedule = request.getParameter("mySchedule");

        String type = request.getParameter("type"); // "all", "request", "campaign"
        boolean technicalOnly = "request".equals(type);
        boolean campaignOnly = "campaign".equals(type);
        Integer statusId = null;
        String statusParam = request.getParameter("status");
        if (statusParam != null && !statusParam.isEmpty()) {
            try {
                statusId = Integer.valueOf(statusParam);
            } catch (NumberFormatException e) {
                // log nhẹ, bỏ lọc nếu user nhập không hợp lệ
                statusId = null;
            }
        }
        List<MaintenanceSchedule> schedules;
        if ("1".equals(mySchedule)) {
            // Lấy lịch do user assign, ví dụ qua userId là người thực hiện
            schedules = dao.getMaintenanceSchedules(userId, technicalOnly, campaignOnly, statusId);
        } else {
            // Lấy toàn bộ (không filter theo userId), hoặc chỉ filter theo các tuỳ chọn còn lại
            schedules = dao.getMaintenanceSchedules(null, technicalOnly, campaignOnly, statusId);
        }
        List<MaintenanceAssignments> assignments = dao.getAllMaintenanceAssignments();
        List<Status> statusList = dao.getAllStatuses();
        Map<Integer, List<MaintenanceAssignments>> assignmentMap
                = assignments.stream().collect(Collectors.groupingBy(MaintenanceAssignments::getMaintenanceScheduleId));

// Gán vào schedule: setAssignedUserIds từ List<MaintenanceAssignments>
        for (MaintenanceSchedule schedule : schedules) {
            List<MaintenanceAssignments> assigns = assignmentMap.getOrDefault(schedule.getId(), Collections.emptyList());
            List<Integer> assignedUserIds = assigns.stream()
                    .map(MaintenanceAssignments::getUserId)
                    .collect(Collectors.toList());
            schedule.setAssignedUserIds(assignedUserIds);
            schedule.setAssignments(assigns);
        }

        Map<LocalDate, List<MaintenanceSchedule>> groupedSchedules
                = schedules.stream().collect(Collectors.groupingBy(MaintenanceSchedule::getScheduledDate));
        updateScheduleStatuses(schedules, dao);
        // Set attribute ra JSP
        request.setAttribute("currentMySchedule", mySchedule);
        request.setAttribute("statusList", statusList);
        request.setAttribute("currentType", type);
        request.setAttribute("currentStatus", statusId);
        request.setAttribute("groupedSchedules", groupedSchedules);
        request.setAttribute("schedules", schedules);
        request.setAttribute("hourLabels", hourLabels);
        request.setAttribute("hours", hours);
        request.setAttribute("days", days);
        // Day view
        request.setAttribute("dayHeader", dayHeader.toUpperCase());
        request.setAttribute("dayTimeLabels", dayTimeLabels);
        request.setAttribute("dayStartTimes", dayStartTimes);
        request.setAttribute("isoDayDate", isoDayDate);
        request.setAttribute("displayDate", displayDate);
        request.setAttribute("today", today);
        request.setAttribute("dayDate", dayDate);
        // Week view
        request.setAttribute("dayHeaders", dayHeaders);
        request.setAttribute("weekDates", weekDates);
        // Month view
        request.setAttribute("dayNumbers", dayNumbers);
        request.setAttribute("isCurrentMonths", isCurrentMonths);
        request.setAttribute("monthDates", monthDates);
        // View mode
        request.setAttribute("viewMode", viewMode != null ? viewMode : "day-view");
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

        // Map để lưu trữ lỗi theo từng trường
        Map<String, String> errors = new HashMap<>();

        try {
            // === 1. LẤY DỮ LIỆU TỪ FORM ===
            int id = Integer.parseInt(request.getParameter("id"));
            String technicalRequestIdStr = request.getParameter("technicalRequestId");
            String campaignIdStr = request.getParameter("campaignId");
            String title = request.getParameter("title");
            String color = request.getParameter("color");
            String scheduledDateStr = request.getParameter("scheduledDate");
            String endDateStr = request.getParameter("endDate");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String statusIdStr = request.getParameter("statusId"); // QUAN TRỌNG: trường status
            String notes = request.getParameter("notes");

            // Dữ liệu địa chỉ mới
            String streetAddress = request.getParameter("streetAddress");
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");
            String wardIdStr = request.getParameter("ward");

            // === 2. VALIDATE DỮ LIỆU ===
            if (title == null || title.trim().isEmpty()) {
                errors.put("titleError", "Vui lòng nhập tiêu đề.");
            } else if (title.trim().length() > 255) {
                errors.put("titleError", "Tiêu đề không được vượt quá 255 ký tự.");
            }

            if (scheduledDateStr == null || scheduledDateStr.trim().isEmpty()) {
                errors.put("scheduledDateError", "Vui lòng chọn ngày bắt đầu.");
            }

            if (provinceIdStr == null || provinceIdStr.isEmpty()) {
                errors.put("provinceError", "Vui lòng chọn Tỉnh/Thành.");
            }

            if (districtIdStr == null || districtIdStr.isEmpty()) {
                errors.put("districtError", "Vui lòng chọn Quận/Huyện.");
            }

            if (wardIdStr == null || wardIdStr.isEmpty()) {
                errors.put("wardError", "Vui lòng chọn Phường/Xã.");
            }

            // Parse và validate ngày tháng
            LocalDate scheduledDate = null;
            LocalDate endDate = null;
            if (scheduledDateStr != null && !scheduledDateStr.trim().isEmpty()) {
                try {
                    scheduledDate = LocalDate.parse(scheduledDateStr);
                } catch (DateTimeParseException e) {
                    errors.put("scheduledDateError", "Ngày bắt đầu không hợp lệ.");
                }
            }

            if (endDateStr != null && !endDateStr.trim().isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateStr);
                } catch (DateTimeParseException e) {
                    errors.put("endDateError", "Ngày kết thúc không hợp lệ.");
                }
            }

            // Validate logic ngày tháng
            if (scheduledDate != null && endDate != null && endDate.isBefore(scheduledDate)) {
                errors.put("endDateError", "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
            }
            if (notes != null && notes.trim().length() > 2000) {
                errors.put("notesError", "Ghi chú không được vượt quá 2000 ký tự.");
            }

            // Validate thời gian
            LocalTime startTime = null;
            LocalTime endTime = null;
            if (startTimeStr != null && !startTimeStr.trim().isEmpty()) {
                try {
                    startTime = LocalTime.parse(startTimeStr);
                } catch (DateTimeParseException e) {
                    errors.put("startTimeError", "Giờ bắt đầu không hợp lệ.");
                }
            }

            if (endTimeStr != null && !endTimeStr.trim().isEmpty()) {
                try {
                    endTime = LocalTime.parse(endTimeStr);
                } catch (DateTimeParseException e) {
                    errors.put("endTimeError", "Giờ kết thúc không hợp lệ.");
                }
            }

            // Validate logic thời gian
            if (startTime != null && endTime != null && (endDate == null || endDate.isEqual(scheduledDate)) && !endTime.isAfter(startTime)) {
                errors.put("endTimeError", "Giờ kết thúc phải sau giờ bắt đầu nếu trong cùng một ngày.");
            }

            // Nếu có lỗi validation, chuyển hướng trở lại form
            if (!errors.isEmpty()) {
                // Lưu các giá trị đã nhập để hiển thị lại
                for (String paramName : request.getParameterMap().keySet()) {
                    if (!paramName.equals("id") && !paramName.equals("technicalRequestId") && !paramName.equals("campaignId")) {
                        request.setAttribute("param_" + paramName, request.getParameter(paramName));
                    }
                }

                // QUAN TRỌNG: Khôi phục lại dữ liệu cho dropdowns, bao gồm status
                restoreEditFormData(request, id);

                // Lưu lỗi vào request
                for (Map.Entry<String, String> error : errors.entrySet()) {
                    request.setAttribute(error.getKey(), error.getValue());
                }

                forwardToEditForm(request, response);
                return;
            }

            // === 3. PARSE VÀ CẬP NHẬT ĐỐI TƯỢNG ===
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(id);
            if (schedule == null) {
                throw new Exception("Lịch bảo trì không còn tồn tại.");
            }

            // Tạo hoặc tìm địa chỉ và lấy ID
            int provinceId = Integer.parseInt(provinceIdStr);
            int districtId = Integer.parseInt(districtIdStr);
            int wardId = Integer.parseInt(wardIdStr);
            int addressId = addressDAO.findOrCreateAddress(streetAddress, wardId, districtId, provinceId);

            // Cập nhật các thuộc tính của đối tượng schedule
            if (technicalRequestIdStr != null && !technicalRequestIdStr.isEmpty()) {
                schedule.setTechnicalRequestId(Integer.valueOf(technicalRequestIdStr));
            } else {
                schedule.setTechnicalRequestId(null);
            }

            if (campaignIdStr != null && !campaignIdStr.isEmpty()) {
                schedule.setCampaignId(Integer.valueOf(campaignIdStr));
            } else {
                schedule.setCampaignId(null);
            }

            Integer statusId = null;
            if (statusIdStr != null && !statusIdStr.isBlank()) {
                statusId = Integer.valueOf(statusIdStr);
            }

            schedule.setTitle(title);
            schedule.setColor(color);
            schedule.setScheduledDate(scheduledDate);
            schedule.setEndDate(endDate);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setAddressId(addressId);
            schedule.setStatusId(statusId); // QUAN TRỌNG: Đặt giá trị status
            schedule.setNotes(notes);

            // === 4. LƯU THAY ĐỔI VÀO DATABASE ===
            boolean scheduleUpdated = scheduleDAO.updateMaintenanceSchedule(schedule);

            // Lấy ID yêu cầu kỹ thuật GẮN VỚI schedule này (không phải từ form)
            if (schedule.getTechnicalRequestId() != null) {
                tr.setId(schedule.getTechnicalRequestId());
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
                campaign.setName(title);
                campaign.setDescription(notes);
                boolean ok = campaignDAO.updateCampaignTitleAndDesc(campaign);
                if (!ok) {
                    request.setAttribute("error", "Cập nhật Campaign thất bại!");
                    forwardToEditForm(request, response);
                    return;
                }
            }

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
                            System.err.println("Invalid user ID format: " + userIdStr);
                        }
                    }
                }

                // 4.2. Gọi DAO để cập nhật bảng MaintenanceAssignments
                boolean assignmentsUpdated = scheduleDAO.updateAssignmentsForSchedule(schedule.getId(), newUserIds);

                // 4.3. Kiểm tra kết quả và chuyển hướng
                if (assignmentsUpdated) {
                    // Chuyển hướng kèm message lên URL
                    request.getSession().setAttribute("successMsg", "Cập nhật lịch bảo trì thành công!");
                    response.sendRedirect(request.getContextPath() + "/schedule?msg=updated");
                } else {
                    throw new Exception("Cập nhật lịch bảo trì thành công, nhưng cập nhật phân công nhân viên thất bại.");
                }

            } else {
                throw new Exception("Cập nhật lịch bảo trì thất bại do lỗi cơ sở dữ liệu.");
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {
            request.setAttribute("error", e.getMessage());
            forwardToEditForm(request, response);
        } catch (Exception e) {
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
            ScheduleDAO dao = new ScheduleDAO();
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
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
        String val = json.optString(key, "").trim();
        return val.isEmpty() ? null : LocalDate.parse(val);
    }

    private LocalTime parseNullableTime(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
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

    private void getDistricts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String provinceIdStr = request.getParameter("provinceId");
        List<District> districts = Collections.emptyList();
        if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
            try {
                districts = new EnterpriseDAO().getDistrictsByProvinceId(Integer.parseInt(provinceIdStr));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(districts));
            out.flush();
        }
    }

    private void getWards(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String districtIdStr = request.getParameter("districtId");
        List<Ward> wards = Collections.emptyList();
        if (districtIdStr != null && !districtIdStr.trim().isEmpty()) {
            try {
                wards = new EnterpriseDAO().getWardsByDistrictId(Integer.parseInt(districtIdStr));
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        try (PrintWriter out = response.getWriter()) {
            out.print(new Gson().toJson(wards));
            out.flush();
        }
    }

    private void updateScheduleStatuses(List<MaintenanceSchedule> schedules, ScheduleDAO scheduleDAO) {
        LocalDateTime now = LocalDateTime.now();

        for (MaintenanceSchedule schedule : schedules) {
            int originalStatusId = schedule.getStatusId();

            if (schedule.getStatusName() != null && schedule.getStatusName().equalsIgnoreCase("Hoàn thành")) {
                schedule.setStatusId(3);
                continue;
            }

            LocalDate scheduledDate = schedule.getScheduledDate();
            LocalDate endDate = schedule.getEndDate() != null ? schedule.getEndDate() : scheduledDate;
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            LocalDateTime scheduleStartDateTime = scheduledDate.atStartOfDay();
            LocalDateTime scheduleEndDateTime = endDate.atTime(LocalTime.MAX);

            if (startTime != null) {
                scheduleStartDateTime = scheduledDate.atTime(startTime);
            }
            if (endTime != null) {
                scheduleEndDateTime = endDate.atTime(endTime);
            }

            // Logic xác định trạng thái mới
            int newStatusId = originalStatusId;
            String newStatusName = schedule.getStatusName();

            if (scheduleEndDateTime.isBefore(now)) {
                newStatusName = "Quá hạn";
                newStatusId = 4;
            } else if (!now.isBefore(scheduleStartDateTime) && !now.isAfter(scheduleEndDateTime)) {
                newStatusName = "Đang thực hiện";
                newStatusId = 2;
            } else if (scheduleStartDateTime.isAfter(now)) {
                newStatusName = "Sắp tới";
                newStatusId = 1;
            }

            // ⭐ PHẦN QUAN TRỌNG: Chỉ gọi UPDATE khi trạng thái thực sự thay đổi
            if (newStatusId != originalStatusId) {
                try {
                    // Gọi hàm DAO để lưu thay đổi vào DB
                    scheduleDAO.updateScheduleStatus(schedule.getId(), newStatusId);

                    // Cập nhật lại đối tượng trong list để hiển thị đúng
                    schedule.setStatusId(newStatusId);
                    schedule.setStatusName(newStatusName);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi cập nhật DB cho lịch #" + schedule.getId());
                    e.printStackTrace();
                }
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

    private void restoreEditFormData(HttpServletRequest request, int scheduleId) {
        try {
            ScheduleDAO scheduleDAO = new ScheduleDAO();
            AddressDAO addressDAO = new AddressDAO();
            UserDAO userDAO = new UserDAO();

            // Lấy lại dữ liệu schedule
            MaintenanceSchedule schedule = scheduleDAO.getMaintenanceScheduleById(scheduleId);
            if (schedule != null) {
                request.setAttribute("schedule", schedule);
            }

            // Lấy lại các danh sách cần thiết cho dropdown
            List<User> assignments = userDAO.getAllTechnicalStaffIdAndFullName();
            List<MaintenanceSchedule> ms = scheduleDAO.getAllTechnicalRequestsAndCampaignsIdAndTitle();
            List<Province> provinces = addressDAO.getAllProvinces();
            List<Integer> assignedUserIds = scheduleDAO.getAssignedUserIdsByScheduleId(scheduleId);
            List<Status> statusList = scheduleDAO.getAllStatuses();

            // Chuyển đổi List thành Map để tra cứu nhanh
            Map<Integer, Boolean> assignedUserMap = new HashMap<>();
            for (Integer userId : assignedUserIds) {
                assignedUserMap.put(userId, true);
            }

            // Đặt lại các attribute
            request.setAttribute("statusList", statusList);
            request.setAttribute("assignedUserMap", assignedUserMap);
            request.setAttribute("assignments", assignments);
            request.setAttribute("technicalRequests", ms);
            request.setAttribute("provinces", provinces);

            // Tải lại danh sách Quận/Huyện và Phường/Xã nếu có
            String provinceIdStr = request.getParameter("province");
            String districtIdStr = request.getParameter("district");

            if (provinceIdStr != null && !provinceIdStr.isEmpty()) {
                try {
                    int provinceId = Integer.parseInt(provinceIdStr);
                    request.setAttribute("districts", addressDAO.getDistrictsByProvinceId(provinceId));

                    if (districtIdStr != null && !districtIdStr.isEmpty()) {
                        int districtId = Integer.parseInt(districtIdStr);
                        request.setAttribute("wards", addressDAO.getWardsByDistrictId(districtId));
                    }
                } catch (NumberFormatException e) {
                    // Ignore number format issues
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Log lỗi nhưng không làm gián đoạn flow chính
        }
    }
}
