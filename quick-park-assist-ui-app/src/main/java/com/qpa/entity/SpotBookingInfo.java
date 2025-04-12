package com.qpa.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SpotBookingInfo {
    private Long bookingId;
    private LocalDate bookingDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Spot spotInfo;
    private Vehicle vehicle;
    private List<AddOns> addOns;
    private double TotalAmount;

    private Boolean paymentStatus=false;

    // Default constructor
    public SpotBookingInfo() {
    }

    // Parameterized constructor
    public SpotBookingInfo(Long bookingId, LocalDate bookingDate, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime, String status, Spot spotInfo,
            Vehicle vehicle, double totalAmount, List<AddOns> addOns,Boolean paymentStatus) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.spotInfo = spotInfo;
        this.vehicle = vehicle;
        this.addOns = addOns;
        this.TotalAmount = totalAmount;
        this.paymentStatus=paymentStatus;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
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

    public void setStartTime(LocalTime startTime2) {
        this.startTime = startTime2;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime2) {
        this.endTime = endTime2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Spot getSpotInfo() {
        return spotInfo;
    }

    public void setSpotInfo(Spot spotInfo) {
        this.spotInfo = spotInfo;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.TotalAmount = totalAmount;
    }

    public List<AddOns> getAddOns() {
        return addOns;
    }

    public void setAddOns(List<AddOns> addOns) {
        this.addOns = addOns;
    }

    @Override
    public String toString() {
        return "SpotBookingInfo{" +
                "bookingId=" + bookingId +
                ", bookingDate=" + bookingDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", totalAmount=" + TotalAmount +
                ", spotInfo=" + (spotInfo != null ? spotInfo.toString() : "null") +
                ", vehicle=" + (vehicle != null ? vehicle.toString() : "null") +
                ", addOns=" + (addOns != null ? addOns.toString() : "null") +
                '}';
    }

}
