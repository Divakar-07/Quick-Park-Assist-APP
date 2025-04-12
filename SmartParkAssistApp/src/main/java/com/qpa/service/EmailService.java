package com.qpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    
    public String sendSimpleMail(String to, String subject,String body){
        try {
            SimpleMailMessage simpleMailMessage =new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(to);
            simpleMailMessage.setText(body);
            simpleMailMessage.setSubject(subject);
            javaMailSender.send(simpleMailMessage);

            return "Mail sent successfully";

        } catch (Exception e) {
           
            return "Error while sending mail";
        }
    }
}
