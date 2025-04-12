package com.qpa.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table(name = "spots")
public class Spot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long spotId;

	private String spotNumber;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	@JsonBackReference
	private UserInfo owner;

	@Enumerated(EnumType.STRING)
	private SpotType spotType;

	@Enumerated(EnumType.STRING)
	private SpotStatus status=SpotStatus.AVAILABLE;

	@Column(columnDefinition = "BOOLEAN")
	private boolean isActive = true;

	@ManyToOne
	@JoinColumn(name = "location_id", nullable = false)
	@JsonIgnore
	private Location location;

	@Column(columnDefinition = "BOOLEAN")
	private boolean hasEVCharging;

	private double price;

	@Enumerated(EnumType.STRING)
	private PriceType priceType;

	private Double averageRating;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@Column(name = "image_url")
	private String spotImage;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "spot_vehicle_types", joinColumns = @JoinColumn(name = "spot_id"))
	@Column(name = "vehicle_type")
	private Set<VehicleType> supportedVehicleTypes; // no set

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public Spot() {

	}

	public Spot(Long spotId, String spotNumber, UserInfo owner, SpotType spotType, SpotStatus status, boolean isActive, Location location, boolean hasEVCharging, double price, PriceType priceType, Double averageRating, LocalDateTime createdAt, LocalDateTime updatedAt, String spotImage, Set<VehicleType> supportedVehicleTypes) {
		this.spotId = spotId;
		this.spotNumber = spotNumber;
		this.owner = owner;
		this.spotType = spotType;
		this.status = status;
		this.isActive = isActive;
		this.location = location;
		this.hasEVCharging = hasEVCharging;
		this.price = price;
		this.priceType = priceType;
		this.averageRating = averageRating;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.spotImage = spotImage;
		this.supportedVehicleTypes = supportedVehicleTypes;
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

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getSpotImage() {
		return spotImage;
	}

	public void setSpotImage(String spotImage) {
		this.spotImage = spotImage;
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

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean active) {
		isActive = active;
	}
}
