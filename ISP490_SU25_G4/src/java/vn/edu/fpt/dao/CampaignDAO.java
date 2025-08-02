package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.User;

public class CampaignDAO {

    // ĐÃ SỬA: Thêm tham số searchTerm để tìm kiếm
    public List<Campaign> getCampaigns(int pageNumber, int pageSize, String searchTerm) {
        List<Campaign> campaigns = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;

        // THAY ĐỔI: Sử dụng StringBuilder để xây dựng câu truy vấn linh hoạt hơn
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT c.campaign_id, c.name, c.description, c.start_date, c.end_date, c.status, ");
        sqlBuilder.append("c.created_by, c.created_at, c.updated_by, c.updated_at, c.attachment_file_name, ");
        sqlBuilder.append("u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code ");
        sqlBuilder.append("FROM Campaigns c ");
        sqlBuilder.append("JOIN Users u ON c.created_by = u.id ");

        // THAY ĐỔI: Thêm điều kiện WHERE cho tìm kiếm nếu searchTerm không rỗng
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sqlBuilder.append("WHERE c.name LIKE ? "); // Tìm kiếm gần đúng theo tên
        }

        sqlBuilder.append("ORDER BY c.campaign_id ASC "); // Sắp xếp theo ID tăng dần
        sqlBuilder.append("LIMIT ? OFFSET ?");

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;
            // THAY ĐỔI: Gán giá trị cho searchTerm nếu có
            if (searchTerm != null && !searchTerm.isEmpty()) {
                ps.setString(paramIndex++, "%" + searchTerm + "%"); // Sử dụng % cho LIKE
            }
            ps.setInt(paramIndex++, pageSize);
            ps.setInt(paramIndex++, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Campaign campaign = new Campaign();
                campaign.setCampaignId(rs.getInt("campaign_id"));
                campaign.setName(rs.getString("name"));
                campaign.setDescription(rs.getString("description"));
                campaign.setStartDate(rs.getDate("start_date"));
                campaign.setEndDate(rs.getDate("end_date"));
                campaign.setStatus(rs.getString("status"));
                campaign.setCreatedBy(rs.getInt("created_by"));
                campaign.setCreatedAt(rs.getTimestamp("created_at"));
                campaign.setUpdatedBy(rs.getInt("updated_by"));
                campaign.setUpdatedAt(rs.getTimestamp("updated_at"));
                
                campaign.setAttachmentFileName(rs.getString("attachment_file_name"));

                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmployeeCode(rs.getString("employee_code"));

                campaign.setUser(user);

                campaigns.add(campaign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return campaigns;
    }

    // ĐÃ SỬA: Thêm tham số searchTerm cho việc đếm tổng số bản ghi khi tìm kiếm
    public int countCampaigns(String searchTerm) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) FROM Campaigns c ");

        // THAY ĐỔI: Thêm điều kiện WHERE cho tìm kiếm nếu searchTerm không rỗng
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sqlBuilder.append("WHERE c.name LIKE ? ");
        }

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            // THAY ĐỔI: Gán giá trị cho searchTerm nếu có
            if (searchTerm != null && !searchTerm.isEmpty()) {
                ps.setString(1, "%" + searchTerm + "%");
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
    
    // Các phương thức khác (addCampaign, getAllUsers, getCampaignById, updateCampaignStatus, updateCampaign) không thay đổi
    public boolean addCampaign(Campaign campaign) {
        String sql = "INSERT INTO Campaigns (name, description, start_date, end_date, status, created_by, updated_by, attachment_file_name) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, campaign.getName());
            ps.setString(2, campaign.getDescription());
            ps.setDate(3, new java.sql.Date(campaign.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(campaign.getEndDate().getTime()));
            ps.setString(5, campaign.getStatus());
            ps.setInt(6, campaign.getCreatedBy());
            
            if(campaign.getUpdatedBy() != null) {
                ps.setInt(7, campaign.getUpdatedBy());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            ps.setString(8, campaign.getAttachmentFileName());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, middle_name, employee_code FROM Users WHERE is_deleted = 0 ORDER BY last_name, first_name";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setMiddleName(rs.getString("middle_name"));
                user.setEmployeeCode(rs.getString("employee_code"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Campaign getCampaignById(int campaignId) {
        Campaign campaign = null;
        
        String sql = "SELECT c.campaign_id, c.name, c.description, c.start_date, c.end_date, c.status, "
                + "c.created_by, c.created_at, c.updated_by, c.updated_at, c.attachment_file_name, "
                + "u.id AS user_id, u.first_name, u.last_name, u.middle_name, u.employee_code "
                + "FROM Campaigns c "
                + "LEFT JOIN Users u ON c.created_by = u.id "
                + "WHERE c.campaign_id = ?";

        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, campaignId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                campaign = new Campaign();
                campaign.setCampaignId(rs.getInt("campaign_id"));
                campaign.setName(rs.getString("name"));
                campaign.setDescription(rs.getString("description"));
                campaign.setStartDate(rs.getDate("start_date"));
                campaign.setEndDate(rs.getDate("end_date"));
                campaign.setStatus(rs.getString("status"));
                campaign.setCreatedBy(rs.getInt("created_by"));
                campaign.setCreatedAt(rs.getTimestamp("created_at"));
                campaign.setUpdatedBy(rs.getInt("updated_by"));
                campaign.setUpdatedAt(rs.getTimestamp("updated_at"));

                campaign.setAttachmentFileName(rs.getString("attachment_file_name"));

                if (rs.getInt("user_id") != 0) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setMiddleName(rs.getString("middle_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmployeeCode(rs.getString("employee_code"));
                    campaign.setUser(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campaign;
    }

    public boolean updateCampaignStatus(int campaignId, String newStatus, Integer updatedByUserId) {
        String sql = "UPDATE Campaigns SET status = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE campaign_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            if (updatedByUserId != null) {
                ps.setInt(2, updatedByUserId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, campaignId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCampaign(Campaign campaign) {
        String sql = "UPDATE Campaigns SET name = ?, description = ?, start_date = ?, end_date = ?, status = ?, "
                   + "updated_at = ?, updated_by = ?, attachment_file_name = ? "
                   + "WHERE campaign_id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, campaign.getName());
            ps.setString(2, campaign.getDescription());
            ps.setDate(3, new java.sql.Date(campaign.getStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(campaign.getEndDate().getTime()));
            ps.setString(5, campaign.getStatus());
            ps.setTimestamp(6, campaign.getUpdatedAt());

            if (campaign.getUpdatedBy() != null) {
                ps.setInt(7, campaign.getUpdatedBy());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            
            ps.setString(8, campaign.getAttachmentFileName());
            
            ps.setInt(9, campaign.getCampaignId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}