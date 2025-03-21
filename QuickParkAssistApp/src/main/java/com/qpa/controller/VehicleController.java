package com.qpa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.InvalidVehicleTypeException;
import com.qpa.exception.UnauthorizedAccessException;
import com.qpa.service.AuthService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired private VehicleService vehicleService;
    @Autowired private AuthService authService;

    /**
     * Saves a new vehicle or updates an existing one.
     * 
     * @param vehicle The vehicle object to be saved.
     * @param request The HTTP request object.
     * @return Response indicating success or failure.
     */
    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<Void>> saveVehicle(@RequestBody Vehicle vehicle, HttpServletRequest request) {
        try {
            if (vehicle.getVehicleId() != null){
                vehicleService.updateVehicle(vehicle);
            }
            else {
                vehicleService.addVehicle(vehicle, request);
            }
            return ResponseEntity.ok(new ResponseDTO<>("Vehicle registered successfully", 200, true, null));
        } catch (InvalidEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO<>(e.getMessage(), HttpStatus.CONFLICT.value(), false, null));
        } catch (RuntimeException e) {
            System.out.println("RuntimeException inside addVehicle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), false, null));
        } catch (Exception e) {
            System.out.println("Exception inside addVehicle: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null));
        }
    }

    /**
     * Deletes a vehicle by its ID.
     * 
     * @param id The ID of the vehicle to be deleted.
     * @param request The HTTP request object.
     * @return Response indicating success or failure.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            Vehicle vehicle = vehicleService.getVehicleById(id);

            // Check if the user is authorized to delete the vehicle
            if (!userId.equals(vehicle.getUserObj().getUserId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO<>("Unauthorized: You do not have permission to delete this vehicle.", 401, false, null));
            }

            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(new ResponseDTO<>("Vehicle deleted successfully", 200, true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), false, null));
        } catch (Exception e) {
            System.out.println("Exception inside deleteVehicle: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value(), false, null));
        }
    }

    /**
     * Retrieves a vehicle by its ID.
     * 
     * @param id The ID of the vehicle.
     * @return Response containing the requested vehicle.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ResponseDTO<Vehicle>> viewVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicle fetched successfully", 200, true, vehicle));
    }

    /**
     * Retrieves all vehicles of a specific type.
     * 
     * @param vehicleType The type of vehicle to filter by.
     * @return Response containing a list of vehicles of the specified type.
     */
    @GetMapping("/type/{vehicleType}")
    @ResponseBody
    public ResponseEntity<ResponseDTO<List<Vehicle>>> viewVehiclesByType(@PathVariable String vehicleType) {
        try {
            VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
            List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);

            if (vehicles.isEmpty()) {
                throw new InvalidEntityException("No vehicles found for type: " + vehicleType);
            }

            return ResponseEntity.ok(new ResponseDTO<>("Vehicles fetched successfully", 200, true, vehicles));
        } catch (IllegalArgumentException e) {
            throw new InvalidVehicleTypeException("Invalid vehicle type: " + vehicleType);
        }
    }

    /**
     * Retrieves a vehicle associated with a specific booking ID.
     * 
     * @param bookingId The booking ID.
     * @param request The HTTP request object.
     * @return Response containing the vehicle details.
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<Vehicle>> getVehicleByBookingId(@PathVariable Long bookingId, HttpServletRequest request) {
        // Ensure the user is authenticated before proceeding
        if (!authService.isAuthenticated(request)) {
            throw new UnauthorizedAccessException("Please login to place the request");
        }
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicles fetched successfully", 200, true, vehicle));
    }

    /**
     * Retrieves all vehicles associated with the authenticated user.
     * 
     * @param request The HTTP request object.
     * @return Response containing the list of vehicles owned by the user.
     */
    @GetMapping("/user")
    public ResponseEntity<ResponseDTO<List<Vehicle>>> getUserVehicle(HttpServletRequest request) {
        List<Vehicle> vehicles = vehicleService.findByUserId(authService.getUserId(request));
        return ResponseEntity.ok(new ResponseDTO<>("vehicles fetched successfully", 200, true, vehicles));
    }
    
}
