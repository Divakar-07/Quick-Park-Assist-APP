package com.qpa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qpa.entity.SpotBookingInfo;
import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
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
    private AuthService authService; 

    @Autowired
    private SpotBookingInfoRepository spotBookingInfoRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired 
    private UserRepository userRepository;

    // Add a new vehicle
    public Vehicle addVehicle(Vehicle vehicle, HttpServletRequest request) { 
        String token = jwtUtil.extractTokenFromCookie(request);
    
        if (token == null) {
            throw new RuntimeException("Token not found in cookies");
        }
    
        Long userId = jwtUtil.extractUserId(token);
    
        if (userId == null) {
            throw new RuntimeException("Invalid token: User ID not found");
        }
    
        Optional<UserInfo> userObj = userRepository.findById(userId);
        
        if (userObj.isPresent()) {
            UserInfo user = userObj.get();
            vehicle.setUserObj(user);
            return vehicleRepository.save(vehicle); 
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
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

    public Vehicle findByBookingId(HttpServletRequest request, Long bookingId){
        if (!authService.isAuthenticated(request)){
            throw new RuntimeException("unAuthorized request");
        }
        Optional<SpotBookingInfo> bookingInfo = spotBookingInfoRepository.findById(bookingId);
        return bookingInfo.map(SpotBookingInfo::getVehicle).orElse(null);

    }
}