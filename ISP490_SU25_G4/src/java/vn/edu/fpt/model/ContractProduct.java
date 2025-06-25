/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;

/**
 * Lớp này đại diện cho một sản phẩm trong một hợp đồng cụ thể.
 * Nó ánh xạ với bảng 'ContractProducts' trong cơ sở dữ liệu.
 *
 * @author datnt
 */
public class ContractProduct {

    /**
     * ID tự tăng của bản ghi trong bảng ContractProducts.
     */
    private long id;

    /**
     * ID của hợp đồng mà sản phẩm này thuộc về.
     * Liên kết với bảng 'Contracts'.
     */
    private long contractId;

    /**
     * ID của sản phẩm được thêm vào hợp đồng.
     * Liên kết với bảng 'Products'.
     */
    private long productId;

    /**
     * Số lượng sản phẩm.
     */
    private int quantity;

    /**
     * Đơn giá của sản phẩm tại thời điểm ký hợp đồng.
     * Sử dụng BigDecimal để đảm bảo độ chính xác cho các phép tính tài chính.
     */
    private BigDecimal unitPrice;

    // --- Constructors ---

    /**
     * Constructor mặc định không tham số.
     */
    public ContractProduct() {
    }

    /**
     * Constructor đầy đủ tham số để khởi tạo một đối tượng ContractProduct.
     *
     * @param id          ID của bản ghi
     * @param contractId  ID của hợp đồng
     * @param productId   ID của sản phẩm
     * @param quantity    Số lượng
     * @param unitPrice   Đơn giá
     */
    public ContractProduct(long id, long contractId, long productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.contractId = contractId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // --- Getters and Setters ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    // --- Optional: toString() method for debugging ---
    
    @Override
    public String toString() {
        return "ContractProduct{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}