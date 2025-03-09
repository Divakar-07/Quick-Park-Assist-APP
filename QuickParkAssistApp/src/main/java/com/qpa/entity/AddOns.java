package com.qpa.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "add_ons") // Explicit table name
public class AddOns {  // Singular class name

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // More conventional PK naming

    @Column(nullable = false)
    private String name;

    @Column(length = 500) // Limit description length
    private String description;

    @Column(nullable = false, precision = 10, scale = 2) // Better for monetary values
    private BigDecimal price;

    @Column(nullable = false)
    private String vehicleType; // Example: "Car", "Bike", etc.

    @Column(precision = 2, scale = 1) 
    private BigDecimal rating; // Changed Float to BigDecimal for precision control

    // Getters and Setters
    public Long getId() { 
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public BigDecimal getRating() {
        return rating;
    }
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
}
