/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author PC
 */
public class DBContext {

    protected Connection connection;

     public DBContext() {
        // Tự động khởi tạo connection khi tạo object DBContext/ProductDAO
        getConnection();
    }


    public Connection getConnection() {
        try {
            // Edit URL, username, password to authenticate with your MySQL Server
            String url = "jdbc:mysql://localhost:3306/datn";
            String userName = "root";
            String password = "1234";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, userName, password);
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex);
        }
        return connection;
    }

    public static void main(String[] args) {
        try {
            DBContext dbContext = new DBContext();
            Connection connection = dbContext.getConnection();

            if (connection != null) {
                System.out.println("Connected to MySQL database!");
                // Thực hiện các thao tác cần thiết với database ở đây

//                // Đóng kết nối sau khi sử dụng
                connection.close();
            } else {
                System.out.println("Failed to connect to MySQL database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
