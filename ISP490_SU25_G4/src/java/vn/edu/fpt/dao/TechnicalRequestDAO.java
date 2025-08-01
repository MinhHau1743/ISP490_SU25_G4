package vn.edu.fpt.dao;

import vn.edu.fpt.model.Contract;
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
import vn.edu.fpt.model.ContractProduct;

public class TechnicalRequestDAO {

    public List<TechnicalRequest> getAllTechnicalRequests(int limit, int offset) throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        String sql = "SELECT tr.*, e.name as enterpriseName, c.contract_code as contractCode, s.name as serviceName, "
                + "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN Contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN Users assignee ON tr.assigned_to_id = assignee.id "
                + "WHERE tr.is_deleted = 0 "
                + "ORDER BY tr.created_at DESC "
                + "LIMIT ? OFFSET ?"; // <-- THÊM DÒNG NÀY

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);   // <-- THÊM DÒNG NÀY
            ps.setInt(2, offset);  // <-- THÊM DÒNG NÀY

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicalRequest req = new TechnicalRequest();
                    // ... phần code gán giá trị giữ nguyên ...
                    req.setId(rs.getInt("id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setStatus(rs.getString("status"));
                    req.setIsBillable(rs.getBoolean("is_billable"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setEnterpriseName(rs.getString("enterpriseName"));
                    req.setContractCode(rs.getString("contractCode"));
                    req.setServiceName(rs.getString("serviceName"));
                    req.setAssignedToName(rs.getString("assignedToName"));
                    req.setEstimatedCost(rs.getDouble("estimated_cost"));
                    requests.add(req);
                }
            }
        }
        return requests;
    }

    public TechnicalRequest getTechnicalRequestById(int id) throws SQLException {
        TechnicalRequest req = null;
        String sql = "SELECT tr.*, e.name as enterpriseName, c.contract_code as contractCode, s.name as serviceName, "
                + "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName, "
                + "CONCAT(reporter.last_name, ' ', reporter.middle_name, ' ', reporter.first_name) as reporterName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "JOIN Users reporter ON tr.reporter_id = reporter.id "
                + "LEFT JOIN Contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN Users assignee ON tr.assigned_to_id = assignee.id "
                + "WHERE tr.id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    req = new TechnicalRequest();
                    req.setId(rs.getInt("id"));
                    req.setRequestCode(rs.getString("request_code"));
                    req.setTitle(rs.getString("title"));
                    req.setDescription(rs.getString("description"));
                    req.setPriority(rs.getString("priority"));
                    req.setStatus(rs.getString("status"));
                    req.setIsBillable(rs.getBoolean("is_billable"));
                    req.setEstimatedCost(rs.getDouble("estimated_cost"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setResolvedAt(rs.getTimestamp("resolved_at"));
                    req.setEnterpriseName(rs.getString("enterpriseName"));
                    req.setContractCode(rs.getString("contractCode"));
                    req.setServiceName(rs.getString("serviceName"));
                    req.setAssignedToName(rs.getString("assignedToName"));
                    req.setReporterName(rs.getString("reporterName"));
                    req.setDevices(getDevicesForRequest(id));
                }
            }
        }
        return req;
    }

    private List<TechnicalRequestDevice> getDevicesForRequest(int requestId) throws SQLException {
        List<TechnicalRequestDevice> devices = new ArrayList<>();
        String sql = "SELECT * FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public List<Enterprise> getAllEnterprises() throws SQLException {
        List<Enterprise> list = new ArrayList<>();
        String sql = "SELECT id, name FROM enterprises WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

                System.out.println("DAO ĐANG LẤY: ID=" + rs.getInt("id") + ", Name=" + rs.getString("name"));
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
        String sql = "SELECT id, last_name, middle_name, first_name FROM Users WHERE status = 'active' AND role_id = 5";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                services.add(s);
            }
        }
        return services;
    }

    public boolean createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        String sqlRequest = "INSERT INTO TechnicalRequests (request_code, enterprise_id, contract_id, service_id, title, description, priority, status, reporter_id, assigned_to_id, is_billable, estimated_cost, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
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
                psRequest.setString(8, "new");
                psRequest.setInt(9, request.getReporterId());
                if (request.getAssignedToId() != null) {
                    psRequest.setInt(10, request.getAssignedToId());
                } else {
                    psRequest.setNull(10, Types.INTEGER);
                }
                psRequest.setBoolean(11, request.isIsBillable());
                psRequest.setDouble(12, request.getEstimatedCost());
                psRequest.setTimestamp(13, new java.sql.Timestamp(System.currentTimeMillis()));

                if (psRequest.executeUpdate() == 0) {
                    throw new SQLException("Tạo yêu cầu thất bại.");
                }

                int newRequestId;
                try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newRequestId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Không lấy được ID yêu cầu.");
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
            }
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

    public List<Contract> getAllActiveContracts() throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT id, contract_code FROM contracts WHERE status = 'active' ORDER BY created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contract c = new Contract();
                c.setId(rs.getInt("id"));
                c.setContractCode(rs.getString("contract_code"));
                contracts.add(c);
            }
        }
        return contracts;
    }

    public List<Contract> getActiveContractsByEnterpriseId(int enterpriseId) throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT id, contract_code FROM contracts WHERE enterprise_id = ? AND status = 'active' ORDER BY created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId); // Gán enterpriseId vào câu lệnh SQL

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract c = new Contract();
                    c.setId(rs.getInt("id"));
                    c.setContractCode(rs.getString("contract_code"));
                    contracts.add(c);
                }
            }
        }
        return contracts;
    }

    public boolean updateTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) throws SQLException {
        // Câu lệnh SQL UPDATE đã được cập nhật đầy đủ các trường
        String sqlUpdateTicket = "UPDATE TechnicalRequests SET enterprise_id=?, service_id=?, assigned_to_id=?, "
                + "title=?, description=?, priority=?, status=?, is_billable=?, estimated_cost=? "
                + "WHERE id=?";
        String sqlDeleteDevices = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        String sqlInsertDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật thông tin phiếu chính
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateTicket)) {
                psUpdate.setInt(1, request.getEnterpriseId());
                psUpdate.setInt(2, request.getServiceId());
                psUpdate.setInt(3, request.getAssignedToId());
                psUpdate.setString(4, request.getTitle());
                psUpdate.setString(5, request.getDescription());
                psUpdate.setString(6, request.getPriority());
                psUpdate.setString(7, request.getStatus());
                psUpdate.setBoolean(8, request.isIsBillable());
                psUpdate.setDouble(9, request.getEstimatedCost());
                psUpdate.setInt(10, request.getId()); // ID của ticket cần update
                psUpdate.executeUpdate();
            }

            // 2. Xóa tất cả thiết bị cũ của phiếu này
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDevices)) {
                psDelete.setInt(1, request.getId());
                psDelete.executeUpdate();
            }

            // 3. Thêm lại danh sách thiết bị mới (nếu có)
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

            conn.commit(); // Hoàn tất transaction
            return true;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // Hoàn tác nếu có lỗi
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

    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        // Câu lệnh SQL giả định bảng sản phẩm của bạn tên là 'products'
        String sql = "SELECT id, name FROM products WHERE is_deleted = 0 ORDER BY name ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                productList.add(product);
            }
        }
        return productList;
    }

    public boolean deleteTechnicalRequest(int requestId) throws SQLException {
        String sql = "UPDATE TechnicalRequests SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public int getTotalTechnicalRequestCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TechnicalRequests WHERE is_deleted = 0";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<TechnicalRequest> getRecentRequestsByEnterprise(int enterpriseId, int limit) throws Exception {
    List<TechnicalRequest> list = new ArrayList<>();

    String sql = """
        SELECT tr.id, tr.request_code, tr.enterprise_id, tr.service_id, tr.title, tr.description,
               tr.priority, tr.status, tr.reporter_id, s.name AS service_name
        FROM technicalrequests tr
        JOIN services s ON tr.service_id = s.id
        WHERE tr.enterprise_id = ?
        ORDER BY tr.id DESC
        LIMIT ?
    """;

    try (
        Connection conn = DBContext.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)
    ) {
        stmt.setInt(1, enterpriseId);
        stmt.setInt(2, limit);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            TechnicalRequest req = new TechnicalRequest();
            req.setId(rs.getInt("id"));
            req.setRequestCode(rs.getString("request_code"));
            req.setEnterpriseId(rs.getInt("enterprise_id"));
            req.setServiceId(rs.getInt("service_id"));
            req.setTitle(rs.getString("title"));
            req.setDescription(rs.getString("description"));
            req.setPriority(rs.getString("priority"));
            req.setStatus(rs.getString("status"));
            req.setReporterId(rs.getInt("reporter_id"));
            req.setServiceName(rs.getString("service_name")); // dùng JOIN lấy tên dịch vụ

            list.add(req);
        }

    } catch (Exception e) {
        e.printStackTrace();
        throw new Exception("Lỗi truy vấn recent requests: " + e.getMessage());
    }

    return list;
}

}
