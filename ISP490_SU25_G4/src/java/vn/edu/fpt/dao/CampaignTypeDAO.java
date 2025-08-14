package vn.edu.fpt.dao;

import vn.edu.fpt.dao.DBContext;
import vn.edu.fpt.model.CampaignType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CampaignTypeDAO  {

    /**
     * Lấy tất cả các loại chiến dịch từ cơ sở dữ liệu.
     *
     * @return một danh sách (List) các đối tượng CampaignType.
     */
    public List<CampaignType> getAllCampaignTypes() {
        List<CampaignType> list = new ArrayList<>();
        String sql = "SELECT id, type_name FROM CampaignTypes ORDER BY type_name ASC";

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CampaignType type = new CampaignType();
                type.setId(rs.getInt("id"));
                type.setTypeName(rs.getString("type_name"));
                list.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return list;
    }

    // Bạn có thể thêm các phương thức khác ở đây nếu cần, ví dụ:
    // public CampaignType getCampaignTypeById(int id) { ... }
}
