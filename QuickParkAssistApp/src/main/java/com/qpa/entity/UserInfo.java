package com.qpa.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "users")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private LocalDate dateOfRegister = LocalDate.now();

    @NotBlank(message= "fullName is required")
    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "date must be past or present")
    private LocalDate dob; // Optional

    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid number")
    @Column(length = 15)
    private String contactNumber; // Optional

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType; // Optional

    private String address; // Optional

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(nullable = true)
    private String imageUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Spot> spots = new ArrayList<>();

    // Default constructor
    public UserInfo() {
    }

    public UserInfo(Long userId, LocalDate dateOfRegister, String fullName, String username, String email, LocalDate dob, String contactNumber, UserType userType, String address, Status status, String imageUrl, List<Spot> spots) {
        this.userId = userId;
        this.dateOfRegister = dateOfRegister;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.dob = dob;
        this.contactNumber = contactNumber;
        this.userType = userType;
        this.address = address;
        this.status = status;
        this.imageUrl = imageUrl;
        this.spots = spots;
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

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Enums
    public enum Status {
        ACTIVE, INACTIVE
    }
}
