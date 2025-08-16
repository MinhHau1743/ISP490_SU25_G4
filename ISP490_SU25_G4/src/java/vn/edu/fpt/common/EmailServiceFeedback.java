/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.common;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author ducanh
 */
public class EmailServiceFeedback {
    // Các thông tin cấu hình được giữ nguyên ở đây

    private static final String FROM_EMAIL = "datnthe171279@fpt.edu.vn"; // Email của bạn
    private static final String PASSWORD = "lzrf xfts bgzb aaxb";      // Mật khẩu ứng dụng của Gmail

    /**
     * Gửi email khảo sát tới khách hàng. Phương thức này là static, có thể gọi
     * trực tiếp: EmailServiceFeedback.sendMail(...)
     *
     * @param toEmail Địa chỉ email người nhận.
     * @param subject Tiêu đề email.
     * @param body Nội dung email (hỗ trợ HTML).
     */
    public static void sendMail(String toEmail, String subject, String body) {
        // Cấu hình properties cho SMTP của Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Tạo đối tượng Authenticator để xác thực
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        };

        // Tạo phiên làm việc (Session)
        Session session = Session.getInstance(props, auth);

        try {
            Message message = new MimeMessage(session);
            // Đặt tên người gửi để email trông chuyên nghiệp hơn
            message.setFrom(new InternetAddress(FROM_EMAIL, "DPCRM - Chăm sóc khách hàng"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            // Giữ lại việc gửi nội dung dạng HTML để link khảo sát có thể bấm được
            message.setContent(body, "text/html; charset=utf-8");

            // Gửi email
            Transport.send(message);

            System.out.println("Email khảo sát đã được gửi thành công tới: " + toEmail);

        } catch (Exception e) {
            // Bắt Exception chung để bao gồm cả MessagingException và UnsupportedEncodingException
            System.err.println("Lỗi khi gửi email khảo sát: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
