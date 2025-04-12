package com.qpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.ContactMessage;
import com.qpa.service.ContactEmailService;
import com.qpa.service.ContactService;

import jakarta.mail.MessagingException;

@RequestMapping("/api")
@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactEmailService contactEmailService;

    @PostMapping("/contact")
    public ResponseEntity<String> saveContactMessage(@RequestBody ContactMessage message) {
        try {
            // Save the message and get the saved entity with generated ID
            ContactMessage savedMessage = contactService.saveMessage(message);
            // Send confirmation email with the generated ID
            contactEmailService.sendConfirmationEmail(savedMessage.getName(), savedMessage.getEmail(),
                    savedMessage.getId());
            return ResponseEntity.ok("Message saved and email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to process request: " + e.getMessage());
        }
    }

    @GetMapping("/contact-messages")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactService.getAllMessages(); // Fetch messages from service
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/contact/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteMessage(@PathVariable Long id) {
        boolean deleted = contactService.deleteMessageById(id);

        if (deleted) {
            return ResponseEntity.ok(new ResponseDTO<>("Message deleted successfully", 200, true));
        } else {
            return ResponseEntity.status(404)
                    .body(new ResponseDTO<>("Message not found", 404, false));
        }
    }
}