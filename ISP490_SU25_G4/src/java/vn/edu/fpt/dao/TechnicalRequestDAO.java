package vn.edu.fpt.dao;

import java.security.Timestamp;
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
import vn.edu.fpt.model.MaintenanceSchedule;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TechnicalRequestDAO {

    public List<TechnicalRequest> getFilteredTechnicalRequests(String query, String status, int serviceId, int limit, int offset) throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT tr.*, e.name as enterpriseName, c.contract_code as contractCode, s.name as serviceName, "
                + "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN Contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN Users assignee ON tr.assigned_to_id = assignee.id "
                + "WHERE tr.is_deleted = 0"
        );

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (tr.request_code LIKE ? OR e.name LIKE ?)");
            params.add("%" + query.trim() + "%");
            params.add("%" + query.trim() + "%");
        }
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            sql.append(" AND tr.status = ?");
            params.add(status);
        }
        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        sql.append(" ORDER BY tr.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        // ... phần try-catch thực thi query giữ nguyên ...
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        }
        return requests;
    }

    public TechnicalRequest getTechnicalRequestById(int id) throws SQLException {
        TechnicalRequest req = null;

        String sql = "SELECT tr.*, e.name as enterpriseName, e.business_email as enterpriseEmail, c.contract_code as contractCode, s.name as serviceName, "
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

                    req.setEnterpriseId(rs.getInt("enterprise_id"));
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
                    req.setEnterpriseEmail(rs.getString("enterpriseEmail"));
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

    public List<TechnicalRequest> getAllTechnicalRequestsIdAndTitle() throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        String sql = "SELECT tr.id, tr.title " // Lấy đúng trường 'title' từ bảng TechnicalRequests
                + "FROM TechnicalRequests tr ";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicalRequest req = new TechnicalRequest();
                    req.setId(rs.getInt("id"));
                    req.setTitle(rs.getString("title"));
                    requests.add(req);
                }
            }
        }
        return requests;
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
        String sql = "SELECT id, last_name, middle_name, first_name FROM Users WHERE is_deleted = 0 AND role_id = 1";
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

    public Integer createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
    Connection conn = null;

    // Đã sửa: Thêm ? thứ 13 cho created_at
    String sqlRequest = """
    INSERT INTO TechnicalRequests (
      request_code, enterprise_id, contract_id, service_id,
      title, description, priority, `status`, reporter_id, assigned_to_id, 
      is_billable, estimated_cost, created_at
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

    String sqlDevice = """
    INSERT INTO TechnicalRequestDevices (
      technical_request_id, device_name, serial_number, problem_description
    ) VALUES (?, ?, ?, ?)
    """;

    try {
        conn = DBContext.getConnection();
        conn.setAutoCommit(false);
        int newRequestId;

        // ---- Insert TechnicalRequest ----
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

            // status dạng String
            String status = (request.getStatus() != null && !request.getStatus().isBlank())
                    ? request.getStatus().trim()
                    : "Mới tạo";
            psRequest.setString(8, status);

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

            try (ResultSet generatedKeys = psRequest.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newRequestId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Không lấy được ID yêu cầu.");
                }
            }
        }

        // ---- Insert danh sách thiết bị ----
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
        return newRequestId; // Trả về ID vừa tạo

    } catch (SQLException e) {
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
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

    public boolean updateTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        // Đổi status_id -> `status` (string)
        String sqlUpdateTicket = """
    UPDATE TechnicalRequests
    SET 
      enterprise_id = ?, 
      contract_id   = ?, 
      service_id    = ?, 
      title         = ?, 
      description   = ?, 
      priority      = ?, 
      `status`      = ?, 
      is_billable   = ?, 
      estimated_cost= ?,
      assigned_to_id= ?          -- thêm cột này
    WHERE id = ?
""";

        String sqlDeleteDevices = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";

        String sqlInsertDevice = """
    INSERT INTO TechnicalRequestDevices (
        technical_request_id, device_name, serial_number, problem_description
    ) VALUES (?, ?, ?, ?)
