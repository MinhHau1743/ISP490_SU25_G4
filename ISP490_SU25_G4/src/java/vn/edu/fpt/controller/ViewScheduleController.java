package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.model.MaintenanceSchedule;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "listScheduleController", urlPatterns = {"/listSchedule"})
public class ViewScheduleController extends HttpServlet {

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
    private final DateTimeFormatter dayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MaintenanceScheduleDAO dao = new MaintenanceScheduleDAO();
        // ========== 1. Lấy ngày hiện tại hoặc từ frontend ==========
        String controllerDay = request.getParameter("controllerDay");     // "prev", "next", or null
        String currentDayStr = request.getParameter("currentDay");        // dạng: yyyy-MM-dd
        String viewMode = request.getParameter("viewMode");               // "day-view", "week-view", "month-view", or null

        LocalDate today = LocalDate.now();                                // fallback nếu không có currentDay

        if (currentDayStr != null && !currentDayStr.isEmpty()) {
            try {
                today = LocalDate.parse(currentDayStr, inputFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Failed to parse currentDayStr: " + currentDayStr);
            }
        }

        // Điều chỉnh today theo controllerDay và viewMode
        if ("prev".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.minusMonths(1); // Lùi 1 tháng cho month-view
            } else if ("week-view".equals(viewMode)) {
                today = today.minusDays(7);
            } else {
                today = today.minusDays(1);
            }
        } else if ("next".equals(controllerDay)) {
            if ("month-view".equals(viewMode)) {
                today = today.plusMonths(1); // Tiến 1 tháng cho month-view
            } else if ("week-view".equals(viewMode)) {
                today = today.plusDays(7);
            } else {
                today = today.plusDays(1);
            }
        }

        LocalDate now = LocalDate.now();
        boolean isToday = today.equals(now);

        // Kiểm tra tuần này (sử dụng WeekFields - tuần bắt đầu từ Thứ Hai theo ISO)
        WeekFields weekFields = WeekFields.of(new Locale("vi", "VN"));
        int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
        int currentWeekYear = now.get(weekFields.weekBasedYear());
        int targetWeek = today.get(weekFields.weekOfWeekBasedYear());
        int targetWeekYear = today.get(weekFields.weekBasedYear());
        boolean isThisWeek = (currentWeek == targetWeek) && (currentWeekYear == targetWeekYear);

        // Kiểm tra tháng này
        boolean isThisMonth = today.getMonthValue() == now.getMonthValue() && today.getYear() == now.getYear();

        // Định dạng cho displayDate dựa trên viewMode
        String displayDate;
        DateTimeFormatter rangeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("vi", "VN")); // "Tháng 7 2025"

        if ("week-view".equals(viewMode)) {
            if (isThisWeek) {
                displayDate = "Tuần này";
            } else {
                // Tính range tuần (từ Thứ Hai đến Chủ Nhật)
                LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Thứ Hai
                LocalDate endOfWeek = today.with(weekFields.dayOfWeek(), 7);  // Chủ Nhật
                displayDate = startOfWeek.format(rangeFormatter) + " - " + endOfWeek.format(rangeFormatter);
            }
        } else if ("month-view".equals(viewMode)) {
            if (isThisMonth) {
                displayDate = "Tháng này";
            } else {
                displayDate = today.format(monthFormatter); // "Tháng 7 2025"
            }
        } else { // Mặc định là day-view (hoặc các view khác)
            displayDate = isToday ? "Hôm nay" : today.format(displayFormatter);
        }

        String isoDayDate = today.toString(); // yyyy-MM-dd

        String dayHeader = today.format(DateTimeFormatter.ofPattern("EEEE", new Locale("vi", "VN"))); // "Thứ Tư"
        String dayDate = today.format(dayDateFormatter);

        // ========== 2. Tạo danh sách thời gian trong ngày ==========
        List<String> dayTimeLabels = new ArrayList<>();
        List<String> dayStartTimes = new ArrayList<>();
        dayTimeLabels.add("Cả ngày");
        dayStartTimes.add("");
        dayTimeLabels.add("12:00 am");
        dayStartTimes.add("00:00");
        dayTimeLabels.add("");
        dayStartTimes.add("00:30");

        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 am");
            dayStartTimes.add(String.format("%02d:00", h));
            dayTimeLabels.add("");
            dayStartTimes.add(String.format("%02d:30", h));
        }
        dayTimeLabels.add("12:00 am");
        dayStartTimes.add("12:00");
        dayTimeLabels.add("");
        dayStartTimes.add("12:30");

        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 pm");
            dayStartTimes.add(String.format("%02d:00", h + 12));
            dayTimeLabels.add("");
            dayStartTimes.add(String.format("%02d:30", h + 12));
        }

        // ========== 3. Xử lý chế độ TUẦN ==========
