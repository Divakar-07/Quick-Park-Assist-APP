package com.qpa.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.AuthUser;
import com.qpa.entity.Spot;
import com.qpa.service.AuthService;
import com.qpa.service.SpotService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/spot")
public class SpotController {

    @Autowired
    private SpotService spotService; // Injects SpotService to handle spot-related operations

    @Autowired
    private AuthService authService; // Injects AuthService to manage authentication

    /**
     * Retrieves all available parking spots if the user is authenticated.
     * 
     * @param request HTTP request containing authentication details.
     * @return List of spots if authorized, otherwise an unauthorized message.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllSpots(HttpServletRequest request) {
        if (authService.isAuthenticated(request)) {
            List<Spot> spots = spotService.getAllSpots();
            return ResponseEntity.ok(spots); // Returns the list of spots in JSON format
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
    }

    /**
     * Adds a new parking spot under a specific location if the user is
     * authenticated.
     * 
     * @param spot       The spot details provided in the request body.
     * @param locationId The ID of the location where the spot is to be added.
     * @param request    HTTP request containing authentication details.
     * @return Added spot details if successful, otherwise an unauthorized message.
     */
    @PostMapping("/location/{locationId}/add")
    public ResponseEntity<?> addSpot(@RequestBody Spot spot,
            @PathVariable Long locationId,
            HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized request"));
        }

        // Get authenticated user details
        AuthUser authUser = authService.getAuth(request);
        spot.setOwner(authUser.getUser()); // Set the authenticated user as the owner of the spot

        return ResponseEntity.ok(spotService.addSpot(spot, locationId)); // Returns the added spot details
    }
}
