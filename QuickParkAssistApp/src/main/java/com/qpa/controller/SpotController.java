package com.qpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.entity.Spot;
import com.qpa.service.SpotService;


@RestController
@RequestMapping("/spot")
public class SpotController {
    @Autowired
    private SpotService spotService;

    @PostMapping("/location/{locationId}/add")
    public ResponseEntity<Spot> addSpot(@RequestBody Spot spot, @PathVariable Long locationId) {
        return ResponseEntity.ok(spotService.addSpot(spot, locationId));
    }
}
