package vn.edu.fpt.dao;

import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Service;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO quản lý các truy vấn liên quan đến Yêu cầu Kỹ thuật. Phiên bản này đã
 * được rà soát và sửa lại toàn bộ dựa trên cấu trúc database chính xác.
 *
 * @author AI Assistant
 */
public class TechnicalRequestDAO extends DBContext {

    public List<TechnicalRequest> getFilteredTechnicalRequests(String query, String status, int serviceId, int limit, int offset) throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT tr.*, "
                + "      e.name AS enterpriseName, "
                + "      c.contract_code AS contractCode, "
                + "      s.name AS serviceName, "
                + "      tr.status AS statusName, " // Lấy trực tiếp từ cột status
                + "      GROUP_CONCAT(DISTINCT CONCAT(u.last_name, ' ', u.middle_name, ' ', u.first_name) SEPARATOR ', ') AS assignedToNames "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN MaintenanceSchedules ms ON ms.technical_request_id = tr.id "
                + "LEFT JOIN MaintenanceAssignments ma ON ma.maintenance_schedule_id = ms.id "
                + "LEFT JOIN Users u ON ma.user_id = u.id "
                + "WHERE tr.is_deleted = 0 "
        );

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (tr.request_code LIKE ? OR e.name LIKE ?)");
            String q = "%" + query.trim() + "%";
            params.add(q);
            params.add(q);
        }

        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status.trim())) {
            sql.append(" AND tr.status = ?"); // Lọc trực tiếp trên cột status
            params.add(status.trim());
        }

        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        sql.append(" GROUP BY tr.id ");
        sql.append(" ORDER BY tr.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToTechnicalRequest(rs));
                }
            }
        }
        return requests;
    }

    public TechnicalRequest getTechnicalRequestById(int id) throws SQLException {
        TechnicalRequest req = null;
        String sql = """
            SELECT tr.*, 
                   e.name AS enterpriseName,
                   c.contract_code AS contractCode,
                   s.name AS serviceName,
                   tr.status AS statusName, 
                   GROUP_CONCAT(DISTINCT CONCAT(u.last_name, ' ', u.middle_name, ' ', u.first_name) SEPARATOR ', ') AS assignedToNames,
                   CONCAT(reporter.last_name, ' ', reporter.middle_name, ' ', reporter.first_name) AS reporterName
            FROM TechnicalRequests tr
            JOIN Enterprises e ON tr.enterprise_id = e.id
            JOIN Services s ON tr.service_id = s.id
            JOIN Users reporter ON tr.reporter_id = reporter.id
            LEFT JOIN contracts c ON tr.contract_id = c.id
            LEFT JOIN MaintenanceSchedules ms ON ms.technical_request_id = tr.id
            LEFT JOIN MaintenanceAssignments ma ON ma.maintenance_schedule_id = ms.id
            LEFT JOIN Users u ON ma.user_id = u.id
            WHERE tr.id = ? AND tr.is_deleted = 0
            GROUP BY tr.id
        """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    req = mapResultSetToTechnicalRequest(rs);
                    // Lấy thêm danh sách thiết bị
                    if (req != null) {
                        req.setDevices(getDevicesForRequest(id));
                    }
                }
            }
        }
        return req;
    }

    public boolean createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        String sqlRequest = "INSERT INTO TechnicalRequests (request_code, enterprise_id, contract_id, service_id, title, description, priority, status, reporter_id, is_billable, estimated_cost, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int newRequestId;
            try (PreparedStatement psRequest = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                psRequest.setString(1, "REQ-" + System.currentTimeMillis());
                psRequest.setInt(2, request.getEnterpriseId());
                if (request.getContractId() != null) {
                    psRequest.setInt(3, request.getContractId());
                } else {
                    psRequest.setNull(3, Types.INTEGER);
                }
                psRequest.setInt(4, request.getServiceId());
                psRequest.setString(5, request.getTitle());
                psRequest.setString(6, request.getDescription());
                psRequest.setString(7, request.getPriority());
                psRequest.setString(8, request.getStatus() != null ? request.getStatus() : "new"); // 'new' là giá trị mặc định trong DB
                psRequest.setInt(9, request.getReporterId());
                psRequest.setBoolean(10, request.isIsBillable());
                psRequest.setDouble(11, request.getEstimatedCost());
                psRequest.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));

                psRequest.executeUpdate();
                try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newRequestId = generatedKeys.getInt(1);
                        request.setId(newRequestId); // Cập nhật ID cho đối tượng request
                    } else {
                        throw new SQLException("Không lấy được ID yêu cầu.");
                    }
                }
            }

            if (devices != null && !devices.isEmpty()) {
                try (PreparedStatement psDevice = conn.prepareStatement(sqlDevice)) {
                    for (TechnicalRequestDevice device : devices) {
                        psDevice.setInt(1, newRequestId);
                        psDevice.setString(2, device.getDeviceName());
                        psDevice.setString(3, device.getSerialNumber());
                        psDevice.setString(4, device.getProblemDescription());
                        psDevice.addBatch();
                    }
                    psDevice.executeBatch();
                }
            }

            // Logic tạo schedule và gán nhân viên nên được xử lý ở service/controller
            // Hoặc nếu muốn giữ ở đây thì cần truyền thêm thông tin
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) throws SQLException {
        String sqlUpdateTicket = "UPDATE TechnicalRequests SET enterprise_id=?, service_id=?, contract_id=?, title=?, description=?, priority=?, status=?, is_billable=?, estimated_cost=? WHERE id=?";
        String sqlDeleteDevices = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        String sqlInsertDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateTicket)) {
                psUpdate.setInt(1, request.getEnterpriseId());
                psUpdate.setInt(2, request.getServiceId());
                if (request.getContractId() != null) {
                    psUpdate.setInt(3, request.getContractId());
                } else {
                    psUpdate.setNull(3, Types.INTEGER);
                }
                psUpdate.setString(4, request.getTitle());
                psUpdate.setString(5, request.getDescription());
                psUpdate.setString(6, request.getPriority());
                psUpdate.setString(7, request.getStatus());
                psUpdate.setBoolean(8, request.isIsBillable());
                psUpdate.setDouble(9, request.getEstimatedCost());
                psUpdate.setInt(10, request.getId());
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDevices)) {
                psDelete.setInt(1, request.getId());
                psDelete.executeUpdate();
            }

            if (devices != null && !devices.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsertDevice)) {
                    for (TechnicalRequestDevice device : devices) {
                        psInsert.setInt(1, request.getId());
                        psInsert.setString(2, device.getDeviceName());
                        psInsert.setString(3, device.getSerialNumber());
                        psInsert.setString(4, device.getProblemDescription());
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public boolean deleteTechnicalRequest(int requestId) throws SQLException {
        String sql = "UPDATE TechnicalRequests SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTotalFilteredRequestCount(String query, String status, int serviceId) throws SQLException {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT tr.id) FROM TechnicalRequests tr JOIN Enterprises e ON tr.enterprise_id = e.id WHERE tr.is_deleted = 0");

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (tr.request_code LIKE ? OR e.name LIKE ?)");
            params.add("%" + query.trim() + "%");
            params.add("%" + query.trim() + "%");
        }
        if (status != null && !status.isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND tr.status = ?");
            params.add(status);
        }
        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<String> getDistinctStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT DISTINCT status FROM TechnicalRequests WHERE is_deleted = 0 AND status IS NOT NULL ORDER BY status";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                statuses.add(rs.getString("status"));
            }
        }
        return statuses;
    }

    // --- CÁC PHƯƠNG THỨC PHỤ TRỢ (HELPER METHODS) ---
    public List<Enterprise> getAllEnterprises() throws SQLException {
        List<Enterprise> list = new ArrayList<>();
        String sql = "SELECT id, name FROM enterprises WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Enterprise e = new Enterprise();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                list.add(e);
            }
        }
        return list;
    }

    public List<User> getAllTechnicians() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, last_name, middle_name, first_name FROM Users WHERE is_deleted = 0 AND role_id = 5"; // Role Kỹ thuật
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setLastName(rs.getString("last_name"));
                u.setMiddleName(rs.getString("middle_name"));
                u.setFirstName(rs.getString("first_name"));
                list.add(u);
            }
        }
        return list;
    }

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT id, name FROM Services ORDER BY name";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                services.add(s);
            }
        }
        return services;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT id, name FROM products WHERE is_deleted = 0 ORDER BY name ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                productList.add(product);
            }
        }
        return productList;
    }

    private List<TechnicalRequestDevice> getDevicesForRequest(int requestId) throws SQLException {
        List<TechnicalRequestDevice> devices = new ArrayList<>();
        String sql = "SELECT * FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicalRequestDevice device = new TechnicalRequestDevice();
                    device.setId(rs.getInt("id"));
                    device.setDeviceName(rs.getString("device_name"));
                    device.setSerialNumber(rs.getString("serial_number"));
                    device.setProblemDescription(rs.getString("problem_description"));
                    devices.add(device);
                }
            }
        }
        return devices;
    }

    private TechnicalRequest mapResultSetToTechnicalRequest(ResultSet rs) throws SQLException {
        TechnicalRequest req = new TechnicalRequest();
        req.setId(rs.getInt("id"));
        req.setEnterpriseId(rs.getInt("enterprise_id"));
        req.setContractId(rs.getObject("contract_id", Integer.class));
        req.setServiceId(rs.getInt("service_id"));
        req.setRequestCode(rs.getString("request_code"));
        req.setTitle(rs.getString("title"));
        req.setDescription(rs.getString("description"));
        req.setPriority(rs.getString("priority"));
        req.setStatus(rs.getString("status"));
        req.setReporterId(rs.getInt("reporter_id"));
        req.setIsBillable(rs.getBoolean("is_billable"));
        req.setEstimatedCost(rs.getDouble("estimated_cost"));
        req.setCreatedAt(rs.getTimestamp("created_at"));
        req.setResolvedAt(rs.getTimestamp("resolved_at"));

        if (hasColumn(rs, "enterpriseName")) {
            req.setEnterpriseName(rs.getString("enterpriseName"));
        }
        if (hasColumn(rs, "contractCode")) {
            req.setContractCode(rs.getString("contractCode"));
        }
        if (hasColumn(rs, "serviceName")) {
            req.setServiceName(rs.getString("serviceName"));
        }
        if (hasColumn(rs, "assignedToNames")) {
            req.setAssignedToNames(rs.getString("assignedToNames"));
        }
        if (hasColumn(rs, "reporterName")) {
            req.setReporterName(rs.getString("reporterName"));
        }

        return req;
    }

    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
