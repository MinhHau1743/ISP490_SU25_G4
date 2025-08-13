package vn.edu.fpt.dao;

import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.ContractProduct;
import vn.edu.fpt.model.ContractStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO duy nhất quản lý các truy vấn liên quan đến Hợp đồng. Phiên bản này
 * đã được sửa lỗi tên cột và chuẩn hóa tên bảng.
 *
 * @author datnt (updated by AI)
 */
public class ContractDAO extends DBContext {

    /**
     * Lấy danh sách các sản phẩm/dịch vụ thuộc về một hợp đồng cụ thể.
     */
    public List<ContractProduct> getContractProductsByContractId(long contractId) {
        List<ContractProduct> items = new ArrayList<>();
        // ## FIX 1: Chuẩn hóa tên bảng thành snake_case ##
        String sql = "SELECT * FROM ContractProducts WHERE contract_id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractProduct item = new ContractProduct();
                    item.setId(rs.getLong("id"));
                    item.setContractId(rs.getLong("contract_id"));
                    item.setProductId(rs.getObject("product_id", Long.class));
                    item.setName(rs.getString("name"));
                    item.setProductCode(rs.getString("product_code"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy chi tiết sản phẩm của hợp đồng.");
            e.printStackTrace();
        }
        return items;
    }

    public boolean createContractWithItems(Contract contract, List<ContractProduct> items) {
        Connection conn = null;
        String contractSQL = "INSERT INTO contracts "
                + "(contract_code, contract_name, enterprise_id, created_by_id, start_date, end_date, signed_date, status_id, total_value, notes, file_url) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // KHẮC PHỤC: Sửa 'contract_products' thành 'ContractProducts'
        String itemsSQL = "INSERT INTO ContractProducts (contract_id, product_id, name, product_code, unit_price, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            long newContractId;
            try (PreparedStatement psContract = conn.prepareStatement(contractSQL, Statement.RETURN_GENERATED_KEYS)) {
                setContractStatementParameters(psContract, contract);
                psContract.executeUpdate();
                try (ResultSet generatedKeys = psContract.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newContractId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Tạo hợp đồng thất bại, không lấy được ID.");
                    }
                }
            }

            if (items != null && !items.isEmpty()) {
                try (PreparedStatement psItems = conn.prepareStatement(itemsSQL)) {
                    for (ContractProduct item : items) {
                        setContractProductStatementParameters(psItems, newContractId, item);
                        psItems.addBatch();
                    }
                    psItems.executeBatch();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("DAO ERROR: Giao dịch tạo hợp đồng thất bại, đang rollback... " + e.getMessage());
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
            closeConnection(conn);
        }
    }

    public boolean updateContractWithItems(Contract contract, List<ContractProduct> items) {
        Connection conn = null;
        String updateContractSQL = "UPDATE contracts SET "
                + "contract_code = ?, contract_name = ?, enterprise_id = ?, created_by_id = ?, "
                + "start_date = ?, end_date = ?, signed_date = ?, status_id = ?, total_value = ?, notes = ?, file_url = ? "
                + "WHERE id = ?";

        // KHẮC PHỤC: Sửa 'contract_products' thành 'ContractProducts'
        String deleteItemsSQL = "DELETE FROM ContractProducts WHERE contract_id = ?";
        String insertItemsSQL = "INSERT INTO ContractProducts (contract_id, product_id, name, product_code, unit_price, quantity) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(updateContractSQL)) {
                setContractStatementParameters(psUpdate, contract);
                psUpdate.setLong(12, contract.getId());
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(deleteItemsSQL)) {
                psDelete.setLong(1, contract.getId());
                psDelete.executeUpdate();
            }

            if (items != null && !items.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertItemsSQL)) {
                    for (ContractProduct item : items) {
                        setContractProductStatementParameters(psInsert, contract.getId(), item);
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("DAO ERROR: Giao dịch cập nhật hợp đồng thất bại, đang rollback... " + e.getMessage());
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
            closeConnection(conn);
        }
    }

