package com.qpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qpa.dto.ResponseDTO;
import com.qpa.entity.Vehicle;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.service.AuthService;
import com.qpa.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    private final AuthService authService;

    public VehicleController(VehicleService vehicleService, AuthService authService) {
        this.vehicleService = vehicleService;
        this.authService = authService;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<Void>> saveVehicle(@RequestBody Vehicle vehicle, HttpServletRequest request)
            throws InvalidEntityException {
        vehicleService.saveVehicle(vehicle, request);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicle registered successfully", 200, true, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteVehicle(@PathVariable Long id, HttpServletRequest request)
            throws InvalidEntityException {
        Long userId = authService.getUserId(request);
        vehicleService.deleteVehicle(id, userId);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicle deleted successfully", 200, true, null));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ResponseDTO<Vehicle>> viewVehicleById(@PathVariable Long id) throws InvalidEntityException {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicle fetched successfully", 200, true, vehicle));
    }

    @GetMapping("/type/{vehicleType}")
    @ResponseBody
    public ResponseEntity<ResponseDTO<List<Vehicle>>> viewVehiclesByType(@PathVariable String vehicleType)
            throws InvalidEntityException {
        VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
        List<Vehicle> vehicles = vehicleService.getVehiclesByType(type);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicles fetched successfully", 200, true, vehicles));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ResponseDTO<Vehicle>> getVehicleByBookingId(@PathVariable Long bookingId,
            HttpServletRequest request) throws InvalidEntityException {
        authService.isAuthenticated(request);
        Vehicle vehicle = vehicleService.findByBookingId(bookingId);
        return ResponseEntity.ok(new ResponseDTO<>("Vehicles fetched successfully", 200, true, vehicle));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDTO<List<Vehicle>>> getUserVehicle(HttpServletRequest request) {
        Long userId = authService.getUserId(request);
        List<Vehicle> vehicles = vehicleService.findByUserId(userId);
        return ResponseEntity.ok(new ResponseDTO<>("vehicles fetched successfully", 200, true, vehicles));
    }
}