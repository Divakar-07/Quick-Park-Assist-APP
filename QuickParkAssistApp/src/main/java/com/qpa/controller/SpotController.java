package com.qpa.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/spot")
public class SpotController {
    @Autowired
    private SpotService spotService;

    @Autowired 
    private AuthService authService;

    @PostMapping("/location/{locationId}/add")
    public ResponseEntity<?> addSpot(@RequestBody Spot spot, @PathVariable Long locationId, HttpServletRequest request) {
        if (!authService.isAuthenticated(request)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized request"));
        }
        AuthUser authUser = authService.getAuth(request);
        spot.setOwner(authUser.getUser());
        return ResponseEntity.ok(spotService.addSpot(spot, locationId));
    }
}
