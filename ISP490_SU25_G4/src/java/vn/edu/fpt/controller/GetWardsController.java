// File: src/java/vn/edu/fpt/controller/GetWardsController.java
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.WardDAO;
import vn.edu.fpt.model.Ward;

@WebServlet(name = "GetWardsController", urlPatterns = {"/getWards"})
public class GetWardsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String districtIdStr = request.getParameter("districtId");
        try (PrintWriter out = response.getWriter()) {
            if (districtIdStr == null || districtIdStr.trim().isEmpty()) {
                out.print("[]");
                return;
            }
            try {
                int districtId = Integer.parseInt(districtIdStr);
                WardDAO wardDAO = new WardDAO();
                List<Ward> wards = wardDAO.getWardsByDistrictId(districtId);

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < wards.size(); i++) {
                    Ward w = wards.get(i);
                    String safeName = w.getName().replace("\"", "\\\"");
                    json.append("{\"id\":").append(w.getId()).append(",\"name\":\"").append(safeName).append("\"}");
                    if (i < wards.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                out.print(json.toString());
            } catch (NumberFormatException e) {
                out.print("[]");
            }
        }
    }
}
