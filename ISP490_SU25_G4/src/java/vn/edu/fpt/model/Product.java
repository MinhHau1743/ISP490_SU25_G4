/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;
/**
 *
 * @author PC
 */

public class Product {
    private int id;
    private String name;
    private String productCode;
    private String origin;
    private double price;
    private String description;
    private int categoryId;
    private String CategoryName;
    private boolean isDeleted;
    private String createdAt;
    private String updatedAt;

    public Product() {
    }

    public Product(int id, String name, String productCode, String origin, double price, String description, int categoryId, String CategoryName, boolean isDeleted, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.productCode = productCode;
        this.origin = origin;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.CategoryName = CategoryName;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }



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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name=" + name + ", productCode=" + productCode + ", origin=" + origin + ", price=" + price + ", description=" + description + ", categoryId=" + categoryId + ", CategoryName=" + CategoryName + ", isDeleted=" + isDeleted + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
    }
    
}
