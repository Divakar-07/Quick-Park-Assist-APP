package com.qpa.entity;

public class AddOns {
	private Long addOnId;
    private String name;
    private String description;
    private Double price;
    private String vehicleType;
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
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
    
    
}