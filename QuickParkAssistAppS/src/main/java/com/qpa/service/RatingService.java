package com.qpa.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qpa.dto.RatingRequestDTO;
import com.qpa.dto.RatingResponseDTO;
import com.qpa.entity.Rating;
import com.qpa.entity.Spot;
import com.qpa.entity.UserInfo;
import com.qpa.exception.ResourceNotFoundException;
import com.qpa.repository.RatingRepository;
import com.qpa.repository.SpotRepository;
import com.qpa.repository.UserRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, SpotRepository spotRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RatingResponseDTO rateSpot(Long spotId, RatingRequestDTO request, Long userId) {
        if (request.getValue() < 1 || request.getValue() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));

        UserInfo user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Rating rating = ratingRepository.findBySpotAndUser(spot, user)
                .orElse(new Rating(user, spot));

        rating.setValue(request.getValue());
        Rating savedRating = ratingRepository.save(rating);

        // Update spot's average rating
        Double avgRating = ratingRepository.getAverageRatingForSpot(spotId);
        spot.setAverageRating(avgRating);
        spotRepository.save(spot);

        return mapToRatingResponse(savedRating);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long spotId) {
        Double avgRating = ratingRepository.getAverageRatingForSpot(spotId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Transactional(readOnly = true)
    public Integer getUserRatingOnSpot(Long spotId, Long userId) {
        try {
            Rating rating = ratingRepository.findBySpot_SpotIdAndUser_UserId(spotId, userId)
                    .orElse(null);
            return rating != null ? rating.getValue() : 0;
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    private RatingResponseDTO mapToRatingResponse(Rating rating) {
        return new RatingResponseDTO(rating.getId(), rating.getValue(), rating.getUser().getUsername());
    }
}
