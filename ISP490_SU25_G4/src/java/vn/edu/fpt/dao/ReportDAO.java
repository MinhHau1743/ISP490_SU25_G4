package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lớp DAO quản lý các truy vấn dùng cho việc báo cáo và thống kê dashboard.
 *
 * @author YourName (updated by AI)
 */
public class ReportDAO extends DBContext {

    private void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
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

    private int getCount(String query, String... params) {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return count;
    }

    public double getTotalRevenue(String startDate, String endDate) {
        String query = "SELECT SUM(cp.quantity * cp.unit_price) "
                + "FROM ContractProducts cp "
                + "JOIN contracts c ON cp.contract_id = c.id "
                + "WHERE c.created_at BETWEEN ? AND ?";
        double total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return total;
    }

    public int getNewCustomerCount(String startDate, String endDate) {
        String query = "SELECT COUNT(id) FROM Enterprises WHERE created_at BETWEEN ? AND ? AND is_deleted = 0";
        return getCount(query, startDate, endDate);
    }

    public int getTotalCustomerCount() {
        String query = "SELECT COUNT(id) FROM Enterprises WHERE is_deleted = 0";
        return getCount(query);
    }

    // ## FIX: Thay thế `c.enterprise_id` bằng `c.customer_id` (hoặc tên cột đúng trong DB của bạn) ##
    public int getReturningCustomerCount(String startDate, String endDate) {
        String query = "SELECT COUNT(DISTINCT c.customer_id) FROM contracts c "
                + "JOIN Enterprises e ON c.customer_id = e.id "
                + "WHERE c.created_at BETWEEN ? AND ? AND e.created_at < ?";
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setString(3, startDate);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return count;
    }

    public Map<String, Integer> getContractStatusCounts(String startDate, String endDate) {
        Map<String, Integer> counts = new HashMap<>();
        String query = "SELECT cs.name, COUNT(c.id) as count "
                + "FROM contracts c "
                + "JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.created_at BETWEEN ? AND ? AND c.is_deleted = 0 "
                + "GROUP BY cs.name";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("name"), rs.getInt("count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return counts;
    }

    public Map<String, Integer> getTechnicalRequestStatusCounts(String startDate, String endDate) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("Đã giải quyết", 0);
        counts.put("Đang xử lý", 0);
        counts.put("Chờ xử lý", 0);

        String query = "SELECT status, COUNT(id) as count "
                + "FROM TechnicalRequests "
                + "WHERE created_at BETWEEN ? AND ? AND is_deleted = 0 "
                + "GROUP BY status";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
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
                        counts.put("Đã giải quyết", counts.get("Đã giải quyết") + count);
                        break;
                    case "assigned":
                    case "in_progress":
                        counts.put("Đang xử lý", counts.get("Đang xử lý") + count);
                        break;
                    case "new":
                        counts.put("Chờ xử lý", counts.get("Chờ xử lý") + count);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return counts;
    }

    public List<Map<String, Object>> getTopProducts(String startDate, String endDate, int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        String query = "SELECT p.name, SUM(cp.quantity) as total_sold "
                + "FROM ContractProducts cp "
                + "JOIN Products p ON cp.product_id = p.id "
                + "JOIN contracts c ON cp.contract_id = c.id "
                + "WHERE p.is_deleted = 0 AND c.created_at BETWEEN ? AND ? "
                + "GROUP BY p.id, p.name "
                + "ORDER BY total_sold DESC "
                + "LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return topProducts;
    }

    public List<Map<String, Object>> getTopProductsByRevenue(String startDate, String endDate, int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        String query = "SELECT p.name, SUM(cp.quantity * cp.unit_price) as total_revenue "
                + "FROM ContractProducts cp "
                + "JOIN Products p ON cp.product_id = p.id "
                + "JOIN contracts c ON cp.contract_id = c.id "
                + "WHERE p.is_deleted = 0 AND c.created_at BETWEEN ? AND ? "
                + "GROUP BY p.id, p.name "
                + "ORDER BY total_revenue DESC "
                + "LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ps.setInt(3, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("name", rs.getString("name"));
                product.put("revenue", rs.getDouble("total_revenue"));
                topProducts.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return topProducts;
    }

