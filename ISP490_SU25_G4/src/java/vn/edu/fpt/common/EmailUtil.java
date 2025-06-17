/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.common;

/**
 *
 * @author ducanh
 */
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    public static void sendOTP(String toEmail, String otpCode) {
        final String fromEmail = "anhndhe172050@fpt.edu.vn";        // Gmail của bạn
        final String password = "gmkw ouil irgq hktz";           // App Password (không phải mật khẩu Gmail)

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");         // SMTP của Gmail
        props.put("mail.smtp.port", "587");                    // TLS port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");        // Bắt buộc dùng TLS

        // Xác thực
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        // Session gửi
        Session session = Session.getInstance(props, auth);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "DPCRM - Hệ thống đăng ký"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject("Mã xác thực đăng ký tài khoản DPCRM");
            msg.setText("Mã OTP của bạn là: " + otpCode + "\nMã có hiệu lực trong 5 phút.");

            Transport.send(msg);
            System.out.println("Đã gửi OTP tới: " + toEmail);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
