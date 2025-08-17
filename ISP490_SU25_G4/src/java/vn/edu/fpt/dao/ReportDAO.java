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
        // Nếu endDate chỉ có dạng yyyy-MM-dd, bổ sung 23:59:59 cho chắc chắn bao trùm cả ngày cuối
        if (endDate != null && endDate.length() == 10) {
            endDate += " 23:59:59";
        }
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

        // Nếu endDate chỉ có định dạng yyyy-MM-dd thì cộng thêm 23:59:59
        if (endDate != null && endDate.length() == 10) {
            endDate += " 23:59:59";
        }

        // Câu SQL lấy số lượng hợp đồng theo trạng thái
        String query = "SELECT cs.id, cs.name, COUNT(c.id) as count "
                + "FROM contracts c "
                + "JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.created_at BETWEEN ? AND ? AND c.is_deleted = 0 "
                + "GROUP BY cs.id, cs.name";

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
                int id = rs.getInt("id");
                int count = rs.getInt("count");

                // Gán key rõ ràng (dùng trong JSP), value là số lượng lấy được từ DB
                switch (id) {
                    case 1:
                        counts.put("negotiating", count);
                        break;
                    case 2:
                        counts.put("inProgress", count);
                        break;
                    case 3:
                        counts.put("completed", count);
                        break;
                    case 4:
                        counts.put("overdue", count);
                        break;
                    case 5:
                        counts.put("signed", count);
                        break;      // CHỈ CÓ "signed" chứ KHÔNG được "Đã ký"
                    case 6:
                        counts.put("paused", count);
                        break;
                    case 7:
                        counts.put("done", count);
                        break;        // nếu k dùng có thể bỏ
                    case 8:
                        counts.put("cancelled", count);
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

    public Map<String, Integer> getTechnicalRequestStatusCounts(String startDate, String endDate) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("upcoming", 0);        // Sắp tới
        counts.put("in_progress", 0);     // Đang thực hiện
        counts.put("completed", 0);       // Hoàn thành
        counts.put("overdue", 0);         // Quá hạn
        counts.put("cancelled", 0);       // Đã huỷ

        String query = "SELECT s.status_name, COUNT(DISTINCT tr.id) as count "
                + "FROM TechnicalRequests tr "
                + "LEFT JOIN MaintenanceSchedules ms ON tr.id = ms.technical_request_id "
                + "LEFT JOIN Statuses s ON ms.status_id = s.id "
                + "WHERE tr.created_at BETWEEN ? AND ? AND tr.is_deleted = 0 "
                + "GROUP BY s.status_name";

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
                String statusName = rs.getString("status_name");
                int count = rs.getInt("count");
                if (statusName == null) {
                    continue;
                }
                switch (statusName.trim()) {
                    case "Sắp tới":
                        counts.put("upcoming", counts.get("upcoming") + count);
                        break;
                    case "Đang thực hiện":
                        counts.put("in_progress", counts.get("in_progress") + count);
                        break;
                    case "Hoàn thành":
                        counts.put("completed", counts.get("completed") + count);
                        break;
                    case "Quá hạn":
                        counts.put("overdue", counts.get("overdue") + count);
                        break;
                    case "Đã huỷ":
                    case "Đã hủy":
                        counts.put("cancelled", counts.get("cancelled") + count);
                        break;
                    default:
                        // Nếu muốn gom trạng thái khác vào nhóm nào thì thêm ở đây
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
        if (endDate != null && endDate.length() == 10) {
            endDate += " 23:59:59";
        }
        String query = "SELECT c.contract_code, e.name as enterprise_name, c.start_date, c.end_date, cs.name as status "
                + "FROM contracts c "
                + "JOIN Enterprises e ON c.enterprise_id = e.id "
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

        // 1. Lấy TechnicalRequests với status (status là trạng thái maintenance gắn vào request)
        String requestsQuery
                = "SELECT tr.id, tr.request_code, tr.title, e.name AS enterprise_name, "
                + "CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) AS assigned_to_name, "
                + "tr.created_at, "
                + "s.status_name AS status "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "LEFT JOIN users u ON tr.assigned_to_id = u.id "
                + "LEFT JOIN MaintenanceSchedules ms ON tr.id = ms.technical_request_id "
                + "LEFT JOIN Statuses s ON ms.status_id = s.id "
                + "WHERE tr.is_deleted = 0 AND tr.created_at BETWEEN ? AND ? "
                + "ORDER BY tr.created_at DESC";

        Connection conn = null;
        PreparedStatement psRequests = null, psDevices = null, psSchedules = null, psAssignments = null;
        ResultSet rsRequests = null, rsDevices = null, rsSchedules = null, rsAssignments = null;
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
                request.put("assigned_staff", new ArrayList<Map<String, Object>>());
                requestMap.put(requestId, request);
                requestIds.add(requestId);
            }

            // 2. Lấy danh sách thiết bị cho từng request
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

            // 3. Lấy danh sách schedules cho từng request (để gom assigned_staff)
            if (!requestIds.isEmpty()) {
                String placeholders = requestIds.stream().map(id -> "?").collect(Collectors.joining(","));
                String scheduleQuery = "SELECT id, technical_request_id FROM MaintenanceSchedules "
                        + "WHERE technical_request_id IN (" + placeholders + ")";
                psSchedules = conn.prepareStatement(scheduleQuery);
                for (int i = 0; i < requestIds.size(); i++) {
                    psSchedules.setInt(i + 1, requestIds.get(i));
                }
                rsSchedules = psSchedules.executeQuery();
                // Map scheduleId -> requestId
                Map<Integer, Integer> scheduleRequestMap = new HashMap<>();
                List<Integer> scheduleIds = new ArrayList<>();
                while (rsSchedules.next()) {
                    int scheduleId = rsSchedules.getInt("id");
                    int reqId = rsSchedules.getInt("technical_request_id");
                    scheduleRequestMap.put(scheduleId, reqId);
                    scheduleIds.add(scheduleId);
                }

                // 4. Lấy list nhân viên phân công theo schedules
                if (!scheduleIds.isEmpty()) {
                    String schPlaceholders = scheduleIds.stream().map(id -> "?").collect(Collectors.joining(","));
                    String assignmentsQuery
                            = "SELECT ma.maintenance_schedule_id, u.id as user_id, "
                            + "CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) as user_name "
                            + "FROM MaintenanceAssignments ma "
                            + "JOIN Users u ON ma.user_id = u.id "
                            + "WHERE ma.maintenance_schedule_id IN (" + schPlaceholders + ")";
                    psAssignments = conn.prepareStatement(assignmentsQuery);
                    for (int i = 0; i < scheduleIds.size(); i++) {
                        psAssignments.setInt(i + 1, scheduleIds.get(i));
                    }
                    rsAssignments = psAssignments.executeQuery();
                    while (rsAssignments.next()) {
                        int scheduleId = rsAssignments.getInt("maintenance_schedule_id");
                        Integer reqId = scheduleRequestMap.get(scheduleId);
                        if (reqId != null && requestMap.get(reqId) != null) {
                            Map<String, Object> assignment = new HashMap<>();
                            assignment.put("user_id", rsAssignments.getInt("user_id"));
                            assignment.put("user_name", rsAssignments.getString("user_name"));

                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> staffList = (List<Map<String, Object>>) requestMap.get(reqId).get("assigned_staff");
                            staffList.add(assignment);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(null, psDevices, rsDevices);
            closeResources(null, psSchedules, rsSchedules);
            closeResources(null, psAssignments, rsAssignments);
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

    public static void main(String[] args) {
        ReportDAO dao = new ReportDAO();
        String startDate = "2025-08-01";
        String endDate = "2025-08-18";

        List<Map<String, Object>> contracts = dao.getContractsList(startDate, endDate);

        System.out.println("Danh sách hợp đồng từ " + startDate + " đến " + endDate + ":");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-12s %-25s %-12s %-12s %-18s\n", "Mã HĐ", "Khách hàng", "Ngày BĐ", "Ngày KT", "Trạng thái");

        for (Map<String, Object> contract : contracts) {
            String code = (String) contract.get("code");
            String enterpriseName = (String) contract.get("enterprise_name");
            java.sql.Date start = (java.sql.Date) contract.get("start_date");
            java.sql.Date end = (java.sql.Date) contract.get("end_date");
            String status = (String) contract.get("status");

            // Định dạng ngày cho dễ đọc
            String startDateStr = start != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(start) : "";
            String endDateStr = end != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(end) : "";

            System.out.printf("%-12s %-25s %-12s %-12s %-18s\n", code, enterpriseName, startDateStr, endDateStr, status);
        }

        if (contracts.isEmpty()) {
            System.out.println("Không có hợp đồng nào trong khoảng thời gian này!");
        }
    }
}
