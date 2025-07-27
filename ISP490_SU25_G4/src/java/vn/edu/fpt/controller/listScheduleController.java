package vn.edu.fpt.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "listScheduleController", urlPatterns = {"/listSchedule"})
public class listScheduleController extends HttpServlet {

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ========== 1. Lấy ngày hiện tại hoặc từ frontend ==========
        String controllerDay = request.getParameter("controllerDay");     // "prev", "next", or null
        String currentDayStr = request.getParameter("currentDay");        // dạng: yyyy-MM-dd

        LocalDate today = LocalDate.now();                                // fallback nếu không có currentDay

        if (currentDayStr != null && !currentDayStr.isEmpty()) {
            try {
                today = LocalDate.parse(currentDayStr, inputFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Failed to parse currentDayStr: " + currentDayStr);
            }
        }

        if ("prev".equals(controllerDay)) {
            today = today.minusDays(1);
        } else if ("next".equals(controllerDay)) {
            today = today.plusDays(1);
        }

        LocalDate now = LocalDate.now();
        boolean isToday = today.equals(now);
        String displayDate = isToday ? "Hôm nay" : today.format(displayFormatter);
        String isoDayDate = today.toString(); // yyyy-MM-dd

        String dayHeader = today.getDayOfWeek().name(); // MONDAY, TUESDAY, ...

        // ========== 2. Tạo danh sách thời gian trong ngày ==========
        List<String> dayTimeLabels = new ArrayList<>();
        List<String> dayStartTimes = new ArrayList<>();
        dayTimeLabels.add("all-day");
        dayStartTimes.add("all-day");

        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 am");
            dayStartTimes.add(h + ":00");
            dayTimeLabels.add("");
            dayStartTimes.add(h + ":30");
        }
        dayTimeLabels.add("12:00 pm");
        dayStartTimes.add("12:00");
        dayTimeLabels.add("");
        dayStartTimes.add("12:30");

        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 pm");
            dayStartTimes.add(h + ":00");
            dayTimeLabels.add("");
            dayStartTimes.add(h + ":30");
        }

        dayTimeLabels.add("12:00 am");
        dayStartTimes.add("12:00");

        // ========== 3. Xử lý chế độ TUẦN ==========
        List<String> days = Arrays.asList("sun", "mon", "tue", "wed", "thu", "fri", "sat");
        int daysFromSunday = today.getDayOfWeek().getValue() % 7;
        LocalDate sunday = today.minusDays(daysFromSunday);
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            sunday = sunday.minusWeeks(1);
        }

        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> dayHeaders = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = sunday.plusDays(i);
            String dayOfWeek = day.getDayOfWeek().toString().substring(0, 3); // MON, TUE, ...
            String formattedDate = day.format(weekFormatter);
            dayHeaders.add(dayOfWeek + " " + formattedDate);
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

        int prevDay = prevMonthDays - prevDaysCount + 1;
        for (int i = 0; i < prevDaysCount; i++) {
            dayNumbers.add(String.valueOf(prevDay + i));
            isCurrentMonths.add(false);
        }

        for (int d = 1; d <= daysInMonth; d++) {
            dayNumbers.add(String.valueOf(d));
            isCurrentMonths.add(true);
        }

        int totalDays = dayNumbers.size();
        int nextDaysCount = (7 - (totalDays % 7)) % 7;
        for (int i = 1; i <= nextDaysCount; i++) {
            dayNumbers.add(String.valueOf(i));
            isCurrentMonths.add(false);
        }

        // ========== 5. Timeline các giờ (Weekly/Month View) ==========
        List<String> hours = new ArrayList<>();
        for (int h = 1; h <= 23; h++) {
            hours.add(String.format("%02d:00", h));
            hours.add(String.format("%02d:30", h));
        }

        // ========== 6. Truyền dữ liệu ra JSP ==========
        request.setAttribute("hours", hours);
        request.setAttribute("days", days);

        // Day View
        request.setAttribute("dayHeader", dayHeader.toUpperCase());
        request.setAttribute("dayTimeLabels", dayTimeLabels);
        request.setAttribute("dayStartTimes", dayStartTimes);
        request.setAttribute("isoDayDate", isoDayDate);         // yyyy-MM-dd, để client-side JS dễ dùng
        request.setAttribute("displayDate", displayDate);       // "Hôm nay" hoặc "Thứ ba, 22/07/2025"

        // Week View
        request.setAttribute("dayHeaders", dayHeaders);

        // Month View
        request.setAttribute("dayNumbers", dayNumbers);
        request.setAttribute("isCurrentMonths", isCurrentMonths);

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
