/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    // Hàm tiện ích để đóng các tài nguyên
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm tiện ích để đếm
    private int getCount(String query, String... params) throws SQLException {
        int count = 0;
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            closeResources();
        }
        return count;
    }

    // 1. Lấy tổng doanh thu trong khoảng thời gian
    public double getTotalRevenue(String startDate, String endDate) throws SQLException {
        String query = "SELECT SUM(cp.quantity * cp.unit_price) "
                + "FROM ContractProducts cp "
                + "JOIN Contracts c ON cp.contract_id = c.id "
                + "WHERE c.start_date BETWEEN ? AND ?";
        double total = 0;
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } finally {
            closeResources();
        }
        return total;
    }

    // 2a. Lấy số lượng khách hàng mới
    public int getNewCustomerCount(String startDate, String endDate) throws SQLException {
        String query = "SELECT COUNT(id) FROM Enterprises WHERE created_at BETWEEN ? AND ? AND is_deleted = 0";
        return getCount(query, startDate, endDate);
    }

    // 2b. Lấy tổng số khách hàng
    public int getTotalCustomerCount() throws SQLException {
        String query = "SELECT COUNT(id) FROM Enterprises WHERE is_deleted = 0";
        return getCount(query);
    }

    // 2c. Lấy số khách hàng quay lại (khách hàng cũ nhưng có hợp đồng mới trong kỳ)
    public int getReturningCustomerCount(String startDate, String endDate) throws SQLException {
        String query = "SELECT COUNT(DISTINCT c.enterprise_id) FROM Contracts c "
                + "JOIN Enterprises e ON c.enterprise_id = e.id "
                + "WHERE c.start_date BETWEEN ? AND ? AND e.created_at < ?";
        int count = 0;
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startDate); // Khách hàng được tạo trước ngày bắt đầu
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            closeResources();
        }
        return count;
    }

    // 3. Lấy số lượng hợp đồng theo trạng thái
    public Map<String, Integer> getContractStatusCounts() throws SQLException {
        String query = "SELECT "
                + "SUM(CASE WHEN status = 'active' AND end_date >= CURDATE() THEN 1 ELSE 0 END) as active, "
                + "SUM(CASE WHEN status = 'active' AND end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 1 ELSE 0 END) as expiring, "
                + "SUM(CASE WHEN status = 'expired' OR end_date < CURDATE() THEN 1 ELSE 0 END) as expired "
                + "FROM Contracts";
        Map<String, Integer> counts = new HashMap<>();
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                counts.put("active", rs.getInt("active"));
                counts.put("expiring", rs.getInt("expiring"));
                counts.put("expired", rs.getInt("expired"));
            }
        } finally {
            closeResources();
        }
        return counts;
    }

    // 4. Lấy số lượng yêu cầu kỹ thuật theo trạng thái
    public Map<String, Integer> getTechnicalRequestStatusCounts(String startDate, String endDate) throws SQLException {
        String query = "SELECT status, COUNT(id) as count "
                + "FROM TechnicalRequests "
                + "WHERE created_at BETWEEN ? AND ? "
                + "GROUP BY status";
        Map<String, Integer> counts = new HashMap<>();
        counts.put("completed", 0);
        counts.put("in_progress", 0);
        counts.put("pending", 0);
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                switch (status) {
                    case "resolved":
                    case "closed":
                        counts.put("completed", counts.get("completed") + count);
                        break;
                    case "assigned":
                    case "in_progress":
                        counts.put("in_progress", counts.get("in_progress") + count);
                        break;
                    case "new":
                        counts.put("pending", counts.get("pending") + count);
                        break;
                }
            }
        } finally {
            closeResources();
        }
        return counts;
    }

    // 5. Lấy danh sách sản phẩm bán chạy trong khoảng thời gian
    public List<Map<String, Object>> getTopProducts(String startDate, String endDate, int limit) throws SQLException {
        String query = "SELECT p.name, SUM(cp.quantity) as total_sold "
                + "FROM ContractProducts cp "
                + "JOIN Products p ON cp.product_id = p.id "
                + "JOIN Contracts c ON cp.contract_id = c.id "
                + "WHERE p.is_deleted = 0 AND c.start_date BETWEEN ? AND ? "
                + "GROUP BY p.id, p.name "
                + "ORDER BY total_sold DESC "
                + "LIMIT ?";
        List<Map<String, Object>> topProducts = new ArrayList<>();
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setInt(3, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("name", rs.getString("name"));
                product.put("sales", rs.getInt("total_sold"));
                topProducts.add(product);
            }
        } finally {
            closeResources();
        }
        return topProducts;
    }

    // 6. Lấy xu hướng doanh thu theo ngày để vẽ biểu đồ
    public List<Map<String, Object>> getRevenueTrend(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> trendData = new ArrayList<>();
        String query = "SELECT DATE(c.start_date) as a_date, SUM(cp.quantity * cp.unit_price) as daily_revenue "
                + "FROM ContractProducts cp "
                + "JOIN Contracts c ON cp.contract_id = c.id "
                + "WHERE c.start_date BETWEEN ? AND ? "
                + "GROUP BY a_date "
                + "ORDER BY a_date ASC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("date", rs.getString("a_date"));
                dataPoint.put("revenue", rs.getDouble("daily_revenue"));
                trendData.add(dataPoint);
            }
        } finally {
            closeResources();
        }
        return trendData;
    }

    // 7. Lấy danh sách khách hàng mới trong khoảng thời gian
    public List<Map<String, Object>> getNewCustomersList(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> customers = new ArrayList<>();
        String query = "SELECT enterprise_code, name, created_at, avatar_url FROM Enterprises "
                + "WHERE created_at BETWEEN ? AND ? AND is_deleted = 0 "
                + "ORDER BY created_at DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("code", rs.getString("enterprise_code"));
                customer.put("name", rs.getString("name"));
                customer.put("created_at", rs.getDate("created_at"));
                customer.put("avatar_url", rs.getString("avatar_url"));
                customers.add(customer);
            }
        } finally {
            closeResources();
        }
        return customers;
    }

    // Thêm 2 phương thức này vào file ReportDAO.java
