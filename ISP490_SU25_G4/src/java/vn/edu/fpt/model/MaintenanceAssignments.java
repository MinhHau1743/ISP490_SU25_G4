/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model; // Hoặc package phù hợp của bạn

public class MaintenanceAssignments {

    private int id;
    private int maintenanceScheduleId;
    private int userId;
    private String fullName;

    public MaintenanceAssignments() {
    }

    public MaintenanceAssignments(int id, int maintenanceScheduleId, int userId, String fullName) {
        this.id = id;
        this.maintenanceScheduleId = maintenanceScheduleId;
        this.userId = userId;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaintenanceScheduleId() {
        return maintenanceScheduleId;
    }

    public void setMaintenanceScheduleId(int maintenanceScheduleId) {
        this.maintenanceScheduleId = maintenanceScheduleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
