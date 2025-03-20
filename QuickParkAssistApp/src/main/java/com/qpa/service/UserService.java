package com.qpa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.qpa.entity.UserInfo;
import com.qpa.entity.Vehicle;
import com.qpa.exception.InvalidEntityException;
import com.qpa.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final AuthService authService;
    
    public UserService(UserRepository userRepository, VehicleService vehicleService, AuthService authService) {
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    public UserInfo addUser(UserInfo user) {
        return userRepository.save(user); 
    }
    
    
    public UserInfo getUserById(Long id) { return userRepository.findById(id).orElseThrow(() -> new InvalidEntityException("User not found")); }
    public List<UserInfo> getAllUsers() { return userRepository.findAll(); }
    public UserInfo updateUser(UserInfo user) { return userRepository.save(user); }

    public void deleteUser(Long id, HttpServletResponse response) {
        UserInfo user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("User not found with ID: " + id));
        try {
            authService.deleteAuth(id, response);
            userRepository.delete(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new InvalidEntityException("Cannot delete user. Related entities exist.");
        }
    }

    public UserInfo viewUserByVehicleId(Long vehicleId){
        return vehicleService.getVehicleById(vehicleId).getUserObj();
    }

    public UserInfo findByBookingId(Long bookingId){
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return viewUserByVehicleId(vehicle.getVehicleId());
    }

    public boolean existsByEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}