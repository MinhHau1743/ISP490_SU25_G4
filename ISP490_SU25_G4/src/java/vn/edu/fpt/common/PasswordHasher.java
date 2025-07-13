/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.common;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author PC
 */
public class PasswordHasher {

    public static void main(String[] args) {
        // Bạn có thể đổi mật khẩu ở đây nếu muốn
        String matKhauCanBam = "123456";

        // Dòng này sẽ tạo ra một chuỗi hash mới mỗi lần chạy
        String hashMoi = BCrypt.hashpw(matKhauCanBam, BCrypt.gensalt());

        System.out.println("Mat khau: " + matKhauCanBam);
        System.out.println("Chuoi hash moi da tao: " + hashMoi);
    }
}
