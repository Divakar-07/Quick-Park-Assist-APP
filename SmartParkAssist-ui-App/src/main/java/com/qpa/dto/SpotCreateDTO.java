package com.qpa.dto;

import com.qpa.entity.PriceType;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.SpotType;
import com.qpa.entity.UserInfo;
import com.qpa.entity.VehicleType;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public class SpotCreateDTO {
	private String spotNumber;
	private SpotType spotType;
	private UserInfo owner;
	private LocationDTO location;
	private boolean hasEVCharging;
	private double price;
	private PriceType priceType;
	private MultipartFile image;
	private Set<VehicleType> supportedVehicle;
	private SpotStatus status;
	
	public SpotCreateDTO() {

	}

	

	public SpotCreateDTO(String spotNumber, SpotType spotType, UserInfo owner, LocationDTO location, boolean hasEVCharging,
			double price, PriceType priceType, MultipartFile image, Set<VehicleType> supportedVehicle, SpotStatus status) {
		this.spotNumber = spotNumber;
		this.spotType = spotType;
		this.owner = owner;
		this.location = location;
		this.hasEVCharging = hasEVCharging;
		this.price = price;
		this.priceType = priceType;
		this.image = image;
		this.supportedVehicle = supportedVehicle;
		this.status = status;
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



	public MultipartFile getImage() {
		return image;
	}



	public void setImage(MultipartFile image) {
		this.image = image;
	}



	public SpotStatus getStatus() {
		return status;
	}



	public void setStatus(SpotStatus status) {
		this.status = status;
	}
	
}
