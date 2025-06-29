/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import vn.edu.fpt.model.Contract; // Import model mới
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Product;   // Import model mới
import vn.edu.fpt.model.Service;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice; // Import model mới
import vn.edu.fpt.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author minh
 */
public class TechnicalRequestDAO {

    public List<TechnicalRequest> getAllTechnicalRequests() {
        List<TechnicalRequest> requests = new ArrayList<>();
        String sql = "SELECT tr.*, e.name as enterpriseName, c.contract_code as contractCode, s.name as serviceName, "
                + "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN Contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN Users assignee ON tr.assigned_to_id = assignee.id "
                + "ORDER BY tr.created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TechnicalRequest req = new TechnicalRequest();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * SỬA LẠI: Tạo yêu cầu kỹ thuật mới và lưu các THIẾT BỊ NHẬP TAY.
     */
    public boolean createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        String sqlRequest = "INSERT INTO TechnicalRequests (request_code, enterprise_id, contract_id, service_id, title, description, priority, status, reporter_id, assigned_to_id, is_billable, estimated_cost, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement psRequest = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS);
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
            conn.commit();
            return true;
        } catch (Exception e) {
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

    /**
     * SỬA LẠI: Lấy thông tin chi tiết của một Yêu cầu, bao gồm cả các thiết bị.
     */
    public TechnicalRequest getTechnicalRequestById(int id) {
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

                    // ĐẢM BẢO LẤY ĐÚNG DỮ LIỆU NGÀY THÁNG
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setResolvedAt(rs.getTimestamp("resolved_at"));

                    req.setEnterpriseName(rs.getString("enterpriseName"));
                    req.setContractCode(rs.getString("contractCode"));
                    req.setServiceName(rs.getString("serviceName"));
                    req.setAssignedToName(rs.getString("assignedToName"));
                    req.setReporterName(rs.getString("reporterName"));

                    // Lấy danh sách thiết bị liên quan
                    req.setDevices(getDevicesForRequest(id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return req;
    }

    /**
     * SỬA LẠI: Phương thức phụ để lấy danh sách thiết bị của một yêu cầu.
     */
    private List<TechnicalRequestDevice> getDevicesForRequest(int requestId) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return devices;
    }

    // --- CÁC HÀM TIỆN ÍCH CŨ ---
    public List<Enterprise> getAllEnterprises() {
        List<Enterprise> list = new ArrayList<>();
        String sql = "SELECT id, name FROM Enterprises WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Enterprise e = new Enterprise();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                list.add(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> getAllTechnicians() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT id, name FROM Services ORDER BY name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                services.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return services;
    }

    public Integer getContractIdByCode(String contractCode) {
        if (contractCode == null || contractCode.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT id FROM Contracts WHERE contract_code = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contractCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        String sqlUpdateRequest = "UPDATE TechnicalRequests SET enterprise_id=?, contract_id=?, service_id=?, title=?, description=?, priority=?, status=?, assigned_to_id=?, is_billable=?, estimated_cost=? WHERE id=?";
        String sqlDeleteDevices = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        String sqlInsertDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Cập nhật thông tin chính của yêu cầu
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateRequest)) {
                psUpdate.setInt(1, request.getEnterpriseId());
                if (request.getContractId() != null) {
                    psUpdate.setInt(2, request.getContractId());
                } else {
                    psUpdate.setNull(2, Types.INTEGER);
                }
                psUpdate.setInt(3, request.getServiceId());
                psUpdate.setString(4, request.getTitle());
                psUpdate.setString(5, request.getDescription());
                psUpdate.setString(6, request.getPriority());
                psUpdate.setString(7, request.getStatus());
                if (request.getAssignedToId() != null) {
                    psUpdate.setInt(8, request.getAssignedToId());
                } else {
                    psUpdate.setNull(8, Types.INTEGER);
                }
                psUpdate.setBoolean(9, request.isIsBillable());
                psUpdate.setDouble(10, request.getEstimatedCost());
                psUpdate.setInt(11, request.getId()); // Điều kiện WHERE
                psUpdate.executeUpdate();
            }

            // 2. Xóa tất cả các thiết bị cũ liên quan đến yêu cầu này
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDevices)) {
                psDelete.setInt(1, request.getId());
                psDelete.executeUpdate();
            }

            // 3. Thêm lại danh sách thiết bị mới
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

            conn.commit(); // Lưu tất cả thay đổi
            return true;

        } catch (Exception e) {
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

    public boolean updateTicketAssignment(int ticketId, String status, String priority, int employeeId, boolean isBillable, double estimatedCost) {
        String sql = "UPDATE TechnicalRequests SET status = ?, priority = ?, assigned_to_id = ?, is_billable = ?, estimated_cost = ? WHERE id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, priority);
            ps.setInt(3, employeeId);
            ps.setBoolean(4, isBillable);
            ps.setDouble(5, estimatedCost);
            ps.setInt(6, ticketId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
