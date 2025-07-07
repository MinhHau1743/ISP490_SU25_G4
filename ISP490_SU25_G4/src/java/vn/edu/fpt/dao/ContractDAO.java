package vn.edu.fpt.dao;

import vn.edu.fpt.model.Contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import vn.edu.fpt.model.ContractProduct;
import java.sql.SQLException;

/**
 * Lớp này chứa các phương thức truy vấn CSDL cho đối tượng Contract. Phiên bản
 * được cập nhật để khớp với cấu trúc bảng tối ưu.
 *
 * @author datnt
 */
public class ContractDAO extends DBContext {

    /**
     * Lấy danh sách hợp đồng, có hỗ trợ tìm kiếm, lọc và phân trang.
     */
    public List<Contract> getContracts(String searchQuery, String status, String startDateFrom, String startDateTo, int page, int pageSize) {
        List<Contract> contracts = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT "
                + "    c.*, "
                + "    e.name AS enterpriseName "
                + "FROM Contracts c "
                + "LEFT JOIN Enterprises e ON c.enterprise_id = e.id "
                + "WHERE c.is_deleted = 0 "
        );

        buildWhereClause(sql, params, searchQuery, status, startDateFrom, startDateTo);

        sql.append("ORDER BY c.created_at DESC ");
        sql.append("LIMIT ?, ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract contract = new Contract();
                    contract.setId(rs.getLong("id"));
                    contract.setContractCode(rs.getString("contract_code"));
                    contract.setContractName(rs.getString("contract_name"));
                    contract.setEnterpriseId(rs.getLong("enterprise_id"));

                    // SỬA LỖI: Dùng rs.getDate() để lấy java.sql.Date
                    contract.setStartDate(rs.getDate("start_date"));
                    contract.setEndDate(rs.getDate("end_date"));

                    contract.setTotalValue(rs.getBigDecimal("total_value"));
                    contract.setStatus(rs.getString("status"));
                    contract.setEnterpriseName(rs.getString("enterpriseName"));
                    contracts.add(contract);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách hợp đồng.");
            e.printStackTrace();
        }
        return contracts;
    }

    /**
     * Đếm tổng số hợp đồng thỏa mãn điều kiện lọc để tính toán phân trang.
     */
    public int getContractCount(String searchQuery, String status, String startDateFrom, String startDateTo) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Contracts c WHERE c.is_deleted = 0 ");

        buildWhereClause(sql, params, searchQuery, status, startDateFrom, startDateTo);

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi đếm số lượng hợp đồng.");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Phương thức private helper để xây dựng mệnh đề WHERE, tránh lặp code.
     */
    private void buildWhereClause(StringBuilder sql, List<Object> params, String searchQuery, String status, String startDateFrom, String startDateTo) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (c.contract_code LIKE ? OR c.contract_name LIKE ?) ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        if (status != null && !status.isEmpty()) {
            sql.append("AND c.status = ? ");
            params.add(status);
        }
        if (startDateFrom != null && !startDateFrom.isEmpty()) {
            sql.append("AND c.start_date >= ? ");
            params.add(startDateFrom);
        }
        if (startDateTo != null && !startDateTo.isEmpty()) {
            sql.append("AND c.start_date <= ? ");
            params.add(startDateTo);
        }
    }

    /**
     * Lấy thông tin chi tiết của một hợp đồng theo ID.
     */
    public Contract getContractById(int contractId) {
        Contract contract = null;
        String sql = "SELECT "
                + "    c.*, "
                + "    e.name AS enterpriseName, "
                + "    u.first_name, u.last_name, u.middle_name "
                + "FROM Contracts c "
                + "LEFT JOIN Enterprises e ON c.enterprise_id = e.id "
                + "LEFT JOIN Users u ON c.created_by_id = u.id "
                + "WHERE c.id = ? AND c.is_deleted = 0";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contract = new Contract();
                    contract.setId(rs.getLong("id"));
                    contract.setContractCode(rs.getString("contract_code"));
                    contract.setContractName(rs.getString("contract_name"));
                    contract.setEnterpriseId(rs.getLong("enterprise_id"));
//                    contract.setContractTypeId(rs.getObject("contract_type_id", Long.class));
                    contract.setCreatedById(rs.getObject("created_by_id", Long.class));

                    // SỬA LỖI: Dùng rs.getDate() để lấy java.sql.Date
                    contract.setStartDate(rs.getDate("start_date"));
                    contract.setEndDate(rs.getDate("end_date"));
                    contract.setSignedDate(rs.getDate("signed_date"));

                    contract.setStatus(rs.getString("status"));
                    contract.setTotalValue(rs.getBigDecimal("total_value"));
                    contract.setNotes(rs.getString("notes"));
                    contract.setFileUrl(rs.getString("file_url"));
                    contract.setRenewedFromContractId(rs.getObject("renewed_from_contract_id", Long.class));
                    contract.setEnterpriseName(rs.getString("enterpriseName"));
                    String createdByFullName = String.join(" ",
                            rs.getString("last_name") != null ? rs.getString("last_name") : "",
                            rs.getString("middle_name") != null ? rs.getString("middle_name") : "",
                            rs.getString("first_name") != null ? rs.getString("first_name") : ""
                    ).trim().replaceAll("\\s+", " ");
                    contract.setCreatedByName(createdByFullName);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy chi tiết hợp đồng.");
            e.printStackTrace();
        }
        return contract;
    }

    /**
     * Tạo một hợp đồng mới cùng với các sản phẩm đi kèm trong một transaction.
     *
     * @param contract Đối tượng Contract chứa thông tin chung (bao gồm mã hợp
     * đồng do người dùng nhập).
     * @param items Danh sách các đối tượng ContractProduct chứa chi tiết sản
     * phẩm (snapshot).
     * @return true nếu tạo thành công, false nếu thất bại.
     */
    public boolean createContractWithItems(Contract contract, List<ContractProduct> items) {
        Connection conn = null;

        // CÂU LỆNH SQL ĐÃ SỬA ĐÚNG: 11 CỘT VÀ 11 DẤU HỎI
        String contractSQL = "INSERT INTO Contracts "
                + "(contract_code, contract_name, enterprise_id, created_by_id, start_date, end_date, signed_date, status, total_value, notes, file_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String itemsSQL = "INSERT INTO ContractProducts "
                + "(contract_id, product_id, name, product_code, unit_price, quantity, description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            long newContractId = 0;

            // BƯỚC 1: CHÈN HỢP ĐỒNG CHÍNH
            try (PreparedStatement psContract = conn.prepareStatement(contractSQL, Statement.RETURN_GENERATED_KEYS)) {

                // CÁC THAM SỐ ĐÃ SỬA ĐÚNG: 11 THAM SỐ
                psContract.setString(1, contract.getContractCode());
                psContract.setString(2, contract.getContractName());
                psContract.setLong(3, contract.getEnterpriseId());
                psContract.setObject(4, contract.getCreatedById());
                psContract.setDate(5, contract.getStartDate());
                psContract.setDate(6, contract.getEndDate());
                psContract.setDate(7, contract.getSignedDate());
                psContract.setString(8, contract.getStatus());
                psContract.setBigDecimal(9, contract.getTotalValue());
                psContract.setString(10, contract.getNotes());
                psContract.setString(11, contract.getFileUrl());

                psContract.executeUpdate();

                try (ResultSet generatedKeys = psContract.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newContractId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Tạo hợp đồng thất bại, không lấy được ID.");
                    }
                }
            }

            // BƯỚC 2: CHÈN CHI TIẾT SẢN PHẨM (BATCH INSERT)
            if (items != null && !items.isEmpty()) {
                try (PreparedStatement psItems = conn.prepareStatement(itemsSQL)) {
                    for (ContractProduct item : items) {
                        psItems.setLong(1, newContractId);
                        psItems.setObject(2, item.getProductId());
                        psItems.setString(3, item.getName());
                        psItems.setString(4, item.getProductCode());
                        psItems.setBigDecimal(5, item.getUnitPrice());
                        psItems.setInt(6, item.getQuantity());
                        psItems.setString(7, item.getDescription());
                        psItems.addBatch();
                    }
                    psItems.executeBatch();
                }
            }

            conn.commit(); // Lưu vĩnh viễn các thay đổi nếu mọi thứ thành công
            return true;

        } catch (Exception e) {
            System.err.println("DAO ERROR: Giao dịch tạo hợp đồng thất bại, đang rollback...");
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

}
