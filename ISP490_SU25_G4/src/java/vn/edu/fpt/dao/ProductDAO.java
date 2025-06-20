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
import vn.edu.fpt.model.ProductCategory;

/**
 *
 * @author phamh
 */
public class ProductDAO extends DBContext {

    public List<Product> viewAllProduct(int indexPage, int pageSize) {
        List<Product> products = new ArrayList<>();
        // Sửa câu truy vấn SQL để JOIN với ProductCategories
        String query = "SELECT pc.name AS category_name,p.*  FROM Products p "
                + "LEFT JOIN ProductCategories pc ON p.category_id = pc.id "
                + "LIMIT ? OFFSET ?";

        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, pageSize);
            st.setInt(2, (indexPage - 1) * pageSize);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setProductCode(rs.getString("product_code"));
                p.setOrigin(rs.getString("origin"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
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

    public boolean editProduct(Product p) {
        String query = "UPDATE Products SET "
                + "name = ?, "
                + "product_code = ?, "
                + "origin = ?, "
                + "price = ?, "
                + "description = ?, "
                + "category_id = ?, "
                + "is_deleted = ?, "
                + "created_at = ?, "
                + "updated_at = ? "
                + "WHERE id = ?";

        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.setString(1, p.getName());
            st.setString(2, p.getProductCode());
            st.setString(3, p.getOrigin());
            st.setDouble(4, p.getPrice());
            st.setString(5, p.getDescription());
            st.setInt(6, p.getCategoryId());
            st.setBoolean(7, p.isIsDeleted());
            st.setString(8, p.getCreatedAt());
            st.setString(9, p.getUpdatedAt());
            st.setInt(10, p.getId());

            int rows = st.executeUpdate();
            return rows > 0; // Trả về true nếu update thành công

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            return false;
        }
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

    public Product getProductById(int id) {
        String query = "SELECT * FROM Products WHERE id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(query);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
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
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu: " + e.getMessage());
        }
        return null;
    }

    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> categories = new ArrayList<>();

        String sql = "SELECT id, name FROM ProductCategories";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ProductCategory cat = new ProductCategory();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static void main(String[] args) {
        ProductDAO productDAO = new ProductDAO();
        List<Product> pro = productDAO.viewAllProduct(1, 10);
        for (Product p : pro) {
            System.out.println(p);
        }
    }
}
