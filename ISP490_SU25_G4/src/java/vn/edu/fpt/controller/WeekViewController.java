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
        List<String> dayHeaders = Arrays.asList("SUN 13/07", "MON 14/07", "TUE 15/07", "WED 16/07", "THU 17/07", "FRI 18/07", "SAT 19/07");

        request.setAttribute("hours", hours);
        request.setAttribute("days", days);
        request.setAttribute("dayHeaders", dayHeaders);
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
