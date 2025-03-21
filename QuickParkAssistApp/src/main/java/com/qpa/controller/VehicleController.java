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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = authService.getUserId(request);
            Vehicle vehicle = vehicleService.getVehicleById(id);

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

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ResponseDTO<Vehicle>> viewVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicle fetched successfully", 200, true, vehicle));
    }

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

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<Vehicle>> getVehicleByBookingId(@PathVariable Long bookingId, HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            throw new UnauthorizedAccessException("Please login to place the request");
        }
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicles fetched successfully", 200, true, vehicle));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDTO<List<Vehicle>>> getUserVehicle(HttpServletRequest request) {
        List<Vehicle> vehicles = vehicleService.findByUserId(authService.getUserId(request));
        return ResponseEntity.ok(new ResponseDTO<>("vehicles fetched successfully", 200, true, vehicles));
    }
    
}
