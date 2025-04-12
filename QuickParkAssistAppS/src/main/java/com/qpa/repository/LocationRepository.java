package com.qpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.qpa.entity.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("SELECT l FROM Location l " +
            "WHERE l.streetAddress = :streetAddress " +
            "AND l.city = :city " +
            "AND l.state = :state " +
            "AND l.pincode = :pincode " +
            "AND l.landmark = :landmark ")
    Optional<Location> findByUniqueLocationAttributes(
            String streetAddress,
            String city,
            String state,
            String pincode,
            String landmark
    );
}
