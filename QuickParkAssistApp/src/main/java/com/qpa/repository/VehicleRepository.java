package com.qpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qpa.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByVehicleType(String vehicleType);
}