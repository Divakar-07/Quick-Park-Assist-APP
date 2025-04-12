package com.qpa.dto;

public class AdminSpotsStatistics {
    private int totalSpots;
    private int totalBookings;
    private int pendingPayments;
    private int activeSpots;
    private int inactiveSpots;
    private int activeBookings;
    private int bookedSpots;

    // Constructors
    public AdminSpotsStatistics() {
    }

    public AdminSpotsStatistics(int totalSpots, int totalBookings, int pendingPayments,
            int activeSpots, int inactiveSpots, int activeBookings, int bookedSpots) {
        this.totalSpots = totalSpots;
        this.totalBookings = totalBookings;
        this.pendingPayments = pendingPayments;
        this.activeSpots = activeSpots;
        this.inactiveSpots = inactiveSpots;
        this.activeBookings = activeBookings;
        this.bookedSpots = bookedSpots;
    }

    // Getters and Setters
    public int getTotalSpots() {
        return totalSpots;
    }

    public void setTotalSpots(int totalSpots) {
        this.totalSpots = totalSpots;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(int pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public int getActiveSpots() {
        return activeSpots;
    }

    public void setActiveSpots(int activeSpots) {
        this.activeSpots = activeSpots;
    }

    public int getInactiveSpots() {
        return inactiveSpots;
    }

    public void setInactiveSpots(int inactiveSpots) {
        this.inactiveSpots = inactiveSpots;
    }

    public int getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(int activeBookings) {
        this.activeBookings = activeBookings;
    }

    public int getBookedSpots() {
        return bookedSpots;
    }

    public void setBookedSpots(int bookedSpots) {
        this.bookedSpots = bookedSpots;
    }

    @Override
    public String toString() {
        return "AdminSpotsStatistics{" +
                "totalSpots=" + totalSpots +
                ", totalBookings=" + totalBookings +
                ", pendingPayments=" + pendingPayments +
                ", activeSpots=" + activeSpots +
                ", inactiveSpots=" + inactiveSpots +
                ", activeBookings=" + activeBookings +
                ", bookedSpots=" + bookedSpots +
                '}';
    }
}
