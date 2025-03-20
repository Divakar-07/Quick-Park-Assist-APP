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
    @Autowired private SpotBookingService spotBookingService;

    @PostMapping("/bookSpot/{spotId}/vehicle/{vehicleId}")
    public ResponseEntity<?> bookSpot(@RequestBody SpotBookingInfo bookingInfo, @PathVariable Long spotId, @PathVariable Long vehicleId) {
        try {
            return ResponseEntity.ok(spotBookingService.bookSpot(bookingInfo, spotId, vehicleId));            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
}
