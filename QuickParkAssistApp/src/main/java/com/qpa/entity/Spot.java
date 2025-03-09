package com.qpa.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "spot")
public class Spot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long spotId;
	
	@NotBlank(message = "Spot Number cannot be blank")
	private String spotNumber;

	@ManyToOne  // Ensures proper mapping of the UserInfo reference
	@JoinColumn(name = "user_id") // Assuming UserInfo has an 'id' field
	private UserInfo owner;

	@Enumerated(EnumType.STRING)  // Converts SpotType Enum to String in DB
	private SpotType spotType;

	@Enumerated(EnumType.STRING)  // Converts SpotStatus Enum to String in DB
	private SpotStatus status;

	@ManyToOne // Ensures proper mapping of Location reference
	@JoinColumn(name = "location_id") // Assuming Location has an 'id' field
	private Location location;

	private boolean hasEVCharging;

	private double price;

	@Enumerated(EnumType.STRING)  // Converts PriceType Enum to String in DB
	private PriceType priceType;

	private Double rating;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@Lob  // Large object storage for image data
	@ElementCollection
	private List<byte[]> spotImages;

	@ElementCollection(targetClass = VehicleType.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "spot_supported_vehicle_types", joinColumns = @JoinColumn(name = "spot_id"))
	@Column(name = "vehicle_type")
	private Set<VehicleType> supportedVehicleTypes;

	// Lifecycle Callbacks
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Constructors, Getters, and Setters...
// Constructors
public Spot() {
}

public Spot(Long spotId, @NotBlank(message = "Spot Number cannot be blank") String spotNumber, UserInfo owner, SpotType spotType, SpotStatus status, Location location, boolean hasEVCharging, double price, PriceType priceType, Double rating, LocalDateTime createdAt, LocalDateTime updatedAt, List<byte[]> spotImages, Set<VehicleType> supportedVehicleTypes) {
	this.spotId = spotId;
	this.spotNumber = spotNumber;
	this.owner = owner;
	this.spotType = spotType;
	this.status = status;
	this.location = location;
	this.hasEVCharging = hasEVCharging;
	this.price = price;
	this.priceType = priceType;
	this.rating = rating;
	this.createdAt = createdAt;
	this.updatedAt = updatedAt;
	this.spotImages = spotImages;
	this.supportedVehicleTypes = supportedVehicleTypes;
}

// Getters and Setters
public Long getSpotId() {
	return spotId;
}

public void setSpotId(Long spotId) {
	this.spotId = spotId;
}

public String getSpotNumber() {
	return spotNumber;
}

public void setSpotNumber(String spotNumber) {
	this.spotNumber = spotNumber;
}

public UserInfo getOwner() {
	return owner;
}

public void setOwner(UserInfo owner) {
	this.owner = owner;
}

public SpotType getSpotType() {
	return spotType;
}

public void setSpotType(SpotType spotType) {
	this.spotType = spotType;
}

public SpotStatus getStatus() {
	return status;
}

public void setStatus(SpotStatus status) {
	this.status = status;
}

public Location getLocation() {
	return location;
}

public void setLocation(Location location) {
	this.location = location;
}

public boolean isHasEVCharging() {
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

public Double getRating() {
	return rating;
}

public void setRating(Double rating) {
	this.rating = rating;
}

public LocalDateTime getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}

public LocalDateTime getUpdatedAt() {
	return updatedAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
	this.updatedAt = updatedAt;
}

public List<byte[]> getSpotImages() {
	return spotImages;
}

public void setSpotImages(List<byte[]> spotImages) {
	this.spotImages = spotImages;
}

public Set<VehicleType> getSupportedVehicleTypes() {
	return supportedVehicleTypes;
}

public void setSupportedVehicleTypes(Set<VehicleType> supportedVehicleTypes) {
	this.supportedVehicleTypes = supportedVehicleTypes;
}
}