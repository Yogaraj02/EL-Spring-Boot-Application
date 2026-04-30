package com.college.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Welcome to College Event Management System!");
            message.setText("Dear " + name + ",\n\n" +
                    "Thank you for registering. You can now log in and join exciting college events!\n\n" +
                    "Best regards,\nEvent Management Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendEventRegistrationEmail(String toEmail, String name, String eventName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Registration Confirmed: " + eventName);
            message.setText("Dear " + name + ",\n\n" +
                    "Your registration and payment for '" + eventName + "' were successful. We look forward to seeing you there!\n\n" +
                    "Best regards,\nEvent Management Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send event registration email to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendCertificateEmail(String toEmail, String name, String eventName, String certType, byte[] pdfAttachment) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);
            
            helper.setTo(toEmail);
            helper.setSubject("Certificate Issued: " + eventName);
            helper.setText("Dear " + name + ",\n\n" +
                    "Congratulations! You have been issued a '" + certType + "' certificate for the event '" + eventName + "'.\n" +
                    "Please find your certificate attached to this email.\n\n" +
                    "Best regards,\nEvent Management Team");
            
            if (pdfAttachment != null) {
                helper.addAttachment("Certificate_" + eventName.replaceAll(" ", "_") + ".pdf", new org.springframework.core.io.ByteArrayResource(pdfAttachment));
            }
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send certificate email to " + toEmail + ": " + e.getMessage());
        }
    }
}
