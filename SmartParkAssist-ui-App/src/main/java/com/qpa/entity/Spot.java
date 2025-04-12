package com.qpa.entity;

import java.time.LocalDateTime;
import java.util.Set;

public class Spot {

	private Long spotId;

	private String spotNumber;

	private UserInfo owner;

	private SpotType spotType;

	private boolean isActive;

	private SpotStatus status;

	private Location location;

	private boolean hasEVCharging;

	private double price;

	private PriceType priceType;

	private Double averageRating;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String spotImage;

	private Set<VehicleType> supportedVehicleTypes; // no set

	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Spot() {

	}

	public Spot(Long spotId, String spotNumber, SpotType spotType, SpotStatus status, Location location,
			boolean hasEVCharging, double price, PriceType priceType, Double averageRating,
			LocalDateTime createdAt, LocalDateTime updatedAt, String spotImage,
			Set<VehicleType> supportedVehicleTypes, boolean isActive) {
		super();
		this.spotId = spotId;
		this.spotNumber = spotNumber;
		this.spotType = spotType;
		this.status = status;
		this.location = location;
		this.hasEVCharging = hasEVCharging;
		this.price = price;
		this.priceType = priceType;
		this.averageRating = averageRating;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.spotImage = spotImage;
		this.supportedVehicleTypes = supportedVehicleTypes;
		this.isActive = isActive;
	}

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

	public boolean hasEVCharging() {
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
		return averageRating;
	}

	public void setRating(Double averageRating) {
		this.averageRating = averageRating;
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

	public Set<VehicleType> getSupportedVehicleTypes() {
		return supportedVehicleTypes;
	}

	public void setSupportedVehicleTypes(Set<VehicleType> supportedVehicleTypes) {
		this.supportedVehicleTypes = supportedVehicleTypes;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public boolean isHasEVCharging() {
		return hasEVCharging;
	}

	public String getSpotImage() {
		return spotImage;
	}

	public void setSpotImage(String spotImage) {
		this.spotImage = spotImage;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

}
