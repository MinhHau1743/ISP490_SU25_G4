/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 *
 * @author phamh
 */
public class MaintenanceSchedule {

    private int id;
    private int technicalRequestId;
    private String title;
    private String color;
    private LocalDate scheduledDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer addressId;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MaintenanceAssignments> assignments;
    private Address fullAddress;

    public MaintenanceSchedule() {
    }

    public MaintenanceSchedule(int id, int technicalRequestId, String title, String color, LocalDate scheduledDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, Integer addressId, String status, String notes, LocalDateTime createdAt, LocalDateTime updatedAt, List<MaintenanceAssignments> assignments, Address fullAddress) {
        this.id = id;
        this.technicalRequestId = technicalRequestId;
        this.title = title;
        this.color = color;
        this.scheduledDate = scheduledDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.addressId = addressId;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignments = assignments;
        this.fullAddress = fullAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTechnicalRequestId() {
        return technicalRequestId;
    }

    public void setTechnicalRequestId(Integer technicalRequestId) {
        this.technicalRequestId = technicalRequestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Address getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(Address fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public List<MaintenanceAssignments> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<MaintenanceAssignments> assignments) {
        this.assignments = assignments;
    }

}
