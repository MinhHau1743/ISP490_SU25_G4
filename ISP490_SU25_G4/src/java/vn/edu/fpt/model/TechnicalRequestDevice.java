/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

/**
 *
 * @author NGUYEN MINH
 */
public class TechnicalRequestDevice {

    private int id;
    private int technicalRequestId;
    private Integer productId; // THÊM: ID của sản phẩm được chọn từ dropdown
    private String deviceName;
    private String serialNumber;
    private String problemDescription;

    // Constructors
    public TechnicalRequestDevice() {
    }

    public TechnicalRequestDevice(Integer productId, String deviceName, String serialNumber, String problemDescription) {
        this.productId = productId;
        this.deviceName = deviceName;
        this.serialNumber = serialNumber;
        this.problemDescription = problemDescription;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTechnicalRequestId() {
        return technicalRequestId;
    }

    public void setTechnicalRequestId(int technicalRequestId) {
        this.technicalRequestId = technicalRequestId;
    }

    // THÊM: Getter và Setter cho productId
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }
}
