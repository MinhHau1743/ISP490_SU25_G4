package vn.edu.fpt.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Product;

import static vn.edu.fpt.dao.DBContext.getConnection;

public class ProductDAO extends DBContext {

    public boolean editProduct(Product p) {
        String query = "UPDATE Products SET "
                + "name = ?, product_code = ?, image = ?, origin = ?, price = ?, "
                + "description = ?, category_id = ?, is_deleted = ?, created_at = ?, updated_at = ? "
                + "WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, p.getName());
            st.setString(2, p.getProductCode());
            st.setString(3, p.getImage());
            st.setString(4, p.getOrigin());
            st.setDouble(5, p.getPrice());
            st.setString(6, p.getDescription());
            st.setInt(7, p.getCategoryId());
            st.setBoolean(8, p.isIsDeleted());
            st.setString(9, p.getCreatedAt());
            st.setString(10, p.getUpdatedAt());
            st.setInt(11, p.getId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            return false;
        }
    }

    public Product getProductById(int id) {
        String query = "SELECT * FROM Products WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setProductCode(rs.getString("product_code"));
                    p.setImage(rs.getString("image"));
                    p.setOrigin(rs.getString("origin"));
                    p.setPrice(rs.getDouble("price"));
                    p.setDescription(rs.getString("description"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setIsDeleted(rs.getBoolean("is_deleted"));
                    p.setCreatedAt(rs.getString("created_at"));
                    p.setUpdatedAt(rs.getString("updated_at"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu: " + e.getMessage());
        }
        return null;
    }

    public boolean isProductCodeExists(String productCode) {
        String sql = "SELECT COUNT(*) FROM Products WHERE product_code = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getAllOrigins() {
        List<String> origins = new ArrayList<>();
        String query = "SELECT DISTINCT origin FROM Products WHERE origin IS NOT NULL AND origin != ''";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                origins.add(rs.getString("origin"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return origins;
    }

    public int insertProduct(Product p) {
        String sql = "INSERT INTO Products (name, category_id, product_code, image, origin, price, description, is_deleted, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            st.setString(1, p.getName());
            st.setInt(2, p.getCategoryId());
            st.setString(3, p.getProductCode());
            st.setString(4, p.getImage());
            st.setString(5, p.getOrigin());
            st.setDouble(6, p.getPrice());
            st.setString(7, p.getDescription());
            st.setBoolean(8, p.isIsDeleted());
            st.setString(9, p.getCreatedAt());
            st.setString(10, p.getUpdatedAt());

            int rows = st.executeUpdate();
            if (rows == 0) throw new SQLException("Creating product failed, no rows affected.");

            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn dữ liệu: " + e.getMessage());
        }
        return -1;
    }

    public void updateProductImage(int productId, String imageFileName) throws SQLException {
        String sql = "UPDATE Products SET image = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imageFileName);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        }
    }

    public List<Product> searchProducts(String keyword, Double minPrice, Double maxPrice, String origin, Integer categoryId) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (origin != null && !origin.trim().isEmpty()) {
            sql.append(" AND origin = ?");
            params.add(origin);
        }
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setProductCode(rs.getString("product_code"));
                    p.setImage(rs.getString("image"));
                    p.setOrigin(rs.getString("origin"));
                    p.setPrice(rs.getDouble("price"));
                    p.setDescription(rs.getString("description"));
                    p.setIsDeleted(rs.getBoolean("is_deleted"));
                    p.setCreatedAt(rs.getString("created_at"));
                    p.setUpdatedAt(rs.getString("updated_at"));
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage());
        }
        return products;
    }

    public int countProductsWithFilter(String keyword, Double minPrice, Double maxPrice, String origin, Integer categoryId) {
        int count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (origin != null && !origin.trim().isEmpty()) {
            sql.append(" AND origin = ?");
            params.add(origin);
        }
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi đếm sản phẩm: " + e.getMessage());
        }
        return count;
    }

    public List<Product> getProductsWithFilter(String keyword, Double minPrice, Double maxPrice, String origin, Integer categoryId, int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (origin != null && !origin.trim().isEmpty()) {
            sql.append(" AND origin = ?");
            params.add(origin);
        }
        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    p.setCategoryId(rs.getInt("category_id"));
                    p.setProductCode(rs.getString("product_code"));
                    p.setImage(rs.getString("image"));
                    p.setOrigin(rs.getString("origin"));
                    p.setPrice(rs.getDouble("price"));
                    p.setDescription(rs.getString("description"));
                    p.setIsDeleted(rs.getBoolean("is_deleted"));
                    p.setCreatedAt(rs.getString("created_at"));
                    p.setUpdatedAt(rs.getString("updated_at"));
                    products.add(p);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy sản phẩm phân trang: " + e.getMessage());
        }
        return products;
    }

    public boolean checkProductCodeExists(String code) {
        String sql = "SELECT COUNT(*) FROM Products WHERE product_code = ?";
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, code);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteProduct(int id) {
        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0;";
        String deleteProduct = "DELETE FROM Products WHERE id = ?";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1;";

        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(disableFKChecks);
            }
            try (PreparedStatement st = conn.prepareStatement(deleteProduct)) {
                st.setInt(1, id);
                st.executeUpdate();
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(enableFKChecks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllActiveProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setProductCode(rs.getString("product_code"));
                p.setImage(rs.getString("image"));
                p.setOrigin(rs.getString("origin"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
                p.setCategoryId(rs.getInt("category_id"));
                products.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn tất cả sản phẩm: " + e.getMessage());
        }
        return products;
    }
}
