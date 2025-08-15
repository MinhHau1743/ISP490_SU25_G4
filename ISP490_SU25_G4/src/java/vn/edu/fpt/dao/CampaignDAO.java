package vn.edu.fpt.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.CampaignType;
import vn.edu.fpt.model.User;

public class CampaignDAO extends DBContext{

    /* ===================== DANH SÁCH ===================== */
    public List<Campaign> getCampaigns(
            int pageNumber, int pageSize,
            String searchTerm,
            Integer statusIdFilter, // lọc theo Statuses.id của bất kỳ schedule nào
            int typeIdFilter, // lọc theo c.type_id
            String startDateFilter, // lọc theo mốc ngày trên bất kỳ schedule nào
            String endDateFilter) {

        List<Campaign> campaigns = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT c.campaign_id, c.campaign_code, c.name, c.description, "
                + "       c.enterprise_id, c.type_id, c.created_by, c.created_at, c.updated_by, c.updated_at, "
                + "       u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code, "
                + "       e.name AS enterpriseName, ct.type_name AS campaignTypeName, "
                + "       ms_rep.scheduled_date AS scheduled_date, ms_rep.end_date AS end_date, "
                + "       ms_rep.status_id AS status_id, s.status_name AS status_name "
                + "FROM Campaigns c "
                + "LEFT JOIN Users u           ON u.id = c.created_by "
                + "LEFT JOIN Enterprises e     ON e.id = c.enterprise_id "
                + "LEFT JOIN CampaignTypes ct  ON ct.id = c.type_id "
                + // --- Lịch đại diện (mới nhất) để hiển thị ---
                "LEFT JOIN ( "
                + "    SELECT ms1.* "
                + "    FROM MaintenanceSchedules ms1 "
                + "    JOIN ( "
                + "        SELECT campaign_id, MAX(scheduled_date) AS max_date "
                + "        FROM MaintenanceSchedules "
                + "        GROUP BY campaign_id "
                + "    ) x ON x.campaign_id = ms1.campaign_id AND x.max_date = ms1.scheduled_date "
                + ") ms_rep ON ms_rep.campaign_id = c.campaign_id "
                + "LEFT JOIN Statuses s ON s.id = ms_rep.status_id "
                + "WHERE 1=1 "
        );

