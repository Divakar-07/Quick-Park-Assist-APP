package com.qpa.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.AdminSpotsStatistics;
import com.qpa.dto.SpotCreateDTO;
import com.qpa.dto.SpotResponseDTO;
import com.qpa.dto.SpotSearchCriteria;
import com.qpa.dto.SpotStatistics;
import com.qpa.entity.UserType;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.service.AuthService;
import com.qpa.service.SpotService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spots")
public class SpotController {

    private final SpotService spotService;
    private final AuthService authService;

    public SpotController(SpotService spotService, AuthService authService) {
        this.spotService = spotService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<SpotResponseDTO> createSpot(
            @Valid @RequestPart("spot") SpotCreateDTO spotDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) throws IOException {
        Long userId = authService.getUserId(request);
        return ResponseEntity.ok(spotService.createSpot(spotDTO, image, userId, request));
    }

    @PutMapping("/{spotId}")
    public ResponseEntity<SpotResponseDTO> updateSpot(
            @PathVariable Long spotId,
            @RequestPart("spot") SpotCreateDTO spotDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) throws InvalidEntityException, IOException {
        // If needed, you can add a check to ensure the user owns the spot
        if (!authService.isAuthenticated(request) || authService.getUserType(request) != UserType.ADMIN) {
            throw new UnauthorizedAccessException("user is not authenticated");
        }

        return ResponseEntity.ok(spotService.updateSpot(spotId, spotDTO, image));
    }

    @DeleteMapping("/{spotId}")
    public ResponseEntity<Void> deleteSpot(
            @PathVariable Long spotId,
            @RequestParam Long userId, HttpServletRequest request) {
        // Add ownership check if needed
        spotService.deleteSpot(spotId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner")
    public ResponseEntity<List<SpotResponseDTO>> getOwnerSpots(
            @RequestParam Long userId) {
        return ResponseEntity.ok(spotService.getSpotByOwner(userId));
    }

    @GetMapping("/{spotId}")
    public ResponseEntity<SpotResponseDTO> getSpotById(@PathVariable Long spotId) {
        return ResponseEntity.ok(spotService.getSpot(spotId));
    }

    @PutMapping("/toggle/{spotId}")
    public ResponseEntity<SpotResponseDTO> toggleSpotActivation(@PathVariable Long spotId) {
        return ResponseEntity.ok(spotService.toggleSpotActivation(spotId));
    }

    // Vehicle Owner endpoints
    @PatchMapping("/{spotId}/rate")
    public ResponseEntity<SpotResponseDTO> rate(
            @PathVariable Long spotId,
            @RequestParam Double rating,
            @RequestParam Long userId, HttpServletRequest request) {
        // You might want to add logic to prevent rating your own spot
        return ResponseEntity.ok(spotService.rateSpot(spotId, rating, request));
    }

    // Remaining endpoints stay mostly the same
    @GetMapping("/statistics")
    public ResponseEntity<SpotStatistics> getStatistics() {
        return ResponseEntity.ok(spotService.getStatistics());
    }

    @GetMapping("/all")
    public ResponseEntity<List<SpotResponseDTO>> getAllSpots() {
        List<SpotResponseDTO> spotList = spotService.getAllSpots();
        System.out.println("my true inside getAlSpots: " + spotList.isEmpty());
        for (SpotResponseDTO spot : spotList) {
            System.out.println(spot.getSpotNumber());
        }
        return ResponseEntity.ok(spotList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SpotResponseDTO>> searchSpots(SpotSearchCriteria criteria) {
        return ResponseEntity.ok(spotService.searchSpots(criteria));
    }

    // redundant
    @GetMapping("/evCharging")
    public ResponseEntity<List<SpotResponseDTO>> getSpotsByEVCharging(
            @RequestParam boolean hasEVCharging) {
        return new ResponseEntity<>(spotService.getSpotsByEVCharging(hasEVCharging), HttpStatus.OK);

    }

    // redundant
    @GetMapping("/availability")
    public ResponseEntity<List<SpotResponseDTO>> getAvailableSpotsByCityAndVehicle(
            @RequestParam String city,
            @RequestParam VehicleType vehicleType) {
        return new ResponseEntity<>(spotService.getAvailableSpotsByCityAndVehicle(city, vehicleType), HttpStatus.OK);
    }

    // redundant
    @GetMapping("/availableSpots")
    public ResponseEntity<List<SpotResponseDTO>> getAvailableSpots() {
        return new ResponseEntity<>(spotService.getAvailableSpots(), HttpStatus.OK);
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<SpotResponseDTO> getSpotByBookingId(@PathVariable long bookingId)
            throws InvalidEntityException {

        return new ResponseEntity<>(spotService.getSpotByBookingId(bookingId), HttpStatus.OK);

    }

    @GetMapping("/booked")
    public ResponseEntity<List<SpotResponseDTO>> getBookedSpots() {
        return new ResponseEntity<>(spotService.getBookedSpots(), HttpStatus.OK);
    }

    @GetMapping("/by-booking")
    public ResponseEntity<List<SpotResponseDTO>> getBookedSpotsByStartAndEndDate(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        return new ResponseEntity<>(spotService.getAvailableSpotsByStartAndEndDate(startDate, endDate), HttpStatus.OK);

    }

    // to get booked spots based on city and landmark
    @GetMapping("/booked-by-filters")
    public ResponseEntity<List<SpotResponseDTO>> getBookedSpots(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String landmark) {
        return new ResponseEntity<>(spotService.getBookedSpots(city, landmark), HttpStatus.OK);
    }

    // to get cities for dropdown
    @GetMapping("/booked/cities")
    public ResponseEntity<List<String>> getCities() {
        System.out.println("==== GET /booked/cities endpoint called ====");
        List<String> cities = spotService.getCities();
        System.out.println("==== Returning cities: " + cities + " ====");
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }

    // to get landmarks for dropdown
    @GetMapping("/booked/landmarks")
    public ResponseEntity<List<String>> getLandmarks(@RequestParam String city) {
        System.out.println("==== GET /booked/landmarks endpoint called ====");
        if (city == null || city.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }
        List<String> landmarks = spotService.getLandmarks(city);
        System.out.println("==== Returning landmarks: " + landmarks + " ====");
        return new ResponseEntity<>(landmarks, HttpStatus.OK);
    }

    @GetMapping("/getAdminSpotStatistics/{userId}")
    public ResponseEntity<AdminSpotsStatistics> getAdminSpotStatistics(@PathVariable Long userId) throws InvalidEntityException {
        return ResponseEntity.ok(spotService.getAdminSpotsStatistics(userId));
    }

}