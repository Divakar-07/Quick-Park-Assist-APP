package com.qpa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.UserRepository;
import com.qpa.repository.VehicleRepository;
import com.qpa.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VehicleService {

    @Autowired 
    private VehicleRepository vehicleRepository;

    @Autowired
    private SpotBookingInfoRepository spotBookingInfoRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired 
    private UserRepository userRepository;

    /**
     * Adds a new vehicle and associates it with the logged-in user.
     */
    public Vehicle addVehicle(Vehicle vehicle, HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null) {
            throw new InvalidEntityException("Token not found in cookies");
        }

        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new InvalidEntityException("Invalid token: User ID not found");
        }

        if (vehicleRepository.findByRegistrationNumber(vehicle.getRegistrationNumber()).isPresent()) {
            throw new InvalidEntityException("Vehicle with registration number " + vehicle.getRegistrationNumber() + " already exists");
        }

        UserInfo user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidEntityException("User not found with ID: " + userId));

        vehicle.setUserObj(user);
        return vehicleRepository.save(vehicle);
    }

    /**
     * Retrieves a vehicle by its ID.
     */
    public Vehicle getVehicleById(Long id) { 
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Vehicle not found with ID: " + id));
    }

    /**
     * Retrieves all vehicles of a specific type.
     */
    public List<Vehicle> getVehiclesByType(VehicleType type) { 
        List<Vehicle> vehicles = vehicleRepository.findByVehicleType(type);
        if (vehicles.isEmpty()) {
            throw new InvalidEntityException("No vehicles found for type: " + type);
        }
        return vehicles;
    }

    /**
     * Retrieves all vehicles.
     */
    public List<Vehicle> getAllVehicles() { 
        return vehicleRepository.findAll(); 
    }

    /**
     * Updates an existing vehicle.
     */
    public Vehicle updateVehicle(Vehicle vehicle) { 
        return vehicleRepository.save(vehicle); 
    }

    /**
     * Deletes a vehicle by its ID.
     */
    public void deleteVehicle(Long id) { 
        Vehicle vehicle = getVehicleById(id); // Ensures vehicle exists
        vehicleRepository.delete(vehicle);
    }

    /**
     * Finds a vehicle by booking ID.
     */
    public Vehicle findByBookingId(Long bookingId) {
        SpotBookingInfo bookingInfo = spotBookingInfoRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidEntityException("No booking found with ID: " + bookingId));

        return bookingInfo.getVehicle();
    }

    /**
     * Finds all vehicles associated with a user ID.
     */
    public List<Vehicle> findByUserId(Long userId) {
        return vehicleRepository.findByUserObj_UserId(userId);
    }
}
