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
    private String deviceName;
    private String serialNumber;
    private String problemDescription;

    // Constructors, Getters và Setters
    public TechnicalRequestDevice() {
    }

    public TechnicalRequestDevice(String deviceName, String serialNumber, String problemDescription) {
        this.deviceName = deviceName;
        this.serialNumber = serialNumber;
        this.problemDescription = problemDescription;
    }

    // (Vui lòng tự tạo các hàm getter và setter cho tất cả các trường trên)
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