    public List<Map<String, Object>> getRevenueTrend(String startDate, String endDate) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        String query = "SELECT DATE(c.created_at) as a_date, SUM(cp.quantity * cp.unit_price) as daily_revenue "
                + "FROM ContractProducts cp "
                + "JOIN contracts c ON cp.contract_id = c.id "
                + "WHERE c.created_at BETWEEN ? AND ? "
                + "GROUP BY a_date "
                + "ORDER BY a_date ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return trendData;
    }

    public List<Map<String, Object>> getNewCustomersList(String startDate, String endDate) {
        List<Map<String, Object>> customers = new ArrayList<>();
        String query = "SELECT enterprise_code, name, created_at, avatar_url FROM Enterprises "
                + "WHERE created_at BETWEEN ? AND ? AND is_deleted = 0 "
                + "ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("code", rs.getString("enterprise_code"));
                customer.put("name", rs.getString("name"));
                customer.put("created_at", rs.getTimestamp("created_at"));
                customer.put("avatar_url", rs.getString("avatar_url"));
                customers.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return customers;
    }

    // ## FIX: Thay thế `c.enterprise_id` bằng `c.customer_id` (hoặc tên cột đúng trong DB của bạn) ##
    public List<Map<String, Object>> getContractsList(String startDate, String endDate) {
        List<Map<String, Object>> contracts = new ArrayList<>();
        String query = "SELECT c.contract_code, e.name as enterprise_name, c.start_date, c.end_date, cs.name as status "
                + "FROM contracts c "
                + "JOIN Enterprises e ON c.customer_id = e.id "
                + "LEFT JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.created_at BETWEEN ? AND ? "
                + "ORDER BY c.created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return contracts;
    }

    public List<Map<String, Object>> getTechnicalRequestsWithDevices(String startDate, String endDate) {
        Map<Integer, Map<String, Object>> requestMap = new HashMap<>();
        String requestsQuery = "SELECT tr.id, tr.request_code, tr.title, e.name as enterprise_name, "
                + "CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) as assigned_to_name, "
                + "tr.created_at, tr.status "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "LEFT JOIN users u ON tr.assigned_to_id = u.id "
                + "WHERE tr.is_deleted = 0 AND tr.created_at BETWEEN ? AND ? "
                + "ORDER BY tr.created_at DESC";

        Connection conn = null;
        PreparedStatement psRequests = null;
        ResultSet rsRequests = null;
        PreparedStatement psDevices = null;
        ResultSet rsDevices = null;

        try {
            conn = getConnection();

            psRequests = conn.prepareStatement(requestsQuery);
            psRequests.setString(1, startDate);
            psRequests.setString(2, endDate);
            rsRequests = psRequests.executeQuery();

            List<Integer> requestIds = new ArrayList<>();
            while (rsRequests.next()) {
                int requestId = rsRequests.getInt("id");
                Map<String, Object> request = new HashMap<>();
                request.put("id", requestId);
                request.put("code", rsRequests.getString("request_code"));
                request.put("title", rsRequests.getString("title"));
                request.put("enterprise_name", rsRequests.getString("enterprise_name"));
                request.put("assigned_to", rsRequests.getString("assigned_to_name"));
                request.put("created_at", rsRequests.getTimestamp("created_at"));
                request.put("status", rsRequests.getString("status"));
                request.put("devices", new ArrayList<Map<String, Object>>());

                requestMap.put(requestId, request);
                requestIds.add(requestId);
            }

            if (!requestIds.isEmpty()) {
                String placeholders = requestIds.stream().map(id -> "?").collect(Collectors.joining(","));
                String devicesQuery = "SELECT technical_request_id, device_name, serial_number, problem_description "
                        + "FROM TechnicalRequestDevices WHERE technical_request_id IN (" + placeholders + ")";

                psDevices = conn.prepareStatement(devicesQuery);
                for (int i = 0; i < requestIds.size(); i++) {
                    psDevices.setInt(i + 1, requestIds.get(i));
                }
                rsDevices = psDevices.executeQuery();

                while (rsDevices.next()) {
                    int technicalRequestId = rsDevices.getInt("technical_request_id");
                    if (requestMap.containsKey(technicalRequestId)) {
                        Map<String, Object> device = new HashMap<>();
                        device.put("device_name", rsDevices.getString("device_name"));
                        device.put("serial_number", rsDevices.getString("serial_number"));
                        device.put("problem_description", rsDevices.getString("problem_description"));

                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> devicesList = (List<Map<String, Object>>) requestMap.get(technicalRequestId).get("devices");
                        devicesList.add(device);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, psDevices, rsDevices);
            closeResources(conn, psRequests, rsRequests);
        }

        return new ArrayList<>(requestMap.values());
    }

    public List<Map<String, Object>> getNewCustomersTrend(String startDate, String endDate) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        String query = "SELECT DATE(created_at) as a_date, COUNT(id) as daily_new_customers "
                + "FROM Enterprises "
                + "WHERE is_deleted = 0 AND created_at BETWEEN ? AND ? "
                + "GROUP BY a_date "
                + "ORDER BY a_date ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> dataPoint = new HashMap<>();
                dataPoint.put("date", rs.getString("a_date"));
                dataPoint.put("count", rs.getInt("daily_new_customers"));
                trendData.add(dataPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, ps, rs);
        }
        return trendData;
    }
}
