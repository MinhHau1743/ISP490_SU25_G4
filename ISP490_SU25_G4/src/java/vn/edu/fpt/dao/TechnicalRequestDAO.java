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
import vn.edu.fpt.model.Address;

public class TechnicalRequestDAO {

    // Trong file: TechnicalRequestDAO.java
    public List<TechnicalRequest> getFilteredTechnicalRequests(String query, String status, int serviceId, int limit, int offset) throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // === BẮT ĐẦU SỬA ĐỔI SQL ===
        StringBuilder sql = new StringBuilder(
                "SELECT tr.id, tr.request_code, tr.is_billable, tr.created_at, tr.estimated_cost, "
                + "e.name as enterpriseName, "
                + "c.contract_code as contractCode, "
                + "s.name as serviceName, "
                + "st.status_name as status, "
                + // Lấy tên status từ bảng Statuses
                "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + // JOIN thêm 2 bảng để lấy được status
                "LEFT JOIN MaintenanceSchedules ms ON tr.id = ms.technical_request_id "
                + "LEFT JOIN Statuses st ON ms.status_id = st.id "
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
            // Lọc theo tên status từ bảng Statuses
            sql.append(" AND st.status_name = ?");
            params.add(status);
        }
        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        sql.append(" ORDER BY tr.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        // === KẾT THÚC SỬA ĐỔI SQL ===

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicalRequest req = new TechnicalRequest();
                    req.setId(rs.getInt("id"));
                    req.setRequestCode(rs.getString("request_code"));
                    // Chú ý: rs.getString("status") bây giờ lấy từ st.status_name
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

    // Trong file: TechnicalRequestDAO.java
    public TechnicalRequest getTechnicalRequestById(int id) throws SQLException {
        TechnicalRequest req = null;

        String sql = "SELECT tr.*, e.name as enterpriseName, e.business_email as enterpriseEmail, "
                + "c.contract_code as contractCode, s.name as serviceName, "
                + "st.status_name as statusName, "
                + "CONCAT(assignee.last_name, ' ', assignee.middle_name, ' ', assignee.first_name) as assignedToName, "
                + "CONCAT(reporter.last_name, ' ', reporter.middle_name, ' ', reporter.first_name) as reporterName "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "JOIN Users reporter ON tr.reporter_id = reporter.id "
                + "LEFT JOIN MaintenanceSchedules ms ON tr.id = ms.technical_request_id "
                + "LEFT JOIN Statuses st ON ms.status_id = st.id "
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
                    req.setStatus(rs.getString("statusName"));
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

                    // === DÒNG CODE QUAN TRỌNG ĐƯỢC BỔ SUNG ===
                    // Lấy ID của hợp đồng và gán vào đối tượng ticket
                    req.setContractId(rs.getObject("contract_id", Integer.class));
                    // ===========================================
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

    // SỬA LẠI HÀM NÀY
    // TRONG TechnicalRequestDAO.java
    public List<User> getAllTechnicians() throws SQLException {
        List<User> list = new ArrayList<>();
        // Sửa lại: Thêm bí danh 'u.' vào trước cột 'id'
        String sql = "SELECT u.id, u.last_name, u.middle_name, u.first_name "
                + "FROM Users u JOIN Roles r ON u.role_id = r.id "
                + "WHERE u.is_deleted = 0 AND r.name = 'Kỹ thuật'";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setLastName(rs.getString("last_name"));
                u.setMiddleName(rs.getString("middle_name"));
                u.setFirstName(rs.getString("first_name"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra để dễ debug
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

    // Trong file: vn/edu/fpt/dao/TechnicalRequestDAO.java
    public Integer createTechnicalRequest(TechnicalRequest request, List<TechnicalRequestDevice> devices) {
        Connection conn = null;

        // ĐÃ SỬA: Xóa `status` và dấu `?` tương ứng khỏi câu lệnh INSERT
        String sqlRequest = """
        INSERT INTO TechnicalRequests (
            request_code, enterprise_id, contract_id, service_id,
            title, description, priority, reporter_id, assigned_to_id, 
            is_billable, estimated_cost, created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) 
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

                // ĐÃ BỎ: Dòng gán giá trị cho status (psRequest.setString(8, ...)) đã được xóa bỏ
                // Các tham số sau được dịch lên 1 bậc
                psRequest.setInt(8, request.getReporterId());
                if (request.getAssignedToId() != null) {
                    psRequest.setInt(9, request.getAssignedToId());
                } else {
                    psRequest.setNull(9, Types.INTEGER);
                }
                psRequest.setBoolean(10, request.isIsBillable());
                psRequest.setDouble(11, request.getEstimatedCost());
                psRequest.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));

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
            return null; // Trả về null nếu có lỗi
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

    // Trong file: TechnicalRequestDAO.java
    public List<Contract> getAllActiveContracts() throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        String sql = "SELECT c.id, c.contract_code, e.id as enterprise_id, e.name as enterprise_name "
                + "FROM contracts c "
                + "JOIN enterprises e ON c.enterprise_id = e.id "
                + "JOIN contract_statuses cs ON c.status_id = cs.id "
                // --- SỬA LẠI ĐIỀU KIỆN WHERE TẠI ĐÂY ---
                + "WHERE (cs.name = 'Đã hoàn thành' OR cs.name = 'Quá thời hạn') AND c.is_deleted = 0 "
                + "ORDER BY c.created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contract contract = new Contract();
                contract.setId(rs.getInt("id"));
                contract.setContractCode(rs.getString("contract_code"));

                Enterprise enterprise = new Enterprise();
                enterprise.setId(rs.getInt("enterprise_id"));
                enterprise.setName(rs.getString("enterprise_name"));
                contract.setEnterprise(enterprise);

                contracts.add(contract);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contracts;
    }

    public List<Contract> getActiveContractsByEnterpriseId(int enterpriseId) throws SQLException {
        List<Contract> contracts = new ArrayList<>();
        // Tương tự, sửa lại câu lệnh SQL để JOIN và lọc cho đúng.
        String sql = "SELECT c.id, c.contract_code "
                + "FROM contracts c "
                + "JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.enterprise_id = ? AND cs.name = 'Đang triển khai' AND c.is_deleted = 0 "
                + "ORDER BY c.created_at DESC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);

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

    // Trong file: vn/edu/fpt/dao/TechnicalRequestDAO.java
    public boolean updateTechnicalRequestAndSchedule(TechnicalRequest request, MaintenanceSchedule schedule, List<TechnicalRequestDevice> devices) {
        Connection conn = null;
        final String UPDATE_TICKET_SQL = """
        UPDATE TechnicalRequests SET 
        enterprise_id = ?, contract_id = ?, service_id = ?, title = ?, 
        description = ?, priority = ?, is_billable = ?, estimated_cost = ?, assigned_to_id = ?
        WHERE id = ? 
    """;
        final String UPDATE_SCHEDULE_SQL = """
        UPDATE MaintenanceSchedules SET
        status_id = ?, address_id = ?, scheduled_date = ?, end_date = ?, 
        start_time = ?, end_time = ?, color = ?, updated_at = NOW()
        WHERE id = ?
    """;
        final String DELETE_DEVICES_SQL = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        final String INSERT_DEVICE_SQL = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";
        final String DELETE_ASSIGNMENTS_SQL = "DELETE FROM MaintenanceAssignments WHERE maintenance_schedule_id = ?";
        final String INSERT_ASSIGNMENT_SQL = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật TechnicalRequest
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_TICKET_SQL)) {
                ps.setInt(1, request.getEnterpriseId());
                ps.setObject(2, request.getContractId());
                ps.setInt(3, request.getServiceId());
                ps.setString(4, request.getTitle());
                ps.setString(5, request.getDescription());
                ps.setString(6, request.getPriority());
                ps.setBoolean(7, request.isIsBillable());
                ps.setDouble(8, request.getEstimatedCost());
                ps.setObject(9, request.getAssignedToId());
                ps.setInt(10, request.getId());
                ps.executeUpdate();
            }

            // 2. Cập nhật MaintenanceSchedule
            if (schedule != null && schedule.getId() > 0) {
                try (PreparedStatement ps = conn.prepareStatement(UPDATE_SCHEDULE_SQL)) {
                    ps.setObject(1, schedule.getStatusId());
                    ps.setObject(2, schedule.getAddressId());
                    ps.setObject(3, schedule.getScheduledDate());
                    ps.setObject(4, schedule.getEndDate());
                    ps.setObject(5, schedule.getStartTime());
                    ps.setObject(6, schedule.getEndTime());
                    ps.setString(7, schedule.getColor());
                    ps.setInt(8, schedule.getId());
                    ps.executeUpdate();
                }

                // 3. Cập nhật MaintenanceAssignments (Xóa cũ, thêm mới)
                try (PreparedStatement ps = conn.prepareStatement(DELETE_ASSIGNMENTS_SQL)) {
                    ps.setInt(1, schedule.getId());
                    ps.executeUpdate();
                }
                if (request.getAssignedToId() != null) { // Giả sử chỉ gán 1 người
                    try (PreparedStatement ps = conn.prepareStatement(INSERT_ASSIGNMENT_SQL)) {
                        ps.setInt(1, schedule.getId());
                        ps.setInt(2, request.getAssignedToId());
                        ps.executeUpdate();
                    }
                }
            }

            // 4. Cập nhật TechnicalRequestDevices (Xóa cũ, thêm mới)
            try (PreparedStatement ps = conn.prepareStatement(DELETE_DEVICES_SQL)) {
                ps.setInt(1, request.getId());
                ps.executeUpdate();
            }
            if (devices != null && !devices.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(INSERT_DEVICE_SQL)) {
                    for (TechnicalRequestDevice device : devices) {
                        ps.setInt(1, request.getId());
                        ps.setString(2, device.getDeviceName());
                        ps.setString(3, device.getSerialNumber());
                        ps.setString(4, device.getProblemDescription());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit(); // Hoàn tất Transaction thành công
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Trong file TechnicalRequestDAO.java
    public List<String> getDistinctStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        // SỬA LẠI: Lấy danh sách trạng thái từ bảng Statuses
        String sql = "SELECT status_name FROM Statuses ORDER BY id ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Lấy dữ liệu từ cột 'status_name'
                statuses.add(rs.getString("status_name"));
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
                Address address = new Address();
                address.setId((Integer) rs.getObject("address_id"));
                address.setStreetAddress(rs.getString("street_address"));
                address.setWardId((Integer) rs.getObject("ward_id"));
                address.setDistrictId((Integer) rs.getObject("district_id"));
                address.setProvinceId((Integer) rs.getObject("province_id"));
                ms.setAddress(address);
                Integer onlyUserId = (Integer) rs.getObject("assigned_user_id");
                List<Integer> assignedUserIds = new ArrayList<>();
                if (onlyUserId != null) {
                    assignedUserIds.add(onlyUserId);
                }
                ms.setAssignedUserIds(assignedUserIds);

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

    public static void main(String[] args) {
        int testTechnicalRequestId = 18; // Thay bằng một id có thật

        TechnicalRequestDAO dao = new TechnicalRequestDAO();
        try {
            MaintenanceSchedule ms = dao.getScheduleByTechnicalRequestId(testTechnicalRequestId);
            if (ms == null) {
                System.out.println("Không tìm thấy schedule với technicalRequestId = " + testTechnicalRequestId);
            } else {
                System.out.println("=== MaintenanceSchedule ===============");
                System.out.println("id = " + ms.getId());
                System.out.println("technicalRequestId = " + ms.getTechnicalRequestId());
                System.out.println("campaignId = " + ms.getCampaignId());
                System.out.println("color = " + ms.getColor());
                System.out.println("scheduledDate = " + ms.getScheduledDate());
                System.out.println("endDate = " + ms.getEndDate());
                System.out.println("startTime = " + ms.getStartTime());
                System.out.println("endTime = " + ms.getEndTime());
                System.out.println("addressId = " + ms.getAddressId());
                System.out.println("statusId = " + ms.getStatusId());
                System.out.println("statusName = " + ms.getStatusName());
                System.out.println("assignedUserIds = " + ms.getAssignedUserIds());
                System.out.println("createdAt = " + ms.getCreatedAt());
                System.out.println("updatedAt = " + ms.getUpdatedAt());

                // In chi tiết địa chỉ nếu có
                Address addr = ms.getAddress();
                if (addr != null) {
                    System.out.println("----- Address -----");
                    System.out.println("address.id = " + addr.getId());
                    System.out.println("address.streetAddress = " + addr.getStreetAddress());
                    System.out.println("address.wardId = " + addr.getWardId());
                    System.out.println("address.districtId = " + addr.getDistrictId());
                    System.out.println("address.provinceId = " + addr.getProvinceId());
                } else {
                    System.out.println("Không có Address.");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi truy vấn hoặc xử lý dữ liệu:");
            e.printStackTrace();
        }
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

    // Hàm mới: Lấy danh sách sản phẩm theo ID hợp đồng
    public List<Product> getProductsByContractId(int contractId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, p.name FROM Products p "
                + "JOIN ContractProducts cp ON p.id = cp.product_id "
                + "WHERE cp.contract_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt("id"));
                    p.setName(rs.getString("name"));
                    products.add(p);
                }
            }
        }
        return products;
    }

    // Hàm mới: Lấy chi tiết hợp đồng và khách hàng đi kèm
    public Contract getContractWithCustomerById(int contractId) throws SQLException {
        Contract contract = null;
        String sql = "SELECT c.id, c.contract_code, e.id as enterprise_id, e.name as enterprise_name "
                + "FROM Contracts c JOIN Enterprises e ON c.enterprise_id = e.id "
                + "WHERE c.id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contract = new Contract();
                    contract.setId(rs.getInt("id"));
                    contract.setContractCode(rs.getString("contract_code"));

                    Enterprise enterprise = new Enterprise();
                    enterprise.setId(rs.getInt("enterprise_id"));
                    enterprise.setName(rs.getString("enterprise_name"));

                    contract.setEnterprise(enterprise); // Giả sử bạn có setter này trong model Contract
                }
            }
        }
        return contract;
    }

    private static String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }
}
