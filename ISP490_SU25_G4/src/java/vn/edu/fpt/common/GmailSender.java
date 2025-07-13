/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.common;

import vn.edu.fpt.common.EmailSender;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import vn.edu.fpt.utils.EmailConfig;

/**
 *
 * @author minhh
 */
public class GmailSender implements EmailSender{
    private final String from = EmailConfig.FROM_EMAIL;
    private final String password = EmailConfig.PASSWORD;

    @Override
    public void sendEmail(String to, String subject, String messageText) throws MessagingException {
        // Set up email properties
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Get the session object
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from, "DPCRM"));
        } catch (java.io.UnsupportedEncodingException e) {
            message.setFrom(new InternetAddress(from));
        }

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        message.setSubject(subject);

        message.setText(messageText);

        message.setContent(messageText, "text/html; charset=UTF8");
        Transport.send(message);
    }
}
