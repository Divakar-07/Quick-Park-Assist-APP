package com.qpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qpa.entity.Rating;
import com.qpa.entity.Spot;
import com.qpa.entity.UserInfo;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findBySpotAndUser(Spot spot, UserInfo user);

    Optional<Rating> findBySpot_SpotIdAndUser_UserId(Long spotId, Long userId);

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.spot.spotId = :spotId")
    Double getAverageRatingForSpot(@Param("spotId") Long spotId);

    @Modifying
    @Query("DELETE FROM Rating r WHERE r.spot.spotId = :spotId")
    void deleteBySpotId(@Param("spotId") Long spotId);
}

