package com.qpa.service;

import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VehicleService {
    
    @Autowired 
    private VehicleRepository vehicleRepository;

    // Add a new vehicle
    public Vehicle addVehicle(Vehicle vehicle) { 
        return vehicleRepository.save(vehicle); 
    }

    // Get vehicle by ID (throws exception if not found)
    public Vehicle getVehicleById(Long id) { 
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Vehicle not found with ID: " + id));
    }

    // Get vehicles by type
    public List<Vehicle> getVehiclesByType(String type) { 
        List<Vehicle> vehicles = vehicleRepository.findByVehicleType(type);
        if (vehicles.isEmpty()) {
            throw new InvalidEntityException("No vehicles found for type: " + type);
        }
        return vehicles;
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() { 
        return vehicleRepository.findAll(); 
    }

    // Update existing vehicle
    public Vehicle updateVehicle(Vehicle vehicle) { 
        return vehicleRepository.save(vehicle); 
    }

    // Delete vehicle by ID (throws exception if not found)
    public void deleteVehicle(Long id) { 
        Vehicle vehicle = getVehicleById(id);  // Ensures existence before deletion
        vehicleRepository.delete(vehicle);
    }
}