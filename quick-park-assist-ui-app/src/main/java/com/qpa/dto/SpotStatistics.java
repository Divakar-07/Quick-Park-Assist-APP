package com.qpa.dto;

import java.util.Map;

import com.qpa.entity.SpotType;

public class SpotStatistics {
    private long totalSpots;
    private long availableSpots;
    private long unavailableSpots;
    
	public SpotStatistics() {

	}

	public SpotStatistics(long totalSpots, long availableSpots, long unavailableSpots) {
		super();
		this.totalSpots = totalSpots;
		this.availableSpots = availableSpots;
		this.unavailableSpots = unavailableSpots;
	}

	public long getTotalSpots() {
		return totalSpots;
	}

	public void setTotalSpots(long totalSpots) {
		this.totalSpots = totalSpots;
	}

	public long getAvailableSpots() {
		return availableSpots;
	}

	public void setAvailableSpots(long availableSpots) {
		this.availableSpots = availableSpots;
	}

	public long getUnavailableSpots() {
		return unavailableSpots;
	}

	public void setUnavailableSpots(long unavailableSpots) {
		this.unavailableSpots = unavailableSpots;
	}
}