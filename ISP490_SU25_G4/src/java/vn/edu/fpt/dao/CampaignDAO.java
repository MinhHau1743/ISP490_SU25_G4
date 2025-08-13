package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.CampaignType;
import vn.edu.fpt.model.User;

public class CampaignDAO {

    /**
     * Lấy danh sách chiến dịch có phân trang và áp dụng các bộ lọc.
     */
    public List<Campaign> getCampaigns(int pageNumber, int pageSize, String searchTerm, String statusFilter, int typeIdFilter, String startDateFilter, String endDateFilter) {
        List<Campaign> campaigns = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT c.campaign_id, c.name, c.description, c.status, c.enterprise_id, ");
        sqlBuilder.append("c.created_by, c.created_at, c.updated_by, c.updated_at, ");
        sqlBuilder.append("u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code, ");
        sqlBuilder.append("ms.scheduled_date AS start_date, ms.end_date AS end_date, ");
        sqlBuilder.append("e.name AS enterpriseName, ct.type_name AS campaignTypeName ");
        sqlBuilder.append("FROM Campaigns c ");
        sqlBuilder.append("LEFT JOIN Users u ON c.created_by = u.id ");
        sqlBuilder.append("LEFT JOIN Enterprises e ON c.enterprise_id = e.id ");
        sqlBuilder.append("LEFT JOIN CampaignTypes ct ON c.type_id = ct.id ");
        sqlBuilder.append("LEFT JOIN MaintenanceSchedules ms ON c.campaign_id = ms.campaign_id ");
        sqlBuilder.append("WHERE 1=1 ");

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sqlBuilder.append("AND (c.name LIKE ? OR e.name LIKE ?) ");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sqlBuilder.append("AND c.status = ? ");
            params.add(statusFilter);
        }
        if (typeIdFilter > 0) {
            sqlBuilder.append("AND c.type_id = ? ");
            params.add(typeIdFilter);
        }
        if (startDateFilter != null && !startDateFilter.isEmpty()) {
            sqlBuilder.append("AND ms.scheduled_date >= ? ");
            params.add(startDateFilter);
        }
        if (endDateFilter != null && !endDateFilter.isEmpty()) {
            sqlBuilder.append("AND ms.scheduled_date <= ? ");
            params.add(endDateFilter);
        }

        sqlBuilder.append("ORDER BY c.campaign_id DESC ");
        sqlBuilder.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // --- BẮT ĐẦU PHẦN MAPPING DỮ LIỆU ---
                Campaign campaign = new Campaign();
                campaign.setCampaignId(rs.getInt("campaign_id"));
                campaign.setName(rs.getString("name"));
                campaign.setDescription(rs.getString("description"));
                campaign.setStartDate(rs.getDate("start_date"));
                campaign.setEndDate(rs.getDate("end_date"));
                campaign.setStatus(rs.getString("status"));
                campaign.setCreatedBy(rs.getInt("created_by"));
                campaign.setCreatedAt(rs.getTimestamp("created_at"));
                campaign.setEnterpriseId(rs.getInt("enterprise_id"));
                campaign.setEnterpriseName(rs.getString("enterpriseName"));
                campaign.setTypeName(rs.getString("campaignTypeName"));

                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    User user = new User();
                    user.setId(userId);
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    campaign.setCreator(user);
                }
                campaigns.add(campaign);
                // --- KẾT THÚC PHẦN MAPPING DỮ LIỆU ---
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaigns;
    }

    /**
     * Lấy thông tin chi tiết của một chiến dịch bằng ID.
     */
    public Campaign getCampaignById(int campaignId) {
        Campaign campaign = null;
        // --- ĐÃ SỬA: Bổ sung JOIN và SELECT để lấy đầy đủ thông tin ---
        String sql = "SELECT c.campaign_id, c.name, c.description, c.status, c.enterprise_id, "
                + "c.created_by, c.created_at, c.updated_by, c.updated_at, "
                + "u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code, "
                + "ms.scheduled_date AS start_date, ms.end_date AS end_date, "
                + "e.name AS enterpriseName, ct.type_name AS campaignTypeName "
                + "FROM Campaigns c "
                + "LEFT JOIN Users u ON c.created_by = u.id "
                + "LEFT JOIN Enterprises e ON c.enterprise_id = e.id "
                + "LEFT JOIN CampaignTypes ct ON c.type_id = ct.id "
                + "LEFT JOIN MaintenanceSchedules ms ON c.campaign_id = ms.campaign_id "
                + "WHERE c.campaign_id = ?";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                campaign = new Campaign();
                // --- BẮT ĐẦU PHẦN MAPPING DỮ LIỆU ---
                campaign.setCampaignId(rs.getInt("campaign_id"));
                campaign.setName(rs.getString("name"));
                campaign.setDescription(rs.getString("description"));
                campaign.setStartDate(rs.getDate("start_date"));
                campaign.setEndDate(rs.getDate("end_date"));
                campaign.setStatus(rs.getString("status"));
                campaign.setCreatedBy(rs.getInt("created_by"));
                campaign.setCreatedAt(rs.getTimestamp("created_at"));
                campaign.setEnterpriseId(rs.getInt("enterprise_id"));
                campaign.setEnterpriseName(rs.getString("enterpriseName"));
                campaign.setTypeName(rs.getString("campaignTypeName"));

                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    User user = new User();
                    user.setId(userId);
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    campaign.setCreator(user);
                }
                // --- KẾT THÚC PHẦN MAPPING DỮ LIỆU ---
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    /**
     * Đếm tổng số chiến dịch có áp dụng các bộ lọc.
     */
    public int countCampaigns(String searchTerm, String statusFilter, int typeIdFilter, String startDateFilter, String endDateFilter) {
        List<Object> params = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(DISTINCT c.campaign_id) FROM Campaigns c ");
        sqlBuilder.append("LEFT JOIN Enterprises e ON c.enterprise_id = e.id ");
        sqlBuilder.append("LEFT JOIN MaintenanceSchedules ms ON c.campaign_id = ms.campaign_id ");
        sqlBuilder.append("WHERE 1=1 ");

        if (searchTerm != null && !searchTerm.isEmpty()) {
            sqlBuilder.append("AND (c.name LIKE ? OR e.name LIKE ?) ");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sqlBuilder.append("AND c.status = ? ");
            params.add(statusFilter);
        }
        if (typeIdFilter > 0) {
            sqlBuilder.append("AND c.type_id = ? ");
            params.add(typeIdFilter);
        }
        if (startDateFilter != null && !startDateFilter.isEmpty()) {
            sqlBuilder.append("AND ms.scheduled_date >= ? ");
            params.add(startDateFilter);
        }
        if (endDateFilter != null && !endDateFilter.isEmpty()) {
            sqlBuilder.append("AND ms.scheduled_date <= ? ");
            params.add(endDateFilter);
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy tất cả các loại chiến dịch.
     */
    public List<CampaignType> getAllCampaignTypes() {
        List<CampaignType> types = new ArrayList<>();
        String sql = "SELECT id, type_name FROM CampaignTypes ORDER BY type_name ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CampaignType type = new CampaignType();
                type.setId(rs.getInt("id"));
                type.setTypeName(rs.getString("type_name"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }
}
