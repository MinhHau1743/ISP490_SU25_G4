// File: src/java/vn/edu/fpt/controller/GetDistrictsController.java
package vn.edu.fpt.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.AddressDAO;
import vn.edu.fpt.model.District;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "GetDistrictsController", urlPatterns = {"/getDistricts"})
public class GetDistrictsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String provinceIdStr = request.getParameter("provinceId");
        List<District> districts = Collections.emptyList(); // Mặc định là danh sách rỗng

        if (provinceIdStr != null && !provinceIdStr.trim().isEmpty()) {
            try {
                int provinceId = Integer.parseInt(provinceIdStr);
                AddressDAO addressDAO = new AddressDAO();
                districts = addressDAO.getDistrictsByProvinceId(provinceId);
            } catch (NumberFormatException e) {
                // Log lỗi nếu provinceId không phải là số
                System.err.println("Invalid provinceId format: " + provinceIdStr);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (Exception e) {
                // Log lỗi nếu có vấn đề với cơ sở dữ liệu
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        Gson gson = new Gson();
        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(districts));
            out.flush();
        }
    }
}

