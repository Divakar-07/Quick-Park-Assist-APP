package com.qpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SpotBookingInfo {

	@Id
	private Long bookingId;

	
/*
bookingId
bookingDate
startDate
endDate
startTime
endTime
SlotInfo slotObj
Vehicle vehicleObj
List<AddOn> addOnList
status

 */
}
