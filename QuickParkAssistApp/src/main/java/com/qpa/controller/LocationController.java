package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.Location;
import com.qpa.service.LocationService;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired 
    private LocationService locationService; // Injects LocationService for handling location-related operations

    /**
     * Adds a new location to the system.
     * @param location The location details provided in the request body.
     * @return ResponseEntity containing the added Location object.
     */
    @PostMapping("/add")
    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
        return ResponseEntity.ok(locationService.addLocation(location));
    }
}
