/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Sử dụng LocalDateTime cho cột TIMESTAMP

/**
 * Lớp này đại diện cho một sản phẩm trong một hợp đồng cụ thể.
 * Nó ánh xạ với bảng 'ContractProducts' trong cơ sở dữ liệu,
 * bao gồm cả các thông tin sản phẩm được "sao chép" (snapshot) tại thời điểm ký.
 *
 * @author datnt
 */
public class ContractProduct {

    private long id;
    private long contractId;
    private Long productId; // Kiểu Long để có thể nhận giá trị null

    // =================================================================
    // CÁC TRƯỜNG THÔNG TIN SẢN PHẨM ĐƯỢC "SAO CHÉP" (SNAPSHOT)
    // =================================================================
    private String name;
    private String productCode;
    private String image;
    private String origin;
    private String description;
    private BigDecimal unitPrice;
    private int quantity;
    
    // =================================================================
    // CÁC TRƯỜNG SIÊU DỮ LIỆU (METADATA)
    // =================================================================
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors ---

    public ContractProduct() {
    }

    /**
     * Constructor đầy đủ tham số.
     */
    public ContractProduct(long id, long contractId, Long productId, String name, String productCode, String image, String origin, String description, BigDecimal unitPrice, int quantity, boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.contractId = contractId;
        this.productId = productId;
        this.name = name;
        this.productCode = productCode;
        this.image = image;
        this.origin = origin;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ContractProduct{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", productCode='" + productCode + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}


// Have You Ever Race UmaMusume Cost Your Life ?