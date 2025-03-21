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

    /**
     * Constructor-based dependency injection for required services.
     *
     * @param userRepository  Repository to manage user data.
     * @param vehicleService  Service to manage vehicle-related operations.
     * @param authService     Service to manage authentication-related operations.
     */
    public UserService(UserRepository userRepository, VehicleService vehicleService, AuthService authService) {
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    /**
     * Adds a new user to the system.
     *
     * @param user The user information to be saved.
     * @return The saved user entity.
     */
    public UserInfo addUser(UserInfo user) {
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user.
     * @return The found user entity.
     * @throws InvalidEntityException if the user is not found.
     */
    public UserInfo getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("User not found"));
    }

    /**
     * Retrieves all users from the system.
     *
     * @return A list of all users.
     */
    public List<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates an existing user's information.
     *
     * @param user The user entity with updated details.
     * @return The updated user entity.
     */
    public UserInfo updateUser(UserInfo user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID, ensuring authentication cleanup.
     *
     * @param id       The ID of the user to be deleted.
     * @param response The HTTP response object for authentication deletion.
     * @throws InvalidEntityException if the user is not found or cannot be deleted due to related entities.
     */
    public void deleteUser(Long id, HttpServletResponse response) {
        UserInfo user = userRepository.findById(id)
                .orElseThrow(() -> new InvalidEntityException("User not found with ID: " + id));
        try {
            authService.deleteAuth(id, response); // Remove authentication details
            userRepository.delete(user); // Delete user entity
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new InvalidEntityException("Cannot delete user. Related entities exist.");
        }
    }

    /**
     * Retrieves the user associated with a given vehicle ID.
     *
     * @param vehicleId The ID of the vehicle.
     * @return The user associated with the vehicle.
     */
    public UserInfo viewUserByVehicleId(Long vehicleId) {
        return vehicleService.getVehicleById(vehicleId).getUserObj();
    }

    /**
     * Finds a user based on a booking ID.
     *
     * @param bookingId The ID of the booking.
     * @return The user associated with the booking.
     */
    public UserInfo findByBookingId(Long bookingId) {
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return viewUserByVehicleId(vehicle.getVehicleId());
    }

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return True if the email exists, false otherwise.
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