    public List<Contract> getContracts(String searchQuery, String statusId, String startDateFrom, String startDateTo, int page, int pageSize) {
        List<Contract> contracts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, e.name AS enterpriseName, cs.name AS statusName "
                + "FROM contracts c "
                + "LEFT JOIN Enterprises e ON c.enterprise_id = e.id "
                + "LEFT JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.is_deleted = 0 "
        );
        buildWhereClause(sql, params, searchQuery, statusId, startDateFrom, startDateTo);
        sql.append("ORDER BY c.created_at DESC LIMIT ?, ?");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapResultSetToContract(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách hợp đồng. " + e.getMessage());
            e.printStackTrace();
        }
        return contracts;
    }

    public int getContractCount(String searchQuery, String statusId, String startDateFrom, String startDateTo) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM contracts c LEFT JOIN Enterprises e ON c.enterprise_id = e.id WHERE c.is_deleted = 0 ");
        buildWhereClause(sql, params, searchQuery, statusId, startDateFrom, startDateTo);
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi đếm số lượng hợp đồng. " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public Contract getContractById(int contractId) {
        String sql = "SELECT c.*, e.name AS enterpriseName, cs.name as statusName, "
                + "CONCAT_WS(' ', u.last_name, u.middle_name, u.first_name) as createdByName "
                + "FROM contracts c "
                + "LEFT JOIN Enterprises e ON c.enterprise_id = e.id "
                + "LEFT JOIN contract_statuses cs ON c.status_id = cs.id "
                + "LEFT JOIN users u ON c.created_by_id = u.id "
                + "WHERE c.id = ? AND c.is_deleted = 0";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToContract(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy chi tiết hợp đồng ID " + contractId + ". " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean softDeleteContract(int contractId) {
        String sql = "UPDATE contracts SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi xóa mềm hợp đồng ID " + contractId + ". " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Contract> getRecentContractsByEnterpriseId(int enterpriseId, int limit) {
        List<Contract> contracts = new ArrayList<>();

        // KHẮC PHỤC: Tên cột trong database là 'contract_name', không phải 'contract_title'
        String sql = "SELECT c.id, c.contract_code, c.contract_name, c.end_date, c.total_value, cs.name as statusName "
                + "FROM contracts c "
                + "LEFT JOIN contract_statuses cs ON c.status_id = cs.id "
                + "WHERE c.enterprise_id = ? AND c.is_deleted = 0 "
                + "ORDER BY c.end_date DESC, c.id DESC "
                + "LIMIT ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enterpriseId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract contract = new Contract();
                    contract.setId(rs.getLong("id"));
                    contract.setContractCode(rs.getString("contract_code"));

                    // KHẮC PHỤC: Lấy dữ liệu từ cột 'contract_name' cho đúng
                    contract.setContractName(rs.getString("contract_name"));

                    contract.setEndDate(rs.getDate("end_date"));
                    contract.setTotalValue(rs.getBigDecimal("total_value"));
                    contract.setStatusName(rs.getString("statusName"));
                    contracts.add(contract);
                }
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách hợp đồng gần đây của khách hàng. " + e.getMessage());
            e.printStackTrace();
        }
        return contracts;
    }

    public List<ContractStatus> getAllContractStatuses() {
        List<ContractStatus> statusList = new ArrayList<>();
        String sql = "SELECT id, name FROM contract_statuses ORDER BY id";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ContractStatus status = new ContractStatus();
                status.setId(rs.getInt("id"));
                status.setName(rs.getString("name"));
                statusList.add(status);
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách trạng thái hợp đồng. " + e.getMessage());
            e.printStackTrace();
        }
        return statusList;
    }

    private void buildWhereClause(StringBuilder sql, List<Object> params, String searchQuery, String statusId, String startDateFrom, String startDateTo) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // ## FIX 2: Sửa tên cột contract_name -> contract_title ##
            sql.append("AND (c.contract_code LIKE ? OR c.contract_title LIKE ? OR e.name LIKE ?) ");
            String searchPattern = "%" + searchQuery.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        if (statusId != null && !statusId.trim().isEmpty() && !statusId.equals("0")) {
            sql.append("AND c.status_id = ? ");
            params.add(Integer.parseInt(statusId));
        }
        if (startDateFrom != null && !startDateFrom.trim().isEmpty()) {
            sql.append("AND c.start_date >= ? ");
            params.add(startDateFrom);
        }
        if (startDateTo != null && !startDateTo.trim().isEmpty()) {
            sql.append("AND c.start_date <= ? ");
            params.add(startDateTo);
        }
    }

    private void setParameters(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private Contract mapResultSetToContract(ResultSet rs) throws SQLException {
        Contract contract = new Contract();
        contract.setId(rs.getLong("id"));
        contract.setContractCode(rs.getString("contract_code"));
        // ## FIX 2: Sửa tên cột contract_name -> contract_title ##
        contract.setContractName(rs.getString("contract_name"));;
        contract.setEnterpriseId(rs.getLong("enterprise_id"));
        contract.setCreatedById(rs.getObject("created_by_id", Long.class));
        contract.setStartDate(rs.getDate("start_date"));
        contract.setEndDate(rs.getDate("end_date"));
        contract.setSignedDate(rs.getDate("signed_date"));
        contract.setStatusId(rs.getInt("status_id"));
        contract.setTotalValue(rs.getBigDecimal("total_value"));
        contract.setNotes(rs.getString("notes"));
        contract.setFileUrl(rs.getString("file_url"));
        if (hasColumn(rs, "enterpriseName")) {
            contract.setEnterpriseName(rs.getString("enterpriseName"));
        }
        if (hasColumn(rs, "statusName")) {
            contract.setStatusName(rs.getString("statusName"));
        }
        if (hasColumn(rs, "createdByName")) {
            contract.setCreatedByName(rs.getString("createdByName"));
        }
        return contract;
    }

    private void setContractStatementParameters(PreparedStatement ps, Contract contract) throws SQLException {
        ps.setString(1, contract.getContractCode());
        ps.setString(2, contract.getContractName());
        ps.setLong(3, contract.getEnterpriseId());
        ps.setObject(4, contract.getCreatedById());
        ps.setDate(5, contract.getStartDate() != null ? new java.sql.Date(contract.getStartDate().getTime()) : null);
        ps.setDate(6, contract.getEndDate() != null ? new java.sql.Date(contract.getEndDate().getTime()) : null);
        ps.setDate(7, contract.getSignedDate() != null ? new java.sql.Date(contract.getSignedDate().getTime()) : null);
        ps.setInt(8, contract.getStatusId());
        ps.setBigDecimal(9, contract.getTotalValue());
        ps.setString(10, contract.getNotes());
        ps.setString(11, contract.getFileUrl());
    }

    private void setContractProductStatementParameters(PreparedStatement ps, long contractId, ContractProduct item) throws SQLException {
        ps.setLong(1, contractId);
        ps.setObject(2, item.getProductId());
        ps.setString(3, item.getName());
        ps.setString(4, item.getProductCode());
        ps.setBigDecimal(5, item.getUnitPrice());
        ps.setInt(6, item.getQuantity());
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Thay thế phương thức cũ trong ContractDAO.java bằng phương thức này
    public List<Contract> getAllActiveContracts() throws SQLException {
        List<Contract> contracts = new ArrayList<>();

        // ## FIX: Cập nhật tên cột thành `contract_name` theo xác nhận mới nhất ##
        String sql = "SELECT id, contract_code, contract_name "
                + "FROM contracts "
                + "WHERE status_id = (SELECT id FROM contract_statuses WHERE name = 'Đang triển khai') "
                + "AND is_deleted = 0 "
                + "ORDER BY created_at DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contract c = new Contract();
                c.setId(rs.getLong("id"));
                c.setContractCode(rs.getString("contract_code"));
                // ## FIX: Lấy dữ liệu từ cột 'contract_name' ##
                c.setContractName(rs.getString("contract_name"));
                contracts.add(c);
            }
        } catch (Exception e) {
            System.err.println("DAO ERROR: Lỗi khi lấy danh sách hợp đồng đang hoạt động.");
            e.printStackTrace();
            throw new SQLException("Lỗi khi truy vấn danh sách hợp đồng.", e);
        }
        return contracts;
    }
}
