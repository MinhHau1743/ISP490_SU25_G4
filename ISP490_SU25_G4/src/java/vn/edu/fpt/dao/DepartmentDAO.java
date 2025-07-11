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
        String sql = "SELECT id, name FROM Departments ORDER BY name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Department(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