// Sử dụng lại WeekFields đã khai báo ở trên để đảm bảo tính nhất quán
// WeekFields weekFields = WeekFields.of(new Locale("vi", "VN")); // đã có ở trên
// 1. Dùng WeekFields để xác định ngày bắt đầu tuần một cách chính xác (Thứ Hai)
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1);

// 2. Chuẩn bị các list và formatter
        List<String> days = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun"); // Cập nhật thứ tự cho đúng
        DateTimeFormatter weekHeaderFormatter = DateTimeFormatter.ofPattern("EEE dd/MM", new Locale("vi", "VN")); // Ví dụ: "Th 2 04/08"
        List<String> dayHeaders = new ArrayList<>();
        List<LocalDate> weekDates = new ArrayList<>();

// 3. Vòng lặp 7 ngày từ Thứ Hai
        for (int i = 0; i < 7; i++) {
            LocalDate currentDayInWeek = startOfWeek.plusDays(i);
            weekDates.add(currentDayInWeek); // Thêm ngày (yyyy-MM-dd) vào list
            dayHeaders.add(currentDayInWeek.format(weekHeaderFormatter)); // Thêm header đã định dạng
        }
        // ========== 4. Xử lý chế độ THÁNG ==========
        int year = today.getYear();
        int month = today.getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        int firstDayValue = firstDayOfWeek.getValue(); // 1=MONDAY ... 7=SUNDAY
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

        // ========== 5. Timeline các giờ (Weekly/Month View) ==========
        List<String> hours = new ArrayList<>();
        List<String> hourLabels = new ArrayList<>();
        for (int h = 0; h <= 23; h++) {
            hours.add(String.format("%02d:00", h));
            hourLabels.add(String.format("%02d:00", h));
            hours.add(String.format("%02d:30", h));
            hourLabels.add("");
        }
        // ========== 6. Truyền dữ liệu ra JSP ==========
        List<MaintenanceSchedule> schedules = dao.getAllMaintenanceSchedules();
// Vì scheduledDate là LocalDate, bỏ toLocalDate()
        Map<LocalDate, List<MaintenanceSchedule>> groupedSchedules
                = schedules.stream()
                        .collect(Collectors.groupingBy(MaintenanceSchedule::getScheduledDate));
        request.setAttribute("groupedSchedules", groupedSchedules);
        request.setAttribute("schedules", schedules);
        request.setAttribute("hourLabels", hourLabels);
        request.setAttribute("hours", hours);
        request.setAttribute("days", days);
        // Day View
        request.setAttribute("dayHeader", dayHeader.toUpperCase());
        request.setAttribute("dayTimeLabels", dayTimeLabels);
        request.setAttribute("dayStartTimes", dayStartTimes);
        request.setAttribute("isoDayDate", isoDayDate);         // yyyy-MM-dd, để client-side JS dễ dùng
        request.setAttribute("displayDate", displayDate);       // "Hôm nay" hoặc "Tuần này" hoặc "Tháng này" v.v.
        request.setAttribute("today", today);
        request.setAttribute("dayDate", dayDate);

        // Week View
        request.setAttribute("dayHeaders", dayHeaders);
        request.setAttribute("weekDates", weekDates);

        // Month View
        request.setAttribute("dayNumbers", dayNumbers);
        request.setAttribute("isCurrentMonths", isCurrentMonths);
        request.setAttribute("monthDates", monthDates);

        // Set viewMode attribute for JSP
        request.setAttribute("viewMode", viewMode != null ? viewMode : "day-view");

        // JSP view
        request.getRequestDispatcher("/jsp/customerSupport/listSchedule.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
