package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.SpotBookingInfo;
import com.qpa.service.SpotBookingService;

@RestController
@RequestMapping("/api/booking")
public class SpotBookingController {

    @Autowired
    private SpotBookingService spotBookingService; // Injects SpotBookingService to handle booking operations

    /**
     * Books a parking spot for a specific vehicle.
     * 
     * @param bookingInfo The booking details provided in the request body.
     * @param spotId      The ID of the parking spot to be booked.
     * @param vehicleId   The ID of the vehicle for which the spot is booked.
     * @return ResponseEntity containing booking confirmation or error message.
     */
    @PostMapping("/bookSpot/{spotId}/vehicle/{vehicleId}")
    public ResponseEntity<?> bookSpot(@RequestBody SpotBookingInfo bookingInfo,
            @PathVariable Long spotId,
            @PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(spotBookingService.bookSpot(bookingInfo, spotId, vehicleId));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage()); // Returns error message if booking fails
        }
    }
}
