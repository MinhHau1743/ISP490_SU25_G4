/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Service;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice;
import vn.edu.fpt.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author minh
 */
public class TechnicalRequestDAO {

    /**
     * Lấy tất cả các yêu cầu kỹ thuật để hiển thị trên danh sách. Phương thức
     * này join nhiều bảng để lấy thông tin tên thay vì chỉ ID.
     *
     * @return Danh sách các đối tượng TechnicalRequest.
     */
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
                // Chú ý: Đảm bảo model TechnicalRequest dùng java.sql.Timestamp
                req.setCreatedAt(rs.getTimestamp("created_at"));

                // Gán các tên đã JOIN được
                req.setEnterpriseName(rs.getString("enterpriseName"));
                req.setContractCode(rs.getString("contractCode"));
                req.setServiceName(rs.getString("serviceName"));
                req.setAssignedToName(rs.getString("assignedToName"));

                requests.add(req);
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return requests;
    }

    /**
     * Tạo một yêu cầu kỹ thuật mới cùng các thiết bị liên quan trong một
     * transaction.
     *
     * @param request Đối tượng TechnicalRequest chứa thông tin chính.
     * @param devices Danh sách các thiết bị liên quan.
     * @return true nếu tạo thành công, false nếu có lỗi.
     */
    public boolean createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        String sqlRequest = "INSERT INTO TechnicalRequests (request_code, enterprise_id, contract_id, service_id, title, description, priority, status, reporter_id, assigned_to_id, is_billable, estimated_cost, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Insert vào bảng TechnicalRequests
            PreparedStatement psRequest = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS);
            psRequest.setString(1, "REQ-" + System.currentTimeMillis()); // Tạo mã yêu cầu tạm thời
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
            psRequest.setString(8, "new"); // Mặc định trạng thái là 'Mới'
            psRequest.setInt(9, request.getReporterId());
            if (request.getAssignedToId() != null) {
                psRequest.setInt(10, request.getAssignedToId());
            } else {
                psRequest.setNull(10, Types.INTEGER);
            }
            psRequest.setBoolean(11, request.isIsBillable()); // Sử dụng isIsBillable() theo model của bạn
            psRequest.setDouble(12, request.getEstimatedCost());
            psRequest.setTimestamp(13, new java.sql.Timestamp(System.currentTimeMillis())); // Dùng java.sql.Timestamp

            if (psRequest.executeUpdate() == 0) {
                throw new SQLException("Tạo yêu cầu thất bại, không có dòng nào được thêm.");
            }

            // Lấy ID của yêu cầu vừa được tạo
            int newRequestId;
            try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newRequestId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Tạo yêu cầu thất bại, không lấy được ID.");
                }
            }

            // 2. Insert các thiết bị liên quan (nếu có)
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

            conn.commit(); // Lưu tất cả thay đổi nếu không có lỗi
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác tất cả thay đổi nếu có lỗi
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

    // --- CÁC HÀM TIỆN ÍCH LẤY DỮ LIỆU CHO FORM ---
    /**
     * Lấy tất cả Doanh nghiệp để đổ vào dropdown
     */
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

    /**
     * Lấy danh sách tất cả nhân viên kỹ thuật đang hoạt động. Dùng để đổ dữ
     * liệu vào dropdown "Gán cho nhân viên".
     */
    public List<User> getAllTechnicians() {
        List<User> list = new ArrayList<>();

        // === THAY ĐỔI QUAN TRỌNG Ở ĐÂY ===
        // Thay vì tìm theo tên "Kỹ thuật", ta tìm trực tiếp theo role_id = 5.
        // Cách này an toàn và ổn định hơn, tránh các lỗi về encoding.
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
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return list;
    }

    /**
     * Lấy ID hợp đồng từ mã hợp đồng
     */
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

    /**
     * Lấy tất cả dịch vụ để đổ vào dropdown
     */
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT id, name FROM Services ORDER BY name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Yêu cầu bạn phải tạo model: vn.edu.fpt.model.Service
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
    
    
}
