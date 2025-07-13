/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.edu.fpt.common;

import javax.mail.MessagingException;

/**
 *
 * @author minhh
 */
public class EmailService {
    private EmailSender emailSender;


    public EmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    public void sendEmail(String to, String subject, String messageText) throws MessagingException {
        emailSender.sendEmail(to, subject, messageText);
    }
    

    public void sendEmailAsync(String to, String subject, String messageText) {
        Runnable task = () -> {
            try {
                emailSender.sendEmail(to, subject, messageText);
            } catch (MessagingException e) {
                e.printStackTrace(); 
            }
        };
        new Thread(task).start();
    }
    
}
