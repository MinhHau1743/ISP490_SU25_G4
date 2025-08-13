/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.math.BigDecimal;
import java.sql.Statement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Product;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp; // KHẮC PHỤC: Import đúng thư viện

/**
 *
 * @author phamh
 */
public class ProductDAO extends DBContext {

    // 1. Phương thức helper để chuyển đổi ResultSet sang đối tượng Product
// Phương thức này đọc cả các cột ảo created_by_name và updated_by_name
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setProductCode(rs.getString("product_code"));
        p.setImage(rs.getString("image"));
        p.setOrigin(rs.getString("origin"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setDeleted(rs.getBoolean("is_deleted"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        p.setCreatedBy(rs.getInt("created_by"));
        p.setUpdatedBy(rs.getInt("updated_by"));

        // Đọc dữ liệu từ 2 cột ảo được tạo bởi JOIN
        p.setCreatedByName(rs.getString("created_by_name"));
        p.setUpdatedByName(rs.getString("updated_by_name"));

        return p;
    }

    public boolean editProduct(Product p) {
        // CẬP NHẬT: Tự động cập nhật `updated_at` bằng hàm của SQL
        String query = "UPDATE Products SET "
                + "name = ?, product_code = ?, image = ?, origin = ?, "
                + "price = ?, description = ?, is_deleted = ?, updated_by = ?, "
                + "updated_at = CURRENT_TIMESTAMP() "
                + "WHERE id = ?";
        // KHẮC PHỤC: Quản lý connection bằng try-with-resources
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, p.getName());
            st.setString(2, p.getProductCode());
            st.setString(3, p.getImage());
            st.setString(4, p.getOrigin());
            st.setBigDecimal(5, p.getPrice());      // KHẮC PHỤC: Sử dụng setBigDecimal
            st.setString(6, p.getDescription());
            st.setBoolean(7, p.getIsDeleted()); // Sửa thành getIsDeleted()       // KHẮC PHỤC: Sử dụng getter mới
            st.setInt(8, p.getUpdatedBy());         // KHẮC PHỤC: Sử dụng setInt
            st.setInt(9, p.getId());
            int rows = st.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Phương thức lấy một sản phẩm bằng ID
    public Product getProductById(int id) {
        // Câu lệnh SQL đã được cập nhật với JOIN
        String query = "SELECT "
                + "    p.*, "
                + "    CONCAT(u_creator.last_name, ' ', u_creator.first_name) AS created_by_name, "
                + "    CONCAT(u_updater.last_name, ' ', u_updater.first_name) AS updated_by_name "
                + "FROM "
                + "    Products p "
                + "LEFT JOIN "
                + "    Users u_creator ON p.created_by = u_creator.id "
                + "LEFT JOIN "
                + "    Users u_updater ON p.updated_by = u_updater.id "
                + "WHERE p.id = ?";

        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    // Gọi helper method để tạo đối tượng Product
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn sản phẩm bằng ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int insertProduct(Product p) {
        String sql = "INSERT INTO Products "
                + "(name, product_code, image, origin, price, description, is_deleted, created_by, updated_by, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // KHẮC PHỤC: Quản lý connection bằng try-with-resources
        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, p.getName());
            st.setString(2, p.getProductCode());
            st.setString(3, p.getImage());
            st.setString(4, p.getOrigin());
            st.setBigDecimal(5, p.getPrice());    // KHẮC PHỤC: setBigDecimal
            st.setString(6, p.getDescription());
            st.setBoolean(7, p.getIsDeleted()); // Sửa thành getIsDeleted()  // KHẮC PHỤC: getter mới
            st.setInt(8, p.getCreatedBy());       // KHẮC PHỤC: setInt
            st.setInt(9, p.getUpdatedBy());       // KHẮC PHỤC: setInt (Thường thì updated_by = created_by khi mới tạo)

            // Khi tạo mới, createdAt và updatedAt thường là cùng một thời điểm
            Timestamp now = new Timestamp(System.currentTimeMillis());
            st.setTimestamp(10, now);             // KHẮC PHỤC: setTimestamp
            st.setTimestamp(11, now);             // KHẮC PHỤC: setTimestamp

            int rows = st.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Phương thức xóa mềm (an toàn hơn)
    public boolean softDeleteProduct(int productId, int updatedByUserId) {
        System.out.println("\n--- BẮT ĐẦU DEBUG: softDeleteProduct ---");
        System.out.println("[DAO] Nhận được yêu cầu xóa mềm cho Product ID: " + productId + ", bởi User ID: " + updatedByUserId);

        String query = "UPDATE Products SET is_deleted = 1, updated_by = ?, updated_at = CURRENT_TIMESTAMP() WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement st = conn.prepareStatement(query)) {

            // Tham số thứ nhất (?) trong câu SQL là updated_by
            st.setInt(1, updatedByUserId);

            // Tham số thứ hai (?) trong câu SQL là id
            st.setInt(2, productId);

            System.out.println("[DAO] Chuẩn bị thực thi câu lệnh UPDATE...");
            System.out.println("[DAO]   - SQL: " + query);
            System.out.println("[DAO]   - Tham số 1 (updated_by): " + updatedByUserId);
            System.out.println("[DAO]   - Tham số 2 (id): " + productId);

            // Thực thi lệnh và lấy số dòng bị ảnh hưởng
            int rowsAffected = st.executeUpdate();

            // === DÒNG DEBUG QUAN TRỌNG NHẤT ===
            System.out.println("[DAO] KẾT QUẢ: Số dòng bị ảnh hưởng bởi UPDATE là: " + rowsAffected);

            System.out.println("--- KẾT THÚC DEBUG: softDeleteProduct ---\n");

            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            System.out.println("[DAO] !!! LỖI SQL KHI THỰC HIỆN XÓA MỀM !!!");
            e.printStackTrace(); // In ra lỗi chi tiết
            System.out.println("--- KẾT THÚC DEBUG: softDeleteProduct ---\n");
            return false;
        }
    }

    // 3. Phương thức lấy danh sách sản phẩm có bộ lọc và phân trang
    public List<Product> getProductsWithFilter(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String origin, int page, int pageSize) {
        List<Product> products = new ArrayList<>();

        // Câu lệnh SQL nền đã được cập nhật với JOIN
        StringBuilder sql = new StringBuilder(
                "SELECT "
                + "    p.*, "
                + "    CONCAT(u_creator.last_name, ' ', u_creator.first_name) AS created_by_name, "
                + "    CONCAT(u_updater.last_name, ' ', u_updater.first_name) AS updated_by_name "
                + "FROM "
                + "    Products p "
                + "LEFT JOIN "
                + "    Users u_creator ON p.created_by = u_creator.id "
                + "LEFT JOIN "
                + "    Users u_updater ON p.updated_by = u_updater.id "
                + "WHERE p.is_deleted = 0"
        );

        List<Object> params = new ArrayList<>();

        // Nối thêm các điều kiện lọc
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (p.name LIKE ? OR p.product_code LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (minPrice != null) {
            sql.append(" AND p.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.price <= ?");
            params.add(maxPrice);
        }
        if (origin != null && !origin.trim().isEmpty()) {
            sql.append(" AND p.origin = ?");
            params.add(origin);
        }

        sql.append(" ORDER BY p.id DESC");

        // Nối thêm logic phân trang
        int offset = (page - 1) * pageSize;
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy sản phẩm phân trang: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public int countProductsWithFilter(String keyword, BigDecimal minPrice, BigDecimal maxPrice, String origin) {
        int count = 0;
        // Logic xây dựng SQL giữ nguyên
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE is_deleted = 0");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR product_code LIKE ?)");
            params.add("%" + keyword + "%");
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
            e.printStackTrace();
        }
        return count;
    }

    // 4. Phương thức lấy tất cả sản phẩm đang hoạt động
    public List<Product> getAllActiveProducts() {
        List<Product> products = new ArrayList<>();

        // Câu lệnh SQL đã được cập nhật với JOIN
        String query = "SELECT "
                + "    p.*, "
                + "    CONCAT(u_creator.last_name, ' ', u_creator.first_name) AS created_by_name, "
                + "    CONCAT(u_updater.last_name, ' ', u_updater.first_name) AS updated_by_name "
                + "FROM "
                + "    Products p "
                + "LEFT JOIN "
                + "    Users u_creator ON p.created_by = u_creator.id "
                + "LEFT JOIN "
                + "    Users u_updater ON p.updated_by = u_updater.id "
                + "WHERE p.is_deleted = 0 ORDER BY p.name";

        try (Connection conn = DBContext.getConnection(); PreparedStatement st = conn.prepareStatement(query); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn tất cả sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    // Các phương thức khác như isProductCodeExists, getAllOrigins, updateProductImage đã đúng
    // và không cần sửa vì chúng không làm việc với các trường bị thay đổi kiểu.
    // Sửa lại phương thức updateProductImage để tự quản lý connection
    public void updateProductImage(int productId, String imageFileName) throws SQLException {
        String sql = "UPDATE Products SET image = ? WHERE id = ?";

        // KHẮC PHỤC: Thêm "Connection conn = getConnection()" vào trong khối try-with-resources
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, imageFileName);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Có thể ghi log lỗi ở đây nếu cần
            // Ném lại exception để lớp gọi nó có thể xử lý
            throw new SQLException("Error updating product image: " + e.getMessage(), e);
        }
    }

    // Dán hoặc sửa lại phương thức này vào trong file ProductDAO.java
    public boolean isProductCodeExists(String productCode) {
        String sql = "SELECT COUNT(*) FROM Products WHERE product_code = ?";
        // Sử dụng try-with-resources để đảm bảo kết nối được đóng
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra mã sản phẩm tồn tại: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Trả về false nếu có lỗi xảy ra
    }

    public List<String> getAllOrigins() {
        List<String> origins = new ArrayList<>();
        String query = "SELECT DISTINCT origin FROM Products WHERE origin IS NOT NULL AND origin != ''";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                origins.add(rs.getString("origin"));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Có thể thay bằng logging nếu cần
        }

        return origins;
    }

}
