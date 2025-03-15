package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("baghlaamit06@gmail.com");
        message.setSubject("Registration Successful");
        message.setText("Hello " + username + ",\n\nYour profile has been successfully registered.\n\nThank you!");

        mailSender.send(message);
    }
}
