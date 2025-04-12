package com.qpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class LocationDTO {
	

	private double latitude;
	

	private double longitude;
	

	private String buildingName;
	
	@NotBlank(message = "Street address cannot be blank")
    @Size(max = 200, message = "Street address must be less than 200 characters")
	private String streetAddress;
	

	private String area;
	
	@NotBlank(message = "City cannot be blank")
	private String city;
	
	@NotBlank(message = "State cannot be blank")
    @Size(max = 50, message = "State must be less than 50 characters")
	private String state;
	
	
	@NotBlank(message = "Pincode cannot be blank")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
	private String pincode;
	

	private String landmark;
	
	

	private int floorNumber;
	
	public LocationDTO() {

	}

	public LocationDTO(double latitude, double longitude, String buildingName, String streetAddress, String area,
			String city, String state, String pincode, int floorNumber, String landmark) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.buildingName = buildingName;
		this.streetAddress = streetAddress;
		this.area = area;
		this.city = city;
		this.state = state;
		this.pincode = pincode;
		this.floorNumber = floorNumber;
		this.landmark = landmark;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(int floorNumber) {
		this.floorNumber = floorNumber;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
}
