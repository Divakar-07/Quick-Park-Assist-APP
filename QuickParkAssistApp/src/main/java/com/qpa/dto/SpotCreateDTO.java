package com.qpa.dto;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.qpa.entity.PriceType;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.SpotType;
import com.qpa.entity.UserInfo;
import com.qpa.entity.VehicleType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SpotCreateDTO {

	@NotBlank(message = "Spot number is required")
    @Pattern(regexp = "^[A-Z0-9-]{3,10}$",
             message = "Spot number must be alphanumeric, 3-10 characters long")
	private String spotNumber;
	
	@NotNull(message = "Spot type is required")
	private SpotType spotType;
	
	private UserInfo owner;

	@Valid
	@NotNull(message = "Location details are required")
	private LocationDTO location;
	
	private boolean hasEVCharging;

	@NotNull(message = "Price is required")
	@Min(value = 0, message = "Price must be non-negative")
	private double price;

	@NotNull(message = "Price type is required")
	private PriceType priceType;
	
	private MultipartFile image;

	@NotEmpty(message = "At least one supported vehicle type is required")
	private Set<VehicleType> supportedVehicle;

	private SpotStatus status = SpotStatus.AVAILABLE;
	
	public SpotCreateDTO() {

	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public String getSpotNumber() {
		return spotNumber;
	}

	public void setSpotNumber(String spotNumber) {
		this.spotNumber = spotNumber;
	}

	public SpotType getSpotType() {
		return spotType;
	}

	public void setSpotType(SpotType spotType) {
		this.spotType = spotType;
	}

	public LocationDTO getLocation() {
		return location;
	}

	public void setLocation(LocationDTO location) {
		this.location = location;
	}

	public boolean getHasEVCharging() {
		return hasEVCharging;
	}

	public void setHasEVCharging(boolean hasEVCharging) {
		this.hasEVCharging = hasEVCharging;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public PriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(PriceType priceType) {
		this.priceType = priceType;
	}

	public Set<VehicleType> getSupportedVehicle() {
		return supportedVehicle;
	}

	public void setSupportedVehicle(Set<VehicleType> supportedVehicle) {
		this.supportedVehicle = supportedVehicle;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public SpotStatus getStatus() {
		return status;
	}

	public void setStatus(SpotStatus status) {
		this.status = status;
	}
}
