package com.qpa.repository;

import com.qpa.entity.Vehicle;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByVehicleType(String vehicleType);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
}