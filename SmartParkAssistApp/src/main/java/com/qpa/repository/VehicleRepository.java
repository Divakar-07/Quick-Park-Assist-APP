package com.qpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    List<Vehicle> findByUserObj_UserId(Long userId);
    Vehicle findByRegistrationNumber(String registrationNumber);
}