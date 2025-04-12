package com.qpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qpa.dto.RatingRequestDTO;
import com.qpa.dto.RatingResponseDTO;
import com.qpa.dto.ResponseDTO;
import com.qpa.service.AuthService;
import com.qpa.service.RatingService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;
    private final AuthService authService;

    public RatingController(RatingService ratingService, AuthService authService) {
        this.ratingService = ratingService;
        this.authService = authService;
    }

    @PostMapping("/{spotId}")
    public ResponseEntity<ResponseDTO<RatingResponseDTO>> rateSpot(
            @PathVariable Long spotId,
            @RequestBody RatingRequestDTO request,
            HttpServletRequest httpRequest) {

        // Check authentication
        if (!authService.isAuthenticated(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Unauthorized", HttpStatus.UNAUTHORIZED.value(), false));
        }

        Long userId = authService.getUserId(httpRequest);
        RatingResponseDTO response = ratingService.rateSpot(spotId, request, userId);

        return ResponseEntity.ok(new ResponseDTO<>("Rating saved successfully", HttpStatus.OK.value(), true, response));
    }

    @GetMapping("/{spotId}/average")
    public ResponseEntity<ResponseDTO<Double>> getAverageRating(@PathVariable Long spotId) {
        Double avgRating = ratingService.getAverageRating(spotId);
        return ResponseEntity.ok(new ResponseDTO<>("Average rating retrieved", HttpStatus.OK.value(), true, avgRating));
    }

    @GetMapping("/{spotId}/user")
    public ResponseEntity<ResponseDTO<Integer>> getUserRatingOnSpot(
            @PathVariable Long spotId,
            HttpServletRequest httpRequest) {

        // Check authentication
        if (!authService.isAuthenticated(httpRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>("Unauthorized", HttpStatus.UNAUTHORIZED.value(), false));
        }

        Long userId = authService.getUserId(httpRequest);
        Integer userRating = ratingService.getUserRatingOnSpot(spotId, userId);

        return ResponseEntity.ok(new ResponseDTO<>("User rating retrieved", HttpStatus.OK.value(), true, userRating));
    }
}