/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import vn.edu.fpt.dao.DBContext; // Giả định bạn có lớp này để kết nối DB
import vn.edu.fpt.model.FeedbackView;
import vn.edu.fpt.model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.InternalNote;

public class FeedbackDAO {

    // Phương thức để thêm một phản hồi mới
    public boolean addFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedbacks (enterprise_id, rating, comment, appointment_id, technical_request_id, status) VALUES (?, ?, ?, ?, ?, 'moi')";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, feedback.getEnterpriseId());
            ps.setInt(2, feedback.getRating());
            ps.setString(3, feedback.getComment());

            if (feedback.getAppointmentId() != null) {
                ps.setInt(4, feedback.getAppointmentId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (feedback.getTechnicalRequestId() != null) {
                ps.setInt(5, feedback.getTechnicalRequestId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace(); // Nên sử dụng logging trong dự án thực tế
            return false;
        }
    }

    // Phương thức để lấy danh sách phản hồi hiển thị ra listFeedback.jsp
    public List<FeedbackView> getFilteredFeedback(String query, String ratingFilter) {
    List<FeedbackView> list = new ArrayList<>();
    List<Object> params = new ArrayList<>(); // Danh sách để chứa các tham số cho PreparedStatement
    
    // Xây dựng câu lệnh SQL cơ bản
    StringBuilder sql = new StringBuilder(
        "SELECT f.id, f.rating, f.comment, f.status, f.created_at, "
      + "e.name AS enterpriseName, tr.title AS serviceName, tr.request_code "
      + "FROM Feedbacks f "
      + "JOIN Enterprises e ON f.enterprise_id = e.id "
      + "LEFT JOIN TechnicalRequests tr ON f.technical_request_id = tr.id "
      + "WHERE f.is_deleted = 0"
    );

    // 1. Thêm điều kiện tìm kiếm (query)
    if (query != null && !query.trim().isEmpty()) {
        sql.append(" AND (e.name LIKE ? OR tr.request_code LIKE ?)");
        params.add("%" + query.trim() + "%");
        params.add("%" + query.trim() + "%");
    }

    // 2. Thêm điều kiện lọc theo rating (ratingFilter)
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

    // Luôn sắp xếp kết quả
    sql.append(" ORDER BY f.created_at DESC");

    // Thực thi câu lệnh
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

        // Gán các tham số vào PreparedStatement
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
                fb.setServiceName(rs.getString("serviceName"));
                fb.setRequestCode(rs.getString("request_code"));
                list.add(fb);
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return list;
}

    public List<Enterprise> getAllEnterprises() {
        List<Enterprise> list = new ArrayList<>();
        // Lấy các doanh nghiệp chưa bị xóa, sắp xếp theo tên
        String sql = "SELECT id, name FROM Enterprises WHERE is_deleted = 0 ORDER BY name";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Enterprise e = new Enterprise();
                e.setId(rs.getInt("id"));
                e.setName(rs.getString("name"));
                list.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public boolean feedbackExistsForTechnicalRequest(int technicalRequestId) {
        String sql = "SELECT COUNT(*) FROM Feedbacks WHERE technical_request_id = ?";
        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, technicalRequestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu đếm được > 0, nghĩa là đã tồn tại
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Log lỗi
        }
        return false; // Mặc định là không tồn tại nếu có lỗi
    }

    /**
     * Lấy tổng số lượng feedback không bị xóa.
     *
     * @return Tổng số feedback.
     */
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

    /**
     * Lấy số lượng feedback dựa trên khoảng rating.
     *
     * @param minRating Rating tối thiểu
     * @param maxRating Rating tối đa
     * @return Số lượng feedback trong khoảng đó.
     */
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

    public FeedbackView getFeedbackById(int feedbackId) {
    FeedbackView feedback = null;
    
    // Đã xóa "e.phone" khỏi câu lệnh SQL
    String sql = "SELECT f.*, "
            + "e.name AS enterpriseName, e.business_email AS enterpriseEmail, "
            // 1. LẤY TÊN DỊCH VỤ TỪ BẢNG SERVICES (s.name) THAY VÌ tr.title
            + "s.name AS serviceName, "
            + "tr.request_code, "
            + "CONCAT_WS(' ', u.first_name, u.middle_name, u.last_name) AS technicianName "
            + "FROM Feedbacks f "
            + "JOIN Enterprises e ON f.enterprise_id = e.id "
            + "LEFT JOIN TechnicalRequests tr ON f.technical_request_id = tr.id "
            // 2. JOIN THÊM BẢNG SERVICES VÀO ĐÂY
            + "LEFT JOIN Services s ON tr.service_id = s.id "
            + "LEFT JOIN Users u ON tr.assigned_to_id = u.id "
            + "WHERE f.id = ?";

    try (Connection conn = new DBContext().getConnection(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {

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
                
                // Phần gán giá trị serviceName không cần thay đổi vì chúng ta đã dùng alias
                feedback.setServiceName(rs.getString("serviceName")); 
                
                feedback.setTechnicianName(rs.getString("technicianName"));
                feedback.setRelatedRequestId(rs.getInt("technical_request_id"));
                feedback.setRequestCode(rs.getString("request_code"));
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace(); 
    }

    return feedback;
}

    /**
     * Lấy danh sách các ghi chú nội bộ cho một feedback cụ thể.
     */
    public List<InternalNote> getNotesByFeedbackId(int feedbackId) {
        List<InternalNote> notes = new ArrayList<>();
        // Join với bảng Users để lấy tên nhân viên
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

    /**
     * Thêm một ghi chú nội bộ mới.
     */
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
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, newNoteText);
        ps.setInt(2, noteId);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

/**
 * Xóa mềm một ghi chú nội bộ (đánh dấu is_deleted = 1).
 * @param noteId ID của ghi chú cần xóa.
 * @return true nếu xóa thành công, false nếu thất bại.
 */
public boolean softDeleteInternalNote(int noteId) {
    // Giả định bạn có cột is_deleted trong bảng internal_notes
    String sql = "UPDATE internal_notes SET is_deleted = 1 WHERE id = ?";
    try (Connection conn = new DBContext().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, noteId);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

}
