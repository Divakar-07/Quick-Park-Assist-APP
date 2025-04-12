package com.qpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
public class AddOns {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long addOnId;

	@NotBlank(message = "Name is mandatory")
	private String name;

	public Long getAddOnId() {
		return addOnId;
	}

	public void setAddOnId(Long addOnId) {
		this.addOnId = addOnId;
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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	@Size(max = 100, message = "Description cannot exceed 100 characters")
	private String description;

	@NotNull(message = "Price is mandatory")
	@Positive(message = "Price must be greater than zero")
	private Double price;

	@NotNull(message = "Vehicle Type is mandatory")
	@Enumerated(EnumType.STRING) // ✅ Store enum as a string in the database
	private VehicleType vehicleType;

	
}