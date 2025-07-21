// File: vn/edu/fpt/controller/listScheduleController.java
package vn.edu.fpt.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@WebServlet(name = "listScheduleController", urlPatterns = {"/listSchedule"})
public class listScheduleController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String> hours = new ArrayList<>();
        for (int h = 1; h <= 23; h++) {
            hours.add(String.format("%02d:00", h));
            hours.add(String.format("%02d:30", h));
        }

        List<String> days = Arrays.asList("sun", "mon", "tue", "wed", "thu", "fri", "sat");

        LocalDate today = LocalDate.now();
        int daysFromSunday = today.getDayOfWeek().getValue() % 7;
        LocalDate sunday = today.minusDays(daysFromSunday);
        if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            sunday = sunday.minusWeeks(1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        List<String> dayHeaders = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = sunday.plusDays(i);
            String dayOfWeek = day.getDayOfWeek().toString().substring(0, 3);
            String formattedDate = day.format(formatter);
            dayHeaders.add(dayOfWeek + " " + formattedDate);
        }

        // Tính toán cho month-view
        int year = today.getYear();
        int month = today.getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();
        int firstDayValue = firstDayOfWeek.getValue(); // 1=MONDAY, 7=SUNDAY
        int prevDaysCount = firstDayValue - 1; // Số ngày tháng trước cần hiển thị (bắt đầu từ Thứ Hai)

        LocalDate lastOfPrevMonth = firstOfMonth.minusDays(1);
        int prevMonthDays = lastOfPrevMonth.getDayOfMonth();

        List<String> dayNumbers = new ArrayList<>();
        List<Boolean> isCurrentMonths = new ArrayList<>();

        // Thêm ngày của tháng trước
        int prevDay = prevMonthDays - prevDaysCount + 1;
        for (int i = 0; i < prevDaysCount; i++) {
            dayNumbers.add(String.valueOf(prevDay + i));
            isCurrentMonths.add(false);
        }

        // Thêm ngày của tháng hiện tại
        for (int d = 1; d <= daysInMonth; d++) {
            dayNumbers.add(String.valueOf(d));
            isCurrentMonths.add(true);
        }

        // Thêm ngày của tháng sau để đủ hàng
        int totalDays = dayNumbers.size();
        int nextDaysCount = (7 - (totalDays % 7)) % 7;
        for (int i = 1; i <= nextDaysCount; i++) {
            dayNumbers.add(String.valueOf(i));
            isCurrentMonths.add(false);
        }

        // Tính toán cho day-view
        String dayDate = today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String dayHeader = today.getDayOfWeek().name();

        // Day time labels and start times
        List<String> dayTimeLabels = new ArrayList<>();
        List<String> dayStartTimes = new ArrayList<>();

        dayTimeLabels.add("all-day");
        dayStartTimes.add("all-day");

        // AM 1-11
        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 am");
            dayStartTimes.add(h + ":00");
            dayTimeLabels.add("");
            dayStartTimes.add(h + ":30");
        }

        // 12 PM (noon)
        dayTimeLabels.add("12:00 pm");
        dayStartTimes.add("12:00");
        dayTimeLabels.add("");
        dayStartTimes.add("12:30");

        // PM 1-11
        for (int h = 1; h <= 11; h++) {
            dayTimeLabels.add(h + ":00 pm");
            dayStartTimes.add(h + ":00");
            dayTimeLabels.add("");
            dayStartTimes.add(h + ":30");
        }

        // 12 AM (midnight)
        dayTimeLabels.add("12:00 am");
        dayStartTimes.add("12:00");

        request.setAttribute("hours", hours);
        request.setAttribute("days", days);
        request.setAttribute("dayHeaders", dayHeaders);
        request.setAttribute("dayNumbers", dayNumbers);
        request.setAttribute("isCurrentMonths", isCurrentMonths);
        request.setAttribute("dayDate", dayDate);
        request.setAttribute("dayHeader", dayHeader.toUpperCase());
        request.setAttribute("dayTimeLabels", dayTimeLabels);
        request.setAttribute("dayStartTimes", dayStartTimes);
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