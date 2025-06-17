/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Product;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author phamh
 */
public class ProductDAO extends DBContext {

   public List<Product> viewAllProduct(int indexPage, int pageSize) {
    List<Product> products = new ArrayList<>();
    String query = "SELECT * FROM Products LIMIT ? OFFSET ?";

    try {
        PreparedStatement st = connection.prepareStatement(query);
        st.setInt(1, pageSize);  
        st.setInt(2, (indexPage - 1) * pageSize); 

        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            Product p = new Product();
            p.setId(rs.getInt("id"));
            p.setName(rs.getString("name"));
            p.setProductCode(rs.getString("product_code"));
            p.setOrigin(rs.getString("origin"));
            p.setPrice(rs.getDouble("price"));
            p.setDescription(rs.getString("description"));
            p.setCategoryId(rs.getInt("category_id"));
            p.setIsDeleted(rs.getBoolean("is_deleted"));
            p.setCreatedAt(rs.getString("created_at"));
            p.setUpdatedAt(rs.getString("updated_at"));

            products.add(p);
        }

    } catch (SQLException e) {
        System.out.println("Lỗi khi truy vấn dữ liệu: " + e.getMessage());
    }

    return products;
}
public int countAllProducts() {
    String query = "SELECT COUNT(*) FROM Products";
    try {
        PreparedStatement st = connection.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.out.println("Lỗi khi đếm sản phẩm: " + e.getMessage());
    }
    return 0;
}



    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAO();

    }
}
