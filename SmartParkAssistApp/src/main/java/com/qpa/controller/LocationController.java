package com.qpa.controller;

import com.qpa.entity.City;
import com.qpa.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(locationService.getAllStates());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(locationService.getAllCities());
    }

    @GetMapping("/state-city-map")
    public ResponseEntity<Map<String, List<String>>> getStateCityMap() {
        return ResponseEntity.ok(locationService.getStateCityMap());
    }

    @GetMapping("/city-pincode-map")
    public ResponseEntity<Map<String, String>> getCityPincodeMap() {
        return ResponseEntity.ok(locationService.getCityPincodeMap());
    }

    @GetMapping("/cities-by-state")
    public ResponseEntity<?> getCitiesByState(@RequestParam String state) {
        List<City> cities = locationService.getCitiesByState(state);
        return ResponseEntity.ok(cities.stream().map(city -> Map.of(
                "name", city.getName(),
                "pincode", city.getPincode()
        )).collect(Collectors.toList()));
    }
}