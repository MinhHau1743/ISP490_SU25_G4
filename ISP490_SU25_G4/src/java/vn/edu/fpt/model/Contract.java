/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author datnt
 */
public class Contract {

    private long id;
    private String contractCode;
    private String name; // Tên hợp đồng (sẽ được tạo ở DAO)
    private String customerName; // Tên khách hàng (lấy từ bảng Enterprises)
    private Date signDate; // Ánh xạ từ cột start_date
    private Date expirationDate; // Ánh xạ từ cột end_date
    private BigDecimal value; // Giá trị hợp đồng (tính từ bảng ContractProducts)
    private String status;

    // Constructor mặc định
    public Contract() {
    }

    // Constructor đầy đủ tham số
    public Contract(long id, String contractCode, String name, String customerName, Date signDate, Date expirationDate, BigDecimal value, String status) {
        this.id = id;
        this.contractCode = contractCode;
        this.name = name;
        this.customerName = customerName;
        this.signDate = signDate;
        this.expirationDate = expirationDate;
        this.value = value;
        this.status = status;
    }

    // Getters and Setters cho tất cả các thuộc tính
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
