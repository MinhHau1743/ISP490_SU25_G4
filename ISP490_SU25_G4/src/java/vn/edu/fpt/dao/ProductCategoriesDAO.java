/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static vn.edu.fpt.dao.DBContext.getConnection;
import vn.edu.fpt.model.ProductCategory;

/**
 *
 * @author phamh
 */
public class ProductCategoriesDAO extends DBContext {

    Connection conn = getConnection();

    public List<ProductCategory> getAllCategories() {
        List<ProductCategory> categories = new ArrayList<>();

        String sql = "SELECT id, name FROM ProductCategories";

        try {
            PreparedStatement st = conn.prepareStatement(sql);
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

    public boolean insertCategory(ProductCategory category) {
        String sql = "INSERT INTO ProductCategories (name) VALUES (?)";
        try {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, category.getName());
            int rowsInserted = st.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicate(String name) {
        String sql = "SELECT COUNT(*) FROM product_category WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
