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
public interface EmailSender {
     void sendEmail(String to, String subject, String messageText) throws MessagingException;
}
