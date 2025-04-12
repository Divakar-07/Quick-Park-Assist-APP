package com.qpa.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SpotBookingDTO {
    private double bookingTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private long spotId;
    private String registrationNumber;

    // ✅ No-Args Constructor
    public SpotBookingDTO() {
    }

    // ✅ All-Args Constructor
    public SpotBookingDTO(double bookingTime, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime, long spotId, String registrationNumber) {
        this.bookingTime = bookingTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.spotId = spotId;
        this.registrationNumber = registrationNumber;
    }

    // ✅ Getters and Setters


    public double getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(double bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public long getSpotId() {
        return spotId;
    }

    public void setSpotId(long spotId) {
        this.spotId = spotId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

}
