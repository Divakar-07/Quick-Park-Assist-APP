package com.qpa.entity;

import java.time.LocalDate;

import jakarta.persistence.*;


@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;
    
    private String registrationNumber;
    private String vehicleType;
    private String brand;
    private String model;
    private LocalDate registrationDate;
    private boolean isActive;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo userObj;

    // Getters and Setters

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public UserInfo getUserObj() {
		return userObj;
	}

	public void setUserObj(UserInfo userObj) {
		this.userObj = userObj;
	}


}
	/*
	 vehicleId
	 registrationNumber
	 vehicleType
	 User userObj
	 */
	
