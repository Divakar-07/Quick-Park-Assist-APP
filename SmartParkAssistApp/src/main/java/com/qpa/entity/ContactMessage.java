package com.qpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = false)
    private String email;
    private String category;
    private String message;
    
    // Default constructor
    public ContactMessage() {}
    
    // Parameterized constructor
    public ContactMessage(String name, String email, String category, String message) {
        this.name = name;
        this.email = email;
        this.category= category;
        this.message = message;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}