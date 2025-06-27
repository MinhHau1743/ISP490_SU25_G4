// File: vn/edu/fpt/controller/SearchSuggestionController.java
package vn.edu.fpt.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.EnterpriseDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "SearchSuggestionController", urlPatterns = {"/searchSuggestions"})
public class SearchSuggestionController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String query = request.getParameter("query");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        if (query == null || query.trim().length() < 2) { // Chỉ tìm khi có ít nhất 2 ký tự
            out.print(gson.toJson(Collections.emptyList()));
            out.flush();
            return;
        }

        try {
            EnterpriseDAO dao = new EnterpriseDAO();
            List<String> suggestions = dao.getCustomerNameSuggestions(query);
            out.print(gson.toJson(suggestions));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Lỗi máy chủ khi lấy gợi ý.\"}");
        }
        out.flush();
    }
}
