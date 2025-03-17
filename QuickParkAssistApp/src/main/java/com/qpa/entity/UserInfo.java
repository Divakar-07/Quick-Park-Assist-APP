package com.qpa.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private LocalDate dateOfRegister = LocalDate.now();

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;  // Optional

    @Size(min = 10, max = 13, message = "Contact number must be between 10 and 13 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Contact number must contain only digits")
    @Column(length = 13)
    private String contactNumber; // Optional

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType; // Optional

    private String address; // Optional

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; // Default status, but can be null in DB

    @Column(nullable = true)
    private String imageUrl; // Optional, can be null

    // Default constructor
    public UserInfo() {
    }

    @PrePersist
    protected void onCreate() {
        if (dateOfRegister == null) {
            dateOfRegister = LocalDate.now();
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDateOfRegister() {
        return dateOfRegister;
    }

    public void setDateOfRegister(LocalDate dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFormattedDateOfRegister() {
        return dateOfRegister != null ? dateOfRegister.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    public String getFormattedDob() {
        return dob != null ? dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    // Enums

    public enum Status { ACTIVE, INACTIVE }
}