// 8. Lấy danh sách hợp đồng chi tiết trong khoảng thời gian
    public List<Map<String, Object>> getContractsList(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> contracts = new ArrayList<>();
        String query = "SELECT c.contract_code, e.name as enterprise_name, c.start_date, c.end_date, c.status "
                + "FROM Contracts c "
                + "JOIN Enterprises e ON c.enterprise_id = e.id "
                + "WHERE c.start_date BETWEEN ? AND ? "
                + "ORDER BY c.start_date DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> contract = new HashMap<>();
                contract.put("code", rs.getString("contract_code"));
                contract.put("enterprise_name", rs.getString("enterprise_name"));
                contract.put("start_date", rs.getDate("start_date"));
                contract.put("end_date", rs.getDate("end_date"));
                contract.put("status", rs.getString("status"));
                contracts.add(contract);
            }
        } finally {
            closeResources();
        }
        return contracts;
    }

// 9. Lấy danh sách yêu cầu sửa chữa chi tiết trong khoảng thời gian
    public List<Map<String, Object>> getTechnicalRequestsList(String startDate, String endDate) throws SQLException {
        List<Map<String, Object>> requests = new ArrayList<>();
        // CONCAT_WS để nối tên nhân viên, nếu chưa phân công (assigned_to_id is NULL) thì sẽ trả về NULL
        String query = "SELECT tr.request_code, tr.title, e.name as enterprise_name, "
                + "CONCAT_WS(' ', u.first_name, u.middle_name, u.last_name) as assigned_to_name, "
                + "tr.created_at, tr.status "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "LEFT JOIN Users u ON tr.assigned_to_id = u.id "
                + // LEFT JOIN để lấy cả các yêu cầu chưa được phân công
                "WHERE tr.created_at BETWEEN ? AND ? "
                + "ORDER BY tr.created_at DESC";
        try {
            conn = new DBContext().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> request = new HashMap<>();
                request.put("code", rs.getString("request_code"));
                request.put("title", rs.getString("title"));
                request.put("enterprise_name", rs.getString("enterprise_name"));
                request.put("assigned_to", rs.getString("assigned_to_name"));
                request.put("created_at", rs.getTimestamp("created_at"));
                request.put("status", rs.getString("status"));
                requests.add(request);
            }
        } finally {
            closeResources();
        }
        return requests;
    }
}
