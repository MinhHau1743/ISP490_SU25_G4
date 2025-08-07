// File: src/java/vn/edu/fpt/controller/GetWardsController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.model.Ward;
import com.google.gson.Gson;

@WebServlet(name = "GetWardsController", urlPatterns = {"/getWards"})
public class GetWardsController extends HttpServlet {
  @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String districtIdStr = request.getParameter("districtId");
        List<Ward> wards = Collections.emptyList(); // Mặc định là danh sách rỗng

        if (districtIdStr != null && !districtIdStr.trim().isEmpty()) {
            try {
                int districtId = Integer.parseInt(districtIdStr);
                AddressDAO addressDAO = new AddressDAO();
                wards = addressDAO.getWardsByDistrictId(districtId);
            } catch (NumberFormatException e) {
                // Log lỗi nếu districtId không phải là số
                System.err.println("Invalid districtId format: " + districtIdStr);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception e) {
                // Log lỗi nếu có vấn đề với cơ sở dữ liệu
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        
        Gson gson = new Gson();
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(wards));
            out.flush();
        }
    }
}