        // Ẩn campaign đã xoá mềm (nếu DB đã có cột này)
        sql.append("AND (c.is_deleted IS NULL OR c.is_deleted = 0) ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (c.name LIKE ? OR e.name LIKE ?) ");
            String like = "%" + searchTerm.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (typeIdFilter > 0) {
            sql.append("AND c.type_id = ? ");
            params.add(typeIdFilter);
        }
        // Lọc theo trạng thái của BẤT KỲ lịch nào thuộc campaign (EXISTS)
        if (statusIdFilter != null) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.status_id = ?) ");
            params.add(statusIdFilter);
        }
        // Lọc theo khoảng ngày của BẤT KỲ lịch nào thuộc campaign (EXISTS)
        if (startDateFilter != null && !startDateFilter.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.scheduled_date >= ?) ");
            params.add(startDateFilter);
        }
        if (endDateFilter != null && !endDateFilter.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.scheduled_date <= ?) ");
            params.add(endDateFilter);
        }

        sql.append("ORDER BY c.campaign_id DESC ");
        sql.append("LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Campaign c = new Campaign();
                    c.setCampaignId(rs.getInt("campaign_id"));
                    c.setCampaignCode(rs.getString("campaign_code"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));

                    c.setEnterpriseId(rs.getInt("enterprise_id"));
                    c.setEnterpriseName(rs.getString("enterpriseName"));

                    c.setTypeId(rs.getInt("type_id"));
                    c.setTypeName(rs.getString("campaignTypeName"));

                    c.setCreatedBy(rs.getInt("created_by"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUpdatedBy((Integer) rs.getObject("updated_by"));
                    c.setUpdatedAt(rs.getTimestamp("updated_at"));

                    // Lịch đại diện (mới nhất)
                    c.setScheduledDate(rs.getDate("scheduled_date"));
                    c.setEndDate(rs.getDate("end_date"));

                    // Trạng thái của lịch đại diện
                    c.setStatusId((Integer) rs.getObject("status_id"));
                    c.setStatusName(rs.getString("status_name"));

                    // Creator
                    int userId = rs.getInt("user_id");
                    if (!rs.wasNull()) {
                        User u = new User();
                        u.setId(userId);
                        u.setFirstName(rs.getString("first_name"));
                        u.setMiddleName(rs.getString("middle_name"));
                        u.setLastName(rs.getString("last_name"));
                        u.setEmployeeCode(rs.getString("employee_code"));
                        c.setCreator(u);
                    }

                    campaigns.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaigns;
    }

    /* ===================== ĐẾM ===================== */
    public int countCampaigns(String searchTerm,
            Integer statusIdFilter,
            int typeIdFilter,
            String startDateFilter,
            String endDateFilter) {

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT COUNT(DISTINCT c.campaign_id) "
                + "FROM Campaigns c "
                + "LEFT JOIN Enterprises e ON e.id = c.enterprise_id "
                + "WHERE 1=1 "
        );

        // Ẩn campaign đã xoá mềm (nếu DB đã có cột này)
        sql.append("AND (c.is_deleted IS NULL OR c.is_deleted = 0) ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (c.name LIKE ? OR e.name LIKE ?) ");
            String like = "%" + searchTerm.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (typeIdFilter > 0) {
            sql.append("AND c.type_id = ? ");
            params.add(typeIdFilter);
        }
        if (statusIdFilter != null) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.status_id = ?) ");
            params.add(statusIdFilter);
        }
        if (startDateFilter != null && !startDateFilter.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.scheduled_date >= ?) ");
            params.add(startDateFilter);
        }
        if (endDateFilter != null && !endDateFilter.isEmpty()) {
            sql.append("AND EXISTS (SELECT 1 FROM MaintenanceSchedules m ")
                    .append("            WHERE m.campaign_id = c.campaign_id AND m.scheduled_date <= ?) ");
            params.add(endDateFilter);
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* ===================== CHI TIẾT ===================== */
    public Campaign getCampaignById(int campaignId) {
        Campaign c = null;
        String sql
                = "SELECT c.campaign_id, c.campaign_code, c.name, c.description, "
                + "       c.enterprise_id, c.type_id, c.created_by, c.created_at, c.updated_by, c.updated_at, "
                + "       u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code, "
                + "       e.name AS enterpriseName, ct.type_name AS campaignTypeName, "
                + "       ms_rep.scheduled_date AS scheduled_date, ms_rep.end_date AS end_date, "
                + "       ms_rep.status_id AS status_id, s.status_name AS status_name "
                + "FROM Campaigns c "
                + "LEFT JOIN Users u           ON u.id = c.created_by "
                + "LEFT JOIN Enterprises e     ON e.id = c.enterprise_id "
                + "LEFT JOIN CampaignTypes ct  ON ct.id = c.type_id "
                + "LEFT JOIN ( "
                + "    SELECT ms1.* "
                + "    FROM MaintenanceSchedules ms1 "
                + "    JOIN ( "
                + "        SELECT campaign_id, MAX(scheduled_date) AS max_date "
                + "        FROM MaintenanceSchedules GROUP BY campaign_id "
                + "    ) x ON x.campaign_id = ms1.campaign_id AND x.max_date = ms1.scheduled_date "
                + ") ms_rep ON ms_rep.campaign_id = c.campaign_id "
                + "LEFT JOIN Statuses s ON s.id = ms_rep.status_id "
                + "WHERE c.campaign_id = ? "
                + "  AND (c.is_deleted IS NULL OR c.is_deleted = 0)";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Campaign();
                    c.setCampaignId(rs.getInt("campaign_id"));
                    c.setCampaignCode(rs.getString("campaign_code"));
                    c.setName(rs.getString("name"));
                    c.setDescription(rs.getString("description"));

                    c.setEnterpriseId(rs.getInt("enterprise_id"));
                    c.setEnterpriseName(rs.getString("enterpriseName"));

                    c.setTypeId(rs.getInt("type_id"));
                    c.setTypeName(rs.getString("campaignTypeName"));

                    c.setCreatedBy(rs.getInt("created_by"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUpdatedBy((Integer) rs.getObject("updated_by"));
                    c.setUpdatedAt(rs.getTimestamp("updated_at"));

                    c.setScheduledDate(rs.getDate("scheduled_date"));
                    c.setEndDate(rs.getDate("end_date"));
                    c.setStatusId((Integer) rs.getObject("status_id"));
                    c.setStatusName(rs.getString("status_name"));

                    int userId = rs.getInt("user_id");
                    if (!rs.wasNull()) {
                        User u = new User();
                        u.setId(userId);
                        u.setFirstName(rs.getString("first_name"));
                        u.setMiddleName(rs.getString("middle_name"));
                        u.setLastName(rs.getString("last_name"));
                        u.setEmployeeCode(rs.getString("employee_code"));
                        c.setCreator(u);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    /* ===================== MASTER DATA ===================== */
    public List<CampaignType> getAllCampaignTypes() {
        List<CampaignType> types = new ArrayList<>();
        String sql = "SELECT id, type_name FROM CampaignTypes ORDER BY type_name ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CampaignType t = new CampaignType();
                t.setId(rs.getInt("id"));
                t.setTypeName(rs.getString("type_name"));
                types.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    /* ===================== TẠO MỚI ===================== */
    public int addCampaignAndReturnId(Campaign campaign, Connection conn) throws SQLException {
        String sql = "INSERT INTO Campaigns (campaign_code, name, type_id, enterprise_id, description, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, campaign.getCampaignCode());
            ps.setString(2, campaign.getName());
            ps.setInt(3, campaign.getTypeId());
            ps.setInt(4, campaign.getEnterpriseId());
            ps.setString(5, campaign.getDescription());
            ps.setInt(6, campaign.getCreatedBy());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Tạo chiến dịch thất bại, không có dòng nào được thêm.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("Tạo chiến dịch thất bại, không lấy được ID.");
            }
        }
    }

    /* ====== Overload cũ (giữ tương thích khi controller truyền status là chuỗi) ====== */
    @Deprecated
    public List<Campaign> getCampaigns(int pageNumber, int pageSize,
            String searchTerm, String statusFilter,
            int typeIdFilter, String startDateFilter, String endDateFilter) {
        Integer statusId = null;
        if (statusFilter != null && statusFilter.matches("\\d+")) {
            statusId = Integer.valueOf(statusFilter);
        }
        return getCampaigns(pageNumber, pageSize, searchTerm, statusId, typeIdFilter, startDateFilter, endDateFilter);
    }

    @Deprecated
    public int countCampaigns(String searchTerm, String statusFilter,
            int typeIdFilter, String startDateFilter, String endDateFilter) {
        Integer statusId = null;
        if (statusFilter != null && statusFilter.matches("\\d+")) {
            statusId = Integer.valueOf(statusFilter);
        }
        return countCampaigns(searchTerm, statusId, typeIdFilter, startDateFilter, endDateFilter);
    }

    // Cập nhật các trường cơ bản của Campaign
    public void updateCampaignCore(Campaign c, Connection conn) throws SQLException {
        String sql = "UPDATE Campaigns SET name=?, type_id=?, enterprise_id=?, description=?, "
                + "updated_by=?, updated_at=NOW() WHERE campaign_id=?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setInt(2, c.getTypeId());
            ps.setInt(3, c.getEnterpriseId());
            ps.setString(4, c.getDescription());
            if (c.getUpdatedBy() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, c.getUpdatedBy());
            }
            ps.setInt(6, c.getCampaignId());
            ps.executeUpdate();
        }
    }
    public boolean softDeleteCampaignById(int campaignId) {
        // Câu lệnh SQL này cập nhật cờ is_deleted thành 1 (hoặc true)
        String sql = "UPDATE Campaigns SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE campaign_id = ?";

        try (Connection conn = DBContext.getConnection(); // Hoặc getConnection()
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, campaignId);

            // executeUpdate() trả về số dòng bị ảnh hưởng
            int rowsAffected = ps.executeUpdate();

            // Nếu có ít nhất 1 dòng bị ảnh hưởng, coi như thành công
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }
    /**
     * Đếm số lượng chiến dịch đang hoạt động (chưa bị xóa) dựa trên TÊN của
     * trạng thái.
     *
     * @param statusName Tên trạng thái cần đếm (ví dụ: "Hoàn thành").
     * @return Số lượng chiến dịch khớp.
     */
    public int countCampaignsByStatusName(String statusName) {
        // SQL này kết nối 3 bảng để đếm số chiến dịch (không trùng lặp)
        // có lịch trình mang trạng thái được chỉ định.
        String sql = "SELECT COUNT(DISTINCT c.campaign_id) "
                + "FROM Campaigns c "
                + "JOIN MaintenanceSchedules ms ON c.campaign_id = ms.campaign_id "
                + "JOIN Statuses s ON ms.status_id = s.id "
                + "WHERE s.status_name = ? AND c.is_deleted = 0";

        try (Connection conn = getConnection(); // Hoặc DBContext.getConnection()
                 PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statusName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Trả về kết quả đếm
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console nếu có
        }

        return 0; // Trả về 0 nếu có lỗi hoặc không tìm thấy
    }
    
}