""";

// Đồng bộ assigned theo schedule
        String sqlGetSchedule = "SELECT id FROM MaintenanceSchedules WHERE technical_request_id = ?";
        String sqlDeleteAssignments = "DELETE FROM MaintenanceAssignments WHERE maintenance_schedule_id = ?";
        String sqlInsertAssignment = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // 1) Update TechnicalRequests (có assigned_to_id)
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateTicket)) {
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

                String status = (request.getStatus() != null && !request.getStatus().isBlank())
                        ? request.getStatus().trim()
                        : "Mới tạo";
                psUpdate.setString(7, status);
                psUpdate.setBoolean(8, request.isIsBillable());
                // estimated_cost có thể null? Nếu có, dùng setObject với Types.DECIMAL
                psUpdate.setDouble(9, request.getEstimatedCost());
                // assigned_to_id (có thể null)
                if (request.getAssignedToId() == null) {
                    psUpdate.setNull(10, Types.INTEGER);
                } else {
                    psUpdate.setInt(10, request.getAssignedToId());
                }

                psUpdate.setInt(11, request.getId()); // WHERE id = ?
                psUpdate.executeUpdate();
            }

            // 2) Xóa devices cũ
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDevices)) {
                psDelete.setInt(1, request.getId());
                psDelete.executeUpdate();
            }

            // 3) Thêm devices mới
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

            // 4) Đồng bộ assigned nhân viên vào MaintenanceAssignments (nếu có schedule)
            List<Integer> assignedUserIds = request.getAssignedUserIds();
            if (assignedUserIds != null) {
                int scheduleId = -1;

                // 4.1 Lấy schedule ID theo technical_request_id
                try (PreparedStatement psGetSchedule = conn.prepareStatement(sqlGetSchedule)) {
                    psGetSchedule.setInt(1, request.getId());
                    try (ResultSet rs = psGetSchedule.executeQuery()) {
                        if (rs.next()) {
                            scheduleId = rs.getInt("id");
                        }
                    }
                }

                if (scheduleId > 0) {
                    // 4.2 Xóa assignments cũ
                    try (PreparedStatement psDelAssign = conn.prepareStatement(sqlDeleteAssignments)) {
                        psDelAssign.setInt(1, scheduleId);
                        psDelAssign.executeUpdate();
                    }

                    // 4.3 Thêm assignments mới
                    if (!assignedUserIds.isEmpty()) {
                        try (PreparedStatement psInsAssign = conn.prepareStatement(sqlInsertAssignment)) {
                            for (Integer userId : assignedUserIds) {
                                if (userId == null) {
                                    continue;
                                }
                                psInsAssign.setInt(1, scheduleId);
                                psInsAssign.setInt(2, userId);
                                psInsAssign.addBatch();
                            }
                            psInsAssign.executeBatch();
                        }
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

    public List<String> getDistinctStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT DISTINCT status FROM TechnicalRequests WHERE is_deleted = 0 ORDER BY status";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                statuses.add(rs.getString("status"));
            }
        }
        return statuses;
    }

    public boolean updateTechnicalRequestTitleAndDesc(TechnicalRequest request) {
        final String SQL = """
        UPDATE TechnicalRequests
        SET title       = ?,
            description = ?
        WHERE id = ?
        """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, request.getTitle());
            ps.setString(2, request.getDescription());
            ps.setInt(3, request.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public MaintenanceSchedule getScheduleByTechnicalRequestId(int technicalRequestId) throws SQLException {
        final String sql = """
    SELECT
      ms.id,
      ms.technical_request_id,
      ms.campaign_id,
      ms.color,
      ms.scheduled_date,
      ms.end_date,
      ms.start_time,
      ms.end_time,
      ms.address_id,
      ms.status_id,
      s.status_name AS status_name,
      a.street_address AS street_address,
      a.ward_id      AS ward_id,
      a.district_id  AS district_id,
      a.province_id  AS province_id,
      ma.user_id     AS assigned_user_id,
      ms.created_at,
      ms.updated_at
    FROM MaintenanceSchedules ms
    LEFT JOIN Statuses   s ON s.id = ms.status_id
    LEFT JOIN Addresses  a ON a.id = ms.address_id
    LEFT JOIN MaintenanceAssignments ma ON ma.maintenance_schedule_id = ms.id
    WHERE ms.technical_request_id = ?
    ORDER BY ms.scheduled_date, ms.start_time
    LIMIT 1
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicalRequestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                MaintenanceSchedule ms = new MaintenanceSchedule();

                // PK & FK
                ms.setId(rs.getInt("id"));
                ms.setTechnicalRequestId((Integer) rs.getObject("technical_request_id"));
                ms.setCampaignId((Integer) rs.getObject("campaign_id"));
                ms.setAddressId((Integer) rs.getObject("address_id"));
                ms.setStatusId((Integer) rs.getObject("status_id"));

                // Trạng thái, màu
                ms.setColor(rs.getString("color"));
                ms.setStatusName(rs.getString("status_name"));

                // Địa chỉ chi tiết
                ms.setStreetAddress(rs.getString("street_address"));
                ms.setWardId((Integer) rs.getObject("ward_id"));
                ms.setDistrictId((Integer) rs.getObject("district_id"));
                ms.setProvinceId((Integer) rs.getObject("province_id"));
                ms.setAssignedUserId((Integer) rs.getObject("assigned_user_id"));
                // Ngày (LocalDate) với fallback
                try {
                    ms.setScheduledDate(rs.getObject("scheduled_date", java.time.LocalDate.class));
                } catch (Throwable t) {
                    java.sql.Date d = rs.getDate("scheduled_date");
                    ms.setScheduledDate(d != null ? d.toLocalDate() : null);
                }
                try {
                    ms.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
                } catch (Throwable t) {
                    java.sql.Date d = rs.getDate("end_date");
                    ms.setEndDate(d != null ? d.toLocalDate() : null);
                }

                // Giờ (LocalTime) với fallback
                try {
                    ms.setStartTime(rs.getObject("start_time", java.time.LocalTime.class));
                } catch (Throwable t) {
                    java.sql.Time tm = rs.getTime("start_time");
                    ms.setStartTime(tm != null ? tm.toLocalTime() : null);
                }
                try {
                    ms.setEndTime(rs.getObject("end_time", java.time.LocalTime.class));
                } catch (Throwable t) {
                    java.sql.Time tm = rs.getTime("end_time");
                    ms.setEndTime(tm != null ? tm.toLocalTime() : null);
                }

                // Dấu thời gian (LocalDateTime) với fallback
                try {
                    ms.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                } catch (Throwable t) {
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    ms.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                }
                try {
                    ms.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));
                } catch (Throwable t) {
                    java.sql.Timestamp ts = rs.getTimestamp("updated_at");
                    ms.setUpdatedAt(ts != null ? ts.toLocalDateTime() : null);
                }

                return ms;
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

    public int getTotalFilteredRequestCount(String query, String status, int serviceId) throws SQLException {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM TechnicalRequests tr JOIN Enterprises e ON tr.enterprise_id = e.id WHERE tr.is_deleted = 0");

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (tr.request_code LIKE ? OR e.name LIKE ?)");
            params.add("%" + query.trim() + "%");
            params.add("%" + query.trim() + "%");
        }
        if (status != null && !status.isEmpty() && !status.equals("all")) {
            sql.append(" AND tr.status = ?");
            params.add(status);
        }
        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        // ... phần try-catch thực thi query giữ nguyên ...
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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
                Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public static void main(String[] args) throws SQLException {

        TechnicalRequestDAO dao = new TechnicalRequestDAO();

        // 2. Tạo đối tượng TechnicalRequest với dữ liệu mới để cập nhật
        TechnicalRequest testRequest = new TechnicalRequest();

        // ❗ QUAN TRỌNG: Thay đổi số 1 thành một ID thực sự tồn tại trong bảng TechnicalRequests của bạn
        testRequest.setId(1);

        // Tạo title và description mới để dễ dàng nhận ra sự thay đổi
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy"));
        testRequest.setTitle("Tiêu đề được cập nhật @ " + currentTime);
        testRequest.setDescription("Mô tả mới được cập nhật từ phương thức main.");

        // 3. Gọi hàm cần kiểm tra
        System.out.println("Đang thực hiện cập nhật cho request có ID = " + testRequest.getId());
        boolean isSuccess = dao.updateTechnicalRequestTitleAndDesc(testRequest);

        // 4. In kết quả ra màn hình
        if (isSuccess) {
            System.out.println("✅ Cập nhật thành công!");
            System.out.println("Vui lòng kiểm tra lại cơ sở dữ liệu để xác nhận thay đổi.");
        } else {
            System.out.println("❌ Cập nhật thất bại. Có thể ID không tồn tại hoặc có lỗi kết nối DB.");
        }
        // Case 5 (nếu DB của bạn có cột tr.status dạng chuỗi):
        // runCase(dao, "Lọc theo status='pending'", null, "pending", 0, 10, 0);
        // Nếu schema dùng status_id + bảng Statuses, hãy sửa DAO để join theo status_name rồi mới test.
    }

    private static void runCase(TechnicalRequestDAO dao, String title,
            String query, String status, int serviceId,
            int limit, int offset) {
        System.out.println("\n===== " + title + " =====");
        try {
            List<TechnicalRequest> list
                    = dao.getFilteredTechnicalRequests(query, status, serviceId, limit, offset);
            print(list);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void print(List<TechnicalRequest> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("(Không có dữ liệu)");
            return;
        }

        // Header
        System.out.printf("%-4s | %-10s | %-12s | %-22s | %-22s | %-22s | %-10s | %-19s%n",
                "ID", "Code", "Status", "Enterprise", "Service", "Assignee", "Cost", "CreatedAt");
        System.out.println("-----+------------+--------------+------------------------+------------------------+------------------------+------------+---------------------");

        // Rows
        for (TechnicalRequest r : items) {
            System.out.printf("%-4d | %-10s | %-12s | %-22s | %-22s | %-22s | %-10.2f | %-19s%n",
                    r.getId(),
                    nullToEmpty(r.getRequestCode()),
                    nullToEmpty(r.getStatus()),
                    nullToEmpty(r.getEnterpriseName()),
                    nullToEmpty(r.getServiceName()),
                    nullToEmpty(r.getAssignedToName()),
                    r.getEstimatedCost(),
                    r.getCreatedAt() == null ? "" : r.getCreatedAt().toString()
            );
        }
    }

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
}
