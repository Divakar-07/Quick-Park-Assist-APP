package com.qpa.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "locations") // Explicit table name
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    private String buildingName;
    private String streetAddress;
    private String area;
    private String city;
    private String state;

    @Column(length = 10)
    private String pincode;

    private String landmark;
    private int floorNumber;


    // ✅ Default constructor (Required by JPA)
    public Location() {}

    // ✅ JSON Creator Constructor (Allows deserialization from a single String)
    @JsonCreator
    public Location(@JsonProperty("buildingName") String buildingName) {
        this.buildingName = buildingName;
    }

    // ✅ Full constructor
    public Location(Long locationId, double latitude, double longitude, String buildingName, String streetAddress,
                    String area, String city, String state, String pincode, int floorNumber, String landmark) {
        this.locationId = locationId;
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

    // ✅ Getters and Setters
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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
