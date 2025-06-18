/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

/**
 *
 * @author ducanh
 */
public class EnterpriseContact {
    private int id;
    private int enterpriseId;
    private String fullName;
    private String position;
    private String email;
    private String phoneNumber;
    private boolean isPrimaryContact;
    private String notes;

    public EnterpriseContact() {
    }

    public EnterpriseContact(int id, int enterpriseId, String fullName, String position, String email, String phoneNumber, boolean isPrimaryContact, String notes) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.fullName = fullName;
        this.position = position;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isPrimaryContact = isPrimaryContact;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "EnterpriseContact{" + "id=" + id + ", enterpriseId=" + enterpriseId + ", fullName=" + fullName + ", position=" + position + ", email=" + email + ", phoneNumber=" + phoneNumber + ", isPrimaryContact=" + isPrimaryContact + ", notes=" + notes + '}';
    }
    
    
}
