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
            "SELECT c.*, e.name AS enterpriseName " +
            "FROM Contracts c " +
            "LEFT JOIN Enterprises e ON c.enterprise_id = e.id " +
            "WHERE c.is_deleted = 0 "
        );

        // Xây dựng mệnh đề WHERE động
        buildWhereClause(sql, params, searchQuery, status, startDateFrom, startDateTo);

        // Thêm sắp xếp và phân trang
        sql.append("ORDER BY c.created_at DESC LIMIT ?, ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Gán các tham số vào câu lệnh SQL
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Contracts c LEFT JOIN Enterprises e ON c.enterprise_id = e.id WHERE c.is_deleted = 0 ");

        // Sử dụng lại cùng logic xây dựng mệnh đề WHERE
        buildWhereClause(sql, params, searchQuery, status, startDateFrom, startDateTo);

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

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
        // Điều kiện tìm kiếm theo từ khóa
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql.append("AND (c.contract_code LIKE ? OR c.contract_name LIKE ? OR e.name LIKE ?) ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        // Điều kiện lọc theo trạng thái
        if (status != null && !status.isEmpty()) {
            sql.append("AND c.status = ? ");
            params.add(status);
        }
        // Điều kiện lọc theo khoảng ngày bắt đầu
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

    /**
     * Lấy danh sách các sản phẩm/dịch vụ thuộc về một hợp đồng cụ thể.
     *
     * @param contractId ID của hợp đồng cần lấy chi tiết.
     * @return Danh sách các đối tượng ContractProduct.
     */
    public List<ContractProduct> getContractProductsByContractId(long contractId) {
        List<ContractProduct> items = new ArrayList<>();
        String sql = "SELECT * FROM ContractProducts WHERE contract_id = ?";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractProduct item = new ContractProduct();
                    item.setId(rs.getLong("id"));
                    item.setContractId(rs.getLong("contract_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setName(rs.getString("name"));
                    item.setProductCode(rs.getString("product_code"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setDescription(rs.getString("description"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy chi tiết sản phẩm của hợp đồng.");
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Cập nhật một hợp đồng và danh sách sản phẩm/dịch vụ đi kèm. Quá trình
     * được bọc trong một transaction để đảm bảo toàn vẹn dữ liệu. Sử dụng chiến
     * lược "xóa tất cả chi tiết cũ và chèn lại chi tiết mới".
     *
     * @param contract Đối tượng Contract chứa thông tin chung đã được cập nhật.
     * @param items Danh sách các đối tượng ContractProduct mới.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateContractWithItems(Contract contract, List<ContractProduct> items) {
        Connection conn = null;

        // Câu lệnh cập nhật thông tin chính của hợp đồng
        String updateContractSQL = "UPDATE Contracts SET "
                + "contract_code = ?, contract_name = ?, enterprise_id = ?, created_by_id = ?, "
                + "start_date = ?, end_date = ?, signed_date = ?, status = ?, total_value = ?, notes = ? "
                + "WHERE id = ?";

        // Câu lệnh xóa tất cả các sản phẩm cũ thuộc hợp đồng này
        String deleteItemsSQL = "DELETE FROM ContractProducts WHERE contract_id = ?";

        // Câu lệnh chèn lại các sản phẩm mới
        String insertItemsSQL = "INSERT INTO ContractProducts "
                + "(contract_id, product_id, name, product_code, unit_price, quantity, description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // BƯỚC 1: CẬP NHẬT THÔNG TIN HỢP ĐỒNG CHÍNH
            try (PreparedStatement psUpdate = conn.prepareStatement(updateContractSQL)) {
                psUpdate.setString(1, contract.getContractCode());
                psUpdate.setString(2, contract.getContractName());
                psUpdate.setLong(3, contract.getEnterpriseId());
                psUpdate.setObject(4, contract.getCreatedById());
                psUpdate.setDate(5, contract.getStartDate());
                psUpdate.setDate(6, contract.getEndDate());
                psUpdate.setDate(7, contract.getSignedDate());
                psUpdate.setString(8, contract.getStatus());
                psUpdate.setBigDecimal(9, contract.getTotalValue());
                psUpdate.setString(10, contract.getNotes());
                psUpdate.setLong(11, contract.getId()); // Điều kiện WHERE
                psUpdate.executeUpdate();
            }

            // BƯỚC 2: XÓA TẤT CẢ SẢN PHẨM CŨ CỦA HỢP ĐỒNG NÀY
            try (PreparedStatement psDelete = conn.prepareStatement(deleteItemsSQL)) {
                psDelete.setLong(1, contract.getId());
                psDelete.executeUpdate();
            }

            // BƯỚC 3: CHÈN LẠI DANH SÁCH SẢN PHẨM MỚI (nếu có)
            if (items != null && !items.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertItemsSQL)) {
                    for (ContractProduct item : items) {
                        psInsert.setLong(1, contract.getId());
                        psInsert.setObject(2, item.getProductId());
                        psInsert.setString(3, item.getName());
                        psInsert.setString(4, item.getProductCode());
                        psInsert.setBigDecimal(5, item.getUnitPrice());
                        psInsert.setInt(6, item.getQuantity());
                        psInsert.setString(7, item.getDescription());
                        psInsert.addBatch(); // Thêm vào lô lệnh để thực thi cùng lúc
                    }
                    psInsert.executeBatch(); // Thực thi lô lệnh
                }
            }

            conn.commit(); // Lưu vĩnh viễn tất cả thay đổi nếu không có lỗi
            return true;

        } catch (Exception e) {
            System.err.println("DAO ERROR: Giao dịch cập nhật hợp đồng thất bại, đang rollback...");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác tất cả thay đổi nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Thực hiện xóa mềm một hợp đồng bằng cách cập nhật cờ is_deleted = 1.
     *
     * @param contractId ID của hợp đồng cần xóa.
     * @return true nếu xóa thành công (1 dòng được cập nhật), ngược lại trả về
     * false.
     * @throws Exception nếu có lỗi xảy ra.
     */
    public boolean softDeleteContract(int contractId) throws Exception {
        // Câu lệnh SQL chỉ cập nhật cờ is_deleted
        String sql = "UPDATE Contracts SET is_deleted = 1 WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contractId);

            // executeUpdate() trả về số dòng bị ảnh hưởng. 
            // Nếu > 0 nghĩa là đã cập nhật thành công.
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Lấy danh sách các hợp đồng gần đây nhất của một doanh nghiệp.
     * @param enterpriseId ID của doanh nghiệp.
     * @param limit Số lượng hợp đồng tối đa cần lấy.
     * @return Danh sách các đối tượng Contract.
     * @throws Exception nếu có lỗi xảy ra.
     */
    public List<Contract> getRecentContractsByEnterpriseId(int enterpriseId, int limit) throws Exception {
        List<Contract> contracts = new ArrayList<>();
        // Lấy các cột cần thiết để hiển thị trên trang chi tiết khách hàng
        String sql = "SELECT id, contract_code, contract_name, signed_date, total_value, status " +
                     "FROM Contracts " +
                     "WHERE enterprise_id = ? AND is_deleted = 0 " +
                     "ORDER BY signed_date DESC, id DESC " +
                     "LIMIT ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract contract = new Contract();
                    contract.setId(rs.getLong("id"));
                    contract.setContractCode(rs.getString("contract_code"));
                    contract.setContractName(rs.getString("contract_name"));
                    contract.setSignedDate(rs.getDate("signed_date"));
                    contract.setTotalValue(rs.getBigDecimal("total_value"));
                    contract.setStatus(rs.getString("status"));
                    contracts.add(contract);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để dễ dàng gỡ rối
            throw new Exception("Lỗi khi lấy danh sách hợp đồng gần đây: " + e.getMessage());
        }
        return contracts;
    }
}
