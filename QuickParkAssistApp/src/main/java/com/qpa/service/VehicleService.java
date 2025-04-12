package com.qpa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.UserRepository;
import com.qpa.repository.VehicleRepository;
import com.qpa.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final SpotBookingInfoRepository spotBookingInfoRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            SpotBookingInfoRepository spotBookingInfoRepository,
            JwtUtil jwtUtil,
            UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.spotBookingInfoRepository = spotBookingInfoRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public Vehicle saveVehicle(Vehicle vehicle, HttpServletRequest request) throws InvalidEntityException {
        String token = jwtUtil.extractTokenFromCookie(request);
        if (token == null) {
            throw new UnauthorizedAccessException("Token not found in cookies");
        }

        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new UnauthorizedAccessException("Invalid token: User ID not found");
        }

        // Check for existing vehicle with same registration number
        Vehicle existingVehicle = vehicleRepository.findByRegistrationNumber(vehicle.getRegistrationNumber());
        if (existingVehicle != null) {
            throw new InvalidEntityException(
                    "Vehicle with registration number " + vehicle.getRegistrationNumber() + " already exists");
        }

        UserInfo user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidEntityException("User not found with ID: " + userId));

        vehicle.setUserObj(user);
        if (vehicle.getVehicleId() == null) {
            return vehicleRepository.save(vehicle);
        } else {
            return updateVehicle(vehicle);
        }
    }

    public Vehicle getVehicleById(Long id) throws InvalidEntityException {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("Vehicle not found with ID: " + id));
    }

    public List<Vehicle> getVehiclesByType(VehicleType type) throws InvalidEntityException {
        List<Vehicle> vehicles = vehicleRepository.findByVehicleType(type);
        if (vehicles.isEmpty()) {
            throw new InvalidEntityException("No vehicles found for type: " + type);
        }
        return vehicles;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id, Long userId) throws InvalidEntityException {
        Vehicle vehicle = getVehicleById(id);

        // Ensure the user is authorized to delete the vehicle
        if (!userId.equals(vehicle.getUserObj().getUserId())) {
            throw new UnauthorizedAccessException("You do not have permission to delete this vehicle");
        }
        List<SpotBookingInfo> bookings = spotBookingInfoRepository.findByVehicle_VehicleId(id);
        if (!bookings.isEmpty()){
            throw new InvalidEntityException("booking exist for the requested vehicle");
        }
        

        vehicleRepository.delete(vehicle);
    }

    public Vehicle findByBookingId(Long bookingId) throws InvalidEntityException {
        SpotBookingInfo bookingInfo = spotBookingInfoRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidEntityException("No booking found with ID: " + bookingId));

        return bookingInfo.getVehicle();
    }

    public List<Vehicle> findByUserId(Long userId) {
        return vehicleRepository.findByUserObj_UserId(userId);
    }
}