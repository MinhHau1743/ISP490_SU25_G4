/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.model;

/**
 *
 * @author minhh
 */
public class Department {
    private int id;
    private String name;

    // Hàm khởi tạo (Constructors)
    public Department() {}

    public Department(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Các hàm Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
