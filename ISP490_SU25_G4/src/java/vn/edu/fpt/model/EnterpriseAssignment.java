/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

import java.sql.Timestamp;

/**
 *
 * @author ducanh
 */
public class EnterpriseAssignment {

    private int enterpriseId;
    private int userId;
    private String assignmentType;
    private Timestamp createdAt;

    // Constructors
    public EnterpriseAssignment() {
    }

    public EnterpriseAssignment(int enterpriseId, int userId, String assignmentType) {
        this.enterpriseId = enterpriseId;
        this.userId = userId;
        this.assignmentType = assignmentType;
    }

    // Getters and Setters
    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
