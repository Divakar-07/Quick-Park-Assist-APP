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

    @NotNull
    @Column(nullable = false)
    private LocalDate dateOfRegister = LocalDate.now(); // Default to current date

    @NotBlank
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;


    @NotBlank
    @Size(min = 10, max = 13)
    @Pattern(regexp = "^[0-9]+$", message = "Contact number must contain only digits")
    @Column(nullable = false, length = 13)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UserType userType;

    private String address;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Status status = Status.ACTIVE; // Default status

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public String getFormattedDateOfRegister() {
        return dateOfRegister != null ? dateOfRegister.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    public String getFormattedDob() {
        return dob != null ? dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
    }

    // Enums
    public enum UserType { VEHICLE_OWNER, SLOT_OWNER }

    public enum Status { ACTIVE, INACTIVE }
}
