/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import vn.edu.fpt.model.Department;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author minhh
 */

public class DepartmentDAO {

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();

        // Câu lệnh SQL để lấy tất cả các phòng ban
        String sql = "SELECT id, name FROM departments ORDER BY name";

        try (Connection conn = DBContext.getConnection(); // Sử dụng lớp DBContext của bạn để kết nối
                 PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            // Lặp qua từng dòng kết quả trả về
            while (rs.next()) {
                Department department = new Department();
                // Đọc dữ liệu từ ResultSet và gán vào đối tượng Department
                department.setId(rs.getInt("id"));
                department.setName(rs.getString("name"));
                // Thêm đối tượng vào danh sách
                list.add(department);
            }
        } catch (Exception e) {
            // In lỗi ra console để dễ dàng gỡ rối nếu có sự cố
            System.err.println("Lỗi khi lấy danh sách phòng ban: " + e.getMessage());
            e.printStackTrace();
        }

        // Trả về danh sách (có thể rỗng nếu không có dữ liệu hoặc có lỗi)
        return list;
    }

    public static void main(String[] args) {
        // Giả sử lớp DAO chứa phương thức getAllDepartments là UserDAO
        DepartmentDAO userDAO = new DepartmentDAO();

        // Gọi phương thức getAllDepartments
        List<Department> departments = userDAO.getAllDepartments();

        // Kiểm tra và in kết quả
        if (departments != null && !departments.isEmpty()) {
            System.out.println("Danh sách các phòng ban:");
            for (Department dept : departments) {
                System.out.println("ID: " + dept.getId() + ", Name: " + dept.getName());
            }
        } else {
            System.out.println("Không có phòng ban nào được tìm thấy hoặc danh sách rỗng.");
        }
    }
}
