package com.qpa.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    private LocalDate dateOfRegister;
	@Column(nullable = false)
    private String firstName;
	@Column(nullable = false)
    private String lastName;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
	@Column(nullable = false, unique = true)

    private String email;
	@Column(nullable = false, length = 13)
    private String contactNumber;

	@Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Encrypted before saving
    
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    private String address;
    
    @Enumerated(EnumType.STRING)
    private Status status;

	public UserInfo(){
		
	}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters, Setters, and Enums

    public enum UserType { VEHICLE_OWNER, SLOT_OWNER }
	public enum Status { ACTIVE, INACTIVE }

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
	
}

	
	/*
	 userId
	dateOfReg
	firstName
	lastName
	emailId
	contactNumber
	userType - can be VehicleOwner/SlotWoner
 	address
	status - Active / Inactive

	If needed include any other attributes
	
	You can have the userName and password here
	 */

