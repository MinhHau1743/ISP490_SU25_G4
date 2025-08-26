package vn.edu.fpt.dao;

import vn.edu.fpt.model.Feedback;
import vn.edu.fpt.model.FeedbackView;
import vn.edu.fpt.model.InternalNote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO extends DBContext {

    /**
     * Thêm một phản hồi mới vào cơ sở dữ liệu. Hỗ trợ lưu cả
     * technical_request_id và contract_id.
     */
    public boolean addFeedback(Feedback feedback) {
        // Sửa lại câu lệnh SQL: Bỏ cột "appointment_id" và tham số tương ứng
        String sql = "INSERT INTO Feedbacks (enterprise_id, rating, comment, technical_request_id, contract_id, status) VALUES (?, ?, ?, ?, ?, 'moi')";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, feedback.getEnterpriseId());
            ps.setInt(2, feedback.getRating());
            ps.setString(3, feedback.getComment());

            // Cập nhật lại chỉ số tham số cho đúng
            ps.setObject(4, feedback.getTechnicalRequestId()); // Trước đây là 5
            ps.setObject(5, feedback.getContractId());      // Trước đây là 6

            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách phản hồi đã được lọc để hiển thị. Hỗ trợ tìm kiếm theo tên
     * khách hàng, mã yêu cầu và mã hợp đồng.
     */
    public List<FeedbackView> getFilteredFeedback(String query, String ratingFilter) {
        List<FeedbackView> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT f.id, f.rating, f.comment, f.status, f.created_at, "
                + "e.name AS enterpriseName, tr.request_code, c.contract_code "
                + "FROM Feedbacks f "
                + "JOIN Enterprises e ON f.enterprise_id = e.id "
                + "LEFT JOIN TechnicalRequests tr ON f.technical_request_id = tr.id "
                + "LEFT JOIN contracts c ON f.contract_id = c.id "
                + "WHERE f.is_deleted = 0"
        );

        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (e.name LIKE ? OR tr.request_code LIKE ? OR c.contract_code LIKE ?)");
            String searchPattern = "%" + query.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (ratingFilter != null && !ratingFilter.equals("all")) {
            switch (ratingFilter) {
                case "good":
                    sql.append(" AND f.rating >= ?");
                    params.add(4);
                    break;
                case "normal":
                    sql.append(" AND f.rating = ?");
                    params.add(3);
                    break;
                case "bad":
                    sql.append(" AND f.rating <= ?");
                    params.add(2);
                    break;
            }
        }

        sql.append(" ORDER BY f.created_at DESC");

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeedbackView fb = new FeedbackView();
                    fb.setId(rs.getInt("id"));
                    fb.setRating(rs.getInt("rating"));
                    fb.setComment(rs.getString("comment"));
                    fb.setStatus(rs.getString("status"));
                    fb.setCreatedAt(rs.getTimestamp("created_at"));
                    fb.setEnterpriseName(rs.getString("enterpriseName"));
                    fb.setRequestCode(rs.getString("request_code"));
                    fb.setContractCode(rs.getString("contract_code"));
                    list.add(fb);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy chi tiết một phản hồi bằng ID. Hỗ trợ lấy thông tin liên quan từ cả
     * Yêu cầu Kỹ thuật và Hợp đồng.
     */
    public FeedbackView getFeedbackById(int feedbackId) {
        FeedbackView feedback = null;
        String sql = "SELECT f.*, "
                + "e.name AS enterpriseName, e.business_email AS enterpriseEmail, "
                + "s.name AS serviceName, "
                + "tr.request_code, "
                + "c.id AS contractId, c.contract_code AS contractCode, "
                + "CONCAT_WS(' ', u_tech.first_name, u_tech.middle_name, u_tech.last_name) AS technicianName, "
                + "CONCAT_WS(' ', u_contract.first_name, u_contract.middle_name, u_contract.last_name) AS contractCreatorName "
                + "FROM Feedbacks f "
                + "JOIN Enterprises e ON f.enterprise_id = e.id "
                + "LEFT JOIN TechnicalRequests tr ON f.technical_request_id = tr.id "
                + "LEFT JOIN Services s ON tr.service_id = s.id "
                + "LEFT JOIN Users u_tech ON tr.assigned_to_id = u_tech.id "
                + "LEFT JOIN contracts c ON f.contract_id = c.id "
                + "LEFT JOIN Users u_contract ON c.created_by_id = u_contract.id "
                + "WHERE f.id = ?";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    feedback = new FeedbackView();
                    feedback.setId(rs.getInt("id"));
                    feedback.setRating(rs.getInt("rating"));
                    feedback.setComment(rs.getString("comment"));
                    feedback.setStatus(rs.getString("status"));
                    feedback.setCreatedAt(rs.getTimestamp("created_at"));
                    feedback.setEnterpriseName(rs.getString("enterpriseName"));
                    feedback.setEnterpriseEmail(rs.getString("enterpriseEmail"));
                    feedback.setServiceName(rs.getString("serviceName"));
                    feedback.setTechnicianName(rs.getString("technicianName"));
                    feedback.setRelatedRequestId(rs.getInt("technical_request_id"));
                    feedback.setRequestCode(rs.getString("request_code"));
                    feedback.setContractId(rs.getObject("contractId", Integer.class));
                    feedback.setContractCode(rs.getString("contractCode"));
                    feedback.setContractCreatorName(rs.getString("contractCreatorName"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return feedback;
    }

    /**
     * Kiểm tra xem một Yêu cầu Kỹ thuật đã có phản hồi hay chưa.
     */
    public boolean feedbackExistsForTechnicalRequest(int technicalRequestId) {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE technical_request_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicalRequestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Kiểm tra xem một Hợp đồng đã có phản hồi hay chưa.
     */
    public boolean feedbackExistsForContract(long contractId) {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE contract_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalFeedbackCount() {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE is_deleted = 0";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public int getFeedbackCountByRatingRange(int minRating, int maxRating) {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE is_deleted = 0 AND rating BETWEEN ? AND ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minRating);
            ps.setInt(2, maxRating);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public List<InternalNote> getNotesByFeedbackId(int feedbackId) {
        List<InternalNote> notes = new ArrayList<>();
        String sql = "SELECT n.*, CONCAT_WS(' ', u.first_name, u.last_name) AS userName "
                + "FROM internal_notes n "
                + "JOIN Users u ON n.user_id = u.id "
                + "WHERE n.feedback_id = ? AND n.is_deleted = 0 "
                + "ORDER BY n.created_at ASC";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InternalNote note = new InternalNote();
                    note.setId(rs.getInt("id"));
                    note.setFeedbackId(rs.getInt("feedback_id"));
                    note.setUserId(rs.getInt("user_id"));
                    note.setNoteText(rs.getString("note_text"));
                    note.setCreatedAt(rs.getTimestamp("created_at"));
                    note.setUserName(rs.getString("userName"));
                    notes.add(note);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    public boolean addInternalNote(InternalNote note) {
        String sql = "INSERT INTO internal_notes (feedback_id, user_id, note_text) VALUES (?, ?, ?)";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, note.getFeedbackId());
            ps.setInt(2, note.getUserId());
            ps.setString(3, note.getNoteText());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInternalNote(int noteId, String newNoteText) {
        String sql = "UPDATE internal_notes SET note_text = ? WHERE id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newNoteText);
            ps.setInt(2, noteId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDeleteInternalNote(int noteId) {
        String sql = "UPDATE internal_notes SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy phản hồi gần đây nhất của một doanh nghiệp, dùng để lấy ID ngay sau
     * khi tạo.
     *
     * @param enterpriseId ID của doanh nghiệp
     * @return Đối tượng Feedback nếu tìm thấy, ngược lại trả về null.
     */
    public Feedback getLastFeedbackByEnterpriseId(int enterpriseId) {
        // Sắp xếp theo ID giảm dần để lấy bản ghi mới nhất
        String sql = "SELECT * FROM Feedbacks WHERE enterprise_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enterpriseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setId(rs.getInt("id"));
                    feedback.setEnterpriseId(rs.getInt("enterprise_id"));
                    feedback.setRating(rs.getInt("rating"));
                    feedback.setComment(rs.getString("comment"));

                    // Lấy các ID có thể là NULL
                    int techId = rs.getInt("technical_request_id");
                    if (!rs.wasNull()) {
                        feedback.setTechnicalRequestId(techId);
                    }
                    int contractId = rs.getInt("contract_id");
                    if (!rs.wasNull()) {
                        feedback.setContractId(contractId);
                    }

                    feedback.setStatus(rs.getString("status"));
                    feedback.setCreatedAt(rs.getTimestamp("created_at"));
                    return feedback;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
