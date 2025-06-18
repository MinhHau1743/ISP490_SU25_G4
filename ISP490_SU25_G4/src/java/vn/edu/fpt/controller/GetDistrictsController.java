// File: src/java/vn/edu/fpt/controller/GetDistrictsController.java
package vn.edu.fpt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.DistrictDAO;
import vn.edu.fpt.model.District;

@WebServlet(name = "GetDistrictsController", urlPatterns = {"/getDistricts"})
public class GetDistrictsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String provinceIdStr = request.getParameter("provinceId");

        try (PrintWriter out = response.getWriter()) {
            if (provinceIdStr == null || provinceIdStr.trim().isEmpty()) {
                out.print("[]");
                return;
            }

            try {
                int provinceId = Integer.parseInt(provinceIdStr);
                DistrictDAO districtDAO = new DistrictDAO();
                List<District> districts = districtDAO.getDistrictsByProvinceId(provinceId);

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < districts.size(); i++) {
                    District d = districts.get(i);
                    String safeName = d.getName().replace("\"", "\\\"");
                    json.append("{\"id\":").append(d.getId()).append(",\"name\":\"").append(safeName).append("\"}");
                    if (i < districts.size() - 1) {
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
