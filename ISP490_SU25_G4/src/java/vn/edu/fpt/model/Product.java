/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author PC
 */
public class Product {

    private int id;
    private String name;
    private String productCode;
    private String image;
    private String origin;
    private BigDecimal price; // Sửa từ double sang BigDecimal để đảm bảo chính xác về tài chính
    private String description;
    private boolean isDeleted;
    private Timestamp createdAt; // Sửa từ String sang Timestamp để phù hợp với SQL
    private Timestamp updatedAt; // Sửa từ String sang Timestamp để phù hợp với SQL
    private int createdBy;       // Sửa từ String sang int để lưu ID người tạo
    private int updatedBy;       // Sửa từ String sang int để lưu ID người cập nhật
    // THÊM 2 THUỘC TÍNH MỚI NÀY:
    private String createdByName;
    private String updatedByName;

    public Product() {
    }

    public Product(int id, String name, String productCode, String image, String origin, BigDecimal price, String description, boolean isDeleted, Timestamp createdAt, Timestamp updatedAt, int createdBy, int updatedBy) {
        this.id = id;
        this.name = name;
        this.productCode = productCode;
        this.image = image;
        this.origin = origin;
        this.price = price;
        this.description = description;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // --- Getters and Setters đã được cập nhật ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Sửa tên getter cho đúng chuẩn Java Beans
    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    // THÊM 4 PHƯƠNG THỨC GETTER/SETTER MỚI NÀY VÀO CUỐI LỚP:
    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    // Trong file model/Product.java
    @Override
    public String toString() {
        return "Product{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", productCode='" + productCode + '\''
                + ", image='" + image + '\''
                + ", origin='" + origin + '\''
                + ", price=" + price
                + ", isDeleted=" + isDeleted
                + ", createdAt=" + createdAt
                + ", updatedBy=" + updatedBy
                + '}';
    }
}
