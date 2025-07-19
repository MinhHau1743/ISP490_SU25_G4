// File: vn/edu/fpt/controller/WeekViewController.java
package vn.edu.fpt.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@WebServlet(name = "WeekViewController", urlPatterns = {"/weekController"})
public class WeekViewController extends HttpServlet {

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

        request.setAttribute("hours", hours);
        request.setAttribute("days", days);
        request.setAttribute("dayHeaders", dayHeaders);
        request.setAttribute("today", today); // Có thể truyền sang JSP để highlight ngày hiện tại
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
