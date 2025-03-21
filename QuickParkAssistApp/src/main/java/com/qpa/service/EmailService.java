package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Injects the JavaMailSender to send emails

    /**
     * Sends a registration confirmation email to the specified recipient.
     * 
     * @param toEmail  The recipient's email address.
     * @param username The username of the registered user.
     */
    public void sendRegistrationEmail(String toEmail, String username) {
        // Create a simple email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail); // Set recipient email
        message.setFrom("baghlaamit06@gmail.com"); // Sender email address
        message.setSubject("Registration Successful"); // Email subject
        message.setText("Hello " + username + ",\n\nYour profile has been successfully registered.\n\nThank you!");

        // Send the email
        mailSender.send(message);
    }
}
