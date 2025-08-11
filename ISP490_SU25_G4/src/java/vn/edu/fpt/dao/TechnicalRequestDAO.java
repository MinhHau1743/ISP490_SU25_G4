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

    public List<TechnicalRequest> getFilteredTechnicalRequests(String query, String status, int serviceId, int limit, int offset) throws SQLException {
        List<TechnicalRequest> requests = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT tr.*, "
                + "       e.name AS enterpriseName, "
                + "       c.contract_code AS contractCode, "
                + "       s.name AS serviceName, "
                + "       st.status_name AS statusName, "
                + "       GROUP_CONCAT(DISTINCT CONCAT(u.last_name, ' ', u.middle_name, ' ', u.first_name) SEPARATOR ', ') AS assignedToNames "
                + "FROM TechnicalRequests tr "
                + "JOIN Enterprises e ON tr.enterprise_id = e.id "
                + "JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN Contracts c ON tr.contract_id = c.id "
                + "LEFT JOIN Statuses st ON tr.status_id = st.id "
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
            sql.append(" AND st.status_name = ?");
            params.add(status.trim());
        }

        if (serviceId > 0) {
            sql.append(" AND tr.service_id = ?");
            params.add(serviceId);
        }

        // Nhóm theo id của yêu cầu để gom các nhân viên assigned
        sql.append(" GROUP BY tr.id ");

        sql.append(" ORDER BY tr.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicalRequest req = new TechnicalRequest();
                    req.setId(rs.getInt("id"));
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
                    req.setContractCode(rs.getString("contractCode"));
                    req.setServiceName(rs.getString("serviceName"));

                    // Danh sách nhân viên assigned dưới dạng chuỗi
                    req.setAssignedToNames(rs.getString("assignedToNames"));

                    requests.add(req);
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
               st.status_name AS statusName,
               GROUP_CONCAT(DISTINCT CONCAT(u.last_name, ' ', u.middle_name, ' ', u.first_name) SEPARATOR ', ') AS assignedToNames,
               CONCAT(reporter.last_name, ' ', reporter.middle_name, ' ', reporter.first_name) AS reporterName
        FROM TechnicalRequests tr
        JOIN Enterprises e ON tr.enterprise_id = e.id
        JOIN Services s ON tr.service_id = s.id
        JOIN Users reporter ON tr.reporter_id = reporter.id
        LEFT JOIN Contracts c ON tr.contract_id = c.id
        LEFT JOIN Statuses st ON tr.status_id = st.id
        LEFT JOIN MaintenanceSchedules ms ON ms.technical_request_id = tr.id
        LEFT JOIN MaintenanceAssignments ma ON ma.maintenance_schedule_id = ms.id
        LEFT JOIN Users u ON ma.user_id = u.id
        WHERE tr.id = ?
        GROUP BY tr.id
    """;

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

                    // Trạng thái từ Statuses
                    req.setStatus(rs.getString("statusName"));

                    req.setIsBillable(rs.getBoolean("is_billable"));
                    req.setEstimatedCost(rs.getDouble("estimated_cost"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setResolvedAt(rs.getTimestamp("resolved_at"));
                    req.setEnterpriseName(rs.getString("enterpriseName"));
                    req.setContractCode(rs.getString("contractCode"));
                    req.setServiceName(rs.getString("serviceName"));

                    // Danh sách nhân viên assigned (ở dạng chuỗi)
                    req.setAssignedToNames(rs.getString("assignedToNames"));

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
        String sql = "SELECT id, last_name, middle_name, first_name FROM Users WHERE is_deleted = 0 AND role_id = 5";
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
        String sqlRequest = "INSERT INTO TechnicalRequests (request_code, enterprise_id, contract_id, service_id, title, description, priority, status_id, reporter_id, is_billable, estimated_cost, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";
        String sqlSchedule = "INSERT INTO MaintenanceSchedules (technical_request_id, scheduled_date) VALUES (?, CURDATE())";
        String sqlAssign = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            int newRequestId;

            // Insert TechnicalRequest
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
                psRequest.setInt(8, request.getStatusId() != null ? request.getStatusId() : 1); // 1: Mặc định 'Đang thực hiện'
                psRequest.setInt(9, request.getReporterId());
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

            // Insert thiết bị liên quan đến request
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

            // Tạo mới MaintenanceSchedule cho request này
            int newScheduleId;
            try (PreparedStatement psSchedule = conn.prepareStatement(sqlSchedule, Statement.RETURN_GENERATED_KEYS)) {
                psSchedule.setInt(1, newRequestId);
                if (psSchedule.executeUpdate() == 0) {
                    throw new SQLException("Tạo lịch bảo trì thất bại.");
                }
                try (ResultSet rsSchedule = psSchedule.getGeneratedKeys()) {
                    if (rsSchedule.next()) {
                        newScheduleId = rsSchedule.getInt(1);
                    } else {
                        throw new SQLException("Không lấy được ID lịch bảo trì.");
                    }
                }
            }

            // Gán nhân viên được phân công vào MaintenanceAssignments
            List<Integer> assignedUserIds = request.getAssignedUserIds();
            if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
                try (PreparedStatement psAssign = conn.prepareStatement(sqlAssign)) {
                    for (Integer userId : assignedUserIds) {
                        psAssign.setInt(1, newScheduleId);
                        psAssign.setInt(2, userId);
                        psAssign.addBatch();
                    }
                    psAssign.executeBatch();
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
        String sqlUpdateTicket = "UPDATE TechnicalRequests SET enterprise_id=?, service_id=?, title=?, description=?, priority=?, status_id=?, is_billable=?, estimated_cost=? WHERE id=?";
        String sqlDeleteDevices = "DELETE FROM TechnicalRequestDevices WHERE technical_request_id = ?";
        String sqlInsertDevice = "INSERT INTO TechnicalRequestDevices (technical_request_id, device_name, serial_number, problem_description) VALUES (?, ?, ?, ?)";

        // Cập nhật danh sách nhân viên assigned nếu cần:
        // (1) Xác định schedule thuộc request này
        // (2) Xóa các assignment cũ
        // (3) Thêm assignment mới
        String sqlGetSchedule = "SELECT id FROM MaintenanceSchedules WHERE technical_request_id = ?";
        String sqlDeleteAssignments = "DELETE FROM MaintenanceAssignments WHERE maintenance_schedule_id = ?";
        String sqlInsertAssignment = "INSERT INTO MaintenanceAssignments (maintenance_schedule_id, user_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // 1. Update TechnicalRequest
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateTicket)) {
                psUpdate.setInt(1, request.getEnterpriseId());
                psUpdate.setInt(2, request.getServiceId());
                psUpdate.setString(3, request.getTitle());
                psUpdate.setString(4, request.getDescription());
                psUpdate.setString(5, request.getPriority());
                psUpdate.setInt(6, request.getStatusId());     // status_id mới: INT
                psUpdate.setBoolean(7, request.isIsBillable());
                psUpdate.setDouble(8, request.getEstimatedCost());
                psUpdate.setInt(9, request.getId());
                psUpdate.executeUpdate();
            }

            // 2. Xóa tất cả thiết bị cũ
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDevices)) {
                psDelete.setInt(1, request.getId());
                psDelete.executeUpdate();
            }

            // 3. Thêm lại thiết bị mới
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

            // 4. Cập nhật nhân viên Assigned qua MaintenanceAssignments
            // (Chỉ thực hiện nếu có danh sách assignedUsers gửi lên)
            List<Integer> assignedUserIds = null;
            if (request.getAssignedUserIds() != null) {
                assignedUserIds = request.getAssignedUserIds();
            }

            if (assignedUserIds != null) {
                int scheduleId = -1;
                // Lấy schedule id của request này
                try (PreparedStatement psGetSchedule = conn.prepareStatement(sqlGetSchedule)) {
                    psGetSchedule.setInt(1, request.getId());
                    try (ResultSet rsSchedule = psGetSchedule.executeQuery()) {
                        if (rsSchedule.next()) {
                            scheduleId = rsSchedule.getInt("id");
                        }
                    }
                }
                if (scheduleId > 0) {
                    // Xóa danh sách assigned cũ
                    try (PreparedStatement psDelAssign = conn.prepareStatement(sqlDeleteAssignments)) {
                        psDelAssign.setInt(1, scheduleId);
                        psDelAssign.executeUpdate();
                    }
                    // Thêm assignment mới
                    if (!assignedUserIds.isEmpty()) {
                        try (PreparedStatement psInsAssign = conn.prepareStatement(sqlInsertAssignment)) {
                            for (Integer userId : assignedUserIds) {
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

    public List<String> getDistinctStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = """
        SELECT DISTINCT s.status_name
        FROM TechnicalRequests tr
        JOIN Statuses s ON tr.status_id = s.id
        WHERE tr.is_deleted = 0
        ORDER BY s.status_name
    """;
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                statuses.add(rs.getString("status_name"));
            }
        }
        return statuses;
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
        TechnicalRequestDAO dao = new TechnicalRequestDAO();

        // Case 1: Lấy tất cả (không filter), phân trang 10 bản ghi đầu
        runCase(dao, "Tất cả", null, "all", 0, 10, 0);

        // Case 2: Tìm theo mã/yêu cầu có chứa "TRQ-" (hoặc tên DN có từ khóa)
        runCase(dao, "Tìm theo query='TRQ-'", "TRQ-", "all", 0, 10, 0);

        // Case 3: Lọc theo serviceId = 3 (ví dụ 'Bảo trì định kỳ hệ thống')
        runCase(dao, "Lọc theo serviceId=3", null, "all", 3, 10, 0);

        // Case 4: Phân trang trang 2 (offset = 10)
        runCase(dao, "Phân trang (offset=10)", null, "all", 0, 10, 10);

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
