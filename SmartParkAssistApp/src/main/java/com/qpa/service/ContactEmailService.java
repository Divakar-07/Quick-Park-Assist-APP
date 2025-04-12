package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ContactEmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String name, String email, Long id) throws MessagingException {
        String subject = "🌟 Thank You for Reaching Out!";
        String body = generateEmailBody(name, id.toString()); // Convert Long to String

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(body, true); // Enable HTML content

        // Add inline resources
        helper.addInline("companyLogo", new ClassPathResource("static/download (5).png"));
        helper.addInline("backgroundImage", new ClassPathResource("static/backgrounfW.png"));

        mailSender.send(message);
    }

    private String generateEmailBody(String name, String id) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: rgb(225, 220, 220); }" +
                ".email-container { max-width: 600px; margin: 20px auto; padding: 40px; border-radius: 10px; text-align: center; color: black; " +
                "box-shadow: 0 4px 10px rgba(0,0,0,0.1); background-color:rgb(226, 226, 225); }" +
                ".content { position: relative; z-index: 1; }" +
                ".logo { width: 150px; display: block; margin: 0 auto 20px; }" +
                ".email-header { font-size: 24px; font-weight: bold; margin-top: 10px; }" +
                ".email-body { font-size: 16px; margin-top: 15px; }" +
                ".query-img { width: 100px; display: block; margin: 15px auto; border-radius: 5px; }" + // Small background image
                ".highlight { font-weight: bold; color: #333; }" +
                ".footer { margin-top: 20px; font-size: 12px; color: #666; }" +
                ".signature { text-align: left; margin-top: 20px; font-size: 16px; font-weight: bold; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='content'>" +
                "<img src='cid:companyLogo' class='logo' alt='Company Logo'>" +
                "<div class='email-header'>Thank You for Contacting Us, " + name + "!</div>" +
                "<div class='email-body'>" +
                "We truly appreciate your interest and will get back to you shortly.<br><br>" +
                "<img src='cid:backgroundImage' class='query-img' alt='Query Image'>" + // Small image before Query ID
                "Your Query ID: <span class='highlight'>" + id + "</span><br><br>" +
                "Have a wonderful day! 😊" +
                "<br><br>" +
                "<div class='signature'>" +
                "Best Regards,<br>" +
                "Smart Park Support Team" +
                "</div>" +
                "</div>" +
                "<div class='footer'>© 2025 Smart Park. All rights reserved.</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    
    
    
}


