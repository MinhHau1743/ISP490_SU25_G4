// File: vn/edu/fpt/model/TechnicalRequest.java
package vn.edu.fpt.model;

import java.sql.Timestamp;
import java.util.List;

public class TechnicalRequest {

    private int id;
    private String requestCode;
    private int enterpriseId;
    private Integer contractId;
    private int serviceId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private int reporterId;
    private Integer assignedToId;
    private boolean isBillable;
    private double estimatedCost;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    // Các trường ảo để hiển thị
    private String enterpriseName;
    private String contractCode;
    private String serviceName;
    private String assignedToName;
    private String reporterName;
    private List<Integer> assignedUserIds;
    // SỬA LẠI Ở ĐÂY: Đảm bảo sử dụng đúng danh sách TechnicalRequestDevice
    private List<TechnicalRequestDevice> devices;
    private String enterpriseEmail;

    public TechnicalRequest() {
    }

    public String getEnterpriseEmail() {
        return enterpriseEmail;
    }

    public void setEnterpriseEmail(String enterpriseEmail) {
        this.enterpriseEmail = enterpriseEmail;
    }
    
    // Getters and Setters cho tất cả các trường...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public Integer getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Integer assignedToId) {
        this.assignedToId = assignedToId;
    }

    public boolean isIsBillable() {
        return isBillable;
    }

    public void setIsBillable(boolean isBillable) {
        this.isBillable = isBillable;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Timestamp resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    // SỬA LẠI Ở ĐÂY: Getter và Setter cho devices
    public List<TechnicalRequestDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<TechnicalRequestDevice> devices) {
        this.devices = devices;
    }

    public List<Integer> getAssignedUserIds() {
        return assignedUserIds;
    }

    public void setAssignedUserIds(List<Integer> assignedUserIds) {
        this.assignedUserIds = assignedUserIds;
    }

}
